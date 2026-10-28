package modelo;

import java.util.ArrayList;

public class Ejercicio {
	
	private String nombre;
	private String descripcion;
	private int descanso;
	private ArrayList<Serie> series;
	private int duracion;
	
	
	
	public Ejercicio(String nombre, String descripcion, int descanso, ArrayList<Serie> series, int duracion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.descanso = descanso;
		this.series = series;
		this.duracion = duracion;
	}

	public Ejercicio() {
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getDescanso() {
		return descanso;
	}

	public void setDescanso(int descanso) {
		this.descanso = descanso;
	}

	public ArrayList<Serie> getSeries() {
		return series;
	}

	public void setSeries(ArrayList<Serie> series) {
		this.series = series;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	
	
	
}
