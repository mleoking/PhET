/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 11, 2003
 * Time: 7:19:41 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.idealgas.physics.body.Balloon;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Observer;
import java.util.Observable;
import java.text.NumberFormat;

public class BalloonPressureMonitor extends JPanel implements Observer {

    private Balloon balloon;
    private PressureGaugePanel insidePressurePanel;
    private PressureGaugePanel outsidePressurePanel;

    private JTextField insidePressureTF;
    private JTextField outsidePressureTF;
    private NumberFormat pressureFormat = NumberFormat.getInstance();


    public BalloonPressureMonitor() {
        init();
    }

    public BalloonPressureMonitor( Balloon balloon ){
        this.balloon = balloon;
        init();
        balloon.addObserver( this );
    }

    public void setBalloon( Balloon balloon ){
        this.balloon = balloon;
        balloon.addObserver( this );
    }

    private void init() {
        this.setPreferredSize( new Dimension( 200, 120 ) );
        Border border = new TitledBorder( "Balloon Pressures" );
        this.setBorder( border );

        pressureFormat.setMaximumFractionDigits( 2 );

        this.setLayout( new FlowLayout() );
        insidePressurePanel = new PressureGaugePanel();
//        outsidePressurePanel = new PressureGaugePanel();
        add( insidePressurePanel );
//        add( outsidePressurePanel );
        insidePressureTF = new JTextField( 8 );
//        outsidePressureTF = new JTextField( 8 );
        add( insidePressureTF );
//        add( outsidePressureTF );
    }

    public void update( Observable o, Object arg ) {
        if( o instanceof Balloon ) {
            Balloon balloon = (Balloon)o;
            float insidePressure = balloon.getInsidePressure();
            float outsidePressure = balloon.getOutsidePressure();
            insidePressurePanel.setLevel( insidePressure );

            insidePressureTF.setText( pressureFormat.format( insidePressure ));
//            outsidePressureTF.setText( pressureFormat.format( outsidePressure ));
        }

    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
    }
}
