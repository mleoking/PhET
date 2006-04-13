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
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JDialog;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.control.BSControlPanel;
import edu.colorado.phet.boundstates.dialog.BSConfigureDialogFactory;
import edu.colorado.phet.boundstates.dialog.BSSuperpositionStateDialog;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.*;
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
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 10; // space at top & bottom edges of canvas
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private BSParticle _particle;
    private BSAbstractPotential _selectedPotential;
    private BSCoulombWells _coulombWells;
    private BSHarmonicOscillatorWell _harmonicOscillatorWell;
    private BSSquareWells _squareWells;
    private BSAsymmetricWell _asymmetricWell;
    private BSSuperpositionCoefficients _superpositionCoefficients;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private BSEnergyLegend _legend;
    private BSCombinedChart _chart;
    private BSCombinedChartNode _chartNode;
    private BSEigenstatesNode _eigenstatesNode;
    
    // Plots
    private BSEnergyPlot _energyPlot;
    private BSWaveFunctionPlot _waveFunctionPlot;
    
    // Nodes to draw plots separately from chart
    private XYPlotNode _energyPlotNode;
    private XYPlotNode _waveFunctionPlotNode;

    // Controls
    private BSControlPanel _controlPanel;

    // Dialogs
    private JDialog _configureWellDialog;
    private BSSuperpositionStateDialog _superpositionStateDialog;
    
    // Colors 
    private BSColorScheme _colorScheme;
    
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
        
        // created in reset method
        
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
        
        // Eigenstate interface
        _eigenstatesNode = new BSEigenstatesNode( _chartNode, _canvas );
        _parentNode.addChild( _eigenstatesNode );
        
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
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new BSControlPanel( this );
        setControlPanel( _controlPanel );
//XXX        WellType[] wellTypeChoices = { WellType.COULOMB, WellType.SQUARE };
        BSWellType[] wellTypeChoices = { BSWellType.COULOMB, BSWellType.HARMONIC_OSCILLATOR, BSWellType.SQUARE, BSWellType.ASYMMETRIC };
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
            
            _eigenstatesNode.setTransform( chartTransform );
            _eigenstatesNode.updateDisplay();
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
            
            // Set the dx for each potential...
            if ( dx >= Double.MIN_VALUE && dx <= Double.MAX_VALUE ) {
                System.out.println( "BSManyModule.layout dx=" + dx ); //XXX
                _coulombWells.setDx( dx );
                _harmonicOscillatorWell.setDx( dx );
                _squareWells.setDx( dx );
                _asymmetricWell.setDx( dx );
            }
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
        if ( _configureWellDialog != null ) {
            _configureWellDialog.hide();
        }
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.hide();
        }
        super.deactivate();
    }
    
    public void activate() {
        if ( _configureWellDialog != null ) {
            _configureWellDialog.show();
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
        _particle = new BSParticle( BSConstants.DEFAULT_MASS );
        _coulombWells = new BSCoulombWells( _particle, 2 );
        _harmonicOscillatorWell = new BSHarmonicOscillatorWell( _particle );
        _squareWells = new BSSquareWells( _particle, 2 );
        _asymmetricWell = new BSAsymmetricWell( _particle );
        _selectedPotential = _harmonicOscillatorWell;
        _superpositionCoefficients = new BSSuperpositionCoefficients( 5 /*XXX*/ );
        
        // View 
        _eigenstatesNode.setPotential( _selectedPotential );
        _chart.getEnergyPlot().setPotential( _selectedPotential );
        
        // Controls
        _controlPanel.setWellType( _selectedPotential.getWellType() );
        _controlPanel.setNumberOfWells( _selectedPotential.getNumberOfWells() );
        _controlPanel.setDisplayType( BSControlPanel.DISPLAY_WAVE_FUNCTION );
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
        //XXX
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        _colorScheme = colorScheme;
        // Chart
        _chart.setColorScheme( colorScheme );
        // Plots, in case we're drawing them separately from the chart...
        _energyPlot.setColorScheme( colorScheme );
        _waveFunctionPlot.setColorScheme( colorScheme );
        // Control panel legend...
        _controlPanel.setColorScheme( colorScheme );
        // Legend above the charts...
        _legend.setColorScheme( colorScheme );
        // Eigenstates...
        _eigenstatesNode.setColorScheme( colorScheme );
        // Superposition dialog...
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.setColorScheme( _colorScheme );
        }
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
    
    public void setWellTypeChoices( BSWellType[] wellTypes ) {
        _controlPanel.setWellTypeChoices( wellTypes );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setWellType( BSWellType wellType ) {
        
        if ( _configureWellDialog != null ) { 
            _configureWellDialog.dispose();
        }
        
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.dispose();
        }
        
        if ( wellType == BSWellType.COULOMB ) {
            _selectedPotential = _coulombWells;
            _controlPanel.setNumberOfWellsControlVisible( true );
            _controlPanel.setNumberOfWells( _selectedPotential.getNumberOfWells() );
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            _selectedPotential = _harmonicOscillatorWell;
            _controlPanel.setNumberOfWellsControlVisible( false );
        }
        else if ( wellType == BSWellType.SQUARE ) {
            _selectedPotential = _squareWells;
            _controlPanel.setNumberOfWellsControlVisible( true );
        }
        else if ( wellType == BSWellType.ASYMMETRIC ) {
            _selectedPotential = _asymmetricWell;
            _controlPanel.setNumberOfWellsControlVisible( false );
        }
        
        _controlPanel.setNumberOfWells( _selectedPotential.getNumberOfWells() );
        _eigenstatesNode.setPotential( _selectedPotential );
        _chart.getEnergyPlot().setPotential( _selectedPotential );
        
        resetClock();
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        _selectedPotential.setNumberOfWells( numberOfWells );
        resetClock();
    }
    
    public void showConfigureEnergyDialog() {
        if ( _configureWellDialog == null ) {
            _configureWellDialog = BSConfigureDialogFactory.createDialog( getFrame(), _selectedPotential );
            _configureWellDialog.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent event ) {
                    _configureWellDialog = null;
                }
                public void windowClosed( WindowEvent event ) {
                    _configureWellDialog = null;
                }
            } );
            _configureWellDialog.show();
        }
    }
    
    public void showSuperpositionStateDialog() {
        if ( _superpositionStateDialog == null ) {
            int startingIndex = _selectedPotential.getStartingIndex();
            _superpositionStateDialog = new BSSuperpositionStateDialog( getFrame(), _superpositionCoefficients, startingIndex, _colorScheme );
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
        _particle.setMass( mass );
        resetClock();
    }

    private void resetClock() {
        getClock().resetSimulationTime();
    }
    
    private void handleClockReset() {
        //XXX
    }
}
