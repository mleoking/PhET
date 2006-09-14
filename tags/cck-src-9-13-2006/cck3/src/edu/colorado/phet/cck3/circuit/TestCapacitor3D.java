package edu.colorado.phet.cck3.circuit;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class TestCapacitor3D {
    private JFrame frame;

    public TestCapacitor3D() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Capacitor3DShapeSet capacitor3DShapeSet = new Capacitor3DShapeSet( Math.PI / 4, 50, 100, new Point2D.Double( 100, 100 ), new Point2D.Double( 500, 200 ), 30 );
        PCanvas pc = new PCanvas();

        pc.getLayer().addChild( new PPath( capacitor3DShapeSet.getPlate2Wire() ) );
        pc.getLayer().addChild( createPNode( capacitor3DShapeSet.getPlate2Shape() ) );
        pc.getLayer().addChild( createPNode( capacitor3DShapeSet.getPlate1Shape() ) );
        pc.getLayer().addChild( new PPath( capacitor3DShapeSet.getPlate1Wire() ) );
        frame.setContentPane( pc );
    }

    private PPath createPNode( Shape s ) {
        PPath path = new PPath( s );
        path.setPaint( Color.yellow );
        path.setStroke( new BasicStroke( 1 ) );
        path.setStrokePaint( Color.black );
        return path;
    }

    public static void main( String[] args ) {
        new TestCapacitor3D().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
