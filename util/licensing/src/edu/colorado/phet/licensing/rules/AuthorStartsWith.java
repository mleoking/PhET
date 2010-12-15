package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * Rule stating that the "author" annotation must start with a specified string.
 */
public class AuthorStartsWith extends AbstractRule {

    public AuthorStartsWith( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return startsWithPattern( annotation.getAuthor() );
    }

    public boolean matches( LicenseInfo info ) {
        return false; // LicenseInfo doesn't have an author property
    }
}
