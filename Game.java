package javaGame;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.AppletGameContainer;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Game extends StateBasedGame {
	
	public static final String GAMENAME = "TD Game!";
	public static final int MENU = 0;
	public static final int MAP = 1;
	public static final int PLAY = 2;
	public static final int WIDTH = 550;
	public static final int HEIGHT = 400;
	
	public Game(String GAMENAME) {
		super(GAMENAME); // add title
		this.addState(new Menu(MENU));
		try {
			this.addState(new MapSelection(MAP));
			this.addState(new Play(PLAY));
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initStatesList(GameContainer gc) throws SlickException {
		this.getState(MENU).init(gc, this);
		this.getState(MAP).init(gc, this);
		this.getState(PLAY).init(gc, this);
		this.enterState(MENU); //which screen for users to start on
	}
	
	public static void main(String[] args) {
		AppGameContainer appgc; //main window
		try {
			//create a window that holds our game
			appgc = new AppGameContainer(new Game(GAMENAME));
			//width, height, fullscreen?
			appgc.setTargetFrameRate(60);
			appgc.setShowFPS(false);
			appgc.setVSync(false);
			appgc.setDisplayMode(WIDTH, HEIGHT, false);
			appgc.start();
		}catch(SlickException e){
			e.printStackTrace();
		}
	}

}
