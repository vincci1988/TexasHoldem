package holdem;

public class AllInInfo extends ActionInfoBase {

	AllInInfo(ActionBase action, TableInfo info) {
		super(action, info);
	}

	@Override
	public String toString() {
		return "<BEGIN: PLAYER ACTION> " + getStage()
				+ (boardForDisplay.length() == 0 ? ", " : ": " + boardForDisplay + ", ") + playerName + " ALL-IN ($"
				+ amt + ") <END PLAYER ACTION>";
	}
}
