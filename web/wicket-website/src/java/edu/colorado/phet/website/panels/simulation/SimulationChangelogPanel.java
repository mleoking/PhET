package edu.colorado.phet.website.panels.simulation;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.data.Changelog;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * Displays the changelog for a simulation
 */
public class SimulationChangelogPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( SimulationChangelogPanel.class.getName() );

    public SimulationChangelogPanel( String id, final LocalizedSimulation simulation, final PageContext context, boolean displayDevEntries ) {
        super( id, context );

        // TODO: refactor so we don't have that long docroot access string
        Changelog log = new Changelog( simulation.getSimulation().getProject().getChangelogFile( PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() ) );

        if ( !displayDevEntries ) {
            log = log.getNonDevChangelog();
        }

        add( new ListView<Changelog.Entry>( "entry", log.getEntries() ) {
            @Override
            protected void populateItem( ListItem<Changelog.Entry> entryItem ) {
                Changelog.Entry entry = entryItem.getModelObject();
                entryItem.add( new Label( "header", entry.headerString( getLocale() ) ) );

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

        add( simulation.getSimulation().getProject().getRawChangelogLinker().getLink( "raw-link", context, getPhetCycle() ) );
    }

}