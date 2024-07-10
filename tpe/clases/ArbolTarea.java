package tpe.clases;

import java.util.ArrayList;
import java.util.List;

public class ArbolTarea {
	
	private Node root;
	private int size;

	public ArbolTarea() {
		this.root = null;
		this.size = 0;
	}
	
	public Node getRoot() {
		return this.root;
	}
	
	public int getSize() {
		return this.size;
	}
	
	private void incrementarSize() {
		this.size++;
	}

	public void insertar(Tarea tarea) {
		root = insertarTarea(root, tarea);
		incrementarSize();
	}

	private Node insertarTarea(Node root, Tarea tarea) {
		if (root == null) {
			return new Node(tarea);
		}
		if (tarea.getNivelPrioridad() <= root.getTarea().getNivelPrioridad()) {
			root.setLeft(insertarTarea(root.getLeft(), tarea)); 
		} else if (tarea.getNivelPrioridad() >= root.getTarea().getNivelPrioridad()) {
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
			System.out.println("tarea esta en rango: " + node.getTarea().getNombre() + ", prioridad: " + node.getTarea().getNivelPrioridad());
			lista.add(node.getTarea());
		}
		
			getTareasRango(node.getLeft(), prioridadInferior, prioridadSuperior, lista);
		

			getTareasRango(node.getRight(), prioridadInferior, prioridadSuperior, lista);
		
	}
}


