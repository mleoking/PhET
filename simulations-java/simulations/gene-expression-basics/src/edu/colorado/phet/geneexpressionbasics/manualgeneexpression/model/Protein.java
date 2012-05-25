// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentState;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.ProteinAttachmentStateMachine;

/**
 * Base class for proteins.  Defines the methods used for growing a protein.
 *
 * @author John Blanco
 */
public abstract class Protein extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Max value for the growth factor, indicates that it is fully grown.
    public static final double MAX_GROWTH_FACTOR = 1;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that gets set when this protein is fully formed and released.
    public final BooleanProperty fullGrown = new BooleanProperty( false );

    // A value between 0 and 1 that defines how fully developed, or "grown"
    // this protein is.
    private double fullSizeProportion = 0;

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    protected Protein( GeneExpressionModel model, Shape initialShape, Color baseColor ) {
        super( model, initialShape, baseColor );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Create the attachment state machine that will govern the way in which
     * this biomolecule attaches to and detaches from other biomolecules.
     *
     * @return
     */
    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new ProteinAttachmentStateMachine( this );
    }

    /**
     * Set the size of this protein by specifying the proportion of its full
     * size.
     *
     * @param fullSizeProportion - Value between 0 and 1 indicating the
     *                           proportion of this protein's full grown size that it should be.
     */
    public void setFullSizeProportion( double fullSizeProportion ) {
        if ( fullSizeProportion < 0 || fullSizeProportion > 1 ) {
            System.out.println( "Issue!" );
        }
        assert fullSizeProportion >= 0 && fullSizeProportion <= 1;
        if ( this.fullSizeProportion != fullSizeProportion ) {
            this.fullSizeProportion = fullSizeProportion;
            AffineTransform transform = AffineTransform.getTranslateInstance( getPosition().getX(), getPosition().getY() );
            Shape untranslatedShape = getUntranslatedShape( fullSizeProportion );
            shapeProperty.set( transform.createTransformedShape( untranslatedShape ) );
        }
    }

    public double getFullSizeProportion() {
        return fullSizeProportion;
    }

    public void grow( double growthAmount ) {
        assert growthAmount >= 0;
        if ( growthAmount < 0 ) {
            // Ignore this.
            return;
        }
        setFullSizeProportion( Math.min( fullSizeProportion + growthAmount, 1 ) );
    }

    protected abstract Shape getUntranslatedShape( double growthFactor );

    /**
     * Method to get an untranslated (in terms of position, not language)
     * version of this protein's shape when it fully grown.  This is intended
     * for use in creating control panel shapes that match this protein's shape.
     *
     * @return Shape representing the fully developed protein.
     */
    public Shape getFullyGrownShape() {
        return getUntranslatedShape( MAX_GROWTH_FACTOR );
    }

    public abstract Protein createInstance( GeneExpressionModel model, Ribosome parentRibosome );

    /**
     * Release this protein from the ribosome and allow it to drift around in
     * the cell.
     */
    public void release() {
        attachmentStateMachine.setState( new AttachmentState.GenericUnattachedAndAvailableState() );
        fullGrown.set( true );
    }

    /**
     * Set the position of this protein such that its "attachment point",
     * which is the point from which it grows when it is being synthesized,
     * is at the specified location.
     *
     * @param attachmentPointLocation
     */
    abstract public void setAttachmentPointPosition( Point2D attachmentPointLocation );
}
