package AI;

public record Experience(float[] state, int action, float reward, float[] nextState, boolean terminalState) {}