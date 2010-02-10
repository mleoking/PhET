package edu.colorado.phet.website.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.content.ContributionPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.util.PageContext;

public class ContributionBrowsePanel extends PhetPanel {

    private final List<Contribution> contributions;

    public ContributionBrowsePanel( String id, final PageContext context, List<Contribution> contributions ) {
        super( id, context );

        this.contributions = contributions;

        // TODO: is localization necessary?

        add( new ListView( "contributions", contributions ) {
            protected void populateItem( ListItem item ) {
                Contribution contribution = (Contribution) item.getModel().getObject();
                Link link = ContributionPage.createLink( "contribution-link", context, contribution );
                link.add( new Label( "contribution-title", contribution.getTitle() ) );
                item.add( link );
                item.add( new Label( "contribution-authors", contribution.getAuthors() ) );
            }
        } );

//        IDataProvider simData = new SimulationDataProvider( simulations );
//        GridView gridView = new GridView( "rows", simData ) {
//            @Override
//            protected void populateEmptyItem( Item item ) {
//                item.setVisible( false );
//            }
//
//            @Override
//            protected void populateItem( Item item ) {
//                LocalizedSimulation simulation = (LocalizedSimulation) item.getModelObject();
//                PhetLink link = SimulationPage.createLink( "simulation-link", context, simulation );
//                link.add( new Label( "title", simulation.getTitle() ) );
//                if ( !simulation.getLocale().equals( context.getLocale() ) ) {
//                    // sim isn't translated
//                    link.add( new AttributeAppender( "class", new Model( "untranslated-sim" ), " " ) );
//                }
//                String alt;
//                try {
//                    alt = MessageFormat.format( "Screenshot of the simulation {0}", encode( simulation.getTitle() ) );
//                }
//                catch( RuntimeException e ) {
//                    e.printStackTrace();
//                    alt = "Screenshot of the simulation";
//                }
//                link.add( new StaticImage( "thumbnail", simulation.getSimulation().getThumbnailUrl(), alt ) );
//                item.add( link );
//            }
//        };
//        gridView.setColumns( 3 );
//        add( gridView );
//
//        add( HeaderContributor.forCss( "/css/simulation-display-v1.css" ) );

    }

//    private static class SimulationDataProvider implements IDataProvider {
//        private List<LocalizedSimulation> simulations;
//
//        private SimulationDataProvider( List<LocalizedSimulation> simulations ) {
//            this.simulations = simulations;
//        }
//
//
//        public Iterator iterator( int first, int count ) {
//            int endIndex = first + count;
//            if ( endIndex > simulations.size() ) {
//                endIndex = simulations.size();
//            }
//            return simulations.subList( first, endIndex ).iterator();
//        }
//
//        public int size() {
//            return simulations.size();
//        }
//
//        public IModel model( final Object o ) {
//            return new IModel() {
//                public Object getObject() {
//                    return o;
//                }
//
//                public void setObject( Object o ) {
//
//                }
//
//                public void detach() {
//
//                }
//            };
//        }
//
//        public void detach() {
//
//        }
//    }

}