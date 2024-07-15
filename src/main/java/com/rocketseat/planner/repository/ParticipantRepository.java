package com.rocketseat.planner.repository;

import com.rocketseat.planner.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
    List<Participant> findByTripId(UUID tripId);

    List<Participant> findParticipantsByTripId(UUID tripId);
}
