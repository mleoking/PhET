/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class ClockPanelLarge extends JPanel implements ClockTickListener {

    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = NumberFormat.getInstance();

    public ClockPanelLarge( AbstractClock clock ) {

        clock.addClockTickListener( this );

        setBackground( new Color( 237, 225, 113 ));
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 5 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ));

        add( new JLabel( "Running time: "));
        clockTF.setEditable( false );
        add( clockTF );
        clockFormat.setMaximumFractionDigits( 1 );
    }

    /**
     *
     */
    public void setClockReading( String reading ) {
        clockTF.setText( reading );
    }

    public void setClockReading( float  reading ) {
        setClockReading( clockFormat.format( reading ));
    }

    public void clockTicked( AbstractClock c, double dt ) {
        String s = Double.toString( c.getRunningTime() );
        setClockReading( s );
    }

    /**
     *
     */
    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    /**
     *
     */
    public boolean isClockPanelVisible() {
        return isVisible();
    }

}
