/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 11, 2003
 * Time: 7:25:17 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.view.components.OrderOfMagnitudeSpinner;
import edu.colorado.phet.instrumentation.BarGauge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

public class PressureGaugePanel extends JPanel {

    private BarGauge pressureGauge;
    private OrderOfMagnitudeSpinner pressureSpinner;
    private int scaleFactor = 1;

    PressureGaugePanel() {
        pressureGauge = new BarGauge( new Point2D.Float( 10, 0 ), 80, new Color( 0, 175, 0 ), 10, true, 0, 1000 );
        pressureSpinner = new OrderOfMagnitudeSpinner( 0.001f, 1000 );

        // Add radio buttons for scale
        JPanel scaleFactorPanel = new JPanel( new GridLayout( 2, 1 ) );
        Action scaleFactor1 = new AbstractAction( "1X" ) {
            public void actionPerformed( ActionEvent evt ) {
                scaleFactor = 1;
            }
        };
        Action scaleFactor10 = new AbstractAction( "10X" ) {
            public void actionPerformed( ActionEvent evt ) {
                scaleFactor = 10;
            }
        };
        JRadioButton scaleFactor1RB = new JRadioButton( scaleFactor1 );
        JRadioButton scaleFactor10RB = new JRadioButton( scaleFactor10 );
        ButtonGroup scaleFactorBG = new ButtonGroup();
        scaleFactorBG.add( scaleFactor1RB );
        scaleFactorBG.add( scaleFactor10RB );
        ButtonModel model = scaleFactor1RB.getModel();
        scaleFactorBG.setSelected( model, true );
        scaleFactorPanel.add( scaleFactor1RB );
        scaleFactorPanel.add( scaleFactor10RB );
        this.add( scaleFactorPanel );
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        pressureGauge.paint( (Graphics2D)g );
    }

    public void setLevel( float level ) {
        pressureGauge.setLevel( level / scaleFactor );
    }
}

