// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.AttachmentStateMachine;
import edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines.GenericAttachmentStateMachine;

/**
 * Class that represents the small ribosomal subunit in the model.
 *
 * @author John Blanco
 */
public class MessengerRnaDestroyer extends MobileBiomolecule {

    private static final double WIDTH = 250;   // In nanometers.

    public MessengerRnaDestroyer( GeneExpressionModel model ) {
        this( model, new Point2D.Double( 0, 0 ) );
    }

    public MessengerRnaDestroyer( GeneExpressionModel model, Point2D position ) {
        super( model, createShape(), new Color( 255, 150, 66 ) );
        setPosition( position );
    }

    private static Shape createShape() {
        Shape circle = new Ellipse2D.Double( -WIDTH / 2, -WIDTH / 2, WIDTH, WIDTH );
        DoubleGeneralPath mouthPath = new DoubleGeneralPath( 0, 0 ) {{
            lineTo( WIDTH, WIDTH * 0.8 );
            lineTo( WIDTH, -WIDTH * 0.8 );
            closePath();
        }};
        Area overallShape = new Area( circle );
        overallShape.subtract( new Area( mouthPath.getGeneralPath() ) );
        return overallShape;
    }

    @Override protected AttachmentStateMachine createAttachmentStateMachine() {
        return new GenericAttachmentStateMachine( this );
    }
}
