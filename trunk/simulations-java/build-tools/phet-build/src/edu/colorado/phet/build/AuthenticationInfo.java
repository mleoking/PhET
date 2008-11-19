package edu.colorado.phet.build;

import javax.swing.*;

public class AuthenticationInfo {
    private String username;
    private String password;

    public AuthenticationInfo( String username, String password ) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        if ( username == null ) {
            username = JOptionPane.showInputDialog( "login username" );
        }
        return username;
    }

    public String getPassword() {
        if ( password == null ) {
            password = JOptionPane.showInputDialog( "login password" );
        }
        return password;
    }
}
