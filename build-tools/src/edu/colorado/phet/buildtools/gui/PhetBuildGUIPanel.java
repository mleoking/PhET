package edu.colorado.phet.buildtools.gui;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.gui.panels.FlashSimulationPanel;
import edu.colorado.phet.buildtools.gui.panels.JavaSimulationPanel;
import edu.colorado.phet.buildtools.gui.panels.MiscJavaPanel;
import edu.colorado.phet.buildtools.gui.panels.StatisticsPanel;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.java.projects.TranslationUtilityProject;
import edu.colorado.phet.buildtools.statistics.StatisticsProject;

/**
 * The main GUI of the PhET Build GUI
 * <p/>
 * Contains a project list on the left, and a project panel on the right
 */
public class PhetBuildGUIPanel extends JSplitPane {
    private ProjectList projectList;
    private Container projectPanel;

    private PhetProject cachedProject;

    public PhetBuildGUIPanel( final File trunk ) {
        super( JSplitPane.HORIZONTAL_SPLIT );

        projectList = new ProjectList( trunk );
        JScrollPane projectListHolder = new JScrollPane( projectList );
        projectListHolder.setBorder( BorderFactory.createTitledBorder( "Projects" ) );

        projectPanel = new JPanel( new GridLayout( 1, 1 ) );

        setLeftComponent( projectListHolder );
        setRightComponent( projectPanel );

        projectList.addListener( new ProjectList.Listener() {
            public void notifyChanged() {
                PhetProject project = projectList.getSelectedProject();
                if ( project == cachedProject ) {
                    return;
                }
                cachedProject = project;
                //System.out.println( "Changing project to " + project );
                // TODO: consider refactoring project panels to be selected based on the project itself, instead of handling directly in the GUI
                if ( project instanceof FlashSimulationProject ) {
                    projectPanel = new FlashSimulationPanel( trunk, (FlashSimulationProject) project );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof JavaSimulationProject ) {
                    projectPanel = new JavaSimulationPanel( trunk, (JavaSimulationProject) project );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof StatisticsProject ) {
                    projectPanel = new StatisticsPanel( trunk, (StatisticsProject) project );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof TranslationUtilityProject ) {
                    projectPanel = new MiscJavaPanel( trunk, (JavaProject) project, true );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof JavaProject ) {
                    projectPanel = new MiscJavaPanel( trunk, (JavaProject) project, false );
                    setRightComponent( projectPanel );
                }
                else {
                    setRightComponent( null );
                }
            }
        } );

    }

    public ProjectList getProjectList() {
        return projectList;
    }
}
