package edu.colorado.phet.buildtools.test.gui;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * Displays the changelog for a particular project
 */
public class TestChangesPanel extends JPanel {

    private PhetProject project;
    private JTextArea changesTextArea;

    public TestChangesPanel( PhetProject project ) {
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
