package com.rocketseat.planner.dto;

import java.util.UUID;

public record TripResponse(UUID id, String destination, String startsAt, String endsAt, String ownerEmail, String ownerName, boolean isConfirmed) {}