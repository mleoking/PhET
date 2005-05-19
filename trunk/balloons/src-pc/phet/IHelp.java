package phet;

/**
 * User: Sam Reid
 * Date: May 19, 2005
 * Time: 9:27:52 AM
 * Copyright (c) May 19, 2005 by Sam Reid
 */
public interface IHelp {
    void setHelpEnabled( boolean miniHelpShowing );

    void showMegaHelp();

    boolean hasMegaHelp();
}
