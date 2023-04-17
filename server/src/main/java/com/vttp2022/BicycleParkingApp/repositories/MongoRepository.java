package com.vttp2022.BicycleParkingApp.repositories;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.vttp2022.BicycleParkingApp.models.mongodb.Result;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Repository
public class MongoRepository {

  public static final String C_BOOKING_BAY = "bookingbay";

  private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  public Integer getAvailability(String image) throws Exception{
    //TODO - retrieve from MongoDB - done?
    Criteria c = Criteria.where("image").is(image);
    Query q = Query.query(c);

    List<String> response = mongoTemplate.findDistinct(q, "availability", C_BOOKING_BAY, String.class);
    if(response.size()<=0) return -10;
    return Integer.valueOf(response.get(0));
    //return null;
  }

  public void insertAvailability(Result r) throws Exception {
    //TODO - insert data into MongoDB - done?
    JsonObject json = Json.createObjectBuilder()
        .add("image", r.getImage())
        .add("availability", r.getAvailability())
        .build();

    Document bay = Document.parse(json.toString());
    Document inserted = mongoTemplate.insert(bay, C_BOOKING_BAY);
    
    logger.info("Inserted to MongoDB >>> "+inserted);
  }

  public void updateAvailability(Result r) throws Exception {
    Query query = Query.query(Criteria.where("image").is(r.getImage()));
    //TODO - update data into MongoDB
  }
  
}
