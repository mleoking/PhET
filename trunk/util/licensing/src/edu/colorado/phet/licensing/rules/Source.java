package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

public class Source extends AbstractRule {
    
    public Source( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return startsWithPattern( annotation.getSource() );
    }

    public boolean matches( LicenseInfo info ) {
        return false;
    }
}
