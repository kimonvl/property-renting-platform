package gr.aueb.cf.property_renting_platform.controllers;

import gr.aueb.cf.property_renting_platform.DTOs.requests.partner.chat.ChatSearchRequest;
import gr.aueb.cf.property_renting_platform.DTOs.responses.GenericResponse;
import gr.aueb.cf.property_renting_platform.DTOs.responses.realtime.ChatDTO;
import gr.aueb.cf.property_renting_platform.exceptions.EntityNotFoundException;
import gr.aueb.cf.property_renting_platform.exceptions.ValidationException;
import gr.aueb.cf.property_renting_platform.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    // TODO: implement getFilteredAndPaginatedChatsForPartner and getFilteredAndPaginatedChats after watching cf
    @PostMapping
    public ResponseEntity<GenericResponse<Page<ChatDTO>>> getFilteredAndPaginatedChatsForPartner(
            @RequestBody ChatSearchRequest filters,
            BindingResult bindingResult,
            Principal principal
    ) throws EntityNotFoundException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException("ChatSearchRequest", "Invalid chat search filters", bindingResult);
        }

        return new ResponseEntity<>(
                new GenericResponse<>(
                        chatService.getMyPropertyFilteredAndPaginatedChatsPartner(filters, principal.getName()),
                        "getChatsFilteredPartner",
                        "Chats retrieved successfully for partner with email " + principal.getName() + " and property id " + filters.propertyId(),
                        true
                ),
                HttpStatus.OK
        );
    }
}
