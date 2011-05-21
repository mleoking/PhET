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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel3;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.control.D2CControlPanel;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.help.HelpBubble;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.persistence.FourierConfig;
import edu.colorado.phet.fourier.view.MinimizedView;
import edu.colorado.phet.fourier.view.d2c.D2CAmplitudesView;
import edu.colorado.phet.fourier.view.d2c.D2CHarmonicsView;
import edu.colorado.phet.fourier.view.d2c.D2CSumView;
import edu.colorado.phet.fourier.view.tools.WavePacketPeriodTool;
import edu.colorado.phet.fourier.view.tools.WavePacketSpacingTool;


/**
 * D2CModule is the "Discrete to Continuous" (D2C) module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CModule extends FourierAbstractModule implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;
    private static final double TOOLS_LAYER = 6;
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = new Color( 240, 255, 255 );
    
    // Gaussian wave packet
    private static final double WAVE_PACKET_SPACING = Math.PI;
    private static final double WAVE_PACKET_WIDTH = 6 * Math.PI;
    private static final double WAVE_PACKET_CENTER = 12 * Math.PI;
    private static final double WAVE_PACKET_SIGNIFICANT_WIDTH = 24 * Math.PI;
    
    // Tools
    private static final Point SPACING_TOOL_LOCATION = new Point( 590, 140 );
    private static final Point PERIOD_TOOL_LOCATION = new Point( 440, 580 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesView _amplitudesView;
    private D2CHarmonicsView _harmonicsView;
    private D2CSumView _sumView;
    private MinimizedView _harmonicsMinimizedView;
    private MinimizedView _sumMinimizedView;
    private D2CControlPanel _controlPanel;
    private Dimension _canvasSize;
    private WavePacketSpacingTool _spacingTool;
    private WavePacketPeriodTool _periodTool;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public D2CModule() {
        
        super( FourierResources.getString( "D2CModule.title" ) );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Gaussian wave packet
        _wavePacket = new GaussianWavePacket( 
                WAVE_PACKET_SPACING, WAVE_PACKET_WIDTH, 
                WAVE_PACKET_CENTER, WAVE_PACKET_SIGNIFICANT_WIDTH );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        // Use ApparatusPanel 3 to improve support for low resolution screens.  The size was sampled at runtime by using ApparatusPanel2 with TransformManager.DEBUG_OUTPUT_ENABLED=true on large screen size, see #2860
        ApparatusPanel2 apparatusPanel = new ApparatusPanel3( getClock(), 727, 630 );
        _canvasSize = apparatusPanel.getSize();
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.addChangeListener( this );

        // Amplitudes view
        _amplitudesView = new D2CAmplitudesView( apparatusPanel, _wavePacket );
        _amplitudesView.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesView, AMPLITUDES_LAYER );
        
        // Harmonics view
        _harmonicsView = new D2CHarmonicsView( apparatusPanel, _wavePacket );
        apparatusPanel.addGraphic( _harmonicsView, HARMONICS_LAYER );
        
        // Harmonics view (minimized)
        _harmonicsMinimizedView = new MinimizedView( apparatusPanel, FourierResources.getString( "D2CHarmonicsView.title" ) );
        apparatusPanel.addGraphic( _harmonicsMinimizedView, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumView = new D2CSumView( apparatusPanel, _wavePacket );
        apparatusPanel.addGraphic( _sumView, SUM_LAYER );
        
        // Sum view (minimized)
        _sumMinimizedView = new MinimizedView( apparatusPanel, FourierResources.getString( "D2CSumView.title" ) );
        apparatusPanel.addGraphic( _sumMinimizedView, SUM_CLOSED_LAYER );
        
        // Spacing (k1,w1) measurement tool
        _spacingTool = new WavePacketSpacingTool( apparatusPanel, _wavePacket, _amplitudesView.getChart() );
        _spacingTool.setDragBounds( _amplitudesView.getChart().getBounds() );
        apparatusPanel.addGraphic( _spacingTool, TOOLS_LAYER );
        
        // Drag bounds for the x-space tools
        int x = 0;
        int y = _amplitudesView.getHeight();
        int w = 700;
        int h = 425;
        Rectangle xToolsDragBounds = new Rectangle( x, y, w, h );
        
        // Period (lamda1,T1) measurement tool
        _periodTool = new WavePacketPeriodTool( apparatusPanel, _wavePacket, _sumView.getChart() );
        _periodTool.setDragBounds( xToolsDragBounds );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        getClockControlPanel().setEnabled( false );
        
        // Control Panel
        _controlPanel = new D2CControlPanel( this, _wavePacket, 
                _amplitudesView, _harmonicsView, _sumView, _spacingTool, _periodTool );
        _controlPanel.addResetAllButton( this );
        setControlPanel( _controlPanel );

        // Link horizontal zoom controls
        _harmonicsView.getHorizontalZoomControl().addZoomListener( _sumView );
        _sumView.getHorizontalZoomControl().addZoomListener( _harmonicsView );
        
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
        
        HelpBubble spacingToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "D2CModule.help.spacingTool" ) );
        spacingToolHelp.pointAt( _spacingTool, HelpBubble.BOTTOM_CENTER, 15 );
        addHelpItem( spacingToolHelp );
        
        HelpBubble periodToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "D2CModule.help.periodTool" ) );
        periodToolHelp.pointAt( _periodTool, HelpBubble.BOTTOM_CENTER, 15 );
        addHelpItem( periodToolHelp );
        
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
        
        _wavePacket.setSpacing( WAVE_PACKET_SPACING );
        _wavePacket.setWidth( WAVE_PACKET_WIDTH );
        _wavePacket.setCenter( WAVE_PACKET_CENTER );
        _wavePacket.setSignificantWidth( WAVE_PACKET_SIGNIFICANT_WIDTH );
        
        _harmonicsView.setVisible( true );
        _harmonicsMinimizedView.setVisible( !_harmonicsView.isVisible() );
        _sumView.setVisible( true );
        _sumMinimizedView.setVisible( !_sumView.isVisible() ); 
        layoutViews();
    
        // Reset after doing layout, because some of these depend on visibility to reset properly.
        _amplitudesView.reset();
        _harmonicsView.reset();
        _sumView.reset();
        
        _spacingTool.setLocation( SPACING_TOOL_LOCATION );
        _periodTool.setLocation( PERIOD_TOOL_LOCATION );
        
        _controlPanel.reset();
    }
    
    //----------------------------------------------------------------------------
    // EventHandling
    //----------------------------------------------------------------------------
    
    /*
     * Resizes and repositions the graphs based on which ones are visible.
     * 
     * @param event
     */
    private void layoutViews() {

        int canvasHeight = _canvasSize.height;
        int availableHeight = canvasHeight - _amplitudesView.getHeight();
        
        _harmonicsMinimizedView.setVisible( !_harmonicsView.isVisible() );
        _sumMinimizedView.setVisible( !_sumView.isVisible() );
        
        if ( _harmonicsView.isVisible() && _sumView.isVisible() ) {
            _harmonicsView.setHeight( availableHeight/2 );
            _harmonicsView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumView.setHeight( availableHeight/2 );
            _sumView.setLocation( _amplitudesView.getX(), _harmonicsView.getY() + _harmonicsView.getHeight() );
        }
        else if ( _harmonicsView.isVisible() ) {
            _harmonicsView.setHeight( availableHeight - _sumMinimizedView.getHeight() );
            _harmonicsView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumMinimizedView.setLocation( _amplitudesView.getX(), _harmonicsView.getY() + _harmonicsView.getHeight() );
        }
        else if ( _sumView.isVisible() ) {
            _harmonicsMinimizedView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumView.setHeight( availableHeight - _harmonicsMinimizedView.getHeight() );
            _sumView.setLocation( _amplitudesView.getX(), _harmonicsMinimizedView.getY() + _harmonicsMinimizedView.getHeight() );
        }
        else {
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
    
    //----------------------------------------------------------------------------
    // Save & Load configurations
    //----------------------------------------------------------------------------
    
    /**
     * Saves the module's configuration.
     * 
     * @return
     */
    public FourierConfig.D2CConfig save() {
        
        FourierConfig.D2CConfig config = new FourierConfig.D2CConfig();
        
        // Save control panel config
        config.setSpacing( _controlPanel.getSpacing() );
        config.setAmplitudesEnvelopeEnabled( _controlPanel.isAmplitudesEnvelopeEnabled() );
        config.setCenter( _controlPanel.getCenter() );
        config.setKWidth( _controlPanel.getKWidth() );
        config.setDomainName( _controlPanel.getDomain().getName() );
        config.setWaveTypeName( _controlPanel.getWaveType().getName() );
        config.setSumEnvelopeEnabled( _controlPanel.isSumEnvelopeEnabled() );
        config.setShowWidthsEnabled( _controlPanel.isShowWidthsEnabled() );
        
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
    public void load( FourierConfig.D2CConfig config ) {
  
        // Load control panel config
        _controlPanel.setSpacing( config.getSpacing() );
        _controlPanel.setAmplitudesEnvelopeEnabled( config.isAmplitudesEnvelopeEnabled() );
        _controlPanel.setCenter( config.getCenter() );
        _controlPanel.setKWidth( config.getKWidth() );
        _controlPanel.setDomain( Domain.getByName( config.getDomainName() ) );
        _controlPanel.setWaveType( WaveType.getByName( config.getWaveTypeName() ) );
        _controlPanel.setSumEnvelopeEnabled( config.isSumEnvelopeEnabled() );
        _controlPanel.setShowWidthsEnabled( config.isShowWidthsEnabled() );
        
        // Load view config
        _harmonicsView.setVisible( config.isHarmonicsViewMaximized() );
        _sumView.setVisible( config.isSumViewMaximized() );
        layoutViews();
    }
}
