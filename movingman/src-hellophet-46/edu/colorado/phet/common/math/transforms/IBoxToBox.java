package edu.colorado.phet.common.math.transforms;

import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 26, 2002
 * Time: 2:13:55 AM
 * To change this template use Options | File Templates.
 */
public interface IBoxToBox extends Transform {
    public Rectangle2D getInputBounds();

    public Rectangle2D getOutputBounds();

    public void setInputBounds( Rectangle2D in );

    public void setOutputBounds( Rectangle2D out );
}
