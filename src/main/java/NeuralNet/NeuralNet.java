package NeuralNet;

import java.io.Serializable;
import java.util.ArrayList;

public class NeuralNet implements Serializable {
    private static final long serialVersionUID = -7108169269793526436L;
    private double[] inputLayer;
    private final double[] hiddenLayerOut;
    private final double[] outputLayerOut;
    private final double[] hiddenLayerErr;
    private final double[] outputLayerErr;
    private ArrayList<Neuron> hiddenLayer;
    private ArrayList<Neuron> outputLayer;
    private double[][] patterns;
    private double[][] answers;

    public NeuralNet(int input, int hidden, int out) {
        inputLayer = new double[input];
        hiddenLayerOut = new double[hidden];
        hiddenLayerErr = new double[hidden];
        outputLayerOut = new double[out];
        outputLayerErr = new double[out];
    }

    public double[][] getPatterns() {
        return patterns;
    }

    public double[] getHiddenLayerOut() {
        return hiddenLayerOut;
    }

    public double[] getHiddenLayerErr() {
        return hiddenLayerErr;
    }

    public double[] getOutputLayerErr() {
        return outputLayerErr;
    }

    public ArrayList<Neuron> getHiddenLayer() {
        return hiddenLayer;
    }

    public ArrayList<Neuron> getOutputLayer() {
        return outputLayer;
    }

    public double[] getOutputLayerOut() {
        return outputLayerOut;
    }

    public double[][] getAnswers() {
        return answers;
    }


    private double[] initWeight(int numOfEl) {
        double[] weight = new double[numOfEl];
        for (int i = 0; i < weight.length; i++) {
            weight[i] = Math.random() * -2 + 1;
        }
        return weight;
    }

    public void initHidden(double[] input) {
        hiddenLayer = new ArrayList<>();
        for (int i = 0; i < hiddenLayerOut.length; i++) {
            hiddenLayer.add(new Neuron(input, initWeight(inputLayer.length)));
        }
    }

    public void initOutputLayer() {
        outputLayer = new ArrayList<>();
        for (int i = 0; i < outputLayerOut.length; i++) {
            outputLayer.add(new Neuron(hiddenLayerOut, initWeight(hiddenLayerOut.length)));
        }
    }

    public void countHiddenLayerOut() {
        int index = 0;
        for (Neuron neuron : hiddenLayer) {
            hiddenLayerOut[index] = neuron.Output();
            //System.out.println("h: "+hiddenLayerOut[index]);
            index++;
        }
    }

    public void countOutput() {
        int index = 0;
        for (Neuron neuron : outputLayer) {
            outputLayerOut[index] = neuron.Output();
            index++;
        }
    }

    public double error(double[] answer) {
        double sum = 0;
        for (int i = 0; i < answer.length; i++) {
            sum += Math.pow(answer[i] - outputLayerOut[i], 2);
        }
        //System.out.println("Err: "+0.5*sum);
        return 0.5 * sum;
    }

    public double[] getOutWeight(int index) {
        double[] weight = new double[answers[0].length];
        int j = 0;
        for (Neuron neuron : outputLayer) {
            weight[j] = neuron.getWeight(index);
            j++;
        }
        return weight;
    }

    public double[] getHiddenWeight(int index) {
        double[] weight = new double[hiddenLayer.size()];
        int j = 0;
        for (Neuron neuron : hiddenLayer) {
            weight[j] = neuron.getWeight(index);
            j++;
        }
        return weight;
    }

    public void setHiddenLayerInputs(double[] hiddenLayerInputs) {
        for (Neuron hiddenNeuron : hiddenLayer) {
            hiddenNeuron.setInputs(hiddenLayerInputs);
        }
    }

    public void setOutputLayerInputs() {
        for (Neuron outputNeuron : outputLayer) {
            outputNeuron.setInputs(hiddenLayerOut);
        }
    }


    public void setPatterns(double[][] patterns) {
        this.patterns = patterns;
    }

    public void setAnswers(double[][] answers) {
        this.answers = answers;
    }

    public void setInputLayer(double[] inputLayer){
        this.inputLayer = inputLayer;
    }

    public double[] prohod(){
        for (Neuron hiddenNeuron: hiddenLayer){
            hiddenNeuron.setInputs(inputLayer);
        }
        countHiddenLayerOut();
        for (Neuron outputNeuron: outputLayer){
            outputNeuron.setInputs(hiddenLayerOut);
        }
        countOutput();
        return getOutputLayerOut();
    }
}
