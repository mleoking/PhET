package edu.colorado.phet.website.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.data.Changelog;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class PublicChangelogTest extends WebPage {

    private static final Logger logger = Logger.getLogger( PublicChangelogTest.class.getName() );

    public PublicChangelogTest() {
        final List<ProjectEntry> entries = new LinkedList<ProjectEntry>();

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
                }
                return true;
            }
        } );

        Collections.sort( entries, new Comparator<ProjectEntry>() {
            public int compare( ProjectEntry a, ProjectEntry b ) {
                int ret = -a.getEntry().getDate().compareTo( b.getEntry().getDate() );
                if( ret == 0 ) {
                    return -a.getEntry().getRevision().compareTo( b.getEntry().getRevision() );
                } else {
                    return ret;
                }
            }
        } );

        add( new ListView<ProjectEntry>( "entry", entries ) {
            @Override
            protected void populateItem( ListItem<ProjectEntry> entryItem ) {
                Changelog.Entry entry = entryItem.getModelObject().getEntry();
                Project project = entryItem.getModelObject().getProject();
                entryItem.add( new Label( "header", project.getName() + " " + entry.headerString( PhetWicketApplication.getDefaultLocale(), true ) ) );

                if ( entry.getLines().isEmpty() ) {
                    entryItem.add( new InvisibleComponent( "line" ) );
                }
                else {
                    entryItem.add( new ListView<Changelog.Line>( "line", entry.getLines() ) {
                        @Override
                        protected void populateItem( ListItem<Changelog.Line> lineItem ) {
                            Changelog.Line line = lineItem.getModelObject();

                            lineItem.add( new Label( "text", line.getMessage() ) );
                        }
                    } );
                }
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