package javaGame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class MapSelection extends gameInfo {
	int mouseX;
	int mouseY;
	Image easy;
	Image easyOver;
	Image hard;
	Image hardOver;
	Image map1;
	Image map2;
	Image diff;
	Image bg;
	int easyX;
	int hardX;
	int padding;
	int textHeight = 125;
	int imageHeight = textHeight+50;
	boolean overEasyBtn;
	boolean overHardBtn;
	
	public MapSelection(int state) {
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		easy = new Image("res/easy.png");
		easyOver = new Image("res/easyOver.png");
		hard = new Image("res/hard.png");
		hardOver = new Image("res/hardOver.png");
		map1 = new Image("res/map1Img.png");
		map2 = new Image("res/map2Img.png");
		diff = new Image("res/difficulty.png");
		bg = new Image("res/bg.png");
		padding = 100;
		easyX = padding;
		hardX = WIDTH-hard.getWidth()-padding;
	}

	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setColor(new Color(0x202020));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(bg, 0, 0);
		
		g.drawImage(diff, (WIDTH/2)-diff.getWidth()/2, 30);
		
		if (overEasyBtn)
			g.drawImage(easyOver, easyX, textHeight);
		else
			g.drawImage(easy, easyX, textHeight);
		
		if (overEasyBtn)
			g.setColor(new Color(0xDFBE71));
		else
			g.setColor(new Color(0x808080));
		g.setLineWidth(5);
		g.drawImage(map1, easyX-map1.getWidth()/2+easy.getWidth()/2, imageHeight);
		g.drawRoundRect(easyX-map1.getWidth()/2+easy.getWidth()/2, imageHeight, 185, 159, 7);
		
		if (overHardBtn)
			g.drawImage(hardOver, hardX, textHeight);
		else
			g.drawImage(hard, hardX, textHeight);
		
		if (overHardBtn)
			g.setColor(new Color(0xDFBE71));
		else
			g.setColor(new Color(0x808080));
		g.drawImage(map2, hardX-map2.getWidth()/2+hard.getWidth()/2, imageHeight);
		g.drawRoundRect(hardX-map2.getWidth()/2+hard.getWidth()/2, imageHeight, 185, 159, 7);
	}

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		mouseX = Mouse.getX();
		mouseY = Mouse.getY();
		if(isOverArea(mouseX, mouseY, easyX-map1.getWidth()/2+easy.getWidth()/2, textHeight-5, 190, easy.getHeight()+imageHeight+90)) {
			overEasyBtn = true;
			//0 is left click, 1 is right click
			if (input.isMouseButtonDown(0)) {
				difficulty = 1;
				sbg.enterState(2, new FadeOutTransition(), new FadeInTransition());
			}
		}else if(isOverArea(mouseX, mouseY, hardX-map2.getWidth()/2+hard.getWidth()/2, textHeight-5, 190, hard.getHeight()+imageHeight+90)) {
			overHardBtn = true;
			//0 is left click, 1 is right click
			if (input.isMouseButtonDown(0)) {
				difficulty = 2;
				sbg.enterState(2, new FadeOutTransition(), new FadeInTransition());
			}
		}else{
			overEasyBtn = false;
			overHardBtn = false;
		}
		
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
	
	public int getID() {
		// TODO Auto-generated method stub
		return 1;
	}
	
	
}
