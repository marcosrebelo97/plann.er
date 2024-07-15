package com.rocketseat.planner.controller;

import com.rocketseat.planner.dto.*;
import com.rocketseat.planner.exception.ErroDateException;
import com.rocketseat.planner.service.ActivityService;
import com.rocketseat.planner.service.LinkService;
import com.rocketseat.planner.model.Trip;
import com.rocketseat.planner.repository.TripRepository;
import com.rocketseat.planner.service.ParticipantService;
import com.rocketseat.planner.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TripService tripService;

    //Trips

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) throws ErroDateException {
        TripCreateResponse newTrip = this.tripService.createTrip(payload);
        return ResponseEntity.ok(newTrip);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripDetails(@PathVariable UUID id){
        try {
            TripResponse response = tripService.getTripDetails(id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) throws ErroDateException {
        TripResponse tripResponse = tripService.updateTrip(id, payload);
        return ResponseEntity.ok(tripResponse);
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<TripResponse> isConfirm(@PathVariable UUID id){
        try {
            TripResponse tripResponse = tripService.confirmTrip(id);
            return ResponseEntity.ok(tripResponse);
        } catch (Exception ex) {return ResponseEntity.notFound().build();}
    }

    //Activities

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) throws ErroDateException{
        ActivityResponse activityResponse = this.tripService.saveActivity(id, payload);
        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityDataList = this.tripService.getAllActivitiesTrip(id);

        return ResponseEntity.ok(activityDataList);
    }

    //Participant

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        ParticipantCreateResponse participantCreateResponse = this.tripService.inviteParticipant(id, payload);
        return ResponseEntity.ok(participantCreateResponse);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
        List<ParticipantData> participantList = this.tripService.getAllParticipantsTrip(id);

        return ResponseEntity.ok(participantList);
    }

    //Links

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        LinkResponse linkResponse = this.tripService.saveLink(id, payload);
        return ResponseEntity.ok(linkResponse);
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> linkDataList = this.tripService.getAllLinksTrip(id);
        return ResponseEntity.ok(linkDataList);
    }
}
