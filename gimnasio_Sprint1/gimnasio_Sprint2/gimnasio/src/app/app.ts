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
  public textoBusqueda: string = '';

  // Obtiene los niveles únicos de las rutinas y los ordena de menor a mayor.
  public obtenerNiveles(): number[] {
    const niveles: number[] = [];
    const rutinas = this.ejercicios();

    // Filtra los niveles únicos de las rutinas
    for (let i = 0; i < rutinas.length; i++) {
      if (niveles.indexOf(rutinas[i].nivel) === -1) {
        niveles.push(rutinas[i].nivel);
      }
    }

    //Ordena los niveles de menor a mayor
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

  // Filtra las rutinas según el nivel seleccionado y el texto de búsqueda.
  public obtenerRutinasFiltradas(): Workout[] {
    const texto = this.textoBusqueda.trim().toLowerCase();
    const rutinas = this.ejercicios();
    const rutinasFiltradas: Workout[] = [];


    // Recorre todas las rutinas y verifica si cumplen con los criterios de filtrado.
    for (let i = 0; i < rutinas.length; i++) {
      const rutina = rutinas[i];
      let coincideBusqueda = false;
      if (texto === '') {
        coincideBusqueda = true;
      }

      // Verifica si el texto de búsqueda coincide con el nombre de la rutina o con el nombre de algún ejercicio.
      for (let j = 0; j < rutina.ejercicios.length; j++) {
        if (rutina.ejercicios[j].nombre.toLowerCase().indexOf(texto) !== -1) {
          coincideBusqueda = true;
          break;
        }
      }

      // Verifica si el nivel de la rutina coincide con el nivel seleccionado.
      let coincideNivel = false;
      if (this.nivelSeleccionado === '' || rutina.nivel.toString() === this.nivelSeleccionado) {
        coincideNivel = true;
      }

      // Si la rutina cumple con ambos criterios, se agrega a la lista de rutinas filtradas.
      if (coincideNivel && coincideBusqueda) {
        rutinasFiltradas.push(rutina);
      }
    }

    return rutinasFiltradas;
  }

  // Guarda el ID de la rutina seleccionada para abrir el panel de detalles inferior.
  public mostrarEjercicios(rutinaId: number): void {
    this.rutinaSeleccionada = rutinaId;
  }

  // Calcula el tiempo total de los ejercicios de la rutina seleccionada.
  public tiempoTotalEjercicios(rutina: Workout): number {
    let tiempoTotal = 0;
    for (const ejercicio of rutina.ejercicios) {
      tiempoTotal += ejercicio.tiempo;
    }
    return tiempoTotal;
  }

  // Busca los datos completos de la rutina que esta seleccionada.
  public obtenerRutinaSeleccionada(): Workout | undefined {
    const rutinas = this.ejercicios();

    // Recorre todas las rutinas y busca la que coincide con el ID de la rutina seleccionada.
    for (let i = 0; i < rutinas.length; i++) {
      if (rutinas[i].id === this.rutinaSeleccionada) {
        return rutinas[i];
      }
    }
    // Si no se encuentra ninguna rutina que coincida con el ID seleccionado, devuelve undefined.
    return undefined;
  }

  // Extrae la lista de ejercicios de la rutina seleccionada para mostrarlos en la tabla inferior.
  public obtenerEjerciciosSeleccionados(): Ejercicio[] {
    const rutina = this.obtenerRutinaSeleccionada();

    // Si no se encuentra ninguna rutina seleccionada, devuelve un arreglo vacío.
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
