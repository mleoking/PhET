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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.D2CControlPanel;
import edu.colorado.phet.fourier.help.FourierHelpItem;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.view.MinimizedView;
import edu.colorado.phet.fourier.view.d2c.D2CAmplitudesView;
import edu.colorado.phet.fourier.view.d2c.D2CHarmonicsView;
import edu.colorado.phet.fourier.view.d2c.D2CSumView;
import edu.colorado.phet.fourier.view.tools.WavePacketKWidthTool;
import edu.colorado.phet.fourier.view.tools.WavePacketPeriodTool;
import edu.colorado.phet.fourier.view.tools.WavePacketSpacingTool;
import edu.colorado.phet.fourier.view.tools.WavePacketXWidthTool;


/**
 * D2CModule is the "Discrete to Continuous" (D2C) module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CModule extends FourierModule implements ApparatusPanel2.ChangeListener {

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
    private WavePacketKWidthTool _kWidthTool;
    private WavePacketXWidthTool _xWidthTool;
    private WavePacketPeriodTool _periodTool;
    
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
        _wavePacket = new GaussianWavePacket( 
                WAVE_PACKET_SPACING, WAVE_PACKET_WIDTH, 
                WAVE_PACKET_CENTER, WAVE_PACKET_SIGNIFICANT_WIDTH );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
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
        _harmonicsMinimizedView = new MinimizedView( apparatusPanel, SimStrings.get( "D2CHarmonicsView.title" ) );
        apparatusPanel.addGraphic( _harmonicsMinimizedView, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumView = new D2CSumView( apparatusPanel, _wavePacket );
        apparatusPanel.addGraphic( _sumView, SUM_LAYER );
        
        // Sum view (minimized)
        _sumMinimizedView = new MinimizedView( apparatusPanel, SimStrings.get( "D2CSumView.title" ) );
        apparatusPanel.addGraphic( _sumMinimizedView, SUM_CLOSED_LAYER );
        
        // Spacing (k1,w1) measurement tool
        _spacingTool = new WavePacketSpacingTool( apparatusPanel, _wavePacket, _amplitudesView.getChart() );
        _spacingTool.setDragBounds( _amplitudesView.getChart().getBounds() );
        apparatusPanel.addGraphic( _spacingTool, TOOLS_LAYER );
        
        // k-space width measurement tool
        Point2D amplitudesChartOrigin = new Point2D.Double( 347, 175 ); // (12pi,0) on chart, in apparatus panel coordinates
        _kWidthTool = new WavePacketKWidthTool( apparatusPanel, _wavePacket, _amplitudesView.getChart(), amplitudesChartOrigin );
        _kWidthTool.setDragBounds( _amplitudesView.getChart().getBounds() );
        apparatusPanel.addGraphic( _kWidthTool, TOOLS_LAYER );
        
        // Drag bounds for the x-space tools
        int x = 0;
        int y = _amplitudesView.getHeight();
        int w = 700;
        int h = 425;
        Rectangle xToolsDragBounds = new Rectangle( x, y, w, h );
        
        // x-space width measurement tool
        Point sumChartOrigin = new Point( 330, 540 ); // (0,0) on chart, in apparatus panel coordinates
        _xWidthTool = new WavePacketXWidthTool( apparatusPanel, _wavePacket, _sumView.getChart(), sumChartOrigin );
        _xWidthTool.setDragBounds( xToolsDragBounds );
        apparatusPanel.addGraphic( _xWidthTool, TOOLS_LAYER );
        
        // Period (lamda1,T1) measurement tool
        _periodTool = new WavePacketPeriodTool( apparatusPanel, _wavePacket, _sumView.getChart() );
        _periodTool.setDragBounds( xToolsDragBounds );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new D2CControlPanel( this, _wavePacket, 
                _amplitudesView, _harmonicsView, _sumView,
                _spacingTool, _kWidthTool, _xWidthTool, _periodTool );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
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
        
        FourierHelpItem spacingToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "D2CModule.help.spacingTool" ) );
        spacingToolHelp.pointAt( _spacingTool, FourierHelpItem.UP, 15 );
        addHelpItem( spacingToolHelp );
        
        FourierHelpItem periodToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "D2CModule.help.periodTool" ) );
        periodToolHelp.pointAt( _periodTool, FourierHelpItem.DOWN, 15 );
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
}
