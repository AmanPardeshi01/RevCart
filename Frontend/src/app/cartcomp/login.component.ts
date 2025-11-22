import { Component } from '@angular/core';
import { Router } from '@angular/router';


@Component({
selector: 'app-login',
templateUrl: './login.component.html',
styleUrls: ['./login.component.css']
})
export class LoginComponent {
email = '';
password = '';
constructor(private router: Router) {}
login(){
// Frontend-only demo: just navigate to home or cart
console.log('login', this.email, this.password);
this.router.navigate(['/']);
}
}