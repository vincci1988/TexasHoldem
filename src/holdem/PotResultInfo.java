package holdem;

import java.util.ArrayList;

public class PotResultInfo {
	
	PotResultInfo(int potSize, int winnerCnt, ArrayList<Hand> sortedHands) {
		this.potSize = potSize;
		this.winnerCnt = winnerCnt;
		this.handInfos = new ArrayList<HandInfo>();
		for (int i = 0; i < sortedHands.size(); i++) {
			handInfos.add(new HandInfo(sortedHands.get(i)));
		}
	}
	
	public String toString() {
		String report = "Pot Size = " + potSize + "\n";
		for (int i = 0; i < handInfos.size(); i++) {		
			report += handInfos.get(i).toString();
			if (i < winnerCnt) report += " (WINNER: $" + potSize / winnerCnt + ")";
			report += "\n";
		}
		return report;
	}
	
	public int potSize;
	public int winnerCnt;
	public ArrayList<HandInfo> handInfos;
}
