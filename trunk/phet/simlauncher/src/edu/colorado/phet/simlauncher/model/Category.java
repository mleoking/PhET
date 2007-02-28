/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model;

import java.util.List;

/**
 * Category
 *
 * @author Ron LeMaster
 * @version $Revision$
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
