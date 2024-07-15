package com.rocketseat.planner.service;

import com.rocketseat.planner.dto.*;
import com.rocketseat.planner.model.Trip;
import com.rocketseat.planner.repository.TripRepository;
import com.rocketseat.planner.exception.ResourceNotFoundException;
import com.rocketseat.planner.exception.ErroDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    ParticipantService participantService;
    @Autowired
    ActivityService activityService;

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
            throw new ErroDateException("Atenção!! A data de início não pode ser maior que a data de fim.");
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
            throw new ErroDateException("Atenção!! A data de início não pode ser maior que a data de fim");
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

    public ActivityResponse saveActivity (UUID tripId, ActivityRequestPayload payload) throws ErroDateException {
        validaFormatoData(payload.occurs_at());
        Trip trip = this.tripRepository.findById(tripId).orElseThrow();
        verificaDataActivity(payload.occurs_at(), trip);
        return this.activityService.registerActivity(payload, trip);
    }

    public List<ActivityData> getAllActivitiesTrip (UUID tripId){
        Trip trip = this.tripRepository.findById(tripId).orElseThrow();
        return this.activityService.getAllActivitiesFromId(trip.getId());
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

    private void verificaDataActivity (String occursAt, Trip trip) throws ErroDateException {
        LocalDateTime data = LocalDateTime.parse(occursAt, DateTimeFormatter.ISO_DATE_TIME);

        if (data.isBefore(trip.getStartsAt()) || data.isAfter(trip.getEndsAt())) {
            throw new ErroDateException
                    ("Atenção!! A data da atividade deve ficar entre a data de início e data de fim da viagem");
        }
    }


}
