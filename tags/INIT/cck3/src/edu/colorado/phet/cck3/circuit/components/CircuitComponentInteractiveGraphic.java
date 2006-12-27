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
public class CircuitComponentInteractiveGraphic extends DefaultInteractiveGraphic {
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

        CircuitComponent cc = circuitComponentGraphic.getComponent();
        if( cc instanceof Battery ) {
            final Battery batt = (Battery)cc;
            BatteryMenu batteryMenu = new BatteryMenu( batt, module );
            addPopupMenuBehavior( batteryMenu.getMenu() );
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

    static class ComponentMenu {
        protected JPopupMenu menu = new JPopupMenu();
        private CCK3Module module;
        Branch branch;

        public ComponentMenu( Branch branch, CCK3Module module ) {
            this.branch = branch;
            this.module = module;
        }

        protected void finish() {
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

        public JPopupMenu getMenu() {
            return menu;
        }
    }

    static class ResistorMenu extends ComponentMenu {
        Resistor res;

        public ResistorMenu( Resistor res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            final ComponentEditor.ResistorEditor re = new ComponentEditor.ResistorEditor( res, module.getApparatusPanel() );
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
        Bulb res;

        public BulbMenu( Bulb res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            final ComponentEditor.ResistorEditor re = new ComponentEditor.ResistorEditor( res, module.getApparatusPanel() );
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

    static class BatteryMenu extends ComponentMenu {
        Battery batt;

        public BatteryMenu( final Battery batt, CCK3Module module ) {
            super( batt, module );
            this.batt = batt;
            final ComponentEditor.BatteryEditor be = new ComponentEditor.BatteryEditor( batt, module.getApparatusPanel() );
            JMenuItem edit = new JMenuItem( "Change Voltage" );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    be.setVisible( true );
                }
            } );
            menu.add( edit );

            JMenuItem reverse = new JMenuItem( "Reverse" );
            reverse.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reverse( batt );
                }
            } );
            menu.add( reverse );
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
    }


    public IComponentGraphic getCircuitComponentGraphic() {
        return circuitComponentGraphic;
    }

    public Branch getBranch() {
        return circuitComponentGraphic.getComponent();
    }

}
