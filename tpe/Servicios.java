package tpe;

import java.util.ArrayList;

import java.util.List;

import tpe.clases.ArbolTarea;
import tpe.clases.Node;
import tpe.clases.Procesador;
import tpe.clases.Solucion;
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
	public Servicios(String pathProcesadores, String pathTareas, int time) {
		this.reader = new CSVReader();
		this.reader.readProcessors(pathProcesadores);
		this.reader.readTasks(pathTareas);

	}

	// Complejidad: O(1) gracias al mapeo

	public Tarea servicio1(String ID) {
		Tarea tareaPorId = this.reader.getTareas().get(ID);
		return tareaPorId;
	}

	// Complejidad: O(t) porque t es el número de tareas

	public List<Tarea> servicio2(boolean esCritica) {
		List<Tarea> result = new ArrayList<>();
		for (Tarea tarea : this.reader.getTareas().values()) {
			if (tarea.esCritica() == esCritica) {
				result.add(tarea);
			}
		}
		return result;
	}

	// O(n) en el peor de los casos si el arbol esta muy desbalanceado,
	// O(log n + t) en el mejor de los casos si el árbol está balanceado, donde n es
	// el primer
	// nodo que cumpla con el rango, y t son todas las tareas procesadas en ese
	// rango

	public List<Tarea> servicio3(int prioridadInferior, int prioridadSuperior) {
		ArbolTarea arbolTareas = this.reader.getArbolTarea();
		return arbolTareas.tareasPorRango(prioridadInferior, prioridadSuperior);
	}

	/*
	 * Inicialmente intente realizar el recorrido con 2 hashmap de procesadores y
	 * tareas, sin embargo fue un error, si bien era eficiente para llamadas por
	 * claves, no era eficiente para recorrerlos al no tener referencia a un
	 * siguiente para el uso de recursividad. luego opte por arraylist que daban una
	 * funcionalidad, pero luego opte por reutilizar el mismo arbolbinario
	 * ArbolTarea usado para la obtencion de uno de los servicios de obtencion de
	 * tareas por rango de prioridad. esto me dio una estructura firme y una
	 * complejidad promedio o(p*t) donde p es la cantidad de procesadores y t la cantidad de tareas.
	 * Si bien es similar a la complejidad de 2 arraylist, el arbol de tareas me da la posibilidad
	 * de poda y cortar el recorrido innecesario, mejorando un poco el costo computacional.
	 * tambien se ahorra un poco en memoria contigua.
	 */
	// Agregar Tareas con backtracking
	public Solucion backTracking(Float tiempoLimite) {
		System.out.println("********* BACKTRACKING : ************");
		ArbolTarea arbolTareas = this.reader.getArbolTarea();
		List<Procesador> procesadores = new ArrayList<>(this.reader.getProcesadores().values());
		List<Tarea> mejorSolucion = new ArrayList<>();
		float[] mejorTiempo = { Float.MAX_VALUE };

		for (Procesador procesador : procesadores) {
			back(procesador, arbolTareas.getRoot(), arbolTareas.getSize(), tiempoLimite, new ArrayList<>(),
					mejorSolucion, mejorTiempo);
		}

		// Devolver la mejor solución encontrada como objeto Solucion
		return new Solucion(mejorSolucion, mejorTiempo[0]);
	}

	// Método de backtracking
	private int back(Procesador procesador, Node nodo, int tareasSize, Float tiempoLimite, List<Tarea> asignacionActual,
			List<Tarea> mejorSolucion, float[] mejorTiempo) {
		int estadosGenerados = 1; // Contador de estados generados

		Tarea tarea = nodo.getTarea();
		Node nodoIzquierdo = nodo.getLeft();
		Node nodoDerecho = nodo.getRight();

		if (cumpleRestricciones(procesador, tarea, tiempoLimite)) {// cumple restricciones

			asignacionActual.add(tarea); // Agregar la tarea a la asignación actual
			procesador.agregarTarea(tarea);// el procesador tambien agrega para contar el tiempo de ejecucion acumulado

			if (nodoDerecho == null && nodoIzquierdo == null) {// ES UNA HOJA?

				if (asignacionActual.size() == tareasSize) { // SI TODAS LAS TAREAS FUERON ASIGNADAS
					float tiempoAcumulado = procesador.getTiempoTareas(); // OBTENGO EL TIEMPO ACUMULADO DEL PROCESADOR

					// ESTA ES UNA ESTRATEGIA QUE CORTA LA BUSQUEDA SI EL TIEMPO ACUMULADO SUPERA AL
					// MEJOR TIEMPO ENCONTRADO
					if (tiempoAcumulado <= mejorTiempo[0]) {
						mejorTiempo[0] = tiempoAcumulado;
						mejorSolucion.clear();
						mejorSolucion.addAll(asignacionActual);

						// IMPRIME SOLUCION DEL PRIMER PROCESADOR EN ENCONTRARLA
						System.out.println("Solución actual en procesador :" + procesador.getId());
						for (Tarea t : asignacionActual) {
							System.out.println(t);
						}
						System.out.println("Tiempo acumulado: " + tiempoAcumulado);
						System.out.println("-----");
						// CORTA LA BUSQUEDA Y RETORNA
						return estadosGenerados;
					}
					// Imprimir cada solución actual para control
					System.out.println("Solución actual en procesador :" + procesador.getId());
					for (Tarea t : asignacionActual) {
						System.out.println(t);
					}
					System.out.println("Tiempo acumulado: " + tiempoAcumulado);
					System.out.println("-----");
				}
			}

			// CONTINUA EL RECORRIDO
			if (nodoIzquierdo != null) {
				estadosGenerados += back(procesador, nodoIzquierdo, tareasSize, tiempoLimite, asignacionActual,
						mejorSolucion, mejorTiempo);
			}
			if (nodoDerecho != null) {
				estadosGenerados += back(procesador, nodoDerecho, tareasSize, tiempoLimite, asignacionActual,
						mejorSolucion, mejorTiempo);
			}
		}

		//SI TERMINO LA BUSQUEDA, RETORNO
		return estadosGenerados;
	}

	// RESTRICCIONES AL AGREGAR TAREAS
	private boolean cumpleRestricciones(Procesador procesador, Tarea tarea, Float tiempoLimite) {

		if (tarea.esCritica() && procesador.getCantCriticas() >= 2) { //HAY MAS DE 2 TAREAS CRITICAS?
			System.out.println("el procesador " + procesador.getId() + " no tolera mas de 2 tareas criticas ");
			System.out.println(" -------------------------- ");
			return false;
		}

		float tiempoTotalConNuevaTarea = procesador.getTiempoTareas() + tarea.getTiempoEjecucion(); //VARIABLE TIEMPO DE EJECUCION

		if (!procesador.estaRefrigerado() && tiempoTotalConNuevaTarea > tiempoLimite) { //VERIFICA SI NO ESTA REFRIGERADO Y QUE NO EXCEDA TIEMPO
			System.out.println("Procesador " + procesador.getId()
					+ " sin refrigeracion no puede exceder el limite para asignar tarea: " + tarea.getId());
			System.out.println(" -------------------------- ");
			return false;
		}

		return true;
	}

}