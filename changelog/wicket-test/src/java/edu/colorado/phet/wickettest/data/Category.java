package edu.colorado.phet.wickettest.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;

import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.menu.NavMenu;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class Category implements Serializable {
    private int id;
    private String name;
    private boolean auto;
    private boolean root;
    private List subcategories = new LinkedList();
    private List simulations = new LinkedList();
    private Category parent;

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

    /**
     * NOTE: must be in session transaction!
     *
     * @param session
     * @param categoriesString
     * @return
     */
    public static Category getCategoryFromPath( Session session, String categoriesString ) {
        Category category;
        System.out.println( "categoriesString = " + categoriesString );
        String[] categories = categoriesString.split( "/" );
        int categoryIndex = categories.length - 1;
        if ( categories[categoryIndex].equals( "" ) ) {
            categoryIndex--;
        }
        String categoryName = categories[categoryIndex];
        category = HibernateUtils.getCategoryByName( session, categoryName );
        if ( category == null ) {
            throw new RuntimeException( "Couldn't find category" );
        }

        System.out.println( "category path: " + category.getCategoryPath() );

        if ( !category.getCategoryPath().equals( categoriesString ) ) {
            throw new RuntimeException( "category path doesn't match category strings: " + category.getCategoryPath() + " != " + categoriesString );
        }
        return category;
    }

    public NavLocation getNavLocation( NavMenu menu ) {
        return menu.getLocationByKey( getName() );
    }

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

    public String getBaseName() {
        if ( isRoot() ) {
            return null;
        }
        if ( getParent().isRoot() ) {
            System.out.println( "My parent is root, I am " + getName() );
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
