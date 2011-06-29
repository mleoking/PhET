package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;
import edu.umd.cs.piccolo.PNode;

public class BondNode extends PNode implements MSNode {
    private final Bond bond;

    private double bondRadius = 15;

    public BondNode( final Bond bond ) {
        this.bond = bond;
        bond.a.position.addObserver( new SimpleObserver() {
            public void update() {
                updateGraphics();
            }
        } );
        bond.b.position.addObserver( new SimpleObserver() {
            public void update() {
                updateGraphics();
            }
        } );
    }

    public void updateGraphics() {
        removeAllChildren();
        ImmutableVector3D aPos = bond.a.position.get();
        ImmutableVector3D bPos = bond.b.position.get();

        final Color color = new Color( 50, 50, 255 );

        ImmutableVector3D aToB = bPos.minus( aPos ).normalized();

        // spots under the surface where the center of the intersecting cylinder/sphere would be
        ImmutableVector3D aSpot = aPos.plus( aToB.times( bond.a.radius * Math.sqrt( 1 - ( bondRadius / bond.a.radius ) * ( bondRadius / bond.a.radius ) ) ) );
        ImmutableVector3D bSpot = bPos.plus( aToB.times( bond.b.radius * Math.sqrt( 1 - ( bondRadius / bond.b.radius ) * ( bondRadius / bond.b.radius ) ) ).negated() );

        // 2d coordinates of the cylinder start and end
        final ImmutableVector2D a = Projection.project( aSpot );
        final ImmutableVector2D b = Projection.project( bSpot );

        // basic joint
        addChild( new PhetPPath( new java.awt.geom.Line2D.Double( a.getX(), a.getY(), b.getX(), b.getY() ) ) {{
            setStroke( new BasicStroke( (float) ( bondRadius * 2 ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ) );
            setStrokePaint( color );
        }} );

        // ellipses for added emphasis (at the base of each bond-atom intersection)
        try {
            // TODO: remove this dependency on the particular orthographic projection!

            // unit vector which in view space is perpendicular to the bond. also in the XY plane
            ImmutableVector3D perpAToB = new ImmutableVector3D( aToB.getY(), -aToB.getX(), 0 ).normalized();

            // unit vector in the direction of the minor axis of our ellipse
            ImmutableVector3D minorAxisVector = perpAToB.cross( aToB ).normalized();

            // angle (in radians) of the minor axis of the ellipse on the screen
            final double angle = Projection.project( minorAxisVector ).getAngle();

            final double minorRadius = Projection.project( minorAxisVector.times( bondRadius ) ).getMagnitude();

            // TODO share code
            addChild( new PNode() {{
                addChild( new PhetPPath( new Ellipse2D.Double( -minorRadius, -bondRadius, minorRadius * 2, bondRadius * 2 ) ) {{
                    rotate( angle );
                    setStroke( null );
                    setPaint( color );
                }} );
                setOffset( a.getX(), a.getY() );
            }} );
            addChild( new PNode() {{
                addChild( new PhetPPath( new Ellipse2D.Double( -minorRadius, -bondRadius, minorRadius * 2, bondRadius * 2 ) ) {{
                    rotate( angle );
                    setStroke( null );
                    setPaint( color );
                }} );
                setOffset( b.getX(), b.getY() );
            }} );
        }
        catch ( Exception e ) {
            // don't draw ellipses, we did a div by zero. they wouldn't show up anyways
        }
    }

    public ImmutableVector3D getCenter() {
        // TODO: simplify
        return new ImmutableVector3D(
                ( bond.a.position.get().getX() + bond.b.position.get().getX() ) / 2,
                ( bond.a.position.get().getY() + bond.b.position.get().getY() ) / 2,
                ( bond.a.position.get().getZ() + bond.b.position.get().getZ() ) / 2
        );
    }
}
