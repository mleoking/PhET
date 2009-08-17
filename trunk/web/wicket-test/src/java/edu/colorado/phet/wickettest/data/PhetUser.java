package edu.colorado.phet.wickettest.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PhetUser implements Serializable {

    private long id;
    private String email;
    private String password;
    private boolean teamMember;
    private Set translations = new HashSet();

    // TODO: don't allow users with the same email address!
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

    public Set getTranslations() {
        return translations;
    }

    public void setTranslations( Set translations ) {
        this.translations = translations;
    }
}
