package edu.colorado.phet.common.piccolophet.nodes.periodictable;

/**
 * Table of element symbols, indexed by the atomic number.  Note that this
 * is not internationalizable, a decision made by the chemistry team.
 * Note that this array starts with a "-" so that index matches atomic number, e.g., 1->H
 *
 * @author John Blanco
 */
public class SymbolTable {
    public static final String[] ELEMENT_SYMBOL_TABLE = {
            "-", // 0, NO ELEMENT
            "H", // 1, HYDROGEN
            "He", // 2, HELIUM
            "Li", // 3, LITHIUM
            "Be", // 4, BERYLLIUM
            "B", // 5, BORON
            "C", // 6, CARBON
            "N", // 7, NITROGEN
            "O", // 8, OXYGEN
            "F", // 9, FLUORINE
            "Ne", // 10, NEON
            "Na", // 11, SODIUM
            "Mg", // 12, MAGNESIUM
            "Al", // 13, ALUMINIUM
            "Si", // 14, SILICON
            "P", // 15, PHOSPHORUS
            "S", // 16, SULFUR
            "Cl", // 17, CHLORINE
            "Ar", // 18, ARGON
            "K", // 19, POTASSIUM
            "Ca", // 20, CALCIUM
            "Sc", // 21, SCANDIUM
            "Ti", // 22, TITANIUM
            "V", // 23, VANADIUM
            "Cr", // 24, CHROMIUM
            "Mn", // 25, MANGANESE
            "Fe", // 26, IRON
            "Co", // 27, COBALT
            "Ni", // 28, NICKEL
            "Cu", // 29, COPPER
            "Zn", // 30, ZINC
            "Ga", // 31, GALLIUM
            "Ge", // 32, GERMANIUM
            "As", // 33, ARSENIC
            "Se", // 34, SELENIUM
            "Br", // 35, BROMINE
            "Kr", // 36, KRYPTON
            "Rb", // 37, RUBIDIUM
            "Sr", // 38, STRONTIUM
            "Y", // 39, YTTRIUM
            "Zr", // 40, ZIRCONIUM
            "Nb", // 41, NIOBIUM
            "Mo", // 42, MOLYBDENUM
            "Tc", // 43, TECHNETIUM
            "Ru", // 44, RUTHENIUM
            "Rh", // 45, RHODIUM
            "Pd", // 46, PALLADIUM
            "Ag", // 47, SILVER
            "Cd", // 48, CADMIUM
            "In", // 49, INDIUM
            "Sn", // 50, TIN
            "Sb", // 51, ANTIMONY
            "Te", // 52, TELLURIUM
            "I", // 53, IODINE
            "Xe", // 54, XENON
            "Cs", // 55, CAESIUM
            "Ba", // 56, BARIUM
            "La", // 57, LANTHANUM
            "Ce", // 58, CERIUM
            "Pr", // 59, PRASEODYMIUM
            "Nd", // 60, NEODYMIUM
            "Pm", // 61, PROMETHIUM
            "Sm", // 62, SAMARIUM
            "Eu", // 63, EUROPIUM
            "Gd", // 64, GADOLINIUM
            "Tb", // 65, TERBIUM
            "Dy", // 66, DYSPROSIUM
            "Ho", // 67, HOLMIUM
            "Er", // 68, ERBIUM
            "Tm", // 69, THULIUM
            "Yb", // 70, YTTERBIUM
            "Lu", // 71, LUTETIUM
            "Hf", // 72, HAFNIUM
            "Ta", // 73, TANTALUM
            "W", // 74, TUNGSTEN
            "Re", // 75, RHENIUM
            "Os", // 76, OSMIUM
            "Ir", // 77, IRIDIUM
            "Pt", // 78, PLATINUM
            "Au", // 79, GOLD
            "Hg", // 80, MERCURY
            "Tl", // 81, THALLIUM
            "Pb", // 82, LEAD
            "Bi", // 83, BISMUTH
            "Po", // 84, POLONIUM
            "At", // 85, ASTATINE
            "Rn", // 86, RADON
            "Fr", // 87, FRANCIUM
            "Ra", // 88, RADIUM
            "Ac", // 89, ACTINIUM
            "Th", // 90, THORIUM
            "Pa", // 91, PROTACTINIUM
            "U", // 92, URANIUM
            "Np", // 93, NEPTUNIUM
            "Pu", // 94, PLUTONIUM
            "Am", // 95, AMERICIUM
            "Cm", // 96, CURIUM
            "Bk", // 97, BERKELIUM
            "Cf", // 98, CALIFORNIUM
            "Es", // 99, EINSTEINIUM
            "Fm", // 100, FERMIUM
            "Md", // 101, MENDELEVIUM
            "No", // 102, NOBELIUM
            "Lr", // 103, LAWRENCIUM
            "Rf", // 104, RUTHERFORDIUM
            "Db", // 105, DUBNIUM
            "Sg", // 106, SEABORGIUM
            "Bh", // 107, BOHRIUM
            "Hs", // 108, HASSIUM
            "Mt", // 109, MEITNERIUM
            "Ds", // 110, DARMSTADTIUM
            "Rg", // 111, ROENTGENIUM
            "Cn", // 112, UNUNBIUM
    };

    public static String getSymbol( int atomicNumber ) {
        return ELEMENT_SYMBOL_TABLE[atomicNumber];
    }
}