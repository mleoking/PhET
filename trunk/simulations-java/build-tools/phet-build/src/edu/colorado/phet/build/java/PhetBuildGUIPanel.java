package edu.colorado.phet.build.java;

import java.io.File;

import javax.swing.*;

public class PhetBuildGUIPanel extends JPanel {
    private ProjectPanel simPanel;
    private ProjectListPanel projectPanel;

    public PhetBuildGUIPanel( File baseDir ) {
//        setLayout( new FlowLayout( ) );
        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );

        projectPanel = new ProjectListPanel( baseDir );
        add( projectPanel );

        simPanel = new ProjectPanel( baseDir, projectPanel.getSelectedProject() );
        add( simPanel );

        projectPanel.addListener( new ProjectListPanel.Listener() {
            public void notifyChanged() {
                updateSelection();
            }
        } );
        updateSelection();
    }

    private void updateSelection() {
        simPanel.setProject( projectPanel.getSelectedProject() );
    }
}
