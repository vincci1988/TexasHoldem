package LSTMPlus;

public class Test {

	public static void main(String[] args) {
		try {
			REM rem = new REM(200);
			double[][] X = new double[200][200];
			Util.gaussianInit(X, 0, 0.5);
			double[] w = new double[200];
			Util.gaussianInit(w, 0, 0.0025);
			double[] y = Util.tanh(Util.multiply(X, w));
			for (int i = 0; i < 500; i++) {
				double err = 0;
				for (int j = 0; j < X.length; j++) {
					double o = rem.activate(X[j]);
					rem.memorize(X[j]);
					err += (o - y[j]) * (o - y[j]) / 2;
					rem.BP(y[j]);
					rem.reset();
				}
				System.out.println(i + ": " + err);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
