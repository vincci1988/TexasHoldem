package holdem;

public class FoldInfo extends ActionInfoBase {

	FoldInfo(ActionBase action, TableInfo info) {
		super(action, info);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " FOLD "
				+ "<END PLAYER ACTION>";
	}

}
