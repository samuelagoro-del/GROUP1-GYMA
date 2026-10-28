
package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import modelo.Ejercicio;
import modelo.Serie;
import modelo.Usuario;
import modelo.Workout;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;
import javax.swing.SwingConstants;

public class VentanaEjercicio extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblNombreWorkout;
	private JLabel lblDescripcionWorkout;
	private JButton btnPausar;
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



	/**
	 * Create the frame.
	 * @param nombreEjercicio, Usuario usuario 
	 * @param workout 
	 */
	public VentanaEjercicio(Workout workout, String nombreEjercicio, Usuario usuarioActual) {
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
		
		lblAvisos = new JLabel("Avisos");
		lblAvisos.setBounds(344, 57, 164, 29);
		panelEjercicio.add(lblAvisos);
		
		lblCronoEjercicio = new JLabel("Crono Ejercicio");
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
		
		lblCronoSerie1 = new JLabel("CronoSerie1");
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
		
		lblCronoSerie2 = new JLabel("CronoSerie2");
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
		
		lblCronoSerie3 = new JLabel("CronoSerie3");
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
		
		btnPausar = new JButton("Iniciar");
		btnPausar.setBounds(332, 40, 92, 23);
		panelWorkout.add(btnPausar);
		btnPausar.setBackground(new Color(50, 205, 50));
		
		btnSiguiente = new JButton("Siguiente");
		btnSiguiente.setBounds(332, 62, 92, 23);
		panelWorkout.add(btnSiguiente);
		btnSiguiente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSiguiente.setBackground(new Color(0, 191, 255));
		
		btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				volverVentanaWorkouts(usuarioActual);
			}
		});
		btnSalir.setBounds(427, 40, 65, 45);
		panelWorkout.add(btnSalir);
		btnSalir.setBackground(Color.RED);
		
		lblCronoWorkout = new JLabel("Crono Workout");
		lblCronoWorkout.setForeground(Color.WHITE);
		lblCronoWorkout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCronoWorkout.setBounds(331, 11, 176, 29);
		panelWorkout.add(lblCronoWorkout);
		
		JLabel lblFondoWorkout = new JLabel("");
		lblFondoWorkout.setBounds(0, 0, 508, 108);
		panelWorkout.add(lblFondoWorkout);

		ImageIcon iconoElorrieta = new ImageIcon(getClass().getResource("/img/FondoWorkout.jpg"));

		Image imagenElorrieta = iconoElorrieta.getImage().getScaledInstance(
		    lblFondoWorkout.getWidth(),
		    lblFondoWorkout.getHeight(),
		    Image.SCALE_SMOOTH
		);

		lblFondoWorkout.setIcon(new ImageIcon(imagenElorrieta));
		
		rellenarCampos(workout, nombreEjercicio);
		
	}



	private void volverVentanaWorkouts(Usuario usuarioActual) {
		VentanaWorkouts ventanaWorkouts = new VentanaWorkouts(usuarioActual);
		ventanaWorkouts.setVisible(true);
		this.dispose();
	}



	private void rellenarCampos(Workout workout, String nombreEjercicio) {
		panelSerie1.setVisible(false);
		panelSerie2.setVisible(false);
		panelSerie3.setVisible(false);

		
		lblNombreWorkout.setText(workout.getNombre());
		lblDescripcionWorkout.setText("<html>" + workout.getDescripcion() + "</html>");
		
		
		Ejercicio ejercicio = obtenerEjercicio(workout.getEjercicios(), nombreEjercicio);
		lblNombreEjercicio.setText(ejercicio.getNombre());
		lblDescripcionEjercicio.setText("<html>" + ejercicio.getDescripcion()+ "</html>" );
		
		int numSerie = 1;
		for(Serie serie: ejercicio.getSeries()) {
			switch(numSerie) {
			case 1:
				lblFotoSerie1.setText(serie.getFoto());
				lblCronoSerie1.setText(String.valueOf(serie.getDuracion()));
				panelSerie1.setVisible(true);
				break;
			case 2: 
				lblFotoSerie2.setText(serie.getFoto());
				lblCronoSerie2.setText(String.valueOf(serie.getDuracion()));
				panelSerie2.setVisible(true);
				break;
			case 3:
				lblFotoSerie3.setText(serie.getFoto());
				lblCronoSerie3.setText(String.valueOf(serie.getDuracion()));
				panelSerie3.setVisible(true);
				break;
			}
			numSerie++;
		}
		
	}



	private Ejercicio obtenerEjercicio(ArrayList<Ejercicio> ejercicios, String nombreEjercicio) {
		Ejercicio ejercicioDevolver = new Ejercicio();
		for (Ejercicio ejercicio: ejercicios) {
			if (ejercicio.getNombre().equals(nombreEjercicio)) {
				ejercicioDevolver = ejercicio;
			}
		}
		return ejercicioDevolver;
	}
}
