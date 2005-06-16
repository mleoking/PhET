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
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.util.Vector2D;
import edu.colorado.phet.fourier.view.*;
import edu.colorado.phet.fourier.view.AmplitudesGraph;
import edu.colorado.phet.fourier.view.HarmonicsGraph;
import edu.colorado.phet.fourier.view.SubscriptedSymbol;
import edu.colorado.phet.fourier.view.SumGraph;


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
    private static final Point WAVELENGTH_TOOL_LOCATION = new Point( 350, 250 );
    private static final Point PERIOD_TOOL_LOCATION = new Point( 350, 280 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 260, 55 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
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
    private WaveMeasurementTool _wavelengthTool, _periodTool;
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
        _fourierSeries = new FourierSeries();
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        
        // Amplitudes view
        _amplitudesGraph = new AmplitudesGraph( apparatusPanel, _fourierSeries );
        _amplitudesGraph.setLocation( AMPLITUDES_LOCATION );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        // Components view
        _harmonicsGraph = new HarmonicsGraph( apparatusPanel, _fourierSeries );
        _harmonicsGraph.setLocation( HARMONICS_LOCATION );
        apparatusPanel.addGraphic( _harmonicsGraph, COMPONENTS_LAYER );
        
        // Sum view
        _sumGraph = new SumGraph( apparatusPanel, _fourierSeries );
        _sumGraph.setLocation( SUM_LOCATION );
        apparatusPanel.addGraphic( _sumGraph, SUM_LAYER );
        
        // Wavelength Tool
        String wavelengthSymbol = SimStrings.get( "symbol.wavelength" );
        _wavelengthTool = new WaveMeasurementTool( apparatusPanel, 
                wavelengthSymbol, _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _wavelengthTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _wavelengthTool );
        
        // Period Tool
        String periodSymbol = SimStrings.get( "symbol.period" );
        _periodTool = new WaveMeasurementTool( apparatusPanel, 
                periodSymbol, _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodTool );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
 
        // Link horizontal zoom controls
        _harmonicsGraph.getHorizontalZoomControl().addZoomListener( _sumGraph );
        _sumGraph.getHorizontalZoomControl().addZoomListener( _harmonicsGraph );
        
        // Harmonic hightlighting
        _amplitudesGraph.addHarmonicFocusListener( _harmonicsGraph );
        _wavelengthTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodTool.addHarmonicFocusListener( _harmonicsGraph );
        
        // Control Panel
        _controlPanel = new DiscreteControlPanel( this, 
                _fourierSeries, _harmonicsGraph, _sumGraph, 
                _wavelengthTool, _periodTool );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        addHelpItem( new HelpItem( apparatusPanel, "<Help goes here>", 200, 150 ) );//XXX
        
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
        
        _fourierSeries.setFundamentalFrequency( FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setNumberOfHarmonics( NUMBER_OF_HARMONICS );
        _fourierSeries.getHarmonic( 0 ).setAmplitude( 1.0 );
        for ( int i = 1; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            ( (Harmonic) _fourierSeries.getHarmonic( i ) ).setAmplitude( 0 );
        }
        
        _amplitudesGraph.reset();
        _harmonicsGraph.reset();
        _sumGraph.reset();
        
        _wavelengthTool.setVisible( false );
        _wavelengthTool.setLocation( WAVELENGTH_TOOL_LOCATION );
        
        _periodTool.setVisible( false );
        _periodTool.setLocation( PERIOD_TOOL_LOCATION );
        
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
