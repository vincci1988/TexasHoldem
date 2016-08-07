package players;

public interface Statistician {
	//public static final String HSDBPath = "/Volumes/My Passport/HSDB";
	public static final String HSDBPath = "B:\\HSDB";
	static HandStrengthEvaluator evaluator = new HandStrengthEvaluator(HSDBPath);
}
