package tpe.clases;

import java.util.List;

public class Solucion {
    private List<Tarea> mejorAsignacion;
    private float mejorTiempo;

    public Solucion(List<Tarea> mejorAsignacion, float mejorTiempo) {
        this.mejorAsignacion = mejorAsignacion;
        this.mejorTiempo = mejorTiempo;
    }

    public List<Tarea> getMejorAsignacion() {
        return mejorAsignacion;
    }

    public float getMejorTiempo() {
        return mejorTiempo;
    }
}