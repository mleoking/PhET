package edu.colorado.phet.energyskatepark;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.energyskatepark.view.swing.LocationControlPanel;

/**
 * Author: Sam Reid
 * May 30, 2007, 1:14:17 PM
 */
public class InterviewSetup {
    private JFrame frame;

    public InterviewSetup( String[] args ) {
        frame = new JFrame( "Interview Options" );
        frame.setContentPane( new InterviewPanel( args ) );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
    }

    static class InterviewPanel extends VerticalLayoutPanel {
        private JCheckBox centered;
        private JRadioButton oneColumn;
        private JRadioButton twoColumn;

        public InterviewPanel( final String[] args ) {
            oneColumn = new JRadioButton( "One Column", true );
            twoColumn = new JRadioButton( "Two Columns", false );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( oneColumn );
            buttonGroup.add( twoColumn );
            centered = new JCheckBox( "Centered", false );

            JButton startButton = new JButton( "Start Application" );
            startButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    EnergySkateParkOptions options = new EnergySkateParkOptions( oneColumn.isSelected() ? (LocationControlPanel.PlanetButtonLayout)new LocationControlPanel.VerticalPlanetButtonLayout() : new LocationControlPanel.TwoColumnLayout(),
                                                                                 centered.isSelected() );
                    //TODO: port this to new EnergySkateParkApplication constructor if it's valuable
//                    EnergySkateParkApplication.main( args, options );
                    JOptionPane.showMessageDialog( null, "this feature is currently disabled" );//XXX
                }
            } );

            VerticalLayoutPanel planetButtonControls = new VerticalLayoutPanel();
            planetButtonControls.setBorder( BorderFactory.createTitledBorder( "Planet Buttons" ) );


            planetButtonControls.add( oneColumn );
            planetButtonControls.add( twoColumn );
            planetButtonControls.add( centered );

            add( planetButtonControls );
            add( Box.createRigidArea( new Dimension( 200, 50 ) ) );
            add( startButton );
        }
    }

    public static void main( String[] args ) {
        new InterviewSetup( args ).start();
    }

    private void start() {
        frame.show();
    }
}
