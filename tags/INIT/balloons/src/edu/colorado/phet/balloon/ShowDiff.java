package edu.colorado.phet.balloon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowDiff implements ActionListener {
    PlusPainter pp;
    MinusPainter mp;

    public ShowDiff( PlusPainter pp, MinusPainter mp ) {
        this.pp = pp;
        this.mp = mp;
    }

    public void actionPerformed( ActionEvent ae ) {
        pp.setPaint( pp.DIFF );
        mp.setPaint( pp.DIFF );
    }
}
