/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.common.RepaintyMenu;
import edu.colorado.phet.cck3.grabbag.GrabBagResistor;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 9:12:00 AM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class CircuitComponentInteractiveGraphic extends DefaultInteractiveGraphic implements Deletable {
    private CircuitGraphic cg;
    private ModelViewTransform2D transform;
    private Point lastPoint;
    private IComponentGraphic circuitComponentGraphic;
    private MouseInputListener mouseListener;
    private CCKMenu menu;

    public CircuitComponentInteractiveGraphic( final IComponentGraphic circuitComponentGraphic, final CircuitGraphic cg ) {
        super( circuitComponentGraphic );
        CCK3Module module = cg.getModule();
        this.cg = cg;
        this.transform = circuitComponentGraphic.getModelViewTransform2D();
        this.circuitComponentGraphic = circuitComponentGraphic;
        addCursorHandBehavior();
        mouseListener = new ComponentMouseListener( cg, circuitComponentGraphic );
        addMouseInputListener( mouseListener );

        CircuitComponent cc = circuitComponentGraphic.getCircuitComponent();
        if( cc instanceof Battery ) {
            final Battery batt = (Battery)cc;
            menu = new BatteryJMenu( batt, module );
            addPopupMenuBehavior( menu.getMenuComponent() );
        }
        else if( cc instanceof Resistor ) {
            Resistor res = (Resistor)cc;
            menu = new ResistorMenu( res, module );
            addPopupMenuBehavior( menu.getMenuComponent() );
        }
        else if( cc instanceof Switch ) {
            Switch swit = (Switch)cc;
            menu = new SwitchMenu( swit, module );
            addPopupMenuBehavior( menu.getMenuComponent() );
        }
        else if( cc instanceof Bulb ) {
            Bulb bulb = (Bulb)cc;
            menu = new BulbMenu( bulb, module );
            addPopupMenuBehavior( menu.getMenuComponent() );
        }
        else if( cc instanceof SeriesAmmeter ) {
            SeriesAmmeter ammeter = (SeriesAmmeter)cc;
            menu = new SeriesAmmeterMenu( ammeter, module );
            addPopupMenuBehavior( menu.getMenuComponent() );
        }

    }

    public void delete() {
        circuitComponentGraphic.delete();
        if( menu != null ) {
            menu.delete();
        }
    }

    public CCKMenu getMenu() {
        return menu;
    }

    static abstract class ComponentMenu implements CCKMenu {
        protected RepaintyMenu menu;
        private CCK3Module module;
        Branch branch;
        private JCheckBoxMenuItem setVisibleItem;

        public ComponentMenu( Branch branch, CCK3Module module ) {
            this.branch = branch;
            this.module = module;
            menu = new RepaintyMenu( module.getApparatusPanel() );
        }

        public boolean isVisiblityRequested() {
            if( branch instanceof GrabBagResistor ) {
                return false;
            }
            return setVisibleItem.isSelected();
        }

        public static JCheckBoxMenuItem finish( final CCK3Module module, final Branch branch, RepaintyMenu menu ) {
            final JCheckBoxMenuItem showValue = new JCheckBoxMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ShowValueMenuItem" ) );
            menu.addPopupMenuListener( new PopupMenuListener() {
                public void popupMenuCanceled( PopupMenuEvent e ) {
                }

                public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                }

                public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                    ReadoutGraphic rg = module.getCircuitGraphic().getReadoutGraphic( branch );
                    if( rg != null ) {
                        showValue.setSelected( rg.isVisible() );
                    }
                }
            } );
            showValue.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    boolean vis = showValue.isSelected();
                    ReadoutGraphic rg = module.getCircuitGraphic().getReadoutGraphic( branch );
                    if( rg != null ) {
                        rg.setVisible( vis );
                    }
                }
            } );
            if( branch instanceof CircuitComponent && !( branch instanceof SeriesAmmeter ) && !( branch instanceof Switch ) ) {
                if( module.getParameters().allowShowReadouts() ) {
                    menu.add( showValue );
                }

            }
            addRemoveButton( menu, module, branch );
            return showValue;
        }

        static void addRemoveButton( RepaintyMenu menu, final CCK3Module module, final Branch branch ) {
            JMenuItem remove = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.RemoveMenuItem" ) );
            remove.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.removeBranch( branch );
                }
            } );
            int num = menu.getComponentCount();
            if( num > 0 ) {
                menu.addSeparator();
            }
            menu.add( remove );
        }


        protected void finish() {
            setVisibleItem = finish( module, branch, getMenu() );
        }

        public RepaintyMenu getMenu() {
            return menu;
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
    }

    static class ResistorMenu extends ComponentMenu {
        Resistor res;
        private ComponentEditor.ResistorEditor editor;

        public ResistorMenu( Resistor res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            if( res instanceof GrabBagResistor ) {
                addRemoveButton( getMenu(), module, res );
            }
            else {
                editor = new ComponentEditor.ResistorEditor( module, res, module.getApparatusPanel(), module.getCircuit() );
                JMenuItem edit = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ResistanceMenuItem" ) );
                edit.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        editor.setVisible( true );
                    }
                } );
                menu.add( edit );
                finish();
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

    static class BulbMenu extends ComponentMenu {
        CircuitComponent bulb;
        private ComponentEditor.BulbResistanceEditor editor;

        public BulbMenu( final Bulb bulb, final CCK3Module module ) {
            super( bulb, module );
            this.bulb = bulb;
            editor = new ComponentEditor.BulbResistanceEditor( module, bulb, module.getApparatusPanel(), module.getCircuit() );
            JMenuItem edit = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.ResistanceMenuItem" ) );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            menu.add( edit );
            final JMenuItem flip = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.FlipMenuItem" ) );
            flip.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    bulb.flip( module );
                }
            } );
            menu.addPopupMenuListener( new PopupMenuListener() {
                public void popupMenuCanceled( PopupMenuEvent e ) {
                }

                public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                }

                public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                    if( module.getCircuitGraphic().isLifelike() ) {
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
            menu.add( flip );
            finish();
        }

        public JPopupMenu getMenuComponent() {
            return menu;
        }

        public void delete() {
            super.delete();
            editor.delete();
        }
    }

    static class SwitchMenu extends ComponentMenu {
        Switch res;

        public SwitchMenu( Switch res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            menu = new RepaintyMenu( module.getApparatusPanel() );
            finish();
        }

        public JPopupMenu getMenuComponent() {
            return getMenu();
        }
    }

    static class SeriesAmmeterMenu extends ComponentMenu {
        SeriesAmmeter res;

        public SeriesAmmeterMenu( SeriesAmmeter res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            menu = new RepaintyMenu( module.getApparatusPanel() );
            finish();
        }

        public JPopupMenu getMenuComponent() {
            return getMenu();
        }
    }

    public static class BatteryJMenu extends RepaintyMenu implements CCKMenu {
        private Battery battery;
        private CCK3Module module;
        private JMenuItem editInternal;

        public static final ArrayList instances = new ArrayList();
        private ComponentEditor.BatteryResistanceEditor resistanceEditor;
        private ComponentEditor.BatteryEditor editor;
        private JCheckBoxMenuItem setVisibleItem;

        public BatteryJMenu( final Battery branch, CCK3Module module ) {
            super( module.getApparatusPanel() );
            this.battery = branch;
            this.module = module;
            JMenuItem edit = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.VoltageMenuItem" ) );
            editor = new ComponentEditor.BatteryEditor( module, branch, module.getApparatusPanel(), module.getCircuit() );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    editor.setVisible( true );
                }
            } );
            add( edit );

            editInternal = new JMenuItem( SimStrings.get( "CircuitComponentInteractiveGraphic.InternalResistanceMenuItem" ) );
            editInternal.setEnabled( false );
            resistanceEditor = new ComponentEditor.BatteryResistanceEditor( module, battery, module.getApparatusPanel(), module.getCircuit() );
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

            setVisibleItem = ComponentMenu.finish( module, branch, this );
            instances.add( this );
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

    public IComponentGraphic getCircuitComponentGraphic() {
        return circuitComponentGraphic;
    }

    public Branch getBranch() {
        return circuitComponentGraphic.getCircuitComponent();
    }

}
