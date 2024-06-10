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
	 * complejidad promedio o(p*t) donde p es la cantidad de procesadores y t la
	 * cantidad de tareas. Si bien es similar a la complejidad de 2 arraylist, el
	 * arbol de tareas me da la posibilidad de poda y cortar el recorrido
	 * innecesario, mejorando un poco el costo computacional. tambien se ahorra un
	 * poco en memoria contigua. Otra estrategia que utilice, es la posibilidad de
	 * ir almacenando atributos auxiliares en las clases de Procesador que lleva la
	 * cuenta de tiempos de ejecucion al tener problemas con la recursividad, y el
	 * atributo size para tener un control del tamanio del arbol de tareas utiles
	 * para la condicion de agregar una solucion que contenga todas las tareas
	 * asignadas por completo.
	 * 
	 * Luego me di cuenta que interprete muy mal la consigna, intentaba que todas
	 * las tareas se intenten asignar a CADA procesador, y en realidad se debian
	 * DISTRIBUIR entre 4 procesadores.
	 * 
	 * SE MODIFICO EL CODIGO, ANTES HABIA UN FOR DE PROCESADORES Y AL ENTRAR A BACK
	 * HABIAN LLAMADAS RECURSIVAS AL ARBOL DE TAREAS, AHORA DENTRO DEL BACK, SE
	 * HACEN POR CADA TAREA DEL ARBOL, RECORRER LOS DISTINTOS PROCESADORES A DONDE
	 * SER ASIGNADAS DICHAS TAREAS. NO ESTA FUNCIONANDO LA RECURSION YA QUE AL
	 * LLEGAR A LA HOJA, SE CORTA LA BUSQUEDA DE LA SOLUCION.
	 * DESPUES DE TANTOS INTENTOS, PUEDO PENSAR QUE NO EXISTE SOLUCION CON BACKTRACKING EN ESTE PROBLEMA
	 * 
	 */
	
	// Agregar Tareas con backtracking
	public Solucion<Tarea> backTracking(Float tiempoLimite) {
		System.out.println("********* BACKTRACKING : ************");
		ArbolTarea arbolTareas = this.reader.getArbolTarea();
		List<Procesador> procesadores = new ArrayList<>(this.reader.getProcesadores().values());
		List<Tarea> mejorSolucion = new ArrayList<>();
		float[] mejorTiempo = { Float.MAX_VALUE };
		int totalEstados = back(arbolTareas.getSize(), arbolTareas.getRoot(), procesadores, tiempoLimite,
				new ArrayList<>(), mejorSolucion, mejorTiempo);

		// Imprimir TITULO DE RESULTADOS Y TOTAL DE ESTADOS GENERADOS
		System.out.println("RESULTADOS FINALES DEL BACKTRACKING: ");
		System.out.println("Total de estados generados: " + totalEstados);
		// Devolver la mejor solución encontrada como objeto Solucion
		return new Solucion<Tarea>(mejorSolucion, mejorTiempo[0]);
	}

	
	private int back(int tareasSize, Node nodo, List<Procesador> procesadores, Float tiempoLimite,
	        List<Tarea> asignacionActual, List<Tarea> mejorSolucion, float[] mejorTiempo) {
	    int estadosGenerados = 1; // Contador de estados generados, comienza en 1 para contar el estado actual

	    if (nodo == null) {
	        return estadosGenerados; // Si el nodo es null, se corta la búsqueda
	    }

	    Tarea tarea = nodo.getTarea();
	    for (Procesador procesador : procesadores) {
	        if (cumpleRestricciones(procesador, tarea, tiempoLimite)) {
	            asignacionActual.add(tarea); // Agregar la tarea a la asignación actual
	            procesador.agregarTarea(tarea); // Agregar la tarea al procesador

	            // Si la asignación actual alcanzó el tamaño total de tareas, se encontró una solución
	            if (asignacionActual.size() == tareasSize) {

	                // Calcular el tiempo total de la asignación actual
	                float tiempoTotal = calcularTiempoTotal(procesadores);
	                // Si es mejor que la mejor solución encontrada hasta ahora, actualizarla
	                if (tiempoTotal < mejorTiempo[0]) {
	                    mejorTiempo[0] = tiempoTotal;
	                    mejorSolucion.clear();
	                    mejorSolucion.addAll(asignacionActual);
	                }
	            } else {
	                // Continuar con la asignación de las tareas restantes
	                estadosGenerados += back(tareasSize, nodo.getLeft(), procesadores, tiempoLimite, asignacionActual,
	                        mejorSolucion, mejorTiempo);
	                estadosGenerados += back(tareasSize, nodo.getRight(), procesadores, tiempoLimite, asignacionActual,
	                        mejorSolucion, mejorTiempo);
	            }

	            // Deshacer la asignación actual para probar con otro procesador
	            asignacionActual.remove(tarea);
	            procesador.removerTarea(tarea);
	        }
	    }

	    return estadosGenerados;
	}


	// Calcular el tiempo total de ejecución de todas las tareas asignadas a los
	// procesadores
	private float calcularTiempoTotal(List<Procesador> procesadores) {
		float tiempoTotal = 0;
		for (Procesador procesador : procesadores) {
			tiempoTotal += procesador.getTiempoTareas();
		}
		return tiempoTotal;
	}

	// RESTRICCIONES AL AGREGAR TAREAS COMUN PARA BACKTRACKING Y GREEDY
	private boolean cumpleRestricciones(Procesador procesador, Tarea tarea, Float tiempoLimite) {

		if (tarea.esCritica() && procesador.getCantCriticas() >= 2) { // HAY MAS DE 2 TAREAS CRITICAS?
			System.out.println("el procesador " + procesador.getId() + " no tolera mas de 2 tareas criticas ");
			System.out.println(" -------------------------- ");
			return false;
		}

		float tiempoTotalConNuevaTarea = procesador.getTiempoTareas() + tarea.getTiempoEjecucion(); // VARIABLE TIEMPO
																									// DE EJECUCION

		if (!procesador.estaRefrigerado() && tiempoTotalConNuevaTarea > tiempoLimite) { // VERIFICA SI NO ESTA
																						// REFRIGERADO Y QUE NO EXCEDA
																						// TIEMPO
			System.out.println("Procesador " + procesador.getId()
					+ " sin refrigeracion no puede exceder el limite para asignar tarea: " + tarea.getId());
			System.out.println(" -------------------------- ");
			return false;
		}

		return true;
	}

	/*
	 * LA ESTRATEGIA PARA EL GREEDY, FUE DE PRIMERO TENER UN COSTO PERMITIDO DE TOMAR EL HASHMAP ARMADO DE TAREAS, PASARLO
	 * A ARRAYLIST Y ORDENARLO DE MANERA DESCENDENTE. GARANTIZANDO QUE LA PRIMER TAREA ASIGNADA SEA LA MAYOR, ELIMINANDO LA POSIBILIDAD
	 * DE AGREGARSE EN PROCESADORES CON LIMITACIONES DE TIEMPO DE EJECUCION POR FALTA DE REFRIGERACION Y CORTAR CON EL PROCESO INNECESARIO.
	 * LUEGO PARA CADA TAREA, ELIJO EL MEJOR PROCESADOR, CALCULANDO LOS TIEMPOS ACUMULADOS Y LA DISPONIBILIDAD DE LOS MISMOS, EVITANDO COLAS DE TAREAS 
	 * QUE PUEDAN AUMENTAR EL TIEMPO DE EJECUCION MAXIMO.
	 * UNA VEZ ENCONTRADA LA PRIMER SOLUCION MEJOR POSIBLE, SE DEVUELVE EL RESULTADO CON 16 CANDIDATOS CONSIDERADOS Y UN MAXIMO DE 100 DE EJECUCION.
	 */
	public Solucion<Procesador> greedy(Float tiempoLimite) {
		System.out.println("********* GREEDY : ************");
		List<Procesador> procesadores = new ArrayList<>(this.reader.getProcesadores().values());
		List<Tarea> tareas = new ArrayList<>(this.reader.getTareas().values()); // Convertir HashMap a ArrayList

		// ordenar las tareas por tiempo de ejecución descendente
		tareas.sort((t1, t2) -> Float.compare(t2.getTiempoEjecucion(), t1.getTiempoEjecucion()));

		float tiempoMaximo = 0;
		int candidatosConsiderados = 0;

		for (Tarea tarea : tareas) {
			
			Procesador mejorProcesador = null;
			float menorTiempoIncrementado = Float.MAX_VALUE;

			// recorre todos los procesadores para encontrar el mejor para asignar la tareas mas pesada
			for (Procesador procesador : procesadores) {
				if (cumpleRestricciones(procesador, tarea, tiempoLimite)) {
					// calculo el tiempo incrementado al asignar la tarea al procesador
					float tiempoIncrementado = procesador.getTiempoTareas() + tarea.getTiempoEjecucion();
					if (tiempoIncrementado < menorTiempoIncrementado) {
						//actualizo procesador indicado
						mejorProcesador = procesador;
						menorTiempoIncrementado = tiempoIncrementado;
					}
				}
				candidatosConsiderados++;
			}

		    // Asignar la tarea al mejor procesador encontrado
			if (mejorProcesador != null) {
				mejorProcesador.agregarTarea(tarea);
				if (mejorProcesador.getTiempoTareas() > tiempoMaximo) {
					tiempoMaximo = mejorProcesador.getTiempoTareas();

				}
			} else {
				System.out.println("No se pudo asignar la tarea: " + tarea.getId());
			}
		}

		

		// Imprimir el tiempo máximo de ejecución
		System.out.println("Tiempo máximo de ejecución: " + tiempoMaximo);

		// Imprimir la métrica de cantidad de candidatos considerados
		System.out.println("Cantidad de candidatos considerados: " + candidatosConsiderados);

		// Devolver la solución como objeto Solucion
		return new Solucion<Procesador>(procesadores, tiempoMaximo);
	}

}