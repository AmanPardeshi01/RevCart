import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, delay } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Order, OrderItem } from '../models/order.model';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = `${environment.apiUrl}/orders`;

    private mockOrders: Order[] = [
        {
            id: 'ORD-001',
            date: '2024-01-15',
            status: 'delivered',
            items: [
                { id: '1', name: 'Fresh Tomatoes', quantity: 2, price: 2.99 },
                { id: '2', name: 'Organic Bananas', quantity: 1, price: 1.99 }
            ],
            total: 7.97,
            deliveryAddress: '123 Main St, City, State 12345'
        }
    ];

    constructor(private httpClient: HttpClient) { }

    getAllOrders(): Observable<Order[]> {
        return this.httpClient.get<Order[]>(this.apiUrl).pipe(
            catchError((error) => {
                console.warn('Backend orders unavailable, using mock data:', error);
                return of(this.mockOrders).pipe(delay(300));
            })
        );
    }

    getUserOrders(userId: string): Observable<Order[]> {
        return this.httpClient.get<Order[]>(this.apiUrl).pipe(
            catchError((error) => {
                console.warn('Backend user orders unavailable, using mock data:', error);
                return of(this.mockOrders).pipe(delay(300));
            })
        );
    }

    getOrderById(orderId: string): Observable<Order | undefined> {
        return this.httpClient.get<Order>(`${this.apiUrl}/${orderId}`).pipe(
            catchError((error) => {
                console.warn('Backend order fetch failed, using mock:', error);
                const order = this.mockOrders.find(o => o.id === orderId);
                return of(order).pipe(delay(200));
            })
        );
    }

    createOrder(orderData: {
        items: OrderItem[];
        total: number;
        deliveryAddress: string;
    }): Observable<Order> {
        return this.httpClient.post<Order>(this.apiUrl, orderData).pipe(
            catchError((error) => {
                console.warn('Backend order creation failed, using mock:', error);
                const newOrder: Order = {
                    id: `ORD-${String(this.mockOrders.length + 1).padStart(3, '0')}`,
                    date: new Date().toISOString().split('T')[0],
                    status: 'processing',
                    ...orderData
                };

                this.mockOrders.unshift(newOrder);
                return of(newOrder).pipe(delay(500));
            })
        );
    }

    updateOrderStatus(orderId: string, status: Order['status']): Observable<Order> {
        return this.httpClient.put<Order>(`${this.apiUrl}/${orderId}`, { status }).pipe(
            catchError((error) => {
                console.warn('Backend order status update failed, using mock:', error);
                const order = this.mockOrders.find(o => o.id === orderId);
                if (order) {
                    order.status = status;
                }
                return of(order!).pipe(delay(300));
            })
        );
    }

    cancelOrder(orderId: string): Observable<boolean> {
        return this.httpClient.delete<boolean>(`${this.apiUrl}/${orderId}`).pipe(
            catchError((error) => {
                console.warn('Backend order cancellation failed, using mock:', error);
                const order = this.mockOrders.find(o => o.id === orderId);
                if (order) {
                    order.status = 'cancelled';
                    return of(true).pipe(delay(300));
                }
                return of(false).pipe(delay(300));
            })
        );
    }
}
