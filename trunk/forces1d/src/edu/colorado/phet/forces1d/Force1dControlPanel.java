/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.forces1d.common.PhetLookAndFeel;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * User: Sam Reid
 * Date: Nov 22, 2004
 * Time: 11:11:57 AM
 * Copyright (c) Nov 22, 2004 by Sam Reid
 */
public class Force1dControlPanel extends ControlPanel {
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

    public Force1dControlPanel( final Force1DModule module ) {
        super( module );
        this.module = module;
        model = module.getForceModel();
        freeBodyDiagramSuite = new FreeBodyDiagramSuite( module );

        freeBodyDiagramSuite.addTo( this );
        comboBox = new JComboBox( module.getImageElements() );
        comboBox.setBorder( PhetLookAndFeel.createSmoothBorder( "Objects" ) );
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                Object sel = comboBox.getSelectedItem();
                setup( (Force1dObject)sel );
            }
        } );
        add( comboBox );

        mass = createControl( 5, 0.1, 1000, 1.0, "Mass", "kg", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setMass( value );
            }
        } );
        gravity = createControl( 9.8, 0, 100, .2, "Gravity", "N/kg", new SpinnerHandler() {
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

        staticFriction = createControl( 0.10, 0, MAX_KINETIC_FRICTION, .01, "Static Friction", "", new SpinnerHandler() {
            public void changed( double value ) {
                model.getBlock().setStaticFriction( value );
            }
        } );
        kineticFriction = createControl( 0.05, 0, MAX_KINETIC_FRICTION, .01, "Kinetic Friction", "", new SpinnerHandler() {
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

//        controls.setBorder( Force1DUtil.createTitledBorder( "Controls" ) );

        barriers = new BarrierCheckBox( module );
        add( barriers );
        add( controls );
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
        setup( module.imageElementAt( 0 ) );

        super.setHelpPanelEnabled( true );
        super.removeTitle();

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


    private void setup( Force1dObject force1dObject ) {
        module.getForcePanel().getBlockGraphic().setImage( force1dObject );
        model.getBlock().setMass( force1dObject.getMass() );
        model.getBlock().setStaticFriction( force1dObject.getStaticFriction() );
        model.getBlock().setKineticFriction( force1dObject.getKineticFriction() );
    }

    private ModelSlider createControl( double value, double min, double max, double spacing, String name, String units, final SpinnerHandler handler ) {
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
            modelSlider.getUnitsReadout().setBackground( PhetLookAndFeel.backgroundColor );
        }
        return modelSlider;
    }

    public void updateGraphics() {
        freeBodyDiagramSuite.updateGraphics();
    }

    public void reset() {
        freeBodyDiagramSuite.reset();
    }

    interface SpinnerHandler {
        void changed( double value );
    }
}

