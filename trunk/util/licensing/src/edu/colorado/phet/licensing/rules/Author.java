package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

public class Author extends AbstractRule {
    
    public Author( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return startsWithPattern( annotation.getAuthor() );
    }

    public boolean matches( LicenseInfo info ) {
        return false;
    }
}
