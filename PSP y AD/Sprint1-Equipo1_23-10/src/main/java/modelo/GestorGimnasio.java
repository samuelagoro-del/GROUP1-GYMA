
package modelo;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GestorGimnasio {

	private static final String ID_PROYECTO = "gimnasio-f3761";
	private static final String FICHERO_CREDENCIALES = "config.json";

	public static Firestore conectar() throws IOException {
		System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");

		try (FileInputStream credenciales =
				new FileInputStream(FICHERO_CREDENCIALES)) {

			FirestoreOptions opciones = FirestoreOptions.getDefaultInstance()
					.toBuilder()
					.setProjectId(ID_PROYECTO)
					.setCredentials(GoogleCredentials.fromStream(credenciales))
					.build();

			Firestore db = opciones.getService();
			return db;
		}
	}

	public boolean registrarUsuario(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {

		boolean registrado = false;

		Map<String, Object> datosUsuario = new HashMap<>();
		datosUsuario.put("nomUsuario", usuario.getNomUsuario());
		datosUsuario.put("nombre", usuario.getNombre());
		datosUsuario.put("apellido", usuario.getApellido());
		datosUsuario.put("contrasena", usuario.getContrasena());
		datosUsuario.put("email", usuario.getEmail());
		datosUsuario.put("fechaNacimiento", usuario.getFechaNacimiento().toString());
		datosUsuario.put("nivel", usuario.getNivel());

		
			CollectionReference coleccion = crearColeccion(db, "Usuarios");

			// Comprobamos si ya existe ese nombre de usuario
			QuerySnapshot resultado = coleccion
					.whereEqualTo("nomUsuario", usuario.getNomUsuario())
					.get()
					.get();

			if (resultado.isEmpty()) {
				// Dejamos que firestore genere un ID automatico
				coleccion.add(datosUsuario).get();
				registrado = true;
			}

		return registrado;
	}

	public Usuario loginUsuario(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {
		Usuario usuarioEncontrado = null;
			// Buscamos por el campo nomUsuario
			QuerySnapshot resultado = db.collection("Usuarios")
					.whereEqualTo("nomUsuario", usuario.getNomUsuario())
					.get()
					.get();

			if (!resultado.isEmpty()) {
				DocumentSnapshot documento = resultado.getDocuments().get(0);
				String contrasenaBaseDatos = documento.getString("contrasena");

				if (contrasenaBaseDatos.equals(usuario.getContrasena())) {
					usuario.setNombre(documento.getString("nombre"));
					usuario.setApellido(documento.getString("apellido"));
					usuario.setContrasena(contrasenaBaseDatos);
					usuario.setEmail(documento.getString("email"));
					String fechaNacimiento = documento.getString("fechaNacimiento");
					usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
					Long nivel = documento.getLong("nivel");
					usuario.setNivel(nivel.intValue());

					usuarioEncontrado = usuario;
				}
			}

		

		return usuarioEncontrado;
	}


	public boolean actualizarUsuario(Firestore db, Usuario usuario, String nuevoNomUsuario) throws InterruptedException, ExecutionException {
		boolean actualizado = false;
		Map<String, Object> datosUsuario = new HashMap<>();
		datosUsuario.put("nomUsuario", nuevoNomUsuario);
		datosUsuario.put("nombre", usuario.getNombre());
		datosUsuario.put("apellido", usuario.getApellido());
		datosUsuario.put("contrasena", usuario.getContrasena());
		datosUsuario.put("email", usuario.getEmail());
		datosUsuario.put("fechaNacimiento", usuario.getFechaNacimiento().toString());
		datosUsuario.put("nivel", usuario.getNivel());
	
			// Buscamos el documento usando el nombre antiguo
			QuerySnapshot resultado = db.collection("Usuarios")
					.whereEqualTo("nomUsuario", usuario.getNomUsuario())
					.get()
					.get();
			if (!resultado.isEmpty()) {
				// Comprobamos si el nuevo nombre esta ocupado
				QuerySnapshot resultadoNuevo = db.collection("Usuarios")
						.whereEqualTo("nomUsuario", nuevoNomUsuario)
						.get()
						.get();
				boolean nombreDisponible = resultadoNuevo.isEmpty() || nuevoNomUsuario.equals(usuario.getNomUsuario());
				if (nombreDisponible) {
					DocumentSnapshot documento = resultado.getDocuments().get(0);
					db.collection("Usuarios")
					.document(documento.getId())
					.update(datosUsuario)
					.get();
					// Actualizamos el objeto tambnien en el usuario de la aplcicacion
					usuario.setNomUsuario(nuevoNomUsuario);
					actualizado = true;
				}
			}

		return actualizado;
	}


	public static CollectionReference crearColeccion(Firestore db, String nombre) {
		return db.collection(nombre);
	}


	public ArrayList<Workout> rellenarWorkouts(Firestore db) throws InterruptedException, ExecutionException {

		ArrayList<Workout> workouts = new ArrayList<>();
		QuerySnapshot resultadoWorkouts = db.collection("Workouts").get().get();
		List<QueryDocumentSnapshot> documentos = resultadoWorkouts.getDocuments();
		
		for (DocumentSnapshot documentoWorkout : documentos) {
			String nombre = documentoWorkout.getString("nombre");
			String descripcion = documentoWorkout.getString("descripcion");
			int nivel = documentoWorkout.getLong("nivel").intValue();
			String url = documentoWorkout.getString("video");

			ArrayList<Ejercicio> ejercicios = new ArrayList<>();
			QuerySnapshot resultadoEjercicios = documentoWorkout
					.getReference()
					.collection("Ejercicios")
					.get()
					.get();

			List<QueryDocumentSnapshot> documentosEjercicios = resultadoEjercicios.getDocuments();

			
			for (DocumentSnapshot documentoEjercicio : documentosEjercicios) {
				String nombreEjercicio = documentoEjercicio.getString("nombre");
				String descripcionEjercicio = documentoEjercicio.getString("descripcion");
				int descanso = documentoEjercicio.getLong("descanso").intValue();

				ArrayList<Serie> series = new ArrayList<>();
				QuerySnapshot resultadoSeries = documentoEjercicio
						.getReference()
						.collection("Series")
						.get()
						.get();

				List<QueryDocumentSnapshot> documentosSeries = resultadoSeries.getDocuments();
				for (DocumentSnapshot documentoSerie : documentosSeries) {
					String nombreSerie = documentoSerie.getString("nombre");
					String foto = documentoSerie.getString("foto");
					int repeticiones = documentoSerie.getLong("repeticiones").intValue();
					int duracion = documentoSerie.getLong("duracion").intValue();
					Serie serie = new Serie(nombreSerie, foto, repeticiones, duracion);
					series.add(serie);
				}

				Ejercicio ejercicio = new Ejercicio(nombreEjercicio, descripcionEjercicio, descanso, series);
				ejercicios.add(ejercicio);
			}

			Workout workout = new Workout(nombre, descripcion, nivel, url, ejercicios);
			workouts.add(workout);
		}
		return workouts;
	}
	
	
	public void backupWorkouts(Firestore db, ArrayList<Workout> workouts) throws InterruptedException, ExecutionException, IOException {

	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    FileWriter writer = new FileWriter("backup_workouts.json");
	    gson.toJson(workouts, writer);
	    writer.close();

	}
	
	public void backupUsuarios(Firestore db)throws InterruptedException, ExecutionException, IOException {

	    QuerySnapshot resultado = db.collection("Usuarios").get().get();
	    List<Map<String, Object>> usuarios = new ArrayList<>();
	    for (DocumentSnapshot documento : resultado.getDocuments()) {
	        Map<String, Object> datos = new HashMap<>();

	        datos.put("nomUsuario", documento.getString("nomUsuario"));
	        datos.put("nombre", documento.getString("nombre"));
	        datos.put("apellido", documento.getString("apellido"));
	        datos.put("contrasena", documento.getString("contrasena"));
	        datos.put("email", documento.getString("email"));
	        datos.put("fechaNacimiento", documento.getString("fechaNacimiento"));
	        datos.put("nivel", documento.getLong("nivel"));

	        usuarios.add(datos);
	    }

	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    FileWriter writer = new FileWriter("backup_usuarios.json");
	    gson.toJson(usuarios, writer);
	    writer.close();
	}
	
	

}
