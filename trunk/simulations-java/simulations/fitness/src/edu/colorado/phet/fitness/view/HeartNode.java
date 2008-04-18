package edu.colorado.phet.fitness.view;

import java.awt.*;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Created by: Sam
* Apr 18, 2008 at 1:10:26 AM
*/
public class HeartNode extends PImage {
    private PImage heart;
    private Human human;
    private PhetPPath smilePath;

    public HeartNode( Human human ) {
        this.human = human;
        heart = new PImage( FitnessResources.getImage( "heart.png" ) );
        addChild( heart );

        double eyeDX = 80;
        double eyeY = 50;
        double eyeScale = 0.4;

        PImage leftEye = new PImage( BufferedImageUtils.multiScale( FitnessResources.getImage( "eye.png" ), eyeScale ) );
        leftEye.setOffset( heart.getFullBounds().getWidth() / 2 - leftEye.getFullBounds().getWidth() / 2 - eyeDX / 2, eyeY );
        addChild( leftEye );

        PImage rightEye = new PImage( BufferedImageUtils.multiScale( FitnessResources.getImage( "eye.png" ), eyeScale ) );
        rightEye.setOffset( heart.getFullBounds().getWidth() / 2 - rightEye.getFullBounds().getWidth() / 2 + eyeDX / 2, eyeY );
        addChild( rightEye );

        smilePath = new PhetPPath( new BasicStroke( 14f ), Color.black );
        addChild( smilePath );
        human.addListener( new Human.Adapter() {
            public void fatPercentChanged() {
                updateSmile();
            }
        } );

        updateSmile();
    }

    private void updateSmile() {
        DoubleGeneralPath smile = new DoubleGeneralPath();
        double smileInsetScaleX = 0.3;
        double smileYFrac = 0.6;
        double happiness = ( human.getLeanMuscleMass() / 100.0 - 0.5 ) * 2;//between 0 and 1
        double controlPointDY = 50 * happiness;
        smile.moveTo( heart.getFullBounds().getWidth() * smileInsetScaleX, heart.getFullBounds().getHeight() * smileYFrac );
        smile.curveTo( heart.getFullBounds().getWidth() * 0.4, heart.getFullBounds().getHeight() * smileYFrac + controlPointDY,
                       heart.getFullBounds().getWidth() * 0.6, heart.getFullBounds().getHeight() * smileYFrac + controlPointDY,
                       heart.getFullBounds().getWidth() * ( 1 - smileInsetScaleX ), heart.getFullBounds().getHeight() * smileYFrac
        );
        smilePath.setOffset( 0, happiness < 0 ? Math.abs( happiness ) * 20 : 0 );
        smilePath.setPathTo( smile.getGeneralPath() );
    }
}
