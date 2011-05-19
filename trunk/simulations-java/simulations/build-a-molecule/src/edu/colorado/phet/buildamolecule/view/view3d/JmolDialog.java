package edu.colorado.phet.buildamolecule.view.view3d;

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

        setTitle( molecule.getDisplayName() );
        setSize( 410, 410 );

        System.out.println( "Showing 3D dialog for " + molecule.getDisplayName() + " #" + molecule.cid );

        JPanel container = new JPanel( new BorderLayout() );
        setContentPane( container );

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

        container.paintImmediately( 0, 0, container.getWidth(), container.getHeight() );

        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    }

    public static JmolDialog displayMolecule3D( Frame frame, CompleteMolecule completeMolecule ) {
        JmolDialog jmolDialog = new JmolDialog( frame, completeMolecule );
        SwingUtils.centerDialogInParent( jmolDialog );
        return jmolDialog;
    }
}
