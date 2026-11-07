package controlador;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.google.cloud.firestore.Firestore;

import modelo.GestorGimnasio;
import modelo.Historico;
import modelo.Usuario;
import modelo.Workout;

public class Controlador {

	private GestorGimnasio gestor = new GestorGimnasio();

	public Firestore conectar() throws IOException {
		Firestore db = gestor.conectar();
		return db;
	}

	public boolean actualizarUsuario(Firestore db, Usuario usuario, String nuevoNomUsuario) throws InterruptedException, ExecutionException {
		boolean actualizado = gestor.actualizarUsuario(db, usuario, nuevoNomUsuario);
		return actualizado;
	}

	public boolean registrarUsuario(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {
		boolean registrado = gestor.registrarUsuario(db, usuario);
		return registrado;
	}

	/*public Usuario loginUsuarioFirebase(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {
		usuario = gestor.loginUsuarioFirebase(db, usuario);
		return usuario;
	}*/

	public ArrayList<Workout> rellenarWorkoutsFirebase(Firestore db) throws InterruptedException, ExecutionException {
		ArrayList<Workout> workouts = gestor.rellenarWorkoutsFirebase(db);
		return workouts;
	}

	public void crearBackups() {
		gestor.crearBackups();
	}

	public  ArrayList<Usuario> devolverUsuariosOffline() throws FileNotFoundException, IOException {
		ArrayList<Usuario> usuarios = gestor.devolverUsuariosOffline();
		return usuarios;
	}

	public ArrayList<Workout> rellenarWorkoutsOffline() throws IOException {
		ArrayList<Workout> workouts = gestor.rellenarWorkoutsOffline();
		return workouts;
	}

	public ArrayList<Usuario> devolverUsuariosFirebase(Firestore db) throws InterruptedException, ExecutionException {
		ArrayList<Usuario> usuarios = gestor.devolverUsuariosFirebase(db);
		return usuarios;
	}

	public ArrayList<Historico> rellenarHistoricoFirebase(Firestore db, Usuario usuarioActual) throws InterruptedException, ExecutionException {
		ArrayList<Historico> historico = gestor.rellenarHistoricoFirebase(db, usuarioActual);
		return historico;
	}

	public ArrayList<Historico> rellenarHistoricoXML(Usuario usuarioActual) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		ArrayList<Historico> workoutsHistorico = gestor.rellenarHistoricoXML(usuarioActual);
		return workoutsHistorico;
	}

	public boolean actualizarHistorico(Firestore db, Usuario usuarioActual) throws InterruptedException, ExecutionException {
		boolean actualizado = gestor.actualizarHistorico(db, usuarioActual);
		return actualizado;
	}

	public boolean actualizarHistoricoXML(Usuario usuarioActual) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		boolean actualizado = gestor.actualizarHistoricoXML(usuarioActual);
		return actualizado;

	}




}
