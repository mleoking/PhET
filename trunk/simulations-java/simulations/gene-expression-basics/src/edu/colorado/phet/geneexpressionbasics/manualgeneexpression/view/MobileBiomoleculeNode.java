// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.multiplecells.view.ColorChangingCellNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Base class for displaying and interacting with mobile biomolecules.  In
 * essence, this observes the shape of the biomolecule, which changes as it
 * moves.
 */
public class MobileBiomoleculeNode extends PPath {

    /**
     * Constructor that uses default stroke.
     *
     * @param mvt
     * @param mobileBiomolecule
     */
    public MobileBiomoleculeNode( final ModelViewTransform mvt, final MobileBiomolecule mobileBiomolecule ) {
        this( mvt, mobileBiomolecule, new BasicStroke( 1 ) );
    }

    /**
     * Constructor.
     *
     * @param mvt
     * @param mobileBiomolecule
     */
    public MobileBiomoleculeNode( final ModelViewTransform mvt, final MobileBiomolecule mobileBiomolecule, Stroke outlineStroke ) {

        addChild( new PhetPPath( outlineStroke, Color.BLACK ) {{

            // Update the shape whenever it changes.
            mobileBiomolecule.addShapeChangeObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    // Create a shape that excludes any offset.
                    Shape centeredShape = getCenteredShape( mvt.modelToView( shape ) );
                    setPathTo( centeredShape );
                    // Account for the offset.
                    setOffset( mvt.modelToView( mobileBiomolecule.getPosition() ) );
                    // Set the gradient paint.
                    setPaint( createGradientPaint( centeredShape, mobileBiomolecule.colorProperty.get() ) );
                }
            } );

            // Update the color whenever it changes.
            mobileBiomolecule.colorProperty.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setPaint( createGradientPaint( getCenteredShape( mvt.modelToView( mobileBiomolecule.getShape() ) ), mobileBiomolecule.colorProperty.get() ) );
                }
            } );

            // Update its existence strength (i.e. fade level) whenever it changes.
            mobileBiomolecule.existenceStrength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double existenceStrength ) {
                    assert existenceStrength >= 0 && existenceStrength <= 1; // Bounds checking.
                    setTransparency( (float) Math.min( existenceStrength.floatValue(), 1 + mobileBiomolecule.zPosition.get() ) );
                }
            } );

            // Update the "closeness" whenever it changes.
            mobileBiomolecule.zPosition.addObserver( new VoidFunction1<Double>() {
                public void apply( Double zPosition ) {
                    assert zPosition >= -1 && zPosition <= 0; // Parameter checking.
                    // The further back the biomolecule is, the more
                    // transparent it is in order to make it look more distant.
                    setTransparency( (float) Math.min( 1 + zPosition, mobileBiomolecule.existenceStrength.get() ) );
                    // Also, as it goes further back, this node is scaled down
                    // a bit, also to make it look further away.
                    setScale( 1 );
                    setScale( 1 + 0.15 * zPosition );
                }
            } );

            // If a polymerase molecule attaches to the DNA strand, move it to
            // the back of its current layer so that nothing can go between it
            // and the DNA molecule.  Otherwise odd-looking things can happen.
            mobileBiomolecule.attachedToDna.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean attachedToDna ) {
                    if ( mobileBiomolecule instanceof RnaPolymerase && attachedToDna ) {
                        moveToBack();
                    }
                }
            } );

            // Cursor handling.
            addInputEventListener( new CursorHandler() );

            // Drag handling.
            addInputEventListener( new BiomoleculeDragHandler( mobileBiomolecule, this, mvt ) );

            // Interactivity control.
            mobileBiomolecule.movableByUser.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean movableByUser ) {
                    setPickable( movableByUser );
                    setChildrenPickable( movableByUser );
                }
            } );
        }} );

        // Move this biomolecule to the top of its layer when grabbed.
        mobileBiomolecule.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                moveToFront();
            }
        } );
    }

    /**
     * Get a shape that is positioned such that its center is at point (0, 0).
     *
     * @param shape
     * @return
     */
    private static Shape getCenteredShape( Shape shape ) {
        double xOffset = shape.getBounds2D().getCenterX();
        double yOffset = shape.getBounds2D().getCenterY();
        AffineTransform transform = AffineTransform.getTranslateInstance( -xOffset, -yOffset );
        return transform.createTransformedShape( shape );
    }

    /**
     * Create a gradient paint in order to give a molecule a little depth.
     * This is public so that it can be used by other nodes that need to
     * depict biomolecules.
     */
    public static Paint createGradientPaint( Shape shape, Color baseColor ) {
        Paint paint;
        if ( baseColor != ColorChangingCellNode.FLORESCENT_FILL_COLOR ) {
            paint = new GradientPaint( (float) shape.getBounds2D().getMinX(),
                                       (float) shape.getBounds2D().getCenterY(),
                                       ColorUtils.brighterColor( baseColor, 0.8 ),
                                       (float) shape.getBounds2D().getMaxX(),
                                       (float) shape.getBounds2D().getCenterY(),
                                       ColorUtils.darkerColor( baseColor, 0.3 ) );

        }
        else {
            // Special case: If using the "fluorescent" color, i.e. the one
            // used to depict green fluorescent protein in the sim, don't
            // create a gradient, because it looks brighter and more distinct.
            paint = baseColor;
        }

        return paint;
    }
}
