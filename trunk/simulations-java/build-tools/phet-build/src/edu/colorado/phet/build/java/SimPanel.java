package edu.colorado.phet.build.java;

import java.io.File;

import javax.swing.*;

import edu.colorado.phet.build.PhetProject;

public class SimPanel extends JPanel {
    private File basedir;
    private PhetProject selectedProject;

    public SimPanel( File basedir ) {
        this.basedir = basedir;
    }

    public void setSelectedProject( PhetProject selectedProject ) {
        this.selectedProject = selectedProject;
    }
}
