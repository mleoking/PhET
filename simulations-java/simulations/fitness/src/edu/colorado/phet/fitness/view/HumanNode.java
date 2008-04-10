package edu.colorado.phet.fitness.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.Human;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Created by: Sam
 * Apr 3, 2008 at 8:43:08 PM
 */
public class HumanNode extends PNode {
    private Human human;
    private PhetPPath leftLeg;
    private PhetPPath rightLeg;
    private PhetPPath body;
    private PhetPPath leftArm;
    private PhetPPath rightArm;
    private PhetPPath head;
    private PImage heart;

    public HumanNode( Human human ) {
        this.human = human;
        leftLeg = new PhetPPath( new BasicStroke( 0.02f ), Color.black );
        rightLeg = new PhetPPath( new BasicStroke( 0.02f ), Color.black );

        body = new PhetPPath( new BasicStroke( 0.02f ), Color.black );

        leftArm = new PhetPPath( new BasicStroke( 0.02f ), Color.black );
        rightArm = new PhetPPath( new BasicStroke( 0.02f ), Color.black );

        head = new PhetPPath( new BasicStroke( 0.02f ), Color.black );

        addChild( head );
        addChild( leftArm );
        addChild( rightArm );
        addChild( body );
        addChild( leftLeg );
        addChild( rightLeg );

        heart = new HeartNode( human );
        heart.scale( 0.25 / heart.getFullBounds().getWidth() );
        addChild( heart );

        human.addListener( new Human.Adapter() {
            public void heightChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        double headWidth = 0.2;
        double headHeight = 0.2;

        double distBetweenShoulders = 0.5;
        double armLength = human.getHeight() * 0.35;

        double hipY = -human.getHeight() * 0.4;
//        double shoulderY = -human.getHeight() * 0.7;
        double neckY = -human.getHeight() + headHeight;
        double shoulderY = neckY + headHeight;


        leftLeg.setPathTo( new Line2D.Double( -distBetweenShoulders / 2, 0, 0, hipY ) );
        rightLeg.setPathTo( new Line2D.Double( +distBetweenShoulders / 2, 0, 0, hipY ) );
        body.setPathTo( new Line2D.Double( 0, hipY, 0, neckY ) );
        leftArm.setPathTo( new Line2D.Double( 0, shoulderY, -armLength, shoulderY ) );
        rightArm.setPathTo( new Line2D.Double( 0, shoulderY, armLength, shoulderY ) );
        head.setPathTo( new Ellipse2D.Double( -headWidth / 2, neckY - headHeight, headWidth, headHeight ) );
        heart.setOffset( -heart.getFullBounds().getWidth() * 0.15, neckY + heart.getFullBounds().getHeight()*1.25 );
        //heart.
    }

    private static class HeartNode extends PImage {
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

            PImage leftEye = new PImage( FitnessResources.getImage( "eye.png" ) );
            leftEye.scale( eyeScale );

            leftEye.setOffset( heart.getFullBounds().getWidth() / 2 - leftEye.getFullBounds().getWidth() / 2 - eyeDX / 2, eyeY );
            addChild( leftEye );

            PImage rightEye = new PImage( FitnessResources.getImage( "eye.png" ) );
            rightEye.scale( eyeScale );
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
            double happiness = ( human.getMusclePercent() / 100.0 - 0.5 ) * 2;//between 0 and 1
            double controlPointDY = 50 * happiness;
            smile.moveTo( heart.getFullBounds().getWidth() * smileInsetScaleX, heart.getFullBounds().getHeight() * smileYFrac );
            smile.curveTo( heart.getFullBounds().getWidth() * 0.4, heart.getFullBounds().getHeight() * smileYFrac + controlPointDY,
                           heart.getFullBounds().getWidth() * 0.6, heart.getFullBounds().getHeight() * smileYFrac + controlPointDY,
                           heart.getFullBounds().getWidth() * ( 1 - smileInsetScaleX ), heart.getFullBounds().getHeight() * smileYFrac
            );
            smilePath.setOffset( 0, happiness<0?Math.abs(happiness)*20 :0);
            smilePath.setPathTo( smile.getGeneralPath() );
        }
    }
}
