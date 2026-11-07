package vista;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

import javax.swing.JPasswordField;
import javax.swing.ImageIcon;

public class VentanaLogin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtUsuario;

	private Controlador controlador = new Controlador();
	private JPasswordField txtContrasena;

	private Firestore db;

	private Usuario usuarioActual;

	public VentanaLogin() {



		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 489, 577);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("Iniciar sesión");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.setBounds(139, 318, 175, 40);
		contentPane.add(btnNewButton);

		txtUsuario = new JTextField();
		txtUsuario.setBounds(192, 197, 175, 23);
		contentPane.add(txtUsuario);
		txtUsuario.setColumns(10);

		JLabel lblUsuario = new JLabel("Usuario: ");
		lblUsuario.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblUsuario.setBounds(73, 197, 109, 23);
		contentPane.add(lblUsuario);

		JLabel lblContrasena = new JLabel("Contraseña: ");
		lblContrasena.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblContrasena.setBounds(73, 261, 109, 23);
		contentPane.add(lblContrasena);

		JLabel lblRegistro = new JLabel("No tienes usuario? Registrate.");
		lblRegistro.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblRegistro.setBounds(128, 385, 211, 23);
		contentPane.add(lblRegistro);

		JButton btnRegistro = new JButton("Registrarse");
		btnRegistro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enseñarRegistro();
			}
		});
		btnRegistro.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnRegistro.setBounds(139, 426, 175, 40);
		contentPane.add(btnRegistro);

		txtContrasena = new JPasswordField();
		txtContrasena.setBounds(192, 264, 175, 20);
		contentPane.add(txtContrasena);

		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(134, 27, 180, 140);
		contentPane.add(lblLogo);
		ImageIcon icono = new ImageIcon(getClass().getResource("/img/Logo.jpg"));

		Image imagen = icono.getImage().getScaledInstance(
				lblLogo.getWidth(),
				lblLogo.getHeight(),
		    Image.SCALE_SMOOTH
		);

		lblLogo.setIcon(new ImageIcon(imagen));

	}

	public static String encriptarContrasena(String passwordPlana) {
		return DigestUtils.sha256Hex(passwordPlana); 
	}

	protected void login() {
		try {
			db = controlador.conectar();
			
			if (!txtUsuario.getText().trim().isEmpty()) {
				if (!(txtContrasena.getPassword().length == 0)) {
					String nomUsuario = txtUsuario.getText();
					char[] contrasena = txtContrasena.getPassword();
					String contrasenaEncriptada = (encriptarContrasena( String.valueOf(contrasena)));
					Usuario usuario = new Usuario();
					usuario.setNomUsuario(nomUsuario);
					usuario.setContrasena(contrasenaEncriptada);
					usuario = controlador.loginUsuario(db, usuario);

					if (usuario != null) {
						usuarioActual = usuario;
						JOptionPane.showMessageDialog(this, "Login Correcto.");
						enseñarWorkouts();
					} else {
						JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrecta. Inicio de sesion invalido.", "Error de validación", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo de contraseña.", "Error de validación", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo de nombre de usuario.", "Error de validación", JOptionPane.ERROR_MESSAGE);
			}

			db.close();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (ExecutionException ee) {
			ee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

	

	private void enseñarWorkouts() {
		VentanaWorkouts ventanaWorkouts = new VentanaWorkouts(usuarioActual);
		ventanaWorkouts.setVisible(true);
		this.dispose();
	}

	private void enseñarRegistro() {
		VentanaRegistro ventanaRegistro = new VentanaRegistro();
		ventanaRegistro.setVisible(true);
		this.dispose();
	}
}
