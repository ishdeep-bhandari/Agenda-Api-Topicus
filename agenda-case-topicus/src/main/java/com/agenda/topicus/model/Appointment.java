package com.agenda.topicus.model;

import javax.persistence.*;

import com.sun.istack.NotNull;

import java.sql.Time;
import java.util.*;

@Entity
@Table(name = "appointments")
public class Appointment {

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long appointmentid;
	
	@Column( name = "appointmentname")
	@NotNull
	private String appointmentName;
	
	@Column( name = "appointmentmessage")
	private String appointmentMessage;
	
	@Column( name = "appointmentdate")
	@NotNull
	private java.sql.Date appointmentDate;
	
	@Column( name = "appointmentstarttime")
	@NotNull
	private java.sql.Time startTime;
	
	@Column( name = "appointmentendtime")
	@NotNull
	private java.sql.Time endTime;

    @ManyToOne
    @JoinColumn(name ="appointmentownerid")
	@NotNull
    private User appointmentOwnerId;
    
    @ManyToOne
    @JoinColumn(name ="appointmentadditionid")
	@NotNull
	private User appointmentAdditionId;
	
	public Appointment() {
		super();
	}
	
	public Appointment(String appointmentName, String appointmentMessage, java.sql.Date appointmentDate,
			Time startTime, Time endTime, User appointmentOwnerId, User appointmentAdditionId) {
		super();
		this.appointmentName = appointmentName;
		this.appointmentMessage = appointmentMessage;
		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.appointmentOwnerId = appointmentOwnerId;
		this.appointmentAdditionId = appointmentAdditionId;
	}

	public long getId() {
		return appointmentid;
	}
	public void setId(long appointmentid) {
		this.appointmentid = appointmentid;
	}

	public String getAppointmentName() {
		return appointmentName;
	}

	public void setAppointmentName(String appointmentName) {
		this.appointmentName = appointmentName;
	}
	
	public String getAppointmentMessage() {
		return appointmentMessage;
	}
	public void setAppointmentMessage(String appointmentMessage) {
		this.appointmentMessage = appointmentMessage;
	}
	
	public long getAppointmentid() {
		return appointmentid;
	}
	public void setAppointmentid(long appointmentid) {
		this.appointmentid = appointmentid;
	}
	public java.sql.Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(java.sql.Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public java.sql.Time getStartTime() {
		return startTime;
	}
	public void setStartTime(java.sql.Time startTime) {
		this.startTime = startTime;
	}
	public java.sql.Time getEndTime() {
		return endTime;
	}
	public void setEndTime(java.sql.Time endTime) {
		this.endTime = endTime;
	}
	
	public void setAppointmentOwnerId(User appointmentOwnerId) {
		this.appointmentOwnerId = appointmentOwnerId;
	}

	public void setAppointmentAdditionId(User appointmentAdditionId) {
		this.appointmentAdditionId = appointmentAdditionId;
	}

	public User getAppointmentOwnerId() {
		return appointmentOwnerId;
	}
	public User getAppointmentAdditionId() {
		return appointmentAdditionId;
	}
	
}
