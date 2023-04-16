package com.vttp2022.BicycleParkingApp.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.vttp2022.BicycleParkingApp.models.mongodb.Result;

@Repository
public class MongoRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  public Integer getAvailability(String image) throws Exception{
    //TODO - retrieve from MongoDB
    return null;
  }

  public Integer updateAvailability(Result r) throws Exception {
    //TODO - update MongoDB
    return null;
  }
  
}
