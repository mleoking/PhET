// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis

/**
 * @author Sam Reid
 */

object simEvents {
  val moleculeShapes =
    Rule("draggingState", "changed", "dragging" -> "true", "dragMode" -> "PAIR_EXISTING_SPHERICAL") ::
    Rule("draggingState", "changed", "dragging" -> "true", "dragMode" -> "MODEL_ROTATE") ::
    Rule("checkBox", "pressed", "text" -> "Molecule Geometry") ::
    Rule("checkBox", "pressed", "text" -> "Show Bond Angles") ::
    Rule("checkBox", "pressed", "text" -> "Show Lone Pairs") ::
    Rule("buttonNode", "pressed", "actionCommand" -> "Remove All") ::
    Rule("bond", "created", "bondOrder" -> "0") ::
    Rule("bond", "created", "bondOrder" -> "1") ::
    Rule("bond", "created", "bondOrder" -> "2") ::
    Rule("bond", "created", "bondOrder" -> "3") ::
    Rule("bond", "removed", "bondOrder" -> "0") ::
    Rule("bond", "removed", "bondOrder" -> "1") ::
    Rule("bond", "removed", "bondOrder" -> "2") ::
    Rule("bond", "removed", "bondOrder" -> "3") ::
    Nil

  val moleculePolarity =
    Rule("tab", "pressed", "text" -> "Two Atoms") ::
    Rule("tab", "pressed", "text" -> "Three Atoms") ::
    Rule("tab", "pressed", "text" -> "Real Molecules") ::
    Rule("checkBox", "pressed", "text" -> "Bond Dipole") ::
    Rule("checkBox", "pressed", "text" -> "Partial Charges") ::
    Rule("checkBox", "pressed", "text" -> "Bond Character") ::
    Rule("radioButton", "pressed", "description" -> "Surface type", "text" -> "none") ::
    Rule("radioButton", "pressed", "description" -> "Surface type", "text" -> "Electrostatic Potential") ::
    Rule("radioButton", "pressed", "description" -> "Surface type", "text" -> "Electron Density") ::
    Rule("radioButton", "pressed", "description" -> "Electric field on", "text" -> "on") ::
    Rule("radioButton", "pressed", "description" -> "Electric field on", "text" -> "off") ::
    Rule("buttonNode", "pressed", "actionCommand" -> "Reset All") ::
    Rule("bond", "removed", "bondOrder" -> "3") ::
    Rule("mouse", "startDrag", "atom" -> "A") ::
    Rule("mouse", "startDrag", "atom" -> "B") ::
    Rule("mouse", "startDrag", "atom" -> "C") ::
    Rule("molecule rotation drag", "started") ::
    Rule("bondAngleDrag", "started") ::
    Rule("comboBoxItem", "selected") ::
    Rule("mouse", "startDrag", "component" -> "jmolViewerNode") ::
    Rule("checkBox", "pressed", "text" -> "Bond Dipoles") ::
    Rule("checkBox", "pressed", "text" -> "Molecular Dipole") ::
    Rule("checkBox", "pressed", "text" -> "Atom Electronegativities") ::
    Rule("checkBox", "pressed", "text" -> "Atom Labels") ::
    Nil

  val balancingChemicalEquations =
    Rule("tab", "pressed", "text" -> "Introduction") ::
    Rule("tab", "pressed", "text" -> "Balancing Game") ::
    Rule("radioButton", "pressed", "text" -> "None") ::
    Rule("radioButton", "pressed", "text" -> "Balance Scales") ::
    Rule("radioButton", "pressed", "text" -> "Bar Charts") ::
    Rule("radioButton", "pressed", "text" -> "<html>Make Ammonia</html>") ::
    Rule("radioButton", "pressed", "text" -> "<html>Separate Water</html>") ::
    Rule("radioButton", "pressed", "text" -> "<html>Combust Methane</html>") ::
    Rule("buttonNode", "pressed", "actionCommand" -> "Reset All") ::
    Rule("spinner", "changed", "description" -> "coefficient for N<sub>2</sub>") ::
    Rule("spinner", "changed", "description" -> "coefficient for H<sub>2</sub>") ::
    Rule("spinner", "changed", "description" -> "coefficient for NH<sub>3</sub>") ::
    Rule("spinner", "changed", "description" -> "coefficient for H<sub>2</sub>O") ::
    Rule("spinner", "changed", "description" -> "coefficient for O<sub>2</sub>") ::
    Rule("spinner", "changed", "description" -> "coefficient for CH<sub>4</sub>") ::
    Rule("spinner", "changed", "description" -> "coefficient for CO<sub>2</sub>") ::
    Rule("buttonNode", "pressed", "actionCommand" -> "Start") ::
    Rule("buttonNode", "pressed", "actionCommand" -> "Check") ::
    Rule("buttonNode", "pressed", "actionCommand" -> "Show Answer") ::
    Rule("system", "gameEnded", "level" -> "1") ::
    Rule("system", "gameEnded", "level" -> "2") ::
    Rule("system", "gameEnded", "level" -> "3") ::
    Nil

}