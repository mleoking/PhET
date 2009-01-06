package edu.colorado.phet.licensing;

public class License extends AbstractRule {

    License( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getLicense() != null && entry.getLicense().toLowerCase().startsWith( getPattern().toLowerCase(  ));
    }
}
