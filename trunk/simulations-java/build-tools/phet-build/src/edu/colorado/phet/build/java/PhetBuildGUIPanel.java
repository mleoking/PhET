package edu.colorado.phet.build.java;

import java.io.File;

import javax.swing.*;

public class PhetBuildGUIPanel extends JPanel {
    private SimPanel simPanel;
    private ProjectListPanel projectPanel;

    public PhetBuildGUIPanel( File baseDir ) {
        projectPanel = new ProjectListPanel( baseDir );
        add( projectPanel );

        simPanel = new SimPanel( baseDir,projectPanel.getSelectedProject() );
        add( simPanel );

        projectPanel.addListener( new ProjectListPanel.Listener() {
            public void notifyChanged() {
                updateSelection();
            }
        } );
        updateSelection();
    }

    private void updateSelection() {
        simPanel.setSelectedProject( projectPanel.getSelectedProject() );
    }
}
