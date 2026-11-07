package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.google.cloud.firestore.Firestore;

import controlador.Controlador;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import modelo.CronoAvisos;
import modelo.CronoEjercicio;
import modelo.CronoSerie;
import modelo.CronoWorkout;
import modelo.Ejercicio;
import modelo.Historico;
import modelo.Serie;
import modelo.Usuario;
import modelo.Workout;

public class VentanaEjercicio extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblNombreWorkout;
	private JLabel lblDescripcionWorkout;
	private JButton btnIniciar;
	private JButton btnSiguiente;
	private JButton btnSalir;
	private JLabel lblCronoWorkout;
	private JLabel lblNombreEjercicio;
	private JLabel lblCronoEjercicio;
	private JLabel lblDescripcionEjercicio;
	private JLabel lblAvisos;

	private JPanel panelSerie1;
	private JLabel lblFotoSerie1;
	private JLabel lblSerie1;
	private JLabel lblCronoSerie1;
	private JPanel panelEjercicio;
	private JPanel panelWorkout;
	private JPanel panelSerie2;
	private JPanel panelSerie3;
	private JLabel lblFotoSerie2;
	private JLabel lblSerie;
	private JLabel lblCronoSerie2;
	private JLabel lblFotoSerie3;
	private JLabel lblSerie3;
	private JLabel lblCronoSerie3;
	private JLabel lblFondoEjercicio;

	private JButton btnPausar;
	private JButton btnReanudar;

	Workout workoutActual;
	Ejercicio ejercicioActual;

	private CronoWorkout cronoWorkout;
	private CronoEjercicio cronoEjercicio;
	private CronoAvisos cronoAvisos;
	private CronoSerie cronoSerieActual;

	private Serie serieActual;
	private int numeroSerieActual = 1;

	private ArrayList<Ejercicio> ejerciciosCompletados = new ArrayList<Ejercicio>();

	private Usuario usuarioActual;
	private ArrayList<Workout> workouts = new ArrayList<Workout>();

	private boolean conexion;
	private Firestore db;

	private Controlador controlador = new Controlador();

	public VentanaEjercicio(Workout workout, String nombreEjercicio, Usuario usuarioActual, ArrayList<Workout> workoutsParametro, boolean conexionParametro) {
		this.usuarioActual = usuarioActual;
		workoutActual = workout;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 544, 499);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelEjercicio = new JPanel();
		panelEjercicio.setBackground(Color.WHITE);
		panelEjercicio.setBounds(10, 131, 508, 318);
		contentPane.add(panelEjercicio);
		panelEjercicio.setLayout(null);

		lblNombreEjercicio = new JLabel("Nombre Ejercicio");
		lblNombreEjercicio.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNombreEjercicio.setBounds(10, 17, 294, 29);
		panelEjercicio.add(lblNombreEjercicio);

		lblDescripcionEjercicio = new JLabel("Descripcion Ejercicio");
		lblDescripcionEjercicio.setVerticalAlignment(SwingConstants.TOP);
		lblDescripcionEjercicio.setBounds(10, 57, 312, 89);
		panelEjercicio.add(lblDescripcionEjercicio);

		lblAvisos = new JLabel("");
		lblAvisos.setBounds(344, 57, 164, 29);
		panelEjercicio.add(lblAvisos);

		lblCronoEjercicio = new JLabel("00:00");
		lblCronoEjercicio.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCronoEjercicio.setBounds(344, 17, 164, 29);
		panelEjercicio.add(lblCronoEjercicio);

		panelSerie1 = new JPanel();
		panelSerie1.setBounds(10, 157, 150, 150);
		panelEjercicio.add(panelSerie1);
		panelSerie1.setLayout(null);
		lblFotoSerie1 = new JLabel("Foto1");
		lblFotoSerie1.setBounds(10, 0, 130, 110);
		panelSerie1.add(lblFotoSerie1);

		lblCronoSerie1 = new JLabel("");
		lblCronoSerie1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCronoSerie1.setBounds(10, 125, 80, 14);
		panelSerie1.add(lblCronoSerie1);

		lblSerie1 = new JLabel("Serie 1");
		lblSerie1.setBounds(10, 110, 80, 15);
		panelSerie1.add(lblSerie1);

		panelSerie2 = new JPanel();
		panelSerie2.setLayout(null);
		panelSerie2.setBounds(180, 157, 150, 150);
		panelEjercicio.add(panelSerie2);

		lblFotoSerie2 = new JLabel("Foto2");
		lblFotoSerie2.setBounds(10, 0, 130, 110);
		panelSerie2.add(lblFotoSerie2);

		lblCronoSerie2 = new JLabel("");
		lblCronoSerie2.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCronoSerie2.setBounds(10, 125, 80, 14);
		panelSerie2.add(lblCronoSerie2);

		lblSerie = new JLabel("Serie 2");
		lblSerie.setBounds(10, 110, 80, 15);
		panelSerie2.add(lblSerie);

		panelSerie3 = new JPanel();
		panelSerie3.setLayout(null);
		panelSerie3.setBounds(348, 157, 150, 150);
		panelEjercicio.add(panelSerie3);

		lblFotoSerie3 = new JLabel("Foto3");
		lblFotoSerie3.setBounds(10, 0, 130, 110);
		panelSerie3.add(lblFotoSerie3);

		lblCronoSerie3 = new JLabel("");
		lblCronoSerie3.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCronoSerie3.setBounds(10, 125, 80, 14);
		panelSerie3.add(lblCronoSerie3);

		lblSerie3 = new JLabel("Serie 3");
		lblSerie3.setBounds(10, 110, 80, 15);
		panelSerie3.add(lblSerie3);

		lblFondoEjercicio = new JLabel("");
		lblFondoEjercicio.setBounds(0, 0, 508, 318);
		panelEjercicio.add(lblFondoEjercicio);
		ImageIcon iconoGris = new ImageIcon(getClass().getResource("/img/FondoGris.jpg"));
		Image imagenGris = iconoGris.getImage().getScaledInstance(
				lblFondoEjercicio.getWidth(),
				lblFondoEjercicio.getHeight(),
				Image.SCALE_SMOOTH
				);
		lblFondoEjercicio.setIcon(new ImageIcon(imagenGris));

		panelWorkout = new JPanel();
		panelWorkout.setBounds(10, 11, 508, 109);
		contentPane.add(panelWorkout);

		panelWorkout.setLayout(null);
		lblDescripcionWorkout = new JLabel("Descripcion Workout");
		lblDescripcionWorkout.setVerticalAlignment(SwingConstants.TOP);
		lblDescripcionWorkout.setForeground(Color.WHITE);
		lblDescripcionWorkout.setBounds(112, 40, 209, 59);
		panelWorkout.add(lblDescripcionWorkout);

		lblNombreWorkout = new JLabel("Nombre Workout");
		lblNombreWorkout.setForeground(Color.WHITE);
		lblNombreWorkout.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNombreWorkout.setBounds(112, 14, 209, 23);
		panelWorkout.add(lblNombreWorkout);

		btnIniciar = new JButton("Iniciar");
		btnIniciar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				btnIniciar.setVisible(false);
				btnPausar.setVisible(true);
				iniciarCronometrosAscendentes();
				iniciarCuentaInicio();
			}
		});
		btnIniciar.setBounds(331, 40, 92, 23);
		panelWorkout.add(btnIniciar);
		btnIniciar.setBackground(new Color(50, 205, 50));

		btnSiguiente = new JButton("Siguiente");
		btnSiguiente.setBounds(331, 62, 92, 23);
		panelWorkout.add(btnSiguiente);
		btnSiguiente.setBackground(new Color(0, 191, 255));
		btnSiguiente.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cargarSiguienteEjercicio();
			}
		});

		btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				finalizarWorkout();
			}
		});
		btnSalir.setBounds(427, 40, 65, 45);
		panelWorkout.add(btnSalir);
		btnSalir.setBackground(Color.RED);

		lblCronoWorkout = new JLabel("00:00");
		lblCronoWorkout.setForeground(Color.WHITE);
		lblCronoWorkout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCronoWorkout.setBounds(331, 11, 176, 29);
		panelWorkout.add(lblCronoWorkout);
		ImageIcon iconoElorrieta = new ImageIcon(getClass().getResource("/img/FondoWorkout.jpg"));

		btnPausar = new JButton("Pausar");
		btnPausar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pausarCronometros();
				btnPausar.setVisible(false);
				btnReanudar.setVisible(true);
			}
		});
		btnPausar.setBackground(new Color(50, 205, 50));
		btnPausar.setBounds(332, 40, 92, 23);
		panelWorkout.add(btnPausar);

		btnReanudar = new JButton("Reanudar");
		btnReanudar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				reanudarCronometros();
				btnReanudar.setVisible(false);
				btnPausar.setVisible(true);
			}
		});
		btnReanudar.setBackground(new Color(50, 205, 50));
		btnReanudar.setBounds(332, 40, 92, 23);
		panelWorkout.add(btnReanudar);

		JLabel lblFondoWorkout = new JLabel("");
		lblFondoWorkout.setBounds(0, 0, 508, 108);
		panelWorkout.add(lblFondoWorkout);

		Image imagenElorrieta = iconoElorrieta.getImage().getScaledInstance(
				lblFondoWorkout.getWidth(),
				lblFondoWorkout.getHeight(),
				Image.SCALE_SMOOTH
				);
		lblFondoWorkout.setIcon(new ImageIcon(imagenElorrieta));
		btnIniciar.setVisible(true);
		btnSiguiente.setVisible(true);
		btnSiguiente.setEnabled(false);
		btnSalir.setVisible(true);
		btnPausar.setVisible(false);
		btnReanudar.setVisible(false);

		conexion = conexionParametro;

		// Buscamos directamente el objeto Ejercicio que hemos seleccionado
		for (Ejercicio ejercicio : workoutActual.getEjercicios()) {
			if (ejercicio.getNombre().equals(nombreEjercicio)) {
				ejercicioActual = ejercicio;
			}
		}

		// Cogemos la primera serie del ejercicio
		for (Serie serie : ejercicioActual.getSeries()) {
			if (serieActual == null) {
				serieActual = serie;
			}
		}
		rellenarCampos(workoutActual, ejercicioActual);
		workouts = workoutsParametro;

	}

	// Inicia los cronos de workout y de ejercicio

	private void iniciarCronometrosAscendentes() {
		// El crono del workout solo se crea una vez
		if (cronoWorkout == null) {
			cronoWorkout = new CronoWorkout(lblCronoWorkout);
			cronoWorkout.start();
		}
		// El crono del ejercicio se crea para cada ejercicio
		if (cronoEjercicio == null) {
			cronoEjercicio = new CronoEjercicio(lblCronoEjercicio);
			cronoEjercicio.start();
		}
	}

	// empieza la cuenta atras de 5 seg

	private void iniciarCuentaInicio() {
		cronoAvisos = new CronoAvisos("INICIO", 5, lblAvisos, this);
		cronoAvisos.start();
	}

	// Pausa todos los cronos

	private void pausarCronometros() {
		if (cronoWorkout != null) {
			cronoWorkout.setPausado(true);
		}
		if (cronoEjercicio != null) {
			cronoEjercicio.setPausado(true);
		}
		if (cronoAvisos != null) {
			cronoAvisos.setPausado(true);
		}
		if (cronoSerieActual != null) {
			cronoSerieActual.setPausado(true);
		}
	}

	// Reanuda todos los cronos que estaban parados

	private void reanudarCronometros() {
		if (cronoWorkout != null) {
			cronoWorkout.setPausado(false);
		}
		if (cronoEjercicio != null) {
			cronoEjercicio.setPausado(false);
		}
		if (cronoAvisos != null) {
			cronoAvisos.setPausado(false);
		}
		if (cronoSerieActual != null) {
			cronoSerieActual.setPausado(false);
		}
	}

	// Cuando termina un inicio o descanso se ejecuta este codigo

	public void notificarFinAviso(String tipoAviso) {

		if (tipoAviso.equals("INICIO")) {

			lblAvisos.setText("");
			// Cogemos la etiqueta que corresponde a la serie actual
			JLabel lblCronoSerieActual = obtenerJlabelSerie(numeroSerieActual);
			// Instanciamos el crono de la serie actual y lo empezamos
			cronoSerieActual = new CronoSerie(serieActual.getDuracion(), lblCronoSerieActual, this);
			cronoSerieActual.start();

		} else if (tipoAviso.equals("DESCANSO")) {
			lblAvisos.setText("");

			// Si la serie actual no es la ultima, buscamos la siguiente
			if (!esUltimaSerie()) {
				boolean serieEncontrada = false;
				boolean siguienteEncontrada = false;

				for (Serie serie : ejercicioActual.getSeries()) {

					if (serieEncontrada && !siguienteEncontrada) {
						serieActual = serie;
						siguienteEncontrada = true;
					}

					if (!serieEncontrada && serie == serieActual) {
						serieEncontrada = true;
					}
				}
				numeroSerieActual++;
				btnPausar.setVisible(false);
				btnReanudar.setVisible(false);
				btnIniciar.setVisible(true);

			} else {

				// Cuando se han terminado todas las series del ejercicio
				// Si hay mas ejercicios habilitamos el boton de siguiente
				if (haySiguienteEjercicio()) {
					btnPausar.setVisible(false);
					btnReanudar.setVisible(false);
					btnIniciar.setVisible(false);
					btnSiguiente.setEnabled(true);

				} else {
					// Si era el ultimo ejericico se termina el wokrout
					finalizarWorkout();
				}
			}
		}
	}

	// Se ejecuta este codigo cuando termina el crono de la serie

	public void notificarFinSerie() {
		// Cogemos el descanso de este ejercicio
		int descanso = ejercicioActual.getDescanso();

		// Y si era la ultima serie el ejercicio ya se ha terminado
		if (esUltimaSerie()) {
			ejerciciosCompletados.add(ejercicioActual);
		}
		// Despues de cada serie empieza el descanso
		cronoAvisos = new CronoAvisos("DESCANSO", descanso, lblAvisos, this);
		cronoAvisos.start();
	}

	// Devuelve el JLabel de la serie que corresponde a la tarjeta

	private JLabel obtenerJlabelSerie(int numeroSerie) {
		JLabel etiqueta = null;
		switch (numeroSerie) {
		case 1:
			etiqueta = lblCronoSerie1;
			break;
		case 2:
			etiqueta = lblCronoSerie2;
			break;
		case 3:
			etiqueta = lblCronoSerie3;
			break;
		}
		return etiqueta;
	}

	// Carga el siguiente ejercicio del workout
	private void cargarSiguienteEjercicio() {

		Ejercicio siguienteEjercicio = null;
		boolean ejercicioEncontrado = false;

		// Buscamos el objeto Ejercicio que viene despues del actual
		for (Ejercicio ejercicio : workoutActual.getEjercicios()) {

			if (ejercicioEncontrado && siguienteEjercicio == null) {
				siguienteEjercicio = ejercicio;
			}

			if (ejercicio == ejercicioActual) {
				ejercicioEncontrado = true;
			}
		}

		if (siguienteEjercicio != null) {

			if (cronoEjercicio != null) {
				cronoEjercicio.interrupt();
				cronoEjercicio = null;
			}
			ejercicioActual = siguienteEjercicio;
			serieActual = null;
			numeroSerieActual = 1;

			// Cogemos la primera serie del nuevo ejercicio
			for (Serie serie : ejercicioActual.getSeries()) {
				if (serieActual == null) {
					serieActual = serie;
				}
			}
			lblCronoEjercicio.setText("00:00");
			rellenarCampos(workoutActual, ejercicioActual);
			btnSiguiente.setEnabled(false);
			btnIniciar.setVisible(true);
			btnPausar.setVisible(false);
			btnReanudar.setVisible(false);
		}
	}

	// Comprueba si la serie actual es la ultima del ejercicio

	private boolean esUltimaSerie() {
		Serie ultimaSerie = null;
		boolean esUltimaSerie = false;
		// Aqui estamos cogiendo la ultima serie ya que da las vueltas en relacion al numero de series que hay en el ejer y se queda con la utima
		for (Serie serie : ejercicioActual.getSeries()) {
			ultimaSerie = serie;
		}
		// Y aqui comparamos si la serie actual que tenemos como atributo de la clase es igual a la ultima de los ejercicios
		if (serieActual == ultimaSerie) {
			esUltimaSerie  = true;
		}
		return esUltimaSerie;
	}

	// Comprueba si hay otro ejercicio despues del actual
	private boolean haySiguienteEjercicio() {
		boolean ejercicioEncontrado = false;
		boolean haySiguiente = false;

		// Aqui recorremos todos los ejercicios uno por uno y si por ejemplo estamos en el utlimo para que haySiguiente fuera true le faltaria dar otra
		// vuelta al for pero no puede ya que la vuelta en la que esta es la ultima
		for (Ejercicio ejercicio : workoutActual.getEjercicios()) {
			if (ejercicioEncontrado) {
				haySiguiente = true;
			}
			if (ejercicio == ejercicioActual) {
				ejercicioEncontrado = true;
			}
		}
		return haySiguiente;
	}

	// Pone los datos del workout y del ejercicio en la ventana
	private void rellenarCampos(Workout workout, Ejercicio ejercicio) {

		panelSerie1.setVisible(false);
		panelSerie2.setVisible(false);
		panelSerie3.setVisible(false);

		lblNombreWorkout.setText(workout.getNombre());
		lblDescripcionWorkout.setText("<html>" + workout.getDescripcion() + "</html>");
		lblNombreEjercicio.setText(ejercicio.getNombre());
		lblDescripcionEjercicio.setText("<html>" + ejercicio.getDescripcion() + "</html>");

		int numSerie = 1;
		for (Serie serie : ejercicio.getSeries()) {
			switch (numSerie) {
			case 1:
				lblSerie1.setText(serie.getNombre());
				lblCronoSerie1.setText(String.valueOf(serie.getDuracion()) + " s");
				panelSerie1.setVisible(true);
				ImageIcon iconoSerie1 = new ImageIcon(getClass().getResource(serie.getFoto()));
				Image imagenSerie1 = iconoSerie1.getImage().getScaledInstance(
						lblFotoSerie1.getWidth(),
						lblFotoSerie1.getHeight(),
						Image.SCALE_SMOOTH);
				lblFotoSerie1.setIcon(new ImageIcon(imagenSerie1));
				break;
			case 2:
				lblSerie.setText(serie.getNombre());
				lblCronoSerie2.setText(String.valueOf(serie.getDuracion()) + " s");
				panelSerie2.setVisible(true);
				ImageIcon iconoSerie2 = new ImageIcon(getClass().getResource(serie.getFoto()));
				Image imagenSerie2 = iconoSerie2.getImage().getScaledInstance(
						lblFotoSerie2.getWidth(),
						lblFotoSerie2.getHeight(),
						Image.SCALE_SMOOTH);
				lblFotoSerie2.setIcon(new ImageIcon(imagenSerie2));
				break;
			case 3:
				lblSerie3.setText(serie.getNombre());
				lblCronoSerie3.setText(String.valueOf(serie.getDuracion()) + " s");
				panelSerie3.setVisible(true);
				ImageIcon iconoSerie3 = new ImageIcon(getClass().getResource(serie.getFoto()));
				Image imagenSerie3 = iconoSerie3.getImage().getScaledInstance(
						lblFotoSerie3.getWidth(),
						lblFotoSerie3.getHeight(),
						Image.SCALE_SMOOTH);
				lblFotoSerie3.setIcon(new ImageIcon(imagenSerie3));
				break;
			}
			numSerie++;
		}
	}

	// Detiene todos los cronos
	private void detenerCronometros() {
		if (cronoWorkout != null) {
			cronoWorkout.interrupt();
		}
		if (cronoEjercicio != null) {
			cronoEjercicio.interrupt();
		}
		if (cronoAvisos != null) {
			cronoAvisos.interrupt();
		}
		if (cronoSerieActual != null) {
			cronoSerieActual.interrupt();
		}
	}

	// Muestra el resumen de lo que ha hecho el usuario y vuelve a la ventana de los workouts
	private void finalizarWorkout() {

		// Nos cargamos todos los cronos
		detenerCronometros();
		int totalEjercicios = workoutActual.getEjercicios().size();
		int realizados = ejerciciosCompletados.size();
		int porcentaje = 0;

		if (totalEjercicios > 0) {
			porcentaje = (int) ((realizados * 100.0) / totalEjercicios);
		}

		// Hacemos la lista de los ejers completados
		String ejercicios = "";
		if (ejerciciosCompletados.isEmpty()) {
			ejercicios = "Todavía no has completado ningún ejercicio.";
		} else {
			for (Ejercicio ejercicio : ejerciciosCompletados) {
				ejercicios += "- " + ejercicio.getNombre() + "\n";
			}
		}

		// Ponemos el mensaje
		String mensaje = "¡Buen trabajo! Cada entrenamiento cuenta.";
		if (porcentaje == 100) {
			mensaje = "¡Enhorabuena! Has completado todo el workout.";
		} else if (porcentaje >= 50) {
			mensaje = "¡Muy bien! Has conseguido completar buena parte del entrenamiento.";
		}

		String resumen = "RESUMEN DEL WORKOUT\n\n"
				+ "Ejercicios completados: " + realizados + " / " + totalEjercicios + "\n\n"
				+ "Ejercicios realizados:\n" + ejercicios + "\n"
				+ "Tiempo total: " + lblCronoWorkout.getText() + "\n"
				+ "Porcentaje completado: " + String.valueOf(porcentaje) + "%\n\n"
				+ mensaje;
		JOptionPane.showMessageDialog(this, resumen, "Entrenamiento finalizado", JOptionPane.INFORMATION_MESSAGE);

		// Añadimos al historico lo que ha hecho
		if (conexion) {
			// Añadimos al historico el progreso de los ejers
			añadirAlHistorico(realizados, porcentaje);
			// Subimos el nivel del usuario si procede
			subirNivelUsuario();
			// Volvemos a la pantalla de los workouts
			volverVentanaWorkouts();
		} else {
			añadirAlHistoricoXML(realizados, porcentaje);
			subirNivelUsuarioOffline();
			volverVentanaWorkouts();
		}
	}


	private void subirNivelUsuarioOffline() {
		int workoutsNivel1 = 0;
		int workoutsNivel2 = 0;
		int workoutsNivel3 = 0;
		int workoutsUsuarioNivel1 = 0;
		int workoutsUsuarioNivel2 = 0;
		int workoutsUsuarioNivel3 = 0;

		for (Workout workout: workouts) {
			if (workout.getNivel() == 0) {
				workoutsNivel1++;
			}
			if (workout.getNivel() == 1) {
				workoutsNivel2++;
			}
			if (workout.getNivel() == 2) {
				workoutsNivel3++;
			}
		}

		for (Historico workoutHistorico: usuarioActual.getHistorico()) {
			if (workoutHistorico.getNivel() == 0) {
				if (workoutHistorico.getPorcentajeEjercicios() == 100) {
					workoutsUsuarioNivel1++;
				}
			}
			if (workoutHistorico.getNivel() == 1) {
				if (workoutHistorico.getPorcentajeEjercicios() == 100) {
					workoutsUsuarioNivel2++;
				}
			}
			if (workoutHistorico.getNivel() == 2) {
				if (workoutHistorico.getPorcentajeEjercicios() == 100) {
					workoutsUsuarioNivel3++;
				}
			}
		}

		if (workoutsNivel3 == workoutsUsuarioNivel3) {
			if(usuarioActual.getNivel() < 2) {
				usuarioActual.setNivel(2);
			}

		}else if (workoutsNivel2 == workoutsUsuarioNivel2) {
			if(usuarioActual.getNivel() < 2) {
				usuarioActual.setNivel(2);
			}
		}else if (workoutsNivel1 == workoutsUsuarioNivel1) {
			if(usuarioActual.getNivel() < 1) {
				usuarioActual.setNivel(1);
			}
		}
	}

	private void añadirAlHistoricoXML(int realizados, int porcentaje) {

		if (realizados > 0) {
			boolean workoutEncontrado = false;
			for (Historico workoutHistorico : usuarioActual.getHistorico()) {
				// Buscamos si el workout ya existe en el historico
				if (workoutHistorico.getNombreWorkout().equals(workoutActual.getNombre())) {
					workoutEncontrado = true;
					// Solo actualizamos si ha conseguido un porcentaje mayor de progreso qu een el anterior workout
					if (porcentaje > workoutHistorico.getPorcentajeEjercicios()) {
						workoutHistorico.setPorcentajeEjercicios(porcentaje);
						workoutHistorico.setFecha(LocalDate.now());
						workoutHistorico.setTiempoTotal(convertirASegundos(lblCronoWorkout.getText()));
						workoutHistorico.setTiempoPrevisto(calcularTiempoPrevisto());
					}
				}
			}

			// Si no existe lo añadimos
			if (!workoutEncontrado) {
				Historico historico = new Historico();
				historico.setNombreWorkout(workoutActual.getNombre());
				historico.setNivel(workoutActual.getNivel());
				historico.setTiempoTotal(convertirASegundos(lblCronoWorkout.getText()));
				historico.setTiempoPrevisto(calcularTiempoPrevisto());
				historico.setFecha(LocalDate.now());
				historico.setPorcentajeEjercicios(porcentaje);
				usuarioActual.getHistorico().add(historico);
			}
			try {
				controlador.actualizarHistoricoXML(usuarioActual);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}

	private void añadirAlHistorico(int realizados, int porcentaje) {

		if (realizados > 0) {
			boolean workoutEncontrado = false;
			for (Historico workoutHistorico : usuarioActual.getHistorico()) {
				// Buscamos si el workout ya existe en el historico
				if (workoutHistorico.getNombreWorkout().equals(workoutActual.getNombre())) {
					workoutEncontrado = true;
					// Solo actualizamos si ha conseguido un porcentaje mayor de progreso qu een el anterior workout
					if (porcentaje > workoutHistorico.getPorcentajeEjercicios()) {
						workoutHistorico.setPorcentajeEjercicios(porcentaje);
						workoutHistorico.setFecha(LocalDate.now());
						workoutHistorico.setTiempoTotal(convertirASegundos(lblCronoWorkout.getText()));
					}
				}
			}

			// Si no existe lo añadimos
			if (!workoutEncontrado) {
				Historico historico = new Historico();
				historico.setNombreWorkout(workoutActual.getNombre());
				historico.setNivel(workoutActual.getNivel());
				historico.setTiempoTotal(convertirASegundos(lblCronoWorkout.getText()));
				historico.setTiempoPrevisto(calcularTiempoPrevisto());
				historico.setFecha(LocalDate.now());
				historico.setPorcentajeEjercicios(porcentaje);
				usuarioActual.getHistorico().add(historico);
			}
			try {
				db = controlador.conectar();
				// Con esto actualizamos el historico del usuario en la Firebase
				controlador.actualizarHistorico(db, usuarioActual);
				db.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private int calcularTiempoPrevisto() {
		int tiempoPrevisto = 0;
		// Recorremos todos los ejercicios del workout
		for (Ejercicio ejercicio : workoutActual.getEjercicios()) {
			// Sumamos el tiempo de descanso de todas sus series
			int i = 0;
			for (Serie serie : ejercicio.getSeries()) {
				tiempoPrevisto += serie.getDuracion();
				if (i > 0) {
					tiempoPrevisto += ejercicio.getDescanso();
				}
				i++;
			}
		}

		return tiempoPrevisto;
	}

	private void subirNivelUsuario() {

		int workoutsNivel1 = 0;
		int workoutsNivel2 = 0;
		int workoutsNivel3 = 0;
		int workoutsUsuarioNivel1 = 0;
		int workoutsUsuarioNivel2 = 0;
		int workoutsUsuarioNivel3 = 0;

		for (Workout workout: workouts) {
			if (workout.getNivel() == 0) {
				workoutsNivel1++;
			}
			if (workout.getNivel() == 1) {
				workoutsNivel2++;
			}
			if (workout.getNivel() == 2) {
				workoutsNivel3++;
			}
		}

		for (Historico workoutHistorico: usuarioActual.getHistorico()) {
			if (workoutHistorico.getNivel() == 0) {
				if (workoutHistorico.getPorcentajeEjercicios() == 100) {
					workoutsUsuarioNivel1++;
				}
			}
			if (workoutHistorico.getNivel() == 1) {
				if (workoutHistorico.getPorcentajeEjercicios() == 100) {
					workoutsUsuarioNivel2++;
				}
			}
			if (workoutHistorico.getNivel() == 2) {
				if (workoutHistorico.getPorcentajeEjercicios() == 100) {
					workoutsUsuarioNivel3++;
				}
			}
		}

		boolean actualizar = false;

		if (workoutsNivel3 == workoutsUsuarioNivel3) {
			if(usuarioActual.getNivel() < 2) {
				usuarioActual.setNivel(2);
				actualizar = true;
			}

		}else if (workoutsNivel2 == workoutsUsuarioNivel2) {
			if(usuarioActual.getNivel() < 2) {
				usuarioActual.setNivel(2);
				actualizar = true;
			}
		}else if (workoutsNivel1 == workoutsUsuarioNivel1) {
			if(usuarioActual.getNivel() < 1) {
				usuarioActual.setNivel(1);
				actualizar = true;
			}
		}
		// Si hay que actualizar el nivel entramos aqui y lo hacemos
		if (actualizar) {
			try {
				db = controlador.conectar();
				// Con esto actualizamos el nivel del usuario
				controlador.actualizarUsuario(db, usuarioActual, usuarioActual.getNomUsuario());
				db.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private int convertirASegundos(String tiempo) {
		String[] partes = tiempo.split(":");

		int minutos = Integer.parseInt(partes[0]);
		int segundos = Integer.parseInt(partes[1]);

		int totalSegundos = (minutos * 60) + segundos;

		return totalSegundos;
	}

	private void volverVentanaWorkouts() {
		VentanaWorkouts ventanaWorkouts = new VentanaWorkouts(usuarioActual, workouts, conexion);
		ventanaWorkouts.setVisible(true);
		this.dispose();
	}
}
