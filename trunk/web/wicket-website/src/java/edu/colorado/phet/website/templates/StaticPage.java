package edu.colorado.phet.website.templates;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.cache.CacheableUrlStaticPanel;
import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.RawBodyLabel;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;

public class StaticPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( StaticPage.class.getName() );

    public StaticPage( PageParameters parameters ) {
        super( parameters );

        try {
            String path = parameters.getString( "path" );

            final Class panelClass = panelMap.get( path );

            // determine whether it is cacheable or not
            boolean cacheable = false;
            for ( Class iface : panelClass.getInterfaces() ) {
                if ( iface.equals( CacheableUrlStaticPanel.class ) ) {
                    cacheable = true;
                    break;
                }
            }

            Method getKeyMethod = panelClass.getMethod( "getKey" );
            String key = (String) getKeyMethod.invoke( null );

            if ( cacheable ) {
                logger.debug( "cacheable static panel: " + panelClass.getSimpleName() );
                add( new SimplePanelCacheEntry( panelClass, this.getClass(), getPageContext().getLocale(), path ) {
                    public PhetPanel constructPanel( String id, PageContext context ) {
                        try {
                            Constructor ctor = panelClass.getConstructor( String.class, PageContext.class );
                            PhetPanel panel = (PhetPanel) ctor.newInstance( "panel", getPageContext() );
                            return panel;
                        }
                        catch( InvocationTargetException e ) {
                            e.printStackTrace();
                        }
                        catch( NoSuchMethodException e ) {
                            e.printStackTrace();
                        }
                        catch( IllegalAccessException e ) {
                            e.printStackTrace();
                        }
                        catch( InstantiationException e ) {
                            e.printStackTrace();
                        }
                        throw new RuntimeException( "failed to construct panel!" );
                    }
                }.instantiate( "panel", getPageContext() ) );
            }
            else {
                Constructor ctor = panelClass.getConstructor( String.class, PageContext.class );
                PhetPanel panel = (PhetPanel) ctor.newInstance( "panel", getPageContext() );
                add( panel );
            }

            addTitle( new ResourceModel( key + ".title" ) );
            NavLocation navLocation = getNavMenu().getLocationByKey( key );
            if ( navLocation == null ) {
                logger.warn( "nav location == null for " + panelClass.getCanonicalName() );
            }
            initializeLocation( navLocation );

            if ( PhetWicketApplication.get().isDevelopment() ) {
                add( new RawBodyLabel( "debug-static-page-class", "<!-- static page " + panelClass.getCanonicalName() + " -->" ) );
            }
            else {
                add( new InvisibleComponent( "debug-static-page-class" ) );
            }

        }
        catch( RuntimeException e ) {
            e.printStackTrace();
        }
        catch( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }

    }

    public static Map<String, Class> panelMap = new HashMap<String, Class>();

    private static boolean addedToMapper = false;

    public static void addPanel( Class panelClass ) {
        if ( addedToMapper ) {
            logger.error( "Attempt to add static page after mappings have been completed" );
            throw new RuntimeException( "Attempt to add static page after mappings have been completed" );
        }
        try {
            Method meth = panelClass.getMethod( "getUrl" );
            String url = (String) meth.invoke( null );
            panelMap.put( url, panelClass );
        }
        catch( NoSuchMethodException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        for ( String url : panelMap.keySet() ) {
            mapper.addMap( "^" + url + "$", StaticPage.class );
        }
        addedToMapper = true;
    }
}