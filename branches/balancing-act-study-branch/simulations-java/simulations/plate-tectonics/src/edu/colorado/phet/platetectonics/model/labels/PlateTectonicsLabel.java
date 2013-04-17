// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.model.labels;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;

public class PlateTectonicsLabel {
    // fired when this label is permanently removed from the model, so we can detach the necessary listeners
    public final ValueNotifier<PlateTectonicsLabel> disposed = new ValueNotifier<PlateTectonicsLabel>( this );
}
