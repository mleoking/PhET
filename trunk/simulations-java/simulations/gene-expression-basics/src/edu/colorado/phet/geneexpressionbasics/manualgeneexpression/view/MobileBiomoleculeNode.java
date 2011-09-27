// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
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
                    setPaint( createGradientPaint( mvt, mobileBiomolecule ) );
                }
            } );
            // Update the color whenever it changes.
            mobileBiomolecule.colorProperty.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setPaint( createGradientPaint( mvt, mobileBiomolecule ) );
                }
            } );
            // Cursor handling.
            addInputEventListener( new CursorHandler() );
            // Drag handling.
            addInputEventListener( new BiomoleculeDragHandler( mobileBiomolecule, this, mvt ) );
        }} );
    }

    private Paint createGradientPaint( ModelViewTransform mvt, MobileBiomolecule mobileBiomolecule ) {
        return new GradientPaint( mvt.modelToView( new Point2D.Double( mobileBiomolecule.getShape().getBounds2D().getMinX(),
                                                                       mobileBiomolecule.getShape().getBounds2D().getCenterY() ) ),
                                  ColorUtils.brighterColor( mobileBiomolecule.colorProperty.get(), 0.8 ),
                                  mvt.modelToView( new Point2D.Double( mobileBiomolecule.getShape().getBounds2D().getMaxX(),
                                                                       mobileBiomolecule.getShape().getBounds2D().getCenterY() ) ),
                                  ColorUtils.darkerColor( mobileBiomolecule.colorProperty.get(), 0.3 ) );
    }
}
