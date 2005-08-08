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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.D2CControlPanel;
import edu.colorado.phet.fourier.help.FourierHelpItem;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.view.AmplitudesGraph;
import edu.colorado.phet.fourier.view.D2CAmplitudesGraph;


/**
 * D2CModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CModule extends FourierModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;
    private static final double TOOLS_LAYER = 6;;

    // Locations
    private static final Point SOME_LOCATION = new Point( 400, 300 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = 7;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private D2CAmplitudesGraph _amplitudesGraph;
    private D2CControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public D2CModule( AbstractClock clock ) {
        
        super( SimStrings.get( "D2CModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Fourier Series
        _fourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );

        // Amplitudes view
        _amplitudesGraph = new D2CAmplitudesGraph( apparatusPanel, _fourierSeries );
        _amplitudesGraph.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new D2CControlPanel( this, _amplitudesGraph );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );

        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Help Items
        FourierHelpItem someHelp = new FourierHelpItem( apparatusPanel, "Hang on, help is coming soon" );
        someHelp.setLocation( 200, 200 );
        addHelpItem( someHelp );
    }
    
    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        _fourierSeries.setNumberOfHarmonics( NUMBER_OF_HARMONICS );
        _fourierSeries.setFundamentalFrequency( FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setPreset( FourierConstants.PRESET_SINE_COSINE );
        _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        
        _amplitudesGraph.reset();
        
        _controlPanel.reset();
    }
}
