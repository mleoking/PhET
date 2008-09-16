/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Nov 13, 2002
 * Time: 10:22:17 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.ohm1d.volt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ShowInsideBattery implements ActionListener {
    private JCheckBox source;
    private BatteryPainter bp;

    public ShowInsideBattery( JCheckBox source, BatteryPainter bp ) {
        this.source = source;
        this.bp = bp;
    }

    public void actionPerformed( ActionEvent e ) {
        bp.setTransparent( source.isSelected() );
    }
}
