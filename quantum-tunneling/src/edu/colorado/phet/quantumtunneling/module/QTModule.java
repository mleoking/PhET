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
import javax.swing.JOptionPane;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.view.LegendItem;
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
    private PText _energyTitle;
    private PText _waveFunctionTitle;
    private PText _probabilityDensityTitle;
    private PText _positionTitle;
    private PPath _energyGraph;
    private PPath _waveFunctionGraph;
    private PPath _probabilityDensityGraph;
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

        // Graph titles and boundaries
        {            
            _energyTitle = new PText( SimStrings.get( "EnergyView.title" ) );
            _energyTitle.setFont( GRAPH_TITLE_FONT );
            _energyTitle.scale( TITLE_SCALE );

            _waveFunctionTitle = new PText( SimStrings.get( "WaveFunctionView.title") );
            _waveFunctionTitle.setFont( GRAPH_TITLE_FONT );
            _waveFunctionTitle.scale( TITLE_SCALE );
            
            _probabilityDensityTitle = new PText( SimStrings.get( "ProbabilityDensityView.title") );
            _probabilityDensityTitle.setFont( GRAPH_TITLE_FONT );
            _probabilityDensityTitle.scale( TITLE_SCALE );
            
            _titleHeight = Math.max( _energyTitle.getFullBounds().getHeight(), 
                    Math.max( _waveFunctionTitle.getHeight(), _probabilityDensityTitle.getHeight() ) );
            
            _positionTitle = new PText( SimStrings.get( "PositionAxis.title") );
            _positionTitle.setFont( GRAPH_TITLE_FONT );
 
            _energyGraph = new PPath();
 
            _waveFunctionGraph = new PPath();
            
            _probabilityDensityGraph = new PPath();
        }
        
        // Add all the nodes to one parent node.
        {
            _parentNode = new PNode();
            
            _parentNode.addChild( _configureButton );
            _parentNode.addChild( _legend );
            _parentNode.addChild( _energyTitle );
            _parentNode.addChild( _waveFunctionTitle );
            _parentNode.addChild( _probabilityDensityTitle );
            _parentNode.addChild( _positionTitle );
            _parentNode.addChild( _energyGraph );
            _parentNode.addChild( _waveFunctionGraph );
            _parentNode.addChild( _probabilityDensityGraph );
            
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
        
        double legendHeight = _legend.getFullBounds().getHeight();
        
        // Location and dimensions of graphs
        final double graphX = X_MARGIN + _titleHeight + X_SPACING;
        double graphY = 0;
        final double graphWidth = _canvas.getWidth() - graphX - X_MARGIN;
        final double graphHeight = ( _canvas.getHeight() - legendHeight - _titleHeight - ( 2 * Y_MARGIN ) - ( 4 * Y_SPACING ) ) / 3;
        
        // Configure button
        {
            AffineTransform configureTransform = new AffineTransform();
            configureTransform.translate( graphX + graphWidth, ( Y_MARGIN + legendHeight + Y_SPACING ) / 2 );
            configureTransform.scale( CONFIGURE_BUTTON_SCALE, CONFIGURE_BUTTON_SCALE );
            configureTransform.translate( -_configureButton.getWidth(), -_configureButton.getHeight() / 2 ); // right center
            _configureButton.setTransform( configureTransform );
        }
        
        // Legend
        {
            AffineTransform legendTransform = new AffineTransform();
            legendTransform.translate( graphX, Y_MARGIN );
            legendTransform.scale( LEGEND_SCALE, LEGEND_SCALE );
            legendTransform.translate( 0, 0 ); // upper left
            _legend.setTransform( legendTransform );
        }
        
        // Graphs
        {    
            // all the same size
            _energyGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
            _waveFunctionGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
            _probabilityDensityGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
            
            AffineTransform energyTransform = new AffineTransform();
            graphY = Y_MARGIN + legendHeight + Y_SPACING;
            energyTransform.translate( graphX, graphY );
            _energyGraph.setTransform( energyTransform );

            AffineTransform waveFunctionTransform = new AffineTransform();
            graphY = _energyGraph.getFullBounds().getY() + graphHeight + Y_SPACING;
            waveFunctionTransform.translate( graphX, graphY );
            _waveFunctionGraph.setTransform( waveFunctionTransform );
            
            AffineTransform probabilityDensityTransform = new AffineTransform();
            graphY = _waveFunctionGraph.getFullBounds().getY() + graphHeight + Y_SPACING;
            probabilityDensityTransform.translate( graphX, graphY );
            _probabilityDensityGraph.setTransform( probabilityDensityTransform );
        }
        
        // Titles
        {
            final double titleX = X_MARGIN;
            double titleY = 0;
            
            AffineTransform energyTransform = new AffineTransform();
            titleY = _energyGraph.getFullBounds().getY() + _energyGraph.getFullBounds().getHeight() / 2;
            energyTransform.translate( titleX, titleY );
            energyTransform.rotate( Math.toRadians( -90 ) );
            energyTransform.scale( TITLE_SCALE, TITLE_SCALE );
            energyTransform.translate( -_energyTitle.getWidth() / 2, 0 ); // top center
            _energyTitle.setTransform( energyTransform );

            AffineTransform waveFunctionTransform = new AffineTransform();
            titleY = _waveFunctionGraph.getFullBounds().getY() + _waveFunctionGraph.getFullBounds().getHeight() / 2;
            waveFunctionTransform.translate( titleX, titleY );
            waveFunctionTransform.rotate( Math.toRadians( -90 ) );
            waveFunctionTransform.scale( TITLE_SCALE, TITLE_SCALE );
            waveFunctionTransform.translate( -_waveFunctionTitle.getWidth() / 2, 0 ); // top center
            _waveFunctionTitle.setTransform( waveFunctionTransform );

            AffineTransform probabilityDensityTransform = new AffineTransform();
            titleY = _probabilityDensityGraph.getFullBounds().getY() + _probabilityDensityGraph.getFullBounds().getHeight() / 2;
            probabilityDensityTransform.translate( titleX, titleY );
            probabilityDensityTransform.rotate( Math.toRadians( -90 ) );
            probabilityDensityTransform.scale( TITLE_SCALE, TITLE_SCALE );
            probabilityDensityTransform.translate( -_probabilityDensityTitle.getWidth() / 2, 0 ); // top center
            _probabilityDensityTitle.setTransform( probabilityDensityTransform );
            
            AffineTransform positionTransform = new AffineTransform();
            titleY = _probabilityDensityGraph.getFullBounds().getY() + _probabilityDensityGraph.getFullBounds().getHeight() + Y_SPACING;
            positionTransform.translate( graphX + ( graphWidth / 2 ), titleY );
            positionTransform.scale( TITLE_SCALE, TITLE_SCALE );
            positionTransform.translate( -_positionTitle.getWidth() / 2, 0 ); // top center
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
                "under construction", "Configure Energy", JOptionPane.PLAIN_MESSAGE );
    }
}
