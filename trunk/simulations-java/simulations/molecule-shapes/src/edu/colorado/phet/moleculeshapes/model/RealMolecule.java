// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.Arrays;
import java.util.List;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.jmolphet.Molecule;

/**
 * Represents a "real" molecule with exact positions, as opposed to a molecule model (which is VSEPR-based
 * and doesn't include other information).
 * <p/>
 * We display these real molecules to the user using Jmol
 */
public class RealMolecule implements Molecule {
    private String name;
    private String data;

    public static List<RealMolecule> getMatchingMolecules( MoleculeModel model ) {
        // TODO: implement
        return Arrays.asList( WATER, ETHANE );
    }

    private RealMolecule( String name, String data ) {
        this.name = name;
        this.data = data;
    }

    public String getDisplayName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public void fixJmolColors( JmolViewer viewer ) {
    }

    public static final RealMolecule WATER = new RealMolecule( "Water", "<?xml version=\"1.0\"?>\n" +
                                                                        "<molecule id=\"id962\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
                                                                        " <name>962</name>\n" +
                                                                        " <atomArray>\n" +
                                                                        "  <atom id=\"a1\" elementType=\"O\" x3=\"0.000000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
                                                                        "  <atom id=\"a2\" elementType=\"H\" x3=\"0.277400\" y3=\"0.892900\" z3=\"0.254400\"/>\n" +
                                                                        "  <atom id=\"a3\" elementType=\"H\" x3=\"0.606800\" y3=\"-0.238300\" z3=\"-0.716900\"/>\n" +
                                                                        " </atomArray>\n" +
                                                                        " <bondArray>\n" +
                                                                        "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
                                                                        "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
                                                                        " </bondArray>\n" +
                                                                        "</molecule>" );

    public static final RealMolecule ETHANE = new RealMolecule( "Ethane", "<?xml version=\"1.0\"?>\n" +
                                                                          "<molecule id=\"id6324\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
                                                                          " <name>6324</name>\n" +
                                                                          " <atomArray>\n" +
                                                                          "  <atom id=\"a1\" elementType=\"C\" x3=\"-0.756000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
                                                                          "  <atom id=\"a2\" elementType=\"C\" x3=\"0.756000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
                                                                          "  <atom id=\"a3\" elementType=\"H\" x3=\"-1.140400\" y3=\"0.658600\" z3=\"0.784500\"/>\n" +
                                                                          "  <atom id=\"a4\" elementType=\"H\" x3=\"-1.140400\" y3=\"0.350100\" z3=\"-0.962600\"/>\n" +
                                                                          "  <atom id=\"a5\" elementType=\"H\" x3=\"-1.140500\" y3=\"-1.008700\" z3=\"0.178100\"/>\n" +
                                                                          "  <atom id=\"a6\" elementType=\"H\" x3=\"1.140400\" y3=\"-0.350100\" z3=\"0.962600\"/>\n" +
                                                                          "  <atom id=\"a7\" elementType=\"H\" x3=\"1.140500\" y3=\"1.008700\" z3=\"-0.178100\"/>\n" +
                                                                          "  <atom id=\"a8\" elementType=\"H\" x3=\"1.140400\" y3=\"-0.658600\" z3=\"-0.784500\"/>\n" +
                                                                          " </atomArray>\n" +
                                                                          " <bondArray>\n" +
                                                                          "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
                                                                          "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
                                                                          "  <bond atomRefs2=\"a1 a4\" order=\"1\"/>\n" +
                                                                          "  <bond atomRefs2=\"a1 a5\" order=\"1\"/>\n" +
                                                                          "  <bond atomRefs2=\"a2 a6\" order=\"1\"/>\n" +
                                                                          "  <bond atomRefs2=\"a2 a7\" order=\"1\"/>\n" +
                                                                          "  <bond atomRefs2=\"a2 a8\" order=\"1\"/>\n" +
                                                                          " </bondArray>\n" +
                                                                          "</molecule>" );
}
