/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:26:31 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class ElectronSetNode extends PNode {
    public void addElectronNode( ElectronNode electronNode ) {
        addChild( electronNode );
    }
}
