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
    // Debugging
    //----------------------------------------------------------------------------
    
    // shows the extension distance, a straight line between head and tail
    private static final boolean SHOW_EXTENSION = true;
    
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
        
        // Draw the extension
        if ( _extensionPath != null ) {
            _extensionPath.reset();
            _extensionPath.moveTo( (float) viewTailPosition.getX(), (float) viewTailPosition.getY() );
            _extensionPath.lineTo( (float) viewHeadPosition.getX(), (float) viewHeadPosition.getY() );
            _extensionNode.setPathTo( _extensionPath );
        }
        
        // Draw the strand, from the tail to the head
        _strandPath.reset();
        //XXX
        _strandNode.setPathTo( _strandPath );
    }

}
