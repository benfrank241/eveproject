import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class EveTournament {
	
	static int numTrials = 500;
	
	public static void runTournament (Chromo[] chromos) {
		
		System.out.printf("Welcome to the tournament of Eve-bots!\nOur participants are:\n");
		for (int c=0; c<chromos.length; c++) {
			System.out.printf("Participant %d: %20s %s\n", c+1,chromos[c].name,Arrays.toString(chromos[c].getPolicy()));
			chromos[c].cumulativeFitness=0;
			//chromos[c].demo();
		}
		
		Arena.randomInitialLocation = false;
		Random rand = new Random();
		Arena arena = null;
		//running all the trials, keeping arena identical for all chromosomes throughout each trial
		for (int t=0; t<numTrials; t++) {
			
			//setup new arena and new starting location
			Chromo.arenaSize = rand.nextInt(3)+5; //adding a min of 5 otherwise we may end up with too small of an arena and crash
			if (rand.nextBoolean())
				Arena.bunchofwalls=true;
			else
				Arena.bunchofwalls=false;
			Arena.testDuration = 2*Chromo.arenaSize*Chromo.arenaSize;//for 10x10 we had 200
			
			arena = new Arena (Chromo.arenaSize+2,Chromo.arenaSize+2);//adding 2 rows and 2 cols for walls
			System.out.printf("\nArena #%d with %d targets (max: %dpts)\n",t+1,arena.targetCount,arena.targetCount*Arena.GOT_TARGET);
			System.out.print(arena.toString());
			
			do {
				Arena.startingRow = rand.nextInt(Chromo.arenaSize)+1;//avoids perimeter walls
				Arena.startingCol = rand.nextInt(Chromo.arenaSize)+1;//avoids perimeter walls
			}while(arena.getField()[Arena.startingRow][Arena.startingCol]==Arena.WALL);
			
			for (int c=0; c<chromos.length; c++) {
				chromos[c].calculateFitness(arena); //this resets target to OG locations after the test
				chromos[c].cumulativeFitness += chromos[c].getFitness();
				//System.out.print(arena.toString());
			}
		}
		
		//averaging out the fitnesses
		for (int c=0; c<chromos.length; c++) {
			chromos[c].setFitness(chromos[c].cumulativeFitness / numTrials);
		}
		Arrays.sort(chromos);
		
		System.out.printf("\n\nDRUMROOOOOOLLLLL....\nAnd the places are:\n\n");
		for (int c=0; c<chromos.length; c++) {
			Chromo chromo = chromos[chromos.length-1-c];
			System.out.printf("In #%d: %20s %7.2f%% %s\n", c+1, chromo.name, chromo.getFitness(), Arrays.toString(chromo.getPolicy()));
		}
		
		chromos[chromos.length-1].demo();
		
	}
	public static void main(String[] args) {
		int submissions = 10;
		
		Chromo.setupPolicyMap();
		//evolvedPolicies[0] = new String [];
		
		Scanner sc;
		try {
			sc = new Scanner(new File("./src/participants.txt"));
			
			int entries = 0;
			while(sc.hasNextLine()) {//count up the entries
				sc.nextLine();
				entries++;
			}
			sc = new Scanner(new File("./src/participants.txt"));
			Chromo[] chromos = new Chromo[entries];
			for (int c=0; c<entries; c++) {//count up the entries
				//scan and skip the real name
				sc.next();
				String codename = sc.next();//scan and store participant code name
				//System.out.println(codename);
				String policy = sc.next();
				//System.out.println(policy);
				chromos[c] = new Chromo(policy);//using nextLine here to consume rest of line
				chromos[c].name=codename;
				//sc.next();//consume end of line character
			}
			
			runTournament(chromos);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
}
