package edu.colorado.phet.licensing;

public class Suffix extends AbstractRule {

    Suffix( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getName().toLowerCase().endsWith( getPattern().toLowerCase( ));
    }
}
