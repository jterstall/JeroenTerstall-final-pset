package terstall.final_pset_jeroenterstall;

// Object which represents an artist

class Artist
{
    private String name;
    private String summary;
    private String tags;
    private String image_url;
    private String url;

    public Artist()
    {

    }

    Artist(String name, String summary, String tags, String image_url, String url)
    {
        this.name = name;
        this.summary = summary;
        this.tags = tags;
        this.image_url = image_url;
        this.url = url;
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
}
