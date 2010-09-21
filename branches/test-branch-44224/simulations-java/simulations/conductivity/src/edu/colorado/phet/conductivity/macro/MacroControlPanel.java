// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.conductivity.ConductivityApplication;
import edu.colorado.phet.conductivity.ConductivityResources;

// Referenced classes of package edu.colorado.phet.semiconductor.macro:
//            MacroModule

public class MacroControlPanel extends JPanel {

    private JCheckBox lightOn;
    private JButton onePhoton;

    public MacroControlPanel( final ConductivityApplication conductivityApplication ) {
        JRadioButton jradiobutton = new JRadioButton( ConductivityResources.getString( "MacroControlPanel.MetalRadioButton" ) );
        JRadioButton jradiobutton1 = new JRadioButton( ConductivityResources.getString( "MacroControlPanel.PlasticRadioButton" ) );
        JRadioButton jradiobutton2 = new JRadioButton( ConductivityResources.getString( "MacroControlPanel.PhotoconductorRadioButton" ) );
        jradiobutton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                conductivityApplication.setConductor();
            }

        } );
        jradiobutton1.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                conductivityApplication.setInsulator();
            }

        } );
        jradiobutton2.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                conductivityApplication.setPhotoconductor();
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
        obj = BorderFactory.createTitledBorder( ( (javax.swing.border.Border) ( obj ) ), ConductivityResources.getString( "MacroControlPanel.MaterialsBorder" ) );
        jpanel.setBorder( ( (javax.swing.border.Border) ( obj ) ) );
        add( jpanel );
        lightOn = new JCheckBox( ConductivityResources.getString( "MacroControlPanel.ShineLightCheckBox" ) );
        lightOn.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent changeevent ) {
                conductivityApplication.setFlashlightOn( lightOn.isSelected() );
            }

        } );
        add( lightOn );
        onePhoton = new JButton( ConductivityResources.getString( "MacroControlPanel.FirePhotonButton" ) );
        onePhoton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent actionevent ) {
                conductivityApplication.firePhoton();
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
