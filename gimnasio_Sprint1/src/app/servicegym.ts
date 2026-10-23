import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Workout } from './idata';

@Injectable({
    providedIn: 'root',
})
export class Servicegym {

    private readonly httpClient = inject(HttpClient);
    readonly API_URL = 'http://127.0.0.1:3000/workouts';

    getEjercicios() {
        return this.httpClient.get<Workout[]>(this.API_URL);
    }
}
