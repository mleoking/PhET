package edu.colorado.phet.movingman.force1d_orig;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ModelSliderLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.movingman.force1d_orig.common_force1d.view.components.VerticalLayoutPanel;
import edu.colorado.phet.movingman.force1d_orig.common_force1d.view.util.GraphicsState;
import edu.colorado.phet.movingman.force1d_orig.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.movingman.force1d_orig.model.Block;
import edu.colorado.phet.movingman.force1d_orig.model.Force1DModel;
import edu.colorado.phet.movingman.force1d_orig.view.FreeBodyDiagramSuite;
import edu.colorado.phet.movingman.force1d_orig.ObjectComboBox;

/**
 * User: Sam Reid
 * Date: Nov 22, 2004
 * Time: 11:11:57 AM
 */
public class Force1dControlPanel extends IForceControl {
    private Force1DApplication module;
    private Force1DModel model;
    public static final double MAX_KINETIC_FRICTION = 2.0;
    private FreeBodyDiagramSuite freeBodyDiagramSuite;
    private JComboBox comboBox;
    private LinearValueControl mass;
    private LinearValueControl gravity;
    private LinearValueControl staticFriction;
    private LinearValueControl kineticFriction;
    private BarrierCheckBox barriers;
    private static final double MAX_GRAV = 30;

    public Force1dControlPanel( final Force1DApplication module ) {
        super( module );
        this.module = module;
        model = module.getForceModel();
        freeBodyDiagramSuite = new FreeBodyDiagramSuite( module );

        JButton lessControls = new JButton( SimStrings.get( "Force1dControlPanel.lessControls" ) );
        lessControls.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSimpleControlPanel();
            }
        } );
        addControl( lessControls );

        freeBodyDiagramSuite.setControlPanel( this );

        addControl( freeBodyDiagramSuite.getCheckBox() );
        addControl( freeBodyDiagramSuite.getFBDPanel() );

        comboBox = new ObjectComboBox( module, module.getImageElements() );
        addControl( comboBox );

        mass = createControl( 5, 0.1, 1000, SimStrings.get( "Force1dControlPanel.mass" ), SimStrings.get( "Force1dControlPanel.kg" ), new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setMass( value );
            }
        } );
        mass.setMajorTickSpacing( ( mass.getMaximum() - mass.getMinimum() ) / 2.0 );
        gravity = createControl( 9.8, 0, MAX_GRAV, SimStrings.get( "Force1dControlPanel.gravity" ), SimStrings.get( "Force1dControlPanel.gravityUnits" ), new SpinnerHandler() {
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

        staticFriction = createControl( 0.10, 0, MAX_KINETIC_FRICTION, SimStrings.get( "Force1dControlPanel.staticFriction" ), "", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setStaticFriction( value );
            }
        } );
        staticFriction.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                //todo: this hack only works if the static and kinetic friction sliders have the same range
                if ( staticFriction.getSlider().getValue() <= kineticFriction.getSlider().getValue() ) {
                    //todo: this hack seems to be necessary to get the slider value to snap to (since the slider somehow has a value different than the location of its thumb icon)
                    staticFriction.getSlider().setValue( kineticFriction.getSlider().getValue() + 1 );
                    staticFriction.getSlider().setValue( kineticFriction.getSlider().getValue() );
                }
            }
        } );
        kineticFriction = createControl( 0.05, 0, MAX_KINETIC_FRICTION, SimStrings.get( "Force1dControlPanel.kineticFriction" ), "", new SpinnerHandler() {
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
                if ( s < k ) {
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
        if ( Toolkit.getDefaultToolkit().getScreenSize().width >= 1280 ) {

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
        JButton restore = new JButton( SimStrings.get( "Force1dControlPanel.restoreDefaults" ) );
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

        labelTable.put( new Double( 1.67 ), toJLabel( SimStrings.get( "Force1dControlPanel.moon" ) ) );
        labelTable.put( new Double( 9.8 ), toJLabel( SimStrings.get( "Force1dControlPanel.earth" ) ) );
        labelTable.put( new Double( 22.9 ), toJLabel( SimStrings.get( "Force1dControlPanel.jupiter" ) ) );
        gravity.setTickLabels( labelTable );
//        gravity.setPaintTicks( false );
    }

    static final Stroke stroke = new BasicStroke( 1 );

    public static JLabel toJLabel( String name ) {
        JLabel horizLabel = new JLabel( name ) {
            protected void paintComponent( Graphics g ) {

                Graphics2D g2 = (Graphics2D) g;
                GraphicsState gs = new GraphicsState( g2 );
                g.setColor( Color.blue );
                int x = getWidth() / 2;
                g2.setStroke( stroke );
                g.drawLine( x, 0, x, 2 );
                gs.restoreGraphics();
                super.paintComponent( g );
            }
        };
        horizLabel.setFont( new PhetFont( Font.PLAIN, 10 ) );
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

    private LinearValueControl createControl( double value, double min, double max, String name, String units, final SpinnerHandler handler ) {
        final LinearValueControl linearValueControl = new LinearValueControl( min, max, name, "0.0", units, new ModelSliderLayoutStrategy() );
        linearValueControl.setValue( value );
        linearValueControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handler.changed( linearValueControl.getValue() );
            }
        } );
        handler.changed( linearValueControl.getValue() );
        linearValueControl.setMinorTicksVisible( false );
        linearValueControl.setMajorTickSpacing( ( max - min ) / 5 );
        linearValueControl.setBorder( BorderFactory.createEtchedBorder() );
        return linearValueControl;
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

