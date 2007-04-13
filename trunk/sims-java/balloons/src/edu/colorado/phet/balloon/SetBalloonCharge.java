package edu.colorado.phet.balloon;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetBalloonCharge implements ActionListener {
    BalloonImage bp;
    JCheckBox b;
    JRadioButton showNone;
    JRadioButton showDiff;
    JRadioButton showAll;

    public SetBalloonCharge( JCheckBox b, BalloonImage bp, JRadioButton showNone, JRadioButton showDiff, JRadioButton showAll ) {
        this.showNone = showNone;
        this.showDiff = showDiff;
        this.showAll = showAll;
        this.b = b;
        this.bp = bp;
    }

    public void actionPerformed( ActionEvent ae ) {
        if( b.isSelected() )//ignore
        {
            bp.setShowCharged( false );
        }
        else {
            if( showAll.isSelected() ) {
                bp.setShowCharged( true );
            }
            else {
                bp.setShowCharged( false );
            }
        }
    }
}
