package srr;

import idx3d.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

public class TestIDX3D {
    private JFrame frame;

    public TestIDX3D() {
        final idx3d_Scene scene = new idx3d_Scene( 800, 600 );
        scene.setAntialias( true );
        scene.useIdBuffer( true );
        frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final JPanel contentPane = new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D) g;
                scene.render();
                g2.drawImage( scene.getImage(), 0, 0, null );
            }
        };
        frame.setContentPane( contentPane );
        contentPane.addMouseMotionListener( new MouseMotionListener() {
            Point2D lastPoint;

            public void mouseDragged( MouseEvent e ) {
                double dx = e.getX() - lastPoint.getX();
                double dy = e.getY() - lastPoint.getY();
                idx3d_Object obj = scene.identifyObjectAt( e.getX(), e.getY() );
//                contentPane.setCursor( obj == null ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
//                System.out.println( "obj = " + obj );
                if ( obj != null ) {
                    obj.shift( (float) ( dx / 800.0 ), (float) ( -dy / 600.0 ), 0 );
                    contentPane.repaint();
                    lastPoint = e.getPoint();
                }
            }

            public void mouseMoved( MouseEvent e ) {
                lastPoint = e.getPoint();
                idx3d_Object obj = scene.identifyObjectAt( e.getX(), e.getY() );
                contentPane.setCursor( obj == null ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
//                System.out.println( "obj = " + obj );
            }
        } );
        scene.environment.setBackground( idx3d_TextureFactory.CHECKERBOARD( 160, 120, 2, 0x000000, 0x999999 ) );

        scene.addLight( "Light1", new idx3d_Light( new idx3d_Vector( 0.2f, 0.2f, 1f ), 0xFFFFFF, 320, 80 ) );
        scene.addLight( "Light2", new idx3d_Light( new idx3d_Vector( -1f, -1f, 1f ), 0xFFCC99, 100, 40 ) );

//        scene.addObject( "myobj",idx3d_ObjectFactory.ROTATIONOBJECT(path,16) );
        idx3d_Object box = idx3d_ObjectFactory.BOX( 0.5f, 0.5f, 0.5f );
        idx3d_Material crystal = new idx3d_Material( "blue.material" );
        crystal.setReflectivity( 255 );
        scene.addMaterial( "Crystal", crystal );
        scene.addObject( "myobj", box );
        scene.object( "myobj" ).setMaterial( scene.material( "Crystal" ) );

        scene.object( "myobj" ).shift( -0.5f, 0f, 0f );


        frame.setSize( 800, 600 );
    }

    public static void main( String[] args ) {
        new TestIDX3D().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
