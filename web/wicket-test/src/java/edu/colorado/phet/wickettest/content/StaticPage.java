package edu.colorado.phet.wickettest.content;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.templates.PhetRegularPage;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;

public class StaticPage extends PhetRegularPage {
    public StaticPage( PageParameters parameters ) {
        super( parameters );

        try {
            String path = parameters.getString( "path" );

            Class panelClass = panelMap.get( path );

            Constructor ctor = panelClass.getConstructor( String.class, PageContext.class );
            Method meth = panelClass.getMethod( "getKey" );
            String key = (String) meth.invoke( null );

            PhetPanel panel = (PhetPanel) ctor.newInstance( "panel", getPageContext() );

            addTitle( new ResourceModel( key + ".title" ) );
            initializeLocation( getNavMenu().getLocationByKey( key ) );
            add( panel );
        }
        catch( RuntimeException e ) {
            System.out.println( e );
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

    public static void addPanel( Class panelClass ) {
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
    }
}