package LSTM;

public class Test {

	public static void main(String[] args) {

		try {
			Cell lstm = new Cell(3);
			double[] x = new double[3];
			x[0] = 0.25;
			x[1] = -1;
			x[2] = 0.75;
			for (int i = 0; i < 3; i++)
				System.out.println(lstm.activate(x));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
