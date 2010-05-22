package edu.colorado.phet.website.admin;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.util.CategoryChangeHandler;
import edu.colorado.phet.website.panels.lists.CategoryOrderItem;
import edu.colorado.phet.website.panels.lists.JustOrderList;
import edu.colorado.phet.website.panels.lists.OrderList;
import edu.colorado.phet.website.panels.lists.SimOrderItem;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.StringUtils;

public class AdminCategoryPage extends AdminPage {
    private Category category;
    private List<Simulation> simulations;
    private List<Simulation> allSimulations;
    private List<SimOrderItem> items;
    private List<SimOrderItem> allItems;
    private List<Category> categories = new LinkedList<Category>();
    private Map<Simulation, String> titleMap;

    private Model autoModel;

    private static final Logger logger = Logger.getLogger( AdminCategoryPage.class.getName() );
    private Label autoLabel;

    public AdminCategoryPage( PageParameters parameters ) {
        super( parameters );

        final int categoryId = parameters.getInt( "categoryId" );

        simulations = new LinkedList<Simulation>();
        allSimulations = new LinkedList<Simulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                category = (Category) session.load( Category.class, categoryId );
                autoModel = new Model( category.isAuto() );
                for ( Object o : category.getSimulations() ) {
                    simulations.add( (Simulation) o );
                }
                List sims = session.createQuery( "select s from Simulation as s" ).list();
                for ( Object s : sims ) {
                    Simulation simulation = (Simulation) s;
                    if ( simulation.isVisible() ) {
                        allSimulations.add( simulation );
                    }
                }
                for ( Object o : category.getSubcategories() ) {
                    Category cat = (Category) o;
                    categories.add( cat );
                }
                return true;
            }
        } );

        autoLabel = new Label( "toggle-auto-label", autoModel );
        autoLabel.setOutputMarkupId( true );
        add( autoLabel );

        add( new AjaxFallbackLink( "toggle-auto-link" ) {
            public void onClick( AjaxRequestTarget target ) {
                final Boolean[] ret = new Boolean[1];
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        cat.setAuto( !cat.isAuto() );
                        ret[0] = cat.isAuto();
                        session.update( cat );
                        return true;
                    }
                } );
                if ( success ) {
                    autoModel.setObject( ret[0] );
                }
                target.addComponent( autoLabel );
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

        if ( category.isRoot() ) {
            add( new Label( "category-title", "Root" ) );
        }
        else {
            add( new LocalizedText( "category-title", category.getNavLocation( getNavMenu() ).getLocalizationKey() ) );
        }

        items = new LinkedList<SimOrderItem>();
        allItems = new LinkedList<SimOrderItem>();

        for ( Simulation simulation : simulations ) {
            items.add( new SimOrderItem( simulation, titleMap.get( simulation ) ) );
        }

        for ( Simulation simulation : allSimulations ) {
            allItems.add( new SimOrderItem( simulation, titleMap.get( simulation ) ) );
        }

        final List<CategoryOrderItem> catItems = new LinkedList<CategoryOrderItem>();

        for ( Category cat : categories ) {
            catItems.add( new CategoryOrderItem( cat, StringUtils.getDefaultStringDirect( getHibernateSession(), cat.getNavLocation( getNavMenu() ).getLocalizationKey() ) ) );
        }

        add( new JustOrderList<CategoryOrderItem>( "categories", getPageContext(), catItems ) {
            public boolean onSwap( final CategoryOrderItem a, final CategoryOrderItem b, final int aIndex, final int bIndex ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, categoryId );
                        if ( ( (Category) cat.getSubcategories().get( aIndex ) ).getId() != a.getId() ) {
                            logger.warn( "invalid match for changing order" );
                            return false;
                        }
                        if ( ( (Category) cat.getSubcategories().get( bIndex ) ).getId() != b.getId() ) {
                            logger.warn( "invalid match for changing order" );
                            return false;
                        }
                        Collections.swap( cat.getSubcategories(), aIndex, bIndex );
                        session.update( cat );
                        return true;
                    }
                } );
                if ( success ) {
                    CategoryChangeHandler.notifyChildrenReordered( category );
                    Collections.swap( categories, aIndex, bIndex );
                }
                logger.debug( "swap succeeded: " + success );
                return success;
            }

            public Component getHeaderComponent( String id ) {
                return new Label( id, "Categories" );
            }
        } );

        add( new OrderList<SimOrderItem>( "simulations", getPageContext(), items, allItems ) {
            public boolean onItemAdd( final SimOrderItem item ) {
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
                        CategoryChangeHandler.notifySimulationChange( cat, sim );
                        return true;
                    }
                } );
                logger.info( "add: #" + item.getId() + " success: " + success );
                return success;
            }

            public boolean onItemRemove( final SimOrderItem item, int index ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        Simulation sim = (Simulation) session.load( Simulation.class, item.getId() );
                        cat.removeSimulation( sim );
                        session.update( cat );
                        session.update( sim );
                        CategoryChangeHandler.notifySimulationChange( cat, sim );
                        return true;
                    }
                } );
                logger.info( "remove: #" + item.getId() + " at " + index + " success: " + success );
                return success;
            }

            public boolean onItemSwap( SimOrderItem a, SimOrderItem b, final int aIndex, final int bIndex ) {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        Collections.swap( cat.getSimulations(), aIndex, bIndex );
                        session.update( cat );
                        CategoryChangeHandler.notifySimulationChange( cat, (Simulation) cat.getSimulations().get( aIndex ) );
                        CategoryChangeHandler.notifySimulationChange( cat, (Simulation) cat.getSimulations().get( bIndex ) );
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

        add( new Form( "remove-form" ) {
            @Override
            protected void onSubmit() {
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Category cat = (Category) session.load( Category.class, category.getId() );
                        cat.getParent().getSubcategories().remove( cat );
                        session.update( cat.getParent() );
                        session.delete( cat );
                        return true;
                    }
                } );

                if ( success ) {
                    CategoryChangeHandler.notifyRemoved( category );
                    setResponsePage( AdminCategoriesPage.class );
                }
            }
        } );
    }

}
