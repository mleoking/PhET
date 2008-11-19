package edu.colorado.phet.build.java;

import java.io.File;

import javax.swing.*;

public class PhetBuildGUIPanel extends JPanel {
    private SimPanel simPanel;

    public PhetBuildGUIPanel( File baseDir ) {
        final ProjectPanel projectPanel = new ProjectPanel( baseDir );
        add( projectPanel );

        simPanel = new SimPanel( baseDir );
        add( simPanel );

        projectPanel.addListener( new ProjectPanel.Listener() {
            public void notifyChanged() {
                simPanel.setSelectedProject( projectPanel.getSelectedProject() );
            }
        } );
    }
}
