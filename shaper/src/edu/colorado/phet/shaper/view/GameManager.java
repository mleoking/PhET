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

import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.control.ShaperControls;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * GameManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameManager extends MouseInputAdapter implements SimpleObserver {

    private FourierSeries _userFourierSeries;
    private FourierSeries _outputFourierSeries;
    private MoleculeAnimation _animation;
    private ShaperControls _controlPanel;
    private boolean _mouseIsPressed;
    
    public GameManager( FourierSeries userFourierSeries, FourierSeries outputFourierSeries,
            MoleculeAnimation animation, ShaperControls controlPanel ) {
        
        _userFourierSeries = userFourierSeries;
        _outputFourierSeries = outputFourierSeries;
        _animation = animation;
        _controlPanel = controlPanel;
        
        _userFourierSeries.addObserver( this );
        _outputFourierSeries.addObserver( this );
        
        _mouseIsPressed = false;
        
        update();
    }
    
    private boolean _gameOver = false; //XXX
    
    public void update() {
        
        if ( !_gameOver ) {
            
            if ( !_animation.isEnabled() ) {

                _gameOver = true;

                // Tell the user they won.
                JFrame frame = PhetApplication.instance().getPhetFrame();
                String title = SimStrings.get( "WinDialog.title" );
                String message = SimStrings.get( "WinDialog.message" );
                JOptionPane.showMessageDialog( frame, message, title, JOptionPane.INFORMATION_MESSAGE );

                // Start a new "game".
                _controlPanel.newOutputPulse();
                _animation.reset();
                _gameOver = false;
            }
            else if ( !_animation.isExploding() ) {

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

                // Do we have a match?
                if ( closeness >= ShaperConstants.CLOSENESS_MATCH ) {

                    // WORKAROUND: Make sure that all other views are updated.
                    {
                        _userFourierSeries.removeObserver( this );
                        _userFourierSeries.notifyObservers();
                        _userFourierSeries.addObserver( this );
                    }
                }
            }
        }
    }
}
