package javaGame;

import java.util.ArrayList;
import java.util.Stack;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Creep extends gameInfo {
	private String images[] = {"res/bad1.png","res/bad2.png","res/bad3.png","res/bad4.png","res/bad4.png"};
	private String imageString;
	
	Stack<ArrayList<Integer>> pathStack = new Stack<ArrayList<Integer>>();
	private double speed = 1;
	private int x = 0;
	private int y = 0;
	private int targetX;
	private int targetY;
	private double health = 1;
	private double maxHealth = health;
	private int width = TILEWIDTH/2;
	private int height = TILEHEIGHT/2;
	private int realX = 0;
	private int realY = 0;
	private double baseMaxHealth;
	public int value;
	public int type;
	public int goalRotation;
	public int rotation;
	public int rotationSpeed = 3;

	public Creep(int type) {
		handleEnemySettings(type);
	}
	
	public Creep(Stack<ArrayList<Integer>> pathStack, int type) {
		this.pathStack = pathStack;
		this.targetX = this.pathStack.pop().get(1);
		this.targetY = this.pathStack.pop().get(0);
		handleEnemySettings(type);
	}
	
	public void handleEnemySettings(int type) {
		if (type >= defaultCreepHealth.length) {
			type = defaultCreepHealth.length;
		}
		this.rotation = 0;
		this.type = type;
		this.health = defaultCreepHealth[type-1];
		this.health = Math.ceil((waveNum*this.health)/2);
		this.baseMaxHealth = this.health;
		this.speed = defaultCreepSpeed[type-1];
		this.maxHealth = health;
		this.width = TILEWIDTH*5/8;
		this.height = 20;
		this.value = defaultCreepValue[type-1];
		this.rotationSpeed = (int) (this.speed*4);
		imageString = images[type-1];
	}
	
	public int getType() {
		return type;
	}
	
	public String getImageString() {
		return imageString;
	}
	
	public void setHealth(double health) {
		this.health = health;
	}

	public double getHealth() {
		return health;
	}
	
	public int getValue() {
		return (int) (this.value*Math.sqrt(waveNum/4)) + 2;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public Stack<ArrayList<Integer>> getPathStack() {
		return pathStack;
	}

	public void setPathStack(Stack<ArrayList<Integer>> pathStack) {
		this.pathStack = pathStack;
		if (!this.pathStack.isEmpty()) {
			int nextTargetX = this.pathStack.peek().get(1);
			int nextTargetY = this.pathStack.pop().get(0);
			setTargetX(nextTargetX);
			setTargetY(nextTargetY);
		}
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public int getTargetX() {
		return targetX*TILEWIDTH;
	}

	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}

	public int getTargetY() {
		return targetY*TILEHEIGHT;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}

	public void setNextX() {
		setTargetX(pathStack.pop().get(1));
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setNextY() {
		setTargetY(pathStack.pop().get(0));
	}
	
	public int getRealX() {
		return realX;
	}

	public void setRealX(int realX) {
		this.realX = realX;
	}

	public int getRealY() {
		return realY;
	}

	public void setRealY(int realY) {
		this.realY = realY;
	}
	
	public int getRotationAngle() {
		return rotation;
	}
	
	public ArrayList<Creep> moveToTarget(ArrayList<Creep> creepList, int i) {
		int xDiff = Math.abs(x-getTargetX());
		int yDiff = Math.abs(y-getTargetY());
		if (x < getTargetX() && xDiff > speed) {
			x+=speed;
			goalRotation = 0;
		}else if (x > getTargetX() && xDiff > speed) {
			x-=speed;
			goalRotation = 180;
		}else
		if (y < getTargetY() && yDiff > speed) {
			y+=speed;
			goalRotation = 90;
		}else if (y > getTargetY() && yDiff > speed) {
			y-=speed;
			goalRotation = -90;
		}
		
		//if close enough snap rotation to correct position
		if (Math.abs(goalRotation-rotation) < speed*rotationSpeed) {
			rotation = goalRotation;
		}else{
			//else rotate it to goal rotation
			int rotDir = (goalRotation-rotation)/Math.abs(goalRotation-rotation);
			rotation += speed*rotDir*rotationSpeed;
		}
		
		if (xDiff<=speed && yDiff<=speed) {
			setX(getTargetX());
			setY(getTargetY());
			if (getPathStack().isEmpty()) { //reaches the end
				lives--;
				GoldFade gf = new GoldFade(value,x-TILEWIDTH,y,0);
				goldList.add(gf);
				Creep c =creepList.get(i);
				creepList.remove(i);
				c = null;
				
			}else{
				int nextTargetX = pathStack.peek().get(1);
				int nextTargetY = pathStack.pop().get(0);
				setTargetX(nextTargetX);
				setTargetY(nextTargetY);
			}
		}
		
		return creepList;
	}
	
	public int getHealthWidth() {
		double ratio = health/maxHealth;
		return (int) (ratio*(TILEWIDTH-4));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
