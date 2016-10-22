package terstall.final_pset_jeroenterstall;

public class Album
{
    private String name;
    private String artist;
    private String summary;
    private String tracks;
    private String tags;
    private String image_url;
    private String url;

    public Album()
    {

    }

    Album(String name, String artist, String tracks, String summary, String tags, String image_url, String url)
    {
        this.name = name;
        this.artist = artist;
        this.tracks = tracks;
        this.summary = summary;
        this.tags = tags;
        this.image_url = image_url;
        this.url = url;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getImage_url()
    {
        return image_url;
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

    public String getTracks()
    {
        return tracks;
    }
}
