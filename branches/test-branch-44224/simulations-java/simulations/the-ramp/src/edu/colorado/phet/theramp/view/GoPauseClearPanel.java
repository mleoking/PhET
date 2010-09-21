/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampTimeSeriesModel;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModel;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModelListenerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 5, 2005
 * Time: 5:03:07 AM
 */

public class GoPauseClearPanel extends VerticalLayoutPanel {
    private TimeSeriesModel module;

    private JButton goPauseButton;
    private JButton clearButton;
    private boolean itsAGoButton = true;
    private ImageIcon goIcon;
    private ImageIcon pauseIcon;

    public GoPauseClearPanel( final TimeSeriesModel module ) {
        this.module = module;
        super.getGridBagConstraints().anchor = GridBagConstraints.CENTER;
        final ActionListener pauseHandler = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPaused( true );
//                module.requestEditInTextBox( GoPauseClearPanel.this );
            }
        };
        goPauseButton = new ControlButton( TheRampStrings.getString( "time.pause" ) );//longer text
        try {
            goIcon = new ImageIcon( ImageLoader.loadBufferedImage( "the-ramp/images/light3.png" ) );
            pauseIcon = new ImageIcon( ImageLoader.loadBufferedImage( "the-ramp/images/stop-20.png" ) );
            goPauseButton.setIcon( goIcon );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        final ActionListener goHandler = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordMode();
                module.setPaused( false );
            }
        };
        goPauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( itsAGoButton ) {
                    goHandler.actionPerformed( e );
                }
                else {
                    pauseHandler.actionPerformed( e );
                }
            }
        } );
        clearButton = new ControlButton( ( TheRampStrings.getString( "controls.clear" ) ) );
        clearButton.setBackground( EarthGraphic.earthGreen );
        setBackground( EarthGraphic.earthGreen );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.confirmAndApplyReset();
            }
        } );
        module.addListener( new TimeSeriesModelListenerAdapter() {
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
        add( goPauseButton );
        add( clearButton );
        setButtons( true, false, false );
    }

    private void setButtons( boolean record, boolean pause, boolean reset ) {
        if( pause && record ) {

        }
        else if( !pause && !record ) {

        }
        if( pause ) {
            goPauseButton.setText( TheRampStrings.getString( "time.pause" ) );
//            goPauseButton.setText( "" );
            goPauseButton.setIcon( pauseIcon );
            paintAll();
            itsAGoButton = false;
        }
        else {
            goPauseButton.setText( "     " + TheRampStrings.getString( "time.go" ) );
            goPauseButton.setIcon( goIcon );
            paintAll();
            itsAGoButton = true;
        }

        clearButton.setEnabled( reset );
        repaintComponents();
    }

    private void paintAll() {
        super.invalidate();
        super.validate();
        super.doLayout();
        super.repaint();
        goPauseButton.repaint();
        clearButton.repaint();
        clearButton.repaint( 0, 0, clearButton.getWidth(), clearButton.getHeight() );
        if( module instanceof RampTimeSeriesModel ) {
            RampTimeSeriesModel rampTimeSeriesModel = (RampTimeSeriesModel)module;
            if( rampTimeSeriesModel.getRampModule() != null && rampTimeSeriesModel.getRampModule().getRampPanel() != null ) {
//                System.out.println( "rampTimeSeriesModel = " + rampTimeSeriesModel );
                rampTimeSeriesModel.getRampModule().getRampPanel().repaint();
            }
        }
    }

    private void repaintComponents() {
        goPauseButton.repaint();
        clearButton.repaint();
        repaint();
    }

    public JButton getGoPauseButton() {
        return goPauseButton;
    }

    static class ControlButton extends JButton {
        //        static Font font = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 14 );
        private static Font font = RampFontSet.getFontSet().getNormalButtonFont();

        public ControlButton( String text ) {
            super( text );
            setFont( font );
            setBackground( EarthGraphic.earthGreen );
        }
    }

}
