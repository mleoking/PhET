package edu.colorado.phet.densityjava.model;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 18, 2009
 * Time: 8:24:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockEnvironment {
    double getFloorY(Block block);

    double getAppliedForce(Block block);
}
