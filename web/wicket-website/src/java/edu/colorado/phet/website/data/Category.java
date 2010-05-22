package edu.colorado.phet.website.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.data.util.IntId;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.util.HibernateUtils;

/**
 * A simulation category. 'All Sims' and 'Translated Sims' are not stored in these categories.
 * <p/>
 * An undisplayed 'root' category is the parent of all others, and is important for its childrens' ordering.
 */
public class Category implements Serializable, IntId {
    private int id;

    /**
     * Used for the nav location key AND URL
     */
    private String name;

    /**
     * Whether the category should include all simulations from children (true), or just simulations assigned to the
     * exact category (false). It is helpful for base categories to be set to auto, so if a sim is added to
     * 'Light &amp; Radiation', it would be auto-added to 'Physics';
     */
    private boolean auto;

    /**
     * Whether this is the root category. Consider it immutable! BAD THINGS will happen if this goes haywire, and checks
     * require there is only 1 root category.
     */
    private boolean root;

    /**
     * Ordered list of subcategories
     */
    private List subcategories = new LinkedList();

    /**
     * Ordered list of simulations
     */
    private List simulations = new LinkedList();

    /**
     * Parent category (root category will have this be null)
     */
    private Category parent;

    private static final Logger logger = Logger.getLogger( Category.class.getName() );

    public Category() {
    }

    public Category( Category parent, String name ) {
        this.parent = parent;
        this.name = name;
        if ( parent == null ) {
            auto = true;
            root = true;
        }
        else if ( parent.isRoot() ) {
            auto = true;
            root = false;
        }
        else {
            auto = false;
            root = false;
        }

        if ( !root ) {
            parent.subcategories.add( this );
        }
    }

    public static String getDefaultCategoryKey() {
        return "new";
    }

    public void addSimulation( Simulation simulation ) {
        simulation.getCategories().add( this );
        getSimulations().add( simulation );
    }

    public void removeSimulation( Simulation simulation ) {
        simulation.getCategories().remove( this );
        getSimulations().remove( simulation );
    }

    /**
     * NOTE: must be in session transaction!
     *
     * @param session
     * @param categoriesString
     * @return
     */
    public static Category getCategoryFromPath( Session session, String categoriesString ) {
        Category category;
        logger.debug( "categoriesString = " + categoriesString );
        String[] categories = categoriesString.split( "/" );
        int categoryIndex = categories.length - 1;
        if ( categories[categoryIndex].equals( "" ) ) {
            categoryIndex--;
        }
        String categoryName = categories[categoryIndex];
        category = HibernateUtils.getCategoryByName( session, categoryName );
        if ( category == null ) {
            logger.warn( "WARNING: attempt to access category " + categoriesString + " resulted in failure" );
            return category;
        }

        logger.debug( "category path: " + category.getCategoryPath() );

        if ( !category.getCategoryPath().equals( categoriesString ) ) {
            throw new RuntimeException( "category path doesn't match category strings: " + category.getCategoryPath() + " != " + categoriesString );
        }
        return category;
    }

    public static Category getRootCategory( Session session ) {
        // NOTE: must be in session transaction
        return (Category) session.createQuery( "select c from Category as c where c.root = true" ).uniqueResult();
    }

    public NavLocation getNavLocation( NavMenu menu ) {
        return menu.getLocationByKey( getName() );
    }

    /**
     * Note: parent should be loaded
     *
     * @return
     */
    public String getCategoryPath() {
        if ( isRoot() ) {
            return "";
        }
        else if ( getParent().isRoot() ) {
            return getName();
        }
        else {
            return getParent().getCategoryPath() + "/" + getName();
        }
    }

    public int getDepth() {
        if ( isRoot() ) {
            return 0;
        }
        else {
            return getParent().getDepth() + 1;
        }
    }

    public String getBaseName() {
        if ( isRoot() ) {
            return null;
        }
        if ( getParent().isRoot() ) {
            logger.warn( "My parent is root, I am " + getName() );
            return getName();
        }
        else {
            return getParent().getBaseName();
        }
    }

    @Override
    public String toString() {
        return "Cat: " + getName();
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto( boolean auto ) {
        this.auto = auto;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot( boolean root ) {
        this.root = root;
    }

    public List getSubcategories() {
        return subcategories;
    }

    public void setSubcategories( List subcategories ) {
        this.subcategories = subcategories;
    }

    public List getSimulations() {
        return simulations;
    }

    public void setSimulations( List simulations ) {
        this.simulations = simulations;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent( Category parent ) {
        this.parent = parent;
    }
}
