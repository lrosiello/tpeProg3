package tpe.clases;

import java.util.ArrayList;
import java.util.List;

public class ArbolTarea {
	
	private Node root;

	public ArbolTarea() {
		this.root = null;
	}

	public void insertar(Tarea tarea) {
		root = insertarTarea(root, tarea);
	}

	private Node insertarTarea(Node root, Tarea tarea) {
		if (root == null) {
			return new Node(tarea);
		}
		if (tarea.getNivelPrioridad() < root.getTarea().getNivelPrioridad()) {
			root.setLeft(insertarTarea(root.getLeft(), tarea)); 
		} else if (tarea.getNivelPrioridad() > root.getTarea().getNivelPrioridad()) {
			root.setRight(insertarTarea(root.getRight(), tarea)); 
		}
		return root;
	}

	public List<Tarea> tareasPorRango(int prioridadInferior, int prioridadSuperior) {
		List<Tarea> result = new ArrayList<>();
		getTareasRango(root, prioridadInferior, prioridadSuperior, result);
		return result;
	}

	private void getTareasRango(Node node, int prioridadInferior, int prioridadSuperior, List<Tarea> lista) {
		if (node == null) {
			return;
		}
		if (node.getTarea().getNivelPrioridad() >= prioridadInferior && node.getTarea().getNivelPrioridad() <= prioridadSuperior) {
			lista.add(node.getTarea());
		}
		if (node.getTarea().getNivelPrioridad() > prioridadInferior) {
			getTareasRango(node.getLeft(), prioridadInferior, prioridadSuperior, lista);
		}
		if (node.getTarea().getNivelPrioridad() < prioridadSuperior) {
			getTareasRango(node.getRight(), prioridadInferior, prioridadSuperior, lista);
		}
	}
}


