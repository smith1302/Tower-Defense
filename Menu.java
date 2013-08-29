package javaGame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import java.awt.Font;

public class Menu extends gameInfo {
	int mouseX;
	int mouseY;
	Image beginBtn; //  148x43
	Image beginBtnOver;
	Image mainMenu;
	int beginBtnWidth;
	int beginBtnHeight;
	int beginBtnCenter;
	int beginBtnY;
	boolean overBtn;
	
	public Menu(int state) {
		
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		beginBtn = new Image("res/beginBtn.png");
		beginBtnOver = new Image("res/beginBtnOver.png");
		beginBtnWidth = beginBtn.getWidth();
		beginBtnHeight = beginBtn.getHeight();
		beginBtnCenter = WIDTH/2 - beginBtnWidth/2;
		beginBtnY = 230;
		overBtn = false;
		mainMenu = new Image("res/mainMenu.png");
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(mainMenu,0, 0);
		//g.drawString(GAMENAME, WIDTH/2-40, 50);
		if (overBtn) {
			g.drawImage(beginBtnOver,beginBtnCenter, beginBtnY);
		}else{
			g.drawImage(beginBtn,beginBtnCenter, beginBtnY);
		}
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		if(isOverArea(mouseX, mouseY, beginBtnCenter, beginBtnY, beginBtnWidth, beginBtnHeight)) {
			overBtn = true;
			//0 is left click, 1 is right click
			if (input.isMouseButtonDown(0)) {
				sbg.enterState(1, new FadeOutTransition(), new FadeInTransition());
			}
		}else{
			overBtn = false;
		}
		
		/*Input input = gc.getInput();
		if(input.isKeyDown(Input.KEY_UP)) {
			towerY--;
		}else if(input.isKeyDown(Input.KEY_RIGHT)) {
			towerX++;
		}else if(input.isKeyDown(Input.KEY_LEFT)) {
			towerX--;
		}else if(input.isKeyDown(Input.KEY_DOWN)) {
			towerY++;
		}*/
		
	}

	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public boolean isOverArea(int mouseX, int mouseY, int x, int y, int width, int height) {
		int left = x;
		int right = x+width;
		int bottom = HEIGHT-(y+height);
		int top = HEIGHT-y;
		if ((mouseX > left && mouseX < right) && (mouseY > bottom && mouseY < top)) {
			return true;
		}else{
			return false;
		}
	}
}
