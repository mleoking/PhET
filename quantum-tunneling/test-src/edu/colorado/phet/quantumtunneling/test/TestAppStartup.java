/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.test;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * TestAppStartup
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestAppStartup extends PhetApplication {
    
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 0.1; // model time: dt per clock tick
    private static final String CLOCK_DISPLAY_PATTERN = "0.0"; // should match precision of MODEL_RATE
    
    public static void main( final String[] args ) {
        try {
            TestAppStartup app = new TestAppStartup( args );
            app.startApplication();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public TestAppStartup( String[] args ) throws InterruptedException
    {
        super( args, "TestAppStartUp", "description", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );
        
        IClock clock = new SwingClock( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        Module module = new TestModule( clock );
        addModule( module );
        
        Thread.currentThread().sleep( 5 * 1000 );
    }
    
    private static class TestModule extends PiccoloModule {
        private PText _textNode;
        
        public TestModule( IClock clock ) {
            super( "TestModule", clock, true /* clockStartsPaused */ );
            
            setLogoPanel( null );
            
            // Model
            {
                ClockListener clockListener = new ClockAdapter() {
                    private DecimalFormat _clockFormat = new DecimalFormat( CLOCK_DISPLAY_PATTERN );
                    
                    public void clockTicked( ClockEvent event ) {
                        updateTimeDisplay( event.getSimulationTime() );
                    }
                    
                    public void simulationTimeReset( ClockEvent event ) {
                        updateTimeDisplay( event.getSimulationTime() );
                    }
                    
                    private void updateTimeDisplay( double time ) {
                        String s = _clockFormat.format( time );
                        _textNode.setText( "t = " + s );
                    }
                };
                getClock().addClockListener( clockListener );
            }
            
            // Play area
            {
                // Canvas
                PhetPCanvas canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
                setPhetPCanvas( canvas );
                
                // Parent of all nodes
                PNode parentNode = new PNode();
                canvas.addScreenChild( parentNode );
               
                // PSwing node (button)
                JButton jButton = new JButton( "JButton" );
                jButton.setOpaque( false );
                PSwing pSwing = new PSwing( canvas, jButton );
                pSwing.translate( 300, 100 );
                parentNode.addChild( pSwing );
                jButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(), "Click OK" );
                    }
                } );
                
                // Text node
                _textNode = new PText( "PText" );
                _textNode.translate( 300, 300 );
                _textNode.scale( 2 );
                parentNode.addChild( _textNode );
            }
            
            // Control panel
            {
                ControlPanel controlPanel = new ControlPanel( this );
                setControlPanel( controlPanel );
                
                // Label and slider in a titled panel
                JPanel panel = new JPanel();
                panel.setBorder( new TitledBorder( "TitledBorder" ) );
                EasyGridBagLayout layout = new EasyGridBagLayout( panel );
                layout.addComponent( new JLabel( "JLabel:" ), 0, 0 );
                layout.addComponent( new JSlider(), 0, 1 );
                controlPanel.addControlFullWidth( panel );
                
                // Misc controls that do nothing
                controlPanel.addControl( new JButton( "JButton" ) );
                controlPanel.addControl( new JCheckBox( "JCheckBox" ) );
                controlPanel.addControlFullWidth( new JSeparator() );
                
                // Reset button, resets the clock
                JButton resetButton = new JButton( "Reset All" );
                controlPanel.addControl( resetButton );
                resetButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        Frame frame = PhetApplication.instance().getPhetFrame();
                        int rval = JOptionPane.showConfirmDialog( frame, "Reset all settings?", "Confirm", JOptionPane.YES_NO_OPTION );
                        if ( rval == JOptionPane.YES_OPTION ) {
                            getClock().resetSimulationTime();  
                        }
                    }
                } );
            }
            
            // Start the clock
            getClock().start();
        }
    }
}
