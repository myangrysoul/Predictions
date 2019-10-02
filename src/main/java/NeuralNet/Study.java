package NeuralNet;


import Parser.Match.Binary;
import Parser.Patterns;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Study implements Serializable {
    private final NeuralNet net1;
    private final double step = 0.25;

    public Study(NeuralNet net1) {
        this.net1 = net1;
    }

    public void initNet(Binary scaler) {
        Patterns patterns1 = scaler.toPatterns();
        net1.setAnswers(patterns1.getOutputs());
        net1.setPatterns(patterns1.getInputs());
        System.out.println(patterns1);
        double[][] patterns = net1.getPatterns();
        net1.initHidden(patterns[0]);
        net1.countHiddenLayerOut();
        net1.initOutputLayer();
    }

    public void study() {
        double[][] patterns = net1.getPatterns();
        double gErr;
        do {
            gErr = 0;
            for (int numOfPatterns = 0; numOfPatterns < patterns.length; numOfPatterns++) {
                net1.setHiddenLayerInputs(patterns[numOfPatterns]);
                net1.countHiddenLayerOut();
                net1.setOutputLayerInputs();
                net1.countOutput();
                gErr += net1.error(net1.getAnswers()[numOfPatterns]);
                for (int idx = 0; idx < net1.getOutputLayer().size(); idx++) {
                    Neuron neuron = net1.getOutputLayer().get(idx);
                    net1.getOutputLayerErr()[idx] =
                            neuron.errorOutNeuron(neuron.Output(), net1.getAnswers()[numOfPatterns][idx]);
                }
                for (int idx = 0; idx < net1.getHiddenLayer().size(); idx++) {
                    Neuron neuron = net1.getHiddenLayer().get(idx);
                    net1.getHiddenLayerErr()[idx] =
                            neuron.errorHiddenNeuron(net1.getOutWeight(idx), net1.getOutputLayerErr());
                }

                for (int idx = 0; idx < net1.getOutputLayer().size(); idx++) {
                    Neuron neuron = net1.getOutputLayer().get(idx);
                    double[] outputWeight = neuron.getWeights();
                    double[] newWeight = new double[outputWeight.length];
                    for (int j = 0; j < outputWeight.length; j++) {
                        Neuron hiddenNeuron = net1.getHiddenLayer().get(j);
                        System.out.println(net1.getOutputLayerErr()[idx] * hiddenNeuron.Output());
                        newWeight[j] = outputWeight[j] + step * net1.getOutputLayerErr()[idx] * hiddenNeuron.Output();
                    }
                    neuron.setWeights(newWeight);
                }
                for (int idx = 0; idx < net1.getHiddenLayer().size(); idx++) {
                    Neuron neuron = net1.getHiddenLayer().get(idx);
                    double[] hiddenWeight = neuron.getWeights();
                    double[] newWeight = new double[hiddenWeight.length];
                    double[] inputNeuron = patterns[numOfPatterns];
                    for (int j = 0; j < hiddenWeight.length; j++) {
                        newWeight[j] = hiddenWeight[j] + step * net1.getHiddenLayerErr()[idx] * inputNeuron[j];
                    }
                    neuron.setWeights(newWeight);
                }
            }
            //System.out.println("Error - " + gErr);
            if (gErr < 10) {
                try {
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("NetTEST.ser"));
                    os.writeObject(net1);
                    System.out.println("File saved");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } while (true);
    }
}
