/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.model.Model;

/**
 * OTModelViewManager is an extension of ModelViewManager that
 * contains node factories specific to this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OTModelViewManager extends ModelViewManager {

    public OTModelViewManager( Model model ) {
        super( model );
        
        //XXX addNodeFactory
    }
}
