package tpe.clases;

import java.util.List;

public class Solucion<T> {
    private List<T> mejorAsignacion;
    private float mejorTiempo;

    public Solucion(List<T> mejorAsignacion, float mejorTiempo) {
        this.mejorAsignacion = mejorAsignacion;
        this.mejorTiempo = mejorTiempo;
    }

    public List<T> getMejorAsignacion() {
    	if(mejorAsignacion.isEmpty()) {
    		System.out.println("EL ALGORITMO NO PUDO ENCONTRAR SOLUCION");
    	}
        return mejorAsignacion;
    }

    public float getMejorTiempo() {
        return mejorTiempo;
    }
}