/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 5, 2003
 * Time: 4:16:28 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.sound.SoundConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class ClockPanelLarge extends JPanel implements ClockTickListener {

    private int numSigDigits = 4;
    private JTextField clockTF = new JTextField();
    private NumberFormat clockFormat = new DecimalFormat( "0.0000" );
    private AbstractClock clock;

    public ClockPanelLarge( AbstractClock clock ) {
        this.clock = clock;
        clock.addClockTickListener( this );
        setBackground( new Color( 237, 225, 113 ) );

        // Create widgets
        setBorder( BorderFactory.createRaisedBevelBorder() );
        clockTF = new JTextField( 5 );
        Font clockFont = clockTF.getFont();
        clockTF.setFont( new Font( clockFont.getName(), Font.BOLD, 16 ) );

        clockTF.setEditable( false );
        clockTF.setHorizontalAlignment( JTextField.RIGHT );

        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                resetClock();
            }
        } );

        // Lay out the panel
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        int padX = 0;
        int padY = 0;
        Insets insets = new Insets( 5, 5, 5, 5 );
        GridBagConstraints gbc = null;
        gbc = new GridBagConstraints( 0, rowIdx, 2, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( "Running Time" ), gbc );
        rowIdx++;
        gbc = new GridBagConstraints( 0, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.EAST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( clockTF, gbc );
        gbc = new GridBagConstraints( 1, rowIdx, 1, 1, 1, 1,
                                      GridBagConstraints.WEST, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( new JLabel( "msec" ), gbc );
        rowIdx++;
        gbc = new GridBagConstraints( 0, rowIdx, 2, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, padX, padY );
        this.add( resetBtn, gbc );
    }

    private void resetClock() {
        clock.resetRunningTime();
        clockTicked( clock, 0 );
    }

    public void clockTicked( AbstractClock c, double dt ) {
        String s = clockFormat.format( c.getRunningTime() * SoundConfig.s_clockScaleFactor / 1000 );
        clockTF.setText( s );
    }

    public void setClockPanelVisible( boolean isVisible ) {
        setVisible( isVisible );
    }

    public boolean isClockPanelVisible() {
        return isVisible();
    }
}
