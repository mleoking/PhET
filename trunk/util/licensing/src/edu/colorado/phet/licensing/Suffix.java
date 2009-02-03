package edu.colorado.phet.licensing;

import edu.colorado.phet.build.util.LicenseInfo;

public class Suffix extends AbstractRule {

    Suffix( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getName().toLowerCase().endsWith( getPattern().toLowerCase( ));
    }

    public boolean matches( LicenseInfo resource ) {
        return false;
    }
}
