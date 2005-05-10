/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.theramp.common.JButton3D;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.theramp.view.arrows.AbstractArrowSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 1:31:12 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class RampControlPanel extends ControlPanel {
    private RampModule rampModule;

    /**
     * @param module
     */
    public RampControlPanel( final RampModule module ) {
        super( module );
        this.rampModule = module;
        JButton3D jb = new JButton3D( "Reset" );
        add( jb );
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
        add( coordinatePanel );

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


        add( forcePanel );

        ObjectComboBox ocb = new ObjectComboBox( module.getRampObjects(), this );
        add( ocb );

        double[] ticks = new double[]{0, 0.5, 1.0, 1.5};

        final ModelSlider staticFriction = createStaticSlider( ticks, module );
        add( staticFriction );

        final ModelSlider kineticFriction = createKineticSlider( ticks, module );
        add( kineticFriction );

        JButton record = new JButton( "Record" );

        record.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.record();
            }
        } );

        JButton playback = new JButton( "Playback" );
        playback.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.playback();
            }
        } );
        add( record );
        add( playback );
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

    public void setup( RampObject rampObject ) {
        rampModule.setObject( rampObject );
    }
}
