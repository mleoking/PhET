package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.controls.ResetDynamicsMenuItem;
import edu.colorado.phet.circuitconstructionkit.controls.JPopupMenuRepaintWorkaround;
import edu.colorado.phet.circuitconstructionkit.model.grabbag.GrabBagResistor;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.*;

public abstract class ComponentMenu extends JPopupMenuRepaintWorkaround {
    protected CCKModule module;
    private Branch branch;
    private JCheckBoxMenuItem setVisibleItem;

    public ComponentMenu( Branch branch, CCKModule module ) {
        super( module.getSimulationPanel() );
        this.branch = branch;
        this.module = module;
    }

    public JPopupMenu getMenuComponent() {
        return this;
    }

    protected JCheckBoxMenuItem createShowValueButton( JPopupMenuRepaintWorkaround menu, final CCKModule module, final Branch branch ) {
        final JCheckBoxMenuItem showValue = new JCheckBoxMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.ShowValueMenuItem" ) );
        menu.addPopupMenuListener( new PopupMenuListener() {
            public void popupMenuCanceled( PopupMenuEvent e ) {
            }

            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
            }

            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                boolean readoutVisibleForBranch = module.isReadoutVisible( branch );
                System.out.println( "readoutVisibleForBranch (" + branch.hashCode() + ")= " + readoutVisibleForBranch );
                showValue.setSelected( readoutVisibleForBranch );
            }
        } );
        showValue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setReadoutVisible( branch, showValue.isSelected() );
            }
        } );
        if ( branch instanceof CircuitComponent && !( branch instanceof SeriesAmmeter ) && !( branch instanceof Switch ) ) {
            if ( module.getParameters().allowShowReadouts() ) {
                menu.add( showValue );
            }
        }
        return showValue;
    }

    protected void addRemoveButton( JPopupMenuRepaintWorkaround menu, final CCKModule module, final Branch branch ) {
        JMenuItem remove = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.RemoveMenuItem" ) );
        remove.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCCKModel().getCircuit().removeBranch( branch );
            }
        } );
        int num = menu.getComponentCount();
        if ( num > 0 ) {
            menu.addSeparator();
        }
        menu.add( remove );
    }

    protected JCheckBoxMenuItem getSetVisibleItem() {
        return setVisibleItem;
    }

    protected void addRemoveButton() {
        addRemoveButton( this, module, branch );
    }

    protected void addShowValueButton() {
        setVisibleItem = createShowValueButton( this, module, branch );
    }

    public JPopupMenuRepaintWorkaround getMenu() {
        return this;
    }

    public void delete() {
    }

    public void setVisibilityRequested( boolean b ) {
        if ( branch instanceof GrabBagResistor ) {
            return;
        }
        if ( setVisibleItem.isSelected() != b ) {
            setVisibleItem.doClick( 20 );
        }
    }

    public static class ResistorMenu extends ComponentMenu {
        private Resistor res;
        private ComponentEditor.ResistorEditor editor;

        public ResistorMenu( Resistor res, CCKModule module ) {
            super( res, module );
            this.res = res;
            if ( res instanceof GrabBagResistor ) {//todo this should have a separate class, not reuse ResistorMenu
                addRemoveButton( getMenu(), module, res );
            }
            else {
                editor = new ComponentEditor.ResistorEditor( module, res, module.getSimulationPanel(), module.getCircuit() );
                JMenuItem edit = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.ResistanceMenuItem" ) );
                edit.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        editor.setVisible( true );
                    }
                } );
                add( edit );
                addShowValueButton();
                addRemoveButton();
            }
        }

        public void delete() {
            super.delete();
            if ( editor != null ) {
                editor.delete();
            }
        }

    }

    public static class InductorMenu extends ComponentMenu {
        private Inductor inductor;
        private ComponentEditor editor;

        public InductorMenu( final Inductor inductor, CCKModule module ) {
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
            addShowValueButton();
            addRemoveButton();
        }

        public void delete() {
            super.delete();
            if ( editor != null ) {
                editor.delete();
            }
        }

    }

    public static class CapacitorMenu extends ComponentMenu {
        private Capacitor capacitor;
        private ComponentEditor editor;

        public CapacitorMenu( final Capacitor capacitor, CCKModule module ) {
            super( capacitor, module );
            this.capacitor = capacitor;
            editor = new ComponentEditor( module, CCKStrings.getString( "capacitance" ), capacitor, module.getSimulationPanel(), CCKStrings.getString( "capacitance" ), CCKStrings.getString( "farads" ), 0, 0.05, capacitor.getCapacitance(), module.getCircuit() ) {
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
            addShowValueButton();
            addRemoveButton();
        }

        public void delete() {
            super.delete();
            if ( editor != null ) {
                editor.delete();
            }
        }

    }

    public static class BulbMenu extends ComponentMenu {
        private Bulb bulb;
        private ComponentEditor.BulbResistanceEditor editor;
        private JMenuItem flip;

        public BulbMenu( final Bulb bulb, final CCKModule module ) {
            super( bulb, module );
            this.bulb = bulb;
            editor = new ComponentEditor.BulbResistanceEditor( module, bulb, module.getSimulationPanel(), module.getCircuit() );
            JMenuItem edit = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.ResistanceMenuItem" ) );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
            flip = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.FlipMenuItem" ) );
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
                    update();
                }
            } );
            add( flip );
            addShowValueButton();
            addRemoveButton();
            update();
        }

        private void update() {
            if ( super.module.isLifelike() ) {
                flip.setEnabled( true );
            }
            else {
                flip.setEnabled( false );
            }
            if ( bulb.isConnectAtLeft() ) {
                flip.setText( CCKResources.getString( "CircuitComponentInteractiveGraphic.RightConnectMenuItem" ) );
            }
            else {
                flip.setText( CCKResources.getString( "CircuitComponentInteractiveGraphic.LeftConnectMenuItem" ) );
            }
        }

        public void delete() {
            super.delete();
            editor.delete();
        }
    }

    public static class SwitchMenu extends DefaultComponentMenu {
        public SwitchMenu( Branch branch, CCKModule module ) {
            super( branch, module );
        }
    }

    public static class DefaultComponentMenu extends ComponentMenu {

        public DefaultComponentMenu( Branch branch, CCKModule module ) {
            super( branch, module );
            addShowValueButton();
            addRemoveButton();
        }
    }

    public static class SeriesAmmeterMenu extends DefaultComponentMenu {
        public SeriesAmmeterMenu( Branch branch, CCKModule module ) {
            super( branch, module );
        }
    }

    public static class BatteryJMenu extends ComponentMenu {
        private Battery battery;
        private JMenuItem editInternal;

        public static final ArrayList instances = new ArrayList();
        private ComponentEditor.BatteryResistanceEditor resistanceEditor;
        private ComponentEditor editor;

        public BatteryJMenu( final Battery branch, CCKModule module ) {
            super( branch, module );
            this.battery = branch;
            JMenuItem edit = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.VoltageMenuItem" ) );
            editor = createVoltageEditor( branch );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
            addOptionalItemsAfterEditor();

            editInternal = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.InternalResistanceMenuItem" ) );
            editInternal.setEnabled( false );
            resistanceEditor = new ComponentEditor.BatteryResistanceEditor( module, battery, module.getSimulationPanel(), module.getCircuit() );
            editInternal.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resistanceEditor.setVisible( true );
                }
            } );
            add( editInternal );

            JMenuItem reverse = new JMenuItem( CCKResources.getString( "CircuitComponentInteractiveGraphic.ReverseMenuItem" ) );
            reverse.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reverse( branch );
                }
            } );
            add( reverse );

            addShowValueButton();
            addRemoveButton();
            instances.add( this );
        }

        public Battery getBattery() {
            return battery;
        }

        protected void addOptionalItemsAfterEditor() {
        }

        protected ComponentEditor createVoltageEditor( Battery branch ) {
            return new ComponentEditor.BatteryEditor( super.getModule(), branch, getModule().getSimulationPanel(), getModule().getCircuit() );
        }

        public void show( Component invoker, int x, int y ) {
            editInternal.setEnabled( CCKModel.INTERNAL_RESISTANCE_ON );
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

        public void delete() {
            resistanceEditor.delete();
            editor.delete();
        }

        public boolean isVisiblityRequested() {
            return getSetVisibleItem().isSelected();
        }

        public void setVisibilityRequested( boolean b ) {
            if ( getSetVisibleItem().isSelected() != b ) {
                getSetVisibleItem().doClick( 20 );
            }
        }
    }

    protected CCKModule getModule() {
        return module;
    }

    public static class GrabBagResistorMenu extends ComponentMenu {
        public GrabBagResistorMenu( GrabBagResistor res, CCKModule module ) {
            super( res, module );
            addRemoveButton( getMenu(), module, res );
        }
    }

    public static class ACVoltageSourceMenu extends ComponentMenu.BatteryJMenu {

        public ACVoltageSourceMenu( Battery branch, CCKModule module ) {
            super( branch, module );
        }

        protected ComponentEditor createVoltageEditor( Battery branch ) {
            return new ComponentEditor.ACVoltageSourceEditor( getModule(), (ACVoltageSource) branch, getModule().getSimulationPanel(), getModule().getCircuit() );
        }

        protected void addOptionalItemsAfterEditor() {
            super.addOptionalItemsAfterEditor();
            JMenuItem edit = new JMenuItem( CCKStrings.getString( "change-frequency" ) );
            final ComponentEditor editor = createFrequencyEditor( (ACVoltageSource) getBattery() );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );
        }

        private ComponentEditor createFrequencyEditor( final ACVoltageSource acVoltageSource ) {
            return new ComponentEditor( getModule(), CCKResources.getString( "ComponentEditor.ACFrequency" ), acVoltageSource, getModule().getSimulationPanel(),
                                        CCKResources.getString( "ComponentEditor.Frequency" ), CCKResources.getString( "ComponentEditor.Hz" ), 0, 5, acVoltageSource.getFrequency() * 100.0, getModule().getCircuit() ) {
                protected void doChange( double value ) {
                    acVoltageSource.setFrequency( value / 100.0 );
                }
            };
        }
    }
}

