package LSTM;

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
	
	static public double tanh(double x) {
		return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
	}
}
