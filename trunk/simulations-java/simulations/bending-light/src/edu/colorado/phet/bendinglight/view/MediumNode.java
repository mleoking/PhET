// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;

import edu.colorado.phet.bendinglight.model.Medium;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic that draws a medium such as air, water, glass, etc.
 *
 * @author Sam Reid
 */
public class MediumNode extends PNode {
    public MediumNode( final ModelViewTransform transform, final Property<Medium> medium ) {
        //Add the shape that paints the medium
        addChild( new PhetPPath( medium.getValue().color ) {{
            //Update whenever the medium changes
            medium.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( medium.getValue().shape ) );
                    final Color color = medium.getValue().color;
                    setPaint( new Color( color.getRed(), color.getGreen(), color.getBlue() ) );
                }
            } );
        }} );

        //User can't interact with the medium except through control panels.
        setPickable( false );
        setChildrenPickable( false );
    }
}
