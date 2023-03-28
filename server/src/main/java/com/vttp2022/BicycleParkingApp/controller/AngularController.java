package com.vttp2022.BicycleParkingApp.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vttp2022.BicycleParkingApp.models.ErrorResponse;
import com.vttp2022.BicycleParkingApp.models.mysql.Favourites;
import com.vttp2022.BicycleParkingApp.models.mysql.UserDetails;
import com.vttp2022.BicycleParkingApp.models.parking.Parkings;
import com.vttp2022.BicycleParkingApp.models.parking.Query;
import com.vttp2022.BicycleParkingApp.models.parking.Value;
import com.vttp2022.BicycleParkingApp.models.postal.Postal;
import com.vttp2022.BicycleParkingApp.models.postal.Results;
import com.vttp2022.BicycleParkingApp.repositories.UserParkingRepository;
import com.vttp2022.BicycleParkingApp.services.ParkingAPIService;
import com.vttp2022.BicycleParkingApp.services.PostalAPIService;
import com.vttp2022.BicycleParkingApp.services.WebNotificationService;
import com.vttp2022.BicycleParkingApp.utilities.CleanString;
import com.vttp2022.BicycleParkingApp.utilities.SortByDistance;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path="/api", 
produces = MediaType.APPLICATION_JSON_VALUE)
public class AngularController {

  private static final Logger logger = LoggerFactory.getLogger(AngularController.class);

  @Autowired
  private UserParkingRepository upRepo;

  @Autowired
  private ParkingAPIService parkingSvc;

  @Autowired
  private PostalAPIService postalSvc;

  @Autowired
  private WebNotificationService wnSvc;

  //Login
  @GetMapping(path="/user")
  public ResponseEntity<?> validifyUser(@RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "password", required = true) String password) throws Exception {

    UserDetails ud = new UserDetails();
    ud = upRepo.checkAndReturnUserCredentials(email, password);

    if(ud == null){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Invalid email/password");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    JsonObject udJson = ud.toUserDetails();
    return ResponseEntity.ok(udJson.toString());
  }

  //Register
  @PostMapping(path="/register")
  public ResponseEntity<?> registration(@RequestHeader(value = "name", required = true) String name, @RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "password", required = true) String password, @RequestHeader(value = "confirmPassword", required = true) String confirmPassword) throws Exception {

    String cleanName = CleanString.cleanName(name);

    if(!password.equals(confirmPassword)) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Passwords do not match");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    Integer success = upRepo.createUser(cleanName, password, email.toLowerCase());

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Email already exist");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }

  //Favourites
  
  @GetMapping(path="/favourites") 
  public ResponseEntity<?> getFavourites(@RequestParam(value = "email", required = true) String email) throws Exception {

    if(upRepo.getUserFavourites(email) == null) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("You have 0 saved bicycle parking location(s)");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    upRepo.getUserFavourites(email).stream()
        .forEach(f -> {
          arrayBuilder.add(f.toFavourites());
        });

    return ResponseEntity.ok(arrayBuilder.build().toString());
  }
  

  //@RequestHeader(value = "result", required = true) String result, 
  @PostMapping(path="/savefav")
  public ResponseEntity<?> addFavourites(@RequestBody String result, @RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "token", required = true) String token) throws Exception {

    Favourites favourites = Favourites.createJson(result);
    Favourites checkFavourites = upRepo.checkFavouritesExist(favourites.getImage());
    Integer success;

    if(checkFavourites != null) {
      success = upRepo.addExistingFavourites(checkFavourites.getParkingId(), email);
    } else {
      success = upRepo.addNewFavourites(favourites, email);
    }

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Failed to save to favourites");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      wnSvc.sendFavNotification(favourites.getDescription(), token);
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
    
  }

  /*
  @DeleteMapping(path="/deletefav/{parkingId}")
  public ResponseEntity<?> deleteFavourites(@PathVariable String parkingId, @RequestHeader(value = "email", required = true) String email) throws Exception {

    logger.info("ParkingID >>> "+ parkingId);
    logger.info("Email >>> "+email);
    Integer success = upRepo.deleteFavourites(parkingId, email);

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Failed to remove from favourites");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }
  */
  
  @DeleteMapping(path="/deletefav")
  public ResponseEntity<?> deleteFavourites(@RequestHeader(value = "parkingId", required = true) String parkingId, @RequestHeader(value = "email", required = true) String email) throws Exception {

    logger.info("ParkingID >>> "+ parkingId);
    logger.info("Email >>> "+email);
    Integer success = upRepo.deleteFavourites(parkingId, email);

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Failed to remove from favourites");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }

  //Search | results
  @GetMapping("/results")
  public ResponseEntity<?> searchParking(@RequestParam(value = "postal", required = true) String postal, @RequestParam(value = "radius", required = true) String radius){

    Query q = new Query();
    Optional<Postal> optPostal = postalSvc.getPostalDetails(Integer.parseInt(postal));

    if(Postal.getFound() == 0){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Postal code invalid, please key in a valid postal code");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    List<Results> results = Postal.getResults();
    if(results.size() >= 1){
      q.setLat(results.get(0).getLatitude());
      q.setLng(results.get(0).getLongitude());
    }
    q.setRadius(Double.parseDouble(radius));
    Optional<Parkings> optParking = parkingSvc.findParking(q);

    if(optParking.isEmpty()){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("No bicycle parking bays found");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    Collections.sort(Parkings.getValue(), new SortByDistance());
    List<Value> val = Parkings.getValue();

    StringBuilder sb = new StringBuilder();
    sb.append("There are ");
    sb.append(val.size());
    sb.append(" bicycle parking bay(s) within ");
    sb.append((int) (q.getRadius()*1000));
    sb.append(" metres of ");
    sb.append(results.get(0).getAddress());
    sb.append(", Singapore ");
    sb.append(postal);
    Parkings.setInfo(sb.toString());
    String info = sb.toString();

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    val.stream()
        .forEach(f -> {
          arrayBuilder.add(f.toResults());
        });
    
    return ResponseEntity.ok(arrayBuilder.build().toString());

    /*
    model.addAttribute("respDetails", Parkings.getInfo());
    if(val.size() > 0){
      model.addAttribute("details", val);
    }

    model.addAttribute("Radius", q.getRadius());

    logger.info("Search successful, results > " + Parkings.getInfo());

    return "result";
    */
  }

  //Bookings
  @GetMapping(path="/bookings") 
  public ResponseEntity<?> getBookings(@RequestParam(value = "email", required = true) String email) throws Exception {

    if(upRepo.getBookings(email) == null) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("You have no bookings");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    upRepo.getBookings(email).stream()
        .forEach(b -> {
          arrayBuilder.add(b.toBookings());
        });

    return ResponseEntity.ok(arrayBuilder.build().toString());

  }




  /*
  @PostMapping(path="/favourites/save", consumes = {"application/json"})
  public ResponseEntity<?> addFavourites(@RequestBody MultiValueMap<String, String> result, @RequestHeader(value = "email", required = true) String email) throws Exception {

    logger.info("Attempting to add favourites, User: "+email);
    logger.info(result.toString());

    Coord coord = new Coord();
    coord.setLat(new BigDecimal(result.getFirst("lat")));
    coord.setLng(new BigDecimal(result.getFirst("lng")));

    Favourites favourites = new Favourites();
    favourites.setParkingId(result.getFirst("parkingId"));
    favourites.setImage(result.getFirst("image"));
    favourites.setDescription(result.getFirst("description"));
    favourites.setRackType(result.getFirst("rackType"));
    favourites.setRackCount(Integer.parseInt(result.getFirst("rackCount")));
    favourites.setCoord(coord);

    Favourites checkFavourites = upRepo.checkFavouritesExist(favourites.getImage());

    Integer success;

    if(checkFavourites != null) {
      success = upRepo.addExistingFavourites(checkFavourites.getParkingId(), email);
    }

    success = upRepo.addNewFavourites(favourites, email);

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Failed to save to favourites");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }
  */

  /*
  @PostMapping(path="/favourites/save")
  public ResponseEntity<?> addFavourites(
      @RequestParam(value = "id", required = true) String id,
      @RequestParam(value = "image", required = true) String image,
      @RequestParam(value = "description", required = true) String description,
      @RequestParam(value = "rackType", required = true) String rackType,
      @RequestParam(value = "rackCount", required = true) Integer rackCount,
      @RequestParam(value = "sheltered", required = true) String sheltered,
      @RequestParam(value = "lat", required = true) String lat,
      @RequestParam(value = "sheltered", required = true) String lng,
      @RequestParam(value = "email", required = true) String email
      ) throws Exception {

    Favourites favourites = new Favourites();
    favourites = upRepo.checkFavouritesExist(image);

    if(favourites == null) {
      Favourites f = new Favourites();
      upRepo.addFavourites(favourites, email)
    }

    return null;
  }
  */

} 
