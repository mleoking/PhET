// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balloons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowAll implements ActionListener {
    PlusPainter pp;
    MinusPainter mp;

    public ShowAll( PlusPainter pp, MinusPainter mp ) {
        this.pp = pp;
        this.mp = mp;
    }

    public void actionPerformed( ActionEvent ae ) {
        pp.setPaint( pp.ALL );
        mp.setPaint( mp.ALL );
    }
}
