/**
 * Class: SetFreqencyCmd Class: edu.colorado.phet.emf.command User: Ron LeMaster
 * Date: Jun 5, 2003 Time: 6:08:50 AM
 */

package edu.colorado.phet.radiowaves.command;

import edu.colorado.phet.common_1200.model.Command;
import edu.colorado.phet.radiowaves.model.EmfModel;

public class SetFreqencyCmd implements Command {

    private EmfModel model;
    private float freq;

    public SetFreqencyCmd( EmfModel model, float freq ) {
        this.model = model;
        this.freq = freq;
    }

    public void doIt() {
        // TODO: rjl 6/26/03
        model.setTransmittingFrequency( freq );
        //        EmfModel.instance().setTransmittingFrequency( freq );
    }
}
