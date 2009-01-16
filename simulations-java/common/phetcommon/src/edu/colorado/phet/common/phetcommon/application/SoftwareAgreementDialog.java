package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.preferences.TrackingDetailsDialog;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 15, 2009
 * Time: 3:30:04 PM
 */
public class SoftwareAgreementDialog extends TrackingDetailsDialog{
    public SoftwareAgreementDialog( Frame frame, ITrackingInfo info ) {
        super(frame, info );
    }
    public SoftwareAgreementDialog( Dialog dialog, ITrackingInfo info ) {
        super(dialog, info );
    }

    protected JComponent createDescription() {
        return new JLabel( "Description of Privacy Agreement");
    }

    protected JComponent createReport( ITrackingInfo trackingInfo ) {
        return new JLabel( "Content of Privacy Agreement");
    }
}
