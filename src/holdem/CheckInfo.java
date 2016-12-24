package holdem;

public class CheckInfo extends ActionInfoBase {

	CheckInfo(ActionBase action, TableInfo info) {
		super(action, info);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " CHECK ($"
				+ amt + ") <END PLAYER ACTION>";
	}
}
