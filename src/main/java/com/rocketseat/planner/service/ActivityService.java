package com.rocketseat.planner.service;

import com.rocketseat.planner.dto.ActivityData;
import com.rocketseat.planner.dto.ActivityRequestPayload;
import com.rocketseat.planner.dto.ActivityResponse;
import com.rocketseat.planner.model.Activity;
import com.rocketseat.planner.model.Trip;
import com.rocketseat.planner.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository repository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip){
        Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);

        this.repository.save(newActivity);

        return new ActivityResponse(newActivity.getId());
    }

    public List<ActivityData> getAllActivitiesFromId(UUID id) {
        return this.repository.findByTripId(id).stream().map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccursAt())).toList();

    }
}
