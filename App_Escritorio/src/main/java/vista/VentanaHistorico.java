package vista;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import modelo.Historico;
import modelo.Usuario;
import modelo.Workout;

import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class VentanaHistorico extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private Usuario usuarioActual;
	private ArrayList<Workout> workouts;
	private boolean conexion;
	private JTable table;
	private DefaultTableModel modelo;



	public VentanaHistorico(Usuario usuarioParamtero, ArrayList<Workout> workoutsParametro, boolean conexionParametro) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 698, 460); 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 91, 662, 303);
		contentPane.add(scrollPane);

		modelo = new DefaultTableModel() {
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		modelo.addColumn("Workout");
		modelo.addColumn("Nivel");
		modelo.addColumn("Tiempo total");
		modelo.addColumn("Tiempo esperado");
		modelo.addColumn("Fecha");
		modelo.addColumn("Porcentaje Ejercicios");
		
		table = new JTable(modelo);
		scrollPane.setViewportView(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		scrollPane.add(table);
		scrollPane.setViewportView(table);

		JLabel lblNewLabel = new JLabel("Historial de Workouts");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblNewLabel.setBounds(213, 27, 253, 29);
		contentPane.add(lblNewLabel);

		JButton btnNewButton = new JButton("Volver");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				volver();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.setBounds(29, 27, 101, 33);
		contentPane.add(btnNewButton);

		usuarioActual = usuarioParamtero;
		workouts = workoutsParametro;
		conexion = conexionParametro;

		rellenarTablaHistorico();
	}

	private void rellenarTablaHistorico() {
		if (!usuarioActual.getHistorico().isEmpty()) {
			for (Historico historico: usuarioActual.getHistorico()) {
				String nivelConvertido = convertirNivel(historico.getNivel());
				String tiempoPrevistoConvertido = historico.formatearTiempo(historico.getTiempoPrevisto());
				String tiempoTotalConvertido = historico.formatearTiempo(historico.getTiempoTotal());
				String fechaString = String.valueOf(historico.getFecha());
				String porcentajeEjercicios = String.valueOf(historico.getPorcentajeEjercicios() + "%");
				String[] historicoArray = {historico.getNombreWorkout(), nivelConvertido, tiempoTotalConvertido, tiempoPrevistoConvertido, fechaString, porcentajeEjercicios};
				modelo.addRow(historicoArray);
			}
		}else {
			JOptionPane.showMessageDialog(this, "No has empezado ningun ejercicio por lo que tu historico esta vacio, volviendo a la ventana de Workouts", "Error de historico", JOptionPane.ERROR_MESSAGE);
			volver();
		}

	}

	private String convertirNivel(int nivel) {
		String nivelConvertido = "";
		switch(nivel) {
		case 0:
			nivelConvertido = "Principiante";
			break;
		case 1:
			nivelConvertido = "Intermedio";
			break;
		case 2:
			nivelConvertido = "Avanzado";
			break;
		}
		return nivelConvertido;
	}

	protected void volver() {

		VentanaWorkouts venanaWorkouts = new VentanaWorkouts(usuarioActual, workouts, conexion);
		venanaWorkouts.setVisible(true);
		this.dispose();
	}
}
