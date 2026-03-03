package com.booking.booking_clone_backend.DTOs.requests.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingRequest(

        @NotNull(message = "{NotNull.createBookingRequest.propertyId}")
        UUID propertyId,

        @NotNull(message = "{NotNull.createBookingRequest.checkIn}")
        LocalDate checkIn,

        @NotNull(message = "{NotNull.createBookingRequest.checkOut}")
        LocalDate checkOut,

        @NotNull(message = "{NotNull.createBookingRequest.guestCount}")
        @Min(value = 1, message = "{Min.createBookingRequest.guestCount}")
        Integer guestCount,

        @NotNull(message = "{NotNull.createBookingRequest.checkOutDetails}")
        @Valid
        CheckOutDetailsDTO checkOutDetails
) { }