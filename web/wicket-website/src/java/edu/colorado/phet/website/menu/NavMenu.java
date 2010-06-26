package edu.colorado.phet.website.menu;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.Link;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.panels.UpdatePasswordSuccessPanel;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.content.*;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.contribution.ContributionBrowsePage;
import edu.colorado.phet.website.content.contribution.ContributionCreatePage;
import edu.colorado.phet.website.content.contribution.ContributionGuidelinesPanel;
import edu.colorado.phet.website.content.contribution.ContributionManagePage;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.getphet.OneAtATimePanel;
import edu.colorado.phet.website.content.getphet.RunOurSimulationsPanel;
import edu.colorado.phet.website.content.search.SearchResultsPage;
import edu.colorado.phet.website.content.simulations.CategoryPage;
import edu.colorado.phet.website.content.simulations.TranslatedSimsPage;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.content.workshops.UgandaWorkshopPhotosPanel;
import edu.colorado.phet.website.content.workshops.UgandaWorkshopsPanel;
import edu.colorado.phet.website.content.workshops.WorkshopsPanel;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.util.AbstractCategoryListener;
import edu.colorado.phet.website.data.util.CategoryChangeHandler;
import edu.colorado.phet.website.translation.TranslationMainPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.Linkable;

/**
 * Initializes and handles the navigation locations (NavLocation)s. Builds from the database during startup, and then
 * uses events to keep the locations synchronized with any possible category changes.
 */
public class NavMenu {

    /**
     * Map of nav location key => nav location, for fast lookup
     */
    private HashMap<String, NavLocation> cache = new HashMap<String, NavLocation>();

    /**
     * List of all nav locations
     */
    private List<NavLocation> locations = new LinkedList<NavLocation>();

    /**
     * Lists nav locations that correspond to simulation categories. For instance the location for "Physics" is in this,
     * but the location for "PhET Translation Utility" isn't.
     */
    private List<NavLocation> locationsBelowCategories = new LinkedList<NavLocation>();

    /**
     * The nav location shown as "Simulations". Essentially the root of categories.
     */
    private NavLocation simulations;

    private static final Logger logger = Logger.getLogger( NavMenu.class.getName() );

    // TODO: now that we have possible runtime modification of locations with changes of categories, something must be done about synchronization

    // TODO: add more docs

    public NavMenu() {
        NavLocation home = new NavLocation( null, "home", IndexPage.getLinker() );
        addMajorLocation( home );

        simulations = new NavLocation( null, "simulations", CategoryPage.getLinker() );
        addMajorLocation( simulations );

        NavLocation teacherIdeas = new NavLocation( null, "teacherIdeas", TeacherIdeasPanel.getLinker() );
        addMajorLocation( teacherIdeas );

        NavLocation teacherIdeasBrowse = new NavLocation( teacherIdeas, "teacherIdeas.browse", ContributionBrowsePage.getLinker() );
        addLocation( teacherIdeasBrowse );
        teacherIdeas.addChild( teacherIdeasBrowse );

        NavLocation workshops = new NavLocation( teacherIdeas, "workshops", WorkshopsPanel.getLinker() );
        addLocation( workshops );
        teacherIdeas.addChild( workshops );

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

        NavLocation aboutLegend = new NavLocation( teacherIdeas, "about.legend", AboutLegendPanel.getLinker() );
        addLocation( aboutLegend );
        teacherIdeas.addChild( aboutLegend );

        // orphan!
        NavLocation classroomUse = new NavLocation( teacherIdeas, "for-teachers.classroom-use", ClassroomUsePanel.getLinker() );
        addLocation( classroomUse );

        NavLocation getPhet = new NavLocation( null, "get-phet", RunOurSimulationsPanel.getLinker() );
        addMajorLocation( getPhet );

        NavLocation online = new NavLocation( getPhet, "get-phet.on-line", CategoryPage.getLinker() );
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

        NavLocation forTranslators = new NavLocation( null, "forTranslators", ForTranslatorsPanel.getLinker() );
        addMajorLocation( forTranslators );

        NavLocation translationUtility = new NavLocation( forTranslators, "forTranslators.translationUtility", TranslationUtilityPanel.getLinker() );
        addLocation( translationUtility );
        forTranslators.addChild( translationUtility );

        NavLocation translateWebsite = new NavLocation( forTranslators, "forTranslators.website", TranslationMainPage.getLinker() );
        addLocation( translateWebsite );
        forTranslators.addChild( translateWebsite );

        NavLocation donate = new NavLocation( null, "donate", DonatePanel.getLinker() );
        addMajorLocation( donate );

        NavLocation research = new NavLocation( null, "research", ResearchPanel.getLinker() );
        addMajorLocation( research );

        NavLocation about = new NavLocation( null, "about", AboutMainPanel.getLinker() );
        addMajorLocation( about );

        NavLocation aboutSourceCode = new NavLocation( about, "about.source-code", AboutSourceCodePanel.getLinker() );
        addLocation( aboutSourceCode );
        about.addChild( aboutSourceCode );

        NavLocation aboutNews = new NavLocation( about, "about.news", AboutNewsPanel.getLinker() );
        addLocation( aboutNews );
        about.addChild( aboutNews );

        NavLocation aboutLicensing = new NavLocation( about, "about.licensing", AboutLicensingPanel.getLinker() );
        addLocation( aboutLicensing );
        about.addChild( aboutLicensing );

        NavLocation aboutContact = new NavLocation( about, "about.contact", AboutContactPanel.getLinker() );
        addLocation( aboutContact );
        about.addChild( aboutContact );

        NavLocation aboutSponsors = new NavLocation( about, "sponsors", AboutSponsorsPanel.getLinker() );
        addLocation( aboutSponsors );
        about.addChild( aboutSponsors );

        // unconnected locations

        NavLocation byKeyword = new NavLocation( null, "simulations.by-keyword", CategoryPage.getLinker() );
        addLocation( byKeyword );

        NavLocation searchResults = new NavLocation( null, "search.results", SearchResultsPage.getLinker( null ) );
        addLocation( searchResults );

        NavLocation uganda = new NavLocation( null, "workshops.uganda", UgandaWorkshopsPanel.getLinker() );
        addLocation( uganda );

        NavLocation ugandaPhotos = new NavLocation( null, "workshops.uganda-photos", UgandaWorkshopPhotosPanel.getLinker() );
        addLocation( ugandaPhotos );

        NavLocation updatePasswordSuccess= new NavLocation( null, "updatePasswordSuccess", UpdatePasswordSuccessPanel.getLinker() );
        addLocation( updatePasswordSuccess );
        
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

        CategoryChangeHandler.addListener( new AbstractCategoryListener() {
            @Override
            public void categoryAdded( Category cat ) {
                // we are assuming that only simulation categories are changed at runtime
                logger.info( "Category " + cat.getName() + " added, adding nav location" );
                NavLocation parentLocation = cat.getParent().getNavLocation( NavMenu.this );
                if ( parentLocation == null ) {
                    parentLocation = simulations;
                }
                NavLocation location = createSimLocation( parentLocation, cat.getName(), cat );
                parentLocation.addChild( location );
                if ( parentLocation == simulations ) {
                    simulations.organizeSimulationLocations();
                }
                addLocation( location );
            }

            @Override
            public void categoryRemoved( Category cat ) {
                logger.info( "Category " + cat.getName() + " removed, removing nav location" );
                NavLocation location = getLocationByKey( cat.getName() );
                if ( location != null ) {
                    removeLocation( location );
                }
                else {
                    logger.warn( "trying to remove nonexistant category?" );
                }
            }

            @Override
            public void categoryChildrenReordered( final Category cat ) {
                NavLocation location = getLocationByKey( cat.getName() );
                if ( location == null ) {
                    location = simulations;
                }
                final List<NavLocation> clocs = new LinkedList<NavLocation>();
                // TODO: warning, can this not be lazy?
                final NavLocation location1 = location;
                HibernateUtils.wrapTransaction( PhetRequestCycle.get().getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category catty = (Category) session.load( Category.class, cat.getId() );
                        for ( Object o : catty.getSubcategories() ) {
                            Category subcat = (Category) o;
                            NavLocation chloc = getLocationByKey( subcat.getName() );
                            clocs.add( chloc );
                        }
                        Collections.sort( location1.getChildren(), new Comparator<NavLocation>() {
                            public int compare( NavLocation a, NavLocation b ) {
                                // slowsort
                                Integer ai = new Integer( clocs.indexOf( a ) );
                                Integer bi = new Integer( clocs.indexOf( b ) );
                                if ( ai == -1 ) { ai = 1000000; }
                                if ( bi == -1 ) { bi = 1000000; }
                                return ai.compareTo( bi );
                            }
                        } );
                        return true;
                    }
                } );

            }
        } );

    }

    private NavLocation createSimLocation( NavLocation parent, String name, final Category category ) {
        return new NavLocation( parent, name, new Linkable() {
            public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                return new RawLink( id, context.getPrefix() + "simulations/category/" + category.getCategoryPath() );
            }
        } );
    }

    public void buildCategoryMenu( NavLocation location, Category category ) {
        for ( Object o : category.getSubcategories() ) {
            final Category subCategory = (Category) o;
            NavLocation subLocation = createSimLocation( location, subCategory.getName(), subCategory );
            addLocation( subLocation );
            location.addChild( subLocation );
            buildCategoryMenu( subLocation, subCategory );
        }
        if ( category.isRoot() ) {
            NavLocation allLocation = new NavLocation( location, "all", new Linkable() {
                public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                    return new RawLink( id, context.getPrefix() + "simulations/index" );
                }
            } );
            addLocation( allLocation );
            location.addChild( allLocation );
            locationsBelowCategories.add( allLocation );

            NavLocation translatedSimsLocation = new NavLocation( location, "simulations.translated", TranslatedSimsPage.getLinker() );
            addLocation( translatedSimsLocation );
            location.addChild( translatedSimsLocation );
            locationsBelowCategories.add( translatedSimsLocation );

            PhetLocales phetLocales = PhetWicketApplication.get().getSupportedLocales();
            for ( String localeName : phetLocales.getSortedNames() ) {
                Locale locale = phetLocales.getLocale( localeName );
                NavLocation loc = new NavLocation( translatedSimsLocation, "language.names." + LocaleUtils.localeToString( locale ), TranslatedSimsPage.getLinker( locale ) );
                addLocation( loc );
                translatedSimsLocation.addChild( loc );

                // don't show all of the tons of language subcategories unless the one we want is selected
                loc.setHidden( true );
            }

        }
    }

    public void addMajorLocation( NavLocation location ) {
        locations.add( location );
        addLocation( location );
    }

    public void addLocation( NavLocation location ) {
        logger.debug( "adding location " + location.getKey() );
        cache.put( location.getKey(), location );
    }

    /**
     * Removes the location and all of its children
     *
     * @param location
     */
    public void removeLocation( NavLocation location ) {
        logger.debug( "removing location " + location.getKey() );
        locations.remove( location );
        cache.remove( location.getKey() );
        NavLocation parent = location.getParent();
        if ( parent != null ) {
            parent.removeChild( location );
        }
        for ( NavLocation child : location.getChildren() ) {
            removeLocation( child );
        }
    }

    public List<NavLocation> getLocations() {
        return locations;
    }

    public NavLocation getLocationByKey( String key ) {
        return cache.get( key );
    }

    public NavLocation getSimulationsLocation() {
        return simulations;
    }

    public List<NavLocation> getLocationsBelowCategories() {
        return locationsBelowCategories;
    }
}
