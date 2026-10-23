package modelo;

import java.util.ArrayList;

public class Workout {
	
	private String nombre;
	private String descripcion;
	private int nivel;
	private String url;
	private ArrayList<Ejercicio> ejercicios;
	
	public Workout(String nombre, String descripcion, int nivel, String url, ArrayList<Ejercicio> ejercicios) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.nivel = nivel;
		this.url = url;
		this.ejercicios = ejercicios;
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
	
	
	
}
