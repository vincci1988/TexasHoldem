package opponent_model;

class NodeStats {

	NodeStats() {
		frequency = 0;
		oppFold = 0;
		myFold = 0;
		showdown = 0;
		oSDH_M = 0;
		oSDH_SoS = 0;
		cReward = 0;
		significance = 0;
	}

	NodeStats(String str) {
		String[] stats = str.split(",");
		frequency = Integer.parseInt(stats[0]);
		oppFold = Integer.parseInt(stats[1]);
		myFold = Integer.parseInt(stats[2]);
		showdown = Integer.parseInt(stats[3]);
		oSDH_M = Double.parseDouble(stats[4]);
		oSDH_SoS = Double.parseDouble(stats[5]);
		cReward = Double.parseDouble(stats[6]);
		significance = Double.parseDouble(stats[7]);
	}

	public String toString() {
		String serial = "";
		serial += frequency + ",";
		serial += oppFold + ",";
		serial += myFold + ",";
		serial += showdown + ",";
		serial += oSDH_M + ",";
		serial += oSDH_SoS + ",";
		serial += cReward + ",";
		serial += significance;
		return serial;
	}
	
	String display() {
		String report = "";
		report += "freq = " + frequency + "\n";
		report += "oppFold = " + oppFold + "\n";
		report += "myFold = " + myFold + "\n";
		report += "showdown = " + showdown + "\n";
		report += "oSDH_M = " + oSDH_M + "\n";
		report += "oSDH_SoS = " + oSDH_SoS + "\n";
		report += "cReward(BB) = " + cReward;
		return report;
	}

	int frequency;
	int oppFold;
	int myFold;
	int showdown;
	double oSDH_M; // Opponent hand strength mean at showdown
	double oSDH_SoS; // Opponent hand strength sum-of-square at showdown
	double cReward;
	double significance;
}
