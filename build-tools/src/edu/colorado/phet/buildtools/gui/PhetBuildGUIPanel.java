package edu.colorado.phet.buildtools.gui;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;

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

    public void addListener( ProjectListPanel.Listener listener ) {
        projectPanel.addListener( listener );
    }

    public PhetProject getSelectedProject() {
        return projectPanel.getSelectedProject();
    }

    private void updateSelection() {
        simPanel.setProject( getSelectedProject() );
    }
}
