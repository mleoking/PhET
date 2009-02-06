package edu.colorado.phet.ohm1d.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ShowPainterListener implements ActionListener {
    JCheckBox source;
    ShowPainters sp;

    public ShowPainterListener( JCheckBox source, ShowPainters sp ) {
        this.source = source;
        this.sp = sp;
    }

    public void actionPerformed( ActionEvent ae ) {
        sp.setShowPainters( source.isSelected() );
    }
}
