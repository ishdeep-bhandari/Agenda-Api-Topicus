package com.agenda.topicus.controller;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

	
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.topicus.repository.AppointmentRepository;
import com.agenda.topicus.repository.UserRepository;
import com.agenda.topicus.exception.ResourceNotFoundException;
import com.agenda.topicus.model.Appointment;
import com.agenda.topicus.model.User;


@RestController
@RequestMapping("/agenda/appointments")
public class AppointmentController {
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private UserRepository userRepository;

	
	
	// Make time slots used by users
	public ArrayList<java.sql.Time> getTimeSlots() {
		SimpleDateFormat timelsotformat = new SimpleDateFormat("HH:mm:ss");
		ArrayList<Time> timeslots = new ArrayList<Time>();
		
		try {
		Date starttimeslot = timelsotformat.parse("07:00:00");
	    Date endtimeslot = timelsotformat.parse("18:30:00");
	    
	    long diff = starttimeslot.getTime();
	    while (diff < endtimeslot.getTime()) {
	    	Time slot = new Time(diff);
	    	timeslots.add(slot);
	    	diff += 1800000;
	    	}	    
		}	
		catch (ParseException e) {
			e.printStackTrace();
		}

		return timeslots;
	}
	
	
	// Get appointments for users by Id
	
	@GetMapping("{userid}/users/{seluserid}/allappointments")
	public ResponseEntity<ArrayList<Appointment>> getAllAppointmentsForUserById(@PathVariable(value = "seluserid") Long seluserid) throws ResourceNotFoundException{
		
		
		userRepository.findById(seluserid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + seluserid));
		
		ArrayList<Appointment> appointments = appointmentRepository.findallappointmentsByUserId(seluserid);
		return ResponseEntity.ok().body(appointments);
	
	}

	// Get all available slots for the user by Id and Date
	
	@GetMapping("/{userid}/slots/{seluserid}/{date}")
	public ResponseEntity<ArrayList<Time>> getFirstAvailableStartTimeSlotForUserByDate(@PathVariable(value = "seluserid") Long seluserid, @PathVariable(value = "date") java.sql.Date appdate) throws ResourceNotFoundException{

		ArrayList<Time> starttimeslots = getTimeSlots();
		
		userRepository.findById(seluserid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + seluserid));
		
		ArrayList<Appointment> appointments = appointmentRepository.findappoinmtmentByUserIdAndDate(seluserid,appdate);
		ArrayList<Time> appointmentstarttimes = new ArrayList<Time>();
		
		if (appointments == null) throw new ResourceNotFoundException("Cannot find appointments for date :: " + appdate);
		
		appointments.stream().forEach((app) -> {
			appointmentstarttimes.add(app.getStartTime());
			});
	
		ArrayList<Time>appointmentstarttimesdup = (ArrayList<Time>) appointmentstarttimes.stream().distinct().collect(Collectors.toList());

		Collections.sort(appointmentstarttimesdup);
		
		for (int i=0; i<appointmentstarttimesdup.size(); i++) {
			
			Iterator<Time> startiter = starttimeslots.iterator();
			while (startiter.hasNext()) {
				   Time s = startiter.next(); // must be called before you can call i.remove()

				   if (s.compareTo(appointmentstarttimesdup.get(i)) == 0) {	   
				       startiter.remove();
				   }			
				}
		   } 
		
		
		return ResponseEntity.ok().body(starttimeslots);
	}
	
	// Get first available slot for user by time and date
	
	@GetMapping("/{id}/slots/{seluserid}/{date}/{time}")
	public ResponseEntity<String> getFirstAvailableSlotForUserByTime(@PathVariable(value = "seluserid") Long seluserid, @PathVariable(value = "date") java.sql.Date appdate,  @PathVariable(value = "time") java.sql.Time apptime) throws ResourceNotFoundException{
		
		ArrayList<Time> starttimeslots = getTimeSlots();
		
		userRepository.findById(seluserid)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for this id :: " + seluserid));
		
		ArrayList<Appointment> appointments = appointmentRepository.findappoinmtmentByUserIdAndDate(seluserid,appdate);
		ArrayList<Time> existingappointmentstarttimes = new ArrayList<Time>();
		
		if (appointments == null) throw new ResourceNotFoundException("Cannot find appointments for date :: " + appdate);
 		
		appointments.stream().forEach((app) -> {
			existingappointmentstarttimes.add(app.getStartTime());
			});
		
		
		ArrayList<Time>existingappointmentstarttimesdup = (ArrayList<Time>) existingappointmentstarttimes.stream().distinct().collect(Collectors.toList());

		Collections.sort(existingappointmentstarttimesdup);

		Collections.sort(starttimeslots);

		Iterator<Time> i = starttimeslots.iterator();
		
		while (i.hasNext()) {
		   Time s = i.next(); 

		   if (s.compareTo(apptime) < 0) {	   
		       i.remove();
		   }
			
		}

		return ResponseEntity.ok().body("First available slot is from:" + starttimeslots.get(0));
	}
	

	// Make an appointment with a user
	@RequestMapping(value = "{id}/slots/{seluserid}/newappointment", method = RequestMethod.POST)
	public Appointment createAppointment(@RequestBody Appointment appointment, @PathVariable(value = "id") User userid, @PathVariable(value = "seluserid") User seluserid){
		
		SimpleDateFormat timeslotparser = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat currentdateformat = new SimpleDateFormat("yyyy/mm/dd");
		Date currentdate = new Date();
		
	    ArrayList<Time> appstarttimesOwner = appointmentRepository.findappoinmtmentstarttimesByUserIdAndDate(userid, appointment.getAppointmentDate());
	    ArrayList<Time> appstarttimesAddition = appointmentRepository.findappoinmtmentstarttimesByUserIdAndDate(seluserid, appointment.getAppointmentDate());

		if (seluserid == userid) throw new IllegalArgumentException("Addtional user cannot be the owner");
		
		if (appointment.getAppointmentDate() == null) throw new NullPointerException("Appointment Date cannot be null"); 
		else {if (appointment.getAppointmentDate().compareTo(currentdate) < 0) throw new IllegalArgumentException("Appointment date cannot be less than current date.");}
	    
		if (appointment.getAppointmentName() == null) throw new NullPointerException("Appointment Name cannot be null");
   
	    if (appointment.getStartTime() == null) throw new NullPointerException("Appointment Start Time cannot be null");
	    
	    if (appointment.getEndTime() == null) throw new NullPointerException("Appointment End Time cannot be null");
	   
	    if (appointment.getStartTime().compareTo(appointment.getEndTime()) > 0) throw new IllegalArgumentException("End time cannot be less than start time");
	    
	    if (appointment.getEndTime().getTime() - appointment.getStartTime().getTime() != 1800000) throw new IllegalArgumentException("Time slot cannot be more than 30 mins");

		try {
		Date starttimelot = timeslotparser.parse("07:00:00");
	    Date endtimelot = timeslotparser.parse("18:30:00");
	    if (appointment.getStartTime().compareTo(starttimelot) < 0 || appointment.getEndTime().compareTo(endtimelot) > 0) throw new IllegalArgumentException("Time slots must be between 07:00:00 and 18:00:00");
		
	    }catch (ParseException e){
	    	e.printStackTrace();
	    }
		
	    for (int i = 0; i< appstarttimesOwner.size(); i++){
		if (appstarttimesOwner.get(i).compareTo(appointment.getStartTime()) == 0) throw new IllegalArgumentException("Time slot being used by the owner");
		}
	    
	    try{
	    	if (appstarttimesAddition == null) throw new NullPointerException();
	    	
	    	else {
	    		
	    		for (int i = 0; i< appstarttimesAddition.size(); i++){
	    		if (appstarttimesAddition.get(i).compareTo(appointment.getStartTime()) == 0) throw new IllegalArgumentException("Time slot being used by the owner" );
	    		}
	    		
	    	}
	    }catch (NullPointerException e){
	    	
	    }		
		appointment.setAppointmentOwnerId(userid);
		
		appointment.setAppointmentAdditionId(seluserid);

	    return appointmentRepository.save(appointment);
	}
	
	// Edit an appointment for a user
	
	@PutMapping("{id}/allappointments/{appid}")
	public ResponseEntity<Appointment> updateAppointment(@PathVariable(value = "id") Long userid, @PathVariable(value = "appid") Long appid,
			@RequestBody Appointment appointmentdetails) throws ResourceNotFoundException{
		
		Appointment appointment = appointmentRepository.findAppointmentByIdAndUserId(userid, appid);
		ArrayList<Time> appOwnerstarttimes = appointmentRepository.findappoinmtmentstarttimesByLongUserIdAndDate(userid,appointment.getAppointmentDate());
		ArrayList<Time> appAdditionstarttimes  = appointmentRepository.findappoinmtmentstarttimesByUserIdAndDate(appointment.getAppointmentAdditionId(), appointment.getAppointmentDate());
	
		if (appointment == null) throw new ResourceNotFoundException("Cannot find requested appointment :: " + appid);
		
		if (appointmentdetails.getAppointmentDate() == null) {
			
		}
		else {
			appointment.setAppointmentDate(appointmentdetails.getAppointmentDate());
		
		}
	 
		if (appointmentdetails.getAppointmentName() == null) {
			
		}
		else
		{
			appointment.setAppointmentName(appointmentdetails.getAppointmentName());
		}
		
		if (appointmentdetails.getAppointmentMessage() == null) {
			
		}
		else
		{
			appointment.setAppointmentMessage(appointmentdetails.getAppointmentMessage());
		}
		
		
		if (appointmentdetails.getStartTime() == null && appointmentdetails.getEndTime() == null) {
		}
			else {
					    
	        	for (int i = 0; i< appOwnerstarttimes.size(); i++){
					if (appOwnerstarttimes.get(i).compareTo(appointmentdetails.getStartTime()) == 0) throw new IllegalArgumentException("Time slot being used by the additional owner");
					}
	        	
			    for (int i = 0; i< appAdditionstarttimes.size(); i++){
					if (appAdditionstarttimes.get(i).compareTo(appointmentdetails.getStartTime()) == 0) throw new IllegalArgumentException("Time slot being used by the additional owner");
					}

			    
			    if (appointmentdetails.getStartTime().compareTo(appointmentdetails.getEndTime()) > 0) throw new IllegalArgumentException("End time cannot be less than start time");		    
				
			    if (appointmentdetails.getEndTime().getTime() - appointmentdetails.getStartTime().getTime() != 1800000 )throw new IllegalArgumentException("Time slot cannot be more than 30 mins");	   
				    
			    appointment.setStartTime(appointmentdetails.getStartTime());
			    appointment.setEndTime(appointmentdetails.getEndTime());
			
			}

		appointment.setAppointmentAdditionId(appointmentdetails.getAppointmentAdditionId());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
		return ResponseEntity.ok(updatedAppointment);
	}
	
	// Delete an appointment for a user
	
	@DeleteMapping("{id}/allappointments/{appid}/delete")
	public Map<String, Boolean> deleteAppointment(@PathVariable(value = "id") Long userid, @PathVariable(value = "appid") Long appid)
			throws ResourceNotFoundException {
		Appointment appointment = appointmentRepository.findAppointmentByIdAndUserId(userid, appid);
		
		if (appointment == null) throw new ResourceNotFoundException("Cannot find requested appointment :: " + appid);

		appointmentRepository.delete(appointment);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);

		return response;
	}
}
