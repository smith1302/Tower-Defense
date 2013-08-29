package javaGame;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Tile extends gameInfo {
	private int id;
	public int row;
	public int type;
	public String kind;
	public int column;
	boolean occupied;
	boolean path = false;
	boolean visited = false;
	boolean invalid = false;
	public String grassShade = "Grass0"; // Grass0, Grass, Grass2, Grass3
	public int pathId = -1;

	private Tower tower;
	public Creep target = null;
	
	public Tile(int id, int row, int column, int type) {
		this.occupied = false;
		this.id = id;
		this.row = row;
		this.column = column;
		Random r = new Random();
		int rand = r.nextInt(3);
		switch (rand) {
			case 0:
				grassShade = "Grass0";
				break;
			case 1:
				grassShade = "Grass";
				break;
			case 2:
				grassShade = "Grass2";
				break;
			case 3:
				grassShade = "Grass3";
				break;
		}
		
		this.type = type;
		this.kind = "Grass";
	}
	
	public void setType(int type) {
		this.type = type;
		if (type != 0 && type != 1 && type != 12 && type != 13 && type != 14) { //not grass or entrance/exit or bridge
			setOccupied(true);
		}
	}
	
	public boolean shootBullet() {
		if (tower != null) {
			return tower.shootBullet();
		}else{
			return false;
		}
	}

	
	public void getTarget(ArrayList<Creep> o) {
		if (tower != null) { // if this is a tower tile
			tower.getTarget(o);
		}
	}
	
	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}
	
	public int getID() {
		return id;
	}
	
	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public void setTower(int type) {
		tower = new Tower(type, this);
		setOccupied(true);
		this.type = 11;
		
	}
	
	public void unsetTower() {
		tower = null;
		setOccupied(false);
		type = 0;
	}
	
	public Tower getTower() {
		return tower;
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

}
