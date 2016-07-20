package tests_manual;

public class Test {

	/**
	 * Instruction:
	 * 1. CREATE INTERACTIVE TEST CLASS
	 * 2. CALL "RUN"
	 */
	public static void main(String[] args) {
		try {
			HumanTableManualTest test = new HumanTableManualTest();
			//TournamentTableManualTest test = new TournamentTableManualTest();
			test.run();
		} catch (Exception exception) {
			System.out.println(exception);
		}
	}
}
