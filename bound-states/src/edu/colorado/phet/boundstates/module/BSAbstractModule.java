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
import edu.colorado.phet.boundstates.control.BSStopwatchPanel;
import edu.colorado.phet.boundstates.dialog.BSConfigureDialogFactory;
import edu.colorado.phet.boundstates.dialog.BSSuperpositionStateDialog;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.*;
import edu.colorado.phet.boundstates.persistence.BSConfig;
import edu.colorado.phet.boundstates.persistence.BSModuleConfig;
import edu.colorado.phet.boundstates.util.AxisSpec;
import edu.colorado.phet.boundstates.view.*;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.piccolo.XYPlotNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;


/**
 * BSAbstractModule is the module implementation shared by all modules 
 * in this simulation.  The BSAbstractModuleSpec (provided in the constructor)
 * is used to describe how the module should be specialized.
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
    private BSMagnifyingGlass _magnifyingGlass;
    
    // Plots
    private BSEnergyPlot _energyPlot;
    private BSBottomPlot _bottomPlot;
    
    // Nodes to draw plots separately from chart
    private XYPlotNode _energyPlotNode;
    private XYPlotNode _bottomPlotNode;

    // Controls
    private BSControlPanel _controlPanel;
    private BSStopwatchPanel _stopwatchPanel;

    // Dialogs
    private JDialog _configureDialog;
    private BSSuperpositionStateDialog _superpositionStateDialog;
    
    // Colors 
    private BSColorScheme _colorScheme;
    
    // Module specification
    private BSAbstractModuleSpec _moduleSpec;
 
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param title
     */
    public BSAbstractModule( String title, BSAbstractModuleSpec moduleSpec ) {
        
        super( title, new BSClock(), true /* startsPaused */ );
        
        _moduleSpec = moduleSpec;
        
        // hide the PhET logo
        setLogoPanel( null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Objects are created in reset method
        
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
        
        // Magnifying glass
        if ( _moduleSpec.isMagnifyingGlassSupported() ) {
            _magnifyingGlass = new BSMagnifyingGlass( _chartNode, BSConstants.COLOR_SCHEME );
            _parentNode.addChild( _magnifyingGlass );
            _magnifyingGlass.addInputEventListener( new PDragEventHandler() );
        }
        
        // Wave Function plot shows time-dependent data
        getClock().addClockListener( _bottomPlot );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new BSControlPanel( this, moduleSpec );
        setControlPanel( _controlPanel );
        
        String timeUnits = SimStrings.get( "units.time" );
        _stopwatchPanel = new BSStopwatchPanel( getClock(), timeUnits, 1, BSConstants.TIME_FORMAT );
        getClockControlPanel().addToLeft( _stopwatchPanel );
        
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
        
        // Magnifying glass 
        if ( _magnifyingGlass != null ) {
            //XXX need to adjust the position (and scale?)
            _magnifyingGlass.setOffset( 200, 200 );
            _magnifyingGlass.updateDisplay();
        }
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
       
    public boolean hasHelp() {
        return true;
    }
    
    /**
     * Disposes of dialogs when we switch to some other module.
     */
    public void deactivate() {
        if ( _configureDialog != null ) {
            _configureDialog.dispose();
        }
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.dispose();
        }
        super.deactivate();
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
            if ( _configureDialog != null ) {
                _configureDialog.dispose();
                _configureDialog = null;
            }

            if ( _superpositionStateDialog != null ) {
                _superpositionStateDialog.dispose();
                _superpositionStateDialog = null;
            }
        }
        
        // Model
        {
            final double massMultiplier = _moduleSpec.getMassMultiplierRange().getDefault();
            final double mass = BSConstants.ELECTRON_MASS * massMultiplier;
            _particle = new BSParticle( mass );

            _superpositionCoefficients = new BSSuperpositionCoefficients();

            final int numberOfWells = _moduleSpec.getNumberOfWellsRange().getDefault();
            BSWellRangeSpec rangeSpec = null;
            
            rangeSpec = _moduleSpec.getAsymmetricRangeSpec();
            if ( rangeSpec != null ) {
                _asymmetricWell = new BSAsymmetricWell( _particle, 
                    rangeSpec.getOffsetRange().getDefault(), 
                    rangeSpec.getHeightRange().getDefault(), 
                    rangeSpec.getWidthRange().getDefault() );
            }

            rangeSpec = _moduleSpec.getCoulomb1DRangeSpec();
            if ( rangeSpec != null ) {
                _coulomb1DWells = new BSCoulomb1DWells( _particle, 
                    numberOfWells, 
                    rangeSpec.getOffsetRange().getDefault(), 
                    rangeSpec.getSpacingRange().getDefault() );
            }
            
            rangeSpec = _moduleSpec.getCoulomb3DRangeSpec();
            if ( rangeSpec != null ) {
                _coulomb3DWell = new BSCoulomb3DWell( _particle, 
                    rangeSpec.getOffsetRange().getDefault() );
            }
            
            rangeSpec = _moduleSpec.getHarmonicOscillatorRangeSpec();
            if ( rangeSpec != null ) {
                _harmonicOscillatorWell = new BSHarmonicOscillatorWell( _particle, 
                    rangeSpec.getOffsetRange().getDefault(), 
                    rangeSpec.getAngularFrequencyRange().getDefault() );
            }
            
            rangeSpec = _moduleSpec.getSquareRangeSpec();
            if ( rangeSpec != null ) {
                _squareWells = new BSSquareWells( _particle, 
                    numberOfWells, 
                    rangeSpec.getOffsetRange().getDefault(), 
                    rangeSpec.getHeightRange().getDefault(), 
                    rangeSpec.getWidthRange().getDefault(), 
                    rangeSpec.getSeparationRange().getDefault() );
            }
            
            // Select the default...
            BSAbstractPotential defaultPotential = null;
            BSWellType defaultWellType = _moduleSpec.getDefaultWellType();
            if ( defaultWellType == BSWellType.ASYMMETRIC ) {
                defaultPotential = _asymmetricWell;
            }
            else if ( defaultWellType == BSWellType.COULOMB_1D ) {
                defaultPotential = _coulomb1DWells;
            }
            else if ( defaultWellType == BSWellType.COULOMB_1D ) {
                defaultPotential = _coulomb1DWells;
            }
            else if ( defaultWellType == BSWellType.COULOMB_3D ) {
                defaultPotential = _coulomb3DWell;
            }
            else if ( defaultWellType == BSWellType.HARMONIC_OSCILLATOR ) {
                defaultPotential = _harmonicOscillatorWell;
            }
            else if ( defaultWellType == BSWellType.SQUARE ) {
                defaultPotential = _squareWells;
            }
            else {
                throw new UnsupportedOperationException( "unsupported well type: " + defaultWellType );
            }
            assert( defaultPotential != null );
            
            // Populate the model...
            _model = new BSModel( _particle, defaultPotential, _superpositionCoefficients );
        }
        
        // View
        {
            _energyPlot.setModel( _model );
            _bottomPlot.setModel( _model );
            _eigenstatesNode.setModel( _model );
            _selectedEquationNode.setModel( _model );
            _hilitedEquationNode.setModel( _model );
            if ( _magnifyingGlass != null ) {
                _magnifyingGlass.setModel( _model );
            }

            // Adjust the Y-axis on the energy plot
            {
                BSWellType wellType = _model.getPotential().getWellType();
                BSWellRangeSpec rangeSpec = _moduleSpec.getRangeSpec( wellType );
                AxisSpec energyAxisSpec = rangeSpec.getEnergyAxisSpec();
                _energyPlot.setEnergyAxis( energyAxisSpec );
                _chart.getEnergyPlot().setEnergyAxis( energyAxisSpec );
            }
        }
        
        // Control
        {
            _controlPanel.setWellType( _model.getWellType() );
            _controlPanel.setNumberOfWellsControlVisible( _model.getPotential().supportsMultipleWells() );
            _controlPanel.setNumberOfWells( _model.getNumberOfWells() );
            _controlPanel.setMagnifyingGlassSelected( _moduleSpec.isMagnifyingGlassSelected() );
            _controlPanel.setRealSelected( true );
            _controlPanel.setImaginarySelected( false );
            _controlPanel.setMagnitudeSelected( false );
            _controlPanel.setPhaseSelected( false );
            _controlPanel.setBottomPlotMode( BSBottomPlotMode.PROBABILITY_DENSITY ); // do this after setting views
            _controlPanel.setParticleMass( _particle.getMass() );
        }
        
        // Clock
        resetClock();
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
        config.setMagnifyingGlassSelected( _controlPanel.isMagnifyingGlassSelected() );
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
        _controlPanel.setMagnifyingGlassSelected( config.isMagnifyingGlassSelected() );
        _controlPanel.setBottomPlotMode( config.getBottomPlotMode() );
        _controlPanel.setRealSelected( config.isRealSelected() );
        _controlPanel.setImaginarySelected( config.isImaginarySelected() );
        _controlPanel.setMagnitudeSelected( config.isMagnitudeSelected() );
        _controlPanel.setPhaseSelected( config.isPhaseSelected() );
        //XXX
    }
    
    /**
     * Sets the module's color scheme.
     * 
     * @param colorScheme
     */
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
        // Magnifying glass
        if ( _magnifyingGlass != null ) {
            _magnifyingGlass.setColorScheme( colorScheme );
        }
        // Superposition dialog...
        if ( _superpositionStateDialog != null ) {
            _superpositionStateDialog.setColorScheme( _colorScheme );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends ComponentAdapter {

        /*
         * Redo the "play area" layout when the window size changes.
         */
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
            
            if ( _configureDialog != null ) {
                _configureDialog.dispose();
            }

            // Adjust y-axis on the Energy plot...
            BSWellRangeSpec rangeSpec = _moduleSpec.getRangeSpec( wellType );
            AxisSpec energyAxisSpec = rangeSpec.getEnergyAxisSpec();
            _energyPlot.setEnergyAxis( energyAxisSpec );
            _chart.getEnergyPlot().setEnergyAxis( energyAxisSpec );
            
            BSAbstractPotential potential = null;
            if ( wellType == BSWellType.ASYMMETRIC ) {
                potential = _asymmetricWell;
            }
            else if ( wellType == BSWellType.COULOMB_1D ) {
                assert( _coulomb1DWells != null );
                potential = _coulomb1DWells;
            }
            else if ( wellType == BSWellType.COULOMB_3D ) {
                assert( _coulomb3DWell != null );
                potential = _coulomb3DWell;
            }
            else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
                potential = _harmonicOscillatorWell;
            }
            else if ( wellType == BSWellType.SQUARE ) {
                potential = _squareWells;
            }
            else {
                throw new IllegalArgumentException( "unsupported wellType: " + wellType );
            }
            assert( potential != null );
            _model.setPotential( potential );
            
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
    
    /**
     * Opens a "Configure Potential" dialog for the currently-selected potential.
     */
    public void showConfigureDialog() {
        if ( _configureDialog == null ) {
            _configureDialog = BSConfigureDialogFactory.createDialog( getFrame(), _model.getPotential(), _moduleSpec );
            _configureDialog.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent event ) {
                    _configureDialog = null;
                }
                public void windowClosed( WindowEvent event ) {
                    _configureDialog = null;
                }
            } );
            _configureDialog.show();
        }
    }
    
    /**
     * Opens a "Superposition State" dialog.
     */
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
    
    public void setBottomPlotMode( BSBottomPlotMode mode ) {
        _chart.getBottomPlot().setMode( mode );
        _bottomPlot.setMode( mode );
        _selectedEquationNode.setMode( mode );
        _hilitedEquationNode.setMode( mode );
    }
    
    public void setParticleMass( double mass ) {
        _particle.setMass( mass );
        resetClock();
    }
    
    public void setMagnifyingGlassVisible( boolean visible ) {
        if ( _magnifyingGlass != null ) {
            _magnifyingGlass.setVisible( visible );
        }
    }

    private void resetClock() {
        getClock().resetSimulationTime();
        _stopwatchPanel.reset();
    }
}
