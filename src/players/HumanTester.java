package players;

import holdem.Call;
import holdem.Check;
import holdem.Fold;
import holdem.AllIn;
import holdem.Raise;
import holdem.Result;

import java.util.Scanner;

import holdem.ActionBase;
import holdem.ActionInfoBase;
import holdem.PlayerBase;
import holdem.TableInfo;

public class HumanTester extends PlayerBase {

	public HumanTester(int id, String name) {
		super(id);
		this.name = name;
	}

	@Override
	public ActionBase getAction(TableInfo info) {
		System.out.println(info.toString());
		System.out.println(getName() + ":");
		System.out.println("Hole Cards: " + this.peek());
		System.out.println("Please decide: [0]Fold, [1]Check, [2]Call, [3]Raise, [4]All-In");
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		int decision = keyboard.nextInt();
		if (decision == 1) {
			System.out.println();
			return new Check(this);
		}
		if (decision == 2) {
			System.out.println();
			return new Call(this);
		}
		if (decision == 3) {
			System.out.println("How much would you like to raise to?");
			int amt = keyboard.nextInt();
			System.out.println();
			return new Raise(this, amt);
		}
		if (decision == 4) {
			System.out.println();
			return new AllIn(this);
		}
		System.out.println();
		return new Fold(this);
	}

	@Override
	public void observe(ActionInfoBase actionInfo) {
		if (actionInfo.playerID != this.id) System.out.println(actionInfo);
	}

	@Override
	public void observe(Result resultInfo) {
		System.out.println(resultInfo);		
	}

	@Override
	public String getName() {
		return name + " (ID = " + id +")";
	}
	
	private String name;
}
