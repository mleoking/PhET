/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.view.GoPauseClearPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 12:43:40 AM
 */

public class RampControlPanel extends ControlPanel {
    private RampModule rampModule;
    private ModelSlider frictionSlider;
    private RampAngleController rampAngleSlider;
    private PositionController positionSlider;
    private GoPauseClearPanel goPauseClear;
    private JCheckBox frictionlessCheckbox;

    public RampControlPanel( RampModule rampModule ) {
        super( rampModule );
        this.rampModule = rampModule;
        JButton jb = new JButton( TheRampStrings.getString( "controls.reset" ) );
        jb.setFont( new Font( PhetFont.getDefaultFontName(), Font.BOLD, 18 ) );

        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().reset();
            }

        } );
        addControl( jb );

        JButton clearHeat = new JButton( TheRampStrings.getString( "controls.cool-ramp" ) );
        addControl( clearHeat );
        clearHeat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().clearHeat();
            }
        } );
        double[] ticks = new double[]{0, 0.5, 1.0, 1.5};
        this.frictionSlider = createFrictionSlider( ticks, rampModule );

//        addWorkEnergyBarGraphControls( );

//        ObjectSelectorComponent objectSelectorComponent=new ObjectSelectorComponent(rampModule);

        this.rampAngleSlider = new RampAngleController( rampModule );
        this.positionSlider = new PositionController( rampModule );
        this.goPauseClear = new GoPauseClearPanel( rampModule.getTimeSeriesModel() );
//        addFullWidth( positionSlider.getComponent() );
//        addFullWidth( rampAngleSlider.getComponent() );
        this.frictionlessCheckbox = createFrictionlessCheckbox();
    }

    private RampModule getModule() {
        return rampModule;
    }


    private void setFrictionEnabled( boolean enabled ) {
        if( enabled ) {
            setFriction( frictionSlider.getValue() );
        }
        else {
            setFriction( 0.0 );
        }
    }

    protected ModelSlider createFrictionSlider( double[] ticks, final RampModule module ) {
//        if( frictionSlider != null ) {
//            return frictionSlider;
//        }
//        else {
        final ModelSlider frictionSlider = new ModelSlider( TheRampStrings.getString( "property.coefficient-of-friction" ), "", 0.1, 1.5, 0.5 );
        frictionSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setFriction( frictionSlider.getValue() );
            }
        } );
        module.getRampPhysicalModel().getBlock().addListener( new Block.Adapter() {
            public void staticFrictionChanged() {
                frictionSlider.setValue( module.getRampPhysicalModel().getBlock().getStaticFriction() );
//                frictionSlider.setValue( module.getRampModel().getBlock().getStaticFriction() );
            }

            public void kineticFrictionChanged() {
                frictionSlider.setValue( module.getRampPhysicalModel().getBlock().getStaticFriction() );
            }
        } );
        frictionSlider.setModelTicks( ticks );
        this.frictionSlider = frictionSlider;
        return frictionSlider;
//        }
    }

    protected void setFriction( double f ) {
        getModule().getRampPhysicalModel().getBlock().setStaticFriction( f );
        getModule().getRampPhysicalModel().getBlock().setKineticFriction( f );
    }

    private JCheckBox createFrictionlessCheckbox() {
        final JCheckBox frictionless = new JCheckBox( TheRampStrings.getString( "controls.frictionless" ), false );
        frictionless.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setFrictionEnabled( !frictionless.isSelected() );
                getModule().record();
            }
        } );
//        getModule().addListener( new RampModule.Listener() {
//            public void objectChanged() {
//                frictionless.setSelected( false );
//            }
//        } );

        getModule().addListener( new RampModule.Listener() {
            public void objectChanged() {
                setFrictionEnabled( !frictionless.isSelected() );
            }
        } );
        return frictionless;
    }

    public ModelSlider getFrictionSlider() {
        return frictionSlider;
    }


    protected void addWorkEnergyBarGraphControls() {
        final JCheckBox energyBars = new JCheckBox( TheRampStrings.getString( "energy.energy" ), true );
        energyBars.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().getRampPanel().setEnergyBarsVisible( energyBars.isSelected() );
            }
        } );
        final JCheckBox workBars = new JCheckBox( TheRampStrings.getString( "energy.work" ), true );
        workBars.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getModule().getRampPanel().setWorkBarsVisible( workBars.isSelected() );
            }
        } );

        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        verticalLayoutPanel.add( workBars );
        verticalLayoutPanel.add( energyBars );
        verticalLayoutPanel.setBorder( BorderFactory.createTitledBorder( TheRampStrings.getString( "display.bar-graph" ) ) );
        addControlFullWidth( verticalLayoutPanel );
    }

    public void addPositionAngleControls() {
        addControlFullWidth( positionSlider.getComponent() );
        addControlFullWidth( rampAngleSlider.getComponent() );
        addControl( goPauseClear );
    }

    protected void finishInit() {
        AudioEnabledController audioEnabledController = new AudioEnabledController( rampModule );
        addControl( audioEnabledController.getCheckBox() );
    }

    public JCheckBox getFrictionlessCheckBox() {
        return frictionlessCheckbox;
    }

    public void reset() {
        frictionlessCheckbox.setSelected( false );
    }
}
