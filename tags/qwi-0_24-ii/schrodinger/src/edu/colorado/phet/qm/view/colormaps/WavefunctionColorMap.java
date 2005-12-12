package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.Wavefunction;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 2:55:30 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */
public interface WavefunctionColorMap {
    Paint getColor( Wavefunction wavefunction, int i, int k );
}
