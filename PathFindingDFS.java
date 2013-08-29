package javaGame;
import java.util.ArrayList;
import java.util.Stack;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class PathFindingDFS extends gameInfo {
	Tile maze[][] = new Tile[MAPROWS][MAPCOLS];

	ArrayList<Integer> currentPos = new ArrayList<Integer>();
	ArrayList<Integer> startPos = new ArrayList<Integer>();
	ArrayList<Integer> finishPos = new ArrayList<Integer>();

	Stack<Integer> directionOptions = new Stack<Integer>();

	Stack<ArrayList<Integer>> pathStack = new Stack<ArrayList<Integer>>(); // x and y at each step
	Stack<Stack<Integer> > directionStack = new Stack<Stack<Integer>>(); // 0-3 up, right, down, left
	int moves = 0;
	int horizontalChange = 0;
	int verticalChange = 0;
	
	public PathFindingDFS(Tile map[][]) {
		finishPos.add((MAPROWS-1)/2);
		finishPos.add(MAPCOLS-1);
		startPos.add(0);
		startPos.add(0);
		init(map);
	}
	
	public PathFindingDFS(Tile map[][], int startX, int startY, int finishX, int finishY) {
		finishPos.add(finishY);
		finishPos.add(finishX);
		startPos.add(startY);
		startPos.add(startX);
		init(map);
	}
	
	public PathFindingDFS(Tile map[][], int startX, int startY, int finishX, int finishY, int hc, int vc) {//for recursion
		
		finishPos.add(finishY);
		finishPos.add(finishX);
		startPos.add(startY);
		startPos.add(startX);
		init(map);
		horizontalChange = hc;
		verticalChange = vc;
	}
	
	public void init(Tile map[][]) {
		maze = map;
		directionOptions.push(0);
		directionOptions.push(1);
		directionOptions.push(2);
		directionOptions.push(3);
		fillDirectionStack();
		currentPos = (ArrayList<Integer>) startPos.clone();
		horizontalChange = Math.abs(startPos.get(1)-finishPos.get(1));
		verticalChange = Math.abs(startPos.get(0)-finishPos.get(0));
		pathStack.push((ArrayList<Integer>) startPos.clone());
		ArrayList<Integer> fakePos = new ArrayList<Integer>(2);
		fakePos.add(-1); fakePos.add(-1);
		//pathStackPrevious.push(fakePos);
		resetPathAndVisited();
	}


	public void fillDirectionStack() {
		Stack<Integer> directionOptions = (Stack<Integer>) this.directionOptions.clone();
		directionStack.push(directionOptions);
	}
	
	void printMap() {
		//add our path
		Stack<ArrayList<Integer>> tempPath = new Stack<ArrayList<Integer>>();
		tempPath = (Stack) pathStack.clone();
		while(!tempPath.isEmpty()) {
			int pathX = tempPath.peek().get(1);
			int pathY = tempPath.peek().get(0);
			maze[pathY][pathX].path = true;
			tempPath.pop();
		}
		
		System.out.println("\n");
		for (int y = 0; y < MAPROWS; y++) {
			for(int x = 0; x < MAPCOLS; x++) {
				 if (maze[y][x].isOccupied()) {
					 System.out.print("X");
				 }else if(maze[y][x].path) {
					 System.out.print("*");
				 }else{
					 System.out.print("O");
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
		temp.add(0); temp.add(0);
		if (direction == 0) { // up
				temp.set(0, currentPos.get(0)-1);
				temp.set(1, currentPos.get(1));
			}else if (direction == 1) { // right
				temp.set(0, currentPos.get(0));
				temp.set(1, currentPos.get(1)+1);
			}else if (direction == 2) { // down
				temp.set(0, currentPos.get(0)+1);
				temp.set(1, currentPos.get(1));
			}else if (direction == 3) { // left
				temp.set(0, currentPos.get(0));
				temp.set(1, currentPos.get(1)-1);
		}
		return temp;
	}

	public void move() {
		//printMap();
		boolean validDirection = false;
		int direction = -1;
		int lowestF = 1000;
		int shortestDirection = -1;
		ArrayList<Integer> nextMove = new ArrayList<Integer>();
		Stack<Integer > shortestDistanceDirectionStack = new Stack<Integer>();
		while (!directionStack.peek().isEmpty()) {
				direction = directionStack.peek().peek();
				directionStack.peek().pop();
				//if we just moved from left to right, dont just left we already came from there
				nextMove = xyChange(direction,currentPos.get(1),currentPos.get(0));
				if (checkValidSpace(nextMove.get(1),nextMove.get(0))) {
					int f = findF(nextMove.get(1),nextMove.get(0));
					if (f<lowestF) {
						lowestF = f;
						shortestDirection = direction;
					}
					shortestDistanceDirectionStack.push(direction);
					validDirection = true;
				}
		}
		while(!shortestDistanceDirectionStack.isEmpty()) {
			if (shortestDistanceDirectionStack.peek() == shortestDirection) { //if we using it dont add
				shortestDistanceDirectionStack.pop();
			}else{
				directionStack.peek().push(shortestDistanceDirectionStack.pop()); 
				//add the directions not used onto stack after its empty
			}
		}
		
		maze[currentPos.get(0)][currentPos.get(1)].visited = true;
		if (validDirection) {
			nextMove = xyChange(shortestDirection,currentPos.get(1),currentPos.get(0));
			moveForward(nextMove.get(1),nextMove.get(0));
		}else{
			maze[pathStack.peek().get(0)][pathStack.peek().get(1)].path=false;
			pathStack.pop();
			if (!checkIfFinished(currentPos.get(1),currentPos.get(0))) {
			moveBackward(pathStack.peek().get(1),pathStack.peek().get(0));
			}
		}
	}

	int findF(int x, int y) {
		int h;
		int xChange = Math.abs(x-(finishPos.get(1)));
		int yChange = Math.abs(y-(finishPos.get(0)));
		//if (xChange == yChange) {//decide who to take precedence over when equal
			if (verticalChange >= horizontalChange) {
				h = Math.abs(y-(finishPos.get(0)));
			}else{
				h = Math.abs(x-(finishPos.get(1)));
			}
		int g = moves;
		return g+h;
	}

	public void moveBackward(int x, int y) {
		moves++;
		//textHelp();

		currentPos.set(0,y);
		currentPos.set(1,x);
		//add new Stack for new spot (new choices of movements)
		directionStack.pop();
		if (!checkIfFinished(x, y)) {
			move();
		}
	}
		

	public void moveForward(int x, int y) {
		moves++;
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp = (ArrayList<Integer>) currentPos.clone();
		//textHelp();
		currentPos.set(0,y);
		currentPos.set(1,x);
		temp = (ArrayList<Integer>) currentPos.clone();
		//add new Stack for new spot (new choices of movements)
		fillDirectionStack();
		pathStack.push(temp);
		if (!checkIfFinished(x, y)) {
			move();
		}
	}

	public boolean checkIfFinished(int x, int y) {
		if(pathStack.isEmpty() || (x == finishPos.get(1) &&  y == finishPos.get(0))) {
			return true;
		}else{
			return false;
		}
	}
	
	public void shortenPath() {
		int numEmptyPoints = 0;
		do {
			resetPathAndVisited();
			Stack<ArrayList<Integer>> tempPathStack = (Stack<ArrayList<Integer>>) pathStack.clone();
			Stack<ArrayList<Integer>> openPoint = new Stack<ArrayList<Integer>>();
			Stack<ArrayList<Integer>> afterPoint = new Stack<ArrayList<Integer>>();
			int emptyPoints = 0;
			while(!tempPathStack.isEmpty())	{
				int numSurroundingPath = 0;
				int x = tempPathStack.peek().get(1);
				int y = tempPathStack.peek().get(0);
				if (y>0) {
				if (maze[y-1][x].path) {
					numSurroundingPath++;
				}
				}
				if(y<MAPROWS-1) {
				if (maze[y+1][x].path) {
					numSurroundingPath++;
				}
				}
				if(x>0){
				if (maze[y][x-1].path) {
					numSurroundingPath++;
				}
				}
				if(x<MAPCOLS-1){
				if (maze[y][x+1].path) {
					numSurroundingPath++;
				}
				}
				if (numSurroundingPath > 2) {
					emptyPoints++;
					int finishX = tempPathStack.peek().get(1);
					int finishY = tempPathStack.peek().get(0);
					PathFindingDFS p = new PathFindingDFS(maze.clone(),startPos.get(1), startPos.get(0), finishX, finishY, horizontalChange, verticalChange);
					p.move();
					tempPathStack.pop();
					Stack<ArrayList<Integer>> fixedOpenPoint = p.getMapStack();
					while(!afterPoint.isEmpty()) {
						fixedOpenPoint.push(afterPoint.pop());
					}
					tempPathStack = (Stack<ArrayList<Integer>>) fixedOpenPoint.clone();
					pathStack = (Stack<ArrayList<Integer>>) fixedOpenPoint.clone();
				}else{
					afterPoint.push(tempPathStack.pop());
				}
			}
			numEmptyPoints = emptyPoints;
			resetPathAndVisited();
		}while(numEmptyPoints != 0);
	}
	
	public void resetPathAndVisited() {
		for (int y = 0; y < MAPROWS; y++) {
			for(int x = 0; x < MAPCOLS; x++) {
				maze[y][x].path = false; //resets * every time
				maze[y][x].visited = false;
			}
		}
		
		Stack<ArrayList<Integer>> tempPath = new Stack<ArrayList<Integer>>();
		tempPath = (Stack) pathStack.clone();
		while(!tempPath.isEmpty()) {
			int pathX = tempPath.peek().get(1);
			int pathY = tempPath.peek().get(0);
			maze[pathY][pathX].path = true;
			tempPath.pop();
		}
	}
	
	public Stack<ArrayList<Integer>> getMapStack() {
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
