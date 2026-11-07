
package modelo;

import javax.swing.JLabel;

public class CronoEjercicio extends Thread {

    private JLabel lblCronometro;
    private boolean pausado = false;
    private int segundos = 0;

    public CronoEjercicio(JLabel lblCronometro) {
        this.lblCronometro = lblCronometro;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                while (estaPausado()) {
                    Thread.sleep(100);
                }

                int min = segundos / 60;
                int seg = segundos % 60;

                lblCronometro.setText(String.format("%02d:%02d", min, seg));

                Thread.sleep(1000);

                if (!estaPausado()) {
                    segundos++;
                }
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
