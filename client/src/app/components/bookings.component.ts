import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Bookings } from '../model';
import { ParkingService } from '../parking.service';

@Component({
  selector: 'app-bookings',
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.css']
})
export class BookingsComponent implements OnInit{

  bookings: Bookings[] = []
  bookingSize!: number

  errorMessage:any = null;

  constructor(private router: Router, private parkingSvc: ParkingService) { }

  ngOnInit(): void {

    if(this.getSessionUserEmail()===null) {
      this.setErrorMessage()
      this.router.navigate(['/'])
    }

    this.parkingSvc.getBookings(this.getSessionUserEmail())
      .then((bookings) => {
        this.bookings = bookings
        this.bookingSize = this.bookings.length
        console.info('>>> bookings: ', this.bookings)
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message
          console.error('>>> error: ', error)
          this.errorMessage=String(constErrorMessage)
          if(this.errorMessage === 'You have no bookings')
            this.bookingSize = 0
          //console.info('>>> error message is : ', this.errorMessage)
        }
      })
  }

  removeBooking(i: number) {
    let booking = this.bookings[i]
  }

  getSessionUserEmail() {
    const email:string = sessionStorage.getItem('email') as string
    return email
  }

  setErrorMessage() {
    sessionStorage.setItem('errorMessage', 'Login to view your bookings')
  }

  

}
