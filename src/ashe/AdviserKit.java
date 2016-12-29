package ashe;

class AdviserKit {
	
	static int getStage(String board) {
		if (board.length() == 0)
			return 0;
		if (board.length() == 6)
			return 1;
		if (board.length() == 8)
			return 2;
		if (board.length() == 10)
			return 3;
		return -1;
	}
	
	static double smooth(double occurrence, int maxCnt) {
		return occurrence >= maxCnt ? 0 : 1.0 - occurrence * 1.0 / maxCnt;
	}
	
	static double prob(double m1, double dev, double x) {
		if (x <= m1 - 3 * dev) return 0;
		double ans = 0;
		double dy = 0.0001;
		double f1 = dy / dev / Math.sqrt(2 * Math.PI);
		double f2 = 2 * dev * dev;
		for (double y = m1 - 3 * dev; y < x; y += dy) 
			ans += f1 * Math.exp(-Math.pow(y - m1, 2) / f2);
		return ans;	
	}
}
