package Exercise3.Genetics.Threads;

import Exercise3.Genetics.Enums.*;
import Exercise3.Genetics.Models.Gene;
import Exercise3.Genetics.Models.GeneSet;
import javafx.scene.chart.AreaChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            try {
                genes[j] = new Gene(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            replicateGenes(genes);
            generationCount++;
            if (generationCount >= maxgenerations)
                break;
        }
        Arrays.sort(genes);
        if (GeneSet.bestGene != null && genes[0].getFitness() < GeneSet.bestGene.getFitness()) {
            GeneSet.bestGene = genes[0];
        } else if (GeneSet.bestGene == null) {
            GeneSet.bestGene = genes[0];
        }
        if (genes[0].getFitness() < bestFitness) {
            bestFitness = genes[0].getFitness();
        }

    }


    private void replicateGenes(Gene[] genes) {
        Arrays.sort(genes);
        switch (replicationScheme) {
            case DOUBLE_BEST_QUARTER:
                for (int i = 0; i < genecnt; i++) {
                    if (i < (genecnt / 4)) {
                        genes[genecnt - 1 - i] = genes[i].clone();
                    } else {
                        genes[genecnt - 1 - i] = genes[ThreadLocalRandom.current().nextInt(genecnt)].clone();
                    }
                }
                break;
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
            default:
                throw new IllegalArgumentException();
        }
    }


    private void mutateGenes(Gene[] genes, double pm) {
        for (int i = 0; i < (genecnt * pm); i++) {
            int geneIndex = ThreadLocalRandom.current().nextInt(0, genes.length);
            genes[geneIndex].mutate();
        }
    }


    //Checks if a gene has reached the desired fitness
    private boolean genesReachedDesiredFitness(Gene[] genes) {
        if (acceptedFitness == 0)
            return false;
        Arrays.sort(genes);
        if (genes[0].getFitness() < bestFitness) {
            bestFitness = genes[0].getFitness();
        }
        return genes[0].getFitness() <= acceptedFitness;
    }
}



