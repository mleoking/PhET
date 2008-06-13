package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.model.Human;
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
//        BasicStroke basicStroke = new BasicStroke( 0.02f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 0.0001f );
        BasicStroke basicStroke = new BasicStroke( 0.02f );
        leftLeg = new PhetPPath( basicStroke, Color.black );
        rightLeg = new PhetPPath( basicStroke, Color.black );

        body = new PhetPPath( basicStroke, Color.black );

        leftArm = new PhetPPath( basicStroke, Color.black );
        rightArm = new PhetPPath( basicStroke, Color.black );

        head = new PhetPPath( basicStroke, Color.black );

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
        double neckY = -human.getHeight() + headHeight;
        double shoulderY = neckY + headHeight;

        leftLeg.setPathTo( new Line2D.Double( -distBetweenShoulders / 2, 0, 0, hipY ) );
        rightLeg.setPathTo( new Line2D.Double( +distBetweenShoulders / 2, 0, 0, hipY ) );
        body.setPathTo( new Line2D.Double( 0, hipY, 0, neckY ) );
        leftArm.setPathTo( new Line2D.Double( 0, shoulderY, -armLength, shoulderY ) );
        rightArm.setPathTo( new Line2D.Double( 0, shoulderY, armLength, shoulderY ) );
        head.setPathTo( new Ellipse2D.Double( -headWidth / 2, neckY - headHeight, headWidth, headHeight ) );
        heart.setOffset( -heart.getFullBounds().getWidth() * 0.15, neckY + heart.getFullBounds().getHeight() * 1.25 );
    }

}
