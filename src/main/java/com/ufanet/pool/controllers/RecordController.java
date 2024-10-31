package com.ufanet.pool.controllers;


import com.ufanet.pool.models.dto.ReservationCancelRequest;
import com.ufanet.pool.models.dto.ReservationRequest;
import com.ufanet.pool.models.dto.ReserveConsistentlyRequest;
import com.ufanet.pool.services.RecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v0/pool/timetable")
@AllArgsConstructor
public class RecordController {
    private RecordService recordService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam String date) {
        return recordService.getAll(date) ;
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailable(@RequestParam String date) {
        return recordService.getAvailable(date);
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody ReservationRequest request) {
        return recordService.reserve(request);
    }
    
    @GetMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestBody ReservationCancelRequest request) {
        return recordService.cancelReservation(request);
    }

    @GetMapping("/clientRecords")
    public ResponseEntity<?> getByName(@RequestParam String name) {
        return recordService.getByName(name);
    }

    @PostMapping("/reserveConsistently")
    public ResponseEntity<?> reserveConsistently(@RequestBody ReserveConsistentlyRequest request) {
        return recordService.reserveConsistently(request);
    }
}
