
package modelo;

import javax.swing.JLabel;

import vista.VentanaEjercicio;

public class CronoSerie extends Thread {

    private int duracion;
    private JLabel lblCronometro;
    private VentanaEjercicio ventana;
    private boolean pausado = false;

    public CronoSerie(int duracion, JLabel lblCronometro, VentanaEjercicio ventana) {
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

                int min = j / 60;
                int seg = j % 60;

                lblCronometro.setText(String.format("%02d:%02d", min, seg));

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
                ventana.notificarFinSerie();
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
