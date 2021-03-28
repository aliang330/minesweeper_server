package minesweeper;


public class Square {
	public enum SquareState {
		BOMB, UNTOUCHED, DUG
	}

	SquareState state;
	private boolean isFlag = false;
	
	int adjacentBombs;

	public Square(SquareState state) {
		this.state = state;
	}
	
	public boolean isBomb() {
		return state.equals(SquareState.BOMB);
	}
	
	public boolean dig() {
		if (state == SquareState.UNTOUCHED) {
			state = SquareState.DUG;
			return false;
		} else if (state == SquareState.BOMB) {
			return true;
		} else {
			 return false;
		}
	}
	
	public boolean toggleFlag(boolean isFlag) {
		if (state.equals(SquareState.UNTOUCHED) || state.equals(SquareState.BOMB)) {
			this.isFlag = isFlag;
			return true;
		} else {
			 return false;
		}
	}
	
	public boolean isFlagged() {
		return this.isFlag;
	}
	
	public boolean isUntouched() {
		return state.equals(SquareState.UNTOUCHED);
	}
	
	public boolean isDug() {
		 return state.equals(SquareState.DUG);
	}

//	public void dig() {
//		switch (state) {
//		case UNTOUCHED:
//			System.out.println("DIG");
//			break;
//		case BOMB:
//			System.out.println("BOOM");
//			break;
//		case FLAG:
//			System.out.println("FLAG");
//			break;
//		case DUG:
//			System.out.println("ALREADY DUG");
//			break;
//		default:
//			System.out.println("dig error");
//			break;
//		}
//	}
}
