// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balloons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class SetBalloonCharge implements ActionListener {
    BalloonImage bp;
    JCheckBox b;
    private JRadioButton showAll;

    public SetBalloonCharge( JCheckBox b, BalloonImage bp, JRadioButton showAll ) {
        this.showAll = showAll;
        this.b = b;
        this.bp = bp;
    }

    public void actionPerformed( ActionEvent ae ) {
        if ( b.isSelected() )//ignore
        {
            bp.setShowCharged( false );
        }
        else {
            if ( showAll.isSelected() ) {
                bp.setShowCharged( true );
            }
            else {
                bp.setShowCharged( false );
            }
        }
    }
}
