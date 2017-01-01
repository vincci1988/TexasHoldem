package ASHE;

public interface Statistician {
	static StrengthEvaluator evaluator = new StrengthEvaluator(AsheParams.HSDBPath);
}
