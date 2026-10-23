export interface Idata {
    workouts: Workout[];
}

export interface Workout {
    id:          number;
    nombre:      string;
    descripcion: string;
    nivel:       number;
    imagen:      string;
    ejercicios:  Ejercicio[];
}

export interface Ejercicio {
    nombre:      string;
    descripcion: string;
    tiempo:      number;
    imagen:      string;
}
