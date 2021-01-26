package Exercise3.Genetics.Models;

//Object to store average needed generation for each parameter
public class ParameterValue {
    private final double pc;
    private final double pm;
    private final int averageGens;

    public ParameterValue(double pc, double pm, int averageGens) {
        this.pc = pc;
        this.pm = pm;
        this.averageGens = averageGens;
    }

    public double getPc() {
        return pc;
    }

    public double getPm() {
        return pm;
    }

    public int getAverageGens() {
        return averageGens;
    }
}