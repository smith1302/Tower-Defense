package javaGame;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Explosion {
	public int x;
	public int y;
	public int numParticles;
	public Particle[] particleList;
	int count;
	
	public Explosion(int x, int y, String type) {
		this.x = x;
		this.y = y;
		Random r = new Random();
		numParticles = r.nextInt(20)+10;
		particleList = new Particle[numParticles];
		
		for (int i = 0; i < numParticles; i++) {
			Particle p = new Particle(x+r.nextInt(4)-2, y+r.nextInt(4)-2, type);
			particleList[i] = p;
		}
		
		int count = 0;
	}

}
