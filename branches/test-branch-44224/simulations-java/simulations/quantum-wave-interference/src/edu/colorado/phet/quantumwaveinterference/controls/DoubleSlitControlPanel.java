/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.FractionalDoubleSlit;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 9:37:07 AM
 */

public class DoubleSlitControlPanel extends VerticalLayoutPanel {
    private QWIModel QWIModel;
    private HorizontalDoubleSlit horizontalDoubleSlit;
    private JComponent slitSize;
    private JComponent slitSeparation;
    private JComponent verticalPosition;
    private SlitDetectorPanel slitDetectorPanel;
    private QWIModule module;
    private FractionalDoubleSlit fractionalSlit;

    public DoubleSlitControlPanel( final QWIModel QWIModel, QWIModule qwiModule ) {
        this.QWIModel = QWIModel;
        this.module = qwiModule;
        this.horizontalDoubleSlit = QWIModel.getDoubleSlitPotential();
        this.fractionalSlit = QWIModel.getFractionalDoubleSlit();
        verticalPosition = createComponent( QWIResources.getString( "controls.slits.vertical-position" ), new Setter() {
            double insetY = 10 / 60.0;

            public void valueChanged( double val ) {
                fractionalSlit.setY( val );
            }

            public double getValue() {
                return fractionalSlit.getY();
            }

            public double getMin() {
                return 0 + insetY;
            }

            public double getMax() {
                return ResolutionControl.DEFAULT_WAVE_SIZE / 60.0 - insetY;
            }

            public int getViewValue( Function modelToView ) {
                return (int)modelToView.evaluate( getValue() );
            }
        } );


        slitSize = createComponent( QWIResources.getString( "controls.slits.width" ), new Setter() {
            public void valueChanged( double val ) {
                fractionalSlit.setSlitSize( val );
            }

            public double getValue() {
                return fractionalSlit.getSlitSize();
            }

            public double getMin() {
                return 4 / 45.0;
            }

            public double getMax() {
                return 25 / 60.0;
            }

            public int getViewValue( Function modelToView ) {
                return (int)modelToView.evaluate( getValue() );
            }
        } );

        slitSeparation = createComponent( QWIResources.getString( "controls.slits.separation" ), new Setter() {
            public void valueChanged( double val ) {
                fractionalSlit.setSlitSeparation( val );
            }

            public double getValue() {
                return fractionalSlit.getSlitSeparation();
            }

            public double getMin() {
                return 6 / 45.0;
            }

            public double getMax() {
                return 30 / 60.0;
            }

            public int getViewValue( Function modelToView ) {
                return (int)modelToView.evaluate( getValue() );
            }
        } );

        final JCheckBox absorbtiveSlit = new JCheckBox( QWIResources.getString( "controls.slits.absorbing-barriers" ), getDiscreteModel().isBarrierAbsorptive() );
        absorbtiveSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setBarrierAbsorptive( absorbtiveSlit.isSelected() );
            }
        } );
        add( absorbtiveSlit );

        add( slitSize );
        add( slitSeparation );
        add( verticalPosition );

        if( qwiModule instanceof IntensityModule ) {//todo use polymorphism here
            slitDetectorPanel = new SlitDetectorPanel( (IntensityModule)qwiModule );
            addFullWidth( slitDetectorPanel );
        }
        setControlsEnabled( true );
        addFullWidth( new InverseSlitsCheckbox( qwiModule.getSchrodingerPanel() ) );
    }

    public SlitDetectorPanel getSlitDetectorPanel() {
        return slitDetectorPanel;
    }

    private void setControlsEnabled( boolean selected ) {
        slitSize.setEnabled( selected );
        slitSeparation.setEnabled( selected );
        verticalPosition.setEnabled( selected );
    }

    private JComponent createComponent( String title, final Setter setter ) {
        final Function.LinearFunction modelToView = new Function.LinearFunction( setter.getMin(), setter.getMax(), 0, 100 );
        final Function.LinearFunction viewToModel = new Function.LinearFunction( 0, 100, setter.getMin(), setter.getMax() );
        int value = setter.getViewValue( modelToView );

//        System.out.println( "title: " + title + "+value = " + value );
        final JSlider comp = new JSlider( 0, 100, value );
        comp.setBorder( BorderFactory.createTitledBorder( title ) );
        comp.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setter.valueChanged( viewToModel.evaluate( comp.getValue() ) );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
//                comp.setValue( (int)modelToView.evaluate( setter.getValue() ) );
                comp.setValue( setter.getViewValue( modelToView ) );
            }
        } );
        return comp;
    }

    private interface Setter {
        void valueChanged( double val );

        double getValue();

        double getMin();

        double getMax();

        int getViewValue( Function modelToView );
    }

    private QWIModel getDiscreteModel() {
        return QWIModel;
    }
}
