package edu.colorado.phet.balloons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class TwoBalloonsHandler implements ActionListener {
    JCheckBox box;
    BalloonPainter bp;

    public TwoBalloonsHandler( JCheckBox box, BalloonPainter bp ) {
        this.box = box;
        this.bp = bp;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( box.isSelected() ) {
            bp.setVisible( true );
        }
        else {
            bp.setVisible( false );
        }
    }
}
