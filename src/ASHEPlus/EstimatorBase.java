package ASHEPlus;

import holdem.ActionBase;
import holdem.TableInfo;

abstract class EstimatorBase {
	
	EstimatorBase(AshePlus ashe) {
		this.ashe = ashe;
	}
	
	abstract double estimate(TableInfo info, Intel intel, double handStrength, ActionBase action) throws Exception;
	
	AshePlus ashe;
}
