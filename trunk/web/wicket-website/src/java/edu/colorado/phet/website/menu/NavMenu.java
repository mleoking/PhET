package edu.colorado.phet.website.menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.Link;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.content.*;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.getphet.OneAtATimePanel;
import edu.colorado.phet.website.content.getphet.RunOurSimulationsPanel;
import edu.colorado.phet.website.content.simulations.SimulationDisplay;
import edu.colorado.phet.website.content.simulations.TranslatedSimsPage;
import edu.colorado.phet.website.content.contribution.*;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.Linkable;

public class NavMenu {
    private HashMap<String, NavLocation> cache = new HashMap<String, NavLocation>();
    private List<NavLocation> locations = new LinkedList<NavLocation>();

    private static Logger logger = Logger.getLogger( NavMenu.class.getName() );

    public NavMenu() {
        NavLocation home = new NavLocation( null, "home", IndexPage.getLinker() );
        addMajorLocation( home );

        NavLocation simulations = new NavLocation( null, "simulations", SimulationDisplay.getLinker() );
        addMajorLocation( simulations );

        NavLocation teacherIdeas = new NavLocation( null, "teacherIdeas", TeacherIdeasPanel.getLinker() );
        addMajorLocation( teacherIdeas );

        NavLocation teacherIdeasBrowse = new NavLocation( teacherIdeas, "teacherIdeas.browse", ContributionBrowsePage.getLinker() );
        addLocation( teacherIdeasBrowse );
        teacherIdeas.addChild( teacherIdeasBrowse );

        NavLocation teacherIdeasSubmit = new NavLocation( teacherIdeas, "teacherIdeas.submit", ContributionCreatePage.getLinker() );
        addLocation( teacherIdeasSubmit );
        teacherIdeas.addChild( teacherIdeasSubmit );

        NavLocation teacherIdeasManage = new NavLocation( teacherIdeas, "teacherIdeas.manage", ContributionManagePage.getLinker() );
        addLocation( teacherIdeasManage );
        teacherIdeas.addChild( teacherIdeasManage );

        NavLocation teacherIdeasEdit = new NavLocation( teacherIdeas, "teacherIdeas.edit", ContributionBrowsePage.getLinker() );
        addLocation( teacherIdeasEdit );

        NavLocation teacherIdeasGuide = new NavLocation( teacherIdeas, "teacherIdeas.guide", ContributionGuidelinesPanel.getLinker() );
        addLocation( teacherIdeasGuide );

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

        NavLocation about = new NavLocation( null, "about", AboutMainPanel.getLinker() );
        addMajorLocation( about );

        NavLocation aboutSourceCode = new NavLocation( about, "about.source-code", AboutSourceCodePanel.getLinker() );
        addLocation( aboutSourceCode );
        about.addChild( aboutSourceCode );

        NavLocation aboutLegend = new NavLocation( about, "about.legend", AboutLegendPanel.getLinker() );
        addLocation( aboutLegend );
        about.addChild( aboutLegend );

        NavLocation aboutContact = new NavLocation( about, "about.contact", AboutContactPanel.getLinker() );
        addLocation( aboutContact );
        about.addChild( aboutContact );

        NavLocation aboutWhoWeAre = new NavLocation( about, "about.who-we-are", AboutWhoWeArePanel.getLinker() );
        addLocation( aboutWhoWeAre );
        about.addChild( aboutWhoWeAre );

        NavLocation aboutLicensing = new NavLocation( about, "about.licensing", AboutLicensingPanel.getLinker() );
        addLocation( aboutLicensing );
        about.addChild( aboutLicensing );

        NavLocation aboutSponsors = new NavLocation( about, "sponsors", AboutSponsorsPanel.getLinker() );
        addLocation( aboutSponsors );
        about.addChild( aboutSponsors );

        // unconnected locations

        NavLocation byKeyword = new NavLocation( null, "simulations.by-keyword", SimulationDisplay.getLinker() );
        addLocation( byKeyword );

        NavLocation searchResults = new NavLocation( null, "search.results", SearchResultsPage.getLinker( null ) );
        addLocation( searchResults );

        Session session = HibernateUtils.getInstance().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Category root = (Category) session.createQuery( "select c from Category as c where c.root = true" ).uniqueResult();
            buildCategoryMenu( simulations, root );

            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
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
                public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                    return new PhetLink( id, context.getPrefix() + "simulations/category/" + subCategory.getCategoryPath() );
                }
            } );
            addLocation( subLocation );
            location.addChild( subLocation );
            buildCategoryMenu( subLocation, subCategory );
        }
        if ( category.isRoot() ) {
            NavLocation allLocation = new NavLocation( location, "all", new Linkable() {
                public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                    return new PhetLink( id, context.getPrefix() + "simulations/index" );
                }
            } );
            addLocation( allLocation );
            location.addChild( allLocation );

            NavLocation translatedSimsLocation = new NavLocation( location, "simulations.translated", TranslatedSimsPage.getLinker() );
            addLocation( translatedSimsLocation );
            location.addChild( translatedSimsLocation );
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
