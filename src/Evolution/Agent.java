package Evolution;

import holdem.PlayerBase;

public class Agent implements Comparable<Agent> {

	public Agent(PlayerBase player) {
		fitness = 0;
		this.player = player;
	}

	public int compareTo(Agent other) {
		if (other.fitness == this.fitness)
			return 0;
		return other.fitness - this.fitness > 0 ? 1 : -1;
	}

	public boolean equals(Object other) {
		if (other instanceof Agent)
			return this.player == ((Agent) other).player;
		return false;
	}

	public double fitness;
	public PlayerBase player;
}
