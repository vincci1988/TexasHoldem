package tests_auto;

import static org.junit.Assert.*;

import org.junit.Test;
import holdem.GameTable;
import holdem.TableInfo;

public class TableAndTableInfoTest {

	@Test
	public void testInitialStates() throws Exception {
		GameTable table = new GameTable(100, 10);
		TableInfo info = table.getTableInfo();
		assertTrue("Info: board size.", info.board.length() == 0);
		assertTrue("Info: BB Amt.", info.BBAmt == 200);
		assertTrue("Info: current bet.", info.currentBet == 0);
		assertTrue("Info: min raise.", info.minRaise == 0);
		assertTrue("Info: pot size.", info.potSize == 0);
		assertTrue("Info: playerInfos.size().", info.playerInfos.size() == 0);
		assertTrue("Table.getActivePlayerCnt().", table.getActivePlayerCnt() == 0);
		assertTrue("Table.getPotSize().", table.getPotSize() == 0);
	}
	
	@Test
	public void testPlayerCntCondition() throws Exception {
		GameTable table = new GameTable(100, 10);
		assertTrue(!table.game());
	}

}
