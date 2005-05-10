/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;
import java.awt.Point;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.HarmonicSeriesPanel;
import edu.colorado.phet.fourier.model.HarmonicSeries;
import edu.colorado.phet.fourier.view.AmplitudesGraphic;
import edu.colorado.phet.fourier.view.ComponentsGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


/**
 * DiscreteModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteModule extends FourierModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double COMPONENTS_LAYER = 2;
    private static final double SUM_LAYER = 3;

    // Locations
    private static final Point AMPLITUDES_LOCATION = new Point( 50, 125 );
    private static final Point COMPONENTS_LOCATION = new Point( 50, 325 );
    private static final Point SUM_LOCATION = new Point( 50, 525 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public DiscreteModule( AbstractClock clock ) {
        
        super( SimStrings.get( "DiscreteModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Harmonic series
        HarmonicSeries harmonicSeriesModel = new HarmonicSeries();
        harmonicSeriesModel.setFundamentalFrequency( 440 );
        harmonicSeriesModel.setNumberOfHarmonics( 5 );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Amplitudes view
        AmplitudesGraphic amplitudesGraphic = new AmplitudesGraphic( apparatusPanel, harmonicSeriesModel );
        amplitudesGraphic.setLocation( AMPLITUDES_LOCATION );
        apparatusPanel.addGraphic( amplitudesGraphic, AMPLITUDES_LAYER );
        
        // Components view
        ComponentsGraphic componentsGraphic = new ComponentsGraphic( apparatusPanel, harmonicSeriesModel );
        componentsGraphic.setLocation( COMPONENTS_LOCATION );
        apparatusPanel.addGraphic( componentsGraphic, COMPONENTS_LAYER );
        
        // Sum view
        SumGraphic sumGraphic = new SumGraphic( apparatusPanel, harmonicSeriesModel );
        sumGraphic.setLocation( SUM_LOCATION );
        apparatusPanel.addGraphic( sumGraphic, SUM_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            
            HarmonicSeriesPanel harmonicSeriesPanel = new HarmonicSeriesPanel( harmonicSeriesModel );
            controlPanel.addFullWidth( harmonicSeriesPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
}
