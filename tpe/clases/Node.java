	package tpe.clases;
	
	public class Node {
		Tarea tarea;
		Node left, right;
	
		Node(Tarea tarea) {
			this.tarea = tarea;
			this.left = null;
			this.right = null;
		}
	
		public Node getLeft() {
			return left;
		}
	
		public void setLeft(Node left) {
			this.left = left;
		}
	
		public Node getRight() {
			return right;
		}
	
		public void setRight(Node right) {
			this.right = right;
		}
	
		public Tarea getTarea() {
			return tarea;
		}
	
		public void setTarea(Tarea tarea) {
			this.tarea = tarea;
		}
		
		
	}
