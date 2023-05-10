package test;

import java.util.HashMap;
import java.util.Map;

public class DictionaryManager {

	private Map <String, Dictionary> map;
	private static DictionaryManager _instance = null;
	public DictionaryManager()
	{
		this.map = new HashMap<String, Dictionary>();
	}
	
	public static DictionaryManager get( ){
		if(_instance == null)
		{
			_instance = new DictionaryManager();
		}
		return _instance;
	}

	public boolean query(String...args){
		boolean found = false;
		String word = args[args.length - 1];
		for(int i = 0; i < args.length - 1; i++)
		{
			if(map.containsKey(args[i])) // search for a file(book) dictionary
			{ // search for the word in the books dictionary and the book itself
				if(map.get(args[i]).query(word))
					found = true;
			}
			else // there is no Dictionary for the book
			{
				Dictionary d = new Dictionary(args[i]); 
				this.map.put(args[i], d);
				if(map.get(args[i]).query(word)) { //search for the word in the book 
					found = true;
				}
			}
		}
		return found;
    }

    public boolean challenge(String...args){
		boolean found = false;
		String word = args[args.length - 1];
		for(int i = 0; i < args.length - 1; i++)
		{
			if(map.containsKey(args[i])) // search for a file(book) dictionary
			{ // search for the word in the books dictionary and the book itself
				if(map.get(args[i]).challenge(word))
					found = true;
			}
			else // there is no Dictionary for the book
			{
				Dictionary d = new Dictionary(args[i]); 
				this.map.put(args[i], d);
				if(map.get(args[i]).challenge(word)) { //search for the word in the book 
					found = true;
				}

			} 

		}
		return found;
    }
    	
    public int getSize()
    {
    	return map.size();
    }
		
}
