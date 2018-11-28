package tree;

public class AssignTree {
	
	private Assignment root;
	
	public AssignTree() {
		this.setRoot(new Assignment());
	}

	public Assignment getRoot() {
		return root;
	}

	public void setRoot(Assignment root) {
		this.root = root;
	}

	

}
