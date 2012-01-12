/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-12 07:58:28 -0500 (Fri, 12 Jun 2009) $
 * $Revision: 11009 $
 *
 * Copyright (C) 2003-2006  Miguel, Jmol Development, www.jmol.org
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
import java.util.BitSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.api.JmolEdge;
import org.jmol.api.JmolMolecule;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.BoxInfo;
import org.jmol.modelset.MeasurementData;
import org.jmol.modelset.Bond.BondSet;
import org.jmol.script.ScriptEvaluator.ScriptException;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.ColorEncoder;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Point3fi;
import org.jmol.util.Quaternion;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.PropertyManager;
import org.jmol.viewer.Viewer;

class ScriptMathProcessor {
  /**
   * Reverse Polish Notation Engine for IF, SET, and %{...} -- Bob Hanson
   * 2/16/2007 Just a (not so simple?) RPN processor that can handle boolean,
   * int, float, String, Point3f, BitSet, Array, Hashtable, Matrix3f, Matrix4f
   * 
   * hansonr@stolaf.edu
   * 
   */

  private boolean isSyntaxCheck;
  private boolean wasSyntaxCheck;
  private boolean logMessages;
  private ScriptEvaluator eval;
  private Viewer viewer;

  private Token[] oStack = new Token[8];
  private ScriptVariable[] xStack = new ScriptVariable[8];
  private char[] ifStack = new char[8];
  private int ifPt = -1;
  private int oPt = -1;
  private int xPt = -1;
  private int parenCount;
  private int squareCount;
  private int braceCount;
  private boolean wasX;
  private int incrementX;
  private boolean isArrayItem;
  private boolean asVector;
  private boolean asBitSet;
  private int ptid = 0;
  private int ptx = Integer.MAX_VALUE;

  ScriptMathProcessor(ScriptEvaluator eval, boolean isArrayItem,
      boolean asVector, boolean asBitSet) {
    this.eval = eval;
    this.viewer = eval.viewer;
    this.logMessages = eval.logMessages;
    this.isSyntaxCheck = wasSyntaxCheck = eval.isSyntaxCheck;
    this.isArrayItem = isArrayItem;
    this.asVector = asVector || isArrayItem;
    this.asBitSet = asBitSet;
    wasX = isArrayItem;
    if (logMessages)
      Logger.info("initialize RPN");
  }

  @SuppressWarnings("unchecked")
  ScriptVariable getResult(boolean allowUnderflow)
      throws ScriptException {
    boolean isOK = true;
    ScriptVariable x = null;
    while (isOK && oPt >= 0)
      isOK = operate();
    if (isOK) {
      if (asVector) {
        List<ScriptVariable> result = new ArrayList<ScriptVariable>();
        for (int i = 0; i <= xPt; i++)
          result.add(ScriptVariable.selectItem(xStack[i]));
        return new ScriptVariable(Token.vector, result);
      }
      if (xPt == 0) {
        if (x == null)
          x = xStack[0];
        if (x.tok == Token.bitset || x.tok == Token.varray
            || x.tok == Token.string || x.tok == Token.matrix3f
            || x.tok == Token.matrix4f)
          x = ScriptVariable.selectItem(x);
        if (asBitSet && x.tok == 
          Token.varray)
          x = new ScriptVariable(Token.bitset, ScriptVariable.unEscapeBitSetArray((ArrayList<ScriptVariable>)x.value, false));
        return x;
      }
    }
    if (!allowUnderflow && (xPt >= 0 || oPt >= 0)) {
      // iToken--;
      eval.error(ScriptEvaluator.ERROR_invalidArgument);
    }
    return null;
  }

  private void putX(ScriptVariable x) {
    // System.out.println("putX skipping : " + skipping + " " + x);
    if (skipping)
      return;
    if (++xPt == xStack.length)
      xStack = (ScriptVariable[]) ArrayUtil.doubleLength(xStack);
    if (logMessages) {
      Logger.info("\nputX: " + x);
    }

    xStack[xPt] = x;
    ptx = ++ptid;
  }

  private void putOp(Token op) {
    if (++oPt >= oStack.length)
      oStack = (Token[]) ArrayUtil.doubleLength(oStack);
    oStack[oPt] = op;
    ptid++;
  }

  private void putIf(char c) {
    if (++ifPt >= ifStack.length)
      ifStack = (char[]) ArrayUtil.doubleLength(ifStack);
    ifStack[ifPt] = c;
  }

  boolean addX(ScriptVariable x) {
    // the standard entry point
    putX(x);
    return wasX = true;
  }

  boolean addX(Object x) {
    // the standard entry point
    ScriptVariable v = ScriptVariable.getVariable(x);
    if (v == null)
      return false;
    putX(v);
    return wasX = true;
  }

  boolean addX(BitSet bs) {
    // the standard entry point for bit sets
    ScriptVariable v = new ScriptVariable(Token.bitset, bs);
    if (v == null)
      return false;
    putX(v);
    return wasX = true;
  }

  boolean addX(Point3f pt) {
    // the standard entry point for bit sets
    ScriptVariable v = new ScriptVariable(Token.point3f, pt);
    if (v == null)
      return false;
    putX(v);
    return wasX = true;
  }

  boolean addXNum(ScriptVariable x) throws ScriptException {
    // corrects for x -3 being x - 3
    // only when coming from expression() or parameterExpression()
    if (wasX)
      switch (x.tok) {
      case Token.integer:
        if (x.intValue < 0) {
          addOp(Token.tokenMinus);
          x = ScriptVariable.intVariable(-x.intValue);
        }
        break;
      case Token.decimal:
        float f = ((Float) x.value).floatValue();
        if (f < 0 || f == 0 && 1 / f == Float.NEGATIVE_INFINITY) {
          addOp(Token.tokenMinus);
          x = new ScriptVariable(Token.decimal, new Float(-f));
        }
        break;
      }
    putX(x);
    return wasX = true;
  }

  private boolean addX(boolean x) {
    putX(ScriptVariable.getVariable(x ? Boolean.TRUE : Boolean.FALSE));
    return wasX = true;
  }

  private boolean addX(int x) {
    // no check for unary minus
    putX(ScriptVariable.intVariable(x));
    return wasX = true;
  }

  private boolean addX(float x) {
    // no check for unary minus
    return Float.isNaN(x) ? addX("NaN") : addX(new Float(x));
  }

  private static boolean isOpFunc(Token op) {
    return (Token.tokAttr(op.tok, Token.mathfunc) && op != Token.tokenArraySquare 
        || op.tok == Token.propselector
        && Token.tokAttr(op.intValue, Token.mathfunc));
  }

  private boolean skipping;

  /**
   * addOp The primary driver of the Reverse Polish Notation evaluation engine.
   * 
   * This method loads operators onto the oStack[] and processes them based on a
   * precedence system. Operands are added by addX() onto the xStack[].
   * 
   * We check here for syntax issues that were not caught in the compiler. I
   * suppose that should be done at compilation stage, but this is how it is for
   * now.
   * 
   * The processing of functional arguments and (___?___:___) constructs is
   * carried out by pushing markers onto the stacks that later can be used to
   * fill argument lists or turn "skipping" on or off. Note that in the case of
   * skipped sections of ( ? : ) no attempt is made to do syntax checking.
   * [That's not entirely true -- when syntaxChecking is true, that is, when the
   * user is typing at the Jmol application console, then this code is being
   * traversed with dummy variables. That could be improved, for sure.
   * 
   * Actually, there's plenty of room for improvement here. I did this based on
   * what I learned in High School in 1974 -- 35 years ago! -- when I managed to
   * build a mini FORTRAN compiler from scratch in machine code. That was fun.
   * (This was fun, too.)
   * 
   * -- Bob Hanson, hansonr@stolaf.edu 6/9/2009
   * 
   * 
   * @param op
   * @return false if an error condition arises
   * @throws ScriptException
   */
  boolean addOp(Token op) throws ScriptException {
    return addOp(op, true);
  }

  private boolean haveSpaceBeforeSquare;
  private int equalCount;
  
  boolean addOp(Token op, boolean allowMathFunc) throws ScriptException {

    if (logMessages) {

      Logger.info("addOp entry\naddOp: " + op ); //+ " oPt=" + oPt + " ifPt = "
         // + ifPt + " skipping=" + skipping + " wasX=" + wasX);
    }

    // are we skipping due to a ( ? : ) construct?
    int tok0 = (oPt >= 0 ? oStack[oPt].tok : 0);
    skipping = (ifPt >= 0 && (ifStack[ifPt] == 'F' || ifStack[ifPt] == 'X'));
    if (skipping) {
      switch (op.tok) {
      case Token.leftparen:
        putOp(op);
        return true;
      case Token.colon:
        // dumpStacks("skipping -- :");
        if (tok0 != Token.colon || ifStack[ifPt] == 'X')
          return true; // ignore if not a clean opstack or T already processed
        // no object here because we were skipping
        // set to flag end of this parens
        ifStack[ifPt] = 'T';
        wasX = false;
        // dumpStacks("(..False...? .skip.. :<--here.... )");
        skipping = false;
        return true;
      case Token.rightparen:
        if (tok0 == Token.leftparen) {
          oPt--; // clear opstack
          return true;
        }
        // dumpStacks("skipping -- )");
        if (tok0 != Token.colon) {
          putOp(op);
          return true;
        }
        wasX = true;
        // and remove markers
        ifPt--;
        oPt -= 2;
        skipping = false;
        // dumpStacks("(..True...? ... : ...skip...)<--here ");
        return true;
      default:
        return true;
      }
    }

    // Do we have the appropriate context for this operator?

    Token newOp = null;
    int tok;
    boolean isLeftOp = false;
    boolean isDotSelector = (op.tok == Token.propselector);

    if (isDotSelector && !wasX)
      return false;

    boolean isMathFunc = (allowMathFunc && isOpFunc(op));

    // the word "plane" can also appear alone, not as a function
    if (oPt >= 1 && op.tok != Token.leftparen && tok0 == Token.plane)
      tok0 = oStack[--oPt].tok;

    // math functions as arguments appear without a prefixing operator
    boolean isArgument = (oPt >= 1 && tok0 == Token.leftparen);

    switch (op.tok) {
    case Token.spacebeforesquare:
      haveSpaceBeforeSquare = true;
      return true;
    case Token.comma:
      if (!wasX)
        return false;
      break;
    case Token.min:
    case Token.max:
    case Token.average:
    case Token.sum:
    case Token.sum2:
    case Token.stddev:
    case Token.minmaxmask:
      tok = (oPt < 0 ? Token.nada : tok0);
      if (!wasX
          || !(tok == Token.propselector || tok == Token.bonds || tok == Token.atoms))
        return false;
      oStack[oPt].intValue |= op.tok;
      return true;
    case Token.leftsquare: // {....}[n][m]
      isLeftOp = true;
      if (!wasX || haveSpaceBeforeSquare) {
        squareCount++;
        op = newOp = Token.tokenArraySquare;
        haveSpaceBeforeSquare = false;
      }
      break;
    case Token.rightsquare:
      break;
    case Token.minusMinus:
    case Token.plusPlus:
      incrementX = (op.tok == Token.plusPlus ? 1 : -1);
      if (ptid == ptx) {
        if (isSyntaxCheck)
          return true;
        ScriptVariable x = xStack[xPt];
        xStack[xPt] = (new ScriptVariable()).set(x, false);
        return x.increment(incrementX);
      }
      break;
    case Token.minus:
      if (wasX)
        break;
      addX(0);
      op = new ScriptVariable(Token.unaryMinus, "-");
      break;
    case Token.rightparen: // () without argument allowed only for math funcs
      if (!wasX && oPt >= 1 && tok0 == Token.leftparen
          && !isOpFunc(oStack[oPt - 1]))
        return false;
      break;
    case Token.opNot:
    case Token.leftparen:
      isLeftOp = true;
      // fall through
    default:
      if (isMathFunc) {
        if (!isDotSelector && wasX && !isArgument)
          return false;
        newOp = op;
        isLeftOp = true;
        break;
      }
      if (wasX == isLeftOp && tok0 != Token.propselector) // for now, because
        // we have .label
        // and .label()
        return false;
      break;
    }

    // do we need to operate?

    while (oPt >= 0
        && tok0 != Token.colon
        && (!isLeftOp || tok0 == Token.propselector
            && (op.tok == Token.propselector || op.tok == Token.leftsquare))
        && Token.getPrecedence(tok0) >= Token.getPrecedence(op.tok)) {

      if (logMessages) {
        Logger.info("\noperating, oPt=" + oPt + " isLeftOp=" + isLeftOp
            + " oStack[oPt]=" + Token.nameOf(tok0) + "        prec="
            + Token.getPrecedence(tok0) + " pending op=\""
            + Token.nameOf(op.tok) + "\" prec=" + Token.getPrecedence(op.tok));
        dumpStacks("operating");
      }
      // ) and ] must wait until matching ( or [ is found
      if (op.tok == Token.rightparen && tok0 == Token.leftparen) {
        // (x[2]) finalizes the selection
        if (xPt >= 0)
          xStack[xPt] = ScriptVariable.selectItem(xStack[xPt]);
        break;
      }
      if (op.tok == Token.rightsquare && tok0 == Token.array) {
        break;
      }
      if (op.tok == Token.rightsquare && tok0 == Token.leftsquare) {
        if (isArrayItem && squareCount == 1 && equalCount == 0) {
          addX(new ScriptVariable(Token.tokenArraySelector));
          break;
        }
        if (!doBitsetSelect())
          return false;
        break;
      }

      // if not, it's time to operate

      if (!operate())
        return false;
      tok0 = (oPt >= 0 ? oStack[oPt].tok : 0);
    }

    // now add a marker on the xStack if necessary

    if (newOp != null)
      addX(new ScriptVariable(Token.opEQ, newOp));

    // fix up counts and operand flag
    // right ) and ] are not added to the stack

    switch (op.tok) {
    case Token.leftparen:
      // System.out.println("----------(----------");
      parenCount++;
      wasX = false;
      break;
    case Token.opIf:
      // System.out.println("---------IF---------");
      boolean isFirst = ScriptVariable.bValue(getX());
      if (tok0 == Token.colon)
        ifPt--;
      else
        putOp(Token.tokenColon);
      putIf(isFirst ? 'T' : 'F');
      skipping = !isFirst;
      wasX = false;
      // dumpStacks("(.." + isFirst + "...?<--here ... :...skip...) ");
      return true;
    case Token.colon:
      // System.out.println("----------:----------");
      if (tok0 != Token.colon)
        return false;
      if (ifPt < 0)
        return false;
      ifStack[ifPt] = 'X';
      wasX = false;
      skipping = true;
      // dumpStacks("(..True...? ... :<--here ...skip...) ");
      return true;
    case Token.rightparen:
      // System.out.println("----------)----------");
      wasX = true;
      if (parenCount-- <= 0)
        return false;
      if (tok0 == Token.colon) {
        // remove markers
        ifPt--;
        oPt--;
        // dumpStacks("(..False...? ...skip... : ...)<--here ");
      }
      oPt--;
      if (oPt < 0)
        return true;
      if (isOpFunc(oStack[oPt]) && !evaluateFunction(0))
        return false;
      skipping = (ifPt >= 0 && ifStack[ifPt] == 'X');
      return true;
    case Token.comma:
      wasX = false;
      return true;
    case Token.leftsquare:
      squareCount++;
      wasX = false;
      break;
    case Token.rightsquare:
      wasX = true;
      if (squareCount-- <= 0 || oPt < 0)
        return false;
      if (oStack[oPt].tok == Token.array)
        return evaluateFunction(Token.leftsquare);
      oPt--;
      return true;
    case Token.propselector:
      wasX = (!allowMathFunc || !Token.tokAttr(op.intValue, Token.mathfunc));
      break;
    case Token.leftbrace:
      braceCount++;
      wasX = false;
      break;
    case Token.rightbrace:
      if (braceCount-- <= 0)
        return false;
      wasX = false;
      break;
    case Token.opAnd:
    case Token.opOr:
      if (!wasSyntaxCheck && xStack[xPt].tok != Token.bitset && xStack[xPt].tok != Token.varray) {
        // check to see if we need to evaluate the second operand or not
        // if not, then set this to syntax check in order to skip :)
        // Jmol 12.0.4, Jmol 12.1.2
        boolean tf = ScriptVariable.bValue(getX());
        addX(ScriptVariable.getBoolean(tf));
        if (tf == (op.tok == Token.opOr)) { // TRUE or.. FALSE and...
          isSyntaxCheck = true;
          op = (op.tok == Token.opOr ? Token.tokenOrTRUE : Token.tokenAndFALSE);
        }
      }
      wasX = false;
      break;
    case Token.opEQ:
      if (squareCount == 0)
        equalCount++;
      wasX = false;
      break;
    default:
      wasX = false;
    }

    // add the operator if possible

    putOp(op);

    // dumpStacks("putOp complete");
    if (op.tok == Token.propselector
        && (op.intValue & ~Token.minmaxmask) == Token.function
        && op.intValue != Token.function) {
      return evaluateFunction(0);
    }
    return true;
  }

  private boolean doBitsetSelect() {
    if (xPt < 0 || xPt == 0 && !isArrayItem) {
      return false;
    }
    ScriptVariable var1 = xStack[xPt--];
    ScriptVariable var = xStack[xPt];
    if (var.tok == Token.hash) {
      ScriptVariable v = var.mapValue(ScriptVariable.sValue(var1));
      xStack[xPt] = (v == null ? ScriptVariable.getVariable("") : v);
      return true;
    }
    int i = ScriptVariable.iValue(var1);
    switch (var.tok) {
    default:
      var = new ScriptVariable(Token.string, ScriptVariable.sValue(var));
      // fall through
    case Token.bitset:
    case Token.varray:
    case Token.string:
    case Token.matrix3f:
    case Token.matrix4f:
      xStack[xPt] = ScriptVariable.selectItem(var, i);
      break;
    }
    return true;
  }

  void dumpStacks(String message) {
    Logger.info("\n\n------------------\nRPN stacks: " + message + "\n");
    for (int i = 0; i <= xPt; i++)
      Logger.info("x[" + i + "]: " + xStack[i]);
    Logger.info("\n");
    for (int i = 0; i <= oPt; i++)
      Logger.info("o[" + i + "]: " + oStack[i] + " prec="
          + Token.getPrecedence(oStack[i].tok));
    Logger.info(" ifStack = " + (new String(ifStack)).substring(0, ifPt + 1));
  }

  private ScriptVariable getX() throws ScriptException {
    if (xPt < 0)
      eval.error(ScriptEvaluator.ERROR_endOfStatementUnexpected);
    ScriptVariable v = ScriptVariable.selectItem(xStack[xPt]);
    xStack[xPt--] = null;
    return v;
  }

  private boolean evaluateFunction(int tok) throws ScriptException {
    Token op = oStack[oPt--];
    // for .xxx or .xxx() functions
    // we store the token in the intValue field of the propselector token
    if (tok == 0)
      tok = (op.tok == Token.propselector ? op.intValue & ~Token.minmaxmask
        : op.tok);

    int nParamMax = Token.getMaxMathParams(tok); // note - this is NINE for
    // dot-operators
    int nParam = 0;
    int pt = xPt;
    while (pt >= 0 && xStack[pt--].value != op)
      nParam++;
    if (nParamMax > 0 && nParam > nParamMax)
      return false;
    ScriptVariable[] args = new ScriptVariable[nParam];
    for (int i = nParam; --i >= 0;)
      args[i] = getX();
    xPt--;
    // no script checking of functions because
    // we cannot know what variables are real
    // if this is a property selector, as in x.func(), then we
    // just exit; otherwise we add a new TRUE to xStack
    if (isSyntaxCheck)
      return (op.tok == Token.propselector ? true : addX(true));
    switch (tok) {
    case Token.abs:
    case Token.acos:
    case Token.cos:
    case Token.now:
    case Token.sin:
    case Token.sqrt:
      return evaluateMath(args, tok);
    case Token.add:
    case Token.div:
    case Token.mul:
    case Token.sub:
      return evaluateList(op.intValue, args);
    case Token.array:
    case Token.leftsquare:
      return evaluateArray(args, tok == Token.leftsquare);
    case Token.axisangle:
    case Token.quaternion:
      return evaluateQuaternion(args, tok);
    case Token.bin:
      return evaluateBin(args);
    case Token.col:
    case Token.row:
      return evaluateRowCol(args, tok);
    case Token.color:
      return evaluateColor(args);
    case Token.compare:
      return evaluateCompare(args);
    case Token.connected:
      return evaluateConnected(args);
    case Token.cross:
      return evaluateCross(args);
    case Token.data:
      return evaluateData(args);
    case Token.angle:
    case Token.distance:
    case Token.dot:
    case Token.measure:
      if ((tok == Token.distance || tok == Token.dot) 
          && op.tok == Token.propselector)
        return evaluateDot(args, tok);
      return evaluateMeasure(args, op.tok);
    case Token.file:
    case Token.load:
      return evaluateLoad(args, tok);
    case Token.find:
      return evaluateFind(args);
    case Token.function:
      return evaluateUserFunction((String) op.value, args, op.intValue,
          op.tok == Token.propselector);
    case Token.format:
    case Token.label:
      return evaluateLabel(op.intValue, args);
    case Token.getproperty:
      return evaluateGetProperty(args);
    case Token.helix:
      return evaluateHelix(args);
    case Token.hkl:
    case Token.plane:
    case Token.intersection:
      return evaluatePlane(args, tok);
    case Token.javascript:
    case Token.script:
      return evaluateScript(args, tok);
    case Token.join:
    case Token.split:
    case Token.trim:
      return evaluateString(op.intValue, args);
    case Token.point:
      return evaluatePoint(args);
    case Token.prompt:
      return evaluatePrompt(args);
    case Token.random:
      return evaluateRandom(args);
    case Token.replace:
      return evaluateReplace(args);
    case Token.search:
    case Token.smiles:
    case Token.substructure:
      return evaluateSubstructure(args, tok);
    case Token.sort:
      return evaluateSort(args);
    case Token.symop:
      return evaluateSymop(args, op.tok == Token.propselector);
    case Token.volume:
      return evaluateVolume(args);
    case Token.within:
      return evaluateWithin(args);
    case Token.write:
      return evaluateWrite(args);
    }
    return false;
  }

  private boolean evaluateCompare(ScriptVariable[] args) throws ScriptException {
    // compare({bitset} or [{positions}],{bitset} or [{positions}] [,"stddev"])
    // compare({bitset},{bitset}[,"SMARTS"|"SMILES"],smilesString [,"stddev"])
    // returns matrix4f for rotation/translation or stddev
    // compare({bitset},{bitset},"ISOMER")  12.1.5

    if (args.length < 2 || args.length > 5)
      return false;
    float stddev;
    String sOpt = ScriptVariable.sValue(args[args.length - 1]);
    boolean isStdDev = sOpt.equalsIgnoreCase("stddev");
    boolean isIsomer = sOpt.equalsIgnoreCase("ISOMER");
    boolean isSmiles = (!isIsomer && args.length > (isStdDev ? 3 : 2));
    BitSet bs1 = (args[0].tok == Token.bitset ? (BitSet) args[0].value : null);
    BitSet bs2 = (args[1].tok == Token.bitset ? (BitSet) args[1].value : null);
    String smiles1 = (bs1 == null ? ScriptVariable.sValue(args[0]) : "");
    String smiles2 = (bs2 == null ? ScriptVariable.sValue(args[1]) : "");
    Matrix4f m = new Matrix4f();
    stddev = Float.NaN;
    List<Point3f> ptsA, ptsB;
    if (isSmiles) {
      if (bs1 == null || bs2 == null)
        return false;
    }
    if (isIsomer) {
      if (args.length != 3)
        return false;
      String mf1 = (bs1 == null ? viewer.getSmilesMatcher()
          .getMolecularFormula(smiles1, false) : JmolMolecule.getMolecularFormula(
          viewer.getModelSet().atoms, bs1, false));
      String mf2 = (bs2 == null ? viewer.getSmilesMatcher()
          .getMolecularFormula(smiles2, false) : JmolMolecule.getMolecularFormula(
          viewer.getModelSet().atoms, bs2, false));
      if (!mf1.equals(mf2))
        return addX("NONE");
      if (bs1 != null)
        smiles1 = (String) eval.getSmilesMatches("", null, bs1, null, false, true);
      boolean check;
      if (bs2 == null) {
        // note: find smiles1 IN smiles2 here
        check = (viewer.getSmilesMatcher().areEqual(smiles2, smiles1) > 0);
      } else {
        check = (((BitSet) eval.getSmilesMatches(smiles1, null, bs2, null,
            false, true)).nextSetBit(0) >= 0);
      }
      if (!check) {
        // MF matched, but didn't match SMILES
        String s = smiles1 + smiles2;
        if (s.indexOf("/") >= 0 || s.indexOf("\\") >= 0 || s.indexOf("@") >= 0) {
          if (smiles1.indexOf("@") >= 0 && (bs2 != null || smiles2.indexOf("@") >= 0)) {
            // reverse chirality centers
            smiles1 = TextFormat.simpleReplace(smiles1, "@@", "!@");
            smiles1 = TextFormat.simpleReplace(smiles1, "@", "@@");
            smiles1 = TextFormat.simpleReplace(smiles1, "!@@", "@");
            smiles1 = TextFormat.simpleReplace(smiles1, "@@SP", "@SP");
            smiles1 = TextFormat.simpleReplace(smiles1, "@@OH", "@OH");
            smiles1 = TextFormat.simpleReplace(smiles1, "@@TB", "@TB");
            if (bs2 == null) {
              check = (viewer.getSmilesMatcher().areEqual(smiles1, smiles2) > 0);
            } else {
              check = (((BitSet) eval.getSmilesMatches(smiles1, null, bs2,
                  null, false, true)).nextSetBit(0) >= 0);
            }
            if (check)
              return addX("ENANTIOMERS");
          }
          // remove all stereochemistry from SMILES string
          if (bs2 == null) {
            check = (viewer.getSmilesMatcher().areEqual("/nostereo/" + smiles2, smiles1) > 0);
          } else {
            Object ret = eval.getSmilesMatches("/nostereo/" + smiles1, null, bs2, null,
                false, true);
            check = (((BitSet) ret).nextSetBit(0) >= 0);
          }
          if (check)
            return addX("DIASTERIOMERS");
        }
        // MF matches, but not enantiomers or diasteriomers
        return addX("CONSTITUTIONAL ISOMERS");
      }
      //identical or conformational 
      if (bs1 == null || bs2 == null)
        return addX("IDENTICAL");
      stddev = eval.getSmilesCorrelation(bs1, bs2, smiles1, null, null, null,
          null, false);
      return addX(stddev < 0.2f ? "IDENTICAL"
          : "IDENTICAL or CONFORMATIONAL ISOMERS (RMSD=" + stddev + ")");
    } else if (isSmiles) {
      ptsA = new ArrayList<Point3f>();
      ptsB = new ArrayList<Point3f>();
      sOpt = ScriptVariable.sValue(args[2]);
      isSmiles = sOpt.equalsIgnoreCase("SMILES");
      boolean isSearch = sOpt.equalsIgnoreCase("SMARTS");
      if (isSmiles || isSearch)
        sOpt = (args.length > 3 ? ScriptVariable.sValue(args[3]) : null);
      if (sOpt == null)
        return false;
      stddev = eval.getSmilesCorrelation(bs1, bs2, sOpt, ptsA, ptsB, m, null,
          !isSmiles);
    } else {
      ptsA = eval.getPointVector(args[0], 0);
      ptsB = eval.getPointVector(args[1], 0);
      if (ptsA != null && ptsB != null)
        stddev = Measure.getTransformMatrix4(ptsA, ptsB, m, null);
    }
    return (isStdDev || Float.isNaN(stddev) ? addX(stddev) : addX(m));
  }

  private boolean evaluateVolume(ScriptVariable[] args) throws ScriptException {
    ScriptVariable x1 = getX();
    if (x1.tok != Token.bitset)
      return false;
    String type = (args.length == 0 ? null : ScriptVariable.sValue(args[0]));
    return addX(viewer.getVolume((BitSet) x1.value, type));
  }

  private boolean evaluateSort(ScriptVariable[] args) throws ScriptException {
    if (args.length > 1)
      return false;
    int n = ScriptVariable.iValue(args[0]);
    return addX(getX().sortOrReverse(n));
  }

  private boolean evaluateSymop(ScriptVariable[] args, boolean haveBitSet)
      throws ScriptException {
    if (args.length == 0)
      return false;
    ScriptVariable x1 = (haveBitSet ? getX() : null);
    if (x1 != null && x1.tok != Token.bitset)
      return false;
    BitSet bs = (x1 != null ? (BitSet) x1.value : args.length > 2
        && args[1].tok == Token.bitset ? (BitSet) args[1].value : viewer
        .getModelUndeletedAtomsBitSet(-1));
    String xyz;
    switch (args[0].tok) {
    case Token.string:
      xyz = ScriptVariable.sValue(args[0]);
      break;
    case Token.matrix4f:
      xyz = args[0].escape();
      break;
    default:
      xyz = null;
    }
    int iOp = (xyz == null ? ScriptVariable.iValue(args[0]) : 0);
    Point3f pt = (args.length > 1 ? ptValue(args[1], true) : null);
    if (args.length == 2 && !Float.isNaN(pt.x))
      return addX(viewer.getSymmetryInfo(bs, xyz, iOp, pt, null, null,
          Token.point));
    String desc = (args.length == 1 ? "" : ScriptVariable
        .sValue(args[args.length - 1])).toLowerCase();
    int tok = Token.draw;
    if (args.length == 1 || desc.equalsIgnoreCase("matrix")) {
      tok = Token.matrix4f;
    } else if (desc.equalsIgnoreCase("array") || desc.equalsIgnoreCase("list")) {
      tok = Token.list;
    } else if (desc.equalsIgnoreCase("description")) {
      tok = Token.label;
    } else if (desc.equalsIgnoreCase("xyz")) {
      tok = Token.info;
    } else if (desc.equalsIgnoreCase("translation")) {
      tok = Token.translation;
    } else if (desc.equalsIgnoreCase("axis")) {
      tok = Token.axis;
    } else if (desc.equalsIgnoreCase("plane")) {
      tok = Token.plane;
    } else if (desc.equalsIgnoreCase("angle")) {
      tok = Token.angle;
    } else if (desc.equalsIgnoreCase("axispoint")) {
      tok = Token.point;
    } else if (desc.equalsIgnoreCase("center")) {
      tok = Token.center;
    }
    return addX(viewer.getSymmetryInfo(bs, xyz, iOp, pt, null, desc, tok));
  }

  private boolean evaluateBin(ScriptVariable[] args) throws ScriptException {
    if (args.length != 3)
      return false;
    ScriptVariable x1 = getX();
    boolean isListf = (x1.tok == Token.listf);
    if (!isListf && x1.tok != Token.varray)
      return addX(x1);
    float f0 = ScriptVariable.fValue(args[0]);
    float f1 = ScriptVariable.fValue(args[1]);
    float df = ScriptVariable.fValue(args[2]);
    float[] data;
    if (isListf) {
      data = (float[]) x1.value;
    } else {
      List<ScriptVariable> list = x1.getList();
      data = new float[list.size()];
      for (int i = list.size(); --i >= 0; )
        data[i] = ScriptVariable.fValue(list.get(i));
    }
    int nbins = (int) ((f1 - f0) / df + 0.01f);
    int[] array = new int[nbins];
    int nPoints = data.length;
    for (int i = 0; i < nPoints; i++) {
      float v = data[i];
      int bin = (int) ((v - f0) / df);
      if (bin < 0)
        bin = 0;
      else if (bin >= nbins)
        bin = nbins;
      array[bin]++;
    }
    return addX(array);
  }

  private boolean evaluateHelix(ScriptVariable[] args) throws ScriptException {
    if (args.length < 1 || args.length > 5)
      return false;
    // helix({resno=3})
    // helix({resno=3},"point|axis|radius|angle|draw|measure|array")
    // helix(resno,"point|axis|radius|angle|draw|measure|array")
    // helix(pt1, pt2, dq, "point|axis|radius|angle|draw|measure|array|")
    // helix(pt1, pt2, dq, "draw","someID")
    // helix(pt1, pt2, dq)
    int pt = (args.length > 2 ? 3 : 1);
    String type = (pt >= args.length ? "array" : ScriptVariable
        .sValue(args[pt])).toLowerCase();
    int tok = Token.getTokFromName(type);
    if (args.length > 2) {
      // helix(pt1, pt2, dq ...)
      Point3f pta = ptValue(args[0], true);
      Point3f ptb = ptValue(args[1], true);
      if (args[2].tok != Token.point4f)
        return false;
      Quaternion dq = new Quaternion((Point4f) args[2].value);
      switch (tok) {
      case Token.nada:
        break;
      case Token.point:
      case Token.axis:
      case Token.radius:
      case Token.angle:
      case Token.measure:
        return addX(Measure.computeHelicalAxis(null, tok, pta, ptb, dq));
      case Token.array:
        String[] data = (String[]) Measure.computeHelicalAxis(null, Token.list,
            pta, ptb, dq);
        if (data == null)
          return false;
        return addX(data);
      default:
        return addX(Measure.computeHelicalAxis(type, Token.draw, pta, ptb, dq));
      }
    } else {
      BitSet bs = (args[0].value instanceof BitSet ? (BitSet) args[0].value
          : eval.compareInt(Token.resno, Token.opEQ, ScriptVariable
              .iValue(args[0])));
      switch (tok) {
      case Token.point:
        return addX(viewer
            .getHelixData(bs, Token.point));
      case Token.axis:
        return addX(viewer
            .getHelixData(bs, Token.axis));
      case Token.radius:
        return addX(viewer
            .getHelixData(bs, Token.radius));
      case Token.angle:
        return addX(((Float) viewer.getHelixData(bs,
            Token.angle)).floatValue());
      case Token.draw:
      case Token.measure:
        return addX(viewer
            .getHelixData(bs, tok));
      case Token.array:
        String[] data = (String[]) viewer.getHelixData(bs, Token.list);
        if (data == null)
          return false;
        return addX(data);
      }
    }
    return false;
  }

  private boolean evaluateDot(ScriptVariable[] args, int tok)
      throws ScriptException {
    if (args.length != 1)
      return false;
    ScriptVariable x1 = getX();
    ScriptVariable x2 = args[0];
    Point3f pt2 = ptValue(x2, true);
    Point4f plane2 = planeValue(x2);
    if (x1.tok == Token.bitset && tok != Token.dot)
      return addX(eval.getBitsetProperty(ScriptVariable.bsSelect(x1),
          Token.distance, pt2, plane2, x1.value, null, false, x1.index, false));
    Point3f pt1 = ptValue(x1, true);
    Point4f plane1 = planeValue(x1);
    if (tok == Token.dot) {
      if (plane1 != null && plane2 != null)
        // q1.dot(q2) assume quaternions
        return addX(plane1.x * plane2.x + plane1.y * plane2.y + plane1.z
            * plane2.z + plane1.w * plane2.w);
      // plane.dot(point) =
      if (plane1 != null)
        pt1 = new Point3f(plane1.x, plane1.y, plane1.z);
      // point.dot(plane)
      if (plane2 != null)
        pt2 = new Point3f(plane2.x, plane2.y, plane2.z);
      return addX(pt1.x * pt2.x + pt1.y * pt2.y + pt1.z * pt2.z);
    }

    if (plane1 == null)
      return addX(plane2 == null ? pt2.distance(pt1) : Measure.distanceToPlane(
          plane2, pt1));
    return addX(Measure.distanceToPlane(plane1, pt2));
  }

  public Point3f ptValue(ScriptVariable x, boolean allowFloat)
      throws ScriptException {
    Object pt;
    if (isSyntaxCheck)
      return new Point3f();
    switch (x.tok) {
    case Token.point3f:
      return (Point3f) x.value;
    case Token.bitset:
      return (Point3f) eval
          .getBitsetProperty(ScriptVariable.bsSelect(x), Token.xyz, null, null,
              x.value, null, false, Integer.MAX_VALUE, false);
    case Token.string:
      pt = Escape.unescapePoint(ScriptVariable.sValue(x));
      if (pt instanceof Point3f)
        return (Point3f) pt;
      break;
    case Token.varray:
      pt = Escape.unescapePoint("{" + ScriptVariable.sValue(x) + "}");
      if (pt instanceof Point3f)
        return (Point3f) pt;
      break;
    }
    if (!allowFloat)
      return null;
    float f = ScriptVariable.fValue(x);
    return new Point3f(f, f, f);
  }

  private Point4f planeValue(Token x) {
    if (isSyntaxCheck)
      return new Point4f();
    switch (x.tok) {
    case Token.point4f:
      return (Point4f) x.value;
    case Token.varray:
    case Token.string:
      Object pt = Escape.unescapePoint(ScriptVariable.sValue(x));
      return (pt instanceof Point4f ? (Point4f) pt : null);
    case Token.bitset:
      // ooooh, wouldn't THIS be nice!
      break;
    }
    return null;
  }

  private boolean evaluateMeasure(ScriptVariable[] args, int tok)
      throws ScriptException {
    int nPoints = 0;
    switch (tok) {
    case Token.measure:
      // note: min/max are always in Angstroms
      // note: order is not important (other than min/max)
      // measure({a},{b},{c},{d}, min, max, format, units)
      // measure({a},{b},{c}, min, max, format, units)
      // measure({a},{b}, min, max, format, units)
      // measure({a},{b},{c},{d}, min, max, format, units)
      List<Object> points = new ArrayList<Object>();
      float[] rangeMinMax = new float[] { Float.MAX_VALUE, Float.MAX_VALUE };
      String strFormat = null;
      String units = null;
      boolean isAllConnected = false;
      boolean isNotConnected = false;
      int rPt = 0;
      boolean isNull = false;
      for (int i = 0; i < args.length; i++) {
        switch (args[i].tok) {
        case Token.bitset:
          BitSet bs = (BitSet) args[i].value;
          if (bs.length() == 0)
            isNull = true;
          points.add(bs);
          nPoints++;
          break;
        case Token.point3f:
          Point3fi v = new Point3fi((Point3f) args[i].value);
          points.add(v);
          nPoints++;
          break;
        case Token.integer:
        case Token.decimal:
          rangeMinMax[rPt++ % 2] = ScriptVariable.fValue(args[i]);
          break;
        case Token.string:
          String s = ScriptVariable.sValue(args[i]);
          if (s.equalsIgnoreCase("notConnected"))
            isNotConnected = true;
          else if (s.equalsIgnoreCase("connected"))
            isAllConnected = true;
          else if (Parser.isOneOf(s.toLowerCase(),
              "nm;nanometers;pm;picometers;angstroms;ang;au"))
            units = s.toLowerCase();
          else
            strFormat = nPoints + ":" + s;
          break;
        default:
          return false;
        }
      }
      if (nPoints < 2 || nPoints > 4 || rPt > 2 || isNotConnected
          && isAllConnected)
        return false;
      if (isNull)
        return addX("");
      MeasurementData md = new MeasurementData(points, 0, rangeMinMax,
          strFormat, units, null, isAllConnected, isNotConnected, true);
      return addX(md.getMeasurements(viewer));
    case Token.angle:
      if ((nPoints = args.length) != 3 && nPoints != 4)
        return false;
      break;
    default: // distance
      if ((nPoints = args.length) != 2)
        return false;
    }
    Point3f[] pts = new Point3f[nPoints];
    for (int i = 0; i < nPoints; i++)
      pts[i] = ptValue(args[i], true);
    switch (nPoints) {
    case 2:
      return addX(pts[0].distance(pts[1]));
    case 3:
      return addX(Measure.computeAngle(pts[0], pts[1], pts[2], true));
    case 4:
      return addX(Measure.computeTorsion(pts[0], pts[1], pts[2], pts[3], true));
    }
    return false;
  }

  private boolean evaluateUserFunction(String name, ScriptVariable[] args,
                                       int tok, boolean isSelector)
      throws ScriptException {
    ScriptVariable x1 = null;
    if (isSelector) {
      x1 = getX();
      if (x1.tok != Token.bitset)
        return false;
    }
    wasX = false;
    List<ScriptVariable> params = new ArrayList<ScriptVariable>();
    for (int i = 0; i < args.length; i++) {
      params.add(args[i]);
    }
    if (isSelector) {
      return addX(eval.getBitsetProperty(ScriptVariable.bsSelect(x1), tok,
          null, null, x1.value, new Object[] { name, params }, false, x1.index,
          false));
    }
    ScriptVariable var = eval.runFunction(null, name, params, null, true);
    return (var == null ? false : addX(var));
  }

  private boolean evaluateFind(ScriptVariable[] args) throws ScriptException {
    if (args.length == 0)
      return false;

    // {*}.find("SMARTS", "CCCC")
    // "CCCC".find("SMARTS", "CC")
    // "CCCC".find("SMILES", "MF")
    // {2.1}.find("CCCC",{1.1}) // find pattern "CCCC" in {2.1} with conformation given by {1.1}

    ScriptVariable x1 = getX();
    String sFind = ScriptVariable.sValue(args[0]);
    String flags = (args.length > 1 && args[1].tok != Token.on
        && args[1].tok != Token.off ? ScriptVariable.sValue(args[1]) : "");
    boolean isSequence = sFind.equalsIgnoreCase("SEQUENCE");
    boolean isSmiles = sFind.equalsIgnoreCase("SMILES");
    boolean isSearch = sFind.equalsIgnoreCase("SMARTS");
    boolean isMF = sFind.equalsIgnoreCase("MF");
    if (isSmiles || isSearch || x1.tok == Token.bitset) {
      int iPt = (isSmiles || isSearch ? 2 : 1);
      BitSet bs2 = (iPt < args.length && args[iPt].tok == Token.bitset ? (BitSet) args[iPt++].value
          : null);
      boolean isAll = (args[args.length - 1].tok == Token.on);
      Object ret = null;
      switch (x1.tok) {
      case Token.string:
        String smiles = ScriptVariable.sValue(x1);
        if (bs2 != null)
          return false;
        if (flags.equalsIgnoreCase("mf")) {
          ret = viewer.getSmilesMatcher()
              .getMolecularFormula(smiles, isSearch);
          if (ret == null)
            eval.evalError(viewer.getSmilesMatcher().getLastException(), null);
        } else {
          ret = eval.getSmilesMatches(flags, smiles, null, null, isSearch, !isAll);
        }
        break;
      case Token.bitset:
        if (isMF)
          return addX(JmolMolecule.getMolecularFormula(
              viewer.getModelSet().atoms, (BitSet) x1.value, false));
        if (isSequence)
          return addX(viewer.getSmiles(-1, -1, (BitSet) x1.value, true, isAll, isAll, false));
        if (isSmiles || isSearch)
          sFind = flags;
        BitSet bsMatch3D = bs2;
        ret = eval.getSmilesMatches(sFind, null, (BitSet) x1.value, bsMatch3D, !isSmiles,
            !isAll);
        break;
      }
      if (ret == null)
        eval.error(ScriptEvaluator.ERROR_invalidArgument); 
      return addX(ret);
    }
    boolean isReverse = (flags.indexOf("v") >= 0);
    boolean isCaseInsensitive = (flags.indexOf("i") >= 0);
    boolean asMatch = (flags.indexOf("m") >= 0);
    boolean isList = (x1.tok == Token.varray);
    boolean isPattern = (args.length == 2);
    if (isList || isPattern) {
      Pattern pattern = null;
      try {
        pattern = Pattern.compile(sFind,
            isCaseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
      } catch (Exception e) {
        eval.evalError(e.getMessage(), null);
      }
      String[] list = ScriptVariable.listValue(x1);
      if (Logger.debugging)
        Logger.debug("finding " + sFind);
      BitSet bs = new BitSet();
      int ipt = 0;
      int n = 0;
      Matcher matcher = null;
      List<String> v = (asMatch ? new ArrayList<String>() : null);
      for (int i = 0; i < list.length; i++) {
        String what = list[i];
        matcher = pattern.matcher(what);
        boolean isMatch = matcher.find();
        if (asMatch && isMatch || !asMatch && isMatch == !isReverse) {
          n++;
          ipt = i;
          bs.set(i);
          if (asMatch)
            v.add(isReverse ? what.substring(0, matcher.start())
                + what.substring(matcher.end()) : matcher.group());
        }
      }
      if (!isList) {
        return (asMatch ? addX(v.size() == 1 ? (String) v.get(0) : "")
            : isReverse ? addX(n == 1) : asMatch ? addX(n == 0 ? "" : matcher
                .group()) : addX(n == 0 ? 0 : matcher.start() + 1));
      }
      if (n == 1)
        return addX(asMatch ? (String) v.get(0) : list[ipt]);
      String[] listNew = new String[n];
      if (n > 0)
        for (int i = list.length; --i >= 0;)
          if (bs.get(i)) {
            --n;
            listNew[n] = (asMatch ? (String) v.get(n) : list[i]);
          }
      return addX(listNew);
    }
    return addX(ScriptVariable.sValue(x1).indexOf(sFind) + 1);
  }

  private boolean evaluateGetProperty(ScriptVariable[] args) {
    int pt = 0;
    String propertyName = (args.length > pt ? ScriptVariable.sValue(args[pt++])
        .toLowerCase() : "");
    if (propertyName.startsWith("$")) {
      // TODO
    }
    Object propertyValue;
    if (propertyName.equalsIgnoreCase("fileContents") && args.length > 2) {
      String s = ScriptVariable.sValue(args[1]);
      for (int i = 2; i < args.length; i++)
        s += "|" + ScriptVariable.sValue(args[i]);
      propertyValue = s;
      pt = args.length;
    } else {
      propertyValue = (args.length > pt && args[pt].tok == Token.bitset ? (Object) ScriptVariable
          .bsSelect(args[pt++])
          : args.length > pt && args[pt].tok == Token.string
              && PropertyManager.acceptsStringParameter(propertyName) ? args[pt++].value
              : (Object) "");
    }
    Object property = viewer.getProperty(null, propertyName, propertyValue);
    if (pt < args.length)
      property = PropertyManager.extractProperty(property, args, pt);
    return addX(ScriptVariable.isVariableType(property) ? property : Escape
        .toReadable(propertyName, property));
  }

  private boolean evaluatePlane(ScriptVariable[] args, int tok)
      throws ScriptException {
    if (tok == Token.hkl && args.length != 3 || tok == Token.intersection
        && args.length != 2 && args.length != 3 || args.length == 0
        || args.length > 4)
      return false;
    Point3f pt1, pt2, pt3;
    Point4f plane;

    switch (args.length) {
    case 1:
      Object pt = Escape.unescapePoint(ScriptVariable.sValue(args[0]));
      if (pt instanceof Point4f)
        return addX(pt);
      return addX("" + pt);
    case 2:
      if (tok == Token.intersection) {
        // intersection(plane, plane)
        // intersection(point, plane)
        if (args[1].tok != Token.point4f)
          return false;
        plane = (Point4f) args[1].value;
        if (args[0].tok == Token.point4f) {
          List<Object> list = Measure.getIntersection((Point4f) args[0].value,
              plane);
          return addX(list == null ? "" : list);
        }
        pt2 = ptValue(args[0], false);
        if (pt2 == null)
          return addX("");
        return addX(Measure.getIntersection(pt2, null, plane));
      }
      // fall through
    case 3:
    case 4:
      switch (tok) {
      case Token.hkl:
        // hkl(i,j,k)
        return addX(eval.getHklPlane(new Point3f(
            ScriptVariable.fValue(args[0]), ScriptVariable.fValue(args[1]),
            ScriptVariable.fValue(args[2]))));
      case Token.intersection:
        pt1 = ptValue(args[0], false);
        pt2 = ptValue(args[1], false);
        if (pt1 == null || pt2 == null)
          return addX("");
        Vector3f vLine = new Vector3f(pt2);
        vLine.normalize();
        if (args[2].tok == Token.point4f) {
          // intersection(ptLine, vLine, plane)
          pt1 = Measure.getIntersection(pt1, vLine, (Point4f) args[2].value);
          return addX(pt1 == null ? "" : pt1);
        }
        pt3 = ptValue(args[2], false);
        if (pt3 == null)
          return addX("");
        // interesection(ptLine, vLine, pt2); // IE intersection of plane perp to line through pt2
        Vector3f v = new Vector3f();
        Measure.projectOntoAxis(pt3, pt1, vLine, v);
        return addX(pt3);
      }
      switch (args[0].tok) {
      case Token.bitset:
      case Token.point3f:
        pt1 = ptValue(args[0], false);
        pt2 = ptValue(args[1], false);
        if (pt2 == null)
          return false;
        pt3 = (args.length > 2
            && (args[2].tok == Token.bitset || args[2].tok == Token.point3f) ? ptValue(
            args[2], false)
            : null);
        Vector3f norm = new Vector3f(pt2);
        if (pt3 == null) {
          plane = new Point4f();
          if (args.length == 2 || !ScriptVariable.bValue(args[2])) {
            // plane(<point1>,<point2>) or 
            // plane(<point1>,<point2>,false)
            pt3 = new Point3f(pt1);
            pt3.add(pt2);
            pt3.scale(0.5f);
            norm.sub(pt1);
            norm.normalize();
          } else {
            // plane(<point1>,<vLine>,true)
            pt3 = pt1;
          }
          Measure.getPlaneThroughPoint(pt3, norm, plane);
          return addX(plane);
        }
        // plane(<point1>,<point2>,<point3>)
        // plane(<point1>,<point2>,<point3>,<pointref>)
        Vector3f vAB = new Vector3f();
        Vector3f vAC = new Vector3f();
        float nd = Measure.getDirectedNormalThroughPoints(pt1, pt2, pt3,
            (args.length == 4 ? ptValue(args[3], true) : null), norm, vAB, vAC);
        return addX(new Point4f(norm.x, norm.y, norm.z, nd));
      }
    }
    if (args.length != 4)
      return false;
    float x = ScriptVariable.fValue(args[0]);
    float y = ScriptVariable.fValue(args[1]);
    float z = ScriptVariable.fValue(args[2]);
    float w = ScriptVariable.fValue(args[3]);
    return addX(new Point4f(x, y, z, w));
  }

  private boolean evaluatePoint(ScriptVariable[] args) {
    if (args.length != 1 && args.length != 3 && args.length != 4)
      return false;
    switch (args.length) {
    case 1:
      if (args[0].tok == Token.decimal || args[0].tok == Token.integer)
        return addX(Integer.valueOf(ScriptVariable.iValue(args[0])));
      Object pt = Escape.unescapePoint(ScriptVariable.sValue(args[0]));
      if (pt instanceof Point3f)
        return addX((Point3f) pt);
      return addX("" + pt);
    case 3:
      return addX(new Point3f(ScriptVariable.fValue(args[0]), ScriptVariable
          .fValue(args[1]), ScriptVariable.fValue(args[2])));
    case 4:
      return addX(new Point4f(ScriptVariable.fValue(args[0]), ScriptVariable
          .fValue(args[1]), ScriptVariable.fValue(args[2]), ScriptVariable
          .fValue(args[3])));
    }
    return false;
  }

  private boolean evaluatePrompt(ScriptVariable[] args) {
    //x = prompt("testing")
    //x = prompt("testing","defaultInput")
    //x = prompt("testing","yes|no|cancel", true)
    //x = prompt("testing",["button1", "button2", "button3"])

    if (args.length != 1 && args.length != 2 && args.length != 3)
      return false;
    String label = ScriptVariable.sValue(args[0]);
    String[] buttonArray = (args.length > 1 && args[1].tok == Token.varray ?
        ScriptVariable.listValue(args[1]) : null);
    boolean asButtons = (buttonArray != null || args.length == 1 || args.length == 3 && ScriptVariable.bValue(args[2]));
    String input = (buttonArray != null ? null : args.length >= 2 ? ScriptVariable.sValue(args[1]) : "OK");
    String s = viewer.prompt(label, input, buttonArray, asButtons);
    return (asButtons && buttonArray != null ? addX(Integer.parseInt(s) + 1) : addX(s));
  }

  private boolean evaluateReplace(ScriptVariable[] args) throws ScriptException {
    if (args.length != 2)
      return false;
    ScriptVariable x = getX();
    String sFind = ScriptVariable.sValue(args[0]);
    String sReplace = ScriptVariable.sValue(args[1]);
    String s = (x.tok == Token.varray ? null : ScriptVariable.sValue(x));
    if (s != null)
      return addX(TextFormat.simpleReplace(s, sFind, sReplace));
    String[] list = ScriptVariable.listValue(x);
    for (int i = list.length; --i >= 0;)
      list[i] = TextFormat.simpleReplace(list[i], sFind, sReplace);
    return addX(list);
  }

  private boolean evaluateString(int tok, ScriptVariable[] args)
      throws ScriptException {
    if (args.length > 1)
      return false;
    ScriptVariable x = getX();
    String s = (tok == Token.split && x.tok == Token.bitset
        || tok == Token.trim && x.tok == Token.varray ? null : ScriptVariable
        .sValue(x));
    String sArg = (args.length == 1 ? ScriptVariable.sValue(args[0])
        : tok == Token.trim ? "" : "\n");
    switch (tok) {
    case Token.split:
      if (x.tok == Token.bitset) {
        BitSet bsSelected = ScriptVariable.bsSelect(x);
        sArg = "\n";
        int modelCount = viewer.getModelCount();
        s = "";
        for (int i = 0; i < modelCount; i++) {
          s += (i == 0 ? "" : "\n");
          BitSet bs = viewer.getModelUndeletedAtomsBitSet(i);
          bs.and(bsSelected);
          s += Escape.escape(bs);
        }
      }
      return addX(TextFormat.split(s, sArg));
    case Token.join:
      if (s.length() > 0 && s.charAt(s.length() - 1) == '\n')
        s = s.substring(0, s.length() - 1);
      return addX(TextFormat.simpleReplace(s, "\n", sArg));
    case Token.trim:
      if (s != null)
        return addX(TextFormat.trim(s, sArg));      
      String[] list = ScriptVariable.listValue(x);
      for (int i = list.length; --i >= 0;)
        list[i] = TextFormat.trim(list[i], sArg);
      return addX(list);
    }
    return addX("");
  }

  private boolean evaluateList(int tok, ScriptVariable[] args)
      throws ScriptException {
    if (args.length != 1
        && !(tok == Token.add && (args.length == 0 || args.length == 2)))
      return false;
    ScriptVariable x1 = getX();
    ScriptVariable x2;
    int len;
    String[] sList1 = null, sList2 = null, sList3 = null;

    if (args.length == 2) {
      // [xxxx].add("\t", [...])
      int itab = (args[0].tok == Token.string ? 0 : 1);
      String tab = ScriptVariable.sValue(args[itab]);
      sList1 = (x1.tok == Token.varray ? ScriptVariable.listValue(x1)
          : TextFormat.split(ScriptVariable.sValue(x1), '\n'));
      x2 = args[1 - itab];
      sList2 = (x2.tok == Token.varray ? ScriptVariable.listValue(x2)
          : TextFormat.split(ScriptVariable.sValue(x2), '\n'));
      sList3 = new String[len = Math.max(sList1.length, sList2.length)];
      for (int i = 0; i < len; i++)
        sList3[i] = (i >= sList1.length ? "" : sList1[i]) + tab
            + (i >= sList2.length ? "" : sList2[i]);
      return addX(sList3);
    }
    x2 = (args.length == 0 ? ScriptVariable.vAll : args[0]);
    boolean isAll = (x2.tok == Token.all);
    if (x1.tok != Token.varray && x1.tok != Token.string) {
      wasX = false;
      addOp(Token.tokenLeftParen);
      addX(x1);
      switch (tok) {
      case Token.add:
        addOp(Token.tokenPlus);
        break;
      case Token.sub:
        addOp(Token.tokenMinus);
        break;
      case Token.mul:
        addOp(Token.tokenTimes);
        break;
      case Token.div:
        addOp(Token.tokenDivide);
        break;
      }
      addX(x2);
      return addOp(Token.tokenRightParen);
    }
    boolean isScalar = (x2.tok != Token.varray && ScriptVariable.sValue(x2)
        .indexOf("\n") < 0);

    float[] list1 = null;
    float[] list2 = null;
    List<ScriptVariable> alist1 = x1.getList();
    List<ScriptVariable> alist2 = x2.getList();

    if (x1.tok == Token.varray) {
      len = alist1.size();
    } else {
      sList1 = (TextFormat.split((String) x1.value, "\n"));
      list1 = new float[len = sList1.length];
      Parser.parseFloatArray(sList1, list1);
    }

    if (isAll) {
      float sum = 0f;
      if (x1.tok == Token.varray) {
        for (int i = len; --i >= 0;)
          sum += ScriptVariable.fValue(alist1.get(i));
      } else {
        for (int i = len; --i >= 0;)
          sum += list1[i];
      }
      return addX(sum);
    }

    ScriptVariable scalar = null;

    if (isScalar) {
      scalar = x2;
    } else if (x2.tok == Token.varray) {
      len = Math.min(list1.length, alist2.size());
    } else {
      sList2 = TextFormat.split((String) x2.value, "\n");
      list2 = new float[sList2.length];
      Parser.parseFloatArray(sList2, list2);
      len = Math.min(list1.length, list2.length);
    }
    
    Token token = null;
    switch (tok) {
    case Token.add:
      token = Token.tokenPlus;
      break;
    case Token.sub:
      token = Token.tokenMinus;
      break;
    case Token.mul:
      token = Token.tokenTimes;
      break;
    case Token.div:
      token = Token.tokenDivide;
      break;
    }

    ScriptVariable[] olist = new ScriptVariable[len];
    
    for (int i = 0; i < len; i++) {
      if (x1.tok == Token.varray)
        addX(alist1.get(i));
      else if (Float.isNaN(list1[i]))
        addX(ScriptVariable.unescapePointOrBitsetAsVariable(sList1[i]));
      else
        addX(list1[i]);

      if (isScalar)
        addX(scalar);
      else if (x2.tok == Token.varray)
        addX(alist2.get(i));
      else if (Float.isNaN(list2[i]))
        addX(ScriptVariable.unescapePointOrBitsetAsVariable(sList2[i]));
      else
        addX(list2[i]);
      
      if (!addOp(token) || !operate())
        return false;
      olist[i] = xStack[xPt--];
    }
    return addX(olist);
  }

  private boolean evaluateRowCol(ScriptVariable[] args, int tok)
      throws ScriptException {
    if (args.length != 1)
      return false;
    int n = ScriptVariable.iValue(args[0]) - 1;
    ScriptVariable x1 = getX();
    float[] f;
    switch (x1.tok) {
    case Token.matrix3f:
      if (n < 0 || n > 2)
        return false;
      Matrix3f m = (Matrix3f) x1.value;
      switch (tok) {
      case Token.row:
        f = new float[3];
        m.getRow(n, f);
        return addX(f);
      case Token.col:
      default:
        f = new float[3];
        m.getColumn(n, f);
        return addX(f);
      }
    case Token.matrix4f:
      if (n < 0 || n > 2)
        return false;
      Matrix4f m4 = (Matrix4f) x1.value;
      switch (tok) {
      case Token.row:
        f = new float[4];
        m4.getRow(n, f);
        return addX(f);
      case Token.col:
      default:
        f = new float[4];
        m4.getColumn(n, f);
        return addX(f);
      }
    }
    return false;

  }

  private boolean evaluateArray(ScriptVariable[] args, boolean allowMatrix) {
    int len = args.length;
    if (allowMatrix && (len == 4 || len == 3)) {
      boolean isMatrix = true;
      for (int i = 0; i < len && isMatrix; i++)
        isMatrix = (args[i].tok == Token.varray && args[i].getList().size() == len);
      if (isMatrix) {
        float[] m = new float[len * len];
        int pt = 0;
        for (int i = 0; i < len && isMatrix; i++) {
          List<ScriptVariable> list = args[i].getList();
          for (int j = 0; j < len; j++) {
            float x = ScriptVariable.fValue(list.get(j));
            if (Float.isNaN(x)) {
              isMatrix = false;
              break;
            }
            m[pt++] = x;
          }
        }
        if (isMatrix) {
          if (len == 3)
            return addX(new Matrix3f(m));
          return addX(new Matrix4f(m));
        }
      }
    }
    ScriptVariable[] a = new ScriptVariable[args.length];
    for (int i = a.length; --i >= 0;)
      a[i] = new ScriptVariable(args[i]);
    return addX(a);
  }

  private boolean evaluateMath(ScriptVariable[] args, int tok) {
    if (tok == Token.now) {
      if (args.length == 1 && args[0].tok == Token.string)
        return addX((new Date()) + "\t" + ScriptVariable.sValue(args[0]));
      return addX(((int) System.currentTimeMillis() & 0x7FFFFFFF)
          - (args.length == 0 ? 0 : ScriptVariable.iValue(args[0])));
    }
    if (args.length != 1)
      return false;
    if (tok == Token.abs) {
      if (args[0].tok == Token.integer)
        return addX(Math.abs(ScriptVariable.iValue(args[0])));
      return addX(Math.abs(ScriptVariable.fValue(args[0])));
    }
    double x = ScriptVariable.fValue(args[0]);
    switch (tok) {
    case Token.acos:
      return addX((float) (Math.acos(x) * 180 / Math.PI));
    case Token.cos:
      return addX((float) Math.cos(x * Math.PI / 180));
    case Token.sin:
      return addX((float) Math.sin(x * Math.PI / 180));
    case Token.sqrt:
      return addX((float) Math.sqrt(x));
    }
    return false;
  }

  private boolean evaluateQuaternion(ScriptVariable[] args, int tok)
      throws ScriptException {
    Point3f pt0 = null;
    // quaternion([quaternion array]) // mean
    // quaternion([quaternion array1], [quaternion array2], "relative") //
    // difference array
    // quaternion(matrix)
    // quaternion({atom1}) // quaternion (1st if array)
    // quaternion({atomSet}, nMax) // nMax quaternions, by group; 0 for all
    // quaternion({atom1}, {atom2}) // difference 
    // quaternion({atomSet1}, {atomset2}, nMax) // difference array, by group; 0 for all
    // quaternion(vector, theta)
    // quaternion(q0, q1, q2, q3)
    // quaternion("{x, y, z, w"})
    // quaternion(center, X, XY)
    // quaternion(mcol1, mcol2)
    // quaternion(q, "id", center) // draw code
    // axisangle(vector, theta)
    // axisangle(x, y, z, theta)
    // axisangle("{x, y, z, theta"})
    int nArgs = args.length;
    int nMax = Integer.MAX_VALUE;
    boolean isRelative = false;
    if (tok == Token.quaternion) {
      if (nArgs > 1 && args[nArgs - 1].tok == Token.string
          && ((String) args[nArgs - 1].value).equalsIgnoreCase("relative")) {
        nArgs--;
        isRelative = true;
      }
      if (nArgs > 1 && args[nArgs - 1].tok == Token.integer 
          && args[0].tok == Token.bitset) {
        nMax = ScriptVariable.iValue(args[nArgs - 1]);
        if (nMax <= 0)
          nMax = Integer.MAX_VALUE - 1;
        nArgs--;
      }
    }

    switch (nArgs) {
    case 1:
    case 4:
      break;
    case 2:
      if (tok == Token.quaternion) {
        if (args[0].tok == Token.varray && args[1].tok == Token.varray)
          break;
        if (args[0].tok == Token.bitset
            && (args[1].tok == Token.integer || args[1].tok == Token.bitset))
          break;
      }
      if ((pt0 = ptValue(args[0], false)) == null || tok != Token.quaternion
          && args[1].tok == Token.point3f)
        return false;
      break;
    case 3:
      if (tok != Token.quaternion)
        return false;
      if (args[0].tok == Token.point4f) {
        if (args[2].tok != Token.point3f && args[2].tok != Token.bitset)
          return false;
        break;
      }
      for (int i = 0; i < 3; i++)
        if (args[i].tok != Token.point3f && args[i].tok != Token.bitset)
          return false;
      break;
    default:
      return false;
    }
    Quaternion q = null;
    Quaternion[] qs = null;
    Point4f p4 = null;
    switch (nArgs) {
    case 1:
    default:
      if (tok == Token.quaternion && args[0].tok == Token.varray) {
        Quaternion[] data1 = getQuaternionArray(args[0].getList());
        Object mean = Quaternion.sphereMean(data1, null, 0.0001f);
        q = (mean instanceof Quaternion ? (Quaternion) mean : null);
        break;
      } else if (tok == Token.quaternion && args[0].tok == Token.bitset) {
        qs = viewer.getAtomGroupQuaternions((BitSet) args[0].value, nMax);
      } else if (args[0].tok == Token.matrix3f) {
        q = new Quaternion((Matrix3f) args[0].value);
      } else if (args[0].tok == Token.point4f) {
        p4 = (Point4f) args[0].value;
      } else {
        Object v = Escape.unescapePoint(ScriptVariable.sValue(args[0]));
        if (!(v instanceof Point4f))
          return false;
        p4 = (Point4f) v;
      }
      if (tok == Token.axisangle)
        q = new Quaternion(new Point3f(p4.x, p4.y, p4.z), p4.w);
      break;
    case 2:
      if (tok == Token.quaternion) {
        if (args[0].tok == Token.varray && args[1].tok == Token.varray) {
          Quaternion[] data1 = getQuaternionArray(args[0].getList());
          Quaternion[] data2 = getQuaternionArray(args[1].getList());
          qs = Quaternion.div(data2, data1, nMax, isRelative);
          break;
        }
        if (args[0].tok == Token.bitset && args[1].tok == Token.bitset) {
          Quaternion[] data1 = viewer.getAtomGroupQuaternions(
              (BitSet) args[0].value, Integer.MAX_VALUE);
          Quaternion[] data2 = viewer.getAtomGroupQuaternions(
              (BitSet) args[1].value, Integer.MAX_VALUE);
          qs = Quaternion.div(data2, data1, nMax, isRelative);
          break;
        }
      }
      Point3f pt1 = ptValue(args[1], false);
      p4 = planeValue(args[0]);
      if (pt1 != null)
        q = Quaternion.getQuaternionFrame(new Point3f(0, 0, 0), pt0, pt1);
      else
        q = new Quaternion(pt0, ScriptVariable.fValue(args[1]));
      break;
    case 3:
      if (args[0].tok == Token.point4f) {
        Point3f pt = (args[2].tok == Token.point3f ? (Point3f) args[2].value
            : viewer.getAtomSetCenter((BitSet) args[2].value));
        return addX((new Quaternion((Point4f) args[0].value)).draw("q",
            ScriptVariable.sValue(args[1]), pt, 1f));
      }
      Point3f[] pts = new Point3f[3];
      for (int i = 0; i < 3; i++)
        pts[i] = (args[i].tok == Token.point3f ? (Point3f) args[i].value
            : viewer.getAtomSetCenter((BitSet) args[i].value));
      q = Quaternion.getQuaternionFrame(pts[0], pts[1], pts[2]);
      break;
    case 4:
      if (tok == Token.quaternion)
        p4 = new Point4f(ScriptVariable.fValue(args[1]), ScriptVariable
            .fValue(args[2]), ScriptVariable.fValue(args[3]), ScriptVariable
            .fValue(args[0]));
      else
        q = new Quaternion(new Point3f(ScriptVariable.fValue(args[0]),
            ScriptVariable.fValue(args[1]), ScriptVariable.fValue(args[2])),
            ScriptVariable.fValue(args[3]));
      break;
    }
    if (qs != null) {
      if (nMax == Integer.MAX_VALUE) {
        q = (qs.length > 0 ? qs[0] : null);
      } else {
        List<Point4f>[] list = ArrayUtil.createArrayOfArrayList(qs.length);
        for (int i = 0; i < qs.length; i++)
          list[i].add(qs[i].toPoint4f());
        return addX(list);
      }
    }
    return addX((q == null ? new Quaternion(p4) : q).toPoint4f());
  }

  private boolean evaluateRandom(ScriptVariable[] args) {
    if (args.length > 2)
      return false;
    float lower = (args.length < 2 ? 0 : ScriptVariable.fValue(args[0]));
    float range = (args.length == 0 ? 1 : ScriptVariable
        .fValue(args[args.length - 1]));
    range -= lower;
    return addX((float) (Math.random() * range) + lower);
  }

  private boolean evaluateCross(ScriptVariable[] args) {
    if (args.length != 2)
      return false;
    ScriptVariable x1 = args[0];
    ScriptVariable x2 = args[1];
    if (x1.tok != Token.point3f || x2.tok != Token.point3f)
      return false;
    Vector3f a = new Vector3f((Point3f) x1.value);
    Vector3f b = new Vector3f((Point3f) x2.value);
    a.cross(a, b);
    return addX(new Point3f(a));
  }

  private boolean evaluateLoad(ScriptVariable[] args, int tok) {
    if (args.length > 2 || args.length < 1)
      return false;
    String file = ScriptVariable.sValue(args[0]);
    int nBytesMax = (args.length == 2 ? ScriptVariable.iValue(args[1])
        : Integer.MAX_VALUE);
    return addX(tok == Token.load ? viewer.getFileAsString(file, nBytesMax,
        false) : viewer.getFilePath(file, false));
  }

  private boolean evaluateWrite(ScriptVariable[] args) throws ScriptException {
    if (args.length == 0)
      return false;
    return addX(eval.write(args));
  }

  private boolean evaluateScript(ScriptVariable[] args, int tok)
      throws ScriptException {
    if (tok == Token.javascript && args.length != 1 || args.length == 0
        || args.length > 2)
      return false;
    String s = ScriptVariable.sValue(args[0]);
    StringBuffer sb = new StringBuffer();
    switch (tok) {
    case Token.script:
      String appID = (args.length == 2 ? ScriptVariable.sValue(args[1]) : ".");
      // options include * > . or an appletID with or without "jmolApplet"
      if (!appID.equals("."))
        sb.append(viewer.jsEval(appID + "\1" + s));
      if (appID.equals(".") || appID.equals("*"))
        eval.runScript(s, sb);
      break;
    case Token.javascript:
      sb.append(viewer.jsEval(s));
      break;
    }
    s = sb.toString();
    float f;
    return (Float.isNaN(f = Parser.parseFloatStrict(s)) ? addX(s) : s
        .indexOf(".") >= 0 ? addX(f) : addX(Parser.parseInt(s)));
  }

  private boolean evaluateData(ScriptVariable[] args) {

    // x = data("somedataname") # the data
    // x = data("data2d_xxxx") # 2D data (x,y paired values)
    // x = data("data2d_xxxx", iSelected) # selected row of 2D data, with <=0
    // meaning "relative to the last row"
    // x = data("property_x", "property_y") # array addition of two property
    // sets
    // x = data({atomno < 10},"xyz") # (or "pdb" or "mol") coordinate data in
    // xyz, pdb, or mol format
    // x = data(someData,ptrFieldOrColumn,nBytes,firstLine) # extraction of a
    // column of data based on a field (nBytes = 0) or column range (nBytes >
    // 0)
    if (args.length != 1 && args.length != 2 && args.length != 4)
      return false;
    String selected = ScriptVariable.sValue(args[0]);
    String type = (args.length == 2 ? ScriptVariable.sValue(args[1]) : "");

    if (args.length == 4) {
      int iField = ScriptVariable.iValue(args[1]);
      int nBytes = ScriptVariable.iValue(args[2]);
      int firstLine = ScriptVariable.iValue(args[3]);
      float[] f = Parser.extractData(selected, iField, nBytes, firstLine);
      return addX(Escape.escape(f, false));
    }

    if (selected.indexOf("data2d_") == 0) {
      // tab, newline separated data
      float[][] f1 = viewer.getDataFloat2D(selected);
      if (f1 == null)
        return addX("");
      if (args.length == 2 && args[1].tok == Token.integer) {
        int pt = args[1].intValue;
        if (pt < 0)
          pt += f1.length;
        if (pt >= 0 && pt < f1.length)
          return addX(Escape.escape(f1[pt], false));
        return addX("");
      }
      return addX(Escape.escape(f1, false));
    }

    // parallel addition of float property data sets

    if (selected.indexOf("property_") == 0) {
      float[] f1 = viewer.getDataFloat(selected);
      if (f1 == null)
        return addX("");
      float[] f2 = (type.indexOf("property_") == 0 ? viewer.getDataFloat(type)
          : null);
      if (f2 != null) {
        f1 = f1.clone();
        for (int i = Math.min(f1.length, f2.length); --i >= 0;)
          f1[i] += f2[i];
      }
      return addX(Escape.escape(f1, false));
    }

    // some other data type -- just return it

    if (args.length == 1) {
      Object[] data = viewer.getData(selected);
      return addX(data == null ? "" : "" + data[1]);
    }
    // {selected atoms} XYZ, MOL, PDB file format
    return addX(viewer.getData(selected, type));
  }

  private boolean evaluateLabel(int intValue, ScriptVariable[] args)
      throws ScriptException {
    // NOT {xxx}.label
    // {xxx}.label("....")
    // {xxx}.yyy.format("...")
    // (value).format("...")
    // format("....",a,b,c...)

    ScriptVariable x1 = (args.length < 2 ? getX() : null);
    String format = (args.length == 0 ? "%U" : ScriptVariable.sValue(args[0]));
    boolean asArray = Token.tokAttr(intValue, Token.minmaxmask);
    if (x1 == null)
      return addX(ScriptVariable.sprintf(args));
    BitSet bs = ScriptVariable.getBitSet(x1, true);
    if (bs == null)
      return addX(ScriptVariable.sprintf(TextFormat.formatCheck(format), x1));
    return addX(eval.getBitsetIdent(bs, format,
          x1.value, true, x1.index, asArray));
  }

  private boolean evaluateWithin(ScriptVariable[] args) throws ScriptException {
    if (args.length < 1 || args.length > 5)
      return false;
    int i = args.length;
    Object withinSpec = args[0].value;
    String withinStr = "" + withinSpec; 
    int tok = args[0].tok;
    if (tok == Token.string)
      tok = Token.getTokFromName(withinStr.toLowerCase());
    BitSet bs;
    float distance = 0;
    boolean isWithinModelSet = false;
    boolean isWithinGroup = false;
    boolean isDistance = (tok == Token.decimal || tok == Token.integer);
    switch (tok) {
    case Token.branch:
      if (i != 3 || !(args[1].value instanceof BitSet)
          || !(args[2].value instanceof BitSet))
        return false;
      return addX(viewer.getBranchBitSet(
          ((BitSet) args[2].value).nextSetBit(0), ((BitSet) args[1].value)
              .nextSetBit(0)));
    case Token.smiles:
    case Token.substructure:  // same as "SMILES"
    case Token.search:
      // within("smiles", "...", {bitset})
      // within("smiles", "...", {bitset})
      BitSet bsSelected = null;
      boolean isOK = true;
      switch (i) {
      case 2:
        break;
      case 3:
        isOK = (args[2].tok == Token.bitset);
        if (isOK)
          bsSelected = (BitSet) args[2].value;
        break;
      default:
        isOK = false;
      }
      if (!isOK)
        eval.error(ScriptEvaluator.ERROR_invalidArgument);
      return addX(eval.getSmilesMatches(ScriptVariable
          .sValue(args[1]), null, bsSelected, null, tok == Token.search, asBitSet));
    }
    if (withinSpec instanceof String) {
      if (tok == Token.nada) {
        tok = Token.spec_seqcode;
        if (i > 2)
          return false;
        i = 2;
      }
    } else if (isDistance) {
      distance = ScriptVariable.fValue(args[0]);
      if (i < 2)
        return false;
      switch (tok = args[1].tok) {
      case Token.on:
      case Token.off:
        isWithinModelSet = ScriptVariable.bValue(args[1]);
        i = 0;
        break;
      case Token.string:
        isWithinGroup = (ScriptVariable.sValue(args[1])
            .equalsIgnoreCase("group"));
        tok = Token.group;
        break;
      }
    } else {
      return false;
    }
    Point3f pt = null;
    Point4f plane = null;
    switch (i) {
    case 1:
      // within (sheet)
      // within (helix)
      // within (boundbox)
      switch (tok) {
      case Token.helix:
      case Token.sheet:
      case Token.boundbox:
        return addX(viewer.getAtomBits(tok, null));
      case Token.basepair:
        return addX(viewer.getAtomBits(tok, ""));
      case Token.spec_seqcode:
        return addX(viewer.getAtomBits(Token.sequence,
            withinStr));
      }
      return false;
    case 2:
      // within (atomName, "XX,YY,ZZZ")
      switch (tok) {
      case Token.spec_seqcode:
        tok = Token.sequence;
        break;
      case Token.atomname:
      case Token.atomtype:
      case Token.basepair:
      case Token.sequence:
        return addX(viewer.getAtomBits(tok, ScriptVariable
            .sValue(args[args.length - 1])));
      }
      break;
    case 3:
      switch (tok) {
      case Token.on:
      case Token.off:
      case Token.group:
      case Token.plane:
      case Token.hkl:
      case Token.coord:
        break;
      case Token.sequence:
        // within ("sequence", "CII", *.ca)
        withinStr = ScriptVariable.sValue(args[2]);
        break;
      default:
        return false;
      }
      // within (distance, group, {atom collection})
      // within (distance, true|false, {atom collection})
      // within (distance, plane|hkl, [plane definition] )
      // within (distance, coord, [point or atom center] )
      break;
    }
    i = args.length - 1;
    if (args[i].value instanceof Point4f) {
      plane = (Point4f) args[i].value;
    } else if (args[i].value instanceof Point3f) {
      pt = (Point3f) args[i].value;
      if (ScriptVariable.sValue(args[1]).equalsIgnoreCase("hkl"))
        plane = eval.getHklPlane(pt);
    }
    if (i > 0 && plane == null && pt == null
        && !(args[i].value instanceof BitSet))
      return false;
    if (plane != null)
      return addX(viewer.getAtomsWithin(distance, plane));
    if (pt != null)
      return addX(viewer.getAtomsWithin(distance, pt));
    bs = (args[i].tok == Token.bitset ? ScriptVariable.bsSelect(args[i]) : null);
    if (tok == Token.sequence)
      return addX(viewer.getSequenceBits(withinStr, bs));
    if (bs == null)
      bs = new BitSet();
    if (!isDistance)
      return addX(viewer.getAtomBits(tok, bs));
    if (isWithinGroup)
      return addX(viewer.getGroupsWithin((int) distance, bs));
    return addX(viewer.getAtomsWithin(distance, bs, isWithinModelSet));
  }

  private boolean evaluateColor(ScriptVariable[] args) {
    // color("rwb")                  # "" for most recently used scheme for coloring by property
    // color("rwb", min, max)        # min/max default to most recent property mapping 
    // color("rwb", min, max, value) # returns color
    // color("$isosurfaceId")        # info for a given isosurface
    // color("$isosurfaceId", value) # color for a given mapped isosurface value
    
    String colorScheme = (args.length > 0 ? ScriptVariable.sValue(args[0])
        : "");
    boolean isIsosurface = colorScheme.startsWith("$");
    ColorEncoder ce = (isIsosurface ? null : viewer.getColorEncoder(colorScheme));
    if (!isIsosurface && ce == null)
      return addX("");
    float lo = (args.length > 1 ? ScriptVariable.fValue(args[1])
        : Float.MAX_VALUE);
    float hi = (args.length > 2 ? ScriptVariable.fValue(args[2])
        : Float.MAX_VALUE);
    float value = (args.length > 3 ? ScriptVariable.fValue(args[3])
        : Float.MAX_VALUE);
    boolean getValue = (value != Float.MAX_VALUE || lo != Float.MAX_VALUE
        && hi == Float.MAX_VALUE);
    boolean haveRange = (hi != Float.MAX_VALUE);
    if (!haveRange && colorScheme.length() == 0) {
      value = lo;
      float[] range = viewer.getCurrentColorRange();
      lo = range[0];
      hi = range[1];
    }
    if (isIsosurface) {
      // isosurface color scheme      
      String id = colorScheme.substring(1);
      Object[] data = new Object[] { id, null};
      if (!viewer.getShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "colorEncoder", data))
        return addX("");
      ce = (ColorEncoder) data[1];
    } else {
      ce.setRange(lo, hi, lo > hi);
    }
    Map<String, Object> key = ce.getColorKey();
    if (getValue)
      return addX(Graphics3D.colorPointFromInt2(ce
          .getArgb(hi == Float.MAX_VALUE ? lo : value)));
    return addX(ScriptVariable.getVariable(key));
  }

  private boolean evaluateConnected(ScriptVariable[] args) {
    /*
     * Two options here:
     * 
     * connected(1, 3, "single", {carbon})
     * 
     * connected(1, 3, "partial 3.1", {carbon})
     * 
     * means "atoms connected to carbon by from 1 to 3 single bonds"
     * 
     * connected(1.0, 1.5, "single", {carbon}, {oxygen})
     * 
     * means "single bonds from 1.0 to 1.5 Angstroms between carbon and oxygen"
     * 
     * the first returns an atom bitset; the second returns a bond bitset.
     */

    if (args.length > 5)
      return false;
    float min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
    float fmin = 0, fmax = Float.MAX_VALUE;

    int order = JmolEdge.BOND_ORDER_ANY;
    BitSet atoms1 = null;
    BitSet atoms2 = null;
    boolean haveDecimal = false;
    boolean isBonds = false;
    for (int i = 0; i < args.length; i++) {
      ScriptVariable var = args[i];
      switch (var.tok) {
      case Token.bitset:
        isBonds = (var.value instanceof BondSet);
        if (isBonds && atoms1 != null)
          return false;
        if (atoms1 == null)
          atoms1 = ScriptVariable.bsSelect(var);
        else if (atoms2 == null)
          atoms2 = ScriptVariable.bsSelect(var);
        else
          return false;
        break;
      case Token.string:
        String type = ScriptVariable.sValue(var);
        if (type.equalsIgnoreCase("hbond"))
          order = JmolEdge.BOND_HYDROGEN_MASK;
        else
          order = JmolConstants.getBondOrderFromString(type);
        if (order == JmolEdge.BOND_ORDER_NULL)
          return false;
        break;
      case Token.decimal:
        haveDecimal = true;
        // fall through
      default:
        int n = ScriptVariable.iValue(var);
        float f = ScriptVariable.fValue(var);
        if (max != Integer.MAX_VALUE)
          return false;

        if (min == Integer.MIN_VALUE) {
          min = Math.max(n, 0);
          fmin = f;
        } else {
          max = n;
          fmax = f;
        }
      }
    }
    if (min == Integer.MIN_VALUE) {
      min = 1;
      max = 100;
      fmin = JmolConstants.DEFAULT_MIN_CONNECT_DISTANCE;
      fmax = JmolConstants.DEFAULT_MAX_CONNECT_DISTANCE;
    } else if (max == Integer.MAX_VALUE) {
      max = min;
      fmax = fmin;
      fmin = JmolConstants.DEFAULT_MIN_CONNECT_DISTANCE;
    }
    if (atoms1 == null)
      atoms1 = viewer.getModelUndeletedAtomsBitSet(-1);
    if (haveDecimal && atoms2 == null)
      atoms2 = atoms1;
    if (atoms2 != null) {
      BitSet bsBonds = new BitSet();
      viewer
          .makeConnections(fmin, fmax, order,
              JmolConstants.CONNECT_IDENTIFY_ONLY, atoms1, atoms2, bsBonds,
              isBonds, 0);
      return addX(new ScriptVariable(Token.bitset, new BondSet(bsBonds, viewer
          .getAtomIndices(viewer.getAtomBits(Token.bonds, bsBonds)))));
    }
    return addX(viewer.getAtomsConnected(min, max, order, atoms1));
  }

  private boolean evaluateSubstructure(ScriptVariable[] args, int tok)
      throws ScriptException {
    // select substucture(....) legacy - was same as smiles(), now search()
    // select smiles(...)
    // select search(...)  now same as substructure
    if (args.length == 0)
      return false;
    BitSet bs = new BitSet();
    String pattern = ScriptVariable.sValue(args[0]);
    if (pattern.length() > 0)
      try {
        BitSet bsSelected = (args.length == 2 && args[1].tok == Token.bitset ? ScriptVariable
            .bsSelect(args[1])
            : null);
        bs = viewer.getSmilesMatcher().getSubstructureSet(pattern,
            viewer.getModelSet().atoms, viewer.getAtomCount(), bsSelected,
            tok != Token.smiles && tok != Token.substructure, false);
      } catch (Exception e) {
        eval.evalError(e.getMessage(), null);
      }
    return addX(bs);
  }

  @SuppressWarnings("unchecked")
  private boolean operate() throws ScriptException {

    Token op = oStack[oPt--];
    Point3f pt;
    Point4f pt4;
    Matrix3f m;
    String s;
    float f;

    if (logMessages) {
      dumpStacks("operate: " + op);
    }

    // check for a[3][2] 
    if (isArrayItem && squareCount == 0 && equalCount == 1 && oPt < 0
        && (op.tok == Token.opEQ)) {
      return true;
    }

    ScriptVariable x2 = getX();
    if (x2 == Token.tokenArraySelector)
      return false;

    // unary:

    if (x2.tok == Token.varray || x2.tok == Token.matrix3f
        || x2.tok == Token.matrix4f)
      x2 = ScriptVariable.selectItem(x2);

    if (op.tok == Token.minusMinus || op.tok == Token.plusPlus) {
      if (!isSyntaxCheck && !x2.increment(incrementX))
        return false;
      wasX = true;
      putX(x2); // reverse getX()
      return true;
    }
    if (op.tok == Token.opNot) {
      if (isSyntaxCheck)
        return addX(true);
      switch (x2.tok) {
      case Token.point4f: // quaternion
        return addX((new Quaternion((Point4f) x2.value)).inv().toPoint4f());
      case Token.matrix3f:
        m = new Matrix3f((Matrix3f) x2.value);
        m.invert();
        return addX(m);
      case Token.matrix4f:
        Matrix4f m4 = new Matrix4f((Matrix4f) x2.value);
        m4.invert();
        return addX(m4);
      case Token.bitset:
        return addX(BitSetUtil.copyInvert(ScriptVariable.bsSelect(x2),
            (x2.value instanceof BondSet ? viewer.getBondCount() : viewer
                .getAtomCount())));
      default:
        return addX(!ScriptVariable.bValue(x2));
      }
    }
    int iv = op.intValue & ~Token.minmaxmask;
    if (op.tok == Token.propselector) {
      switch (iv) {
      case Token.length:
        if (!(x2.value instanceof BondSet))
          return addX(ScriptVariable.sizeOf(x2));
        break;
      case Token.size:
        return addX(ScriptVariable.sizeOf(x2));
      case Token.type:
        return addX(ScriptVariable.typeOf(x2));
      case Token.keys:
        if (x2.tok != Token.hash)
          return addX("");
        Object[] keys = ((Map<String, ScriptVariable>) x2.value).keySet()
            .toArray();
        Arrays.sort(keys);
        String[] ret = new String[keys.length];
        for (int i = 0; i < keys.length; i++)
          ret[i] = (String) keys[i];
        return addX(ret);
      case Token.lines:
        switch (x2.tok) {
        case Token.matrix3f:
        case Token.matrix4f:
          s = ScriptVariable.sValue(x2);
          s = TextFormat.simpleReplace(s.substring(1, s.length() - 1), "],[",
              "]\n[");
          break;
        case Token.string:
          s = (String) x2.value;
          break;
        default:
          s = ScriptVariable.sValue(x2);
        }
        s = TextFormat.simpleReplace(s, "\n\r", "\n").replace('\r', '\n');
        return addX(TextFormat.split(s, '\n'));
      case Token.color:
        switch (x2.tok) {
        case Token.string:
        case Token.varray:
          s = ScriptVariable.sValue(x2);
          pt = new Point3f();
          return addX(Graphics3D.colorPointFromString(s, pt));
        case Token.integer:
        case Token.decimal:
          return addX(viewer.getColorPointForPropertyValue(ScriptVariable
              .fValue(x2)));
        case Token.point3f:
          return addX(Escape.escapeColor(Graphics3D
              .colorPtToInt((Point3f) x2.value)));
        default:
          // handle bitset later
        }
        break;
      case Token.boundbox:
        return (isSyntaxCheck ? addX("x") : getBoundBox(x2));
      }
      if (isSyntaxCheck)
        return addX(ScriptVariable.sValue(x2));
      if (x2.tok == Token.string) {
        Object v = ScriptVariable
            .unescapePointOrBitsetAsVariable(ScriptVariable.sValue(x2));
        if (!(v instanceof ScriptVariable))
          return false;
        x2 = (ScriptVariable) v;
      }
      if (op.tok == x2.tok)
        x2 = getX();
      return getPointOrBitsetOperation(op, x2);
    }

    // binary:
    ScriptVariable x1 = getX();
    if (isSyntaxCheck) {
      if (op == Token.tokenAndFALSE || op == Token.tokenOrTRUE)
        isSyntaxCheck = false;
      return addX(new ScriptVariable(x1));
    }
    switch (op.tok) {
    case Token.opAND:
    case Token.opAnd:
      switch (x1.tok) {
      case Token.bitset:
        BitSet bs = ScriptVariable.bsSelect(x1);
        switch (x2.tok) {
        case Token.bitset:
          bs = BitSetUtil.copy(bs);
          bs.and(ScriptVariable.bsSelect(x2));
          return addX(bs);
        case Token.integer:
          int x = ScriptVariable.iValue(x2);
          return (addX(x < 0 ? false : bs.get(x)));
        }
        break;
      }
      return addX(ScriptVariable.bValue(x1) && ScriptVariable.bValue(x2));
    case Token.opOr:
      switch (x1.tok) {
      case Token.bitset:
        BitSet bs = BitSetUtil.copy(ScriptVariable.bsSelect(x1));
        switch (x2.tok) {
        case Token.bitset:
          bs.or(ScriptVariable.bsSelect(x2));
          return addX(bs);
        case Token.integer:
          int x = ScriptVariable.iValue(x2);
          if (x < 0) 
            break;
          bs.set(x);
          return addX(bs);
        case Token.varray:
          List<ScriptVariable> sv = (ArrayList<ScriptVariable>) x2.value;
          for (int i = sv.size(); --i >= 0;)
            bs.set(ScriptVariable.iValue(sv.get(i)));
          return addX(bs);
        }
        break;
      case Token.varray:
        return addX(ScriptVariable.concatList(x1, x2, false));
      }
      return addX(ScriptVariable.bValue(x1) || ScriptVariable.bValue(x2));
    case Token.opXor:
      if (x1.tok == Token.bitset && x2.tok == Token.bitset) {
        BitSet bs = BitSetUtil.copy(ScriptVariable.bsSelect(x1));
        bs.xor(ScriptVariable.bsSelect(x2));
        return addX(bs);
      }
      boolean a = ScriptVariable.bValue(x1);
      boolean b = ScriptVariable.bValue(x2);
      return addX(a && !b || b && !a);
    case Token.opToggle:
      if (x1.tok != Token.bitset || x2.tok != Token.bitset)
        return false;
      return addX(BitSetUtil.toggleInPlace(BitSetUtil.copy(ScriptVariable
          .bsSelect(x1)), ScriptVariable.bsSelect(x2)));
    case Token.opLE:
      return addX(ScriptVariable.fValue(x1) <= ScriptVariable.fValue(x2));
    case Token.opGE:
      return addX(ScriptVariable.fValue(x1) >= ScriptVariable.fValue(x2));
    case Token.opGT:
      return addX(ScriptVariable.fValue(x1) > ScriptVariable.fValue(x2));
    case Token.opLT:
      return addX(ScriptVariable.fValue(x1) < ScriptVariable.fValue(x2));
    case Token.opEQ:
      return addX(ScriptVariable.areEqual(x1, x2));
    case Token.opNE:
      if (x1.tok == Token.string && x2.tok == Token.string)
        return addX(!(ScriptVariable.sValue(x1).equalsIgnoreCase(ScriptVariable
            .sValue(x2))));
      if (x1.tok == Token.point3f && x2.tok == Token.point3f)
        return addX(((Point3f) x1.value).distance((Point3f) x2.value) >= 0.000001);
      if (x1.tok == Token.point4f && x2.tok == Token.point4f)
        return addX(((Point4f) x1.value).distance((Point4f) x2.value) >= 0.000001);
      {
        float f1 = ScriptVariable.fValue(x1);
        float f2 = ScriptVariable.fValue(x2);
        return addX(Float.isNaN(f1) || Float.isNaN(f2)
            || Math.abs(f1 - f2) >= 0.000001);
      }
    case Token.plus:
      switch (x1.tok) {
      default:
        return addX(ScriptVariable.fValue(x1) + ScriptVariable.fValue(x2));
      case Token.varray:
        return addX(ScriptVariable.concatList(x1, x2, true));
      case Token.integer:
        switch (x2.tok) {
        case Token.string:
          if ((s = (ScriptVariable.sValue(x2)).trim()).indexOf(".") < 0
              && s.indexOf("+") <= 0 && s.lastIndexOf("-") <= 0)
            return addX(x1.intValue + ScriptVariable.iValue(x2));
          break;
        case Token.decimal:
          return addX(x1.intValue + ScriptVariable.fValue(x2));
        default:
          return addX(x1.intValue + ScriptVariable.iValue(x2));
        }
      case Token.string:
        return addX(new ScriptVariable(Token.string, ScriptVariable.sValue(x1)
            + ScriptVariable.sValue(x2)));
      case Token.point4f:
        Quaternion q1 = new Quaternion((Point4f) x1.value);
        switch (x2.tok) {
        default:
          return addX(q1.add(ScriptVariable.fValue(x2)).toPoint4f());
        case Token.point4f:
          return addX(q1.mul(new Quaternion((Point4f) x2.value)).toPoint4f());
        }
      case Token.point3f:
        pt = new Point3f((Point3f) x1.value);
        switch (x2.tok) {
        case Token.point3f:
          pt.add((Point3f) x2.value);
          return addX(pt);
        case Token.point4f:
          // extract {xyz}
          pt4 = (Point4f) x2.value;
          pt.add(new Point3f(pt4.x, pt4.y, pt4.z));
          return addX(pt);
        default:
          f = ScriptVariable.fValue(x2);
          return addX(new Point3f(pt.x + f, pt.y + f, pt.z + f));
        }
      case Token.matrix3f:
        switch (x2.tok) {
        default:
          return addX(ScriptVariable.fValue(x1) + ScriptVariable.fValue(x2));
        case Token.matrix3f:
          m = new Matrix3f((Matrix3f) x1.value);
          m.add((Matrix3f) x2.value);
          return addX(m);
        case Token.point3f:
          return addX(getMatrix4f((Matrix3f) x1.value, (Point3f) x2.value));
        }
      }
    case Token.minus:
      if (x1.tok == Token.integer) {
        if (x2.tok == Token.string) {
          if ((s = (ScriptVariable.sValue(x2)).trim()).indexOf(".") < 0
              && s.indexOf("+") <= 0 && s.lastIndexOf("-") <= 0)
            return addX(x1.intValue - ScriptVariable.iValue(x2));
        } else if (x2.tok != Token.decimal)
          return addX(x1.intValue - ScriptVariable.iValue(x2));
      }
      if (x1.tok == Token.string && x2.tok == Token.integer) {
        if ((s = (ScriptVariable.sValue(x1)).trim()).indexOf(".") < 0
            && s.indexOf("+") <= 0 && s.lastIndexOf("-") <= 0)
          return addX(ScriptVariable.iValue(x1) - x2.intValue);
      }
      switch (x1.tok) {
      default:
        return addX(ScriptVariable.fValue(x1) - ScriptVariable.fValue(x2));
      case Token.hash:
        Map<String, ScriptVariable> ht = new Hashtable<String, ScriptVariable>(
            (Map<String, ScriptVariable>) x1.value);
        ht.remove(ScriptVariable.sValue(x2));
        return addX(ScriptVariable.getVariable(ht));
      case Token.matrix3f:
        switch (x2.tok) {
        default:
          return addX(ScriptVariable.fValue(x1) - ScriptVariable.fValue(x2));
        case Token.matrix3f:
          m = new Matrix3f((Matrix3f) x1.value);
          m.sub((Matrix3f) x2.value);
          return addX(m);
        }
      case Token.matrix4f:
        switch (x2.tok) {
        default:
          return addX(ScriptVariable.fValue(x1) - ScriptVariable.fValue(x2));
        case Token.matrix4f:
          Matrix4f m4 = new Matrix4f((Matrix4f) x1.value);
          m4.sub((Matrix4f) x2.value);
          return addX(m4);
        }
      case Token.point3f:
        pt = new Point3f((Point3f) x1.value);
        switch (x2.tok) {
        default:
          f = ScriptVariable.fValue(x2);
          return addX(new Point3f(pt.x - f, pt.y - f, pt.z - f));
        case Token.point3f:
          pt.sub((Point3f) x2.value);
          return addX(pt);
        case Token.point4f:
          // extract {xyz}
          pt4 = (Point4f) x2.value;
          pt.sub(new Point3f(pt4.x, pt4.y, pt4.z));
          return addX(pt);
        }
      case Token.point4f:
        Quaternion q1 = new Quaternion((Point4f) x1.value);
        switch (x2.tok) {
        default:
          return addX(q1.add(-ScriptVariable.fValue(x2)).toPoint4f());
        case Token.point4f:
          Quaternion q2 = new Quaternion((Point4f) x2.value);
          return addX(q2.mul(q1.inv()).toPoint4f());
        }
      }
    case Token.unaryMinus:
      switch (x2.tok) {
      default:
        return addX(-ScriptVariable.fValue(x2));
      case Token.integer:
        return addX(-ScriptVariable.iValue(x2));
      case Token.point3f:
        pt = new Point3f((Point3f) x2.value);
        pt.scale(-1f);
        return addX(pt);
      case Token.point4f:
        pt4 = new Point4f((Point4f) x2.value);
        pt4.scale(-1f);
        return addX(pt4);
      case Token.matrix3f:
        m = new Matrix3f((Matrix3f) x2.value);
        m.transpose();
        return addX(m);
      case Token.matrix4f:
        Matrix4f m4 = new Matrix4f((Matrix4f) x2.value);
        m4.transpose();
        return addX(m4);
      case Token.bitset:
        return addX(BitSetUtil.copyInvert(ScriptVariable.bsSelect(x2),
            (x2.value instanceof BondSet ? viewer.getBondCount() : viewer
                .getAtomCount())));
      }
    case Token.times:
      if (x1.tok == Token.integer && x2.tok != Token.decimal)
        return addX(x1.intValue * ScriptVariable.iValue(x2));
      pt = (x1.tok == Token.matrix3f ? ptValue(x2, false)
          : x2.tok == Token.matrix3f ? ptValue(x1, false) : null);
      pt4 = (x1.tok == Token.matrix4f ? planeValue(x2)
          : x2.tok == Token.matrix4f ? planeValue(x1) : null);
      // checking here to make sure arrays remain arrays and
      // points remain points with matrix operations.
      // we check x2, because x1 could be many things.
      switch (x2.tok) {
      case Token.matrix3f:
        if (pt != null) {
          // pt * m
          Matrix3f m3b = new Matrix3f((Matrix3f) x2.value);
          m3b.transpose();
          m3b.transform(pt);
          if (x1.tok == Token.varray)
            return addX(ScriptVariable.getVariable(new float[] { pt.x, pt.y,
                pt.z }));
          return addX(pt);
        }
        if (pt4 != null) {
          // q * m --> q
          return addX(((new Quaternion(pt4)).mul(new Quaternion(
              (Matrix3f) x2.value))));
        }
        break;
      case Token.matrix4f:
        // pt4 * m4
        // [a b c d] * m4
        if (pt4 != null) {
          Matrix4f m4b = new Matrix4f((Matrix4f) x2.value);
          m4b.transpose();
          m4b.transform(pt4);
          if (x1.tok == Token.varray)
            return addX(ScriptVariable.getVariable(new float[] { pt4.x, pt4.y,
                pt4.z, pt4.w }));
          return addX(pt4);
        }
        break;
      }
      switch (x1.tok) {
      default:
        return addX(ScriptVariable.fValue(x1) * ScriptVariable.fValue(x2));
      case Token.matrix3f:
        Matrix3f m3 = (Matrix3f) x1.value;
        if (pt != null) {
          m3.transform(pt);
          if (x2.tok == Token.varray)
            return addX(ScriptVariable.getVariable(new float[] { pt.x, pt.y,
                pt.z }));
          return addX(pt);
        }
        switch (x2.tok) {
        case Token.matrix3f:
          m = new Matrix3f((Matrix3f) x2.value);
          m.mul(m3, m);
          return addX(m);
        case Token.point4f:
          // m * q
          return addX((new Quaternion(m3)).mul(
              new Quaternion((Point4f) x2.value)).getMatrix());
        default:
          f = ScriptVariable.fValue(x2);
          AxisAngle4f aa = new AxisAngle4f();
          aa.set(m3);
          aa.angle *= f;
          Matrix3f m2 = new Matrix3f();
          m2.set(aa);
          return addX(m2);
        }
      case Token.matrix4f:
        Matrix4f m4 = (Matrix4f) x1.value;
        if (pt != null) {
          m4.transform(pt);
          if (x2.tok == Token.varray)
            return addX(ScriptVariable.getVariable(new float[] { pt.x, pt.y,
                pt.z }));
          return addX(pt);
        }
        if (pt4 != null) {
          m4.transform(pt4);
          if (x2.tok == Token.varray)
            return addX(ScriptVariable.getVariable(new float[] { pt4.x, pt4.y,
                pt4.z, pt4.w }));
          return addX(pt4);
        }
        switch (x2.tok) {
        case Token.matrix4f:
          Matrix4f m4b = new Matrix4f((Matrix4f) x2.value);
          m4b.mul(m4, m4b);
          return addX(m4b);
        default:
          return addX("NaN");
        }
      case Token.point3f:
        pt = new Point3f((Point3f) x1.value);
        switch (x2.tok) {
        case Token.point3f:
          Point3f pt2 = ((Point3f) x2.value);
          return addX(pt.x * pt2.x + pt.y * pt2.y + pt.z * pt2.z);
        default:
          f = ScriptVariable.fValue(x2);
          return addX(new Point3f(pt.x * f, pt.y * f, pt.z * f));
        }
      case Token.point4f:
        switch (x2.tok) {
        case Token.point4f:
          // quaternion multiplication
          // note that Point4f is {x,y,z,w} so we use that for
          // quaternion notation as well here.
          return addX((new Quaternion((Point4f) x1.value)).mul(new Quaternion(
              (Point4f) x2.value)));
        }
        return addX(new Quaternion((Point4f) x1.value).mul(
            ScriptVariable.fValue(x2)).toPoint4f());
      }
    case Token.percent:
      // more than just modulus

      // float % n round to n digits; n = 0 does "nice" rounding
      // String % -n trim to width n; left justify
      // String % n trim to width n; right justify
      // Point3f % n ah... sets to multiple of unit cell!
      // bitset % n
      // Point3f * Point3f does dot product
      // Point3f / Point3f divides by magnitude
      // float * Point3f gets m agnitude
      // Point4f % n returns q0, q1, q2, q3, or theta
      // Point4f % Point4f
      s = null;
      int n = ScriptVariable.iValue(x2);
      switch (x1.tok) {
      case Token.on:
      case Token.off:
      case Token.integer:
      default:
        if (n == 0)
          return addX(0);
        return addX(ScriptVariable.iValue(x1) % n);
      case Token.decimal:
        f = ScriptVariable.fValue(x1);
        // neg is scientific notation
        if (n == 0)
          return addX((int) (f + 0.5f * (f < 0 ? -1 : 1)));
        s = TextFormat.formatDecimal(f, n);
        return addX(s);
      case Token.string:
        s = (String) x1.value;
        if (n == 0)
          return addX(TextFormat.trim(s, "\n\t "));
        if (n == 9999)
          return addX(s.toUpperCase());
        if (n == -9999)
          return addX(s.toLowerCase());
        if (n > 0)
          return addX(TextFormat.format(s, n, n, false, false));
        return addX(TextFormat.format(s, n, n - 1, true, false));
      case Token.varray:
        String[] list = ScriptVariable.listValue(x1);
        for (int i = 0; i < list.length; i++) {
          if (n == 0)
            list[i] = list[i].trim();
          else if (n > 0)
            list[i] = TextFormat.format(list[i], n, n, true, false);
          else
            list[i] = TextFormat.format(s, -n, n, false, false);
        }
        return addX(list);
      case Token.point3f:
        pt = new Point3f((Point3f) x1.value);
        viewer.toUnitCell(pt, new Point3f(n, n, n));
        return addX(pt);
      case Token.point4f:
        pt4 = (Point4f) x1.value;
        if (x2.tok == Token.point3f)
          return addX((new Quaternion(pt4)).transform((Point3f) x2.value));
        if (x2.tok == Token.point4f) {
          Point4f v4 = new Point4f((Point4f) x2.value);
          (new Quaternion(pt4)).getThetaDirected(v4);
          return addX(v4);
        }
        switch (n) {
        // q%0 w
        // q%1 x
        // q%2 y
        // q%3 z
        // q%4 normal
        // q%-1 vector(1)
        // q%-2 theta
        // q%-3 Matrix column 0
        // q%-4 Matrix column 1
        // q%-5 Matrix column 2
        // q%-6 AxisAngle format
        // q%-9 Matrix format
        case 0:
          return addX(pt4.w);
        case 1:
          return addX(pt4.x);
        case 2:
          return addX(pt4.y);
        case 3:
          return addX(pt4.z);
        case 4:
          return addX((new Quaternion(pt4)).getNormal());
        case -1:
          return addX(new Quaternion(pt4).getVector(-1));
        case -2:
          return addX((new Quaternion(pt4)).getTheta());
        case -3:
          return addX((new Quaternion(pt4)).getVector(0));
        case -4:
          return addX((new Quaternion(pt4)).getVector(1));
        case -5:
          return addX((new Quaternion(pt4)).getVector(2));
        case -6:
          AxisAngle4f ax = (new Quaternion(pt4)).toAxisAngle4f();
          return addX(new Point4f(ax.x, ax.y, ax.z,
              (float) (ax.angle * 180 / Math.PI)));
        case -9:
          return addX((new Quaternion(pt4)).getMatrix());
        default:
          return addX(pt4);
        }
      case Token.matrix4f:
        Matrix4f m4 = (Matrix4f) x1.value;
        switch (n) {
        case 1:
          Matrix3f m3 = new Matrix3f();
          m4.get(m3);
          return addX(m3);
        case 2:
          Vector3f v3 = new Vector3f();
          m4.get(v3);
          return addX(v3);
        default:
          return false;
        }
      case Token.bitset:
        return addX(ScriptVariable.bsSelectRange(x1, n));
      }
    case Token.divide:
      if (x1.tok == Token.integer && x2.tok == Token.integer
          && x2.intValue != 0)
        return addX(x1.intValue / x2.intValue);
      float f2 = ScriptVariable.fValue(x2);
      switch (x1.tok) {
      default:
        float f1 = ScriptVariable.fValue(x1);
        if (f2 == 0)
          return addX(f1 == 0 ? 0f : f1 < 0 ? Float.POSITIVE_INFINITY
              : Float.POSITIVE_INFINITY);
        return addX(f1 / f2);
      case Token.point3f:
        pt = new Point3f((Point3f) x1.value);
        if (f2 == 0)
          return addX(new Point3f(Float.NaN, Float.NaN, Float.NaN));
        return addX(new Point3f(pt.x / f2, pt.y / f2, pt.z / f2));
      case Token.point4f:
        if (x2.tok == Token.point4f)
          return addX(new Quaternion((Point4f) x1.value).div(
              new Quaternion((Point4f) x2.value)).toPoint4f());
        if (f2 == 0)
          return addX(new Point4f(Float.NaN, Float.NaN, Float.NaN, Float.NaN));
        return addX(new Quaternion((Point4f) x1.value).mul(1 / f2).toPoint4f());
      }
    case Token.leftdivide:
      f = ScriptVariable.fValue(x2);
      switch (x1.tok) {
      default:
        return addX(f == 0 ? 0
            : (int) (ScriptVariable.fValue(x1) / ScriptVariable.fValue(x2)));
      case Token.point4f:
        if (f == 0)
          return addX(new Point4f(Float.NaN, Float.NaN, Float.NaN, Float.NaN));
        if (x2.tok == Token.point4f)
          return addX(new Quaternion((Point4f) x1.value).divLeft(
              new Quaternion((Point4f) x2.value)).toPoint4f());
        return addX(new Quaternion((Point4f) x1.value).mul(1 / f).toPoint4f());
      }
    case Token.timestimes:
      f = (float) Math
          .pow(ScriptVariable.fValue(x1), ScriptVariable.fValue(x2));
      return (x1.tok == Token.integer && x2.tok == Token.integer ? addX((int) f)
          : addX(f));
    }
    return true;
  }

  static Matrix4f getMatrix4f(Matrix3f matRotate, Tuple3f vTranslate) {
    return new Matrix4f(matRotate, vTranslate == null ? new Vector3f() : new Vector3f(vTranslate), 1);
  }

  private boolean getBoundBox(ScriptVariable x2) {
    if (x2.tok != Token.bitset)
      return false;
    if (isSyntaxCheck)
      return addX("");
    BoxInfo b = viewer.getBoxInfo(ScriptVariable.bsSelect(x2), 1);
    Point3f[] pts = b.getBoundBoxPoints();
    return addX(new String[] { Escape.escape(pts[0]), Escape.escape(pts[1]),
        Escape.escape(pts[2]), Escape.escape(pts[3]) });
  }

  private boolean getPointOrBitsetOperation(Token op, ScriptVariable x2)
      throws ScriptException {
    switch (x2.tok) {
    case Token.varray:
      if (op.intValue == Token.min || op.intValue == Token.max
          || op.intValue == Token.average || op.intValue == Token.stddev
          || op.intValue == Token.sum || op.intValue == Token.sum2) {
        return addX(getMinMax(x2.getList(), op.intValue));
      }
      if (op.intValue == Token.sort || op.intValue == Token.reverse)
        return addX(x2.sortOrReverse(op.intValue == Token.reverse ? Integer.MIN_VALUE : 1));
      ScriptVariable[] list2 = new ScriptVariable[x2.getList().size()];
      for (int i = 0; i < list2.length; i++) {
        Object v = ScriptVariable.unescapePointOrBitsetAsVariable(x2.getList().get(i));
        if (!(v instanceof ScriptVariable)
            || !getPointOrBitsetOperation(op, (ScriptVariable) v))
          return false;
        list2[i] = xStack[xPt--];
      }
      return addX(list2);
    case Token.point3f:
      switch (op.intValue) {
      case Token.atomx:
      case Token.x:
        return addX(((Point3f) x2.value).x);
      case Token.atomy:
      case Token.y:
        return addX(((Point3f) x2.value).y);
      case Token.atomz:
      case Token.z:
        return addX(((Point3f) x2.value).z);
      case Token.xyz:
        Point3f pt = new Point3f((Point3f) x2.value);
        // assumes a fractional coordinate
        viewer.toCartesian(pt, true);
        return addX(pt);
      case Token.fracx:
      case Token.fracy:
      case Token.fracz:
      case Token.fracxyz:
        Point3f ptf = new Point3f((Point3f) x2.value);
        viewer.toFractional(ptf, true);
        return (op.intValue == Token.fracxyz ? addX(ptf)
            : addX(op.intValue == Token.fracx ? ptf.x
                : op.intValue == Token.fracy ? ptf.y : ptf.z));
      case Token.fux:
      case Token.fuy:
      case Token.fuz:
      case Token.fuxyz:
        Point3f ptfu = new Point3f((Point3f) x2.value);
        viewer.toFractional(ptfu, false);
        return (op.intValue == Token.fracxyz ? addX(ptfu)
            : addX(op.intValue == Token.fux ? ptfu.x
                : op.intValue == Token.fuy ? ptfu.y : ptfu.z));
      case Token.unitx:
      case Token.unity:
      case Token.unitz:
      case Token.unitxyz:
        Point3f ptu = new Point3f((Point3f) x2.value);
        viewer.toUnitCell(ptu, null);
        viewer.toFractional(ptu, false);
        return (op.intValue == Token.unitxyz ? addX(ptu)
            : addX(op.intValue == Token.unitx ? ptu.x
                : op.intValue == Token.unity ? ptu.y : ptu.z));
      }
      break;
    case Token.point4f:
      switch (op.intValue) {
      case Token.atomx:
      case Token.x:
        return addX(((Point4f) x2.value).x);
      case Token.atomy:
      case Token.y:
        return addX(((Point4f) x2.value).y);
      case Token.atomz:
      case Token.z:
        return addX(((Point4f) x2.value).z);
      case Token.w:
        return addX(((Point4f) x2.value).w);
      }
      break;
    case Token.bitset:
      if (op.intValue == Token.bonds && x2.value instanceof BondSet)
        return addX(x2);
      BitSet bs = ScriptVariable.bsSelect(x2);
      Object val = eval.getBitsetProperty(bs, op.intValue, null, null,
          x2.value, op.value, false, x2.index, false);
      if (op.intValue == Token.bonds)
        val = new ScriptVariable(Token.bitset, new BondSet((BitSet) val, viewer
            .getAtomIndices(bs)));
      return addX(val);
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  private static Object getMinMax(Object floatOrSVArray, int tok) {
    float[] data = null;
    List<ScriptVariable> sv = null;
    int ndata = 0;
    while (true) {
      if (floatOrSVArray instanceof float[]) {
        data = (float[]) floatOrSVArray;
        ndata = data.length;
        if (ndata == 0)
          break;
      } else if (floatOrSVArray instanceof List<?>) {
        sv = (ArrayList<ScriptVariable>) floatOrSVArray;
        ndata = sv.size();
        if (ndata == 0)
          break;
        ScriptVariable sv0 = sv.get(0);
        if (sv0.tok == Token.string && ((String) sv0.value).startsWith("{")) {
          Object pt = ScriptVariable.ptValue(sv0);
          if (pt instanceof Point3f)
            return getMinMaxPoint(sv, tok);
          if (pt instanceof Point4f)
            return getMinMaxQuaternion(sv, tok);
          break;
        }
      } else {
        break;
      }
      double sum;
      switch (tok) {
      case Token.min:
        sum = Float.MAX_VALUE;
        break;
      case Token.max:
        sum = -Float.MAX_VALUE;
        break;
      default:
        sum = 0;
      }
      double sum2 = 0;
      int n = 0;
      for (int i = ndata; --i >= 0;) {
        float v = (data == null ? ScriptVariable.fValue(sv.get(i)) : data[i]);
        if (Float.isNaN(v))
          continue;
        n++;
        switch (tok) {
        case Token.sum2:
        case Token.stddev:
          sum2 += ((double) v) * v;
          // fall through
        case Token.sum:
        case Token.average:
          sum += v;
          break;
        case Token.min:
          if (v < sum)
            sum = v;
          break;
        case Token.max:
          if (v > sum)
            sum = v;
          break;
        }
      }
      if (n == 0)
        break;
      switch (tok) {
      case Token.average:
        sum /= n;
        break;
      case Token.stddev:
        if (n == 1)
          break;
        sum = Math.sqrt((sum2 - sum * sum / n) / (n - 1));
        break;
      case Token.min:
      case Token.max:
      case Token.sum:
        break;
      case Token.sum2:
        sum = sum2;
        break;
      }
      return Float.valueOf((float) sum);
    }
    return "NaN";
  }

  /**
   * calculates the statistical value for x, y, and z independently
   * 
   * @param pointOrSVArray
   * @param tok
   * @return Point3f or "NaN"
   */
  @SuppressWarnings("unchecked")
  private static Object getMinMaxPoint(Object pointOrSVArray, int tok) {
    Point3f[] data = null;
    List<ScriptVariable> sv = null;
    int ndata = 0;
    if (pointOrSVArray instanceof Quaternion[]) {
      data = (Point3f[]) pointOrSVArray;
      ndata = data.length;
    } else if (pointOrSVArray instanceof List<?>) {
      sv = (ArrayList<ScriptVariable>) pointOrSVArray;
      ndata = sv.size();
    }
    if (sv != null || data != null) {
      Point3f result = new Point3f();
      float[] fdata = new float[ndata];
      boolean ok = true;
      for (int xyz = 0; xyz < 3 && ok; xyz++) {
        for (int i = 0; i < ndata; i++) {
          Point3f pt = (data == null ? ScriptVariable.ptValue(sv.get(i)) : data[i]);
          if (pt == null) {
            ok = false;
            break;
          }
          switch (xyz) {
          case 0:
            fdata[i] = pt.x;
            break;
          case 1:
            fdata[i] = pt.y;
            break;
          case 2:
            fdata[i] = pt.z;
            break;
          }
        }
        if (!ok)
          break;
        Object f = getMinMax(fdata, tok);
        if (f instanceof Float) {
          float value = ((Float) f).floatValue();
          switch (xyz) {
          case 0:
            result.x = value;
            break;
          case 1:
            result.y = value;
            break;
          case 2:
            result.z = value;
            break;
          }
        } else {
          break;
        }
      }
      return result;
    }
    return "NaN";
  }

  private static Object getMinMaxQuaternion(Object quaternionOrSVData,
                                            int tok) {
    Quaternion[] data;
    switch (tok) {
    case Token.min:
    case Token.max:
    case Token.sum:
    case Token.sum2:
      return "NaN";
    }

    // only stddev and average

    while (true) {
      data = getQuaternionArray(quaternionOrSVData);
      if (data == null)
        break;
      float[] retStddev = new float[1];
      Quaternion result = Quaternion.sphereMean(data, retStddev, 0.0001f);
      switch (tok) {
      case Token.average:
        return result;
      case Token.stddev:
        return new Float(retStddev[0]);
      }
      break;
    }
    return "NaN";
  }

  @SuppressWarnings("unchecked")
  protected static Quaternion[] getQuaternionArray(Object quaternionOrSVData) {
    Quaternion[] data;
    if (quaternionOrSVData instanceof Quaternion[]) {
      data = (Quaternion[]) quaternionOrSVData;
    } else if (quaternionOrSVData instanceof Point4f[]) {
      Point4f[] pts = (Point4f[]) quaternionOrSVData;
      data = new Quaternion[pts.length];
      for (int i = 0; i < pts.length; i++)
        data[i] = new Quaternion(pts[i]);
    } else if (quaternionOrSVData instanceof List<?>) {
      List<ScriptVariable> sv = (ArrayList<ScriptVariable>) quaternionOrSVData;
      data = new Quaternion[sv.size()];
      for (int i = 0; i < sv.size(); i++) {
        Point4f pt = ScriptVariable.pt4Value(sv.get(i));
        if (pt == null)
          return null;
        data[i] = new Quaternion(pt);
      }
    } else {
      return null;
    }
    return data;
  }

}
