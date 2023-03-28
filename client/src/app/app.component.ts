import { Component, OnInit } from '@angular/core';
import { environment } from "../environments/environment";
import { getMessaging, getToken, onMessage } from "firebase/messaging";
import { Router } from '@angular/router';
import { map, Observable, of } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'bicycleparking';
  message:any = null;

  //name: string | null = null 
  name = sessionStorage.getItem('name')
  nameExist: boolean = false
  //constructor(private router: Router) {}
  ngOnInit(): void {
    
    this.requestPermission()
    //this.listen();
  }
  requestPermission() {
    const messaging = getMessaging();
    getToken(messaging,{ vapidKey: environment.firebase.vapidKey})
    .then(token => {
         if (token) {
           console.log(token);
           this.storeToken(token)
         } else {
           console.log('No registration token available. Request permission to generate one.');
         }
     }).catch(error => {
        console.log('Token Error >>> ', error);
    });
  }

  storeToken(token: string) {
    sessionStorage.setItem('token', token)
  }

  /*
  listen() {
    const messaging = getMessaging();
    onMessage(messaging, (payload) => {
      console.log('Message received. ', payload);
      this.message=payload;
    });
  }
  */

  
}
