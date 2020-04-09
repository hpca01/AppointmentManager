/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal;

import appointmentcal.models.Address;
import appointmentcal.models.Appointment;
import appointmentcal.models.City;
import appointmentcal.models.Country;
import appointmentcal.models.Customer;
import appointmentcal.models.User;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;

/**
 *
 * @author hp
 */
public class AppointmentManager {
    
    private static final Logger log = Logger.getLogger("AppointmentLog");

    private ObservableList<Address> addresses; //done
    private Address currentAddress;
    
    private ObservableList<Appointment> appointments; //done
    private Appointment currentAppointment;
    
    private ObservableList<City> cities; //done
    private City currentCity;
    
    private ObservableList<Country> countries;//done
    private Country currentCountry;
    
    private ObservableList<Customer> customers; //done
    private Customer currentCustomer;
    
    private ObservableList<User> users; //done
    private User currentUser;
    
    private LocalDate currentDate = null;
    
    private static void initiateLogging(){
        FileHandler fh;
        String userDir = System.getProperty("user.home");
        try{
           fh = new FileHandler(userDir+File.separator+"appointmentLog.log", true);
           log.addHandler(fh);
           SimpleFormatter formatter = new SimpleFormatter();
           fh.setFormatter(formatter);
        } catch (IOException ex) {
            Logger.getLogger(AppointmentManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(AppointmentManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Logger getLog() {
        return log;
    }
    
    private void periodicCustomerGet(){
        Task results = new Task<ArrayList<Customer>>(){
            @Override
            protected ArrayList<Customer> call() throws Exception {
                return Customer.getAllCustomers();
            }            
        };   
    }
    
    public ObservableList<Customer> getCustomers() {
        return customers;
    }

    public ObservableList<Address> getAddresses() {
        return addresses;
    }

    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    public ObservableList<City> getCities() {
        return cities;
    }

    public ObservableList<Country> getCountries() {
        return countries;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }    
    
    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setCurrentAppointment(Appointment currentAppointment) {
        this.currentAppointment = currentAppointment;
    }

    public Appointment getCurrentAppointment() {
        return currentAppointment;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public User getCurrentUser(){
        return currentUser;
    }
    
    private final ZoneId currentZone;
    
    
    public AppointmentManager() {
        this.addresses = AppointmentManager.getAllAddresses();
        this.appointments = AppointmentManager.getAllAppointments();
        this.cities = AppointmentManager.getAllCities();
        this.countries = AppointmentManager.getAllCountries();
        this.customers = AppointmentManager.getAllCustomers();
        this.users = AppointmentManager.getAllUsers();
        this.currentZone = ZoneId.systemDefault();
        initiateLogging();
    }
    
    public void refreshCustomers(){
        Platform.runLater(()->{
            this.customers.forEach(customer->customer.refresh());
        });
    }
    public void refreshCities(){
        Platform.runLater(()->{
            this.cities.setAll(AppointmentManager.getAllCities());
        });

    }
    public void refreshAddresses(){
        Platform.runLater(()->{
            this.addresses.forEach(address->address.refresh());
        });
    }
    public void refreshCountries(){
        Platform.runLater(()->{
            this.countries.setAll(AppointmentManager.getAllCountries());
        });
    }
    public void refreshAppointments(){
        Platform.runLater(()->{
            this.appointments.setAll(AppointmentManager.getAllAppointments());
        });
    }
    
    public Optional<User> validateUser(String usn, String pass){
        //lambda inside streams api for checking to see if usn/pass matches
        if (this.users.stream().anyMatch((u)->{
            return u.getUserName().equals(usn) && u.getPassword().equals(pass);
        })){
            Optional<User> user = this.users.filtered((u)->u.getUserName().equals(usn)).stream().findFirst();
            User u = Utils.getIfPresent(user);
            log.info("User "+u.getUserName()+" logged in at "+ZonedDateTime.now().toString());
            return user;
        }
        log.info("User with name "+usn+" had a failed log in at "+ZonedDateTime.now().toString());
        return Optional.empty();
    }
    
    public static ObservableList<Country> getAllCountries(){
        ObservableList<Country> countries = FXCollections.observableArrayList();
        
        Task results = new Task<ArrayList<Country>>(){
            @Override
            protected ArrayList<Country> call() throws Exception {
                return Country.getAllCountries();
            }            
        };
        new Thread(results).start();
        
        results.setOnSucceeded(event->{
            countries.setAll((ArrayList<Country>) results.getValue());
        });
        
        return countries;
    }
    
    public static ObservableList<City> getAllCities(){
        ObservableList<City> cities = FXCollections.observableArrayList();
        
        Task results = new Task<ArrayList<City>>(){
            @Override
            protected ArrayList<City> call() throws Exception {
                return City.getAllCities();
            }
        };
        
        new Thread(results).start();
        results.setOnSucceeded(event->{
            cities.setAll((ArrayList<City>) results.getValue());
        });
        
        return cities;
    }
    
    public static ObservableList<Address> getAllAddresses(){
        ObservableList<Address> addresses = FXCollections.observableArrayList();
        
        Task results = new Task<ArrayList<Address>>(){
            @Override
            protected ArrayList<Address> call() throws Exception {
                return Address.getAllAddresses();
            }
        };
        
        new Thread(results).start();
        results.setOnSucceeded(event->{
            addresses.addAll((ArrayList<Address>) results.getValue());
        });
        
        return addresses;
    }
    
    public static ObservableList<User> getAllUsers(){
        ObservableList<User> users = FXCollections.observableArrayList();
        
        Task results = new Task<ArrayList<User>>(){
            @Override
            protected ArrayList<User> call() throws Exception {
                return User.getAllUsers();
            }
        };
        new Thread(results).start();
        results.setOnSucceeded(event->{
            users.addAll((ArrayList<User>) results.getValue());
        });
        
        return users;
    }
    
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> rtns = FXCollections.observableArrayList();
        
        Task results = new Task<ArrayList<Customer>>() {
            @Override
            protected ArrayList<Customer> call() throws Exception {
                return Customer.getAllCustomers(); //To change body of generated methods, choose Tools | Templates.
            }
        };
        new Thread(results).start();
        //using lambda here to get customer array list and insert it into the observable list
        results.setOnSucceeded(event->{
            ArrayList<Customer> returned = (ArrayList<Customer>) results.getValue();
            rtns.addAll(returned);
        });
        
        return rtns;
    }

    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        
        Task results = new Task<ArrayList<Appointment>>(){
            @Override
            protected ArrayList<Appointment> call() throws Exception {
                return Appointment.getAllAppointments();
            }  
        };
        
        new Thread(results).start();
        results.setOnSucceeded(event->{
            appointments.addAll((ArrayList<Appointment>) results.getValue());
        });
        
        return appointments;
    }
}
