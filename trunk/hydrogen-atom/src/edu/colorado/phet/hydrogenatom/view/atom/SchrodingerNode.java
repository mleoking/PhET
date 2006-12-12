/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.piccolo.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * SchrodingerNode is the visual representation of the Schrodinger model of the hydrogen atom.
 * <p>
 * Coordinate system orientation: x horizontal, z vertical, y depth
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SchrodingerNode extends AbstractHydrogenAtomNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // 3D grid size
    private static final int NUMBER_OF_HORIZONTAL_CELLS = 10;
    private static final int NUMBER_OF_VERTICAL_CELLS = 10;
    private static final int NUMBER_OF_DEPTH_CELLS = 10;
    
    // 3D cell size
    private final static double CELL_WIDTH = HAConstants.ANIMATION_BOX_SIZE.getWidth() / NUMBER_OF_HORIZONTAL_CELLS;
    private final static double CELL_HEIGHT = HAConstants.ANIMATION_BOX_SIZE.getHeight() / NUMBER_OF_VERTICAL_CELLS;
    private final static double CELL_DEPTH = HAConstants.ANIMATION_BOX_SIZE.getHeight() / NUMBER_OF_DEPTH_CELLS;
    
    // colors
    private static final Color MAX_COLOR = ElectronNode.getColor();
    private static final Color MIN_COLOR = Color.BLACK;
        
    // margin between axes and animation box
    private static final double AXES_MARGIN = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SchrodingerModel _atom;
    
    private PPath[][] _cellNodes;
    private double[][] _waveFunctionSums;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param atom
     */
    public SchrodingerNode( SchrodingerModel atom ) {
        super();
        
        _atom = atom;
        _atom.addObserver( this );
        
        // axes, positioned at lower left
        {
            Axes2DNode axesNode = new Axes2DNode( "x", "z" );
            addChild( axesNode );
            axesNode.setPickable( false );
            double xOffset = -( HAConstants.ANIMATION_BOX_SIZE.getWidth() / 2 ) + AXES_MARGIN;
            double yOffset = ( HAConstants.ANIMATION_BOX_SIZE.getHeight() / 2 ) - axesNode.getHeight() - AXES_MARGIN;
            axesNode.setOffset( xOffset, yOffset );
        }
        
        // Create a 2D grid (in row major order) that covers the animation box
        PNode gridParentNode = new PNode();
        gridParentNode.setPickable( false );
        gridParentNode.setChildrenPickable( false );
        addChild( gridParentNode );
        {
            _cellNodes = new PPath[NUMBER_OF_HORIZONTAL_CELLS][NUMBER_OF_VERTICAL_CELLS];
            _waveFunctionSums = new double[ NUMBER_OF_VERTICAL_CELLS ][NUMBER_OF_HORIZONTAL_CELLS ];
            
            double cellX = 0;
            double cellY = 0;
            for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
                cellY = row * CELL_HEIGHT;
                for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                    cellX = column * CELL_WIDTH;
                    PPath gridNode = new PPath();
                    gridNode.setPickable( false );
                    gridNode.setPathTo( new Rectangle2D.Double( cellX, cellY, CELL_WIDTH, CELL_HEIGHT ) );
                    gridNode.setStroke( null );
                    _cellNodes[row][column] = gridNode;
                    gridParentNode.addChild( gridNode );
                }
            }
        }
        
        setOffset( ModelViewTransform.transform( _atom.getPosition() ) );
        
        update( _atom, AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE );
    }
    
    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------
    
    public void handleStateChange() {
        
        System.out.println( "SchrodingerNode.handleStateChange" );//XXX
        
        /* 
         * Compute the wave function for each cell in the 3D grid
         * Sum the wave function values over y (depth).
         */
        double maxSum = 0;
        for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
            double z = ( ( row * CELL_HEIGHT ) + ( CELL_HEIGHT / 2 ) ) - ( CELL_HEIGHT * NUMBER_OF_VERTICAL_CELLS / 2 ); // coordinate at center of cell
            for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                double x = ( ( row * CELL_WIDTH ) + ( CELL_WIDTH / 2 ) ) - ( CELL_WIDTH * NUMBER_OF_HORIZONTAL_CELLS / 2 ); // coordinate at center of cell
                double sum = 0;
                for ( int depth = 0; depth < NUMBER_OF_DEPTH_CELLS; depth++ ) {
                    double y = ( depth * CELL_DEPTH + ( CELL_DEPTH / 2 ) ) - ( CELL_HEIGHT * NUMBER_OF_DEPTH_CELLS / 2 ); // coordinate at center of cell
                    double w = _atom.getProbabilityDensity( x, y, z );
                    sum += w;
                }
                _waveFunctionSums[ row ][ column ] = sum;
                if ( sum > maxSum ) {
                    maxSum = sum;
                }
            }
        }
        System.out.println( "SchrodingerNode maxSum=" + maxSum );//XXX
        
        /*
         * Normalize the wave function sum for each cell, 
         * map the normalized value to a color,
         * set the color of 2D node that corresponds to the cell.
         */ 
        for ( int row = 0; row < NUMBER_OF_VERTICAL_CELLS; row++ ) {
            for ( int column = 0; column < NUMBER_OF_HORIZONTAL_CELLS; column++ ) {
                double brightness = _waveFunctionSums[ row ][ column ] / maxSum;
                System.out.println( "row=" + row + " col=" + column + " sum=" + _waveFunctionSums[ row ][ column ] + " brightness=" + brightness );//XXX
                Color color = ColorUtils.interpolateRBGA( MIN_COLOR, MAX_COLOR, brightness );
                _cellNodes[ row ][ column ].setPaint( color );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                handleStateChange();
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_IONIZED ) {
                //XXX
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Axes node
    //----------------------------------------------------------------------------
    
    private static class Axes2DNode extends PNode {
        
        private static final double AXIS_LENGTH = 100;
        private static final Color AXIS_COLOR = Color.WHITE;
        
        private static final double ARROW_HEAD_HEIGHT = 10;
        private static final double ARROW_HEAD_WIDTH = 10;
        private static final double ARROW_TAIL_WIDTH = 1;
        
        private static final Font LABEL_FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
        private static final Color LABEL_COLOR = Color.WHITE;
        private static final double LABEL_SPACING = 5;
        
        public Axes2DNode( String hLabel, String vLabel ) {
            super();
            
            Point2D origin = new Point2D.Double( 0, 0 );
            
            Point2D hAxisTipPoint = new Point2D.Double( AXIS_LENGTH, 0 );
            ArrowNode hAxis = new ArrowNode( origin, hAxisTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            hAxis.setPaint( AXIS_COLOR );
            hAxis.setStroke( null );
            
            Point2D vAxisTipPoint = new Point2D.Double( 0, -AXIS_LENGTH );
            ArrowNode vAxis = new ArrowNode( origin, vAxisTipPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH);
            vAxis.setPaint( AXIS_COLOR );
            vAxis.setStroke( null );
            
            PText hText = new PText( hLabel );
            hText.setFont( LABEL_FONT );
            hText.setTextPaint( LABEL_COLOR );
            
            PText vText = new PText( vLabel );
            vText.setFont( LABEL_FONT );
            vText.setTextPaint( LABEL_COLOR );
            
            hText.setOffset( hAxis.getWidth() + LABEL_SPACING, -hText.getHeight() / 2 );
            vText.setOffset( -vText.getWidth() / 2, -( vAxis.getHeight() + LABEL_SPACING + vText.getHeight() ) );
            
            addChild( hAxis );
            addChild( vAxis );
            addChild( hText );
            addChild( vText );
        }
    }
}
