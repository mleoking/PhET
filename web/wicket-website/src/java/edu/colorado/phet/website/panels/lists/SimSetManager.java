package edu.colorado.phet.website.panels.lists;

import java.io.Serializable;
import java.util.*;

import org.hibernate.Session;

import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * Handles a simulation set control where the user can select or remove simulations
 */
public abstract class SimSetManager implements Serializable {
    private List<Simulation> simulations;
    private List<Simulation> allSimulations;
    private Map<Simulation, String> titleMap;
    private List<SimOrderItem> items;
    private List<SimOrderItem> allItems;

    /**
     * NOTE: already in transaction, so throw all exceptions!
     *
     * @param session current session
     * @return list of simulations currently in set
     */
    public abstract Set getInitialSimulations( Session session );

    public SimSetManager( Session session, final Locale locale ) {
        simulations = new LinkedList<Simulation>();
        allSimulations = new LinkedList<Simulation>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( Object o : getInitialSimulations( session ) ) {
                    simulations.add( (Simulation) o );
                }
                List sims = session.createQuery( "select s from Simulation as s" ).list();
                for ( Object s : sims ) {
                    Simulation simulation = (Simulation) s;
                    if ( simulation.isVisible() ) {
                        allSimulations.add( simulation );
                    }
                }
                return true;
            }
        } );

        titleMap = new HashMap<Simulation, String>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( Simulation simulation : allSimulations ) {
                    titleMap.put( simulation, simulation.getBestLocalizedSimulation( locale ).getTitle() );
                }
                return true;
            }
        } );

        items = new LinkedList<SimOrderItem>();
        allItems = new LinkedList<SimOrderItem>();

        for ( Simulation simulation : simulations ) {
            items.add( new SimOrderItem( simulation, titleMap.get( simulation ) ) );
        }

        for ( Simulation simulation : allSimulations ) {
            allItems.add( new SimOrderItem( simulation, titleMap.get( simulation ) ) );
        }
    }

    public SortedList<SimOrderItem> getComponent( String id, PageContext context ) {
        return new SortedList<SimOrderItem>( id, context, items, allItems ) {
            public boolean onItemAdd( final SimOrderItem item ) {
                for ( SimOrderItem oldItem : items ) {
                    if ( oldItem.getId() == item.getId() ) {
                        // already in list. don't want duplicates!
                        return false;
                    }
                }
                return true;
            }

            public boolean onItemRemove( final SimOrderItem item, int index ) {
                return true;
            }

        };
    }

    public List<SimOrderItem> getItems() {
        return items;
    }

    public List<Simulation> getSimulations() {
        List<Simulation> simulations = new LinkedList<Simulation>();
        for ( SimOrderItem item : items ) {
            simulations.add( item.getSimulation() );
        }
        return simulations;
    }
}
