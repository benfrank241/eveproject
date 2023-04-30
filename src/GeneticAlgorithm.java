import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

	private Chromo[] population;
	private int populationSize;
	private double mutationRate = 1;
	private double crossoverRate = 1;
	private int elites =10;

	private String crossoverType;
	private String mutationType;

	private double totalFitness;

	public GeneticAlgorithm() {
		populationSize = 40;
		population = new Chromo[populationSize];
		for (int c = 0; c < populationSize; c++) {
			population[c] = new Chromo();
		}
	}

	public Chromo selectParent() {

		/*//---------------- FITNESS PROPORTIONAL SELECTION --------------------
		// this one is setup to minimize fitness, so you'd need to adjust it to use it here
		double runningFitness = 0;
		Random rand = new Random();
		double probability = rand.nextDouble();
		//System.out.println("selection probability: "+probability);
		for (int c=0; c<this.populationSize; c++) {
			runningFitness += this.population[c].getFitness();
			//System.out.println("running fitness: "+runningFitness + "/total = "+runningFitness/totalFitness);
			if (probability < runningFitness/this.totalFitness)
				return this.population[c];			
		}
		//we'll only end up here if all fitnesses were zero, so let's pick a parent at random
		return this.population[rand.nextInt(this.populationSize)];*/
		

		// ---------------- RANK PROPORTIONAL SELECTION --------------------
		// since chromos in current generation have been sorted, their spot is their
		// rank
		double sumRank = 0;
		double overallSumRanks = (this.populationSize + 1) * this.populationSize / 2.0;
		Random rand = new Random();
		double probability = rand.nextDouble();
		// System.out.println("selection probability: "+probability);
		for (int c = 0; c < this.populationSize; c++) {
			sumRank += (c + 1);
			// System.out.println("running fitness: "+runningFitness + "/total =
			// "+runningFitness/totalFitness);
			if (probability < sumRank / overallSumRanks)
				return this.population[c];
		}
		// dummy return; let's pick a parent at random
		return this.population[rand.nextInt(this.populationSize)];
	}

	public void generateNextGeneration() {

		Chromo[] tng = new Chromo[this.populationSize];
		int tngSize = 0;
		Random rand = new Random();
		
		// ------------------- ELITISM ---------------------------
		for (int e=0; e<this.elites; e++) {
			tng[tngSize] = this.population[this.populationSize - 1 - e];
			tngSize++;
		}
				
		// ------------ CROSSOVER -------------------------

		while (tngSize < this.populationSize) {

			Chromo parent1 = selectParent();
			Chromo parent2 = selectParent();
			while (parent1 == parent2)
				parent2 = selectParent();

			Chromo child1 = new Chromo(parent1.getPolicy());
			Chromo child2 = new Chromo(parent2.getPolicy());

			if (rand.nextDouble() < crossoverRate)
				// Chromo.uniform_crossover(child1,child2);
				// Chromo.singlepoint_crossover(child1,child2);
				Chromo.twopoint_crossover(child1, child2);
			if (rand.nextDouble() < mutationRate)
				child1.mutate();
			if (rand.nextDouble() < mutationRate)
				child2.mutate();

			tng[tngSize] = child1;
			tngSize++;
			tng[tngSize] = child2;
			tngSize++;
		}
		

		this.population = tng;
	}

	public static void main(String[] args) {

		Chromo.setupPolicyMap();

		//Arena arena = new Arena (12,12);
		//System.out.println(arena);
		
		/*Chromo testerChromo = new Chromo();
		//setting up a fake chromo to do specific things
		char[] state = new char[] {' ','*','w'};//empty, target, wall
		Random rand = new Random();
		for (int north=0; north<3; north++) {
			for (int south=0; south<3; south++) {
				for (int east=0; east<3; east++) {
					for (int west=0; west<3; west++) {
						for (int current=0; current<3; current++) {
							String scenario = ""+state[north]+state[south]+state[east]+state[west]+state[current];
							if (state[current]=='*')
								testerChromo.setAction(5,Chromo.policyMap.get(scenario)); //set to pickup if standing on target
							else {//if (state[current]==' ') {
								if (scenario.charAt(0)=='*')
									testerChromo.setAction(0,Chromo.policyMap.get(scenario)); //set to pickup if standing on target
								else if (scenario.charAt(1)=='*')
									testerChromo.setAction(1,Chromo.policyMap.get(scenario)); //set to pickup if standing on target
								else if (scenario.charAt(2)=='*')
									testerChromo.setAction(2,Chromo.policyMap.get(scenario)); //set to pickup if standing on target
								else if (scenario.charAt(3)=='*')
									testerChromo.setAction(3,Chromo.policyMap.get(scenario)); //set to pickup if standing on target
								else
									testerChromo.setAction(6,Chromo.policyMap.get(scenario)); //set to pickup if standing on target
							}
							//else testerChromo.setAction(6,Chromo.policyMap.get(scenario)); //move randomly
						}
					}
				}
			}
		}
		testerChromo.calculateFitness();
		System.out.println(testerChromo);
		*/

		// ----------------------
		if (Chromo.vision == 5) {
			// Chromo testerChromo_MMitchell = new
			// Chromo("656353656252353252656353656151353151252353252151353151656353656252353252656353656050353050252353252050353050151353151252353252151353151050353050252353252050353050656353656252353252656353656151353151252353252151353151656353656252353252656353454");
			// testerChromo_MMitchell.calculateFitness();
			// System.out.println(testerChromo_MMitchell);
			// testerChromo_MMitchell.demo();

			// ----------------------
			Chromo testerChromo_bestEvolved;
			// -----------------------------------------------------
			testerChromo_bestEvolved = new Chromo(
					"650255356356250136054253314150151150154156332016111111355052351352055035055562411001001155056030606050253603103100154101663060614155530001001001000133634056620504256252152135656563252253426153152151331333244151266036141652615355143653212223146");
			testerChromo_bestEvolved.calculateFitness();
			System.out.println(testerChromo_bestEvolved);

			testerChromo_bestEvolved = new Chromo(
					"650253356353255663256252632156251355155254352151656553052253056355350355255021241052052355354351632050056210052204351153265664150056250056260055654064663250625553155255156354252631251250134151154156656332152650641231321440035046155160313600032");
			testerChromo_bestEvolved.calculateFitness();
			System.out.println(testerChromo_bestEvolved);

			testerChromo_bestEvolved = new Chromo(
					"051256154356231353256256225152115154315666151152213216352254350356655356052224015054255156350234065050253351610152351616352363212251442052621303354224401052625162255253151355356635150222311114111155254651114115603014013044634236632505434166533");
			testerChromo_bestEvolved.calculateFitness();
			System.out.println(testerChromo_bestEvolved);
			// testerChromo_bestEvolved.demo();

			testerChromo_bestEvolved = new Chromo(
					"251256054350625356150252425153163153354322156153151543050250352050253352252256652053055056353354001256025530016016154016661105252254544053255350056051031251603564356625350356320350153652422152350656352121152116115062034540106204163356220551430");
			testerChromo_bestEvolved.calculateFitness();
			System.out.println(testerChromo_bestEvolved);
			// testerChromo_bestEvolved.demo();

		}
		int numRuns = 1;
		Chromo[] bestEves = new Chromo[numRuns];
		for (int run = 0; run < numRuns; run++) {
			GeneticAlgorithm ga = new GeneticAlgorithm();
			for (int gen = 0; gen < 5000; gen++) {
				System.out.println("========================== GEN " + gen + " ===========================");
				ga.totalFitness = 0;
				for (int c = 0; c < ga.populationSize; c++) {
					ga.population[c].calculateFitness();
					// System.out.println("Chromo# " + c +": ID" +ga.population[c].chromoID + " " +
					// ga.population[c]);
					ga.totalFitness += ga.population[c].getFitness();
				}

				Arrays.sort(ga.population);

				System.out.println("Best Chromo: ID" + ga.population[ga.populationSize - 1].chromoID + " "
						+ ga.population[ga.populationSize - 1]);
				System.out.println(
						"-------------- avg fitness: " + ga.totalFitness / ga.populationSize + " ---------------");

				ga.generateNextGeneration();
			}
			bestEves[run] = ga.population[ga.populationSize - 1];
			System.out.println("Final generation's best: ID" + ga.population[ga.populationSize - 1].chromoID + " "
					+ ga.population[ga.populationSize - 1]);
			// ga.population[ga.populationSize-1].demo();
		}
		
		EveTournament.runTournament(bestEves);
	}
}
