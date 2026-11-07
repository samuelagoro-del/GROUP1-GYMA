package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GestorBackups {

	private static final String ID_PROYECTO = "gimnasio-f3761";
	private static final String FICHERO_CREDENCIALES = "config.json";
	public static boolean backup_usuarios = false;
	public static boolean backup_workouts = false;
	public static boolean backup_workouts_json = false;
	public static boolean backup_historicos = false;


	// Conecta con Firestore utilizando las credenciales y el identificador del proyecto.
	public static Firestore conectar() throws IOException {

		System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");

		// El try cierra automáticamente el archivo de credenciales al terminar.
		try (FileInputStream credenciales = new FileInputStream(FICHERO_CREDENCIALES)) {

			FirestoreOptions opciones = FirestoreOptions.getDefaultInstance()
					.toBuilder()
					.setProjectId(ID_PROYECTO)
					.setCredentials(GoogleCredentials.fromStream(credenciales))
					.build();

			return opciones.getService();
		}

	}


	public void backupWorkoutsDat(ArrayList<Workout> workouts) throws IOException, InterruptedException, ExecutionException {

		FileWriter fichero = null;
		PrintWriter pw = null;

		fichero = new FileWriter(System.getProperty("user.home") + "/eclipse-workspace/App_Escritorio/workouts_backup.dat");
		pw = new PrintWriter(fichero);

		// Recorremos todos los workouts.
		for (Workout workout : workouts) {

			pw.println("WORKOUT");
			pw.println("nombre: " + workout.getNombre());
			pw.println("descripcion: " + workout.getDescripcion());
			pw.println("nivel: " + workout.getNivel());
			pw.println("video: " + workout.getUrl());

			// Recorremos los ejercicios del workout.
			for (Ejercicio ejercicio : workout.getEjercicios()) {

				pw.println("EJERCICIO");
				pw.println("nombre: " + ejercicio.getNombre());
				pw.println("descripcion: " + ejercicio.getDescripcion());
				pw.println("descanso: " + ejercicio.getDescanso());

				// Recorremos las series del ejercicio.
				for (Serie serie : ejercicio.getSeries()) {

					pw.println("SERIE");
					pw.println("nombre: " + serie.getNombre());
					pw.println("foto: " + serie.getFoto());
					pw.println("repeticiones: " + serie.getRepeticiones());
					pw.println("duracion: " + serie.getDuracion());
				}
			}
		}
		
		pw.close();
		fichero.close();
		backup_workouts = true;
	}

	public void backupWorkoutsJson(ArrayList<Workout> workouts)throws InterruptedException, ExecutionException, IOException {
		// Gson convierte los objetos Java a JSON; PrettyPrinting facilita su lectura.
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// Creamos el archivo de copia de seguridad y escribimos todos los entrenamientos.
		FileWriter writer = new FileWriter(System.getProperty("user.home") + "/eclipse-workspace/App_Escritorio/workouts_backup.json");
		gson.toJson(workouts, writer);
		writer.close();
		backup_workouts_json = true;

	}


	public void backupUsuariosDat(Firestore db) throws InterruptedException, ExecutionException, IOException {

		// Obtenemos todos los documentos de la colección Usuarios
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

		// Creamos el fichero del backup de usuarios
		FileWriter fichero = null;
		PrintWriter pw = null;

		fichero = new FileWriter(System.getProperty("user.home") + "/eclipse-workspace/App_Escritorio/usuarios_backup.dat");
		pw = new PrintWriter(fichero);

		// Recorremos todos los usuarios
		for (Map<String, Object> usuario : usuarios) {
			pw.println("USUARIO");
			pw.println("nomUsuario: " + usuario.get("nomUsuario"));
			pw.println("nombre: " + usuario.get("nombre"));
			pw.println("apellido: " + usuario.get("apellido"));
			pw.println("contrasena: " + usuario.get("contrasena"));
			pw.println("email: " + usuario.get("email"));
			pw.println("fechaNacimiento: " + usuario.get("fechaNacimiento"));
			pw.println("nivel: " + usuario.get("nivel"));

		}

		pw.close();
		fichero.close();
		backup_usuarios = true;
	}

	public ArrayList<Workout> devolverWorkouts(Firestore db) throws InterruptedException, ExecutionException {

		ArrayList<Workout> workouts = new ArrayList<>();

		// Obtenemos todos los documentos de la colección principal de entrenamientos.
		QuerySnapshot resultadoWorkouts = db.collection("Workouts").get().get();
		List<QueryDocumentSnapshot> documentos = resultadoWorkouts.getDocuments();

		for (DocumentSnapshot documentoWorkout : documentos) {
			String nombre = documentoWorkout.getString("nombre");
			String descripcion = documentoWorkout.getString("descripcion");
			int nivel = documentoWorkout.getLong("nivel").intValue();
			String url = documentoWorkout.getString("video");

			ArrayList<Ejercicio> ejercicios = new ArrayList<>();

			// Cada entrenamiento tiene su propia subcolección de ejercicios.
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

				// Cada ejercicio tiene su propia subcolección de series.
				QuerySnapshot resultadoSeries = documentoEjercicio
						.getReference()
						.collection("Series")
						.get()
						.get();

				List<QueryDocumentSnapshot> documentosSeries =
						resultadoSeries.getDocuments();

				for (DocumentSnapshot documentoSerie : documentosSeries) {
					String nombreSerie = documentoSerie.getString("nombre");
					String foto = documentoSerie.getString("foto");
					int repeticiones = documentoSerie.getLong("repeticiones").intValue();
					int duracionSerie = documentoSerie.getLong("duracion").intValue();

					Serie serie = new Serie(nombreSerie, foto, repeticiones, duracionSerie);
					series.add(serie);
				}

				// Cuando tenemos todas sus series, construimos el ejercicio completo.
				Ejercicio ejercicio = new Ejercicio(nombreEjercicio, descripcionEjercicio, descanso, series);
				ejercicios.add(ejercicio);
			}

			// Cuando tenemos todos sus ejercicios, construimos el entrenamiento completo.
			Workout workout = new Workout(nombre, descripcion, nivel, url, ejercicios);
			workouts.add(workout);
		}

		return workouts;
	}


	public void backupHistoricos(Firestore db) throws InterruptedException, ExecutionException, ParserConfigurationException, TransformerException {

		// Cogemos la coleccion de historicos
		QuerySnapshot historicosUsuariosQuery = db.collection("Historicos").get().get();
		List<QueryDocumentSnapshot> listaHistoricosUsuarios = historicosUsuariosQuery.getDocuments();
		
		ArrayList<Historicos> historicos = new ArrayList<Historicos>(); 
		
		for (DocumentSnapshot docHistorico : listaHistoricosUsuarios) {
			
			String nomUsuario = docHistorico.getString("nomUsuario");
			// Accedemos a la subcolección interna "Historico" de este documento específico
			QuerySnapshot resultadoHistorico = docHistorico.getReference().collection("Historico").get().get();
			List<QueryDocumentSnapshot> docHistoricoWorkouts = resultadoHistorico.getDocuments();
			ArrayList<Historico> workoutsHistorico = new ArrayList<Historico>();
			
			for (DocumentSnapshot docHistoricoWorkout : docHistoricoWorkouts) {
				String nombreWorkout = docHistoricoWorkout.getString("nombreWorkout");
				int nivel = docHistoricoWorkout.getLong("nivel").intValue();
				int tiempoPrevisto = docHistoricoWorkout.getLong("tiempoPrevisto").intValue();
				int tiempoTotal = docHistoricoWorkout.getLong("tiempoTotal").intValue();
				LocalDate fecha = LocalDate.parse(docHistoricoWorkout.getString("fecha"));
				int porcentajeEjercicios = docHistoricoWorkout.getLong("porcentajeEjercicios").intValue();
				Historico historico = new Historico(nombreWorkout, nivel, tiempoTotal, tiempoPrevisto,  fecha, porcentajeEjercicios);
				workoutsHistorico.add(historico);
			}
			Historicos h = new Historicos(nomUsuario, workoutsHistorico);
			historicos.add(h);
		}
		
		escribirHistoricos(historicos);
		backup_historicos = true;
	}


	private void escribirHistoricos(ArrayList<Historicos> historicos) throws ParserConfigurationException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.newDocument();
		// Elemento raiz
		Element elementoRaiz = doc.createElement("Historicos");
		doc.appendChild(elementoRaiz);
		
		for (Historicos historicoUsuario: historicos) {
			
			ArrayList<Historico> historicoWorkoutsUsuario = historicoUsuario.getWorkoutsHistorico();
			// Por cada vez que se recorre el for creamos un historico para cada usuario
			Element historico = doc.createElement("historico");
			elementoRaiz.appendChild(historico);

			// Creo un atributo que sera el nombre de usuario para saber a que usuario pertenece
			Attr atributoNomUsuario = doc.createAttribute("nomUsuario");
			atributoNomUsuario.setValue(historicoUsuario.getNomUsuario());
			historico.setAttributeNode(atributoNomUsuario);
			
			for (Historico historicoWorkoutUsuario: historicoWorkoutsUsuario) {

				Element workout = doc.createElement("workout");
				historico.appendChild(workout);
				
				// Añadimos el nombre del Workout
				Element nombreWorkout = doc.createElement("nombreWorkout");
				workout.appendChild(nombreWorkout);
				nombreWorkout.appendChild(doc.createTextNode(historicoWorkoutUsuario.getNombreWorkout()));

				// Y repetimos con todos los atributos del historico
				Element nivel = doc.createElement("nivel");
				workout.appendChild(nivel);
				nivel.appendChild(doc.createTextNode(String.valueOf(historicoWorkoutUsuario.getNivel())));

				Element tiempoTotal = doc.createElement("tiempoTotal");
				workout.appendChild(tiempoTotal);
				tiempoTotal.appendChild(doc.createTextNode(String.valueOf(historicoWorkoutUsuario.getTiempoTotal())));
				
				Element tiempoPrevisto = doc.createElement("tiempoPrevisto");
				workout.appendChild(tiempoPrevisto);
				tiempoPrevisto.appendChild(doc.createTextNode(String.valueOf(historicoWorkoutUsuario.getTiempoPrevisto())));

				Element fecha = doc.createElement("fecha");
				workout.appendChild(fecha);
				fecha.appendChild(doc.createTextNode(String.valueOf(historicoWorkoutUsuario.getFecha())));

				Element porcentajeEjercicios = doc.createElement("porcentajeEjercicios");
				workout.appendChild(porcentajeEjercicios);
				porcentajeEjercicios.appendChild(doc.createTextNode(String.valueOf(historicoWorkoutUsuario.getPorcentajeEjercicios())));

			}
		}
		// Guardamos el fichero
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(System.getProperty("user.home") + "/eclipse-workspace/App_Escritorio/historicos.xml"));
		transformer.transform(source, result);

	}
}
