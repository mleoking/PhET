package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * Rule stating that the "source" annotation must start with a specified string.
 */
public class SourceStartsWith extends AbstractRule {

    public SourceStartsWith( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return startsWithPattern( annotation.getSource() );
    }

    public boolean matches( LicenseInfo info ) {
        return false; // LicenseInfo doesn't have a source property
    }
}
