package edu.colorado.phet.common.view.basicgraphics.repaint;

import edu.colorado.phet.common.view.basicgraphics.RepaintDelegate;

/**
 * User: Sam Reid
 * Date: Sep 11, 2004
 * Time: 9:18:16 PM
 * Copyright (c) Sep 11, 2004 by Sam Reid
 */
public interface SynchronizedRepaintDelegate extends RepaintDelegate {
    public void finishedUpdateCycle();
}
