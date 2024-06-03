package tpe;

public class Main {

	public static void main(String args[]) {
		Servicios servicios = new Servicios("./src/tpe/datasets/Procesadores.csv", "./src/tpe/datasets/Tareas.csv");
	
		// Servicio 1: Dado un identificador de tarea obtener toda la información
		// de la tarea asociada.

        System.out.println(servicios.servicio1("T1"));

        // Servicio 2: Permitir que el usuario decida si quiere ver todas las tareas críticas
        // o no críticas y generar el listado apropiado resultante.

        System.out.println(servicios.servicio2(true));

        // Servicio 3: Obtener todas las tareas entre 2 niveles de prioridad indicados
        System.out.println(servicios.servicio3(30, 90));
	}
}