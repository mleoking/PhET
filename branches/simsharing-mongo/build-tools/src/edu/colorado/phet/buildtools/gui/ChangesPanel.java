package edu.colorado.phet.buildtools.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * Displays the changelog for a particular project
 */
public class ChangesPanel extends JPanel {

    private PhetProject project;
    private JTextArea changesTextArea;

    public ChangesPanel( PhetProject project ) {
        super( new GridLayout( 1, 1 ) );
        this.project = project;
        changesTextArea = new JTextArea( 10, 30 );
        changesTextArea.setEditable( false );
        JScrollPane changesScrollPane = new JScrollPane( changesTextArea );

        add( changesScrollPane );

        updateChanges();
    }

    public void updateChanges() {
        changesTextArea.setText( project.getChangesText() );
    }

}
