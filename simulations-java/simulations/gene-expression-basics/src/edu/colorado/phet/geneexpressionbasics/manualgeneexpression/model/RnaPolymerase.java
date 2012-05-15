// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.AttachmentSite;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.RnaPolymeraseAttachmentStateMachine;

/**
 * Class that represents RNA polymerase in the model.
 *
 * @author John Blanco
 */
public class RnaPolymerase extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Overall size of the polymerase molecule.
    private static final double WIDTH = 340;   // picometers
    private static final double HEIGHT = 480;  // picometers

    // Offset from the center of the molecule to the location where mRNA
    // should emerge when transcription is occurring.  This is determined
    // empirically, and may need to change if the shape is changed.
    public static final ImmutableVector2D MESSENGER_RNA_GENERATION_OFFSET = new ImmutableVector2D( -WIDTH * 0.4, HEIGHT * 0.4 );

    // This the threshold for the affinity which triggers the polymerase to
    // start transcribing.  Not sure if this is a reasonable thing to do, or
    // if the attachment site should explicitly say whether the transcription
    // factors are present.  For now at least, this works.
    private static final double START_TRANSCRIPTION_THRESHOLD = 0.9;

    // Set of points that outline the basic, non-distorted shape of this
    // molecule.  The shape is meant to look like illustrations in "The
    // Machinery of Life" by David Goodsell.
    private static final List<Point2D> shapePoints = new ArrayList<Point2D>() {{
        add( new Point2D.Double( 0, HEIGHT / 2 ) ); // Middle top.
        add( new Point2D.Double( WIDTH / 2, HEIGHT * 0.25 ) );
        add( new Point2D.Double( WIDTH * 0.35, -HEIGHT * 0.25 ) );
        add( new Point2D.Double( 0, -HEIGHT / 2 ) ); // Middle bottom.
        add( new Point2D.Double( -WIDTH * 0.35, -HEIGHT * 0.25 ) );
        add( new Point2D.Double( -WIDTH / 2, HEIGHT * 0.25 ) );
    }};

    // Colors used by this molecule.
    private static final Color NOMINAL_COLOR = new Color( 0, 153, 210 );
    private static final Color CONFORMED_COLOR = Color.CYAN;

    // Random number generator.
    private static final Random RAND = new Random( System.currentTimeMillis() + 2 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Copy of the attachment state machine reference from base class, but
    // with the more specific type.
    private final RnaPolymeraseAttachmentStateMachine rnaPolymeraseAttachmentStateMachine;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Default constructor, used mostly when instances are needed in places like
     * control panels.
     */
    public RnaPolymerase() {
        this( new StubGeneExpressionModel(), new Point2D.Double( 0, 0 ) );
    }

    public RnaPolymerase( GeneExpressionModel model, Point2D position ) {
        super( model, createShape(), NOMINAL_COLOR );
        rnaPolymeraseAttachmentStateMachine = (RnaPolymeraseAttachmentStateMachine) attachmentStateMachine;
        setPosition( position );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    // Overridden to provide attachment behavior that is unique to polymerase.
    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new RnaPolymeraseAttachmentStateMachine( this );
    }

    @Override public void changeConformation( double changeFactor ) {
        Shape newUntranslatedShape = BioShapeUtils.createdDistortedRoundedShapeFromPoints( shapePoints, changeFactor, 259 ); // Seed value chosen by trial and error.
        Shape newTranslatedShape = AffineTransform.getTranslateInstance( getPosition().getX(), getPosition().getY() ).createTransformedShape( newUntranslatedShape );
        shapeProperty.set( newTranslatedShape );
        colorProperty.set( ColorUtils.interpolateRBGA( NOMINAL_COLOR, CONFORMED_COLOR, changeFactor ) );
    }

    @Override public AttachmentSite proposeAttachments() {
        // Propose attachment to the DNA.
        return model.getDnaMolecule().considerProposalFrom( this );
    }

    @Override public ImmutableVector2D getDetachDirection() {
        // Randomly either up or down when detaching from DNA.
        return RAND.nextBoolean() ? new ImmutableVector2D( 0, 1 ) : new ImmutableVector2D( 0, -1 );
    }

    public void setRecycleMode( boolean recycleMode ) {
        rnaPolymeraseAttachmentStateMachine.setRecycleMode( recycleMode );
    }

    public void addRecycleReturnZone( Rectangle2D recycleReturnZone ) {
        rnaPolymeraseAttachmentStateMachine.addRecycleReturnZone( recycleReturnZone );
    }

    private static Shape createShape() {
        // Shape is meant to look like illustrations in "The Machinery of
        // Life" by David Goodsell.
        return BioShapeUtils.createRoundedShapeFromPoints( shapePoints );
    }
}
