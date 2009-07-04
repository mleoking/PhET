package edu.colorado.phet.wickettest.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhetUrlMapper {

    private List<UrlMap> mappings;

    public PhetUrlMapper() {
        mappings = new LinkedList<UrlMap>();
    }

    public void addMap( String regex, Class toClass ) {
        mappings.add( new UrlMap( regex, toClass ) );
    }

    // TODO: add removing mapping

    public Class getMappedClass( String url ) {
        //System.out.println( "Testing map to " + url );
        for ( UrlMap mapping : mappings ) {
            if ( mapping.matches( url ) ) {
                return mapping.getToClass();
            }
        }
        return null;
    }

    public boolean containsClass( Class toClass ) {
        for ( UrlMap mapping : mappings ) {
            if ( mapping.getToClass().equals( toClass ) ) {
                return true;
            }
        }
        return false;
    }

    private static class UrlMap {
        private Pattern pattern;
        private Class toClass;

        private UrlMap( String pattern, Class toClass ) {
            this.pattern = Pattern.compile( pattern );
            this.toClass = toClass;
        }

        public Class getToClass() {
            return toClass;
        }

        public boolean matches( String url ) {
            Matcher matcher = pattern.matcher( url );
            return matcher.find();
        }
    }
}
