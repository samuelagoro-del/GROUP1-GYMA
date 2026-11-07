package modelo;

public class Serie {

	private String nombre;
	private String foto;
	private int repeticiones;
	private int duracion;
	
	public Serie(String nombre, String foto, int repeticiones, int duracion) {
		this.nombre = nombre;
		this.foto = foto;
		this.repeticiones = repeticiones;
		this.duracion = duracion;
	}
	
	public Serie() {
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public int getRepeticiones() {
		return repeticiones;
	}

	public void setRepeticiones(int repeticiones) {
		this.repeticiones = repeticiones;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	@Override
	public String toString() {
		return "Serie [nombre=" + nombre + ", foto=" + foto + ", repeticiones=" + repeticiones + ", duracion="
				+ duracion + "]";
	}
	
	
}
