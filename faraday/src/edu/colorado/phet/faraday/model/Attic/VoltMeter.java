/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import edu.colorado.phet.common.util.SimpleObserver;


/**
 * VoltMeter
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeter extends AbstractResistor implements SimpleObserver {

    private Current _currentModel;
    
    public VoltMeter( Current currentModel, double ohms ) {
        super( ohms );
        _currentModel = currentModel;
        _currentModel.addObserver( this );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _currentModel.removeObserver( this );
        _currentModel = null;
    }

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        // TODO Auto-generated method stub
        
    }
}
