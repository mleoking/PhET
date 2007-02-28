/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 4, 2003
 * Time: 2:14:35 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.controller.command;

/**
 * The base class for all commands that are used to control a PhET
 * application
 */
public interface Command {

    public Object doIt();
}
