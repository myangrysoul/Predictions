package Parser;

import java.util.ArrayList;

public class Patterns {
    ArrayList<ArrayList<Double>> inputs;
    ArrayList<Double> outputs;

    public Patterns(ArrayList<ArrayList<Integer>> inputs, ArrayList<Double> outputs) {
        this.inputs = new ArrayList<>();
        ArrayList<Double> buf;
        for (ArrayList<Integer> a :
                inputs) {
            buf = new ArrayList<>();
            for (Integer integer :
                    a) {
                buf.add(Double.parseDouble(integer.toString()));
            }
            this.inputs.add(buf);
        }
        this.outputs = new ArrayList<Double>(outputs);
    }

    public double[][] getInputs() {
        double[][] mas = new double[inputs.size()][inputs.get(0).size()];
        for (int i = 0; i < inputs.size(); i++) {
            for (int c = 0; c < inputs.get(0).size(); c++) {
                mas[i][c] = inputs.get(i).get(c);
            }
        }
        return mas;
    }

    public double[][] getOutputs() {
        double[][] mas = new double[outputs.size()][10];
        int a;
        for (int i = 0; i < outputs.size(); i++) {
            a = (int) Math.round(outputs.get(i) * 10);
            for (int c = 0; c < 10; c++) {
                switch (Math.abs(a - c)) {
                case 0:
                    mas[i][c] = 0.8;
                    break;
                case 1:
                    mas[i][c] = 0.07;
                    break;
                case 2:
                    mas[i][c] = 0.03;
                    break;
                default:
                    mas[i][c] = 0;
                    break;
                }
            }
        }
        return mas;
    }


    @Override
    public String toString() {
        return inputs.size() + "\n" + inputs.get(0).size() + "\n" + outputs.size() + "\n" + outputs;
    }
}