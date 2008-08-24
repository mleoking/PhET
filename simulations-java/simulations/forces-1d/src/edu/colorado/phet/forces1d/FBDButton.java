package edu.colorado.phet.forces1d;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

/**
 * Created by: Sam
 * Aug 22, 2008 at 8:25:29 PM
 */
public class FBDButton extends JButton {
    private FreeBodyDiagramSuite fbdSuite;

    public FBDButton( final FreeBodyDiagramSuite fbdSuite ) {
        super( Force1DResources.get( "FreeBodyDiagramSuite.freeBodyDiagram" ) + " " + Force1DResources.get( "FreeBodyDiagramSuite.expand" ) );
        this.fbdSuite = fbdSuite;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fbdSuite.setPanelVisible( true );
            }
        } );
        fbdSuite.addListener( new FreeBodyDiagramSuite.Listener() {
            public void visibilityChanged() {
                updateButtonVisible();
            }
        } );
        updateButtonVisible();
    }

    private void updateButtonVisible() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                setVisible( !fbdSuite.isFBDVisible() );
            }
        } );
    }
}
