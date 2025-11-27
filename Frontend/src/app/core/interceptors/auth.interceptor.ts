import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor,
    HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        // Get the JWT token from localStorage
        const token = localStorage.getItem('revcart_token');

        // Clone the request and add the token if it exists
        if (token) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`
                }
            });
        }

        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 401) {
                    // Unauthorized - clear auth and redirect to login
                    this.authService.logout();
                    this.router.navigate(['/auth/login']);
                } else if (error.status === 403) {
                    // Forbidden - user doesn't have permission
                    console.error('Access forbidden:', error.message);
                    this.router.navigate(['/']);
                }
                return throwError(() => error);
            })
        );
    }
}
