package experiments;

public class Executable {

	public static void main(String[] args) {
		/**
		 * Instruction: 
		 * 1. CREATE EXPERIMENT CLASS 
		 * 2. CALL "RUN"
		 */
		try {
			HeadsUpQueryEvaluation test = new HeadsUpQueryEvaluation("NLHeadsUpPerformance.txt", "NLHeadsUpGameLog.txt");
			test.run();
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}

	}

}
