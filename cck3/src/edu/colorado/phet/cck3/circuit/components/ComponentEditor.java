/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
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
    private CCK3Module module;
    private CircuitComponent element;
    private Component parent;
    private Circuit circuit;
    private PhetSlider slider;

    public ComponentEditor( final CCK3Module module, String windowTitle, final CircuitComponent element, Component parent, String name, String units,
                            double min, double max, double startvalue, Circuit circuit ) throws HeadlessException {
        super( getAncestor( parent ), windowTitle, false );
        this.module = module;
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
                setReadoutVisible( true );
                doChange( value );
            }
        } );
        JButton done = new JButton( "Done" );
        done.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean r = module.getCircuitGraphic().isReadoutGraphicsVisible();

                setVisible( false );
                if( r ) {
                    setReadoutVisible( true );
                }
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

    private void setReadoutVisible( boolean visible ) {
        ReadoutGraphic rg = module.getCircuitGraphic().getReadoutGraphic( element );
        if( rg == null ) {
            throw new RuntimeException( "Null ReadoutGRaphic for component." );
        }
        else {
            rg.setVisible( visible );
        }
    }

    public void setVisible( boolean b ) {
        slider.requestSliderFocus();
        super.setVisible( b );
        //ensure that the editor value is visible.
        setReadoutVisible( b );
    }

    protected abstract void doChange( double value );

    private static Frame getAncestor( Component parent ) {
        return (Frame)SwingUtilities.getWindowAncestor( parent );
    }

    public static class BatteryEditor extends ComponentEditor {
        public BatteryEditor( CCK3Module module, final CircuitComponent element, Component parent, Circuit circuit ) throws HeadlessException {
            super( module, "Editing Battery", element, parent, "Voltage", "Volts", 0, 100, 9, circuit );
        }

        protected void doChange( double value ) {
            super.element.setVoltageDrop( value );
        }

    }

    public static class ResistorEditor extends ComponentEditor {
        public ResistorEditor( CCK3Module module, final CircuitComponent element, Component parent, Circuit circuit ) {
            super( module, "Editing Resistor", element, parent, "Resistance", "Ohms", 0, 100, 10, circuit );
        }

        protected void doChange( double value ) {
            super.element.setResistance( value );
        }

    }

    public static class BulbResistanceEditor extends ComponentEditor {
        public BulbResistanceEditor( CCK3Module module, final CircuitComponent element, Component parent, Circuit circuit ) {
            super( module, "Editing Bulb", element, parent, "Resistance", "Ohms", 0, 100, 10, circuit );
        }

        protected void doChange( double value ) {
            super.element.setResistance( value );
        }

    }

    public static class BatteryResistanceEditor extends ComponentEditor {
        public BatteryResistanceEditor( CCK3Module module, Battery element, Component parent, Circuit circuit ) {
            super( module, "Battery Internal Resistance", element, parent, "Internal Resistance", "Ohms", Battery.MIN_RESISTANCE, 9, element.getResistance(), circuit );
        }

        protected void doChange( double value ) {
            super.element.setResistance( value );
        }
    }
}
