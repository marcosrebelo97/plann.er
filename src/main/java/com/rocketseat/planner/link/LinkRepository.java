package com.rocketseat.planner.link;

import com.rocketseat.planner.trip.LinkData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    public List<Link> findByTripId(UUID id);
}
