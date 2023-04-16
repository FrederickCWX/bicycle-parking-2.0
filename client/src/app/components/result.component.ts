import { Component, Input, OnInit } from '@angular/core';
import { Form, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Bookings, Results } from '../model';
import { ParkingService } from '../parking.service';

@Component({
  selector: 'app-result',
  templateUrl: './result.component.html',
  styleUrls: ['./result.component.css']
})
export class ResultComponent implements OnInit{

  @Input()
  booking: Bookings | null=null

  bookingForm!: FormGroup

  results: Results[] = []
  resultSize!: number
  result: Results | null = null
  radius: string = String(sessionStorage.getItem('radius'))
  postal: string = String(sessionStorage.getItem('postal'))

  constructor(private fb: FormBuilder, private parkingSvc: ParkingService, private router: Router) { }

  ngOnInit(): void {
    this.bookingForm = this.createBooking(this.booking)
    this.results = this.parkingSvc.results
    this.resultSize = this.results.length
    sessionStorage.removeItem('postal')
    sessionStorage.removeItem('radius')
  }

  saveFavourites(i: number) {
    let result = this.results[i]
    console.info('Add to favourites (Result Component) >>> ', result)
    this.parkingSvc.addFavourites(result, this.getSessionUserEmail())
    .then(result => {
      console.info('>>> Save favourites status: ', result)
      this.router.navigate(['/favourites'])
    })
    .catch(error => {
      console.error('>>> Error: ', error)
    })
  }

  processBooking(i: number) {
    let result = this.results[i]
    const booking: Bookings = this.bookingForm.value as Bookings
    booking.description = result.description
    booking.image = result.image
    booking.rackCount = result.rackCount
    booking.rackType = result.rackType
    booking.sheltered = result.sheltered
    booking.email = this.getSessionUserEmail()
    console.info(booking)

    this.parkingSvc.addBooking(booking, sessionStorage.getItem('name') as string)
    .then(result => {
      console.info('>>> Booking status: ', result)
      this.router.navigate(['/booking'])

      //TODO add update to mongodb
    })
    .catch(error => {
      console.error('>>> Error: ', error)
    })
  }

  getSessionUserEmail() {
    const email:string = sessionStorage.getItem('email') as string
    return email
  }

  private createBooking(booking: Bookings | null = null): FormGroup {
    return this.fb.group({
      date: this.fb.control(booking?.bookingDate? booking.bookingDate: '', [Validators.required])
    })
  }

}
