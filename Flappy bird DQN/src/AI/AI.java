package AI;

public class AI {
    private float[][] layers;
    private float[][][] weights;
    private float[][] biases;
    private final float learningRate;
    private final int[] layerSizes;
    private float clippingThreshold = Float.MAX_VALUE;

    public AI(int[] layerSizes, float learningRate) {
        this.layerSizes = layerSizes;

        //layers initialisation
        this.learningRate = learningRate;
        this.layers = new float[layerSizes.length][];
        for (int i = 0; i < layers.length; i++) {
            layers[i] = new float[layerSizes[i]];
        }

        //weight initialisation
        this.weights = new float[layerSizes.length-1][][];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = new float[layerSizes[i+1]][layerSizes[i]];
            //random start-value using the xavier-initialisation
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = (float) ((Math.random() - 0.5) * 2 / Math.sqrt(layerSizes[i]));
                }
            }
        }

        //biases initialisation
        this.biases = new float[layerSizes.length-1][];
        for (int i = 0; i < biases.length; i++) {
            biases[i] = new float[layerSizes[i+1]];
            //everything is set to 0
        }

        //debug
        //printLayerSizes();
    }

    public void train(float[] inputLayer,int targetIndex, float targetValue){
        //checks for NaN values (I have an exploding gradients problem) and shuts down the program if necessary
        checkForNaN();

        //calculate output layer
        forwardPropagation(inputLayer);
        float[] outputLayer = layers[layers.length-1];

        //MSE = (current Solution - correct Value)²
        //Just for checking, if the AI is getting better
        float loss = (float) Math.pow(outputLayer[targetIndex] - targetValue, 2);
        System.out.println("loss: " + loss);

        //The backpropagation, works by using a full output layer and not just one Value
        //We set the desired values of the other values to the output layer, so nothing gets changed except for the value we want
        float[] trueVektor = outputLayer.clone();
        trueVektor[targetIndex] = targetValue;

        //calculate the differences of the correct values and the actual ones
        float[][] errorVektors = backPropagation(trueVektor);

        updateParameters(errorVektors);
    }

    public float[] forwardPropagation(float[] inputLayer){
        layers[0] = inputLayer.clone();
        //start at 1 so the input layer doesn't get changed
        for (int i = 1; i < layers.length; i++) {
            float[] weightedSums = ComplexCalculator.matrixVectorMultiplikation(weights[i-1], layers[i-1]);
            float[] preActivation = ComplexCalculator.vectorAddition(weightedSums, biases[i-1]);

            //hidden layers
            if(i != layers.length - 1){
                layers[i] = relu(preActivation);
            }else{ //output layer
                layers[i] = preActivation;
            }

        }
        return layers[layers.length-1];
    }

    @Override
    public AI clone(){
        AI clonedAI = new AI(this.layerSizes, this.learningRate);

        //copy weights
        for (int i = 0; i < this.weights.length; i++) {
            for (int j = 0; j < this.weights[i].length; j++) {
                for (int k = 0; k < this.weights[i][j].length; k++) {
                    clonedAI.weights[i][j][k] = this.weights[i][j][k];
                }
            }
        }

        //copy biases
        for (int i = 0; i < this.biases.length; i++) {
            for (int j = 0; j < this.biases[i].length; j++) {
                clonedAI.biases[i][j] = this.biases[i][j];
            }
        }

        return clonedAI;
    }

    //calculates deltas for weights and biases
    private float[][] backPropagation(float[] trueVektor) {
        int L = layers.length;
        float[][] delta = new float[L][];

        delta[L - 1] = new float[layers[L - 1].length];
        for (int i = 0; i < delta[L - 1].length; i++) {
            delta[L - 1][i] = layers[L - 1][i] - trueVektor[i];
        }

        for (int l = L - 2; l > 0; l--) {
            delta[l] = new float[layers[l].length];
            for (int i = 0; i < delta[l].length; i++) {
                float sum = 0f;
                for (int j = 0; j < delta[l + 1].length; j++) {
                    sum += weights[l][j][i] * delta[l + 1][j];
                }
                delta[l][i] = sum * reluDerivative(layers[l][i]);
            }
        }
        return delta;
    }

    //calculates the new values of the weights and biases
    private void updateParameters(float[][] delta) {
        //loping through the layers
        for (int l = 1; l < layers.length; l++) {
            for (int i = 0; i < delta[l].length; i++) {
                //update the biases
                biases[l - 1][i] -= learningRate * delta[l][i];

                //update the weights
                for (int j = 0; j < layers[l - 1].length; j++) {
                    float gradient = learningRate * delta[l][i] * layers[l - 1][j];

                    //gradient clipping because of exploding gradients
                    gradient = gradientClippingByNorm(new float[]{gradient})[0];

                    weights[l - 1][i][j] -= gradient;
                }
            }
        }
    }

    //clips the gradient above the clipping threshold, so the gradients don't explode
    private float[] gradientClippingByNorm(float[] vector){
        float[] newVector = vector.clone();
        float norm = ComplexCalculator.calculateVectorNorm(newVector, 2);
        if(norm > clippingThreshold){
            for (int i = 0; i < newVector.length; i++) {
                newVector[i] = clippingThreshold * newVector[i]/norm;
            }
        }
        return newVector;
    }

    //activation function relu = max(0,n)
    private float[] relu(float[] layer){
        float[] neuerLayer = new float[layer.length];
        for (int i = 0; i < layer.length; i++) {
            neuerLayer[i] = Math.max(layer[i], 0f);
        }
        return neuerLayer;
    }

    private float reluDerivative(float x) {
        if(x > 0f){
            return 1f;
        }
        return 0.001f;
    }

    private void printLayerSizes(){
        System.out.print("layer sizes: ");
        for (int i = 0; i < layers.length; i++) {
            System.out.print(layers[i].length + " ");
        }
        System.out.println();
    }

    //exploding gradients is a big problem in my code, this leads to value becoming NaN
    //this function shuts down the program, if the networks values aren't numbers anymore
    private void checkForNaN(){
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    if(Float.isNaN(weights[i][j][k])){
                        ComplexCalculator.printMatrix(biases);
                        System.err.println("Fehler: Weight ist NaN");
                        System.exit(0);
                    }


                }
            }
        }
    }

    public void setClippingThreshold(float clippingThreshold) {
        this.clippingThreshold = clippingThreshold;
    }

    //returns the index of the highest value
    public int getChoice(){
        int top = 0;
        for (int i = 0; i < layers[layers.length-1].length; i++) {
            if(layers[layers.length-1][i] > layers[layers.length-1][top]){
                top = i;
            }
        }
        return top;
    }
}