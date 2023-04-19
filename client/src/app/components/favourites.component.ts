import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Favourites } from '../model';
import { ParkingService } from '../parking.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-favourites',
  templateUrl: './favourites.component.html',
  styleUrls: ['./favourites.component.css']
})
export class FavouritesComponent implements OnInit{

  favourites: Favourites[] = []
  favSize!: number 

  errorMessage:any = null;

  constructor(private router: Router, private parkingSvc: ParkingService, private location: Location) { }

  ngOnInit(): void {

    if(this.getSessionUserEmail()===null) {
      this.setErrorMessage()
      this.router.navigate(['/'])
    }

    this.parkingSvc.getFavourites(this.getSessionUserEmail())
      .then((favourites) => {
        this.favourites = favourites
        this.favSize = this.favourites.length
        console.info('>>> favourites: ', this.favourites)
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message
          console.error('>>> error: ', error)
          this.errorMessage=String(constErrorMessage)
        }
      })
  }
  
  deleteFavourite(i: number) {
    let favourite = this.favourites[i]
    const parkingId = favourite.parkingId as string
    const email = this.getSessionUserEmail() as string
    console.info('Fav Component: To delete >>> ', parkingId)
    
    this.parkingSvc.removeFavourite(parkingId, email)
    .then(result => {
      console.info('>>> Delete favourites status: ', result)
      this.refreshPage()
    })
    .catch(error => {
      console.error('>>> Error: ', error)
    })
  }

  getSessionUserEmail() {
    const email:string = sessionStorage.getItem('email') as string
    return email
  }

  setErrorMessage() {
    sessionStorage.setItem('errorMessage', 'Login to view your favourites')
  }

  refreshPage() {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['/favourites']);
    }); 
  }

}
