/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampObject;
import edu.colorado.phet.theramp.view.FreeBodyDiagram;
import edu.colorado.phet.theramp.view.InitialConditionPanel;
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
 */

public class AdvancedRampControlPanel extends RampControlPanel {
    private RampModule module;
    private ModelSlider massSlider;

    /**
     * @param module
     */
    public AdvancedRampControlPanel( final RampModule module ) {
        super( module );
        this.module = module;

        final JCheckBox measureCheckBox = new JCheckBox( TheRampStrings.getString( "controls.measuring-tape" ) );
        addControl( measureCheckBox );
        measureCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getRampPanel().setMeasureTapeVisible( measureCheckBox.isSelected() );
            }
        } );

        ShowZeroPointPotentialControl showZeroPointPotentialCheckBox = new ShowZeroPointPotentialControl( module );
        addControl( showZeroPointPotentialCheckBox.getComponent() );

//        addWorkEnergyBarGraphControls(  );

        JPanel coordinatePanel = new VerticalLayoutPanel();
        final RampPanel rampPanel = module.getRampPanel();
        final JCheckBox descartes = new JCheckBox( TheRampStrings.getString( "coordinates.entire-vectors" ), rampPanel.isCartesianVisible() );
        descartes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setCartesianArrowsVisible( descartes.isSelected() );
            }
        } );
        final JCheckBox parallel = new JCheckBox( TheRampStrings.getString( "coordinates.parallel-components" ), rampPanel.isParallelVisible() );
        parallel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setParallelArrowsVisible( parallel.isSelected() );
            }
        } );
        final JCheckBox perpendicular = new JCheckBox( TheRampStrings.getString( "coordinates.perpendicular-components" ), rampPanel.isPerpendicularVisible() );
        perpendicular.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setPerpendicularArrowsVisible( perpendicular.isSelected() );
            }
        } );

        final JCheckBox x = new JCheckBox( TheRampStrings.getString( "coordinates.x-components" ), rampPanel.isXVisible() );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setXArrowsVisible( x.isSelected() );
            }
        } );

        final JCheckBox y = new JCheckBox( TheRampStrings.getString( "coordinates.y-components" ), rampPanel.isYVisible() );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.setYArrowsVisible( y.isSelected() );
            }
        } );

        coordinatePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), TheRampStrings.getString( "coordinates.frames" ) ) );
        coordinatePanel.add( descartes );
        coordinatePanel.add( parallel );
        coordinatePanel.add( perpendicular );
        coordinatePanel.add( x );
        coordinatePanel.add( y );

        JPanel forcePanel = new VerticalLayoutPanel();
        forcePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), TheRampStrings.getString( "controls.forces-to-show" ) ) );

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
        JLabel label = new JLabel( TheRampStrings.getString( "controls.choose-object" ) );
        addControlFullWidth( label );
        addControl( ocb );

        addControl( super.getFrictionSlider() );
//        frictionlessCheckbox = createFrictionlessCheckbox();
        addControl( getFrictionlessCheckBox() );

        massSlider = createMassSlider();
        addControl( massSlider );

        super.addPositionAngleControls();

        GraphButtonSet graphButtonSet = new GraphButtonSet();
        addAdvancedControl( graphButtonSet, TheRampStrings.getString( "display.graphs" ) );

        JPanel controls = new InitialConditionPanel( module );
        addAdvancedControl( controls, TheRampStrings.getString( "controls.controls" ) );


        {
            PhetPCanvas controlPanelFBD = new PhetPCanvas();
            controlPanelFBD.setPreferredSize( new Dimension( 200, 200 ) );
            FreeBodyDiagram freeBodyDiagram = new FreeBodyDiagram( rampPanel, module, controlPanelFBD );
            controlPanelFBD.addWorldChild( freeBodyDiagram );

            AdvancedPanel advancedFBDPanel = new AdvancedPanel( TheRampStrings.getString( "controls.show-free-body-diagram" ), TheRampStrings.getString( "controls.hide-free-body-diagram" ) );
            advancedFBDPanel.addControlFullWidth( controlPanelFBD );
            addControlFullWidth( advancedFBDPanel );
        }
        finishInit();
    }


    private void addAdvancedControl( JPanel panel, String name ) {
        AdvancedPanel advancedPanel = new AdvancedPanel( name + ">>", name + "<<" );
        advancedPanel.addControlFullWidth( panel );
        addControlFullWidth( advancedPanel );
    }


    class GraphButtonSet extends VerticalLayoutPanel {
        public GraphButtonSet() {
            setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), TheRampStrings.getString( "display.graphs" ) ) );
            for( int i = 0; i < module.getRampPlotSet().numDataUnits(); i++ ) {
                final RampPlotSet.DataUnit unit = module.getRampPlotSet().dataUnitAt( i );
                final JCheckBox checkBox = new JCheckBox( unit.getFullName(), true );

                checkBox.setBackground( unit.getColor() );
                checkBox.setForeground( Color.black );
                checkBox.setFont( new Font( PhetFont.getDefaultFontName(), Font.BOLD, 14 ) );
                super.addFullWidth( checkBox );
                checkBox.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        unit.setVisible( checkBox.isSelected() );
                    }
                } );
            }
        }
    }

    private ModelSlider createMassSlider() {
        final ModelSlider ms = new ModelSlider( TheRampStrings.getString( "property.mass" ), TheRampStrings.getString( "units.abbr.kg" ), 100, 500, 100, new DecimalFormat( "000" ) );
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
        final ModelSlider kineticFriction = new ModelSlider( TheRampStrings.getString( "forces.kinetic-friction" ), "", 0, 1.5, 0.5 );
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
        final ModelSlider staticFriction = new ModelSlider( TheRampStrings.getString( "forces.static-friction" ), "", 0, 1.5, 0.5 );
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
