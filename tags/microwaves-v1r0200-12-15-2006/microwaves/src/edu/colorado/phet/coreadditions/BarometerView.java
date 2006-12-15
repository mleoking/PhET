/**
 * Class: BarometerView
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 25, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BarometerView /*implements InteractiveGraphic */{

    JDialog display;
    private BufferedImage crosshairs;
    int targetWidth = 20;
    int targetHeight = 20;
    private JPanel crosshairPanel;

    public BarometerView() {
        display = new JDialog();
        display.setUndecorated( true );
        Container contentPane = display.getContentPane();
        contentPane.setLayout( new BorderLayout() );
        crosshairs = new BufferedImage( targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)crosshairs.getGraphics();
        g2.setColor( Color.black );
        g2.drawOval( 0, 0, targetWidth / 2, targetHeight / 2 );
        crosshairPanel = new JPanel( null );
        contentPane.add( crosshairPanel, BorderLayout.WEST );
        JPanel readoutPane = new JPanel();
        readoutPane.add( new JLabel( SimStrings.get( "BarometerView.PressureLabel" ) + ": " ));
        contentPane.add( readoutPane, BorderLayout.CENTER );
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    public void paint( Graphics2D graphics2D ) {
        display.setLocation( 500, 500 );
        graphics2D.drawImage( crosshairs, (int)crosshairPanel.getBounds().getMinX(), (int)crosshairPanel.getBounds().getMinY(), null );
    }

}
