package javaGame;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Tower extends gameInfo{
	private String images[] = {"res/tower1.png","res/tower2.png","res/tower3.png","res/tower4.png","res/tower5.png","res/tower3.png"};
	private String imageString;
	
	public Tile tile;
	public Creep target;
	public Bullet b = null;
	
	Random r = new Random();
	int shotRateOffSet = r.nextInt(10);
	public boolean shooting = false;
	private int shotRate;
	private int shotTimer = shotRate;
	
	private int type;
	private int range = 10;
	private int damage = 10;
	public int upgrade = 1;
	int value;
	int upgradeCost;
	
	public Tower(int type) {
		this.type = type;
		imageString = images[type];
	}
	

	public Tower(int type, Tile tile) {
		this.type = type;
		imageString = images[type];
		this.range = defaultTowerRange[type];
		this.tile = tile;
		this.range = defaultTowerRange[type];
		this.damage = defaultTowerDamage[type];
		this.shotRate = defaultTowerROF[type]+shotRateOffSet;
		this.shotTimer = this.shotRate;
		this.value = defaultTowerCost[type];
		this.upgradeCost = upgradeFormula();
	}
	
	public String getImageString() {
		return imageString;
	}
	
	public int getUpgradeCost() {
		return upgradeCost;
	}
	
	public int upgradeFormula() {
		return (int) Math.ceil(Math.pow(Math.E, (double) Math.sqrt(upgrade*50)/3));
	}
	
	public int getType() {
		return type;
	}
	
	public int getRange() {
		return range;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getValue() {
		return (int) Math.ceil(value/2);
	}
	
	public int getBuyValue() {
		return value;
	}
	
	public void newUpgrade() {
		upgrade++;
		damage = defaultTowerDamage[type]*upgrade;
		range = defaultTowerRange[type]+upgrade*20;
		upgradeCost = upgradeFormula();
		value += upgradeCost;
	}
	
	public double angleToTarget() {
		double xDiff = target.getX() - tile.column*TILEWIDTH;
		double yDiff = target.getY() - tile.row*TILEHEIGHT;
		if (xDiff == 0) { xDiff = .001; }
		double angle = Math.toDegrees(Math.atan2(yDiff, xDiff)) + 90;
		return angle;
	}
	
	public void getTarget(ArrayList<Creep> o) {
		//System.out.println(shooting);
			if (shooting) {
				shotTimer--;
				if (shotTimer <= 0) {
					shotRateOffSet = r.nextInt(10);
					shotTimer = shotRate + shotRateOffSet;
					shooting = false;
				}
			}
			
			ArrayList<Creep> creepList = o; // make copy as to not affect original!
			double min = -1;
			for (int i = 0; i < creepList.size(); i++) {
				Creep current = creepList.get(i);
				int tileX = tile.column*TILEWIDTH;
				int tileY = tile.row*TILEHEIGHT;
				int xDiff = Math.abs(tileX-current.getX());
				int yDiff = Math.abs(tileY-current.getY());
				double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
				if ((min == -1 || distance < min) && distance <= range) {
					min = distance;
					target = current;
				}	
			}
			if (min == -1) { //if there are no closest enemies
				target = null;
			}
	}
	
	public boolean shootBullet() {
		if (target != null) {
			if (type == 1 && !shooting) {
				generateLightning(tile.column*TILEWIDTH+TILEWIDTH/2, tile.row*TILEHEIGHT+TILEHEIGHT/2, target.getRealX(), target.getRealY());
				target.setHealth(target.getHealth()-damage);
				shooting = true;
			}else if (type != 1 && ((b != null && b.remove) || b == null)) {
				b = new Bullet(this, this.target, (tile.column*TILEWIDTH)+TILEWIDTH/2, (tile.row*TILEHEIGHT)+TILEHEIGHT/2, defaultTowerSpeed[type]);
				shooting = true;
			}
			return true;
		}else{
			return false;
		}
	}
	
	public void moveBullet() {
		b.moveToTarget();
	}
	
	public void generateLightning(int startX, int startY, int endX, int endY) {
		float xDiff = endX-startX;
		float yDiff = endY-startY;
		ArrayList<Float> vector = new ArrayList<Float>();
		ArrayList<Float> normalized = new ArrayList<Float>();
		//normalize
		float magnitude = (float) Math.sqrt(xDiff*xDiff + yDiff*yDiff);
		vector.add(xDiff);
		vector.add(yDiff);
		normalized.add(xDiff/=magnitude);
		normalized.add(yDiff/=magnitude);
		
		Random r = new Random();
		int intervals = 20; //divisble by 2
		int spazzes = 14;
		int randomInterval = r.nextInt(intervals)+intervals/2;
		float previousX =(float) (normalized.get(0)*10+startX);
		float previousY = (float) (normalized.get(1)*10+startY);
		float x;
		float y;
		int strands = 1;
		g.setColor(getRandomLightningColor(r));
			for (double i = 6; i <= magnitude; i+=randomInterval) {
				
				g.setLineWidth(getRandomThickness(r));
				x =(float) (normalized.get(0)*i+startX);
				y = (float) (normalized.get(1)*i+startY);
				int randomX = r.nextInt(spazzes)-spazzes/2;
				int randomY = r.nextInt(spazzes)-spazzes/2;
				x = x + randomX;
				y = y + randomY;
				g.drawLine(previousX, previousY, x, y);
				previousX = x;
				previousY = y;
				g.fillRect(x+randomX*2, y-randomY*2, 2, 2);
				randomInterval = r.nextInt(intervals)+intervals/2;
			}
			g.drawLine(previousX, previousY, endX, endY);
			g.setLineWidth(2);
			g.setColor(Color.yellow);
		
	}
	
	public float getRandomThickness(Random r) {
		int random = r.nextInt(3);
		switch(random) {
			case 0:
				return 1;
			case 1:
				return 2;
			case 2:
				return 3;
			default:
				return 2;
		}
	}
	
	public Color getRandomLightningColor(Random r) {
		int random = r.nextInt(4);
		switch(random) {
			case 0:
				return Color.yellow;
			case 1:
				return Color.orange;
			case 2:
				return new Color(0xFFEA00);
			case 3:
				return Color.white;
			default:
				return Color.yellow;
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
