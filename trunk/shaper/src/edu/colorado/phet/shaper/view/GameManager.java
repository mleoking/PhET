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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.model.FourierSeries;
import edu.colorado.phet.shaper.module.ShaperModule;


/**
 * GameManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameManager extends MouseInputAdapter implements SimpleObserver {

    private ShaperModule _module;
    private FourierSeries _userFourierSeries;
    private FourierSeries _outputFourierSeries;
    private MoleculeAnimation _animation;
    private boolean _isAdjusting;
    
    public GameManager( ShaperModule module, 
            FourierSeries userFourierSeries, FourierSeries outputFourierSeries,
            MoleculeAnimation animation  ) {
        
        _module = module;
        _userFourierSeries = userFourierSeries;
        _outputFourierSeries = outputFourierSeries;
        _animation = animation;
        
        _userFourierSeries.addObserver( this );
        _outputFourierSeries.addObserver( this );
        
        _isAdjusting = false;
        
        update();
    }
    
    public void gameOver() {
        
        _isAdjusting = true;

        // Tell the user they won.
        JFrame frame = PhetApplication.instance().getPhetFrame();
        String title = SimStrings.get( "WinDialog.title" );
        String message = SimStrings.get( "WinDialog.message" );
        JOptionPane.showMessageDialog( frame, message, title, JOptionPane.INFORMATION_MESSAGE );

        // Start a new "game".
        _module.newGame();
        
        _isAdjusting = false;
    }
    
    public void update() {

        if ( !_isAdjusting && !_animation.isExploding() ) {

            /*
             * Compare the Fourier series.
             * 
             * The calculation for "closeness" is:
             * 
             *     closeness = 1 - ( Math.sqrt( (U1-D1)^2 + (U2-D2)^2 + ...) / Math.sqrt( D1^2 + D2^2 + ... ) )
             * 
             * where:
             *     Un is the user's amplitude for component n
             *     Dn is the desired amplitude for component n
             */
            double numerator = 0;
            double denominator = 0;
            int numberOfHarmonics = _userFourierSeries.getNumberOfHarmonics();
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                double userAmplitude = _userFourierSeries.getHarmonic( i ).getAmplitude();
                double outputAmplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
                numerator += Math.pow( Math.abs( userAmplitude - outputAmplitude ), 2 );
                denominator += Math.pow( outputAmplitude, 2 );
            }
            double closeness = 1.0 - ( Math.sqrt( numerator ) / Math.sqrt( denominator ) );
            if ( closeness < 0 ) {
                closeness = 0;
            }

            // Update the animation
            _animation.setCloseness( closeness );

            /*
             * WORKAROUND: If we have a match, make sure that all 
             * other views are updated before the molecule animation
             * happens and the "you've won" dialog is shown.
             */
            if ( closeness >= ShaperConstants.CLOSENESS_MATCH ) {
                _userFourierSeries.removeObserver( this );
                _userFourierSeries.notifyObservers();
                _userFourierSeries.addObserver( this );
            }
        }
    }
}
