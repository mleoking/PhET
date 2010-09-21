package edu.colorado.phet.website.panels.simulation;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

import static edu.colorado.phet.website.util.HtmlUtils.encode;

public class SimulationDisplayPanel extends PhetPanel {

    public SimulationDisplayPanel( String id, final PageContext context, List<LocalizedSimulation> simulations ) {
        super( id, context );

        IDataProvider simData = new SimulationDataProvider( simulations );
        GridView gridView = new GridView( "rows", simData ) {
            @Override
            protected void populateEmptyItem( Item item ) {
                item.setVisible( false );
            }

            @Override
            protected void populateItem( Item item ) {
                LocalizedSimulation simulation = (LocalizedSimulation) item.getModelObject();
                Link link = SimulationPage.getLinker( simulation ).getLink( "simulation-link", context, getPhetCycle() );
                link.add( new Label( "title", simulation.getTitle() ) );
                if ( !simulation.getLocale().getLanguage().equals( context.getLocale().getLanguage() ) ) {
                    // sim isn't translated
                    link.add( new AttributeAppender( "class", new Model( "untranslated-sim" ), " " ) );
                }
                String alt;
                try {
                    alt = StringUtils.messageFormat( "Screenshot of the simulation {0}", encode( simulation.getTitle() ) );
                }
                catch( RuntimeException e ) {
                    e.printStackTrace();
                    alt = "Screenshot of the simulation";
                }
                link.add( new StaticImage( "thumbnail", simulation.getSimulation().getThumbnailUrl(), alt ) );
                item.add( link );
            }
        };
        gridView.setColumns( 3 );
        add( gridView );

        add( HeaderContributor.forCss( CSS.CATEGORY_PAGE ) );

    }

    private static class SimulationDataProvider implements IDataProvider {
        private List<LocalizedSimulation> simulations;

        private SimulationDataProvider( List<LocalizedSimulation> simulations ) {
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