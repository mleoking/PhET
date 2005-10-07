/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.game;

import java.awt.Component;

import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.view.discrete.DiscreteAmplitudesView;


/**
 * GameAmplitudesView
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameAmplitudesView extends DiscreteAmplitudesView {

    /**
     * Sole constructor.
     * 
     * @param component
     * @param fourierSeries
     */
    public GameAmplitudesView( Component component, FourierSeries fourierSeries ) {
        super( component, fourierSeries );
    }
}
