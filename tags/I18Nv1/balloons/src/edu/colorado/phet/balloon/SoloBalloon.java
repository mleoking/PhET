package edu.colorado.phet.balloon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SoloBalloon implements ActionListener {
    JCheckBox box;
    BalloonPainter bp;

    public SoloBalloon( JCheckBox box, BalloonPainter bp ) {
        this.box = box;
        this.bp = bp;
    }

    public void actionPerformed( ActionEvent ae ) {
        if( box.isSelected() ) {
            bp.setVisible( false );
        }
        else {
            bp.setVisible( true );
        }
    }
}
