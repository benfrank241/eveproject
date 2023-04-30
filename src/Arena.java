import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;

public class Arena {
	
	public static boolean demo = false;
	
	public static char WALL = 'w'; //character used in arena to represent walls
	public static char EMPTY = ' '; //character used in arena to represent empty spaces
	public static char TARGET = '*'; //character used in arena to represent targets
	
	private static boolean visualize = true; 
	public static boolean randomInitialLocation = true;
	public static int testDuration = 300;//number of steps we act for during a single test
	public static int GOT_TARGET = 10; //there was a target on our space and we picked it up
	public static int HIT_WALL = -5; //went to move and ran into a wall
	public static int SITUP = -1; //went to pick up target but there was none to pickup
	
	public static boolean bunchofwalls = false;
	
	private int height; //arena height, i.e. rows, including wall rows
	private int width; //arena width, i.e. cols, including wall cols
	public int targetCount; //probabilistic but this way we know how many we had
	private char[][] field;
	
	private int currentRow; // robot's current row in the arena
	private int currentCol; // robot's current col in the arena
	private int currentScore; // robot's current score (using GOT_TARGET, HIT_WALL, and SITUP)
	
	public static int startingRow; //where to begin each test
	public static int startingCol; //where to begin each test
	
	public Arena (int h, int w) {
		
		this.height = h;
		this.width = w; 
		this.field = new char[this.height][this.width];
		
		//perimeter walls: top and bottom
		for (int c=0; c<this.width; c++) {
			this.field[0][c] = WALL;
			this.field[this.height-1][c]=WALL;
		}
		//perimeter walls: left and right
		for (int r=0; r<this.height; r++) {
			this.field[r][0]=WALL;
			this.field[r][this.width-1]=WALL;
		}
		
		reset();
	}
	
	public void reset() {
		
		// if we don't want random walls throughout, put targets in each cell with 50/50 chance
		if (!bunchofwalls) {
			this.targetCount=0;
			Random rand = new Random();
			for (int r=1; r<this.height-1; r++) {
				for (int c=1; c<this.width-1; c++) {
					if (rand.nextBoolean()) {
						this.field[r][c]=TARGET;
						this.targetCount++;
					}
					else this.field[r][c]=EMPTY;
				}
			}
		}
		else { //10% walls, 45% targets, rest empty
			this.targetCount=0;
			Random rand = new Random();
			for (int r=1; r<this.height-1; r++) {
				for (int c=1; c<this.width-1; c++) {
					if (rand.nextDouble()<0.1) //10% chance for wall
						this.field[r][c]=WALL;
					else if (rand.nextBoolean()) {//45% chance for target
						this.field[r][c]=TARGET;
						this.targetCount++;
					}
					else this.field[r][c]=EMPTY;
				}
			}
		}
	}
	
	public char[][] getField() {
		return this.field;
	}
	
	public void setField(char[][] field) {
		this.field = field;
	}
	
	public String toString() {//we can use this to "draw" our arena 
		String str = "";
		for (int r=0; r<this.height; r++) {
			for (int c=0; c<this.width; c++) {
				if (r==this.currentRow && c==this.currentCol) {
					if (this.field[r][c] == TARGET)//agent and target are here
						str += '@' + " ";
					else //just agent is here
						str += 'A' + " ";
				}
				else
					str += this.field[r][c] + " ";//adding space to accommodate for vertical line spacing
			}
			str += "\n";
		}
		return str;
	}
	
	public double testChromo(Chromo chromo) {
		Random rand = new Random();

		// Initialize the score to 0
		this.currentScore = 0;
		
		// Set the robot's initial location to a random empty space
		do{
			currentRow = rand.nextInt(this.height-2)+1;
			currentCol = rand.nextInt(this.width-2)+1;
		}while(this.field[currentRow][currentCol] == WALL);

		

	
		// Move arounD the arena for testDuration steps
		for (int step = 0; step < testDuration; step++) {

			// Assess the situation
			String situation = "";
			int[] dRow = {-1, 1, 0, 0};
			int[] dCol = {0, 0, 1, -1};
	
			for (int i = 0; i < 4; i++) {
				int newRow = currentRow + dRow[i];
				int newCol = currentCol + dCol[i];
	
				if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
					situation += field[newRow][newCol];
				} else {
					situation += WALL;
				}
			}
	
			situation += field[currentRow][currentCol];
	
			// Find the situation index in the policy map
			int situationIndex = Chromo.policyMap.get(situation);
	
			// Get the corresponding action from the chromo's policy
			int action = chromo.getAction(situationIndex);
	
			// Perform the action
			switch (action) {
				case 0: // Move North
					move(-1, 0);
					break;
				case 1: // Move South
					move(1, 0);
					break;
				case 2: // Move East
					move(0, 1);
					break;
				case 3: // Move West
					move(0, -1);
					break;
				case 4: // Stay Put
					break;
				case 5: // Pick Up Target
					pickup();
					break;
				case 6: // Move Random
					move(rand.nextInt(3) - 1, rand.nextInt(3) - 1);
					break;
				default:
					break;
			}
		}
		// Return the total score earned
		return this.currentScore;
	}
	

	
	
	public boolean move(int deltaRow, int deltaCol) {

		// Calculate the new positions
		int newRow = currentRow + deltaRow;
		int newCol = currentCol + deltaCol;
	
		// Check if newRow and newCol are within the bounds of the field array
		if (newRow >= 0 && newRow < field.length && newCol >= 0 && newCol < field[0].length) {
			// Check if the new position is not a WALL
			if (field[newRow][newCol] != Arena.WALL) {
				// Move the robot to the new position
				currentRow = newRow;
				currentCol = newCol;
				return true;
			} else {
				// Apply the HIT_WALL penalty if the new position is a WALL
				currentScore += HIT_WALL;
			}
		}
	
		return false;
	}
	
	
	public boolean pickup() {
		// If there is a TARGET at the current location
		if (field[currentRow][currentCol] == TARGET) {	
			// Replace the target with an EMPTY space
			field[currentRow][currentCol] = EMPTY;
			// Incur a gain called GOT_TARGET
			currentScore += GOT_TARGET;
			// Return true to indicate pickup success
			return true;
		} else {
			// If there is no TARGET at the current location
			// Incur a penalty called SITUP
			currentScore += SITUP;
			// Return false to indicate pickup failure
			return false;
		}
	}
	
}
