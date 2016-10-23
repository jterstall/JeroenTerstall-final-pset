package terstall.final_pset_jeroenterstall;

class Constants
{
    // Json names
    static final String JSON_NAME = "name";
    static final String JSON_IMAGE = "image";
    static final String JSON_IMAGE_URL = "#text";
    static final String JSON_TRACKS = "tracks";
    static final String JSON_SUMMARY = "summary";
    static final String JSON_WIKI = "wiki";
    static final String JSON_TOPTAGS = "toptags";
    static final String JSON_TAGS = "tags";
    static final String JSON_TAG = "tag";
    static final String JSON_URL = "url";
    static final String JSON_BIO = "bio";
    static final String JSON_RESULT = "results";
    static final String JSON_TRACK_MATCH = "trackmatches";
    static final String JSON_ALBUM_MATCH = "albummatches";
    static final String JSON_ARTIST_MATCH = "artistmatches";
    static final String JSON_TRACK = "track";
    static final String JSON_ALBUM = "album";
    static final String JSON_ARTIST = "artist";
    static final int JSON_IMAGE_SIZE = 2; // 0 = small, 1 = medium, 2 = large, 3 = extra large

    // Types of storage
    static String TRACK_TYPE = "Track";
    static String ARTIST_TYPE = "Artist";
    static String ALBUM_TYPE = "Album";

    // Indices for stack of navigation bar
    static String INDEX = "index";
    static int HOME_STACK_INDEX = 0;
    static int SEARCH_STACK_INDEX = 1;
    static int SEARCH_USER_STACK_INDEX = 2;
    static int COLLECTION_STACK_INDEX = 3;
    static int USER_COLLECTION_STACK_INDEX = 4;

    // API KEY for last fm api and URLs to use the api
    static String API_KEY = "&api_key=09668701cd6843de7d1ebaed460ae800&format=json";
    static String GET_ARTIST_URL = "http://ws.audioscrobbler.com/2.0/?method=artist.getInfo&artist=";
    static String GET_ALBUM_URL = "http://ws.audioscrobbler.com/2.0/?method=album.getInfo&";
    static String GET_TRACK_URL = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&";
    static String SEARCH_ARTIST_URL = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=";
    static String SEARCH_ALBUM_URL = "http://ws.audioscrobbler.com/2.0/?method=album.search&album=";
    static String SEARCH_TRACK_URL = "http://ws.audioscrobbler.com/2.0/?method=track.search&track=";

    // Reference for database users category
    static String USERS = "users";
    static String USERNAME = "username";
    static String EMAIL = "email";
    static String FOLLOWED_USERS = "followed";
}
