// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package edu.colorado.phet.semiconductor.macro:
//            MacroModule

public class MacroControlPanel extends JPanel {

    private JCheckBox lightOn;
    private JButton onePhoton;

    public MacroControlPanel( final MacroModule macroModule ) {
        JRadioButton jradiobutton = new JRadioButton( SimStrings.get( "MacroControlPanel.MetalRadioButton" ) );
        JRadioButton jradiobutton1 = new JRadioButton( SimStrings.get( "MacroControlPanel.PlasticRadioButton" ) );
        JRadioButton jradiobutton2 = new JRadioButton( SimStrings.get( "MacroControlPanel.PhotoconductorRadioButton" ) );
        jradiobutton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                macroModule.setConductor();
            }

        } );
        jradiobutton1.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                macroModule.setInsulator();
            }

        } );
        jradiobutton2.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                macroModule.setPhotoconductor();
            }

        } );
        setLayout( new BoxLayout( this, 1 ) );
        JPanel jpanel = new JPanel();
        jpanel.setLayout( new BoxLayout( jpanel, 1 ) );
        ButtonGroup buttongroup = new ButtonGroup();
        buttongroup.add( jradiobutton );
        buttongroup.add( jradiobutton1 );
        buttongroup.add( jradiobutton2 );
        jradiobutton.setSelected( true );
        jpanel.add( jradiobutton );
        jpanel.add( jradiobutton1 );
        jpanel.add( jradiobutton2 );
        Object obj = BorderFactory.createRaisedBevelBorder();
        obj = BorderFactory.createTitledBorder( ( (javax.swing.border.Border)( obj ) ), SimStrings.get( "MacroControlPanel.MaterialsBorder" ) );
        jpanel.setBorder( ( (javax.swing.border.Border)( obj ) ) );
        add( jpanel );
        lightOn = new JCheckBox( SimStrings.get( "MacroControlPanel.ShineLightCheckBox" ) );
        lightOn.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent changeevent ) {
                macroModule.setFlashlightOn( lightOn.isSelected() );
            }

        } );
        add( lightOn );
        onePhoton = new JButton( SimStrings.get( "MacroControlPanel.FirePhotonButton" ) );
        onePhoton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                macroModule.firePhoton();
            }

        } );
    }

    public void disableFlashlight() {
        lightOn.setEnabled( false );
        onePhoton.setEnabled( false );
        repaint();
    }

    public void enableFlashlight() {
        lightOn.setEnabled( true );
        onePhoton.setEnabled( true );
        repaint();
    }

}
