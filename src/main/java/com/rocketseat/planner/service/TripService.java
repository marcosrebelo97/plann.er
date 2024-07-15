package com.rocketseat.planner.service;

import com.rocketseat.planner.dto.TripCreateResponse;
import com.rocketseat.planner.dto.TripRequestPayload;
import com.rocketseat.planner.dto.TripResponse;
import com.rocketseat.planner.model.Trip;
import com.rocketseat.planner.repository.TripRepository;
import com.rocketseat.planner.exception.ResourceNotFoundException;
import com.rocketseat.planner.exception.ErroDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    ParticipantService participantService;

    public TripResponse getTripDetails (UUID tripId) {
        return this.tripRepository.findById(tripId)
                .map(this::to)
                .orElseThrow();
    }

    public TripCreateResponse createTrip (TripRequestPayload payload) throws ErroDateException {

        validaFormatoData(payload.starts_at());
        validaFormatoData(payload.ends_at());

        Trip newTrip = new Trip(payload);

        if (newTrip.getStartsAt().isAfter(newTrip.getEndsAt())) {
            throw new ErroDateException("A data de início não pode ser maior que a data de fim.");
        }

        this.tripRepository.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return new TripCreateResponse(newTrip.getId());
    }

    public TripResponse updateTrip (UUID tripId, TripRequestPayload payload) throws ErroDateException {
        validaFormatoData(payload.starts_at());
        validaFormatoData(payload.ends_at());

        Trip trip = this.tripRepository.findById(tripId).orElseThrow();
        updateDataTrip(trip, payload);

        if (trip.getStartsAt().isAfter(trip.getEndsAt())) {
            throw new ErroDateException("A data de início não pode ser maior que a data de fim");
        }

        this.tripRepository.save(trip);
        return to(trip);
    }

    public TripResponse confirmTrip (UUID tripId) throws ResourceNotFoundException {
        Trip trip = this.tripRepository.findById(tripId).orElseThrow();
        trip.setIsConfirmed(true);
        this.tripRepository.save(trip);

        return to(trip);
    }

    private TripResponse to (Trip trip) {
        return new TripResponse(trip.getId(), trip.getDestination(),
                trip.getStartsAt().format(DateTimeFormatter.ISO_DATE_TIME),
                trip.getEndsAt().format(DateTimeFormatter.ISO_DATE_TIME),
                trip.getOwnerName(), trip.getOwnerEmail(), trip.getIsConfirmed());
    }

    private void updateDataTrip (Trip trip, TripRequestPayload payload) {
        trip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
        trip.setDestination(payload.destination());
    }

    private void validaFormatoData (String data) throws ErroDateException {
        try {
            LocalDateTime.parse(data, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeException exception) {
            throw new ErroDateException ("Formato de data inválido. Use ex: 2021-08-01T10:00:00");
        }
    }


}
