package edu.colorado.phet.website.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.PageParameters;

/**
 * This keeps track of regular expression mappings from prefix-less urls to pages.
 * 
 * @author Jonathan Olson
 */
public class PhetUrlMapper {

    private List<UrlMap> mappings;

    public PhetUrlMapper() {
        mappings = new LinkedList<UrlMap>();
    }

    public void addMap( String regex, Class toClass ) {
        mappings.add( new UrlMap( regex, toClass, new String[0] ) );
    }

    public void addMap( String regex, Class toClass, String[] paramNames ) {
        mappings.add( new UrlMap( regex, toClass, paramNames ) );
    }

    // TODO: add removing mapping

    public Class getMappedClass( String url ) {
        for ( UrlMap mapping : mappings ) {
            if ( mapping.matches( url ) ) {
                return mapping.getToClass();
            }
        }
        return null;
    }

    public Class getMappedClass( String url, PageParameters params ) {
        for ( UrlMap mapping : mappings ) {
            if ( mapping.matches( url ) ) {
                initParameters( url, params, mapping );
                return mapping.getToClass();
            }
        }
        return null;
    }

    private void initParameters( String url, PageParameters params, UrlMap mapping ) {
        if ( mapping.getNumParamNames() > 0 ) {
            Matcher matcher = mapping.getPattern().matcher( url );
            if ( !matcher.matches() ) {
                return;
            }
            for ( int i = 0; i < matcher.groupCount() && i < mapping.getNumParamNames(); i++ ) {
                String paramName = mapping.getParam( i );
                String paramValue = matcher.group( i + 1 );
                if ( paramName != null && paramValue != null ) {
                    params.add( paramName, paramValue );
                }
            }
        }
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
        private String[] paramNames;

        private UrlMap( String pattern, Class toClass, String[] paramNames ) {
            this.pattern = Pattern.compile( pattern );
            this.toClass = toClass;
            this.paramNames = paramNames;
        }

        public Class getToClass() {
            return toClass;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public int getNumParamNames() {
            return paramNames.length;
        }

        public String getParam( int index ) {
            return paramNames[index];
        }

        public boolean matches( String url ) {
            Matcher matcher = pattern.matcher( url );
            return matcher.find();
        }
    }


}
