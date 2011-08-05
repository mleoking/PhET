// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
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
    public MobileBiomoleculeNode( final ModelViewTransform mvt, final MobileBiomolecule mobileBiomolecule ) {

        Paint gradientPaint = new GradientPaint( mvt.modelToView( new Point2D.Double( mobileBiomolecule.getShape().getBounds2D().getMinX(),
                                                                                      mobileBiomolecule.getShape().getBounds2D().getCenterY() ) ),
                                                 ColorUtils.brighterColor( mobileBiomolecule.getBaseColor(), 0.5 ),
                                                 new Point2D.Double( mobileBiomolecule.getShape().getBounds2D().getMaxX(),
                                                                     mobileBiomolecule.getShape().getBounds2D().getCenterY() ),
                                                 ColorUtils.darkerColor( mobileBiomolecule.getBaseColor(), 0.5 ) );
        addChild( new PhetPPath( new BasicStroke( 1 ), Color.BLACK ) {{
            // Update the shape whenever it changes.
            mobileBiomolecule.addShapeChangeObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( mvt.modelToView( shape ) );
                    setPaint( createGradientPaint( mvt, mobileBiomolecule ) );
                }
            } );
            // Cursor handling.
            addInputEventListener( new CursorHandler() );
            // Drag handling.
            addInputEventListener( new BiomoleculeDragHandler( mobileBiomolecule, this, mvt ) );
        }} );
    }

    Paint createGradientPaint( ModelViewTransform mvt, MobileBiomolecule mobileBiomolecule ) {
        return new GradientPaint( mvt.modelToView( new Point2D.Double( mobileBiomolecule.getShape().getBounds2D().getMinX(),
                                                                       mobileBiomolecule.getShape().getBounds2D().getCenterY() ) ),
                                  ColorUtils.brighterColor( mobileBiomolecule.getBaseColor(), 0.8 ),
                                  mvt.modelToView( new Point2D.Double( mobileBiomolecule.getShape().getBounds2D().getMaxX(),
                                                                       mobileBiomolecule.getShape().getBounds2D().getCenterY() ) ),
                                  ColorUtils.darkerColor( mobileBiomolecule.getBaseColor(), 0.3 ) );
    }
}
