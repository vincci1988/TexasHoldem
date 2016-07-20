package holdem;

public abstract class ActionBase {
	
	ActionBase(PlayerBase player) {
		this.player = player;
	}
	
	PlayerBase player;
}
