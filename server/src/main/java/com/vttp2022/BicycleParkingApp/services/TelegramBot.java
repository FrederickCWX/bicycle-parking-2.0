package com.vttp2022.BicycleParkingApp.services;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.vttp2022.BicycleParkingApp.models.mysql.Bookings;
import com.vttp2022.BicycleParkingApp.repositories.UserParkingRepository;

import static com.vttp2022.BicycleParkingApp.repositories.Queries.*;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@Service
public class TelegramBot extends TelegramLongPollingBot{

    @Autowired
    private UserParkingRepository upRepo;

    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Override
    public void onUpdateReceived(Update update) {
        //System.out.println(update.getMessage().getText());
        //System.out.println(update.getMessage().getFrom().getFirstName());
        logger.info(String.format("(Telegram Bot) Message from %s >>> %s",update.getMessage().getFrom().getFirstName(),update.getMessage().getText()));
        
        String command = update.getMessage().getText();

        if(command.equals("/reservation")) {
            logger.info(String.format("User: %s is requesting for reservations list", update.getMessage().getFrom().getFirstName()));
            String message = "Please enter the email used to register on the web application";
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if(command.contains("@")) {
            String email = command;
            logger.info("Reservation request email >>> "+email);
            String message = "";

            try {
                logger.info("test");
                // UserParkingRepository upRepo = new UserParkingRepository();
                List<Bookings> bookingsList = upRepo.getBookings(email);
                //List<Bookings> bookingsList = UserParkingRepository.getBookings(email);
                //JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                
                if(bookingsList.size()<=0) {
                    message = "You have no bicycle parking reservation";
                }else {
                
                    StringBuilder sb = new StringBuilder();
                    sb.append("You have ");
                    sb.append(bookingsList.size());
                    sb.append(" bicycle parking reservation(s)\n");
                    for(Bookings b: bookingsList) {
                        sb.append("\nDate: ");
                        sb.append(b.getBookingDate());
                        sb.append("\nDescription: ");
                        sb.append(b.getDescription());
                        sb.append("\nRack Type: ");
                        sb.append(b.getRackType());
                        sb.append("\nRack Count: ");
                        sb.append(b.getRackCount());
                        sb.append("\nSheltered Bay: ");
                        sb.append(b.getSheltered());
                    }
                    message = sb.toString();
                }
            } catch (Exception e) {
                logger.error("ERROR >>> ");
                logger.error(e.getMessage());
            }

            logger.info(message);

            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

        /*
        if(command.contains("@")) {
            String email = command;
            logger.info("User email received >>> "+email);
            String message = "You have no bicycle parking reservation";
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        */

        //Testing Purpose
        /*
        if(command.contains("@")) {
            String email = command;
            logger.info("User email received >>> "+email);
            String message = "";
            if(email.equals("fcweixun@gmail.com")) {
                message = "You have 2 bicycle parking reservations\n\nDate/Time: 16 May 2023 @ 2.30pm\nDescription: 113 Lorong 1 Toa Payoh - Rack 1\nRack Type: HDB Racks\nSheltered bay: Unsheltered\n\nDate/Time: 25 June 2023 @ 9.00pm\nDescription: 115 Lorong 1 Toa Payoh - Rack 1\nRack Type: HDB Racks\nSheltered bay: Unsheltered";
            }else {
                message = "You have no bicycle parking reservation";
            }
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        */


        if(command.equals("/test")) {
            String message = "Bot Testing Successful";
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);

            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "BicycleParkingBot";
    }

    @Override
    public String getBotToken() {
        return "6143365163:AAEKyL6zfGPS_54MSEn8SfDV2gfi9udFv1U";
    }
  
}
