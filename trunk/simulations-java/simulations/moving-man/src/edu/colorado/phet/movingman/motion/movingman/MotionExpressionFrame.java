package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;

/**
 * Created by: Sam
 * Dec 5, 2007 at 8:58:57 PM
 */
public class MotionExpressionFrame extends JDialog {
    public MotionExpressionFrame( JFrame frame, final OptionsMenu.MovingManOptions module ) {
        super( frame, SimStrings.get( "expressions.title" ), false );
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        contentPane.setAnchor( GridBagConstraints.CENTER );
        contentPane.add( new JLabel( SimStrings.get( "expressions.description" ) ) );

        JPanel horizontalLayoutPanel = new JPanel( new FlowLayout() );
        horizontalLayoutPanel.add( new JLabel( " " + SimStrings.get( "expressions.range" ) + " = " ) );

        String testEquation = SimStrings.get( "expressions.example" );
        final JTextField expression = new JTextField( testEquation, 15 ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };

        expression.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setExpressionUpdate( expression.getText() );
            }
        } );
        expression.setBackground( Color.white );

        horizontalLayoutPanel.add( expression );
        contentPane.add( horizontalLayoutPanel );


        TimeSeriesControlPanel controlPanel = new TimeSeriesControlPanel( module.getTimeSeriesModel(), MovingManMotionModule.MIN_DT, MovingManMotionModule.MAX_DT );
        contentPane.add( controlPanel );


        setContentPane( contentPane );
        pack();


    }
}
