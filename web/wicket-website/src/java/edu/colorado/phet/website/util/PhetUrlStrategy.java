package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;

public class PhetUrlStrategy implements IRequestTargetUrlCodingStrategy {
    private String prefix;
    private PhetUrlMapper mapper;

    private static final Logger logger = Logger.getLogger( PhetUrlStrategy.class.getName() );

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
            // we shouldn't ever have to encode a URL like this.
        }
        throw new RuntimeException( "PhetUrlStrategy.encode" );
    }

    public IRequestTarget decode( RequestParameters requestParameters ) {
        logger.debug( "X decode( RequestParameters ): " + requestParameters );
        logger.debug( "X Path: " + requestParameters.getPath() );
        logger.debug( "X ComponentPath: " + requestParameters.getComponentPath() );
        PageParameters params = new PageParameters( requestParameters.getParameters() );
        String requestPath = requestParameters.getPath();
        String strippedPath = stripPath( requestPath );
        params.add( "fullPath", requestPath );
        params.add( "path", strippedPath );
        params.add( "localeString", prefix );
        params.add( "prefixString", "/" + prefix + "/" );
        if ( prefix.equals( "error" ) ) {
            params.put( "locale", PhetWicketApplication.getDefaultLocale() );
        }
        else {
            params.put( "locale", LocaleUtils.stringToLocale( prefix ) );
        }
        Class toClass = mapper.getMappedClass( strippedPath, params );
        return new BookmarkablePageRequestTarget( toClass, params );
    }

    private String stripPath( String path ) {
        try {
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
        catch( RuntimeException e ) {
            return path;
        }
    }

    public boolean matches( IRequestTarget target ) {
        return mapper.containsClass( target.getClass() );
    }

    public boolean matches( String str ) {
        Class clazz = mapper.getMappedClass( stripPath( str ) );
        boolean ret = clazz != null;
        logger.debug( " XMatches? : " + str + " = " + ret + ( ret ? " for " + clazz.getCanonicalName() : "" ) );
        return ret;
    }
}
