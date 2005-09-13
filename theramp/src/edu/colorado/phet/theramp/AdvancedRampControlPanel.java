/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.view.FreeBodyDiagram;
import edu.colorado.phet.theramp.view.InitialConditionPanel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.theramp.view.RampUtil;
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

public class AdvancedRampControlPanel extends RampControlPanel {
    private RampModule module;
//    private ModelSlider frictionSlider;
    private ModelSlider massSlider;

    /**
     * @param module
     */
    public AdvancedRampControlPanel( final RampModule module ) {
        super( module );
        this.module = module;

        final JCheckBox measureCheckBox = new JCheckBox( "Measuring Tape" );
        addControl( measureCheckBox );
        measureCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getRampPanel().setMeasureTapeVisible( measureCheckBox.isSelected() );
            }
        } );

//        addWorkEnergyBarGraphControls(  );

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

        AdvancedPanel advancedPanel = new AdvancedPanel();
        advancedPanel.addControlFullWidth( coordinatePanel );
        advancedPanel.addControlFullWidth( forcePanel );
        addControlFullWidth( advancedPanel );

        ObjectComboBox ocb = new ObjectComboBox( module.getRampObjects(), this );
        addControl( ocb );

        addControl( super.getFrictionSlider() );
        final JCheckBox frictionless = createFrictionlessCheckbox();
        addControl( frictionless );

        massSlider = createMassSlider();
        addControl( massSlider );

        GraphButtonSet graphButtonSet = new GraphButtonSet();
        addAdvancedControl( graphButtonSet, "Graphs" );

        JPanel controls = new InitialConditionPanel( module );
        addAdvancedControl( controls, "Controls" );

        {
            PhetPCanvas controlPanelFBD = new PhetPCanvas();
            controlPanelFBD.setPreferredSize( new Dimension( 200, 200 ) );
            FreeBodyDiagram freeBodyDiagram = new FreeBodyDiagram( rampPanel, module, controlPanelFBD );
            controlPanelFBD.addWorldChild( freeBodyDiagram );

            AdvancedPanel advancedFBDPanel = new AdvancedPanel( "Free Body Diagram>>", "Free Body Diagram<<" );
//        FBDPanel fbdContent = new FBDPanel( freeBodyDiagram );
            advancedFBDPanel.addControlFullWidth( controlPanelFBD );
            addControlFullWidth( advancedFBDPanel );
        }

//        {
//            final JDialog dialog = new JDialog( module.getPhetFrame(), false );
//            PhetPCanvas dialogPanelFBD = new PhetPCanvas();
//            dialogPanelFBD.setPreferredSize( new Dimension( 200, 200 ) );
//            FreeBodyDiagram freeBodyDiagram = new FreeBodyDiagram( rampPanel, module, dialogPanelFBD );
//            dialogPanelFBD.addChild( freeBodyDiagram );
//            dialog.setContentPane( dialogPanelFBD );
//            dialog.pack();
//            dialog.show();
//        }

//

//
//        PNode windowizeButton = new PPath( new Rectangle( 0, 0, 50, 50 ) );
//        windowizeButton.setPaint( Color.red );
//        windowizeButton.addInputEventListener( new PBasicInputEventHandler() {
//            public void mouseReleased( PInputEvent event ) {
//                super.mouseReleased( event );
//                dialog.setVisible( !dialog.isVisible() );
////                if (dialog.isVisible()){
////                    dialog.hide();
////                }else{
////
////                }
//            }
//        } );
//        freeBodyDiagram.addChild( windowizeButton );
//        dialog.show();
//        fbdPanel2.setDebugRegionManagement( true );
////        fbdPanel2.setDebugRegionManagement( true );
//        Timer timer=new Timer( 30,new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
////                fbdPanel2.paintImmediately( 0,0,fbdPanel2.getWidth(), fbdPanel2.getHeight() );
////                fbdPanel2.repaint();// 0,0,fbdPanel2.getWidth(), fbdPanel2.getHeight() );
//            }
//        } );
//        timer.start();
    }

//    static class FBDPanel extends PhetPCanvas {
//        public FBDPanel( FreeBodyDiagram freeBodyDiagram ) {
//            setPreferredSize( new Dimension( 200, 200 ) );
//            addChild( freeBodyDiagram );
//        }
//    }

    private void addAdvancedControl( JPanel panel, String name ) {
        AdvancedPanel advancedPanel = new AdvancedPanel( name + ">>", name + "<<" );
        advancedPanel.addControlFullWidth( panel );
        addControlFullWidth( advancedPanel );
    }


    class GraphButtonSet extends VerticalLayoutPanel {
        public GraphButtonSet() {
            setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Graphs" ) );
            for( int i = 0; i < module.getRampPlotSet().numDataUnits(); i++ ) {
                final RampPlotSet.DataUnit unit = module.getRampPlotSet().dataUnitAt( i );
                final JCheckBox checkBox = new JCheckBox( unit.getName(), true );

                Color reverse = RampUtil.inverseColor( unit.getColor() );
                checkBox.setBackground( unit.getColor() );
                checkBox.setForeground( Color.black );
                checkBox.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
                super.addFullWidth( checkBox );
                checkBox.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        unit.setVisible( checkBox.isSelected() );
                    }
                } );
            }
        }
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
                module.getRampPhysicalModel().setStepStrategyEmergent();
            }
        } );
        constrained.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getRampPhysicalModel().setStepStrategyConstrained();
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
        module.getRampPhysicalModel().getBlock().addListener( new Block.Adapter() {
            public void massChanged() {
                ms.setValue( module.getBlock().getMass() );
            }
        } );
        return ms;
    }


    private ModelSlider createKineticSlider( double[] ticks, final RampModule module ) {
        final ModelSlider kineticFriction = new ModelSlider( "Kinetic Friction", "", 0, 1.5, 0.5 );
        kineticFriction.setModelTicks( ticks );
        kineticFriction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getRampPhysicalModel().getBlock().setKineticFriction( kineticFriction.getValue() );
            }
        } );
        module.getRampPhysicalModel().getBlock().addListener( new Block.Adapter() {
            public void kineticFrictionChanged() {
                kineticFriction.setValue( module.getRampPhysicalModel().getBlock().getKineticFriction() );
            }
        } );
        return kineticFriction;
    }

    private ModelSlider createStaticSlider( double[] ticks, final RampModule module ) {
        final ModelSlider staticFriction = new ModelSlider( "Static Friction", "", 0, 1.5, 0.5 );
        staticFriction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getRampPhysicalModel().getBlock().setStaticFriction( staticFriction.getValue() );
            }
        } );
        module.getRampPhysicalModel().getBlock().addListener( new Block.Adapter() {
            public void staticFrictionChanged() {
                staticFriction.setValue( module.getRampPhysicalModel().getBlock().getStaticFriction() );
            }
        } );
        staticFriction.setModelTicks( ticks );
        return staticFriction;
    }

    public void setup( RampObject rampObject ) {
        module.setObject( rampObject );
    }
}
