package javaGame;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.BasicGameState;

public abstract class gameInfo extends BasicGameState {
	public static final String GAMENAME = "Tower Defense!";
	public static final int MENU = 0;
	public static final int PLAY = 1;
	public static final int WIDTH = 550;
	public static final int HEIGHT = 400;
	public static final int BOTTOMGUIHEIGHT = 100; //intervals of 25
	public static final int MAPHEIGHT = HEIGHT-BOTTOMGUIHEIGHT;
	public static final int TILEHEIGHT = 25;
	public static final int TILEWIDTH = 25;
	public static final int MAPROWS = MAPHEIGHT/TILEHEIGHT;
	public static final int MAPCOLS = WIDTH/TILEWIDTH;
	public static final int NUMTOWERS = 5;
	public static final int NUMCREEPS = 4;
	
	// ---------
	public int[] defaultCreepSpeed = {2,2,5,1};
	int adjustment = 10;
	public int[] defaultCreepHealth = {1*adjustment,2*adjustment,2*adjustment,3*adjustment};
	public int[] defaultCreepValue= {5,6,7,8};
	
	
	public int[] defaultTowerRange = {70,90,110,160,50};
	public int[] defaultTowerDamage = {1*adjustment,5,25*adjustment,3*adjustment,40*adjustment};
	public int[] defaultTowerDmgUpgrade = {4,3,7,12};
	public int[] defaultTowerROF = {60,-5,120,5,60};
	public int[] defaultTowerCost = {25,175,200,325,450};
	public int[] defaultTowerSpeed = {11,11,10,13,10};
	public String[] descriptions = 
	{"Your basic tower. It's cheap, weak \nand fires plain bullets.",
	 "Crafted by Thor himself. Produces a steady \nstream of lightning to absolutely dominate \nyour enemies.",
	 "The big guns. Don't expect a high rate of \nfire, but you can be sure when it hits, \nit really causes some damage.",
	 "Not your ordinary tower. Though it doesn't \ndo much damage, it's rate of fire and \nrange is legendary.",
	 "Awful range. Packs a punch."
	};
	public String[] enemyDescriptions = 
	{"Normal","Tough","Fast","Slow","Mix","Normal","Tough","Fast","Slow","Mix","Normal","Tough","Fast","Slow","Mix"};

	public static int money = 100;
	public static int lives = 3;
	public static int waveNum = -1;
	public static float waveBlockSpeed = (float) .2;
	public static int difficulty = 1;
	
	public static int mouseX = 0;
	public static int mouseY = 0;
	public static Graphics g = null;
	
	public static ArrayList<GoldFade> goldList = new ArrayList<GoldFade>();
	ArrayList<Explosion> explosionList = new ArrayList<Explosion>();
}
