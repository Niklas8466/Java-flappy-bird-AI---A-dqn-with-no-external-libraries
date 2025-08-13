package AI;

//sometimes the Math library isn't enough
//This class has more complex operations
public class ComplexCalculator {
    public static double roundDouble(double value, int places){
        value *= Math.pow(10, places);
        return Math.round(value) / Math.pow(10, places);
    }

    public static float[][] matrixMultiplikation(float[][] m1, float[][] m2){
        if(m1[0].length != m2.length)
            throw new RuntimeException("Matrix has the wrong size");

        float[][] newMatrix = new float[m1.length][m2[0].length];

        //loping through every value of matrix
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[0].length; j++) {

                //calculating the value
                for (int k = 0; k < m1[0].length; k++) {
                    newMatrix[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return newMatrix;
    }

    public static float clampToRange(float value, float min, float max){
        return Math.max(min, Math.min(value, max));
    }

    public static float calculateVectorNorm(float[] vector, int p){
        //p = infinity => p = max value
        if(p == Integer.MAX_VALUE)
            return getMax(vector);

        float sum = 0f;
        for (int i = 0; i < vector.length; i++) {
            sum += (float) Math.pow(vector[i], p);
        }
        return (float) Math.pow(sum, (double) 1 / p);
    }

    public static float[] matrixVectorMultiplikation(float[][] matrix, float[] vektor){
        if(matrix[0].length != vektor.length)
            throw new RuntimeException("Matrix has the wrong size");

        float[] neuerVektor = new float[matrix.length];
        for (int i = 0; i < neuerVektor.length; i++) {
            for (int j = 0; j < vektor.length; j++) {
                neuerVektor[i] += matrix[i][j] * vektor[j];
            }
        }
        return  neuerVektor;
    }

    public static float[] vectorAddition(float[] v1, float[] v2){
        if(v2.length != v1.length)
            throw new RuntimeException("Vector has the wrong size");

        float[] neuerVektor = new float[v1.length];
        for (int i = 0; i < v1.length; i++) {
            neuerVektor[i] = v1[i] + v2[i];
        }
        return neuerVektor;
    }

    public static void printMatrix(float[][] m){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[j].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printVector(float[] v){
        System.out.print("Vektor: ");
        for (int i = 0; i < v.length; i++) {
            System.out.print(v[i] + " ");
        }
        System.out.println();
    }

    public static float getMax(float[] v){
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < v.length; i++) {
            if(v[i] > max)
                max = v[i];
        }
        return max;
    }

    public static float getMin(float[] v){
        float max = Float.POSITIVE_INFINITY;
        for (int i = 0; i < v.length; i++) {
            if(v[i] < max)
                max = v[i];
        }
        return max;
    }
}
