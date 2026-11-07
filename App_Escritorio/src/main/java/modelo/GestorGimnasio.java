package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

public class GestorGimnasio {

	private static final String ID_PROYECTO = "gimnasio-f3761";
	private static final String FICHERO_CREDENCIALES = "config.json";

	public static Firestore conectar() throws IOException{

		System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");

		try (FileInputStream credenciales = new FileInputStream(FICHERO_CREDENCIALES)) {

			FirestoreOptions opciones = FirestoreOptions.getDefaultInstance()
					.toBuilder()
					.setProjectId(ID_PROYECTO)
					.setCredentials(GoogleCredentials.fromStream(credenciales))
					.build();

			Firestore db = opciones.getService();

			try {
				db.collection("Test_De_Conexion").limit(1).get().get();
			} catch (Exception e) {
				db = null;
			}

			return db;
		}

	}

	public boolean registrarUsuario(Firestore db, Usuario usuario) throws InterruptedException, ExecutionException {

		boolean registrado = false;

		// Guardamos los datos del usuario en un mapa para enviarlos a Firestore.
		Map<String, Object> datosUsuario = new HashMap<>();
		datosUsuario.put("nomUsuario", usuario.getNomUsuario());
		datosUsuario.put("nombre", usuario.getNombre());
		datosUsuario.put("apellido", usuario.getApellido());
		datosUsuario.put("contrasena", usuario.getContrasena());
		datosUsuario.put("email", usuario.getEmail());
		datosUsuario.put("fechaNacimiento", usuario.getFechaNacimiento().toString());
		datosUsuario.put("nivel", usuario.getNivel());

		CollectionReference coleccion = crearColeccion(db, "Usuarios");

		// Comprobamos que no exista ya un usuario con el mismo nombre de usuario.
		QuerySnapshot resultado = coleccion
				.whereEqualTo("nomUsuario", usuario.getNomUsuario())
				.get()
				.get();

		if (resultado.isEmpty()) {
			// Firestore genera el ID del documento al guardar el usuario
			coleccion.add(datosUsuario).get();
			registrado = true;
		}

		return registrado;
	}


	public boolean actualizarUsuario(Firestore db, Usuario usuario, String nuevoNomUsuario) throws InterruptedException, ExecutionException {

		boolean actualizado = false;

		// Preparamos los datos del usuario
		Map<String, Object> datosUsuario = new HashMap<>();
		datosUsuario.put("nomUsuario", nuevoNomUsuario);
		datosUsuario.put("nombre", usuario.getNombre());
		datosUsuario.put("apellido", usuario.getApellido());
		datosUsuario.put("contrasena", usuario.getContrasena());
		datosUsuario.put("email", usuario.getEmail());
		datosUsuario.put("fechaNacimiento", usuario.getFechaNacimiento().toString());
		datosUsuario.put("nivel", usuario.getNivel());

		// Buscamos el documento utilizando el nombre de usuario actual
		QuerySnapshot resultado = db.collection("Usuarios")
				.whereEqualTo("nomUsuario", usuario.getNomUsuario())
				.get()
				.get();

		if (!resultado.isEmpty()) {

			// Comprobamos si el nuevo nombre ya pertenece a otro usuario
			QuerySnapshot resultadoNuevo = db.collection("Usuarios")
					.whereEqualTo("nomUsuario", nuevoNomUsuario)
					.get()
					.get();

			boolean nombreDisponible = resultadoNuevo.isEmpty() || nuevoNomUsuario.equals(usuario.getNomUsuario());

			if (nombreDisponible) {
				DocumentSnapshot documento = resultado.getDocuments().get(0);

				// Actualizamos el documento usando su ID, no su nombre de usuario
				db.collection("Usuarios")
				.document(documento.getId())
				.update(datosUsuario)
				.get();

				// Actualizamos también el objeto que se está utilizando en la aplicación
				usuario.setNomUsuario(nuevoNomUsuario);
				actualizado = true;
			}
		}

		return actualizado;
	}

	// Devuelve una referencia a la colección indicada de Firestore
	public static CollectionReference crearColeccion(Firestore db, String nombre) {
		return db.collection(nombre);
	}

	public ArrayList<Workout> rellenarWorkoutsFirebase(Firestore db) throws InterruptedException, ExecutionException {

		ArrayList<Workout> workouts = new ArrayList<>();

		// Obtenemos todos los documentos de la colección principal de entrenamientos
		QuerySnapshot resultadoWorkouts = db.collection("Workouts").get().get();
		List<QueryDocumentSnapshot> documentos = resultadoWorkouts.getDocuments();

		for (DocumentSnapshot documentoWorkout : documentos) {
			String nombre = documentoWorkout.getString("nombre");
			String descripcion = documentoWorkout.getString("descripcion");
			int nivel = documentoWorkout.getLong("nivel").intValue();
			String url = documentoWorkout.getString("video");

			ArrayList<Ejercicio> ejercicios = new ArrayList<>();

			// Cada entrenamiento tiene su propia subcolección de ejercicios
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

				// Cada ejercicio tiene su propia subcolección de series
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

				// Cuando tenemos todas sus series, construimos el ejercicio completo
				Ejercicio ejercicio = new Ejercicio(nombreEjercicio, descripcionEjercicio, descanso, series);
				ejercicios.add(ejercicio);
			}

			// Cuando tenemos todos sus ejercicios, construimos el entrenamiento completo
			Workout workout = new Workout(nombre, descripcion, nivel, url, ejercicios);
			workouts.add(workout);
		}

		return workouts;
	}

	public void crearBackups() {

			try {
				ProcessBuilder pb = new ProcessBuilder("java", "-jar", "AppCreacionBakcups.jar");

				pb.inheritIO();
				Process proceso = pb.start();

				proceso.waitFor();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	public ArrayList<Usuario> devolverUsuariosOffline() throws FileNotFoundException, IOException {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		archivo = new File("usuarios_backup.dat");
		ArrayList<Usuario> usuarios = new ArrayList<Usuario>();

		fr = new FileReader (archivo);
		br = new BufferedReader(fr);

		String nomUsuario, nombre, apellido, contrasena, email;
		int nivel = 0;
		LocalDate fechaNacimiento;

		String linea = br.readLine();

		while (linea != null) {
			if (linea.equals("USUARIO")) {
				// Nombre Usuario
				linea = br.readLine();
				String cadena [] = linea.split(": ");
				nomUsuario = cadena[1];

				// Nombre
				linea = br.readLine();
				cadena = linea.split(": ");
				nombre = cadena[1];

				// Apellido
				linea = br.readLine();
				cadena = linea.split(": ");
				apellido = cadena[1];

				// Contraseña
				linea = br.readLine();
				cadena = linea.split(": ");
				contrasena = cadena[1];

				// Email
				linea = br.readLine();
				cadena = linea.split(": ");
				email = cadena[1];

				// Fecha de Nacimiento
				linea = br.readLine();
				cadena = linea.split(": ");
				fechaNacimiento = LocalDate.parse(cadena[1]);

				// Nivel
				linea = br.readLine();
				cadena = linea.split(": ");
				nivel = Integer.parseInt(cadena[1]);

				Usuario usuario = new Usuario(nomUsuario, nombre, apellido, contrasena, email, fechaNacimiento, nivel);
				usuarios.add(usuario);
			}
			linea = br.readLine();
		}

		br.close();
		fr.close();

		return usuarios;
	}

	public ArrayList<Workout> rellenarWorkoutsOffline() throws IOException {
		File archivo = new File("workouts_backup.dat");
		ArrayList<Workout> workouts = new ArrayList<Workout>();

		Workout ultimoWorkout = null;
		Ejercicio ultimoEjercicio = null;

		FileReader fr = new FileReader(archivo);
		BufferedReader br = new BufferedReader(fr);

		String linea = br.readLine();

		while (linea != null) {
			if (linea.equals("WORKOUT")) {
				// Nombre
				linea = br.readLine();
				String[] cadena = linea.split(": ");
				String nombre = cadena[1];

				// Descripción
				linea = br.readLine();
				cadena = linea.split(": ");
				String descripcion = cadena[1];

				// Nivel
				linea = br.readLine();
				cadena = linea.split(": ");
				int nivel = Integer.parseInt(cadena[1]);

				// Video
				linea = br.readLine();
				cadena = linea.split(": ");
				String video = cadena[1];

				ultimoWorkout = new Workout(nombre, descripcion, nivel, video, new ArrayList<Ejercicio>());
				workouts.add(ultimoWorkout);
			} 
			else if (linea.equals("EJERCICIO")) {
				// Nombre
				linea = br.readLine();
				String[] cadena = linea.split(": ");
				String nombre = cadena[1];

				// Descripción
				linea = br.readLine();
				cadena = linea.split(": ");
				String descripcion = cadena[1];

				// Descanso
				linea = br.readLine();
				cadena = linea.split(": ");
				int descanso = Integer.parseInt(cadena[1]);

				ultimoEjercicio = new Ejercicio(nombre, descripcion, descanso, new ArrayList<Serie>());
				ultimoWorkout.getEjercicios().add(ultimoEjercicio);
			} 
			else if (linea.equals("SERIE")) {

				// Nombre
				linea = br.readLine();
				String[] cadena = linea.split(": ");
				String nombre = cadena[1];

				// Foto
				linea = br.readLine();
				cadena = linea.split(": ");
				String foto = cadena[1];

				// Repeticiones
				linea = br.readLine();
				cadena = linea.split(": ");
				int repeticiones = Integer.parseInt(cadena[1]);

				// Duración
				linea = br.readLine();
				cadena = linea.split(": ");
				int duracion = Integer.parseInt(cadena[1]);

				Serie nuevaSerie = new Serie(nombre, foto, repeticiones, duracion);
				ultimoEjercicio.getSeries().add(nuevaSerie);
			}

			linea = br.readLine();
		}

		br.close();
		fr.close();

		return workouts;
	}

	public ArrayList<Usuario> devolverUsuariosFirebase(Firestore db) throws InterruptedException, ExecutionException {
		ArrayList<Usuario> usuarios = new ArrayList<>();

		// Obtenemos todos los documentos de la colección de usuarios
		QuerySnapshot resultado = db.collection("Usuarios").get().get();
		List<QueryDocumentSnapshot> documentos = resultado.getDocuments();

		for (DocumentSnapshot documento : documentos) {
			String nomUsuario = documento.getString("nomUsuario");
			String nombre = documento.getString("nombre");
			String apellido = documento.getString("apellido");
			String contrasena = documento.getString("contrasena");
			String email = documento.getString("email");

			LocalDate fechaNacimiento = LocalDate.parse(documento.getString("fechaNacimiento"));
			int nivel = documento.getLong("nivel").intValue();

			Usuario usuario = new Usuario(nombre, apellido, nomUsuario, contrasena, email, fechaNacimiento, nivel);
			usuarios.add(usuario);
		}

		return usuarios;
	}

	public ArrayList<Historico> rellenarHistoricoFirebase(Firestore db, Usuario usuarioActual) throws InterruptedException, ExecutionException { 
		ArrayList<Historico> historicoUsuario = new ArrayList<Historico>(); 

		// Cogemos la coleccion de historicos
		QuerySnapshot historicos = db.collection("Historicos").get().get(); 
		List<QueryDocumentSnapshot> listaHistoricos = historicos.getDocuments(); 

		for (DocumentSnapshot docHistorico : listaHistoricos) { 
			String nomUsuario = docHistorico.getString("nomUsuario"); 

			// Si el documento pertenece al usuario actual, entramos a la subcoleccion de historico
			if (nomUsuario.equals(usuarioActual.getNomUsuario())){ 
				// Accedemos a la subcolección interna "Historico" de este documento específico
				QuerySnapshot resultadoHistorico = docHistorico.getReference().collection("Historico").get().get();
				List<QueryDocumentSnapshot> docHistoricoWorkouts = resultadoHistorico.getDocuments();

				// Aqui sacamos cada workout del historico
				for (DocumentSnapshot docWorkout : docHistoricoWorkouts) {
					String nombreWorkout = docWorkout.getString("nombreWorkout");
					int nivel = docWorkout.getLong("nivel").intValue();
					int tiempoPrevisto = docWorkout.getLong("tiempoPrevisto").intValue();
					int tiempoTotal = docWorkout.getLong("tiempoTotal").intValue();
					LocalDate fecha = LocalDate.parse(docWorkout.getString("fecha"));
					int porcentajeEjercicios = docWorkout.getLong("porcentajeEjercicios").intValue();

					Historico h = new Historico(nombreWorkout, nivel, tiempoTotal, tiempoPrevisto,  fecha, porcentajeEjercicios);
					historicoUsuario.add(h);
				} 
			} 
		} 
		return historicoUsuario; 
	}
	public ArrayList<Historico> rellenarHistoricoXML(Usuario usuarioActual) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		ArrayList<Historico> workoutsHistorico = new ArrayList<Historico>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse("historicos.xml");
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList workoutsUsuario = (NodeList) xpath.evaluate("//historico[@nomUsuario = '" + usuarioActual.getNomUsuario() + "']/*", doc, XPathConstants.NODESET);
		for (int i = 0; i < workoutsUsuario.getLength(); i++) {
			Node workout = workoutsUsuario.item(i);
			NodeList datosWorkout = workout.getChildNodes();
			String nombreWorkout = "";
			LocalDate fecha = null;
			int nivel = 0, tiempoPrevisto = 0, tiempoTotal = 0, porcentajeEjericios = 0;
			int k = 0;
			for (int j = 0; j < datosWorkout.getLength(); j++) {
				Node datoWorkout = datosWorkout.item(j);

				if (datoWorkout.getNodeType() == Node.ELEMENT_NODE) {
					switch (k) {
					case 0: 
						nombreWorkout = datoWorkout.getTextContent();
						break;
					case 1:
						nivel = Integer.parseInt(datoWorkout.getTextContent());
						break;
					case 2:
						tiempoTotal = Integer.parseInt(datoWorkout.getTextContent());
						break;
					case 3:
						tiempoPrevisto = Integer.parseInt(datoWorkout.getTextContent());
						break;
					case 4:
						fecha = LocalDate.parse(datoWorkout.getTextContent());
						break;
					case 5:
						porcentajeEjericios = Integer.parseInt(datoWorkout.getTextContent());
						break;
					}
					k++;
				}
			}
			Historico workoutHistorico = new Historico(nombreWorkout, nivel, tiempoTotal, tiempoPrevisto,  fecha, porcentajeEjericios);
			workoutsHistorico.add(workoutHistorico);
		}

		return workoutsHistorico;
	}

	public boolean actualizarHistorico(Firestore db, Usuario usuarioActual) throws InterruptedException, ExecutionException {

		boolean actualizado = false;

		// Buscamos el documento del usuario en la coleccion Historicos
		QuerySnapshot resultado = db.collection("Historicos")
				.whereEqualTo("nomUsuario", usuarioActual.getNomUsuario())
				.get()
				.get();

		DocumentReference documentoUsuario;

		if (!resultado.isEmpty()) {

			// Si existe, obtenemos su documento
			documentoUsuario = resultado.getDocuments().get(0).getReference();

		} else {

			// Si no existe, creamos un documento nuevo
			documentoUsuario = db.collection("Historicos").document();

			Map<String, Object> datosUsuario = new HashMap<>();
			datosUsuario.put("nomUsuario", usuarioActual.getNomUsuario());

			documentoUsuario.set(datosUsuario).get();
		}

		// Accedemos a su subcoleccion Historico
		CollectionReference coleccionHistorico = documentoUsuario.collection("Historico");

		// Recorremos todos los workouts del historico del usuario
		for (Historico historico : usuarioActual.getHistorico()) {

			// Preparamos los datos del historico
			Map<String, Object> datosHistorico = new HashMap<>();
			datosHistorico.put("fecha", historico.getFecha().toString());
			datosHistorico.put("nivel", historico.getNivel());
			datosHistorico.put("nombreWorkout", historico.getNombreWorkout());
			datosHistorico.put("porcentajeEjercicios", historico.getPorcentajeEjercicios());
			datosHistorico.put("tiempoPrevisto", historico.getTiempoPrevisto());
			datosHistorico.put("tiempoTotal", historico.getTiempoTotal());

			// Buscamos si ya existe ese workout en la subcoleccion
			QuerySnapshot resultadoHistorico = coleccionHistorico
					.whereEqualTo("nombreWorkout", historico.getNombreWorkout())
					.get()
					.get();

			if (!resultadoHistorico.isEmpty()) {

				// Si existe, actualizamos el documento
				DocumentSnapshot documentoHistorico = resultadoHistorico.getDocuments().get(0);

				coleccionHistorico.document(documentoHistorico.getId())
				.update(datosHistorico)
				.get();

			} else {

				// Si no existe, creamos un documento nuevo
				coleccionHistorico.add(datosHistorico).get();
			}
		}

		actualizado = true;

		return actualizado;
	}


	public boolean actualizarHistoricoXML(Usuario usuarioActual) throws ParserConfigurationException, SAXException, IOException, TransformerException {

		boolean actualizado = false;

		File fichero = new File("historicos.xml");

		// Leemos el documento del XML
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(fichero);

		// Cogemos los nodos historico que hay dentro de Historicos
		NodeList historicos = doc.getDocumentElement().getChildNodes();

		// Por si no tiene historico creado
		Node historicoUsuario = null;
		boolean encontradoUsuario = false;

		// Buscamos el historico del usuario actual
		for (int i = 0; i < historicos.getLength(); i++) {

			Node nodoUsuarioFor = historicos.item(i);

			if (nodoUsuarioFor.getNodeType() == Node.ELEMENT_NODE) {

				if (nodoUsuarioFor.getAttributes().getNamedItem("nomUsuario").getNodeValue().equals(usuarioActual.getNomUsuario())) {
					historicoUsuario = nodoUsuarioFor;
					encontradoUsuario = true;
				}
			}
		}

		// Si no existe el historico del usuario, lo creamos
		if (!encontradoUsuario) {
			Element nuevoHistorico = doc.createElement("historico");
			nuevoHistorico.setAttribute("nomUsuario", usuarioActual.getNomUsuario());
			// Añadimos el nuivo historico del usuairo que no tenia a historicos
			doc.getDocumentElement().appendChild(nuevoHistorico);
			historicoUsuario = nuevoHistorico;
		}

		// Eliminamos los workouts anteriores del usuario
		NodeList workoutsAnteriores = historicoUsuario.getChildNodes();
		// Empezamos el for asi porque tienes que eliminar del ultimo al primero porque si eliminas el primer workout
		// entonces el segundo pasaria a tener el indice del primero y habria problemas
		for (int i = workoutsAnteriores.getLength() - 1; i >= 0; i--) {
			Node nodo = workoutsAnteriores.item(i);
			// Siempre comprobamos que sea un nodo antes de hacer nada porque pueden ser espacios en blanco
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				historicoUsuario.removeChild(nodo);
			}
		}

		// Añadimos los workouts que tiene actualmente el usuario parecido a como hacemos en las backups de los historicos
		for (Historico workoutHistorico : usuarioActual.getHistorico()) {

			Element workout = doc.createElement("workout");

			Element nombreWorkout = doc.createElement("nombreWorkout");
			nombreWorkout.appendChild(doc.createTextNode(workoutHistorico.getNombreWorkout()));
			workout.appendChild(nombreWorkout);

			Element nivel = doc.createElement("nivel");
			nivel.appendChild(doc.createTextNode(String.valueOf(workoutHistorico.getNivel())));
			workout.appendChild(nivel);

			Element tiempoTotal = doc.createElement("tiempoTotal");
			tiempoTotal.appendChild(doc.createTextNode(String.valueOf(workoutHistorico.getTiempoTotal())));
			workout.appendChild(tiempoTotal);
			
			Element tiempoPrevisto = doc.createElement("tiempoPrevisto");
			tiempoPrevisto.appendChild(doc.createTextNode(String.valueOf(workoutHistorico.getTiempoPrevisto())));
			workout.appendChild(tiempoPrevisto);

			Element fecha = doc.createElement("fecha");
			fecha.appendChild(doc.createTextNode(String.valueOf(workoutHistorico.getFecha())));
			workout.appendChild(fecha);

			Element porcentajeEjercicios = doc.createElement("porcentajeEjercicios");
			porcentajeEjercicios.appendChild(doc.createTextNode(String.valueOf(workoutHistorico.getPorcentajeEjercicios())));
			workout.appendChild(porcentajeEjercicios);

			// Y añadimos el workout al historico del usuario en el xml
			historicoUsuario.appendChild(workout);
		}

		// Guardamos el documento XML con el mismo nombre
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(fichero);

		transformer.transform(source, result);

		actualizado = true;

		return actualizado;
	}



}