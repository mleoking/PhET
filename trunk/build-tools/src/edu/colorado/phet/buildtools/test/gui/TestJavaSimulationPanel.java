package edu.colorado.phet.buildtools.test.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.VersionIncrement;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class TestJavaSimulationPanel extends JPanel {
    private File trunk;

    private JavaSimulationProject project;
    private TestLocaleListPanel localeList;
    private JList simulationList;
    private JCheckBox deployDevJARs;
    private JRadioButton incrementMinor;
    private JRadioButton incrementMajor;

    public TestJavaSimulationPanel( File trunk, JavaSimulationProject project ) {
        super( new BorderLayout() );

        this.trunk = trunk;
        this.project = project;

        JLabel title = new JLabel( project.getName() + " : " + project.getFullVersionString() );
        title.setHorizontalAlignment( SwingConstants.CENTER );
        add( title, BorderLayout.NORTH );

        localeList = new TestLocaleListPanel( project.getLocales() );

        simulationList = new JList( project.getSimulationNames() );
        simulationList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        simulationList.setSelectedIndex( 0 );
        JScrollPane simulationScrollPane = new JScrollPane( simulationList );
        simulationScrollPane.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
        simulationScrollPane.setMinimumSize( new Dimension( 150, 0 ) );
        simulationScrollPane.setMaximumSize( new Dimension( 150, 10000 ) );
        simulationScrollPane.setPreferredSize( new Dimension( 150, 400 ) );

        JPanel leftPanel = new JPanel( new GridLayout( 2, 1 ) );
        leftPanel.add( localeList );
        leftPanel.add( simulationScrollPane );

        add( leftPanel, BorderLayout.WEST );


        add( new TestChangesPanel( project ), BorderLayout.CENTER );

        JPanel controlPanel = new JPanel();

        JPanel testPanel = new VerticalLayoutPanel();
        testPanel.setBorder( BorderFactory.createTitledBorder( "Testing" ) );

        JButton testButton = new JButton( "Test" );
        testPanel.add( testButton );

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
        boolean confirm = TestGUI.confirmProdDeploy( project, PhetServer.PRODUCTION );

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

        new BuildScript( trunk, project ).deployProd( buildLocalProperties.getDevAuthenticationInfo(), buildLocalProperties.getProdAuthenticationInfo(), versionIncrement );
    }

}