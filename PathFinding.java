package javaGame;
import java.util.ArrayList;
import java.util.Stack;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class PathFinding extends gameInfo {
	Tile maze[][] = new Tile[MAPROWS][MAPCOLS];

	ArrayList<Integer> currentPos = new ArrayList<Integer>();
	ArrayList<Integer> startPos = new ArrayList<Integer>();
	ArrayList<Integer> finishPos = new ArrayList<Integer>();

	Stack<ArrayList<Integer>> pathStack = new Stack<ArrayList<Integer>>(); // x and y at each step
	int moves = 0;
	
	public PathFinding(Tile map[][]) {
		finishPos.add((MAPROWS-1)/2);
		finishPos.add(MAPCOLS-1);
		startPos.add(0);
		startPos.add(0);
		init(map);
		if (map[0][0].isOccupied()) { 
			//if for some reason our starting point is on a wall (put a tower right on creep)
			pathStack = null;
		}else{
			move(startPos.get(1),startPos.get(0),moves);
		}
	}
	
	public PathFinding(Tile map[][], int startX, int startY, int finishX, int finishY) {
		finishPos.add(finishY);
		finishPos.add(finishX);
		startPos.add(startY);
		startPos.add(startX);
		init(map);
		if (map[startY][startX].isOccupied()) { 
			//if for some reason our starting point is on a wall (put a tower right on creep)
			pathStack = null;
		}else{
			move(startPos.get(1),startPos.get(0),moves);
		}
	}
	
	public void init(Tile map[][]) {
		maze = map;
		resetPathAndVisited();
		currentPos = (ArrayList<Integer>) startPos.clone();
		pathStack.push((ArrayList<Integer>) startPos.clone());
		//pathStackPrevious.push(fakePos);
		pathStack.add(currentPos);
		maze[currentPos.get(0)][currentPos.get(1)].pathId = 0;
	}
	
	void printMap() {
		
		System.out.println("\n");
		for (int y = 0; y < MAPROWS; y++) {
			for(int x = 0; x < MAPCOLS; x++) {
				 if (maze[y][x].isOccupied()) {
					 System.out.print("X,");
				 }else {
					 System.out.print(maze[y][x].pathId+",");
				 }
			}
			System.out.println("");
		}
	}

	public boolean checkValidSpace(int x, int y) {
		boolean returnType = false;
		if (x < 0 || y < 0 || y >= MAPROWS || x >= MAPCOLS || maze[y][x].isOccupied() || (maze[y][x].visited)) {
			returnType = false;
		}else{
			returnType = true;
		}
		return returnType;
	}

	public ArrayList<Integer> xyChange(int direction, int originalX, int originalY) {
		ArrayList<Integer> temp = new ArrayList<Integer>(2);
		temp.add(originalY); temp.add(originalX);
		if (direction == 0) { // up
				temp.set(0, originalY-1);
			}else if (direction == 1) { // right
				temp.set(1, originalX+1);
			}else if (direction == 2) { // down
				temp.set(0, originalY+1);
			}else if (direction == 3) { // left
				temp.set(1, originalX-1);
		}
		return temp;
	}

	public void move(int x, int y, int moves) {
		Stack<ArrayList<Integer>> secondaryPath = new Stack<ArrayList<Integer>>();
		while(!pathStack.isEmpty()) {
			int cx = pathStack.peek().get(1);
			int cy = pathStack.pop().get(0);
			maze[cy][cx].visited=true;
			int moveCount = maze[cy][cx].pathId;
			ArrayList<Integer> up = xyChange(0,cx,cy);
			ArrayList<Integer> right = xyChange(1,cx,cy);
			ArrayList<Integer> down = xyChange(2,cx,cy);
			ArrayList<Integer> left = xyChange(3,cx,cy);
			if (checkValidSpace(up.get(1),up.get(0)) && maze[up.get(0)][up.get(1)].pathId == -1) {
				maze[up.get(0)][up.get(1)].pathId = moveCount+1;
				secondaryPath.push(up);
			}
			if (checkValidSpace(right.get(1),right.get(0)) && maze[right.get(0)][right.get(1)].pathId == -1) {
				maze[right.get(0)][right.get(1)].pathId = moveCount+1;
				secondaryPath.push(right);
			}
			if (checkValidSpace(down.get(1),down.get(0)) && maze[down.get(0)][down.get(1)].pathId == -1) {
				maze[down.get(0)][down.get(1)].pathId = moveCount+1;
				secondaryPath.push(down);
			}
			if (checkValidSpace(left.get(1),left.get(0)) && maze[left.get(0)][left.get(1)].pathId == -1) {
				maze[left.get(0)][left.get(1)].pathId = moveCount+1;
				secondaryPath.push(left);
			}
			if (pathStack.isEmpty()) {
				pathStack = (Stack<ArrayList<Integer>>) secondaryPath.clone();
				while(!secondaryPath.isEmpty()) {
					secondaryPath.pop();
				}
			}
		}
		
		for (int ay = 0; ay < MAPROWS; ay++) {
			for(int ax = 0; ax < MAPCOLS; ax++) {
				maze[ay][ax].path = false; //resets * every time
				maze[ay][ax].visited = false;
			}
		}
		//we have all the tiles numbered, go back and find the shortest path
		int curX = finishPos.get(1); int curY = finishPos.get(0);
		if (maze[curY][curX].pathId != -1) { //blocked Path check
			while(curX != startPos.get(1) || curY != startPos.get(0)) {
				currentPos.set(0, curY);
				currentPos.set(1, curX);
				pathStack.push((ArrayList<Integer>) currentPos.clone());
				maze[currentPos.get(0)][currentPos.get(1)].path=true;
				int smallest = maze[currentPos.get(0)][currentPos.get(1)].pathId+4;
				ArrayList<Integer> up = xyChange(0,curX,curY);
				ArrayList<Integer> right = xyChange(1,curX,curY);
				ArrayList<Integer> down = xyChange(2,curX,curY);
				ArrayList<Integer> left = xyChange(3,curX,curY);
				
				if (checkValidSpace(up.get(1),up.get(0)) && maze[up.get(0)][up.get(1)].pathId < smallest) {
					smallest = maze[up.get(0)][up.get(1)].pathId;
					curX = up.get(1); curY = up.get(0);
				}
	
				if (checkValidSpace(right.get(1),right.get(0)) && maze[right.get(0)][right.get(1)].pathId < smallest) {
					smallest = maze[right.get(0)][right.get(1)].pathId;
					curX = right.get(1); curY = right.get(0);
				}
				
				if (checkValidSpace(down.get(1),down.get(0)) && maze[down.get(0)][down.get(1)].pathId < smallest) {
					smallest = maze[down.get(0)][down.get(1)].pathId;
					curX = down.get(1); curY = down.get(0);
				}
				
				if (checkValidSpace(left.get(1),left.get(0)) && maze[left.get(0)][left.get(1)].pathId < smallest) {
					smallest = maze[left.get(0)][left.get(1)].pathId;
					curX = left.get(1); curY = left.get(0);
				}
			}
		}else{
			pathStack = null;
		}
	}
	
	public void resetPathAndVisited() {
		for (int y = 0; y < MAPROWS; y++) {
			for(int x = 0; x < MAPCOLS; x++) {
				maze[y][x].path = false; //resets * every time
				maze[y][x].visited = false;
				maze[y][x].pathId = -1;
			}
		}
	}
	
	public boolean checkIfFinished(int x, int y) {
		if(pathStack.isEmpty() || (x == finishPos.get(1) &&  y == finishPos.get(0))) {
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isEmpty() {
		if (pathStack == null) {
			return true;
		}else{
			return false;
		}
	}
	
	public Stack<ArrayList<Integer>> getPath() {
		return pathStack;
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
