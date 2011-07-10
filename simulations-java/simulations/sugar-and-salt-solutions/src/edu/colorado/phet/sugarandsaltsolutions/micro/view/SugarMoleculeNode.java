package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node that draws a shaded sphere in the location of the spherical particle.
 *
 * @author Sam Reid
 */
public class SugarMoleculeNode extends PNode {
    public SugarMoleculeNode( final ModelViewTransform transform, final SugarMolecule particle ) {
        addChild( new PImage( SucroseImage.getSucroseImage() ) {{
            particle.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D position ) {
                    setOffset( transform.modelToView( position ).toPoint2D() );
                }
            } );
        }} );
    }
}