// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * A dialog that shows a 3D molecule structure, and allows the user to switch between representation modes
 */
public class JmolDialog extends JDialog {
    public JmolDialog( Frame owner, Molecule molecule, final String spacefillString, final String ballAndStickString, String loadingString ) {
        super( owner );

        setTitle( molecule.getDisplayName() );
        setSize( 410, 410 );

        JPanel container = new JPanel( new BorderLayout() );
        setContentPane( container );

        final JmolPanel jmolPanel = new JmolPanel( molecule, loadingString );
        getContentPane().add( jmolPanel, BorderLayout.CENTER );

        getContentPane().add( new JPanel() {{
            final ButtonGroup group = new ButtonGroup();
            add( new JRadioButton( spacefillString, true ) {{ // 50% size
                group.add( this );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        jmolPanel.setSpaceFill();
                    }
                } );
            }} );
            add( new JRadioButton( ballAndStickString, false ) {{
                group.add( this );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        jmolPanel.setBallAndStick();
                    }
                } );
            }} );
        }}, BorderLayout.SOUTH );

        container.paintImmediately( 0, 0, container.getWidth(), container.getHeight() );

        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    }

    public static JmolDialog displayMolecule3D( Frame frame, Molecule completeMolecule, String spaceFillString, String ballAndStickString, String loadingString ) {
        JmolDialog jmolDialog = new JmolDialog( frame, completeMolecule, spaceFillString, ballAndStickString, loadingString );
        SwingUtils.centerInParent( jmolDialog );
        jmolDialog.setVisible( true );
        return jmolDialog;
    }
}
