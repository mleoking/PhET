// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.DNAPivot;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

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
    
    private static final Color STRAND_STROKE_COLOR = Color.BLACK;
    private static final Stroke STRAND_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private OTModelViewTransform _modelViewTransform;
    
    private GeneralPath _strandPath;
    private PPath _strandNode;
    private DNAExtensionNode _extensionNode;
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
    public DNAStrandNode( DNAStrand dnaStrand, OTModelViewTransform modelViewTransform ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _dnaStrand = dnaStrand;
        _dnaStrand.addObserver( this );
        
        _modelViewTransform = modelViewTransform;

        // strand
        _strandPath = new GeneralPath();
        _strandNode = new PPath();
        _strandNode.setStroke( STRAND_STROKE );
        _strandNode.setStrokePaint( STRAND_STROKE_COLOR );
        addChild( _strandNode );

        // extension
        _extensionNode = new DNAExtensionNode();
        addChild( _extensionNode );

        // pivots
        _pivotsParentNode = new PhetPNode();
        addChild( _pivotsParentNode );

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
        if ( visible != _pivotsParentNode.getVisible() ) {
            _pivotsParentNode.setVisible( visible );
            update();
            firePropertyChange( -1, PROPERTY_PIVOTS_VISIBLE, null, null );
        }
    }
    
    public boolean isPivotsVisible() {
        return _pivotsParentNode.getVisible();
    }
    
    public void setExtensionVisible( boolean visible ) {
        if ( visible != _extensionNode.getVisible() ) {
            _extensionNode.setVisible( visible );
            update();
            firePropertyChange( -1, PROPERTY_EXTENSION_VISIBLE, null, null );
        }
    }
    
    public boolean isExtensionVisible() {
        return _extensionNode.getVisible();
    }
    
    public void setStrandColor( Color color ) {
        _strandNode.setStrokePaint( color );
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
        
        // pin position, in view coordinates
        double viewPinX = _modelViewTransform.modelToView( _dnaStrand.getPinX() );
        double viewPinY = _modelViewTransform.modelToView( _dnaStrand.getPinY() );
        
        // Draw the extension, straight line from pin to bead
        if ( isExtensionVisible() ) {
            double headExtension = _dnaStrand.getExtension();
            double viewBeadX = _modelViewTransform.modelToView( _dnaStrand.getBeadX() );
            double viewBeadY = _modelViewTransform.modelToView( _dnaStrand.getBeadY() );
            _extensionNode.update( headExtension, viewPinX, viewPinY, viewBeadX, viewBeadY );
        }

        // Draw the strand, from the pin to the bead
        _strandPath.reset();
        _pivotsParentNode.removeAllChildren();
        ArrayList pivots = _dnaStrand.getPivots();
        final int numberOfPivots = pivots.size();
        if ( numberOfPivots > 1 ) {

            DNAPivot pivot;
            double viewPivotX, viewPivotY;
            for ( int i = 0; i < numberOfPivots; i++ ) {

                pivot = (DNAPivot) pivots.get( i );

                // draw line segment from previous to current pivot point
                viewPivotX = _modelViewTransform.modelToView( pivot.getX() );
                viewPivotY = _modelViewTransform.modelToView( pivot.getY() );
                if ( i == 0 ) {
                    // tail
                    _strandPath.moveTo( (float) viewPivotX, (float) viewPivotY );
                }
                else {
                    _strandPath.lineTo( (float) viewPivotX, (float) viewPivotY );
                }

                // draw pivot point
                if ( isPivotsVisible() ) {
                    DNAPivotNode pivotNode = new DNAPivotNode( viewPivotX, viewPivotY );
                    _pivotsParentNode.addChild( pivotNode );
                }
            }
        }
        _strandNode.setPathTo( _strandPath );
    }
}
