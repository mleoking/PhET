/**
 * Class: SetFreqencyCmd
 * Class: edu.colorado.phet.emf.command
 * User: Ron LeMaster
 * Date: Jun 5, 2003
 * Time: 6:08:50 AM
 */
package edu.colorado.phet.emf.command;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.model.EmfModel;

public class SetFreqencyCmd implements Command {
    private float freq;

    public SetFreqencyCmd( float freq ) {
        this.freq = freq;
    }

    public void doIt() {
        EmfModel.instance().setTransmittingFrequency( freq );
    }
}
