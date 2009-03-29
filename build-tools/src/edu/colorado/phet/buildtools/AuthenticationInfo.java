package edu.colorado.phet.buildtools;


public class AuthenticationInfo {

    private final String username;
    private final String password;

    public AuthenticationInfo( String username, String password ) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
