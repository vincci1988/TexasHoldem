package stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import holdem.*;

public class HandStrengthDataBaseConstructor {
	
	static public void MASSCompute_ConstructDB(String path) throws Exception {
		if (System.getProperty("os.name").contains("Windows")) separator = "\\";
		else separator = "/";
		File root = new File(path + separator + "HSDB");
		root.mkdir();
		String rootPath = root.getAbsolutePath();
		String riverPath = rootPath + separator + "river";
		File river = new File(riverPath);
		river.mkdir();
		String turnPath = rootPath + separator + "turn";
		File turn = new File(turnPath);
		turn.mkdir();
		String flopPath = rootPath + separator + "flop";
		File flop = new File(flopPath);
		flop.mkdir();
		String preflopPath = rootPath + separator + "preflop";
		File preflop = new File(preflopPath);
		preflop.mkdir();
		MASSCompute_RiverStrength(riverPath);
		MASSCompute_TurnStrength(turnPath, riverPath);
		MASSCompute_FlopStrength(flopPath, riverPath);
		MASSCompute_PreflopStrength(preflopPath, flopPath);
	}

	static private void MASSCompute_RiverStrength(String path) throws Exception {
		int start = 0;
		int end = 48;
		int total = 0;
		for (int i1 = start; i1 < end; i1++)
			for (int i2 = i1 + 1; i2 < 49; i2++)
				for (int i3 = i2 + 1; i3 < 50; i3++)
					for (int i4 = i3 + 1; i4 < 51; i4++)
						for (int i5 = i4 + 1; i5 < 52; i5++)
							total++;
		int onePercent = total / 100;
		System.out.println("MASSComputation (River): i1 = [" + start + ", " + end + ")...");
		int cnt = 0;
		for (int i1 = start; i1 < end; i1++) {
			for (int i2 = i1 + 1; i2 < 49; i2++) {
				for (int i3 = i2 + 1; i3 < 50; i3++) {
					for (int i4 = i3 + 1; i4 < 51; i4++) {
						for (int i5 = i4 + 1; i5 < 52; i5++) {
							Deck deck = new Deck();
							Board board = new Board();
							board.add(deck.get(i1));
							board.add(deck.get(i2));
							board.add(deck.get(i3));
							board.add(deck.get(i4));
							board.add(deck.get(i5));		
							for (int i = 0; i < board.size(); i++)
								deck.remove(board.get(i));
							ArrayList<Hand> hands = new ArrayList<Hand>();
							for (int i6 = 0; i6 < deck.size() - 1; i6++) {
								for (int i7 = i6 + 1; i7 < deck.size(); i7++) {
									HoleCards holeCards = new HoleCards(deck.get(i6), deck.get(i7));
									hands.add(Judge.getBestHand(board, holeCards));
									Collections.sort(hands);
								}
							}
							PrintWriter writer = new PrintWriter(path + separator + board + ".txt");			
							for (int i = 0; i < hands.size(); i++) {
								int better = 0;
								HoleCards holeCards = hands.get(i).getHoleCards();
								for (int j = 0; j < i; j++) {
									if (hands.get(j).compareTo(hands.get(i)) < 0
											&& !(hands.get(j).getHoleCards().contains(holeCards.getHighCard())
													|| hands.get(j).getHoleCards().contains(holeCards.getKicker())))
										better++;
								}
								writer.println(hands.get(i).getHoleCards() + " " + better);
							}
							writer.close();
							if (++cnt % onePercent == 0)
								System.out.println(
										"Computing i1 = [" + start + ", " + end + "): " + (cnt / onePercent) + "%");
						}
					}
				}
			}
		}
		System.out.println("Job Completed!");
	}

	static private void MASSCompute_TurnStrength(String path, String riverDBPath) throws Exception {
		int start = 0;
		int end = 49;
		int total = 0;
		for (int i1 = start; i1 < end; i1++)
			for (int i2 = i1 + 1; i2 < 50; i2++)
				for (int i3 = i2 + 1; i3 < 51; i3++)
					for (int i4 = i3 + 1; i4 < 52; i4++)
						total++;
		int onePercent = total / 100;
		System.out.println("MASSComputation (Turn): i1 = [" + start + ", " + end + ")...");
		int cnt = 0;
		for (int i1 = start; i1 < end; i1++) {
			for (int i2 = i1 + 1; i2 < 50; i2++) {
				for (int i3 = i2 + 1; i3 < 51; i3++) {
					for (int i4 = i3 + 1; i4 < 52; i4++) {
						Deck deck = new Deck();
						Board board = new Board();
						board.add(deck.get(i1));
						board.add(deck.get(i2));
						board.add(deck.get(i3));
						board.add(deck.get(i4));
						for (int i = 0; i < board.size(); i++)
							deck.remove(board.get(i));
						HoleCardsStrengthData holeCardsStrengths = new HoleCardsStrengthData();
						for (int i = 0; i < deck.size(); i++) {
							Board current = new Board();
							current.addAll(board);
							current.add(deck.get(i));
							FileReader fr = new FileReader(riverDBPath + separator + current + ".txt");
							BufferedReader reader = new BufferedReader(fr);
							for (int j = 0; j < riverDataFileLineCnt; j++) {
								String line = reader.readLine();
								String holeCards = line.substring(0, 4);
								int rank = Integer.parseInt(line.substring(5));
								holeCardsStrengths.update(holeCards, rank);
							}
							reader.close();
						}
						holeCardsStrengths.finalize(turnHoleCardOccurences);
						FileWriter fileWriter = new FileWriter(path + separator + board + ".txt");
						BufferedWriter writer = new BufferedWriter(fileWriter);
						for (int i = 0; i < holeCardsStrengths.size(); i++) {
							writer.write(holeCardsStrengths.get(i) + "\n");
						}
						writer.close();
						if (++cnt % onePercent == 0)
							System.out.println(
									"Computing i1 = [" + start + ", " + end + "): " + (cnt / onePercent) + "%");
					}
				}
			}
		}
		System.out.println("Job Completed!");
	}

	static private void MASSCompute_FlopStrength(String path, String turnDBPath) throws Exception {
		int start = 0;
		int end = 50;
		int total = 0;
		for (int i1 = start; i1 < end; i1++)
			for (int i2 = i1 + 1; i2 < 51; i2++)
				for (int i3 = i2 + 1; i3 < 52; i3++)
					total++;
		int onePercent = total / 100;
		System.out.println("MASSComputation (Flop): i1 = [" + start + ", " + end + ")...");
		int cnt = 0;
		for (int i1 = start; i1 < end; i1++) {
			for (int i2 = i1 + 1; i2 < 51; i2++) {
				for (int i3 = i2 + 1; i3 < 52; i3++) {
					Deck deck = new Deck();
					Board board = new Board();
					board.add(deck.get(i1));
					board.add(deck.get(i2));
					board.add(deck.get(i3));
					for (int i = 0; i < board.size(); i++)
						deck.remove(board.get(i));
					HoleCardsStrengthData holeCardsStrengths = new HoleCardsStrengthData();
					for (int i = 0; i < deck.size(); i++) {
						Board current = new Board();
						current.addAll(board);
						current.add(deck.get(i));
						FileReader fr = new FileReader(turnDBPath + separator + current + ".txt");
						BufferedReader reader = new BufferedReader(fr);
						for (int j = 0; j < turnDataFileLineCnt; j++) {
							String line = reader.readLine();
							String holeCards = line.substring(0, 4);
							float rank = Float.parseFloat(line.substring(5));
							holeCardsStrengths.update(holeCards, rank);
						}
						reader.close();
					}
					holeCardsStrengths.finalize(flopHoleCardOccurences);
					FileWriter fileWriter = new FileWriter(path + separator + board + ".txt");
					BufferedWriter writer = new BufferedWriter(fileWriter);
					for (int i = 0; i < holeCardsStrengths.size(); i++) {
						writer.write(holeCardsStrengths.get(i) + "\n");
					}
					writer.close();
					if (++cnt % onePercent == 0)
						System.out.println("Computing i1 = [" + start + ", " + end + "): " + (cnt / onePercent) + "%");
				}
			}
		}
		System.out.println("Job Completed!");
	}

	static private void MASSCompute_PreflopStrength(String path, String flopDBPath) throws Exception {
		System.out.println("MASSComputation (Preflop)...");
		HoleCardsStrengthData holeCardsStrengths = new HoleCardsStrengthData();
		Deck deck = new Deck();
		int total = 0;
		for (int j1 = 0; j1 < 50; j1++)
			for (int j2 = j1 + 1; j2 < 51; j2++)
				for (int j3 = j2 + 1; j3 < 52; j3++)
					total++;
		int onePercent = total / 100;
		int cnt = 0;
		for (int j1 = 0; j1 < 50; j1++) {
			for (int j2 = j1 + 1; j2 < 51; j2++) {
				for (int j3 = j2 + 1; j3 < 52; j3++) {
					Board board = new Board();
					board.add(deck.get(j1));
					board.add(deck.get(j2));
					board.add(deck.get(j3));
					FileReader fr = new FileReader(flopDBPath + separator + board + ".txt");
					BufferedReader reader = new BufferedReader(fr);
					for (int j = 0; j < flopDataFileLineCnt; j++) {
						String line = reader.readLine();
						String holeCards = line.substring(0, 4);
						float rank = Float.parseFloat(line.substring(5));
						holeCardsStrengths.update(holeCards, rank);
					}
					reader.close();
					if (++cnt % onePercent == 0)
						System.out.println("Computing: " + cnt / onePercent + "%");
				}
			}
		}
		holeCardsStrengths.finalize(preflopHoleCardOccurences);
		FileWriter fileWriter = new FileWriter(path + separator + "preflop.txt");
		BufferedWriter writer = new BufferedWriter(fileWriter);
		for (int i = 0; i < holeCardsStrengths.size(); i++) {
			writer.write(holeCardsStrengths.get(i).toString().substring(0, 11) + "\n");
		}
		writer.close();
		System.out.println("Job Completed!");
	}

	static private final int flopDataFileLineCnt = 1176;
	static private final int turnDataFileLineCnt = 1128;
	static private final int riverDataFileLineCnt = 1081;
	static private final int preflopHoleCardOccurences = 19600;
	static private final int flopHoleCardOccurences = 47;
	static private final int turnHoleCardOccurences = 46;
	static private String separator;
}
