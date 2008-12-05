package edu.colorado.phet.rotation.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.ClockProfiler;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Author: Sam Reid
 * Jul 15, 2007, 3:56:14 PM
 */
public class RotationDevMenu extends JMenu {
    private PhetApplication rotationApplication;
    private AbstractRotationModule rotationModule;

    public RotationDevMenu( final PiccoloPhetApplication rotationApplication, final AbstractRotationModule rotationModule ) {
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

        addSeparator();
        final JCheckBoxMenuItem lowQualityRender = new JCheckBoxMenuItem( "Low Quality" );
        lowQualityRender.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for ( int i = 0; i < rotationApplication.numModules(); i++ ) {
                    Module module = rotationApplication.moduleAt( i );
                    try {
                        Method m = module.getClass().getMethod( "getSimulationPanel", new Class[0] );
                        Object sp = m.invoke( module, new Object[0] );
                        if ( sp instanceof PCanvas ) {
                            PCanvas pc = (PCanvas) sp;
                            pc.setDefaultRenderQuality( lowQualityRender.isSelected() ? PPaintContext.LOW_QUALITY_RENDERING : PPaintContext.HIGH_QUALITY_RENDERING );
                        }
                    }
                    catch( NoSuchMethodException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( InvocationTargetException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( IllegalAccessException e1 ) {
                        e1.printStackTrace();
                    }

                }
            }
        } );
        add( lowQualityRender );
    }

    private PNode getCircleNode() {
        return rotationModule.getRotationSimulationPanel().getRotationPlayAreaNode().getCircularMotionNode();
    }
}
