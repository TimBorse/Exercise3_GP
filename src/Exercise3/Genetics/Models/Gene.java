package Exercise3.Genetics.Models;

import Exercise3.Genetics.Enums.ConstOperations;
import Exercise3.Genetics.Enums.Operations;
import Exercise3.Genetics.Enums.Protection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Gene implements Comparable<Gene> {

    private String data;

    //Gets updated when the data changes
    private double fitness;

    /**
     * Standard Constructor for Genes
     */
    public Gene(int geneLength) throws Exception {
        data = "";
        StringBuilder sb = new StringBuilder();
        ConstOperations[] constOperations = ConstOperations.class.getEnumConstants();
        Operations[] operations = Operations.class.getEnumConstants();

        int rdm = ThreadLocalRandom.current().nextInt(1, geneLength / 2);
        for (int i = 1; i <= rdm; i++) {
            if (i == rdm) {
                sb.append(ThreadLocalRandom.current().nextInt(100));
                data += ThreadLocalRandom.current().nextInt(100);

            } else {
                Operations operation;
                if(ThreadLocalRandom.current().nextInt(10)<9){
                    operation = Operations.MULTIPLY;
                }else{
                    operation = operations[ThreadLocalRandom.current().nextInt(operations.length)];
                }
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
                } else if (operation == Operations.DIVIDE) {
                    sb.append(ThreadLocalRandom.current().nextInt(100))
                            .append(" ")
                            .append(operation)
                            .append(" ")
                            .append("x ")
                            .append(Operations.EXP)
                            .append(" ")
                            .append(i)
                            .append(" ");
                } else {
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
        try {
            calculateFitness();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public double getFitness() {
        return fitness;
    }


    /**
     * Recalculates the current fitness value
     */
    public void calculateFitness() throws Exception {
        double variance = 0;
        fitness = 0;
        double[] intermediateResults = new double[GeneSet.functionValuesArr.length];
        String[] functionParts = this.data.split("(\\sADD\\s|\\sSUBTRACT\\s)");
        ArrayList<String> simpleOperations = new ArrayList<>();
        Pattern pattern = Pattern.compile("(ADD | SUBTRACT)");
        Matcher matcher = pattern.matcher(this.data);
        while (matcher.find()) {
            simpleOperations.add(matcher.group().trim());
        }
        int functionPartCounter = 0;
        for (String functionPart : functionParts) {
            int counter = 0;
            for (double[] value : GeneSet.functionValuesArr) {
                if (functionPartCounter == 0) {
                    intermediateResults[counter] += calculatePart(functionPart, value[0]);
                } else {
                    if (functionPartCounter < functionParts.length && functionParts.length > 1) {
                        if (simpleOperations.get(functionPartCounter - 1).equals("ADD"))
                            intermediateResults[counter] += calculatePart(functionPart, value[0]);
                        else {
                            intermediateResults[counter] -= calculatePart(functionPart, value[0]);
                        }
                    }
                }
                if (functionPartCounter == functionParts.length - 1) {
                    variance += Math.abs(intermediateResults[counter] - GeneSet.functionValuesArr[counter][1]);
                }

                counter++;
            }
            functionPartCounter++;
        }

        this.fitness = variance / GeneSet.functionValuesArr.length;

    }

    public void mutate() {
        String[] tempParts = data.split("[\\s]*x EXP [\\d][\\s]*");
        ArrayList<String> exponents = new ArrayList<>();
        Pattern pattern = Pattern.compile("(x EXP [\\d]+)");
        Matcher matcher = pattern.matcher(this.data);
        int count = 0;
        while (matcher.find()) {
            count++;
            exponents.add(matcher.group().trim());
        }
        ArrayList<String> parts = new ArrayList<>();
        for (String part : tempParts) {
            parts.addAll(Arrays.asList(part.split("\\s+")));
        }
        parts.remove("");
        int rdm = ThreadLocalRandom.current().nextInt(parts.size());
        String part = parts.get(rdm);
        Operations[] operations = Operations.class.getEnumConstants();
        Operations operation = operations[ThreadLocalRandom.current().nextInt(operations.length)];
        switch (part) {
            case "ADD":
                parts.set(rdm, "SUBTRACT");
                break;
            case "SUBTRACT":
                parts.set(rdm, "ADD");
                break;
            case "DIVIDE":
                if (rdm > 0 && parts.get(rdm - 1).matches("\\d+")) {
                    parts.set(rdm, operation.toString());
                    if (operation.equals(Operations.LN) || operation.equals(Operations.COS) || operation.equals(Operations.SIN)) {
                        parts.remove(rdm - 1);
                    }
                } else {
                    parts.set(rdm, parts.get(rdm + 1));
                    parts.set(rdm + 1, operation.toString());
                    if (operation.equals(Operations.LN) || operation.equals(Operations.COS) || operation.equals(Operations.SIN)) {
                        parts.remove(rdm);
                    }
                }
                break;
            default:
                if (part.matches("\\d+")) {
                    parts.set(rdm, "" + ThreadLocalRandom.current().nextInt(100));
                } else {
                    if (part.equals("LN") || part.equals("COS") || part.equals("SIN")) {
                        parts.set(rdm, operation.toString());
                        if (!(operation.equals(Operations.LN) || operation.equals(Operations.COS) || operation.equals(Operations.SIN))) {
                            parts.add(rdm, "" + ThreadLocalRandom.current().nextInt(100));
                        }
                    } else {
                        parts.set(rdm, operation.toString());
                        if (operation.equals(Operations.LN) || operation.equals(Operations.COS) || operation.equals(Operations.SIN)) {
                            parts.remove(rdm - 1);
                        }
                    }
                }
                break;
        }
        this.data = "";
        StringBuilder sb = new StringBuilder();
        int expCounter = 0;
        for (int i = 0; i < parts.size(); i++) {
            if ((parts.get(i).equals("ADD") && !parts.get(i - 1).matches("\\d+"))
                    || (parts.get(i).equals("SUBTRACT") && !parts.get(i - 1).matches("\\d+"))
                    || (parts.get(i).equals("DIVIDE") && i < parts.size() - 1 && parts.get(i + 1).matches("\\d+"))) {

                sb.append(exponents.get(expCounter)).append(" ").append(parts.get(i)).append(" ");
                expCounter++;
            } else {
                sb.append(parts.get(i)).append(" ");
            }
        }
        this.data = sb.toString().trim();
    }

    private double calculatePart(String functionPart, double x) throws Exception {
        String[] parts = functionPart.split("[\\s]+");
        double result = 0;
        if (parts.length > 1) {
            switch (parts[0]) {
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
                    switch (parts[1]) {
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
                            result = Math.log10(Math.pow(x, Double.parseDouble(parts[4]))) / Math.log10(Double.parseDouble(parts[0]));
                            break;
                        default:
                            throw new Exception(functionPart);

                    }
                    break;

            }
        } else if(!parts[0].equals("")){
            try{
                result = Double.parseDouble(parts[0]);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return result;
    }

    public String getData() {
        return data;
    }


    /**
     * Comparator for gene
     */
    @Override
    public int compareTo(Gene gene) {
        try {
            calculateFitness();
            gene.calculateFitness();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Double.compare(fitness, gene.getFitness());
    }
}
