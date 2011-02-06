// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class LightRayNode extends PNode {
    public LightRayNode( final ModelViewTransform transform, final LightRay lightRay ) {
        float powerFraction = (float) lightRay.getPowerFraction();
        Color color = lightRay.getColor();
        addChild( new PhetPPath( new BasicStroke( 4 ), new Color( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float) Math.sqrt( powerFraction ) ) ) {{
            lightRay.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( new Line2D.Double( lightRay.tip.getValue().toPoint2D(), lightRay.tail.getValue().toPoint2D() ) ) );
                }
            } );
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }
}
