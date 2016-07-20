package holdem;

public class CallInfo extends ActionInfoBase {

	CallInfo(ActionBase action, Board board) {
		super(action, board);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " CALL ($" + amt
				+ ") <END PLAYER ACTION>";
	}
}
