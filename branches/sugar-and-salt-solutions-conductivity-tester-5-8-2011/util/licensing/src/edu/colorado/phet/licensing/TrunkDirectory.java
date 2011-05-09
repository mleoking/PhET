package edu.colorado.phet.licensing;

import java.io.File;

/**
 * Directory in the user's SVN working copy that contains the trunk of PhET's SVN repository.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TrunkDirectory extends File {

    public TrunkDirectory( String path ) {
        super( path );
        if ( !exists() ) {
            throw new IllegalArgumentException( path + " does not exist." );
        }
        if ( !isDirectory() ) {
            throw new IllegalArgumentException( path + " is not a directory." );
        }
    }
}
