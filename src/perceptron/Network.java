package perceptron;

import drawer.model.MyImage;

import java.io.*;

import static drawer.model.MyImage.SIZE;
import static perceptron.MathUtils.ReLU;

public class Network {

    private static final int HIDDEN_SIZE = 2;
    private static final int HIDDEN_LENGTH = 16;
    private static final int OUTPUT_LENGTH = 10;
    private static final double START_BIAS = 10;

    private transient File file;

    private double[][][] weights;
    private double[][] biases;

    public Network(String file) throws IOException, ClassNotFoundException {
        this(new File(file));
    }

    public Network(File file) throws IOException, ClassNotFoundException {
        if (!file.exists())
            file.createNewFile();
        this.file = file;
        deserialize();
    }

    public Network() {
        initWeightsAndBiases();
    }

    public void tryToSerialize() {
        try {
            serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serialize() throws IOException {
        FileOutputStream fOutStream = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fOutStream);

        out.writeObject(this);

        out.close();
        fOutStream.close();
    }

    public void tryToDeserialize() {
        try {
            deserialize();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deserialize() throws IOException, ClassNotFoundException {
        FileInputStream fInStream = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fInStream);

        Network buffer = (Network) in.readObject();
        copyToThis(buffer);

        in.close();
        fInStream.close();
    }

    private void copyToThis(Network another) {
        this.weights = another.weights;
        this.biases = biases;
    }

    public void initWeightsAndBiases() {
        initWeights();
        initBiases();
    }

    private void initWeights() {
        int layersCount = 1 + HIDDEN_SIZE + 1;
        weights = new double[layersCount - 1][][];

        int inputToHid = 0;
        int hidToOut = weights.length - 1;

        weights[inputToHid] = new double[SIZE * SIZE][HIDDEN_LENGTH];
        for (int i = 1; i < hidToOut; ++i)
            weights[i] = new double[HIDDEN_LENGTH][HIDDEN_LENGTH];
        weights[hidToOut] = new double[HIDDEN_LENGTH][OUTPUT_LENGTH];
        initWeightsRandomly();
    }

    private void initBiases() {
        int firstEmptyBiasesLayer = 1;
        biases = new double[1 + HIDDEN_SIZE + 1][];
        biases[0] = new double[0];  // just to easy work with indexes
        for (int hiddenLayerInd = 0; hiddenLayerInd < HIDDEN_SIZE; hiddenLayerInd++) {
            biases[hiddenLayerInd] = getBiasesForLayer(HIDDEN_LENGTH);
        }
        int outputLayerIndex = biases.length - 1;
        biases[outputLayerIndex] = getBiasesForLayer(outputLayerIndex);
    }

    private double[] getBiasesForLayer(int size) {
        double[] biases = new double[size];
        for (int bias = 0; bias < size; ++bias)
            biases[bias] = START_BIAS;
        return biases;
    }

    public void doCalculationsAndLearn(MyImage image) {
        double[][] layers = initLayers(image);
        doHiddenMagic(layers);

        int output = layers.length - 1;
        int answer = getMax(layers[output]);
        int expected = image.getLabel();

        double cost = countAverageCost(layers[output], getExpectedOutput(expected));
        System.out.println("answer=" + answer + " expected=" + expected + " cost=" + cost);

        backPropagation(layers, expected);
    }

    private double[][] initLayers(MyImage image) {
        int input = 1;
        int output = 1;
        double[][] layers = new double[input + HIDDEN_SIZE + output][];

        layers[0] = image.toVector();

        for (int hidLayer = 1; hidLayer <= HIDDEN_SIZE; hidLayer++)
            layers[hidLayer] = new double[HIDDEN_LENGTH];

        int outputIndex = layers.length - 1;
        layers[outputIndex] = new double[OUTPUT_LENGTH];

        return layers;
    }

    private void backPropagation(double[][] layers, int expected) {
        int lastLayer = layers.length - 1;
        double[] deltaForPrev = countOutputDelta(layers, expected);
        for(int layerInd = lastLayer; layerInd > 0; layerInd--){
            updateLayer(layers[layerInd], deltaForPrev);
        }
    }

    private double[] countOutputDelta(double[][] layers, int expectedNumber){
        double[] delta = new double[OUTPUT_LENGTH];
        double[] expectedOutput = getExpectedOutput(expectedNumber);
        int outputLayerInd = layers.length - 1;
        double[] actualOutput = layers[outputLayerInd];

        for(int i = 0; i < OUTPUT_LENGTH; ++i)
            delta[i] = expectedOutput[i] - actualOutput[i];

        return delta;
    }

    private void updateLayer(double[] layer, double[] deltaForPrev){
//        for(int neuronIndex = 0; neuronIndex < layer.length; ++neuronIndex)

    }

    private double countAverageCost(double[] actualOutput, double[] expectedOutput) {
        double result = 0;
        for (int i = 0; i < actualOutput.length; ++i) {
            double a = actualOutput[i];
            double b = expectedOutput[i];
            result += (a - b) * (a - b);
        }
        return result;
    }

    private double[] getExpectedOutput(int number) {
        double[] expectedOutput = new double[OUTPUT_LENGTH];
        expectedOutput[number] = 1;
        return expectedOutput;
    }

    private int getMax(double[] output) {
        int maxInd = 0;
        double max = output[maxInd];
        for (int i = 1; i < output.length; ++i)
            if (output[i] > max) {
                max = output[i];
                maxInd = i;
            }
        return maxInd;
    }

    private void doHiddenMagic(double[][] layers) {
        for (int layerIndex = 1; layerIndex < layers.length; ++layerIndex) {
            calculateLayer(layers, layerIndex);
        }
    }

    private void calculateLayer(double[][] layers, int curLayerIndex) {
        int prevLayerIndex = curLayerIndex - 1;
        for (int layerNeuronIndx = 0; layerNeuronIndx < layers[curLayerIndex].length; ++layerNeuronIndx) {
            double sum = calculateNeuronSum(layers, prevLayerIndex, curLayerIndex, layerNeuronIndx);
            layers[curLayerIndex][layerNeuronIndx] = countNeuronActivation(sum);
        }
    }

    private double calculateNeuronSum(double[][] layers, int prevIndex, int index, int secondLayerNeuronIndx) {
        double sum = 0;
        for (int firstLayerNeuronIndx = 0; firstLayerNeuronIndx < layers[prevIndex].length; ++firstLayerNeuronIndx) {
            double ai = layers[firstLayerNeuronIndx][secondLayerNeuronIndx];
            double wi = weights[prevIndex][firstLayerNeuronIndx][secondLayerNeuronIndx];
            sum += ai * wi;
        }
        double z = sum - biases[index][secondLayerNeuronIndx];
        return z;
    }

    private double countNeuronActivation(double sumWithBias) {
        return ReLU(sumWithBias);
    }

    private void initWeightsRandomly() {
        for (int i = 0; i < weights.length; ++i)
            for (int j = 0; j < weights[i].length; ++j)
                for (int k = 0; k < weights[i][j].length; ++k)
                    weights[i][j][k] = getRandomWeight();
    }

    private double getRandomWeight() {
        return (Math.random() - 0.5) * 2;
    }

    private int inputSize() {
        return SIZE * SIZE;
    }

}
