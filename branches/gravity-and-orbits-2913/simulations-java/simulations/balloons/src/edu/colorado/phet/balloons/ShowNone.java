// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balloons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowNone implements ActionListener {
    PlusPainter pp;
    MinusPainter mp;

    public ShowNone( PlusPainter pp, MinusPainter mp ) {
        this.pp = pp;
        this.mp = mp;
    }

    public void actionPerformed( ActionEvent ae ) {
        pp.setPaint( pp.NONE );
        mp.setPaint( mp.NONE );
    }
}
