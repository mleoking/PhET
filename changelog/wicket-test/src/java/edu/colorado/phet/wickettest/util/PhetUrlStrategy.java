package edu.colorado.phet.wickettest.util;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class PhetUrlStrategy implements IRequestTargetUrlCodingStrategy {
    private String prefix;
    private PhetUrlMapper mapper;

    public PhetUrlStrategy( String prefix, PhetUrlMapper mapper ) {
        this.mapper = mapper;
        this.prefix = prefix;
    }

    public String getMountPath() {
        return prefix;
    }

    public CharSequence encode( IRequestTarget request ) {
        if ( request instanceof BookmarkablePageRequestTarget ) {
            BookmarkablePageRequestTarget bookRequest = (BookmarkablePageRequestTarget) request;
            // TODO
        }
        throw new RuntimeException( "PhetUrlStrategy.encode" );
    }

    public IRequestTarget decode( RequestParameters requestParameters ) {
        //System.out.println( "X decode( RequestParameters ): " + requestParameters );
        //System.out.println( "X Path: " + requestParameters.getPath() );
        //System.out.println( "X ComponentPath: " + requestParameters.getComponentPath() );
        PageParameters params = new PageParameters( requestParameters.getParameters() );
        String requestPath = requestParameters.getPath();
        String strippedPath = stripPath( requestPath );
        params.add( "fullPath", requestPath );
        params.add( "path", strippedPath );
        params.add( "localeString", prefix );
        params.add( "prefixString", "/" + prefix + "/" );
        params.put( "locale", LocaleUtils.stringToLocale( prefix ) );
        Class toClass = mapper.getMappedClass( strippedPath, params );
        return new BookmarkablePageRequestTarget( toClass, params );
    }

    private String stripPath( String path ) {
        if ( path.startsWith( "/" + prefix ) ) {
            return path.substring( prefix.length() + 2 );
        }
        else if ( path.startsWith( prefix ) ) {
            return path.substring( prefix.length() + 1 );
        }
        else {
            return path;
        }
    }

    public boolean matches( IRequestTarget target ) {
        return mapper.containsClass( target.getClass() );
    }

    public boolean matches( String str ) {
        Class clazz = mapper.getMappedClass( stripPath( str ) );
        boolean ret = clazz != null;
        //System.out.println( " XMatches? : " + str + " = " + ret + ( ret ? " for " + clazz.getCanonicalName() : "" ) );
        return ret;
    }
}
