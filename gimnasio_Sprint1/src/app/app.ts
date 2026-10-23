import { Component, inject, OnInit, signal } from '@angular/core';
import { Ejercicio, Workout } from './idata';
import { Servicegym } from './servicegym';
import { FormsModule } from '@angular/forms';

@Component({
  imports: [FormsModule],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {
  public gimnasioService = inject(Servicegym);
  public ejercicios = signal<Workout[]>([]);
  public nivelSeleccionado: string = '';
  public rutinaSeleccionada: number | null = null;

  public obtenerNiveles(): number[] {
    const niveles: number[] = [];
    const rutinas = this.ejercicios();

    for (let i = 0; i < rutinas.length; i++) {
      if (niveles.indexOf(rutinas[i].nivel) === -1) {
        niveles.push(rutinas[i].nivel);
      }
    }

    for (let i = 0; i < niveles.length; i++) {
      for (let j = i + 1; j < niveles.length; j++) {
        if (niveles[i] > niveles[j]) {
          const nivelTemporal = niveles[i];
          niveles[i] = niveles[j];
          niveles[j] = nivelTemporal;
        }
      }
    }

    return niveles;
  }

  public obtenerRutinasFiltradas(): Workout[] {
    const rutinas = this.ejercicios();
    const rutinasFiltradas: Workout[] = [];

    for (let i = 0; i < rutinas.length; i++) {
      const rutina = rutinas[i];
      const coincideNivel =
        this.nivelSeleccionado === '' || rutina.nivel.toString() === this.nivelSeleccionado;

      if (coincideNivel) {
        rutinasFiltradas.push(rutina);
      }
    }

    return rutinasFiltradas;
  }

  public mostrarEjercicios(rutinaId: number): void {
    this.rutinaSeleccionada = rutinaId;
  }

  public obtenerRutinaSeleccionada(): Workout | undefined {
    const rutinas = this.ejercicios();

    for (let i = 0; i < rutinas.length; i++) {
      if (rutinas[i].id === this.rutinaSeleccionada) {
        return rutinas[i];
      }
    }

    return undefined;
  }

  public obtenerEjerciciosSeleccionados(): Ejercicio[] {
    const rutina = this.obtenerRutinaSeleccionada();

    if (rutina === undefined) {
      return [];
    }

    return rutina.ejercicios;
  }

  public ngOnInit(): void {
    this.gimnasioService.getEjercicios().subscribe({
      next: (resultado) => {
        this.ejercicios.set(resultado);
      },
      error: (error) => {
        console.log(error);
      },
    });
  }
}
