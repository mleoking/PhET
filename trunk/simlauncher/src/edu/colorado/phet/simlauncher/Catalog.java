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
 * Catalog
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Catalog {

    public List getSimulations() {        
        return new SimulationFactory().getSimulations( "simulations.xml",
                                                       Configuration.instance().getLocalRoot() );
    }

}
