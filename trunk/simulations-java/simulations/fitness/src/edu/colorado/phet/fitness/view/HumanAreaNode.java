package edu.colorado.phet.fitness.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessCanvas;
import edu.colorado.phet.fitness.module.fitness.FitnessRenderingSizeStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Created by: Sam
 * Apr 3, 2008 at 8:43:08 PM
 */
public class HumanAreaNode extends PNode {
    private Human human;
    private PhetPPath head;
    private PImage heart;
    private PhetPPath areaNode = new PhetPPath( Color.white, new BasicStroke( 0.02f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), Color.black );

    public HumanAreaNode( Human human ) {
        this.human = human;
        head = new PhetPPath( Color.white, new BasicStroke( 0.02f ), Color.black );
        addChild( areaNode );

        heart = new HeartNode( human );
        heart.scale( 0.25 / heart.getFullBounds().getWidth() );
        addChild( heart );
        addChild( head );
        human.addListener( new Human.Adapter() {
            public void heightChanged() {
                update();
            }

            public void weightChanged() {
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

        Line2D.Double leftLeg = ( new Line2D.Double( -distBetweenShoulders / 2, 0, 0, hipY ) );
        Line2D.Double rightLeg = ( new Line2D.Double( +distBetweenShoulders / 2, 0, 0, hipY ) );
        Line2D.Double body = ( new Line2D.Double( 0, hipY, 0, neckY ) );
        Line2D.Double leftArm = ( new Line2D.Double( 0, shoulderY, -armLength, shoulderY ) );
        Line2D.Double rightArm = ( new Line2D.Double( 0, shoulderY, armLength, shoulderY ) );
        Ellipse2D.Double head = ( new Ellipse2D.Double( -headWidth / 2, neckY - headHeight, headWidth, headHeight ) );
        this.head.setPathTo( head );

        BasicStroke limbStroke = new BasicStroke( (float) ( 0.08f * human.getMass() / 75 ), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        BasicStroke bodyStroke = new BasicStroke( (float) ( 0.08f * human.getMass() / 75 * 1.2 ), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        Area bodyArea = new Area();
        bodyArea.add( new Area( limbStroke.createStrokedShape( leftLeg ) ) );
        bodyArea.add( new Area( limbStroke.createStrokedShape( rightLeg ) ) );
        Shape bodyShape = bodyStroke.createStrokedShape( body );
        bodyArea.add( new Area( bodyShape ) );
        bodyArea.add( new Area( limbStroke.createStrokedShape( rightArm ) ) );
        bodyArea.add( new Area( limbStroke.createStrokedShape( leftArm ) ) );
        bodyArea.add( new Area( createStomachShape( bodyShape ) ) );

        areaNode.setPathTo( bodyArea );
        areaNode.setStroke( new BasicStroke( (float) ( Math.min( 0.02f * human.getMass() / 75, 0.025f ) ) ) );

        heart.setOffset( -heart.getFullBounds().getWidth() * 0.15, neckY + heart.getFullBounds().getHeight() * 1.25 );
    }

    private Shape createStomachShape( Shape bodyShape ) {
        Rectangle2D bounds = bodyShape.getBounds2D();
        double w = Math.max( 0.05 * 2 * human.getMass() / 75 / 2 - 0.05, 0 );
        Ellipse2D.Double stomach = new Ellipse2D.Double( bounds.getX() - w / 2, bounds.getCenterY(), bounds.getWidth() + w, bounds.getHeight() / 2 );
        return stomach;
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        PhetPCanvas contentPane = new BufferedPhetPCanvas( new PDimension( 10, 10 ) );
        contentPane.setWorldTransformStrategy( new FitnessRenderingSizeStrategy( contentPane, FitnessCanvas.CANVAS_WIDTH, FitnessCanvas.CANVAS_HEIGHT ) );
        frame.setContentPane( contentPane );

        final Human human1 = new Human();
        HumanAreaNode humanNodeArea = new HumanAreaNode( human1 );
        contentPane.addWorldChild( humanNodeArea );
        frame.setVisible( true );
        JFrame controlFrame = new JFrame();
        JPanel cp = new VerticalLayoutPanel();
        controlFrame.setContentPane( cp );

        final LinearValueControl control = new LinearValueControl( 0, 500, 75, "mass", "0.00", "kg" );
        control.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                human1.setMass( control.getValue() );
            }
        } );
        cp.add( control );

        controlFrame.setVisible( true );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        controlFrame.pack();
        controlFrame.setLocation( frame.getX() + frame.getWidth(), frame.getY() );
    }
}