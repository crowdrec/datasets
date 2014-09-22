package de.dailab.movielens2crowdrec.convert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * It converts the MovieLens100K dataset
 * (http://grouplens.org/datasets/movielens/) to the CrowDrec format.
 * 
 * == Input == An instance of the MovieLens100K composed by 3 files: u.data,
 * u.item and u.user
 * 
 * == Output == The dataset converted to Crowdrec format, composed by 2 files:
 * entities.dat and relations.dat
 * 
 * entities.dat * etype: entity type (e.g., user, movie, actor,...) * eid:
 * entity unique identifier * timestamp: null * properties: a map of properties
 * (e.g., title, genre, age ...) * linkedEntities: a map of linked entities
 * (e.g., movie actors, friends...)
 * 
 * relations.dat: * rtype: relation type (e.g., rating, view,...) * rid:
 * relation unique identifier * timestamp: the timestamp the relation was
 * created (e.g., when a rating was given) * properties: a map of properties
 * (e.g., rating) * linkedEntities: a map of linked entities (e.g.,
 * subject/object of the relation,...)
 * 
 * @author thanh tu
 * @author andreas
 * 
 */
public class ConvertMovieLens100KToCrowdrec {

	static final String RELATIONS_DATA = "relations.dat";
	static final String ENTITIES_DATA = "entities.dat";

	/**
	 * 
	 * Convert rating data from MovieLens_100K to the CrowdRec format.
	 * 
	 * @param input
	 *            Path to rating data from MovieLens_100K.
	 * @param output
	 *            Path to folder that the relations.dat file will be saved in.
	 */
	public static void convertRelations(final String input, final String output) {
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(input));
			bw = new BufferedWriter(new FileWriter(output + "\\"
					+ RELATIONS_DATA));
			String line = "";
			/* A type of relation is always "rating.explicit" */
			String rType = "rating.explicit";
			int rid = 0;
			/* Read the data line by line and stores in relations.dat */
			for (line = br.readLine(); line != null; line = br.readLine()) {
				String[] tokens = line.split("\t");
				String userid = "";
				String itemid = "";
				String properties = "";
				String entities = "";
				int rating = 0;
				Long timestamp = 0L;

				if (tokens.length == 4) {
					userid = tokens[0] + "0";
					itemid = tokens[1] + "1";
					rating = Integer.parseInt(tokens[2]);
					properties = "{\"rating\":" + rating + "}";
					entities = "{\"subject\":\"user:" + userid
							+ "\",\"object\":\"movie:" + itemid + "\"}";
					timestamp = Long.parseLong(tokens[3]);
					/* Increase the id of relation */
					rid = rid + 1;

					String newDataLine = rType + "\t" + rid + "\t" + timestamp
							+ "\t" + properties + "\t" + entities;
					/* write to relations.dat */
					bw.append(newDataLine);
					bw.newLine();
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR! Files not found! ");
			System.out.println("Input: " + input);
		} catch (IOException e) {
			System.out.println("ERROR! " + e.toString());
			System.out.println("Output: " + output);
		} finally {
			try {
				br.close();
				bw.close();
			} catch (IOException e) {
				System.out.println("Warning! can't close reader and writer!");
			}
		}

	}

	/**
	 * 
	 * Convert entity data from MovieLens_100K to the Crowdrec format.
	 * 
	 * @param user_input
	 *            Path to data of users from MovieLens.
	 * @param item_input
	 *            Path to data of movies from MovieLens.
	 * @param output
	 *            Path to folder that the entities.dat file will be saved in.
	 */
	public static void convertEntities(final String user_input,
			final String item_input, final String output) {
		BufferedReader user_br = null;
		BufferedReader item_br = null;
		BufferedWriter bw = null;
		try {
			user_br = new BufferedReader(new FileReader(user_input));
			item_br = new BufferedReader(new FileReader(item_input));
			bw = new BufferedWriter(new FileWriter(output + "\\"
					+ ENTITIES_DATA));
			String line = "";
			String etype = "";
			String entities = "";
			int eid = 0;
			/* Read the users data line by line and stores in entities.dat */
			for (line = user_br.readLine(); line != null; line = user_br
					.readLine()) {
				String[] tokens = line.split("\\|");
				int age = 0;
				String gender = "";
				String occupation = "";
				String zipCode = "";
				String properties = "";
				etype = "user";
				if (tokens.length > 0) {
					/*
					 * The id of users will be multiplied by 10 to distinguish
					 * with movie_id
					 */
					eid = Integer.parseInt(tokens[0] + "0");
				}
				if (tokens.length > 1) {
					age = Integer.parseInt(tokens[1]);
				}
				if (tokens.length > 2) {
					gender = tokens[2];
				}
				if (tokens.length > 3) {
					occupation = tokens[3];
				}
				if (tokens.length > 4) {
					zipCode = tokens[4];
				}
				properties = "{\"age\":" + age + ",\"gender\":\"" + gender
						+ "\",\"occupation\":\"" + occupation
						+ "\",\"zipCode\":\"" + zipCode + "\"}";

				/* timeStamp and linked_entities are empty */
				String newDataLine = etype + "\t" + eid + "\t\t" + properties
						+ "\t" + entities;

				/* write to entities.dat */
				bw.append(newDataLine);
				bw.newLine();
				if (eid % 100 == 0) {
					bw.flush();
					System.out.println(etype + " : " + eid);
				}

			}
			
			/* Read the movies data line by line and stores in entities.dat */
			eid = 0;
			for (line = item_br.readLine(); line != null; line = item_br
					.readLine()) {
				String[] tokens = line.split("\\|");
				String title = "";
				String releaseDate = "";
				String imdbUrl = "";
				String genre = "";
				String properties = "";
				etype = "movie";
				if (tokens.length > 0) {
					/*
					 * The id of movies will be multiplied by 10 and added with
					 * 1 to distinguish with user_id
					 */
					eid = Integer.parseInt(tokens[0] + "1");
				}
				if (tokens.length > 1) {
					title = tokens[1];
				}
				if (tokens.length > 2) {
					releaseDate = tokens[2];
				}
				if (tokens.length > 4) {
					imdbUrl = tokens[4];
				}
				if (tokens.length > 23) {
					int[] genreCodes = new int[19];
					for (int i = 5; i < tokens.length; i++) {
						genreCodes[i - 5] = Integer.parseInt(tokens[i]);
					}
					for (int i = 0; i < genreCodes.length; i++) {
						if (genreCodes[i] == 1) {
							switch (i) {
							case 0:
								genre = genre + ", unknown";
								break;
							case 1:
								genre = genre + ", Action";
								break;
							case 2:
								genre = genre + ", Adventure";
								break;
							case 3:
								genre = genre + ", Animation";
								break;
							case 4:
								genre = genre + ", Children's";
								break;
							case 5:
								genre = genre + ", Comedy";
								break;
							case 6:
								genre = genre + ", Crime";
								break;
							case 7:
								genre = genre + ", Documentary";
								break;
							case 8:
								genre = genre + ", Drama";
								break;
							case 9:
								genre = genre + ", Fantasy";
								break;
							case 10:
								genre = genre + ", Film-Noir";
								break;
							case 11:
								genre = genre + ", Horror";
								break;
							case 12:
								genre = genre + ", Musical";
								break;
							case 13:
								genre = genre + ", Mystery";
								break;
							case 14:
								genre = genre + ", Romance";
								break;
							case 15:
								genre = genre + ", Sci-Fi";
								break;
							case 16:
								genre = genre + ", Thriller";
								break;
							case 17:
								genre = genre + ", War";
								break;
							case 18:
								genre = genre + ", Western";
								break;
							default:
								break;
							}
						}

					}
					if (genre.length() > 2) {
						genre = genre.substring(2);
					}
				}
				properties = "{\"title\":\"" + title + "\",\"release date\":\""
						+ releaseDate + "\",\"imdbUrl\":\"" + imdbUrl
						+ "\",\"genres\":\"" + genre + "\"}";
				/* timeStamp and linked_entities are empty */
				String newDataLine = etype + "\t" + eid + "\t\t" + properties
						+ "\t" + entities;

				/* write to entities.dat */
				bw.append(newDataLine);
				bw.newLine();
				if (eid % 100 == 0) {
					bw.flush();
					System.out.println(etype + " : " + eid);
				}

			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR! Files not found! ");
			System.out.println("User input: " + user_input);
			System.out.println("Item input: " + item_input);
		} catch (IOException e) {
			System.out.println("ERROR! " + e.toString());
			System.out.println("Output: " + output);
		} finally {
			try {
				user_br.close();
				item_br.close();
				bw.close();
			} catch (IOException e) {
				System.out.println("Warning! cannot close reader and writer!");
			}
		}

	}

	/**
	 * 
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out
				.println("usage: java de.dailab.movielens2crowdrec.convert.ConvertMovieLens100KToCrowdrec <userDataFile> <itemDataFile> <ratingDataFile> [<outputDirectory>]\n");
			System.out
					.println("Please enter the paths to the required files.");
			System.out
					.println("You need at least three arguments: user's data, item's data and rating's data.");
			System.out
					.println("The 4th argument (optional) defines the path to the output directory.");
		} else {
			String users = args[0];
			String items = args[1];
			String rating = args[2];
			String outputPath = "";
			if (args.length > 3) {
				outputPath = args[3];
			}
			convertEntities(users, items, outputPath);
			convertRelations(rating, outputPath);
			System.out.println("The convert process is finished!");
			System.out.println("You can find the entities-data under: "
					+ outputPath + "\\" + ENTITIES_DATA);
			System.out.println("You can find the relations-data under: "
					+ outputPath + "\\" + RELATIONS_DATA);
		}
	}
}
