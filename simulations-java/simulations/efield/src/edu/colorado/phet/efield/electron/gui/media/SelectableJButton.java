// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui.media;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

public class SelectableJButton extends JButton
        implements ActionListener {

    public SelectableJButton( String s, Icon icon, Icon icon1, boolean flag ) {
        super( s, icon1 );
        buttonGroup = new Vector();
        addActionListener( this );
        setSelected( flag );
    }

    public void addGroupElement( SelectableJButton selectablejbutton ) {
        buttonGroup.add( selectablejbutton );
    }

    public void actionPerformed( ActionEvent actionevent ) {
        setSelected( true );
        for ( int i = 0; i < buttonGroup.size(); i++ ) {
            SelectableJButton selectablejbutton = (SelectableJButton) buttonGroup.get( i );
            selectablejbutton.setSelected( false );
        }

    }

    public void setSelected( boolean flag ) {
        if ( flag ) {
            setBorder( BorderFactory.createLoweredBevelBorder() );
        }
        else {
            setBorder( BorderFactory.createRaisedBevelBorder() );
        }
        repaint();
    }

    Vector buttonGroup;
}
