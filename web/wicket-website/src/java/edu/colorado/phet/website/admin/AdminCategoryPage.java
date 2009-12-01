package edu.colorado.phet.website.admin;

import java.io.Serializable;
import java.util.*;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminCategoryPage extends AdminPage {
    private Category category;
    private List<Simulation> simulations;
    private List<Simulation> allSimulations;
    private List<SimOrderItem> items;
    private List<SimOrderItem> allItems;
    private Map<Simulation, String> titleMap;

    private static Logger logger = Logger.getLogger( AdminCategoryPage.class.getName() );

    public AdminCategoryPage( PageParameters parameters ) {
        super( parameters );

        final int categoryId = parameters.getInt( "categoryId" );

        simulations = new LinkedList<Simulation>();
        allSimulations = new LinkedList<Simulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                category = (Category) session.load( Category.class, categoryId );
                for ( Object o : category.getSimulations() ) {
                    simulations.add( (Simulation) o );
                }
                List sims = session.createQuery( "select s from Simulation as s" ).list();
                for ( Object s : sims ) {
                    Simulation simulation = (Simulation) s;
                    if ( simulation.getProject().isVisible() ) {
                        allSimulations.add( simulation );
                    }
                }
                return true;
            }
        } );

        titleMap = new HashMap<Simulation, String>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                for ( LocalizedSimulation localizedSimulation : HibernateUtils.getLocalizedSimulationsMatching( session, null, null, PhetWicketApplication.getDefaultLocale() ) ) {
                    titleMap.put( localizedSimulation.getSimulation(), localizedSimulation.getTitle() );
                }
                return true;
            }
        } );


        Collections.sort( allSimulations, new Comparator<Simulation>() {
            public int compare( Simulation a, Simulation b ) {
                String ta = titleMap.get( a );
                String tb = titleMap.get( b );
                if ( ta != null ) {
                    return ta.compareToIgnoreCase( tb );
                }
                else {
                    return -1;
                }
            }
        } );

        add( new LocalizedText( "category-title", category.getNavLocation( getNavMenu() ).getLocalizationKey() ) );

        items = new LinkedList<SimOrderItem>();
        allItems = new LinkedList<SimOrderItem>();

        for ( Simulation simulation : simulations ) {
            items.add( new SimOrderItem( simulation ) );
        }

        for ( Simulation simulation : allSimulations ) {
            allItems.add( new SimOrderItem( simulation ) );
        }

        add( new OrderList<SimOrderItem>( "simulations", getPageContext(), items, allItems ) {
            public boolean onAdd( final SimOrderItem item ) {
                for ( SimOrderItem curItem : items ) {
                    if ( curItem.getId() == item.getId() ) {
                        return false;
                    }
                }
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        Simulation sim = (Simulation) session.load( Simulation.class, item.getId() );
                        cat.addSimulation( sim );
                        session.update( cat );
                        session.update( sim );
                        return true;
                    }
                } );
                logger.info( "add: #" + item.getId() + " success: " + success );
                return success;
            }

            public boolean onRemove( final SimOrderItem item, int index ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        Simulation sim = (Simulation) session.load( Simulation.class, item.getId() );
                        cat.removeSimulation( sim );
                        session.update( cat );
                        session.update( sim );
                        return true;
                    }
                } );
                logger.info( "remove: #" + item.getId() + " at " + index + " success: " + success );
                return success;
            }

            public boolean onSwap( SimOrderItem a, SimOrderItem b, final int aIndex, final int bIndex ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        Collections.swap( cat.getSimulations(), aIndex, bIndex );
                        session.update( cat );
                        return true;
                    }
                } );
                logger.info( "swap: #" + a.getId() + ", " + b.getId() + " at " + aIndex + ", " + bIndex + " success: " + success );
                return success;
            }

            public Component getHeaderComponent( String id ) {
                return new Label( id, "Simulations" );
            }
        } );
    }

    private class SimOrderItem implements OrderListItem, Serializable {
        private Simulation simulation;
        private String title;

        public SimOrderItem( Simulation simulation ) {
            this.simulation = simulation;
            this.title = titleMap.get( simulation );
        }

        public String getDisplayValue() {
            return title;
        }

        public Component getDisplayComponent( String id ) {
            return new Label( id, title );
        }

        public int getId() {
            return simulation.getId();
        }
    }
}
