package Exercise3.Genetics.Models;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Gene implements Comparable<Gene> {

    private int[] data;

    //Gets updated when the data changes
    private double fitness;

    /**
     * Standard Constructor for Genes
     */
    public Gene(int cityCount) {
        data = new int[cityCount];
        ArrayList<Integer> remainingCities = new ArrayList<>();
        for(int i=1; i<=cityCount; i++){
            remainingCities.add(i);
        }
        //Sets values of data-array with the probability of the parameter initrate
        for (int i = 0; i < cityCount; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(remainingCities.size());
            data[i] = remainingCities.get(randomIndex);
            remainingCities.remove(randomIndex);
        }
        calculateFitness();
    }

    public Gene(int[] data){
        this.data = data;
        calculateFitness();
    }

    /**
     *  Constructor to clone a Gene
     */
    private Gene(int[] data, double fitness) {
        this.data = new int[data.length];
        this.fitness = fitness;
        initializeCopyData(data);
    }

    /**
     * Copies the array data and sets it to this gene object as data
     */
    private void initializeCopyData(int[] data) {
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    /**
     * Clones a gene
     */
    public Gene clone() {
        return new Gene(this.getData(), this.fitness);
    }

    /**
     *  Sets the value of the gene data at the given position to the given value
     *  (-1 means to invert the current value)
     */
    public void setPos(int pos, int value) {
        data[pos] = value;
        calculateFitness();
    }


    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Recalculates the current fitness value
     */
    public void calculateFitness() {
        double fitness = 0d;
        for(int i = 0; i< data.length-1; i++){
            // -1 because index starts at 0 and not at 1
            int from = data[i]-1;
            int to = data[i+1]-1;
            fitness += GeneSet.distanceMap[from][to];
        }
        fitness += GeneSet.distanceMap[data[data.length-1]-1][data[0]-1];
        setFitness(fitness);
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
        calculateFitness();
    }

    /**
     *  Comparator for gene
     */
    @Override
    public int compareTo(Gene gene) {
        calculateFitness();
        gene.calculateFitness();
        return Double.compare(fitness, gene.getFitness());
    }
}
