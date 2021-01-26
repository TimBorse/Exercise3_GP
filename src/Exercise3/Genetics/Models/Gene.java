package Exercise3.Genetics.Models;

import Exercise3.Genetics.Enums.ConstOperations;
import Exercise3.Genetics.Enums.Operations;
import Exercise3.Genetics.Enums.Protection;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Gene implements Comparable<Gene> {

    private String data;

    //Gets updated when the data changes
    private double fitness;

    /**
     * Standard Constructor for Genes
     */
    public Gene(int geneLength) {
        data = "";
        StringBuilder sb = new StringBuilder();
        ConstOperations[] constOperations = ConstOperations.class.getEnumConstants();
        Operations[] operations = Operations.class.getEnumConstants();

        int rdm = ThreadLocalRandom.current().nextInt(geneLength / 2);
        for (int i = 1; i <= rdm; i++) {
            if (i == rdm) {
                //ToDo: Wertebereich bestimmen
                sb.append(ThreadLocalRandom.current().nextInt(100));
                data += ThreadLocalRandom.current().nextInt(100);

            } else {
                Operations operation = operations[ThreadLocalRandom.current().nextInt(operations.length)];
                int randomNumber = ThreadLocalRandom.current().nextInt(2);
                if (operation != Operations.LN && operation != Operations.COS && operation != Operations.SIN && operation != Operations.DIVIDE) {
                    sb.append(ThreadLocalRandom.current().nextInt(100))
                    .append(" ");
                }
                if (operation == Operations.DIVIDE && randomNumber != 0) {
                    sb.append("x ")
                            .append(Operations.EXP)
                            .append(" ")
                            .append(i)
                            .append(" ")
                            .append(operation)
                            .append(" ")
                            .append(ThreadLocalRandom.current().nextInt(100))
                            .append(" ");
                } else if (operation == Operations.DIVIDE){
                    sb.append(ThreadLocalRandom.current().nextInt(100))
                            .append(" ")
                            .append(operation)
                            .append(" ")
                            .append("x ")
                            .append(Operations.EXP)
                            .append(" ")
                            .append(i)
                            .append(" ");
                }else{
                    sb.append(operation)
                            .append(" ")
                            .append("x ")
                            .append(Operations.EXP)
                            .append(" ")
                            .append(i)
                            .append(" ");
                }
                sb.append(constOperations[ThreadLocalRandom.current().nextInt(constOperations.length)])
                        .append(" ");

                data = sb.toString();


            }

        }
        calculateFitness();
    }

    public Gene(String data) {
        this.data = data;
        calculateFitness();
    }

    /**
     * Constructor to clone a Gene
     */
    private Gene(String data, double fitness) {
        this.data = data;
        this.fitness = fitness;
    }

    /**
     * Clones a gene
     */
    public Gene clone() {
        return new Gene(this.getData(), this.fitness);
    }

    /**
     * Sets the value of the gene data at the given position to the given value
     * (-1 means to invert the current value)
     */
    public void setPos(int pos, int value) {
        // data[pos] = value;
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
        fitness = 0;
        double[] intermediateResults = new double[GeneSet.functionValuesArr.length];
        String[] functionParts = this.data.split("(ADD | SUBTRACT)");
        for (String functionPart : functionParts) {
            for (double[] value : GeneSet.functionValuesArr) {

            }
        }

    }

    private double calculatePart(String functionPart, double x) throws HoustonWeGotAProblemException {
        String[] parts = functionPart.split(" ");
        double result = 0;
            switch (parts[0]){
                case "SIN":
                    result = Math.sin(Math.pow(x, Double.parseDouble(parts[3])));
                    break;
                case "COS":
                    result = Math.cos(Math.pow(x, Double.parseDouble(parts[3])));
                    break;
                case "LN":
                    result = Math.log(Math.pow(x, Double.parseDouble(parts[3])));
                    break;
                case "x":
                    result = Math.pow(x, Double.parseDouble(parts[2])) / Double.parseDouble(parts[4]);
                    break;
                default:
                    switch (parts[1]){
                        case "MULTIPLY":
                            result = Double.parseDouble(parts[0]) * Math.pow(x, Double.parseDouble(parts[4]));
                            break;
                        case "DIVIDE":
                            result = Double.parseDouble(parts[0]) / Math.pow(x, Double.parseDouble(parts[4]));
                            break;
                        case "EXP":
                            result = Math.pow(Double.parseDouble(parts[0]), Math.pow(x, Double.parseDouble(parts[4])));
                            break;
                        case "LOG":
                            result = Math.log10(Math.pow(x, Double.parseDouble(parts[4])))/Math.log10(Double.parseDouble(parts[0]));
                            break;
                        default:
                            throw new HoustonWeGotAProblemException();

                    }
                    break;

            }
            return result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        calculateFitness();
    }

    /**
     * Comparator for gene
     */
    @Override
    public int compareTo(Gene gene) {
        calculateFitness();
        gene.calculateFitness();
        return Double.compare(fitness, gene.getFitness());
    }
}
