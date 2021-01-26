package Exercise3;

import Exercise3.Genetics.Enums.Protection;
import Exercise3.Genetics.Enums.RecombinationType;
import Exercise3.Genetics.Enums.ReplicationScheme;
import Exercise3.Genetics.Models.GeneSet;
import Exercise3.Genetics.UI.SimulationGui;

public class Run {
    public static SimulationGui window;
    public static void main(String[] args) {
        try {
            GeneSet gs = new GeneSet("05-map-10x10-36border.txt" ,10, 200, 1000, 36, 50, ReplicationScheme.NONE, RecombinationType.GREEDY_CROSSOVER, Protection.NONE);
            //gs.runSimulation(0.9,0.05);
            long start = System.currentTimeMillis();
            window = new SimulationGui("Simulation");
            window.setVisible(true);
            //gs.findIdealParameters(0.85, 0.9, 0.05, 0, 0.01, 0.005);
            //System.out.println(gs.getBestParameters().getPm()+";"+gs.getBestParameters().getPc()+";"+gs.getBestParameters().getAverageGens());
            long end = System.currentTimeMillis();
            System.out.println("The process finished after ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
