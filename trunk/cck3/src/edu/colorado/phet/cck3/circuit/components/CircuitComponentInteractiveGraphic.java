/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

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
        protected RepaintyMenu menu;
        private CCK3Module module;
        Branch branch;

        public ComponentMenu( Branch branch, CCK3Module module ) {
            this.branch = branch;
            this.module = module;
            menu = new RepaintyMenu( module.getApparatusPanel() );
        }

        public static void finish( final CCK3Module module, final Branch branch, RepaintyMenu menu ) {
            final JCheckBoxMenuItem showValue = new JCheckBoxMenuItem( "Show Value" );
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
                menu.add( showValue );
            }

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

        public RepaintyMenu getMenu() {
            return menu;
        }
    }

    static class ResistorMenu extends ComponentMenu {
        Resistor res;

        public ResistorMenu( Resistor res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            final ComponentEditor.ResistorEditor re = new ComponentEditor.ResistorEditor( module, res, module.getApparatusPanel(), module.getCircuit() );
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
        CircuitComponent bulb;

        public BulbMenu( final Bulb bulb, final CCK3Module module ) {
            super( bulb, module );
            this.bulb = bulb;
            final ComponentEditor.BulbResistanceEditor re = new ComponentEditor.BulbResistanceEditor( module, bulb, module.getApparatusPanel(), module.getCircuit() );
            JMenuItem edit = new JMenuItem( "Change Resistance" );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    re.setVisible( true );
                }
            } );
            menu.add( edit );
            final JMenuItem flip = new JMenuItem( "Flip" );
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
                        flip.setText( "Show Connection at Left" );
                    }
                    else {
                        flip.setText( "Show Connection at Right" );
                    }
                }
            } );
            menu.add( flip );
            finish();
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

    }

    static class SeriesAmmeterMenu extends ComponentMenu {
        SeriesAmmeter res;

        public SeriesAmmeterMenu( SeriesAmmeter res, CCK3Module module ) {
            super( res, module );
            this.res = res;
            menu = new RepaintyMenu( module.getApparatusPanel() );
            finish();
        }

    }

    public static class RepaintyMenu extends JPopupMenu {
        private Component target;

        public RepaintyMenu( Component target ) {
            this.target = target;
            addPopupMenuListener( new PopupMenuListener() {
                public void popupMenuCanceled( PopupMenuEvent e ) {
                    waitValidRepaint();
                }

                public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                    waitValidRepaint();
                }

                public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                    waitValidRepaint();
                }
            } );
        }

        public void waitValidRepaint() {
            Thread thread = new Thread( new Runnable() {
                public void run() {
                    try {
                        Thread.sleep( 250 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    Window window = SwingUtilities.getWindowAncestor( target );
                    if( window instanceof JFrame ) {
                        JFrame jeff = (JFrame)window;
                        Container jp = jeff.getContentPane();
                        jp.invalidate();
                        jp.validate();
                        jp.repaint();
                    }
                }
            } );
            thread.setPriority( Thread.MAX_PRIORITY );
            thread.start();
        }

        public void validateRepaint() {
            super.invalidate();
            super.validate();
            super.repaint();
        }

    }

    public static class BatteryJMenu extends RepaintyMenu {
        private Battery battery;
        private CCK3Module module;
        private JMenuItem editInternal;

        public static final ArrayList instances = new ArrayList();
        private ComponentEditor.BatteryResistanceEditor resistanceEditor;

        public BatteryJMenu( final Battery branch, CCK3Module module ) {
            super( module.getApparatusPanel() );
            this.battery = branch;
            this.module = module;
            JMenuItem edit = new JMenuItem( "Change Voltage" );
            final ComponentEditor.BatteryEditor be = new ComponentEditor.BatteryEditor( module, branch, module.getApparatusPanel(), module.getCircuit() );
            edit.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    be.setVisible( true );
                }
            } );
            add( edit );

            editInternal = new JMenuItem( "Change Internal Resistance" );
            editInternal.setEnabled( false );
            resistanceEditor = new ComponentEditor.BatteryResistanceEditor( module, battery, module.getApparatusPanel(), module.getCircuit() );
            editInternal.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resistanceEditor.setVisible( true );
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
    }

    public IComponentGraphic getCircuitComponentGraphic() {
        return circuitComponentGraphic;
    }

    public Branch getBranch() {
        return circuitComponentGraphic.getCircuitComponent();
    }

}
