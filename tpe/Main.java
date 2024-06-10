package tpe;

import tpe.clases.Solucion;
import tpe.clases.Tarea;

public class Main {

	public static void main(String args[]) {
		Servicios servicios = new Servicios("./src/tpe/datasets/Procesadores.csv", "./src/tpe/datasets/Tareas.csv", 10);

		// Servicio 1: Dado un identificador de tarea obtener toda la información
		// de la tarea asociada.

		System.out.println(servicios.servicio1("T1"));

		// Servicio 2: Permitir que el usuario decida si quiere ver todas las tareas
		// críticas
		// o no críticas y generar el listado apropiado resultante.

		System.out.println(servicios.servicio2(true));

		// Servicio 3: Obtener todas las tareas entre 2 niveles de prioridad indicados
		System.out.println(servicios.servicio3(30, 90));

		float tiempoLimite = 100.0f;
        Solucion solucion = servicios.backTracking(tiempoLimite);

        // SERVICIO DE BACKTRACKING ASIGNANDO TAREAS
        System.out.println("Mejor solución encontrada:");
        for (Tarea tarea : solucion.getMejorAsignacion()) {
            System.out.println(tarea);
        }
        System.out.println("Tiempo total: " + solucion.getMejorTiempo());
	}
}