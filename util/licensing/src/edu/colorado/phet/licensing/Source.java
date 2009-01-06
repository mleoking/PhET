package edu.colorado.phet.licensing;

public class Source extends AbstractRule {
    Source( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getSource() != null && entry.getSource().toLowerCase().startsWith( getPattern().toLowerCase( ));
    }
}
