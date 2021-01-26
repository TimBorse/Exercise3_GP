package Exercise3.Genetics.Threads;

import Exercise3.Genetics.Enums.Protection;
import Exercise3.Genetics.Enums.RecombinationType;
import Exercise3.Genetics.Enums.ReplicationScheme;
import Exercise3.Genetics.Models.Gene;
import Exercise3.Genetics.Models.GeneSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class RunGenerationsThread extends Thread {

    private final int genecnt;
    private final int cityCount;
    private final int maxgenerations;
    private final double acceptedFitness;
    private final double pc;
    private final double pm;
    private final RecombinationType crossingOverMethod;
    private final ReplicationScheme replicationScheme;
    private final Protection protection;

    private ArrayList<Integer> cities;
    public int generationCount;
    public double bestFitness;


    public RunGenerationsThread(int genecnt, int cityCount, int maxgenerations, double acceptedFitness, double pc, double pm, ReplicationScheme replicationScheme, RecombinationType crossingOverMethod, Protection protection) {
        super();
        this.genecnt = genecnt;
        this.maxgenerations = maxgenerations;
        this.acceptedFitness = acceptedFitness;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
        this.cityCount = cityCount;
        this.pc = pc;
        this.pm = pm;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    @Override
    public void run() {
        Gene[] genes = new Gene[genecnt];
        bestFitness = Double.MAX_VALUE;
        //Initializes new genes
        for (int j = 0; j < genecnt; j++) {
            genes[j] = new Gene(cityCount);
        }

        //Standard procedure of the simulation
        while (!genesReachedDesiredFitness(genes)) {
            Gene[] selectedGenes;
            switch (protection) {
                case NONE:
                    selectedGenes = genes;
                    break;
                case BEST:
                    selectedGenes = new Gene[genecnt - 1];
                    if (genecnt - 1 >= 0) System.arraycopy(genes, 1, selectedGenes, 0, genecnt - 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + protection);
            }
            mutateGenes(selectedGenes, pm);
            if (genesReachedDesiredFitness(genes)) {
                break;
            }
            crossOver(selectedGenes, pc);
            if (genesReachedDesiredFitness(genes)) {
                break;
            }
            replicateGenes(genes);
            generationCount++;
            if (generationCount >= maxgenerations)
                break;
        }
        Arrays.sort(genes);
        if (GeneSet.bestGene != null && genes[0].getFitness() < GeneSet.bestGene.getFitness()) {
            GeneSet.bestGene = genes[0];
        }else if(GeneSet.bestGene == null){
            GeneSet.bestGene = genes[0];
        }
        if (genes[0].getFitness() < bestFitness) {
            bestFitness = genes[0].getFitness();
        }

    }

    private void initializeCities(){
        cities = new ArrayList<>();
        for (int k = 1; k <= cityCount; k++) {
            cities.add(k);
        }
    }

    private ArrayList<Integer> getCitiesList(){
        return (ArrayList<Integer>) cities.clone();
    }

    private void crossOver(Gene[] genes, double pc) {
        if (crossingOverMethod == RecombinationType.GREEDY_CROSSOVER || crossingOverMethod == RecombinationType.ADVANCED_GREEDY_CROSSOVER) {
            initializeCities();
            Gene[] oldGenes = new Gene[genes.length];
            System.arraycopy(genes, 0, oldGenes, 0, genes.length);
            for (int i = 0; i < (int) (genecnt * pc); i++) {
                ArrayList<Integer> unusedCities = getCitiesList();

                int geneIndex1 = ThreadLocalRandom.current().nextInt(0, genes.length);
                int geneIndex2 = ThreadLocalRandom.current().nextInt(0, genes.length);
                int[] data1 = genes[geneIndex1].getData();
                int[] data2 = genes[geneIndex2].getData();
                int[] newData = new int[cityCount];

                newData[0] = data1[0];
                unusedCities.remove((Integer) data1[0]);

                for (int j = 0; j < (cityCount - 1); j++) {
                    int index1 = getIndexOf(data1, newData[j]);
                    int index2 = getIndexOf(data2, newData[j]);
                    int nextValue1;
                    int nextValue2;
                    if (index1 < cityCount - 1)
                        nextValue1 = data1[index1 + 1];
                    else {
                        nextValue1 = data1[0];
                    }
                    if (index2 < cityCount - 1)
                        nextValue2 = data2[index2 + 1];
                    else {
                        nextValue2 = data2[0];
                    }
                    double dist1 = GeneSet.distanceMap[data1[index1] - 1][nextValue1 - 1];
                    double dist2 = GeneSet.distanceMap[data2[index2] - 1][nextValue2 - 1];
                    boolean value1IsUsed = !unusedCities.contains(nextValue1);
                    boolean value2IsUsed = !unusedCities.contains(nextValue2);
                    if (value1IsUsed && value2IsUsed) {
                        if (crossingOverMethod == RecombinationType.ADVANCED_GREEDY_CROSSOVER) {
                            int closestCity = findClosestRemaining(unusedCities, newData[j]);
                            setNewData(newData, j + 1, closestCity, unusedCities);
                        } else {
                            int rdm = ThreadLocalRandom.current().nextInt(unusedCities.size());
                            setNewData(newData, j + 1, unusedCities.get(rdm), unusedCities);
                        }

                    } else if (value1IsUsed) {
                        setNewData(newData, j + 1, nextValue2, unusedCities);
                    } else if (value2IsUsed) {
                        setNewData(newData, j + 1, nextValue1, unusedCities);
                    } else if (dist1 < dist2) {
                        setNewData(newData, j + 1, nextValue1, unusedCities);
                    } else {
                        setNewData(newData, j + 1, nextValue2, unusedCities);
                    }
                }
                genes[i].setData(newData);
            }

            for (int i = (int) (genecnt * pc); i < genes.length; i++) {
                int rdm = ThreadLocalRandom.current().nextInt(oldGenes.length);
                genes[i].setData(oldGenes[rdm].getData());
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private int findClosestRemaining(ArrayList<Integer> unusedCities, int value){
        int bestValue = 0;
        for(int currentValue : unusedCities){
            if(bestValue == 0 || GeneSet.distanceMap[bestValue-1][value-1] > GeneSet.distanceMap[currentValue-1][value-1])
                bestValue = currentValue;
        }
        return bestValue;
    }

    private void setNewData(int[] newData, int index, int value, ArrayList<Integer> unusedCities){
        newData[index] = value;
        unusedCities.remove((Integer) value);
    }

    private int getIndexOf(int[] data, int value) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == value)
                return i;
        }
        return -1;
    }

    private void replicateGenes(Gene[] genes) {
        Arrays.sort(genes);
        switch (replicationScheme) {
            case DOUBLE_BEST_TWO:
                Gene bestGene = genes[0];
                Gene secondBestGene = genes[1];
                Gene[] newGenes = new Gene[genecnt];
                for (int i = 0; i < genecnt; i++) {
                    if (i < (genecnt / 4)) {
                        newGenes[i] = bestGene.clone();
                    } else if (i < (genecnt / 2))
                        newGenes[i] = secondBestGene.clone();
                    else {
                        int rdm = ThreadLocalRandom.current().nextInt(genes.length);
                        newGenes[i] = genes[rdm].clone();
                    }
                }
                System.arraycopy(newGenes, 0, genes, 0, genecnt);
                break;
            case DOUBLE_BEST_HALF:
                for (int i = 0; i < genecnt; i++) {
                    if (i < (genecnt / 2)) {
                        genes[(genecnt / 2) + i] = genes[i].clone();
                    }
                }
                break;
            case NONE:
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    private void mutateGenes(Gene[] genes, double pm) {
        for (int i = 0; i < (genecnt * cityCount * pm); i++) {
            int geneIndex = ThreadLocalRandom.current().nextInt(0, genes.length);
            int pos1 = ThreadLocalRandom.current().nextInt(0, cityCount);
            int pos2 = ThreadLocalRandom.current().nextInt(0, cityCount);
            swapCityPositions(genes[geneIndex], pos1, pos2);
        }
    }

    private void swapCityPositions(Gene gene, int pos1, int pos2) {
        int temp = gene.getData()[pos1];
        gene.setPos(pos1, gene.getData()[pos2]);
        gene.setPos(pos2, temp);
    }

    //Checks if a gene has reached the desired fitness
    private boolean genesReachedDesiredFitness(Gene[] genes) {
        if(acceptedFitness == 0)
            return false;
        Arrays.sort(genes);
        if (genes[0].getFitness() < bestFitness) {
            bestFitness = genes[0].getFitness();
        }
        return genes[0].getFitness() <= acceptedFitness;
    }
}



