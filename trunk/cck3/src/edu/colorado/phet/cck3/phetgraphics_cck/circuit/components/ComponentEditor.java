/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.common.CCKStrings;
import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.CircuitListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.components.ACVoltageSource;
import edu.colorado.phet.cck3.model.components.Battery;
import edu.colorado.phet.cck3.model.components.Branch;
import edu.colorado.phet.cck3.model.components.CircuitComponent;
import edu.colorado.phet.cck3.phetgraphics_cck.CCKModule;
import edu.colorado.phet.cck3.phetgraphics_cck.circuit.ReadoutGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.math.MathUtil;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.components.PhetSlider;
import edu.colorado.phet.common_cck.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common_cck.view.util.SwingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 10:19:00 AM
 * Copyright (c) Jun 9, 2004 by Sam Reid
 */
public abstract class ComponentEditor extends JDialog {
    private CCKModule module;
    protected CircuitComponent element;
    private Component parent;
    private Circuit circuit;
    protected PhetSlider slider;
    protected JPanel contentPane;
    private CircuitListener circuitListener;

    public ComponentEditor( final CCKModule module, String windowTitle, final CircuitComponent element, Component parent, String name, String units,
                            double min, double max, double startvalue, Circuit circuit ) throws HeadlessException {
        super( getAncestor( parent ), windowTitle, false );
        if( startvalue > max ) {

            System.out.println( "StartValue exceeded max: " + startvalue + "/" + max );
            startvalue = max;
        }
        else if( startvalue < min ) {
            System.out.println( "StartValue too low: " + startvalue + "/" + min );
            startvalue = min;
        }
        this.module = module;
        this.element = element;
        this.parent = parent;
        this.circuit = circuit;
        DecimalFormat formatter = new DecimalFormat( "0.0##" );
        slider = new PhetSlider( name, units, min, max, startvalue, formatter );
        slider.setNumMajorTicks( 5 );
        contentPane = new VerticalLayoutPanel();
//        contentPane.setLayout( new BorderLayout() );
//        contentPane.add( slider, BorderLayout.CENTER );
        contentPane.add( slider );
        setContentPane( contentPane );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = slider.getValue();
                setReadoutVisible( true );
                doChange( value );
            }
        } );
        JButton done = new JButton( SimStrings.get( "ComponentEditor.DoneButton" ) );
        done.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean ok = slider.testCommit();
                if( ok ) {
                    boolean bo = false;
                    InteractiveGraphic ccig = module.getCircuitGraphic().getGraphic( element );
                    if( ccig instanceof CircuitComponentInteractiveGraphic ) {
                        CircuitComponentInteractiveGraphic cx = (CircuitComponentInteractiveGraphic)ccig;
                        CCKMenu menu = cx.getMenu();
                        if( menu.isVisiblityRequested() ) {
                            bo = true;
                        }
                    }
//                    CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)module.getCircuitGraphic().getGraphic( element );
//                    System.out.println( "ccig.getClass() = " + ccig.getClass() );
                    boolean r = module.getCircuitGraphic().isReadoutGraphicsVisible() || bo;
                    setVisible( false );
                    if( r ) {
                        setReadoutVisible( true );
                    }
                }
            }
        } );
        JPanel donePanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        donePanel.add( done, gbc );
        contentPane.add( donePanel );
        addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                slider.requestSliderFocus();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        circuitListener = new CircuitListener() {
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

            public void junctionAdded( Junction junction ) {
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
            }


        };
        circuit.addCircuitListener( circuitListener );
        pack();
        SwingUtils.centerDialogInParent( this );
        addWindowStateListener( new WindowStateListener() {
            public void windowStateChanged( WindowEvent e ) {
                validateRepaint();
            }
        } );
        addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                validateRepaint();
            }

            public void windowLostFocus( WindowEvent e ) {
                validateRepaint();
            }
        } );
    }

    private void setReadoutVisible( boolean visible ) {
        ReadoutGraphic rg = module.getCircuitGraphic().getReadoutGraphic( element );
        if( rg == null ) {
//            throw new RuntimeException( "Null ReadoutGRaphic for component: "+element.getClass()+", element="+element );
            //maybe it was already removed...
            if( visible ) {
                throw new RuntimeException( "Null ReadoutGRaphic for component: " + element.getClass() + ", element=" + element );
            }
        }
        else {
            rg.setVisible( visible );
        }
    }

    public void setVisible( boolean b ) {
        super.setVisible( b );
        //ensure that the editor value is visible.
        setReadoutVisible( b );
        if( b ) {
            slider.requestSliderFocus();
        }
        validateRepaint();
    }

    public void validateRepaint() {
        getContentPane().invalidate();
        getContentPane().validate();
        getContentPane().repaint();
    }

    protected abstract void doChange( double value );

    private static Frame getAncestor( Component parent ) {
        return (Frame)SwingUtilities.getWindowAncestor( parent );
    }

    public static class BatteryEditor extends ComponentEditor {
        public BatteryEditor( CCKModule module, final CircuitComponent element, Component parent, Circuit circuit ) throws HeadlessException {
            super( module, SimStrings.get( "ComponentEditor.BatteryVoltageTitle" ), element, parent,
                   SimStrings.get( "ComponentEditor.BatteryVoltageName" ),
                   SimStrings.get( "ComponentEditor.BatteryVoltageUnits" ), 0, 100, element.getVoltageDrop(), circuit );
            if( module.getParameters().hugeRangeOnBatteries() ) {

                final JCheckBox hugeRange = new JCheckBox( SimStrings.get( "ComponentEditor.MoreVoltsCheckBox" ), false );
                hugeRange.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
//                            System.out.println( "hugeRange.isSelected() = " + hugeRange.isSelected() );
                        setHugeRange( hugeRange.isSelected() );
                    }
                } );
                super.contentPane.add( hugeRange );
                super.pack();
            }

        }

        private PhetSlider getSlider() {
            return super.slider;
        }

        private void setHugeRange( boolean hugeRange ) {
            PhetSlider slider = getSlider();
            double origValue = slider.getValue();
            if( hugeRange ) {
                int max = 100000;
                slider.setRange( 0, max );
                slider.setPaintLabels( false );
                slider.setValue( MathUtil.clamp( 0, origValue, max ) );
            }
            else {
                int max = 100;
                slider.setRange( 0, max );
                slider.setPaintLabels( true );
                slider.setValue( MathUtil.clamp( 0, origValue, max ) );
            }
        }

        protected void doChange( double value ) {
            super.element.setVoltageDrop( value );
        }

    }

    public static class ResistorEditor extends ComponentEditor {
        public ResistorEditor( CCKModule module, final CircuitComponent element, Component parent, Circuit circuit ) {
            super( module, SimStrings.get( "ComponentEditor.ResistorResistanceTitle" ),
                   element, parent, SimStrings.get( "ComponentEditor.ResistorResistanceName" ),
                   SimStrings.get( "ComponentEditor.ResistorResistanceUnits" ), 0, 100, element.getResistance(), circuit );
        }

        protected void doChange( double value ) {
            if( value < CCKModel.MIN_RESISTANCE ) {
                value = CCKModel.MIN_RESISTANCE;
            }
            super.element.setResistance( value );
        }

    }

    public static class BulbResistanceEditor extends ComponentEditor {
        public BulbResistanceEditor( CCKModule module, final CircuitComponent element, Component parent, Circuit circuit ) {
            super( module, SimStrings.get( "ComponentEditor.BulbResistanceTitle" ),
                   element, parent, SimStrings.get( "ComponentEditor.BulbResistanceName" ),
                   SimStrings.get( "ComponentEditor.BulbResistanceTitle" ), 0, 100, element.getResistance(), circuit );
        }

        protected void doChange( double value ) {
            if( value < CCKModel.MIN_RESISTANCE ) {
                value = CCKModel.MIN_RESISTANCE;
            }
            super.element.setResistance( value );
        }

    }

    public static class BatteryResistanceEditor extends ComponentEditor {
        private Battery battery;

        public BatteryResistanceEditor( CCKModule module, Battery element, Component parent, Circuit circuit ) {
            super( module, SimStrings.get( "ComponentEditor.BatteryResistanceTitle" ),
                   element, parent, SimStrings.get( "ComponentEditor.BatteryResistanceName" ),
                   SimStrings.get( "ComponentEditor.BatteryResistanceUnits" ), 0, 9, element.getInteralResistance(), circuit );
            this.battery = element;
        }

        protected void doChange( double value ) {
            if( value < CCKModel.MIN_RESISTANCE ) {
                value = CCKModel.MIN_RESISTANCE;
            }
//            super.element.setResistance( value );
//            System.out.println( "set battery internal resistance= " + value );
            battery.setInternalResistance( value );
        }
    }

    public void delete() {
        circuit.removeCircuitListener( circuitListener );
        setVisible( false );
        dispose();
    }

    public static class ACVoltageSourceEditor extends ComponentEditor {
        private ACVoltageSource branch;

        public ACVoltageSourceEditor( CCKModule module, ACVoltageSource branch, ApparatusPanel apparatusPanel, Circuit circuit ) {
            super( module, CCKStrings.getString( "ac.voltage.source.editor" ), branch, apparatusPanel, CCKStrings.getString( "BranchSource.AC" ), CCKStrings.getString( "ReadoutGraphic.ACVolts" ), 0, 100, 10, circuit );
            this.branch = branch;
        }

        protected void doChange( double value ) {
            branch.setAmplitude( value );
        }
    }
}
