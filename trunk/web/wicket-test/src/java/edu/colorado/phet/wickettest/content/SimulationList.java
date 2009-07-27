package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.panels.SimulationListPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetRegularPage;

public class SimulationList extends PhetRegularPage {
    public SimulationList( PageParameters parameters ) {
        super( parameters );

        Label title = new Label( "page-title", new ResourceModel( "simulationDisplay.simulations" ) );
        add( title );

        add( new SimulationListPanel( "simulation-list-panel", getPageContext() ) );

    }

    public static String getMappingString() {
        return "^all-simulations$";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + "all-simulations" );
            }
        };
    }
}