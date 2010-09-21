package edu.colorado.phet.semiconductor.macro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.semiconductor.SemiconductorApplication;
import edu.colorado.phet.semiconductor.SemiconductorResources;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:13:50 PM
 */
public class DiodeControlPanel extends JPanel {
    ButtonGroup bg = new ButtonGroup();
    private JPanel pan = new JPanel();
    private JCheckBox gateCheckBox;
    private SemiconductorApplication application;

    public DiodeControlPanel( final SemiconductorApplication application ) {
        this.application = application;
        pan.setLayout( new BoxLayout( pan, BoxLayout.Y_AXIS ) );

        addJButton( SemiconductorResources.getString( "DiodeControlPanel.OneButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.setSingleSection();
            }
        }, false );
        addJButton( SemiconductorResources.getString( "DiodeControlPanel.TwoButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.setDoubleSection();
            }
        }, true );
//        addJButton( SemiconductorResources.getString( "DiodeControlPanel.ThreeButton" ), new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.setTripleSection();
//            }
//        }, false );
        Border b = BorderFactory.createTitledBorder( SemiconductorResources.getString( "DiodeControlPanel.SegmentBorder" ) );
        pan.setBorder( b );

        setBackground( new Color( 240, 230, 210 ) );
        add( pan );
        gateCheckBox = new JCheckBox( SemiconductorResources.getString( "DiodeControlPanel.GateCheckBox" ) );
        gateCheckBox.setSelected( false );
        gateCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateGate();
            }
        } );
        updateGate();
//        add( gateCheckBox );
    }

    private void updateGate() {
//        SemiconductorModule module = ...;
//        JCheckBox gateCheckBox = ...;
        application.getMagnetGraphic().setVisible( gateCheckBox.isSelected() );
        if ( !gateCheckBox.isSelected() ) {
            application.releaseGate();
        }
    }

    private void addJButton( String s, ActionListener actionListener, boolean selected ) {
        JRadioButton button = new JRadioButton( s );
        button.addActionListener( actionListener );
        button.setSelected( selected );
        bg.add( button );
        pan.add( button );
    }
}
