/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/model/Category.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/07/25 18:00:17 $
 */
package edu.colorado.phet.simlauncher.model;

import java.util.List;

/**
 * Category
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public class Category {
//    private static List instances = new CategoryFactory().getCategories( "simulations.xml");
//
//    public static List getInstances() {
//        return instances;
//    }

    private String name;
    private List simulations;

    public Category( String name, List simulations ) {
        this.name = name;
        this.simulations = simulations;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public List getSimulations() {
        return simulations;
    }
}
