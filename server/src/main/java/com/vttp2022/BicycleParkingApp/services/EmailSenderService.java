package com.vttp2022.BicycleParkingApp.services;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

  private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class); 

  @Autowired
  private JavaMailSender mailSender;

  public void sendEmail(String toEmail, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("bicycleparkingapp@gmail.com");
    message.setTo(toEmail);
    message.setText(body);
    message.setSubject(subject);

    mailSender.send(message);

    logger.info("Mail Sent successfully to "+toEmail);
    
  }

  //The following location has been added to your favourites.\n\nWarm Regards,\nBicycle Parking Team

  public void sendFavouritesEmail(String toEmail, String example) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("bicycleparkingapp@gmail.com");
    message.setTo(toEmail);
    message.setText(String.format("The following location has been added to your favourites.\n\nWarm Regards,\nBicycle Parking Team", example));
    message.setSubject("Bicycle parking added to favourites");

    mailSender.send(message);

    logger.info("Mail Sent successfully to "+toEmail);
    
  }
  
}
