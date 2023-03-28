import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Favourites } from '../model';
import { ParkingService } from '../parking.service';

@Component({
  selector: 'app-favourites',
  templateUrl: './favourites.component.html',
  styleUrls: ['./favourites.component.css']
})
export class FavouritesComponent implements OnInit{

  favourites: Favourites[] = []
  favSize!: number 

  errorMessage:any = null;

  constructor(private router: Router, private parkingSvc: ParkingService) { }

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
          //console.info('>>> error message is : ', this.errorMessage)
        }
      })
  }

  /*
  deleteFavourite(i: number) {
    let favourite = this.favourites[i]
    this.parkingSvc.deleteFavourite(favourite, this.getSessionUserEmail()).subscribe(
      () => {
        console.log('Example deleted successfully');
      },
      error => {
        console.error('Error deleting example:', error);
      }
    );
  }
  */
  
  deleteFavourite(i: number) {
    let favourite = this.favourites[i]
    console.info('Delete from favourites >>> ', favourite)
    console.info('User to delete from >>>', this.getSessionUserEmail())
    this.parkingSvc.deleteFavourite(favourite, this.getSessionUserEmail())
    .then(result => {
      console.info('>>> Delete favourites status: ', result)
      //this.router.navigate(['/favourites'])
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



  /*

  params$!: Subscription
  favourites!: Favourites

  constructor(private activatedRoute: ActivatedRoute, private parkingSvc: ParkingService) { }

  ngOnInit(): void {

    this.params$ = this.activatedRoute.params.subscribe(
      (params) => {
        const favouritesId = params['favouritesId']
        this.parkingSvc.getFavourites()
          .then(result => {
            this.favourites = result
            console.info('>>> favourites: ', this.favourites)
          })
          .catch(error => {
            console.error('>> error: ', error)
          })
      }
    )
      
  }
  */

}