package edu.colorado.phet.licensing;

public abstract class AbstractRule {
    private String pattern;

    AbstractRule( String pattern ) {
        this.pattern = pattern;
    }

    public abstract boolean matches( ResourceAnnotation entry );

    protected String getPattern() {
        return pattern;
    }
}
