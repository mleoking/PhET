package edu.colorado.phet.wickettest.content;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import edu.colorado.phet.tomcattest.WebSimulation;
import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.util.PhetPage;
import edu.colorado.phet.wickettest.util.StaticImage;

public class SimulationDisplay extends PhetPage {
    public SimulationDisplay( PageParameters parameters ) {
        super( parameters );

        List<SimulationModel> models = getOrderedSimulationModels();

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );
                item.add( new Label( "title", simulation.getTitle() ) );
                item.add( new StaticImage( "thumbnail", new Model( simulation.getThumbnailUrl() ) ) );
            }
        };
        add( simulationList );

    }
}