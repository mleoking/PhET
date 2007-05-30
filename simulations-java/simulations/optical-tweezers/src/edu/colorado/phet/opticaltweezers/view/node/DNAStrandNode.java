/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DNAStrandNode is the visual representation of a DNA strand.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrandNode extends PNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double TAIL_DIAMETER = 100; // nm
    private static final Color TAIL_FILL_COLOR = Color.GRAY;
    private static final Color TAIL_STROKE_COLOR = Color.BLACK;
    private static final Stroke TAIL_STROKE = new BasicStroke( 1f );
    
    private static final Color STRAND_STROKE_COLOR = Color.BLACK;
    private static final Stroke STRAND_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private ModelViewTransform _modelViewTransform;
    
    private GeneralPath _strandPath;
    private PPath _strandNode;
    private PNode _tailNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAStrandNode( DNAStrand dnaStrand, ModelViewTransform modelViewTransform ) {
        super();
        
        _dnaStrand = dnaStrand;
        _dnaStrand.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _strandPath = new GeneralPath();
        _strandNode = new PPath();
        _strandNode.setStroke( STRAND_STROKE );
        _strandNode.setStrokePaint( STRAND_STROKE_COLOR );
        addChild( _strandNode );
        
        double viewTailDiameter = _modelViewTransform.modelToView( TAIL_DIAMETER );
        _tailNode = new SphericalNode( viewTailDiameter, TAIL_FILL_COLOR, TAIL_STROKE, TAIL_STROKE_COLOR, false /* convertToImage */ );
        addChild( _tailNode );
        
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
        if ( o == _dnaStrand ) {
           update(); 
        }
    }
    
    /*
     * Updates the view to match the model.
     */
    private void update() {
        
        // Move the tail
        Point2D modelTailPosition = _dnaStrand.getTailPositionRef();
        Point2D viewTailPosition = _modelViewTransform.modelToView( modelTailPosition );
        _tailNode.setOffset( viewTailPosition );
        
        Point2D modelHeadPosition = _dnaStrand.getHeadPositionRef();
        Point2D viewHeadPosition = _modelViewTransform.modelToView( modelHeadPosition );
        
        // Draw the strand, from the tail to the head
        _strandPath.reset();
        _strandPath.moveTo( (float)viewTailPosition.getX(), (float)viewTailPosition.getY() );
        _strandPath.lineTo( (float)viewHeadPosition.getX(), (float )viewHeadPosition.getY() );
        _strandNode.setPathTo( _strandPath );
    }

}
