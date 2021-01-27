package Exercise3.Genetics.Models;

import Exercise3.Genetics.Enums.Protection;
import Exercise3.Genetics.Enums.RecombinationType;
import Exercise3.Genetics.Enums.ReplicationScheme;
import Exercise3.Genetics.Threads.RunGenerationsThread;
import Exercise3.Run;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class GeneSet {

    public static int progress = 0;

    private ArrayList<ParameterValue> parameterValues;
    private Object[] result;
    public static double[][] functionValuesArr;
    private final int genecnt;
    private final int maxgenerations;
    private double pc;
    private double pm;
    private final double acceptedFitness;
    private final int numberOfRuns;
    private final ReplicationScheme replicationScheme;
    private final RecombinationType crossingOverMethod;
    private final Protection protection;
    private final String functionFileName;
    public static Gene bestGene;

    public GeneSet(String mapFileName,int mapSize, int genecnt, int maxgenerations, double acceptedFitness, int numberOfRuns, ReplicationScheme replicationScheme, RecombinationType crossingOverMethod, Protection protection) throws IOException {
        this.functionFileName = mapFileName;
        this.genecnt = genecnt;
        this.maxgenerations = maxgenerations;
        this.acceptedFitness = acceptedFitness;
        this.numberOfRuns = numberOfRuns;
        this.replicationScheme = replicationScheme;
        this.crossingOverMethod = crossingOverMethod;
        this.protection = protection;
        this.mapSize = mapSize;
        generateFunctionValues();
    }

    private void generateFunctionValues() throws IOException {
        ArrayList<double[]> functionValues = new ArrayList<>();
        File valuesFile = new File("values/" + functionFileName);
        BufferedReader reader = new BufferedReader(new FileReader(valuesFile));
        String readLine;
        while((readLine = reader.readLine()) != null){
            functionValues.add(Arrays.stream(readLine.replaceAll(",", ".").trim().split("\\s+")).mapToDouble(Double::parseDouble).toArray());
        }
        reader.close();
        functionValuesArr = new double[functionValues.size()][2];

        for (int i = 0; i < functionValuesArr.length; i++){
            System.arraycopy(functionValues.get(i), 0, functionValuesArr[i], 0, 2);
        }
    }

    public Object[] getResult(){
        return this.result;
    }




    //Tests all Parameters of the given range and writes it to a results file
    public void findIdealParameters(double pcStart, double pcEnd, double pcStep, double pmStart, double pmEnd, double pmStep) throws IOException, InterruptedException {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        FileWriter fileWriter = new FileWriter("output/results "+day+"-"+month+"-"+year+" "+hour+"."+minute+".txt");
        parameterValues = new ArrayList<>();
        //Amount of runs required to check each parameter combination
        int requiredRuns = (int) (((pcEnd-pcStart)/pcStep+1)*((pmEnd-pmStart)/pmStep+1));
        for (double a = pcStart; BigDecimal.valueOf(a).setScale(5, RoundingMode.HALF_UP).doubleValue() <= BigDecimal.valueOf(pcEnd).setScale(5, RoundingMode.HALF_UP).doubleValue(); a += pcStep) {
            pc = BigDecimal.valueOf(a).setScale(5, RoundingMode.HALF_UP).doubleValue();
            for (double j = pmStart; BigDecimal.valueOf(j).setScale(5, RoundingMode.HALF_UP).doubleValue() <= BigDecimal.valueOf(pmEnd).setScale(5, RoundingMode.HALF_UP).doubleValue(); j += pmStep) {
                pm = BigDecimal.valueOf(j).setScale(5, RoundingMode.HALF_UP).doubleValue();
                writeFile(fileWriter, pc, pm);
                progress++;
                Run.window.setProgressValue((int) ((double)progress/(double)requiredRuns*100d));
                Run.window.setProgressLabel(progress, requiredRuns);
            }
            try {
                fileWriter.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        fileWriter.close();
        System.out.println("Best Parameters:\nPC: "+ getBestParameters().getPc() + "\nPM: "+ getBestParameters().getPm() + "\nAverageGens: " + getBestParameters().getAverageGens());
    }

    //Runs Simulation for given pc and pm and writes it to result file
    private void writeFile(FileWriter fileWriter, double pc, double pm) throws InterruptedException, FileNotFoundException {
        Object[] result = runGeneration();
        int avgRuns = (int)result[0];
        parameterValues.add(new ParameterValue(pc, pm, avgRuns));
        try {
            fileWriter.write(pm + "\t" + pc + "\t" + avgRuns + "\n");
            System.out.println("Average Number of Generations: "+ (int)result[0]);
            System.out.println("PC: " + pc);
            System.out.println("PM: " + pm);
            System.out.println("-------------------------------------------------------------------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object[] runGeneration() throws InterruptedException, FileNotFoundException {
        RunGenerationsThread[] threads = new RunGenerationsThread[numberOfRuns];
        int sum = 0;
        double maxValue = 0;
        //Starts one thread for each number of runs
        //Example: You want the average of 10 runs/Parameter -> 10 Threads
        for (int i = 0; i < numberOfRuns; i++) {
            threads[i] = new RunGenerationsThread(genecnt, cityCount, maxgenerations, acceptedFitness, pc, pm, replicationScheme, crossingOverMethod, protection);
            threads[i].start();
        }

        //Checks if the threads are finished and adds the given values to the sum/maxValue
        for (RunGenerationsThread thread : threads) {
            if (thread != null) {
                thread.join();
                sum += thread.getGenerationCount();
                if (maxValue < thread.getBestFitness()){
                    maxValue = thread.getBestFitness();
                }

            }

        }
        return new Object[]{(sum / this.numberOfRuns), bestGene.getFitness(), bestGene.getData()};
    }

    //Runs simulation for given pc and pm
    public void runSimulation(double pc, double pm) throws InterruptedException, FileNotFoundException {
        this.pc = pc;
        this.pm = pm;
        this.result = runGeneration();
        System.out.println("Average Number of Generations: "+ (int)result[0]);
        System.out.println("Best Fitness: " + result[1]);
        System.out.println("Found function: " + result[2]);

    }

    //Gets the best found parameter combination
    public ParameterValue getBestParameters(){
        ParameterValue bestValue = null;
        for(ParameterValue value : parameterValues){
            if(bestValue==null)
                bestValue = value;
            else if(value.getAverageGens()<bestValue.getAverageGens()){
                bestValue = value;
            }
        }
        return bestValue;
    }

}
