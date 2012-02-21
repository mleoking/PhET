// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.multiplecells.view.ColorChangingCellNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for displaying and interacting with mobile biomolecules.  In
 * essence, this observes the shape of the biomolecule, which changes as it
 * moves.
 */
public class MobileBiomoleculeNode extends PNode {

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
                    setPathTo( mvt.modelToView( shape ) );
                    setPaint( createGradientPaint( mvt.modelToView( mobileBiomolecule.getShape() ), mobileBiomolecule.colorProperty.get() ) );
                }
            } );

            // Update the color whenever it changes.
            mobileBiomolecule.colorProperty.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setPaint( createGradientPaint( mvt.modelToView( mobileBiomolecule.getShape() ), mobileBiomolecule.colorProperty.get() ) );
                }
            } );

            // Update its existence strength (i.e. fade level) whenever it changes.
            mobileBiomolecule.existenceStrength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double existenceStrength ) {
                    setTransparency( existenceStrength.floatValue() );
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
