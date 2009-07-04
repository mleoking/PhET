package edu.colorado.phet.wickettest.content;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;

public class SimulationList extends PhetPage {
    public SimulationList( PageParameters parameters ) {
        super( parameters );

        List<SimulationModel> models = getOrderedSimulationModels();

        ListView simulationList = new ListView( "simulation-list", models ) {
            protected void populateItem( ListItem item ) {
                WebSimulation simulation = (WebSimulation) ( ( (SimulationModel) ( item.getModel().getObject() ) ).getObject() );

                Label title = new Label( "title", simulation.getTitle() );
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( title );
                item.add( link );
            }
        };
        add( simulationList );

    }
}