package AI;

import java.util.ArrayList;
import java.util.Random;

public class ReplayBuffer {

    private ArrayList<Experience> replayBuffer;
    private final int maxSize;

    public ReplayBuffer(int maxSize){
        this.maxSize = maxSize;
        replayBuffer = new ArrayList<>();
    }

    public void addExperience(float[] state, int action, float reward, float[] nextState, boolean terminalState){
        Experience experience = new Experience(state, action, reward, nextState, terminalState);
        replayBuffer.add(experience);

        //keeps the List at a max Size, by removing the last entry, if it gets too big
        if(replayBuffer.size() > maxSize)
            replayBuffer.removeLast();
    }

    public int getSize(){
        return replayBuffer.size();
    }

    public Experience[] getRandomBatch(int batchSize){
        if(batchSize > replayBuffer.size())
            throw new RuntimeException("Not enough data for batch size of " + batchSize);

        Random random = new Random();
        Experience[] batch = new Experience[batchSize];

        //fills the batch with random experiences
        for (int i = 0; i < batchSize; i++) {
            int randomIndex = random.nextInt(replayBuffer.size());
            batch[i] = replayBuffer.get(randomIndex);
        }
        return batch;
    }
}
