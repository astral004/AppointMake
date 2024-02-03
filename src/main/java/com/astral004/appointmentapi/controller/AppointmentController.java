package com.astral004.appointmentapi.controller;

import com.astral004.appointmentapi.domain.Appointment;
import com.astral004.appointmentapi.domain.Clinic;
import com.astral004.appointmentapi.domain.Doctor;
import com.astral004.appointmentapi.domain.Patient;
import com.astral004.appointmentapi.exception.AppointmentDoesNotExistException;
import com.astral004.appointmentapi.exception.DateNotAcceptableException;
import com.astral004.appointmentapi.exception.NullParametersException;
import com.astral004.appointmentapi.repository.AppointmentRepository;
import com.astral004.appointmentapi.repository.ClinicRepository;
import com.astral004.appointmentapi.repository.DoctorRepository;
import com.astral004.appointmentapi.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@RestController
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ClinicRepository clinicRepository;

    /**
     * Basic GET method that will be invoked by accessing the /appointments with no parameters.
     * Will return all appointments that currently exist in the db.
     * @return List of appointments scheduled
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments(){
        try {
            List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAll());
            if (appointments.isEmpty()) {
                throw new AppointmentDoesNotExistException("Appointments do not exist in the DB!");
            }
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e){
            if(e instanceof AppointmentDoesNotExistException) throw e;
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET mapping to retrieve appointments by clinic, doctor, and/or patient. If no parameters are supplied,
     * uses the default GET mapping to return all.
     * @param cID Clinic id
     * @param dID Doctor id
     * @param pID Patient id
     * @return List of appointments matching the criteria
     */
    @GetMapping("/appointmentsByValue")
    public ResponseEntity<List<Appointment>> getAppointmentsByValue(@RequestParam(required = false) String cID,
                                                                    @RequestParam(required = false) String dID,
                                                                    @RequestParam(required = false) String pID){
        List<Appointment> appointments = new ArrayList<>();
        if (cID == null && dID == null && pID == null){
            return getAllAppointments();
        }

        try {
            if(cID != null && dID != null && pID != null){
                appointments.addAll(appointmentRepository.findAppointmentsByClinicIdAndDoctorIdAndPatientId(cID, dID, pID));
            }
            else if(cID != null && dID != null && pID == null){
                appointments.addAll(appointmentRepository.findAppointmentsByClinicIdAndDoctorId(cID,dID));
            }
            else if(cID != null && dID == null && pID != null){
                appointments.addAll(appointmentRepository.findAppointmentsByClinicIdAndPatientId(cID,pID));
            }
            else if(cID == null && dID != null && pID != null){
                appointments.addAll(appointmentRepository.findAppointmentsByDoctorIdAndPatientId(dID,pID));
            }
            else if (cID != null){
                appointments.addAll(appointmentRepository.findAppointmentsByClinicId(cID));
            }
            else if(dID != null){
                appointments.addAll(appointmentRepository.findAppointmentsByDoctorId(dID));
            }
            else {
                appointments.addAll(appointmentRepository.findAppointmentsByPatientId(pID));
            }

            if(appointments.isEmpty()){
                throw new AppointmentDoesNotExistException("Appointments for PID: "+pID+", Doctor Id: "+dID+", and Clinic Id: "+cID+"not found!");
            }

            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } catch (Exception e) {
            if(e instanceof AppointmentDoesNotExistException) throw e;
            else return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET mapping to return a specific appointment based on the appointment id
     * @param id Appointment Id
     * @return Appointment in the form of a javascript object, and an HTTP status.
     */
    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentByID(@PathVariable("id") String id){
        Optional<Appointment> apptData = appointmentRepository.findById(id);
        return apptData.map(appointment -> new ResponseEntity<>(appointment, HttpStatus.OK))
                       .orElseThrow(() -> new AppointmentDoesNotExistException("Appointment with id: "+id+"not found!"));
    }

    /**
     * POST mapping to create a new appointment. Validates that the date is in the future and that the patient, doctor,
     * and clinic actually exist.
     * @param appointment The appointment to be added
     * @return A javascript representing the appointment created, and an HTTP status.
     */
    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment){
        Appointment _appointment = new Appointment();
        copyAppointment(appointment, _appointment);
        //Date validation
        if (_appointment.getTiming().isBefore(LocalDate.now())){
            throw new DateNotAcceptableException("This is not a future date!");
        }
        //check if patient, doctor, and clinic exist
        Optional<Patient> patient = patientRepository.findById(_appointment.getPatientId());
        Optional<Doctor> doctor = doctorRepository.findById(_appointment.getDoctorId());
        Optional<Clinic> clinic = clinicRepository.findById(_appointment.getClinicId());
        if (!patient.isPresent() || !doctor.isPresent() || !clinic.isPresent()){
            throw new NullParametersException("Appointment cannot be created as the patient, doctor, or clinic could not be found!");
        }
        try{
            _appointment.setStatus("Scheduled");
            return new ResponseEntity<>(appointmentRepository.save(_appointment), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *  PUT mapping to update an existing appointment
     * @param id ID of the appointment to be updated
     * @param appointment The new values to be added to the appointment
     * @return The updated appointment, and an HTTP status
     */
    @PutMapping("/appointments/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable("id")String id, @RequestBody Appointment appointment){
        Optional<Appointment> apptData = appointmentRepository.findById(id);

        if(apptData.isPresent()){
            Appointment _appointment = apptData.get();
            LocalDate tempTime = LocalDate.now();
            if(tempTime.isAfter(_appointment.getTiming().minusDays(2)) && tempTime .isBefore(_appointment.getTiming())){
                throw new DateNotAcceptableException("Cannot reschedule within a 48 hour window!");
            }
            copyAppointment(appointment, _appointment);
            _appointment.setStatus("Rescheduled");
            try {
                return new ResponseEntity<>(appointmentRepository.save(_appointment), HttpStatus.OK);
            } catch (Exception e){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            throw new AppointmentDoesNotExistException("The requested Appointment does not yet exist in the system!");
        }
    }

    /**
     * DELETE mapping to soft delete appointments by setting their status to Cancelled
     * @param id ID of appointment to cancel
     * @return Cancelled appointment, and an HTTP status.
     */
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Appointment> deleteAppointmentByID(@PathVariable("id") String id){
        Optional<Appointment> apptData = appointmentRepository.findById(id);
        if(apptData.isPresent()) {
            Appointment _appointment = apptData.get();
            LocalDate tempTime = LocalDate.now();
            if(tempTime.isAfter(_appointment.getTiming().minusDays(2)) && tempTime.isBefore(_appointment.getTiming())){
                throw new DateNotAcceptableException("Cannot cancel within a 48 hour window!");
            }
            _appointment.setStatus("Cancelled");
            try {
                return new ResponseEntity<>(appointmentRepository.save(_appointment), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            throw new AppointmentDoesNotExistException("Appointment does not exist!");
        }
    }

    /**
     * Helper method to copy received appointment body into a new appointment object to be persisted
     * @param oldAppointment appointment passed in through HTTP
     * @param newAppt appointment to be persisted
     */
    private void copyAppointment(Appointment oldAppointment, Appointment newAppt) {
        newAppt.setId(oldAppointment.getId());
        newAppt.setPatientId(oldAppointment.getPatientId());
        newAppt.setDoctorId(oldAppointment.getDoctorId());
        newAppt.setClinicId(oldAppointment.getClinicId());
        newAppt.setTiming(oldAppointment.getTiming());
        newAppt.setDescription(oldAppointment.getDescription());
        newAppt.setInsuranceId(oldAppointment.getInsuranceId());
        newAppt.setCopay(oldAppointment.getCopay());
        newAppt.setStatus(oldAppointment.getStatus());
    }

}
