/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements;

import edu.colorado.phet.cck.elements.branch.Branch;

/**
 * User: Sam Reid
 * Date: Oct 28, 2003
 * Time: 10:17:41 PM
 * Copyright (c) Oct 28, 2003 by Sam Reid
 */
public class DeleteAction {
//    String componentName;
    String text;
    Branch branch;

    public DeleteAction(Branch branch) {
//        this.componentName = componentName;
        this.branch = branch;
        this.text = "Delete";
    }

    public String getName() {
        return text;
    }

    public void invoke() {
        branch.getCircuit().removeBranch(branch);
    }
}
