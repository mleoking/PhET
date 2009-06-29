package edu.colorado.phet.wickettest;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;

public class WicketApplication extends WebApplication {
    public Class getHomePage() {
        return HelloWorld.class;
    }

    @Override
    protected void init() {
        mount( new IRequestTargetUrlCodingStrategy() {
            public String getMountPath() {
                System.out.println( "getMountPath()" );
                return "intl";
            }

            public CharSequence encode( IRequestTarget target ) {
                System.out.println( "encode( IRequestTarget ): " + target.getClass().getCanonicalName() );
                return "intl/en/simulations";
            }

            public IRequestTarget decode( RequestParameters request ) {
                System.out.println( "decode( RequestParameters ): " + request );
                System.out.println( "Path: " + request.getPath() );
                System.out.println( "ComponentPath: " + request.getComponentPath() );
                PageParameters params = new PageParameters( request.getParameters() );
                params.add( "localeString", getLocaleString( request.getPath() ) );
                return new BookmarkablePageRequestTarget( HelloWorld.class, params );
            }

            public boolean matches( IRequestTarget target ) {
                System.out.println( "matches( IRequestTarget ): " + target.getClass().getCanonicalName() );
                return target instanceof HelloWorld;
            }

            public boolean matches( String str ) {
                System.out.println( "matches( String ): " + str );
                return str.endsWith( "simulations" ) && getLocaleString( str ) != null;
            }

            public String getLocaleString( String url ) {
                String[] chunks = url.split( "/" );
                if ( chunks.length > 1 ) {
                    String str = chunks[1];
                    System.out.println( "chunk 1:" + str );
                    if ( str.length() == 2 || ( str.length() == 5 && str.substring( 2, 3 ).equals( "_" ) ) ) {
                        boolean pass = true;
                        for ( int i = 0; i < str.length(); i++ ) {
                            if ( !Character.isLetter( str.charAt( i ) ) ) {
                                if ( i != 2 ) {
                                    pass = false;
                                    break;
                                }
                            }
                        }
                        if ( pass ) {
                            return str;
                        }
                    }
                }
                return null;
            }
        } );
    }
}
