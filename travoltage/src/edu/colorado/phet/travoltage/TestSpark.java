package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.gui.ParticlePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class TestSpark extends MouseMotionAdapter implements MouseListener {
    Spark s;

    TestSpark( Spark s ) {
        this.s = s;
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public void mouseEntered( MouseEvent me ) {
    }

    public void mouseExited( MouseEvent me ) {
    }

    public void mouseReleased( MouseEvent me ) {
    }

    public void mousePressed( MouseEvent me ) {
        s.setSink( me.getPoint() );
    }

    public void mouseDragged( MouseEvent me ) {
        mousePressed( me );
    }

    static void test( int x, int y ) {
        double ang = new Spark( null, null, 0, 0, 0 ).getAngle( x, y );
        System.out.println( "(" + x + ", " + y + ") -> " + ang );
    }

    public static void main( String[] args ) {
        test( 10, 10 );
        test( -10, 10 );
        test( 10, -10 );
        test( -10, -10 );
        Point start = new Point( 100, 100 );
        Point end = new Point( 200, 200 );
        //double angle=Math.PI/4;
        double angle = Math.PI / 3.8;
        Spark s = new Spark( start, end, angle, 4, 6 );
        JFrame f = new JFrame();
        ParticlePanel pp = new ParticlePanel();
        pp.addMouseListener( new TestSpark( s ) );
        pp.addMouseMotionListener( new TestSpark( s ) );
        Spark.SparkRunner sr = s.newSparkRunner( pp, 100000, 20 );
        pp.add( sr );
        f.setContentPane( pp );
        f.setVisible( true );
        f.setSize( new Dimension( 400, 400 ) );
        f.validate();
        new Thread( sr ).start();
    }
}
