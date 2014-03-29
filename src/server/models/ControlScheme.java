package server.models;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import common.events.GameKeyEvent;

public class ControlScheme {
	
	public static enum Control { Up, Left, Down, Right, Bomb, Start, Exit };
	private static final Map<Integer, Control> bindings = new HashMap<>();
	private static final Collection<ControlScheme> schemes = new ArrayList<>();
	
	private final Map<Control, Integer> controls;
	public final String name;
	
	public ControlScheme(String name){
		this.name = name;
		controls = new HashMap<>();
		schemes.add(this);
	}

	public void put(Control control, int keyCode){
		controls.put(control, keyCode);
		bindings.put(keyCode, control);
	}
	
	public Map<Control, Integer> getControls(){
		return new HashMap<Control, Integer>(controls);
	}
	
	// Static methods
	
	public static void createControlScheme(
		String name,
		int upCode, int leftCode, int downCode, int rightCode, int bombCode
	){
		ControlScheme scheme = new ControlScheme(name);
		scheme.put(Control.Up, upCode);
		scheme.put(Control.Left, leftCode);
		scheme.put(Control.Down, downCode);
		scheme.put(Control.Right, rightCode);
		scheme.put(Control.Bomb, bombCode);
	}
	
	public static Collection<ControlScheme> getControlSchemes(){
		return new ArrayList<ControlScheme>(schemes);
	}
	
	public static Control parse(GameKeyEvent event){
		return bindings.get(event.getKeyCode());
	}
	
	// Static Initializer
	static {
		ControlScheme common = new ControlScheme("Common");
		common.put(Control.Start, KeyEvent.VK_ENTER);
		common.put(Control.Exit, KeyEvent.VK_ESCAPE);	
		
		ControlScheme.createControlScheme(
			"Righty", KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S,
			KeyEvent.VK_D, KeyEvent.VK_F
		);
		ControlScheme.createControlScheme(
			"SouthPaw", KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K,
			KeyEvent.VK_L, KeyEvent.VK_SEMICOLON
		);
		ControlScheme.createControlScheme(
			"n00b", KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN,
			KeyEvent.VK_RIGHT, KeyEvent.VK_SPACE
		);
	}
}
