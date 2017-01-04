package ASHE;

import holdem.ActionBase;
import holdem.TableInfo;

abstract class EstimatorBase {
	
	EstimatorBase(Ashe ashe) {
		this.ashe = ashe;
	}
	
	abstract double estimate(TableInfo info, Intel intel, double handStrength, ActionBase action) throws Exception;
	
	Ashe ashe;
}
