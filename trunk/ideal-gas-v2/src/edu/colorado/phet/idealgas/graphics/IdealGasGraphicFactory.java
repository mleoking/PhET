/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 16, 2003
 * Time: 10:53:01 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.physics.collision.Box2D;
//import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.idealgas.physics.body.IdealGasParticle;
//import edu.colorado.phet.idealgas.physics.body.Balloon;

import java.util.Map;

public class IdealGasGraphicFactory extends GraphicFactory {

    private IdealGasGraphicFactory() {
        Map classMap = getPhysicalToGraphicalClassMap();
        classMap.put( Box2D.class, edu.colorado.phet.idealgas.graphics.Box2DGraphic.class );
        classMap.put( PressureSensingBox.class, edu.colorado.phet.idealgas.graphics.Box2DGraphic.class );
        classMap.put( HeavySpecies.class, HeavySpeciesGraphic.class );
        classMap.put( LightSpecies.class, LightSpeciesGraphic.class );
        classMap.put( HollowSphere.class, edu.colorado.phet.idealgas.graphics.HollowSphereGraphic.class );
        classMap.put( Balloon.class, edu.colorado.phet.idealgas.graphics.HollowSphereGraphic.class );
        classMap.put( IdealGasParticle.class, ParticleGraphic.class );
        classMap.put( Box2D.class, Box2DGraphic.class );
        classMap.put( HotAirBalloon.class, HotAirBalloonGraphic.class );
    }

    //
    // Static fields and methods
    //
    private static IdealGasGraphicFactory instance = new IdealGasGraphicFactory();

    public static GraphicFactory instance() {
        return instance;
    }
}
