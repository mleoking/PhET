package edu.colorado.phet.licensing;

import edu.colorado.phet.build.util.LicenseInfo;

public class License extends AbstractRule {

    License( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getLicense() != null && entry.getLicense().toLowerCase().startsWith( getPattern().toLowerCase() );
    }

    public boolean matches( LicenseInfo resource ) {
        String licenseName = resource.getLicenseName();
        return licenseName != null && licenseName.toLowerCase().startsWith( getPattern().toLowerCase() );
    }
}
