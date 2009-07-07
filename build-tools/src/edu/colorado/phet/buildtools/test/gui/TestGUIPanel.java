package edu.colorado.phet.buildtools.test.gui;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.java.projects.TranslationUtilityProject;
import edu.colorado.phet.buildtools.statistics.StatisticsProject;

public class TestGUIPanel extends JSplitPane {
    private TestProjectList projectList;
    private Container projectPanel;

    private PhetProject cachedProject;

    public TestGUIPanel( final File trunk ) {
        super( JSplitPane.HORIZONTAL_SPLIT );

        projectList = new TestProjectList( trunk );
        JScrollPane projectListHolder = new JScrollPane( projectList );
        projectListHolder.setBorder( BorderFactory.createTitledBorder( "Projects" ) );

        projectPanel = new JPanel( new GridLayout( 1, 1 ) );
        //projectPanel.add( new JButton( "Test" ) );


        setLeftComponent( projectListHolder );
        setRightComponent( projectPanel );

        projectList.addListener( new TestProjectList.Listener() {
            public void notifyChanged() {
                PhetProject project = projectList.getSelectedProject();
                if ( project == cachedProject ) {
                    return;
                }
                cachedProject = project;
                System.out.println( "Changing project to " + project );
                if ( project instanceof FlashSimulationProject ) {
                    projectPanel = new TestFlashSimulationPanel( trunk, (FlashSimulationProject) project );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof JavaSimulationProject ) {
                    projectPanel = new TestJavaSimulationPanel( trunk, (JavaSimulationProject) project );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof StatisticsProject ) {
                    projectPanel = new TestStatisticsPanel( trunk, (StatisticsProject) project );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof TranslationUtilityProject ) {
                    projectPanel = new TestMiscJavaPanel( trunk, (JavaProject) project, true );
                    setRightComponent( projectPanel );
                }
                else if ( project instanceof JavaProject ) {
                    projectPanel = new TestMiscJavaPanel( trunk, (JavaProject) project, false );
                    setRightComponent( projectPanel );
                }
                else {
                    setRightComponent( null );
                }
            }
        } );

    }

    public TestProjectList getProjectList() {
        return projectList;
    }
}
