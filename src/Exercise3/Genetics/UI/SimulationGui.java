package Exercise3.Genetics.UI;

import Exercise3.Genetics.Enums.Protection;
import Exercise3.Genetics.Enums.RecombinationType;
import Exercise3.Genetics.Enums.ReplicationScheme;
import Exercise3.Genetics.Models.GeneSet;
import Exercise3.Genetics.UI.Elements.DoubleSpinner;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SimulationGui extends JFrame {

    private JSlider gencntSlider;
    private JPanel genetischeAlgorithmen;
    private JTextField gencntValue;
    private JTextField maxGenValue;
    private JTextField numOfRunsValue;
    private JSlider maxGenSlider;
    private JSlider numOfRunsSlider;
    private JComboBox<ReplicationScheme> replicationSchemes;
    private JComboBox<RecombinationType> recombinationType;
    private JComboBox<Protection> protections;
    private JSlider pmSlider;
    private JSlider pcSlider;
    private JTextField pmValue;
    private JTextField pcValue;
    private DoubleSpinner pcStart;
    private DoubleSpinner pcEnd;
    private DoubleSpinner pcStep;
    private DoubleSpinner pmStart;
    private DoubleSpinner pmEnd;
    private DoubleSpinner pmStep;
    private JButton findParametersButton;
    private JButton runSimulationButton;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JLabel resultLabel1;
    private JLabel resultLabel2;
    private JLabel resultLabel3;
    private JLabel resultLabel4;
    private JLabel resultLabel5;
    private JSpinner spinnerS;
    private JTextField acceptedFitnessText;
    private JComboBox citiesComboBox;
    private JSlider mapSizeSlider;
    private JTextField mapSizeText;

    private int mapSize;
    private String city;
    private int genecnt;
    private int genelen;
    private int maxgenerations;
    private double initrate;
    private double acceptedFitness;
    private int numberOfRuns;
    private double pc;
    private double pm;
    private RecombinationType crossingOverMethod;
    private ReplicationScheme replicationScheme;
    private Protection protection;
    double pcStartValue;
    double pcEndValue;
    double pcStepValue;
    double pmStartValue;
    double pmEndValue;
    double pmStepValue;

    public SimulationGui(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(genetischeAlgorithmen);
        this.pack();
        spinnerS.setVisible(false);
        spinnerS.setValue(2);
        addChangeListenerToSlider(gencntSlider, gencntValue);
        addChangeListenerToSlider(maxGenSlider, maxGenValue);
        addChangeListenerToSlider(numOfRunsSlider, numOfRunsValue);
        addChangeListenerToSliderSmallPercent(pmSlider, pmValue);
        addChangeListenerToSliderSmallPercent(pcSlider, pcValue);
        addChangeListenerToSlider(mapSizeSlider, mapSizeText);
        pcStart.setValue(0.5);
        pcEnd.setValue(0.9);
        pcStep.setValue(0.02);
        pmStart.setValue(0.0);
        pmEnd.setValue(0.03);
        pmStep.setValue(0.002);
        ReplicationScheme[] replicationSchemesArr = ReplicationScheme.class.getEnumConstants();
        RecombinationType[] recombinationTypesArr = RecombinationType.class.getEnumConstants();
        Protection[] protectionsArr = Protection.class.getEnumConstants();
        for (ReplicationScheme scheme : replicationSchemesArr)
            replicationSchemes.addItem(scheme);
        for (RecombinationType type : recombinationTypesArr)
            recombinationType.addItem(type);
        for (Protection protection : protectionsArr)
            protections.addItem(protection);
        File folder = new File("cities/");
        ArrayList<String> citiesFiles = listFilesForFolder(folder);
        for(String cityFile : citiesFiles){
            citiesComboBox.addItem(cityFile);
        }



        runSimulationButton.addActionListener(e -> {
            clearResults();
            progressBar.setValue(0);
            progressBar.setString("Working...");
            progressBar.setStringPainted(true);
            readData();
            GeneSet gs = null;
            try {
                gs = new GeneSet(city ,mapSize, genecnt, maxgenerations, acceptedFitness, numberOfRuns, replicationScheme, crossingOverMethod, protection);
                long startTime = System.currentTimeMillis();
                gs.runSimulation(pc, pm);
                long endTime = System.currentTimeMillis();
                progressBar.setString("Complete");
                progressBar.setValue(100);
                double[] result = gs.getResult();
                resultLabel1.setText("Result:");
                resultLabel2.setText("Average Generations: " + result[0]);
                resultLabel3.setText("Highest Value: " + result[1]);
                resultLabel4.setText("Time: " + (int) ((endTime - startTime) / 1000 / 60) + ":" + (int) ((endTime - startTime) / 1000 % 60));
            } catch (Exception interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        findParametersButton.addActionListener(e -> {
            readData();
            clearResults();
            progressBar.setStringPainted(false);
            GeneSet.progress = 0;
            GeneSet gs;
            try {
                gs = new GeneSet(city ,mapSize, genecnt, maxgenerations, acceptedFitness, numberOfRuns, replicationScheme, crossingOverMethod, protection);

                SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        long startTime = System.currentTimeMillis();
                        gs.findIdealParameters(pcStartValue, pcEndValue, pcStepValue, pmStartValue, pmEndValue, pmStepValue);
                        long endTime = System.currentTimeMillis();

                        resultLabel1.setText("The best found Parameters are:");
                        resultLabel2.setText("Mutation Rate: " + gs.getBestParameters().getPm());
                        resultLabel3.setText("Recombination Rate: " + gs.getBestParameters().getPc());
                        resultLabel4.setText("Average Generations: " + gs.getBestParameters().getAverageGens());
                        resultLabel5.setText("Time: " + (int) ((endTime - startTime) / 1000 / 60) + ":" + (int) ((endTime - startTime) / 1000 % 60));
                        return null;
                    }
                };
                worker.execute();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

    }

    private void clearResults() {
        resultLabel5.setText("");
        resultLabel4.setText("");
        resultLabel3.setText("");
        resultLabel2.setText("");
        resultLabel1.setText("");
    }

    private void readData() {
        this.genecnt = Integer.parseInt(gencntValue.getText());
        this.maxgenerations = Integer.parseInt(maxGenValue.getText());
        this.pc = Double.parseDouble(pcValue.getText().replace("%", "")) / 100d;
        this.pm = Double.parseDouble(pmValue.getText().replace("%", "")) / 100d;
        try{
            this.acceptedFitness = Double.parseDouble(acceptedFitnessText.getText());
        }catch (Exception e){
            this.acceptedFitness = 0;
        }
        this.mapSize = mapSizeSlider.getValue();
        this.crossingOverMethod = (RecombinationType) recombinationType.getSelectedItem();
        this.replicationScheme = (ReplicationScheme) replicationSchemes.getSelectedItem();
        this.protection = (Protection) protections.getSelectedItem();
        this.numberOfRuns = Integer.parseInt(numOfRunsValue.getText());
        this.pcStartValue = (double) pcStart.getValue();
        this.pcEndValue = (double) pcEnd.getValue();
        this.pcStepValue = (double) pcStep.getValue();
        this.pmStartValue = (double) pmStart.getValue();
        this.pmEndValue = (double) pmEnd.getValue();
        this.pmStepValue = (double) pmStep.getValue();
        this.city = (String) citiesComboBox.getSelectedItem();
    }

    private void addChangeListenerToSlider(JSlider slider, JTextField label) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            label.setText(String.valueOf(value));
        });

    }

    private void addChangeListenerToSliderPercent(JSlider slider, JTextField label) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            label.setText(value + "%");
        });

    }

    public ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> fileNames = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileNames.add(fileEntry.getName());
            }
        }
        return fileNames;
    }

    private void addChangeListenerToSliderSmallPercent(JSlider slider, JTextField label) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            label.setText((double) value / 10d + "%");
        });

    }

    public void setProgressValue(int value) {
        this.progressBar.setValue(value);
    }

    public void setProgressLabel(int progress, int amount) {
        progressLabel.setText(progress + "/" + amount + " Parameters tested (" + (int) ((double) progress / (double) amount * 100d) + "%)");
    }
}

