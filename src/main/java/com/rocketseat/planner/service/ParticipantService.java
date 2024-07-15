package com.rocketseat.planner.service;

import com.rocketseat.planner.model.Participant;
import com.rocketseat.planner.model.Trip;
import com.rocketseat.planner.dto.ParticipantCreateResponse;
import com.rocketseat.planner.dto.ParticipantData;
import com.rocketseat.planner.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip){
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();

        this.repository.saveAll(participants);

        System.out.println(participants.get(0).getId());
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip){
        Participant participant = new Participant(email, trip);
        this.repository.save(participant);

        return new ParticipantCreateResponse(participant.getId());
    }

    public ParticipantCreateResponse registerParticipantToTrip(String email, Trip trip) {
        Participant participant = new Participant(email, trip);
        this.repository.saveAndFlush(participant);
        return new ParticipantCreateResponse(participant.getId());
    }

    public void triggerConfirmationToParticipants(UUID tripId){}

    public void triggerConfirmationEmailToParticipant(UUID tripId, String email){}

    public List<ParticipantData> getAllParticipantsFromEvent(UUID tripId) {
        return this.repository.findByTripId(tripId).stream().map(participant -> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
    }

    public List<ParticipantData> getAllParticipantsFromTrip(UUID id) {
        return repository.findParticipantsByTripId(id)
                .stream()
                .map(participant -> new ParticipantData(participant.getId(),
                        participant.getName(),
                        participant.getEmail(),
                        participant.getIsConfirmed()))
                .toList();
    }
}
