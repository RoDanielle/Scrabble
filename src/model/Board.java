package model;
import java.util.ArrayList;

public class Board {

	private static Board _instance = null;
	Tile TilesBoard[][];


	Board()
	{
		TilesBoard = new Tile[15][15];
	}


	public static Board getBoard( ) {
		if(_instance == null)
		{
			_instance = new Board();
		}
		return _instance;
	}

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

	public boolean enoughSpace(Word w)
	{
		if(w.isvertical()) // check vertical space (col j moves)
		{
			if(w.row + w.tiles.length > 15)
				return false;
		}

		else // check horizontal space (row i moves)
		{
			if(w.col + w.tiles.length > 15)
				return false;
		}

		return true; // enough space
	}

	// next five function check if new words were created around our word (tzamood)
	public boolean checkAbove(Word w)
	{
		int i = w.col;
		while(i <= w.col + w.tiles.length)
		{
			if(TilesBoard[w.row - 1][i] != null)
				return true;
			i++;
		}
		return false;
	}

	public boolean checkUnder(Word w)
	{
		int i = w.col;
		while(i <= w.col + w.tiles.length)
		{
			if(TilesBoard[w.row + 1][i] != null)
				return true;
			i++;
		}
		return false;
	}


	public boolean checkLeft(Word w)
	{
		int i = w.row;
		while(i <= w.row + w.tiles.length)
		{
			if(TilesBoard[i][w.col - 1] != null)
				return true;
			i++;
		}
		return false;
	}

	public boolean checkRight(Word w)
	{
		int i = w.row;
		while(i <= w.row + w.tiles.length)
		{
			if(TilesBoard[i][w.col + 1] != null)
				return true;
			i++;
		}
		return false;
	}

	public boolean checkCorners(Word w)
	{
		if((w.col == 0 || w.row == 0) && w.tiles.length == 15)
			return false; // no letters at edges
		else
		{
			if(w.isvertical())
			{
				if(w.row == 0)
				{
					//check only under
					if(TilesBoard[w.row + w.tiles.length][w.col] != null)
						return true;
					else
						return false;

				}
				else if(w.row + w.tiles.length == 14)
				{
					// check only above
					if(TilesBoard[w.row - 1][w.col] != null)
						return true;
					else
						return false;
				}
				else
				{
					// check both
					if(TilesBoard[w.row - 1][w.col] != null) // above
						return true;
					else if(TilesBoard[w.row + w.tiles.length][w.col] != null) // under
						return true;
					else
						return false;
				}
			}
			else // horizontal
			{
				if(w.col == 0)
				{
					// check only one side(right)
					if(TilesBoard[w.row][w.col + w.tiles.length] != null)
						return true;
					else
						return false;

				}
				else if(w.col + w.tiles.length == 14)
				{
					// check only one side(left)
					if(TilesBoard[w.row][w.col - 1] != null) // left
						return true;
					else
						return false;
				}
				else // check both sides
				{
					if(TilesBoard[w.row][w.col - 1] != null) // left
						return true;
					else if(TilesBoard[w.row][w.col + w.tiles.length] != null) // right
						return true;
					else
						return false;
				}
			}
		}
	}


	public boolean isNear(Word w)
	{
		if(w.isvertical())
			return (checkLeft(w) || checkRight(w) || checkCorners(w));
		else
			return (checkUnder(w) || checkAbove(w) || checkCorners(w));
	}

	public boolean boardLegal(Word w)
	{
		if(w.row < 0 || w.row > 14 || w.col < 0 || w.col > 14)
			return false;

		if(TilesBoard[7][7] == null) // star at 7x7 is empty
		{
			// place the first word
			if(enoughSpace(w)) // enough space
			{
				if(w.isvertical())
				{
					// check if goes over star
					if(w.col == 7)
					{
						if(w.tiles.length + w.row - 1 >= 7) // goes over 7x7
							return true;
						else
							return false;
					}
					return false; // doesn't go over 7x7
				}
				else // horizontal
				{
					// check if goes over star
					if(w.row == 7)
					{
						if(w.tiles.length + w.col - 1 >= 7) // goes over 7x7
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
		else if(enoughSpace(w)) // enough space
		{
			// check if overlaps existing tile and no need to switch tiles for the word
			int overlap = 0;

			if(w.isvertical()) // i row moves
			{
				int i = w.row;
				int p = 0; // goes over words letters
				while(p < w.tiles.length)
				{

					if(w.tiles[p].equals(TilesBoard[i][w.col])) // overlaps and no switching tiles with same letter
					{
						overlap++;
						p++;
						i++;
					}

					else if(TilesBoard[i][w.col] == null)
					{
						p++;
						i++;
					}
					else // in order to fit the word we need to switch existing tiles - no good
						return false;
				}

				if(overlap != 0 || isNear(w))
					return true;
				else // word is floating
					return false;
			}

			else // horizontal
			{
				int j = w.col;
				int p = 0; // goes over words letters
				while(p < w.tiles.length)
				{

					if(w.tiles[p].equals(TilesBoard[w.row][j])) // overlaps and no switching tiles with same letter
					{
						overlap++;
						p++;
						j++;
					}

					else if(TilesBoard[w.row][j] == null)
					{
						p++;
						j++;
					}
					else // in order to fit the word we need to switch existing tiles - no good
						return false;
				}

				if(overlap != 0 || isNear(w))
					return true;
				else // word is floating
					return false;
			}
		}

		return false; // not enough space
	}

	public boolean dictionaryLegal(Word w)
	{
		return true;
	}

	public ArrayList<Word> getWords(Word w)
	{
		ArrayList<Word> wordscreated = new ArrayList<Word>();
		wordscreated.add(w); // add our word
		//this section finds the new words created

		if(w.isvertical())
		{

			boolean vertical = false; // the new word can only be horizontal
			Tile Newtiles[] = null;

			for(int k = 0; k < w.tiles.length; k++)
			{
				int leftend = -1;
				int rightend = 15;
				int i = w.row + k; // not changing for the entire "new" word
				int j = w.col;
				int totheleftcount = 0; // how many letters are left of the letter we are checking

				if(TilesBoard[w.row + k][w.col] == null) // new part
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
					j = w.col;
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
								Newtiles[p] = TilesBoard[w.row + k][leftend + p];
							}
							else
								Newtiles[p] = w.tiles[k]; // fill the letter from original word we wanted to place (isn't on the board)
						}
						// adding new word
						Word newWord = new Word(Newtiles, w.row + k, leftend,vertical);
						wordscreated.add(newWord);
					}
				}
			}
		}

		else // horizontal
		{
			boolean vertical = true; // the new word can only be vertical
			Tile Newtiles[] = null; // the new word's tile array

			for(int k = 0; k < w.tiles.length; k++)
			{
				if(TilesBoard[w.row][w.col + k] == null)
				{
					int topend = -1;
					int bottomend = 15;
					int i = w.row;
					int j = w.col + k ;// not changing for the entire "new" word
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
					i = w.row;
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
								Newtiles[p] = TilesBoard[topend + p][w.col + k];
							}
							else
								Newtiles[p] = w.tiles[k]; // fill the letter from original word we wanted to place (isn't on the board)
						}
						// adding new word
						Word newWord = new Word(Newtiles, topend, w.col + k,vertical);
						wordscreated.add(newWord);
					}
				}
			}

		}
		return wordscreated;
	}


	public boolean isRed(int i, int j)
	{
		if( i == 7 )
			if(j == 7)
				return false;

		if((i % 7 == 0 && j % 7 == 0))
			return true;

		return false;
	}

	public boolean isYellow(int i, int j)
	{
		if(((0<i && i<5) && (i==j || i+j == 14)) || ((9 < i && i < 14) && (i == j || i + j == 14)))
			return true;

		return false;
	}

	private boolean isBlue(int i, int j)
	{
		if((i == 1 && (j == 5 || j == 9)) || (i == 5 && (j == 1 || j == 5 || j == 9 || j== 13)) || (i == 9 && (j == 1 || j == 5 || j == 9 || j== 13)) || (i == 13 && (j == 5 || j == 9)))
			return true;

		return false;
	}

	public boolean isBabyBlue(int i, int j)
	{
		if(((i == 8 || i == 6) && (j == 8 || j == 6)) || ((i == 3 || i == 11) && (j % 7 == 0)) || ((j == 3 || j == 11) && i% 7 == 0) ||((i == 2 || i == 12) && (j== 6 || j ==8)) || ((i == 8 || i == 6) && (j == 2 || j == 12)))
			return true;

		return false;
	}

	public int getScore(Word w)
	{
		int mult = 1; // remember by how much to multiply at the end;
		int score = 0;
		int i = w.row;
		int j = w.col;

		if(TilesBoard[7][7] == null) // first word in - star
		{
			mult *= 2;
		}

		for(int k = 0; k < w.tiles.length; k++)
		{
			// set color to get score
			if(isRed(i,j)) // red
			{
				score += w.tiles[k].score;
				mult *= 3;
				if(w.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else if(isYellow(i,j)) // yellow
			{
				score += w.tiles[k].score;
				mult *= 2;
				if(w.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else if(isBlue(i,j)) // blue
			{
				score += 3*w.tiles[k].score;
				if(w.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else if(isBabyBlue(i,j)) // babyblue
			{
				score += 2*w.tiles[k].score;
				if(w.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}

			else // green
			{
				score += w.tiles[k].score;
				if(w.isvertical())// vertical - col(j) stays the same
					i++;
				else//not vertical (horizontal) - row(i) stays the same
					j++;
			}


		}
		return score*mult;
	}

	public int tryPlaceWord(Word w)
	{
		// fill in spaces(_) and overlapping tiles
		for(int t = 0; t < w.tiles.length ; t++)
		{
			if(w.tiles[t] == null) //w.tiles[t].letter == '_' ||
			{
				if(w.isvertical())
				{
					w.tiles[t] = TilesBoard[w.row + t][w.col];
				}
				else
					w.tiles[t] = TilesBoard[w.row][w.col + t];
			}
		}


		int totalscore = 0;
		if(dictionaryLegal(w)) // our word is dictionary legal
		{
			if(boardLegal(w)) // our word is board legal
			{
				int i = 0;
				boolean legalword = true;
				ArrayList<Word> copycareatedwords = getWords(w); // check if copied or as pointer------
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
				int row = w.row;
				int col = w.col;
				for(int k = 0; k < w.tiles.length; k++)
				{
					if(TilesBoard[row][col] == null) // the tile doesn't exist in board, need to fill in
						TilesBoard[row][col] = w.tiles[k]; // check of we need to copy------

					if(w.isvertical())
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



