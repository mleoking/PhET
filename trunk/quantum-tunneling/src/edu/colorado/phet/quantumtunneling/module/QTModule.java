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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.control.QTControlPanel;
import edu.colorado.phet.quantumtunneling.view.QTCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;


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
    
    // All of these values are in local coordinates
    private static final int X_MARGIN = 20; // space at left & right edges of canvas
    private static final int Y_MARGIN = 20; // space at top & bottom edges of canvas
    private static final int X_SPACING = 10; // horizontal space between nodes
    private static final int Y_SPACING = 10; // vertical space between nodes
    private static final Dimension CANVAS_SIZE = new Dimension( 1000, 1000 );
    private static final int CANVAS_BOUNDARY_STROKE_WIDTH = 1;
    private static final double TITLE_SCALE = 2.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTCanvas _canvas;
    private PText _energyTitle;
    private PText _waveFunctionTitle;
    private PText _probabilityDensityTitle;
    private PText _positionTitle;
    private PPath _energyGraph;
    private PPath _waveFunctionGraph;
    private PPath _probabilityDensityGraph;
    private PPath _canvasBoundary;
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

        // Canvas (aka "the play area")
        _canvas = new QTCanvas( CANVAS_SIZE );
        _canvas.removeInputEventListener( _canvas.getZoomEventHandler() ); // disable zoom
        _canvas.removeInputEventListener( _canvas.getPanEventHandler() ); // disable pan
        setCanvas( _canvas );
        
        // Graph titles and boundaries
        {            
            _energyTitle = new PText( SimStrings.get( "EnergyView.title" ) );
            _energyTitle.setFont( GRAPH_TITLE_FONT );
            _canvas.addNode( _energyTitle );

            _waveFunctionTitle = new PText( SimStrings.get( "WaveFunctionView.title") );
            _waveFunctionTitle.setFont( GRAPH_TITLE_FONT );
            _canvas.addNode( _waveFunctionTitle );
            
            _probabilityDensityTitle = new PText( SimStrings.get( "ProbabilityDensityView.title") );
            _probabilityDensityTitle.setFont( GRAPH_TITLE_FONT );
            _canvas.addNode( _probabilityDensityTitle );
            
            _positionTitle = new PText( SimStrings.get( "PositionAxis.title") );
            _positionTitle.setFont( GRAPH_TITLE_FONT );
            _canvas.addNode( _positionTitle );
            
            _titleHeight = TITLE_SCALE * Math.max( _energyTitle.getFullBounds().getHeight(), 
                    Math.max( _waveFunctionTitle.getHeight(), _probabilityDensityTitle.getHeight() ) );
            
            _energyGraph = new PPath();
            _canvas.addNode( _energyGraph );
            
            _waveFunctionGraph = new PPath();
            _canvas.addNode( _waveFunctionGraph );
            
            _probabilityDensityGraph = new PPath();
            _canvas.addNode( _probabilityDensityGraph );
            
            _canvasBoundary = new PPath();
            _canvasBoundary.setStroke( new BasicStroke( CANVAS_BOUNDARY_STROKE_WIDTH ) );
            _canvasBoundary.setStrokePaint( Color.RED );
            _canvas.addNode( _canvasBoundary );
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
        
        // Dimensions of graphs
        double graphWidth = canvasSize.width - _titleHeight - ( 2 * X_MARGIN ) - X_SPACING;
        double graphHeight = ( canvasSize.height - _titleHeight - ( 2 * Y_MARGIN ) - ( 3 * Y_SPACING ) ) / 3;
        
        // Titles
        {
            PAffineTransform energyTransform = new PAffineTransform();
            energyTransform.translate( X_MARGIN, Y_MARGIN + ( graphHeight / 2 ) );
            energyTransform.rotate( Math.toRadians( -90 ) );
            energyTransform.scale( TITLE_SCALE, TITLE_SCALE );
            energyTransform.translate( -_energyTitle.getWidth() / 2, 0 ); // top center
            _energyTitle.setTransform( energyTransform );

            PAffineTransform waveFunctionTransform = new PAffineTransform();
            waveFunctionTransform.translate( X_MARGIN, Y_MARGIN + graphHeight + Y_SPACING + ( graphHeight / 2 ) );
            waveFunctionTransform.rotate( Math.toRadians( -90 ) );
            waveFunctionTransform.scale( TITLE_SCALE, TITLE_SCALE );
            waveFunctionTransform.translate( -_waveFunctionTitle.getWidth() / 2, 0 ); // top center
            _waveFunctionTitle.setTransform( waveFunctionTransform );

            PAffineTransform probabilityDensityTransform = new PAffineTransform();
            probabilityDensityTransform.translate( X_MARGIN, Y_MARGIN + ( 2 * graphHeight ) + ( 2 * Y_SPACING ) + ( graphHeight / 2 ) );
            probabilityDensityTransform.rotate( Math.toRadians( -90 ) );
            probabilityDensityTransform.scale( TITLE_SCALE, TITLE_SCALE );
            probabilityDensityTransform.translate( -_probabilityDensityTitle.getWidth() / 2, 0 ); // top center
            _probabilityDensityTitle.setTransform( probabilityDensityTransform );
            
            PAffineTransform positionTransform = new PAffineTransform();
            positionTransform.translate( X_MARGIN + _titleHeight + X_SPACING + ( graphWidth / 2 ), Y_MARGIN + ( 3 * Y_SPACING ) + ( 3 * graphHeight ) );
            positionTransform.scale( TITLE_SCALE, TITLE_SCALE );
            positionTransform.translate( -_positionTitle.getWidth() / 2, 0 ); // top center
            _positionTitle.setTransform( positionTransform );
        }
        
        // Graphs
        {
            PAffineTransform energyTransform = new PAffineTransform();
            energyTransform.translate( X_MARGIN + _titleHeight + X_SPACING, Y_MARGIN );
            _energyGraph.setTransform( energyTransform );
            _energyGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );

            PAffineTransform waveFunctionTransform = new PAffineTransform();
            waveFunctionTransform.translate( X_MARGIN + _titleHeight + X_SPACING, Y_MARGIN + graphHeight + Y_SPACING );
            _waveFunctionGraph.setTransform( waveFunctionTransform );
            _waveFunctionGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
            
            PAffineTransform probabilityDensityTransform = new PAffineTransform();
            probabilityDensityTransform.translate( X_MARGIN + _titleHeight + X_SPACING, Y_MARGIN + graphHeight + Y_SPACING + graphHeight + Y_SPACING );
            _probabilityDensityGraph.setTransform( probabilityDensityTransform );
            _probabilityDensityGraph.setPathToRectangle( 0, 0, (int) graphWidth, (int) graphHeight );
        }
        
        // Canvas boundary
        int w = CANVAS_BOUNDARY_STROKE_WIDTH;
        _canvasBoundary.setPathToRectangle( w, w, canvasSize.width - w, canvasSize.height - w );
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
}
