package gr.aueb.cf.property_renting_platform.services;

import gr.aueb.cf.property_renting_platform.DTOs.responses.realtime.MessageDTO;
import gr.aueb.cf.property_renting_platform.DTOs.responses.realtime.NotificationEvent;

public interface WebsocketService {
    public void sendMessageToUser(String userEmail, MessageDTO event);
    public void sendNotificationToUser(String userEmail, NotificationEvent event);
}
