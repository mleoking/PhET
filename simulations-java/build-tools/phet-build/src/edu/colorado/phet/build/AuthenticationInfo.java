package edu.colorado.phet.build;

import javax.swing.*;

public class AuthenticationInfo {
    private String username;
    private String password;

    public AuthenticationInfo( String username, String password ) {
        this.username = username;
        this.password = password;
    }

    public String getUsername( String context ) {
        if ( username == null ) {
            username = JOptionPane.showInputDialog( "login username to " + context );
        }
        return username;
    }

    public String getPassword( String context ) {
        if ( password == null ) {
            password = JOptionPane.showInputDialog( "login password to " + context );
        }
        return password;
    }
}
