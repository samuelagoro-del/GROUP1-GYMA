package vista;

import java.awt.Desktop;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.google.cloud.firestore.Firestore;

import controlador.Controlador;
import modelo.Ejercicio;
import modelo.Serie;
import modelo.Usuario;
import modelo.Workout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class VentanaWorkouts extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultTableModel modeloWorkout;
	private DefaultTableModel modeloEjercicios;

	private JTable tableWorkout;
	private JTable tableEjercicios;
	private Firestore db;
	private Controlador controlador = new Controlador();

	private ArrayList<Workout> workouts = new ArrayList<Workout>();
	private ArrayList<Workout> workoutsFiltrados = new ArrayList<Workout>();
	private JComboBox<String> cbNivel;

	private Usuario usuarioActual;


	public VentanaWorkouts(Usuario usuario) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 612, 557);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblWorkouts = new JLabel("Sesiones de Entrenamiento");
		lblWorkouts.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblWorkouts.setBounds(10, 71, 293, 29);
		contentPane.add(lblWorkouts);

		JButton btnPerfil = new JButton("Perfil");
		btnPerfil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enseñarPerfil();
			}
		});
		btnPerfil.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnPerfil.setBounds(10, 11, 92, 37);
		contentPane.add(btnPerfil);

		JButton btnVolver = new JButton("Volver");
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enseñarLogin();
			}
		});
		btnVolver.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnVolver.setBounds(494, 11, 92, 37);
		contentPane.add(btnVolver);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 117, 576, 158);
		contentPane.add(scrollPane);

		modeloWorkout = new DefaultTableModel() {
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};
		modeloWorkout.addColumn("Nivel");
		modeloWorkout.addColumn("Nombre");
		modeloWorkout.addColumn("Descripcion");
		modeloWorkout.addColumn("Guia");


		tableWorkout = new JTable(modeloWorkout);
		tableWorkout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rellenarTablaEjercicios(workoutsFiltrados);
				int fila = tableWorkout.convertRowIndexToModel(tableWorkout.getSelectedRow());			
				int columna = tableWorkout.getSelectedColumn();
				if (fila != -1 && columna == 3) {
					String url = workoutsFiltrados.get(fila).getUrl();
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch(URISyntaxException urise) {
						urise.printStackTrace();
					}
				}
			}
		});
		tableWorkout.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableWorkout.setAutoCreateRowSorter(true);
		scrollPane.add(tableWorkout);
		scrollPane.setViewportView(tableWorkout);

		cbNivel = new JComboBox<>();

		cbNivel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		if (usuario.getNivel() == 0) {
			cbNivel.setModel(new DefaultComboBoxModel<>(new String[] {"Todos los niveles", "Nivel 1"}));
		} else if (usuario.getNivel() == 1) {
			cbNivel.setModel(new DefaultComboBoxModel<>(new String[] {"Todos los niveles", "Nivel 1", "Nivel 2"}));
		} else {
			cbNivel.setModel(new DefaultComboBoxModel<>(new String[] {"Todos los niveles", "Nivel 1", "Nivel 2", "Nivel 3", }));

		}


		cbNivel.setBounds(422, 77, 164, 29);
		contentPane.add(cbNivel);

		JScrollPane scrollPaneEjercicios = new JScrollPane();
		scrollPaneEjercicios.setBounds(10, 349, 576, 158);
		contentPane.add(scrollPaneEjercicios);

		modeloEjercicios = new DefaultTableModel() {
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		modeloEjercicios.addColumn("Ejercicio");
		modeloEjercicios.addColumn("Descripcion");
		modeloEjercicios.addColumn("Duracion");

		tableEjercicios = new JTable(modeloEjercicios);
		tableEjercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableEjercicios.setAutoCreateRowSorter(true);
		scrollPaneEjercicios.add(tableEjercicios);
		scrollPaneEjercicios.setViewportView(tableEjercicios);

		JLabel lblEjercicios = new JLabel("Ejercicios");
		lblEjercicios.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblEjercicios.setBounds(10, 309, 105, 29);
		contentPane.add(lblEjercicios);

		JButton btnComenzarEntrenamiento = new JButton("Comenzar Entrenamiento");
		btnComenzarEntrenamiento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enseñarVentanaEjercicio();
			}
		});
		btnComenzarEntrenamiento.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnComenzarEntrenamiento.setBounds(298, 301, 288, 37);
		contentPane.add(btnComenzarEntrenamiento);

		cbNivel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrarWorkouts();
			}
		});


		usuarioActual = usuario;
		rellenarWorkouts();
	}


	private void enseñarVentanaEjercicio() {

		// Cogemos las filas seleccionadas
		int filaWorkout = tableWorkout.getSelectedRow();
		int filaEjercicio = tableEjercicios.getSelectedRow();

		if (filaWorkout == -1) {
			JOptionPane.showMessageDialog(this, "Tienes que seleccionar un workout.", "Error de seleccion de fila", JOptionPane.ERROR_MESSAGE);
		} else if (filaEjercicio == -1) {
			JOptionPane.showMessageDialog(this, "Tienes que seleccionar un ejercicio del workout.", "Error de seleccion de fila", JOptionPane.ERROR_MESSAGE);
		} else {
			// Convertimos las filas visibles a las filas del modelo
			filaWorkout = tableWorkout.convertRowIndexToModel(filaWorkout);
			filaEjercicio = tableEjercicios.convertRowIndexToModel(filaEjercicio);

			Workout workoutVentanaEjercicio = new Workout();
			String nombreWorkout = tableWorkout.getModel().getValueAt(filaWorkout, 1).toString();
			for (Workout workout : workouts) {
				if (workout.getNombre().equals(nombreWorkout)) {
					workoutVentanaEjercicio = workout;
				}
			}

			String nombreEjercicio = tableEjercicios.getModel().getValueAt(filaEjercicio, 0).toString();
			VentanaEjercicio ventanaEjercicio = new VentanaEjercicio(workoutVentanaEjercicio, nombreEjercicio, usuarioActual);
			ventanaEjercicio.setVisible(true);
			this.dispose();
		}
	}






	private void rellenarWorkouts() {
		try {
			db = controlador.conectar();
			workouts = controlador.rellenarWorkouts(db);
			for (Workout workout: workouts) {
				if (workout.getNivel() == 0) {
					workoutsFiltrados.add(workout);
				}
			}
			crearBackups(workouts);
			filtrarWorkouts();
			rellenarTablaWorkouts(workoutsFiltrados);
			db.close();

		}catch(ExecutionException ee) {
			ee.printStackTrace();
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void crearBackups(ArrayList<Workout> workouts) {
		try {
			controlador.crearBackups(db, workouts);
			System.out.println("Backups creados correctamente.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


	private void rellenarTablaEjercicios(ArrayList<Workout> listaWorkouts) {
		modeloEjercicios.setRowCount(0);
		int fila = tableWorkout.convertRowIndexToModel(tableWorkout.getSelectedRow());
		String nombreWorkout = tableWorkout.getModel().getValueAt(fila, 1).toString();
		for (Workout workout : listaWorkouts) {
			if (nombreWorkout.equals(workout.getNombre())) {
				ArrayList<Ejercicio> arrayEjercicios = workout.getEjercicios();
				for (Ejercicio ejercicio: arrayEjercicios) {
					String duracion = duracionEjercicio(ejercicio);
					String[] ejercicioArray = {ejercicio.getNombre(), ejercicio.getDescripcion(), duracion};
					modeloEjercicios.addRow(ejercicioArray);
				}
			}
		}
	}


	private String duracionEjercicio(Ejercicio ejercicio) {
		int duracionTotal = 0;
		ArrayList<Serie> series = ejercicio.getSeries();

		for (Serie serie : series) {
			duracionTotal = duracionTotal + serie.getDuracion();
		}

		// Sumamos los descansos entre series
		if (series.size() > 1) {
			duracionTotal = duracionTotal + (series.size() - 1) * ejercicio.getDescanso();
		}

		int minutos = duracionTotal / 60;
		int segundos = duracionTotal % 60;

		return String.format("%02d:%02d", minutos, segundos);
	}


	private void rellenarTablaWorkouts(ArrayList<Workout> listaWorkouts) {
		
	}

	private void filtrarWorkouts() {
		
		// Vaciamos el arrayList de los workoutsFiltrados para filtrarlos de nuevo con el nivel solicitado
		workoutsFiltrados = new ArrayList<>();

		// Cogemos el texto del combobox para saber cual es el nivel que se solicita
		String nivelSeleccionado = cbNivel.getSelectedItem().toString();

		// Y en relacion al nivel solicitado se rellena el array de los workoutsFiltrados de unos u otros workouts
		// en relacion al nivel del usuario y de cada Workout
		
		switch(nivelSeleccionado) {
		case "Todos los niveles":
			if (usuarioActual.getNivel() == 0) {
				for (Workout workout: workouts) {
					if (workout.getNivel() == 0) {
						workoutsFiltrados.add(workout);
					}
				}
			} else if (usuarioActual.getNivel() == 1) {
				for (Workout workout: workouts) {
					if (workout.getNivel() == 0 || workout.getNivel() == 1) {
						workoutsFiltrados.add(workout);
					}
				}
			} else if (usuarioActual.getNivel() == 2) {
				for (Workout workout: workouts) {
					if (workout.getNivel() == 0 || workout.getNivel() == 1 || workout.getNivel() == 2) {
						workoutsFiltrados.add(workout);
					}
				}
			}
			break;
		case "Nivel 1":
			for (Workout workout: workouts) {
				if (workout.getNivel() == 0) {
					workoutsFiltrados.add(workout);
				}
			}
			break;
		case "Nivel 2":
			for (Workout workout: workouts) {
				if (workout.getNivel() == 1) {
					workoutsFiltrados.add(workout);
				}
			}
			break;
		case "Nivel 3":
			for (Workout workout: workouts) {
				if (workout.getNivel() == 2) {
					workoutsFiltrados.add(workout);
				}
			}
			break;
		}
		
		// Añadimos a la tabla Workouts los workouts filtrados
		modeloWorkout.setRowCount(0);
		for (Workout workout : workoutsFiltrados) {
			String[] workoutArray = {String.valueOf(workout.getNivel() + 1), workout.getNombre(), workout.getDescripcion(), "Ver guia"};
			modeloWorkout.addRow(workoutArray);
		}	}

	private void enseñarLogin() {
		VentanaLogin ventanaLogin = new VentanaLogin();
		ventanaLogin.setVisible(true);
		this.dispose();
	}

	private void enseñarPerfil() {
		VentanaPerfil ventanaPerfil = new VentanaPerfil(usuarioActual);
		ventanaPerfil.setVisible(true);
		this.dispose();
	}
}
