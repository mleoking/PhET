/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.theramp.view.arrows.AbstractArrowSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 1:31:12 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class RampControlPanel extends ControlPanel {
    private RampModule module;
    public ModelSlider frictionSlider;
    private ModelSlider massSlider;

    /**
     * @param module
     */
    public RampControlPanel( final RampModule module ) {
        super( module );
        this.module = module;
//        JButton3D jb = new JButton3D( "Reset" );
        JButton jb = new JButton( "Reset" );
        addControl( jb );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        JPanel coordinatePanel = new VerticalLayoutPanel();
        final RampPanel rampPanel = module.getRampPanel();
        final JCheckBox descartes = new JCheckBox( "Entire Vectors", rampPanel.isCartesianVisible() );
        descartes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setCartesianArrowsVisible( descartes.isSelected() );
            }
        } );
        final JCheckBox parallel = new JCheckBox( "Parallel Components", rampPanel.isParallelVisible() );
        parallel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setParallelArrowsVisible( parallel.isSelected() );
            }
        } );
        final JCheckBox perpendicular = new JCheckBox( "Perpendicular Components", rampPanel.isPerpendicularVisible() );
        perpendicular.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setPerpendicularArrowsVisible( perpendicular.isSelected() );
            }
        } );

        final JCheckBox x = new JCheckBox( "X-Components", rampPanel.isXVisible() );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setXArrowsVisible( x.isSelected() );
            }
        } );

        final JCheckBox y = new JCheckBox( "Y-Components", rampPanel.isYVisible() );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setYArrowsVisible( y.isSelected() );
            }
        } );

        coordinatePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Coordinate Frames" ) );
        coordinatePanel.add( descartes );
        coordinatePanel.add( parallel );
        coordinatePanel.add( perpendicular );
        coordinatePanel.add( x );
        coordinatePanel.add( y );
        addControl( coordinatePanel );

        JPanel forcePanel = new VerticalLayoutPanel();
        forcePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Forces to Show" ) );

        final JCheckBox showFriction = new JCheckBox( AbstractArrowSet.FRICTION, true );
        showFriction.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setForceVisible( showFriction.getText(), showFriction.isSelected() );
            }
        } );

        final JCheckBox showApplied = new JCheckBox( AbstractArrowSet.APPLIED, true );
        showApplied.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setForceVisible( showApplied.getText(), showApplied.isSelected() );
            }
        } );
        final JCheckBox showTotal = new JCheckBox( AbstractArrowSet.TOTAL, true );
        showTotal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setForceVisible( showTotal.getText(), showTotal.isSelected() );
            }
        } );
        final JCheckBox showWall = new JCheckBox( AbstractArrowSet.WALL, true );
        showWall.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setForceVisible( showWall.getText(), showWall.isSelected() );
            }
        } );
        final JCheckBox showGravity = new JCheckBox( AbstractArrowSet.WEIGHT, true );
        showGravity.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setForceVisible( showGravity.getText(), showGravity.isSelected() );
            }
        } );
        final JCheckBox showNormal = new JCheckBox( AbstractArrowSet.NORMAL, true );
        showNormal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setForceVisible( showNormal.getText(), showNormal.isSelected() );
            }
        } );
        forcePanel.add( showFriction );
        forcePanel.add( showApplied );
        forcePanel.add( showTotal );
        forcePanel.add( showWall );
        forcePanel.add( showGravity );
        forcePanel.add( showNormal );

        addControl( forcePanel );

        ObjectComboBox ocb = new ObjectComboBox( module.getRampObjects(), this );
        addControl( ocb );

        double[] ticks = new double[]{0, 0.5, 1.0, 1.5};
        frictionSlider = createFrictionSlider( ticks, module );

        addControl( frictionSlider );
        final JCheckBox frictionless = new JCheckBox( "Frictionless", false );
        frictionless.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setFrictionEnabled( !frictionless.isSelected() );
            }
        } );
        module.addListener( new RampModule.Listener() {
            public void objectChanged() {
                frictionless.setSelected( false );
            }
        } );
        addControl( frictionless );


        massSlider = createMassSlider();
        addControl( massSlider );

        addControl( createModelSelector() );
    }

    private Component createModelSelector() {
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        JRadioButton emergent = new JRadioButton( "Emergent" );
        JRadioButton constrained = new JRadioButton( "Constrained" );
        panel.add( emergent );
        panel.add( constrained );
        panel.setBorder( BorderFactory.createTitledBorder( "Model" ) );
        constrained.setSelected( true );
        emergent.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getRampModel().setStepStrategyEmergent();
            }
        } );
        constrained.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getRampModel().setStepStrategyConstrained();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( emergent );
        buttonGroup.add( constrained );
        return panel;
    }

    private ModelSlider createMassSlider() {
        final ModelSlider ms = new ModelSlider( "Mass", "kg", 100, 500, 100, new DecimalFormat( "000" ) );
        ms.setModelTicks( new double[]{100, 500} );
        ms.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = ms.getValue();
                module.setMass( value );
            }
        } );
        module.getRampModel().getBlock().addListener( new Block.Adapter() {
            public void massChanged() {
                ms.setValue( module.getBlock().getMass() );
            }
        } );
        return ms;
    }

    private void setFrictionEnabled( boolean enabled ) {
        if( enabled ) {
            setFriction( frictionSlider.getValue() );
        }
        else {
            setFriction( 0.0 );
        }
    }

    private ModelSlider createKineticSlider( double[] ticks, final RampModule module ) {
        final ModelSlider kineticFriction = new ModelSlider( "Kinetic Friction", "", 0, 1.5, 0.5 );
        kineticFriction.setModelTicks( ticks );
        kineticFriction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getRampModel().getBlock().setKineticFriction( kineticFriction.getValue() );
            }
        } );
        module.getRampModel().getBlock().addListener( new Block.Adapter() {
            public void kineticFrictionChanged() {
                kineticFriction.setValue( module.getRampModel().getBlock().getKineticFriction() );
            }
        } );
        return kineticFriction;
    }

    private ModelSlider createStaticSlider( double[] ticks, final RampModule module ) {
        final ModelSlider staticFriction = new ModelSlider( "Static Friction", "", 0, 1.5, 0.5 );
        staticFriction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getRampModel().getBlock().setStaticFriction( staticFriction.getValue() );
            }
        } );
        module.getRampModel().getBlock().addListener( new Block.Adapter() {
            public void staticFrictionChanged() {
                staticFriction.setValue( module.getRampModel().getBlock().getStaticFriction() );
            }
        } );
        staticFriction.setModelTicks( ticks );
        return staticFriction;
    }

    private void setFriction( double f ) {
        module.getRampModel().getBlock().setStaticFriction( f );
        module.getRampModel().getBlock().setKineticFriction( f );
    }

    private ModelSlider createFrictionSlider( double[] ticks, final RampModule module ) {
        final ModelSlider frictionSlider = new ModelSlider( "Friction", "", 0.1, 1.5, 0.5 );
        frictionSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setFriction( frictionSlider.getValue() );
            }
        } );
        module.getRampModel().getBlock().addListener( new Block.Adapter() {
            public void staticFrictionChanged() {
                frictionSlider.setValue( module.getRampModel().getBlock().getStaticFriction() );
//                frictionSlider.setValue( module.getRampModel().getBlock().getStaticFriction() );
            }

            public void kineticFrictionChanged() {
                frictionSlider.setValue( module.getRampModel().getBlock().getStaticFriction() );
            }
        } );
        frictionSlider.setModelTicks( ticks );
        return frictionSlider;
    }

    public void setup( RampObject rampObject ) {
        module.setObject( rampObject );
    }
}
