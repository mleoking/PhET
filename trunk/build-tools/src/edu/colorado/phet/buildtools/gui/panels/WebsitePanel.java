package edu.colorado.phet.buildtools.gui.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.gui.ChangesPanel;
import edu.colorado.phet.buildtools.java.projects.WebsiteProject;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * Panel that gives GUI options for the wicket-based website
 */
public class WebsitePanel extends JPanel {
    private File trunk;

    private WebsiteProject project;
    private JRadioButton incrementMinor;
    private JRadioButton incrementMajor;

    public WebsitePanel( File trunk, WebsiteProject project ) {
        super( new BorderLayout() );

        this.trunk = trunk;
        this.project = project;

        JLabel title = new JLabel( project.getName() + " : " + project.getFullVersionString() );
        title.setHorizontalAlignment( SwingConstants.CENTER );
        add( title, BorderLayout.NORTH );

        add( new ChangesPanel( project ), BorderLayout.CENTER );

        JPanel controlPanel = new JPanel();

        JPanel warPanel = new VerticalLayoutPanel();
        warPanel.setBorder( BorderFactory.createTitledBorder( "WAR" ) );

        JButton buildButton = new JButton( "Build" );
        warPanel.add( buildButton );

        controlPanel.add( warPanel );

        buildButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doBuildWar();
            }
        } );

        JPanel deployLocalPanel = new VerticalLayoutPanel();
        deployLocalPanel.setBorder( BorderFactory.createTitledBorder( "Dev Deploy" ) );

        JButton testButton = new JButton( "Deploy Dev" );
        deployLocalPanel.add( testButton );

        controlPanel.add( deployLocalPanel );

        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployLocal();
            }
        } );

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
        JButton deployProdButton = new JButton( "Deploy Prod" );
        deployProdPanel.add( deployProdButton );

        controlPanel.add( deployProdPanel );

        add( controlPanel, BorderLayout.SOUTH );

        deployProdButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                doDeployProd();
            }
        } );
    }

    private BuildScript getBuildScript() {
        return new BuildScript( trunk, project );
    }

    private void doBuildWar() {
        getBuildScript().clean();
        boolean success = getBuildScript().build();
        if ( !success ) {
            System.out.println( "Errors on build" );
        }
    }

    private void doDeployLocal() {
        BuildLocalProperties props = BuildLocalProperties.getInstance();

        getBuildScript().clean();

        boolean success = getBuildScript().build();

        if ( !success ) {
            System.out.println( "Errors on build" );
            return;
        }

        success = project.deploy( props.getWebsiteDevHost(), props.getWebsiteDevProtocol(), props.getWebsiteDevAuthenticationInfo(), props.getWebsiteDevManagerAuthenticationInfo(), true );

        if ( !success ) {
            System.out.println( "Errors on deploy" );
        }
    }

    private void doDeployProd() {
//        boolean confirm = PhetBuildGUI.confirmProdDeploy( project, OldPhetServer.PRODUCTION );
//
//        if ( !confirm ) {
//            System.out.println( "Cancelled" );
//            return;
//        }

        BuildLocalProperties props = BuildLocalProperties.getInstance();

        // TODO: once in production, start incrementing version numbers
//        VersionIncrement versionIncrement = null;
//        if ( incrementMinor.isSelected() ) {
//            versionIncrement = new VersionIncrement.UpdateProdMinor();
//        }
//        else if ( incrementMajor.isSelected() ) {
//            versionIncrement = new VersionIncrement.UpdateProdMajor();
//        }

        getBuildScript().clean();

        boolean success = getBuildScript().build();

        if ( !success ) {
            System.out.println( "Errors on build" );
            return;
        }

        success = project.deploy( props.getWebsiteProdHost(), props.getWebsiteProdProtocol(), props.getWebsiteProdAuthenticationInfo(), props.getWebsiteProdManagerAuthenticationInfo(), true );

        if ( !success ) {
            System.out.println( "Errors on deploy" );
        }
    }

}