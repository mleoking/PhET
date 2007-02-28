/*
 * Class: IdealGasConfig
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Oct 28, 2002
 */
package edu.colorado.phet.controller;

/**
 * This class is intended to contain static constants used by specific applications,
 * and to help form the specifications for guided inquiries.
 * <p>
 * It also contains abstract methods called by other classes in the application
 * framework to get critical configuration information.
 *
 *
 */
public abstract class Config {

    /**
     * REturns the title of the application
     * @return The title of the application
     */
    public abstract String getTitle();

    /**
     * Returns the default time step to be used by the physical system's clock.
     * @return
     */
    public abstract float  getTimeStep();

    /**
     * Returns the default wait time used by the physical system's clock between
     * time steps.
     * @return
     */
    public abstract int getWaitTime();

    // Images
    public static final String IMAGE_DIRECTORY = "images/";

    public static final String HELP_ITEM_ICON_IMAGE_FILE = IMAGE_DIRECTORY + "help-item-icon.gif";
}
