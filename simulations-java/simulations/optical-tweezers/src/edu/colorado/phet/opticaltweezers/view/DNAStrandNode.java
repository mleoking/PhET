/* Copyright 2007, University of Colorado */

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
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;
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
    public static final String PROPERTY_EXTENSIONS_VISIBLE = "extensionsVisible";
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color STRAND_STROKE_COLOR = Color.BLACK;
    private static final Stroke STRAND_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DNAStrand _dnaStrand;
    private ModelViewTransform _modelViewTransform;
    
    private GeneralPath _headStrandPath, _tailStrandPath;
    private PPath _headStrandNode, _tailStrandNode;
    private DNAExtensionNode _headExtensionNode, _tailExtensionNode;
    
    private PNode _strandParentNode;
    private PNode _extensionsParentNode;
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

        // strand
        {
            _strandParentNode = new PhetPNode();
            addChild( _strandParentNode );
            
            _headStrandPath = new GeneralPath();
            _headStrandNode = new PPath();
            _headStrandNode.setStroke( STRAND_STROKE );
            _headStrandNode.setStrokePaint( STRAND_STROKE_COLOR );
            _strandParentNode.addChild( _headStrandNode );

            _tailStrandPath = new GeneralPath();
            _tailStrandNode = new PPath();
            _tailStrandNode.setStroke( STRAND_STROKE );
            _tailStrandNode.setStrokePaint( STRAND_STROKE_COLOR );
            _strandParentNode.addChild( _tailStrandNode );
        }
        
        // extension
        {
            _extensionsParentNode = new PhetPNode();
            addChild( _extensionsParentNode );
            
            _headExtensionNode = new DNAExtensionNode();
            _extensionsParentNode.addChild( _headExtensionNode );
            
            _tailExtensionNode = new DNAExtensionNode();
            _extensionsParentNode.addChild( _tailExtensionNode );
        }

        // pivots (debug)
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
    
    public void setExtensionsVisible( boolean visible ) {
        if ( visible != _extensionsParentNode.getVisible() ) {
            _extensionsParentNode.setVisible( visible );
            update();
            firePropertyChange( -1, PROPERTY_EXTENSIONS_VISIBLE, null, null );
        }
    }
    
    public boolean isExtensionsVisible() {
        return _extensionsParentNode.getVisible();
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
        double viewPinX = _modelViewTransform.modelToView( _dnaStrand.getPinX() );
        double viewPinY = _modelViewTransform.modelToView( _dnaStrand.getPinY() );
        
        // Draw the extensions
        if ( isExtensionsVisible() ) {
            
            double headExtension = _dnaStrand.getHeadExtension();
            if ( headExtension > 0 ) {
                double viewHeadX = _modelViewTransform.modelToView( _dnaStrand.getHeadX() );
                double viewHeadY = _modelViewTransform.modelToView( _dnaStrand.getHeadY() );
                _headExtensionNode.update( headExtension, viewPinX, viewPinY, viewHeadX, viewHeadY );
            }
            
            double tailExtension = _dnaStrand.getTailExtension();
            if ( tailExtension > 0 ) {
                double viewTailX = _modelViewTransform.modelToView( _dnaStrand.getTailX() );
                double viewTailY = _modelViewTransform.modelToView( _dnaStrand.getTailY() );
                _tailExtensionNode.update( tailExtension, viewPinX, viewPinY, viewTailX, viewTailY );
            }
        }
        
        _pivotsParentNode.removeAllChildren();
        
        // Draw the strand, from the pin to the head
        {
            _headStrandPath.reset();
            
            ArrayList pivots = _dnaStrand.getHeadPivots();
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
                        _headStrandPath.moveTo( (float) viewPivotX, (float) viewPivotY );
                    }
                    else {
                        _headStrandPath.lineTo( (float) viewPivotX, (float) viewPivotY );
                    }

                    // draw pivot point
                    if ( isPivotsVisible() ) {
                        DNAPivotNode pivotNode = new DNAPivotNode( viewPivotX, viewPivotY );
                        _pivotsParentNode.addChild( pivotNode );
                    }
                }
            }
            _headStrandNode.setPathTo( _headStrandPath );
        }
        
        // Draw the strand, from the pin to the tail
        {
            _tailStrandPath.reset();
            
            ArrayList pivots = _dnaStrand.getTailPivots();
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
                        _tailStrandPath.moveTo( (float) viewPivotX, (float) viewPivotY );
                    }
                    else {
                        _tailStrandPath.lineTo( (float) viewPivotX, (float) viewPivotY );
                    }

                    // draw pivot point
                    if ( isPivotsVisible() ) {
                        DNAPivotNode pivotNode = new DNAPivotNode( viewPivotX, viewPivotY );
                        _pivotsParentNode.addChild( pivotNode );
                    }
                }
            }
            _tailStrandNode.setPathTo( _tailStrandPath );
        }
    }
}
