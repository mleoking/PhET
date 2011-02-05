// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.Medium;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MediumNode extends PNode {
    public MediumNode( final ModelViewTransform transform, final Property<Medium> medium ) {
        addChild( new PhetPPath( medium.getValue().getColor() ) {{
            medium.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( medium.getValue().getShape() ) );
                    setPaint( medium.getValue().getColor() );
                }
            } );
        }} );

    }
}
