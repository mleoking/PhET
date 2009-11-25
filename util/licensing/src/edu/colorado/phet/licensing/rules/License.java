package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

public class License extends AbstractRule {

    public License( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return startsWithPattern( annotation.getLicense() );
    }

    public boolean matches( LicenseInfo info ) {
        return startsWithPattern( info.getLicenseName() );
    }
}
