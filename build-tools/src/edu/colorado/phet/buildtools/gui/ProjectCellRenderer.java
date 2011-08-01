package edu.colorado.phet.buildtools.gui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * Render an entry of a project in the project list
 */
public class ProjectCellRenderer extends DefaultListCellRenderer {

    private File trunk;

    public ProjectCellRenderer( File trunk ) {
        this.trunk = trunk;
    }

    @Override
    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
        super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        ProjectList.ProjectListElement element = (ProjectList.ProjectListElement) value;
        PhetProject project = element.getProject();

        setText( project.getName() );
        try {
            if ( project instanceof JavaProject ) {
                setIcon( new ImageIcon( new File( trunk, "build-tools/data/build-tools/images/java.png" ).getCanonicalPath() ) );
            }
            else if ( project instanceof FlashSimulationProject ) {
                setIcon( new ImageIcon( new File( trunk, "build-tools/data/build-tools/images/flash.png" ).getCanonicalPath() ) );
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        return this;
    }
}
