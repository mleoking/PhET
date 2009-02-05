package edu.colorado.phet.licensing;

import edu.colorado.phet.buildtools.util.LicenseInfo;

public class Author extends AbstractRule {
    Author( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getAuthor() != null && entry.getAuthor().toLowerCase().startsWith( getPattern().toLowerCase( ));
    }

    public boolean matches( LicenseInfo resource ) {
        return false;
    }
}
