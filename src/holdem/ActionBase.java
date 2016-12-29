package holdem;

public abstract class ActionBase {
	
	ActionBase(PlayerBase player) {
		this.player = player;
		this.bet = player.getMyBet();
	}
	
	public int getBet() {
		return bet;
	}
	
	PlayerBase player;
	int bet;
}
