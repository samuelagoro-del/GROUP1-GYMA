package vista;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.firestore.Firestore;

import controlador.Controlador;
import modelo.Usuario;
import com.toedter.calendar.JDateChooser;
import javax.swing.JPasswordField;

public class VentanaPerfil extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNombre;
	private JTextField txtApellido;
	private JTextField txtEmail;
	private JTextField txtNivel;
	private JTextField txtUsuario;
	private JLabel lblUsuario;
	private JLabel lblPerfil;
	private JButton btnActualizar;
	private JButton btnVolver;
	private Controlador controlador = new Controlador();
	private Firestore db;
	private JDateChooser dcFechaNacimiento;
	private JPasswordField txtContrasena;

	private Usuario usuarioActual;

	public VentanaPerfil(Usuario usuario) {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNombre = new JLabel("Nombre: ");
		lblNombre.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNombre.setBounds(66, 120, 183, 23);
		contentPane.add(lblNombre);

		txtNombre = new JTextField();
		txtNombre.setColumns(10);
		txtNombre.setBounds(259, 120, 201, 23);
		contentPane.add(txtNombre);

		JLabel lblApellido = new JLabel("Apellido: ");
		lblApellido.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblApellido.setBounds(66, 154, 183, 23);
		contentPane.add(lblApellido);

		txtApellido = new JTextField();
		txtApellido.setColumns(10);
		txtApellido.setBounds(259, 154, 201, 23);
		contentPane.add(txtApellido);

		JLabel lblEmail = new JLabel("Email: ");
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblEmail.setBounds(66, 222, 183, 23);
		contentPane.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setColumns(10);
		txtEmail.setBounds(259, 222, 201, 23);
		contentPane.add(txtEmail);

		JLabel lblContrasena = new JLabel("Contraseña: ");
		lblContrasena.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblContrasena.setBounds(66, 256, 183, 23);
		contentPane.add(lblContrasena);

		JLabel lblNivel = new JLabel("Nivel: ");
		lblNivel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNivel.setBounds(66, 324, 183, 23);
		contentPane.add(lblNivel);

		txtNivel = new JTextField();
		txtNivel.setText("0");
		txtNivel.setEditable(false);
		txtNivel.setColumns(10);
		txtNivel.setBounds(259, 324, 201, 23);
		contentPane.add(txtNivel);

		txtUsuario = new JTextField();
		txtUsuario.setColumns(10);
		txtUsuario.setBounds(259, 188, 201, 23);
		contentPane.add(txtUsuario);

		lblUsuario = new JLabel("Nombre de usuario: ");
		lblUsuario.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblUsuario.setBounds(66, 188, 183, 23);
		contentPane.add(lblUsuario);

		lblPerfil = new JLabel("PERFIL");
		lblPerfil.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblPerfil.setBounds(246, 62, 75, 23);
		contentPane.add(lblPerfil);

		btnActualizar = new JButton("Actualizar");
		btnActualizar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actualizarUsuario();
			}
		});
		btnActualizar.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnActualizar.setBounds(222, 364, 146, 40);
		contentPane.add(btnActualizar);

		btnVolver = new JButton("Volver");
		btnVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				volver();
			}
		});
		btnVolver.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnVolver.setBounds(428, 11, 146, 40);
		contentPane.add(btnVolver);

		JLabel lblFechaDeNacimiento = new JLabel("Fecha de Nacimiento: ");
		lblFechaDeNacimiento.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblFechaDeNacimiento.setBounds(66, 290, 183, 23);
		contentPane.add(lblFechaDeNacimiento);

		dcFechaNacimiento = new JDateChooser();
		dcFechaNacimiento.setDateFormatString("yyyy-MM-dd");
		dcFechaNacimiento.setBounds(259, 290, 201, 23);
		contentPane.add(dcFechaNacimiento);

		txtContrasena = new JPasswordField();
		txtContrasena.setBounds(259, 256, 201, 23);
		contentPane.add(txtContrasena);

		usuarioActual = usuario;

		txtNombre.setText(usuarioActual.getNombre());
		txtApellido.setText(usuarioActual.getApellido());
		txtUsuario.setText(usuarioActual.getNomUsuario());
		txtEmail.setText(usuarioActual.getEmail());
		dcFechaNacimiento.setDate(Date.from(usuarioActual.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		txtNivel.setText(String.valueOf(usuarioActual.getNivel()));
	}



	private void volver() {
		VentanaWorkouts ventanaWorkouts = new VentanaWorkouts(usuarioActual);
		ventanaWorkouts.setVisible(true);
		this.dispose();
	}

	private void actualizarUsuario() {
		try {
			boolean actualizado = false;
			db = controlador.conectar();
			if (!comprobarDatos()) {
				String nombre = txtNombre.getText();
				String apellido = txtApellido.getText();
				String nuevoNomUsuario = txtUsuario.getText();
				char[] contrasena = txtContrasena.getPassword();
				String contrasenaEncriptada = encriptarContrasena(String.valueOf(contrasena));
				String email = txtEmail.getText();
				LocalDate fechaNacimiento = dcFechaNacimiento.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				int nivel = Integer.parseInt(txtNivel.getText());
				usuarioActual.setNombre(nombre);
				usuarioActual.setApellido(apellido);
				usuarioActual.setContrasena(contrasenaEncriptada);
				usuarioActual.setEmail(email);
				usuarioActual.setFechaNacimiento(fechaNacimiento);
				usuarioActual.setNivel(nivel);

				actualizado = controlador.actualizarUsuario(db, usuarioActual, nuevoNomUsuario);
				if (actualizado) {
					JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente");
				} else {
					JOptionPane.showMessageDialog(this, "No se ha podido actualizar el usuario");
				}
			}
			db.close();
		}catch (InterruptedException ie) {
			// TODO Auto-generated catch block
			ie.printStackTrace();
		} catch (ExecutionException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

	}

	private boolean comprobarNombre() {
		boolean nombreCorrecto = false;

		if (!txtNombre.getText().trim().isEmpty()) {
			nombreCorrecto = true;
		} else {
			JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo del nombre", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}
		return nombreCorrecto;
	}

	private boolean comprobarApellido() {
		boolean apellidoCorrecto = false;

		if (!txtApellido.getText().trim().isEmpty()) {
			apellidoCorrecto = true;
		}else {
			JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo del apellido", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}
		return apellidoCorrecto;
	}

	private boolean comprobarNomUsuario() {
		boolean usuarioCorrecto = false;

		if (!txtUsuario.getText().trim().isEmpty()) {
			usuarioCorrecto = true;
		}else {
			JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo del nombre de usuario", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}
		return usuarioCorrecto;
	}

	private boolean comprobarContrasena() {
		boolean contrasenaCorrecta = false;
		char[] contrasena = txtContrasena.getPassword();

		if (!(contrasena.length == 0)) {
			if (!(contrasena.length < 4)) {
				contrasenaCorrecta = true;
			} else {
				JOptionPane.showMessageDialog(this, "La contraseña debe tener minimo 4 caracteres", "Error de validación", JOptionPane.ERROR_MESSAGE);
			}
		}else {
			JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo de la contraseña", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}
		return contrasenaCorrecta;
	}

	private boolean comprobarEmail() {
		boolean emailCorrecto = false;

		if (!txtEmail.getText().trim().isEmpty()) {
			if (txtEmail.getText().trim().contains("@") && txtEmail.getText().trim().contains(".")) {
				emailCorrecto = true;
			}else {
				JOptionPane.showMessageDialog(this, "El correo electrónico debe contener un '@' y un '.'", "Error de validación", JOptionPane.ERROR_MESSAGE);
			}
		}else {
			JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo del email", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}
		return emailCorrecto;
	}

	private boolean comprobarFecha() {
		boolean fechaCorrecta = false;

		if (dcFechaNacimiento != null && dcFechaNacimiento.getDate() != null) {

			LocalDate fechaNacimiento = dcFechaNacimiento.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate fechaLimite = LocalDate.now().minusYears(16);

			if (!fechaNacimiento.isAfter(fechaLimite)) {
				fechaCorrecta = true;
			} else {
				JOptionPane.showMessageDialog(this, "Debes ser mayor de 16 años para registrarte.", "Error de edad", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Por favor, selecciona una fecha de nacimiento válida.", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}

		return fechaCorrecta;
	}


	private boolean comprobarNivel() {
		boolean nivelCorrecto = false;
		int nivel = Integer.parseInt(txtNivel.getText());
		if (nivel == 0 || nivel == 1 || nivel == 2 || nivel == 3) {
			nivelCorrecto = true;
		} else {
			JOptionPane.showMessageDialog(this, "El nivel tiene que ser 0, 1, 2 o 3", "Error de validación", JOptionPane.ERROR_MESSAGE);
		}
		return nivelCorrecto;
	}

	private boolean comprobarDatos() {
		boolean error = false;

		if(!comprobarNombre()){
			error = true;
		} else if (!comprobarApellido()){
			error = true;
		} else if (!comprobarNomUsuario()) {
			error = true;
		} else if (!comprobarEmail()) {
			error = true;
		} else if (!comprobarContrasena()) {
			error = true;
		} else if (!comprobarFecha()) {
			error = true;
		}else if (!comprobarNivel()) {
			error = true;
		}

		return error;
	}

	public static String encriptarContrasena(String passwordPlana) {
		return DigestUtils.sha256Hex(passwordPlana); 
	}
}
