// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import org.jbox2d.common.Vec2;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroscopicModel.WaterMolecule;
import edu.umd.cs.piccolo.PNode;

/**
 * Draws a single water molecule, including a circle for each of the O, H, H
 *
 * @author Sam Reid
 */
public class WaterMoleculeNode extends PNode {
    public WaterMoleculeNode( final ModelViewTransform transform, final WaterMolecule waterMolecule, VoidFunction1<VoidFunction0> addListener ) {

        //Get the diameters in view coordinates
        double oxygenDiameter = transform.modelToViewDeltaX( waterMolecule.oxygen.radius * 2 );
        double hydrogenDiameter = transform.modelToViewDeltaX( waterMolecule.h1.radius * 2 );

        //Create shapes for O, H, H
        final PhetPPath oxygen = new PhetPPath( new Ellipse2D.Double( -oxygenDiameter / 2, -oxygenDiameter / 2, oxygenDiameter, oxygenDiameter ), Color.blue );
        final PhetPPath h1 = new PhetPPath( new Ellipse2D.Double( -hydrogenDiameter / 2, -hydrogenDiameter / 2, hydrogenDiameter, hydrogenDiameter ), Color.green );
        final PhetPPath h2 = new PhetPPath( new Ellipse2D.Double( -hydrogenDiameter / 2, -hydrogenDiameter / 2, hydrogenDiameter, hydrogenDiameter ), Color.red );

        //Update the graphics for the updated model objects
        VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                double angle = waterMolecule.body.getAngle();
                oxygen.setOffset( transform.modelToView( toImmutableVector2D( waterMolecule.body.getPosition() ) ).toPoint2D() );
                h1.setOffset( transform.modelToView( toImmutableVector2D( waterMolecule.body.getPosition().add( waterMolecule.h1.localPosition ) ) ).toPoint2D() );
                h2.setOffset( transform.modelToView( toImmutableVector2D( waterMolecule.body.getPosition().add( waterMolecule.h2.localPosition ) ) ).toPoint2D() );
            }
        };
        addListener.apply( update );

        //Also update initially
        update.apply();

        //Add the children in staggered layers so it looks 3d
        addChild( h1 );
        addChild( oxygen );
        addChild( h2 );
    }

    public static ImmutableVector2D toImmutableVector2D( Vec2 from ) {
        return new ImmutableVector2D( from.x, from.y );
    }
}
