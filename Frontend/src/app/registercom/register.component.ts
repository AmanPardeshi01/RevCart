import { Component } from '@angular/core';
import { Router } from '@angular/router';


@Component({
selector: 'app-register',
templateUrl: './register.component.html',
styleUrls: ['./register.component.css']
})
export class RegisterComponent{
name=''; email=''; password='';
constructor(private router:Router){}
register(){
console.log('register', this.name, this.email);
this.router.navigate(['/login']);
}
}