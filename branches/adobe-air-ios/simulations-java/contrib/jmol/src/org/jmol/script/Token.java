/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-19 10:40:53 -0700 (Sat, 19 Mar 2011) $
 * $Revision: 15325 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;

public class Token {

  public int tok;
  public Object value;
  public int intValue = Integer.MAX_VALUE;

  public Token() {
    
  }

  public Token(int tok, int intValue, Object value) {
    this.tok = tok;
    this.intValue = intValue;
    this.value = value;
  }
 
  //integer tokens or have a value that is (more likely to be) non-null
  //null token values can cause problems in Eval.statementAsString()
  public Token(int tok) {
    this.tok = tok;
  }

  public Token(int tok, int intValue) {
    this.tok = tok;
    this.intValue = intValue;
  }

  public Token(int tok, Object value) {
    this.tok = tok;
    this.value = value;
  }

  public final static Token intToken(int intValue) {
    return new Token(integer, intValue);
  }

  public final static int nada       =  0;
  public final static int integer    =  2;
  public final static int decimal    =  3;
  public final static int string     =  4;
  
  final static int seqcode    =  5;
  final static int hash       =  6;  // associative array; Hashtable
  final static int varray     =  7;  // List<ScriptVariable>
  final static int point3f    =  8;
  final static int point4f    =  9;  
  final static int bitset     =  10;
  
  public final static int matrix3f   = 11;  
  public final static int matrix4f   = 12;  
  // listf "list-float" is specifically for xxx.all.bin, 
  // but it could be developed further
  final static int listf             = 13;     
  final private static int keyword   = 14;
  

  final static String[] astrType = {
    "nada", "identifier", "integer", "decimal", "string",
    "seqcode", "hash", "array", "point", "point4", "bitset",
    "matrix3f",  "matrix4f", "listf", "keyword"
  };

  public static boolean tokAttr(int a, int b) {
    return (a & b) == b;
  }
  
  public static boolean tokAttrOr(int a, int b1, int b2) {
    return (a & b1) == b1 || (a & b2) == b2;
  }
  
 

  // TOKEN BIT FIELDS
  
  // first 9 bits are generally identifier bits
  // or bits specific to a type
  
  /* bit flags:
   * 
   * parameter bit flags:
   * 
   * 3         2         1         0
   * 0987654321098765432109876543210
   *  x                  xxxxxxxxxxx setparam  "set THIS ...."
   *  x     x                        stringparam
   *  x    x                         intparam
   *  x   x                          floatparam
   *  x  x                           booleanparam
   * xx                              deprecatedparam
   * x                   xxxxxxxxxxx misc
   * 
   * 3         2         1         0
   * 0987654321098765432109876543210
   *                   x             sciptCommand
   *                  xx             atomExpressionCommand
   *                 x x           o implicitStringCommand (parsing of @{x})
   *                 x x           x implicitStringCommand (no initial parsing of @{x})
   *                x  x             mathExpressionCommand
   *               xx  x             flowCommand
   *              x    x             shapeCommand
   *             x                   noArgs
   *            x                    defaultON
   *                     xxxxxxxxxxx uniqueID (may include math flags)
   * 
   *              
   * math bit flags:
   * 
   * 3         2         1         0
   * 0987654321098765432109876543210
   *    FFFF    FFFF    FFFF    FFFF
   *           x                     expression
   *          xx                     predefined set
   * x       x x                     atomproperty
   * x      xx x                     strproperty
   * x     x x x                     intproperty
   * x    x  x x                     floatproperty
   * x   x     x                     mathproperty
   *    x      x                     mathfunc
   *        
   *        
   *                           xxxxx unique id 1 to 0x1F (31)
   *                          x      min
   *                         x       max
   *                         xx      average
   *                        x        sum
   *                        x x      sum2
   *                        xx       stddev
   *                        xxx      selectedfloat  (including just the atoms selected)
   *                       x         fullfloat (including all atoms, not just selected)
   *                       x???      [available] 
   *                       xxxx      minmaxmask (all)
   *                     xx          maximum number of parameters for function
   *                    x            settable
   *                   
   * 3         2         1         0
   * 0987654321098765432109876543210
   *   x       x                     mathop
   *   x       x           x         comparator
   *                            xxxx unique id (0 to 15)
   *                        xxxx     precedence
   *
   *                        
   * 
   */
   
  //
  // parameter bit flags
  //
  
  final static int setparam          = (1 << 29); // parameter to set command
  final static int misc              = (1 << 30); // misc parameter
  final static int deprecatedparam   = setparam | misc;
  
  public final static int identifier =  misc;

  public final static int scriptCommand            = (1 << 12);
  
  // the command assumes an atom expression as the first parameter
  // -- center, define, delete, display, hide, restrict, select, subset, zap
  final static int atomExpressionCommand  = (1 << 13) | scriptCommand;
  
  // this implicitString flag indicates that then entire command is an implied quoted string  
  // -- ODD echo, hover, label, message, pause  -- do NOT parse variables the same way
  // -- EVEN help, javascript, cd, gotocmd -- allow for single starting variable
  final static int implicitStringCommand     = (1 << 14) | scriptCommand;
  
  // this implicitExpression flag indicates that phrases surrounded 
  // by ( ) should be considered the same as { }. 
  // -- elseif, forcmd, ifcmd, print, returncmd, set, var, whilecmd
  final static int mathExpressionCommand = (1 << 15) | scriptCommand;
  
  // program flow commands include:
  // -- breakcmd, continuecmd, elsecmd, elseif, end, endifcmd, switch, case, 
  //    forcmd, function, ifcmd, whilecmd
  final static int flowCommand        = (1 << 16) | mathExpressionCommand;

  // these commands will be handled specially
  final static int shapeCommand   = (1 << 17) | scriptCommand;

  // Command argument compile flags
  
  final static int noArgs         = (1 << 18);
  final static int defaultON      = (1 << 19);
  
  final static int expression           = (1 << 20);
  final static int predefinedset        = (1 << 21) | expression;
  
  public final static int atomproperty  = (1 << 22) | expression | misc; 
  // all atom properties are either a member of one of the next three groups,
  // or they are a point/vector, in which case they are just atomproperty
  public final static int strproperty   = (1 << 23) | atomproperty; // string property
  public final static int intproperty   = (1 << 24) | atomproperty; // int parameter
  public final static int floatproperty = (1 << 25) | atomproperty; // float parameter

  public final static int PROPERTYFLAGS = strproperty | intproperty | floatproperty;

  // parameters that can be set using the SET command
  public final static int strparam   = (1 << 23) | setparam; // string parameter
  public final static int intparam   = (1 << 24) | setparam; // int parameter
  public final static int floatparam = (1 << 25) | setparam; // float parameter
  public final static int booleanparam = (1 << 26) | setparam; // boolean parameter
  
  // note: the booleanparam and the mathproperty bits are the same, but there is no
  //       conflict because mathproperty is only checked in ScriptEvaluator.getBitsetProperty
  //       meaning it is coming after a "." as in {*}.min
  
  final static int mathproperty         = (1 << 26) | expression | misc; // {xxx}.nnnn
  final static int mathfunc             = (1 << 27) | expression;  
  final static int mathop               = (1 << 28) | expression;
  final static int comparator           = mathop | (1 << 8);
  
  public final static int center       = 1 | atomExpressionCommand;
  public final static int define       = 2 | atomExpressionCommand | expression;
  public final static int delete       = 3 | atomExpressionCommand;
  public final static int display      = 4 | atomExpressionCommand | deprecatedparam;
  final static int fixed        = 5 | atomExpressionCommand | expression; // Jmol 12.0.RC15
  final static int hide         = 6 | atomExpressionCommand;
  final static int restrict     = 7 | atomExpressionCommand;
//final static int select       see mathfunc
  final static int subset       = 8 | atomExpressionCommand | predefinedset;
  final static int zap          = 9 | atomExpressionCommand | expression;

  final static int print        = 1 | mathExpressionCommand;
  final static int returncmd    = 2 | mathExpressionCommand;
  final static int set          = 3 | mathExpressionCommand | expression;
  final static int var          = 4 | mathExpressionCommand;
  final static int log          = 5 | mathExpressionCommand;
  //final static int prompt     see mathfunc
  
  public final static int echo  = 1 | implicitStringCommand | shapeCommand | setparam;
  final static int help         = 2 /* must be even */ | implicitStringCommand;
  public final static int hover = 3 | implicitStringCommand | defaultON;
//final static int javascript   see mathfunc
//final static int label        see mathfunc
  final static int message      = 5 | implicitStringCommand;
  public final static int pause = 7 | implicitStringCommand;

  //these commands control flow
  //sorry about GOTO!
//final static int function     see mathfunc
//final static int ifcmd        see mathfunc
  final static int elseif       = 2 | flowCommand;
  final static int elsecmd      = 3 | flowCommand | noArgs;
  final static int endifcmd     = 4 | flowCommand | noArgs;
//final static int forcmd       see mathfunc
  final static int whilecmd     = 6 | flowCommand;
  final static int breakcmd     = 7 | flowCommand;
  final static int continuecmd  = 8 | flowCommand;
  final static int end          = 9 | flowCommand | expression;
  final static int switchcmd    = 10 | flowCommand;
  final static int casecmd      = 11 | flowCommand;
  final static int catchcmd     = 12 | flowCommand;
  final static int defaultcmd   = 13 | flowCommand;
  final static int trycmd       = 14 | flowCommand | noArgs;
  
  final static int animation    = scriptCommand | 1;
  final static int assign       = scriptCommand | 2;
  final static int background   = scriptCommand | 3 | deprecatedparam;
  final static int bind         = scriptCommand | 4;
  final static int bondorder    = scriptCommand | 5;
  final static int calculate    = scriptCommand | 6;
  final static int cd           = scriptCommand | 8 | implicitStringCommand | expression; // must be even
  final static int centerAt     = scriptCommand | 9;
//final static int color        see intproperty
//final static int configuration see intproperty
  public final static int connect = scriptCommand | 10;
  final static int console      = scriptCommand | 11 | defaultON;
//final static int data         see mathfunc
  final static int delay        = scriptCommand | 12 | defaultON;
  public final static int depth = scriptCommand | 13 | intparam | defaultON;
  final static int exit         = scriptCommand | 14 | noArgs;
  final static int exitjmol     = scriptCommand | 17 | noArgs;
//final static int file         see intproperty
  final static int font         = scriptCommand | 18;
  final static int frame        = scriptCommand | 19;
//final static int getproperty  see mathfunc
  final static int gotocmd      = scriptCommand | 20 /*must be even*/| implicitStringCommand;
  public final static int hbond = scriptCommand | 22 | deprecatedparam | expression | defaultON;
  final static int history      = scriptCommand | 23 | deprecatedparam;
  final static int initialize   = scriptCommand | 24 | noArgs;
  final static int invertSelected = scriptCommand | 25;
//final static int load         see mathfunc
  final static int loop         = scriptCommand | 26 | defaultON;
  final static int mapProperty  = scriptCommand | 28 | expression;
  final static int minimize     = scriptCommand | 30;
//final static int model        see mathfunc
//final static int measure      see mathfunc
  final static int move         = scriptCommand | 32;
  public final static int moveto = scriptCommand | 34;
  public final static int navigate = scriptCommand | 35;
//final static int quaternion   see mathfunc
  final static int parallel     = flowCommand   | 36;
  final static int plot         = scriptCommand | 37;
  final static int pop          = scriptCommand | 38 | noArgs; //internal only
  final static int process      = flowCommand   | 39;
//  final static int prompt     see mathfunc
  final static int push         = scriptCommand | 40 | noArgs; //internal only
  final static int quit         = scriptCommand | 41 | noArgs;
  final static int ramachandran = scriptCommand | 42 | expression;
  final static int refresh      = scriptCommand | 43 | noArgs;
  final static int reset        = scriptCommand | 44;
  final static int restore      = scriptCommand | 45;
  public final static int resume = scriptCommand | 46 | noArgs;
  final static int rotate       = scriptCommand | 48 | defaultON;
  final static int rotateSelected = scriptCommand | 49;
  final static int save         = scriptCommand | 50;
//final static int script   see mathfunc
  public final static int selectionhalos = scriptCommand | 51 | deprecatedparam | defaultON;
  final static int show         = scriptCommand | 52;
  public final static int slab  = scriptCommand | 53 | intparam | defaultON;
  final static int spin         = scriptCommand | 55 | deprecatedparam | defaultON;
  public final static int ssbond = scriptCommand | 56 | deprecatedparam | defaultON;
  final static int step         = scriptCommand | 58 | noArgs;
  final static int stereo       = scriptCommand | 59 | defaultON;
//final static int structure    see intproperty
  final static int sync         = scriptCommand | 60;
  final static int timeout      = scriptCommand | 62 | setparam;
  final static int translate    = scriptCommand | 64;
  final static int translateSelected = scriptCommand | 66;
  final static int unbind       = scriptCommand | 68;
  public final static int vibration    = scriptCommand | 70;
  //final static int write   see mathfunc
  final static int zoom         = scriptCommand | 72;
  final static int zoomTo       = scriptCommand | 74;

  // shapes:
  
  public final static int axes         = shapeCommand | 2 | deprecatedparam | defaultON;
//final static int boundbox     see mathproperty
  public final static int dipole       = shapeCommand | 14;
  public final static int draw         = shapeCommand | 16;
  public final static int frank        = shapeCommand | 21 | deprecatedparam | defaultON;
  public final static int isosurface   = shapeCommand | 29;
  public final static int lcaocartoon  = shapeCommand | 30;
  public final static int measurements = shapeCommand | 31 | setparam;
  public final static int mo           = shapeCommand | 33 | expression;
  public final static int pmesh        = shapeCommand | 38;
  public final static int plot3d       = shapeCommand | 39;
  public final static int polyhedra    = shapeCommand | 40;
  //public final static int spacefill see floatproperty
  public final static int struts       = shapeCommand | 57 | defaultON | expression;
  public final static int unitcell     = shapeCommand | 64 | deprecatedparam | expression | predefinedset | defaultON;
  public final static int vector       = shapeCommand | 65;
  public final static int wireframe    = shapeCommand | 67 | defaultON;


  

  //
  // atom expression terms
  //
  
  final static int expressionBegin     = expression | 1;
  final static int expressionEnd       = expression | 2;
  public final static int all          = expression | 3;
  public final static int branch       = expression | 4;
  final static int coord               = expression | 6;
  final static int dollarsign          = expression | 7;
  final static int per                 = expression | 8;
  public final static int isaromatic   = expression | 9;
  final static int leftbrace           = expression | 10;
  final static int none                = expression | 11;
  public final static int off          = expression | 12; //for within(dist,false,...)
  public final static int on           = expression | 13; //for within(dist,true,...)
  final static int rightbrace          = expression | 14;
  final static int semicolon           = expression | 15;

  // generated by compiler:
  
  public final static int spec_alternate       = expression | 31;
  public final static int spec_atom            = expression | 32;
  public final static int spec_chain           = expression | 33;
  public final static int spec_model           = expression | 34;  // /3, /4
  final static int spec_model2                 = expression | 35;  // 1.2, 1.3
  public final static int spec_name_pattern    = expression | 36;
  public final static int spec_resid           = expression | 37;
  public final static int spec_seqcode         = expression | 38;
  public final static int spec_seqcode_range   = expression | 39;

  final static int amino                = predefinedset | 2;
  public final static int dna           = predefinedset | 4;
  public final static int hetero        = predefinedset | 6 | deprecatedparam;
  final static int helixalpha           = predefinedset | 7;  // new Jmol 12.1.14
  final static int helix310             = predefinedset | 8;  // new Jmol 12.1.14
  final static int helixpi              = predefinedset | 10; // new
  public final static int hydrogen      = predefinedset | 12 | deprecatedparam;
  public final static int nucleic       = predefinedset | 14;
  public final static int protein       = predefinedset | 16;
  public final static int purine        = predefinedset | 18;
  public final static int pyrimidine    = predefinedset | 20;
  public final static int rna           = predefinedset | 22;
  public final static int solvent       = predefinedset | 24 | deprecatedparam;
  public final static int sidechain     = predefinedset | 26;
  final static int surface              = predefinedset | 28;
  final static int thismodel            = predefinedset | 30;
  public final static int sheet         = predefinedset | 32;
  public final static int spine         = predefinedset | 34; // new 11.9.34
  // these next are predefined in the sense that they are known quantities
  public final static int carbohydrate    = predefinedset | 36;
  final static int clickable              = predefinedset | 38;
  final static int displayed              = predefinedset | 40;
  final static int hidden                 = predefinedset | 42;
  public final static int specialposition = predefinedset | 44;
  final static int visible                = predefinedset | 46;

  
  static int getPrecedence(int tokOperator) {
    return ((tokOperator >> 4) & 0xF);  
  }


  final static int leftparen    = 0 | mathop | 1 << 4;
  final static int rightparen   = 1 | mathop | 1 << 4;

  final static int opIf         = 1 | mathop | 2 << 4 | setparam;   // set ?
  final static int colon        = 2 | mathop | 2 << 4;

  final static int comma        = 0 | mathop | 3 << 4;

  final static int leftsquare   = 0 | mathop | 4 << 4;
  final static int rightsquare  = 1 | mathop | 4 << 4;

  final static int opOr         = 0 | mathop | 5 << 4;
  final static int opXor        = 1 | mathop | 5 << 4;
  public final static int opToggle = 2 | mathop | 5 << 4;

  final static int opAnd        = 0 | mathop | 6 << 4;
 
  final static int opNot        = 0 | mathop | 7 << 4;

  final static int opAND        = 0 | mathop | 8 << 4;

  final static int opGT         = 0 | comparator | 9 << 4;
  final static int opGE         = 1 | comparator | 9 << 4;
  final static int opLE         = 2 | comparator | 9 << 4;
  final static int opLT         = 3 | comparator | 9 << 4;
  public final static int opEQ  = 4 | comparator | 9 << 4;
  final static int opNE         = 6 | comparator | 9 << 4;
   
  final static int minus        = 0 | mathop | 10 << 4;
  final static int plus         = 1 | mathop | 10 << 4;
 
  final static int divide         = 0 | mathop | 11 << 4;
  final static int times          = 1 | mathop | 11 << 4;
  public final static int percent = 2 | mathop | 11 << 4;
  final static int leftdivide     = 3 | mathop | 11 << 4;  //   quaternion1 \ quaternion2
  
  final static int unaryMinus   = 0 | mathop | 12 << 4;
  final static int minusMinus   = 1 | mathop | 12 << 4;
  final static int plusPlus     = 2 | mathop | 12 << 4;
  final static int timestimes   = 3 | mathop | 12 << 4;
  
  
  final static int propselector = 1 | mathop | 13 << 4;

  final static int andequals    = 2 | mathop | 13 << 4;

  // these atom and math properties are invoked after a ".":
  // x.atoms
  // myset.bonds
  
  // .min and .max, .average, .sum, .sum2, .stddev, and .all 
  // are bitfields added to a preceding property selector
  // for example, x.atoms.max, x.atoms.all
  // .all gets incorporated as minmaxmask
  // .selectedfloat is a special flag used by mapPropety() and plot()
  // to pass temporary float arrays to the .bin() function
  // .allfloat is a special flag for colorShape() to get a full
  // atom float array
  
  final static int minmaxmask /*all*/ = 0xF << 5; 
  public final static int min           = 1 << 5;
  public final static int max           = 2 << 5;
  public final static int average       = 3 << 5;
  public final static int sum           = 4 << 5;
  public final static int sum2          = 5 << 5;
  public final static int stddev        = 6 << 5;
  public final static int selectedfloat = 7 << 5; //not user-selectable
  public final static int allfloat      = 8 << 5; //not user-selectable

  final static int settable           = 1 << 11;
  
  // bits 0 - 4 are for an identifier -- DO NOT GO OVER 31!
  // but, note that we can have more than 1 provided other parameters differ
  
  // ___.xxx math properties and all atom properties 
    
  public final static int atoms     = 1 | mathproperty;
  public final static int bonds     = 2 | mathproperty | deprecatedparam;
  final static int length           = 3 | mathproperty;
  final static int lines            = 4 | mathproperty;
  public final static int reverse   = 5 | mathproperty;
  final static int size             = 6 | mathproperty;
  public final static int type      = 8 | mathproperty;
  public final static int boundbox  = 9 | mathproperty | deprecatedparam | shapeCommand | defaultON;
  public final static int xyz       =10 | mathproperty | atomproperty | settable;
  public final static int fracxyz   =11 | mathproperty | atomproperty | settable;
  public final static int fuxyz     =12 | mathproperty | atomproperty | settable;
  public final static int unitxyz   =13 | mathproperty | atomproperty;
  public final static int vibxyz    =14 | mathproperty | atomproperty | settable;
  final static int w                =15 | mathproperty;// new
  final static int keys             =16 | mathproperty; // new
  
  // occupancy, radius, and structure are odd, because they takes different meanings when compared
  
  public final static int occupancy     = intproperty | floatproperty | 1 | settable;
  public final static int radius        = intproperty | floatproperty | 2 | deprecatedparam | settable;
  public final static int structure     = intproperty | strproperty   | 3 | setparam | scriptCommand;

  // any new int, float, or string property should be added also to LabelToken.labelTokenIds
  // and the appropriate Atom.atomPropertyXXXX() method
  
  public final static int atomtype      = strproperty | 1 | settable;
  public final static int atomname      = strproperty | 2 | settable;
  public final static int altloc        = strproperty | 3;
  public final static int chain         = strproperty | 4;
  public final static int element       = strproperty | 5 | settable;
  public final static int group         = strproperty | 6;
  public final static int group1        = strproperty | 7;
  public final static int sequence      = strproperty | 8;
  public final static int identify      = strproperty | 9;
  public final static int insertion     = strproperty |10;
  public final static int shape         = strproperty |11;
  public final static int strucid       = strproperty |12;
  public final static int symbol        = strproperty |13 | settable;
  public final static int symmetry      = strproperty |14 | predefinedset;

  public final static int atomno        = intproperty | 1 | settable;
  public final static int atomid        = intproperty | 2;
  public final static int atomindex     = intproperty | 3;
  public final static int bondcount     = intproperty | 4;
  public final static int cell          = intproperty | 5;
  public final static int configuration = intproperty | 6 | scriptCommand;
  //color: see xxx(a, b, c, d)
  public final static int elemno        = intproperty | 8 | settable;
  //file: see xxx(a)
  public final static int formalcharge  = intproperty | 9 | setparam | settable;
  public final static int groupid       = intproperty | 10;
  public final static int groupindex    = intproperty | 11;
  public final static int model         = intproperty | 12 | scriptCommand;
  public final static int modelindex    = intproperty | 13;
  public final static int molecule      = intproperty | 14;
  public final static int polymer       = intproperty | 15;
  public final static int polymerlength = intproperty | 16;
  public final static int resno         = intproperty | 17;
  public final static int site          = intproperty | 18;
  public final static int strucno       = intproperty | 19;
  public final static int valence       = intproperty | 20 | settable;

  // float values must be multiplied by 100 prior to comparing to integer values

  // max 31 here
  
  public final static int adpmax          = floatproperty | 1;
  public final static int adpmin          = floatproperty | 2;
  public final static int covalent        = floatproperty | 3;
  public final static int eta             = floatproperty | 4; // Jmol 12.0.RC23
  public final static int mass            = floatproperty | 5;
  public final static int omega           = floatproperty | 6;
  public final static int phi             = floatproperty | 7;
  public final static int psi             = floatproperty | 8;
  public final static int straightness    = floatproperty | 9;
  public final static int surfacedistance = floatproperty | 10;
  public final static int theta           = floatproperty | 11; // Jmol 12.0.RC23
  public final static int unitx           = floatproperty | 12;
  public final static int unity           = floatproperty | 13;
  public final static int unitz           = floatproperty | 14;
  public final static int atomx           = floatproperty | 1 | settable;
  public final static int atomy           = floatproperty | 2 | settable;
  public final static int atomz           = floatproperty | 3 | settable;
  public final static int fracx           = floatproperty | 4 | settable;
  public final static int fracy           = floatproperty | 5 | settable;
  public final static int fracz           = floatproperty | 6 | settable;
  public final static int fux             = floatproperty | 7 | settable;
  public final static int fuy             = floatproperty | 8 | settable;
  public final static int fuz             = floatproperty | 9 | settable;
  public final static int ionic           = floatproperty | 10 | settable;
  public final static int partialcharge   = floatproperty | 11| settable;
  public final static int property        = floatproperty | 12| mathproperty | setparam | settable;
  public final static int selected        = floatproperty | 13 | settable | predefinedset;
  public final static int temperature     = floatproperty | 14 | settable;
  public final static int vanderwaals     = floatproperty | 15 | settable;
  public final static int vibx            = floatproperty | 16 | settable;
  public final static int viby            = floatproperty | 17 | settable;
  public final static int vibz            = floatproperty | 18 | settable;
  public final static int x               = floatproperty | 19 | settable;
  public final static int y               = floatproperty | 20 | settable;
  public final static int z               = floatproperty | 21 | settable;
  
  public final static int backbone     = floatproperty | shapeCommand | 1 | predefinedset | defaultON | settable;
  public final static int cartoon      = floatproperty | shapeCommand | 2 | defaultON | settable;
  public final static int dots         = floatproperty | shapeCommand | 3 | defaultON;
  public final static int ellipsoid    = floatproperty | shapeCommand | 4 | defaultON;
  public final static int geosurface   = floatproperty | shapeCommand | 5 | defaultON;
  public final static int halo         = floatproperty | shapeCommand | 6 | defaultON | settable;
  public final static int meshRibbon   = floatproperty | shapeCommand | 7 | defaultON | settable;
  public final static int ribbon       = floatproperty | shapeCommand | 9 | defaultON | settable;
  public final static int rocket       = floatproperty | shapeCommand | 10 | defaultON | settable;
  public final static int spacefill    = floatproperty | shapeCommand | 11 | defaultON | settable;
  public final static int star         = floatproperty | shapeCommand | 12 | defaultON | settable;
  public final static int strands      = floatproperty | shapeCommand | 13 | deprecatedparam | defaultON | settable;
  public final static int trace        = floatproperty | shapeCommand | 14 | defaultON | settable;

  // mathfunc               means x = somefunc(a,b,c)
  // mathfunc|mathproperty  means x = y.somefunc(a,b,c)
  // 
  // maximum number of parameters is set by the << 9 shift
  // the min/max mask requires that the first number here must not exceed 63
  // the only other requirement is that these numbers be unique


  static int getMaxMathParams(int tokCommand) {
    return  ((tokCommand >> 9) & 0x7);
  }

  // 0 << 9 indicates that ScriptMathProcessor 
  // will check length in second stage of compilation

  // xxx(a,b,c,d,e,...)
  
  public final static int angle     = 1 | 0 << 9 | mathfunc;
  public final static int array     = 2 | 0 << 9 | mathfunc;
  final static int axisangle        = 3 | 0 << 9 | mathfunc;
  public final static int color     = 4 | 0 << 9 | mathfunc | intproperty | scriptCommand | deprecatedparam | settable;
  final static int compare          = 5 | 0 << 9 | mathfunc | scriptCommand;
  public final static int connected = 6 | 0 << 9 | mathfunc;
  public final static int data      = 7 | 0 << 9 | mathfunc | scriptCommand;
  public final static int format    = 8 | 0 << 9 | mathfunc | mathproperty | strproperty | settable;
  final static int function         = 9 | 0 << 9 | mathfunc | flowCommand;
  final static int getproperty      = 10 | 0 << 9 | mathfunc | scriptCommand;
  public final static int label     = 11 /* must be odd */| 0 << 9 | mathfunc | mathproperty | strproperty | settable | implicitStringCommand | shapeCommand | defaultON | deprecatedparam; 
  public final static int helix     = 12 | 0 << 9 | mathfunc | predefinedset;
  public final static int measure   = 13 | 0 << 9| mathfunc | shapeCommand | deprecatedparam | defaultON;
  final static int now              = 14 | 0 << 9 | mathfunc;
  public final static int plane     = 15 | 0 << 9 | mathfunc;
  public final static int point     = 16 | 0 << 9 | mathfunc;
  final static int quaternion       = 17 | 0 << 9 | mathfunc | scriptCommand;
  public final static int sort      = 18 | 0 << 9 | mathfunc | mathproperty;
  final static int within           = 19 | 0 << 9 | mathfunc;
  final static int write            = 20 | 0 << 9 | mathfunc | scriptCommand;

  // xxx(a)
  
  final static int acos         = 3 | 1 << 9 | mathfunc;
  final static int sin          = 4 | 1 << 9 | mathfunc;
  final static int cos          = 5 | 1 << 9 | mathfunc;
  final static int sqrt         = 6 | 1 << 9 | mathfunc;
  public final static int file  = 7 | 1 << 9 | mathfunc | intproperty | scriptCommand;
  final static int forcmd       = 8 | 1 << 9 | mathfunc | flowCommand;
  final static int ifcmd        = 9 | 1 << 9 | mathfunc | flowCommand;
  final static int abs          = 10 | 1 << 9 | mathfunc;
  final static int javascript   = 12 /* must be even */| 1 << 9 | mathfunc | implicitStringCommand;

  // ___.xxx(a)
  
  // a.distance(b) is in a different set -- distance(b,c) -- because it CAN take
  // two parameters and it CAN be a dot-function (but not both together)
  
  final static int div          = 0 | 1 << 9 | mathfunc | mathproperty;
  final static int dot          = 1 | 1 << 9 | mathfunc | mathproperty;
  final static int join         = 2 | 1 << 9 | mathfunc | mathproperty;
  final static int mul          = 3 | 1 << 9 | mathfunc | mathproperty;
  final static int split        = 4 | 1 << 9 | mathfunc | mathproperty;
  final static int sub          = 5 | 1 << 9 | mathfunc | mathproperty;
  final static int trim         = 6 | 1 << 9 | mathfunc | mathproperty;  
  public final static int volume = 7 | 1 << 9 | mathfunc | mathproperty | floatproperty;  
  final static int col           = 8 | 1 << 9 | mathfunc | mathproperty;
  final static int row           = 9 | 1 << 9 | mathfunc | mathproperty;

  // xxx(a,b)
  
  public final static int cross = 1 | 2 << 9 | mathfunc;
  final static int load         = 2 | 2 << 9 | mathfunc | scriptCommand;
  final static int random       = 4 | 2 << 9 | mathfunc;
  final static int script       = 5 | 2 << 9 | mathfunc | scriptCommand;
  public final static int substructure = 6 | 2 << 9 | mathfunc | intproperty | strproperty;
  final static int search       = 7 | 2 << 9 | mathfunc;
  final static int smiles       = 8 | 2 << 9 | mathfunc;
 

  // ___.xxx(a,b)

  // note that distance is here because it can take two forms:
  //     a.distance(b)
  // and
  //     distance(a,b)
  //so it can be a math property and it can have up to two parameters
  
  final static int add          = 1 | 2 << 9 | mathfunc | mathproperty;
  final static int distance     = 2 | 2 << 9 | mathfunc | mathproperty;
  final static int find         = 4 | 3 << 9 | mathfunc | mathproperty;
  final static int replace      = 3 | 2 << 9 | mathfunc | mathproperty;

  // xxx(a,b,c)
  
  final static int hkl          = 1 | 3 << 9 | mathfunc;
  final static int intersection = 2 | 3 << 9 | mathfunc;
  final static int prompt       = 3 | 3 << 9 | mathfunc | mathExpressionCommand;
  final static int select       = 4 | 3 << 9 | mathfunc | atomExpressionCommand;

  // ___.xxx(a,b,c)
  
  final static int bin          = 1 | 3 << 9 | mathfunc | mathproperty;
  public final static int symop = 2 | 3 << 9 | mathfunc | mathproperty | intproperty; 

  // anything beyond 3 are set "unlimited"

  // set parameters 
  
  // deprecated or handled specially in ScriptEvaluator
  
  final static int bondmode           = deprecatedparam | 1;  
  final static int fontsize           = deprecatedparam | 2;
  final static int measurementnumbers = deprecatedparam | 3;
  final static int scale3d            = deprecatedparam | 4;
  final static int togglelabel        = deprecatedparam | 5;

  // handled specially in ScriptEvaluator

  public final static int backgroundmodel  = setparam | 2;
  public final static int debug            = setparam | 4;
  public final static int defaultlattice   = setparam | 6;
  public final static int highlight        = setparam | 8;// 12.0.RC14
  public final static int showscript       = setparam | 10;
  public final static int specular         = setparam | 12;
  public final static int trajectory       = setparam | 14;
  public final static int usercolorscheme  = setparam | 16;

  // full set of all Jmol "set" parameters

  public final static int appletproxy                    = strparam | 2;
  public final static int atomtypes                      = strparam | 4;
  public final static int axescolor                      = strparam | 6;
  public final static int axis1color                     = strparam | 8;
  public final static int axis2color                     = strparam | 10;
  public final static int axis3color                     = strparam | 12;
  public final static int backgroundcolor                = strparam | 14;
  public final static int boundboxcolor                  = strparam | 16;
  public final static int currentlocalpath               = strparam | 18;
  public final static int dataseparator                  = strparam | 20;
  public final static int defaultanglelabel              = strparam | 22;
  public final static int defaultcolorscheme             = strparam | 24;
  public final static int defaultdirectory               = strparam | 26;
  public final static int defaultdistancelabel           = strparam | 28;
  public final static int defaultloadfilter              = strparam | 29;
  public final static int defaultloadscript              = strparam | 30;
  public final static int defaults                       = strparam | 32;
  public final static int defaulttorsionlabel            = strparam | 34;
  public final static int defaultvdw                     = strparam | 36;
  public final static int edsurlcutoff                   = strparam | 37;
  public final static int edsurlformat                   = strparam | 38;
  public final static int filecachedirectory             = strparam | 39;
  public final static int helppath                       = strparam | 40;
  public final static int hoverlabel                     = strparam | 42;
  public final static int language                       = strparam | 44;
  public final static int loadformat                     = strparam | 46;
  public final static int logfile                        = strparam | 47;
  public final static int measurementunits               = strparam | 48;  
  public final static int picking                        = strparam | 49;
  public final static int pickingstyle                   = strparam | 50;
  public final static int picklabel                      = strparam | 52;
  public final static int propertycolorscheme            = strparam | 54;
  public final static int quaternionframe                = strparam | 58;
  public final static int smilesurlformat                = strparam | 60;
  public final static int unitcellcolor                  = strparam | 62;
  
  public final static int axesscale                      = floatparam | 2;
  public final static int bondtolerance                  = floatparam | 4;
  public final static int cameradepth                    = floatparam | 6;
  public final static int defaultdrawarrowscale          = floatparam | 8;
  public final static int defaulttranslucent             = floatparam | 10;
  public final static int dipolescale                    = floatparam | 12;
  public final static int ellipsoidaxisdiameter          = floatparam | 14;
  public final static int gestureswipefactor             = floatparam | 15;
  public final static int hbondsangleminimum             = floatparam | 16;
  public final static int hbondsdistancemaximum          = floatparam | 17;
  public final static int hoverdelay                     = floatparam | 18;
  public final static int loadatomdatatolerance          = floatparam | 19;  
  public final static int minbonddistance                = floatparam | 20;
  public final static int minimizationcriterion          = floatparam | 21;
  public final static int mousedragfactor                = floatparam | 22;
  public final static int mousewheelfactor               = floatparam | 23;
  public final static int multiplebondradiusfactor       = floatparam | 24;
  public final static int multiplebondspacing            = floatparam | 25;
  public final static int navfps                         = floatparam | 26;
  public final static int navigationdepth                = floatparam | 27;
  public final static int navigationslab                 = floatparam | 28;
  public final static int navigationspeed                = floatparam | 30;
  public final static int navx                           = floatparam | 32;
  public final static int navy                           = floatparam | 34;
  public final static int navz                           = floatparam | 36;
  public final static int pointgroupdistancetolerance    = floatparam | 38;
  public final static int pointgrouplineartolerance      = floatparam | 40;
  public final static int rotationradius                 = floatparam | 44;
  public final static int scaleangstromsperinch          = floatparam | 46;
  public final static int sheetsmoothing                 = floatparam | 48;
  public final static int slabrange                      = floatparam | 49;
  public final static int solventproberadius             = floatparam | 50;
  public final static int spinfps                        = floatparam | 52;
  public final static int spinx                          = floatparam | 54;
  public final static int spiny                          = floatparam | 56;
  public final static int spinz                          = floatparam | 58;
  public final static int stereodegrees                  = floatparam | 60;
  public final static int strutdefaultradius             = floatparam | 62;
  public final static int strutlengthmaximum             = floatparam | 64;
  public final static int vectorscale                    = floatparam | 66;
  public final static int vibrationperiod                = floatparam | 68;
  public final static int vibrationscale                 = floatparam | 70;
  public final static int visualrange                    = floatparam | 72;

  public final static int ambientpercent                 = intparam | 2;               
  public final static int animationfps                   = intparam | 4;
  public final static int axesmode                       = intparam | 6;
  public final static int bondradiusmilliangstroms       = intparam | 8;
  public final static int delaymaximumms                 = intparam | 10;
  public final static int diffusepercent                 = intparam | 14;
  public final static int dotdensity                     = intparam | 15;
  public final static int dotscale                       = intparam | 16;
  public final static int ellipsoiddotcount              = intparam | 17;  
  public final static int helixstep                      = intparam | 18;
  public final static int hermitelevel                   = intparam | 19;
  public final static int historylevel                   = intparam | 20;
  public final static int isosurfacepropertysmoothingpower=intparam | 21;
  public final static int loglevel                       = intparam | 22;
  public final static int minimizationsteps              = intparam | 23;
  public final static int percentvdwatom                 = intparam | 24;
  public final static int perspectivemodel               = intparam | 26;
  public final static int phongexponent                  = intparam | 28;
  public final static int pickingspinrate                = intparam | 30;
  public final static int propertyatomnumberfield        = intparam | 31;
  public final static int propertyatomnumbercolumncount  = intparam | 32;
  public final static int propertydatacolumncount        = intparam | 34;
  public final static int propertydatafield              = intparam | 36;
  public final static int repaintwaitms                  = intparam | 37;
  public final static int ribbonaspectratio              = intparam | 38;
  public final static int scriptreportinglevel           = intparam | 40;
  public final static int smallmoleculemaxatoms          = intparam | 42;
  public final static int specularexponent               = intparam | 44;
  public final static int specularpercent                = intparam | 46;
  public final static int specularpower                  = intparam | 48;
  public final static int strandcount                    = intparam | 50;
  public final static int strandcountformeshribbon       = intparam | 52;
  public final static int strandcountforstrands          = intparam | 54;
  public final static int strutspacing                   = intparam | 56;
  public final static int zdepth                         = intparam | 58;
  public final static int zslab                          = intparam | 60;
  public final static int zshadepower                    = intparam | 62;

  public final static int allowembeddedscripts           = booleanparam | 2;
  public final static int allowgestures                  = booleanparam | 4;
  public final static int allowkeystrokes                = booleanparam | 5;
  public static final int allowmodelkit                  = booleanparam | 6; // Jmol 12.RC15
  public final static int allowmoveatoms                 = booleanparam | 7; // Jmol 12.1.21
  public static final int allowmultitouch                = booleanparam | 8; // Jmol 11.9.24
  public final static int allowrotateselected            = booleanparam | 9;
  public final static int antialiasdisplay               = booleanparam | 10;
  public final static int antialiasimages                = booleanparam | 12;
  public final static int antialiastranslucent           = booleanparam | 14;
  public final static int appendnew                      = booleanparam | 16;
  public final static int applysymmetrytobonds           = booleanparam | 18;
  public final static int atompicking                    = booleanparam | 20;
  public final static int autobond                       = booleanparam | 22;
  public final static int autofps                        = booleanparam | 24;
//  public final static int autoloadorientation            = booleanparam | 26;
  public final static int axesmolecular                  = booleanparam | 28;
  public final static int axesorientationrasmol          = booleanparam | 30;
  public final static int axesunitcell                   = booleanparam | 32;
  public final static int axeswindow                     = booleanparam | 34;
  public final static int bondmodeor                     = booleanparam | 36;
  public final static int bondpicking                    = booleanparam | 38;
// set mathproperty  public final static int bonds                          = booleanparam | 40;
  public final static int cartoonbaseedges               = booleanparam | 42;
  public final static int cartoonrockets                 = booleanparam | 43;
  public final static int chaincasesensitive             = booleanparam | 44;
  public final static int colorrasmol                    = booleanparam | 46;
  public final static int debugscript                    = booleanparam | 48;
  public final static int defaultstructuredssp           = booleanparam | 49;
  public final static int disablepopupmenu               = booleanparam | 50;
  public final static int displaycellparameters          = booleanparam | 52;
  public final static int dotsselectedonly               = booleanparam | 53;
  public final static int dotsurface                     = booleanparam | 54;
  public final static int dragselected                   = booleanparam | 55;
  public final static int drawhover                      = booleanparam | 56;
  public final static int drawpicking                    = booleanparam | 57;
  public final static int dsspcalchydrogen               = booleanparam | 58;
  public final static int dynamicmeasurements            = booleanparam | 59;
  public final static int ellipsoidarcs                  = booleanparam | 60;  
  public final static int ellipsoidaxes                  = booleanparam | 61;  
  public final static int ellipsoidball                  = booleanparam | 62;  
  public final static int ellipsoiddots                  = booleanparam | 63;  
  public final static int ellipsoidfill                  = booleanparam | 64;  
  public final static int filecaching                    = booleanparam | 66;
  public final static int fontcaching                    = booleanparam | 68;
  public final static int fontscaling                    = booleanparam | 69;
  public final static int forceautobond                  = booleanparam | 70;
  public final static int fractionalrelative             = booleanparam | 72;
// see shapecommand public final static int frank                          = booleanparam | 72;
  public final static int greyscalerendering             = booleanparam | 74;
  public final static int hbondsbackbone                 = booleanparam | 76;
  public final static int hbondsrasmol                   = booleanparam | 77;
  public final static int hbondssolid                    = booleanparam | 78;
// see predefinedset  public final static int hetero                         = booleanparam | 80;
  public final static int hidenameinpopup                = booleanparam | 82;
  public final static int hidenavigationpoint            = booleanparam | 84;
  public final static int hidenotselected                = booleanparam | 86;
  public final static int highresolution                 = booleanparam | 88;
// see predefinedset  public final static int hydrogen                       = booleanparam | 90;
  public final static int imagestate                     = booleanparam | 92;
  public static final int iskiosk                        = booleanparam | 93;// new 11.9.29
  public final static int isosurfacepropertysmoothing    = booleanparam | 94;
  public final static int justifymeasurements            = booleanparam | 96;
  public final static int languagetranslation            = booleanparam | 97;
  public final static int legacyautobonding              = booleanparam | 98;// new
  public final static int logcommands                    = booleanparam | 99;
  public final static int loggestures                    = booleanparam | 100;
  public final static int measureallmodels               = booleanparam | 101;
  public final static int measurementlabels              = booleanparam | 102;
  public final static int messagestylechime              = booleanparam | 103;
  public final static int minimizationrefresh            = booleanparam | 104;
  public final static int minimizationsilent             = booleanparam | 105;
  public final static int modelkitmode                   = booleanparam | 106; // new 12.0.RC15
  public final static int monitorenergy                  = booleanparam | 107;
  public final static int multiprocessor                 = booleanparam | 108;
  public final static int navigatesurface                = booleanparam | 109;
  public final static int navigationmode                 = booleanparam | 110;
  public final static int navigationperiodic             = booleanparam | 111;
  public final static int pdbgetheader                   = booleanparam | 112;
  public final static int pdbsequential                  = booleanparam | 114;
  public final static int perspectivedepth               = booleanparam | 116;
  public final static int preservestate                  = booleanparam | 117;
  public final static int rangeselected                  = booleanparam | 118;
  public final static int refreshing                     = booleanparam | 120;
  public final static int ribbonborder                   = booleanparam | 122;
  public final static int rocketbarrels                  = booleanparam | 124;
  public final static int saveproteinstructurestate      = booleanparam | 126;
  public final static int scriptqueue                    = booleanparam | 128;
  public final static int selectallmodels                = booleanparam | 130;
  public final static int selecthetero                   = booleanparam | 132;
  public final static int selecthydrogen                 = booleanparam | 134;
  // see commands public final static int selectionhalo                  = booleanparam | 136;
  public final static int showaxes                       = booleanparam | 138;
  public final static int showboundbox                   = booleanparam | 140;
  public final static int showfrank                      = booleanparam | 142;
  public final static int showhiddenselectionhalos       = booleanparam | 144;
  public final static int showhydrogens                  = booleanparam | 146;
  public final static int showkeystrokes                 = booleanparam | 148;
  public final static int showmeasurements               = booleanparam | 150;
  public final static int showmultiplebonds              = booleanparam | 152;
  public final static int shownavigationpointalways      = booleanparam | 154;
// see intparam  public final static int showscript                     = booleanparam | 156;
  public final static int showunitcell                   = booleanparam | 160;
  public final static int slabbyatom                     = booleanparam | 162;
  public final static int slabbymolecule                 = booleanparam | 164;
  public final static int slabenabled                    = booleanparam | 166;
  public final static int smartaromatic                  = booleanparam | 168;
// see predefinedset  public final static int solvent                        = booleanparam | 170;
  public final static int solventprobe                   = booleanparam | 172;
// see intparam  public final static int specular                       = booleanparam | 174;
  public final static int ssbondsbackbone                = booleanparam | 176;
  public final static int statusreporting                = booleanparam | 178;
  public final static int strutsmultiple                 = booleanparam | 179;
  public final static int syncmouse                      = booleanparam | 180;
  public final static int syncscript                     = booleanparam | 182;
  public final static int testflag1                      = booleanparam | 184;
  public final static int testflag2                      = booleanparam | 186;
  public final static int testflag3                      = booleanparam | 188;
  public final static int testflag4                      = booleanparam | 190;
  public final static int tracealpha                     = booleanparam | 191;
  public final static int undo                           = booleanparam | 192;
  public final static int usearcball                     = booleanparam | 193;
  public final static int useminimizationthread          = booleanparam | 194;
  public final static int usenumberlocalization          = booleanparam | 196;
  public final static int waitformoveto                  = booleanparam | 197;
  public final static int windowcentered                 = booleanparam | 198;
  public final static int wireframerotation              = booleanparam | 200;
  public final static int zerobasedxyzrasmol             = booleanparam | 202;
  public final static int zoomenabled                    = booleanparam | 204;
  public final static int zoomlarge                      = booleanparam | 206;
  public final static int zshade                         = booleanparam | 208;

  
  // misc

  final static int absolute      = misc  | 2;
  final static int addhydrogens  = misc  | 4;// new
  final static int adjust        = misc  | 6;// new
  final static int align         = misc  | 8;
  final static int allconnected  = misc  | 10;// new
  final static int angstroms     = misc  | 12;// new
  final static int anisotropy    = misc  | 14;// new
  final static int arc           = misc  | 16 | expression;// new
  final static int area          = misc  | 18;// new
  final static int aromatic      = misc  | 20 | predefinedset;// new
  final static int arrow         = misc  | 22;// new
  final static int as            = misc  | 24;// new
  final static int atomicorbital = misc  | 26;// new
  public final static int auto   = misc  | 28;// new
  public final static int axis   = misc  | 30;
  final static int babel         = misc  | 32;
  final static int babel21       = misc  | 34; // new
  final static int back          = misc  | 36;
  final static int barb          = misc  | 37;// new
  public final static int backlit = misc  | 38;
  public final static int basepair      = misc  | 40;
  final static int binary        = misc  | 42;// new
  final static int blockdata     = misc  | 44;// new
  final static int bondset       = misc  | 46;
  final static int bottom        = misc  | 48;
  final static int cancel        = misc  | 49;// new
  final static int cap           = misc  | 50 | expression;// new
  final static int cavity        = misc  | 52;// new
  final static int check         = misc  | 54;// new
  final static int circle        = misc  | 56;// new
  final static int clear         = misc  | 58;
  final static int clipboard     = misc  | 60;
  final static int collapsed     = misc  | 62;// new
  final static int colorscheme   = misc  | 64;// new
  final static int command       = misc  | 66;// new
  final static int commands      = misc  | 68;// new
  final static int constraint    = misc  | 70;
  final static int contour       = misc  | 72;// new
  public final static int contourlines  = misc  | 74;
  final static int contours      = misc  | 76;// new
  final static int corners       = misc  | 78;// new
  public final static int create = misc  | 80;// new
  final static int criterion     = misc  | 81;// new
  final static int crossed       = misc  | 82;// new
  final static int curve         = misc  | 84;// new
  final static int cutoff        = misc  | 86;// new
  final static int cylinder      = misc  | 88;// new
  final static int density        = misc  | 90;// new
  final static int dssp           = misc  | 91;// new
  final static int diameter       = misc  | 92;// new
  final static int direction      = misc  | 94;
  final static int discrete       = misc  | 96;// new
  final static int displacement   = misc  | 98;
  final static int distancefactor = misc  | 100;// new
  final static int dotted         = misc  | 102;
  final static int downsample     = misc  | 104;// new
  final static int eccentricity   = misc  | 106;// new
  final static int ed             = misc  | 108 | expression;// new
  final static int edges          = misc  | 110;// new
  final static int energy         = misc  | 111;// new
  final static int error          = misc  | 112; // new
  final static int facecenteroffset = misc  | 113;// new
  public final static int fill    = misc  | 114;
  final static int filter         = misc  | 116;// new
  public final static int first   = misc  | 118;// new
  final static int fixedtemp      = misc  | 122;
  final static int flat           = misc  | 124;// new
  final static int fps            = misc  | 126 | expression;// new
  final static int from           = misc  | 128;// new
  public final static int front   = misc  | 130;
  final static int frontedges     = misc  | 132;// new
  public final static int frontlit = misc  | 134;
  public final static int frontonly = misc  | 136;
  final static int full            = misc  | 137;// new
  final static int fullplane       = misc  | 138;// new
  public final static int fullylit = misc  | 140;
  final static int functionxy     = misc  | 142;// new
  final static int functionxyz    = misc  | 144;// new
  final static int gridpoints     = misc  | 146;// new
  final static int homo           = misc  | 149;// new
  final static int id             = misc  | 150 | expression;
  final static int ignore         = misc  | 152;// new
  final static int image          = misc  | 154;
  final static int in             = misc  | 155;// new
  final static int increment      = misc  | 156;// new
  public final static int info    = misc  | 158;
  final static int insideout      = misc  | 160;// new
  final static int interior       = misc  | 162;// new
  final static int internal       = misc  | 164;// new
  public final static int jmol    = misc  | 168;
  public final static int last    = misc  | 170;
  final static int lattice        = misc  | 171;// new
  final static int left           = misc  | 172;
  final static int line           = misc  | 174;// new
  final static int linedata       = misc  | 176;// new
  public final static int list    = misc  | 177; // just "list"
  final static int lobe           = misc  | 178;// new
  final static int lonepair       = misc  | 180;// new
  final static int lp             = misc  | 182;// new
  final static int lumo           = misc  | 184;// new
  final static int manifest       = misc  | 186;// new
  final static int maxset         = misc  | 190;// new
  final static int mep            = misc  | 192;
  public final static int mesh    = misc  | 194;
  final static int minset         = misc  | 196;// new
  final static int mlp            = misc  | 197;
  final static int mode           = misc  | 198;
  public final static int modify         = misc  | 200;// new
  public final static int modifyorcreate = misc  | 202;// new
  final static int modelbased     = misc  | 204;// new
  final static int molecular      = misc  | 206;// new
  final static int monomer        = misc  | 208;
  final static int mrc            = misc  | 209;// new
  final static int msms           = misc  | 210;// new
  public final static int next    = misc  | 211;
  public final static int nocontourlines  = misc  | 212;
  final static int nocross        = misc  | 214;// new
  final static int nodebug        = misc  | 216;// new
  public final static int nodots  = misc  | 218;
  final static int noedges        = misc  | 220;// new
  public final static int nofill  = misc  | 222;
  final static int nohead         = misc  | 224;// new
  final static int noload         = misc  | 226;// new
  public final static int nomesh  = misc  | 228;
  final static int noplane        = misc  | 230;// new
  final static int normal         = misc  | 232;
  public final static int notfrontonly  = misc  | 234;
  public final static int notriangles   = misc  | 236;
  final static int obj            = misc  | 238;// new
  final static int object         = misc  | 240;// new
  final static int offset         = misc  | 242;// new
  final static int offsetside     = misc  | 244;// new
  final static int once           = misc  | 246;// new
  final static int only           = misc  | 248;
  final static int opaque         = misc  | 250;
  final static int orbital        = misc  | 252;// new
  final static int orientation    = misc  | 254;
  final static int out            = misc  | 255;// new
  final static int packed         = misc  | 256;// new
  final static int palindrome     = misc  | 258;// new
  final static int path           = misc  | 260;// new
  final static int pdb            = misc  | 262 | expression;// new
  final static int pdbheader      = misc  | 264;
  final static int period         = misc  | 266;// new
  final static int perpendicular  = misc  | 268;// new
  final static int phase          = misc  | 270;// new
  public final static int play    = misc  | 272;
  public final static int playrev = misc  | 274;
  final static int pocket         = misc  | 276;// new
  final static int pointgroup     = misc  | 278;
  final static int pointsperangstrom = misc  | 280;// new
  final static int polygon        = misc  | 282;// new
  public final static int prev    = misc  | 284;
  final static int rad            = misc  | 286;// new
  final static int radical        = misc  | 288;// new
  final static int range          = misc  | 290;
  public final static int rasmol  = misc  | 292;
  final static int reference      = misc  | 294;// new
  public final static int residue = misc  | 296;
  final static int resolution     = misc  | 298;// new
  final static int reversecolor   = misc  | 300;// new
  public final static int rewind  = misc  | 302;
  final static int right          = misc  | 304;
  final static int rotate45       = misc  | 306;// new
  public final static int rotation = misc  | 308;
  final static int rubberband     = misc  | 310;
  final static int sasurface      = misc  | 312;
  final static int scale          = misc  | 314;
  final static int selection      = misc  | 316;// new
  final static int shapely        = misc  | 320;
  final static int sigma          = misc  | 322;// new
  final static int sign           = misc  | 323;// new
  final static int silent         = misc  | 324;// new
  final static int solid          = misc  | 327;
  final static int spacegroup     = misc  | 328;
  final static int sphere         = misc  | 330;// new
  final static int squared        = misc  | 332;// new
  final static int state          = misc  | 334;
  final static int steps          = misc  | 336;// new
  final static int stop           = misc  | 338;// new
  final static int supercell      = misc  | 339;//
  final static int ticks          = misc  | 340; // new
  final static int title          = misc  | 342;// new
  final static int titleformat    = misc  | 344;// new
  final static int to             = misc  | 346 | expression;// new
  final static int top            = misc  | 348 | expression;
  final static int torsion        = misc  | 350;
  final static int transform      = misc  | 352;
  public final static int translation   = misc  | 354;
  public final static int translucent   = misc  | 356;
  public final static int triangles     = misc  | 358;
  final static int url             = misc  | 360 | expression;
  final static int user            = misc  | 362;
  final static int val             = misc  | 364;// new
  final static int variable        = misc  | 366;// new
  final static int variables       = misc  | 368;// new
  final static int vertices        = misc  | 370;// new
  final static int spacebeforesquare      = misc  | 371;// new
  final static int width           = misc  | 372;// new
  
  
  // predefined Tokens: 
  
  final static Token tokenSpaceBeforeSquare = new Token(spacebeforesquare, " ");
  final static Token tokenOn  = new Token(on, 1, "on");
  final static Token tokenOff = new Token(off, 0, "off");
  final static Token tokenAll = new Token(all, "all");
  final static Token tokenIf = new Token(ifcmd, "if");
  public final static Token tokenAnd = new Token(opAnd, "and");
  public final static Token tokenAND = new Token(opAND, "");
  public final static Token tokenOr  = new Token(opOr, "or");
  public final static Token tokenAndFALSE = new Token(opAnd, "and");
  public final static Token tokenOrTRUE = new Token(opOr, "or");
  public final static Token tokenOpIf  = new Token(opIf, "?");
  public final static Token tokenComma = new Token(comma, ",");
  final static Token tokenDefineString = new Token(Token.define, Token.string, "@");
  final static Token tokenPlus = new Token(plus, "+");
  final static Token tokenMinus = new Token(minus, "-");
  final static Token tokenTimes = new Token(times, "*");
  final static Token tokenDivide = new Token(divide, "/");

  public final static Token tokenLeftParen = new Token(leftparen, "(");
  public final static Token tokenRightParen = new Token(rightparen, ")");
  final static Token tokenArraySquare = new Token(array, "[");
  final static Token tokenArraySelector = new Token(leftsquare, "[");
 
  public final static Token tokenExpressionBegin = new Token(expressionBegin, "expressionBegin");
  public final static Token tokenExpressionEnd   = new Token(expressionEnd, "expressionEnd");
  public final static Token tokenConnected       = new Token(connected, "connected");
  final static Token tokenCoordinateBegin = new Token(leftbrace, "{");
  final static Token tokenRightBrace = new Token(rightbrace, "}");
  final static Token tokenCoordinateEnd = tokenRightBrace;
  final static Token tokenColon           = new Token(colon, ':');
  final static Token tokenSetCmd          = new Token(set, "set");
  final static Token tokenSet             = new Token(set, '=', "");
  final static Token tokenSetArray        = new Token(set, '[', "");
  final static Token tokenSetProperty     = new Token(set, '.', "");
  final static Token tokenSetVar          = new Token(set, '=', "var");
  final static Token tokenEquals          = new Token(opEQ, "=");
  final static Token tokenScript          = new Token(script, "script");
  final static Token tokenSwitch          = new Token(switchcmd, "switch");
    
  private static Map<String, Token> tokenMap = new Hashtable<String, Token>();
  public static void addToken(String ident, Token token) {
    tokenMap.put(ident, token);
  }
  
  public static Token getTokenFromName(String name) {
    return tokenMap.get(name);
  }
  
  public static int getTokFromName(String name) {
    Token token = getTokenFromName(name);
    return (token == null ? Token.nada : token.tok);
  }


  
  /**
   * note: nameOf is a very inefficient mechanism for getting 
   * the name of a token. But it is only used for error messages
   * and listings of variables and such.
   * 
   * @param tok
   * @return     the name of the token or 0xAAAAAA
   */
  public static String nameOf(int tok) {
    for (Token token : tokenMap.values()) {
      if (token.tok == tok)
        return "" + token.value;
    }
    return "0x"+Integer.toHexString(tok);
   }
   
  @Override
  public String toString() {
    return "Token["
        + astrType[tok < keyword ? tok : keyword]
        + "("+(tok%(1<<9))+"/0x" + Integer.toHexString(tok) + ")"
        + ((intValue == Integer.MAX_VALUE) ? "" : " intValue=" + intValue
            + "(0x" + Integer.toHexString(intValue) + ")")
        + ((value == null) ? "" : value instanceof String ? " value=\"" + value
            + "\"" : " value=" + value) + "]";
  }
  
  ////////command sets ///////

  /**
   * retrieves an unsorted list of viable commands that could be
   * completed by this initial set of characters. If fewer than
   * two characters are given, then only the "preferred" command
   * is given (measure, not monitor, for example), and in all cases
   * if both a singular and a plural might be returned, only the
   * singular is returned.
   * 
   * @param strBegin initial characters of the command, or null
   * @return UNSORTED semicolon-separated string of viable commands
   */
  public static String getCommandSet(String strBegin) {
    String cmds = "";
    Map<String, Boolean> htSet = new Hashtable<String, Boolean>();
    int nCmds = 0;
    String s = (strBegin == null || strBegin.length() == 0 ? null : strBegin
        .toLowerCase());
    boolean isMultiCharacter = (s != null && s.length() > 1);
    for (Map.Entry<String, Token> entry : tokenMap.entrySet()) {
      String name = entry.getKey();
      Token token = entry.getValue();
      if ((token.tok & scriptCommand) != 0
          && (s == null || name.indexOf(s) == 0)
          && (isMultiCharacter || ((String) token.value).equals(name)))
        htSet.put(name, Boolean.TRUE);
    }
    for (Map.Entry<String, Boolean> entry : htSet.entrySet()) {
      String name = entry.getKey();
      if (name.charAt(name.length() - 1) != 's'
          || !htSet.containsKey(name.substring(0, name.length() - 1)))
        cmds += (nCmds++ == 0 ? "" : ";") + name;
    }
    return cmds;
  }
  
  public static String[] getTokensLike(String type) {
    int attr = (type.equals("setparam") ? setparam 
        : type.equals("misc") ? misc 
        : type.equals("mathfunc") ? mathfunc : scriptCommand);
    int notattr = (attr == setparam ? deprecatedparam : nada);
    List<String> v = new ArrayList<String>();
    for (Map.Entry<String, Token> entry : tokenMap.entrySet()) {
      String name = entry.getKey();
      Token token = entry.getValue();
      if (tokAttr(token.tok, attr) && (notattr == nada || !tokAttr(token.tok, notattr)))
        v.add(name);
    }
    String[] a = new String[v.size()];
    for (int i = 0; i < a.length; i++)
      a[i] = v.get(i);
    Arrays.sort(a);
    return a;
  }

  public static int getSettableTokFromString(String s) {
    int tok = getTokFromName(s);
    return (tok != nada && tokAttr(tok, settable) 
          && !tokAttr(tok, mathproperty) ? tok : nada);
  }

  public static String completeCommand(Map<String, Token> map, boolean isSet, 
                                       boolean asCommand, 
                                       String str, int n) {
    if (map == null)
      map = Token.tokenMap;
    else
      asCommand = false;
    List<String> v = new ArrayList<String>();
    str = str.toLowerCase();
    for (String name : map.keySet()) {
      if (!name.startsWith(str))
        continue;
      int tok = getTokFromName(name);
      if (asCommand ? tokAttr(tok, scriptCommand) 
          : isSet ? tokAttr(tok, setparam) && !tokAttr(tok, deprecatedparam) 
          : true)
        v.add(name);
    }
    return ArrayUtil.sortedItem(v, n);
  }

  static {

    Object[] arrayPairs  = {

    // atom expressions

      "(",            tokenLeftParen,
      ")",            tokenRightParen,
      "and",          tokenAnd,
      "&",            null,
      "&&",           null,
      "or",           tokenOr,
      "|",            null,
      "||",           null,
      "?",            tokenOpIf,
      ",",            tokenComma,
      "+=",           new Token(andequals),
      "-=",           null,
      "*=",           null,
      "/=",           null,
      "\\=",          null,
      "&=",           null,
      "|=",           null,
      "not",          new Token(opNot),
      "!",            null,
      "xor",          new Token(opXor),
    //no-- don't do this; it interferes with define
    //  "~",            null,
      "tog",          new Token(opToggle),
      "<",            new Token(opLT),
      "<=",           new Token(opLE),
      ">=",           new Token(opGE),
      ">",            new Token(opGT),
      "=",            tokenEquals,
      "==",           null,
      "!=",           new Token(opNE),
      "<>",           null,
      "within",       new Token(within),
      ".",            new Token(per),
      "[",            new Token(leftsquare),
      "]",            new Token(rightsquare),
      "{",            new Token(leftbrace),
      "}",            new Token(rightbrace),
      "$",            new Token(dollarsign),
      "%",            new Token(percent),
      ":",            tokenColon,
      ";",            new Token(semicolon),
      "++",           new Token(plusPlus),
      "--",           new Token(minusMinus),
      "**",           new Token(timestimes),
      "+",            tokenPlus,
      "-",            tokenMinus,
      "*",            tokenTimes,
      "/",            tokenDivide,
      "\\",           new Token(leftdivide),
    
    // commands
        
      "animation",         new Token(animation),
      "anim",              null,
      "assign",            new Token(assign),
      "axes",              new Token(axes),
      "backbone",          new Token(backbone),
      "background",        new Token(background),
      "bind",              new Token(bind),
      "bondorder",         new Token(bondorder),
      "boundbox",          new Token(boundbox),
      "boundingBox",       null,
      "break",             new Token(breakcmd),
      "calculate",         new Token(calculate),
      "cartoon",           new Token(cartoon),
      "cartoons",          null,
      "case",              new Token(casecmd),
      "catch",             new Token(catchcmd),
      "cd",                new Token(cd),
      "center",            new Token(center),
      "centre",            null,
      "centerat",          new Token(centerAt),
      "color",             new Token(color),
      "colour",            null,
      "compare",           new Token(compare),
      "configuration",     new Token(configuration),
      "conformation",      null,
      "config",            null,
      "connect",           new Token(connect),
      "console",           new Token(console),
      "continue",          new Token(continuecmd),
      "data",              new Token(data),
      "default",           new Token(defaultcmd),
      "define",            new Token(define),
      "@",                 null,
      "delay",             new Token(delay),
      "delete",            new Token(delete),
      "density",           new Token(density),
      "depth",             new Token(depth),
      "dipole",            new Token(dipole),
      "dipoles",           null,
      "display",           new Token(display),
      "dot",               new Token(dot),
      "dots",              new Token(dots),
      "draw",              new Token(draw),
      "echo",              new Token(echo),
      "ellipsoid",         new Token(ellipsoid),
      "ellipsoids",        null,
      "else",              new Token(elsecmd),
      "elseif",            new Token(elseif),
      "end",               new Token(end),
      "endif",             new Token(endifcmd),
      "exit",              new Token(exit),
      "file",              new Token(file),
      "font",              new Token(font),
      "for",               new Token(forcmd),
      "format",            new Token(format),
      "frame",             new Token(frame),
      "frames",            null,
      "frank",             new Token(frank),
      "function",          new Token(function),
      "functions",         null,
      "geosurface",        new Token(geosurface),
      "getProperty",       new Token(getproperty),
      "goto",              new Token(gotocmd),
      "halo",              new Token(halo),
      "halos",             null,
      "helix",             new Token(helix),
      "helixalpha",        new Token(helixalpha),
      "helix310",          new Token(helix310),
      "helixpi",           new Token(helixpi),
      "hbond",             new Token(hbond),
      "hbonds",            null,
      "help",              new Token(help),
      "hide",              new Token(hide),
      "history",           new Token(history),
      "hover",             new Token(hover),
      "if",                new Token(ifcmd),
      "in",                new Token(in),
      "initialize",        new Token(initialize),
      "invertSelected",    new Token(invertSelected),
      "isosurface",        new Token(isosurface),
      "javascript",        new Token(javascript),
      "label",             new Token(label),
      "labels",            null,
      "lcaoCartoon",       new Token(lcaocartoon),
      "lcaoCartoons",      null,
      "load",              new Token(load),
      "log",               new Token(log),
      "loop",              new Token(loop),
      "measure",           new Token(measure),
      "measures",          null,
      "monitor",           null,
      "monitors",          null,
      "meshribbon",        new Token(meshRibbon),
      "meshribbons",       null,
      "message",           new Token(message),
      "minimize",          new Token(minimize),
      "minimization",      null,
      "mo",                new Token(mo),
      "model",             new Token(model),
      "models",            null,
      "move",              new Token(move),
      "moveTo",            new Token(moveto),
      "navigate",          new Token(navigate),
      "navigation",        null,
      "out",               new Token(out),
      "parallel",          new Token(parallel),
      "pause",             new Token(pause),
      "wait",              null,
      "plot",              new Token(plot),
      "plot3d",            new Token(plot3d),
      "pmesh",             new Token(pmesh),
      "polygon",           new Token(polygon),
      "polyhedra",         new Token(polyhedra),
      "print",             new Token(print),
      "process",           new Token(process),
      "prompt",            new Token(prompt),
      "quaternion",        new Token(quaternion),
      "quaternions",       null,
      "quit",              new Token(quit),
      "ramachandran",      new Token(ramachandran),
      "rama",              null,
      "refresh",           new Token(refresh),
      "reset",             new Token(reset),
      "unset",             null,
      "restore",           new Token(restore),
      "restrict",          new Token(restrict),
      "return",            new Token(returncmd),
      "ribbon",            new Token(ribbon),
      "ribbons",           null,
      "rocket",            new Token(rocket),
      "rockets",           null,
      "rotate",            new Token(rotate),
      "rotateSelected",    new Token(rotateSelected),
      "save",              new Token(save),
      "script",            tokenScript,
      "source",            null,
      "select",            new Token(select),
      "selectionHalos",    new Token(selectionhalos),
      "selectionHalo",     null,
      "showSelections",    null,
      "set",               tokenSetCmd,
      "sheet",             new Token(sheet),
      "show",              new Token(show),
      "slab",              new Token(slab),
      "spacefill",         new Token(spacefill),
      "cpk",               null,
      "spin",              new Token(spin),
      "ssbond",            new Token(ssbond),
      "ssbonds",           null,
      "star",              new Token(star),
      "stars",             null,
      "step",              new Token(step),
      "stereo",            new Token(stereo),
      "strand",            new Token(strands),
      "strands",           null,
      "structure",         new Token(structure),
      "_structure",        null,
      "strucNo",           new Token(strucno),
      "struts",            new Token(struts),
      "strut",             null,
      "subset",            new Token(subset),
      "switch",            tokenSwitch,
      "synchronize",       new Token(sync),
      "sync",              null,
      "trace",             new Token(trace),
      "translate",         new Token(translate),
      "translateSelected", new Token(translateSelected),
      "try",               new Token(trycmd),
      "unbind",            new Token(unbind),
      "unitcell",          new Token(unitcell),
      "var",               new Token(var),
      "vector",            new Token(vector),
      "vectors",           null,
      "vibration",         new Token(vibration),
      "while",             new Token(whilecmd),
      "wireframe",         new Token(wireframe),
      "write",             new Token(write),
      "zap",               new Token(zap),
      "zoom",              new Token(zoom),
      "zoomTo",            new Token(zoomTo),
                            
      //                   show parameters
  
      "atom",              new Token(atoms),
      "atoms",             null,
      "axis",              new Token(axis),
      "axisangle",         new Token(axisangle),
      "basepair",          new Token(basepair),
      "basepairs",         null,
      "orientation",       new Token(orientation),
      "orientations",      null,
      "pdbheader",         new Token(pdbheader),                          
      "polymer",           new Token(polymer),
      "polymers",          null,
      "residue",           new Token(residue),
      "residues",          null,
      "rotation",          new Token(rotation),
      "row",               new Token(row),
      "sequence",          new Token(sequence),
      "shape",             new Token(shape),
      "state",             new Token(state),
      "symbol",            new Token(symbol),
      "symmetry",          new Token(symmetry),
      "spaceGroup",        new Token(spacegroup),
      "transform",         new Token(transform),
      "translation",       new Token(translation),
      "url",               new Token(url),
  
      // misc
  
      "abs",             new Token(abs),
      "absolute",        new Token(absolute),
      "acos",            new Token(acos),
      "add",             new Token(add),
      "adpmax",          new Token(adpmax),
      "adpmin",          new Token(adpmin),
      "align",           new Token(align),
      "all",             tokenAll,
      "altloc",          new Token(altloc),
      "altlocs",         null,
      "amino",           new Token(amino),
      "angle",           new Token(angle),
      "array",           new Token(array),
      "as",              new Token(as),
      "atomID",          new Token(atomid),
      "_atomID",         null,
      "_a",              null, 
      "atomIndex",       new Token(atomindex),
      "atomName",        new Token(atomname),
      "atomno",          new Token(atomno),
      "atomType",        new Token(atomtype),
      "atomX",           new Token(atomx),
      "atomY",           new Token(atomy),
      "atomZ",           new Token(atomz),
      "average",         new Token(average),
      "babel",           new Token(babel),
      "babel21",         new Token(babel21), 
      "back",            new Token(back),
      "backlit",         new Token(backlit),
      "bin",             new Token(bin),
      "bondCount",       new Token(bondcount),
      "bottom",          new Token(bottom),
      "branch",          new Token(branch),
      "carbohydrate",    new Token(carbohydrate),
      "cell",            new Token(cell),
      "chain",           new Token(chain),
      "chains",          null,
      "clear",           new Token(clear),
      "clickable",       new Token(clickable),
      "clipboard",       new Token(clipboard),
      "connected",       new Token(connected),
      "constraint",      new Token(constraint),
      "contourLines",    new Token(contourlines),
      "coord",           new Token(coord),
      "coordinates",     null,
      "coords",          null,
      "cos",             new Token(cos),
      "cross",           new Token(cross),
      "covalent",        new Token(covalent),
      "direction",       new Token(direction),
      "displacement",    new Token(displacement),
      "displayed",       new Token(displayed),
      "distance",        new Token(distance),
      "div",             new Token(div),
      "DNA",             new Token(dna),
      "dotted",          new Token(dotted),
      "DSSP",            new Token(dssp),
      "element",         new Token(element),
      "elemno",          new Token(elemno),
      "_e",              null,
      "error",           new Token(error),
      "fill",            new Token(fill),
      "find",            new Token(find),
      "fixedTemperature",new Token(fixedtemp),
      "formalCharge",    new Token(formalcharge),
      "charge",          null, 
      "eta",             new Token(eta),
      "front",           new Token(front),
      "frontlit",        new Token(frontlit),
      "frontOnly",       new Token(frontonly),
      "fullylit",        new Token(fullylit),
      "fx",              new Token(fracx),
      "fy",              new Token(fracy),
      "fz",              new Token(fracz),
      "fxyz",            new Token(fracxyz),
      "fux",             new Token(fux),
      "fuy",             new Token(fuy),
      "fuz",             new Token(fuz),
      "fuxyz",           new Token(fuxyz),
      "group",           new Token(group),
      "groups",          null,
      "group1",          new Token(group1),
      "groupID",         new Token(groupid),
      "_groupID",        null, 
      "_g",              null, 
      "groupIndex",      new Token(groupindex),
      "hidden",          new Token(hidden),
      "highlight",       new Token(highlight),
      "hkl",             new Token(hkl),
      "id",              new Token(id),
      "identify",        new Token(identify),
      "ident",           null,
      "image",           new Token(image),
      "info",            new Token(info),
      "insertion",       new Token(insertion),
      "insertions",      null, 
      "ionic",           new Token(ionic),
      "ionicRadius",     null,
      "isAromatic",      new Token(isaromatic),
      "Jmol",            new Token(jmol),
      "join",            new Token(join),
      "keys",            new Token(keys),
      "last",            new Token(last),
      "left",            new Token(left),
      "length",          new Token(length),
      "lines",           new Token(lines),
      "list",            new Token(list),
      "mass",            new Token(mass),
      "max",             new Token(max),
      "mep",             new Token(mep),
      "mesh",            new Token(mesh),
      "min",             new Token(min),
      "mlp",             new Token(mlp),
      "mode",            new Token(mode),
      "modify",          new Token(modify),
      "modifyOrCreate",  new Token(modifyorcreate),
      "molecule",        new Token(molecule),
      "molecules",       null, 
      "modelIndex",      new Token(modelindex),
      "monomer",         new Token(monomer),
      "mul",             new Token(mul),
      "next",            new Token(next),
      "noDots",          new Token(nodots),
      "noFill",          new Token(nofill),
      "noMesh",          new Token(nomesh),
      "none",            new Token(none),
      "null",            null,
      "inherit",         null,
      "normal",          new Token(normal),
      "noContourLines",  new Token(nocontourlines),
      "notFrontOnly",    new Token(notfrontonly),
      "noTriangles",     new Token(notriangles),
      "now",             new Token(now),
      "nucleic",         new Token(nucleic),
      "occupancy",       new Token(occupancy),
      "off",             tokenOff, 
      "false",           null, 
      "on",              tokenOn,
      "true",            null, 
      "omega",           new Token(omega),
      "only",            new Token(only),
      "opaque",          new Token(opaque),
      "partialCharge",   new Token(partialcharge),
      "phi",             new Token(phi),
      "plane",           new Token(plane),
      "play",            new Token(play),
      "playRev",         new Token(playrev),
      "point",           new Token(point),
      "points",          null,
      "pointGroup",      new Token(pointgroup),
      "polymerLength",   new Token(polymerlength),
      "previous",        new Token(prev),
      "prev",            null,
      "property",        new Token(property),
      "properties",      null,
      "protein",         new Token(protein),
      "psi",             new Token(psi),
      "purine",          new Token(purine),
      "pyrimidine",      new Token(pyrimidine),
      "random",          new Token(random),
      "range",           new Token(range),
      "rasmol",          new Token(rasmol),
      "replace",         new Token(replace),
      "resno",           new Token(resno),
      "resume",          new Token(resume),
      "rewind",          new Token(rewind),
      "reverse",         new Token(reverse),
      "right",           new Token(right),
      "RNA",             new Token(rna),
      "rubberband",      new Token(rubberband),
      "saSurface",       new Token(sasurface),
      "scale",           new Token(scale),
      "search",          new Token(search),
      "smarts",          null,
      "selected",        new Token(selected),
      "shapely",         new Token(shapely),
      "sidechain",       new Token(sidechain),
      "sin",             new Token(sin),
      "site",            new Token(site),
      "size",            new Token(size),
      "smiles",          new Token(smiles),
      "substructure",    new Token(substructure),  // 12.0 substructure-->smiles (should be smarts, but for legacy reasons, need this to be smiles
      "solid",           new Token(solid),
      "sort",            new Token(sort),
      "specialPosition", new Token(specialposition),
      "sqrt",            new Token(sqrt),
      "split",           new Token(split),
      "stddev",          new Token(stddev),
      "straightness",    new Token(straightness),
      "structureId",     new Token(strucid),
      "supercell",       new Token(supercell),
      "sub",             new Token(sub),
      "sum",             new Token(sum), // sum
      "sum2",            new Token(sum2), // sum of squares
      "surface",         new Token(surface),
      "surfaceDistance", new Token(surfacedistance),
      "symop",           new Token(symop),
      "temperature",     new Token(temperature),
      "relativeTemperature", null,
      "theta",           new Token(theta),
      "thisModel",       new Token(thismodel),
      "ticks",           new Token(ticks),
      "top",             new Token(top),
      "torsion",         new Token(torsion),
      "trajectory",      new Token(trajectory),
      "trajectories",    null,
      "translucent",     new Token(translucent),
      "triangles",       new Token(triangles),
      "trim",            new Token(trim),
      "type",            new Token(type),
      "ux",              new Token(unitx),
      "uy",              new Token(unity),
      "uz",              new Token(unitz),
      "uxyz",            new Token(unitxyz),
      "user",            new Token(user),
      "valence",         new Token(valence),
      "vanderWaals",     new Token(vanderwaals),
      "vdw",             null,
      "vdwRadius",       null,
      "visible",         new Token(visible),
      "volume",          new Token(volume),
      "vx",              new Token(vibx),
      "vy",              new Token(viby),
      "vz",              new Token(vibz),
      "vxyz",            new Token(vibxyz),
      "xyz",             new Token(xyz),
      "w",               new Token(w),
      "x",               new Token(x),
      "y",               new Token(y),
      "z",               new Token(z),

      // more misc parameters
      "addHydrogens",    new Token(addhydrogens),
      "allConnected",    new Token(allconnected),
      "angstroms",       new Token(angstroms),
      "anisotropy",      new Token(anisotropy),
      "arc",             new Token(arc),
      "area",            new Token(area),
      "aromatic",        new Token(aromatic),
      "arrow",           new Token(arrow),
      "auto",            new Token(auto),
      "barb",            new Token(barb),
      "binary",          new Token(binary),
      "blockData",       new Token(blockdata),
      "cancel",          new Token(cancel),
      "cap",             new Token(cap),
      "cavity",          new Token(cavity),
      "check",           new Token(check),
      "circle",          new Token(circle),
      "collapsed",       new Token(collapsed),
      "col",             new Token(col),
      "colorScheme",     new Token(colorscheme),
      "command",         new Token(command),
      "commands",        new Token(commands),
      "contour",         new Token(contour),
      "contours",        new Token(contours),
      "corners",         new Token(corners),
      "criterion",       new Token(criterion),
      "create",          new Token(create),
      "crossed",         new Token(crossed),
      "curve",           new Token(curve),
      "cutoff",          new Token(cutoff),
      "cylinder",        new Token(cylinder),
      "diameter",        new Token(diameter),
      "discrete",        new Token(discrete),
      "distanceFactor",  new Token(distancefactor),
      "downsample",      new Token(downsample),
      "eccentricity",    new Token(eccentricity),
      "ed",              new Token(ed),
      "edges",           new Token(edges),
      "energy",          new Token(energy),
      "exitJmol",        new Token(exitjmol),
      "faceCenterOffset",new Token(facecenteroffset),
      "filter",          new Token(filter),
      "first",           new Token(first),
      "fixed",           new Token(fixed),
      "fix",             null,
      "flat",            new Token(flat),
      "fps",             new Token(fps),
      "from",            new Token(from),
      "frontEdges",      new Token(frontedges),
      "full",            new Token(full),
      "fullPlane",       new Token(fullplane),
      "functionXY",      new Token(functionxy),
      "functionXYZ",     new Token(functionxyz),
      "gridPoints",      new Token(gridpoints),
      "homo",            new Token(homo),
      "ignore",          new Token(ignore),
      "increment",       new Token(increment),
      "insideout",       new Token(insideout),
      "interior",        new Token(interior),
      "intersection",    new Token(intersection),
      "internal",        new Token(internal),
      "lattice",         new Token(lattice),
      "line",            new Token(line),
      "lineData",        new Token(linedata),
      "lobe",            new Token(lobe),
      "lonePair",        new Token(lonepair),
      "lp",              new Token(lp),
      "lumo",            new Token(lumo),
      "manifest",        new Token(manifest),
      "mapProperty",     new Token(mapProperty),
      "map",             null,
      "maxSet",          new Token(maxset),
      "minSet",          new Token(minset),
      "modelBased",      new Token(modelbased),
      "molecular",       new Token(molecular),
      "mrc",             new Token(mrc),
      "msms",            new Token(msms),
      "noCross",         new Token(nocross),
      "noDebug",         new Token(nodebug),
      "noEdges",         new Token(noedges),
      "noHead",          new Token(nohead),
      "noLoad",          new Token(noload),
      "noPlane",         new Token(noplane),
      "object",          new Token(object),
      "obj",             new Token(obj),
      "offset",          new Token(offset),
      "offsetSide",      new Token(offsetside),
      "once",            new Token(once),
      "orbital",         new Token(orbital),
      "atomicOrbital",   new Token(atomicorbital),
      "packed",          new Token(packed),
      "palindrome",      new Token(palindrome),
      "path",            new Token(path),
      "pdb",             new Token(pdb),
      "period",          new Token(period),
      "periodic",        null,
      "perpendicular",   new Token(perpendicular),
      "perp",            null,
      "phase",           new Token(phase),
      "pocket",          new Token(pocket),
      "pointsPerAngstrom", new Token(pointsperangstrom),
      "radical",         new Token(radical),
      "rad",             new Token(rad),
      "reference",       new Token(reference),
      "resolution",      new Token(resolution),
      "reverseColor",    new Token(reversecolor),
      "rotate45",        new Token(rotate45),
      "selection",       new Token(selection),
      "sigma",           new Token(sigma),
      "sign",            new Token(sign),
      "silent",          new Token(silent),
      "sphere",          new Token(sphere),
      "squared",         new Token(squared),
      "steps",           new Token(steps),
      "stop",            new Token(stop),
      "title",           new Token(title),
      "titleFormat",     new Token(titleformat),
      "to",              new Token(to),
      "value",           new Token(val),
      "variable",        new Token(variable),
      "variables",       new Token(variables),
      "vertices",        new Token(vertices),
      "width",           new Token(width),

      // set params

      "backgroundModel",                          new Token(backgroundmodel),
      "debug",                                    new Token(debug),
      "defaultLattice",                           new Token(defaultlattice),
      "measurements",                             new Token(measurements),
      "measurement",                              null,
      "scale3D",                                  new Token(scale3d),
      "toggleLabel",                              new Token(togglelabel),
      "userColorScheme",                          new Token(usercolorscheme),
      "timeout",                                  new Token(timeout),
      "timeouts",                                 null,
      
      // string
      
      "appletProxy",                              new Token(appletproxy),
      "atomTypes",                                new Token(atomtypes),
      "axesColor",                                new Token(axescolor),
      "axis1Color",                               new Token(axis1color),
      "axis2Color",                               new Token(axis2color),
      "axis3Color",                               new Token(axis3color),
      "backgroundColor",                          new Token(backgroundcolor),
      "bondmode",                                 new Token(bondmode),
      "boundBoxColor",                            new Token(boundboxcolor),
      "boundingBoxColor",                         null,
      "currentLocalPath",                         new Token(currentlocalpath),
      "dataSeparator",                            new Token(dataseparator),
      "defaultAngleLabel",                        new Token(defaultanglelabel),
      "defaultColorScheme",                       new Token(defaultcolorscheme),
      "defaultColors",                            null,
      "defaultDirectory",                         new Token(defaultdirectory),
      "defaultDistanceLabel",                     new Token(defaultdistancelabel),
      "defaultLoadFilter",                        new Token(defaultloadfilter),
      "defaultLoadScript",                        new Token(defaultloadscript),
      "defaults",                                 new Token(defaults),
      "defaultTorsionLabel",                      new Token(defaulttorsionlabel),
      "defaultVDW",                               new Token(defaultvdw),
      "edsUrlCutoff",                             new Token(edsurlcutoff),
      "edsUrlFormat",                             new Token(edsurlformat),
      "fileCacheDirectory",                       new Token(filecachedirectory),
      "fontsize",                                 new Token(fontsize),
      "helpPath",                                 new Token(helppath),
      "hoverLabel",                               new Token(hoverlabel),
      "language",                                 new Token(language),
      "loadFormat",                               new Token(loadformat),
      "logFile",                                  new Token(logfile),
      "measurementUnits",                         new Token(measurementunits),
      "picking",                                  new Token(picking),
      "pickingStyle",                             new Token(pickingstyle),
      "pickLabel",                                new Token(picklabel),
      "propertyColorScheme",                      new Token(propertycolorscheme),
      "quaternionFrame",                          new Token(quaternionframe),
      "smilesUrlFormat",                          new Token(smilesurlformat),
      "unitCellColor",                            new Token(unitcellcolor),

      // float
      
      "axesScale",                                new Token(axesscale),
      "axisScale",                                null, // legacy
      "bondTolerance",                            new Token(bondtolerance),
      "cameraDepth",                              new Token(cameradepth),
      "defaultDrawArrowScale",                    new Token(defaultdrawarrowscale),
      "defaultTranslucent",                       new Token(defaulttranslucent),
      "dipoleScale",                              new Token(dipolescale),
      "ellipsoidAxisDiameter",                    new Token(ellipsoidaxisdiameter),
      "gestureSwipeFactor",                       new Token(gestureswipefactor),
      "hbondsAngleMinimum",                       new Token(hbondsangleminimum),
      "hbondsDistanceMaximum",                    new Token(hbondsdistancemaximum),
      "hoverDelay",                               new Token(hoverdelay),
      "loadAtomDataTolerance",                    new Token(loadatomdatatolerance),
      "minBondDistance",                          new Token(minbonddistance),
      "minimizationCriterion",                    new Token(minimizationcriterion),
      "mouseDragFactor",                          new Token(mousedragfactor),
      "mouseWheelFactor",                         new Token(mousewheelfactor),
      "navFPS",                                   new Token(navfps),
      "navigationDepth",                          new Token(navigationdepth),
      "navigationSlab",                           new Token(navigationslab),
      "navigationSpeed",                          new Token(navigationspeed),
      "navX",                                     new Token(navx),
      "navY",                                     new Token(navy),
      "navZ",                                     new Token(navz),
      "pointGroupDistanceTolerance",              new Token(pointgroupdistancetolerance),
      "pointGroupLinearTolerance",                new Token(pointgrouplineartolerance),
      "radius",                                   new Token(radius),
      "rotationRadius",                           new Token(rotationradius),
      "scaleAngstromsPerInch",                    new Token(scaleangstromsperinch),
      "sheetSmoothing",                           new Token(sheetsmoothing),
      "slabRange",                                new Token(slabrange),
      "solventProbeRadius",                       new Token(solventproberadius),
      "spinFPS",                                  new Token(spinfps),
      "spinX",                                    new Token(spinx),
      "spinY",                                    new Token(spiny),
      "spinZ",                                    new Token(spinz),
      "stereoDegrees",                            new Token(stereodegrees),
      "strutDefaultRadius",                       new Token(strutdefaultradius),
      "strutLengthMaximum",                       new Token(strutlengthmaximum),
      "vectorScale",                              new Token(vectorscale),
      "vibrationPeriod",                          new Token(vibrationperiod),
      "vibrationScale",                           new Token(vibrationscale),
      "visualRange",                              new Token(visualrange),

      // int

      "ambientPercent",                           new Token(ambientpercent),
      "ambient",                                  null, 
      "animationFps",                             new Token(animationfps),
      "axesMode",                                 new Token(axesmode),
      "bondRadiusMilliAngstroms",                 new Token(bondradiusmilliangstroms),
      "delayMaximumMs",                           new Token(delaymaximumms),
      "diffusePercent",                           new Token(diffusepercent),
      "diffuse",                                  null, 
      "dotDensity",                               new Token(dotdensity),
      "dotScale",                                 new Token(dotscale),
      "ellipsoidDotCount",                        new Token(ellipsoiddotcount),
      "helixStep",                                new Token(helixstep),
      "hermiteLevel",                             new Token(hermitelevel),
      "historyLevel",                             new Token(historylevel),
      "logLevel",                                 new Token(loglevel),
      "minimizationSteps",                        new Token(minimizationsteps),
      "percentVdwAtom",                           new Token(percentvdwatom),
      "perspectiveModel",                         new Token(perspectivemodel),
      "phongExponent",                            new Token(phongexponent),
      "pickingSpinRate",                          new Token(pickingspinrate),
      "propertyAtomNumberField",                  new Token(propertyatomnumberfield),
      "propertyAtomNumberColumnCount",            new Token(propertyatomnumbercolumncount),
      "propertyDataColumnCount",                  new Token(propertydatacolumncount),
      "propertyDataField",                        new Token(propertydatafield),
      "repaintWaitMs",                            new Token(repaintwaitms),
      "ribbonAspectRatio",                        new Token(ribbonaspectratio),
      "scriptReportingLevel",                     new Token(scriptreportinglevel),
      "showScript",                               new Token(showscript),
      "smallMoleculeMaxAtoms",                    new Token(smallmoleculemaxatoms),
      "specular",                                 new Token(specular),
      "specularExponent",                         new Token(specularexponent),
      "specularPercent",                          new Token(specularpercent),
      "specPercent",                              null,
      "specularPower",                            new Token(specularpower),
      "specpower",                                null, 
      "strandCount",                              new Token(strandcount),
      "strandCountForMeshRibbon",                 new Token(strandcountformeshribbon),
      "strandCountForStrands",                    new Token(strandcountforstrands),
      "strutSpacing",                             new Token(strutspacing),
      "zDepth",                                   new Token(zdepth),
      "zSlab",                                    new Token(zslab),
      "zshadePower",                              new Token(zshadepower),

      // boolean

      "allowEmbeddedScripts",                     new Token(allowembeddedscripts),
      "allowGestures",                            new Token(allowgestures),
      "allowKeyStrokes",                          new Token(allowkeystrokes),
      "allowModelKit",                            new Token(allowmodelkit),
      "allowMoveAtoms",                           new Token(allowmoveatoms),
      "allowMultiTouch",                          new Token(allowmultitouch),
      "allowRotateSelected",                      new Token(allowrotateselected),
      "antialiasDisplay",                         new Token(antialiasdisplay),
      "antialiasImages",                          new Token(antialiasimages),
      "antialiasTranslucent",                     new Token(antialiastranslucent),
      "appendNew",                                new Token(appendnew),
      "applySymmetryToBonds",                     new Token(applysymmetrytobonds),
      "atomPicking",                              new Token(atompicking),
      "autobond",                                 new Token(autobond),
      "autoFPS",                                  new Token(autofps),
//      "autoLoadOrientation",                      new Token(autoloadorientation),
      "axesMolecular",                            new Token(axesmolecular),
      "axesOrientationRasmol",                    new Token(axesorientationrasmol),
      "axesUnitCell",                             new Token(axesunitcell),
      "axesWindow",                               new Token(axeswindow),
      "bondModeOr",                               new Token(bondmodeor),
      "bondPicking",                              new Token(bondpicking),
      "bonds",                                    new Token(bonds),
      "bond",                                     null, 
      "cartoonBaseEdges",                         new Token(cartoonbaseedges),
      "cartoonRockets",                           new Token(cartoonrockets),
      "chainCaseSensitive",                       new Token(chaincasesensitive),
      "colorRasmol",                              new Token(colorrasmol),
      "debugScript",                              new Token(debugscript),
      "defaultStructureDssp",                     new Token(defaultstructuredssp),
      "disablePopupMenu",                         new Token(disablepopupmenu),
      "displayCellParameters",                    new Token(displaycellparameters),
      "dotsSelectedOnly",                         new Token(dotsselectedonly),
      "dotSurface",                               new Token(dotsurface),
      "dragSelected",                             new Token(dragselected),
      "drawHover",                                new Token(drawhover),
      "drawPicking",                              new Token(drawpicking),
      "dsspCalculateHydrogenAlways",              new Token(dsspcalchydrogen),
      "dynamicMeasurements",                      new Token(dynamicmeasurements),
      "ellipsoidArcs",                            new Token(ellipsoidarcs),
      "ellipsoidAxes",                            new Token(ellipsoidaxes),
      "ellipsoidBall",                            new Token(ellipsoidball),
      "ellipsoidDots",                            new Token(ellipsoiddots),
      "ellipsoidFill",                            new Token(ellipsoidfill),
      "fileCaching",                              new Token(filecaching),
      "fontCaching",                              new Token(fontcaching),
      "fontScaling",                              new Token(fontscaling),
      "forceAutoBond",                            new Token(forceautobond),
      "fractionalRelative",                       new Token(fractionalrelative),
// see commands     "frank",                                    new Token(frank),
      "greyscaleRendering",                       new Token(greyscalerendering),
      "hbondsBackbone",                           new Token(hbondsbackbone),
      "hbondsRasmol",                             new Token(hbondsrasmol),
      "hbondsSolid",                              new Token(hbondssolid),
      "hetero",                                   new Token(hetero),
      "hideNameInPopup",                          new Token(hidenameinpopup),
      "hideNavigationPoint",                      new Token(hidenavigationpoint),
      "hideNotSelected",                          new Token(hidenotselected),
      "highResolution",                           new Token(highresolution),
      "hydrogen",                                 new Token(hydrogen),
      "hydrogens",                                null,
      "imageState",                               new Token(imagestate),
      "isKiosk",                                  new Token(iskiosk),
      "isosurfacePropertySmoothing",              new Token(isosurfacepropertysmoothing),
      "isosurfacePropertySmoothingPower",         new Token(isosurfacepropertysmoothingpower),
      "justifyMeasurements",                      new Token(justifymeasurements),
      "languageTranslation",                      new Token(languagetranslation),
      "legacyAutoBonding",                        new Token(legacyautobonding),
      "logCommands",                              new Token(logcommands),
      "logGestures",                              new Token(loggestures),
      "measureAllModels",                         new Token(measureallmodels),
      "measurementLabels",                        new Token(measurementlabels),
      "measurementNumbers",                       new Token(measurementnumbers),
      "messageStyleChime",                        new Token(messagestylechime),
      "minimizationRefresh",                      new Token(minimizationrefresh),
      "minimizationSilent",                       new Token(minimizationsilent),
      "modelkitMode",                             new Token(modelkitmode),
      "monitorEnergy",                            new Token(monitorenergy),
      "multipleBondRadiusFactor",                 new Token(multiplebondradiusfactor),
      "multipleBondSpacing",                      new Token(multiplebondspacing),
      "multiProcessor",                           new Token(multiprocessor),
      "navigateSurface",                          new Token(navigatesurface),
      "navigationMode",                           new Token(navigationmode),
      "navigationPeriodic",                       new Token(navigationperiodic),
      "pdbGetHeader",                             new Token(pdbgetheader),
      "pdbSequential",                            new Token(pdbsequential),
      "perspectiveDepth",                         new Token(perspectivedepth),
      "preserveState",                            new Token(preservestate),
      "rangeSelected",                            new Token(rangeselected),
      "refreshing",                               new Token(refreshing),
      "ribbonBorder",                             new Token(ribbonborder),
      "rocketBarrels",                            new Token(rocketbarrels),
      "saveProteinStructureState",                new Token(saveproteinstructurestate),
      "scriptQueue",                              new Token(scriptqueue),
      "selectAllModels",                          new Token(selectallmodels),
      "selectHetero",                             new Token(selecthetero),
      "selectHydrogen",                           new Token(selecthydrogen),
// see commands     "selectionHalos",                           new Token(selectionhalo),
      "showAxes",                                 new Token(showaxes),
      "showBoundBox",                             new Token(showboundbox),
      "showBoundingBox",                          null,
      "showFrank",                                new Token(showfrank),
      "showHiddenSelectionHalos",                 new Token(showhiddenselectionhalos),
      "showHydrogens",                            new Token(showhydrogens),
      "showKeyStrokes",                           new Token(showkeystrokes),
      "showMeasurements",                         new Token(showmeasurements),
      "showMultipleBonds",                        new Token(showmultiplebonds),
      "showNavigationPointAlways",                new Token(shownavigationpointalways),
// see intparam      "showScript",                               new Token(showscript),
      "showUnitcell",                             new Token(showunitcell),
      "slabByAtom",                               new Token(slabbyatom),
      "slabByMolecule",                           new Token(slabbymolecule),
      "slabEnabled",                              new Token(slabenabled),
      "smartAromatic",                            new Token(smartaromatic),
      "solvent",                                  new Token(solvent),
      "solventProbe",                             new Token(solventprobe),
// see intparam     "specular",                                 new Token(specular),
      "ssBondsBackbone",                          new Token(ssbondsbackbone),
      "statusReporting",                          new Token(statusreporting),
      "strutsMultiple",                           new Token(strutsmultiple),
      "syncMouse",                                new Token(syncmouse),
      "syncScript",                               new Token(syncscript),
      "testFlag1",                                new Token(testflag1),
      "testFlag2",                                new Token(testflag2),
      "testFlag3",                                new Token(testflag3),
      "testFlag4",                                new Token(testflag4),
      "traceAlpha",                               new Token(tracealpha),
      "undo",                                     new Token(undo),
      "useArcBall",                               new Token(usearcball),
      "useMinimizationThread",                    new Token(useminimizationthread),
      "useNumberLocalization",                    new Token(usenumberlocalization),
      "waitForMoveTo",                            new Token(waitformoveto),
      "windowCentered",                           new Token(windowcentered),
      "wireframeRotation",                        new Token(wireframerotation),
      "zeroBasedXyzRasmol",                       new Token(zerobasedxyzrasmol),
      "zoomEnabled",                              new Token(zoomenabled),
      "zoomLarge",                                new Token(zoomlarge),
      "zShade",                                   new Token(zshade),

    };

    Token tokenLast = null;
    String stringThis;
    Token tokenThis;
    String lcase;
    for (int i = 0; i + 1 < arrayPairs.length; i += 2) {
      stringThis = (String) arrayPairs[i];
      lcase = stringThis.toLowerCase();
      tokenThis = (Token) arrayPairs[i + 1];
      if (tokenThis == null)
        tokenThis = tokenLast;
      if (tokenThis.value == null)
        tokenThis.value = stringThis;
      if (tokenMap.get(lcase) != null)
        Logger.error("duplicate token definition:" + lcase);
      tokenMap.put(lcase, tokenThis);
      tokenLast = tokenThis;
    }
    Logger.info(arrayPairs.length + " script command tokens");
  }

}
