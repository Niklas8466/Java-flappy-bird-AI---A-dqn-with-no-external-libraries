import AI.ComplexCalculator;
import AI.QLearning;
import game.*;

public class Main {
    int height = 1000;
    float highestReward = 0;
    float epsilon = 0.99f;
    float speed = 2;
    FlappyBird game = new FlappyBird(1900, height);
    QLearning dqn = new QLearning(5, 2, 0.0001f, 0.9f);

    public void main(){
        dqn.setAIClippingThreshold(1);
        dqn.setTargetValueRange(-10, 10);
        gameloop();
    }

    public void gameloop(){
        long lastTime = System.currentTimeMillis();
        while (true) {
            long currentTime = System.currentTimeMillis();
            changeSpeed();
            if (currentTime - lastTime >= 1000 / (60*speed)) {

                //get data
                float[] previousState = game.getGameState();
                int score = game.getScore();



                //calculate epsilon based on generation
                epsilon = (float) Math.max(epsilon - 0.00001 , 0.01);

                //choose action
                int action = dqn.chooseAction(previousState, 0.1);
                boolean jump = (action == 1);
                game.update(jump);




                //calculate reward
                float reward = 0.0f;
                //bird died
                if(game.isTerminalState())
                    reward -= 1.0f;
                //bird cleared a pipe
                if (game.getScore() > score)
                    reward += 1.0f;
                //punish him for dying to the top
                if(previousState[0] >= 1)
                    reward -= 10f;


                if(highestReward < extraReward)
                    highestReward = extraReward;


                //get new data
                float[] nextState = game.getGameState();

                //record experience
                dqn.addExperience(previousState, action, reward, nextState, game.isTerminalState());

                //train the network
                dqn.train(16);

                //update information panel
                System.out.println(game.getGeneration());
                if(game.getScore() > game.getHighScore())
                    game.setHighScore(game.getScore());

                System.out.println("Nicht springen: " + ComplexCalculator.roundDouble(dqn.getQValue(previousState,0), 4));
                System.out.println("springen: " +  ComplexCalculator.roundDouble(dqn.getQValue(previousState,1), 4));
                System.out.println("Correct Value: " +  reward);
                System.out.println("epsilon " + epsilon);

                if(game.isTerminalState()){
                    game.setGeneration(game.getGeneration()+1);
                    game.reset();
                }

                lastTime = currentTime;
            }
        }
    }

    private void changeSpeed(){
        if(game.isPressed('d')){
            speed *= 2;
            game.removePressedButton('d');
        }
        else if(game.isPressed('a')){
            speed *= 0.5f;
            game.removePressedButton('a');
        }
        else if(game.isPressed(' ') && speed != 0){
            speed = 0;
            game.removePressedButton(' ');
        }
        else if(game.isPressed(' ') && speed == 0){
            speed = 1;
            game.removePressedButton(' ');
        }


    }


    public static void main(String[] args) {
        new Main().main();
    }
}



