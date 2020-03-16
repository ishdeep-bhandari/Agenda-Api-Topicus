package com.agenda.topicus.repository;

import java.sql.Time;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agenda.topicus.model.Appointment;
import com.agenda.topicus.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query(value = "SELECT * FROM appointments a where a.appointmentownerid = :id OR a.appointmentadditionid = :id", nativeQuery=true) 
    ArrayList<Appointment> findallappointmentsByUserId(Long id);
    
    @Query(value = "SELECT * FROM appointments a where a.appointmentownerid = :id AND a.appointmentid = :appid", nativeQuery=true) 
    Appointment findAppointmentByIdAndUserId(Long id, Long appid);
    
    @Query(value = "SELECT * FROM appointments a where a.appointmentownerid = :id OR a.appointmentadditionid = :id AND a.appointmentdate = :appdate", nativeQuery=true)
    ArrayList<Appointment> findappoinmtmentByUserIdAndDate(Long id, java.sql.Date appdate);
    
    @Query(value = "SELECT a.appointmentstarttime FROM appointments a where a.appointmentownerid = :id OR a.appointmentadditionid = :id AND a.appointmentdate = :appdate", nativeQuery=true)
    ArrayList<Time> findappoinmtmentstarttimesByUserIdAndDate(User id, java.sql.Date appdate);
    
    @Query(value = "SELECT a.appointmentstarttime FROM appointments a where a.appointmentownerid = :id OR a.appointmentadditionid = :id AND a.appointmentdate = :appdate", nativeQuery=true)
    ArrayList<Time> findappoinmtmentstarttimesByLongUserIdAndDate(Long id, java.sql.Date appdate);


}