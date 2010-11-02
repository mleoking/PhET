package edu.colorado.phet.website.translation.entities;

import java.util.HashSet;

import org.hibernate.Session;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.SideNavMenu;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class NavigationEntity extends TranslationEntity {
    public NavigationEntity() {
        addString( "nav.home" );
        addString( "nav.simulations" );
        HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Category root = Category.getRootCategory( session );
                addCategory( root );
                return true;
            }
        } );
        addString( "nav.all" );
        addString( "nav.simulations.translated" );
        addString( "nav.simulations.by-keyword" );
        addString( "nav.teacherIdeas" );
        addString( "nav.teacherIdeas.browse" );
        addString( "nav.teacherIdeas.submit" );
        addString( "nav.teacherIdeas.edit" );
        addString( "nav.teacherIdeas.manage" );
        addString( "nav.teacherIdeas.guide" );
        addString( "nav.for-teachers.classroom-use" );
        addString( "nav.workshops" );
        addString( "nav.stayConnected" );
        addString( "nav.get-phet" );
        addString( "nav.get-phet.on-line" );
        addString( "nav.get-phet.full-install" );
        addString( "nav.get-phet.one-at-a-time" );
        addString( "nav.troubleshooting.main" );
        addString( "nav.troubleshooting.java" );
        addString( "nav.troubleshooting.flash" );
        addString( "nav.troubleshooting.javascript" );
        addString( "nav.donate" );
        addString( "nav.research" );
        addString( "nav.forTranslators" );
        addString( "nav.forTranslators.translationUtility" );
        addString( "nav.forTranslators.website" );
        addString( "nav.about" );
        addString( "nav.about.source-code" );
        addString( "nav.about.news" );
        addString( "nav.about.legend" );
        addString( "nav.about.contact" );
        addString( "nav.about.who-we-are" );
        addString( "nav.about.licensing" );
        addString( "nav.sponsors" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                HashSet<NavLocation> locations = new HashSet<NavLocation>();
                locations.add( ( (PhetWicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "motion" ) );
                locations.add( ( (PhetWicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "tools" ) );
                locations.add( ( (PhetWicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "get-phet.full-install" ) );
                locations.add( ( (PhetWicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "troubleshooting.java" ) );
                locations.add( ( (PhetWicketApplication) requestCycle.getApplication() ).getMenu().getLocationByKey( "about.licensing" ) );
                return new SideNavMenu( id, context, locations );
            }
        }, "Navigation Menu" );

        // categories (and translatable strings) can change now during runtime, so we need to keep this up to date
        HibernateEventListener.addListener( Category.class, new AbstractChangeListener() {
            @Override
            public void onInsert( Object object, PostInsertEvent event ) {
                Category cat = (Category) object;
                addString( "nav." + cat.getName() );
            }

            @Override
            public void onDelete( Object object, PostDeleteEvent event ) {
                Category cat = (Category) object;
                removeString( "nav." + cat.getName() );
            }
        } );
    }

    public String getDisplayName() {
        return "Navigation";
    }

    private void addCategory( Category cat ) {
        if ( !cat.isRoot() ) {
            addString( "nav." + cat.getName() );
        }
        for ( Object o : cat.getSubcategories() ) {
            addCategory( (Category) o );
        }
    }
}
