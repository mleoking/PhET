package edu.colorado.phet.buildtools.gui.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.OldPhetServer;
import edu.colorado.phet.buildtools.VersionIncrement;
import edu.colorado.phet.buildtools.gui.ChangesPanel;
import edu.colorado.phet.buildtools.gui.LocaleListPanel;
import edu.colorado.phet.buildtools.gui.PhetBuildGUI;
import edu.colorado.phet.buildtools.gui.PhetBuildGUIProperties;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.preprocessor.ResourceGenerator;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class JavaSimulationPanel extends JPanel {
    private File trunk;

    private JavaSimulationProject project;
    private LocaleListPanel localeList;
    private JList simulationList;
    private JCheckBox deployDevJARs;
    private JRadioButton incrementMinor;
    private JRadioButton incrementMajor;

    public JavaSimulationPanel( final File trunk, final JavaSimulationProject project ) {
        super( new BorderLayout() );

        this.trunk = trunk;
        this.project = project;

        final PhetBuildGUIProperties properties = PhetBuildGUIProperties.getInstance();

        JLabel title = new JLabel( project.getName() + " : " + project.getFullVersionString() );
        title.setHorizontalAlignment( SwingConstants.CENTER );
        add( title, BorderLayout.NORTH );

        localeList = new LocaleListPanel( project.getLocales() );

        simulationList = new JList( project.getSimulationNames() );
        simulationList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        //Provide support for saving/loading the selected sim, see #2336
        int selected = Arrays.asList( project.getSimulationNames() ).indexOf( properties.getSimSelected() );
        if ( selected < 0 ) {
            selected = 0;//just choose the first item if no item was selected or if the name was not found
        }
        simulationList.setSelectedIndex( selected );
        simulationList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                properties.setSimSelected( simulationList.getSelectedValue().toString() );
                System.out.println( "Saved sim selection: " + properties.getSimSelected() );
            }
        } );

        JScrollPane simulationScrollPane = new JScrollPane( simulationList );
        simulationScrollPane.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
        simulationScrollPane.setMinimumSize( new Dimension( 150, 0 ) );
        simulationScrollPane.setMaximumSize( new Dimension( 150, 10000 ) );
        simulationScrollPane.setPreferredSize( new Dimension( 150, 400 ) );

        JPanel leftPanel = new JPanel( new GridLayout( 2, 1 ) );
        leftPanel.add( localeList );
        leftPanel.add( simulationScrollPane );

        add( leftPanel, BorderLayout.WEST );


        add( new ChangesPanel( project ), BorderLayout.CENTER );

        JPanel controlPanel = new JPanel();

        JPanel testPanel = new VerticalLayoutPanel();
        testPanel.setBorder( BorderFactory.createTitledBorder( "Testing" ) );

        JButton testButton = new JButton( "Test" );
        testPanel.add( testButton );

        //Add a button that allows the user to generate resources
        controlPanel.add( new JButton( "Generate Resources" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    try {
                        new ResourceGenerator( trunk ).generateResources( "simulations-java/simulations/" + simulationList.getSelectedValue() );
                    }
                    catch ( IOException ioException ) {
                        ioException.printStackTrace();
                    }
                }
            } );
        }} );

        controlPanel.add( testPanel );

        JPanel deployDevPanel = new VerticalLayoutPanel();
        deployDevPanel.setBorder( BorderFactory.createTitledBorder( "Dev Deploy" ) );
        deployDevJARs = new JCheckBox( "Generate locale JARs" );
        deployDevJARs.setSelected( false );
        deployDevPanel.add( deployDevJARs );
        JButton deployDevButton = new JButton( "Deploy Dev" );
        deployDevPanel.add( deployDevButton );

        controlPanel.add( deployDevPanel );

        JPanel deployProdPanel = new VerticalLayoutPanel();
        deployProdPanel.setBorder( BorderFactory.createTitledBorder( "Production Deploy" ) );
        incrementMinor = new JRadioButton( "Increment minor version" );
        incrementMajor = new JRadioButton( "Increment major version " );
        ButtonGroup incrementGroup = new ButtonGroup();
        incrementGroup.add( incrementMinor );
        incrementGroup.add( incrementMajor );
        deployProdPanel.add( incrementMinor );
        deployProdPanel.add( incrementMajor );
        incrementMinor.setSelected( true );
        JButton deployProdButton = new JButton( "Deploy Dev & Prod" );
        deployProdPanel.add( deployProdButton );

        controlPanel.add( deployProdPanel );

        JPanel testProdPanel = new VerticalLayoutPanel();
        testProdPanel.setBorder( BorderFactory.createTitledBorder( "Test" ) );
        JButton testProdButton = new JButton( "Test Deploy" );
        testProdPanel.add( testProdButton );
//see #2290
        controlPanel.add( testProdPanel );

        add( controlPanel, BorderLayout.SOUTH );


        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doTest();
            }
        } );

        deployDevButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployDev();
            }
        } );

        deployProdButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployProd();
            }
        } );

        testProdButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doWicketTest();
            }
        } );
    }

    private BuildScript getBuildScript() {
        return new BuildScript( trunk, project );
    }

    private void doTest() {
        getBuildScript().clean();
        boolean success = getBuildScript().build();
        if ( success ) {
            project.runSim( localeList.getSelectedLocale(), (String) simulationList.getSelectedValue() );
        }
        else {
            System.out.println( "Errors on build" );
        }
    }

    private void doDeployDev() {
        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

        boolean generateOfflineJars = deployDevJARs.isSelected();
        System.out.println( "genoj=" + generateOfflineJars );
        getBuildScript().deployDev( buildLocalProperties.getDevAuthenticationInfo(), generateOfflineJars );
    }

    private void doDeployProd() {
        boolean confirm = PhetBuildGUI.confirmProdDeploy( project, PhetBuildGUI.getProductionWebsite().getOldProductionServer() );

        if ( !confirm ) {
            System.out.println( "Cancelled" );
            return;
        }

        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

        VersionIncrement versionIncrement = null;
        if ( incrementMinor.isSelected() ) {
            versionIncrement = new VersionIncrement.UpdateProdMinor();
        }
        else if ( incrementMajor.isSelected() ) {
            versionIncrement = new VersionIncrement.UpdateProdMajor();
        }

        new BuildScript( trunk, project ).deployToDevelopmentAndProductionServers(
                buildLocalProperties.getDevAuthenticationInfo(),
                PhetBuildGUI.getProductionWebsite().getServerAuthenticationInfo( buildLocalProperties ),
                versionIncrement, PhetBuildGUI.getProductionWebsite()
        );
    }

    private void doWicketTest() {
//        boolean confirm = PhetBuildGUI.confirmProdDeploy( project, OldPhetServer.FIGARO );
//
//        if ( !confirm ) {
//            System.out.println( "Cancelled" );
//            return;
//        }

        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

//        VersionIncrement versionIncrement = null;
//        if ( incrementMinor.isSelected() ) {
//            versionIncrement = new VersionIncrement.UpdateProdMinor();
//        }
//        else if ( incrementMajor.isSelected() ) {
//            versionIncrement = new VersionIncrement.UpdateProdMajor();
//        }

//        new BuildScript( trunk, project ).deployToDevelopmentAndProductionServers(
//                buildLocalProperties.getDevAuthenticationInfo(),
//                PhetWebsite.FIGARO.getServerAuthenticationInfo( buildLocalProperties ),
//                versionIncrement, PhetWebsite.FIGARO );

        // NOT WORKING!
        new BuildScript( trunk, project ).newDeployToProductionAndDevelopment(
                PhetBuildGUI.getProductionWebsite(),
                OldPhetServer.DEFAULT_DEVELOPMENT_SERVER, buildLocalProperties.getDevAuthenticationInfo(), true, new VersionIncrement.UpdateProdMinor() );
        //new BuildScript( trunk, project ).newDeployToDev( OldPhetServer.PHET_SERVER_DEV, buildLocalProperties.getDevAuthenticationInfo(), true );
    }

}