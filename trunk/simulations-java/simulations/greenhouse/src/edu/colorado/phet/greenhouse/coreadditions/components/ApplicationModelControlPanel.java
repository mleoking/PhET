/*, 2003.*/
package edu.colorado.phet.greenhouse.coreadditions.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.phetcommon.model.ApplicationModel;
import edu.colorado.phet.greenhouse.phetcommon.model.command.Command;

/**
 * User: Sam Reid
 * Date: May 18, 2003
 * Time: 10:24:21 PM
 */
public class ApplicationModelControlPanel extends JPanel {
    Resettable rh;

    JButton play;
    JButton pause;
    //    JButton reset;
    JButton step;
    ApplicationModel model;

    Resettable resetHandler;
    private JButton resetButton;

    public ApplicationModelControlPanel( ApplicationModel runner ) {
        this( runner, null );
    }

    public ApplicationModelControlPanel( final ApplicationModel runner, final Resettable rh ) {
        this.model = runner;

        String root = "icons/java/media/";
        BufferedImage playU = GreenhouseResources.getImage( root + "Play24.gif" );
        BufferedImage pauseU = GreenhouseResources.getImage( root + "Pause24.gif" );
        BufferedImage stepU = GreenhouseResources.getImage( root + "StepForward24.gif" );
        ImageIcon playIcon = new ImageIcon( playU );
        ImageIcon pauseIcon = new ImageIcon( pauseU );
        ImageIcon stepIcon = new ImageIcon( stepU );
        this.rh = rh;
        play = new JButton( SimStrings.get( "ApplicationModelControlPanel.PlayButton" ), playIcon );
        pause = new JButton( SimStrings.get( "ApplicationModelControlPanel.PauseButton" ), pauseIcon );
        step = new JButton( SimStrings.get( "ApplicationModelControlPanel.StepButton" ), stepIcon );
        step.setEnabled( false );

        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setRunning( true );
                play.setEnabled( false );
                pause.setEnabled( true );
                step.setEnabled( false );
            }
        } );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setRunning( false );
                play.setEnabled( true );
                pause.setEnabled( false );
                step.setEnabled( true );
            }
        } );

        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.execute( new Command() {
                    public void doIt() {
                        //model.stepInTime();
                        model.tickOnce();
                    }
                } );
            }
        } );

        setLayout( new BorderLayout() );
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( play );
        buttonPanel.add( pause );
        buttonPanel.add( step );
        this.add( buttonPanel, BorderLayout.CENTER );


        BufferedImage resetU = GreenhouseResources.getImage( root + "Stop24.gif" );
        ImageIcon resetIcon = new ImageIcon( resetU );
        resetButton = new JButton( SimStrings.get( "ApplicationModelControlPanel.ResetButton" ), resetIcon );
        resetButton.addActionListener( new ResetActionListener() );
        if ( rh != null ) {
            add( resetButton );
        }

        play.setEnabled( false );
        pause.setEnabled( true );
    }

    class ResetActionListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            resetHandler.reset();
        }

    }

    public void setResettable( Resettable r ) {
        if ( r == null ) {
            remove( resetButton );
        }
        else {
            resetHandler = r;
            add( resetButton );
        }
    }

}
