/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.circuit;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.junction.Junction;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 10:56:37 PM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class BranchPath {
    ArrayList elements = new ArrayList();

    private BranchPath() {

    }

    public BranchPath(Junction a, Branch startbranch) {
        BranchPathElement elm = new BranchPathElement(a, startbranch);
        elements.add(elm);
    }

    public String toString() {
        String s = new String();
        for (int i = 0; i < elements.size(); i++) {
            BranchPathElement branchPathElement = (BranchPathElement) elements.get(i);
            s += branchPathElement.toString() + "\n";
        }
        return s + " -> " + getEndJunction();
    }

    public BranchPathElement lastElement() {
        return (BranchPathElement) elements.get(elements.size() - 1);
    }

    public Junction getEndJunction() {
        return lastElement().getEndJunction();
    }

    public Junction getStartJunction() {
        return firstElement().getStartJunction();
    }

    private BranchPathElement firstElement() {
        return (BranchPathElement) elements.get(0);
    }

    public boolean containsBranch(Branch branch2) {
        for (int i = 0; i < elements.size(); i++) {
            BranchPathElement branchPathElement = (BranchPathElement) elements.get(i);
            if (branchPathElement.getBranch() == branch2)
                return true;
        }
        return false;
    }

    public void addBranch(Branch branch) {
        BranchPathElement element = new BranchPathElement(getEndJunction(), branch);
        elements.add(element);
    }

    public BranchPath getAppendedPath(Branch branch2) {
        BranchPath copy = copy();
        copy.addBranch(branch2);
        return copy;
    }

    private BranchPath copy() {
//        ArrayList list=new ArrayList();
//        list.addAll(this.elements);
        BranchPath bp = new BranchPath();
        bp.elements.addAll(elements);
//        bp.elements=list;
        return bp;//new List, same elements.
    }

    public double getVoltageDrop() {
        System.out.println("<Getting voltage Drop>");
        System.out.println("BranchPath.this = " + this);
        double drop = 0;
        for (int i = 0; i < elements.size(); i++) {
            BranchPathElement branchPathElement = (BranchPathElement) elements.get(i);
            double term = branchPathElement.getVoltageDrop();
            System.out.println("term [" + i + "], for id=" + branchPathElement.getBranch().getId() + " = " + term);
            drop += term;
        }
        double answer = drop;
        System.out.println("answer = " + answer);
        System.out.println("</Getting voltage Drop>");
        return answer;
    }

}
