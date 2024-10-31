package com.ufanet.pool.services;


import com.ufanet.pool.repositoties.ClientRepository;
import com.ufanet.pool.repositoties.RecordRepository;
import com.ufanet.pool.models.Client;
import com.ufanet.pool.models.Record;
import com.ufanet.pool.models.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final ClientRepository clientRepository;

    private final String API_KEY = "385c7c21-2577-415b-8175-f71d223c4758";
    private final String COUNTRY = "RU";
    private final String HOLIDAY_API_URL = "https://holidayapi.com/v1/holidays/"; /*бесплатная версия этого сервиса
                                                                                   позволяет получать данные только
                                                                                   за прошлый год(((                */

    private static final int MAX_RECORDS_PER_HOUR = 10;                     //максимальное кол-во записей в час
    private static final int WORK_HOURS_START = 9;                           //начало рабочего дня
    private static final int WORK_HOURS_PER_DAY = 9;                         //кол-во рабочих часов в день
    private static final int MAX_RECORDS_PER_CLIENT = 4;

    private static final List<LocalTime> WORKING_HOURS = Stream.iterate(LocalTime.of(WORK_HOURS_START, 0),
            time -> time.plusHours(1)).limit(WORK_HOURS_PER_DAY).toList();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ResponseEntity<?> getAll(String date) {
        try {
            return ResponseEntity.ok(recordRepository.findRecordsCountByDate(LocalDate.parse(date, FORMATTER)));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    public ResponseEntity<?> getAvailable(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, FORMATTER);
            List<RecordGetAllResponse> occupiedSlots = recordRepository.findRecordsCountByDate(localDate);
            List<RecordGetAllResponse> availableSlots = new ArrayList<>();
            Map<LocalTime, Long> occupiedCountMap = occupiedSlots.stream()
                    .collect(Collectors.toMap(RecordGetAllResponse::getTime, RecordGetAllResponse::getCount));
            for (LocalTime time : WORKING_HOURS) {
                availableSlots.add(new RecordGetAllResponse(time, MAX_RECORDS_PER_HOUR - occupiedCountMap.getOrDefault(time, 0L)));
            }
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date");
        }
    }

    public ResponseEntity<?> reserve(ReservationRequest reservationRequest) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(reservationRequest.getDatetime(), formatter);
            LocalDate date = dateTime.toLocalDate();
            LocalTime time = dateTime.toLocalTime();

            if (isHoliday(date.getDayOfMonth(), date.getMonthValue(), date.getYear())) {
                throw new IllegalStateException("It is holiday!");
            }

            Client existingClient = clientRepository.findById(reservationRequest.getClientId())
                    .orElseThrow(() -> new IllegalArgumentException("Client doesn't exist."));

            if (!WORKING_HOURS.contains(time)) {
                throw new IllegalStateException("The pool is closed at this time");
            }
            if (recordRepository.countRecordsByDateTime(date, time) >= MAX_RECORDS_PER_HOUR) {
                throw new IllegalStateException("The pool is busy at this time");
            }

            if (recordRepository.countRecordsByClientId(existingClient.getId(), date) >= MAX_RECORDS_PER_CLIENT) {
                throw new IllegalStateException("Max records per client at this day");
            }

            Record newReservation = new Record();
            newReservation.setClient(existingClient);
            newReservation.setDate(date);
            newReservation.setTime(time);

            return ResponseEntity.ok(new ReservationResponse(recordRepository.save(newReservation).getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> cancelReservation(ReservationCancelRequest reservationCancelRequest) {
        if (recordRepository.deleteRecordByClientIdAndId(reservationCancelRequest.getClientId(), Long.valueOf(reservationCancelRequest.getOrderId())) > 0) {
            return ResponseEntity.ok("Record has been cancelled");
        }
        return ResponseEntity.badRequest().body("Can't cancel record");
    }

    private boolean isHoliday(int day, int month, int year) {
        String url = UriComponentsBuilder.fromHttpUrl(HOLIDAY_API_URL)
                .queryParam("key", API_KEY)
                .queryParam("country", COUNTRY)
                .queryParam("day", day)
                .queryParam("month", month)
                .queryParam("year", year)
                .build().toString();

        try {
            ResponseEntity<HolidayResponse> response = new RestTemplate().getForEntity(
                    url, HolidayResponse.class);
            return !Objects.requireNonNull(response.getBody()).getHolidays().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public ResponseEntity<?> getByName(String name) {
        System.out.println(recordRepository.findAllByClientName(name));
        Optional<List<ReservationByNameRequest>> records = recordRepository.findAllByClientName(name);
        if (records.isPresent() && !records.get().isEmpty())
            return ResponseEntity.ok(records.get());
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Records not found");
    }


    public ResponseEntity<?> reserveConsistently(ReserveConsistentlyRequest request) {
        try {

            LocalDate date = request.getDate();
            LocalTime startTime = request.getStartTime();
            LocalTime endTime = request.getEndTime();

            Client existingClient = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new IllegalArgumentException("Client doesn't exist."));
            if (isHoliday(date.getDayOfMonth(), date.getMonthValue(), date.getYear())) {
                throw new IllegalStateException("It is holiday!");
            }

            if (!WORKING_HOURS.contains(startTime) || !WORKING_HOURS.contains(endTime)) {
                throw new IllegalStateException("The pool is closed at this time");
            }
            if (endTime.getHour() - startTime.getHour() > MAX_RECORDS_PER_HOUR ) {
                throw new IllegalStateException("Max records per client at this day");
            }

            List<Record> reservations= new ArrayList<>();

            for (int time = startTime.getHour(); time < endTime.getHour(); time++ ) {
                if (recordRepository.countRecordsByDateTime(date, LocalTime.of(time, 0)) >= MAX_RECORDS_PER_HOUR) {
                    throw new IllegalStateException("The pool is busy at this time: " + time);
                }
                reservations.add(new Record(date, LocalTime.of(time, 0), existingClient));
            }
            return ResponseEntity.ok(recordRepository.saveAll(reservations).stream().map(Record::getId).toList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
