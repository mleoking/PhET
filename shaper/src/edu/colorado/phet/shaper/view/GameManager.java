/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.control.ShaperControls;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * GameManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameManager implements SimpleObserver {

    private FourierSeries _userFourierSeries;
    private FourierSeries _outputFourierSeries;
    private MoleculeAnimation _animation;
    private ShaperControls _controlPanel;
    
    public GameManager( FourierSeries userFourierSeries, FourierSeries outputFourierSeries,
            MoleculeAnimation animation, ShaperControls controlPanel ) {
        
        _userFourierSeries = userFourierSeries;
        _outputFourierSeries = outputFourierSeries;
        _animation = animation;
        _controlPanel = controlPanel;
        
        _userFourierSeries.addObserver( this );
        _outputFourierSeries.addObserver( this );
        
        update();
    }
    
    public void update() {
        
        double closeness = 0;
        
        // Compare the Fourier series
        int numberOfHarmonics = _userFourierSeries.getNumberOfHarmonics();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            double userAmplitude = _userFourierSeries.getHarmonic( i ).getAmplitude();
            double outputAmplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
            if ( outputAmplitude == 0 ) {
                outputAmplitude = 0.000000000001;
            }
            closeness += ( 1.0 - ( Math.abs( ( userAmplitude - outputAmplitude ) / outputAmplitude ) ) );
        }
        closeness /= numberOfHarmonics;
        if ( closeness < 0 ) {
            closeness = 0;
        }
        
        // Update the animation
        _animation.setCloseness( closeness );
        
        // Update the control panel readout
        _controlPanel.setCloseness( closeness );
    }

}
