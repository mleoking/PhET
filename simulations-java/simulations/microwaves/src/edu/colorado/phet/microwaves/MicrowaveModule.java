/**
 * Class: MicrowaveModule
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Aug 21, 2003
 */
package edu.colorado.phet.microwaves;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common_microwaves.application.Module;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.common_microwaves.view.graphics.Graphic;
import edu.colorado.phet.common_microwaves.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.microwaves.coreadditions.BufferedApparatusPanel;
import edu.colorado.phet.microwaves.coreadditions.ModelViewTx1D;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;
import edu.colorado.phet.microwaves.model.Microwave;
import edu.colorado.phet.microwaves.model.MicrowaveModel;
import edu.colorado.phet.microwaves.model.WaterMolecule;
import edu.colorado.phet.microwaves.model.waves.WaveMedium;
import edu.colorado.phet.microwaves.view.*;

public abstract class MicrowaveModule extends Module {
    protected Microwave muWave;
    private MicrowaveModel microwaveModel;
    private Rectangle2D.Double modelBounds;
    private Rectangle viewBounds;
    private float savedFreq = (float) MicrowavesConfig.s_initFreq;
    private float savedAmp = (float) MicrowavesConfig.s_initAmp;
    private ModelViewTransform2D modelViewTransform;
    private int latticeSpace = 40;
    private Box2D oven;
    private FieldLatticeView fieldLattiveView;
    private boolean isMicrowaveOn = false;

    public MicrowaveModule( String name ) {
        super( name );
        microwaveModel = new MicrowaveModel();
        super.setModel( microwaveModel );
        createApparatusPanel();
        init();
    }

    protected void init() {

        // Put a microwave in the microwaveModel
        muWave = new Microwave( 0f, 0f );
        microwaveModel.addMicrowave( muWave );

        // Set up appatatus panel
        getApparatusPanel().setBackground( new Color( 250, 250, 220 ) );

        // Put a box in the model, for the walls of the microwave
        createOven();

        // Add the field view
        createFieldView();

        // Add a thermometer
        Thermometer thermometer = new Thermometer( microwaveModel );
        thermometer.setLocation( new Point2D.Double( 50, 50 ) );
        microwaveModel.addModelElement( thermometer );
        Graphic thermometerGraphic = new ThermometerView( thermometer );
        getApparatusPanel().addGraphic( thermometerGraphic, 100.0 );

        super.setControlPanel( new MicrowaveControlPanel( this, microwaveModel ) );

//        PressureMeasurementTool t = new PressureMeasurementTool( getApparatusPanel() );
//        t.setArmed( true );
//
    }

    public void reset() {
        microwaveModel.clear();
        getApparatusPanel().removeAllGraphics();
        isMicrowaveOn = false;
        init();
    }

    private void createApparatusPanel() {
        modelBounds = new Rectangle2D.Double( 0, 0, 600, 500 );
        viewBounds = new Rectangle( 0, 0, 600, 500 );
        modelViewTransform = new ModelViewTransform2D( modelBounds, viewBounds );
        setApparatusPanel( new BufferedApparatusPanel( new FlipperAffineTransformFactory( modelBounds ) ) );
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
            }
        } );

    }

    private void createOven() {
        oven = new Box2D( new Point2D.Double( 100, 100 ),
                          new Point2D.Double( 500, 400 ) );
        microwaveModel.addModelElement( oven );
        microwaveModel.setOven( oven );
        OvenGraphic ovenGraphic = new OvenGraphic( oven, modelViewTransform );
        ovenGraphic.update( null, null );
        double d = 1;
        getApparatusPanel().addGraphic( ovenGraphic, d );
    }

    private void createFieldView() {
        WaveMedium waveMedium = getMicrowaveModel().getWaveMedium();
        FieldVector.setLength( latticeSpace - 5 );
        // If we try to instantiate a new FieldLatticeView on every reset, we run out of memory. I haven't
        // debugged that, but this works.
        if ( fieldLattiveView == null ) {
            fieldLattiveView = new FieldLatticeView( waveMedium,
                                                     new Point2D.Double( oven.getMinX() + latticeSpace,
                                                                         oven.getMinY() + latticeSpace ),
                                                     oven.getMaxX() - oven.getMinX() - latticeSpace,
                                                     oven.getMaxY() - oven.getMinY() - latticeSpace,
                                                     latticeSpace, latticeSpace,
                                                     getApparatusPanel(),
                                                     getModelViewTransform() );
        }
        getApparatusPanel().addGraphic( fieldLattiveView, 5.0 );
    }

    public MicrowaveModel getMicrowaveModel() {
        return microwaveModel;
    }

    public Rectangle2D.Double getModelBounds() {
        return modelBounds;
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }

    public void setMicrowaveFrequency( double freq ) {
        if ( isMicrowaveOn ) {
            microwaveModel.setMicrowaveFrequency( freq );
        }
        else {
            savedFreq = (float) freq;
        }
    }

    public void setMicrowaveAmplitude( double amp ) {
        if ( isMicrowaveOn ) {
            microwaveModel.setMicrowaveAmplitude( amp );
        }
        else {
            savedAmp = (float) amp;
        }
    }

    public void toggleMicrowave() {
        isMicrowaveOn = !isMicrowaveOn;
        if ( isMicrowaveOn ) {
            turnMicrowaveOn( savedFreq, savedAmp );
        }
        else {
            turnMicrowaveOff();
        }
    }

    public void turnMicrowaveOn( double freq, double amp ) {
        microwaveModel.setMicrowaveFrequency( freq );
        microwaveModel.setMicrowaveAmplitude( amp );
    }

    public void turnMicrowaveOff() {
        savedFreq = (float) microwaveModel.getFrequency();
        savedAmp = (float) microwaveModel.getAmplitude();
        microwaveModel.setMicrowaveFrequency( 0f );
        microwaveModel.setMicrowaveAmplitude( 0f );
        muWave.setFrequency( 0f );
        muWave.setMaxAmplitude( 0f );
    }

    public ModelViewTransform2D getModelViewTransform() {
        return modelViewTransform;
    }

    public void activate( PhetApplication phetApplication ) {
    }

    public void setFieldViewOff() {
        fieldLattiveView.setViewOff();
    }

    public void setFieldViewFull() {
        fieldLattiveView.setViewFull();
    }

    public void setFieldViewSingle() {
        fieldLattiveView.setViewSingle();
    }

    public void setFiledViewSpline() {
        fieldLattiveView.setViewSpline();
    }

    //
    // Inner classes
    //
    public static class ControlMenu extends JMenu {
        public ControlMenu() {
            super( MicrowavesResources.getString( "MicrowaveModule.ControlMenu" ) );
            JMenuItem physicalParamsMI = new JMenuItem( MicrowavesResources.getString( "MicrowaveModule.PhysicalParametersMenuItem" ) );
            this.add( physicalParamsMI );
            physicalParamsMI.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    JDialog physicalParamsDlg = new PhysicalParamsDlg();
                    physicalParamsDlg.setVisible( true );
                }
            } );
        }
    }

    static class PhysicalParamsDlg extends JDialog {

        private JTextField cTF;
        private JTextField dTF;

        PhysicalParamsDlg() {
            super( PhetApplication.instance().getApplicationView().getPhetFrame() );
            SwingUtils.centerDialogInParent( this );

            // Parameter controls for testing
            final JSlider cSlider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                                 100, 50 );
            final ModelViewTx1D cSliderTx = new ModelViewTx1D( 0, WaterMolecule.s_c,
                                                               0, 100 );
            cSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    WaterMolecule.s_c = cSliderTx.viewToModel( cSlider.getValue() );
                    cTF.setText( Double.toString( WaterMolecule.s_c ) );
                }
            } );

            final JSlider dampingSlider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                                       100,
                                                       50 );
            final ModelViewTx1D dSliderTx = new ModelViewTx1D( 0, WaterMolecule.s_b,
                                                               0, 100 );
            dampingSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    WaterMolecule.s_b = dSliderTx.viewToModel( dampingSlider.getValue() );
                    dTF.setText( Double.toString( WaterMolecule.s_b ) );
                }
            } );


            getContentPane().setLayout( new GridBagLayout() );
            int rowIdx = 0;
            try {
                SwingUtils.addGridBagComponent( getContentPane(), new JLabel( MicrowavesResources.getString( "MicrowaveModule.PolarSesitivityLabel" ) ),
                                                0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE,
                                                GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( getContentPane(), cSlider,
                                                0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE,
                                                GridBagConstraints.CENTER );
                cTF = new JTextField( Double.toString( cSliderTx.viewToModel( cSlider.getValue() ) ), 10 );
                SwingUtils.addGridBagComponent( getContentPane(), cTF,
                                                0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE,
                                                GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( getContentPane(), new JLabel( MicrowavesResources.getString( "MicrowaveModule.DampingLabel" ) ),
                                                0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE,
                                                GridBagConstraints.CENTER );
                SwingUtils.addGridBagComponent( getContentPane(), dampingSlider,
                                                0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE,
                                                GridBagConstraints.CENTER );
                dTF = new JTextField( Double.toString( dSliderTx.viewToModel( dampingSlider.getValue() ) ), 10 );
                SwingUtils.addGridBagComponent( getContentPane(), dTF,
                                                0, rowIdx++, 1, 1,
                                                GridBagConstraints.NONE,
                                                GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
            this.pack();
        }


    }
}
