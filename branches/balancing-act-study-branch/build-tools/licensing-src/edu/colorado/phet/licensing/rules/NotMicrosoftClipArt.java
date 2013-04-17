// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * @author Sam Reid
 */
public class NotMicrosoftClipArt extends AbstractRule {

    public NotMicrosoftClipArt() {
        super( "microsoft" );
    }

    @Override public boolean matches( ResourceAnnotation annotation ) {
        return !startsWithPattern( annotation.getSource() );
    }

    @Override public boolean matches( LicenseInfo info ) {
        return true; // LicenseInfo doesn't have a source property, so it can't be microsoft clip art
    }
}
