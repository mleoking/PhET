package edu.colorado.phet.wickettest.data;

import java.io.Serializable;

public class PhetUser implements Serializable {

    private long id;
    private String email;
    private String password;
    private boolean teamMember;

    public PhetUser() {
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public boolean isTeamMember() {
        return teamMember;
    }

    public void setTeamMember( boolean teamMember ) {
        this.teamMember = teamMember;
    }
}
