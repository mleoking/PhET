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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.FourierControlPanel;
import edu.colorado.phet.fourier.control.GameControlPanel;
import edu.colorado.phet.fourier.help.FourierHelpItem;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.view.MinimizedView;
import edu.colorado.phet.fourier.view.discrete.DiscreteSumView;
import edu.colorado.phet.fourier.view.game.GameAmplitudesView;
import edu.colorado.phet.fourier.view.game.GameHarmonicsView;
import edu.colorado.phet.fourier.view.game.GameSumView;


/**
 * GameModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameModule extends FourierModule implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;

    // Locations
    private static final Point SOME_LOCATION = new Point( 400, 300 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = FourierConfig.MAX_HARMONICS;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private GameAmplitudesView _amplitudesView;
    private GameHarmonicsView _harmonicsView;
    private MinimizedView _harmonicsMinimizedView;
    private GameSumView _sumView;
    private MinimizedView _sumMinimizedView;
    private FourierControlPanel _controlPanel;
    private Dimension _canvasSize;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public GameModule( AbstractClock clock ) {
        
        super( SimStrings.get( "GameModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Fourier Series
        _fourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setPreset( FourierConstants.PRESET_CUSTOM );
        _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _fourierSeries.getHarmonic( i ).setAmplitude( 0 );
        }
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.addChangeListener( this );
        _canvasSize = apparatusPanel.getSize();
        
        // Amplitudes view
        _amplitudesView = new GameAmplitudesView( apparatusPanel, _fourierSeries );
        _amplitudesView.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesView, AMPLITUDES_LAYER );
        
        // Harmonics view
        _harmonicsView = new GameHarmonicsView( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _harmonicsView, HARMONICS_LAYER );
        
        // Harmonics view (minimized)
        _harmonicsMinimizedView = new MinimizedView( apparatusPanel, SimStrings.get( "GameHarmonicsView.title" ) );
        apparatusPanel.addGraphic( _harmonicsMinimizedView, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumView = new GameSumView( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _sumView, SUM_LAYER );
        
        // Sum view (minimized)
        _sumMinimizedView = new MinimizedView( apparatusPanel, SimStrings.get( "GameSumView.title" ) );
        apparatusPanel.addGraphic( _sumMinimizedView, SUM_CLOSED_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        setClockControlsEnabled( false );
        
        // Control Panel
        _controlPanel = new GameControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Harmonic hightlighting
        _amplitudesView.addHarmonicFocusListener( _harmonicsView );
        
        // Minimize/maximize buttons on views
        {
            // Harmonics minimize
            _harmonicsView.getMinimizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsView.setVisible( false );
                    _harmonicsMinimizedView.setVisible( true );
                    layoutViews();
                }
             } );
            
            // Harmonics maximize
            _harmonicsMinimizedView.getMaximizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsView.setVisible( true );
                    _harmonicsMinimizedView.setVisible( false );
                    setWaitCursorEnabled( true );
                    layoutViews();
                    setWaitCursorEnabled( false );
                }
             } );
            
            // Sum minimize
            _sumView.getMinimizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumView.setVisible( false );
                    _sumMinimizedView.setVisible( true );
                    layoutViews();
                }
             } );
            
            // Sum maximize
            _sumMinimizedView.getMaximizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumView.setVisible( true );
                    _sumMinimizedView.setVisible( false );
                    setWaitCursorEnabled( true );
                    layoutViews();
                    setWaitCursorEnabled( false );
                }
             } );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Help Items
        FourierHelpItem slidersToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.sliders" ) );
        slidersToolHelp.pointAt( new Point( 252, 117 ), FourierHelpItem.UP, 30 );
        addHelpItem( slidersToolHelp );
        
        FourierHelpItem textfieldsToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.textfields" ) );
        textfieldsToolHelp.pointAt( new Point( 205, 44 ), FourierHelpItem.UP, 15 );
        addHelpItem( textfieldsToolHelp );
   
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        reset();
    }
    
    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        _harmonicsView.setVisible( true );
        _harmonicsMinimizedView.setVisible( !_harmonicsView.isVisible() );
        _sumView.setVisible( true );
        _sumMinimizedView.setVisible( !_sumView.isVisible() );
        layoutViews();
        
        _controlPanel.reset();
    }
    
//  ----------------------------------------------------------------------------
    // EventHandling
    //----------------------------------------------------------------------------
    
    /*
     * Resizes and repositions the views based on which ones are visible.
     * 
     * @param event
     */
    private void layoutViews() {

        int canvasHeight = _canvasSize.height;
        int availableHeight = canvasHeight - _amplitudesView.getHeight();
        
        if ( _harmonicsView.isVisible() && _sumView.isVisible() ) {
            // Both maximized
            _harmonicsView.setHeight( availableHeight/2 );
            _harmonicsView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumView.setHeight( availableHeight/2 );
            _sumView.setLocation( _amplitudesView.getX(), _harmonicsView.getY() + _harmonicsView.getHeight() );
        }
        else if ( _harmonicsView.isVisible() ) {
            // Harmonics maximized
            _harmonicsView.setHeight( availableHeight - _sumMinimizedView.getHeight() );
            _harmonicsView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumMinimizedView.setLocation( _amplitudesView.getX(), _harmonicsView.getY() + _harmonicsView.getHeight() );
        }
        else if ( _sumView.isVisible() ) {
            // Sum maximized
            _harmonicsMinimizedView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumView.setHeight( availableHeight - _harmonicsMinimizedView.getHeight() );
            _sumView.setLocation( _amplitudesView.getX(), _harmonicsMinimizedView.getY() + _harmonicsMinimizedView.getHeight() );
        }
        else {
            // Both minimized
            _harmonicsMinimizedView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumMinimizedView.setLocation( _amplitudesView.getX(), _harmonicsMinimizedView.getY() + _harmonicsMinimizedView.getHeight() );
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Redoes the layout whenever the apparatus panel's canvas size changes.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _canvasSize.setSize( event.getCanvasSize() );
        layoutViews();
    }

}
