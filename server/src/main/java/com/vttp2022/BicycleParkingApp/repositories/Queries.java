package com.vttp2022.BicycleParkingApp.repositories;

public class Queries {

  //Table - user_details
  public static final String SQL_INSERT_USER_DETAILS = "insert ignore into user_details(name, password, email) values(?, sha1(?), ?)";

  //public static final String SQL_UPDATE_USER_FROM_USER_DETAILS = "insert ignore into user(user_details_id) select user_details_id from user_details;";

  public static final String SQL_CHECK_USER_DETAILS = "select * from userparking.user_details where email=? and password=sha1(?)";


  //Table - user_parking
  public static final String SQL_UPDATE_FAVOURITES_TO_USER_PARKING = "insert ignore into user_parking(email, parking_id) values(?, ?)";

  public static final String SQL_GET_FAVOURITES_ID_BY_USER_ID = "select parking_id from user_parking where email=? and parking_id is not null";

  public static final String SQL_DELETE_FAVOURITES_FROM_USER_PARKING = "delete from user_parking where email=? and parking_id=?";
  

  //Table - favourites
  public static final String SQL_ADD_FAVOURITES = "insert ignore into favourites(parking_id, image, description, rack_type, rack_count, sheltered) values(?, ?, ?, ?, ?, ?)";

  public static final String SQL_CHECK_FAVOURITES_ALREADY_EXIST = "select * from favourites where image=?";

  public static final String SQL_GET_FAVOURITES = "select * from favourites where parking_id=?;";


  //Table - favourites_coord
  public static final String SQL_ADD_FAVOURITES_COORD = "insert ignore into favourites_coord(parking_id, lat, lng) values(?, ?, ?)";

  public static final String SQL_GET_FAVOURITES_COORD = "select * from favourites_coord where parking_id=?";


  //Table - bookings
  public static final String SQL_GET_BOOKINGS_BY_USER_EMAIL = "select * from bookings where email=?";


  
}
