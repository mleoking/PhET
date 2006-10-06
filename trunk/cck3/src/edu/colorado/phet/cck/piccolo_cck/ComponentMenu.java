package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.ResetDynamicsMenuItem;
import edu.colorado.phet.cck.common.CCKStrings;
import edu.colorado.phet.cck.common.JPopupMenuRepaintWorkaround;
import edu.colorado.phet.cck.grabbag.GrabBagResistor;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public abstract class ComponentMenu extends AbstractComponentMenu {
    private ICCKModule module;
    private Branch branch;
    private JCheckBoxMenuItem setVisibleItem;

    public ComponentMenu( Branch branch, ICCKModule module ) {
        super( module.getSimulationPanel() );
        this.branch = branch;
        this.module = module;
    }

    public boolean isVisiblityRequested() {
        if( branch instanceof GrabBagResistor ) {
            return false;
        }
        return setVisibleItem.isSelected();
    }

//    protected void finish() {
//        
//    }

    protected void initRemoveButton() {
        addRemoveButton( this, module, branch );
    }

    protected void initShowValueButton() {
        setVisibleItem = createShowValueButton( this, module, branch );
    }

    public JPopupMenuRepaintWorkaround getMenu() {
        return this;
    }

    public void delete() {
    }

    public void setVisibilityRequested( boolean b ) {
        if( branch instanceof GrabBagResistor ) {
            return;
        }
        if( setVisibleItem.isSelected() != b ) {
            setVisibleItem.doClick( 20 );
        }
    }

    public static class ResistorMenu extends ComponentMenu {
        private Resistor res;
        private ComponentEditor.ResistorEditor editor;

        public ResistorMenu( Resistor res, ICCKModule module ) {
            super( res, module );
            this.res = res;
            if( res instanceof GrabBagResistor ) {//todo this should have a separate class, not reuse ResistorMenu
                addRemoveButton( getMenu(), module, res );
            }
            else {
                editor = new ComponentEditor.ResistorEditor( module, res, module.getSimulationPanel(), module.getCircuit() );
                JMenuItem edit = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ResistanceMenuItem" ) );
                edit.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        editor.setVisible( true );
                    }
                } );
                add( edit );
                initShowValueButton();
                initRemoveButton();
            }
        }

        public JPopupMenu getMenuComponent() {
            return getMenu();
        }

        public void delete() {
            super.delete();
            if( editor != null ) {
                editor.delete();
            }
        }

    }

    public static class InductorMenu extends ComponentMenu {
        private Inductor inductor;
        private ComponentEditor editor;

        public InductorMenu( final Inductor inductor, ICCKModule module ) {
            super( inductor, module );
            this.inductor = inductor;
            double min = 0;
            double max = 50;
            editor = new ComponentEditor( module, CCKStrings.getString( "inductance" ), inductor, module.getSimulationPanel(),
                                          CCKStrings.getString( "inductance" ), CCKStrings.getString( "henries" ), min, max,
                                          inductor.getInductance(), module.getCircuit() ) {
                protected void doChange( double value ) {
                    inductor.setInductance( value );
                }
            };
            JMenuItem edit = new JMenuItem( CCKStrings.getString( "edit.inductance" ) );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
            add( new ResetDynamicsMenuItem( CCKStrings.getString( "discharge.inductor" ), inductor ) );
            initShowValueButton();
            initRemoveButton();
        }

        public JPopupMenu getMenuComponent() {
            return getMenu();
        }

        public void delete() {
            super.delete();
            if( editor != null ) {
                editor.delete();
            }
        }

    }

    public static class CapacitorMenu extends ComponentMenu {
        private Capacitor capacitor;
        private ComponentEditor editor;

        public CapacitorMenu( final Capacitor capacitor, ICCKModule module ) {
            super( capacitor, module );
            this.capacitor = capacitor;
            editor = new ComponentEditor( module, CCKStrings.getString( "capacitance" ), capacitor, module.getSimulationPanel(), CCKStrings.getString( "capacitance" ), CCKStrings.getString( "farads" ), 0, 0.05, Capacitor.DEFAULT_CAPACITANCE, module.getCircuit() ) {
                protected void doChange( double value ) {
                    capacitor.setCapacitance( value );
                }
            };
            JMenuItem edit = new JMenuItem( CCKStrings.getString( "edit.capacitance" ) );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
            add( new ResetDynamicsMenuItem( CCKStrings.getString( "discharge.capacitor" ), capacitor ) );
            initShowValueButton();
            initRemoveButton();
        }

        public JPopupMenu getMenuComponent() {
            return getMenu();
        }

        public void delete() {
            super.delete();
            if( editor != null ) {
                editor.delete();
            }
        }

    }

    public static class BulbMenu extends ComponentMenu {
        CircuitComponent bulb;
        private ComponentEditor.BulbResistanceEditor editor;

        public BulbMenu( final Bulb bulb, final ICCKModule module ) {
            super( bulb, module );
            this.bulb = bulb;
            editor = new ComponentEditor.BulbResistanceEditor( module, bulb, module.getSimulationPanel(), module.getCircuit() );
            JMenuItem edit = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ResistanceMenuItem" ) );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
            final JMenuItem flip = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.FlipMenuItem" ) );
            flip.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    bulb.flip( module.getCircuit() );
                }
            } );
            addPopupMenuListener( new PopupMenuListener() {
                public void popupMenuCanceled( PopupMenuEvent e ) {
                }

                public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                }

                public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                    if( module.isLifelike() ) {
                        flip.setEnabled( true );
                    }
                    else {
                        flip.setEnabled( false );
                    }
                    if( bulb.isConnectAtRight() ) {
                        flip.setText( SimStrings.get( "CircuitComponentInteractiveGraphic.LeftConnectMenuItem" ) );
                    }
                    else {
                        flip.setText( SimStrings.get( "CircuitComponentInteractiveGraphic.RightConnectMenuItem" ) );
                    }
                }
            } );
            add( flip );
            initShowValueButton();
            initRemoveButton();
        }

        public JPopupMenu getMenuComponent() {
            return this;
        }

        public void delete() {
            super.delete();
            editor.delete();
        }
    }

    public static class SwitchMenu extends DefaultComponentMenu {
        public SwitchMenu( Branch branch, ICCKModule module ) {
            super( branch, module );
        }
    }

    public static class DefaultComponentMenu extends ComponentMenu {

        public DefaultComponentMenu( Branch branch, ICCKModule module ) {
            super( branch, module );
            initShowValueButton();
            initRemoveButton();
        }
    }

    public static class SeriesAmmeterMenu extends DefaultComponentMenu {
        public SeriesAmmeterMenu( Branch branch, ICCKModule module ) {
            super( branch, module );
        }
    }

    public static class BatteryJMenu extends AbstractComponentMenu {
        private Battery battery;
        private ICCKModule module;
        private JMenuItem editInternal;

        public static final ArrayList instances = new ArrayList();
        private ComponentEditor.BatteryResistanceEditor resistanceEditor;
        private ComponentEditor editor;
        private JCheckBoxMenuItem setVisibleItem;

        public BatteryJMenu( final Battery branch, ICCKModule module ) {
            super( module.getSimulationPanel() );
            this.battery = branch;
            this.module = module;
            JMenuItem edit = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.VoltageMenuItem" ) );
            editor = createVoltageEditor( branch );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
            addOptionalItemsAfterEditor();

            editInternal = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.InternalResistanceMenuItem" ) );
            editInternal.setEnabled( false );
            resistanceEditor = new ComponentEditor.BatteryResistanceEditor( module, battery, module.getSimulationPanel(), module.getCircuit() );
            editInternal.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resistanceEditor.setVisible( true );
                }
            } );
            add( editInternal );

            JMenuItem reverse = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ReverseMenuItem" ) );
            reverse.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reverse( branch );
                }
            } );
            add( reverse );

            setVisibleItem = createShowValueButton( this, module, branch );
            addRemoveButton( this, module, branch );
            instances.add( this );
        }

        public Battery getBattery() {
            return battery;
        }

        protected void addOptionalItemsAfterEditor() {
        }

        public ICCKModule getModule() {
            return module;
        }

        protected ComponentEditor createVoltageEditor( Battery branch ) {
            return new ComponentEditor.BatteryEditor( module, branch, module.getSimulationPanel(), module.getCircuit() );
        }

        public void show( Component invoker, int x, int y ) {
            editInternal.setEnabled( module.isInternalResistanceOn() );
            super.show( invoker, x, y );
        }

        public ComponentEditor.BatteryResistanceEditor getResistanceEditor() {
            return resistanceEditor;
        }

        private void reverse( Battery batt ) {
            Junction start = batt.getStartJunction();
            Junction end = batt.getEndJunction();
            batt.setStartJunction( end );
            batt.setEndJunction( start );
            batt.notifyObservers();
            batt.fireKirkhoffChange();
        }

        public JPopupMenu getMenuComponent() {
            return this;
        }

        public void delete() {
            resistanceEditor.delete();
            editor.delete();
        }

        public boolean isVisiblityRequested() {
            return setVisibleItem.isSelected();
        }

        public void setVisibilityRequested( boolean b ) {
            if( setVisibleItem.isSelected() != b ) {
                setVisibleItem.doClick( 20 );
            }
        }
    }


    public static class GrabBagResistorMenu extends ComponentMenu {

        public GrabBagResistorMenu( GrabBagResistor res, ICCKModule module ) {
            super( res, module );
            addRemoveButton( getMenu(), module, res );
        }

    }
}

