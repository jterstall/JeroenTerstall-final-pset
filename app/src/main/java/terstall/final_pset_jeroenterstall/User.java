package terstall.final_pset_jeroenterstall;

class User
{
    private String email;
    private String username;

    User()
    {

    }

    User(String email, String username)
    {
        this.email = email;
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public String getUsername()
    {
        return username;
    }
}
