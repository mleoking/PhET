package edu.colorado.phet.rotation;

import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Sam Reid
 * Jul 15, 2007, 3:56:14 PM
 */
public class RotationDevMenu extends JMenu {
    private RotationApplication rotationApplication;

    public RotationDevMenu( final RotationApplication rotationApplication ) {
        super( "Options" );
        this.rotationApplication = rotationApplication;
        setMnemonic( 'o' );
        {
            JMenuItem bim = new JMenuItem( "Buffered Immediate" );
            bim.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rotationApplication.getRotationModule().getRotationSimulationPanel().setGraphsBufferedImmediateSeries();
                }
            } );
            add( bim );
        }
        {

            JMenuItem bim = new JMenuItem( "Buffered" );
            bim.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rotationApplication.getRotationModule().getRotationSimulationPanel().setGraphsBufferedSeries();
                }
            } );
            add( bim );
        }
        {

            JMenuItem bim = new JMenuItem( "Piccolo" );
            bim.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rotationApplication.getRotationModule().getRotationSimulationPanel().setGraphsPiccoloSeries();
                }
            } );
            add( bim );
        }
        addSeparator();
        final JCheckBoxMenuItem circleNodeVisible = new JCheckBoxMenuItem( "Show Circular Regression", getCircleNode().getVisible() );
        circleNodeVisible.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getCircleNode().setVisible( circleNodeVisible.isSelected() );
            }
        } );

        add( circleNodeVisible );
    }

    private PNode getCircleNode() {
        return rotationApplication.getRotationModule().getRotationSimulationPanel().getRotationPlayAreaNode().getCircularMotionNode();
    }
}
