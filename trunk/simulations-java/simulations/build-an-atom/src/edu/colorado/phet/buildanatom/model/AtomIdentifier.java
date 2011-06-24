/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.common.phetcommon.util.PrecisionDecimal;

/**
 * This class identifies and classifies atoms based on their configuration of
 * protons, neutrons, and electrons.  It is common functionality that is used
 * by a number of different classes that represent atoms, which is why it was
 * separated out.  The intent is that this be used by the atom classes
 * themselves so that clients of the atom classes can get the needed
 * information from directly from them, rather than using this class as a sort
 * of "3rd party expert".
 * <p/>
 * Information used within this class was gathered from a number of sources,
 * but the bulk came from the National Institute of Standards Technology
 * (NIST) web site.  The original data is preserved in a set of data
 * structures, and the various information is pulled from them either at run
 * time or is pre-processed into a more rapidly accessible format at init
 * time.
 *
 * @author John Blanco
 */
public class AtomIdentifier {

    // An arbitrary value used to signify a 'trace' abundance, meaning that
    // a very small amount of this isotope is present on Earth.
    private static final double TRACE_ABUNDANCE = 0.000000000001;

    // This data structure lists the isotopes that are considered stable for
    // the purposes of this simulation.  This means that their half life is
    // less than the age of the universe.  This table was put together from
    // information gathered at the NIST web site.
    private static final List<IsotopeKey> STABLE_ISOTOPES = new ArrayList<IsotopeKey>() {
        {
            //H
            add( new IsotopeKey( 1, 0 ) );
            add( new IsotopeKey( 1, 1 ) );
            //He
            add( new IsotopeKey( 2, 1 ) );
            add( new IsotopeKey( 2, 2 ) );
            //Li
            add( new IsotopeKey( 3, 3 ) );
            add( new IsotopeKey( 3, 4 ) );
            //Be
            add( new IsotopeKey( 4, 5 ) );
            //B
            add( new IsotopeKey( 5, 5 ) );
            add( new IsotopeKey( 5, 6 ) );
            //C
            add( new IsotopeKey( 6, 6 ) );
            add( new IsotopeKey( 6, 7 ) );
            //N
            add( new IsotopeKey( 7, 7 ) );
            add( new IsotopeKey( 7, 8 ) );
            //O
            add( new IsotopeKey( 8, 8 ) );
            add( new IsotopeKey( 8, 9 ) );
            add( new IsotopeKey( 8, 10 ) );
            //F
            add( new IsotopeKey( 9, 10 ) );
            //Ne
            add( new IsotopeKey( 10, 10 ) );
            add( new IsotopeKey( 10, 11 ) );
            add( new IsotopeKey( 10, 12 ) );
            // Na
            add( new IsotopeKey( 11, 12 ) );
            // Mg
            add( new IsotopeKey( 12, 12 ) );
            add( new IsotopeKey( 12, 13 ) );
            add( new IsotopeKey( 12, 14 ) );
            // Al
            add( new IsotopeKey( 13, 14 ) );
            // Si
            add( new IsotopeKey( 14, 14 ) );
            add( new IsotopeKey( 14, 15 ) );
            add( new IsotopeKey( 14, 16 ) );
            // P
            add( new IsotopeKey( 15, 16 ) );
            // S
            add( new IsotopeKey( 16, 16 ) );
            add( new IsotopeKey( 16, 17 ) );
            add( new IsotopeKey( 16, 18 ) );
            add( new IsotopeKey( 16, 20 ) );
            // Cl
            add( new IsotopeKey( 17, 18 ) );
            add( new IsotopeKey( 17, 20 ) );
            // Ar
            add( new IsotopeKey( 18, 18 ) );
            add( new IsotopeKey( 18, 20 ) );
            add( new IsotopeKey( 18, 22 ) );
        }
    };

    // Table of element symbols, indexed by the atomic number.  Note that this
    // is not internationalizable, a decision made by the chemistry team.
    private static final String[] ELEMENT_SYMBOL_TABLE = {
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

    // CSV-formatted table containing information about various attributes of
    // isotopes.  This was obtained from the National Institute of Standards and
    // Technology (NIST) at the URL
    //
    // http://physics.nist.gov/cgi-bin/Compositions/stand_alone.pl?ele=&ascii=html&isotype=some
    //
    // ...though some minor manual post-processing was necessary to get it
    // into the format below.  This is converted at init time into a data
    // structure that provides faster access to this information.
    private static final String RAW_ISOTOPE_INFORMATION_TABLE_STR =
            // Format:
            // Atomic number (empty if same as previous), Symbol, mass number, atomic mass, abundance.
            "1,H,1,1.00782503207,0.999885\n" +
            ",D,2,2.0141017778,0.000115\n" +
            ",T,3,3.0160492777," + TRACE_ABUNDANCE + "\n" +    // Use trace abundance, since Wikipedia just says "trace" and the NIST table contained it but didn't state abundance.
            "2,He,3,3.0160293191,0.00000134\n" +
            ",,4,4.00260325415,0.99999866\n" +
            "3,Li,6,6.015122795,0.0759\n" +
            ",,7,7.01600455,0.9241\n" +
            "4,Be,9,9.0121822,1.0000\n" +
            "5,B,10,10.0129370,0.199\n" +
            ",,11,11.0093054,0.801\n" +
            "6,C,12,12.0000000,0.9893\n" +
            ",,13,13.0033548378,0.0107\n" +
            ",,14,14.003241989," + TRACE_ABUNDANCE + "\n" +     // Use trace abundance, since Wikipedia just says "trace" and the NIST table contained it but didn't state abundance.
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
    // and subsequently post-processed to remove unneeded data:
    //
    // http://physics.nist.gov/cgi-bin/Compositions/stand_alone.pl?ele=&ascii=ascii2&isotype=some
    //
    // This is processed at init time to create a table from which this data
    // can be quickly obtained.
    private static final String MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS_STRING =
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


    // Aggregated data structure that contains all the information needed
    // about the various isotopes used within this simulation.
    private static final Map<IsotopeKey, IsotopeInfo> ISOTOPE_INFO_MAP = generateIsotopeInfoTable();

    // This table maps the atomic model to the standard atomic mass, also
    // known as the atomic weight.
    private static final Map<Integer, String> MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS = generateMapOfAtomicNumberToMass();

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
        if ( protonCount == 0 ) {
            return BuildAnAtomStrings.ELEMENT_NONE_NAME;
        }
        else {
            for ( IsotopeKey key : ISOTOPE_INFO_MAP.keySet() ) {
                if ( key.getNumProtons() == protonCount ) {
                    return ISOTOPE_INFO_MAP.get( key ).getElementName();
                }
            }
        }
        System.err.println( "Error: No element found for proton count = " + protonCount );
        return "Unknown";
    }

    public static boolean isStable( IAtom atom ) {
        IsotopeKey key = new IsotopeKey( atom );
        return ISOTOPE_INFO_MAP.containsKey( key ) ? ISOTOPE_INFO_MAP.get( key ).isStable() : false;
    }

    public static double getAtomicMass( IAtom atom ) {
        IsotopeKey key = new IsotopeKey( atom );
        return ISOTOPE_INFO_MAP.containsKey( key ) ? ISOTOPE_INFO_MAP.get( key ).getAtomicMass() : 0;
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
        PrecisionDecimal defaultReturnValue = new PrecisionDecimal( 0, 10 ); //Default to 0 (with high precision) in case no match is found.
        IsotopeKey key = new IsotopeKey( atom );
        return ISOTOPE_INFO_MAP.containsKey( key ) ? ISOTOPE_INFO_MAP.get( key ).getAbundance() : defaultReturnValue;
    }

    /**
     * Get the configuration of the most abundant isotope of the element with
     * with given atomic number.  The returned atom will be neutral.
     *
     * @param atomicNumber
     */
    public static ImmutableAtom getMostCommonIsotope( int atomicNumber ) {
        Map<IsotopeKey, IsotopeInfo> matchingIsotopes = new HashMap<IsotopeKey, IsotopeInfo>();
        for ( IsotopeKey key : ISOTOPE_INFO_MAP.keySet() ) {
            if ( key.getNumProtons() == atomicNumber ) {
                matchingIsotopes.put( key, ISOTOPE_INFO_MAP.get( key ) );
            }
        }
        double maxAbundance = 0;
        IsotopeKey keyOfMostAbundantIsotope = new IsotopeKey( 0, 0 );
        for ( IsotopeKey key : matchingIsotopes.keySet() ) {
            if ( matchingIsotopes.get( key ).getAbundance().getPreciseValue() > maxAbundance ) {
                keyOfMostAbundantIsotope = key;
                maxAbundance = matchingIsotopes.get( key ).getAbundance().getPreciseValue();
            }
        }
        return new ImmutableAtom( keyOfMostAbundantIsotope.getNumProtons(), keyOfMostAbundantIsotope.getNumNeutrons(), keyOfMostAbundantIsotope.getNumProtons() );
    }

    /**
     * Get a list of all isotopes for the given atomic number.
     *
     * @param atomicNumber
     * @return
     */
    public static ArrayList<ImmutableAtom> getAllIsotopesOfElement( int atomicNumber ) {
        ArrayList<ImmutableAtom> isotopeList = new ArrayList<ImmutableAtom>();
        for ( IsotopeKey key : ISOTOPE_INFO_MAP.keySet() ) {
            if ( atomicNumber == key.getNumProtons() ) {
                isotopeList.add( new ImmutableAtom( key.getNumProtons(), key.getNumNeutrons(), key.getNumProtons() ) );
            }
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
    public static ArrayList<ImmutableAtom> getStableIsotopesOfElement( int atomicNumber ) {
        ArrayList<ImmutableAtom> isotopeList = getAllIsotopesOfElement( atomicNumber );
        ArrayList<ImmutableAtom> stableIsotopeList = new ArrayList<ImmutableAtom>();
        for ( ImmutableAtom isotope : isotopeList ) {
            if ( isStable( isotope ) ) {
                stableIsotopeList.add( isotope );
            }
        }
        return stableIsotopeList;
    }

    public static double getStandardAtomicMass( int atomicNumber ) {
        if ( MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS.containsKey( atomicNumber ) ) {
            return Double.parseDouble( MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS.get( atomicNumber ) );
        }
        else {
            System.out.println( "Warning: No standard atomic mass available for atomic number " + atomicNumber + ", returning zero." );
            return 0;
        }
    }

    /**
     * Get a "precision decimal" that contains the value of the average atomic
     * mass as well as an integer that represents the number of decimal digits
     * to which the value is known.
     *
     * @param atomicNumber
     * @return
     */
    public static PrecisionDecimal getStandardAtomicMassPrecisionDecimal( int atomicNumber ) {
        PrecisionDecimal precisionDecimal = new PrecisionDecimal( 0, 5 ); // Default value.
        if ( MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS.containsKey( atomicNumber ) ) {
            String massString = MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS.get( atomicNumber );
            double value = Double.parseDouble( massString );
            int precision = massString.indexOf( '.' ) >= 0 ? massString.substring( massString.indexOf( '.' ) + 1 ).length() : 0;
            precisionDecimal = new PrecisionDecimal( value, precision );
        }
        return precisionDecimal;
    }

    /**
     * Generate a data structure that maps atomic number to average atomic
     * mass.  This prints to the console, with the intent being that the
     * output is pasted back into this file.  Call this from the "main"
     * function in order to regenerate the table.
     */
    private static Map<Integer, String> generateMapOfAtomicNumberToMass() {
        // Break the overall string into lines.
        String[] lines = MAP_ATOMIC_NUMBER_TO_AVERAGE_MASS_STRING.split( "\n" );
        // Process each line to extract atomic number and a string for the
        // mass.  This is done as a string to preserve the precision.
        Map<Integer, String> mapOfAtomicNumberToMass = new HashMap<Integer, String>();
        int currentAtomicNumber = 0;
        for ( String line : lines ) {
            String[] dataElements = line.split( "," );
            mapOfAtomicNumberToMass.put( Integer.parseInt( dataElements[0] ), dataElements[1].trim() );
        }
        return mapOfAtomicNumberToMass;
    }

    /**
     * Pull a bunch of the information in the static tables above into a
     * single data structure that can provide much of the information needed
     * about the various isotopes depicted in this simulation.
     */
    private static Map<IsotopeKey, IsotopeInfo> generateIsotopeInfoTable() {
        long entryTime = System.currentTimeMillis();
        System.out.println( "Entry, time = " + entryTime );
        Map<IsotopeKey, IsotopeInfo> isotopeInfoMap = new HashMap<IsotopeKey, IsotopeInfo>();

        // Create the map by going through the raw information table and
        // adding an entry for each entry in the raw table.
        String[] lines = RAW_ISOTOPE_INFORMATION_TABLE_STR.split( "\n" );
        int currentAtomicNumber = 0;
        String currentSymbol = "";
        for ( String line : lines ) {
            final String[] dataElements = line.split( "," );
            if ( dataElements[0].length() != 0 ) {
                currentAtomicNumber = Integer.parseInt( dataElements[0] );
                currentSymbol = dataElements[1]; // This ignores isotope names, which is what we want.
            }
            int numNeutrons = Integer.parseInt( dataElements[2] ) - currentAtomicNumber;
            IsotopeKey isotopeKey = new IsotopeKey( currentAtomicNumber, Integer.parseInt( dataElements[2] ) - currentAtomicNumber );
            IsotopeInfo isotopeInfo = new IsotopeInfo();
            isotopeInfo.setAtomicMass( Double.parseDouble( dataElements[3] ) );
            String abundanceString = dataElements.length >= 5 ? dataElements[4] : "0";
            int abundancePrecision = abundanceString.indexOf( '.' ) >= 0 ? abundanceString.substring( abundanceString.indexOf( '.' ) + 1 ).length() : 0;
            isotopeInfo.setAbundance( new PrecisionDecimal( Double.parseDouble( abundanceString ), abundancePrecision ) );
            isotopeInfo.setElementSymbol( currentSymbol );
            isotopeInfoMap.put( isotopeKey, isotopeInfo );
        }

        // Add the translatable element names.  Only those element names that
        // are shown in the sim are translatable.
        setIsotopeName( 0, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_NONE_NAME );
        setIsotopeName( 1, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_HYDROGEN_NAME );
        setIsotopeName( 2, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_HELIUM_NAME );
        setIsotopeName( 3, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_LITHIUM_NAME );
        setIsotopeName( 4, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_BERYLLIUM_NAME );
        setIsotopeName( 5, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_BORON_NAME );
        setIsotopeName( 6, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_CARBON_NAME );
        setIsotopeName( 7, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_NITROGEN_NAME );
        setIsotopeName( 8, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_OXYGEN_NAME );
        setIsotopeName( 9, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_FLUORINE_NAME );
        setIsotopeName( 10, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_NEON_NAME );
        setIsotopeName( 11, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_SODIUM_NAME );
        setIsotopeName( 12, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_MAGNESIUM_NAME );
        setIsotopeName( 13, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_ALUMINUM_NAME );
        setIsotopeName( 14, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_SILICON_NAME );
        setIsotopeName( 15, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_PHOSPHORUS_NAME );
        setIsotopeName( 16, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_SULFUR_NAME );
        setIsotopeName( 17, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_CALCIUM_NAME );
        setIsotopeName( 18, isotopeInfoMap, BuildAnAtomStrings.ELEMENT_ARGON_NAME );

        for ( IsotopeKey key : isotopeInfoMap.keySet() ) {
            // Add the chemical symbols for each element.
            isotopeInfoMap.get( key ).setElementSymbol( key.getNumProtons() < ELEMENT_SYMBOL_TABLE.length ? ELEMENT_SYMBOL_TABLE[key.getNumProtons()] : BuildAnAtomStrings.ELEMENT_NONE_SYMBOL );
            // Add the stability information for each element.
            isotopeInfoMap.get( key ).setStable( STABLE_ISOTOPES.contains( key ) );
        }

        System.out.println( "Table created, time to create = " + ( System.currentTimeMillis() - entryTime ) );

        return isotopeInfoMap;
    }

    /**
     * Extract a list of all isotopes within the supplied map that match the
     * supplied atomic number.
     */
    private static List<IsotopeInfo> getMatchingIsotopes( int atomicNumber, Map<IsotopeKey, IsotopeInfo> isotopeInfoMap ) {
        List<IsotopeInfo> matchingIsotopes = new ArrayList<IsotopeInfo>();
        for ( IsotopeKey key : isotopeInfoMap.keySet() ) {
            if ( key.getNumProtons() == atomicNumber ) {
                matchingIsotopes.add( isotopeInfoMap.get( key ) );
            }
        }
        return matchingIsotopes;
    }

    /**
     * Set the name for all isotopes within the supplied map that matches the
     * specified atomic number.
     */
    private static void setIsotopeName( int atomicNumber, Map<IsotopeKey, IsotopeInfo> isotopeInfoMap, String name ) {
        for ( IsotopeInfo isotopeInfo : getMatchingIsotopes( atomicNumber, isotopeInfoMap ) ) {
            isotopeInfo.setElementName( name );
        }
    }

    // Test harness.
    public static void main( String[] args ) {
        generateIsotopeInfoTable();
    }

    /**
     * Class that contains the number of protons and neutrons in an atomic
     * nucleus.  This information can be used to uniquely identify any
     * isotope, so it is used as the key to the tables where isotope
     * information is stored.
     */
    private static class IsotopeKey {
        private final int numProtons; // a.k.a. the atomic number

        private final int numNeutrons;

        /**
         * Constructor.
         */
        public IsotopeKey( IAtom atomConfig ) {
            this( atomConfig.getNumProtons(), atomConfig.getNumNeutrons() );
        }

        /**
         * Constructor.
         */
        public IsotopeKey( int numProtons, int numNeutrons ) {
            this.numProtons = numProtons;
            this.numNeutrons = numNeutrons;
        }

        public int getNumProtons() {
            return numProtons;
        }

        public int getNumNeutrons() {
            return numNeutrons;
        }

        //Regenerate equals and hashcode if you change the contents of the isotope

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            IsotopeKey isotopeKey = (IsotopeKey) o;

            if ( numProtons != isotopeKey.getNumProtons() ) {
                return false;
            }
            if ( numNeutrons != isotopeKey.getNumNeutrons() ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = numProtons;
            result = 31 * result + numNeutrons;
            return result;
        }
    }

    /**
     * Class that contains a bunch of information for an isotope.  Note that
     * it does NOT contain the configuration of the isotope nucleus, since it
     * is expected to be used in a map that associates the configuration with
     * this information.
     */
    private static class IsotopeInfo {
        public double atomicMass;
        public String elementSymbol;
        public String elementName;
        public PrecisionDecimal abundance;  // On earth, present day.  Must track the precision of this number.
        public boolean stable;              // True if the half life is greater than the age of the universe.

        /**
         * Constructor.
         */
        public IsotopeInfo() {
            // Default constructor does no initialization.
        }

        /**
         * Constructor.
         */
        private IsotopeInfo( double atomicMass, String elementSymbol, String elementName, PrecisionDecimal abundance, boolean stable ) {
            this.atomicMass = atomicMass;
            this.elementSymbol = elementSymbol;
            this.elementName = elementName;
            this.abundance = abundance;
            this.stable = stable;
        }

        public double getAtomicMass() {
            return atomicMass;
        }

        public void setAtomicMass( double atomicMass ) {
            this.atomicMass = atomicMass;
        }

        public String getElementSymbol() {
            return elementSymbol;
        }

        public void setElementSymbol( String elementSymbol ) {
            this.elementSymbol = elementSymbol;
        }

        public String getElementName() {
            return elementName;
        }

        public void setElementName( String elementName ) {
            this.elementName = elementName;
        }

        public PrecisionDecimal getAbundance() {
            return abundance;
        }

        public void setAbundance( PrecisionDecimal abundance ) {
            this.abundance = abundance;
        }

        public boolean isStable() {
            return stable;
        }

        public void setStable( boolean stable ) {
            this.stable = stable;
        }
    }
}