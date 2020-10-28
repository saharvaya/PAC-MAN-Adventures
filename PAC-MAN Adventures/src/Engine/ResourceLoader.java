/**
 * PAC-MAN Adventures
 * @author Itay Bouganim, 305278384
 * @author Sahar Vaya , 205583453
 */
package Engine;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

import Game.Score;


import Pickups.Fruit;

/*
 * A class used to call resource files such as images animation and sounds.
 * With the use of the method in this class Resources can be read from inside a JAR file.
 */
final public class ResourceLoader {

	public static Font gameFont; //Game font
	public static LinkedList<Score> highestScores; //High scores stored table.
	public static final int HIGH_SCORE_AMOUNT = 10; //High scores count to store.
	
	/*
	 * Loads all resources.
	 */
	public void load()
	{
		highestScores = new LinkedList<Score>();
		loadFont();
		loadScores();
	}

	/*
	 * Writes high scores to resource scores.txt file.
	 */
	public static void writeHighestScores()
	{
		try {
			String path = URLDecoder.decode("./HighScores.dat", "UTF-8");
			File scores = new File(path);
			PrintWriter writer = new PrintWriter(scores);
			for (Score score : highestScores) {
				writer.write (score.name+" "+score.score +" "+score.pinapples+" "+score.grapes+" "+score.strawberries+" "+score.bananas+System.lineSeparator());
			}
			writer.close ();
			} catch (IOException e) {
		}
	}
	
	/*
	 * Loads score high score list from scores.txt resource file.
	 */
	@SuppressWarnings("resource")
	public void loadScores()
	{
		boolean readAcomplished = false;
		File highScores = new File("./HighScores.dat");
		BufferedReader br = null;
		try {
			br = Files.newBufferedReader(highScores.toPath());
			Scanner scanner = new Scanner(br);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				StringTokenizer tokenizer = new StringTokenizer(line, " ");
				ArrayList<Fruit> fruits = new ArrayList<Fruit>();
				Score current = new Score("", 0, fruits);
				int count = tokenizer.countTokens();
				while (tokenizer.hasMoreTokens() && count >=6) // Counting name words - Loads player name
				{
					current.name+=tokenizer.nextToken()+" ";
					count--;
				}
				if (tokenizer.hasMoreTokens())
				{
					current.score=Integer.parseInt(tokenizer.nextToken()); // Loads player score
					current.pinapples = Integer.parseInt(tokenizer.nextToken()); // Loads collected fruit count. 
					current.grapes = Integer.parseInt(tokenizer.nextToken());
					current.strawberries = Integer.parseInt(tokenizer.nextToken());
					current.bananas = Integer.parseInt(tokenizer.nextToken());
				}
				highestScores.add(current);
			}
			readAcomplished = true;
			
		} catch (IOException e) {
			try {
				highScores.createNewFile();
			} catch (IOException e1) {}
		}
		if(highestScores.isEmpty()) {
			for(int i=0; i<HIGH_SCORE_AMOUNT; i++)
			{
				highestScores.add(new Score("EMPTY", 0, new ArrayList<Fruit>()));
			}
			if(!readAcomplished) writeHighestScores();
		}
	}
	
	/*
	 * Loads the game font from resource file.
	 */
	public static void loadFont() {
	    try {
		    InputStream is = ResourceLoader.class.getResourceAsStream("/fonts/gameFont.ttf");
			ResourceLoader.gameFont = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * A methods that loads a File as InputStream from a certain path.
	 * @param path - the path of the file inside the resources folder.
	 * @return InputStream that can stream the file to use during runtime.
	 */
	public static InputStream load(String path)
	{
		InputStream input = ResourceLoader.class.getClassLoader().getResourceAsStream(path);
		if(input == null)
			input = ResourceLoader.class.getResourceAsStream("/"+path);
		
		return input;		
	}
}
