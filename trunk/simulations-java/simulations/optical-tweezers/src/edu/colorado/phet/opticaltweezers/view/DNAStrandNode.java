/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.DNAPivot;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * DNAStrandNode is the visual representation of a DNA strand.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrandNode extends PNode implements Observer {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_PIVOTS_VISIBLE = "pivotsVisible";
    public static final String PROPERTY_EXTENSION_VISIBLE = "extensionVisible";
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
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
    private PNode _pivotsParentNode;
    private PImage _pushpinNode;
    
    private boolean _pivotsVisible;
    private boolean _extensionVisible;
    
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

        _extensionPath = new GeneralPath();
        _extensionNode = new PPath();
        _extensionNode.setStroke( EXTENSION_STROKE );
        _extensionNode.setStrokePaint( EXTENSION_STROKE_COLOR );
        addChild( _extensionNode );

        _pivotsParentNode = new PComposite();
        addChild( _pivotsParentNode );

        _pushpinNode = new PImage( OTResources.getImage( OTConstants.IMAGE_PUSHPIN ) );
        addChild( _pushpinNode );
        
        _pivotsVisible = false;
        _extensionVisible = false;
        
        update();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _dnaStrand.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPivotsVisible( boolean visible ) {
        if ( visible != _pivotsVisible ) {
            _pivotsVisible = visible;
            update();
            firePropertyChange( -1, PROPERTY_PIVOTS_VISIBLE, null, null );
        }
    }
    
    public boolean isPivotsVisible() {
        return _pivotsVisible;
    }
    
    public void setExtensionVisible( boolean visible ) {
        if ( visible != _extensionVisible ) {
            _extensionVisible = visible;
            update();
            firePropertyChange( -1, PROPERTY_EXTENSION_VISIBLE, null, null );
        }
    }
    
    public boolean isExtensionVisible() {
        return _extensionVisible;
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
        
        // tail position, in view coordinates
        double viewTailX = _modelViewTransform.modelToView( _dnaStrand.getTailX() );
        double viewTailY = _modelViewTransform.modelToView( _dnaStrand.getTailY() );
        
        // pushpin at the tail
        double xOffset = viewTailX - _pushpinNode.getFullBounds().getWidth();
        double yOffset = viewTailY - _pushpinNode.getFullBounds().getHeight();
        _pushpinNode.setOffset( xOffset, yOffset );
        
        // Draw the extension, a straight line from tail to head
        _extensionPath.reset();
        if ( _extensionVisible ) {
            double viewHeadX = _modelViewTransform.modelToView( _dnaStrand.getHeadX() );
            double viewHeadY = _modelViewTransform.modelToView( _dnaStrand.getHeadY() );
            _extensionPath.moveTo( (float) viewTailX, (float) viewTailY );
            _extensionPath.lineTo( (float) viewHeadX, (float) viewHeadY );
        }
        _extensionNode.setPathTo( _extensionPath );
        
        _pivotsParentNode.removeAllChildren();

        // Draw the strand, from the tail to the head
        _strandPath.reset();
        DNAPivot[] pivots = _dnaStrand.getPivots();
        double viewPivotX, viewPivotY;
        for ( int i = 0; i < pivots.length; i++ ) {
            
            // draw line segment from previous to current pivot point
            viewPivotX = _modelViewTransform.modelToView( pivots[i].getX() );
            viewPivotY = _modelViewTransform.modelToView( pivots[i].getY() );
            if ( i == 0 ) {
                // tail
                _strandPath.moveTo( (float) viewPivotX, (float) viewPivotY );
            }
            else {
                _strandPath.lineTo( (float) viewPivotX, (float) viewPivotY );
            }
            
            // draw pivot point
            if ( _pivotsVisible ) {
                DNAPivotNode pivotNode = new DNAPivotNode( viewPivotX, viewPivotY );
                _pivotsParentNode.addChild( pivotNode );
            }
        }
        _strandNode.setPathTo( _strandPath );
    }
}
