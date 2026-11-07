package modelo;

import java.util.ArrayList;

public class Workout {
	
	private String nombre;
	private String descripcion;
	private int nivel;
	private String url;
	private ArrayList<Ejercicio> ejercicios;
	private int duracion;
	
	
	
	public Workout(String nombre, String descripcion, int nivel, String url, ArrayList<Ejercicio> ejercicios, int duracion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nivel = nivel;
		this.url = url;
		this.ejercicios = ejercicios;
		this.duracion = duracion;
	}

	public Workout() {
		
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

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<Ejercicio> getEjercicios() {
		return ejercicios;
	}

	public void setEjercicios(ArrayList<Ejercicio> ejercicios) {
		this.ejercicios = ejercicios;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}
	
	
	
	
}
