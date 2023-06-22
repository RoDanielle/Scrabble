package model;

import java.util.Arrays;

public class Word{
	Tile tiles[];
	int row;
	int col;
	boolean vertical;
	public Word(Tile[] tilesarr, int row, int col, boolean vertical) {
		super();
		this.tiles = tilesarr;
		this.row = row;
		this.col = col;
		this.vertical = vertical;
	}
	public Tile[] getTiles() {
		return tiles;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isvertical() {
		return vertical;
	}

	/**
  * The equals function checks to see if the object passed in is equal to this Word.
  * It does so by checking that the row, column, and verticality are all equal.
  * Then it checks that each tile in tiles is also present in other's tiles array.
  *
  * @param obj Compare the two objects
  *
  * @return True if the two words are identical
  *
  */
 @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		return col == other.col && row == other.row && Arrays.equals(tiles, other.tiles)
				&& vertical == other.vertical;
	}

}
