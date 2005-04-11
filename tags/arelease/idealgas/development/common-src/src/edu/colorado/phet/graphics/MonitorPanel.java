/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 16, 2003
 * Time: 9:35:25 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * This class of panel holds Swing components that display information about the state of
 * the PhysicalSystem. It is an Observer of the PhysicalSystem.
 */
public class MonitorPanel extends PhetMonitorPanel {

//    private ClockPanelLarge clockPanel = new ClockPanelLarge();

    public MonitorPanel() {

        this.setLayout( new FlowLayout());
//        this.add( clockPanel );
//        clockPanel.setVisible( false );
    }

//    public void update( Observable o, Object arg ) {
//        clockPanel.setClockReading( PhysicalSystem.instance().getSystemClock().getRunningTime() );
//    }
//
//    /**
//     *
//     */
//    public void setClockPanelVisible( boolean isVisible ) {
//        clockPanel.setVisible( isVisible );
//    }
//
//    /**
//     *
//     */
//    public boolean isClockPanelVisible() {
//        return clockPanel.isVisible();
//    }
//
//    /**
//     *
//     */
//    protected void setClockReading( String reading ) {
//        clockPanel.setClockReading( reading );
//    }
//
    /**
     *
     */
    public void clear() {
//        clockTF.setText( "0" );
    }

    public void update( Observable o, Object arg ) {
    }
}
