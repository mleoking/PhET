/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.common.view.components.clockgui;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Observable;

public class ClockTimeReadout extends JPanel {

    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = NumberFormat.getInstance();

    public ClockTimeReadout() {

        setBackground(new Color(237, 225, 113));
        setBorder(BorderFactory.createRaisedBevelBorder());
        clockTF = new JTextField(8);
        Font clockFont = clockTF.getFont();
        clockTF.setFont(new Font(clockFont.getName(), Font.BOLD, 16));

        add(new JLabel( SimStrings.get( "ClockTimeReadout.RunningTimeLabel" ) + ": " ) );
        clockTF.setEditable(false);
        add(clockTF);
        clockFormat.setMaximumFractionDigits(1);
    }

    /**
     *
     */
    public void setClockReading(String reading) {
        clockTF.setText(reading);
    }

    public void setClockReading(double reading) {
        setClockReading(clockFormat.format(reading));
    }

    public void update(Observable o, Object arg) {
//        setClockReading(PhysicalSystem.instance().getSystemClock().getRunningTime());
    }

    /**
     *
     */
    public void setClockPanelVisible(boolean isVisible) {
        setVisible(isVisible);
    }

    /**
     *
     */
    public boolean isClockPanelVisible() {
        return isVisible();
    }

}
