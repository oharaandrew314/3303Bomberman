package common.models;

public class Wall extends Box {

	private static final long serialVersionUID = -7083256597186660972L;

	public Wall() {
		super("Wall");
	}
	
	public Wall(Wall wall) {
		super(wall);
	}

	@Override
	public String toString(){
		return "*";
	}
}
