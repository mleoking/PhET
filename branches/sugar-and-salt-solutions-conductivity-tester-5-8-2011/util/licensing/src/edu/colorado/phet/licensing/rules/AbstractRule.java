package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

public abstract class AbstractRule {

    private final String pattern;

    public AbstractRule( String pattern ) {
        this.pattern = pattern;
    }

    protected String getPattern() {
        return pattern;
    }

    /**
     * True if s starts with this rule's pattern.
     * Comparison is case insensitive.
     */
    protected boolean startsWithPattern( String s ) {
        boolean match = false;
        if ( s != null && getPattern() != null ) {
            match = s.toLowerCase().startsWith( getPattern().toLowerCase() );
        }
        return match;
    }

    /**
     * True if s ends with this rule's pattern.
     * Comparison is case insensitive.
     */
    protected boolean endsWithPattern( String s ) {
        boolean match = false;
        if ( s != null && getPattern() != null ) {
            match = s.toLowerCase().endsWith( getPattern().toLowerCase() );
        }
        return match;
    }

    public abstract boolean matches( ResourceAnnotation annotation );

    public abstract boolean matches( LicenseInfo info );
}
