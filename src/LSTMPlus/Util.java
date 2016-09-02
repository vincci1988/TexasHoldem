package LSTMPlus;

import java.util.Random;

public class Util {
	
	public static double dotProduct(double[] x, double[] y) throws Exception {
		double result = 0;
		for (int i = 0; i < x.length; i++)
			result += x[i] * y[i];
		return result;
	}
	
	public static double[] pointWiseMultiply(double[] x, double[] y) {
		double[] z = new double[x.length];
		for (int i = 0; i < x.length; i++) 
			z[i] = x[i] * y[i];
		return z;
	}
	
	public static double[] multiply(double[] x, double a) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) 
			y[i] = x[i] * a;
		return y;
	}
	
	public static double[] multiply(double[][] A, double[] x) throws Exception {
		double[] y = new double[A.length];
		for (int i = 0; i < A.length; i++)
			y[i] = Util.dotProduct(x, A[i]);
		return y;
	}
	
	public static double[] add(double[] x, double[] y) {
		double[] z = new double[x.length];
		for (int i = 0; i < x.length; i++) 
			z[i] = x[i] + y[i];
		return z;
	}

	public static double sigmoid(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	public static double[] sigmoid(double[] x) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++)
			y[i]= sigmoid(x[i]);
		return y;
	}
	
	public static double tanh(double x) {
		return Math.tanh(x);
	}
	
	public static double[] tanh(double[] x) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++)
			y[i]= Util.tanh(x[i]);
		return y;
	}
	
	public static double[] serialize(double[][] A) {
		double[] serialized = null;
		for (int i = 0; i < A.length; i++)
			serialized = concat(serialized, A[i]);
		return serialized;
	}
	
	public static double[] concat(double[] X, double[] Y) {
		if (X == null && Y == null) return null;
		if (X == null) return head(Y, Y.length);
		if (Y == null) return head(X, X.length);
		double[] Z = new double[X.length + Y.length];
		int p = 0;
		for (int i = 0; i < X.length; i++)
			Z[p++] = X[i];
		for (int i = 0; i < Y.length; i++)
			Z[p++] = Y[i];
		return Z;
	}
	
	public static double[] head(double[] X, int length) {
		return subArray(X, 0, length);
	}
	
	public static double[] tail(double[] X, int length) {
		return subArray(X, X.length - length, length);
	}
	
	public static double[] subArray(double[] X, int start, int length) {
		double [] Y = new double[length];
		for (int i = 0; i < length; i++) 
			Y[i] = X[start + i];
		return Y;
	}
	
	public static void gaussianInit(double[] X) {
		gaussianInit(X, 0.0, 1.0);
	}
	
	public static void gaussianInit(double[] X, double mean, double var) {
		Random rand = new Random();
		for (int i = 0; i < X.length; i++)
			X[i] = mean + var * rand.nextGaussian();
	}
	
	public static void gaussianInit(double[][] A) {
		for (int i = 0; i < A.length; i++) 
			gaussianInit(A[i]);
	}
	
	public static void gaussianInit(double[][] A, double mean, double var) {
		for (int i = 0; i < A.length; i++) 
			gaussianInit(A[i], mean, var);
	}
	
	static int initByGenome(double[] vector, double[] genome, int start) {
		for (int i = 0; i < vector.length; i++)
			vector[i] = genome[start++];
		return start;
	}

	static int initByGenome(double[][] matrix, double[] genome, int start) {
		for (int i = 0; i < matrix.length; i++) 
			start = initByGenome(matrix[i], genome, start);
		return start;
	}
}
