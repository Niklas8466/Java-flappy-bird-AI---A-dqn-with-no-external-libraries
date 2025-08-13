package AI;

import java.util.ArrayList;
import java.util.Random;

public class QLearning {
    private final float discountFactor;
    private final int actionAmount;
    private final AI mainNetwork;
    private AI targetNetwork;
    private final ReplayBuffer replayBuffer;
    private final int maxBufferSize = 50000;
    private final int updateTargetNetworkAmount = 1000;
    private float minTargetValue = Float.MIN_VALUE;
    private float maxTargetValue = Float.MAX_VALUE;

    private int updateTargetNetworkCounter;

    public QLearning(int inputSize, int actionAmount, float learningRate, float discountFactor){
        this.discountFactor = discountFactor;
        this.actionAmount = actionAmount;

        int[] layers = {inputSize, 8, actionAmount};
        mainNetwork = new AI(layers, learningRate);
        targetNetwork = new AI(layers, learningRate);

        replayBuffer = new ReplayBuffer(maxBufferSize);
    }

    //returns the Q Value of the action possible in the state
    public double getQValue(float[] state, int action){
        return mainNetwork.forwardPropagation(state)[action];
    }

    //returns the best possible Q value you can get, by using the best action in that state
    public double getBestQValue(float[] state){
        float[] outputLayer = mainNetwork.forwardPropagation(state);
        int highestIndex = 0;
        for (int i = 0; i < outputLayer.length; i++) {
            if(outputLayer[highestIndex] < outputLayer[i])
                highestIndex = i;
        }
        return outputLayer[highestIndex];
    }

    //returns the best possible Q value you can get, by using the best action in that state
    private double getBestTargetQValue(float[] state){
        float[] outputLayer = targetNetwork.forwardPropagation(state);
        int highestIndex = 0;
        for (int i = 0; i < outputLayer.length; i++) {
            if(outputLayer[highestIndex] < outputLayer[i])
                highestIndex = i;
        }
        return outputLayer[highestIndex];
    }

    //1. play an episode and add experience to replay buffer
    //2. create random batch
    //3. calculate Target Value
    //4. train main network on the random batch
    //5. have a counter on the train Method
    //6. Everytime the counter reaches the refreshing Amount copy the main network into the target network
    public void train(int batchSize){
        //checks if there even are enough experiences in the replay buffer
        if(replayBuffer.getSize() < batchSize)
            return;

        //creating the batch
        Experience[] batch = replayBuffer.getRandomBatch(batchSize);

        //training everything in the batch
        for (int i = 0; i < batch.length; i++) {
            //calculating target value
            float target = calculateTargetValue(batch[i]);
            target = ComplexCalculator.clampToRange(target, minTargetValue, maxTargetValue);
            mainNetwork.train(batch[i].state(), batch[i].action(), target);

            //occasionally update the target network
            updateTargetNetworkCounter++;
            if(updateTargetNetworkCounter == updateTargetNetworkAmount){
                targetNetwork = mainNetwork.clone();
                updateTargetNetworkCounter = 0;
            }
        }
    }

    //targetQ = reward + γ * max(targetNetwork.forward(nextState));
    private float calculateTargetValue(Experience experience){
        if(experience.terminalState())
            return experience.reward();
        return (float) (experience.reward() + discountFactor * getBestTargetQValue(experience.nextState()));
    }

    //return all the best actions in the state, if multiple actions are equally good
    //If one is the best it returns the best action in an array with the size of
    private int[] getBestActions(float[] state){
        //saves the best actions in an ArrayList
        double highestValue = getBestQValue(state);
        ArrayList<Integer> bestActions = new ArrayList<>();
        for (int i = 0; i < actionAmount; i++) {
            if(getQValue(state, i) == highestValue){
                bestActions.add(i);
            }
        }

        //turns ArrayList into an array
        int[] actionsArray = new int[bestActions.size()];
        for (int i = 0; i < actionsArray.length; i++) {
            actionsArray[i] = bestActions.get(i);
        }
        return actionsArray;
    }

    //returns the best action. If multiple actions are the best, it will return the one with the first index
    private int getBestAction(float[] state){
        mainNetwork.forwardPropagation(state);
        return mainNetwork.getChoice();
    }

    //This function chooses either best or random move based on given epsilon value [1,0]
    public int chooseAction(float[] state, double epsilon){
        Random random = new Random();
        //Epsilon greedy function
        if(random.nextDouble() <= epsilon){
            //choose randomly
            return random.nextInt(actionAmount);
        }
        else{
            return getBestAction(state);
        }
    }

    public void addExperience(float[] state, int action, float reward, float[] nextState, boolean terminalState){
        replayBuffer.addExperience(state, action, reward, nextState, terminalState);
    }

    public void setAIClippingThreshold(float clippingThreshold){
        mainNetwork.setClippingThreshold(clippingThreshold);
        targetNetwork.setClippingThreshold(clippingThreshold);
    }

    //prevents crazy target values
    //If the values don't get set, the values aren't getting clipped
    public void setTargetValueRange(float min, float max){
        minTargetValue = min;
        maxTargetValue = max;
    }
}
