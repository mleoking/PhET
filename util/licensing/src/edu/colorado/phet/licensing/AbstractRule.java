package edu.colorado.phet.licensing;

import edu.colorado.phet.build.util.LicenseInfo;

public abstract class AbstractRule {
    private String pattern;

    AbstractRule( String pattern ) {
        this.pattern = pattern;
    }

    public abstract boolean matches( ResourceAnnotation entry );

    protected String getPattern() {
        return pattern;
    }

    public abstract boolean matches( LicenseInfo resource );
}
