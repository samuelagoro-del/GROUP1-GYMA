package main;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import com.google.cloud.firestore.Firestore;

import controlador.Controlador;

public class Main {

	private static Controlador controlador = new Controlador();
	private static Firestore db;

	public static void main(String[] args) {


		try {
			db = controlador.conectar();
			boolean backups_creados = controlador.crearBackups(db);
			if (backups_creados) {
				System.out.println("Backups creados correctamente.");
			}
			db.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(TransformerConfigurationException tce) {
			tce.printStackTrace();
		} catch(TransformerException te) {
			te.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
