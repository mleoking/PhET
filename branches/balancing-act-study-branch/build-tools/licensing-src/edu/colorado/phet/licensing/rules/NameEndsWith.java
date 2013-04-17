package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * Rule stating that the resource name must end with a specified string.
 */
public class NameEndsWith extends AbstractRule {

    public NameEndsWith( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return endsWithPattern( annotation.getName() );
    }

    public boolean matches( LicenseInfo info ) {
        return endsWithPattern( info.getID() );
    }
}
