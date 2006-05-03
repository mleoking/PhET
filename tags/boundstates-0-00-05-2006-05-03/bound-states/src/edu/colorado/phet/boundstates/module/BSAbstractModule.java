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
import javax.swing.JFrame;

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
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.view.clock.StopwatchPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.piccolo.XYPlotNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;


/**
 * BSSharedModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractModule extends PiccoloModule {

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
    private BSModel _model;
    private BSParticle _particle;
    private BSSuperpositionCoefficients _superpositionCoefficients;
    private BSAsymmetricWell _asymmetricWell;
    private BSCoulomb1DWells _coulomb1DWells;
    private BSCoulomb3DWell _coulomb3DWell;
    private BSHarmonicOscillatorWell _harmonicOscillatorWell;
    private BSSquareWells _squareWells;
    
    // View
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private BSEnergyLegend _legend;
    private BSCombinedChart _chart;
    private BSCombinedChartNode _chartNode;
    private BSEigenstatesNode _eigenstatesNode;
    private BSSelectedEquation _selectedEquationNode;
    private BSHilitedEquation _hilitedEquationNode;
    
    // Plots
    private BSEnergyPlot _energyPlot;
    private BSBottomPlot _bottomPlot;
    
    // Nodes to draw plots separately from chart
    private XYPlotNode _energyPlotNode;
    private XYPlotNode _bottomPlotNode;

    // Controls
    private BSControlPanel _controlPanel;

    // Dialogs
    private JDialog _configureWellDialog;
    private BSSuperpositionStateDialog _superpositionStateDialog;
    
    // Colors 
    private BSColorScheme _colorScheme;
    
    // Ranges and defaults
    private BSIntegerRange _numberOfWellsRange;
    private BSDoubleRange _massMultiplierRange;
    private BSDoubleRange _offsetRange, _depthRange, _widthRange, _spacingRange, _separationRange, _angularFrequencyRange;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param title
     */
    public BSAbstractModule( 
            String title,
            BSWellType[] wellTypes,
            final boolean supportsSuperpositionControls,
            final boolean supportsParticleControls,
            BSIntegerRange numberOfWellsRange,
            BSDoubleRange massMultiplierRange,
            BSDoubleRange offsetRange,
            BSDoubleRange depthRange,
            BSDoubleRange widthRange,
            BSDoubleRange spacingRange,
            BSDoubleRange separationRange,
            BSDoubleRange angularFrequencyRange
            ) {
        
        super( title, new BSClock(), true /* startsPaused */ );
        
        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Objects are created in reset method
        
        // Save ranges and default.
        {
            _numberOfWellsRange = numberOfWellsRange;
            _massMultiplierRange = massMultiplierRange;
            _offsetRange = offsetRange;
            _depthRange = depthRange;
            _widthRange = widthRange;
            _spacingRange = spacingRange;
            _separationRange = separationRange;
            _angularFrequencyRange = angularFrequencyRange;
        }
        
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
            _bottomPlot = _chart.getBottomPlot();
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
            _bottomPlot = new BSBottomPlot();

            RenderingHints renderingHints = _chart.getRenderingHints();
            
            _energyPlotNode = new XYPlotNode( _energyPlot );
            _energyPlotNode.setRenderingHints( renderingHints );
            _energyPlotNode.setName( "energyPlotNode" ); // debug
            _parentNode.addChild( _energyPlotNode );

            _bottomPlotNode = new XYPlotNode( _bottomPlot );
            _bottomPlotNode.setRenderingHints( renderingHints );
            _bottomPlotNode.setName( "bottomPlotNode" ); // debug
            _parentNode.addChild( _bottomPlotNode );
        }
        
        // Eigenstate interface
        _eigenstatesNode = new BSEigenstatesNode( _chartNode, _canvas );
        _parentNode.addChild( _eigenstatesNode );
        
        // Equations
        _selectedEquationNode = new BSSelectedEquation();
        _parentNode.addChild( _selectedEquationNode );
        _hilitedEquationNode = new BSHilitedEquation();
        _parentNode.addChild( _hilitedEquationNode );
        
        // Wave Function plot shows time-dependent data
        getClock().addClockListener( _bottomPlot );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new BSControlPanel( this, wellTypes, numberOfWellsRange, massMultiplierRange, supportsSuperpositionControls, supportsParticleControls );
        setControlPanel( _controlPanel );
        
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
        Rectangle2D bottomPlotBounds = _chartNode.localToGlobal( _chartNode.getBottomPlotBounds() );

        // Plot nodes (these exist if JFreeChart is not drawing dynamic elements)
        {
            if ( _energyPlotNode != null ) {
                _energyPlotNode.setOffset( 0, 0 );
                _energyPlotNode.setDataArea( energyPlotBounds );
            }
            if ( _bottomPlotNode != null ) {
                _bottomPlotNode.setOffset( 0, 0 );
                _bottomPlotNode.setDataArea( bottomPlotBounds );
            }
        }

        // dx (sample point spacing)
        {
            // All charts have the same x axis, so just use the Energy chart.
            Point2D p1 = _chartNode.nodeToEnergy( new Point2D.Double( 0, 0 ) );
            Point2D p2 = _chartNode.nodeToEnergy( new Point2D.Double( BSConstants.PIXELS_PER_POTENTIAL_SAMPLE_POINT, 0 ) );
            double dx = p2.getX() - p1.getX();
//            System.out.println( "BSAbstractModule.layout dx=" + dx );
            
            if ( dx >= Double.MIN_VALUE && dx <= Double.MAX_VALUE ) {            
                // Set the dx use to draw the potentials...
                _energyPlot.setDx( dx );
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
        
        // Equations
        {
            // Move to upper right corner of bottom chart
            double x1 = bottomPlotBounds.getX() + bottomPlotBounds.getWidth() - 5;
            double y1 = bottomPlotBounds.getY() + 5;
            _selectedEquationNode.setLocation( x1, y1 );
            
            // Just below other equations
            double x2 = x1;
            double y2 = bottomPlotBounds.getY() + _selectedEquationNode.getFullBounds().getHeight() + 5;
            _hilitedEquationNode.setLocation( x2, y2 );
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
    // 
    //----------------------------------------------------------------------------
    
    /**
     * Resets the module to its initial state.
     */
    public void reset() {
        
        // Close all dialogs...
        {
            if ( _configureWellDialog != null ) {
                _configureWellDialog.dispose();
                _configureWellDialog = null;
            }

            if ( _superpositionStateDialog != null ) {
                _superpositionStateDialog.dispose();
                _superpositionStateDialog = null;
            }
        }
        
        // Model
        final double mass = BSConstants.ELECTRON_MASS * _massMultiplierRange.getDefault();
        _particle = new BSParticle( mass );
        _superpositionCoefficients = new BSSuperpositionCoefficients();
        _asymmetricWell = new BSAsymmetricWell( _particle, 
                _offsetRange.getDefault(), _depthRange.getDefault(), _widthRange.getDefault() );
        _coulomb1DWells = new BSCoulomb1DWells( _particle, _numberOfWellsRange.getDefault(), 
                _offsetRange.getDefault(), _spacingRange.getDefault() );
        _coulomb3DWell = new BSCoulomb3DWell( _particle,  
                _offsetRange.getDefault() );
        _harmonicOscillatorWell = new BSHarmonicOscillatorWell( _particle, 
                _offsetRange.getDefault(), _angularFrequencyRange.getDefault() );
        _squareWells = new BSSquareWells( _particle, _numberOfWellsRange.getDefault(), 
                _offsetRange.getDefault(), _depthRange.getDefault(), _widthRange.getDefault(), _separationRange.getDefault() );
        _model = new BSModel( _squareWells, _superpositionCoefficients );
        
        // View
        _energyPlot.setModel( _model );
        _bottomPlot.setModel( _model );
        _eigenstatesNode.setModel( _model );
        _selectedEquationNode.setModel( _model );
        _hilitedEquationNode.setModel( _model );
        
        // Control
        _controlPanel.setWellType( _model.getWellType() );
        _controlPanel.setNumberOfWellsControlVisible( _model.getPotential().supportsMultipleWells() );
        _controlPanel.setNumberOfWells( _model.getNumberOfWells() );
        _controlPanel.setRealSelected( true );
        _controlPanel.setImaginarySelected( false );
        _controlPanel.setMagnitudeSelected( false );
        _controlPanel.setPhaseSelected( false );
        _controlPanel.setBottomPlotMode( BSBottomPlot.MODE_PROBABILITY_DENSITY ); // do this after setting views
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------
    
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
        config.setRealSelected( _controlPanel.isRealSelected() );
        config.setImaginarySelected( _controlPanel.isImaginarySelected() );
        config.setMagnitudeSelected( _controlPanel.isMagnitudeSelected( ) );
        config.setPhaseSelected( _controlPanel.isPhaseSelected() );
        config.setBottomPlotMode( _controlPanel.getBottomPlotMode() ); // do this after setting views
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
        _controlPanel.setBottomPlotMode( config.getBottomPlotMode() );
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
        _bottomPlot.setColorScheme( colorScheme );
        // Control panel legend...
        _controlPanel.setColorScheme( colorScheme );
        // Legend above the charts...
        _legend.setColorScheme( colorScheme );
        // Eigenstates...
        _eigenstatesNode.setColorScheme( colorScheme );
        // Equations...
        _selectedEquationNode.setColorScheme( colorScheme );
        _hilitedEquationNode.setColorScheme( colorScheme );
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
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets this module's frame.
     * 
     * @return JFrame
     */
    public JFrame getFrame() {
        return PhetApplication.instance().getPhetFrame();
    }
    
    /**
     * Adds a listener to the module's clock.
     * 
     * @param listener
     */
    public void addClockListener( ClockListener listener ) {
        getClock().addClockListener( listener );
    }
    
    /**
     * Removes a listener from the module's clock.
     * 
     * @param listener
     */
    public void removeClockListener( ClockListener listener ) {
        getClock().removeClockListener( listener );
    }
    
    public void setWellType( BSWellType wellType ) {
        
        if ( wellType != _model.getWellType() ) {
            
            if ( _configureWellDialog != null ) {
                _configureWellDialog.dispose();
            }

            if ( wellType == BSWellType.ASYMMETRIC ) {
                _model.setPotential( _asymmetricWell );
            }
            else if ( wellType == BSWellType.COULOMB_1D ) {
                _model.setPotential( _coulomb1DWells );
            }
            else if ( wellType == BSWellType.COULOMB_3D ) {
                _model.setPotential( _coulomb3DWell );
            }
            else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
                _model.setPotential( _harmonicOscillatorWell );
            }
            else if ( wellType == BSWellType.SQUARE ) {
                _model.setPotential( _squareWells );
            }
            else {
                throw new IllegalArgumentException( "unsupported wellType: " + wellType );
            }

            _controlPanel.setWellType( wellType );
            _controlPanel.setNumberOfWellsControlVisible( _model.getPotential().supportsMultipleWells() );
            _controlPanel.setNumberOfWells( _model.getNumberOfWells() );

            resetClock();
        }
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != _model.getNumberOfWells() ) {
            _model.getPotential().setNumberOfWells( numberOfWells );
            resetClock();
        }
    }
    
    public void showConfigureEnergyDialog() {
        if ( _configureWellDialog == null ) {
            _configureWellDialog = BSConfigureDialogFactory.createDialog( getFrame(), _model.getPotential(),
                    _offsetRange, _depthRange, _widthRange, _spacingRange, _separationRange, _angularFrequencyRange );
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
            _superpositionStateDialog = new BSSuperpositionStateDialog( getFrame(), _model, _colorScheme );
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
    
    public void setRealVisible( boolean visible ) {
        _bottomPlot.setRealVisible( visible );
    }
    
    public void setImaginaryVisible( boolean visible ) {
        _bottomPlot.setImaginaryVisible( visible );
    }
    
    public void setMagnitudeVisible( boolean visible ) {
        _bottomPlot.setMagnitudeVisible( visible );
    }
    
    public void setPhaseVisible( boolean visible ) {
        _bottomPlot.setPhaseVisible( visible );
    }
    
    public void setBottomPlotMode( int mode ) {
        _chart.getBottomPlot().setMode( mode );
        _bottomPlot.setMode( mode );
        _selectedEquationNode.setMode( mode );
        _hilitedEquationNode.setMode( mode );
    }
    
    public void setParticleMass( double mass ) {
        _particle.setMass( mass );
        resetClock();
    }

    private void resetClock() {
        getClock().resetSimulationTime();
    }
}
