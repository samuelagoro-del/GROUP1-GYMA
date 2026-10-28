package modelo;

import java.time.LocalDate;

public class Usuario {

	private String nombre;
	private String apellido;
	private String nomUsuario;
	private String contrasena;
	private String email;
	private LocalDate fechaNacimiento;
	private int nivel;

	public Usuario() {

	}



	public Usuario(String nombre, String apellido, String usuario, String contrasena, String email,
			LocalDate fechaNacimiento, int nivel) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.nomUsuario = usuario;
		this.contrasena = contrasena;
		this.email = email;
		this.fechaNacimiento = fechaNacimiento;
		this.nivel = nivel;
	}



	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNomUsuario() {
		return nomUsuario;
	}
	public void setNomUsuario(String usuario) {
		this.nomUsuario = usuario;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public int getNivel() {
		return nivel;
	}
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", apellido=" + apellido + ", usuario=" + nomUsuario + ", contrasena="
				+ contrasena + ", email=" + email + ", fechaNacimiento=" + fechaNacimiento + ", nivel=" + nivel + "]";
	}



}
