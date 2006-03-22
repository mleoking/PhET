package edu.colorado.phet.qm.view.complexcolormaps;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Dec 4, 2005
 * Time: 11:07:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ComplexColorMap {
    public Paint getColor( Complex value );
}
