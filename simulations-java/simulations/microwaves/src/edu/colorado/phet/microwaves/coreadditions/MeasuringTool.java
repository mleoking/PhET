/**
 * Class: MeasuringTool
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 3, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.common_microwaves.view.ApparatusPanel;
import edu.colorado.phet.common_microwaves.view.graphics.Graphic;
import edu.colorado.phet.common_microwaves.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.common_microwaves.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class MeasuringTool extends MouseInputAdapter {

    Point2D.Double startPoint = new Point2D.Double();
    Point2D.Double endPoint = new Point2D.Double();
    private ApparatusPanel panel;
    private JDialog dialog;
    private JTextField xTF = new JTextField( 5 );
    private JTextField yTF = new JTextField( 5 );
    private JTextField rTF = new JTextField( 5 );
    private JTextField thetaTF = new JTextField( 5 );
    private MeasuringGraphic measuringGraphic = new MeasuringGraphic();
    private ModelViewTransform2D tx;
    private boolean armed;

    public MeasuringTool( ApparatusPanel panel ) {

        this.panel = panel;

        // Lay out the display
        Frame frame = PhetApplication.instance().getApplicationView().getPhetFrame();
        dialog = new JDialog( frame, SimStrings.get( "MeasuringTool.DialogTitle" ) );
        dialog.setUndecorated( true );
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout( new GridBagLayout() );
        try {
            int rowIdx = 0;
//            GraphicsUtil.addGridBagComponent( contentPane, new JLabel( "x" ),
//                                              0, rowIdx,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.EAST );
//            GraphicsUtil.addGridBagComponent( contentPane, xTF,
//                                              1, rowIdx++,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.WEST );
//            GraphicsUtil.addGridBagComponent( contentPane, new JLabel( "y" ),
//                                              0, rowIdx,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.EAST );
//            GraphicsUtil.addGridBagComponent( contentPane, yTF,
//                                              1, rowIdx++,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.WEST );
//            GraphicsUtil.addGridBagComponent( contentPane, new JTextField( "r" ),
//                                              0, rowIdx,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.EAST );
            GraphicsUtil.addGridBagComponent( contentPane, rTF,
                                              1, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
//            GraphicsUtil.addGridBagComponent( contentPane, new JLabel( "theta" ),
//                                              0, rowIdx,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.EAST );
//            GraphicsUtil.addGridBagComponent( contentPane, thetaTF,
//                                              1, rowIdx++,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.WEST );
        }
        catch( AWTException e1 ) {
            e1.printStackTrace();
        }
        dialog.pack();
    }

    // implements java.awt.event.MouseListener
    public void mousePressed( MouseEvent e ) {
//        tx = panel.getTx();
        startPoint = tx.viewToModel( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
        endPoint = tx.viewToModel( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
        dialog.setLocation( (int)( panel.getLocationOnScreen().getX() + e.getPoint().getX() ),
                            (int)( panel.getLocationOnScreen().getY() + e.getPoint().getY() + 10 ) );

    }

    // implements java.awt.event.MouseListener
    public void mouseReleased( MouseEvent e ) {
        super.mouseReleased( e );
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseMoved( MouseEvent e ) {
        super.mouseMoved( e );
    }

    // implements java.awt.event.MouseMotionListener
    public void mouseDragged( MouseEvent e ) {

        endPoint = tx.viewToModel( (int)e.getPoint().getX(), (int)e.getPoint().getY() );
        double dx = endPoint.getX() - startPoint.getX();
        double dy = endPoint.getY() - startPoint.getY();
        double r = Math.sqrt( dx * dx + dy * dy );
        double theta = Math.toDegrees( Math.atan2( dy, dx ) );
        xTF.setText( "x: " + Double.toString( (int)dx ) );
        yTF.setText( "y: " + Double.toString( (int)dy ) );
        rTF.setText( "r: " + Double.toString( (int)r ) );
        thetaTF.setText( "theta: " + Double.toString( (int)theta ) );
    }

    public void setArmed( boolean armed ) {
        this.armed = armed;
        if( armed ) {
            panel.addMouseListener( this );
            panel.addMouseMotionListener( this );
            dialog.setVisible( true );
            panel.addGraphic( measuringGraphic, 9 );
        }
        else {
            panel.removeMouseListener( this );
            panel.removeMouseMotionListener( this );
            dialog.setVisible( false );
            panel.removeGraphic( measuringGraphic );
        }
    }

    //
    // Inner classes
    //
    private class MeasuringGraphic implements Graphic {

        public void paint( Graphics2D g2 ) {
            g2.setColor( Color.BLACK );
            g2.drawLine( (int)startPoint.getX() - 10, (int)startPoint.getY(),
                         (int)startPoint.getX() + 10, (int)startPoint.getY() );
            g2.drawLine( (int)startPoint.getX(), (int)startPoint.getY() - 10,
                         (int)startPoint.getX(), (int)startPoint.getY() + 10 );
            g2.drawLine( (int)endPoint.getX() - 10, (int)endPoint.getY(),
                         (int)endPoint.getX() + 10, (int)endPoint.getY() );
            g2.drawLine( (int)endPoint.getX(), (int)endPoint.getY() - 10,
                         (int)endPoint.getX(), (int)endPoint.getY() + 10 );
            g2.drawLine( (int)startPoint.getX(), (int)startPoint.getY(),
                         (int)endPoint.getX(), (int)endPoint.getY() );
        }
    }
}
