package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListener;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.circuitconstructionkit.model.components.Battery;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 10:19:00 AM
 */
public abstract class ComponentEditor extends JDialog {
    protected ICCKModule module;
    protected CircuitComponent circuitComponent;
    private Component parent;
    private Circuit circuit;
    protected ModelSlider slider;
    protected JPanel contentPane;
    private CircuitListener circuitListener;

    public ComponentEditor( final ICCKModule module, String windowTitle, final CircuitComponent element, Component parent, String name, String units,
                            double min, double max, double startvalue, Circuit circuit ) throws HeadlessException {
        super( getAncestor( parent ), windowTitle, false );
        if ( startvalue > max ) {
            System.out.println( "StartValue exceeded max: " + startvalue + "/" + max );
            startvalue = max;
        }
        else if ( startvalue < min ) {
            System.out.println( "StartValue too low: " + startvalue + "/" + min );
            startvalue = min;
        }
        this.module = module;
        this.circuitComponent = element;
        this.parent = parent;
        this.circuit = circuit;
        DecimalFormat formatter = new DecimalFormat( "0.0#" );
        slider = new ModelSlider( name, units, min, max, startvalue, formatter );
        slider.setTitleFont( slider.getTitleLabel().getFont().deriveFont( 24.0f ) );
        slider.setNumMajorTicks( 5 );
        contentPane = new VerticalLayoutPanel();
        contentPane.add( slider );
        setContentPane( contentPane );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                doChange( slider.getValue() );
            }
        } );
        JButton done = new JButton( CCKResources.getString( "ComponentEditor.DoneButton" ) );
        done.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean ok = slider.testCommit();
                if ( ok ) {
                    setVisible( false );
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
        addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
                circuitComponent.setEditing( true );
            }

            public void windowClosing( WindowEvent e ) {
                circuitComponent.setEditing( false );
            }
        } );
        circuitListener = new CircuitListenerAdapter() {

            public void branchRemoved( Branch branch ) {
                if ( branch == element ) {
                    setVisible( false );
                    dispose();
                }
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

    public void setVisible( boolean b ) {
        super.setVisible( b );
        //ensure that the editor value is visible.
        if ( b ) {
            slider.requestSliderFocus();
        }
        validateRepaint();
        circuitComponent.setEditing( b );
    }

    public void validateRepaint() {
        getContentPane().invalidate();
        getContentPane().validate();
        getContentPane().repaint();
    }

    protected abstract void doChange( double value );

    private static Frame getAncestor( Component parent ) {
        return (Frame) SwingUtilities.getWindowAncestor( parent );
    }

    public static class BatteryEditor extends ComponentEditor {
        public BatteryEditor( ICCKModule module, final CircuitComponent element, Component parent, Circuit circuit ) throws HeadlessException {
            super( module, CCKResources.getString( "ComponentEditor.BatteryVoltageTitle" ), element, parent,
                   CCKResources.getString( "ComponentEditor.BatteryVoltageName" ),
                   CCKResources.getString( "ComponentEditor.BatteryVoltageUnits" ), 0, 100, element.getVoltageDrop(), circuit );
//                   SimStrings.get( "ComponentEditor.BatteryVoltageUnits" ), 0, element.getVoltageDrop() > 100 ? 100000 : 100, element.getVoltageDrop(), circuit );
            if ( module.getParameters().hugeRangeOnBatteries() ) {
                final JCheckBox hugeRange = new JCheckBox( CCKResources.getString( "ComponentEditor.MoreVoltsCheckBox" ), false );
                hugeRange.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setHugeRange( hugeRange.isSelected() );
                    }
                } );
                super.contentPane.add( hugeRange );
                boolean highVal = element.getVoltageDrop() > 100;
                setHugeRange( highVal );
                hugeRange.setSelected( highVal );

                super.pack();
            }
        }

        private ModelSlider getSlider() {
            return super.slider;
        }

        private void setHugeRange( boolean hugeRange ) {
            ModelSlider slider = getSlider();
//            double origValue = slider.getValue();
            if ( hugeRange ) {
                int max = 100000;
                slider.setRange( 0, max );
                slider.setPaintLabels( false );
                slider.setValue( MathUtil.clamp( 0, circuitComponent.getVoltageDrop(), max ) );
                super.pack();
            }
            else {
                int max = 100;
                slider.setRange( 0, max );
                slider.setPaintLabels( true );
                slider.setValue( MathUtil.clamp( 0, circuitComponent.getVoltageDrop(), max ) );
                super.pack();
            }
        }

        protected void doChange( double value ) {
            super.circuitComponent.setVoltageDrop( value );
            super.updateDuringDrag();
        }

    }

    protected void updateDuringDrag() {
        module.getCCKModel().stepInTime( 1.0 );//todo this is a hack to ensure things keep flowing during a drag operation.
    }

    public static class ResistorEditor extends ComponentEditor {
        public ResistorEditor( ICCKModule module, final CircuitComponent element, Component parent, Circuit circuit ) {
            super( module, CCKResources.getString( "ComponentEditor.ResistorResistanceTitle" ),
                   element, parent, CCKResources.getString( "ComponentEditor.ResistorResistanceName" ),
                   CCKResources.getString( "ComponentEditor.ResistorResistanceUnits" ), 0, 100, element.getResistance(), circuit );
        }

        protected void doChange( double value ) {
            if ( value < CCKModel.MIN_RESISTANCE ) {
                value = CCKModel.MIN_RESISTANCE;
            }
            super.circuitComponent.setResistance( value );
            updateDuringDrag();
        }

    }

    public static class BulbResistanceEditor extends ComponentEditor {
        public BulbResistanceEditor( ICCKModule module, final CircuitComponent element, Component parent, Circuit circuit ) {
            super( module, CCKResources.getString( "ComponentEditor.BulbResistanceTitle" ),
                   element, parent, CCKResources.getString( "ComponentEditor.BulbResistanceName" ),
                   CCKResources.getString( "ComponentEditor.BulbResistanceTitle" ), 0, 100, element.getResistance(), circuit );
        }

        protected void doChange( double value ) {
            if ( value < CCKModel.MIN_RESISTANCE ) {
                value = CCKModel.MIN_RESISTANCE;
            }
            super.circuitComponent.setResistance( value );
            updateDuringDrag();
        }

    }

    public static class BatteryResistanceEditor extends ComponentEditor {
        private Battery battery;

        public BatteryResistanceEditor( ICCKModule module, Battery element, Component parent, Circuit circuit ) {
            super( module, CCKResources.getString( "ComponentEditor.BatteryResistanceTitle" ),
                   element, parent, CCKResources.getString( "ComponentEditor.BatteryResistanceName" ),
                   CCKResources.getString( "ComponentEditor.BatteryResistanceUnits" ), 0, 9, element.getInteralResistance(), circuit );
            this.battery = element;
        }

        protected void doChange( double value ) {
            if ( value < CCKModel.MIN_RESISTANCE ) {
                value = CCKModel.MIN_RESISTANCE;
            }
//            super.element.setResistance( value );
//            System.out.println( "set battery internal resistance= " + value );
            battery.setInternalResistance( value );
            updateDuringDrag();
        }
    }

    public void delete() {
        circuit.removeCircuitListener( circuitListener );
        setVisible( false );
        dispose();
    }

    public static class ACVoltageSourceEditor extends ComponentEditor {
        private ACVoltageSource branch;

        public ACVoltageSourceEditor( ICCKModule module, ACVoltageSource branch, JComponent apparatusPanel, Circuit circuit ) {
            super( module, CCKStrings.getString( "ac.voltage.source.editor" ), branch, apparatusPanel, CCKStrings.getString( "BranchSource.AC" ), CCKStrings.getString( "ReadoutGraphic.ACVolts" ), 0, 100, branch.getAmplitude(), circuit );
            this.branch = branch;
        }

        protected void doChange( double value ) {
            branch.setAmplitude( value );
            updateDuringDrag();
        }
    }
}
