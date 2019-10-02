package NeuralNet;

import java.io.Serializable;

public class Neuron implements Serializable {
    private static final long serialVersionUID = 1429440418997085430L;
    private double[] inputs;
    private double[] weights;

    Neuron(double[] inputs, double[] weights) {
        this.inputs = inputs;
        this.weights = weights;
    }

    private double activator(double[] i, double[] w) {
        double sum = 0;
        for (int j = 0; j < i.length; j++) {
            sum += i[j] * w[j];
        }
        return Math.pow(1 + Math.exp(-sum), -1);
    }

    public double[] getInputs() {
        return inputs;
    }

    public double[] getWeights() {
        return weights;
    }

    double getWeight(int index) {
        return weights[index];
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    void setInputs(double[] inputs) {
        this.inputs = inputs;
    }

    public double Output() {
        return activator(inputs, weights);
    }

    private double derivative() {
        return Output() * (1 - Output());
    }

    public double errorOutNeuron(double f, double goal) {
        return f * (1 - f) * (goal - f);
    }

    public double errorHiddenNeuron(double[] weight, double[] er) {
        double sum = 0;
        for (int j = 0; j < weight.length; j++) {
            sum += weight[j] * er[j];
        }
        return derivative() * sum;
    }
}
