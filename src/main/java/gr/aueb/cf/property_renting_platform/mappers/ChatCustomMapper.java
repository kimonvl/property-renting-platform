package gr.aueb.cf.property_renting_platform.mappers;

import gr.aueb.cf.property_renting_platform.DTOs.responses.realtime.ChatDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.user.UserDTO;
import gr.aueb.cf.property_renting_platform.models.chat.Chat;
import gr.aueb.cf.property_renting_platform.models.chat.ChatParticipant;
import gr.aueb.cf.property_renting_platform.models.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ChatCustomMapper {

    private final UserMapper userMapper;

    public ChatDTO chatToChatDTO(Chat chat) {
        Set<UserDTO> participants = userMapper.toDtoSet(chat.getAllParticipants().stream().map(ChatParticipant::getUser).collect(Collectors.toSet()));
         return new ChatDTO(
                 chat.getUuid(),
                 chat.getBooking().getProperty().getId(),
                 participants,
                 chat.getLastMessageAt()
         );
    }
}
