package holdem;

public class CallInfo extends ActionInfoBase {

	CallInfo(ActionBase action, TableInfo info) {
		super(action, info);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " CALL ($" + amt
				+ ") <END PLAYER ACTION>";
	}
}
