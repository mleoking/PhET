/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.CircuitListener;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.common.PhetSlider;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 10:19:00 AM
 * Copyright (c) Jun 9, 2004 by Sam Reid
 */
public abstract class ComponentEditor extends JDialog {
    private CircuitComponent element;
    private Component parent;
    private Circuit circuit;
    private PhetSlider slider;

    public ComponentEditor( String windowTitle, final CircuitComponent element, Component parent, String name, String units,
                            double min, double max, double startvalue, Circuit circuit ) throws HeadlessException {
        super( getAncestor( parent ), windowTitle, false );
        this.element = element;
        this.parent = parent;
        this.circuit = circuit;

        slider = new PhetSlider( name, units, min, max, startvalue );
        slider.setNumMajorTicks( 5 );
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( slider, BorderLayout.CENTER );
        setContentPane( contentPane );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = slider.getValue();
                doChange( value );
            }
        } );
        JButton done = new JButton( "Done" );
        done.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
            }
        } );
        contentPane.add( done, BorderLayout.SOUTH );
        addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                slider.requestSliderFocus();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        circuit.addCircuitListener( new CircuitListener() {
            public void junctionRemoved( Junction junction ) {
            }

            public void branchRemoved( Branch branch ) {
                if( branch == element ) {
                    setVisible( false );
                    dispose();
                }
            }

            public void junctionsMoved() {
            }

            public void branchesMoved( Branch[] branches ) {
            }
        } );
        pack();
        GraphicsUtil.centerDialogInParent( this );
    }

    public void setVisible( boolean b ) {
        slider.requestSliderFocus();
        super.setVisible( b );
    }

    protected abstract void doChange( double value );

    private static Frame getAncestor( Component parent ) {
        return (Frame)SwingUtilities.getWindowAncestor( parent );
    }

    public static class BatteryEditor extends ComponentEditor {
        public BatteryEditor( final CircuitComponent element, Component parent, Circuit circuit ) throws HeadlessException {
            super( "Editing Battery", element, parent, "Voltage", "Volts", 0, 100, 9, circuit );
        }

        protected void doChange( double value ) {
            super.element.setVoltageDrop( value );
        }

    }

    public static class ResistorEditor extends ComponentEditor {
        public ResistorEditor( final CircuitComponent element, Component parent, Circuit circuit ) {
            super( "Editing Resistor", element, parent, "Resistance", "Ohms", 1, 100, 10, circuit );
        }

        protected void doChange( double value ) {
            super.element.setResistance( value );
        }

    }
}
