package javaGame;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Button extends gameInfo {
	public int x;
	public int y;
	public int width;
	public int height;

	public Button(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean isOverArea() {
		int left = x;
		int right = x + width;
		int bottom = y;
		int top = height + y;
		if ((mouseX > left && mouseX < right) && (mouseY > bottom && mouseY < top)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void init(GameContainer arg0, StateBasedGame arg1)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2)
			throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer arg0, StateBasedGame arg1, int arg2)
			throws SlickException {
		Input input = arg0.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		System.out.println(mouseX);
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}
}
