package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.chemicalreactions.box2d.BodyModel;
import edu.colorado.phet.chemicalreactions.box2d.DebugHandler;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.BOX2D_MODEL_TRANSFORM;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.BOX2D_WALL_THICKNESS;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.ENABLE_BOX2D_DEBUG_DRAW;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_ITERATIONS_PER_STEP;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.STEPS_PER_FRAME;

/**
 * Contains all of the Box2D-related logic specific to this sim. Bodies can be added and removed.
 */
public class Box2dModel {
    private final World world;
    private final List<BodyModel> bodyModels = new ArrayList<BodyModel>();
    private final DebugHandler debugHandler;
    private final LayoutBounds layoutBounds;

    public Box2dModel( final LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;

        // no "gravity" since we are imagining a table-top
        world = new World( new Vec2( 0, 0 ), true );

        setupWalls();

        debugHandler = ENABLE_BOX2D_DEBUG_DRAW ? new DebugHandler( world ) : null;
    }

    public void addBody( BodyModel bodyWrapper ) {
        Body body = world.createBody( bodyWrapper.getBodyDef() );
        bodyModels.add( bodyWrapper );
        bodyWrapper.setBody( body );
    }

    public void removeBody( BodyModel bodyWrapper ) {
        bodyModels.remove( bodyWrapper );
        world.destroyBody( bodyWrapper.getBody() );
        bodyWrapper.setBody( null );
    }

    public void tick( final double simulationTimeChange ) {
        FunctionalUtils.repeat(
                new Runnable() {
                    public void run() {
                        world.step( (float) ( simulationTimeChange ) / STEPS_PER_FRAME,
                                    MODEL_ITERATIONS_PER_STEP,
                                    MODEL_ITERATIONS_PER_STEP );
                        for ( BodyModel bodyModel : bodyModels ) {
                            bodyModel.intraStep();
                        }
                    }
                }, STEPS_PER_FRAME );

        world.clearForces();

        if ( debugHandler != null ) {
            debugHandler.step();
        }

        for ( BodyModel bodyModel : bodyModels ) {
            bodyModel.postStep();
        }
    }

    private void setupWalls() {
        final float left = (float) BOX2D_MODEL_TRANSFORM.viewToModelX( layoutBounds.getAvailablePlayAreaModelBounds().getMinX() );
        final float right = (float) BOX2D_MODEL_TRANSFORM.viewToModelX( layoutBounds.getAvailablePlayAreaModelBounds().getMaxX() );
        final float top = (float) BOX2D_MODEL_TRANSFORM.viewToModelY( layoutBounds.getAvailablePlayAreaModelBounds().getMaxY() );
        final float bottom = (float) BOX2D_MODEL_TRANSFORM.viewToModelY( layoutBounds.getAvailablePlayAreaModelBounds().getMinY() );

        final float paddedLeft = left - BOX2D_WALL_THICKNESS;
        final float paddedRight = right + BOX2D_WALL_THICKNESS;
        final float paddedTop = top + BOX2D_WALL_THICKNESS;
        final float paddedBottom = bottom - BOX2D_WALL_THICKNESS;

        final Body wallBody = world.createBody( new BodyDef() {{
            type = BodyType.STATIC;
        }} );

        // left wall
        wallBody.createFixture( new FixtureDef() {{
            shape = new PolygonShape() {{
                set( new Vec2[]{
                        new Vec2( left, paddedBottom ),
                        new Vec2( left, paddedTop ),
                        new Vec2( paddedLeft, paddedTop ),
                        new Vec2( paddedLeft, paddedBottom )
                }, 4 );
            }};
            density = 0;
            restitution = 1;
        }} );

        // right wall
        wallBody.createFixture( new FixtureDef() {{
            shape = new PolygonShape() {{
                set( new Vec2[]{
                        new Vec2( paddedRight, paddedBottom ),
                        new Vec2( paddedRight, paddedTop ),
                        new Vec2( right, paddedTop ),
                        new Vec2( right, paddedBottom )
                }, 4 );
            }};
            density = 0;
            restitution = 1;
        }} );

        // top wall
        wallBody.createFixture( new FixtureDef() {{
            shape = new PolygonShape() {{
                set( new Vec2[]{
                        new Vec2( paddedRight, top ),
                        new Vec2( paddedRight, paddedTop ),
                        new Vec2( paddedLeft, paddedTop ),
                        new Vec2( paddedLeft, top )
                }, 4 );
            }};
            density = 0;
            restitution = 1;
        }} );

        // bottom wall
        wallBody.createFixture( new FixtureDef() {{
            shape = new PolygonShape() {{
                set( new Vec2[]{
                        new Vec2( paddedRight, paddedBottom ),
                        new Vec2( paddedRight, bottom ),
                        new Vec2( paddedLeft, bottom ),
                        new Vec2( paddedLeft, paddedBottom )
                }, 4 );
            }};
            density = 0;
            restitution = 1;
        }} );
    }

}
