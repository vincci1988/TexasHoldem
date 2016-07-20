package holdem;

import java.util.ArrayList;

public class Pot {
	
	Pot() {
		seats = new ArrayList<Seat>();
		size = 0;
		isFull = false;
	}
	
	Pot(int size, ArrayList<Seat> seats) {
		this.size = size;
		this.seats = new ArrayList<Seat>(seats);
	}
	
	int getPotSize() {
		 return size;
	}
	
	Seat getSeat(int i) {
		return seats.get(i);
	}
	
	int getSeatCnt() {
		return seats.size();
	}
	
	void inc(int amount) {
		size += amount;
	}
	
	private int size;
	boolean isFull;
	ArrayList<Seat> seats;
}
