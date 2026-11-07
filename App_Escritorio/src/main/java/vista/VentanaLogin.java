package vista;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.xml.sax.SAXException;

import com.google.cloud.firestore.Firestore;

import controlador.Controlador;
import modelo.Historico;
import modelo.Usuario;
import modelo.Workout;

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

	private ArrayList<Workout> workouts = new ArrayList<Workout>();

	private boolean conexion;

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

		try {
			db = controlador.conectar();
			if (db != null) {
				conexion = true;
			} else {
				conexion = false;
			}
		} catch (IOException e1) {

		}
	}

	public static String encriptarContrasena(String passwordPlana) {
		return DigestUtils.sha256Hex(passwordPlana); 
	}

	private void login() {
		try {
			if (!txtUsuario.getText().trim().isEmpty()) {
				if (!(txtContrasena.getPassword().length == 0)) {
					String nomUsuario = txtUsuario.getText();
					char[] contrasena = txtContrasena.getPassword();
					String contrasenaEncriptada = encriptarContrasena(String.valueOf(contrasena));

					Usuario usuarioIngresado = new Usuario();
					usuarioIngresado.setNomUsuario(nomUsuario);
					usuarioIngresado.setContrasena(contrasenaEncriptada);

					ArrayList<Usuario> usuarios;

					// Obtenemos los usuarios de una forma o de otra segun el estado de la conex
					if (conexion) {
						usuarios = controlador.devolverUsuariosFirebase(db);
						controlador.crearBackups();
					} else {
						usuarios = controlador.devolverUsuariosOffline();
						if (usuarios.isEmpty()) {
							JOptionPane.showMessageDialog(this, "No tienes el fichero de Backups creado", "Error de backup", JOptionPane.ERROR_MESSAGE);
						}
					}

					boolean usuarioCorrecto = false;
					boolean contrasenaCorrecta = false;

					// Miramos si el login es correcto recorriendo el arraylist de usuarios
					for (Usuario usuarioArray : usuarios) {
						if (usuarioArray.getNomUsuario().equals(usuarioIngresado.getNomUsuario())) {
							usuarioCorrecto = true;
							if (usuarioArray.getContrasena().equals(usuarioIngresado.getContrasena())) {
								// Guardamos el usuario autenticado completo
								contrasenaCorrecta = true;
								usuarioActual = usuarioArray;
							}
						}
					}
					if (usuarioCorrecto && contrasenaCorrecta) {
						JOptionPane.showMessageDialog(this, "Login Correcto.");
						if (conexion) {
							workouts = controlador.rellenarWorkoutsFirebase(db);
							ArrayList<Historico> workoutsHistoricoUsuario = controlador.rellenarHistoricoFirebase(db, usuarioActual);
							usuarioActual.setHistorico(workoutsHistoricoUsuario);
							db.close();
						} else {
							workouts = controlador.rellenarWorkoutsOffline();
							ArrayList<Historico> workoutsHistoricoUsuario = controlador.rellenarHistoricoXML(usuarioActual);
							usuarioActual.setHistorico(workoutsHistoricoUsuario);
						}
						enseñarWorkouts();
					} else if (!usuarioCorrecto) {
						JOptionPane.showMessageDialog(this, "Usuario incorrecto. Inicio de sesión invalido.", "Error de validación", JOptionPane.ERROR_MESSAGE);
					} else if (!contrasenaCorrecta) {
						JOptionPane.showMessageDialog(this, "Contraseña incorrecta. Inicio de sesión invalido.", "Error de validación", JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo de contraseña.", "Error de validación", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Tienes que rellenar el campo de nombre de usuario.", "Error de validación", JOptionPane.ERROR_MESSAGE);
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (ExecutionException ee) {
			ee.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch(XPathExpressionException xee) {
			xee.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(SAXException saxe) {
			saxe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enseñarWorkouts() {
		VentanaWorkouts ventanaWorkouts = new VentanaWorkouts(usuarioActual, workouts, conexion);
		ventanaWorkouts.setVisible(true);
		this.dispose();
	}

	private void enseñarRegistro() {
		if (db == null) {
			JOptionPane.showMessageDialog(this, "No se puede acceder al registro de usuarios ya que no hay conexion con la base de datos.", "Error de conexion", JOptionPane.ERROR_MESSAGE);
		}else {
			VentanaRegistro ventanaRegistro = new VentanaRegistro();
			ventanaRegistro.setVisible(true);
			this.dispose();
		}

	}
}
