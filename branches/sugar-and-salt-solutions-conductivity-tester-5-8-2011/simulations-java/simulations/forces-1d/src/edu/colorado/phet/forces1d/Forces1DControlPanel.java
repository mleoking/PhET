// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.HelpPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;
import edu.colorado.phet.forces1d.model.Block;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.FreeBodyDiagramSuite;

/**
 * User: Sam Reid
 * Date: Jan 11, 2005
 * Time: 8:15:29 PM
 */

public class Forces1DControlPanel extends IForceControl {
    private FreeBodyDiagramSuite fbdSuite;
    private BarrierCheckBox barriers;
    private Forces1DModule module;

    private LinearValueControl mass;
    private LinearValueControl gravity;
    private LinearValueControl staticFriction;
    private LinearValueControl kineticFriction;
    private Force1DModel model;
    private FrictionControl frictionControl;

    static final Stroke stroke = new BasicStroke( 1 );
    private static final double MAX_GRAV = 30;
    public static final double MAX_KINETIC_FRICTION = 2.0;
    private LinearValueControl setPositionControl;

    public Forces1DControlPanel( final Forces1DModule module ) {
        super( module );
        this.module = module;
        this.model = module.getForceModel();


        frictionControl = new FrictionControl( module );

        fbdSuite = new FreeBodyDiagramSuite( module );
        fbdSuite.setControlPanel( this );
        fbdSuite.setPanelVisible( true );


        addControl( new FBDButton( fbdSuite ) );
        addControl( fbdSuite.getFreeBodyDiagramPanel() );

//        if ( Toolkit.getDefaultToolkit().getScreenSize().width < 1200 ) {
//            super.removeTitle();
//        }

        addFullWidth( new ShowComponentForcesCheckBox( module ) );
        addFullWidth( new ShowTotalForceCheckBox( module ) );

        addControl( frictionControl );
        barriers = new BarrierCheckBox( module );
        addFullWidth( barriers );
        super.setHelpPanelEnabled( true );
        module.getForceModel().getPlotDeviceModel().addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                setChangesEnabled( true );
                setPositionControl.setEnabled( false );
            }

            public void playbackStarted() {
                setChangesEnabled( false );
            }

            public void playbackPaused() {
                setChangesEnabled( true );
            }

            public void playbackFinished() {
                setChangesEnabled( true );
            }
        } );
        ObjectSelectionPanel osp = new ObjectSelectionPanel( module.getForce1dObjects(), this );
        addControl( osp );

        setPositionControl = createControl( model.getBlock().getPosition(), -10, 10, Force1DResources.get( "Force1dControlPanel.initial-position" ), Force1DResources.get( "Force1DPanel.positionUnits" ), new SpinnerHandler() {
            public void changed( double value, boolean onFocusLost ) {
                model.getBlock().setPosition( value );
            }
        } );
//        model.getBlock().addListener( new Block.Listener() {
//            public void positionChanged() {
//                setPositionControl.setValue( MathUtil.clamp( -10, model.getBlock().getPosition(), 10 ) );
//            }
//
//            public void propertyChanged() {
//            }
//        } );
        addControl( setPositionControl );

        AdvancedPanel advancedPanel = new AdvancedPanel( Force1DResources.get( "SimpleControlPanel.moreControls" ), Force1DResources.get( "Force1dControlPanel.lessControls" ) );
        addControl( advancedPanel );


        JButton restore = new JButton( Force1DResources.get( "Force1dControlPanel.restoreDefaults" ) );
        restore.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.restoreDefaults();
            }
        } );


        mass = createControl( model.getBlock().getMass(), 0.1, 1000, Force1DResources.get( "Force1dControlPanel.mass" ), Force1DResources.get( "Force1dControlPanel.kg" ), new SpinnerHandler() {
            public void changed( double value, boolean onFocusLost ) {
                model.getBlock().setMass( value );
            }
        } );
        mass.setMajorTickSpacing( ( mass.getMaximum() - mass.getMinimum() ) / 2.0 );
        mass.getTextField().setColumns( 5 );
        gravity = createControl( model.getGravity(), 0, MAX_GRAV, Force1DResources.get( "Force1dControlPanel.gravity" ), Force1DResources.get( "Force1dControlPanel.gravityUnits" ), new SpinnerHandler() {
            public void changed( double value, boolean onFocusLost ) {
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

        staticFriction = createControl( model.getBlock().getStaticFriction(), 0, MAX_KINETIC_FRICTION, Force1DResources.get( "Force1dControlPanel.staticFriction" ), "", new SpinnerHandler() {
            public void changed( double value, boolean onFocusLost ) {
                if ( staticFriction == null || staticFriction.getSlider().hasFocus() || staticFriction.getTextField().hasFocus() || onFocusLost ) {
                    model.getBlock().userSetStaticFriction( value );
                }
            }
        } );
        staticFriction.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                //todo: this hack only works if the static and kinetic friction sliders have the same range
                if ( staticFriction.getSlider().getValue() <= kineticFriction.getSlider().getValue() ) {
                    model.getBlock().userSetStaticFriction( model.getBlock().getKineticFriction() );
//                    //todo: this hack seems to be necessary to get the slider value to snap to (since the slider somehow has a value different than the location of its thumb icon)
//                    staticFriction.getSlider().setValue( kineticFriction.getSlider().getValue() + 1 );
//                    staticFriction.getSlider().setValue( kineticFriction.getSlider().getValue() );
                }
            }
        } );
        kineticFriction = createControl( model.getBlock().getKineticFriction(), 0, MAX_KINETIC_FRICTION, Force1DResources.get( "Force1dControlPanel.kineticFriction" ), "", new SpinnerHandler() {
            public void changed( double value, boolean onFocusLost ) {
                if ( onFocusLost || kineticFriction == null || kineticFriction.getSlider().hasFocus() || kineticFriction.getTextField().hasFocus() ) {
                    model.getBlock().userSetKineticFriction( value );
                }
            }
        } );
//        kineticFriction.

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

//        model.getBlock().addListener( new Block.Listener() {
//            public void positionChanged() {
//            }
//
//            public void propertyChanged() {
//                //make sure static>=kinetic.
//                double s = model.getBlock().getStaticFriction();
//                double k = model.getBlock().getKineticFriction();
//                if ( s < k ) {
//                    staticFriction.setValue( k );
//                }
//            }
//        } );
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

        Hashtable labelTable = new Hashtable();
        labelTable.put( new Double( 1.67 ), toJLabel( Force1DResources.get( "Force1dControlPanel.moon" ) ) );
        labelTable.put( new Double( 9.8 ), toJLabel( Force1DResources.get( "Force1dControlPanel.earth" ) ) );
        labelTable.put( new Double( 22.9 ), toJLabel( Force1DResources.get( "Force1dControlPanel.jupiter" ) ) );
        gravity.setTickLabels( labelTable );

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

//        advancedPanel.addControl( setPositionControl );
        advancedPanel.addControl( gravity );
        advancedPanel.addControl( mass );
        advancedPanel.addControl( staticFriction );
        advancedPanel.addControl( kineticFriction );
        advancedPanel.addControl( restore );
    }


    private void disableChanges() {
        setChangesEnabled( false );
    }

    private void enableChanges() {
        setChangesEnabled( true );
    }

    private void setChangesEnabled( boolean enabled ) {
        barriers.setEnabled( enabled );
        mass.setEnabled( enabled );
        gravity.setEnabled( enabled );
        staticFriction.setEnabled( enabled );
        kineticFriction.setEnabled( enabled );
        barriers.setEnabled( enabled );
        frictionControl.setEnabled( enabled );
    }

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
        horizLabel.setFont( new Font( PhetFont.getDefaultFontName(), 0, 10 ) );
        Dimension pre = horizLabel.getPreferredSize();
        horizLabel.setPreferredSize( new Dimension( pre.width, pre.height + 5 ) );
        return horizLabel;
    }

    private LinearValueControl createControl( double value, double min, double max, String name, String units, final SpinnerHandler handler ) {
        final LinearValueControl linearValueControl = new LinearValueControl( min, max, name, "0.0", units );
        linearValueControl.getTextField().addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                handler.changed( linearValueControl.getValue(), true );
            }
        } );
        linearValueControl.setValue( value );
        linearValueControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handler.changed( linearValueControl.getValue(), false );
            }
        } );
        handler.changed( linearValueControl.getValue(), false );
        linearValueControl.setMinorTicksVisible( false );
        linearValueControl.setMajorTickSpacing( ( max - min ) / 5 );
        linearValueControl.setBorder( BorderFactory.createEtchedBorder() );
        return linearValueControl;
    }

    public void updateGraphics() {
        fbdSuite.updateGraphics();
    }

    public void reset() {
        fbdSuite.reset();
        setPositionControl.setEnabled( true );
        setPositionControl.setValue( model.getBlock().getPosition() );
    }

    public void setup( Force1dObject object ) {
        module.setObject( object );
    }

    public void handleUserInput() {
        fbdSuite.handleUserInput();
    }

    public FreeBodyDiagramSuite getFreeBodyDiagramSuite() {
        return fbdSuite;
    }

    interface SpinnerHandler {
        void changed( double value, boolean onFocusLost );
    }
}
