// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Line2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class TestRulerAlignment extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 500, 300 );
    
    private static final double RULER_Y_SPACING = 25;

    public TestRulerAlignment() {
        
        double distanceBetweenFirstAndLastTick = 200;
        double height = 50;
        String[] majorTickLabels1 = { "-100", "0", "100" };
        String units = "nm";
        int numMinorTicksBetweenMajors = 4;
        int fontSize = 10;
        RulerNode rulerNode1 = new RulerNode( distanceBetweenFirstAndLastTick, height, majorTickLabels1, units, numMinorTicksBetweenMajors, fontSize );
        rulerNode1.setUnitsAssociatedMajorTickLabel( "0" );
        rulerNode1.setOffset( ( FRAME_SIZE.getWidth() / 2 ) - ( rulerNode1.getFullBounds().getWidth() / 2 ), RULER_Y_SPACING );

        distanceBetweenFirstAndLastTick = 400;
        String[] majorTickLabels2 = { "-200", "-100", "0", "100", "200" };
        RulerNode rulerNode2 = new RulerNode( distanceBetweenFirstAndLastTick, height, majorTickLabels2, units, numMinorTicksBetweenMajors, fontSize );
        rulerNode2.setUnitsAssociatedMajorTickLabel( "0" );
        rulerNode2.setOffset( ( FRAME_SIZE.getWidth() / 2 ) - ( rulerNode2.getFullBounds().getWidth() / 2 ), rulerNode1.getFullBounds().getMaxY() + RULER_Y_SPACING );
        
        distanceBetweenFirstAndLastTick = 600;
        String[] majorTickLabels3 = { "-300", "-200", "-100", "0", "100", "200", "300" };
        RulerNode rulerNode3 = new RulerNode( distanceBetweenFirstAndLastTick, height, majorTickLabels3, units, numMinorTicksBetweenMajors, fontSize );
        rulerNode3.setUnitsAssociatedMajorTickLabel( "0" );
        rulerNode3.setOffset( ( FRAME_SIZE.getWidth() / 2 ) - ( rulerNode3.getFullBounds().getWidth() / 2 ), rulerNode2.getFullBounds().getMaxY() + RULER_Y_SPACING );
        
        PPath verticalAxisNode = new PPath( new Line2D.Double( 0, 0, 0, 1000 ) );
        verticalAxisNode.setStrokePaint( Color.RED );
        verticalAxisNode.setStroke( new BasicStroke( 1f ) );
        verticalAxisNode.setOffset( ( FRAME_SIZE.getWidth() / 2 ) - (verticalAxisNode.getFullBounds().getWidth()/2 ), 0 );

        PhetPCanvas canvas = new PhetPCanvas( FRAME_SIZE );
        canvas.getLayer().addChild( rulerNode1 );
        canvas.getLayer().addChild( rulerNode2 );
        canvas.getLayer().addChild( rulerNode3 );
        canvas.getLayer().addChild( verticalAxisNode );

        setContentPane( canvas );
        setSize( FRAME_SIZE );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        JFrame testFrame = new TestRulerAlignment();
        testFrame.show();
    }
}
