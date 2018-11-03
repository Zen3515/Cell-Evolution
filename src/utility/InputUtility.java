package utility;

import java.util.HashSet;

import javafx.scene.input.KeyCode;

public class InputUtility {

	public static double mouseX,mouseY;
	public static boolean mouseOnScreen = true;
	public static boolean isLeftDown = false;
	private static boolean isLeftClickedLastTick = false;
	private static HashSet<KeyCode> keyPressed = new HashSet<KeyCode>(); 
	
	public static boolean getKeyPressed(KeyCode keycode) {
		//System.out.println(keyPressed.contains(keycode) + " , for is press " + keycode);
		return keyPressed.contains(keycode);
	}
	public static void setKeyPressed(KeyCode keycode,boolean pressed) {
		if(pressed){
			if(!keyPressed.contains(keycode)){
				keyPressed.add(keycode);
			}
		}else{
			keyPressed.remove(keycode);
		}
		//System.out.println(keyPressed);
	}
	
	public static boolean hasKeyPressed() {
		return !keyPressed.isEmpty();
	}
	
	public static void mouseLeftDown(){
		isLeftDown = true;
		isLeftClickedLastTick = true;
	}
	
	public static void mouseLeftRelease(){
		isLeftDown = false;
	}
	
	public static boolean isLeftClickTriggered(){
		return isLeftClickedLastTick;
	}
	
	public static void updateInputState(){
		isLeftClickedLastTick = false;
	}
	
	public static void reset() {
		keyPressed.clear();
	}
	
}
