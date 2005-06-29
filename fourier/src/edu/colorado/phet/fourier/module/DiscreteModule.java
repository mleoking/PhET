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
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.Vector2D;
import edu.colorado.phet.fourier.view.*;


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
    private static final double TOOLS_LAYER = 4;

    // Locations
    private static final Point AMPLITUDES_LOCATION = new Point( 60, 125 );
    private static final Point HARMONICS_LOCATION = new Point( 60, 325 );
    private static final Point SUM_LOCATION = new Point( 60, 525 );
    private static final Point WAVELENGTH_TOOL_LOCATION = new Point( 615, 245 );
    private static final Point PERIOD_TOOL_LOCATION = WAVELENGTH_TOOL_LOCATION;
    private static final Point PERIOD_DISPLAY_LOCATION = new Point( 690, 360 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 280, 60 );
    
    // Colors
    private static final Color APPARATUS_PANEL_BACKGROUND = new Color( 215, 215, 215 );;
    private static final Color WIGGLE_ME_COLOR = Color.RED;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = 7;
  
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private AmplitudesGraph _amplitudesGraph;
    private HarmonicsGraph _harmonicsGraph;
    private SumGraph _sumGraph;
    private WavelengthTool _wavelengthTool;
    private PeriodTool _periodTool;
    private PeriodDisplay _periodDisplay;
    private DiscreteControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public DiscreteModule( AbstractClock clock ) {
        
        super( SimStrings.get( "DiscreteModule.title" ), clock );

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
        apparatusPanel.setBackground( APPARATUS_PANEL_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        // Amplitudes view
        _amplitudesGraph = new AmplitudesGraph( apparatusPanel, _fourierSeries );
        _amplitudesGraph.setLocation( AMPLITUDES_LOCATION );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        // Components view
        _harmonicsGraph = new HarmonicsGraph( apparatusPanel, _fourierSeries );
        _harmonicsGraph.setLocation( HARMONICS_LOCATION );
        apparatusPanel.addGraphic( _harmonicsGraph, COMPONENTS_LAYER );
        model.addModelElement( _harmonicsGraph );//XXX not really part of the model, just need clock
        
        // Sum view
        _sumGraph = new SumGraph( apparatusPanel, _fourierSeries );
        _sumGraph.setLocation( SUM_LOCATION );
        apparatusPanel.addGraphic( _sumGraph, SUM_LAYER );
        model.addModelElement( _sumGraph );//XXX not really part of the model, just need clock
        
        // Wavelength Tool
        _wavelengthTool = new WavelengthTool( apparatusPanel, _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _wavelengthTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _wavelengthTool );
        
        // Period Tool
        _periodTool = new PeriodTool( apparatusPanel, _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodTool );
        
        // Period Display
        _periodDisplay = new PeriodDisplay( apparatusPanel, _fourierSeries.getHarmonic(0) );
        apparatusPanel.addGraphic( _periodDisplay, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodDisplay );
        model.addModelElement( _periodDisplay );//XXX not really part of the model, just need clock
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new DiscreteControlPanel( this, 
                _fourierSeries, _harmonicsGraph, _sumGraph, 
                _wavelengthTool, _periodTool, _periodDisplay );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );
        
        // Link horizontal zoom controls
        _harmonicsGraph.getHorizontalZoomControl().addZoomListener( _sumGraph );
        _sumGraph.getHorizontalZoomControl().addZoomListener( _harmonicsGraph );
        
        // Harmonic hightlighting
        _amplitudesGraph.addHarmonicFocusListener( _harmonicsGraph );
        _wavelengthTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodDisplay.addHarmonicFocusListener( _harmonicsGraph );
        
        // Slider movement by the user
        _amplitudesGraph.addChangeListener( _controlPanel );
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        addHelpItem( new HelpItem( apparatusPanel, "<Help goes here>", 200, 150 ) );//XXX to test help
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _fourierSeries );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
        wiggleMe.setEnabled( false ); //XXX remove this line to enable the wiggle me
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
        _harmonicsGraph.reset();
        _sumGraph.reset();
        
        _wavelengthTool.setVisible( false );
        _wavelengthTool.setLocation( WAVELENGTH_TOOL_LOCATION );
        
        _periodTool.setVisible( false );
        _periodTool.setLocation( PERIOD_TOOL_LOCATION );
        
        _periodDisplay.setVisible( false );
        _periodDisplay.setLocation( PERIOD_DISPLAY_LOCATION );
        
        _controlPanel.reset();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic {

        private FourierSeries _fourierSeriesModel;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param fourierSeriesModel
         */
        public ThisWiggleMeGraphic( final Component component, BaseModel model, FourierSeries fourierSeriesModel ) {
            super( component, model );

            _fourierSeriesModel = fourierSeriesModel;
            
            setText( SimStrings.get( "DiscreteModule.wiggleMe" ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.MIDDLE_LEFT, new Vector2D( -40, 30 ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.TOP_LEFT, new Vector2D( -40, -30 ), WIGGLE_ME_COLOR );
            setRange( 20, 10 );
            setEnabled( true );
            
            // Disable the wiggle me when the mouse is pressed.
            component.addMouseListener( new MouseInputAdapter() { 
                public void mousePressed( MouseEvent event ) {
                    // Disable
                    setEnabled( false );
                    // Unwire
                    component.removeMouseListener( this );
                }
           } );
        }
    }
}
