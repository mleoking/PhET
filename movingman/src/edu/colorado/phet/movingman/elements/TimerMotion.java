package edu.colorado.phet.movingman.elements;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 3, 2003
 * Time: 11:10:56 PM
 * To change this template use Options | File Templates.
 */
public interface TimerMotion {
    double getPosition(double time);

    void setInitialPosition(double x);
}
