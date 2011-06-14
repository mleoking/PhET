// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.particleFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

// Referenced classes of package edu.colorado.phet.efield.particleFactory:
//            ParticlePropertyDialog

public class ShowParticlePropertyDialog
        implements ActionListener {
    public static class HideListener
            implements ActionListener {

        public void actionPerformed( ActionEvent actionevent ) {
            jf.setVisible( false );
        }

        Component jf;

        public HideListener( Component component ) {
            jf = component;
        }
    }


    public ShowParticlePropertyDialog( double d, double d1 ) {
        ppd = new ParticlePropertyDialog( d, d1 );
        jf = new JFrame();
        jf.setContentPane( ppd );
        ppd.getDoneButton().addActionListener( new HideListener( jf ) );
        jf.pack();
    }

    public ParticlePropertyDialog getDialog() {
        return ppd;
    }

    public void actionPerformed( ActionEvent actionevent ) {
        jf.setVisible( true );
    }

    ParticlePropertyDialog ppd;
    JFrame jf;
}
