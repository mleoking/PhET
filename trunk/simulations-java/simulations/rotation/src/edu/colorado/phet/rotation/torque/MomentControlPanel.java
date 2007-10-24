package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Created by: Sam
 * Oct 24, 2007 at 10:46:26 AM
 */
public class MomentControlPanel extends FullTorqueControlPanel {
    public MomentControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, final AbstractTorqueModule torqueModule, VectorViewModel vectorViewModel ) {
        super( rulerNode, rotationGraphSet, graphSetModel, torqueModule, vectorViewModel );
    }

    protected TorqueSlider[] getSliders( AbstractTorqueModule torqueModule, RotationPlatform rp ) {
        return new TorqueSlider[]{
                createOuterRadiusSlider( rp ),
                createInnerRadiusSlider( rp ),
                createMassSlider( rp ),
                createFrictionSlider( torqueModule )
        };
    }
}