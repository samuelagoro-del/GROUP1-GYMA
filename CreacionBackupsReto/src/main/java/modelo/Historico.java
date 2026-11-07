package modelo;

import java.time.LocalDate;

public class Historico {
	
	private String nombreWorkout;
	private int nivel;
	private int tiempoTotal;
	private int tiempoPrevisto;
	private LocalDate fecha;
	private int porcentajeEjercicios;

	public Historico() {
		
	}

	public Historico(String nombreWorkout, int nivel, int tiempoTotal, int tiempoPrevisto, LocalDate fecha, int porcentajeEjercicios) {
		this.nombreWorkout = nombreWorkout;
		this.nivel = nivel;
		this.tiempoTotal = tiempoTotal;
		this.tiempoPrevisto = tiempoPrevisto;
		this.fecha = fecha;
		this.porcentajeEjercicios = porcentajeEjercicios;
	}






	public String getNombreWorkout() { 
		return nombreWorkout; 
	}

	public void setNombreWorkout(String nombreWorkout) { 
		this.nombreWorkout = nombreWorkout; 
	}

	public int getNivel() { 
		return nivel; 
	}

	public void setNivel(int nivel) { 
		this.nivel = nivel;
	}

	public int getTiempoTotal() { 
		return tiempoTotal; 
	}

	public void setTiempoTotal(int tiempoTotal) { 
		this.tiempoTotal = tiempoTotal; 
	}

	public int getTiempoPrevisto() { 
		return tiempoPrevisto; 
	}

	public void setTiempoPrevisto(int tiempoPrevisto) {
		this.tiempoPrevisto = tiempoPrevisto;
	}

	public LocalDate getFecha() { 
		return fecha; 
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha; 
	}

	public int getPorcentajeEjercicios() { 
		return porcentajeEjercicios;
	}

	public void setPorcentajeEjercicios(int porcentajeEjercicios) {
		this.porcentajeEjercicios = porcentajeEjercicios;
	}
	
	public static String formatearTiempo(int segundosTotales) {
	    int minutos = segundosTotales / 60;
	    int segundos = segundosTotales % 60;

	    return String.format("%02d:%02d", minutos, segundos);
	}
}