package javaGame;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Bullet extends gameInfo {
	private Tower parent;
	private Creep target;
	public float x;
	public float y;
	private int targetX;
	private int targetY;
	private int speed;
	boolean remove = false;
	
	public Bullet(Tower parent, Creep target, int originX, int originY, int speed) {
		this.parent = parent;
		this.target = target;
		this.x = originX;
		this.y = originY;
		this.speed = speed;
	}
	
	public Tower getTower() {
		return this.parent;
	}
	
	public void moveToTarget() {
		float oldX = x;
		targetX = target.getRealX();
		targetY = target.getRealY();
		float xDiff = targetX - x;
		float yDiff = targetY - y;
		float hyp = (float) Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		double distance = Math.sqrt(Math.abs(xDiff) + Math.abs(yDiff));
		if (hyp == 0) {
			hyp = (float) .01;
		}
		xDiff = xDiff/hyp;
		yDiff = yDiff/hyp;
		x += xDiff*speed;
		y += yDiff*speed;
		if (distance < this.speed/2) {
			target.setHealth(target.getHealth()-parent.getDamage());
			remove = true;
		}
		
		if (target == null) {
			remove = true;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

}
