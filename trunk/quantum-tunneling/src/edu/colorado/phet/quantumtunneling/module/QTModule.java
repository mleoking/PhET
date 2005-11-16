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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.view.LegendItem;
import edu.colorado.phet.quantumtunneling.view.QTCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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
    
    private static final Font GRAPH_TITLE_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Font LEGEND_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 20; // space at left & right edges of canvas
    private static final int Y_MARGIN = 20; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_SIZE = new Dimension( 1000, 1000 );
    private static final int CANVAS_BOUNDARY_STROKE_WIDTH = 1;
    private static final double TITLE_SCALE = 2.0;
    private static final double LEGEND_SCALE = 1.8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTCanvas _canvas;
    private PNode _parentNode;
    private PNode _legend;
    private PText _energyTitle;
    private PText _waveFunctionTitle;
    private PText _probabilityDensityTitle;
    private PText _positionTitle;
    private PPath _energyGraph;
    private PPath _waveFunctionGraph;
    private PPath _probabilityDensityGraph;
    private PPath _canvasBoundary;
    private PSwing _configureButton;
    private double _titleHeight;
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
        
        // Canvas (aka "the play area")
        _canvas = new QTCanvas( CANVAS_SIZE );
        _canvas.addComponentListener( listener );
        setCanvas( _canvas );
        
        // Parent for all other nodes.
        _parentNode = new PNode();
        _canvas.addWorldChild( _parentNode );
        
        // Configure button
        {
            JButton jButton = new JButton( "Configure..." );
            jButton.setOpaque( true );
            jButton.addActionListener( listener );
            _configureButton = new PSwing( _canvas, jButton );
            _parentNode.addChild( _configureButton );
        }
        
        // Energy graph legend
        { 
            LegendItem totalEnergyItem = 
                new LegendItem( "Total Energy", QTConstants.TOTAL_ENERGY_COLOR );
            
            LegendItem potentialEnergyItem = 
                new LegendItem( "Potential Energy", QTConstants.POTENTIAL_ENERGY_COLOR );
            potentialEnergyItem.translate( totalEnergyItem.getFullBounds().getWidth() + 20, 0 );

            _legend = new PNode();
            _legend.addChild( totalEnergyItem );
            _legend.addChild( potentialEnergyItem );
            _parentNode.addChild( _legend );
        }

        // Graph titles and boundaries
        {            
            _energyTitle = new PText( SimStrings.get( "EnergyView.title" ) );
            _energyTitle.setFont( GRAPH_TITLE_FONT );
            _parentNode.addChild( _energyTitle );

            _waveFunctionTitle = new PText( SimStrings.get( "WaveFunctionView.title") );
            _waveFunctionTitle.setFont( GRAPH_TITLE_FONT );
            _parentNode.addChild( _waveFunctionTitle );
            
            _probabilityDensityTitle = new PText( SimStrings.get( "ProbabilityDensityView.title") );
            _probabilityDensityTitle.setFont( GRAPH_TITLE_FONT );
            _parentNode.addChild( _probabilityDensityTitle );
            
            _positionTitle = new PText( SimStrings.get( "PositionAxis.title") );
            _positionTitle.setFont( GRAPH_TITLE_FONT );
            _parentNode.addChild( _positionTitle );
            
            _titleHeight = TITLE_SCALE * Math.max( _energyTitle.getFullBounds().getHeight(), 
                    Math.max( _waveFunctionTitle.getHeight(), _probabilityDensityTitle.getHeight() ) );
            
            _energyGraph = new PPath();
            _parentNode.addChild( _energyGraph );
            
            _waveFunctionGraph = new PPath();
            _parentNode.addChild( _waveFunctionGraph );
            
            _probabilityDensityGraph = new PPath();
            _parentNode.addChild( _probabilityDensityGraph );
            
            _canvasBoundary = new PPath();
            _canvasBoundary.setStroke( new BasicStroke( CANVAS_BOUNDARY_STROKE_WIDTH ) );
            _canvasBoundary.setStrokePaint( Color.RED );
            _parentNode.addChild( _canvasBoundary );
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
        
        layoutCanvas( CANVAS_SIZE );
        reset();
    }

    /*
     * Lays out nodes on the canvas.
     */
    private void layoutCanvas( Dimension canvasSize ) {
        
        double legendHeight = LEGEND_SCALE * _legend.getFullBounds().getHeight();
        
        // Dimensions of graphs
        double graphWidth = canvasSize.width - _titleHeight - ( 2 * X_MARGIN ) - X_SPACING;
        double graphHeight = ( canvasSize.height - legendHeight - _titleHeight - ( 2 * Y_MARGIN ) - ( 4 * Y_SPACING ) ) / 3;
        
        // Configure button
        {
            AffineTransform configureTransform = new AffineTransform();
            configureTransform.translate( X_MARGIN + _titleHeight + X_SPACING, ( Y_MARGIN + legendHeight + Y_SPACING ) / 2 );
            configureTransform.scale( 1.2, 1.2 );
            configureTransform.translate( 0, -_configureButton.getHeight() / 2 ); // left center
            _configureButton.setTransform( configureTransform );
        }
        
        // Legend
        {
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( X_MARGIN + _titleHeight + X_SPACING + graphWidth, Y_MARGIN );
            legendTransform.scale( LEGEND_SCALE, LEGEND_SCALE );
            legendTransform.translate( -_legend.getFullBounds().getWidth(), 0 ); // top right
            _legend.setTransform( legendTransform );
        }
        
        // Titles
        {
            AffineTransform energyTransform = new AffineTransform();
            energyTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + Y_SPACING + ( graphHeight / 2 ) );
            energyTransform.rotate( Math.toRadians( -90 ) );
            energyTransform.scale( TITLE_SCALE, TITLE_SCALE );
            energyTransform.translate( -_energyTitle.getWidth() / 2, 0 ); // top center
            _energyTitle.setTransform( energyTransform );

            AffineTransform waveFunctionTransform = new AffineTransform();
            waveFunctionTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + graphHeight + ( 2 * Y_SPACING ) + ( graphHeight / 2 ) );
            waveFunctionTransform.rotate( Math.toRadians( -90 ) );
            waveFunctionTransform.scale( TITLE_SCALE, TITLE_SCALE );
            waveFunctionTransform.translate( -_waveFunctionTitle.getWidth() / 2, 0 ); // top center
            _waveFunctionTitle.setTransform( waveFunctionTransform );

            AffineTransform probabilityDensityTransform = new AffineTransform();
            probabilityDensityTransform.translate( X_MARGIN, Y_MARGIN + legendHeight + ( 2 * graphHeight ) + ( 3 * Y_SPACING ) + ( graphHeight / 2 ) );
            probabilityDensityTransform.rotate( Math.toRadians( -90 ) );
            probabilityDensityTransform.scale( TITLE_SCALE, TITLE_SCALE );
            probabilityDensityTransform.translate( -_probabilityDensityTitle.getWidth() / 2, 0 ); // top center
            _probabilityDensityTitle.setTransform( probabilityDensityTransform );
            
            AffineTransform positionTransform = new AffineTransform();
            positionTransform.translate( X_MARGIN + _titleHeight + X_SPACING + ( graphWidth / 2 ), Y_MARGIN + legendHeight + ( 4 * Y_SPACING ) + ( 3 * graphHeight ) );
            positionTransform.scale( TITLE_SCALE, TITLE_SCALE );
            positionTransform.translate( -_positionTitle.getWidth() / 2, 0 ); // top center
            _positionTitle.setTransform( positionTransform );
        }
        
        // Graphs
        {
            AffineTransform energyTransform = new AffineTransform();
            energyTransform.translate( X_MARGIN + _titleHeight + X_SPACING, Y_MARGIN + legendHeight + Y_SPACING );
            _energyGraph.setTransform( energyTransform );
            _energyGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );

            AffineTransform waveFunctionTransform = new AffineTransform();
            waveFunctionTransform.translate( X_MARGIN + _titleHeight + X_SPACING, Y_MARGIN + legendHeight + graphHeight + ( 2 * Y_SPACING ) );
            _waveFunctionGraph.setTransform( waveFunctionTransform );
            _waveFunctionGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
            
            AffineTransform probabilityDensityTransform = new AffineTransform();
            probabilityDensityTransform.translate( X_MARGIN + _titleHeight + X_SPACING, Y_MARGIN + legendHeight + ( 3 * Y_SPACING ) + ( 2 * graphHeight ) );
            _probabilityDensityGraph.setTransform( probabilityDensityTransform );
            _probabilityDensityGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
        }
        
        // Canvas boundary
        int w = CANVAS_BOUNDARY_STROKE_WIDTH;
//        _canvasBoundary.setPathToRectangle( w, w, canvasSize.width - w, canvasSize.height - w );
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
                handleCanvasResized();
            }
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _configureButton.getComponent() ) {
                handleConfigureButton();
            }
        }
    }
    
    private void handleCanvasResized() {
        System.out.println( "canvas size = " + _canvas.getSize() );//XXX
    }
    
    private void handleConfigureButton() {
        System.out.println( "configure..." ); //XXX
    }
}
