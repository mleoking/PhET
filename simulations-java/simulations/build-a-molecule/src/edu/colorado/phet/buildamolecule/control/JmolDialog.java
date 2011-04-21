package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * A dialog that shows a 3D molecule structure, and allows the user to switch between representation modes
 */
public class JmolDialog extends JDialog {
    public JmolDialog( Frame owner, CompleteMolecule molecule ) {
        super( owner );

        setTitle( molecule.getCommonName() );
        setSize( 410, 410 );

        setContentPane( new JPanel( new BorderLayout() ) );

        final JmolPanel jmolPanel = new JmolPanel( molecule );
        getContentPane().add( jmolPanel, BorderLayout.CENTER );

        getContentPane().add( new JPanel() {{
            final ButtonGroup group = new ButtonGroup();
            add( new JRadioButton( BuildAMoleculeStrings.JMOL_3D_SPACE_FILLING, true ) {{ // 50% size
                group.add( this );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        jmolPanel.setSpaceFill();
                    }
                } );
            }} );
            add( new JRadioButton( BuildAMoleculeStrings.JMOL_3D_BALL_AND_STICK, false ) {{
                group.add( this );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        jmolPanel.setBallAndStick();
                    }
                } );
            }} );
        }}, BorderLayout.SOUTH );

        setVisible( true );
    }

    public static void displayMolecule3D( Frame frame, CompleteMolecule completeMolecule ) {
        JDialog jmolDialog = new JmolDialog( frame, completeMolecule );
        SwingUtils.centerDialogInParent( jmolDialog );
    }
}
