/**
 * Class: Level1
 * Package: edu.colorado.phet.distanceladder.levels
 * Author: Another Guy
 * Date: Apr 16, 2004
 */
package edu.colorado.phet.distanceladder.levels;

import edu.colorado.phet.distanceladder.exercise.Exercise;
import edu.colorado.phet.distanceladder.exercise.XmlExercise;
import edu.colorado.phet.distanceladder.model.NormalStar;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.UniverseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Similar to Level3A, but the starship coordinate graphic is disabled on the star maps
 */
public class Level5 extends Exercise {

    private Star[] starField = new Star[]{
        new NormalStar( Color.white, 1E6, new Point2D.Double( 350, -50 ), 0 ),
        new NormalStar( Color.white, 1E6, new Point2D.Double( 2500, 50 ), 0 ),

    };

    public Level5( JFrame frame, final UniverseModel model ) {
        super( frame, new XmlExercise( "exercises/level5.xml" ) );
        model.getStarShip().setPov( 0, 0, 0 );
        model.getStarField().reset();
        for( int i = 0; i < starField.length; i++ ) {
            Star star = starField[i];
            model.getStarField().addStar( star );
        }
        model.setStarField( model.getStarField() );
    }
}
