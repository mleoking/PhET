package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.panels.SimulationListPanel;
import edu.colorado.phet.wickettest.util.PhetPage;

public class SimulationList extends PhetPage {
    public SimulationList( PageParameters parameters ) {
        super( parameters );

        Label title = new Label( "page-title", new ResourceModel( "simulationDisplay.simulations" ) );
        add( title );

        add( new SimulationListPanel( "simulation-list-panel", this ) );

    }

    public static String getMappingString() {
        return "^all-simulations$";
    }
}