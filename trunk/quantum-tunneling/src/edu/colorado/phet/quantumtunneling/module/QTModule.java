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

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.view.DrawableNode;
import edu.colorado.phet.quantumtunneling.view.LegendItem;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChart;
import edu.umd.cs.piccolo.PNode;


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
    
    private static final Font LEGEND_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 10; // space at left & right edges of canvas
    private static final int Y_MARGIN = 10; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 1000, 1000 );
    private static final int CANVAS_BOUNDARY_STROKE_WIDTH = 1;
    private static final double LEGEND_SCALE = 1.2;
    private static final double CONFIGURE_BUTTON_SCALE = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetPCanvas _canvas;
    private PNode _parentNode;
    private PNode _legend;
    private PSwing _configureButton;
    private DrawableNode _chartNode;
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
            _canvas.setBackground( QTConstants.CANVAS_BACKGROUND );
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
                new LegendItem( SimStrings.get( "legend.totalEnergy" ), QTConstants.TOTAL_ENERGY_PAINT );
            
            LegendItem potentialEnergyItem = 
                new LegendItem( SimStrings.get( "legend.potentialEnergy" ), QTConstants.POTENTIAL_ENERGY_PAINT );
            potentialEnergyItem.translate( totalEnergyItem.getFullBounds().getWidth() + 20, 0 );

            _legend = new PNode();
            _legend.scale( LEGEND_SCALE );
            _legend.addChild( totalEnergyItem );
            _legend.addChild( potentialEnergyItem );
        }
        
        // Combined chart
        {
            QTCombinedChart chart = new QTCombinedChart();
            _chartNode = new DrawableNode( chart );
        }
        
        // Add all the nodes to one parent node.
        {
            _parentNode = new PNode();
            
            _parentNode.addChild( _configureButton );
            _parentNode.addChild( _legend );
            _parentNode.addChild( _chartNode );

            _canvas.addScreenChild( _parentNode );
        }        
        
        //XXX add data
        {
            QTCombinedChart chart = (QTCombinedChart) _chartNode.getDrawable();
            
            XYSeries totalEnergySeries = chart.getTotalEnergySeries();
            totalEnergySeries.add( 0, 5 );
            totalEnergySeries.add( 20, 5 );
            
            XYSeries potentialEnergySeries = chart.getPotentialEnergySeries();
            potentialEnergySeries.add( 0, 0 );
            potentialEnergySeries.add( 8, 0 );
            potentialEnergySeries.add( 8, 1 );
            potentialEnergySeries.add( 20, 1 );
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
        
        // Location and dimensions of charts
        final double chartWidth = _canvas.getWidth() - ( 2 * X_MARGIN );
        final double chartHeight = _canvas.getHeight() - ( legendHeight  + ( 2 * Y_MARGIN ) + Y_SPACING );
        
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
        
        // Combined chart
        {
            _chartNode.setBounds( 0, 0, chartWidth, chartHeight );
            AffineTransform chartTransform = new AffineTransform();
            chartTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + Y_SPACING );
            chartTransform.translate( 0, 0 ); // registration point @ upper left
            _chartNode.setTransform( chartTransform );
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
