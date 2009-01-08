package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.movingman.MovingManResources;

/**
 * Created by: Sam
 * Dec 5, 2007 at 8:58:57 PM
 */
public class MotionExpressionFrame extends JDialog {
    private JTextField expressionTextField;
    private OptionsMenu.MovingManOptions module;

    public MotionExpressionFrame( JFrame frame, final OptionsMenu.MovingManOptions module ) {
        super( frame, MovingManResources.getString( "expressions.title" ), false );
        this.module = module;
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        contentPane.setAnchor( GridBagConstraints.CENTER );
        contentPane.add( new JLabel( MovingManResources.getString( "expressions.description" ) ) );

        JPanel horizontalLayoutPanel = new JPanel( new FlowLayout() );
        horizontalLayoutPanel.add( new JLabel( " " + MovingManResources.getString( "expressions.range" ) + " = " ) );

        String testEquation = MovingManResources.getString( "expressions.example" );
        expressionTextField = new JTextField( testEquation, 15 ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };

        expressionTextField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setExpressionToModel();
            }
        } );
        expressionTextField.setBackground( Color.white );

        horizontalLayoutPanel.add( expressionTextField );
        contentPane.add( horizontalLayoutPanel );


        TimeSeriesControlPanel controlPanel = new TimeSeriesControlPanel( module.getTimeSeriesModel(), MovingManMotionModule.MIN_DT, MovingManMotionModule.MAX_DT );
        controlPanel.addListener( new TimeSeriesControlPanel.Listener() {
            public void recordButtonPressed() {
                setExpressionToModel();
            }
        } );
        contentPane.add( controlPanel );


        setContentPane( contentPane );
        pack();
    }

    private void setExpressionToModel() {
        this.module.setExpressionUpdate( expressionTextField.getText() );
    }
}
