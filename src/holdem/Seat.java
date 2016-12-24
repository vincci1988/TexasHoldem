package holdem;

public class Seat implements Comparable<Seat> {

	public Seat() {
		player = null;
		bet = 0;
		stack = 0;
		active = false;
		holeCards = null;
	}

	public boolean isEmpty() {
		return player == null;
	}

	@Override
	public int compareTo(Seat other) {
		return this.bet - other.bet;
	}

	void mount(PlayerBase player, int buyInAmt) {
		this.player = player;
		stack = buyInAmt;
		player.dec(buyInAmt);
	}

	boolean unmount() {
		if (active)
			return false;
		player.deposit(stack);
		stack = 0;
		player = null;
		return true;
	}

	String getHoleCards() {
		return holeCards == null ? "UNKNOWN" : holeCards.toString();
	}

	void deal(Deck deck) throws Exception {
		if (!isEmpty()) {
			holeCards = new HoleCards(deck.draw(), deck.draw());
			holeCards.seat = this;
			active = true;
		}
	}
	
	void deal(String cards) throws Exception {
		if (!isEmpty()) {
			if(cards.length() == 4) {
				holeCards = new HoleCards(new Card(cards.substring(0,2)), new Card(cards.substring(2,4)));
				holeCards.seat = this;
			}
			else holeCards = null;
			active = true;
		}
	}

	ActionBase request(int amt) {
		if (player == null || !active)
			return null;
		return player.give(amt);
	}

	ActionBase playerMove(TableInfo info) throws Exception {
		if (active && stack > 0)
			return player.move(info);
		return null;
	}
	
	void receive(ActionBase action, TableInfo info) {
		if (player != null) player.observe(action, info);
	}
	
	void receive(Result resultInfo) throws Exception {
		if (player != null) player.observe(resultInfo);
	}

	PlayerBase clear() {
		holeCards = null;
		bet = 0;
		active = false;
		if (stack == 0 && player != null) {
			player.seat = null;
			PlayerBase removed = player;
			player = null;
			return removed;
		}
		return null;
	}

	PlayerBase player;
	int stack;
	int bet;
	boolean active;
	HoleCards holeCards;
	

}
