package econ;

public class Goods {
	public enum Good {
		WHEAT (20, 1),
		MEAT (10, 0.5),
		LUMBER (8, 0.2),
		OIL (12, 0.4),
		CLOTH (15, 1.5),
		METAL (15, 1),
		TOOLS (40, 4);
	
	
	private final double initialUtil;
	private final double utilDimRate;
	
	Good(double initialUtil, double utilDimRate) {
		this.initialUtil = initialUtil;
		this.utilDimRate = utilDimRate;
	}
	
	public double util(int units) {
		return initialUtil - ((double) units * utilDimRate);
	}
	
	}
}
