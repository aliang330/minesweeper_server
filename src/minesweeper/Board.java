package minesweeper;
/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */


import java.util.Random;

import minesweeper.Square.SquareState;
/**
 * TODO: Specification
 */
public class Board {

	Square[][] board;
	int x;
	int y;
	int bombs = 5;

	public Board(int x, int y) {
		this.x = x;
		this.y = y;
		board = new Square[x][y];
		fillBoard();
	}
	
	public boolean newBoard(int x, int y, int bombs) {
		if (validateNewBoard(x, y, bombs)) {
			board = new Square[x][y];
			this.x = x;
			this.y = y; 
			this.bombs = bombs;
			fillBoard();
			return true;
		} else {
			return false;
		}
		
	}

	private void fillBoard() {
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				if (new Random().nextInt(bombs) == 0) {
					board[i][j] = new Square(SquareState.BOMB);
				} else {
					board[i][j] = new Square(SquareState.UNTOUCHED);
				}
			}
		}
	}

	private int getAdjacentBombs(int x , int y) {
		int count = 0;

		int[][] neighbors = {{x-1, y-1}, {x-1, y}, {x-1, y+1}, {x, y-1}, {x, y+1},
				{x+1, y-1}, {x+1, y}, {x+1, y+1}};

		for (int[] coord : neighbors) {
			int coordX = coord[0];
			int coordY = coord[1];
//			System.out.printf("x: %d, y: %d\n", coordX, coordY);
			if (validateCoords(coordX, coordY)) {
				Square square = board[coordX][coordY];
				if (square.isBomb()) {
					count += 1;
				}
			}
		}
		return count;
	}
	
	private boolean validateNewBoard(int x, int y, int bombs) {
		if (x > 0 && x < 100 && y > 0 && y < 100 && bombs > 1 && bombs < 100) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean validateCoords(int x, int y) {
		if (x >= this.x || x < 0 || y >= this.y || y < 0) {
			 return false;
		} else {
			return true;
		}
	}

	private void digAdjacent(int x, int y) {
		int[][] neighbors = {{x-1, y-1}, {x-1, y}, {x-1, y+1}, {x, y-1}, {x, y+1},
				{x+1, y-1}, {x+1, y}, {x+1, y+1}};

		for (int [] coord : neighbors) {
			int coordX = coord[0];
			int coordY = coord[1];
			if (validateCoords(coordX, coordY)) {
				Square square = board[coordX][coordY];
				if (square.state == SquareState.UNTOUCHED) {
					square.dig();
					square.adjacentBombs = getAdjacentBombs(coordX, coordY);
					if (square.adjacentBombs == 0) {
						digAdjacent(coordX, coordY);
					}
				}
			}
		}
	}
	
	public boolean checkBoard() {
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				Square square = board[i][j];
				if (square.isUntouched()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void getNewBoard() {
		fillBoard();
	}

	public boolean dig(int x, int y) throws IllegalArgumentException {
		if (!validateCoords(x, y)) {
			throw new IllegalArgumentException();
		}
		Square square = board[x][y];
		switch (square.state) {
		case UNTOUCHED:
			square.dig();
			square.adjacentBombs = getAdjacentBombs(x,y);
			if (square.adjacentBombs == 0) {
				digAdjacent(x,y);
			}
			checkBoard();
			return false;
		case BOMB:

			return true;
		case DUG:
			return false;
		default:
			return false;
		}
	}
	
	public boolean toggleFlag(int x, int y, boolean isFlag) {
		if (validateCoords(x, y)) {
			Square square = board[x][y];
			return square.toggleFlag(isFlag);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		String boardString = "\r\n\r\n   ";
		for (int xAxis = 0; xAxis < this.x; xAxis++) {
			boardString += String.format("\033[31m%d\033[0m ", xAxis);
		}
		boardString += "\r\n";
		int yAxis = 0;
		for (int i = 0; i < this.x; i++) {
			if (i < 10) {
				boardString += String.format("\033[36m%d\033[0m  ", yAxis); //yAxis + "  "; 
			} else {
				boardString += String.format("\033[36m%d\033[0m ", yAxis);
			}
			yAxis++;
			for (int j = 0; j < this.y; j++) {
				Square square = this.board[i][j];
				if (j > 9) {
					boardString += " ";
				}
				if ((square.isUntouched() || square.isBomb()) && square.isFlagged() ) {
					boardString += "\033[41mF\033[0m ";
				} else if (square.isBomb() || square.isUntouched()) {
					boardString += "- ";
				} else if (square.isDug()) {
					int adjBombs = square.adjacentBombs;
					switch (adjBombs) {
					case 1:
						boardString += String.format("\033[34m%d\033[0m ", adjBombs);
						break;
					case 2:
						boardString += String.format("\033[32m%d\033[0m ", adjBombs);
						break;
					case 3:
						boardString += String.format("\033[31m%d\033[0m ", adjBombs);
						break;
					case 4:
						boardString += String.format("\033[34m%d\033[0m ", adjBombs);
						break;
					case 5:
						boardString += String.format("\033[91m%d\033[0m ", adjBombs);
						break;
					default:
						boardString += adjBombs + " ";
						break;
					}
				}
			}
			boardString += "\r\n";
			
		}
		
		
//		System.out.println(boardString);
//
//		boardString = "";
//		for (int i = 0; i < this.x; i++) {
//			for (int j = 0; j < this.y; j++) {
//				Square square = this.board[i][j];
//				if (square.state == SquareState.UNTOUCHED) {
//					boardString += getAdjacentBombs(i, j) + " ";
//				} else if (square.isBomb()) {
//					boardString += "B ";
//				} else if (square.state== SquareState.DUG) {
//					boardString += square.adjacentBombs + " ";
//				}
//			}
//			boardString += "\n";
//		}
		System.out.println(boardString);
		

		return boardString;
	}
	
	public String getRawBoard() {
		String rawBoardString = "";
		
		for (int i = 0; i < this.x; i++) {
			for (int j = 0; j < this.y; j++) {
				Square square = board[i][j];
				if (square.isFlagged()) {
					rawBoardString += "F ";
				} else {
					switch (square.state) {
					case UNTOUCHED:
						rawBoardString += "- ";
						break;
					case BOMB:
						rawBoardString += "- ";
						break;
					case DUG:
						rawBoardString += square.adjacentBombs + " ";
					default:
						break;
					}
				}
			}
			rawBoardString += "\n";
		}
		return rawBoardString;
	}
}
