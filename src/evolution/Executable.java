package evolution;

public class Executable {

	public static void main(String[] args) {
		try {
			EvolutionBase evo = new AgentEvolution();
			evo.run();
		} catch (Exception exception) {
			System.out.println(exception);
			exception.printStackTrace();
		}
	}

}
