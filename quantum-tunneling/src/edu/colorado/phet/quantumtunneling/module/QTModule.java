/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.module;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.view.LegendItem;
import edu.colorado.phet.quantumtunneling.view.QTChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * QTModule is the sole module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font AXIS_LABEL_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 16 );
    private static final Font LEGEND_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 20; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    private static final int CANVAS_BOUNDARY_STROKE_WIDTH = 1;
    private static final double TITLE_SCALE = 1.5;
    private static final double LEGEND_SCALE = 1.2;
    private static final double CONFIGURE_BUTTON_SCALE = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private PNode _legend;
    private PSwing _configureButton;
    private QTChart _energyChart, _waveFunctionChart, _probabilityDensityChart;
    private PText _positionTitle;
    private QTControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public QTModule( AbstractClock clock ) {
        super( SimStrings.get( "QTModule.title" ), clock );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        EventListener listener = new EventListener();
        
        // Piccolo canvas
        {
            _canvas = new PhetPCanvas( CANVAS_RENDERING_SIZE );
            _canvas.addComponentListener( listener );
            setCanvas( _canvas );
        }
        
        // Configure button
        {
            JButton jButton = new JButton( SimStrings.get( "button.configure" ) );
            jButton.setOpaque( false );
            jButton.addActionListener( listener );
            _configureButton = new PSwing( _canvas, jButton );
        }
        
        // Energy graph legend
        { 
            LegendItem totalEnergyItem = 
                new LegendItem( SimStrings.get( "legend.totalEnergy" ), QTConstants.TOTAL_ENERGY_COLOR );
            
            LegendItem potentialEnergyItem = 
                new LegendItem( SimStrings.get( "legend.potentialEnergy" ), QTConstants.POTENTIAL_ENERGY_COLOR );
            potentialEnergyItem.translate( totalEnergyItem.getFullBounds().getWidth() + 20, 0 );

            _legend = new PNode();
            _legend.scale( LEGEND_SCALE );
            _legend.addChild( totalEnergyItem );
            _legend.addChild( potentialEnergyItem );
        }
        
        // Energy chart
        {
            String energyLabel = SimStrings.get( "axis.energy" );
            _energyChart = new QTChart( _canvas, null, null, energyLabel );
            _energyChart.setAxisLabelFont( AXIS_LABEL_FONT );
            _energyChart.setXRange( QTConstants.POSITION_RANGE );
            _energyChart.setYRange( QTConstants.ENERGY_RANGE );
        }
        
        // Wave Function chart
        {
            String waveFunctionLabel = SimStrings.get( "axis.waveFunction" );
            _waveFunctionChart = new QTChart( _canvas, null, null, waveFunctionLabel );
            _waveFunctionChart.setAxisLabelFont( AXIS_LABEL_FONT );
            _waveFunctionChart.setXRange( QTConstants.POSITION_RANGE );
            _waveFunctionChart.setYRange( QTConstants.WAVE_FUNCTION_RANGE );
        }
        
        // Probaility Density chart
        {
            String probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
            _probabilityDensityChart = new QTChart( _canvas, null, null, probabilityDensityLabel );
            _probabilityDensityChart.setAxisLabelFont( AXIS_LABEL_FONT );
            _probabilityDensityChart.setXRange( QTConstants.POSITION_RANGE );
            _probabilityDensityChart.setYRange( QTConstants.PROBABILITY_DENSITY_RANGE );
        }
        
        // Position x-axis label
        {
            _positionTitle = new PText( SimStrings.get( "axis.position" ) );
            _positionTitle.setFont( AXIS_LABEL_FONT );
        }
        
        // Add all the nodes to one parent node.
        {
            _parentNode = new PNode();
            
            _parentNode.addChild( _configureButton );
            _parentNode.addChild( _legend );
            _parentNode.addChild( _energyChart );
            _parentNode.addChild( _waveFunctionChart );
            _parentNode.addChild( _probabilityDensityChart );
            _parentNode.addChild( _positionTitle );
            
            _canvas.addScreenChild( _parentNode );
        }        
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new QTControlPanel( this );
        setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------
        
        layoutCanvas();
        reset();
    }

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Lays out nodes on the canvas.
     * This is called whenever the canvas size changes.
     */
    private void layoutCanvas() {
        
        // Height of the legend along the top edge
        double legendHeight = _legend.getFullBounds().getHeight();
        // Height of the title along the bottom edge
        double titleHeight = _positionTitle.getFullBounds().getHeight();
        
        // Location and dimensions of charts
        final double chartWidth = _canvas.getWidth() - ( 2 * X_MARGIN );
        final double chartHeight = ( _canvas.getHeight() - legendHeight - titleHeight - ( 2 * Y_MARGIN ) - ( 4 * Y_SPACING ) ) / 3;
        
        // Legend
        {
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( X_MARGIN + 100, Y_MARGIN );
            legendTransform.scale( LEGEND_SCALE, LEGEND_SCALE );
            legendTransform.translate( 0, 0 ); // upper left
            _legend.setTransform( legendTransform );
        }
        
        // Configure button
        {
            AffineTransform configureTransform = new AffineTransform();
            configureTransform.translate( X_MARGIN + chartWidth, ( Y_MARGIN + legendHeight + Y_SPACING ) / 2 );
            configureTransform.scale( CONFIGURE_BUTTON_SCALE, CONFIGURE_BUTTON_SCALE );
            configureTransform.translate( -_configureButton.getWidth(), -_configureButton.getHeight() / 2 ); // registration point = right center
            _configureButton.setTransform( configureTransform );
        }
        
        // Energy chart
        {    
            _energyChart.setSize( (int) chartWidth, (int) chartHeight );
            AffineTransform energyTransform = new AffineTransform();
            double graphY = Y_MARGIN + legendHeight + Y_SPACING;
            energyTransform.translate( X_MARGIN, graphY );
            energyTransform.translate( 0, 0 ); // registration point = upper left
            _energyChart.setTransform( energyTransform );
        }
        
        // Wave Function chart
        {
            _waveFunctionChart.setSize( (int) chartWidth, (int) chartHeight );
            AffineTransform waveFunctionTransform = new AffineTransform();
            double graphY = _energyChart.getFullBounds().getY() + chartHeight + Y_SPACING;
            waveFunctionTransform.translate( X_MARGIN, graphY );
            waveFunctionTransform.translate( 0, 0 ); // registration point = upper left
            _waveFunctionChart.setTransform( waveFunctionTransform );
        }
        
        // Probability Density chart
        {
            _probabilityDensityChart.setSize( (int) chartWidth, (int) chartHeight );
            AffineTransform probabilityDensityTransform = new AffineTransform();
            double graphY = _waveFunctionChart.getFullBounds().getY() + chartHeight + Y_SPACING;
            probabilityDensityTransform.translate( X_MARGIN, graphY );
            probabilityDensityTransform.translate( 0, 0 ); // registration point = upper left
            _probabilityDensityChart.setTransform( probabilityDensityTransform );
        }
        
        // Position x-axis label
        {
            AffineTransform positionTransform = new AffineTransform();
            double titleY = _probabilityDensityChart.getFullBounds().getY() + chartHeight + Y_SPACING;
            positionTransform.translate( X_MARGIN + ( chartWidth / 2 ), titleY );
            positionTransform.translate( -_positionTitle.getWidth() / 2, 0 ); // registration point = top center
            _positionTitle.setTransform( positionTransform );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the module to its initial state.
     */
    public void reset() {
        _controlPanel.reset();
    }
    
    //XXX hack, remove this!
    public boolean hasHelp() {
        return true;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends ComponentAdapter implements ActionListener {

        public void componentResized( ComponentEvent event ) {
            if ( event.getSource() == _canvas ) {
                layoutCanvas();
            }
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _configureButton.getComponent() ) {
                handleConfigureButton();
            }
        }
    }
    
    /*
     * When the "Configure Energy" button is pressed, open a dialog.
     */
    private void handleConfigureButton() {
        JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(), 
                "Under construction!", "Configure Energy", JOptionPane.PLAIN_MESSAGE );
    }
}
