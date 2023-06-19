package model;
import java.util.ArrayList;

public class Board {

	private static Board _instance = null;
	Tile TilesBoard[][];


	/**
  * The Board function creates a new board object and initializes the tilesBoard matrix.
  *
  *
  * @return void, create a new object
  */
 	private Board()
	{
		TilesBoard = new Tile[15][15];
	}

 	/**
   * The getBoard function is a static function that returns the current instance of the Board class.
   * If there is no current instance, it creates one and then returns it.
   *
   *
   * @return The singleton instance of board
   *
   * @docauthor Trelent
   */
  	public static Board getBoard() {
		if(_instance == null)
		{
			_instance = new Board();
		}
		return _instance;
	}

	/**
  * The getTiles function returns a copy of the TilesBoard matrix.
  *
  *  @return A copy of the tile board
  */
 	public Tile[][] getTiles()
	{
		Tile copyboard[][];
		copyboard = new Tile[15][15];
		for(int i = 0; i < TilesBoard.length;i++)
		{
			System.arraycopy(TilesBoard[i], 0, copyboard[i], 0, TilesBoard[i].length);
		}

		return copyboard;
	}

	/**
  * The enoughSpace function checks to see if the word can be placed on the board.
  * It does this by checking if there is enough space for it to fit in either a vertical or horizontal direction.
  *
  *
  * @param word Check if the word does not exceed the limits of the board
  *
  * @return True if the word can be placed on the board
  */
 	public boolean enoughSpace(Word word)
	{
		if(word.isvertical()) // check vertical space (col j moves)
		{
			if(word.row + word.tiles.length > 15)
				return false;
		}

		else // check horizontal space (row i moves)
		{
			if(word.col + word.tiles.length > 15)
				return false;
		}

		return true; // enough space
	}

	// next five function check if new words were created around our word (parallel to out word)

	/**
  * The checkAbove function checks if there is a tile or a whole word above the word provided to it.
  *
  *
  * @param word Get the row and column of the word
  *
  * @return True if there is a tile or word above the vigen word
  */
 	public boolean checkAbove(Word word)
	{
		int i = word.col;
		while(i <= word.col + word.tiles.length)
		{
			if(TilesBoard[word.row - 1][i] != null)
				return true;
			i++;
		}
		return false;
	}

	/**
  * The checkUnder function checks to see if there is a tile or a whole word below the word that is being placed.
  *
  *
  * @param  word Get the row and column of the word
  *
  * @return True if there is a tile under the word
  */
 	public boolean checkUnder(Word word)
	{
		int i = word.col;
		while(i <= word.col + word.tiles.length)
		{
			if(TilesBoard[word.row + 1][i] != null)
				return true;
			i++;
		}
		return false;
	}


	/**
  * The checkLeft function checks to see if there is a tile to the left of the word.
  *
  *
  * @param  word Pass the word object that is being checked, using its placement
  *
  * @return True if the left side of the word is not empty, there are other tiles on the left of the given word.
  */
 	public boolean checkLeft(Word word)
	{
		int i = word.row;
		while(i <= word.row + word.tiles.length)
		{
			if(TilesBoard[i][word.col - 1] != null)
				return true;
			i++;
		}
		return false;
	}

	/**
  * The checkRight function checks to see if there is a tile in the right of the word.
  *
  *
  * @param  word Pass the word object into the function, using its placement
  *
  * @return True if the word can be placed, there is a word or a tile to the right of the given word
  */
 	public boolean checkRight(Word word)
	{
		int i = word.row;
		while(i <= word.row + word.tiles.length)
		{
			if(TilesBoard[i][word.col + 1] != null)
				return true;
			i++;
		}
		return false;
	}

	/**
  * The checkCorners function checks to see if the word is placed on the board in a legal manner.
  * It does this by checking to see if there are any tiles adjacent to it, and returns true or false accordingly.
  *
  *
  * @param word Pass the word object to be checked
  *
  * @return True if the there are other words or tiles in the corners of the given word
  */
 	public boolean checkCorners(Word word)
	{
		if((word.col == 0 || word.row == 0) && word.tiles.length == 15)
			return false; // no letters at edges
		else
		{
			if(word.isvertical())
			{
				if(word.row == 0)
				{
					//check only under
					if(TilesBoard[word.row + word.tiles.length][word.col] != null)
						return true;
					else
						return false;

				}
				else if(word.row + word.tiles.length == 14)
				{
					// check only above
					if(TilesBoard[word.row - 1][word.col] != null)
						return true;
					else
						return false;
				}
				else
				{
					// check both
					if(TilesBoard[word.row - 1][word.col] != null) // above
						return true;
					else if(TilesBoard[word.row + word.tiles.length][word.col] != null) // under
						return true;
					else
						return false;
				}
			}
			else // horizontal
			{
				if(word.col == 0)
				{
					// check only one side(right)
					if(TilesBoard[word.row][word.col + word.tiles.length] != null)
						return true;
					else
						return false;

				}
				else if(word.col + word.tiles.length == 14)
				{
					// check only one side(left)
					if(TilesBoard[word.row][word.col - 1] != null) // left
						return true;
					else
						return false;
				}
				else // check both sides
				{
					if(TilesBoard[word.row][word.col - 1] != null) // left
						return true;
					else if(TilesBoard[word.row][word.col + word.tiles.length] != null) // right
						return true;
					else
						return false;
				}
			}
		}
	}

	/**
  * The isNear function checks to see if the word is near another word or tile in all directions, uses other functions to check all sides.
  *
  *
  * @param  word to be checked for valid placement
  *
  * @return True if the word is near this word

  */
 	public boolean isNear(Word word)
	{
		if(word.isvertical())
			return (checkLeft(word) || checkRight(word) || checkCorners(word));
		else
			return (checkUnder(word) || checkAbove(word) || checkCorners(word));
	}

	/**
  * The boardLegal function checks if the word can be legally placed on the board.
  * It checks if there is enough space for it, and that it overlaps with an existing tile or not.
  * If it does overlap, then we check that no switching of tiles was needed in order to fit the word.
  * In addition, it checks if this is the first word in the board.
  *
  * @param  word Check if the word placement is legal
  *
  * @return True if the word is legal on the board, and false otherwise
  */
 	public boolean boardLegal(Word word)
	{
		if(word.row < 0 || word.row > 14 || word.col < 0 || word.col > 14)
			return false;

		if(TilesBoard[7][7] == null) // star at 7x7 is empty
		{
			// place the first word
			if(enoughSpace(word)) // enough space
			{
				if(word.isvertical())
				{
					// check if goes over star
					if(word.col == 7)
					{
						if(word.tiles.length + word.row - 1 >= 7) // goes over 7x7
							return true;
						else
							return false;
					}
					return false; // doesn't go over 7x7
				}
				else // horizontal
				{
					// check if goes over star
					if(word.row == 7)
					{
						if(word.tiles.length + word.col - 1 >= 7) // goes over 7x7
							return true;
						else
							return false;
					}
					return false; // doesn't go over 7x7
				}
			}
			else
				return false; // not enough space
		}


		// from this part the board has at least one word
		else if(enoughSpace(word)) // enough space
		{
			// check if overlaps existing tile and no need to switch tiles for the word
			int overlap = 0;

			if(word.isvertical()) // i row moves
			{
				int i = word.row;
				int p = 0; // goes over words letters
				while(p < word.tiles.length)
				{

					if(word.tiles[p].equals(TilesBoard[i][word.col])) // overlaps and no switching tiles with same letter
					{
						overlap++;
						p++;
						i++;
					}

					else if(TilesBoard[i][word.col] == null)
					{
						p++;
						i++;
					}
					else // in order to fit the word we need to switch existing tiles - no good
						return false;
				}

				if(overlap != 0 || isNear(word))
					return true;
				else // word is floating
					return false;
			}

			else // horizontal
			{
				int j = word.col;
				int p = 0; // goes over words letters
				while(p < word.tiles.length)
				{

					if(word.tiles[p].equals(TilesBoard[word.row][j])) // overlaps and no switching tiles with same letter
					{
						overlap++;
						p++;
						j++;
					}

					else if(TilesBoard[word.row][j] == null)
					{
						p++;
						j++;
					}
					else // in order to fit the word we need to switch existing tiles - no good
						return false;
				}

				if(overlap != 0 || isNear(word))
					return true;
				else // word is floating
					return false;
			}
		}

		return false; // not enough space
	}

	/**
  * The dictionaryLegal function checks to see if the word is in the dictionary.
  *
  *
  * @param word Pass the word to be checked into the function
  *
  * @return True if the word is in the dictionary, and false otherwise
  */
	public boolean dictionaryLegal(Word word)
	{
		return true;
	}

	/**
  * The getWords function takes a Word object as an argument and returns an ArrayList of Word objects.
  * The function first adds the word passed to it as an argument to the ArrayList that will be returned.
  * Then, it checks around each letter in the word for new words created by placing that letter on
  * a blank space on the board. If there are any new words created, they are added to this list of Words and returned at
  * completion of this function call. If no new words were created by placing one or more letters from word onto blank spaces
  * on our board (
  *
  * @param  word Get the row, col, and tiles of the word to be placed on the board
  *
  * @return A list of words that are created by placing the given word on the board
  */
 	public ArrayList<Word> getWords(Word word)
	{
		ArrayList<Word> wordscreated = new ArrayList<Word>();
		wordscreated.add(word); // add our word
		//this section finds the new words created

		if(word.isvertical())
		{

			boolean vertical = false; // the new word can only be horizontal
			Tile Newtiles[] = null;

			for(int k = 0; k < word.tiles.length; k++)
			{
				int leftend = -1;
				int rightend = 15;
				int i = word.row + k; // not changing for the entire "new" word
				int j = word.col;
				int totheleftcount = 0; // how many letters are left of the letter we are checking

				if(TilesBoard[word.row + k][word.col] == null) // new part
				{
					// check left of letter
					while(j > 0)
					{
						if(TilesBoard[i][j-1] == null)
						{
							leftend = j;
							break;
						}
						else
							j = j - 1;
					}
					if (leftend == -1)
						leftend = 0;

					// check right of letter
					j = word.col;
					while(j < 14)
					{
						if(TilesBoard[i][j+1] == null)
						{
							rightend = j;
							break;
						}
						else
							j = j + 1;
					}
					if(rightend == 15)
						rightend = 14;

					if(leftend != rightend) // a new word was created
					{
						// create the array for the new word
						Newtiles = new Tile[rightend - leftend + 1];
						for(int p = 0; p < Newtiles.length; p++)
						{
							if(p != totheleftcount) // not an overlapping letter(was on board)
							{
								Newtiles[p] = TilesBoard[word.row + k][leftend + p];
							}
							else
								Newtiles[p] = word.tiles[k]; // fill the letter from original word we wanted to place (isn't on the board)
						}
						// adding new word
						Word newWord = new Word(Newtiles, word.row + k, leftend,vertical);
						wordscreated.add(newWord);
					}
				}
			}
		}

		else // horizontal
		{
			boolean vertical = true; // the new word can only be vertical
			Tile Newtiles[] = null; // the new word's tile array

			for(int k = 0; k < word.tiles.length; k++)
			{
				if(TilesBoard[word.row][word.col + k] == null)
				{
					int topend = -1;
					int bottomend = 15;
					int i = word.row;
					int j = word.col + k ;// not changing for the entire "new" word
					int ontopcount = 0;// how many letters are above the letter we are checking
					// check above letter

					while(i > 0)
					{
						if(TilesBoard[i - 1][j] == null)
						{
							topend = i;
							break;
						}
						else
						{
							i = i - 1;
							ontopcount++;
						}
					}
					if(topend == -1)
						topend = 0;

					// check under letter
					i = word.row;
					while(i < 14)
					{
						if(TilesBoard[i + 1][j] == null)
						{
							bottomend = i;
							break;
						}
						else
							i = i + 1;
					}
					if(bottomend == 15)
						bottomend = 14;

					if(topend != bottomend) // a new word was created
					{
						// create the array for the new word
						Newtiles = new Tile[bottomend - topend + 1];
						for(int p = 0; p < Newtiles.length; p++)
						{
							if(p != ontopcount) // not an overlapping letter(was on board)
							{
								Newtiles[p] = TilesBoard[topend + p][word.col + k];
							}
							else
								Newtiles[p] = word.tiles[k]; // fill the letter from original word we wanted to place (isn't on the board)
						}
						// adding new word
						Word newWord = new Word(Newtiles, topend, word.col + k,vertical);
						wordscreated.add(newWord);
					}
				}
			}
		}
		return wordscreated;
	}


	/**
  * The isRed function checks if the square on in given coordinates red.
  *
  *
  * @param i Represent the row number of the board
  * @param j Represent the column number of the board
  *
  * @return True if the square at row i and column j is red
  */
 	public boolean isRed(int i, int j)
	{
		if( i == 7 )
			if(j == 7)
				return false;

		if((i % 7 == 0 && j % 7 == 0))
			return true;

		return false;
	}

	/**
  * The isYellow function checks if the square on in given coordinates is yellow.
  *
  *
  * @param i Represent the row number of the board
  * @param j Represent the column number of the board
  *
  * @return True if the square at row i and column j is yellow
  */
 	public boolean isYellow(int i, int j)
	{
		if(((0<i && i<5) && (i==j || i+j == 14)) || ((9 < i && i < 14) && (i == j || i + j == 14)))
			return true;

		return false;
	}

	/**
  * The isBlue function checks if the square on in given coordinates is blue.
  *
  *
  * @param i Represent the row number of the board
  * @param j Represent the column number of the board
  *
  * @return True if the square at row i and column j is blue
  */
 	private boolean isBlue(int i, int j)
	{
		if((i == 1 && (j == 5 || j == 9)) || (i == 5 && (j == 1 || j == 5 || j == 9 || j== 13)) || (i == 9 && (j == 1 || j == 5 || j == 9 || j== 13)) || (i == 13 && (j == 5 || j == 9)))
			return true;

		return false;
	}

	/**
  * The isBabyBlue function checks if the square on in given coordinates is baby blue.
  *
  *
  * @param i Represent the row number of the board
  * @param j Represent the column number of the board
  *
  * @return True if the square at row i and column j is a baby blue square
  */
 	public boolean isBabyBlue(int i, int j)
	{
		if(((i == 8 || i == 6) && (j == 8 || j == 6)) || ((i == 3 || i == 11) && (j % 7 == 0)) || ((j == 3 || j == 11) && i% 7 == 0) ||((i == 2 || i == 12) && (j== 6 || j ==8)) || ((i == 8 || i == 6) && (j == 2 || j == 12)))
			return true;

		return false;
	}

	/**
  * The getScore function calculates the score of a word.
  * The score is calculated for the given word and considers the words that are created by placing this word on the board.
  *
  * @param word Get the row and column of the first letter in the word in and all the ist letters and scoring information.
  *
  * @return The score of the word according to the rules
  */
 	public int getScore(Word word)
	{
		int mult = 1; // remember by how much to multiply at the end;
		int score = 0;
		int i = word.row;
		int j = word.col;

		if(TilesBoard[7][7] == null) // first word in - star
		{
			mult *= 2;
		}

		for(int k = 0; k < word.tiles.length; k++)
		{
			// set color to get score
			if(isRed(i,j)) // red
			{
				score += word.tiles[k].score;
				mult *= 3;
				if(word.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else if(isYellow(i,j)) // yellow
			{
				score += word.tiles[k].score;
				mult *= 2;
				if(word.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else if(isBlue(i,j)) // blue
			{
				score += 3*word.tiles[k].score;
				if(word.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else if(isBabyBlue(i,j)) // babyblue
			{
				score += 2*word.tiles[k].score;
				if(word.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else // green
			{
				score += word.tiles[k].score;
				if(word.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}


		}
		return score*mult;
	}

	/**
  * The tryPlaceWord function checks if a word can be placed on the board.
  * It first checks if the word is dictionary legal, then it checks if it's board legal.
  * If both are true, we check if all created words are dictionary legal, if so, we add their score to our total score.
  * We return 0 in case of an error or illegal move, otherwise we return our total score (including new created words).
  *
  * @param word Check if the word is legal on the board
  *
  * @return The score for the word if it can be placed,
  */
 	public int tryPlaceWord(Word word)
	{
		// fill in spaces(_) and overlapping tiles
		for(int t = 0; t < word.tiles.length ; t++)
		{
			if(word.tiles[t] == null) //w.tiles[t].letter == '_' ||
			{
				if(word.isvertical())
				{
					word.tiles[t] = TilesBoard[word.row + t][word.col];
				}
				else
					word.tiles[t] = TilesBoard[word.row][word.col + t];
			}
		}

		int totalscore = 0;
		if(dictionaryLegal(word)) // our word is dictionary legal
		{
			if(boardLegal(word)) // our word is board legal
			{
				int i = 0;
				boolean legalword = true;
				ArrayList<Word> copycareatedwords = getWords(word);
				while(legalword && i < copycareatedwords.size())
				{
					if(dictionaryLegal(copycareatedwords.get(i))) // created word is dictionary legal
					{
						totalscore += getScore(copycareatedwords.get(i)); // add our word's and new created words score
						i++;
					}
					else // word created not legal, can't put our word into the board
					{
						legalword = false;
						return 0;
					}
				}

				// all words created were ok, we can put our word into the board
				int row = word.row;
				int col = word.col;
				for(int k = 0; k < word.tiles.length; k++)
				{
					if(TilesBoard[row][col] == null) // the tile doesn't exist in board, need to fill in
						TilesBoard[row][col] = word.tiles[k]; // check of we need to copy------

					if(word.isvertical())
						row++;
					else
						col++;
				}
				return totalscore;
			}
			return 0; // our word wasn't board legal
		}
		return 0; // our word wasn't dictionary legal
	}
}



