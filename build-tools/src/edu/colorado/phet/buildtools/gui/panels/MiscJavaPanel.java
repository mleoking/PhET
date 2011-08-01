package edu.colorado.phet.buildtools.gui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.OldPhetServer;
import edu.colorado.phet.buildtools.VersionIncrement;
import edu.colorado.phet.buildtools.gui.ChangesPanel;
import edu.colorado.phet.buildtools.gui.PhetBuildGUI;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

public class MiscJavaPanel extends JPanel {
    private File trunk;

    private JavaProject project;
    private JRadioButton incrementMinor;
    private JRadioButton incrementMajor;

    public MiscJavaPanel( File trunk, JavaProject project ) {
        this( trunk, project, false );
    }

    public MiscJavaPanel( File trunk, JavaProject project, boolean testable ) {
        super( new BorderLayout() );

        this.trunk = trunk;
        this.project = project;

        JLabel title = new JLabel( project.getName() + " : " + project.getFullVersionString() );
        title.setHorizontalAlignment( SwingConstants.CENTER );
        add( title, BorderLayout.NORTH );

        add( new ChangesPanel( project ), BorderLayout.CENTER );

        JPanel controlPanel = new JPanel();

        if ( testable ) {
            JPanel testPanel = new VerticalLayoutPanel();
            testPanel.setBorder( BorderFactory.createTitledBorder( "Testing" ) );

            JButton testButton = new JButton( "Test" );
            testPanel.add( testButton );

            controlPanel.add( testPanel );

            testButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent actionEvent ) {
                    doTest();
                }
            } );
        }

        JPanel deployDevPanel = new VerticalLayoutPanel();
        deployDevPanel.setBorder( BorderFactory.createTitledBorder( "Dev Deploy" ) );
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
                doDeployTest();
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
            project.runSim( LocaleUtils.stringToLocale( "en" ), project.getName() );
        }
        else {
            System.out.println( "Errors on build" );
        }
    }

    private void doDeployDev() {
        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

        boolean generateOfflineJars = false;
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
                versionIncrement, PhetBuildGUI.getProductionWebsite() );
    }

    private void doDeployTest() {
        BuildLocalProperties buildLocalProperties = BuildLocalProperties.getInstance();

        new BuildScript( trunk, project ).newDeployToProductionAndDevelopment(
                PhetBuildGUI.getProductionWebsite(),
                OldPhetServer.DEFAULT_DEVELOPMENT_SERVER, buildLocalProperties.getDevAuthenticationInfo(), true, new VersionIncrement.UpdateProdMinor() );
        //new BuildScript( trunk, project ).newDeployToDev( OldPhetServer.PHET_SERVER_DEV, buildLocalProperties.getDevAuthenticationInfo(), true );
    }

}