// Copyright 2002-2011, University of Colorado

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
import java.util.ArrayList;

import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.view.AmplitudeSlider;
import edu.colorado.phet.fourier.view.discrete.DiscreteAmplitudesView;


/**
 * GameAmplitudesView is the "Amplitudes" view in the Game module.
 * It is extends the view in the Discrete module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameAmplitudesView extends DiscreteAmplitudesView {
    
    private FourierSeries _randomFourierSeries;
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param fourierSeries
     */
    public GameAmplitudesView( Component component, FourierSeries fourierSeries, FourierSeries randomFourierSeries ) {
        super( component, fourierSeries );
        
        _randomFourierSeries = randomFourierSeries;
        _randomFourierSeries.addObserver( this );
    }
    
    public void cleanup() {
        _randomFourierSeries.removeObserver( this );
        _randomFourierSeries = null;
    }
    
    /**
     * Sets the visibility of one of the amplitude sliders.
     * 
     * @param order
     * @param visible
     */
    public void setSliderVisible( int order, boolean visible ) {
        if ( order >= getFourierSeries().getNumberOfHarmonics() ) {
            throw new IllegalArgumentException( "order is out of range: " + order );
        }
        AmplitudeSlider slider = (AmplitudeSlider) getSliders().get( order );
        slider.setVisible( visible );
    }
    
    /**
     * Sets the visibility of all of the amplitude sliders.
     * 
     * @param visible
     */
    public void setSlidersVisible( boolean visible ) {
        ArrayList sliders = getSliders();
        int numberOfHarmonics = getFourierSeries().getNumberOfHarmonics();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            AmplitudeSlider slider = (AmplitudeSlider) sliders.get( i );
            slider.setVisible( visible );
        }
    }
}
