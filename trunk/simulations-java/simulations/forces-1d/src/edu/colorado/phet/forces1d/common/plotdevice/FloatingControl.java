/*  */
package edu.colorado.phet.forces1d.common.plotdevice;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.forces1d.phetcommon.view.components.VerticalLayoutPanel;
import edu.colorado.phet.forces1d.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.view.PlotDeviceFontManager;

/**
 * User: Sam Reid
 * Date: Jan 15, 2005
 * Time: 10:37:22 AM
 */
public class FloatingControl extends VerticalLayoutPanel {
    private static BufferedImage play;
    private static BufferedImage pause;
    private JButton pauseButton;
    private JButton recordButton;
    private JButton resetButton;

    public JButton getGoButton() {
        return recordButton;
    }

    static {
        try {
            play = ImageLoader.loadBufferedImage( "forces-1d/images/icons/java/media/Play16.gif" );
            pause = ImageLoader.loadBufferedImage( "forces-1d/images/icons/java/media/Pause16.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    static class ControlButton extends JButton {
        static Font font = PlotDeviceFontManager.getFontSet().getControlButtonFont();

        public ControlButton( String text ) {
            super( text );
            setFont( font );
        }
    }

    public FloatingControl( final PlotDeviceModel plotDeviceModel, final ApparatusPanel apparatusPanel ) {
        pauseButton = new ControlButton( Force1DResources.get( "FloatingControl.pause" ) );
        pauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                plotDeviceModel.setPaused( true );
            }
        } );
        recordButton = new ControlButton( Force1DResources.get( "FloatingControl.go" ) );
        recordButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                plotDeviceModel.setRecordMode();
                plotDeviceModel.setPaused( false );
            }
        } );

        resetButton = new ControlButton( Force1DResources.get( "FloatingControl.clear" ) );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean paused = plotDeviceModel.isPaused();
                plotDeviceModel.setPaused( true );
                int option = JOptionPane.showConfirmDialog( apparatusPanel, Force1DResources.get( "FloatingControl.sureToClear" ), Force1DResources.get( "FloatingControl.confirmReset" ), JOptionPane.YES_NO_CANCEL_OPTION );
                if ( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
                    plotDeviceModel.reset();
                }
                else if ( option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION ) {
                    plotDeviceModel.setPaused( paused );
                }
            }
        } );
        plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                setButtons( false, true, true );
            }

            public void recordingPaused() {
                setButtons( true, false, true );
            }

            public void recordingFinished() {
                setButtons( false, false, true );
            }

            public void reset() {
                setButtons( true, false, false );
            }

            public void rewind() {
                setButtons( true, false, true );
            }
        } );
        add( recordButton );
        add( pauseButton );
        add( resetButton );
        pauseButton.setEnabled( false );
    }

    private void setButtons( boolean record, boolean pause, boolean reset ) {
        recordButton.setEnabled( record );
        pauseButton.setEnabled( pause );
        resetButton.setEnabled( reset );
    }

    public void setVisible( boolean aFlag ) {
        super.setVisible( aFlag );
    }
}
