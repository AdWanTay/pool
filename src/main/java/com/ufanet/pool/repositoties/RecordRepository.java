package com.ufanet.pool.repositoties;

import com.ufanet.pool.models.Record;
import com.ufanet.pool.models.dto.RecordGetAllResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("SELECT new com.ufanet.pool.models.dto.RecordGetAllResponse(tt.time, count(tt)) " +
            "FROM Record tt WHERE tt.date = :date " +
            "GROUP BY tt.time")
    List<RecordGetAllResponse> findRecordsCountByDate(@Param("date") LocalDate date);


    @Query("SELECT COUNT(tt)\n " +
            "FROM Record tt WHERE tt.date = :date AND tt.time = :time")
    long countRecordsByDateTime(@Param("date") LocalDate date, @Param("time") LocalTime time);

    @Modifying
    @Transactional
    @Query("DELETE FROM Record r WHERE r.client.id = :clientId AND r.id = :id")
    int deleteRecordByClientIdAndId(@Param("clientId") Long client_id, @Param("id") Long id);

    @Query("SELECT tt.date,tt.time " +
            "FROM Record tt left join Client c on tt.client.id = c.id " +
            "where c.name = :name")
    Optional<List<Record>> findAllByClientName(@Param("name") String name);


    @Query("SELECT count(tt) " +
            "FROM Record tt left join Client c on tt.client.id = c.id " +
            "where tt.date = :date and tt.client.id = :clientId")
    long countRecordsByClientId(Long clientId, LocalDate date);
}
