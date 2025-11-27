import { Injectable, signal, computed, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, of, delay, tap, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User, LoginCredentials, SignupData } from '../models/user.model';

interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  name: string;
  role: string;
}

interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSignal = signal<User | null>(null);
  private loadingSignal = signal<boolean>(true);

  // Computed signals
  user = this.userSignal.asReadonly();
  isAuthenticated = computed(() => this.userSignal() !== null);
  isLoading = this.loadingSignal.asReadonly();

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(
    private router: Router,
    private httpClient: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.initializeAuth();
  }

  private initializeAuth(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.loadingSignal.set(false);
      return;
    }

    const storedUser = localStorage.getItem('revcart_user');
    if (storedUser) {
      try {
        const user = JSON.parse(storedUser);
        this.userSignal.set(user);
      } catch (error) {
        console.error('Error parsing stored user:', error);
        localStorage.removeItem('revcart_user');
        localStorage.removeItem('revcart_token');
      }
    }

    this.loadingSignal.set(false);
  }

  login(credentials: LoginCredentials): Observable<User> {
    return this.httpClient.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      map((response) => {
        // Store token immediately
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('revcart_token', response.token);
        }
        return {
          id: response.userId.toString(),
          email: response.email,
          name: response.name,
          role: this.normalizeRole(response.role)
        } as User;
      }),
      tap((user) => {
        this.userSignal.set(user);
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('revcart_user', JSON.stringify(user));
        }
      })
    );
  }

  signup(data: SignupData): Observable<User> {
    const registerReq: RegisterRequest = {
      name: data.name,
      email: data.email,
      password: data.password
    };

    return this.httpClient.post<LoginResponse>(`${this.apiUrl}/register`, registerReq).pipe(
      map((response) => {
        // Store token immediately
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('revcart_token', response.token);
        }
        return {
          id: response.userId.toString(),
          email: response.email,
          name: response.name,
          phone: data.phone,
          role: 'customer' as const
        } as User;
      }),
      tap((user) => {
        this.userSignal.set(user);
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('revcart_user', JSON.stringify(user));
        }
      })
    );
  }

  private normalizeRole(backendRole: string): 'customer' | 'admin' | 'delivery_agent' {
    // Backend returns roles like "ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_DELIVERY"
    // Frontend expects "admin", "customer", "delivery_agent"
    const roleMap: { [key: string]: 'customer' | 'admin' | 'delivery_agent' } = {
      'ROLE_ADMIN': 'admin',
      'ROLE_CUSTOMER': 'customer',
      'ROLE_DELIVERY': 'delivery_agent',
      'admin': 'admin',
      'customer': 'customer',
      'delivery_agent': 'delivery_agent'
    };

    return roleMap[backendRole] || 'customer';
  }

  logout(): void {
    this.userSignal.set(null);

    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('revcart_user');
      localStorage.removeItem('revcart_token');
    }

    this.router.navigate(['/auth/login']);
  }

  hasRole(role: string): boolean {
    return this.userSignal()?.role === role;
  }
}

