package edu.colorado.phet.rotation.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.ClockProfiler;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * Jul 15, 2007, 3:56:14 PM
 */
public class RotationDevMenu extends JMenu {
    private NonPiccoloPhetApplication rotationApplication;
    private AbstractRotationModule rotationModule;

    public RotationDevMenu( final NonPiccoloPhetApplication rotationApplication, final AbstractRotationModule rotationModule ) {
        super( "Options" );
        this.rotationApplication = rotationApplication;
        this.rotationModule = rotationModule;
        setMnemonic( 'o' );
        {
            JMenuItem bim = new JMenuItem( "Buffered Immediate" );
            bim.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rotationModule.getRotationSimulationPanel().setGraphsBufferedImmediateSeries();
                }
            } );
            add( bim );
        }
        {

            JMenuItem bim = new JMenuItem( "Buffered" );
            bim.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rotationModule.getRotationSimulationPanel().setGraphsBufferedSeries();
                }
            } );
            add( bim );
        }
        {

            JMenuItem bim = new JMenuItem( "Piccolo" );
            bim.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rotationModule.getRotationSimulationPanel().setGraphsPiccoloSeries();
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

        final JMenuItem profiler = new JMenuItem( "Profile" );
        profiler.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new ClockProfiler( rotationApplication.getPhetFrame(), rotationModule.getName(), rotationModule.getConstantDTClock() ).show();
            }
        } );
        add( profiler );
    }

    private PNode getCircleNode() {
        return rotationModule.getRotationSimulationPanel().getRotationPlayAreaNode().getCircularMotionNode();
    }
}
