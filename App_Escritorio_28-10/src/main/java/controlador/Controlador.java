package controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.Firestore;

import modelo.GestorGimnasio;
import modelo.Usuario;
import modelo.Workout;

public class Controlador {

	public Firestore conectar() throws IOException {
		GestorGimnasio gestor = new GestorGimnasio();
		Firestore db = gestor.conectar();
		return db;
	}

	public boolean actualizarUsuario(Firestore db, Usuario usuario, String nuevoNomUsuario) throws InterruptedException, ExecutionException {
		GestorGimnasio gestor = new GestorGimnasio();
		boolean actualizado = gestor.actualizarUsuario(db, usuario, nuevoNomUsuario);
		return actualizado;
	}
	
	public boolean registrarUsuario(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {
		GestorGimnasio gestor = new GestorGimnasio();
		boolean registrado = gestor.registrarUsuario(db, usuario);
		return registrado;
	}

	public Usuario loginUsuario(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {
		GestorGimnasio gestor = new GestorGimnasio();
		usuario = gestor.loginUsuario(db, usuario);
		return usuario;
	}

	public ArrayList<Workout> rellenarWorkouts(Firestore db) throws InterruptedException, ExecutionException {
		GestorGimnasio gestor = new GestorGimnasio();
		ArrayList<Workout> workouts = gestor.rellenarWorkouts(db);
		return workouts;
	}
	
	public void crearBackups(Firestore db, ArrayList<Workout> workouts) throws InterruptedException, ExecutionException, IOException {

	    GestorGimnasio gestor = new GestorGimnasio();

	    gestor.backupWorkouts(workouts);
	    gestor.backupUsuarios(db);
	}

}
