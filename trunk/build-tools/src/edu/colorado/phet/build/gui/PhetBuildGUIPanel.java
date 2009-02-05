package edu.colorado.phet.build.gui;

import java.awt.*;
import java.io.File;

import javax.swing.*;

public class PhetBuildGUIPanel extends JPanel {
    private ProjectPanel simPanel;
    private ProjectListPanel projectPanel;

    public PhetBuildGUIPanel( File trunk ) {
        setLayout( new BorderLayout() );
        projectPanel = new ProjectListPanel( trunk );
        simPanel = new ProjectPanel( trunk, projectPanel.getSelectedProject() );

        add( Boxer.horizontalBox( projectPanel, simPanel ), BorderLayout.CENTER );

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
