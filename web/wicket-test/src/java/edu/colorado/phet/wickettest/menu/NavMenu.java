package edu.colorado.phet.wickettest.menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.link.Link;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.content.*;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class NavMenu {
    private HashMap<String, NavLocation> cache = new HashMap<String, NavLocation>();
    private List<NavLocation> locations = new LinkedList<NavLocation>();

    public NavMenu() {
        NavLocation home = new NavLocation( null, "home", IndexPage.getLinker() );
        addMajorLocation( home );

        NavLocation simulations = new NavLocation( null, "simulations", SimulationDisplay.getLinker() );
        addMajorLocation( simulations );

        NavLocation workshops = new NavLocation( null, "workshops", WorkshopsPanel.getLinker() );
        addMajorLocation( workshops );

        NavLocation getPhet = new NavLocation( null, "get-phet", RunOurSimulationsPanel.getLinker() );
        addMajorLocation( getPhet );

        NavLocation online = new NavLocation( getPhet, "get-phet.on-line", SimulationDisplay.getLinker() );
        addLocation( online );
        getPhet.addChild( online );

        NavLocation fullInstall = new NavLocation( getPhet, "get-phet.full-install", FullInstallPanel.getLinker() );
        addLocation( fullInstall );
        getPhet.addChild( fullInstall );

        NavLocation oneAtATime = new NavLocation( getPhet, "get-phet.one-at-a-time", OneAtATimePanel.getLinker() );
        addLocation( oneAtATime );
        getPhet.addChild( oneAtATime );

        NavLocation troubleshooting = new NavLocation( null, "troubleshooting.main", TroubleshootingMainPanel.getLinker() );
        addMajorLocation( troubleshooting );

        NavLocation troubleshootingJava = new NavLocation( troubleshooting, "troubleshooting.java", TroubleshootingJavaPanel.getLinker() );
        addLocation( troubleshootingJava );
        troubleshooting.addChild( troubleshootingJava );

        NavLocation troubleshootingFlash = new NavLocation( troubleshooting, "troubleshooting.flash", TroubleshootingFlashPanel.getLinker() );
        addLocation( troubleshootingFlash );
        troubleshooting.addChild( troubleshootingFlash );

        NavLocation troubleshootingJavascript = new NavLocation( troubleshooting, "troubleshooting.javascript", TroubleshootingJavascriptPanel.getLinker() );
        addLocation( troubleshootingJavascript );
        troubleshooting.addChild( troubleshootingJavascript );

        NavLocation contribute = new NavLocation( null, "contribute", ContributePanel.getLinker() );
        addMajorLocation( contribute );

        NavLocation research = new NavLocation( null, "research", ResearchPanel.getLinker() );
        addMajorLocation( research );

        NavLocation about = new NavLocation( null, "about", AboutPhetPanel.getLinker() );
        addMajorLocation( about );

        Session session = HibernateUtils.getInstance().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Category root = (Category) session.createQuery( "select c from Category as c where c.root = true" ).uniqueResult();
            buildCategoryMenu( simulations, root );

            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }
        session.close();
    }

    public void buildCategoryMenu( NavLocation location, Category category ) {
        for ( Object o : category.getSubcategories() ) {
            final Category subCategory = (Category) o;
            NavLocation subLocation = new NavLocation( location, subCategory.getName(), new Linkable() {
                public Link getLink( String id, PageContext context ) {
                    return new PhetLink( id, context.getPrefix() + "simulations/category/" + subCategory.getCategoryPath() );
                }
            } );
            addLocation( subLocation );
            location.addChild( subLocation );
            buildCategoryMenu( subLocation, subCategory );
        }
        if ( category.isRoot() ) {
            NavLocation subLocation = new NavLocation( location, "all", new Linkable() {
                public Link getLink( String id, PageContext context ) {
                    return new PhetLink( id, context.getPrefix() + "simulations" );
                }
            } );
            addLocation( subLocation );
            location.addChild( subLocation );
        }
    }

    public void addMajorLocation( NavLocation location ) {
        locations.add( location );
        addLocation( location );
    }

    public void addLocation( NavLocation location ) {
        cache.put( location.getKey(), location );
    }

    public List<NavLocation> getLocations() {
        return locations;
    }

    public NavLocation getLocationByKey( String key ) {
        return cache.get( key );
    }
}
