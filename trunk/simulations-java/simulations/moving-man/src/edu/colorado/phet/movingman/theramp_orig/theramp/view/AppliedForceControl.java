/*  */
package edu.colorado.phet.movingman.theramp_orig.theramp.view;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.movingman.theramp_orig.theramp.RampModule;
import edu.colorado.phet.movingman.motion.TheRampStrings;
import edu.colorado.phet.movingman.theramp_orig.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 6:38:01 PM
 */

public class AppliedForceControl extends PNode {
    private RampModule module;
    private RampPanel rampPanel;

    public AppliedForceControl( final RampModule module, RampPanel rampPanel ) {
        this.module = module;
        this.rampPanel = rampPanel;
        double maxValue = 3000;
        final ModelSlider modelSlider = new ModelSlider( TheRampStrings.getString( "forces.applied-force" ), TheRampStrings.getString( "units.newtons" ), -maxValue, maxValue, 0, new DecimalFormat( "0.00" ) );
        modelSlider.setModelTicks( new double[]{-maxValue, 0, maxValue} );
        PSwing pSwing = new PSwing( modelSlider );
        addChild( pSwing );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setAppliedForce( modelSlider.getValue() );
            }
        } );
        module.getRampPhysicalModel().addListener( new RampPhysicalModel.Adapter() {
            public void appliedForceChanged() {
                modelSlider.setValue( module.getRampPhysicalModel().getAppliedForceScalar() );
            }

        } );
        modelSlider.getSlider().addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
            }

            public void mouseReleased( MouseEvent e ) {
//                module.setAppliedForce( 0.0);
            }
        } );

//        scale( 0.65 );//todo fix the size?  See Wendy.
    }
}
