package vista;

import java.awt.Desktop;
import java.awt.EventQueue;

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
	private DefaultTableModel modelo;
	private DefaultTableModel modeloEjercicios;

	private JTable table;
	private JTable tableEjercicios;
	private Firestore db;
	private Controlador controlador = new Controlador();

	private ArrayList<Workout> workouts;
	private ArrayList<Workout> workoutsFiltrados;
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
		scrollPane.setBounds(10, 117, 576, 107);
		contentPane.add(scrollPane);

		modelo = new DefaultTableModel() {
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};
		modelo.addColumn("Nivel");
		modelo.addColumn("Nombre");
		modelo.addColumn("Descripcion");
		modelo.addColumn("Guia");


		table = new JTable(modelo);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rellenarTablaEjercicios(workoutsFiltrados);
				int fila = table.convertRowIndexToModel(table.getSelectedRow());			
				int columna = table.getSelectedColumn();
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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		scrollPane.add(table);
		scrollPane.setViewportView(table);

		cbNivel = new JComboBox<>();

		cbNivel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		cbNivel.setModel(new DefaultComboBoxModel<>(
				new String[] {"Todos los niveles", "Nivel 1", "Nivel 2", "Nivel 3"}));

		cbNivel.setBounds(422, 77, 164, 29);
		contentPane.add(cbNivel);

		JScrollPane scrollPaneEjercicios = new JScrollPane();
		scrollPaneEjercicios.setBounds(10, 357, 576, 107);
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
		lblEjercicios.setBounds(10, 317, 293, 29);
		contentPane.add(lblEjercicios);

		JButton btnComenzarEntrenamiento = new JButton("Comenzar Entrenamiento");
		btnComenzarEntrenamiento.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnComenzarEntrenamiento.setBounds(298, 309, 288, 37);
		contentPane.add(btnComenzarEntrenamiento);

		cbNivel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filtrarWorkouts();
			}
		});


		usuarioActual = usuario;
		rellenarWorkouts();
	}


	private void rellenarWorkouts() {
		try {
			db = controlador.conectar();
			workouts = controlador.rellenarWorkouts(db);
			workoutsFiltrados = workouts;
			crearBackups(workouts);
			rellenarTablaWorkouts(workouts);
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
		int fila = table.convertRowIndexToModel(table.getSelectedRow());
		String nombreWorkout = table.getValueAt(fila, 1).toString();

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
		modelo.setRowCount(0);
		for (Workout workout : listaWorkouts) {
			String[] workoutArray = {String.valueOf(workout.getNivel() + 1), workout.getNombre(), workout.getDescripcion(), "Ver guia"};
			modelo.addRow(workoutArray);
		}
	}

	private void filtrarWorkouts() {
		workoutsFiltrados = new ArrayList<>();

		int nivelSeleccionado = cbNivel.getSelectedIndex();

		if (nivelSeleccionado == 0) {
			workoutsFiltrados.addAll(workouts);
		} else {
			for (Workout workout : workouts) {
				if (workout.getNivel() == nivelSeleccionado - 1) {
					workoutsFiltrados.add(workout);
				}
			}
		}

		rellenarTablaWorkouts(workoutsFiltrados);
	}

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
