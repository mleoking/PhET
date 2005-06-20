/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Nov 22, 2004
 * Time: 11:11:57 AM
 * Copyright (c) Nov 22, 2004 by Sam Reid
 */
public class Force1dControlPanel extends IForceControl {
    private Force1DModule module;
    private Force1DModel model;
    public static final double MAX_KINETIC_FRICTION = 2.0;
    private FreeBodyDiagramSuite freeBodyDiagramSuite;
    private JComboBox comboBox;
    private ModelSlider mass;
    private ModelSlider gravity;
    private ModelSlider staticFriction;
    private ModelSlider kineticFriction;
    private BarrierCheckBox barriers;
    private static final double MAX_GRAV = 30;

    public Force1dControlPanel( final Force1DModule module ) {
        super( module );
        this.module = module;
        model = module.getForceModel();
        freeBodyDiagramSuite = new FreeBodyDiagramSuite( module );

        JButton lessControls = new JButton( "Less Controls." );
        lessControls.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSimpleControlPanel();
            }
        } );
        addControl( lessControls );

        freeBodyDiagramSuite.setControlPanel( this );

        addControl( freeBodyDiagramSuite.getCheckBox() );
        addControl( freeBodyDiagramSuite.getFBDPanel() );

        comboBox = new ObjectComboBox( module, module.getImageElements(), this );
        addControl( comboBox );

        mass = createControl( 5, 0.1, 1000, "Mass", "kg", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setMass( value );
            }
        } );
        gravity = createControl( 9.8, 0, MAX_GRAV, "Gravity", "N/kg", new SpinnerHandler() {
            public void changed( double value ) {
                model.setGravity( value );
            }
        } );
        model.addListener( new Force1DModel.Listener() {
            public void appliedForceChanged() {
            }

            public void gravityChanged() {
                gravity.setValue( model.getGravity() );
            }

            public void wallForceChanged() {
            }
        } );

        staticFriction = createControl( 0.10, 0, MAX_KINETIC_FRICTION, "Static Friction", "", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setStaticFriction( value );
            }
        } );
        kineticFriction = createControl( 0.05, 0, MAX_KINETIC_FRICTION, "Kinetic Friction", "", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setKineticFriction( value );
            }
        } );

        model.getBlock().addListener( new Block.Listener() {
            public void positionChanged() {
            }

            public void propertyChanged() {
                double staticFrictionVal = model.getBlock().getStaticFriction();
                double kineticFrictionVal = model.getBlock().getKineticFriction();
                staticFriction.setValue( staticFrictionVal );
                kineticFriction.setValue( kineticFrictionVal );
            }
        } );
        VerticalLayoutPanel controls = new VerticalLayoutPanel();
        controls.add( gravity );
        controls.add( mass );
        controls.add( staticFriction );
        controls.add( kineticFriction );

        barriers = new BarrierCheckBox( module );
        model.getBlock().addListener( new Block.Listener() {
            public void positionChanged() {
            }

            public void propertyChanged() {
                //make sure static>=kinetic.
                double s = model.getBlock().getStaticFriction();
                double k = model.getBlock().getKineticFriction();
                if( s < k ) {
                    staticFriction.setValue( k );
                }
            }
        } );
        model.getBlock().addListener( new Block.Listener() {
            public void positionChanged() {
            }

            public void propertyChanged() {
                double s = model.getBlock().getStaticFriction();
                double k = model.getBlock().getKineticFriction();
                staticFriction.setValue( s );
                kineticFriction.setValue( k );
                mass.setValue( model.getBlock().getMass() );
            }
        } );
        module.setObject( module.imageElementAt( 0 ) );
        super.setHelpPanelEnabled( true );
        if( Toolkit.getDefaultToolkit().getScreenSize().width >= 1280 ) {

        }
        else {
            super.removeTitle();
        }

        module.getForceModel().getPlotDeviceModel().addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                enableChanges();
            }

            public void playbackStarted() {
                disableChanges();
            }

            public void playbackPaused() {
                enableChanges();
            }

            public void playbackFinished() {
                enableChanges();
            }
        } );
        JButton restore = new JButton( "Restore Defaults" );
        restore.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.restoreDefaults();
            }
        } );


        JPanel smallPanel = new JPanel();
        smallPanel.add( barriers );
        smallPanel.add( restore );

        addControlFullWidth( smallPanel );
        addControlFullWidth( controls );

        Hashtable labelTable = new Hashtable();

        labelTable.put( new Double( 1.67 ), toJLabel( "Moon" ) );
        labelTable.put( new Double( 9.8 ), toJLabel( "Earth" ) );
        labelTable.put( new Double( 22.9 ), toJLabel( "Jupiter" ) );
        gravity.setModelLabels( labelTable );
        gravity.setPaintTicks( false );
    }

    static final Stroke stroke = new BasicStroke( 1 );

    public static JLabel toJLabel( String name ) {
        JLabel horizLabel = new JLabel( name ) {
            protected void paintComponent( Graphics g ) {

                Graphics2D g2 = (Graphics2D)g;
                GraphicsState gs = new GraphicsState( g2 );
                g.setColor( Color.blue );
                int x = getWidth() / 2;
                g2.setStroke( stroke );
                g.drawLine( x, 0, x, 2 );
                gs.restoreGraphics();
                super.paintComponent( g );
            }
        };
        horizLabel.setFont( new Font( "Lucida Sans", 0, 10 ) );
        Dimension pre = horizLabel.getPreferredSize();
        horizLabel.setPreferredSize( new Dimension( pre.width, pre.height + 5 ) );
        return horizLabel;
    }

    private void disableChanges() {
        setChangesEnabled( false );
    }

    private void enableChanges() {
        setChangesEnabled( true );
    }

    private void setChangesEnabled( boolean enabled ) {
        barriers.setEnabled( enabled );
        comboBox.setEnabled( enabled );
        mass.setEnabled( enabled );
        gravity.setEnabled( enabled );
        staticFriction.setEnabled( enabled );
        kineticFriction.setEnabled( enabled );
        barriers.setEnabled( enabled );
    }

    private ModelSlider createControl( double value, double min, double max, String name, String units, final SpinnerHandler handler ) {
        final ModelSlider modelSlider = new ModelSlider( name, units, min, max, value );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = modelSlider.getValue();
                handler.changed( value );
            }
        } );
        handler.changed( value );
        modelSlider.setNumMajorTicks( 5 );
        modelSlider.setNumMinorTicks( 0 );
        if( modelSlider.getUnitsReadout() != null ) {
            modelSlider.getUnitsReadout().setBackground( module.getPhetLookAndFeel().getBackgroundColor() );
        }
        modelSlider.setTitleFont( module.getPhetLookAndFeel().getFont() );
        return modelSlider;
    }

    public void reset() {
        freeBodyDiagramSuite.reset();
    }

    public void handleUserInput() {
        freeBodyDiagramSuite.handleUserInput();
    }

    public void updateGraphics() {
        freeBodyDiagramSuite.updateGraphics();
    }

    public FreeBodyDiagramSuite getFreeBodyDiagramSuite() {
        return freeBodyDiagramSuite;

    }

    interface SpinnerHandler {
        void changed( double value );
    }
}

