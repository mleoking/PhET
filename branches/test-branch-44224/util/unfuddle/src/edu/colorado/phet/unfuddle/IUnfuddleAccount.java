package edu.colorado.phet.unfuddle;

/**
 * Created by: Sam
 * Feb 17, 2008 at 5:07:53 PM
 */
public interface IUnfuddleAccount {
    public UnfuddlePerson getPersonForID( int id );

    public String getComponentForID( int id );

}
