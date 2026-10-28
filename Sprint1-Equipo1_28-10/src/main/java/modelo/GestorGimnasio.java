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

    public boolean registrarUsuario(Firestore db, Usuario usuario)
            throws InterruptedException, ExecutionException {

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
            // Firestore genera automáticamente el ID del documento al guardar el usuario.
            coleccion.add(datosUsuario).get();
            registrado = true;
        }

        return registrado;
    }

    public Usuario loginUsuario(Firestore db, Usuario usuario)
            throws InterruptedException, ExecutionException {

        Usuario usuarioEncontrado = null;

        // Buscamos al usuario por su nombre para comprobar sus credenciales.
        QuerySnapshot resultado = db.collection("Usuarios")
                .whereEqualTo("nomUsuario", usuario.getNomUsuario())
                .get()
                .get();

        if (!resultado.isEmpty()) {
            DocumentSnapshot documento = resultado.getDocuments().get(0);
            String contrasenaBaseDatos = documento.getString("contrasena");

            // Comparamos la contraseña recibida con la almacenada en Firestore.
            if (contrasenaBaseDatos.equals(usuario.getContrasena())) {
                // Si coincide, completamos el objeto con los datos del usuario.
                usuario.setNombre(documento.getString("nombre"));
                usuario.setApellido(documento.getString("apellido"));
                usuario.setContrasena(contrasenaBaseDatos);
                usuario.setEmail(documento.getString("email"));

                // Convertimos la fecha almacenada como texto a un objeto LocalDate.
                String fechaNacimiento = documento.getString("fechaNacimiento");
                usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));

                Long nivel = documento.getLong("nivel");
                usuario.setNivel(nivel.intValue());

                usuarioEncontrado = usuario;
            }
        }

        // Si no existe el usuario o la contraseña no coincide, se devuelve null.
        return usuarioEncontrado;
    }

    public boolean actualizarUsuario(Firestore db, Usuario usuario, String nuevoNomUsuario)
            throws InterruptedException, ExecutionException {

        boolean actualizado = false;

        // Preparamos los datos que tendrá el usuario después de la actualización.
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nomUsuario", nuevoNomUsuario);
        datosUsuario.put("nombre", usuario.getNombre());
        datosUsuario.put("apellido", usuario.getApellido());
        datosUsuario.put("contrasena", usuario.getContrasena());
        datosUsuario.put("email", usuario.getEmail());
        datosUsuario.put("fechaNacimiento", usuario.getFechaNacimiento().toString());
        datosUsuario.put("nivel", usuario.getNivel());

        // Buscamos el documento utilizando el nombre de usuario actual.
        QuerySnapshot resultado = db.collection("Usuarios")
                .whereEqualTo("nomUsuario", usuario.getNomUsuario())
                .get()
                .get();

        if (!resultado.isEmpty()) {

            // Comprobamos si el nuevo nombre ya pertenece a otro usuario.
            QuerySnapshot resultadoNuevo = db.collection("Usuarios")
                    .whereEqualTo("nomUsuario", nuevoNomUsuario)
                    .get()
                    .get();

            boolean nombreDisponible = resultadoNuevo.isEmpty() || nuevoNomUsuario.equals(usuario.getNomUsuario());

            if (nombreDisponible) {
                DocumentSnapshot documento = resultado.getDocuments().get(0);

                // Actualizamos el documento usando su ID, no su nombre de usuario.
                db.collection("Usuarios")
                        .document(documento.getId())
                        .update(datosUsuario)
                        .get();

                // Actualizamos también el objeto que se está utilizando en la aplicación.
                usuario.setNomUsuario(nuevoNomUsuario);
                actualizado = true;
            }
        }

        return actualizado;
    }

    // Devuelve una referencia a la colección indicada de Firestore.
    public static CollectionReference crearColeccion(Firestore db, String nombre) {
        return db.collection(nombre);
    }

    public ArrayList<Workout> rellenarWorkouts(Firestore db)
            throws InterruptedException, ExecutionException {

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
                    int duracion = documentoSerie.getLong("duracion").intValue();

                    Serie serie = new Serie(nombreSerie, foto, repeticiones, duracion);
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

    public void backupWorkouts(Firestore db, ArrayList<Workout> workouts)
            throws InterruptedException, ExecutionException, IOException {

        // Gson convierte los objetos Java a JSON; PrettyPrinting facilita su lectura.
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Creamos el archivo de copia de seguridad y escribimos todos los entrenamientos.
        FileWriter writer = new FileWriter("backup_workouts.json");
        gson.toJson(workouts, writer);
        writer.close();
    }

    public void backupUsuarios(Firestore db) throws InterruptedException, ExecutionException, IOException {

        // Obtenemos todos los documentos de la colección Usuarios.
        QuerySnapshot resultado = db.collection("Usuarios").get().get();

        List<Map<String, Object>> usuarios = new ArrayList<>();

        for (DocumentSnapshot documento : resultado.getDocuments()) {
            Map<String, Object> datos = new HashMap<>();

            // Copiamos los campos del documento al mapa que representa al usuario.
            datos.put("nomUsuario", documento.getString("nomUsuario"));
            datos.put("nombre", documento.getString("nombre"));
            datos.put("apellido", documento.getString("apellido"));
            datos.put("contrasena", documento.getString("contrasena"));
            datos.put("email", documento.getString("email"));
            datos.put("fechaNacimiento", documento.getString("fechaNacimiento"));
            datos.put("nivel", documento.getLong("nivel"));

            // Añadimos el usuario completo a la lista de usuarios.
            usuarios.add(datos);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter writer = new FileWriter("backup_usuarios.json");
        gson.toJson(usuarios, writer);
        writer.close();
    }
}