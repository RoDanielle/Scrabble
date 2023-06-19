package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class IOSearcher {



	/**
 	 * The search function takes a word and an array of files as input.
 	 * It searches the word in all the files.

 	 *
	 * @param word Search for a word in the files
 	 * @param files Search in all files
 	 *
	 * @return True if the word is found in any of the files, false otherwise
 	 *
  	 * @docauthor Trelent
  	*/
 	public static boolean search(String word, String...files) {
		for(int i = 0; i < files.length; i++) {
			try {
				Stream<String> Fline = Files.lines(Paths.get(files[i])); // takes a line from the file
				if(Fline.anyMatch(lines->lines.contains(word))) // searches a word in a line
					return true;
			}
			catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return false;
	}
}
