package dev.crowdrec.io

/**
 * It converts the MovieTweetings dataset (https://github.com/sidooms/MovieTweetings) to the CrowdRec format:
 * 
 * == INPUT ==
 * An instance of the MovieTweetings dataset composed by 3 files: users.dat, movies.dat, ratings.dat
 * 
 * == OUTPUT
 * The dataset converted to CrowdRec format, composed by 2 files: entities.dat, relations.dat
 * 
 * entities.dat:
 * * etype: entity type (e.g., user, movie, actor,...)
 * * eid: entity unique identifier
 * * properties: a map of properties (e.g., title, genre, ...)
 * * linkedEntities: a map of linked entities (e.g., movie actors,...)
 *
 * relations.dat:
 * * rtype: relation type (e.g., rating, view,...)
 * * rid: relation unique identifier
 * * timestamp: the timestamp the relation was created (e.g., when a rating was given)
 * * properties: a map of properties (e.g., playtime)
 * * linkedEntities: a map of linked entities (e.g., subject/object of the relation,...)
 * 
 * == USAGE ==
 * groovy ConvertMovieTweetingsToCrowdRec INDIR OUTDIR
 * * INDIR: input directory containing user.dat, movies.dat, and ratings.dat
 * * OUTDIR: output directory that will contain entities.dat and relations.dat
 * 
 * @author Roberto Turrin
 * @version 1.0
 */
class ConvertMovieTweetingsToCrowdRec {
	public static final String IN_USERS_FILENAME = 'users.dat'
	public static final String IN_MOVIES_FILENAME = 'movies.dat'
	public static final String IN_RATINGS_FILENAME = 'ratings.dat'
	
	public static final String OUT_ENTITIES_FILENAME = 'entities.dat'
	public static final String OUT_RELATIONS_FILENAME = 'relations.dat'
	
	public static final String USER_ETYPE = 'user'
	public static final String MOVIE_ETYPE = 'movie'
	public static final String RATING_RTYPE = 'rating.explicit'
	
	private static final String IN_COL_SPLIT = '::'
	private static final String OUT_COL_SPLIT = '\t'
	private static final String PROP_SEP = '::'
	private static final String CR = '\n'
	
	private static final String HEADER_ENTITIES = ['etype','eid','properties','linkedEntities'].join(OUT_COL_SPLIT)
	private static final String HEADER_RELATIONS = ['rtype','rid','timestamp','properties','linkedEntities'].join(OUT_COL_SPLIT)
	
	private static final boolean DEBUG = false // if DEBUG it will not write to files but only to screen 
	private static final boolean PRINT_HEADER = true // if enabled it print the header in files
	
	/**
	 * 
	 * @param args
	 * $0: input folder
	 * $1: output folder (optional)
	 */
	static main(args) {
		String outpath;
		if ( args.length < 1 ) {
			println 'missing parameters'
			return
		}
		String inpath = args[0]
		if ( args.length > 1 ) {
			outpath = args[1]
		} else {
			outpath = inpath
		}
		File users_in = new File(inpath + File.separator + IN_USERS_FILENAME)
		File movies_in = new File(inpath + File.separator + IN_MOVIES_FILENAME)
		File ratings_in = new File(inpath + File.separator + IN_RATINGS_FILENAME)
		
		File entities_out = new File(outpath + File.separator + OUT_ENTITIES_FILENAME)
		File relations_out = new File(outpath + File.separator + OUT_RELATIONS_FILENAME)
		
		if ( !DEBUG && PRINT_HEADER ) {
			entities_out.append(HEADER_ENTITIES + CR)
			relations_out.append(HEADER_RELATIONS + CR)
		}
		users_in.eachLine { user ->
			def user_els = user.split(IN_COL_SPLIT)
			if ( user_els != null && user_els.size() >= 1 ) {
				def userid = user_els[0]
				def twitter_userid = user_els.size() >= 2 ? user_els[1] : ''
				addEntity(entities_out, USER_ETYPE, userid, [twitterid:twitter_userid], [])
			}
		}
		
		movies_in.eachLine { movie ->
			def movie_els = movie.split(IN_COL_SPLIT)
			if ( movie_els != null && movie_els.size() >= 1 ) {
				def movieid = movie_els[0]
				def title = movie_els.size() >= 2 ? movie_els[1] : ''
				addEntity(entities_out, MOVIE_ETYPE, movieid, [title:quote(title)], [])
			}
		}
		
		ratings_in.eachLine { rating, rowindex ->
			def rating_els = rating.split(IN_COL_SPLIT)
			if ( rating_els != null && rating_els.size() >= 4 ) {
				def userid = rating_els[0]
				def movieid = rating_els[1]
				def rating_value = rating_els[2]
				def ts = rating_els[3]
				addRelation(relations_out, RATING_RTYPE, rowindex, ts, [], [subject:createEntityRef(USER_ETYPE,userid), object:createEntityRef(MOVIE_ETYPE,movieid), rating:rating_value  ])
			}
		}
	}
	
	static createEntityRef(type, id) {
		'('+type+':'+ id+')'
	}
	
	static addEntity (file, etype, eid, propset, linkedentities) {
		def props = flattenMap(propset)
		def links = flattenMap(linkedentities)
		def output = [etype, eid, props, links].join(OUT_COL_SPLIT) + CR
		if ( !DEBUG ) {
			file.append(output)
		} else {
			print output
		}
	}
	
	static addRelation(file, rtype, rid, timestamp, propset, linkedentities) {
		def props = flattenMap(propset)
		def links = flattenMap(linkedentities)
		def output = [rtype, rid, timestamp, props, links].join(OUT_COL_SPLIT) + CR
		if ( !DEBUG ) {
			file.append(output)
		} else {
			print output
		}
	}
	
	static flattenMap(map) {
		return map ?
			map.collect { ptype, pname ->
				ptype + '=' + pname + ''
			}.join(PROP_SEP)
			: ''
	}
	
	static quote(str) {
		'\'' + (str?str:'') + '\''
	}
}
