// Copyright 2002-2011, University of Colorado

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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel3;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.control.GameControlPanel;
import edu.colorado.phet.fourier.enums.GameLevel;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.help.HelpBubble;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.persistence.FourierConfig;
import edu.colorado.phet.fourier.view.MinimizedView;
import edu.colorado.phet.fourier.view.game.GameAmplitudesView;
import edu.colorado.phet.fourier.view.game.GameHarmonicsView;
import edu.colorado.phet.fourier.view.game.GameManager;
import edu.colorado.phet.fourier.view.game.GameSumView;


/**
 * GameModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameModule extends FourierAbstractModule implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;

    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = FourierConstants.MAX_HARMONICS;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _userFourierSeries;
    private FourierSeries _randomFourierSeries;
    private GameAmplitudesView _amplitudesView;
    private GameHarmonicsView _harmonicsView;
    private MinimizedView _harmonicsMinimizedView;
    private GameSumView _sumView;
    private MinimizedView _sumMinimizedView;
    private GameControlPanel _controlPanel;
    private Dimension _canvasSize;
    private GameManager _gameManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public GameModule() {

        super( FourierResources.getString( "GameModule.title" ) );

        getModulePanel().addComponentListener( new ModuleVisibleListener() );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );

        // The user's Fourier Series
        _userFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );
        _userFourierSeries.setPreset( Preset.CUSTOM );
        _userFourierSeries.setWaveType( WaveType.SINES );
        for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
            _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
        }

        // The randomly generated Fourier Series
        _randomFourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        // Use ApparatusPanel 3 to improve support for low resolution screens.  The size was sampled at runtime by using ApparatusPanel2 with TransformManager.DEBUG_OUTPUT_ENABLED=true on large screen size, see #2860
        ApparatusPanel2 apparatusPanel = new ApparatusPanel3( getClock(), 710, 630 );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.addChangeListener( this );
        _canvasSize = apparatusPanel.getSize();

        // Amplitudes view
        _amplitudesView = new GameAmplitudesView( apparatusPanel, _userFourierSeries, _randomFourierSeries );
        _amplitudesView.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesView, AMPLITUDES_LAYER );

        // Harmonics view
        _harmonicsView = new GameHarmonicsView( apparatusPanel, _userFourierSeries );
        apparatusPanel.addGraphic( _harmonicsView, HARMONICS_LAYER );

        // Harmonics view (minimized)
        _harmonicsMinimizedView = new MinimizedView( apparatusPanel, FourierResources.getString( "GameHarmonicsView.title" ) );
        apparatusPanel.addGraphic( _harmonicsMinimizedView, HARMONICS_CLOSED_LAYER );

        // Sum view
        _sumView = new GameSumView( apparatusPanel, _userFourierSeries, _randomFourierSeries );
        apparatusPanel.addGraphic( _sumView, SUM_LAYER );

        // Sum view (minimized)
        _sumMinimizedView = new MinimizedView( apparatusPanel, FourierResources.getString( "GameSumView.title" ) );
        apparatusPanel.addGraphic( _sumMinimizedView, SUM_CLOSED_LAYER );

        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        getClockControlPanel().setEnabled( false );

        _gameManager = new GameManager( _userFourierSeries, _randomFourierSeries, _amplitudesView );
        apparatusPanel.addMouseListener( _gameManager );

        // Control Panel
        _controlPanel = new GameControlPanel( this, _gameManager );
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
        HelpBubble slidersToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "GameModule.help.sliders" ) );
        slidersToolHelp.pointAt( new Point( 252, 117 ), HelpBubble.TOP_CENTER, 30 );
        addHelpItem( slidersToolHelp );

        HelpBubble textfieldsToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "GameModule.help.textfields" ) );
        textfieldsToolHelp.pointAt( new Point( 205, 44 ), HelpBubble.TOP_CENTER, 15 );
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

    //----------------------------------------------------------------------------
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

        _harmonicsMinimizedView.setVisible( !_harmonicsView.isVisible() );
        _sumMinimizedView.setVisible( !_sumView.isVisible() );

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
    // ComponentAdapter
    //----------------------------------------------------------------------------

    private static class ModuleVisibleListener extends ComponentAdapter {
        boolean _instructionsHaveBeenDisplayed = false;
        public void componentShown( ComponentEvent event ) {
            if ( ! _instructionsHaveBeenDisplayed  ) {
                _instructionsHaveBeenDisplayed = true;
                String message = FourierResources.getString( "GameInstructionsDialog.message" );
                String title = FourierResources.getString( "GameInstructionsDialog.title" );
                PhetOptionPane.showMessageDialog( PhetApplication.getInstance().getPhetFrame(), message, title, JOptionPane.PLAIN_MESSAGE );
            }
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

    //----------------------------------------------------------------------------
    // Save & Load configurations
    //----------------------------------------------------------------------------

    /**
     * Saves the module's configuration.
     *
     * @return
     */
    public FourierConfig.GameConfig save() {
        
        FourierConfig.GameConfig config = new FourierConfig.GameConfig();

        // Save control panel config
        config.setGameLevelName( _controlPanel.getGameLevel().getName() );
        config.setPresetName( _controlPanel.getPreset().getName() );

        // Save view config
        config.setHarmonicsViewMaximized( _harmonicsView.isVisible() );
        config.setSumViewMaximized( _sumView.isVisible() );
        
        return config;
    }

    /**
     * Loads the module's configuration.
     *
     * @param config
     */
    public void load( FourierConfig.GameConfig config ) {

        // Load control panel config
        _controlPanel.setGameLevel( GameLevel.getByName( config.getGameLevelName() ) );
        _controlPanel.setPreset( Preset.getByName( config.getPresetName() ) );

        // Load view config
        _harmonicsView.setVisible( config.isHarmonicsViewMaximized() );
        _sumView.setVisible( config.isSumViewMaximized() );
        layoutViews();
    }
}
