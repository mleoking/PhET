package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node that draws a shaded sphere in the location of the spherical particle.
 *
 * @author Sam Reid
 */
public class SugarMoleculeNode extends PNode {

    private static BufferedImage sucroseImage = SucroseImage.getSucroseImage();

    public SugarMoleculeNode( final ModelViewTransform transform, final SugarMolecule particle ) {
        //Show the image that represents the sugar molecule
        addChild( new PImage( sucroseImage ) {{
            particle.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D position ) {
                    //scale so the width fits
                    double desiredWidth = transform.modelToView( particle.getShape() ).getBounds2D().getWidth();
                    double scale = desiredWidth / sucroseImage.getWidth();
                    setScale( scale );

                    //Center at the model position
                    Point2D.Double modelPoint = transform.modelToView( position ).toPoint2D();
                    setOffset( modelPoint.x - sucroseImage.getWidth() / 2 * scale, modelPoint.y - sucroseImage.getHeight() / 2 * scale );
                }
            } );
        }} );

        //Show the model bounds for debugging
        addChild( new PhetPPath( new BasicStroke( 2 ), Color.yellow ) {{
            particle.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D immutableVector2D ) {
                    setPathTo( transform.modelToView( particle.getShape() ) );
                }
            } );
        }} );
    }
}