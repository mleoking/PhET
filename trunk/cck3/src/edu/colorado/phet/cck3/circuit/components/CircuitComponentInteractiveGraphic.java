/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public CircuitComponentInteractiveGraphic( final IComponentGraphic circuitComponentGraphic, final CircuitGraphic cg ) {
        super( circuitComponentGraphic );
        CCK3Module module = cg.getModule();
        this.cg = cg;
        this.transform = circuitComponentGraphic.getModelViewTransform2D();
        this.circuitComponentGraphic = circuitComponentGraphic;
        addCursorHandBehavior();
        MouseInputListener mouseListener = new ComponentMouseListener( cg, circuitComponentGraphic );
        addMouseInputListener( mouseListener );

        CircuitComponent cc = circuitComponentGraphic.getCircuitComponent();
        if( cc instanceof Battery ) {
            final Battery batt = (Battery)cc;
            BatteryJMenu bj = new BatteryJMenu( batt, module );
            addPopupMenuBehavior( bj );
        }
        else if( cc instanceof Resistor ) {
            Resistor res = (Resistor)cc;
            ResistorMenu resistorMenu = new ResistorMenu( res, module );
            addPopupMenuBehavior( resistorMenu.getMenu() );
        }
        else if( cc instanceof Switch ) {
            Switch swit = (Switch)cc;
            SwitchMenu switchMenu = new SwitchMenu( swit, module );
            addPopupMenuBehavior( switchMenu.getMenu() );
        }
        else if( cc instanceof Bulb ) {
            Bulb bulb = (Bulb)cc;
            BulbMenu bulbMenu = new BulbMenu( bulb, module );
            addPopupMenuBehavior( bulbMenu.getMenu() );
        }
        else if( cc instanceof SeriesAmmeter ) {
            SeriesAmmeter ammeter = (SeriesAmmeter)cc;
            SeriesAmmeterMenu sam = new SeriesAmmeterMenu( ammeter, module );
            addPopupMenuBehavior( sam.getMenu() );
        }

    }

    public void delete() {
        circuitComponentGraphic.delete();
    }

    static class ComponentMenu {
        protected JPopupMenu menu = new JPopupMenu();
        private CCK3Module module;
        Branch branch;

        public ComponentMenu( Branch branch, CCK3Module module ) {
            this.branch = branch;
            this.module = module;
        }

        public static void finish( final CCK3Module module, final Branch branch, JPopupMenu menu ) {
            JMenuItem remove = new JMenuItem( "Remove" );
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
            finish( module, branch, getMenu() );
        }

        public JPopupMenu getMenu() {
            return menu;
        }
    }

    static class ResistorMenu extends ComponentMenu {
        Resistor res;

        public ResistorMenu( Resistor res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            final ComponentEditor.ResistorEditor re = new ComponentEditor.ResistorEditor( res, module.getApparatusPanel(), module.getCircuit() );
            JMenuItem edit = new JMenuItem( "Change Resistance" );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    re.setVisible( true );
                }
            } );
            menu.add( edit );
            finish();
        }

    }

    static class BulbMenu extends ComponentMenu {
        CircuitComponent res;

        public BulbMenu( CircuitComponent res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            final ComponentEditor.ResistorEditor re = new ComponentEditor.ResistorEditor( res, module.getApparatusPanel(), module.getCircuit() );
            JMenuItem edit = new JMenuItem( "Change Resistance" );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    re.setVisible( true );
                }
            } );
            menu.add( edit );
            finish();
        }
    }

    static class SwitchMenu extends ComponentMenu {
        Switch res;

        public SwitchMenu( Switch res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            menu = new JPopupMenu();
            finish();
        }

    }

    static class SeriesAmmeterMenu extends ComponentMenu {
        SeriesAmmeter res;

        public SeriesAmmeterMenu( SeriesAmmeter res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            menu = new JPopupMenu();
            finish();
        }

    }

    static class BatteryJMenu extends JPopupMenu {
        Battery battery;
        private CCK3Module module;
        private JMenuItem editInternal;

        public BatteryJMenu( final Battery branch, CCK3Module module ) {
            this.battery = branch;
            this.module = module;
            JMenuItem edit = new JMenuItem( "Change Voltage" );
            final ComponentEditor.BatteryEditor be = new ComponentEditor.BatteryEditor( branch, module.getApparatusPanel(), module.getCircuit() );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    be.setVisible( true );
                }
            } );
            add( edit );

            editInternal = new JMenuItem( "Change Internal Resistance" );
            editInternal.setEnabled( false );
            final ComponentEditor.BatteryResistanceEditor bre = new ComponentEditor.BatteryResistanceEditor( battery, module.getApparatusPanel(), module.getCircuit() );
            editInternal.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    bre.setVisible( true );
                }
            } );
            add( editInternal );

            JMenuItem reverse = new JMenuItem( "Reverse" );
            reverse.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reverse( branch );
                }
            } );
            add( reverse );

            ComponentMenu.finish( module, branch, this );
        }

        public void show( Component invoker, int x, int y ) {
            editInternal.setEnabled( module.isInternalResistanceOn() );
            super.show( invoker, x, y );
        }

        private void reverse( Battery batt ) {
            Junction start = batt.getStartJunction();
            Junction end = batt.getEndJunction();
            batt.setStartJunction( end );
            batt.setEndJunction( start );
            batt.notifyObservers();
            batt.fireKirkhoffChange();
        }
    }

    static class BatteryMenu extends ComponentMenu {
        Battery batt;

        public BatteryMenu( final Battery batt, CCK3Module module ) {
            super( batt, module );
            this.batt = batt;
//            final ComponentEditor.BatteryEditor be = new ComponentEditor.BatteryEditor( batt, module.getApparatusPanel(), module.getCircuit() );
//            JMenuItem edit = new JMenuItem( "Change Voltage" );
//            edit.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    be.setVisible( true );
//                }
//            } );
//            menu.add( edit );

//            JMenuItem reverse = new JMenuItem( "Reverse" );
//            reverse.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    reverse( batt );
//                }
//            } );
//            menu.add( reverse );
            finish();
        }

        private void reverse( Battery batt ) {
            Junction start = batt.getStartJunction();
            Junction end = batt.getEndJunction();
            batt.setStartJunction( end );
            batt.setEndJunction( start );
            batt.notifyObservers();
            batt.fireKirkhoffChange();
        }

        public JPopupMenu getMenu() {
            return super.getMenu();
        }
    }


    public IComponentGraphic getCircuitComponentGraphic() {
        return circuitComponentGraphic;
    }

    public Branch getBranch() {
        return circuitComponentGraphic.getCircuitComponent();
    }

}
