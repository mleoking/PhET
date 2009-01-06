package edu.colorado.phet.licensing;

public class Author extends AbstractRule {
    Author( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation entry ) {
        return entry.getAuthor() != null && entry.getAuthor().toLowerCase().startsWith( getPattern().toLowerCase( ));
    }
}
