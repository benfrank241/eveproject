import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;



public class Chromo  implements Comparable<Chromo>{
	
	public int chromoID;
	private int[] policy;
	private double fitness;
	
	public String name;//used for tournaments
	public double cumulativeFitness;//used for tournaments
	
	public static int vision = 5; //5: N,S,E,W,Current; 9:NW,NE,SW,SE,N,S,E,W,Current
	public static int length= 243;//(int)(Math.pow(3, vision));
	private static int actions= 7; //including 0
		//(for vision 5)
		//0 = MoveNorth, 1 = MoveSouth, 2 = MoveEast, 3 = MoveWest
		//4 = StayPut, 	5 = PickUpTarget, 	6 = MoveRandom
		//(for vision 9)
		//7 = move NorthWest, 8 = move NorthEast, 9 = move SouthWest, 10 = move SouthEast

	private static double mutationAmount = 0.05;
	private static double crossoverAmount = 0.5;
	private static int totalChromos=0;
	public static int testingSessions = 100;
	public static int arenaSize = 10;// arenaSize x arenaSize, not counting walls
	
	public static HashMap<String, Integer> policyMap = null; //maps situations to policy indices
	
	public Chromo() { 
		Random rand = new Random();
		this.policy = new int[Chromo.length];
		for (int c=0; c<Chromo.length; c++) {
			this.policy[c] = rand.nextInt(Chromo.actions);
		}
		//System.out.println(Arrays.toString(this.policy));
		this.fitness=-1;
		this.chromoID = Chromo.totalChromos;
		Chromo.totalChromos++;
		
		if (policyMap==null) {
			setupPolicyMap();
		}
	}
	
	public static void setupPolicyMap() {
		int scenario = 0;
		
		char[] state = new char[] {Arena.EMPTY,Arena.TARGET,Arena.WALL};//' ', '*', 'w'
		policyMap = new HashMap<String,Integer>();
		
		if (vision==9) {
			for (int northwest=0; northwest<3; northwest++) {
				for (int northeast=0; northeast<3; northeast++) {
					for (int southwest=0; southwest<3; southwest++) {
						for (int southeast=0; southeast<3; southeast++) {
							for (int north=0; north<3; north++) {
								for (int south=0; south<3; south++) {
									for (int east=0; east<3; east++) {
										for (int west=0; west<3; west++) {
											for (int current=0; current<3; current++) {
												policyMap.put(""+state[northwest]+state[northeast]+state[southwest]+state[southeast]+state[north]+state[south]+state[east]+state[west]+state[current], scenario);
												//System.out.println("putting scenario: "+scenario+" "+policyMap.entrySet());
												scenario++;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else {
			for (int north=0; north<3; north++) {
				for (int south=0; south<3; south++) {
					for (int east=0; east<3; east++) {
						for (int west=0; west<3; west++) {
							for (int current=0; current<3; current++) {
								policyMap.put(""+state[north]+state[south]+state[east]+state[west]+state[current], scenario);
								scenario++;
							}
						}
					}
				}
			}
		}
		//System.out.println("Policy Map is: " + policyMap.entrySet());
	}
	
	public Chromo (int[] policy) {
		this.policy = policy.clone();
		
		//System.out.println(Arrays.toString(this.policy));
		this.fitness=-1;
		this.chromoID = Chromo.totalChromos;
		Chromo.totalChromos++;
	}
	
	public Chromo (String policy) {
		if (policy.length() != Chromo.length)
			return;
		this.policy = new int[Chromo.length];
		for (int gene=0; gene<Chromo.length; gene++) {
			this.policy[gene] = Character.getNumericValue(policy.charAt(gene));
		}
		//System.out.println(Arrays.toString(this.policy));
		this.fitness=-1;
		this.chromoID = Chromo.totalChromos;
		Chromo.totalChromos++;
	}
	
	public int[] getPolicy() {
		return this.policy;
	}
	
	public String toString() {
		return "Policy: "+ Arrays.toString(this.policy) + "\nFitness: "+this.fitness;
	}
	
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public double getFitness()	{
		return this.fitness;
	}
	
	public int getAction(int location) {
		return this.policy[location];
	}
	
	public void setAction(int action, int situation) {
		this.policy[situation] = action;
	}
	
	public int compareTo (Chromo other) {
		if (this.fitness<other.fitness)
			return -1;
		else if (this.fitness>other.fitness)
			return 1;
		else return 0;
		
	}
	
	public static void uniform_crossover (Chromo chromo1, Chromo chromo2) {
		Random rand = new Random();
		for (int gene=0; gene<Chromo.length; gene++) {
			if (rand.nextDouble()<crossoverAmount) {
				int temp = chromo1.policy[gene];
				chromo1.policy[gene] = chromo2.policy[gene];
				chromo2.policy[gene] = temp;
			}
		}
	}
	
	public static void singlepoint_crossover (Chromo chromo1, Chromo chromo2) {
		Random rand = new Random();
		int point  = rand.nextInt(Chromo.length);
		for (int gene=point; gene<Chromo.length; gene++) {
			int temp = chromo1.policy[gene];
			chromo1.policy[gene] = chromo2.policy[gene];
			chromo2.policy[gene] = temp;
		}
	}
	
	public static void twopoint_crossover (Chromo chromo1, Chromo chromo2) {
		Random rand = new Random();
		int point1  = rand.nextInt(Chromo.length);
		int point2;
		do{
			point2 = rand.nextInt(Chromo.length);
		}while(point1==point2);
		if (point1>point2) {
			int temp = point1;
			point1=point2;
			point2=temp;
		}
		for (int gene=point1; gene<=point2; gene++) {
			int temp = chromo1.policy[gene];
			chromo1.policy[gene] = chromo2.policy[gene];
			chromo2.policy[gene] = temp;
		}
	}
	
	public void mutate() {
		Random rand = new Random();
		for (int gene=0; gene<Chromo.length; gene++) {
			if (rand.nextDouble()<mutationAmount)
				this.policy[gene] = rand.nextInt(Chromo.actions);
		}
	}
	
	public void calculateFitness() {
		this.fitness = 0;
		Arena arena = new Arena (arenaSize+2,arenaSize+2);//adding 2 rows and 2 cols for walls
		for (int session=0; session<testingSessions; session++) {
			this.fitness += arena.testChromo(this);
			arena.reset();//clear our everything but the walls and randomly spread new targets
		}
		this.fitness /= testingSessions;	
	}
	
	public void calculateFitness(Arena arena) {//given specific arena only
		
		char[][] field = arena.getField();
		char[][] startingField = new char[field.length][field[0].length];
		for(int r=0; r<field.length;r++)
			startingField[r] = field[r].clone();

		this.fitness = arena.testChromo(this);

		//replace all targets
		for(int r=0; r<field.length;r++)
			field[r] = startingField[r].clone(); 
		arena.setField(field);

	}
	
	public void demo() {
		double score;
		Arena.demo = true;
		Arena arena = new Arena (arenaSize+2,arenaSize+2);//adding 2 rows and 2 cols for walls
		Scanner input = new Scanner(System.in);  // Create a Scanner object
		
		do {
			arena.reset();
			score = arena.testChromo(this);
			System.out.println("Score earned: "+score);
			System.out.println("Demo on a new arena? y/n");
		} while (input.next().toLowerCase().equals("y"));
		input.close();
		Arena.demo = false;
	}
}
