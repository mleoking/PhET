package edu.colorado.phet.buildamolecule.model;

import java.awt.*;
import java.util.List;

import edu.colorado.phet.chemistry.model.Atom;

/**
 * Holds atoms in a kit
 */
public class Bucket {
    private List<Atom> atoms;
    private Color color;
    private String name;

    public Bucket( String name, Color color, List<Atom> atoms ) {
        this.atoms = atoms;
        this.color = color;
        this.name = name;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public int getQuantity() {
        return atoms.size();
    }

    public String getName() {
        return name;
    }

    public Paint getColor() {
        return color;
    }
}
