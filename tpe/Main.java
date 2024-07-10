package tpe;

import tpe.clases.Procesador;
import tpe.clases.Solucion;
import tpe.clases.Tarea;


public class Main {

	public static void main(String args[]) {
		Servicios servicios = new Servicios("./src/tpe/datasets/Procesadores.csv", "./src/tpe/datasets/Tareas.csv", 10);

		// Servicio 1: Dado un identificador de tarea obtener toda la información
		// de la tarea asociada.
		System.out.println("*****************************************");
		System.out.println("********** SERVICIO 1***************");
		System.out.println("***************************************************");
		System.out.println("SERVICIO 1");
		System.out.println(servicios.servicio1("T1"));

		// Servicio 2: Permitir que el usuario decida si quiere ver todas las tareas
		// críticas
		// o no críticas y generar el listado apropiado resultante.
		System.out.println("***************CORREGIDO***************************");
		System.out.println("********** SE RESOLVIO EL SERVICIO 2***************");
		System.out.println("***************************************************");
		System.out.println("SERVICIO 2");
		System.out.println(servicios.servicio2(false));

		// Servicio 3: Obtener todas las tareas entre 2 niveles de prioridad indicados
		System.out.println("***************CORREGIDO***************************");
		System.out.println("********** SE RESOLVIO EL SERVICIO 3***************");
		System.out.println("***************************************************");
		System.out.println("SERVICIO 3");
		System.out.println(servicios.servicio3(30, 90));

		float tiempoLimite = 200.0f;
		
        Solucion<Tarea> solucion = servicios.backTracking(tiempoLimite);

        // SERVICIO DE BACKTRACKING ASIGNANDO TAREAS
       
        System.out.println("Tiempo total: " + solucion.getMejorTiempo());
       
        Solucion<Procesador> solucion2 = servicios.greedy(tiempoLimite);
        
        // SERVICIO GREEDY DE  ASIGNADO DE TAREAS
        System.out.println("Mejor solución Greedy encontrada:");
        
        for (Procesador procesador : solucion2.getMejorAsignacion()) {
            System.out.println("procesador: " + procesador.getId() + " con tareas " + procesador.getTareasAsignadas());
        }
        System.out.println("Tiempo total: " + solucion2.getMejorTiempo());
	}
}