package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * Rule stating that the "license" annotation must start with a specified string.
 */
public class LicenseStartsWith extends AbstractRule {

    public LicenseStartsWith( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return startsWithPattern( annotation.getLicense() );
    }

    public boolean matches( LicenseInfo info ) {
        return startsWithPattern( info.getLicense() );
    }
}
