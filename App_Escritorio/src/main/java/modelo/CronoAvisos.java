
package modelo;

import javax.swing.JLabel;

import vista.VentanaEjercicio;

public class CronoAvisos extends Thread {

	private String tipo;
	private int duracion;
	private JLabel lblCronometro;
	private VentanaEjercicio ventana;
	private boolean pausado = false;

	public CronoAvisos(String tipo, int duracion, JLabel lblCronometro, VentanaEjercicio ventana) {
		this.tipo = tipo;
		this.duracion = duracion;
		this.lblCronometro = lblCronometro;
		this.ventana = ventana;
	}

	@Override
	public void run() {
		try {
			int j = duracion;
			boolean terminado = false;

			while (!terminado && !Thread.currentThread().isInterrupted()) {

				while (estaPausado()) {
					Thread.sleep(100);
				}

				if (tipo.equals("INICIO")) {
					lblCronometro.setText("Empieza en... " + j);
				} else {
					lblCronometro.setText("Descanso: " + j + "s");
				}

				if (j == 0) {
					terminado = true;
				} else {
					Thread.sleep(1000);

					if (!estaPausado()) {
						j--;
					}
				}
			}

			if (terminado && !Thread.currentThread().isInterrupted()) {
				ventana.notificarFinAviso(tipo);
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public synchronized void setPausado(boolean pausado) {
		this.pausado = pausado;
	}

	public synchronized boolean estaPausado() {
		return pausado;
	}
}
