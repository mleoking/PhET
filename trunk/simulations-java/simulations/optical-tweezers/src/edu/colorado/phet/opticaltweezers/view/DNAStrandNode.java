/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.model.DNAPivot;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * DNAStrandNode is the visual representation of a DNA strand.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrandNode extends PNode implements Observer {

    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // shows the extension distance, a straight line between head and tail
    private static final boolean SHOW_EXTENSION = true;
    
    // shows the pivot points
    private static final boolean SHOW_PIVOTS = true;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double TAIL_DIAMETER = 100; // nm
    private static final Color TAIL_FILL_COLOR = Color.GRAY;
    private static final Color TAIL_STROKE_COLOR = Color.BLACK;
    private static final Stroke TAIL_STROKE = new BasicStroke( 1f );
    
    private static final Color EXTENSION_STROKE_COLOR = Color.BLACK;
    private static final Stroke EXTENSION_STROKE = 
        new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,6}, 0 ); // dashed
    
    private static final Color STRAND_STROKE_COLOR = Color.BLACK;
    private static final Stroke STRAND_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private ModelViewTransform _modelViewTransform;
    
    private GeneralPath _strandPath;
    private PPath _strandNode;
    private GeneralPath _extensionPath;
    private PPath _extensionNode; // straight line between head and tail
    private PNode _tailNode;
    private PNode _pivotsParentNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param dnaStrand
     * @param modelViewTransform
     */
    public DNAStrandNode( DNAStrand dnaStrand, ModelViewTransform modelViewTransform ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _dnaStrand = dnaStrand;
        _dnaStrand.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _strandPath = new GeneralPath();
        _strandNode = new PPath();
        _strandNode.setStroke( STRAND_STROKE );
        _strandNode.setStrokePaint( STRAND_STROKE_COLOR );
        addChild( _strandNode );
        
        if ( SHOW_EXTENSION ) {
            _extensionPath = new GeneralPath();
            _extensionNode = new PPath();
            _extensionNode.setStroke( EXTENSION_STROKE );
            _extensionNode.setStrokePaint( EXTENSION_STROKE_COLOR );
            addChild( _extensionNode );
        }
        
        double viewTailDiameter = _modelViewTransform.modelToView( TAIL_DIAMETER );
        _tailNode = new SphericalNode( viewTailDiameter, TAIL_FILL_COLOR, TAIL_STROKE, TAIL_STROKE_COLOR, false /* convertToImage */ );
        addChild( _tailNode );
        
        if ( SHOW_PIVOTS ) {
            _pivotsParentNode = new PComposite();
            addChild( _pivotsParentNode );
        }
        
        update();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _dnaStrand.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _dnaStrand && arg == DNAStrand.PROPERTY_SHAPE ) {
           update();
        }
    }
    
    /*
     * Updates the view to match the model.
     */
    private void update() {
        
        // Move the tail
        double viewTailX = _modelViewTransform.modelToView( _dnaStrand.getTailX() );
        double viewTailY = _modelViewTransform.modelToView( _dnaStrand.getTailY() );
        _tailNode.setOffset( viewTailX, viewTailY );
        
        // Draw the extension, a straight line from tail to head
        if ( _extensionPath != null ) {
            double viewHeadX = _modelViewTransform.modelToView( _dnaStrand.getHeadX() );
            double viewHeadY = _modelViewTransform.modelToView( _dnaStrand.getHeadY() );
            _extensionPath.reset();
            _extensionPath.moveTo( (float) viewTailX, (float) viewTailY );
            _extensionPath.lineTo( (float) viewHeadX, (float) viewHeadY );
            _extensionNode.setPathTo( _extensionPath );
        }
        
        if ( _pivotsParentNode != null ) {
            _pivotsParentNode.removeAllChildren();
        }
        
        // Draw the strand, from the tail to the head
        _strandPath.reset();
        DNAPivot[] pivots = _dnaStrand.getPivots();
        double viewX, viewY;
        for ( int i = 0; i < pivots.length; i++ ) {
            
            // draw line segment from previous to current pivot point
            viewX = _modelViewTransform.modelToView( pivots[i].getX() );
            viewY = _modelViewTransform.modelToView( pivots[i].getY() );
            if ( i == 0 ) {
                // tail
                _strandPath.moveTo( (float) viewX, (float) viewY );
            }
            else {
                _strandPath.lineTo( (float) viewX, (float) viewY );
            }
            
            // draw pivot point
            if ( _pivotsParentNode != null ) {
                DNAPivotNode pivotNode = new DNAPivotNode( viewX, viewY );
                _pivotsParentNode.addChild( pivotNode );
            }
        }
        _strandNode.setPathTo( _strandPath );
    }
}
