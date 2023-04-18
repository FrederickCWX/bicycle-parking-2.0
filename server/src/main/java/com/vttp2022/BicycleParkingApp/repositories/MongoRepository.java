package com.vttp2022.BicycleParkingApp.repositories;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.vttp2022.BicycleParkingApp.models.mongodb.BookingAvailability;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Repository
public class MongoRepository {

  public static final String C_BOOKING_BAY = "bookingbay";

  private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  public Integer getCurrentAvailability(String image) throws Exception{
    //TODO - retrieve from MongoDB - done?
    String now = LocalDate.now().toString();
    Criteria c = Criteria
        .where("image").is(image)
        .andOperator(
          Criteria.where("date").is(now)
        );
    Query q = Query.query(c);

    List<String> response = mongoTemplate.findDistinct(q, "availability", C_BOOKING_BAY, String.class);
    if(response.size()<=0) return -10;
    logger.info("Image >>> "+image+", Availability >>> "+response.get(0));
    return Integer.valueOf(response.get(0));
  }

  public Integer getAvailability(String image, String date) throws Exception{
    //TODO - retrieve from MongoDB - done?
    Criteria c = Criteria
        .where("image").is(image)
        .andOperator(
          Criteria.where("date").is(date)
        );
    Query q = Query.query(c);
    BookingAvailability response = mongoTemplate.findOne(q, BookingAvailability.class, C_BOOKING_BAY);
    Integer empty = -10;
    if(response == null) return empty;
    logger.info("Availability found >>> "+response.getAvailability());
    return Integer.valueOf(response.getAvailability());
  }

  public void insertAvailability(BookingAvailability ba) throws Exception {

    logger.info("Inserting to Mongo...");
    logger.info("Image >> " +ba.getImage());
    logger.info("Availability >> "+ba.getAvailability());
    logger.info("Date >> "+ba.getDate());

    //mongoTemplate.insert(ba, C_BOOKING_BAY);
    JsonObject json = Json.createObjectBuilder()
        .add("image", ba.getImage())
        .add("availability", ba.getAvailability())
        .add("date", ba.getDate())
        .build();
    
    logger.info(json.toString());
    
    mongoTemplate.insert(json.toString(), C_BOOKING_BAY);

    //Document bay = Document.parse(json.toString());
    //Document inserted = mongoTemplate.insert(bay, C_BOOKING_BAY);
    
    //logger.info("Inserted to MongoDB >>> "+inserted);
  }

  public void updateAvailability(BookingAvailability ba) throws Exception {
    Criteria c = Criteria
        .where("image").is(ba.getImage())
        .andOperator(
          Criteria.where("date").is(ba.getDate())
        );
    Query query = Query.query(c);
    Update update = new Update().set("availability", ba.getAvailability());

    mongoTemplate.updateFirst(query, update, C_BOOKING_BAY);
  }
  
}
