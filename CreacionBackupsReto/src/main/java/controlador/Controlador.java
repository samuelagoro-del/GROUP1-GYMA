package controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.google.cloud.firestore.Firestore;

import modelo.GestorBackups;
import modelo.Workout;

public class Controlador {

	private GestorBackups gestor = new GestorBackups();

	public Firestore conectar() throws IOException {
		Firestore db = gestor.conectar();
		return db;
	}

	public boolean crearBackups(Firestore db) throws InterruptedException, ExecutionException, IOException, ParserConfigurationException, TransformerException {
		
		ArrayList<Workout> workouts = gestor.devolverWorkouts(db);
		boolean backups_creados = false;
		gestor.backupWorkoutsDat(workouts);
		gestor.backupWorkoutsJson(workouts);
		gestor.backupHistoricos(db);
		gestor.backupUsuariosDat(db);
		if (gestor.backup_historicos && gestor.backup_usuarios && gestor.backup_workouts && gestor.backup_workouts_json) {
			backups_creados = true;
		}
		return backups_creados;
	}


}
