/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.charts;

import java.awt.Color;
import java.awt.Component;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.LinePlot;
import edu.colorado.phet.fourier.event.HarmonicColorChangeEvent;
import edu.colorado.phet.fourier.event.HarmonicColorChangeListener;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicColors;


/**
 * HarmonicDataSetGraphic is the graphical representation, provided
 * to a Chart, of a HarmonicDataSet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicDataSetGraphic extends LinePlot implements HarmonicColorChangeListener {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param chart
     * @param dataSet
     */
    public HarmonicDataSetGraphic( Component component, Chart chart, HarmonicDataSet dataSet ) {
        super( component, chart, dataSet );
        
        // Interested in changes to harmonic colors.
        HarmonicColors.getInstance().addHarmonicColorChangeListener( this );
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        HarmonicColors.getInstance().removeHarmonicColorChangeListener( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the data set associated with this graphic.
     * 
     * @return HarmonicDataSet
     */
    public HarmonicDataSet getHarmonicDataSet() {
        return (HarmonicDataSet) getDataSet();
    }

    /**
     * Gets the harmonic associated with this graphic's data set.
     * 
     * @return Harmonic
     */
    public Harmonic getHarmonic() {
        return getHarmonicDataSet().getHarmonic();
    }
    
    //----------------------------------------------------------------------------
    // HarmonicColorChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Change the graphic's color when its associated harmonic color changes.
     */
    public void harmonicColorChanged( HarmonicColorChangeEvent e ) {
        if ( getHarmonic().getOrder() == e.getOrder() ) {
            Color harmonicColor = HarmonicColors.getInstance().getColor( getHarmonic() );
            setBorderColor( harmonicColor );
        }
    }
}
