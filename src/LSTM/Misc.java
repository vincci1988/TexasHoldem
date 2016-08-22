package LSTM;

import java.util.Random;

public class Misc {
	
	static public double dotProduct(double[] x, double[] y) throws Exception {
		if (x.length != y.length)
			throw new Exception("LSTM.Misc.dotProduct(double[],double[]): INEQUAL VECTOR LENGTH");
		double result = 0;
		for (int i = 0; i < x.length; i++)
			result += x[i] * y[i];
		return result;
	}

	static public double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	static public double[] concat(double[] X, double[] Y) {
		double[] Z = new double[X.length + Y.length];
		int p = 0;
		for (int i = 0; i < X.length; i++)
			Z[p++] = X[i];
		for (int i = 0; i < Y.length; i++)
			Z[p++] = Y[i];
		return Z;
	}
	
	static public double[] head(double[] X, int end) {
		return subArray(X, 0, end);
	}
	
	static public double[] tail(double[] X, int start) {
		return subArray(X, start, X.length - start);
	}
	
	static public double[] subArray(double[] X, int start, int length) {
		double [] Y = new double[length];
		for (int i = 0; i < length; i++) 
			Y[i] = X[start + i];
		return Y;
	}
	
	static public void gaussianInit(double[] X) {
		gaussianInit(X, 0.0, 1.0);
	}
	
	static public void gaussianInit(double[] X, double mean, double var) {
		Random rand = new Random();
		for (int i = 0; i < X.length; i++)
			X[i] = mean + var * rand.nextGaussian();
	}
}
