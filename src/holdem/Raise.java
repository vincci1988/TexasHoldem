package holdem;

public class Raise extends ActionBase {
	
	public Raise(PlayerBase player, int amt) {
		super(player);
		raiseToAmt = amt;
	}
	
	public int getAmt() {
		return raiseToAmt;
	}
	
	int raiseToAmt;
}
