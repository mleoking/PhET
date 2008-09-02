package edu.colorado.phet.greenhouse.common_greenhouse.model;


/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Feb 24, 2003
 * Time: 11:08:37 PM
 * To change this template use Options | File Templates.
 */
public abstract class ModelElement extends AutomatedObservable {
    public abstract void stepInTime(double dt);
}
