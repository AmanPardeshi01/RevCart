import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, delay } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Product, Category } from '../models/product.model';
import { MOCK_PRODUCTS, MOCK_CATEGORIES } from '../../../assets/data/mock-data';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/products`;

  constructor(private httpClient: HttpClient) { }

  getProducts(filters?: {
    category?: string;
    search?: string;
    minPrice?: number;
    maxPrice?: number;
  }): Observable<Product[]> {
    // Try backend first, fallback to mock if not available
    let params = new HttpParams();

    if (filters?.search) {
      params = params.set('q', filters.search);
    }
    if (filters?.category) {
      params = params.set('category', filters.category);
    }

    return this.httpClient.get<any>(`${this.apiUrl}`, { params }).pipe(
      catchError((error) => {
        console.warn('Backend products unavailable, using mock data:', error);
        return this.getProductsMock(filters);
      })
    );
  }

  private getProductsMock(filters?: {
    category?: string;
    search?: string;
    minPrice?: number;
    maxPrice?: number;
  }): Observable<Product[]> {
    let products = [...MOCK_PRODUCTS];

    if (filters) {
      if (filters.category) {
        products = products.filter(p => p.categoryId === filters.category);
      }
      if (filters.search) {
        const search = filters.search.toLowerCase();
        products = products.filter(p =>
          p.name.toLowerCase().includes(search) ||
          p.description.toLowerCase().includes(search)
        );
      }
      if (filters.minPrice !== undefined) {
        products = products.filter(p => p.price >= filters.minPrice!);
      }
      if (filters.maxPrice !== undefined) {
        products = products.filter(p => p.price <= filters.maxPrice!);
      }
    }

    return of(products).pipe(delay(300));
  }

  getProductById(id: string): Observable<Product | undefined> {
    return this.httpClient.get<Product>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        console.warn('Backend product fetch failed, using mock:', error);
        const product = MOCK_PRODUCTS.find(p => p.id === id);
        return of(product).pipe(delay(200));
      })
    );
  }

  getCategories(): Observable<Category[]> {
    return of(MOCK_CATEGORIES);
  }

  getBestSellers(limit?: number): Observable<Product[]> {
    return this.getProducts().pipe(
      catchError(() => {
        const sorted = [...MOCK_PRODUCTS].sort((a, b) => {
          const scoreA = a.rating * a.reviews;
          const scoreB = b.rating * b.reviews;
          return scoreB - scoreA;
        });
        return of(limit ? sorted.slice(0, limit) : sorted);
      })
    );
  }

  getNewArrivals(limit?: number): Observable<Product[]> {
    return this.getProducts().pipe(
      catchError(() => {
        const newProducts = [...MOCK_PRODUCTS].reverse();
        return of(limit ? newProducts.slice(0, limit) : newProducts);
      })
    );
  }
}
