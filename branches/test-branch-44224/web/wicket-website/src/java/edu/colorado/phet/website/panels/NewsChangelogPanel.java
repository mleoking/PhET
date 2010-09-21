package edu.colorado.phet.website.panels;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.components.VisListView;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.*;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.data.util.IChangeListener;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class NewsChangelogPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( NewsChangelogPanel.class.getName() );

    public NewsChangelogPanel( String id, final PageContext context ) {
        super( id, context );

        // TODO: localize

        final List<ProjectEntry> entries = new LinkedList<ProjectEntry>();

        // store titles for later so we don't have to open multiple hibernate transactions
        final Map<Simulation, String> titleMap = new HashMap<Simulation, String>();

        HibernateUtils.wrapTransaction( PhetRequestCycle.get().getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List projects = session.createQuery( "select p from Project as p where p.visible = true" ).list();
                for ( Object o : projects ) {
                    Project project = (Project) o;
                    Changelog log = new Changelog( project.getChangelogFile( PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() ) );
                    log = log.getNonDevChangelog();
                    for ( Changelog.Entry entry : log.getEntries() ) {
                        if ( entry.getDate() == null ) { continue; }
                        entries.add( new ProjectEntry( project, entry ) );
                    }

                    // load lazy hibernate simulations
                    for ( Object ox : project.getSimulations() ) {
                        Simulation sim = (Simulation) ox;
                        titleMap.put( sim, sim.getBestLocalizedSimulation( getLocale() ).getTitle() );
                    }
                }
                return true;
            }
        } );

        Collections.sort( entries, new Comparator<ProjectEntry>() {
            public int compare( ProjectEntry a, ProjectEntry b ) {
                int ret = -a.getEntry().getDate().compareTo( b.getEntry().getDate() );
                if ( ret == 0 ) {
                    return -a.getEntry().getRevision().compareTo( b.getEntry().getRevision() );
                }
                else {
                    return ret;
                }
            }
        } );

        add( new ListView<ProjectEntry>( "entry", entries ) {
            @Override
            protected void populateItem( ListItem<ProjectEntry> entryItem ) {
                Changelog.Entry entry = entryItem.getModelObject().getEntry();
                Project project = entryItem.getModelObject().getProject();
                entryItem.add( new Label( "header", project.getName() + " " + entry.headerString( getLocale(), false ) ) );

                entryItem.add( new ListView<Simulation>( "sim", new LinkedList<Simulation>( project.getSimulations() ) ) {
                    @Override
                    protected void populateItem( ListItem<Simulation> simItem ) {
                        Simulation sim = simItem.getModelObject();
                        Link simLink = SimulationPage.getLinker( sim ).getLink( "sim-link", PageContext.getNewDefaultContext(), PhetRequestCycle.get() );
                        simItem.add( simLink );

                        simLink.add( new Label( "sim-title", titleMap.get( sim ) ) );
                    }
                } );

                entryItem.add( new VisListView<Changelog.Line>( "line", entry.getLines() ) {
                    @Override
                    protected void populateItem( ListItem<Changelog.Line> lineItem ) {
                        Changelog.Line line = lineItem.getModelObject();

                        lineItem.add( new Label( "text", line.getMessage() ) );
                    }
                } );
            }
        } );

        // TODO: refactor to make these dependencies fixed
        addDependency( new EventDependency() {

            private IChangeListener stringListener;

            @Override
            protected void addListeners() {
                logger.debug( " added" );
                stringListener = createTranslationChangeInvalidator( context.getLocale() );
                HibernateEventListener.addListener( Project.class, getAnyChangeInvalidator() );
                HibernateEventListener.addListener( TranslatedString.class, stringListener );
                HibernateEventListener.addListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.addListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }

            @Override
            protected void removeListeners() {
                HibernateEventListener.removeListener( Project.class, getAnyChangeInvalidator() );
                HibernateEventListener.removeListener( TranslatedString.class, stringListener );
                HibernateEventListener.removeListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.removeListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }
        } );
    }

    public static class ProjectEntry {
        private Project project;
        private Changelog.Entry entry;

        public ProjectEntry( Project project, Changelog.Entry entry ) {
            this.project = project;
            this.entry = entry;
        }

        public Project getProject() {
            return project;
        }

        public Changelog.Entry getEntry() {
            return entry;
        }
    }
}