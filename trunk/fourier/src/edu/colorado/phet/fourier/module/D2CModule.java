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
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.view.D2CAmplitudesGraph;


/**
 * D2CModule is the "Discrete to Continuous" (D2C) module.
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
    
    // Gaussian wave packet
    private static final double WAVE_PACKET_SPACING = 2 * Math.PI;
    private static final double WAVE_PACKET_WIDTH = 6 * Math.PI;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
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
        
        // Gaussian wave packet
        _wavePacket = new GaussianWavePacket( WAVE_PACKET_SPACING, WAVE_PACKET_WIDTH );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );

        // Amplitudes view
        _amplitudesGraph = new D2CAmplitudesGraph( apparatusPanel, _wavePacket );
        _amplitudesGraph.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new D2CControlPanel( this, _wavePacket, _amplitudesGraph );
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
        
        _wavePacket.setSpacing( WAVE_PACKET_SPACING );
        _wavePacket.setWidth( WAVE_PACKET_WIDTH );
        
        _amplitudesGraph.reset();
        
        _controlPanel.reset();
    }
}
