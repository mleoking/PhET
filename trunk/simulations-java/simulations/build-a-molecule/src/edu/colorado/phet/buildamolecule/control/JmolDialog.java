package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class JmolDialog extends JDialog {
    public JmolDialog( Frame owner, CompleteMolecule molecule ) {
        super( owner );

        setTitle( molecule.getCommonName() ); // TODO: i18n?
        setSize( 410, 410 );

        JmolPanel jmolPanel = new JmolPanel( molecule );
        getContentPane().add( jmolPanel );

        setVisible( true );
    }

    public static void displayMolecule3D( Frame frame, CompleteMolecule completeMolecule ) {
        JDialog jmolDialog = new JmolDialog( frame, completeMolecule );
        SwingUtils.centerDialogInParent( jmolDialog );
    }
}
