package tpe.clases;

import java.util.ArrayList;
import java.util.List;

public class Procesador {
	private String id;
	private String codigo;
	private boolean estaRefrigerado;
	private int anioFuncionamiento;
	private List<Tarea> tareasAsignadas;
	private float tiempoTotalTareas;
	private int cantidadCriticas;

	public Procesador(String id, String codigo, boolean estaRefrigerado, int anioFuncionamiento) {
		this.id = id;
		this.codigo = codigo;
		this.estaRefrigerado = estaRefrigerado;
		this.anioFuncionamiento = anioFuncionamiento;
		this.tareasAsignadas = new ArrayList<>();
		this.tiempoTotalTareas = 0;
		this.cantidadCriticas = 0;
	}

	// Getters y setters

	public String getId() {
		return id;
	}

	public String getCodigo() {
		return codigo;
	}

	public boolean estaRefrigerado() {
		return estaRefrigerado;
	}

	public int getAnioFuncionamiento() {
		return anioFuncionamiento;
	}

	public List<Tarea> getTareasAsignadas() {
		return tareasAsignadas;
	}

	public void agregarTarea(Tarea tarea) {
		if (tarea.esCritica()) {
			cantidadCriticas++;
		}
		tareasAsignadas.add(tarea);
		tiempoTotalTareas += tarea.getTiempoEjecucion();

	}

	public void removerTarea(Tarea tarea) {
		if (tarea.esCritica()) {
			cantidadCriticas--;
		}
		tareasAsignadas.remove(tarea);
		tiempoTotalTareas -= tarea.getTiempoEjecucion();
	}

	public float getTiempoTareas() {
		return tiempoTotalTareas;
	}

	public int getCantCriticas() {
		return cantidadCriticas;
	}

}
