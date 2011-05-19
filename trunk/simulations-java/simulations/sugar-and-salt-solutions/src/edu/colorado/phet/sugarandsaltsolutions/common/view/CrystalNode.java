// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;
import edu.umd.cs.piccolo.PNode;

import static java.awt.Color.white;

/**
 * Graphical representation of a salt or sugar crystal, shown as a white square
 *
 * @author Sam Reid
 */
public class CrystalNode extends PNode {
    public CrystalNode( final ModelViewTransform transform, final MacroCrystal crystal, final ObservableProperty<Color> color, final double size ) {
        //Draw the shape of the salt crystal at its location
        addChild( new PhetPPath( white ) {{
            crystal.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D modelPosition ) {
                    ImmutableVector2D viewPosition = transform.modelToView( modelPosition );

                    //Use a scaled cartoon size for the grains, since actual grain sizes would be much to large
                    double cartoonSize = size / 5;
                    setPathTo( new Rectangle2D.Double( viewPosition.getX() - cartoonSize / 2, viewPosition.getY() - cartoonSize / 2, cartoonSize, cartoonSize ) );
                }
            } );
            //Synchronize the color with the specified color, which can be changed by the user in the color chooser dialog
            color.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setPaint( color );
                }
            } );
        }} );
    }
}
