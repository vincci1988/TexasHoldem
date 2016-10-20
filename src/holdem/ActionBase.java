package holdem;

public abstract class ActionBase {
	
	ActionBase(PlayerBase player) {
		this.player = player;
		this.bet = player.getMyBet();
	}
	
	PlayerBase player;
	int bet;
}
