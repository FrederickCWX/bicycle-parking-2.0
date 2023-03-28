import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { getMessaging, onMessage } from 'firebase/messaging';
import { LoginAttempt } from '../model';
import { ParkingService } from '../parking.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{

  @Input()
  loginAttempt: LoginAttempt | null = null

  loginForm!: FormGroup

  errorMessage:any = null;

  constructor(private fb: FormBuilder, private parkingSvc: ParkingService, private router: Router) { }

  ngOnInit(): void {
      this.loginForm = this.createLogin(this.loginAttempt)
  }

  processLogin() {
    const loginAttempt: LoginAttempt = this.loginForm.value as LoginAttempt
    this.parkingSvc.checkUserExist(loginAttempt.email, loginAttempt.password)
      .then(result => {
        console.info('>>> Login result: ', result)
        sessionStorage.setItem('name', result.name)
        sessionStorage.setItem('email', result.email)
        this.router.navigate(['/search'])
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          console.error(`Backend returned code ${error.status}, body was: ${error.error}`);
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message;
          console.error(`Error message: ${constErrorMessage}`);
          console.error('>>> error: ', error)
          this.errorMessage=String(constErrorMessage)
          this.listen();
        }
        //console.info('>>> error: ', error)
      })
  }

  listen() {
    const messaging = getMessaging();
    onMessage(messaging, (payload) => {
      console.log('Message received. ', payload);
      this.errorMessage=payload;
    });
  }

  private createLogin(loginAttempt: LoginAttempt | null=null): FormGroup {
    return this.fb.group({
      email: this.fb.control(loginAttempt?.email? loginAttempt.email: '', [Validators.required, Validators.email]),
      password: this.fb.control(loginAttempt?.password? loginAttempt.password:'', [Validators.required])
    })
  }

}
