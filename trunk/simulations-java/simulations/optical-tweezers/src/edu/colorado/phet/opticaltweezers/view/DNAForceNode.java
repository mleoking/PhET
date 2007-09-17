/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.colorado.phet.opticaltweezers.model.NewDNAStrand;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * DNAForceNode displays the force exerted on the bead by a DNA strand.
 * The bead is attached to the head of the DNA strand.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAForceNode extends AbstractForceNode implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private NewDNAStrand _dnaStrand;
    private ModelViewTransform _modelViewTransform;
    private Point2D _pModel; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAForceNode( Bead bead, NewDNAStrand dnaStrand, ModelViewTransform modelViewTransform, double modelReferenceMagnitude, double viewReferenceLength ) {
        super( modelReferenceMagnitude, viewReferenceLength, OTResources.getString( "units.force" ), OTConstants.DNA_FORCE_COLOR );
        
        _bead = bead;
        _bead.addObserver( this );

        _dnaStrand = dnaStrand;
        _dnaStrand.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _pModel = new Point2D.Double();
        
        updatePosition();
        updateVectors();
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
        _dnaStrand.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            if ( visible ) {
                updatePosition();
                updateVectors();
            }
            super.setVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( isVisible() ) {
            if ( o == _bead ) {
                updatePosition();
                updateVectors();
            }
            else if ( o == _dnaStrand && arg == NewDNAStrand.PROPERTY_FORCE ) {
                updateVectors();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updatePosition() {
        _modelViewTransform.modelToView( _bead.getPositionReference(), _pModel );
        setOffset( _pModel );
    }
    
    private void updateVectors() {
        // calcuate the trap force at the bead's position
        Vector2D dnaForce = _bead.getDNAForce();
        // update the vector
        setForce( dnaForce );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Icon createIcon() {
        return AbstractForceNode.createIcon( OTConstants.DNA_FORCE_COLOR );
    }
}
