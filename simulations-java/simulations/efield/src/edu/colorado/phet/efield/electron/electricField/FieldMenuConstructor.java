package edu.colorado.phet.efield.electron.electricField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.efield.electron.gui.popupMenu.MenuConstructor;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;

public class FieldMenuConstructor implements MenuConstructor {
    ChargeFieldSource cfs;
    Component paintMe;

    public FieldMenuConstructor( ChargeFieldSource cfs, Component paintMe ) {
        this.cfs = cfs;
        this.paintMe = paintMe;
    }

    public JMenu getMenu( Particle p ) {
        JMenu jm = new JMenu( SimStrings.get( "FieldMenuConstructor.ParticleMenuTitle" ) );
        ShowEField se = ( new ShowEField( cfs, p, paintMe ) );
        se.setSelected( !cfs.isIgnoring( p ) );
        jm.add( se );
        return jm;
    }

    public static class ShowEField extends JCheckBoxMenuItem implements ActionListener {
        ChargeFieldSource cfs;
        Particle p;
        Component paintMe;

        public ShowEField( ChargeFieldSource cfs, Particle p, Component paintMe ) {
            super( SimStrings.get( "FieldMenuConstructor.ShowContributionCheckBox" ), true );
            this.cfs = cfs;
            this.p = p;
            addActionListener( this );
            this.paintMe = paintMe;
        }

        public void actionPerformed( ActionEvent ae ) {
            if ( isSelected() ) {
                cfs.removeFromIgnore( p );
            }
            else {
                //util.Debug.traceln("Ignoring: "+p);
                cfs.ignore( p );
            }
            paintMe.repaint();
        }
    }
}
