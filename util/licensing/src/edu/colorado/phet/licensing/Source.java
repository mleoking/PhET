package edu.colorado.phet.licensing;

import edu.colorado.phet.build.util.LicenseInfo;

public class Source extends AbstractRule {
    Source( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getSource() != null && entry.getSource().toLowerCase().startsWith( getPattern().toLowerCase( ));
    }

    public boolean matches( LicenseInfo resource ) {
        return false;
    }
}
