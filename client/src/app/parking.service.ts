import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { firstValueFrom, Observable} from "rxjs";
import { environment } from "src/environments/environment";
import { Bookings, Favourites, Results, UserDetails } from "./model";

@Injectable()
export class ParkingService {

  private apiServerUrl = environment.apiBaseUrl;

  results: Results[] = []

  constructor(private http: HttpClient, private router: Router) { }

  //User
  checkUserExist(email: string, password: string): Promise<UserDetails> {
    const headers = new HttpHeaders().set("email", email).set("password", password);
    
    return firstValueFrom(
      this.http.get<UserDetails>(`${this.apiServerUrl}/api/user`, {headers})
    )
    .then(result => {
      this.router.navigate(['/search'])
      return result
    })
  }

  registerNewUser(name: string, email: string, password: string, confirmPassword: string): Promise<string> {
    const headers = new HttpHeaders().set("name", name).set("email", email).set("password", password).set("confirmPassword", confirmPassword);

    return firstValueFrom(
      this.http.post<string>(`${this.apiServerUrl}/api/register`, null, {headers})
    )
    .then(result => {
      this.router.navigate(['/search'])
      return result
    })
    //return Promise.reject('Registration failed - Passwords do not match');
  }

  //Search | Results
  getResults(postal: number, radius: number): Promise<Results[]> {
    const params = new HttpParams().set("postal", postal).set("radius", radius)

    return firstValueFrom(
      this.http.get<Results[]>(`${this.apiServerUrl}/api/results`, {params})
    )
      .then(result => {
        console.info("Search Results >>> ", result);
        //this.router.navigate(['/results'])
        return result
      })
  }

  //Favourites
  getFavourites(email: string): Promise<Favourites[]> {
    const params = new HttpParams().set("email", email)

    return firstValueFrom(
      this.http.get<Favourites[]>(`${this.apiServerUrl}/api/favourites`, {params})
    )
    .then(result => {
      console.info("Favourites >>> ", result);
      return result
    })
  }
  
  addFavourites(results: Results, email: string): Promise<string> {
    const token = sessionStorage.getItem('token') as string
    const jsonString = JSON.stringify(results)
    const headers = new HttpHeaders().set("email", email).set("token", token)
    
    console.info('Headers >>> ', headers)

    return firstValueFrom(
      this.http.post<string>(`${this.apiServerUrl}/api/savefav`, jsonString, {headers})
    )
    .then(result => {
      console.info("Favourites saved status >>> ", result)
      return result
    })
  }

  /*
  deleteFavourite(favourite: Favourites, email: string): Observable<void> {
    const url = `${this.apiServerUrl}/api/favourites/${favourite.parkingId}/${email}`;
    return this.http.delete<void>(url);
  }
  */

  deleteFavourite(favourite: Favourites, email: string): Promise<string> {

    const headers = new HttpHeaders().set("email", email)
    const jsonString = JSON.stringify(favourite)
    console.info(jsonString)

    return firstValueFrom(
        this.http.delete<string>(`${this.apiServerUrl}/api/deletefav`,
        {
          headers: headers,
          body: jsonString
        })
    )
    .then(result => {
      console.info("Delete favourite status >>> ", result)
      return result
    })
  }
    
    // const httpOptions = {
    //   headers: new HttpHeaders({
    //     'email': email
    //   })
    // };

    // console.info('TO DELETE >>> ', favourite.parkingId)
    // console.info('Email >>> ', email)
    // const headers = new HttpHeaders().set("email", email)
    // const parkingId: string = favourite.parkingId as string
    // //console.info(parkingId)
    // const parkingEmail: string = parkingId+email
    // console.info(parkingEmail)
    // return firstValueFrom(
    //   this.http.delete<string>(`${this.apiServerUrl}/api/favourites/${parkingEmail}`)
    // )
  //}

  /*
  deleteFavourites(favourite: Favourites, email: string): Promise<string> {

    console.info('TO DELETE >>> ', favourite.parkingId)
    console.info('Email >>> ', email)
    const params = new HttpParams().set("email", email).set("parkingId", favourite.parkingId)
    //console.info(headers)

    return firstValueFrom(
      this.http.delete<string>(`${this.apiServerUrl}/api/deletefav`, {params})
    )
    .then(result => {
      console.info("Favourites delete status >>> ", result)
      return result
    })
  }
  */

  //Bookings
  addBooking(booking: Bookings, name: string): Promise<string> {
    const token = sessionStorage.getItem('token') as string
    const jsonString = JSON.stringify(booking)
    const headers = new HttpHeaders().set("token", token).set("name", name)
    
    console.info('Headers >>> ', headers)

    return firstValueFrom(
      this.http.post<string>(`${this.apiServerUrl}/api/savebooking`, jsonString, {headers})
    )
    .then(result => {
      console.info("Booking status >>> ", result)
      return result
    })
  }

  getBookings(email: string): Promise<Bookings[]> {
    const params = new HttpParams().set("email", email)

    return firstValueFrom(
      this.http.get<Bookings[]>(`${this.apiServerUrl}/api/bookings`, {params})
    )
    .then(result => {
      console.info("Bookings >>> ", result)
      return result
    })
  }

  removeBookings(booking: Bookings, name: string): Promise<string> {
    const token = sessionStorage.getItem('token') as string
    //const jsonString = JSON.stringify(booking)
    const headers = new HttpHeaders().set("token", token).set("name", name)
    console.info("Deleting booking...")

    return firstValueFrom(
      this.http.delete<string>(`${this.apiServerUrl}/api/deletebooking`,
        {
          headers,
          body: JSON.stringify(booking)
        })
    )
    .then(result => {
      console.info("Remove booking status >>> ", result)
      return result
    })
  }


  //MONGODB - available lots
  checkAvailability(image: string): Promise<Results[]>{
    const params = new HttpParams().set("image", image)
    
    return firstValueFrom(
      this.http.get<Results[]>(`${this.apiServerUrl}/api/check`, {params})
    )
    .then(result => {
      console.info("Availability lots >>> ", result)
      return result
    })
  }

  //to delete?
  minusAvailability(booking: Bookings): Promise<string> {
    const jsonString = JSON.stringify(booking)

    return firstValueFrom(
      this.http.post<string>(`${this.apiServerUrl}/api/updateminus`, jsonString)
    )
    .then(result => {
      console.info("Mongodb update status >>> ", result)
      return result
    })
  }

  addAvailability(booking: Bookings): Promise<string> {
    const jsonString = JSON.stringify(booking)

    return firstValueFrom(
      this.http.post<string>(`${this.apiServerUrl}/api/updateadd`, jsonString)
    )
    .then(result => {
      console.info("Mongodb update status >>> ", result)
      return result
    })
  }

  

}
