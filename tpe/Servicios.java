package tpe;

import java.util.ArrayList;
import java.util.List;

import tpe.clases.Tarea;
import tpe.utils.CSVReader;

/**
 * NO modificar la interfaz de esta clase ni sus métodos públicos. Sólo se podrá
 * adaptar el nombre de la clase "Tarea" según sus decisiones de implementación.
 */
public class Servicios {

	private CSVReader reader;

	/*
	 * Complejidad: O(p + t) porque p es el número de procesadores y t es el número
	 * de tareas
	 */
	public Servicios(String pathProcesadores, String pathTareas) {
		reader = new CSVReader();
		reader.readProcessors(pathProcesadores);
		reader.readTasks(pathTareas);
	}

	/*
	 * Complejidad: O(1)
	 */
	public Tarea servicio1(String ID) {
		return reader.getTareas().get(ID);
	}

	/*
	 * Complejidad: O(t) porque t es el número de tareas
	 */
	public List<Tarea> servicio2(boolean esCritica) {
		List<Tarea> result = new ArrayList<>();
		for (Tarea tarea : reader.getTareas().values()) {
			if (tarea.esCritica() == esCritica) {
				result.add(tarea);
			}
		}
		return result;
	}

	/*
	 * Complejidad: O(t) porque t es el número de tareas
	 */
	public List<Tarea> servicio3(int prioridadInferior, int prioridadSuperior) {
		List<Tarea> result = new ArrayList<>();
		for (Tarea tarea : reader.getTareas().values()) {
			int prioridad = tarea.getNivelPrioridad();
			if (prioridad >= prioridadInferior && prioridad <= prioridadSuperior) {
				result.add(tarea);
			}
		}
		return result;
	}

}