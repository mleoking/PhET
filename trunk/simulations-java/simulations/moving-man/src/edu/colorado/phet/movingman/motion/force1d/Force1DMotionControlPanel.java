package edu.colorado.phet.movingman.motion.force1d;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * Created by: Sam
 * Dec 7, 2007 at 10:14:08 AM
 */
public class Force1DMotionControlPanel extends VerticalLayoutPanel {
    public Force1DMotionControlPanel( final Force1DMotionModel model ) {
        //add(new FBD());
        add( new ObjectSelectionPanel( model.getObjects(), new ObjectSelectionPanel.Listener() {
            public void objectChanged( Force1DObject force1DObject ) {
                model.setObject( force1DObject );
            }
        } ) );
    }
}
