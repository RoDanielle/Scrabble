package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class IOSearcher {
	public static boolean search(String word, String...files)
	{
		for(int i = 0; i < files.length; i++) // searches in all files
		{
			try
			{
				Stream<String> Fline = Files.lines(Paths.get(files[i])); // takes a line from the file
				if(Fline.anyMatch(lines->lines.contains(word))) // searches a word in a line
					return true;
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return false;
	}
}
