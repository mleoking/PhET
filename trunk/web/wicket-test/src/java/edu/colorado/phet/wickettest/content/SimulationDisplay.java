package edu.colorado.phet.wickettest.content;

import java.text.MessageFormat;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;
import edu.colorado.phet.wickettest.util.StaticImage;

public class SimulationDisplay extends PhetPage {
    public SimulationDisplay( PageParameters parameters ) {
        super( parameters );

        List<SimulationModel> models = getOrderedSimulationModels();

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( new Label( "title", simulation.getTitle() ) );
                link.add( new StaticImage( "thumbnail", simulation.getThumbnailUrl(), MessageFormat.format( "Screenshot of the simulation {0}", simulation.getTitle() ) ) );
                item.add( link );
            }
        };
        add( simulationList );

    }
}