package javaGame;
import java.awt.Cursor;
import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import org.newdawn.slick.*;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class Play extends gameInfo {

	Resource resource = new Resource();
	
	SpriteSheet sheet;
	Animation turret;
	
	Image tower;
	Image creep;
	Image turretImage;
	SimpleFont font;
	
	PathFinding path;
	
	Stack<ArrayList<Integer>> pathStack = null;
	ArrayList<Tile> lightningTowers = new ArrayList<Tile>();
	ArrayList<Creep> creepList = new ArrayList<Creep>();
	ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	ArrayList<Integer> creepType = new ArrayList<Integer>();
	ArrayList<waveBlock> waveList;
	HashMap<String, Button> buttonMap = new HashMap<String, Button>();
	
	Tile map[][] = new Tile[MAPROWS][MAPCOLS];
	Tile fakeTile = new Tile(-1, -1, -1, -1);
	Tile currentTile = fakeTile; // fake tile for starting purposes
	Tile changedTile = fakeTile; // fake tile for starting purposes
	int towerButtonPos[][] = new int[NUMTOWERS][2];
	
	boolean init = false;
	boolean overBlock = false;
	boolean offMap = true;
	boolean tileChange = false;
	boolean click = false;
	boolean quickClick = false;
	boolean clickUp = false;
	int overTowerButton = -1;
	//the towerbutton selected
	int selectedTower = 0;
	int editTower = -1;
	int editTowerId = -1;
	Tower editTowerTower;
	Color colorAlpha;
	String debugText = "";
	
	Image turretImg;
	Image turretButtonImg;
	
	int startX = 0;
	int startY = 0;
	int finishX = MAPCOLS-1;
	int finishY = MAPROWS-1;
	int rotation = 0;
	
	double timer;
	boolean pause;
	boolean gameStart;
	int creepRate = 1;
	int currentCreepSelection = 1;
	Random randomCreep;
	
	Input input;

	public Play(int state) throws SlickException {
	}

	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		resource.loadImage("heart", "res/heart.png");
		
		resource.loadImage("Grass", "res/grass.png");
		resource.loadImage("Grass0", "res/grass0.png");
		resource.loadImage("Grass2", "res/grass2.png");
		resource.loadImage("Grass3", "res/grass3.png");
		resource.loadImage("Path", "res/path.png");
		resource.loadImage("turretButton", "res/turretButton.png");
		resource.loadImage("waveBar", "res/waveBar.png");
		resource.loadImage("waveGUI", "res/waveGUI.png");
		
		resource.loadImage("tile1", "res/specialTile.png");
		
		//waterTiles
		resource.loadImage("tile2", "res/ulw.png");
		resource.loadImage("tile3", "res/umw.png");
		resource.loadImage("tile4", "res/urw.png");
		resource.loadImage("tile5", "res/mlw.png");
		resource.loadImage("tile6", "res/mmw.png");
		resource.loadImage("tile7", "res/mrw.png");
		resource.loadImage("tile8", "res/blw.png");
		resource.loadImage("tile9", "res/bmw.png");
		resource.loadImage("tile10", "res/brw.png");
		
		resource.loadImage("tile12", "res/bridgel.png");
		resource.loadImage("tile13", "res/bridge.png");
		resource.loadImage("tile14", "res/bridger.png");
		
		//init stuff
		
		pause = false;
		gameStart = false;
		timer = System.currentTimeMillis()/1000;
		//load initial creep type
		for (int i = 1; i <= 4; i++) {
			creepType.add(i);
		}
		randomCreep = new Random();
		
		//set the id for each tile
		int id = 0;
		for (int i = 0; i < map.length; i++) {
		for (int j = 0; j < map[i].length; j++) {
				map[i][j] = new Tile(id, i, j, 0);
				map[i][j].setType(0);
				id ++;
			}
		}
		map[startY][startX].setType(1);
		map[finishY][finishX].setType(1);
		
		//
		
				
		sheet = new SpriteSheet("res/spritesheet/TurretSheet.png",25,35);
		turret = new Animation(sheet, 100);
		turretImage = turret.getImage(0);
		
		font = new SimpleFont("ARIAL", Font.BOLD, 11);
		
		//make images for each tower
		for (int i = 0; i < NUMTOWERS; i++) {
			Tower t = new Tower(i);
			resource.loadImage("Tower" + i, t.getImageString());
		}
		
		//make images for each creep
		for (int i = 1; i < NUMCREEPS+2; i++) {
			Creep c = new Creep(i);
			resource.loadImage("bad" + i, c.getImageString());
		}
		
		
		//make the "waveBlocks" and put them into a queue
		waveList = new ArrayList<waveBlock>();
		int waveBlockWidth = 150;
		int waveBlockHeight = 25;
		for (int i = 0; i < 40; i++) {
			waveList.add(new waveBlock((i+1), waveBlockWidth, waveBlockHeight, (int) (i*(waveBlockWidth+4))+100));
		}
		
		turretImg = resource.getImage("turretButton");
		turretButtonImg = resource.getImage("turretButton");
		//lets make our buttons!
		buttonMap.put("buyButton", new Button(WIDTH-68, MAPHEIGHT+40, 35, 15));
		buttonMap.put("gui", new Button(0, MAPHEIGHT, WIDTH, BOTTOMGUIHEIGHT));
		buttonMap.put("sellButton", new Button(WIDTH/2+23, MAPHEIGHT+69, 64, 15));
		
		input = gc.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
	}

	// RENDER RENDER RENDER RENDER RENDER RENDER RENDER RENDER RENDER RENDER
	// RENDER RENDER RENDER RENDER RENDER RENDER
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawString(debugText, 200, 0);
		g.setColor(Color.white);
		this.g = g;
		
		// draw map =======================================
		drawMap(g);
		// end draw map ================================+
		drawTowerHover(g);
		// draw creeps =======================================
		drawCreeps(g);
		// end draw creeps =======================================
		
		// draw bullets =========================================
		drawBullets(g);
		// end draw bullets =====================================
		
		// Draw bottom gui ================================
		drawGUI(g);
		// end draw bottom gui ===========================
		drawExplosions(g);
		
		buttonHandler(g);
		
		goldListHandler(g);
	}
	
	

	
	// UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE
	// UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE
	// UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE
	// UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE
	// UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE UPDATE
	
	
	

	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		if (!init) {
			Random r = new Random();
			if (difficulty == 2) {
				waterBridge(r.nextInt(3)+4, r.nextInt(MAPROWS-1)+1);
				waterBridge(r.nextInt(3)+13, r.nextInt(MAPROWS-1)+1);
			}
			waterBlock(MAPCOLS-3,0,1);		
			waterBlock(10,MAPROWS-2,0);
			init = true;
		}
		
		if (lives <= 0)  {
			sbg.addState(new Play(2));
			sbg.getState(2).init(gc, sbg);
			sbg.enterState(0, new FadeOutTransition(), new FadeInTransition());
			reset();
		}
		
		
		input = gc.getInput();
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();

		if (pathStack == null) getPath(startX,startY);
		// debugText = "currentID:"
		// +currentTile.getID()+", old ID:"+oldTile.getID();

		createCreep();
		
		clickUp = false;
		if (input.isMouseButtonDown(0)) { // mouse down
			click = true;
		}

		if (!input.isMouseButtonDown(0)) { // mouse Up
			if (click) clickUp = true;
			click = false;
			tileChange = true;
		}
		
		// add or remove tower ******
		if (clickUp && mouseY <= MAPHEIGHT) {
			if (currentTile.type == 11) { // edit if tower exists
				editTowerId = currentTile.getID();
				editTower = currentTile.getTower().getType();
				editTowerTower = currentTile.getTower();
				selectedTower = -1;
			} else {
				// make sure path isn't blocked (returns if pathstack is empty
				//valid move makes sure its in bounds
				if (!currentTile.isOccupied() && validMove(currentTile.getRow(), currentTile.getColumn()) && selectedTower != -1) {
					currentTile.setTower(selectedTower); // set tower if nothing exists
					//when we make a new tower, make a turret image specific for it
					resource.loadImage("turret"+Integer.toString(currentTile.getID()), new Image("res/turret1.png"));
					//start timer when first tower is placed
					if (isPathEmpty()) { 
						//if path is blocked
						currentTile.unsetTower();
					}else{
						//if we actually place a tower... update paths
						updateCreepPaths();
						getPath(startX,startY);
						if (currentTile.getTower() != null && currentTile != null) {
							money -= currentTile.getTower().getBuyValue();
							if (currentTile.getTower().getType() == 1 && currentTile != null) {
								lightningTowers.add(currentTile);
							}
						}
					}
					gameStart = true;
				}else{
					currentTile.setInvalid(true);
				}
			}
			clickUp = false;
		}

		if (click && overTowerButton != -1) { // if click on towerButton
			selectedTower = overTowerButton;
			nullEditTower();
		}

		// check for hovering over tower buttons
		boolean overSomeButton = false;
		for (int i = 0; i < towerButtonPos.length; i++) {
			int x = towerButtonPos[i][1];
			int y = towerButtonPos[i][0];
			if (isOverArea(mouseX, mouseY, x, y, TILEWIDTH, TILEHEIGHT)) {
				overTowerButton = i;
				overSomeButton = true;
			}
		}
		if (!overSomeButton) {
			overTowerButton = -1;
		}

		// Check for hovering of tiles ---------------
		// also, check for new targets
		int id = 0;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				int x = j * TILEWIDTH;
				int y = i * TILEHEIGHT;
				map[i][j].getTarget(creepList);
				if (isOverArea(mouseX, mouseY, x, y, TILEWIDTH, TILEHEIGHT)) {
					changedTile = map[i][j];
				}
				id++;
			}
		}// --------------------------------------------

	}
	
	
	
	// =============================================================================================================================
	// =============================================================================================================================
	// =============================================================================================================================
	// =============================================================================================================================
	// =============================================================================================================================
	// =============================================================================================================================
	// =============================================================================================================================
	// =============================================================================================================================
	
	
	
	

	public int getID() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	public void nullEditTower() {
		editTower = -1;
		editTowerTower = null;
		editTowerId = -1;
	}
	
	// =========================================== CREEP STUFF =======================================================================
	
	public void createCreep() {
		//we make creeps when:
		// our timer lets us, its not paused, the game has started (e.g, placed a tower), the first block has hit
		if (System.currentTimeMillis()/1000 >= timer+creepRate && !pause && gameStart && waveNum != -1 && overBlock) {
			getPath(startX,startY);
			Creep c = new Creep(pathStack, currentCreepSelection);
			creepList.add(c);
			timer = System.currentTimeMillis()/1000;
		}
	}
	
	public void updateCreepPaths() {
		for (int i = 0; i < creepList.size(); i++) {
			Creep cc = creepList.get(i);
			//we need the tile index not the actual x and y
			int creepTileX = cc.getTargetX()/TILEWIDTH;
			int creepTileY = cc.getTargetY()/TILEHEIGHT;
			Stack<ArrayList<Integer>> newPath = getPath(creepTileX,creepTileY);
			if (newPath == null) {
				currentTile.unsetTower();
				newPath = getPath(creepTileX,creepTileY);
			}
			cc.setPathStack(newPath);
		}
	}
	
	public void drawCreeps(Graphics g) {
		for (int i = 0; i < creepList.size(); i++) {
			// pop off the next path and get x
			Creep cc = creepList.get(i);
			if (cc.getHealth() > 0) 
			{
				creepList = cc.moveToTarget(creepList, i);
				int topOffSet = (TILEHEIGHT-cc.getHeight())/2;
				int leftOffSet = (TILEWIDTH-cc.getWidth())/2;
				//draw health bar
				//	g.setLineWidth(1);
				//	g.setColor(new Color(1.0f,0,0,.3f));
				//	g.fillRect(cc.getX()+3, cc.getY()+1, cc.getHealthWidth(), 4);
				//draw creep
				g.setColor(Color.white);
				int creepType = cc.getType();
				creep = resource.getImage("bad" + creepType);
				creep.setRotation(cc.getRotationAngle());
				g.drawImage(creep, leftOffSet+cc.getX()-4, cc.getY()+topOffSet-2);
				cc.setRealX(leftOffSet+cc.getX()+cc.getWidth()/2);
				cc.setRealY(cc.getY()+topOffSet + +cc.getHeight()/2);
			}
			else // death of creep
			{
				int value = creepList.get(i).getValue();
				int cx = creepList.get(i).getRealX();
				int cy = creepList.get(i).getRealY();
				creepList.remove(i);
				cc = null;
				explosionList.add(new Explosion(cx,cy, "Red"));
				GoldFade gf = new GoldFade(value,cx,cy,1);
				money += value;
				goldList.add(gf);
			}
		}
	}
	
	// =================================================== END CREEP STUFF ================================================

	public boolean isPathEmpty() {
		Tile temp[][] = map.clone();
		path = new PathFinding(temp, startX, startY, finishX, finishY);
		boolean isEmpty = path.isEmpty();
		return isEmpty;
	}
	
	public Stack<ArrayList<Integer>> getPath(int startX, int startY) {
		Tile temp[][] = map.clone();
		path = new PathFinding(temp, startX, startY, finishX, finishY);
		if (path.isEmpty()) {
			return null;
		}else{
			pathStack = path.getPath();
			return pathStack;
		}
	}

	public boolean validMove(int y, int x) {
		// check to see if this column works
		if ((x == startX && y == startY) || (x == finishX && y == finishY)) {
			return false;
		}else{
			return true;
		}
	}

	public boolean isOverArea(int mouseX, int mouseY, int x, int y, int width, int height) {
		int left = x;
		int right = x + width;
		int bottom = y;
		int top = HEIGHT + y;
		if ((mouseX > left && mouseX < right) && (mouseY > bottom && mouseY < top)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void drawMap(Graphics g) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				int x = j * TILEWIDTH;
				int y = i * TILEHEIGHT;
				//System.out.println("i: "+i+", j: "+j+", type:" + map[i][j].type);
				if (map[i][j].type == 11) { // if its a tower tile
					int towerType = map[i][j].getTower().getType();
					tower = resource.getImage("Tower" + towerType);
					g.drawImage(tower, x, y);
					if (map[i][j].getID() == editTowerId) {
						g.setColor(Color.yellow);
						g.drawRect(x+1, y+1, TILEWIDTH-1, TILEHEIGHT-1);
					}
					//turret.draw(x,y);
					//don't try to rotate if its null.. check out here so we dont change the rotation
					if(towerType != 1) {
						turretImg = resource.getImage("turret"+Integer.toString(map[i][j].getID()));
						if (map[i][j].getTower().target != null) {
							turretImg.setRotation((float) map[i][j].getTower().angleToTarget());
						}
						g.drawImage(turretImg, x, y);
					}
					if (!map[i][j].getTower().shooting) {
						if (map[i][j].getTower().getType() != 1 && map[i][j].shootBullet()) {
							//fire a shot (add bullet to list)
							bulletList.add(map[i][j].getTower().b);
						}
					}
					
				} else {
					//start and finish tiles
					if (map[i][j].type == 1) {
						g.setColor(Color.black);
						//g.fillRect(x, y, TILEWIDTH, TILEHEIGHT);
						g.drawImage(resource.getImage("tile1"),x,y);
					}else
						//water tiles
					if ((map[i][j].type >= 2 && map[i][j].type <= 10) || (map[i][j].type >= 12 && map[i][j].type <= 14)) {
						g.drawImage(resource.getImage("tile"+map[i][j].type),x,y);
					}else{
						//draw grass
						g.drawImage(resource.getImage(map[i][j].grassShade),x,y);
					}
				}
			}
		}
	}
	
	public void drawTowerHover(Graphics g) {
		if (selectedTower != -1)
		{
			int[] myCoord = getTileCoordinates();
			int x = myCoord[0];
			int y = myCoord[1];
			if (y<MAPHEIGHT && !map[y/TILEHEIGHT][x/TILEWIDTH].isOccupied()) {
				int radius = defaultTowerRange[selectedTower]*2 -5;
				g.setLineWidth(2);
				if (currentTile.isInvalid()) {
					colorAlpha = new Color(1.0f,0,0,.2f);
					g.setColor(Color.red);
					g.drawRect(x, y, TILEWIDTH, TILEHEIGHT);
				}else{
					colorAlpha = new Color(1f,1f,1f,.2f);
				}
				//draw rangeCircle
				g.setColor(colorAlpha);
				g.fillOval(x+TILEWIDTH/2-radius/2, y+TILEHEIGHT/2-radius/2, radius, radius);
				g.drawImage(resource.getImage("Tower"+Integer.toString(selectedTower)),x,y);
				if (selectedTower != 1) {
					
					g.drawImage(turretButtonImg, x, y);
				}
			}
		}
		if (editTower != -1)
		{
			Tile tileOfTower = editTowerTower.tile;
			int x = tileOfTower.getColumn()*TILEWIDTH;
			int y = tileOfTower.getRow()*TILEHEIGHT;
			int radius = editTowerTower.getRange()*2 -5;
			colorAlpha = new Color(1f,1f,1f,.2f);
			g.setColor(colorAlpha);
			g.fillOval(x+TILEWIDTH/2-radius/2, y+TILEHEIGHT/2-radius/2, radius, radius);
		}
	}

	
	public int[] getTileCoordinates() {
		int x = (int) Math.floor(mouseX/TILEWIDTH);
		int y = (int) Math.floor(mouseY/TILEHEIGHT);
		int[] myCoord = {x*TILEWIDTH,y*TILEHEIGHT};
		return myCoord;
	}
	
	public void drawExplosions(Graphics g) {
		for (int i = 0; i < explosionList.size(); i++) {
			for (int j = 0; j < explosionList.get(i).numParticles; j++) {
				explosionList.get(i).count++;
				explosionList.get(i).particleList[j].Move();
				int px = explosionList.get(i).particleList[j].x;
				int py = explosionList.get(i).particleList[j].y;
				int width = explosionList.get(i).particleList[j].width;
				int height = explosionList.get(i).particleList[j].height;
				Color c = explosionList.get(i).particleList[j].pColor;
				g.setColor(c);
				g.fillOval(px, py, width, height);
				if (explosionList.get(i).count >= 500) {
					explosionList.remove(i);
					break;
				}
			}
		}
	}
	
	public void drawHearts(Graphics g, int startX, int startY) {
		for (int i = 1; i <= lives; i++) {
			g.drawImage(resource.getImage("heart"),startX-(resource.getImage("heart").getWidth()+5)*i,startY);
		}
	}
	
	public void drawBullets(Graphics g) {
		for (int i = 0; i < bulletList.size(); i++) {
			if (!bulletList.get(i).remove && bulletList.get(i).getTower() != null) {
				bulletList.get(i).getTower().moveBullet();
				g.setColor(Color.white);
				g.fillRect(bulletList.get(i).x, bulletList.get(i).y, 4, 4);
			}else{
				explosionList.add(new Explosion((int) bulletList.get(i).x+TILEWIDTH/2,(int) bulletList.get(i).y+TILEHEIGHT/2, "Yellow"));
				bulletList.remove(i);
			}
		}
		for (int i = 0; i < lightningTowers.size(); i++) {
			if (lightningTowers.get(i).getTower() != null) {
				if (!lightningTowers.get(i).getTower().shooting) {
					lightningTowers.get(i).shootBullet();
				}
			}else{
				lightningTowers.remove(i);
			}
		}
	}
	
	public void drawWaveBlocks(Graphics g) {
		for (int i = 0; i < waveList.size(); i++) {
			waveBlock current = waveList.get(i);
			float waveX = current.getX();
			float waveY = current.getY();
			//we get a weird blinking when the block is removed, make bg color the same when this happens. workaround
			g.setColor(Color.gray);
			g.setLineWidth(2);
			g.drawImage(resource.getImage("waveBar"), waveX, waveY);
			//actual block
			g.setLineWidth(2);
			g.drawImage(resource.getImage("waveBar"), waveX, waveY);
			g.drawRect(waveX, waveY, current.getWidth(), current.getHeight());
			
			if (waveNum == current.getWaveNum()) {
				g.setColor(Color.orange);
			}else{
				g.setColor(Color.white);
			}
			String waveText = "Wave "+current.getWaveNum();
			g.drawString(waveText, waveX+current.getWidth()/3, waveY+6);
			if (gameStart && waveX >= 0)
				current.setX(waveX-waveBlockSpeed);
			if(waveX < 0) {
				overBlock = true;
				waveNum = current.getWaveNum();
				current.setY(waveY+waveBlockSpeed*4);
			}
			if (waveY > MAPHEIGHT + current.getHeight()+2) {//most of its gone
				currentCreepSelection++;
				if (currentCreepSelection > NUMCREEPS)
					currentCreepSelection = 1;
				waveList.remove(i);
				break;
			}
		}
	}
	
	//public boolean isOverArea(int mouseX, int mouseY, int x, int y, int width, int height)
	public void buttonHandler(Graphics g) {
		if (buttonMap.get("buyButton").isOverArea() && editTower != -1 && clickUp && (money-editTowerTower.getValue())>=0) {
			editTowerTower.newUpgrade();
			int upgradeCost = editTowerTower.getUpgradeCost();
			money -= upgradeCost;
			clickUp = false;
		}
		
		if (buttonMap.get("sellButton").isOverArea() && editTower != -1 && clickUp) {
			int sellCost = editTowerTower.getValue();
			money += sellCost;
			clickUp = false;
			editTowerTower.tile.unsetTower();
			nullEditTower();
			updateCreepPaths();
			getPath(startX,startY);
		}
		
		if (buttonMap.get("gui").isOverArea()) { 
			//if not on map dont hightlight a square
			changedTile = fakeTile;
			currentTile = fakeTile;
		} else { // update square change
			if (changedTile.getID() != currentTile.getID()) {
				if (click) {
					tileChange = true;
				}
				currentTile = changedTile;
			} else {
				if (click) {
					tileChange = false;
				}
			}
		}
	}
	
	public void drawGUI(Graphics g) throws SlickException {
		int waveBar = 25;
		int startOfMenu = MAPHEIGHT+waveBar;
		int menuHeight = BOTTOMGUIHEIGHT-25;
		//some variables
		int padding = 10; // padding for tower stuff
		int towerStuffOffset = 7; // offset for tower stuff
		int guiMiddleY = startOfMenu + menuHeight / 2;
		
		//Wave bar ----------------------------------WAVE BAR--------------------------------------WAVE BAR
		g.setColor(new Color(0x202020)); //the bar behind the blocks
		g.setLineWidth(1);
		g.fillRect(0, MAPHEIGHT, WIDTH, waveBar);
		g.drawImage(resource.getImage("waveGUI"), 0, MAPHEIGHT);
		g.setFont(font.get());
		drawWaveBlocks(g);
		
		//-------------------------------------------------------------------------------------------------
		g.setLineWidth(2);
		//actual bottom box container
		g.setColor(new Color(0x545454));
		g.drawRect(0, startOfMenu, WIDTH, menuHeight);
		//g.drawImage(resource.getImage("bottomGUI"), 0, startOfMenu);
		g.setColor(Color.black);
		g.fillRect(0, startOfMenu, WIDTH, menuHeight);
		
		
		
		//money/data info section -------------------------------------------------------------
		g.setFont(font.get());
		g.setColor(Color.white);
		g.drawString("Money: $"+money, 20-padding, startOfMenu+3);
		drawHearts(g, 45 * NUMTOWERS-20+padding*2 +20-padding, startOfMenu+6);
		
		
		
		//behind tower button section ---------------------------------------------------
		g.setColor(new Color(0x212121));
		//x,y,width,height, corner radius
		g.fillRoundRect(20-padding, (guiMiddleY - TILEHEIGHT/2 - padding + towerStuffOffset), 45 * NUMTOWERS-20+padding*2, TILEHEIGHT+padding*2, 3);
		g.setColor(new Color(0x545454));
		g.drawRoundRect(20-padding, (guiMiddleY - TILEHEIGHT/2 - padding +towerStuffOffset), 45 * NUMTOWERS-20+padding*2, TILEHEIGHT+padding*2, 3);
		
		//tower button section
		for (int i = 0; i < NUMTOWERS; i++) {
				String img = new Tower(i).getImageString();
				Image image = new Image(img);
				int towerX = 45 * i + 20;
				int towerY = guiMiddleY - TILEHEIGHT/2;
				//draw selector around select tower
				if (selectedTower == i) {
					g.setColor(Color.yellow);
					g.drawRect(towerX-1, towerY-1+towerStuffOffset,TILEWIDTH + 1, TILEHEIGHT + 1);
				}
				//draw image, x offset of 20 with 80 in between.
				g.drawImage(resource.getImage("Tower"+Integer.toString(i)), towerX, towerY+towerStuffOffset );
				if (i != 1)
					g.drawImage(turretButtonImg, towerX, towerY+towerStuffOffset);
				towerButtonPos[i][0] = towerY;
				towerButtonPos[i][1] = towerX;
		}
		
		
		
		//tower description box ----------------------------------------------------
		padding = 7;
		int offSet = 20;
		g.setColor(new Color(0x212121));
		//x,y,width,height, corner radius
		int leftX = WIDTH/2 - offSet;
		int topY = startOfMenu+padding;
		g.fillRoundRect(leftX, topY, WIDTH/2-padding+offSet, menuHeight-padding*2, 5);
		g.setColor(new Color(0x545454));
		g.drawRoundRect(leftX, topY, WIDTH/2-padding+offSet, menuHeight-padding*2, 5);
		
		padding = 8;
		int xPadding = 12;
		String towerText = "No tower selected";
		if (selectedTower != -1 && editTower == -1) 
		{
			int price = defaultTowerCost[selectedTower];
			g.drawImage(resource.getImage("Tower"+selectedTower), leftX+xPadding, topY+padding);
			g.drawImage(turretButtonImg, leftX+xPadding, topY+padding);
			towerText = descriptions[selectedTower];
			g.setColor(Color.white);
			g.drawString(towerText, leftX+xPadding*2+4+TILEWIDTH, topY+padding);
			g.drawString("$"+price, leftX+padding+3, topY+padding*2+TILEHEIGHT);
		}
		else if (editTower != -1 && selectedTower == -1) 
		{
			int upgradeCost = editTowerTower.getUpgradeCost();
			int sellCost = editTowerTower.getValue();
			int type = editTowerTower.getType();
			Tower tower = editTowerTower;
			g.drawImage(resource.getImage("Tower"+type), leftX+xPadding, topY+padding);
			g.drawImage(turretButtonImg, leftX+xPadding, topY+padding);
			g.setColor(new Color(0x3B3B3B));
			g.fillRoundRect(leftX+padding*14+4+TILEWIDTH, topY+padding/2, WIDTH/4-padding*2+offSet, menuHeight-padding*3,3);
			//textual upgrade stuff
			g.setColor(Color.white);
			g.drawString("Damage: "+tower.getDamage()+
						"\nRange: "+tower.getRange()+
						"\n$"+sellCost+" [ Sell ]", leftX+padding*2+4+TILEWIDTH, topY+padding);
			g.drawString("Upgrade: $"+upgradeCost+"   [ Buy ]\n" +
						 "Damage: "+Math.floor(tower.getDamage()*1.5)+"\n"+
						 "Range: "+Math.floor(tower.getRange()*1.5), leftX+padding*15+4+TILEWIDTH, topY+padding);
		}
	}
	
	
	//text effect
	
	public void goldListHandler(Graphics g) {
		for (int i = 0; i < goldList.size(); i++) {
			goldList.get(i).y--;
			goldList.get(i).fadeOut();
			int fadeAmount = goldList.get(i).fade;
			int amount = goldList.get(i).amount;
			int x = goldList.get(i).x;
			int y = goldList.get(i).y;
			g.setColor(new Color(255,247,0,fadeAmount));
			if (goldList.get(i).type == 1) {
				g.drawString("+ $"+Integer.toString(amount), x, y);
			}else{
				g.setColor(new Color(255,0,0,fadeAmount));
				g.drawString("-1 Lives", x, y);
			}
		}
	}
	
	public boolean checkMoney(int amount) {
		if (money-amount < 0) {
			return false;
		}else{
			return true;
		}
	}
	
	public void waterBridge(int locationX, int opening) {
		int locationY = 0;
		int width = 3;
		//min = 2;
		if (width < 2)
			width = 2;
		for (int rows = 0; rows < MAPROWS; rows++) {
			for (int cols = 0; cols < width; cols++) {
				int type = 5;
				if (cols == 0) {
					if (rows == opening-1) {
						type = 8;
					}else
					if (rows == opening+1) {
						type = 2;
					}else{
						type = 5;
					}
				} else 
				if (cols == width-1) {
					if (rows == opening-1) {
						type = 10;
					}else
					if (rows == opening+1) {
							type = 4;
						
					}else{
						type = 7;
					}
				}else { //middle
						if (rows == opening-1) {
							type = 9;
						}else
						if (rows == opening+1) {
								type = 3;
							
						}else{
							type = 6;
						}
				}
					
				if (rows == opening) {
					type = 0;
				}
				
				map[locationY+rows][locationX+cols].setType(type);
			}
		}
	}
	
	public void waterBlock(int locationX, int locationY, int ex) {
		int count = 0;
		int size = 2+ex;
		//size min is 2
		if (size < 2)
			size = 2;
		for (int rows = 0; rows < size; rows++) {
			for (int cols = 0; cols < size; cols++) {
				int type = 2;
				//corners
				if (rows == 0 && cols == 0) // ul
					type = 2;
				else
				if (rows == size-1 && cols == 0) // bl
					type = 8;
				else
				if (rows == 0 && cols == size-1) // ur
					type = 4;
				else
				if (rows == size-1 && cols == size-1) // br
					type = 10;
				else
				if (rows == 0)  //um
					type = 3;
				else 
				if (rows == size-1) //bm
					type = 9;
				else
				if (cols == 0) //ml
					type = 5;
				else
				if (cols == size-1) //mr
					type = 7;
				else
					type = 6;
				
				map[locationY+rows][locationX+cols].setType(type);
			}
		}
	}
	
	public void reset() {
		lives = 3;
		money = 100;
		lives = 3;
		waveNum = -1;
		for (int i = 0; i < creepList.size(); i++) {
			creepList.remove(i);
		}
		for (int i = 0; i < waveList.size(); i++) {
			waveList.remove(i);
		}
		for (int i = 0; i < goldList.size(); i++) {
			goldList.remove(i);
		}
	}
	
}
