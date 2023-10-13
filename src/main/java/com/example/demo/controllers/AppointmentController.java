package com.example.demo.controllers;

import com.example.demo.repositories.*;
import com.example.demo.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AppointmentController {

    @Autowired
    AppointmentRepository appointmentRepository;

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments(){
        List<Appointment> appointments = new ArrayList<>();

        appointmentRepository.findAll().forEach(appointments::add);

        if (appointments.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") long id){
        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (appointment.isPresent()){
            return new ResponseEntity<>(appointment.get(),HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/appointment")
    public ResponseEntity<List<Appointment>> createAppointment(@RequestBody Appointment appointment){
    	
        /** TODO 
         * Implement this function, which acts as the POST /api/appointment endpoint.
         * Make sure to check out the whole project. Specially the Appointment.java class
         */
    	
    	// Verificacion de si el horario de inicio es anterior al horario de finalización
    	
    	if(appointment.getStartsAt().isAfter(appointment.getFinishesAt())) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	//Verificacion de sihay conflicto de citas
    	List<Appointment> appointments = new ArrayList<>();
    	appointmentRepository.findAll().forEach(appointments::add);
    	
    	for (Appointment appointment2 : appointments) {
    		
    		//Comprobar si est libre:
    		if (appointment2.getRoom() == appointment.getRoom() &&
    			    appointment.getFinishesAt().isBefore(appointment2.getStartsAt()) &&
    			    appointment.getStartsAt().isAfter(appointment2.getStartsAt())) {
    			    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    			}
    		
    		
    		//comprobacion si hay una cita en esa sala a esa hora
    		if(appointment2.getRoom() == appointment.getRoom() && appointment2.getStartsAt() == appointment.getStartsAt()) {
    			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    			
    		}
			
		}
    	//guardar la cita
    	appointmentRepository.save(appointment);
    	
        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }



    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<HttpStatus> deleteAppointment(@PathVariable("id") long id){

        Optional<Appointment> appointment = appointmentRepository.findById(id);

        if (!appointment.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        appointmentRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
        
    }

    @DeleteMapping("/appointments")
    public ResponseEntity<HttpStatus> deleteAllAppointments(){
        appointmentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
