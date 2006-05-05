/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import java.util.List;

/**
 * Category
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Category {
    private static List instances = new SimulationFactory().getCategories( "simulations.xml");

    public List getInstances() {
        return instances;
    }


    private String name;

    public Category( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
