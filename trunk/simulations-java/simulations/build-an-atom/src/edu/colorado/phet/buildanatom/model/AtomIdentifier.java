/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.text.DecimalFormat;
import java.util.*;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.common.phetcommon.util.PrecisionDecimal;

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
            put( 16, new String( BuildAnAtomStrings.ELEMENT_SULFUR_NAME ) );
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
            // Na
            add( new Isotope( 23, 12 ) );
            // Mg
            add( new Isotope( 24, 12 ) );
            add( new Isotope( 25, 13 ) );
            add( new Isotope( 26, 14 ) );
            // Al
            add( new Isotope( 27, 14 ) );
            // Si
            add( new Isotope( 28, 14 ) );
            add( new Isotope( 29, 15 ) );
            add( new Isotope( 30, 16 ) );
            // P
            add( new Isotope( 31, 16 ) );
            // S
            add( new Isotope( 32, 16 ) );
            add( new Isotope( 33, 17 ) );
            add( new Isotope( 34, 18 ) );
            add( new Isotope( 36, 20 ) );
            // Cl
            add( new Isotope( 35, 18 ) );
            add( new Isotope( 37, 20 ) );
            // Ar
            add( new Isotope( 36, 18 ) );
            add( new Isotope( 38, 20 ) );
            add( new Isotope( 40, 22 ) );
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
            "16 \tSULFUR \tS \t32.064\n" +
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

    // CSV-formatted table representing isotopes and atomic masses.  This was
    // obtained from:
    //
    // http://physics.nist.gov/cgi-bin/Compositions/stand_alone.pl?ele=&ascii=html&isotype=some
    //
    // ...though some post-processing was necessary to get it into the format below.
    private static final String ISOTOPE_INFORMATION_TABLE_STR =
        // Format:
        // Atomic number (empty if same as previous), Symbol, mass number, atomic mass, abundance.
        "1,H,1,1.00782503207,0.999885\n" +
        ",D,2,2.0141017778,0.000115\n" +
        ",T,3,3.0160492777,0.000000000001\n" +         // I (jblanco) made up the abundance, since Wikipedia just says "trace".
        "2,He,3,3.0160293191,0.00000134\n" +
        ",,4,4.00260325415,0.99999866\n" +
        "3,Li,6,6.015122795,0.0759\n" +
        ",,7,7.01600455,0.9241\n" +
        "4,Be,9,9.0121822,1.0000\n" +
        "5,B,10,10.0129370,0.199\n" +
        ",,11,11.0093054,0.801\n" +
        "6,C,12,12.0000000,0.9893\n" +
        ",,13,13.0033548378,0.0107\n" +
        ",,14,14.003241989,0.000000000001\n" +         // I (jblanco) made up the abundance, since Wikipedia just says "trace".
        "7,N,14,14.0030740048,0.99636\n" +
        ",,15,15.0001088982,0.00364\n" +
        "8,O,16,15.99491461956,0.99757\n" +
        ",,17,16.99913170,0.00038\n" +
        ",,18,17.9991610,0.00205\n" +
        "9,F,19,18.99840322,1.0000\n" +
        "10,Ne,20,19.9924401754,0.9048\n" +
        ",,21,20.99384668,0.0027\n" +
        ",,22,21.991385114,0.0925\n" +
        "11,Na,23,22.9897692809,1.0000\n" +
        "12,Mg,24,23.985041700,0.7899\n" +
        ",,25,24.98583692,0.1000\n" +
        ",,26,25.982592929,0.1101\n" +
        "13,Al,27,26.98153863,1.0000\n" +
        "14,Si,28,27.9769265325,0.92223\n" +
        ",,29,28.976494700,0.04685\n" +
        ",,30,29.97377017,0.03092\n" +
        "15,P,31,30.97376163,1.0000\n" +
        "16,S,32,31.97207100,0.9499\n" +
        ",,33,32.97145876,0.0075\n" +
        ",,34,33.96786690,0.0425\n" +
        ",,36,35.96708076,0.0001\n" +
        "17,Cl,35,34.96885268,0.7576\n" +
        ",,37,36.96590259,0.2424\n" +
        "18,Ar,36,35.967545106,0.003365\n" +
        ",,38,37.9627324,0.000632\n" +
        ",,40,39.9623831225,0.996003\n" +
        "19,K,39,38.96370668,0.932581\n" +
        ",,40,39.96399848,0.000117\n" +
        ",,41,40.96182576,0.067302\n" +
        "20,Ca,40,39.96259098,0.96941\n" +
        ",,42,41.95861801,0.00647\n" +
        ",,43,42.9587666,0.00135\n" +
        ",,44,43.9554818,0.02086\n" +
        ",,46,45.9536926,0.00004\n" +
        ",,48,47.952534,0.00187\n" +
        "21,Sc,45,44.9559119,1.0000\n" +
        "22,Ti,46,45.9526316,0.0825\n" +
        ",,47,46.9517631,0.0744\n" +
        ",,48,47.9479463,0.7372\n" +
        ",,49,48.9478700,0.0541\n" +
        ",,50,49.9447912,0.0518\n" +
        "23,V,50,49.9471585,0.00250\n" +
        ",,51,50.9439595,0.99750\n" +
        "24,Cr,50,49.9460442,0.04345\n" +
        ",,52,51.9405075,0.83789\n" +
        ",,53,52.9406494,0.09501\n" +
        ",,54,53.9388804,0.02365\n" +
        "25,Mn,55,54.9380451,1.0000\n" +
        "26,Fe,54,53.9396105,0.05845\n" +
        ",,56,55.9349375,0.91754\n" +
        ",,57,56.9353940,0.02119\n" +
        ",,58,57.9332756,0.00282\n" +
        "27,Co,59,58.9331950,1.0000\n" +
        "28,Ni,58,57.9353429,0.680769\n" +
        ",,60,59.9307864,0.262231\n" +
        ",,61,60.9310560,0.011399\n" +
        ",,62,61.9283451,0.036345\n" +
        ",,64,63.9279660,0.009256\n" +
        "29,Cu,63,62.9295975,0.6915\n" +
        ",,65,64.9277895,0.3085\n" +
        "30,Zn,64,63.9291422,0.48268\n" +
        ",,66,65.9260334,0.27975\n" +
        ",,67,66.9271273,0.04102\n" +
        ",,68,67.9248442,0.19024\n" +
        ",,70,69.9253193,0.00631\n" +
        "31,Ga,69,68.9255736,0.60108\n" +
        ",,71,70.9247013,0.39892\n" +
        "32,Ge,70,69.9242474,0.2038\n" +
        ",,72,71.9220758,0.2731\n" +
        ",,73,72.9234589,0.0776\n" +
        ",,74,73.9211778,0.3672\n" +
        ",,76,75.9214026,0.0783\n" +
        "33,As,75,74.9215965,1.0000\n" +
        "34,Se,74,73.9224764,0.0089\n" +
        ",,76,75.9192136,0.0937\n" +
        ",,77,76.9199140,0.0763\n" +
        ",,78,77.9173091,0.2377\n" +
        ",,80,79.9165213,0.4961\n" +
        ",,82,81.9166994,0.0873\n" +
        "35,Br,79,78.9183371,0.5069\n" +
        ",,81,80.9162906,0.4931\n" +
        "36,Kr,78,77.9203648,0.00355\n" +
        ",,80,79.9163790,0.02286\n" +
        ",,82,81.9134836,0.11593\n" +
        ",,83,82.914136,0.11500\n" +
        ",,84,83.911507,0.56987\n" +
        ",,86,85.91061073,0.17279\n" +
        "37,Rb,85,84.911789738,0.7217\n" +
        ",,87,86.909180527,0.2783\n" +
        "38,Sr,84,83.913425,0.0056\n" +
        ",,86,85.9092602,0.0986\n" +
        ",,87,86.9088771,0.0700\n" +
        ",,88,87.9056121,0.8258\n" +
        "39,Y,89,88.9058483,1.0000\n" +
        "40,Zr,90,89.9047044,0.5145\n" +
        ",,91,90.9056458,0.1122\n" +
        ",,92,91.9050408,0.1715\n" +
        ",,94,93.9063152,0.1738\n" +
        ",,96,95.9082734,0.0280\n" +
        "41,Nb,93,92.9063781,1.0000\n" +
        "42,Mo,92,91.906811,0.1477\n" +
        ",,94,93.9050883,0.0923\n" +
        ",,95,94.9058421,0.1590\n" +
        ",,96,95.9046795,0.1668\n" +
        ",,97,96.9060215,0.0956\n" +
        ",,98,97.9054082,0.2419\n" +
        ",,100,99.907477,0.0967\n" +
        "43,Tc,97,96.906365,\n" +
        ",,98,97.907216,\n" +
        ",,99,98.9062547,\n" +
        "44,Ru,96,95.907598,0.0554\n" +
        ",,98,97.905287,0.0187\n" +
        ",,99,98.9059393,0.1276\n" +
        ",,100,99.9042195,0.1260\n" +
        ",,101,100.9055821,0.1706\n" +
        ",,102,101.9043493,0.3155\n" +
        ",,104,103.905433,0.1862\n" +
        "45,Rh,103,102.905504,1.0000\n" +
        "46,Pd,102,101.905609,0.0102\n" +
        ",,104,103.904036,0.1114\n" +
        ",,105,104.905085,0.2233\n" +
        ",,106,105.903486,0.2733\n" +
        ",,108,107.903892,0.2646\n" +
        ",,110,109.905153,0.1172\n" +
        "47,Ag,107,106.905097,0.51839\n" +
        ",,109,108.904752,0.48161\n" +
        "48,Cd,106,105.906459,0.0125\n" +
        ",,108,107.904184,0.0089\n" +
        ",,110,109.9030021,0.1249\n" +
        ",,111,110.9041781,0.1280\n" +
        ",,112,111.9027578,0.2413\n" +
        ",,113,112.9044017,0.1222\n" +
        ",,114,113.9033585,0.2873\n" +
        ",,116,115.904756,0.0749\n" +
        "49,In,113,112.904058,0.0429\n" +
        ",,115,114.903878,0.9571\n" +
        "50,Sn,112,111.904818,0.0097\n" +
        ",,114,113.902779,0.0066\n" +
        ",,115,114.903342,0.0034\n" +
        ",,116,115.901741,0.1454\n" +
        ",,117,116.902952,0.0768\n" +
        ",,118,117.901603,0.2422\n" +
        ",,119,118.903308,0.0859\n" +
        ",,120,119.9021947,0.3258\n" +
        ",,122,121.9034390,0.0463\n" +
        ",,124,123.9052739,0.0579\n" +
        "51,Sb,121,120.9038157,0.5721\n" +
        ",,123,122.9042140,0.4279\n" +
        "52,Te,120,119.904020,0.0009\n" +
        ",,122,121.9030439,0.0255\n" +
        ",,123,122.9042700,0.0089\n" +
        ",,124,123.9028179,0.0474\n" +
        ",,125,124.9044307,0.0707\n" +
        ",,126,125.9033117,0.1884\n" +
        ",,128,127.9044631,0.3174\n" +
        ",,130,129.9062244,0.3408\n" +
        "53,I,127,126.904473,1.0000\n" +
        "54,Xe,124,123.9058930,0.000952\n" +
        ",,126,125.904274,0.000890\n" +
        ",,128,127.9035313,0.019102\n" +
        ",,129,128.9047794,0.264006\n" +
        ",,130,129.9035080,0.040710\n" +
        ",,131,130.9050824,0.212324\n" +
        ",,132,131.9041535,0.269086\n" +
        ",,134,133.9053945,0.104357\n" +
        ",,136,135.907219,0.088573\n" +
        "55,Cs,133,132.905451933,1.0000\n" +
        "56,Ba,130,129.9063208,0.00106\n" +
        ",,132,131.9050613,0.00101\n" +
        ",,134,133.9045084,0.02417\n" +
        ",,135,134.9056886,0.06592\n" +
        ",,136,135.9045759,0.07854\n" +
        ",,137,136.9058274,0.11232\n" +
        ",,138,137.9052472,0.71698\n" +
        "57,La,138,137.907112,0.00090\n" +
        ",,139,138.9063533,0.99910\n" +
        "58,Ce,136,135.907172,0.00185\n" +
        ",,138,137.905991,0.00251\n" +
        ",,140,139.9054387,0.88450\n" +
        ",,142,141.909244,0.11114\n" +
        "59,Pr,141,140.9076528,1.0000\n" +
        "60,Nd,142,141.9077233,0.272\n" +
        ",,143,142.9098143,0.122\n" +
        ",,144,143.9100873,0.238\n" +
        ",,145,144.9125736,0.083\n" +
        ",,146,145.9131169,0.172\n" +
        ",,148,147.916893,0.057\n" +
        ",,150,149.920891,0.056\n" +
        "61,Pm,145,144.912749,\n" +
        ",,147,146.9151385,\n" +
        "62,Sm,144,143.911999,0.0307\n" +
        ",,147,146.9148979,0.1499\n" +
        ",,148,147.9148227,0.1124\n" +
        ",,149,148.9171847,0.1382\n" +
        ",,150,149.9172755,0.0738\n" +
        ",,152,151.9197324,0.2675\n" +
        ",,154,153.9222093,0.2275\n" +
        "63,Eu,151,150.9198502,0.4781\n" +
        ",,153,152.9212303,0.5219\n" +
        "64,Gd,152,151.9197910,0.0020\n" +
        ",,154,153.9208656,0.0218\n" +
        ",,155,154.9226220,0.1480\n" +
        ",,156,155.9221227,0.2047\n" +
        ",,157,156.9239601,0.1565\n" +
        ",,158,157.9241039,0.2484\n" +
        ",,160,159.9270541,0.2186\n" +
        "65,Tb,159,158.9253468,1.0000\n" +
        "66,Dy,156,155.924283,0.00056\n" +
        ",,158,157.924409,0.00095\n" +
        ",,160,159.9251975,0.02329\n" +
        ",,161,160.9269334,0.18889\n" +
        ",,162,161.9267984,0.25475\n" +
        ",,163,162.9287312,0.24896\n" +
        ",,164,163.9291748,0.28260\n" +
        "67,Ho,165,164.9303221,1.0000\n" +
        "68,Er,162,161.928778,0.00139\n" +
        ",,164,163.929200,0.01601\n" +
        ",,166,165.9302931,0.33503\n" +
        ",,167,166.9320482,0.22869\n" +
        ",,168,167.9323702,0.26978\n" +
        ",,170,169.9354643,0.14910\n" +
        "69,Tm,169,168.9342133,1.0000\n" +
        "70,Yb,168,167.933897,0.0013\n" +
        ",,170,169.9347618,0.0304\n" +
        ",,171,170.9363258,0.1428\n" +
        ",,172,171.9363815,0.2183\n" +
        ",,173,172.9382108,0.1613\n" +
        ",,174,173.9388621,0.3183\n" +
        ",,176,175.9425717,0.1276\n" +
        "71,Lu,175,174.9407718,0.9741\n" +
        ",,176,175.9426863,0.0259\n" +
        "72,Hf,174,173.940046,0.0016\n" +
        ",,176,175.9414086,0.0526\n" +
        ",,177,176.9432207,0.1860\n" +
        ",,178,177.9436988,0.2728\n" +
        ",,179,178.9458161,0.1362\n" +
        ",,180,179.9465500,0.3508\n" +
        "73,Ta,180,179.9474648,0.00012\n" +
        ",,181,180.9479958,0.99988\n" +
        "74,W,180,179.946704,0.0012\n" +
        ",,182,181.9482042,0.2650\n" +
        ",,183,182.9502230,0.1431\n" +
        ",,184,183.9509312,0.3064\n" +
        ",,186,185.9543641,0.2843\n" +
        "75,Re,185,184.9529550,0.3740\n" +
        ",,187,186.9557531,0.6260\n" +
        "76,Os,184,183.9524891,0.0002\n" +
        ",,186,185.9538382,0.0159\n" +
        ",,187,186.9557505,0.0196\n" +
        ",,188,187.9558382,0.1324\n" +
        ",,189,188.9581475,0.1615\n" +
        ",,190,189.9584470,0.2626\n" +
        ",,192,191.9614807,0.4078\n" +
        "77,Ir,191,190.9605940,0.373\n" +
        ",,193,192.9629264,0.627\n" +
        "78,Pt,190,189.959932,0.00014\n" +
        ",,192,191.9610380,0.00782\n" +
        ",,194,193.9626803,0.32967\n" +
        ",,195,194.9647911,0.33832\n" +
        ",,196,195.9649515,0.25242\n" +
        ",,198,197.967893,0.07163\n" +
        "79,Au,197,196.9665687,1.0000\n" +
        "80,Hg,196,195.965833,0.0015\n" +
        ",,198,197.9667690,0.0997\n" +
        ",,199,198.9682799,0.1687\n" +
        ",,200,199.9683260,0.2310\n" +
        ",,201,200.9703023,0.1318\n" +
        ",,202,201.9706430,0.2986\n" +
        ",,204,203.9734939,0.0687\n" +
        "81,Tl,203,202.9723442,0.2952\n" +
        ",,205,204.9744275,0.7048\n" +
        "82,Pb,204,203.9730436,0.014\n" +
        ",,206,205.9744653,0.241\n" +
        ",,207,206.9758969,0.221\n" +
        ",,208,207.9766521,0.524\n" +
        "83,Bi,209,208.9803987,1.0000\n" +
        "84,Po,209,208.9824304,\n" +
        ",,210,209.9828737,\n" +
        "85,At,210,209.987148,\n" +
        ",,211,210.9874963,\n" +
        "86,Rn,211,210.990601,\n" +
        ",,220,220.0113940,\n" +
        ",,222,222.0175777,\n" +
        "87,Fr,223,223.0197359,\n" +
        "88,Ra,223,223.0185022,\n" +
        ",,224,224.0202118,\n" +
        ",,226,226.0254098,\n" +
        ",,228,228.0310703,\n" +
        "89,Ac,227,227.0277521,\n" +
        "90,Th,230,230.0331338,\n" +
        ",,232,232.0380553,1.0000\n" +
        "91,Pa,231,231.0358840,1.0000\n" +
        "92,U,233,233.0396352,\n" +
        ",,234,234.0409521,0.000054\n" +
        ",,235,235.0439299,0.007204\n" +
        ",,236,236.0455680,\n" +
        ",,238,238.0507882,0.992742\n" +
        "93,Np,236,236.046570,\n" +
        ",,237,237.0481734,\n" +
        "94,Pu,238,238.0495599,\n" +
        ",,239,239.0521634,\n" +
        ",,240,240.0538135,\n" +
        ",,241,241.0568515,\n" +
        ",,242,242.0587426,\n" +
        ",,244,244.064204,\n" +
        "95,Am,241,241.0568291,\n" +
        ",,243,243.0613811,\n" +
        "96,Cm,243,243.0613891,\n" +
        ",,244,244.0627526,\n" +
        ",,245,245.0654912,\n" +
        ",,246,246.0672237,\n" +
        ",,247,247.070354,\n" +
        ",,248,248.072349,\n" +
        "97,Bk,247,247.070307,\n" +
        ",,249,249.0749867,\n" +
        "98,Cf,249,249.0748535,\n" +
        ",,250,250.0764061,\n" +
        ",,251,251.079587,\n" +
        ",,252,252.081626,\n" +
        "99,Es,252,252.082980,\n" +
        "100,Fm,257,257.095105,\n" +
        "101,Md,258,258.098431,\n" +
        ",,260,260.10365,\n" +
        "102,No,259,259.10103,\n" +
        "103,Lr,262,262.10963,\n" +
        "104,Rf,265,265.11670,\n" +
        "105,Db,268,268.12545,\n" +
        "106,Sg,271,271.13347,\n" +
        "107,Bh,272,272.13803,\n" +
        "108,Hs,270,270.13465,\n" +
        "109,Mt,276,276.15116,\n" +
        "110,Ds,281,281.16206,\n" +
        "111,Rg,280,280.16447,\n" +
        "112,Cn,285,285.17411,\n" +
        "113,Uut,284,284.17808,\n" +
        "114,Uuq,289,289.18728,\n" +
        "115,Uup,288,288.19249,\n";

    // CSV-formatted table that maps atomic numbers to standard atomic mass
    // (a.k.a. standard atomic weight).  This was obtained from the URL below
    // subsequently post-processed to remove unneeded data:
    //
    // http://physics.nist.gov/cgi-bin/Compositions/stand_alone.pl?ele=&ascii=ascii2&isotype=some
    private static final String STRING_MAP_ATOMIC_NUM_TO_STD_MASS =
        "1, 1.00794\n" +
        "2, 4.002602\n" +
        "3, 6.941\n" +
        "4, 9.012182\n" +
        "5, 10.811\n" +
        "6, 12.0107\n" +
        "7, 14.0067\n" +
        "8, 15.9994\n" +
        "9, 18.9984032\n" +
        "10, 20.1797\n" +
        "11, 22.98976928\n" +
        "12, 24.3050\n" +
        "13, 26.9815386\n" +
        "14, 28.0855\n" +
        "15, 30.973762\n" +
        "16, 32.065\n" +
        "17, 35.453\n" +
        "18, 39.948\n" +
        "19, 39.0983\n" +
        "20, 40.078\n" +
        "21, 44.955912\n" +
        "22, 47.867\n" +
        "23, 50.9415\n" +
        "24, 51.9961\n" +
        "25, 54.938045\n" +
        "26, 55.845\n" +
        "27, 58.933195\n" +
        "28, 58.6934\n" +
        "29, 63.546\n" +
        "30, 65.38\n" +
        "31, 69.723\n" +
        "32, 72.64\n" +
        "33, 74.9216\n" +
        "34, 78.96\n" +
        "35, 79.904\n" +
        "36, 83.798\n" +
        "37, 85.4678\n" +
        "38, 87.62\n" +
        "39, 88.90585\n" +
        "40, 91.224\n" +
        "41, 92.90638\n" +
        "42, 95.96\n" +
        "43, 98\n" +
        "44, 101.07\n" +
        "45, 102.9055\n" +
        "46, 106.42\n" +
        "47, 107.8682\n" +
        "48, 112.411\n" +
        "49, 114.818\n" +
        "50, 118.71\n" +
        "51, 121.76\n" +
        "52, 127.6\n" +
        "53, 126.90447\n" +
        "54, 131.293\n" +
        "55, 132.9054519\n" +
        "56, 137.327\n" +
        "57, 138.90547\n" +
        "58, 140.116\n" +
        "59, 140.90765\n" +
        "60, 144.242\n" +
        "61, 145\n" +
        "62, 150.36\n" +
        "63, 151.964\n" +
        "64, 157.25\n" +
        "65, 158.92535\n" +
        "66, 162.5\n" +
        "67, 164.93032\n" +
        "68, 167.259\n" +
        "69, 168.93421\n" +
        "70, 173.054\n" +
        "71, 174.9668\n" +
        "72, 178.49\n" +
        "73, 180.94788\n" +
        "74, 183.84\n" +
        "75, 186.207\n" +
        "76, 190.23\n" +
        "77, 192.217\n" +
        "78, 195.084\n" +
        "79, 196.966569\n" +
        "80, 200.59\n" +
        "81, 204.3833\n" +
        "82, 207.2\n" +
        "83, 208.9804\n";


    // Table that maps atomic number to the atomic weight and abundances for
    // that isotope.  This was generated from data obtained from
    // http://physics.nist.gov/cgi-bin/Compositions/stand_alone.pl?ele=&ascii=html&isotype=some
    private static final Map<Integer, ArrayList<Isotope2>> ISOTOPE_INFORMATION_TABLE = new HashMap<Integer, ArrayList<Isotope2>>() {{
// Automatically generated, see routines in this class.
        ArrayList<Isotope2> listForAtomicNumber1 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 1, 0, 1.00782503207, "0.999885" ) );
            add( new Isotope2( 1, 1, 2.0141017778, "0.000115" ) );
            add( new Isotope2( 1, 2, 3.0160492777, "0.000000000001" ) );
        }};
        put( 1, listForAtomicNumber1 );
        ArrayList<Isotope2> listForAtomicNumber2 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 2, 1, 3.0160293191, "0.00000134" ) );
            add( new Isotope2( 2, 2, 4.00260325415, "0.99999866" ) );
        }};
        put( 2, listForAtomicNumber2 );
        ArrayList<Isotope2> listForAtomicNumber3 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 3, 3, 6.015122795, "0.0759" ) );
            add( new Isotope2( 3, 4, 7.01600455, "0.9241" ) );
        }};
        put( 3, listForAtomicNumber3 );
        ArrayList<Isotope2> listForAtomicNumber4 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 4, 5, 9.0121822, "1.0000" ) );
        }};
        put( 4, listForAtomicNumber4 );
        ArrayList<Isotope2> listForAtomicNumber5 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 5, 5, 10.012937, "0.199" ) );
            add( new Isotope2( 5, 6, 11.0093054, "0.801" ) );
        }};
        put( 5, listForAtomicNumber5 );
        ArrayList<Isotope2> listForAtomicNumber6 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 6, 6, 12.0, "0.9893" ) );
            add( new Isotope2( 6, 7, 13.0033548378, "0.0107" ) );
            add( new Isotope2( 6, 8, 14.003241989, "0.000000000001" ) );
        }};
        put( 6, listForAtomicNumber6 );
        ArrayList<Isotope2> listForAtomicNumber7 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 7, 7, 14.0030740048, "0.99636" ) );
            add( new Isotope2( 7, 8, 15.0001088982, "0.00364" ) );
        }};
        put( 7, listForAtomicNumber7 );
        ArrayList<Isotope2> listForAtomicNumber8 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 8, 8, 15.99491461956, "0.99757" ) );
            add( new Isotope2( 8, 9, 16.9991317, "0.00038" ) );
            add( new Isotope2( 8, 10, 17.999161, "0.00205" ) );
        }};
        put( 8, listForAtomicNumber8 );
        ArrayList<Isotope2> listForAtomicNumber9 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 9, 10, 18.99840322, "1.0000" ) );
        }};
        put( 9, listForAtomicNumber9 );
        ArrayList<Isotope2> listForAtomicNumber10 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 10, 10, 19.9924401754, "0.9048" ) );
            add( new Isotope2( 10, 11, 20.99384668, "0.0027" ) );
            add( new Isotope2( 10, 12, 21.991385114, "0.0925" ) );
        }};
        put( 10, listForAtomicNumber10 );
        ArrayList<Isotope2> listForAtomicNumber11 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 11, 12, 22.9897692809, "1.0000" ) );
        }};
        put( 11, listForAtomicNumber11 );
        ArrayList<Isotope2> listForAtomicNumber12 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 12, 12, 23.9850417, "0.7899" ) );
            add( new Isotope2( 12, 13, 24.98583692, "0.1000" ) );
            add( new Isotope2( 12, 14, 25.982592929, "0.1101" ) );
        }};
        put( 12, listForAtomicNumber12 );
        ArrayList<Isotope2> listForAtomicNumber13 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 13, 14, 26.98153863, "1.0000" ) );
        }};
        put( 13, listForAtomicNumber13 );
        ArrayList<Isotope2> listForAtomicNumber14 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 14, 14, 27.9769265325, "0.92223" ) );
            add( new Isotope2( 14, 15, 28.9764947, "0.04685" ) );
            add( new Isotope2( 14, 16, 29.97377017, "0.03092" ) );
        }};
        put( 14, listForAtomicNumber14 );
        ArrayList<Isotope2> listForAtomicNumber15 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 15, 16, 30.97376163, "1.0000" ) );
        }};
        put( 15, listForAtomicNumber15 );
        ArrayList<Isotope2> listForAtomicNumber16 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 16, 16, 31.972071, "0.9499" ) );
            add( new Isotope2( 16, 17, 32.97145876, "0.0075" ) );
            add( new Isotope2( 16, 18, 33.9678669, "0.0425" ) );
            add( new Isotope2( 16, 20, 35.96708076, "0.0001" ) );
        }};
        put( 16, listForAtomicNumber16 );
        ArrayList<Isotope2> listForAtomicNumber17 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 17, 18, 34.96885268, "0.7576" ) );
            add( new Isotope2( 17, 20, 36.96590259, "0.2424" ) );
        }};
        put( 17, listForAtomicNumber17 );
        ArrayList<Isotope2> listForAtomicNumber18 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 18, 18, 35.967545106, "0.003365" ) );
            add( new Isotope2( 18, 20, 37.9627324, "0.000632" ) );
            add( new Isotope2( 18, 22, 39.9623831225, "0.996003" ) );
        }};
        put( 18, listForAtomicNumber18 );
        ArrayList<Isotope2> listForAtomicNumber19 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 19, 20, 38.96370668, "0.932581" ) );
            add( new Isotope2( 19, 21, 39.96399848, "0.000117" ) );
            add( new Isotope2( 19, 22, 40.96182576, "0.067302" ) );
        }};
        put( 19, listForAtomicNumber19 );
        ArrayList<Isotope2> listForAtomicNumber20 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 20, 20, 39.96259098, "0.96941" ) );
            add( new Isotope2( 20, 22, 41.95861801, "0.00647" ) );
            add( new Isotope2( 20, 23, 42.9587666, "0.00135" ) );
            add( new Isotope2( 20, 24, 43.9554818, "0.02086" ) );
            add( new Isotope2( 20, 26, 45.9536926, "0.00004" ) );
            add( new Isotope2( 20, 28, 47.952534, "0.00187" ) );
        }};
        put( 20, listForAtomicNumber20 );
        ArrayList<Isotope2> listForAtomicNumber21 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 21, 24, 44.9559119, "1.0000" ) );
        }};
        put( 21, listForAtomicNumber21 );
        ArrayList<Isotope2> listForAtomicNumber22 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 22, 24, 45.9526316, "0.0825" ) );
            add( new Isotope2( 22, 25, 46.9517631, "0.0744" ) );
            add( new Isotope2( 22, 26, 47.9479463, "0.7372" ) );
            add( new Isotope2( 22, 27, 48.94787, "0.0541" ) );
            add( new Isotope2( 22, 28, 49.9447912, "0.0518" ) );
        }};
        put( 22, listForAtomicNumber22 );
        ArrayList<Isotope2> listForAtomicNumber23 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 23, 27, 49.9471585, "0.00250" ) );
            add( new Isotope2( 23, 28, 50.9439595, "0.99750" ) );
        }};
        put( 23, listForAtomicNumber23 );
        ArrayList<Isotope2> listForAtomicNumber24 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 24, 26, 49.9460442, "0.04345" ) );
            add( new Isotope2( 24, 28, 51.9405075, "0.83789" ) );
            add( new Isotope2( 24, 29, 52.9406494, "0.09501" ) );
            add( new Isotope2( 24, 30, 53.9388804, "0.02365" ) );
        }};
        put( 24, listForAtomicNumber24 );
        ArrayList<Isotope2> listForAtomicNumber25 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 25, 30, 54.9380451, "1.0000" ) );
        }};
        put( 25, listForAtomicNumber25 );
        ArrayList<Isotope2> listForAtomicNumber26 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 26, 28, 53.9396105, "0.05845" ) );
            add( new Isotope2( 26, 30, 55.9349375, "0.91754" ) );
            add( new Isotope2( 26, 31, 56.935394, "0.02119" ) );
            add( new Isotope2( 26, 32, 57.9332756, "0.00282" ) );
        }};
        put( 26, listForAtomicNumber26 );
        ArrayList<Isotope2> listForAtomicNumber27 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 27, 32, 58.933195, "1.0000" ) );
        }};
        put( 27, listForAtomicNumber27 );
        ArrayList<Isotope2> listForAtomicNumber28 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 28, 30, 57.9353429, "0.680769" ) );
            add( new Isotope2( 28, 32, 59.9307864, "0.262231" ) );
            add( new Isotope2( 28, 33, 60.931056, "0.011399" ) );
            add( new Isotope2( 28, 34, 61.9283451, "0.036345" ) );
            add( new Isotope2( 28, 36, 63.927966, "0.009256" ) );
        }};
        put( 28, listForAtomicNumber28 );
        ArrayList<Isotope2> listForAtomicNumber29 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 29, 34, 62.9295975, "0.6915" ) );
            add( new Isotope2( 29, 36, 64.9277895, "0.3085" ) );
        }};
        put( 29, listForAtomicNumber29 );
        ArrayList<Isotope2> listForAtomicNumber30 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 30, 34, 63.9291422, "0.48268" ) );
            add( new Isotope2( 30, 36, 65.9260334, "0.27975" ) );
            add( new Isotope2( 30, 37, 66.9271273, "0.04102" ) );
            add( new Isotope2( 30, 38, 67.9248442, "0.19024" ) );
            add( new Isotope2( 30, 40, 69.9253193, "0.00631" ) );
        }};
        put( 30, listForAtomicNumber30 );
        ArrayList<Isotope2> listForAtomicNumber31 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 31, 38, 68.9255736, "0.60108" ) );
            add( new Isotope2( 31, 40, 70.9247013, "0.39892" ) );
        }};
        put( 31, listForAtomicNumber31 );
        ArrayList<Isotope2> listForAtomicNumber32 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 32, 38, 69.9242474, "0.2038" ) );
            add( new Isotope2( 32, 40, 71.9220758, "0.2731" ) );
            add( new Isotope2( 32, 41, 72.9234589, "0.0776" ) );
            add( new Isotope2( 32, 42, 73.9211778, "0.3672" ) );
            add( new Isotope2( 32, 44, 75.9214026, "0.0783" ) );
        }};
        put( 32, listForAtomicNumber32 );
        ArrayList<Isotope2> listForAtomicNumber33 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 33, 42, 74.9215965, "1.0000" ) );
        }};
        put( 33, listForAtomicNumber33 );
        ArrayList<Isotope2> listForAtomicNumber34 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 34, 40, 73.9224764, "0.0089" ) );
            add( new Isotope2( 34, 42, 75.9192136, "0.0937" ) );
            add( new Isotope2( 34, 43, 76.919914, "0.0763" ) );
            add( new Isotope2( 34, 44, 77.9173091, "0.2377" ) );
            add( new Isotope2( 34, 46, 79.9165213, "0.4961" ) );
            add( new Isotope2( 34, 48, 81.9166994, "0.0873" ) );
        }};
        put( 34, listForAtomicNumber34 );
        ArrayList<Isotope2> listForAtomicNumber35 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 35, 44, 78.9183371, "0.5069" ) );
            add( new Isotope2( 35, 46, 80.9162906, "0.4931" ) );
        }};
        put( 35, listForAtomicNumber35 );
        ArrayList<Isotope2> listForAtomicNumber36 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 36, 42, 77.9203648, "0.00355" ) );
            add( new Isotope2( 36, 44, 79.916379, "0.02286" ) );
            add( new Isotope2( 36, 46, 81.9134836, "0.11593" ) );
            add( new Isotope2( 36, 47, 82.914136, "0.11500" ) );
            add( new Isotope2( 36, 48, 83.911507, "0.56987" ) );
            add( new Isotope2( 36, 50, 85.91061073, "0.17279" ) );
        }};
        put( 36, listForAtomicNumber36 );
        ArrayList<Isotope2> listForAtomicNumber37 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 37, 48, 84.911789738, "0.7217" ) );
            add( new Isotope2( 37, 50, 86.909180527, "0.2783" ) );
        }};
        put( 37, listForAtomicNumber37 );
        ArrayList<Isotope2> listForAtomicNumber38 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 38, 46, 83.913425, "0.0056" ) );
            add( new Isotope2( 38, 48, 85.9092602, "0.0986" ) );
            add( new Isotope2( 38, 49, 86.9088771, "0.0700" ) );
            add( new Isotope2( 38, 50, 87.9056121, "0.8258" ) );
        }};
        put( 38, listForAtomicNumber38 );
        ArrayList<Isotope2> listForAtomicNumber39 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 39, 50, 88.9058483, "1.0000" ) );
        }};
        put( 39, listForAtomicNumber39 );
        ArrayList<Isotope2> listForAtomicNumber40 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 40, 50, 89.9047044, "0.5145" ) );
            add( new Isotope2( 40, 51, 90.9056458, "0.1122" ) );
            add( new Isotope2( 40, 52, 91.9050408, "0.1715" ) );
            add( new Isotope2( 40, 54, 93.9063152, "0.1738" ) );
            add( new Isotope2( 40, 56, 95.9082734, "0.0280" ) );
        }};
        put( 40, listForAtomicNumber40 );
        ArrayList<Isotope2> listForAtomicNumber41 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 41, 52, 92.9063781, "1.0000" ) );
        }};
        put( 41, listForAtomicNumber41 );
        ArrayList<Isotope2> listForAtomicNumber42 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 42, 50, 91.906811, "0.1477" ) );
            add( new Isotope2( 42, 52, 93.9050883, "0.0923" ) );
            add( new Isotope2( 42, 53, 94.9058421, "0.1590" ) );
            add( new Isotope2( 42, 54, 95.9046795, "0.1668" ) );
            add( new Isotope2( 42, 55, 96.9060215, "0.0956" ) );
            add( new Isotope2( 42, 56, 97.9054082, "0.2419" ) );
            add( new Isotope2( 42, 58, 99.907477, "0.0967" ) );
        }};
        put( 42, listForAtomicNumber42 );
        ArrayList<Isotope2> listForAtomicNumber43 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 43, 54, 96.906365, "0" ) );
            add( new Isotope2( 43, 55, 97.907216, "0" ) );
            add( new Isotope2( 43, 56, 98.9062547, "0" ) );
        }};
        put( 43, listForAtomicNumber43 );
        ArrayList<Isotope2> listForAtomicNumber44 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 44, 52, 95.907598, "0.0554" ) );
            add( new Isotope2( 44, 54, 97.905287, "0.0187" ) );
            add( new Isotope2( 44, 55, 98.9059393, "0.1276" ) );
            add( new Isotope2( 44, 56, 99.9042195, "0.1260" ) );
            add( new Isotope2( 44, 57, 100.9055821, "0.1706" ) );
            add( new Isotope2( 44, 58, 101.9043493, "0.3155" ) );
            add( new Isotope2( 44, 60, 103.905433, "0.1862" ) );
        }};
        put( 44, listForAtomicNumber44 );
        ArrayList<Isotope2> listForAtomicNumber45 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 45, 58, 102.905504, "1.0000" ) );
        }};
        put( 45, listForAtomicNumber45 );
        ArrayList<Isotope2> listForAtomicNumber46 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 46, 56, 101.905609, "0.0102" ) );
            add( new Isotope2( 46, 58, 103.904036, "0.1114" ) );
            add( new Isotope2( 46, 59, 104.905085, "0.2233" ) );
            add( new Isotope2( 46, 60, 105.903486, "0.2733" ) );
            add( new Isotope2( 46, 62, 107.903892, "0.2646" ) );
            add( new Isotope2( 46, 64, 109.905153, "0.1172" ) );
        }};
        put( 46, listForAtomicNumber46 );
        ArrayList<Isotope2> listForAtomicNumber47 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 47, 60, 106.905097, "0.51839" ) );
            add( new Isotope2( 47, 62, 108.904752, "0.48161" ) );
        }};
        put( 47, listForAtomicNumber47 );
        ArrayList<Isotope2> listForAtomicNumber48 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 48, 58, 105.906459, "0.0125" ) );
            add( new Isotope2( 48, 60, 107.904184, "0.0089" ) );
            add( new Isotope2( 48, 62, 109.9030021, "0.1249" ) );
            add( new Isotope2( 48, 63, 110.9041781, "0.1280" ) );
            add( new Isotope2( 48, 64, 111.9027578, "0.2413" ) );
            add( new Isotope2( 48, 65, 112.9044017, "0.1222" ) );
            add( new Isotope2( 48, 66, 113.9033585, "0.2873" ) );
            add( new Isotope2( 48, 68, 115.904756, "0.0749" ) );
        }};
        put( 48, listForAtomicNumber48 );
        ArrayList<Isotope2> listForAtomicNumber49 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 49, 64, 112.904058, "0.0429" ) );
            add( new Isotope2( 49, 66, 114.903878, "0.9571" ) );
        }};
        put( 49, listForAtomicNumber49 );
        ArrayList<Isotope2> listForAtomicNumber50 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 50, 62, 111.904818, "0.0097" ) );
            add( new Isotope2( 50, 64, 113.902779, "0.0066" ) );
            add( new Isotope2( 50, 65, 114.903342, "0.0034" ) );
            add( new Isotope2( 50, 66, 115.901741, "0.1454" ) );
            add( new Isotope2( 50, 67, 116.902952, "0.0768" ) );
            add( new Isotope2( 50, 68, 117.901603, "0.2422" ) );
            add( new Isotope2( 50, 69, 118.903308, "0.0859" ) );
            add( new Isotope2( 50, 70, 119.9021947, "0.3258" ) );
            add( new Isotope2( 50, 72, 121.903439, "0.0463" ) );
            add( new Isotope2( 50, 74, 123.9052739, "0.0579" ) );
        }};
        put( 50, listForAtomicNumber50 );
        ArrayList<Isotope2> listForAtomicNumber51 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 51, 70, 120.9038157, "0.5721" ) );
            add( new Isotope2( 51, 72, 122.904214, "0.4279" ) );
        }};
        put( 51, listForAtomicNumber51 );
        ArrayList<Isotope2> listForAtomicNumber52 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 52, 68, 119.90402, "0.0009" ) );
            add( new Isotope2( 52, 70, 121.9030439, "0.0255" ) );
            add( new Isotope2( 52, 71, 122.90427, "0.0089" ) );
            add( new Isotope2( 52, 72, 123.9028179, "0.0474" ) );
            add( new Isotope2( 52, 73, 124.9044307, "0.0707" ) );
            add( new Isotope2( 52, 74, 125.9033117, "0.1884" ) );
            add( new Isotope2( 52, 76, 127.9044631, "0.3174" ) );
            add( new Isotope2( 52, 78, 129.9062244, "0.3408" ) );
        }};
        put( 52, listForAtomicNumber52 );
        ArrayList<Isotope2> listForAtomicNumber53 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 53, 74, 126.904473, "1.0000" ) );
        }};
        put( 53, listForAtomicNumber53 );
        ArrayList<Isotope2> listForAtomicNumber54 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 54, 70, 123.905893, "0.000952" ) );
            add( new Isotope2( 54, 72, 125.904274, "0.000890" ) );
            add( new Isotope2( 54, 74, 127.9035313, "0.019102" ) );
            add( new Isotope2( 54, 75, 128.9047794, "0.264006" ) );
            add( new Isotope2( 54, 76, 129.903508, "0.040710" ) );
            add( new Isotope2( 54, 77, 130.9050824, "0.212324" ) );
            add( new Isotope2( 54, 78, 131.9041535, "0.269086" ) );
            add( new Isotope2( 54, 80, 133.9053945, "0.104357" ) );
            add( new Isotope2( 54, 82, 135.907219, "0.088573" ) );
        }};
        put( 54, listForAtomicNumber54 );
        ArrayList<Isotope2> listForAtomicNumber55 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 55, 78, 132.905451933, "1.0000" ) );
        }};
        put( 55, listForAtomicNumber55 );
        ArrayList<Isotope2> listForAtomicNumber56 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 56, 74, 129.9063208, "0.00106" ) );
            add( new Isotope2( 56, 76, 131.9050613, "0.00101" ) );
            add( new Isotope2( 56, 78, 133.9045084, "0.02417" ) );
            add( new Isotope2( 56, 79, 134.9056886, "0.06592" ) );
            add( new Isotope2( 56, 80, 135.9045759, "0.07854" ) );
            add( new Isotope2( 56, 81, 136.9058274, "0.11232" ) );
            add( new Isotope2( 56, 82, 137.9052472, "0.71698" ) );
        }};
        put( 56, listForAtomicNumber56 );
        ArrayList<Isotope2> listForAtomicNumber57 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 57, 81, 137.907112, "0.00090" ) );
            add( new Isotope2( 57, 82, 138.9063533, "0.99910" ) );
        }};
        put( 57, listForAtomicNumber57 );
        ArrayList<Isotope2> listForAtomicNumber58 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 58, 78, 135.907172, "0.00185" ) );
            add( new Isotope2( 58, 80, 137.905991, "0.00251" ) );
            add( new Isotope2( 58, 82, 139.9054387, "0.88450" ) );
            add( new Isotope2( 58, 84, 141.909244, "0.11114" ) );
        }};
        put( 58, listForAtomicNumber58 );
        ArrayList<Isotope2> listForAtomicNumber59 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 59, 82, 140.9076528, "1.0000" ) );
        }};
        put( 59, listForAtomicNumber59 );
        ArrayList<Isotope2> listForAtomicNumber60 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 60, 82, 141.9077233, "0.272" ) );
            add( new Isotope2( 60, 83, 142.9098143, "0.122" ) );
            add( new Isotope2( 60, 84, 143.9100873, "0.238" ) );
            add( new Isotope2( 60, 85, 144.9125736, "0.083" ) );
            add( new Isotope2( 60, 86, 145.9131169, "0.172" ) );
            add( new Isotope2( 60, 88, 147.916893, "0.057" ) );
            add( new Isotope2( 60, 90, 149.920891, "0.056" ) );
        }};
        put( 60, listForAtomicNumber60 );
        ArrayList<Isotope2> listForAtomicNumber61 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 61, 84, 144.912749, "0" ) );
            add( new Isotope2( 61, 86, 146.9151385, "0" ) );
        }};
        put( 61, listForAtomicNumber61 );
        ArrayList<Isotope2> listForAtomicNumber62 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 62, 82, 143.911999, "0.0307" ) );
            add( new Isotope2( 62, 85, 146.9148979, "0.1499" ) );
            add( new Isotope2( 62, 86, 147.9148227, "0.1124" ) );
            add( new Isotope2( 62, 87, 148.9171847, "0.1382" ) );
            add( new Isotope2( 62, 88, 149.9172755, "0.0738" ) );
            add( new Isotope2( 62, 90, 151.9197324, "0.2675" ) );
            add( new Isotope2( 62, 92, 153.9222093, "0.2275" ) );
        }};
        put( 62, listForAtomicNumber62 );
        ArrayList<Isotope2> listForAtomicNumber63 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 63, 88, 150.9198502, "0.4781" ) );
            add( new Isotope2( 63, 90, 152.9212303, "0.5219" ) );
        }};
        put( 63, listForAtomicNumber63 );
        ArrayList<Isotope2> listForAtomicNumber64 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 64, 88, 151.919791, "0.0020" ) );
            add( new Isotope2( 64, 90, 153.9208656, "0.0218" ) );
            add( new Isotope2( 64, 91, 154.922622, "0.1480" ) );
            add( new Isotope2( 64, 92, 155.9221227, "0.2047" ) );
            add( new Isotope2( 64, 93, 156.9239601, "0.1565" ) );
            add( new Isotope2( 64, 94, 157.9241039, "0.2484" ) );
            add( new Isotope2( 64, 96, 159.9270541, "0.2186" ) );
        }};
        put( 64, listForAtomicNumber64 );
        ArrayList<Isotope2> listForAtomicNumber65 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 65, 94, 158.9253468, "1.0000" ) );
        }};
        put( 65, listForAtomicNumber65 );
        ArrayList<Isotope2> listForAtomicNumber66 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 66, 90, 155.924283, "0.00056" ) );
            add( new Isotope2( 66, 92, 157.924409, "0.00095" ) );
            add( new Isotope2( 66, 94, 159.9251975, "0.02329" ) );
            add( new Isotope2( 66, 95, 160.9269334, "0.18889" ) );
            add( new Isotope2( 66, 96, 161.9267984, "0.25475" ) );
            add( new Isotope2( 66, 97, 162.9287312, "0.24896" ) );
            add( new Isotope2( 66, 98, 163.9291748, "0.28260" ) );
        }};
        put( 66, listForAtomicNumber66 );
        ArrayList<Isotope2> listForAtomicNumber67 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 67, 98, 164.9303221, "1.0000" ) );
        }};
        put( 67, listForAtomicNumber67 );
        ArrayList<Isotope2> listForAtomicNumber68 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 68, 94, 161.928778, "0.00139" ) );
            add( new Isotope2( 68, 96, 163.9292, "0.01601" ) );
            add( new Isotope2( 68, 98, 165.9302931, "0.33503" ) );
            add( new Isotope2( 68, 99, 166.9320482, "0.22869" ) );
            add( new Isotope2( 68, 100, 167.9323702, "0.26978" ) );
            add( new Isotope2( 68, 102, 169.9354643, "0.14910" ) );
        }};
        put( 68, listForAtomicNumber68 );
        ArrayList<Isotope2> listForAtomicNumber69 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 69, 100, 168.9342133, "1.0000" ) );
        }};
        put( 69, listForAtomicNumber69 );
        ArrayList<Isotope2> listForAtomicNumber70 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 70, 98, 167.933897, "0.0013" ) );
            add( new Isotope2( 70, 100, 169.9347618, "0.0304" ) );
            add( new Isotope2( 70, 101, 170.9363258, "0.1428" ) );
            add( new Isotope2( 70, 102, 171.9363815, "0.2183" ) );
            add( new Isotope2( 70, 103, 172.9382108, "0.1613" ) );
            add( new Isotope2( 70, 104, 173.9388621, "0.3183" ) );
            add( new Isotope2( 70, 106, 175.9425717, "0.1276" ) );
        }};
        put( 70, listForAtomicNumber70 );
        ArrayList<Isotope2> listForAtomicNumber71 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 71, 104, 174.9407718, "0.9741" ) );
            add( new Isotope2( 71, 105, 175.9426863, "0.0259" ) );
        }};
        put( 71, listForAtomicNumber71 );
        ArrayList<Isotope2> listForAtomicNumber72 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 72, 102, 173.940046, "0.0016" ) );
            add( new Isotope2( 72, 104, 175.9414086, "0.0526" ) );
            add( new Isotope2( 72, 105, 176.9432207, "0.1860" ) );
            add( new Isotope2( 72, 106, 177.9436988, "0.2728" ) );
            add( new Isotope2( 72, 107, 178.9458161, "0.1362" ) );
            add( new Isotope2( 72, 108, 179.94655, "0.3508" ) );
        }};
        put( 72, listForAtomicNumber72 );
        ArrayList<Isotope2> listForAtomicNumber73 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 73, 107, 179.9474648, "0.00012" ) );
            add( new Isotope2( 73, 108, 180.9479958, "0.99988" ) );
        }};
        put( 73, listForAtomicNumber73 );
        ArrayList<Isotope2> listForAtomicNumber74 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 74, 106, 179.946704, "0.0012" ) );
            add( new Isotope2( 74, 108, 181.9482042, "0.2650" ) );
            add( new Isotope2( 74, 109, 182.950223, "0.1431" ) );
            add( new Isotope2( 74, 110, 183.9509312, "0.3064" ) );
            add( new Isotope2( 74, 112, 185.9543641, "0.2843" ) );
        }};
        put( 74, listForAtomicNumber74 );
        ArrayList<Isotope2> listForAtomicNumber75 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 75, 110, 184.952955, "0.3740" ) );
            add( new Isotope2( 75, 112, 186.9557531, "0.6260" ) );
        }};
        put( 75, listForAtomicNumber75 );
        ArrayList<Isotope2> listForAtomicNumber76 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 76, 108, 183.9524891, "0.0002" ) );
            add( new Isotope2( 76, 110, 185.9538382, "0.0159" ) );
            add( new Isotope2( 76, 111, 186.9557505, "0.0196" ) );
            add( new Isotope2( 76, 112, 187.9558382, "0.1324" ) );
            add( new Isotope2( 76, 113, 188.9581475, "0.1615" ) );
            add( new Isotope2( 76, 114, 189.958447, "0.2626" ) );
            add( new Isotope2( 76, 116, 191.9614807, "0.4078" ) );
        }};
        put( 76, listForAtomicNumber76 );
        ArrayList<Isotope2> listForAtomicNumber77 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 77, 114, 190.960594, "0.373" ) );
            add( new Isotope2( 77, 116, 192.9629264, "0.627" ) );
        }};
        put( 77, listForAtomicNumber77 );
        ArrayList<Isotope2> listForAtomicNumber78 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 78, 112, 189.959932, "0.00014" ) );
            add( new Isotope2( 78, 114, 191.961038, "0.00782" ) );
            add( new Isotope2( 78, 116, 193.9626803, "0.32967" ) );
            add( new Isotope2( 78, 117, 194.9647911, "0.33832" ) );
            add( new Isotope2( 78, 118, 195.9649515, "0.25242" ) );
            add( new Isotope2( 78, 120, 197.967893, "0.07163" ) );
        }};
        put( 78, listForAtomicNumber78 );
        ArrayList<Isotope2> listForAtomicNumber79 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 79, 118, 196.9665687, "1.0000" ) );
        }};
        put( 79, listForAtomicNumber79 );
        ArrayList<Isotope2> listForAtomicNumber80 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 80, 116, 195.965833, "0.0015" ) );
            add( new Isotope2( 80, 118, 197.966769, "0.0997" ) );
            add( new Isotope2( 80, 119, 198.9682799, "0.1687" ) );
            add( new Isotope2( 80, 120, 199.968326, "0.2310" ) );
            add( new Isotope2( 80, 121, 200.9703023, "0.1318" ) );
            add( new Isotope2( 80, 122, 201.970643, "0.2986" ) );
            add( new Isotope2( 80, 124, 203.9734939, "0.0687" ) );
        }};
        put( 80, listForAtomicNumber80 );
        ArrayList<Isotope2> listForAtomicNumber81 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 81, 122, 202.9723442, "0.2952" ) );
            add( new Isotope2( 81, 124, 204.9744275, "0.7048" ) );
        }};
        put( 81, listForAtomicNumber81 );
        ArrayList<Isotope2> listForAtomicNumber82 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 82, 122, 203.9730436, "0.014" ) );
            add( new Isotope2( 82, 124, 205.9744653, "0.241" ) );
            add( new Isotope2( 82, 125, 206.9758969, "0.221" ) );
            add( new Isotope2( 82, 126, 207.9766521, "0.524" ) );
        }};
        put( 82, listForAtomicNumber82 );
        ArrayList<Isotope2> listForAtomicNumber83 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 83, 126, 208.9803987, "1.0000" ) );
        }};
        put( 83, listForAtomicNumber83 );
        ArrayList<Isotope2> listForAtomicNumber84 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 84, 125, 208.9824304, "0" ) );
            add( new Isotope2( 84, 126, 209.9828737, "0" ) );
        }};
        put( 84, listForAtomicNumber84 );
        ArrayList<Isotope2> listForAtomicNumber85 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 85, 125, 209.987148, "0" ) );
            add( new Isotope2( 85, 126, 210.9874963, "0" ) );
        }};
        put( 85, listForAtomicNumber85 );
        ArrayList<Isotope2> listForAtomicNumber86 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 86, 125, 210.990601, "0" ) );
            add( new Isotope2( 86, 134, 220.011394, "0" ) );
            add( new Isotope2( 86, 136, 222.0175777, "0" ) );
        }};
        put( 86, listForAtomicNumber86 );
        ArrayList<Isotope2> listForAtomicNumber87 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 87, 136, 223.0197359, "0" ) );
        }};
        put( 87, listForAtomicNumber87 );
        ArrayList<Isotope2> listForAtomicNumber88 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 88, 135, 223.0185022, "0" ) );
            add( new Isotope2( 88, 136, 224.0202118, "0" ) );
            add( new Isotope2( 88, 138, 226.0254098, "0" ) );
            add( new Isotope2( 88, 140, 228.0310703, "0" ) );
        }};
        put( 88, listForAtomicNumber88 );
        ArrayList<Isotope2> listForAtomicNumber89 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 89, 138, 227.0277521, "0" ) );
        }};
        put( 89, listForAtomicNumber89 );
        ArrayList<Isotope2> listForAtomicNumber90 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 90, 140, 230.0331338, "0" ) );
            add( new Isotope2( 90, 142, 232.0380553, "1.0000" ) );
        }};
        put( 90, listForAtomicNumber90 );
        ArrayList<Isotope2> listForAtomicNumber91 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 91, 140, 231.035884, "1.0000" ) );
        }};
        put( 91, listForAtomicNumber91 );
        ArrayList<Isotope2> listForAtomicNumber92 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 92, 141, 233.0396352, "0" ) );
            add( new Isotope2( 92, 142, 234.0409521, "0.000054" ) );
            add( new Isotope2( 92, 143, 235.0439299, "0.007204" ) );
            add( new Isotope2( 92, 144, 236.045568, "0" ) );
            add( new Isotope2( 92, 146, 238.0507882, "0.992742" ) );
        }};
        put( 92, listForAtomicNumber92 );
        ArrayList<Isotope2> listForAtomicNumber93 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 93, 143, 236.04657, "0" ) );
            add( new Isotope2( 93, 144, 237.0481734, "0" ) );
        }};
        put( 93, listForAtomicNumber93 );
        ArrayList<Isotope2> listForAtomicNumber94 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 94, 144, 238.0495599, "0" ) );
            add( new Isotope2( 94, 145, 239.0521634, "0" ) );
            add( new Isotope2( 94, 146, 240.0538135, "0" ) );
            add( new Isotope2( 94, 147, 241.0568515, "0" ) );
            add( new Isotope2( 94, 148, 242.0587426, "0" ) );
            add( new Isotope2( 94, 150, 244.064204, "0" ) );
        }};
        put( 94, listForAtomicNumber94 );
        ArrayList<Isotope2> listForAtomicNumber95 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 95, 146, 241.0568291, "0" ) );
            add( new Isotope2( 95, 148, 243.0613811, "0" ) );
        }};
        put( 95, listForAtomicNumber95 );
        ArrayList<Isotope2> listForAtomicNumber96 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 96, 147, 243.0613891, "0" ) );
            add( new Isotope2( 96, 148, 244.0627526, "0" ) );
            add( new Isotope2( 96, 149, 245.0654912, "0" ) );
            add( new Isotope2( 96, 150, 246.0672237, "0" ) );
            add( new Isotope2( 96, 151, 247.070354, "0" ) );
            add( new Isotope2( 96, 152, 248.072349, "0" ) );
        }};
        put( 96, listForAtomicNumber96 );
        ArrayList<Isotope2> listForAtomicNumber97 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 97, 150, 247.070307, "0" ) );
            add( new Isotope2( 97, 152, 249.0749867, "0" ) );
        }};
        put( 97, listForAtomicNumber97 );
        ArrayList<Isotope2> listForAtomicNumber98 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 98, 151, 249.0748535, "0" ) );
            add( new Isotope2( 98, 152, 250.0764061, "0" ) );
            add( new Isotope2( 98, 153, 251.079587, "0" ) );
            add( new Isotope2( 98, 154, 252.081626, "0" ) );
        }};
        put( 98, listForAtomicNumber98 );
        ArrayList<Isotope2> listForAtomicNumber99 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 99, 153, 252.08298, "0" ) );
        }};
        put( 99, listForAtomicNumber99 );
        ArrayList<Isotope2> listForAtomicNumber100 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 100, 157, 257.095105, "0" ) );
        }};
        put( 100, listForAtomicNumber100 );
        ArrayList<Isotope2> listForAtomicNumber101 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 101, 157, 258.098431, "0" ) );
            add( new Isotope2( 101, 159, 260.10365, "0" ) );
        }};
        put( 101, listForAtomicNumber101 );
        ArrayList<Isotope2> listForAtomicNumber102 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 102, 157, 259.10103, "0" ) );
        }};
        put( 102, listForAtomicNumber102 );
        ArrayList<Isotope2> listForAtomicNumber103 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 103, 159, 262.10963, "0" ) );
        }};
        put( 103, listForAtomicNumber103 );
        ArrayList<Isotope2> listForAtomicNumber104 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 104, 161, 265.1167, "0" ) );
        }};
        put( 104, listForAtomicNumber104 );
        ArrayList<Isotope2> listForAtomicNumber105 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 105, 163, 268.12545, "0" ) );
        }};
        put( 105, listForAtomicNumber105 );
        ArrayList<Isotope2> listForAtomicNumber106 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 106, 165, 271.13347, "0" ) );
        }};
        put( 106, listForAtomicNumber106 );
        ArrayList<Isotope2> listForAtomicNumber107 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 107, 165, 272.13803, "0" ) );
        }};
        put( 107, listForAtomicNumber107 );
        ArrayList<Isotope2> listForAtomicNumber108 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 108, 162, 270.13465, "0" ) );
        }};
        put( 108, listForAtomicNumber108 );
        ArrayList<Isotope2> listForAtomicNumber109 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 109, 167, 276.15116, "0" ) );
        }};
        put( 109, listForAtomicNumber109 );
        ArrayList<Isotope2> listForAtomicNumber110 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 110, 171, 281.16206, "0" ) );
        }};
        put( 110, listForAtomicNumber110 );
        ArrayList<Isotope2> listForAtomicNumber111 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 111, 169, 280.16447, "0" ) );
        }};
        put( 111, listForAtomicNumber111 );
        ArrayList<Isotope2> listForAtomicNumber112 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 112, 173, 285.17411, "0" ) );
        }};
        put( 112, listForAtomicNumber112 );
        ArrayList<Isotope2> listForAtomicNumber113 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 113, 171, 284.17808, "0" ) );
        }};
        put( 113, listForAtomicNumber113 );
        ArrayList<Isotope2> listForAtomicNumber114 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 114, 175, 289.18728, "0" ) );
        }};
        put( 114, listForAtomicNumber114 );
        ArrayList<Isotope2> listForAtomicNumber115 = new ArrayList<Isotope2>() {{
            add( new Isotope2( 115, 173, 288.19249, "0" ) );
        }};
        put( 115, listForAtomicNumber115 );
    }};

    // This table
    private static final Map<Integer, Double> mapAtomicNumberToMass = new HashMap<Integer, Double>() {{
        // Automatically generated, see routines in this class.
        put( 1, 1.00794 );
        put( 2, 4.002602 );
        put( 3, 6.941 );
        put( 4, 9.012182 );
        put( 5, 10.811 );
        put( 6, 12.0107 );
        put( 7, 14.0067 );
        put( 8, 15.9994 );
        put( 9, 18.9984032 );
        put( 10, 20.1797 );
        put( 11, 22.98976928 );
        put( 12, 24.305 );
        put( 13, 26.9815386 );
        put( 14, 28.0855 );
        put( 15, 30.973762 );
        put( 16, 32.065 );
        put( 17, 35.453 );
        put( 18, 39.948 );
        put( 19, 39.0983 );
        put( 20, 40.078 );
        put( 21, 44.955912 );
        put( 22, 47.867 );
        put( 23, 50.9415 );
        put( 24, 51.9961 );
        put( 25, 54.938045 );
        put( 26, 55.845 );
        put( 27, 58.933195 );
        put( 28, 58.6934 );
        put( 29, 63.546 );
        put( 30, 65.38 );
        put( 31, 69.723 );
        put( 32, 72.64 );
        put( 33, 74.9216 );
        put( 34, 78.96 );
        put( 35, 79.904 );
        put( 36, 83.798 );
        put( 37, 85.4678 );
        put( 38, 87.62 );
        put( 39, 88.90585 );
        put( 40, 91.224 );
        put( 41, 92.90638 );
        put( 42, 95.96 );
        put( 43, 98.0 );
        put( 44, 101.07 );
        put( 45, 102.9055 );
        put( 46, 106.42 );
        put( 47, 107.8682 );
        put( 48, 112.411 );
        put( 49, 114.818 );
        put( 50, 118.71 );
        put( 51, 121.76 );
        put( 52, 127.6 );
        put( 53, 126.90447 );
        put( 54, 131.293 );
        put( 55, 132.9054519 );
        put( 56, 137.327 );
        put( 57, 138.90547 );
        put( 58, 140.116 );
        put( 59, 140.90765 );
        put( 60, 144.242 );
        put( 61, 145.0 );
        put( 62, 150.36 );
        put( 63, 151.964 );
        put( 64, 157.25 );
        put( 65, 158.92535 );
        put( 66, 162.5 );
        put( 67, 164.93032 );
        put( 68, 167.259 );
        put( 69, 168.93421 );
        put( 70, 173.054 );
        put( 71, 174.9668 );
        put( 72, 178.49 );
        put( 73, 180.94788 );
        put( 74, 183.84 );
        put( 75, 186.207 );
        put( 76, 190.23 );
        put( 77, 192.217 );
        put( 78, 195.084 );
        put( 79, 196.966569 );
        put( 80, 200.59 );
        put( 81, 204.3833 );
        put( 82, 207.2 );
        put( 83, 208.9804 );
    }};

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
        double atomicMass = 0;
        ArrayList<Isotope2> isotopeList = ISOTOPE_INFORMATION_TABLE.get( atom.getNumProtons() );
        if ( isotopeList != null && isotopeList.size() > 0 ) {
            for ( Isotope2 isotope : isotopeList ) {
                if ( isotope.neutronCount == atom.getNumNeutrons() ) {
                    atomicMass = isotope.atomicMass;
                    break;
                }
            }
        }
        return atomicMass;
    }

    /**
     * Get the proportion of this particular isotope on present-day earth
     * versus all isotopes for this element.
     *
     * @return - A value from 0 to 1 representing the natural abundance
     *         proportion.
     */
    public static double getNaturalAbundance( IAtom atom ) {
        return getNaturalAbundancePrecisionDecimal( atom ).getPreciseValue();
    }

    /**
     * Get the proportion of this particular isotope on present-day earth
     * versus all isotopes for this element.
     *
     * @param atom
     * @return - A value from 0 to 1 representing the natural abundance
     *         proportion.
     */
    public static PrecisionDecimal getNaturalAbundancePrecisionDecimal( IAtom atom ) {
        PrecisionDecimal precisionDecimal = new PrecisionDecimal( 0, 10 );//Default to 0 (with high precision) in case no match is found
        ArrayList<Isotope2> isotopeList = ISOTOPE_INFORMATION_TABLE.get( atom.getNumProtons() );
        if ( isotopeList != null ) {
            for ( Isotope2 isotope : isotopeList ) {
                if ( atom.getNumNeutrons() == isotope.neutronCount ) {
                    // Found the matching isotope.
                    precisionDecimal = isotope.abundance;
                    break;
                }
            }
        }
        return precisionDecimal;
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

    private static class Isotope2 {
        public final int protonCount;
        public final int neutronCount;
        public final double atomicMass;
        public final PrecisionDecimal abundance;

        public Isotope2( int protonCount, int neutronCount, double atomicMass, String abundance ) {
            this.protonCount = protonCount;
            this.neutronCount = neutronCount;
            this.atomicMass = atomicMass;
            //Count the number of digits after the decimal point
            int precision = abundance.indexOf( '.' )>=0?abundance.substring( abundance.indexOf( '.' )+1 ).length(): 0;
            this.abundance = new PrecisionDecimal( Double.parseDouble( abundance ), precision );
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            Isotope2 isotope = (Isotope2) o;

            if ( protonCount != isotope.protonCount ) {
                return false;
            }
            if ( neutronCount != isotope.neutronCount ) {
                return false;
            }

            return true;
        }
    }

    /**
     * Get the configuration of the most abundant isotope of the element with
     * with given atomic number.  The returned atom will be neutral.
     *
     * @param atomicNumber
     */
    public static ImmutableAtom getMostCommonIsotope( int atomicNumber ) {
        ArrayList<Isotope2> isotopeList = new ArrayList<Isotope2>( ISOTOPE_INFORMATION_TABLE.get( atomicNumber ) );
        if ( isotopeList.size() == 0 ) {
            System.err.println( "Error - No isotope information found for atomic number " + atomicNumber );
            return new ImmutableAtom( 0, 0, 0 );
        }
        else {
            Collections.sort( isotopeList, new Comparator<Isotope2>() {
                public int compare( Isotope2 o1, Isotope2 o2 ) {
                    return new Double( o2.abundance.getPreciseValue() ).compareTo( o1.abundance.getPreciseValue() );
                }
            } );
        }
        Isotope2 isotope = isotopeList.get( 0 );
        return new ImmutableAtom( isotope.protonCount, isotope.neutronCount, isotope.protonCount );
    }

    /**
     * Get a list of all stable isotopes for the given atomic weight.
     *
     * @param atomicNumber
     * @return
     */
    public static ArrayList<ImmutableAtom> getAllIsotopes( int atomicNumber ) {
        ArrayList<ImmutableAtom> isotopeList = new ArrayList<ImmutableAtom>();
        ArrayList<Isotope2> isotopeInfoList = new ArrayList<Isotope2>( ISOTOPE_INFORMATION_TABLE.get( atomicNumber ) );
        for ( Isotope2 isotope : isotopeInfoList ) {
            isotopeList.add( new ImmutableAtom( isotope.protonCount, isotope.neutronCount, isotope.protonCount ) );
        }
        return isotopeList;
    }

    /**
     * Get a list of all isotopes that are considered stable.  This is needed
     * because the complete list of isotopes used by this class includes some
     * that exist on earth but are not stable, such as carbon-14.
     *
     * @param atomicNumber
     * @return
     */
    public static ArrayList<ImmutableAtom> getStableIsotopes( int atomicNumber ) {
        ArrayList<ImmutableAtom> isotopeList = getAllIsotopes( atomicNumber );
        ArrayList<ImmutableAtom> stableIsotopeList = new ArrayList<ImmutableAtom>( isotopeList );
        for ( ImmutableAtom isotope : isotopeList ){
            if ( !isStable(isotope) ){
                stableIsotopeList.remove( isotope );
            }
        }
        return stableIsotopeList;
    }

    public static double getStandardAtomicMass( int atomicNumber ){
        if ( mapAtomicNumberToMass.containsKey( atomicNumber )){
            return mapAtomicNumberToMass.get( atomicNumber );
        }
        else{
            System.out.println("Warning: No standard atomic mass available for atomic number " + atomicNumber + ", returning zero." );
            return 0;
        }
    }

    /**
     * Use to regenerate element symbol table if needed.  Run from "main" if
     * needed.
     */
    private static void generateSymbolTable() {
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

    /**
     * Generate a data structure from the Isotope table that is in string
     * format.  Rename to "main" to use.
     */
    private static void generateIsotopeInfoTable() {

        // Break the overall string into lines.
        String[] lines = ISOTOPE_INFORMATION_TABLE_STR.split( "\n" );

        System.out.println("// Automatically generated, see routines in this class.");

        // Process each line.
        int currentAtomicNumber = 0;
        for ( String line : lines ) {
            String[] dataElements = line.split( "," );
            if (dataElements[0].length() != 0){
                if (currentAtomicNumber != 0 ){
                    // Finish off the structure for the previous atomic number.
                    System.out.println("}};");
                    System.out.println("put( " + currentAtomicNumber + ", listForAtomicNumber" + currentAtomicNumber + " );");
                }
                currentAtomicNumber = Integer.parseInt( dataElements[0] );
                // Start the list of isotopes for this atomic number.
                System.out.println("ArrayList<Isotope2> listForAtomicNumber" + currentAtomicNumber + "= new ArrayList<Isotope2>(){{");
            }
            // Add the individual entry for this isotope.
            int numNeutrons = Integer.parseInt( dataElements[ 2 ] ) - currentAtomicNumber;
            double atomicWeight = Double.parseDouble( dataElements[ 3 ] );
            double abundance = dataElements.length >= 5 ? Double.parseDouble( dataElements[ 4 ] ) : 0;
            System.out.println("   add( new Isotope2( " + currentAtomicNumber + ", " + numNeutrons + ", " + atomicWeight + ", " + abundance + " ) );" );
        }
        System.out.println( "}};\n" +
                            "        put( 115, listForAtomicNumber115 );" );//Add the suffix so we don't have to do it manually
    }

    public static class GenerateIsotopeInfoTable {
        public static void main( String[] args ) {
            AtomIdentifier.generateIsotopeInfoTable();
        }
    }

    /**
     * Generate a data structure that maps atomic number to average atomic
     * mass.  Rename to "main" to use.
     */
    private static void generateMapOfAtomicNumberToMass() {

        // Break the overall string into lines.
        String[] lines = STRING_MAP_ATOMIC_NUM_TO_STD_MASS.split( "\n" );

        System.out.println( "// Automatically generated, see routines in this class." );

        // Process each line.
        int currentAtomicNumber = 0;
        for ( String line : lines ) {
            String[] dataElements = line.split( "," );
            currentAtomicNumber = Integer.parseInt( dataElements[0] );
            // Start the list of isotopes for this atomic number.
            System.out.println( "put( " + currentAtomicNumber + ", " + Double.parseDouble( dataElements[1] ) + " );" );
        }
    }

    public static void main( String[] args ) {
        // Uncomment the needed method if you need to regenerate one of the tables.
//        generateIsotopeInfoTable();
        generateMapOfAtomicNumberToMass();
//        generateSymbolTable();
    }
}
