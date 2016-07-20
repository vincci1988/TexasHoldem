package holdem;

public class FoldInfo extends ActionInfoBase {

	FoldInfo(ActionBase action, Board board) {
		super(action, board);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " FOLD "
				+ "<END PLAYER ACTION>";
	}

}
