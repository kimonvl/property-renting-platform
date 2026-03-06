-- Align chat schema with refactored entities: Chat, ChatParticipant, Message.

-- 1) Chats: add per-chat last message timestamp
ALTER TABLE chats
    ADD COLUMN IF NOT EXISTS last_message_at TIMESTAMPTZ;

-- 2) Remove old reverse booking->chat linkage (current model is Chat -> Booking only)
ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS fk_bookings_chat;

DROP INDEX IF EXISTS uq_bookings_chat_id;

ALTER TABLE bookings
    DROP COLUMN IF EXISTS chat_id;

-- 3) Messages: remove legacy FK that depended on composite PK in chat_participants
ALTER TABLE messages
    DROP CONSTRAINT IF EXISTS fk_messages_chat_author_participant;

-- 4) Chat participants: convert join table to full entity table
ALTER TABLE chat_participants
    ADD COLUMN IF NOT EXISTS id BIGINT;

CREATE SEQUENCE IF NOT EXISTS chat_participants_id_seq;

ALTER TABLE chat_participants
    ALTER COLUMN id SET DEFAULT nextval('chat_participants_id_seq');

UPDATE chat_participants
SET id = nextval('chat_participants_id_seq')
WHERE id IS NULL;

ALTER TABLE chat_participants
    ALTER COLUMN id SET NOT NULL;

-- Keep sequence in sync after backfill
SELECT setval(
    'chat_participants_id_seq',
    COALESCE((SELECT MAX(id) FROM chat_participants), 1),
    TRUE
);

ALTER TABLE chat_participants
    ADD COLUMN IF NOT EXISTS last_read_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

ALTER TABLE chat_participants
    DROP CONSTRAINT IF EXISTS chat_participants_pkey;

DO $$ BEGIN
    ALTER TABLE chat_participants
        ADD CONSTRAINT pk_chat_participants PRIMARY KEY (id);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    ALTER TABLE chat_participants
        ADD CONSTRAINT uk_chat_participant UNIQUE (chat_id, user_id);
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

CREATE INDEX IF NOT EXISTS idx_chat_participants_chat
    ON chat_participants(chat_id);

CREATE INDEX IF NOT EXISTS idx_chat_participants_user
    ON chat_participants(user_id);

-- 5) Seen-by table is obsolete after per-user read state moved to chat_participants.last_read_at
DROP TABLE IF EXISTS message_seen_by;