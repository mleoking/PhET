/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.BSConfigureEnergyDialog;
import edu.colorado.phet.boundstates.control.BSSharedControlPanel;
import edu.colorado.phet.boundstates.control.BSSuperpositionStateDialog;
import edu.colorado.phet.boundstates.enum.WellType;
import edu.colorado.phet.boundstates.model.BSClock;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSTotalEnergy;
import edu.colorado.phet.boundstates.persistence.BSConfig;
import edu.colorado.phet.boundstates.persistence.BSModuleConfig;
import edu.colorado.phet.boundstates.view.*;
import edu.colorado.phet.common.view.clock.StopwatchPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.piccolo.XYPlotNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;


/**
 * BSManyModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyModule extends BSAbstractModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font LEGEND_FONT = new Font( BSConstants.FONT_NAME, Font.PLAIN, 12 );
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 10; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private BSTotalEnergy _totalEnergy;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private BSEnergyLegend _legend;
    private BSCombinedChart _chart;
    private BSCombinedChartNode _chartNode;
    private BSTotalEnergyNode _totalEnergyNode;
    
    // Plots
    private BSEnergyPlot _energyPlot;
    private BSWaveFunctionPlot _waveFunctionPlot;
    
    // Nodes to draw plots separately from chart
    private XYPlotNode _energyPlotNode;
    private XYPlotNode _waveFunctionPlotNode;

    // Controls
    private BSSharedControlPanel _controlPanel;

    // Dialogs
    private BSConfigureEnergyDialog _configureEnergyDialog;
    private BSSuperpositionStateDialog _superpositionStateDialog;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public BSManyModule() {
        this( SimStrings.get( "BSManyModule.title" ) );
    }
    
    /*
     * Constructor for use by subclasses that need to change title.
     * 
     * @param title
     */
    protected BSManyModule( String title ) {
        super( title, new BSClock(), true /* startsPaused */ );
        
        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        double[] energies = { 1, 2, 2.5, 3, 3.25, 3.5, 3.75, 4, 4.1, 4.2, 4.3, 4.4, 4.5, 4.55, 4.6, 4.65, 4.7, 4.75 };
        BSEigenstate[] eigenstates = BSEigenstate.createEigenstates( energies );
        _totalEnergy = new BSTotalEnergy( eigenstates );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        EventListener listener = new EventListener();
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.setBackground( BSConstants.CANVAS_BACKGROUND );
            setSimulationPanel( _canvas );
        }
        
        // Root of our scene graph
        {
            _parentNode = new PNode();
            _canvas.addScreenChild( _parentNode );
        }
        
        // Combined chart
        {
            _chart = new BSCombinedChart();
            _chart.setBackgroundPaint( BSConstants.CANVAS_BACKGROUND );
            
            _chartNode = new BSCombinedChartNode( _chart );
            _parentNode.addChild( _chartNode );
        }
        
        // Energy graph legend
        { 
            _legend = new BSEnergyLegend();
            _parentNode.addChild( _legend );
        }
        
        // This is where we decide how much of the drawing JFreeChart will be doing...
        if ( BSConstants.JFREECHART_DYNAMIC ) {
            /*
             * If JFreeChart is being used to draw all elements (both static and dynamic) 
             * then get references to the plots that are in the combined chart.
             * We'll add our data to those plots so that the chart changes dynamically.
             */
            _energyPlot = _chart.getEnergyPlot();
            _waveFunctionPlot = _chart.getWaveFunctionPlot();
        }
        else {
            /*
             * If JFreeChart is being used to draw only static elements, then turn on
             * buffering of the chart node.  The chart will be drawn as an image, and
             * the image will be created only when the chart changes.  To limit the 
             * changes that occur to the chart, we won't be adding any data to it.
             * So we'll create our own plots, to which we'll add the data. And we'll
             * draw those plots top of the static chart using XYPlotNodes.  We'll use 
             * some of the chart's rendering hints for the XYPlot nodes so that the 
             * "look" is consistent with the static chart.
             */
            _chartNode.setBuffered( true );
            
            _energyPlot = new BSEnergyPlot();
            _waveFunctionPlot = new BSWaveFunctionPlot();

            RenderingHints renderingHints = _chart.getRenderingHints();
            
            _energyPlotNode = new XYPlotNode( _energyPlot );
            _energyPlotNode.setRenderingHints( renderingHints );
            _energyPlotNode.setName( "energyPlotNode" ); // debug
            _parentNode.addChild( _energyPlotNode );

            _waveFunctionPlotNode = new XYPlotNode( _waveFunctionPlot );
            _waveFunctionPlotNode.setRenderingHints( renderingHints );
            _waveFunctionPlotNode.setName( "waveFunctionPlotNode" ); // debug
            _parentNode.addChild( _waveFunctionPlotNode );
        } 
        
        _totalEnergyNode = new BSTotalEnergyNode( _totalEnergy, _chartNode, _canvas );
        _parentNode.addChild( _totalEnergyNode );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new BSSharedControlPanel( this );
        setControlPanel( _controlPanel );
        WellType[] wellTypeChoices = { WellType.COULOMB, WellType.SQUARE };
        setWellTypeChoices( wellTypeChoices );
        
        String timeUnits = SimStrings.get( "units.time" );
        StopwatchPanel stopwatchPanel = new StopwatchPanel( getClock(), timeUnits, 1, BSConstants.TIME_FORMAT );
        getClockControlPanel().addToLeft( stopwatchPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        HelpPane helpPane = getDefaultHelpPane();

        HelpBalloon configureHelp = new HelpBalloon( helpPane, SimStrings.get( "help.potentialControl" ), HelpBalloon.RIGHT_CENTER, 40 );
        helpPane.add( configureHelp );
        configureHelp.pointAt( _controlPanel.getPotentialControl() );
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
       
        reset();
        layoutCanvas();
        _canvas.addComponentListener( listener );
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Lays out nodes on the canvas.
     * This is called whenever the canvas size changes.
     */
    private void layoutCanvas() {

        // Height of the legend
        double legendHeight = _legend.getFullBounds().getHeight();

        // Location and dimensions of combined chart
        final double chartWidth = _canvas.getWidth() - ( 2 * X_MARGIN );
        final double chartHeight = _canvas.getHeight() - ( legendHeight + ( 2 * Y_MARGIN ) + Y_SPACING );

        // Charts
        {
            _chartNode.setBounds( 0, 0, chartWidth, chartHeight );
            AffineTransform chartTransform = new AffineTransform();
            chartTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + Y_SPACING );
            chartTransform.translate( 0, 0 ); // registration point @ upper left
            _chartNode.setTransform( chartTransform );
            _chartNode.updateChartRenderingInfo();
            
            _totalEnergyNode.setTransform( chartTransform );
            _totalEnergyNode.updateDisplay();
        }
        
        // Bounds of plots, in global coordinates -- get these after transforming the chart!
        Rectangle2D energyPlotBounds = _chartNode.localToGlobal( _chartNode.getEnergyPlotBounds() );
        Rectangle2D waveFunctionPlotBounds = _chartNode.localToGlobal( _chartNode.getWaveFunctionPlotBounds() );

        // Plot nodes (these exist if JFreeChart is not drawing dynamic elements)
        {
            if ( _energyPlotNode != null ) {
                _energyPlotNode.setOffset( 0, 0 );
                _energyPlotNode.setDataArea( energyPlotBounds );
            }
            if ( _waveFunctionPlotNode != null ) {
                _waveFunctionPlotNode.setOffset( 0, 0 );
                _waveFunctionPlotNode.setDataArea( waveFunctionPlotBounds );
            }
        }

        // dx (sample point spacing)
        {
            // All charts have the same x axis, so just use the Energy chart.
            Point2D p1 = _chartNode.nodeToEnergy( new Point2D.Double( 0, 0 ) );
            Point2D p2 = _chartNode.nodeToEnergy( new Point2D.Double( BSConstants.PIXELS_PER_SAMPLE_POINT, 0 ) );
            double dx = p2.getX() - p1.getX();
            //XXX now do something with dx...
        }

        // Legend
        {
            // Aligned with left edge of energy plot, centered in space about energy plot
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( energyPlotBounds.getX(), Y_MARGIN );
            legendTransform.translate( 0, 0 ); // upper left
            _legend.setTransform( legendTransform );
        }
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
       
    public boolean hasHelp() {
        return true;
    }
    
    public void deactivate() {
        if ( _configureEnergyDialog != null ) {
            _configureEnergyDialog.hide();
        }
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.hide();
        }
        super.deactivate();
    }
    
    public void activate() {
        if ( _configureEnergyDialog != null ) {
            _configureEnergyDialog.show();
        }
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.show();
        }
        super.activate();
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the module to its initial state.
     */
    public void reset() {
        
        // Model
        //XXX
        
        // Controls
        _controlPanel.setDisplayType( BSSharedControlPanel.DISPLAY_WAVE_FUNCTION );
        _controlPanel.setRealSelected( true );
        _controlPanel.setImaginarySelected( false );
        _controlPanel.setMagnitudeSelected( false );
        _controlPanel.setPhaseSelected( false );
        //XXX
    }
    
    /**
     * Saves the module's configuration by writing it to a provided configuration object.
     * 
     * @param appConfig
     */
    public void save( BSConfig appConfig ) {
        BSModuleConfig config = appConfig.getModuleConfig();
        
        // Clock
        config.setClockRunning( getClock().isRunning() );
        
        // Model
        //XXX
        
        // Control panel
        config.setDisplayType( _controlPanel.getDisplayType() );
        config.setRealSelected( _controlPanel.isRealSelected() );
        config.setImaginarySelected( _controlPanel.isImaginarySelected() );
        config.setMagnitudeSelected( _controlPanel.isMagnitudeSelected( ) );
        config.setPhaseSelected( _controlPanel.isPhaseSelected() );
        //XXX
    }
    
    /**
     * Loads the module's configuration by reading it from a provided configuration object.
     * 
     * @param appConfig
     */
    public void load( BSConfig appConfig ) {
        BSModuleConfig config = appConfig.getModuleConfig();
        
        // Clock
        if ( config.isClockRunning() ) {
            getClock().start();
        }
        else {
            getClock().pause();
        }
    
        // Model
        //XXX
        
        // Control panel
        _controlPanel.setDisplayType( config.getDisplayType() );
        _controlPanel.setRealSelected( config.isRealSelected() );
        _controlPanel.setImaginarySelected( config.isImaginarySelected() );
        _controlPanel.setMagnitudeSelected( config.isMagnitudeSelected() );
        _controlPanel.setPhaseSelected( config.isPhaseSelected() );
        //XXXX
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends ComponentAdapter {

        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                layoutCanvas();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Feature enablers
    //----------------------------------------------------------------------------
    
    public void setNumberOfWellsControlVisible( boolean visible ) {
        _controlPanel.setNumberOfWellsControlVisible( visible );
    }
    
    public void setWellTypeChoices( WellType[] wellTypes ) {
        _controlPanel.setWellTypeChoices( wellTypes );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setWellType( WellType wellType ) {
        //XXX
        resetClock();
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        //XXX
        resetClock();
    }
    
    public void showConfigureEnergyDialog() {
        if ( _configureEnergyDialog == null ) {
            _configureEnergyDialog = new BSConfigureEnergyDialog( getFrame() );
            _configureEnergyDialog.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent event ) {
                    _configureEnergyDialog = null;
                }
                public void windowClosed( WindowEvent event ) {
                    _configureEnergyDialog = null;
                }
            } );
            _configureEnergyDialog.show();
        }
    }
    
    public void showSuperpositionStateDialog() {
        if ( _superpositionStateDialog == null ) {
            _superpositionStateDialog = new BSSuperpositionStateDialog( getFrame() );
            _superpositionStateDialog.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent event ) {
                    _superpositionStateDialog = null;
                }
                public void windowClosed( WindowEvent event ) {
                    _superpositionStateDialog = null;
                }
            } );
            _superpositionStateDialog.show();
        }
    }
    
    public void setDisplayType( int displayType ) {
        //XX displayType is one of the BSManyControlPanel.DISPLAY_* constants
        resetClock();
    }
    
    public void setRealVisible( boolean visible ) {
        //XXX
    }
    
    public void setImaginaryVisible( boolean visible ) {
        //XXX
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        //XXX
    }
    
    public void setPhaseVisible( boolean visible ) {
        //XXX
    }
    
    public void setParticleMass( double mass ) {
        //XXX
        resetClock();
    }

    private void resetClock() {
        getClock().resetSimulationTime();
    }
    
    private void handleClockReset() {
        //XXX
    }
}
