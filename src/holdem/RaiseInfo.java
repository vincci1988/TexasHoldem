package holdem;

public class RaiseInfo extends ActionInfoBase {

	RaiseInfo(ActionBase action, TableInfo info) {
		super(action, info);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " RAISE ($"
				+ amt + ") <END PLAYER ACTION>";
	}
}
