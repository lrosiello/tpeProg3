package tpe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tpe.clases.ArbolTarea;
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
		
		if(esCritica) {
			return new ArrayList<>(this.reader.getTareasCriticas().values());
		}
		return new ArrayList<>(this.reader.getTareas().values());
		
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
	 * LLEGAR A LA HOJA, SE CORTA LA BUSQUEDA DE LA SOLUCION. DESPUES DE TANTOS
	 * INTENTOS, PUEDO PENSAR QUE NO EXISTE SOLUCION CON BACKTRACKING EN ESTE
	 * PROBLEMA
	 * 
	 */

	/*
	 * NUEVA ESTRATEGIA, SE DEJO EL ARBOL DE LADO PARA RECORRER LISTAS PROVENIENTES
	 * DE LA ESTRUCTURA HASHMAP PREVIA DEL READER
	 */
	// Agregar Tareas con backtracking
	public Solucion<Tarea> backTracking(Float tiempoLimite) {
		System.out.println("***************************************************");
		System.out.println("********* BACKTRACKING : ************");
		System.out.println("***************************************************");
		//LISTADO DE TAREAS
		ArrayList<Tarea> listaTareas = new ArrayList<>(this.reader.getTareas().values());
		listaTareas.addAll(this.reader.getTareasCriticas().values()); //UNIFICO TAREAS Y TAREASCRITICAS
		
		List<Procesador> procesadores = new ArrayList<>(this.reader.getProcesadores().values());
		List<Tarea> mejorSolucion = new ArrayList<>();
		float[] mejorTiempo = { Float.MAX_VALUE };
		Map<Procesador, List<Tarea>> mejorAsignacion = new HashMap<>();
		float[] tiemposAcumulados = new float[procesadores.size()];

		int totalEstados = back(listaTareas, 0, procesadores, tiempoLimite, mejorSolucion, mejorTiempo, mejorAsignacion,
				tiemposAcumulados);

		// Imprimir TITULO DE RESULTADOS Y TOTAL DE ESTADOS GENERADOS
		System.out.println("RESULTADOS FINALES DEL BACKTRACKING: ");
		System.out.println("Total de estados generados: " + totalEstados);

		// Imprimir la mejor asignación
		if (mejorAsignacion.isEmpty()) {
			mejorTiempo[0] = 0;
			System.out.println("NO HAY SOLUCION");
		}
		for (Map.Entry<Procesador, List<Tarea>> entry : mejorAsignacion.entrySet()) {
			System.out.println("Procesador: " + entry.getKey().getId() + " -> Tareas: " + entry.getValue());
		}

		// Devolver la mejor solución encontrada como objeto Solucion
		return new Solucion<>(mejorSolucion, mejorTiempo[0]);
	}

	private int back(ArrayList<Tarea> listaTareas, int posicion, List<Procesador> procesadores, Float tiempoLimite,
			List<Tarea> mejorSolucion, float[] mejorTiempo, Map<Procesador, List<Tarea>> mejorAsignacion,
			float[] tiemposAcumulados) {
		int estadosGenerados = 1; // Contador de estados generados, comienza en 1 para contar el estado actual

		Tarea tarea = listaTareas.get(posicion);
		for (int i = 0; i < procesadores.size(); i++) {
			Procesador procesador = procesadores.get(i);
			if (cumpleRestricciones(procesador, tarea, tiempoLimite)) {

				procesador.agregarTarea(tarea); // Agregar la tarea al procesador
				tiemposAcumulados[i] += tarea.getTiempoEjecucion(); // Actualizar el tiempo acumulado

				// Si la asignación actual alcanzó el tamaño total de tareas, se encontró una
				// solución
				if (posicion == listaTareas.size() - 1) {

					// Calcular el tiempo total de la asignación actual
					float tiempoTotal = calcularTiempoMaximoAcumulado(tiemposAcumulados);
					// Si es mejor que la mejor solución encontrada hasta ahora, actualizarla
					if (tiempoTotal < mejorTiempo[0]) {
						mejorTiempo[0] = tiempoTotal;
						mejorSolucion.clear();
						mejorSolucion.addAll(listaTareas);

						// Actualizar mejorAsignacion
						mejorAsignacion.clear();
						for (Procesador p : procesadores) {
							mejorAsignacion.put(p, new ArrayList<>(p.getTareasAsignadas()));
						}
					}
				} else {
					// Continuar con la asignación de las tareas restantes
					posicion++;
					estadosGenerados += back(listaTareas, posicion, procesadores, tiempoLimite, mejorSolucion,
							mejorTiempo, mejorAsignacion, tiemposAcumulados);

					posicion--; // Deshacer la asignación actual para probar con otro procesador
				}

				// Deshacer la asignación actual para probar con otro procesador
				procesador.removerTarea(tarea);
				tiemposAcumulados[i] -= tarea.getTiempoEjecucion(); // Revertir el tiempo acumulado
			}
		}

		return estadosGenerados;
	}

	private float calcularTiempoMaximoAcumulado(float[] tiemposAcumulados) {
		float maxTiempo = 0;
		for (float tiempo : tiemposAcumulados) {
			if (tiempo > maxTiempo) {
				maxTiempo = tiempo;
			}
		}
		return maxTiempo;
	}

	// RESTRICCIONES AL AGREGAR TAREAS COMUN PARA BACKTRACKING Y GREEDY
	private boolean cumpleRestricciones(Procesador procesador, Tarea tarea, Float tiempoLimite) {

		if (tarea.esCritica() && procesador.getCantCriticas() >= 2) { // HAY MAS DE 2 TAREAS CRITICAS?

			return false;
		}

		float tiempoTotalConNuevaTarea = procesador.getTiempoTareas() + tarea.getTiempoEjecucion(); // VARIABLE TIEMPO
																									// DE EJECUCION

		if (!procesador.estaRefrigerado() && tiempoTotalConNuevaTarea > tiempoLimite) { // VERIFICA SI NO ESTA
																						// REFRIGERADO Y QUE NO
																						// EXCEDA
																						// TIEMPO

			return false;
		}

		return true;
	}

	/*
	 * LA ESTRATEGIA PARA EL GREEDY, FUE DE PRIMERO TENER UN COSTO PERMITIDO DE
	 * TOMAR EL HASHMAP ARMADO DE TAREAS, PASARLO A ARRAYLIST Y ORDENARLO DE MANERA
	 * DESCENDENTE. GARANTIZANDO QUE LA PRIMER TAREA ASIGNADA SEA LA MAYOR,
	 * ELIMINANDO LA POSIBILIDAD DE AGREGARSE EN PROCESADORES CON LIMITACIONES DE
	 * TIEMPO DE EJECUCION POR FALTA DE REFRIGERACION Y CORTAR CON EL PROCESO
	 * INNECESARIO. LUEGO PARA CADA TAREA, ELIJO EL MEJOR PROCESADOR, CALCULANDO LOS
	 * TIEMPOS ACUMULADOS Y LA DISPONIBILIDAD DE LOS MISMOS, EVITANDO COLAS DE
	 * TAREAS QUE PUEDAN AUMENTAR EL TIEMPO DE EJECUCION MAXIMO. UNA VEZ ENCONTRADA
	 * LA PRIMER SOLUCION MEJOR POSIBLE, SE DEVUELVE EL RESULTADO CON 16 CANDIDATOS
	 * CONSIDERADOS Y UN MAXIMO DE 100 DE EJECUCION.
	 */

	/*
	 * SE ORDENO PROCESADORES INICIANDO RECORRIDO DESDE LOS REFRIGERADOS PARA TOMAR
	 * LAS TAREAS DE MAYOR TIEMPO DE EJECUCION
	 */
	/*
	 * HICE LAS PRUEBAS EXHAUSTIVAMENTE, NO COINCIDE CON LO OPTIMO QUE FIGURA EN LA
	 * PLANILLA, SIN EMBARGO, COMO SE DETALLA EN LA DESCRIPCION DEL FUNCIONAMIENTO
	 * DEL GREEDY POR CONSOLA, PARECE COHERENTE LA SOLUCION QUE FIGURA
	 */
	public Solucion<Procesador> greedy(Float tiempoLimite) {
		System.out.println("***************************************************");
		System.out.println("********* GREEDY : ************");
		System.out.println("***************************************************");
		List<Procesador> procesadores = new ArrayList<>(this.reader.getProcesadores().values());
		List<Tarea> tareas = new ArrayList<>(this.reader.getTareas().values()); // Convertir HashMap a ArrayList TAREAS
		List<Tarea> tareasCriticas = new ArrayList<>(this.reader.getTareasCriticas().values()); // Convertir HashMap
																								// TAREASCRITICAS

		// METODO DE ORDEN
		ordenadoDeListasDeTareas(tareas, tareasCriticas);
		// UNIFICACION DE LISTAS ORDENADAS
		tareas.addAll(tareasCriticas);

		// priorizar para tareas de mas tiempo los procesadores refrigerados,
		// ORDENAMIENTO DE PROCESADORES
		procesadores.sort((p1, p2) -> Boolean.compare(!p1.estaRefrigerado(), !p2.estaRefrigerado()));

		float tiempoMaximo = 0;
		int candidatosConsiderados = 0;

		for (Tarea tarea : tareas) {
			System.out.println("PREPARANDO: " + tarea.getNombre());
			Procesador mejorProcesador = null;
			float menorTiempoIncrementado = Float.MAX_VALUE;

			// recorre todos los procesadores para encontrar el mejor para asignar la tareas
			// mas pesada
			for (Procesador procesador : procesadores) {
				if (cumpleRestricciones(procesador, tarea, tiempoLimite)) {
					// calculo el tiempo incrementado al asignar la tarea al procesador

					float tiempoIncrementado = procesador.getTiempoTareas() + tarea.getTiempoEjecucion();
					if (tiempoIncrementado < menorTiempoIncrementado) {
						// actualizo procesador indicado
						mejorProcesador = procesador;
						menorTiempoIncrementado = tiempoIncrementado;
					}
				}
				candidatosConsiderados++;
			}

			// Asignar la tarea al mejor procesador encontrado
			if (mejorProcesador != null && cumpleRestricciones(mejorProcesador, tarea, tiempoLimite)) {
				mejorProcesador.agregarTarea(tarea);
				System.out.println("Se agrego " + tarea.getNombre() + "(tiempo ejecucion: " + tarea.getTiempoEjecucion()
						+ ") " + " en el procesador: " + mejorProcesador.getId() + " con "
						+ mejorProcesador.getCantCriticas() + " tareas criticas y acumulando tiempo de ejecucion de: "
						+ mejorProcesador.getTiempoTareas());
				if (mejorProcesador.getTiempoTareas() > tiempoMaximo) {
					tiempoMaximo = mejorProcesador.getTiempoTareas();
					System.out.println("el nuevo tiempo maximo es: " + tiempoMaximo);
				}
			} else {
				System.out.println("No se pudo asignar la tarea: " + tarea.getId() + " por lo que NO HAY SOLUCION");
				ArrayList<Procesador> vacio = new ArrayList<Procesador>();
				return new Solucion<Procesador>(vacio, 0);
			}
		}

		// Imprimir el tiempo máximo de ejecución
		System.out.println("Tiempo máximo de ejecución: " + tiempoMaximo);

		// Imprimir la métrica de cantidad de candidatos considerados
		System.out.println("Cantidad de candidatos considerados: " + candidatosConsiderados);

		// Devolver la solución como objeto Solucion
		return new Solucion<Procesador>(procesadores, tiempoMaximo);
	}

	private void ordenadoDeListasDeTareas(List<Tarea> tareas, List<Tarea> tareasCriticas) {
		// Ordenamiento de tareas, criticas al final y todas en orden descendente
		tareasCriticas.sort((t1, t2) -> {
			// ordenar las tareas por tiempo de ejecución descendente
			return Float.compare(t2.getTiempoEjecucion(), t1.getTiempoEjecucion());
		});
		tareas.sort((t1, t2) -> {
			// ordenar las tareas por tiempo de ejecución descendente
			return Float.compare(t2.getTiempoEjecucion(), t1.getTiempoEjecucion());
		});
	}

}