package edu.colorado.phet.semiconductor_semi.macro;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor_semi.SemiconductorApplication;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        addJButton( SimStrings.get( "DiodeControlPanel.OneButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.setSingleSection();
            }
        }, false );
        addJButton( SimStrings.get( "DiodeControlPanel.TwoButton" ), new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                application.setDoubleSection();
            }
        }, true );
//        addJButton( SimStrings.get( "DiodeControlPanel.ThreeButton" ), new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.setTripleSection();
//            }
//        }, false );
        Border b = BorderFactory.createTitledBorder( SimStrings.get( "DiodeControlPanel.SegmentBorder" ) );
        pan.setBorder( b );

        setBackground( new Color( 240, 230, 210 ) );
        add( pan );
        gateCheckBox = new JCheckBox( SimStrings.get( "DiodeControlPanel.GateCheckBox" ) );
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
        if( !gateCheckBox.isSelected() ) {
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
