package server;

import java.util.HashMap;
import java.util.Map;

public class DictionaryManager {
	private Map <String, Dictionary> map;
	private static DictionaryManager _instance = null;



	/**
	 * The DictionaryManager function is used to add new dictionaries only as needed and will provide the answers of the appropriate dictionaries.

 	 *
 	 * @docauthor Trelent
  	*/
 	public DictionaryManager() {
		this.map = new HashMap<String, Dictionary>();
	}



	/**
	* The get function is a static function that returns the singleton instance of the DictionaryManager class.
  	* If no instance exists, it creates one and then returns it.

  	*
  	* @return The dictionarymanager object
  	*
  	* @docauthor Trelent
  	*/
 	public static DictionaryManager get( ){
		if(_instance == null)
		{
			_instance = new DictionaryManager();
		}
		return _instance;
	}



	/**
	 * The query function takes a variable number of arguments, the list of books with the last argument being the word to search for.
	 * The function then searches each book to see if it contains the word.
	 * If any book is not in the map, we create a value for it and add its dictionary to our map.
	 * Then we look for a way in the dictionary of this new book the word.
	 * If at any point we discover that one of these dictionaries contains our word, we will return true.

	 *
	 * @param args Arguments that display the list of books so that the last argument is the word to search for
 	 *
 	 * @return True if the word is found in any of the books, false otherwise
 	 *
	 * @docauthor Trelent
  	*/
 	public boolean query(String...args){

		boolean found = false;
		String word = args[args.length - 1];
		for(int i = 0; i < args.length - 1; i++)
		{
			if(map.containsKey(args[i])){
				if(map.get(args[i]).query(word))
					found = true;
			}
			else {
				Dictionary d = new Dictionary(args[i]);
				this.map.put(args[i], d);
				if(map.get(args[i]).query(word))
					found = true;
			}
		}
		return found;
	}



	/**
	 * The query function takes a variable number of arguments, the list of books with the last argument being the word to search for.
	 * The function then searches each book to see if it contains the word.
	 * If any book is not in the map, we create a value for it and add its dictionary to our map.
	 * Then we look for a way in the dictionary of this new book the word.
	 * If at any point we discover that one of these dictionaries contains our word, we will return true.

	 *
	 * @param args Arguments that display the list of books so that the last argument is the word to search for
	 *
	 * @return True if the word is found in any of the books, false otherwise
	 *
	 * @docauthor Trelent
	 */
 	public boolean challenge(String...args){
		boolean found = false;
		String word = args[args.length - 1];
		for(int i = 0; i < args.length - 1; i++) {
			if(map.containsKey(args[i])) {
				if(map.get(args[i]).challenge(word))
					found = true;
			}
			else {
				Dictionary d = new Dictionary(args[i]);
				this.map.put(args[i], d);
				if(map.get(args[i]).challenge(word))
					found = true;
			}
		}
		return found;
	}



	/**
  	* The getSize function returns the size of the map.

  	*
  	* @return The size of the map
 	*
 	* @docauthor Trelent
	*/
 public int getSize() {
		return map.size();
	}
}
