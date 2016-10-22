package terstall.final_pset_jeroenterstall;

public class Track
{
    private String name;
    private String artist;
    private String summary;
    private String tags;
    private String url;

    public Track()
    {

    }

    public Track(String name, String artist, String summary, String tags, String url)
    {
        this.name = name;
        this.artist = artist;
        this.summary = summary;
        this.tags = tags;
        this.url = url;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getName()
    {
        return name;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getTags()
    {
        return tags;
    }

    public String getUrl()
    {
        return url;
    }
}
