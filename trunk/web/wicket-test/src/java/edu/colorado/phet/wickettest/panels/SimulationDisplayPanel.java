package edu.colorado.phet.wickettest.panels;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import edu.colorado.phet.wickettest.WebSimulation;
import edu.colorado.phet.wickettest.content.SimulationPage;
import static edu.colorado.phet.wickettest.util.HtmlUtils.encode;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.SqlUtils;
import edu.colorado.phet.wickettest.util.StaticImage;

public class SimulationDisplayPanel extends PhetPanel {

    public SimulationDisplayPanel( String id, final Locale myLocale ) {
        super( id, myLocale );

        List<WebSimulation> simulations = SqlUtils.getOrderedSimulations( getContext(), getMyLocale() );

        IDataProvider simData = new SimulationDataProvider( simulations );
        GridView gridView = new GridView( "rows", simData ) {
            @Override
            protected void populateEmptyItem( Item item ) {
                item.setVisible( false );
            }

            @Override
            protected void populateItem( Item item ) {
                WebSimulation simulation = (WebSimulation) item.getModelObject();
                PhetLink link = SimulationPage.createLink( "simulation-link", getMyLocale(), simulation );
                link.add( new Label( "title", simulation.getTitle() ) );
                link.add( new StaticImage( "thumbnail", simulation.getThumbnailUrl(), MessageFormat.format( "Screenshot of the simulation {0}", encode( simulation.getTitle() ) ) ) );
                item.add( link );
            }
        };
        gridView.setColumns( 3 );
        add( gridView );

    }

    private static class SimulationDataProvider implements IDataProvider {
        private List<WebSimulation> simulations;

        private SimulationDataProvider( List<WebSimulation> simulations ) {
            this.simulations = simulations;
        }


        public Iterator iterator( int first, int count ) {
            int endIndex = first + count;
            if ( endIndex > simulations.size() ) {
                endIndex = simulations.size();
            }
            return simulations.subList( first, endIndex ).iterator();
        }

        public int size() {
            return simulations.size();
        }

        public IModel model( final Object o ) {
            return new IModel() {
                public Object getObject() {
                    return o;
                }

                public void setObject( Object o ) {

                }

                public void detach() {

                }
            };
        }

        public void detach() {

        }
    }

}