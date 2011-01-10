/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;

/**
 * This class identifies and classifies atoms based on their configuration of
 * protons, neturons, and electrons.  It is common functionality that is used
 * by a number of different classes that represent atoms, which is why it was
 * separated out.  The intent is that this be used by the atom classes
 * themselves so that clients of the atom classes can get the needed
 * information from directly from them, rather than using this class as a sort
 * of "3rd party expert".
 *
 * @author John Blanco
 */
public class AtomIdentifier {

    // This data structure maps the proton number, a.k.a. the atomic number,
    // to the element name.
    private static final HashMap<Integer, String> MAP_PROTON_COUNT_TO_NAME = new HashMap<Integer, String>() {
        {
            put( 0, new String( BuildAnAtomStrings.ELEMENT_NONE_NAME ) );//for an unbuilt or empty atom
            put( 1, new String( BuildAnAtomStrings.ELEMENT_HYDROGEN_NAME ) );
            put( 2, new String( BuildAnAtomStrings.ELEMENT_HELIUM_NAME ) );
            put( 3, new String( BuildAnAtomStrings.ELEMENT_LITHIUM_NAME ) );
            put( 4, new String( BuildAnAtomStrings.ELEMENT_BERYLLIUM_NAME ) );
            put( 5, new String( BuildAnAtomStrings.ELEMENT_BORON_NAME ) );
            put( 6, new String( BuildAnAtomStrings.ELEMENT_CARBON_NAME ) );
            put( 7, new String( BuildAnAtomStrings.ELEMENT_NITROGEN_NAME ) );
            put( 8, new String( BuildAnAtomStrings.ELEMENT_OXYGEN_NAME ) );
            put( 9, new String( BuildAnAtomStrings.ELEMENT_FLUORINE_NAME ) );
            put( 10, new String( BuildAnAtomStrings.ELEMENT_NEON_NAME ) );
            put( 11, new String( BuildAnAtomStrings.ELEMENT_SODIUM_NAME ) );
            put( 12, new String( BuildAnAtomStrings.ELEMENT_MAGNESIUM_NAME ) );
            put( 13, new String( BuildAnAtomStrings.ELEMENT_ALUMINUM_NAME ) );
            put( 14, new String( BuildAnAtomStrings.ELEMENT_SILICON_NAME ) );
            put( 15, new String( BuildAnAtomStrings.ELEMENT_PHOSPHORUS_NAME ) );
            put( 16, new String( BuildAnAtomStrings.ELEMENT_SULPHER_NAME ) );
            put( 17, new String( BuildAnAtomStrings.ELEMENT_CHLORINE_NAME ) );
            put( 18, new String( BuildAnAtomStrings.ELEMENT_ARGON_NAME ) );
            put( 19, new String( BuildAnAtomStrings.ELEMENT_POTASSIUM_NAME ) );
            put( 20, new String( BuildAnAtomStrings.ELEMENT_CALCIUM_NAME ) );
        }
    };

    // This data structure lists the isotopes that are considered stable for
    // the purposes of this simulation.  This means that their half life is
    // less than the age of the universe.  The table only goes up as high as
    // needed for the sim.  This is taken from the table at
    // https://docs.google.com/document/edit?id=1VGGhLUetiwijbDneU-U6BPrjRlkI0rt939zk8Y4AuA4&authkey=CMLM4ZUC&hl=en#
    private static final ArrayList<Isotope> STABLE_ISOTOPES = new ArrayList<Isotope>() {
        {
            //H
            add( new Isotope( 1, 0 ) );
            add( new Isotope( 2, 1 ) );
            //He
            add( new Isotope( 3, 1 ) );
            add( new Isotope( 4, 2 ) );
            //Li
            add( new Isotope( 6, 3 ) );
            add( new Isotope( 7, 4 ) );
            //Be
            add( new Isotope( 9, 5 ) );
            //B
            add( new Isotope( 10, 5 ) );
            add( new Isotope( 11, 6 ) );
            //C
            add( new Isotope( 12, 6 ) );
            add( new Isotope( 13, 7 ) );
            //N
            add( new Isotope( 14, 7 ) );
            add( new Isotope( 15, 8 ) );
            //O
            add( new Isotope( 16, 8 ) );
            add( new Isotope( 17, 9 ) );
            add( new Isotope( 18, 10 ) );
            //F
            add( new Isotope( 19, 10 ) );
            //Ne
            add( new Isotope( 20, 10 ) );
            add( new Isotope( 21, 11 ) );
            add( new Isotope( 22, 12 ) );
        }
    };

    // Copied table from http://www.zyra.org.uk/elements.htm
    // Used for generating the element table version below in main()
    private static final String ORIGINAL_TABLE =
            "1 \tHYDROGEN  \tH  \t1.008\n" +
            "2 \tHELIUM \tHe \t4.003\n" +
            "3 \tLITHIUM \tLi \t6.939\n" +
            "4 \tBERYLLIUM \tBe \t9.012\n" +
            "5 \tBORON \tB \t10.811\n" +
            "6 \tCARBON \tC \t12.011\n" +
            "7 \tNITROGEN \tN \t14.007\n" +
            "8 \tOXYGEN \tO \t15.999\n" +
            "9 \tFLUORINE \tF \t18.998\n" +
            "10 \tNEON \tNe \t20.183\n" +
            "11 \tSODIUM \tNa \t22.990\n" +
            "12 \tMAGNESIUM \tMg \t24.312\n" +
            "13 \tALUMINIUM \tAl \t26.982\n" +
            "14 \tSILICON \tSi \t28.086\n" +
            "15 \tPHOSPHORUS \tP \t30.974\n" +
            "16 \tSULPHUR \tS \t32.064\n" +
            "17 \tCHLORINE \tCl \t35.453\n" +
            "18 \tARGON \tAr \t39.948\n" +
            "19 \tPOTASSIUM \tK \t39.102\n" +
            "20 \tCALCIUM \tCa \t40.08\n" +
            "21 \tSCANDIUM \tSc \t44.956\n" +
            "22 \tTITANIUM \tTi \t47.90\n" +
            "23 \tVANADIUM \tV \t50.94\n" +
            "24 \tCHROMIUM \tCr \t52.00\n" +
            "25 \tMANGANESE \tMn \t54.94\n" +
            "26 \tIRON \tFe \t55.85\n" +
            "27 \tCOBALT \tCo \t58.93\n" +
            "28 \tNICKEL \tNi \t58.71\n" +
            "29 \tCOPPER \tCu \t63.54\n" +
            "30 \tZINC \tZn \t65.37\n" +
            "31 \tGALLIUM \tGa \t69.72\n" +
            "32 \tGERMANIUM \tGe \t72.59\n" +
            "33 \tARSENIC \tAs \t74.92\n" +
            "34 \tSELENIUM \tSe \t78.96\n" +
            "35 \tBROMINE \tBr \t79.909\n" +
            "36 \tKRYPTON \tKr \t83.80\n" +
            "37 \tRUBIDIUM \tRb \t85.47\n" +
            "38 \tSTRONTIUM \tSr \t87.62\n" +
            "39 \tYTTRIUM \tY \t88.905\n" +
            "40 \tZIRCONIUM \tZr \t91.22\n" +
            "41 \tNIOBIUM \tNb \t92.906\n" +
            "42 \tMOLYBDENUM \tMo \t95.94\n" +
            "43 \tTECHNETIUM \tTc \t99.00\n" +
            "44 \tRUTHENIUM \tRu \t101.07\n" +
            "45 \tRHODIUM \tRh \t102.92\n" +
            "46 \tPALLADIUM \tPd \t106.4\n" +
            "47 \tSILVER \tAg \t107.87\n" +
            "48 \tCADMIUM \tCd \t112.40\n" +
            "49 \tINDIUM \tIn \t114.82\n" +
            "50 \tTIN \tSn \t118.69\n" +
            "51 \tANTIMONY \tSb \t121.75\n" +
            "52 \tTELLURIUM \tTe \t127.60\n" +
            "53 \tIODINE \tI \t126.904\n" +
            "54 \tXENON \tXe \t131.30\n" +
            "55 \tCAESIUM \tCs \t132.905\n" +
            "56 \tBARIUM \tBa \t137.34\n" +
            "57 \tLANTHANUM \tLa \t138.91\n" +
            "58 \tCERIUM \tCe \t140.12\n" +
            "59 \tPRASEODYMIUM \tPr \t140.907\n" +
            "60 \tNEODYMIUM \tNd \t144.24\n" +
            "61 \tPROMETHIUM \tPm \t147\n" +
            "62 \tSAMARIUM \tSm \t150.35\n" +
            "63 \tEUROPIUM \tEu \t151.96\n" +
            "64 \tGADOLINIUM \tGd \t157.25\n" +
            "65 \tTERBIUM \tTb \t158.92\n" +
            "66 \tDYSPROSIUM \tDy \t162.50\n" +
            "67 \tHOLMIUM \tHo \t164.93\n" +
            "68 \tERBIUM \tEr \t167.26\n" +
            "69 \tTHULIUM \tTm \t168.93\n" +
            "70 \tYTTERBIUM \tYb \t173.04\n" +
            "71 \tLUTETIUM \tLu \t174.97\n" +
            "72 \tHAFNIUM \tHf \t178.49\n" +
            "73 \tTANTALUM \tTa \t180.95\n" +
            "74 \tTUNGSTEN\tW \t183.85\n" +
            "75 \tRHENIUM \tRe \t186.2\n" +
            "76 \tOSMIUM \tOs \t190.2\n" +
            "77 \tIRIDIUM \tIr \tI92.2\n" +
            "78 \tPLATINUM \tPt \t195.09\n" +
            "79 \tGOLD \tAu \t196.97\n" +
            "80 \tMERCURY \tHg \t200.59\n" +
            "81 \tTHALLIUM \tTl \t204.37\n" +
            "82 \tLEAD \tPb \t207.19\n" +
            "83 \tBISMUTH \tBi \t208.98\n" +
            "84 \tPOLONIUM \tPo \t210\n" +
            "85 \tASTATINE \tAt \t211\n" +
            "86 \tRADON \tRn \t222\n" +
            "87 \tFRANCIUM \tFr \t223\n" +
            "88 \tRADIUM \tRa \t226.05\n" +
            "89 \tACTINIUM \tAc \t227.05\n" +
            "90 \tTHORIUM \tTh \t232.12\n" +
            "91 \tPROTACTINIUM \tPa \t231.05\n" +
            "92 \tURANIUM \tU \t238.07\n" +
            "93 \tNEPTUNIUM \tNp \t237\n" +
            "94 \tPLUTONIUM \tPu \t239\n" +
            "95 \tAMERICIUM \tAm \t241\n" +
            "96 \tCURIUM \tCm \t242\n" +
            "97 \tBERKELIUM \tBk \t243_250\n" +
            "98 \tCALIFORNIUM \tCf \t251\n" +
            "99 \tEINSTEINIUM \tEs \t246,247\n" +
            "100 \tFERMIUM \tFm \t250,252_256\n" +
            "101 \tMENDELEVIUM \tMd \t256\n" +
            "102 \tNOBELIUM \tNo \t254\n" +
            "103 \tLAWRENCIUM \tLr \t257\n" +
            "104 \tRUTHERFORDIUM\tRf\t267 ?\n" +
            "105 \tDUBNIUM\tDb\t268 ?\n" +
            "106 \tSEABORGIUM \tSg\t271 ?\n" +
            "107 \tBOHRIUM\tBh\n" +
            "108 \tHASSIUM\tHs\n" +
            "109 \tMEITNERIUM\tMt\n" +
            "110 \tDARMSTADTIUM\tDs\n" +
            "111 \tROENTGENIUM\tRg\n" +
            "112 \tUNUNBIUM\tCn\n" + //Modified this line based on http://www.ptable.com/
            "113 \tUNUNTRIUM\tUut\n" +
            "114 \tUNUNQUADIUM\tUuq\n" +
            "115 \tUNUNPENTIUM\tUup\n" +
            "116 \tUNUNHEXIUM\tUuh\n" +
            "117 \tUNUNSEPTIUM\tUus\n" +
            "118 \tUNUNOCTIUM\tUuo\n" +
            "119 \tUNUNENNIUM\tUue\n" +
            "120 \tUNBINILIUM\tUbn\n" +
            "121 \tUNBIUNIUM\tUbu\n" +
            "122 \tUNBIBIUM\tUbb\n" +
            "123 \tUNBITRIUM\tUbt\n" +
            "124 \tUNBIQUADIUM\tUbq\n" +
            "125 \tUNBIPENTIUM\tUbp\n" +
            "126 \tUNBIHEXIUM\tUbh\n" +
            "127 \tUNBISEPTIUM\tUbs\n" +
            "128 \tUNBIOCTIUM\tUbo\n" +
            "129 \tUNBIENNIUM\tUbe\n" +
            "130 \tUNTRINILIUM\tUtn\n" +
            "131 \tUNTRINIUM\tUtu";

    // Table of element symbols, indexed by the atomic number.  Note that this
    // is not internationalizable, a decision made by the chemistry team.
    public static final String [] ELEMENT_SYMBOL_TABLE = {
        BuildAnAtomStrings.ELEMENT_NONE_SYMBOL, // 0, NO ELEMENT
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
        "S", // 16, SULPHUR
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


    public static String getSymbol( IAtom atom ) {
        return getSymbol( atom.getNumProtons() );
    }

    public static String getSymbol( int protonCount ) {
        if ( protonCount <= 0 || protonCount > ELEMENT_SYMBOL_TABLE.length ) {
            return BuildAnAtomStrings.ELEMENT_NONE_SYMBOL;
        }
        else {
            return ELEMENT_SYMBOL_TABLE[protonCount];
        }
    }

    public static String getName( IAtom atom ) {
        return getName( atom.getNumProtons() );
    }

    public static String getName( int protonCount ) {
        String symbol = MAP_PROTON_COUNT_TO_NAME.get( protonCount );
        if ( symbol == null ) {
            System.err.println( "Error: No element found for proton count = " + protonCount );
            symbol = "Unknown";
        }
        return symbol;
    }

    public static boolean isStable( IAtom atom ) {
        return atom.getMassNumber() == 0 || STABLE_ISOTOPES.contains( new Isotope( atom.getMassNumber(), atom.getNumNeutrons() ) );
    }

    public static double getAtomicMass( IAtom atom ) {
        // TODO This needs to be filled in with a table of the atomic mass.
        return 1;
    }

    private static class Isotope {
        public final int massNumber;
        public final int neutronNumber;

        //Regenerate equals and hashcode if you change the contents of the isotope

        public Isotope( int massNumber, int neutronNumber ) {
            this.massNumber = massNumber;
            this.neutronNumber = neutronNumber;
        }

        //Autogenerated

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            Isotope isotope = (Isotope) o;

            if ( massNumber != isotope.massNumber ) {
                return false;
            }
            if ( neutronNumber != isotope.neutronNumber ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = massNumber;
            result = 31 * result + neutronNumber;
            return result;
        }
    }

    /**
     * Use to regenerate element symbol table if needed.
     */
    public static void main( String[] args ) {
        String t = ORIGINAL_TABLE;
        StringTokenizer stringTokenizer = new StringTokenizer( t, "\n" );
        while ( stringTokenizer.hasMoreElements() ) {
            String line = stringTokenizer.nextToken();
            StringTokenizer st = new StringTokenizer( line, "\t " );
            int index = Integer.parseInt( st.nextToken() );//index
            if ( index <= 112 ) {
                String name = st.nextToken();//name;
                String symbol = st.nextToken();//symbol
                System.out.println( "      \"" + symbol + "\"," + " // " + index + ", " + name );
            }
        }
    }
}
