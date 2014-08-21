package edu.colorado.phet.buildtools.gui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.PhetWebsite;
import edu.colorado.phet.buildtools.gui.ChangesPanel;
import edu.colorado.phet.buildtools.java.projects.WebsiteProject;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * Panel that gives GUI options for the wicket-based website
 */
public class WebsitePanel extends JPanel {
    private File trunk;

    private WebsiteProject project;

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

        for ( final PhetWebsite website : new PhetWebsite[]{ PhetWebsite.LOCAL_SERVER, PhetWebsite.PHET_SERVER, PhetWebsite.SIMIAN, PhetWebsite.FIGARO } ){
            JPanel deployLocalPanel = new VerticalLayoutPanel();
            deployLocalPanel.setBorder( BorderFactory.createTitledBorder( "Deploy" ) );

            JButton localServerButton = new JButton( website.getName() );
            deployLocalPanel.add( localServerButton );

            controlPanel.add( deployLocalPanel );

            localServerButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent actionEvent ) {
                    doDeploy( website );
                }
            } );
        }

        add( controlPanel, BorderLayout.SOUTH );
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

    private void doDeploy( PhetWebsite website ) {
        System.out.println( "Preparing to deploy website to " + website );

        BuildLocalProperties props = BuildLocalProperties.getInstance();

        getBuildScript().clean();

        boolean success = getBuildScript().build();

        if ( !success ) {
            System.out.println( "Errors on build" );
            return;
        }

        System.out.println( "Deploying website to " + website );

        success = project.deploy(
                website.getServerHost(),
                website.getTomcatManagerProtocol(),
                website.getServerAuthenticationInfo( props ),
                website.getTomcatManagerAuthenticationInfo( props ) );

        if ( !success ) {
            System.out.println( "Errors on deploy" );
        }
    }

}