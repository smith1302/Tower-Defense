package javaGame;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Particle {
	public int x;
	public int y;
	public int width;
	public int height;
	public ArrayList<Integer> directionVector;
	public Color pColor;
	public float fade;
	public float fadeSpeed;
	public int c1;
	public int c2;
	public int c3;
	public String type;
	public double velY = .04;
	
	public Particle(int x, int y, String type) {
		Random r = new Random();
		
		this.type = type;
		this.x = x;
		this.y = y;
		this.width = 1+r.nextInt(3);
		this.height = this.width;
		fade = .5f;
		fadeSpeed = .03f;
		c1 = 0;
		c2 = 0;
		c3 = 0;
		
		directionVector = new ArrayList<Integer>(2);
		directionVector.add(r.nextInt(6)-3); // x
		directionVector.add(r.nextInt(6)-3); // y
		
		int randColor = r.nextInt(3);
		if (randColor == 0)
			c1 = 200;
		else if (randColor == 1)
			c1 = 255;
		else
			c1 = 88;

		if (type == "Rock") {
			fadeSpeed = .001f;
			fade = .9f;
			this.width = (int) Math.round(width*(r.nextInt(1)+2));
			this.height = this.width;
			if (randColor == 0) {
				c1 = 0; c2 = 0; c3 = 0;
			}else{
				c1 = 8; c2 = 8; c3 = 8;
			}
			directionVector.set(1, -1*(r.nextInt(3)+3));
			directionVector.set(0, directionVector.get(0)*2);
			pColor = new Color(c1,c2,c3,fade);
		}
		if (type == "Gray") {
			fadeSpeed = .03f;
			this.width = (int) Math.round(width*1.5);
			this.height = this.width;
			c1 = 64; c2 = 64; c3 = 64;
			pColor = new Color(c1,c2,c3,fade);
		}else if (type == "Yellow") {
			c2 = c1;
			fade = .8f;
			fadeSpeed = .05f;
			pColor = new Color(c1,c2,0,fade);
		}else if (type == "Red") {
			fade = .8f;
			fadeSpeed = .05f;
			pColor = new Color(c1,c2,0,fade);
		}
		
		
		
	}
	
	public void Move() {
		x += directionVector.get(0);
		y += directionVector.get(1)+velY;
		velY += .5;
		fade -= fadeSpeed;
		pColor = new Color(c1,c2,c3,fade);
	}
}
