package model;

import java.util.Objects;
import java.util.Random;

public class Tile {

	public final char letter;
	public final int score;

	private Tile(char letter, int score) {
		super();
		this.letter = letter;
		this.score = score;
	}

	/**
  * The hashCode function is used to generate a unique hash code for each object.
  * This is useful when storing objects in data structures that use hashing, such as HashMaps and HashSets.
  * The hashCode function should return the same value for two objects if they are equal according to the equals method.

  *
  *
  * @return The hash code value for this object
  *
  */
 @Override
	public int hashCode() {
		return Objects.hash(letter, score);
	}

	/**
  * The equals function is used to compare two objects of the same class.
  * It returns true if the two objects are equal, and false otherwise.
  * In this case, we want to return true if both tiles have the same letter and score.

  *
  * @param obj Compare the current object to another object
  *
  * @return True if the two objects are equal
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
		Tile other = (Tile) obj;
		return letter == other.letter && score == other.score;
	}

	public static class Bag {

		private static Bag _instance = null;
		int lettercount[];
		int copycount[];
		Tile tilearr[];
		public int sum = 98;

		private Bag() {
			lettercount  = new int[] {9,2,2,4,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1};
			copycount = new int[26];
			System.arraycopy(lettercount, 0, copycount,0,26);
			tilearr = new Tile[26];
			tilearr[0] = new Tile('A', 1);
			tilearr[1] = new Tile('B', 3);
			tilearr[2] = new Tile('C', 3);
			tilearr[3] = new Tile('D', 2);
			tilearr[4] = new Tile('E', 1);
			tilearr[5] = new Tile('F', 4);
			tilearr[6] = new Tile('G', 2);
			tilearr[7] = new Tile('H', 4);
			tilearr[8] = new Tile('I', 1);
			tilearr[9] = new Tile('J', 8);
			tilearr[10] = new Tile('K', 5);
			tilearr[11] = new Tile('L', 1);
			tilearr[12] = new Tile('M', 3);
			tilearr[13] = new Tile('N', 1);
			tilearr[14] = new Tile('O', 1);
			tilearr[15] = new Tile('P', 3);
			tilearr[16] = new Tile('Q', 10);
			tilearr[17] = new Tile('R', 1);
			tilearr[18] = new Tile('S', 1);
			tilearr[19] = new Tile('T', 1);
			tilearr[20] = new Tile('U', 1);
			tilearr[21] = new Tile('V', 4);
			tilearr[22] = new Tile('W', 4);
			tilearr[23] = new Tile('X', 8);
			tilearr[24] = new Tile('Y', 4);
			tilearr[25] = new Tile('Z', 10);
		}

		/**
   * The getBag function is a static function that returns the singleton instance of the Bag class.
   *
   *
   *
   * @return A reference to the bag object
   *
   */
  public static Bag getBag() {
			if(_instance == null)
			{
				_instance = new Bag();
			}
			return _instance;
		}

		/**
   * The getRand function returns a random tile from the bag.
   *
   *
   *
   * @return A random tile from the bag
   *
   */
  public Tile getRand() {
			if(sum == 0)
				return null;
			else {
				int rand;
				do {
					rand = new Random().nextInt(lettercount.length);
				} while (lettercount[rand] == 0);
				lettercount[rand] = lettercount[rand] - 1;
				sum--;
				return tilearr[rand];
			}
		}

		/**
   * The getTile function takes in a character and returns the tile object associated with that character.
   *
   *
   * @param cha Determine which tile to return
   *
   * @return A tile with the given letter
   *
   */
  public Tile getTile(char cha){
			if(Character.isUpperCase(cha)) {
				if(lettercount[cha - 'A'] == 0)
					return null;
				else {
					lettercount[cha - 'A'] = lettercount[cha - 'A'] - 1;
					sum--;
					return tilearr[cha - 'A'];
				}
			}
			else
				return null;
		}

		/**
   * The put function adds a tile to the bag.
   *
   *
   * @param t Add a tile to the bag
   *
   *
   */
  public void put(Tile t){
			if(lettercount[t.letter- 'A'] < copycount[t.letter- 'A'])
			{
				lettercount[t.letter- 'A'] = lettercount[t.letter- 'A'] + 1;
				sum++;
			}
		}

		/**
   * The size function returns the number of elements in the list.
   *
   * @return the size
   */
  public int size() {
			return sum;
		}

		/**
   * The getQuantities function returns an array of integers that represents the number of times each letter appears in the text.
   *
   *
   * @return An array of integers that contains the number of times each letter occurs in the text
   *
   */
  public int[] getQuantities() {
			int Quantities[];
			Quantities = new int[26];
			System.arraycopy(lettercount, 0, Quantities,0,26);
			return Quantities;
		}
	}
}
