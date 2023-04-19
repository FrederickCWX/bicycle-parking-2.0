package com.vttp2022.BicycleParkingApp.services;

import java.util.LinkedList;
import java.util.List;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.vttp2022.BicycleParkingApp.models.mysql.Bookings;
import com.vttp2022.BicycleParkingApp.repositories.UserParkingRepository;

public class Tele{

  @Autowired
  private UserParkingRepository upRepo;
  
  //private List<Bookings> bookingsList = new LinkedList<>();
    
  public static List<Bookings> getBookings(String email){
    Tele tele = new Tele();
    tele.upRepo = new UserParkingRepository();
    return tele.upRepo.getTeleBookings(email);
  }
}

