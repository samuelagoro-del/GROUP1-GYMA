package modelo;

import java.util.ArrayList;

public class Historicos {
	
	private String nomUsuario;
	private ArrayList<Historico> workoutsHistorico = new ArrayList<Historico>();
	
	public Historicos() {
		
	}

	public Historicos(String nomUsuario, ArrayList<Historico> workoutsHistorico) {
		this.nomUsuario = nomUsuario;
		this.workoutsHistorico = workoutsHistorico;
	}

	public String getNomUsuario() {
		return nomUsuario;
	}

	public void setNomUsuario(String nomUsuario) {
		this.nomUsuario = nomUsuario;
	}

	public ArrayList<Historico> getWorkoutsHistorico() {
		return workoutsHistorico;
	}

	public void setWorkoutsHistorico(ArrayList<Historico> workoutsHistorico) {
		this.workoutsHistorico = workoutsHistorico;
	}
	
	
}
