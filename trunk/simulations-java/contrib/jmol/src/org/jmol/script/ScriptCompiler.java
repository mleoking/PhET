/* $Author: hansonr $
 * $Date: 2011-02-19 20:52:15 -0800 (Sat, 19 Feb 2011) $
 * $Revision: 15214 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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

import org.jmol.util.Escape;
import org.jmol.util.CommandHistory;
import org.jmol.util.JpegEncoder;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;
import org.jmol.i18n.GT;
import org.jmol.modelset.Group;
import org.jmol.modelset.Bond.BondSet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;

public class ScriptCompiler extends ScriptCompilationTokenParser {

  /**
   * The Compiler class is really two parts -- 
   * 
   * Compiler.class          going from characters to tokens
   * CompilationTokenParser  further syntax checking and modifications
   * 
   * The data structures follow the following sequences:
   * 
   * String script ==> Vector lltoken[][] --> Token[][] aatokenCompiled[][]
   * 
   * A given command goes through the sequence:
   * 
   * String characters --> Token token --> Vector ltoken[] --> Token[][] aatokenCompiled[][]
   * 
   */
  private static final String LOAD_TYPES = "auto;append;data;files;inline;menu;smiles;trajectory;models;" + JmolConstants.LOAD_ATOM_DATA_TYPES;
  
  public ScriptCompiler(Viewer viewer) {
    this.viewer = viewer;
  }
  
  public ScriptCompiler(ScriptCompiler compiler) {
    this.viewer = compiler.viewer;
  }

  private String filename;
  private boolean isSilent;

  // returns:
  
  private Map<String, ScriptVariable> contextVariables;
  private Token[][] aatokenCompiled;
  private short[] lineNumbers;
  private int[][] lineIndices;
  
  private int lnLength = 8;
  private boolean preDefining;
  private boolean isShowScriptOutput;
  private boolean isCheckOnly;
  private boolean haveComments;

  String scriptExtensions;
  
  private ScriptFunction thisFunction;
  
 
  /**
   * return a structure that is only the first part of the process - identifying lines and commands
   * for the scriptEditor
   * 
   * @param script
   * @return        ScriptContext
   */
  
  ScriptContext parseScriptForTokens(String script) {
    this.script = script;
    filename = null;
    isCheckOnly = true;
    isSilent = true;
    logMessages = false;
    preDefining = false;
    return parseScript(false);
  }
 
  private ScriptContext parseScript(boolean doFull) {
    if (!compile0(doFull))
      handleError();
    ScriptContext sc = new ScriptContext();
    sc.script = script;
    sc.scriptExtensions = scriptExtensions;
    sc.errorType = errorType;
    if (errorType != null) {
      sc.iCommandError = iCommand;
      setAaTokenCompiled();
    }
    sc.aatoken = aatokenCompiled;
    sc.errorMessage = errorMessage;
    sc.errorMessageUntranslated = (errorMessageUntranslated == null 
        ? errorMessage : errorMessageUntranslated);
    sc.lineIndices = lineIndices;
    sc.lineNumbers = lineNumbers;
    sc.contextVariables = contextVariables;
    return sc;
  }

  ScriptContext compile(String filename, String script, boolean isPredefining,
                  boolean isSilent, boolean debugScript, boolean isCheckOnly) {
    this.isCheckOnly = isCheckOnly;
    this.filename = filename;
    this.isSilent = isSilent;
    this.script = script;
    logMessages = (!isSilent && !isPredefining && debugScript);
    preDefining = (filename == "#predefine");
    return parseScript(true);
  }

  private void addContextVariable(String ident) {
    theToken = new Token(Token.identifier, ident);
    if (pushCount > 0) {
      ContextToken ct = (ContextToken) vPush.get(pushCount - 1);
      ct.addName(ident);
      if (ct.tok != Token.trycmd)
        return;
    }
    if (thisFunction == null) {
      if (contextVariables == null)
        contextVariables = new Hashtable<String, ScriptVariable>();
      addContextVariable(contextVariables, ident);
    } else {
      thisFunction.addVariable(ident, false);
    }
  }
  
  static void addContextVariable(Map<String, ScriptVariable> contextVariables, String ident) {
    contextVariables.put(ident, (new ScriptVariable(Token.string, "")).setName(ident));
  }

  private boolean isContextVariable(String ident) {
    for (int i = vPush.size(); --i >= 0;) {
      ContextToken ct = (ContextToken) vPush.get(i);
      if (ct.contextVariables != null && ct.contextVariables.containsKey(ident))
        return true;
    }
    return (thisFunction != null ? thisFunction.isVariable(ident)
      : contextVariables != null && contextVariables.containsKey(ident));
  }
  
  /**
   * allows for three kinds of comments.
   * NOTE: closing involves asterisks and slash together, but that can't be shown here. 
   * 
   * 1) /** .... ** /  super-comment
   * 2) /* ..... * /   may be INSIDE /**....** /).
   * 3)  \n//.....\n   single-line comments -- like #, but removed entirely 
   * The reason is that /* ... * / will appear as standard in MOVETO command
   * but we still might want to escape it, so around that you can have /** .... ** /
   * 
   * The terminator is not necessary -- so you can quickly escape anything in a file 
   * after /** or /*
   * 
   * In addition, we can have [/*|/**] .... **** Jmol Embedded Script ****  [script commands] [** /|* /]
   * Then ONLY that script is taken. This is a powerful and simple way then to include Jmol scripting
   * in any file -- including, for example, HTML as an HTML comment. Just send the whole file to 
   * Jmol, and it will find its script!
   * 
   * @param script
   * @return cleaned script
   */
  private String cleanScriptComments(String script) {
    int pt = (script.indexOf("\1##"));
    if (pt >= 0) {
      // these are for jmolConsole and scriptEditor
      scriptExtensions = script.substring(pt + 1);
      script = script.substring(0, pt);
    }
    haveComments = (script.indexOf("#") >= 0); // speeds processing
    return getEmbeddedScript(script);
  }
  
  public static String getEmbeddedScript(String script) {
    if (script == null)
      return script;
    int pt = script.indexOf(JmolConstants.EMBEDDED_SCRIPT_TAG);
    if (pt < 0)
      return script;
    int pt1 = script.lastIndexOf("/*", pt);
    int pt2 = script.indexOf((script.charAt(pt1 + 2) == '*' ? "*" : "") + "*/",
        pt);
    if (pt1 >= 0 && pt2 >= pt)
      script = script.substring(
          pt + JmolConstants.EMBEDDED_SCRIPT_TAG.length(), pt2)
          + "\n";
    while ((pt1 = script.indexOf(JpegEncoder.CONTINUE_STRING)) >= 0)
      script = script.substring(0, pt1)
          + script.substring(pt1 + JpegEncoder.CONTINUE_STRING.length() + 4);
    if (Logger.debugging)
      Logger.info(script);
    return script;
  }

  private ScriptFlowContext flowContext;
  private List<Token> ltoken;
  private List<Token[]> lltoken;
  private List<Token> vBraces;


  private int ichBrace;
  private int cchToken;
  private int cchScript;

  private int nSemiSkip;
  private int parenCount;
  private int braceCount;
  private int setBraceCount;
  private int bracketCount;
  private int ptSemi;
  private int forPoint3;
  private int setEqualPt;
  private int iBrace;

  private boolean iHaveQuotedString;
  private boolean isEndOfCommand;
  private boolean needRightParen;
  private boolean endOfLine;

  private String comment;

  private void addTokenToPrefix(Token token) {
    if (logMessages)
      Logger.info("addTokenToPrefix" + token);
    ltoken.add(token);
    lastToken = token;
  }

  private final static int OK = 0;
  private final static int OK2 = 1;
  private final static int CONTINUE = 2;
  private final static int EOL = 3;
  private final static int ERROR = 4;

  private int tokLastMath;
  
  private List<ScriptFunction> vFunctionStack;
  
  private boolean compile0(boolean isFull) {
    vFunctionStack = new ArrayList<ScriptFunction>();
    htUserFunctions = new Hashtable<String, Boolean>();
    script = script.replace('\u201C', '"').replace('\u201D', '"');
    script = cleanScriptComments(script);
    ichToken = script.indexOf(Viewer.STATE_VERSION_STAMP);
    isStateScript = (ichToken >= 0);
    if (isStateScript) {
      ptSemi = script.indexOf(";", ichToken);
      if (ptSemi >= ichToken)
        viewer.setStateScriptVersion(script.substring(
            ichToken + Viewer.STATE_VERSION_STAMP.length(), ptSemi).trim());
    }
    cchScript = this.script.length();

    // these four will be returned:
    contextVariables = null;
    lineNumbers = null;
    lineIndices = null;
    aatokenCompiled = null;
    thisFunction = null;
    flowContext = null;
    errorType = null;
    errorMessage = null;
    errorMessageUntranslated = null;
    errorLine = null;

    nSemiSkip = 0;
    ichToken = 0;
    ichCurrentCommand = 0;
    ichComment = 0;
    ichBrace = 0;
    lineCurrent = 1;
    iCommand = 0;
    tokLastMath = 0;
    lastToken = Token.tokenOff;
    vBraces = new ArrayList<Token>();
    vPush = new ArrayList<Token>();
    pushCount = 0;
    iBrace = 0;
    braceCount = 0;
    parenCount = 0;
    ptSemi = -10;
    cchToken = 0;
    lnLength = 8;
    lineNumbers = new short[lnLength];
    lineIndices = new int[lnLength][2];
    isNewSet = isSetBrace = false;
    ptNewSetModifier = 1;
    isShowScriptOutput = false;    
    iHaveQuotedString = false;
    lltoken = new ArrayList<Token[]>();
    ltoken = new ArrayList<Token>();
    tokCommand = Token.nada;
    lastFlowCommand = null;
    tokenAndEquals = null;
    setBraceCount = 0;
    bracketCount = 0;
    forPoint3 = -1;
    setEqualPt = Integer.MAX_VALUE;
    endOfLine = false;
    comment = null;
    isEndOfCommand = false;
    needRightParen = false;
    theTok = Token.nada;
    short iLine = 1;

    for (; true; ichToken += cchToken) {
      if ((nTokens = ltoken.size()) == 0) { 
        if (thisFunction != null && thisFunction.chpt0 == 0)
          thisFunction.chpt0 = ichToken;
        ichCurrentCommand = ichToken;
        iLine = lineCurrent;
      }
      if (lookingAtLeadingWhitespace())
        continue;
      endOfLine = false;
      if (!isEndOfCommand) {
        endOfLine = lookingAtEndOfLine();
        switch (endOfLine ? OK : lookingAtComment()) {
        case CONTINUE: //  short /*...*/ or comment to completely ignore 
          continue;
        case EOL: // /* .... \n ... */ -- flag as end of line but ignore
          isEndOfCommand = true;
          continue;
        case OK2: // really just line-ending comment -- mark it for later inclusion
          isEndOfCommand = true;
          // start-of line comment -- include as Token.nada 
          comment = script.substring(ichToken, ichToken + cchToken).trim();
          break;
        }
        isEndOfCommand = isEndOfCommand || endOfLine || lookingAtEndOfStatement();
      }
      
      if (isEndOfCommand) {
        isEndOfCommand = false;
        switch (processTokenList(iLine, isFull)) {
        case CONTINUE:
          continue;
        case ERROR:
          return false;
        }
        if (ichToken < cchScript)
          continue;
        setAaTokenCompiled();
        return (flowContext == null 
            || error(ERROR_missingEnd, Token.nameOf(flowContext.token.tok)));
      }
      
      if (nTokens > 0) {
        switch (checkSpecialParameterSyntax()) {
        case CONTINUE:
          continue;
        case ERROR:
          return false;
        }
      }
      if (lookingAtLookupToken(ichToken)) {        
        String ident = getPrefixToken();
        switch (parseKnownToken(ident)) {
        case CONTINUE:
          continue;
        case ERROR:
          return false;
        }
        switch (parseCommandParameter(ident)) {
        case CONTINUE:
          continue;
        case ERROR:
          return false;
        }
        addTokenToPrefix(theToken);
        continue;
      }
      if (nTokens == 0 || (isNewSet || isSetBrace)
          && nTokens == ptNewSetModifier) {
        if (nTokens == 0) {
          if (lookingAtString(true)) {
            addTokenToPrefix(setCommand(Token.tokenScript));
            cchToken = 0;
            continue;
          }
          if (lookingAtImpliedString(true))
            ichEnd = ichToken + cchToken;
        }
        return commandExpected();
      }
      return error(ERROR_unrecognizedToken, script.substring(ichToken,
          ichToken + 1));
    }
  }
  
  private void setAaTokenCompiled() {
    aatokenCompiled = new Token[lltoken.size()][];
    for (int i = lltoken.size(); --i >= 0;)
      aatokenCompiled[i] = lltoken.get(i);
  }

  private boolean lookingAtLeadingWhitespace() {
    int ichT = ichToken;
    while (ichT < cchScript && isSpaceOrTab(script.charAt(ichT)))
      ++ichT;
    if (isLineContinuation(ichT, true))
      ichT += 1 + nCharNewLine(ichT + 1);
    cchToken = ichT - ichToken;
    return cchToken > 0;
  }

  private boolean isLineContinuation(int ichT, boolean checkMathop) {
    boolean isEscaped = (ichT + 2 < cchScript && script.charAt(ichT) == '\\' && nCharNewLine(ichT + 1) > 0 
        || checkMathop && lookingAtMathContinuation(ichT));   
    if (isEscaped)
      lineCurrent++;
    return isEscaped;
  }

  private boolean lookingAtMathContinuation(int ichT) {
    int n;
    if (ichT >= cchScript || (n = nCharNewLine(ichT)) == 0 || lastToken.tok == Token.leftbrace)
      return false;
    if (parenCount > 0 || bracketCount > 0)
      return true;
    if ((tokCommand != Token.set || !isNewSet) && tokCommand != Token.print && tokCommand != Token.log)
        return false;
    if (lastToken.tok == tokLastMath)
      return true;
    ichT += n;
    while (ichT < cchScript && isSpaceOrTab(script.charAt(ichT)))
      ++ichT;
    return (lookingAtLookupToken(ichT) 
        && tokLastMath == 1);
  }

  private boolean lookingAtEndOfLine() {
    int ichT = ichEnd = ichToken;
    if (ichToken >= cchScript) {
      ichEnd = cchScript;
      return true;
    }
    int n = nCharNewLine(ichT);
    if (n == 0)
      return false;
    ichEnd = ichToken;
    cchToken = n;
    return true;    
  }
  
  private int nCharNewLine(int ichT) {
    char ch;
    return (ichT >= cchScript ? 0 
        : (ch = script.charAt(ichT)) != '\r' ? (ch == '\n' ? 1 : 0) 
        : ++ichT < cchScript && script.charAt(ichT) == '\n' ? 2 : 1);
  }

  private boolean lookingAtEndOfStatement() {
    boolean isSemi = (script.charAt(ichToken) == ';');
    if (isSemi && nTokens > 0)
      ptSemi = nTokens;
    if (!isSemi || nSemiSkip-- > 0)
      return false;
    cchToken = 1;
    return true;
  }

  private boolean isShowCommand;
  private boolean inlineLoad;
  
  private int lookingAtComment() {
    char ch = script.charAt(ichToken);
    int ichT = ichToken;
    int ichFirstSharp = -1;

    // return CONTINUE: totally ignore
    // return EOL: treat as line end, even though it isn't
    // return OK: no comment here

    /*
     * New in Jmol 11.1.9: we allow for output from the set showScript command
     * to be used as input. These lines start with $ and have a [...] phrase
     * after them. Their presence switches us to this new mode where we use
     * those statements as our commands and any line WITHOUT those as comments.
     */
    if (ichToken == ichCurrentCommand && ch == '$') {
      isShowScriptOutput = true;
      isShowCommand = true;
      while (ch != ']' && ichT < cchScript && !eol(ch = script.charAt(ichT)))
        ++ichT;
      cchToken = ichT - ichToken;
      return CONTINUE;
    } else if (isShowScriptOutput && !isShowCommand) {
      ichFirstSharp = ichT;
    }
    if (ch == '/' && ichT + 1 < cchScript)
      switch (script.charAt(++ichT)) {
      case '/':
        ichFirstSharp = ichToken;
        ichEnd = ichT - 1;
        break;
      case '*':
        ichEnd = ichT - 1;
        String terminator = (++ichT < cchScript && (ch = script.charAt(ichT)) == '*' 
            ? "**/" : "*/");
        ichT = script.indexOf(terminator, ichToken + 2);
        if (ichT < 0) {
          ichToken = cchScript;
          return EOL;
        }
        // ichT points at char after /*, whatever that is. So even /***/ will be caught
        incrementLineCount(script.substring(ichToken, ichT));
        cchToken = ichT + (ch == '*' ? 3 : 2) - ichToken;
        return CONTINUE;
      default:
        return OK;
      }

    boolean isSharp = (ichFirstSharp < 0);
    if (isSharp && !haveComments)
      return OK;

    // old way:
    // first, find the end of the statement and scan for # (sharp) signs

    if (ichComment > ichT)
      ichT = ichComment;
    for (; ichT < cchScript; ichT++) {
      if (eol(ch = script.charAt(ichT))) {
        ichEnd = ichT;
        if (isLineContinuation(ichT - 1, false)) {
          ichT += nCharNewLine(ichT);
          continue;
        }
        if (!isSharp && ch == ';')
          continue;
        break;
      }
      if (ichFirstSharp >= 0)
        continue;
      if (ch == '#')
        ichFirstSharp = ichT;
    }
    if (ichFirstSharp < 0) // there were no sharps found
      return OK;
    ichComment = ichFirstSharp;
    /****************************************************************
     * check for #jc comment if it occurs anywhere in the statement, then the
     * statement is not executed. This allows statements which are executed in
     * RasMol but are comments in Jmol
     ****************************************************************/

    if (isSharp && nTokens == 0 && cchScript - ichFirstSharp >= 3
        && script.charAt(ichFirstSharp + 1) == 'j'
        && script.charAt(ichFirstSharp + 2) == 'c') {
      // statement contains a #jc before then end ... strip it all
      cchToken = ichT - ichToken;
      return CONTINUE;
    }

    // if the sharp was not the first character then it isn't a comment
    if (ichFirstSharp != ichToken)
      return OK;

    /****************************************************************
     * check for leading #jx <space> or <tab> if you see it, then only strip
     * those 4 characters. if they put in #jx <newline> then they are not going
     * to execute anything, and the regular code will take care of it
     ****************************************************************/
    if (isSharp && cchScript > ichToken + 3 && script.charAt(ichToken + 1) == 'j'
        && script.charAt(ichToken + 2) == 'x'
        && isSpaceOrTab(script.charAt(ichToken + 3))) {
      cchToken = 4; // #jx[\s\t]
      return CONTINUE;
    }
    
    if (ichT == ichToken)
      return OK;

    // first character was a sharp, but was not #jx ... strip it all
    cchToken = ichT - ichToken;
    return (nTokens == 0 ? OK2 : CONTINUE);
  }

  private int processTokenList(short iLine, boolean doCompile) {
    if (nTokens > 0 || comment != null) {
      if (nTokens == 0) {
        // just a comment
        ichCurrentCommand = ichToken;
        if (comment != null)
          addTokenToPrefix(new Token(Token.nada, comment));
      } else if (setBraceCount > 0 && endOfLine && ichToken < cchScript) {
        return CONTINUE;
      }
      // end of command or comment
      iCommand = lltoken.size();
      if (thisFunction != null && thisFunction.cmdpt0 < 0) {
        thisFunction.cmdpt0 = iCommand;
      }
      if (nTokens == 1 && braceCount == 1) {
        // ...{...
        if (lastFlowCommand == null) {
          parenCount = setBraceCount = braceCount = 0;
          ltoken.remove(0);
          iBrace++;
          Token t = new ContextToken(Token.push, 0, "{");
          addTokenToPrefix(setCommand(t));
          pushCount++;
          vPush.add(t);
          vBraces.add(tokenCommand);
        } else {
          parenCount = setBraceCount = 0;
          setCommand(lastFlowCommand);
          if (lastFlowCommand.tok != Token.process 
              && (tokAt(0) == Token.leftbrace))
            ltoken.remove(0);
          lastFlowCommand = null;
        }
      }
      if (bracketCount > 0 || setBraceCount > 0 || parenCount > 0
          || braceCount == 1 && !checkFlowStartBrace(true)) {
        error(nTokens == 1 ? ERROR_commandExpected
            : ERROR_endOfCommandUnexpected);
        return ERROR;
      }
      if (needRightParen) {
        addTokenToPrefix(Token.tokenRightParen);
        needRightParen = false;
      }

      if (ltoken.size() > 0) {
        if (doCompile && !compileCommand())
          return ERROR;
        if (logMessages) {
          Logger.debug("-------------------------------------");
        }
        boolean doEval = true;
        switch (tokCommand) {
        case Token.trycmd:
        case Token.parallel: 
        case Token.function: // formerly "noeval"
        case Token.end:          
          // end switch may have - or + intValue, depending upon default or not
          // end function and the function call itself has intValue 0,
          // but the FUNCTION declaration itself will have MAX_VALUE intValue
          doEval = (atokenInfix.length > 0 && atokenInfix[0].intValue != Integer.MAX_VALUE);
          break;
        }
        if (doEval) {
          if (iCommand == lnLength) {
            short[] lnT = new short[lnLength * 2];
            System.arraycopy(lineNumbers, 0, lnT, 0, lnLength);
            lineNumbers = lnT;
            int[][] lnI = new int[lnLength * 2][2];
            System.arraycopy(lineIndices, 0, lnI, 0, lnLength);
            lineIndices = lnI;
            lnLength *= 2;
          }
          lineNumbers[iCommand] = iLine;
          lineIndices[iCommand][0] = ichCurrentCommand;
          lineIndices[iCommand][1] = Math.max(ichCurrentCommand, Math.min(
              cchScript, ichEnd == ichCurrentCommand ? ichToken : ichEnd));
          lltoken.add(atokenInfix);
          iCommand = lltoken.size();
        }
        if (tokCommand == Token.set)
          lastFlowCommand = null;
      }
      setCommand(null);
      comment = null;
      iHaveQuotedString = isNewSet = isSetBrace = needRightParen = false;
      ptNewSetModifier = 1;
      ltoken.clear();
      nTokens = nSemiSkip = 0;
      tokenAndEquals = null;
      ptSemi = -10;
      forPoint3 = -1;
      setEqualPt = Integer.MAX_VALUE;

    }
    if (endOfLine) {
      if (flowContext != null && flowContext.checkForceEndIf()) {
        forceFlowEnd(flowContext.token);
        isEndOfCommand = true;
        cchToken = 0;
        ichCurrentCommand = ichToken;
        lineCurrent--;
        return CONTINUE;
      }
      isShowCommand = false;
      ++lineCurrent;
    }
    if (ichToken >= cchScript) {
      // check for end of all brace work
      setCommand(Token.tokenAll);
      theTok = Token.nada;
      switch (checkFlowEndBrace()) {
      case ERROR:
        return ERROR;
      case CONTINUE:
        isEndOfCommand = true;
        cchToken = 0;
        return CONTINUE;
      }
      ichToken = cchScript;
      return OK; // main loop exit
    }
    return OK;
  }

  private boolean compileCommand() {
    if (ltoken.size() == 0) {
      // comment
      atokenInfix = new Token[0];
      return true;
    }
    setCommand(tokenAt(0));
    int size = ltoken.size();
    if (size == 1 && Token.tokAttr(tokCommand, Token.defaultON))
      addTokenToPrefix(Token.tokenOn);
    if (tokenAndEquals != null) {
      int j;
      int i = 0;
      for (i = 1; i < size; i++) {
        if ((j = tokAt(i)) == Token.andequals)
          break;
      }
      size = i;
      i++;
      if (ltoken.size() < i) {
        Logger.error("COMPILER ERROR! - andEquals ");
      } else {
        for (j = 1; j < size; j++, i++)
          ltoken.add(i, tokenAt(j));
        ltoken.set(size, Token.tokenEquals);
        ltoken.add(i, tokenAndEquals);
        ltoken.add(++i, Token.tokenLeftParen);
        addTokenToPrefix(Token.tokenRightParen);
      }
    }

    atokenInfix = new Token[size = ltoken.size()];
    for (int i = ltoken.size(); --i >= 0;)
      atokenInfix[i] = ltoken.get(i);

    if (logMessages) {
      Logger.debug("token list:");
      for (int i = 0; i < atokenInfix.length; i++)
        Logger.debug(i + ": " + atokenInfix[i]);
      Logger.debug("vBraces list:");
      for (int i = 0; i < vBraces.size(); i++)
        Logger.debug(i + ": " + vBraces.get(i));
      Logger.debug("-------------------------------------");
    }

    // compile expressions  (ScriptCompilerTokenParser.java)
    return compileExpressions();

  }

  private Token tokenAt(int i) {
    return ltoken.get(i);
  }

  @Override
  protected int tokAt(int i) {
    return (i < ltoken.size() ? tokenAt(i).tok : Token.nada);
  }
  
  private Token setCommand(Token token) {
    tokenCommand = token;
    if (token == null) {
      tokCommand = Token.nada;
    } else {
      tokCommand = tokenCommand.tok;
      isMathExpressionCommand = (tokCommand == Token.identifier || Token.tokAttr(tokCommand,
          Token.mathExpressionCommand));
      isSetOrDefine = (tokCommand == Token.set || tokCommand == Token.define);
      isCommaAsOrAllowed = Token.tokAttr(tokCommand,
          Token.atomExpressionCommand);
    }
    return token;
  }

  private void replaceCommand(Token token) {
    ltoken.remove(0);
    ltoken.add(0, setCommand(token));
  }

  private boolean isUserToken;
  
  private String getPrefixToken() {
    String ident = script.substring(ichToken, ichToken + cchToken);
    String identLC = ident.toLowerCase();
    boolean isUserVar = isContextVariable(identLC);
    if (nTokens == 0)
      isUserToken = isUserVar;
    if (nTokens == 1 && (tokCommand == Token.function  || tokCommand == Token.parallel || tokCommand == Token.var)
        || nTokens != 0 && isUserVar
        || isUserFunction(identLC)  && (thisFunction == null || !thisFunction.name.equals(identLC))) {
      // we need to allow:
      
      // var color = "xxx"
      // color @color
      // print color
      // etc. 
      // BUT we also should allow
      // color = ...
      // color += ...
      // color[ ...
      
      ident = identLC;
      theToken = null;
    } else if (ident.length() == 1) {
      // hack to support case sensitive alternate locations and chains
      // if an identifier is a single character long, then
      // allocate a new Token with the original character preserved
      if ((theToken = Token.getTokenFromName(ident)) == null
          && (theToken = Token.getTokenFromName(identLC)) != null)
        theToken = new Token(theToken.tok, theToken.intValue, ident);
    } else {
      ident = identLC;
      theToken = Token.getTokenFromName(ident);
    }
    if (theToken == null) {
      if (ident.indexOf("property_") == 0)
        theToken = new Token(Token.property, ident);
      else
        theToken = new Token(Token.identifier, ident);
    }    
    theTok = theToken.tok;
    return ident;
  }

  private int checkSpecialParameterSyntax() {
    char ch;
    if (nTokens == ptNewSetModifier) {
      ch = script.charAt(ichToken);
      boolean isAndEquals = ("+-\\*/&|=".indexOf(ch) >= 0);
      boolean isOperation = (isAndEquals || ch == '.' || ch == '[');
      char ch2 = (ichToken + 1 >= cchScript ? 0 : script.charAt(ichToken + 1));
      if (!isNewSet && isUserToken && isOperation && (ch == '=' || ch2 == ch || ch2 == '=')) {
        isNewSet = true;
        // Var data = ""
        // data = 5
        // data++
        // data += what
        
      }
      if (isNewSet || tokCommand == Token.set || Token.tokAttr(tokCommand, Token.setparam)) {
        if (ch == '=')
          setEqualPt = ichToken;

        // axes, background, define, display, echo, frank, hbond, history,
        // set, var
        // can all appear with or without "set" in front of them. These
        // are then
        // both commands and parameters for the SET command, but only if
        // they are
        // the FIRST parameter of the set command.
        if (Token.tokAttr(tokCommand, Token.setparam) && ch == '='
            || (isNewSet || isSetBrace) && isOperation) {
          setCommand(isAndEquals ? Token.tokenSet
              : ch == '[' && !isSetBrace ? Token.tokenSetArray
                  : Token.tokenSetProperty);
          ltoken.add(0, tokenCommand);
          cchToken = 1;
          switch (ch) {
          case '[':
            addTokenToPrefix(new Token(Token.leftsquare, "["));
            bracketCount++;
            return CONTINUE;
          case '.':
            addTokenToPrefix(new Token(Token.per, "."));
            return CONTINUE;
          case '-':
          case '+':
          case '*':
          case '/':
          case '\\':
          case '&':
          case '|':
            if (ch2 == 0)
              return ERROR(ERROR_endOfCommandUnexpected);
            if (ch2 != ch && ch2 != '=')
              return ERROR(ERROR_badContext, "\"" + ch + "\"");
            break;
          default:
            lastToken = Token.tokenMinus; // just to allow for {(....)}
            return CONTINUE;
          }
        }
      }
    }

    // cd, echo, gotocmd, help, hover, javascript, label, message, and pause
    // all are implicitly strings. You CAN use "..." but you don't have to,
    // and you cannot use '...'. This way the introduction of single quotes 
    // as an equivalent of double quotes cannot break existing scripts. -- BH 06/2009

    if (lookingAtString(!Token.tokAttr(tokCommand, Token.implicitStringCommand))) {
      if (cchToken < 0)
        return ERROR(ERROR_endOfCommandUnexpected);
      String str;
      if ((tokCommand == Token.load || tokCommand == Token.background || tokCommand == Token.script)
          && !iHaveQuotedString) {
        if (inlineLoad) {
          str = getUnescapedStringLiteral();
        } else {
          str = script.substring(ichToken + 1, ichToken + cchToken - 1);
          if (str.indexOf("\\u") >= 0)
            str = Escape.unescapeUnicode(str);
        }
      } else {
        str = getUnescapedStringLiteral();
      }
      iHaveQuotedString = true;
      if (tokCommand == Token.load && lastToken.tok == Token.data
          || tokCommand == Token.data && str.indexOf("@") < 0) {
        if (!getData(str))
          return ERROR(ERROR_missingEnd, "data");
      } else {
        addTokenToPrefix(new Token(Token.string, str));
      }
      return CONTINUE;
    }
    if (tokCommand == Token.sync && nTokens == 1 && charToken()) {
      String ident = script.substring(ichToken, ichToken + cchToken);
      addTokenToPrefix(new Token(Token.identifier, ident));
      return CONTINUE;
    } else if (tokCommand == Token.load) {
      if (script.charAt(ichToken) == '@') {
        iHaveQuotedString = true;
        return OK;
      }
      if (nTokens == 1) {
        inlineLoad = false;
        if (lookingAtLoadFormat()) {
          String strFormat = script.substring(ichToken, ichToken + cchToken);
          int tok = (strFormat.indexOf("=") == 0 || strFormat.indexOf("$") == 0 ? Token.string
              : (strFormat = strFormat.toLowerCase()).equals("data") ? Token.data
                  : Parser.isOneOf(strFormat, LOAD_TYPES) ? Token.identifier
                      : 0);
          if (tok != 0) {
            inlineLoad = (strFormat.equals("inline"));
            addTokenToPrefix(new Token(tok, strFormat));
            iHaveQuotedString = (tok == Token.string);
          }
          return CONTINUE;
        }
      }
      BitSet bs;
      if (script.charAt(ichToken) == '{' || parenCount > 0) {
      } else if ((bs = lookingAtBitset()) != null) {
        addTokenToPrefix(new Token(Token.bitset, bs));
        return CONTINUE;
      } else if (!iHaveQuotedString && lookingAtImpliedString(false)) {
        String str = script.substring(ichToken, ichToken + cchToken);
        addTokenToPrefix(new Token(Token.string, str));
        iHaveQuotedString = true;
        return CONTINUE;
      }
    } else if (tokCommand == Token.script || tokCommand == Token.getproperty) {
      if (!iHaveQuotedString && lookingAtImpliedString(false)) {
        String str = script.substring(ichToken, ichToken + cchToken);
        addTokenToPrefix(new Token(Token.string, str));
        iHaveQuotedString = true;
        return CONTINUE;
      }
    } else if (tokCommand == Token.write) {
      int pt = cchToken;
      // write image 300 300 filename
      // write script filename
      // write spt filename
      // write jpg filename
      // write filename
      if (nTokens == 2 && lastToken.tok == Token.frame)
        iHaveQuotedString = true;
      if (!iHaveQuotedString && lookingAtImpliedString(true)) {
        String str = script.substring(ichToken, ichToken + cchToken);
        if (str.startsWith("@{")) {
          iHaveQuotedString = true;
        } else if (str.indexOf(" ") < 0) {
          addTokenToPrefix(new Token(Token.string, str));
          iHaveQuotedString = true;
          return CONTINUE;
        }
        cchToken = pt;
      }
    }
    if (Token.tokAttr(tokCommand, Token.implicitStringCommand)
        && !(tokCommand == Token.script && iHaveQuotedString)
        && lookingAtImpliedString(true)) {
      String str = script.substring(ichToken, ichToken + cchToken);
      if (tokCommand == Token.label
          && Parser.isOneOf(str.toLowerCase(), "on;off;hide;display"))
        addTokenToPrefix(Token.getTokenFromName(str.toLowerCase()));
      else
        addTokenToPrefix(new Token(Token.string, str));
      return CONTINUE;
    }
    float value;
    if (!Float.isNaN(value = lookingAtExponential())) {
      addTokenToPrefix(new Token(Token.decimal, new Float(value)));
      return CONTINUE;
    }
    if (lookingAtObjectID(nTokens == 1)) {
      addTokenToPrefix(Token.getTokenFromName("$"));
      addTokenToPrefix(new Token(Token.identifier, script.substring(ichToken,
          ichToken + cchToken)));
      return CONTINUE;
    }
    if (lookingAtDecimal()) {
      value =
      // can't use parseFloat with jvm 1.1
      // Float.parseFloat(script.substring(ichToken, ichToken +
      // cchToken));
      Float.valueOf(script.substring(ichToken, ichToken + cchToken))
          .floatValue();
      int intValue = (JmolConstants.modelValue(script.substring(ichToken,
          ichToken + cchToken)));
      addTokenToPrefix(new Token(Token.decimal, intValue, new Float(value)));
      return CONTINUE;
    }
    if (lookingAtSeqcode()) {
      ch = script.charAt(ichToken);
      try {
        int seqNum = (ch == '*' || ch == '^' ? Integer.MAX_VALUE : Integer
            .parseInt(script.substring(ichToken, ichToken + cchToken - 2)));
        char insertionCode = script.charAt(ichToken + cchToken - 1);
        if (insertionCode == '^')
          insertionCode = ' ';
        if (seqNum < 0) {
          seqNum = -seqNum;
          addTokenToPrefix(Token.tokenMinus);
        }
        int seqcode = Group.getSeqcode(seqNum, insertionCode);
        addTokenToPrefix(new Token(Token.seqcode, seqcode, "seqcode"));
        return CONTINUE;
      } catch (NumberFormatException nfe) {
        return ERROR(ERROR_invalidExpressionToken, "" + ch);
      }
    }
    int val = lookingAtInteger();
    if (val != Integer.MAX_VALUE) {
      String intString = script.substring(ichToken, ichToken + cchToken);
      if (tokCommand == Token.breakcmd || tokCommand == Token.continuecmd) {
        if (nTokens != 1)
          return ERROR(ERROR_badArgumentCount);
        ScriptFlowContext f = (flowContext == null ? null : flowContext
            .getBreakableContext(val = Math.abs(val)));
        if (f == null)
          return ERROR(ERROR_badContext, (String) tokenCommand.value);
        tokenAt(0).intValue = f.pt0; // copy
      }
      if (val == 0 && intString.equals("-0"))
        addTokenToPrefix(Token.tokenMinus);
      addTokenToPrefix(new Token(Token.integer, val, intString));
      return CONTINUE;
    }
    if (!isMathExpressionCommand && parenCount == 0
        || lastToken.tok != Token.identifier
        && !tokenAttr(lastToken, Token.mathfunc)) {
      // here if:
      //   structure helix ({...})
      //   frame align ({...})
      //   polyhedra BONDS ({...})
      //   isosurface select ({...})
      //   isosurface within({...})
      // NOT 
      //   myfunc({...})
      //   mathFunc({...})
      // if you want to use a bitset there, you must use
      // bitsets properly: x.distance( ({1 2 3}) )
      boolean isBondOrMatrix = (script.charAt(ichToken) == '[');
      BitSet bs = lookingAtBitset();
      if (bs == null) {
        if (isBondOrMatrix) {
          Object m = lookingAtMatrix();
          if (m instanceof Matrix3f || m instanceof Matrix4f) {
            addTokenToPrefix(new Token((m instanceof Matrix3f ? Token.matrix3f
                : Token.matrix4f), m));
            return CONTINUE;
          }
        }
      } else {
        if (isBondOrMatrix)
          addTokenToPrefix(new Token(Token.bitset, new BondSet(bs)));
        // occasionally BondSet appears unknown in Eclipse even though it
        // is defined
        // in Eval.java -- doesn't seem to matter.
        else
          addTokenToPrefix(new Token(Token.bitset, bs));
        return CONTINUE;
      }
    }
    return OK;
  }

  private Object lookingAtMatrix() {
    int ipt;
    Object m;
    if (ichToken + 4 >= cchScript 
        || script.charAt(ichToken) != '[' || script.charAt(ichToken + 1) != '['
        || (ipt = script.indexOf("]]", ichToken)) < 0
        || (m = Escape.unescapeMatrix(script.substring(ichToken, ipt + 2))) == null)
      return null;
    cchToken = ipt + 2 - ichToken;
    return m;
  }

  private int parseKnownToken(String ident) {

    // specific token-based issues depend upon where we are in the command

    Token token;

    if (tokLastMath != 0)
      tokLastMath = theTok;
    if (flowContext != null && flowContext.token.tok == Token.switchcmd
        && flowContext.var != null && theTok != Token.casecmd
        && theTok != Token.defaultcmd && lastToken.tok != Token.switchcmd)
      return ERROR(ERROR_badContext, ident);
    switch (theTok) {
    case Token.identifier:
      if (nTokens == 0) {
        if (ident.charAt(0) == '\'') {
          addTokenToPrefix(setCommand(Token.tokenScript));
          cchToken = 0;
          return CONTINUE;
        }
        if (ichToken + cchToken < cchScript && script.charAt(ichToken + cchToken) == '.') {
          addTokenToPrefix(setCommand(Token.tokenScript));
          nTokens = 1;
          cchToken = 0;
          return CONTINUE;
        }
      }
      break;
    case Token.andequals:
      if (nSemiSkip == forPoint3 && nTokens == ptSemi + 2) {
        token = lastToken;
        addTokenToPrefix(Token.tokenEquals);
        addTokenToPrefix(token);
        token = Token.getTokenFromName(ident.substring(0, 1));
        addTokenToPrefix(token);
        addTokenToPrefix(Token.tokenLeftParen);
        needRightParen = true;
        return CONTINUE;
      }
      // check to see if we have a command name that
      // was not registered yet as a local variable:
      // var color = 3
      // color += 3
      checkNewSetCommand();
      if (tokCommand == Token.set) {
        tokenAndEquals = Token.getTokenFromName(ident.substring(0, 1));
        setEqualPt = ichToken;
        return OK;
      }
      if (tokCommand == Token.slab || tokCommand == Token.depth) {
        addTokenToPrefix(tokenCommand);
        replaceCommand(Token.tokenSet);
        tokenAndEquals = Token.getTokenFromName(ident.substring(0, 1));
        setEqualPt = ichToken;
        return OK;
      }
      // otherwise ignore
      return CONTINUE;
    case Token.end:
    case Token.endifcmd:
      if (flowContext != null)
        flowContext.forceEndIf = false;
      // fall through
    case Token.elsecmd:
      if (nTokens > 0) {
        isEndOfCommand = true;
        cchToken = 0;
        return CONTINUE;
      }
      break;
    case Token.forcmd:
      if (bracketCount > 0) // ignore [FOR], as in 1C4D 
        break;
      // fall through
    case Token.casecmd:
    case Token.defaultcmd:
    case Token.elseif:
    case Token.ifcmd:
    case Token.switchcmd:
    case Token.whilecmd:
    case Token.catchcmd:
      if (nTokens > 1 && tokCommand != Token.set) {
        isEndOfCommand = true;
        if (flowContext != null)
          flowContext.forceEndIf = true;
        cchToken = 0;
        return CONTINUE;
      }
      break;
    case Token.minusMinus:
    case Token.plusPlus:
      if (!isNewSet && nTokens == 1)
        checkNewSetCommand();
      if (isNewSet && parenCount == 0 && bracketCount == 0
          && ichToken <= setEqualPt) {
        nTokens = ltoken.size();
        addTokenToPrefix(Token.tokenEquals);
        setEqualPt = 0;
        for (int i = 1; i < nTokens; i++)
          addTokenToPrefix(ltoken.get(i));
        addTokenToPrefix(theTok == Token.minusMinus ? Token.tokenMinus
            : Token.tokenPlus);
        addTokenToPrefix(Token.intToken(1));
        return CONTINUE;
      } else if (nSemiSkip == forPoint3 && nTokens == ptSemi + 2) {
        token = lastToken;
        addTokenToPrefix(Token.tokenEquals);
        addTokenToPrefix(token);
        addTokenToPrefix(theTok == Token.minusMinus ? Token.tokenMinus
            : Token.tokenPlus);
        addTokenToPrefix(Token.intToken(1));
        return CONTINUE;
      }
      break;
    case Token.opEQ:
      if (parenCount == 0 && bracketCount == 0)
        setEqualPt = ichToken;
      break;
    case Token.per:
      if (tokCommand == Token.set && parenCount == 0 && bracketCount == 0
          && ichToken < setEqualPt) {
        ltoken.add(1, Token.tokenExpressionBegin);
        addTokenToPrefix(Token.tokenExpressionEnd);
        ltoken.set(0, Token.tokenSetProperty);
        setEqualPt = 0;
      }
      break;
    case Token.leftbrace:
      braceCount++;
      if (braceCount == 1 && parenCount == 0 && checkFlowStartBrace(false)) {
        isEndOfCommand = true;
        if (flowContext != null)
          flowContext.forceEndIf = false;
        return CONTINUE;
      }
      // fall through
    case Token.leftparen:
      parenCount++;
      // the select() function uses dual semicolon notation
      // but we must differentiate from isosurface select(...) and set
      // picking select
      if (nTokens > 1
          && (lastToken.tok == Token.select || lastToken.tok == Token.forcmd || lastToken.tok == Token.ifcmd))
        nSemiSkip += 2;
      break;
    case Token.rightbrace:
      if (iBrace > 0 && parenCount == 0 && braceCount == 0) {
        ichBrace = ichToken;
        if (nTokens == 0) {
          braceCount = parenCount = 1;
        } else {
          braceCount = parenCount = nSemiSkip = 0;
          if (theToken.tok != Token.casecmd && theToken.tok != Token.defaultcmd)
            vBraces.add(theToken);
          iBrace++;
          isEndOfCommand = true;
          ichEnd = ichToken;
          return CONTINUE;
        }
      }
      braceCount--;
      // fall through
    case Token.rightparen:
      parenCount--;
      if (parenCount < 0)
        return ERROR(ERROR_tokenUnexpected, ident);
      // we need to remove the semiskip if parentheses or braces have been
      // closed. 11.5.46
      if (parenCount == 0)
        nSemiSkip = 0;
      if (needRightParen) {
        addTokenToPrefix(Token.tokenRightParen);
        needRightParen = false;
      }
      break;
    case Token.leftsquare:
      if (ichToken > 0 && Character.isWhitespace(script.charAt(ichToken - 1)))
        addTokenToPrefix(Token.tokenSpaceBeforeSquare);
      bracketCount++;
      break;
    case Token.rightsquare:
      bracketCount--;
      if (bracketCount < 0)
        return ERROR(ERROR_tokenUnexpected, "]");
    }
    return OK;
  }

  private boolean checkNewSetCommand() {
    String name = ltoken.get(0).value.toString();
    if (!isContextVariable(name.toLowerCase()))
      return false;
    Token t = setNewSetCommand(false, name);
    setCommand(Token.tokenSet);
    ltoken.add(0, tokenCommand);
    ltoken.set(1, t);
    return true;
  }

  private int parseCommandParameter(String ident) {
    // PART II:
    //
    // checking tokens based on the current command
    // all command starts are handled by case Token.nada

    nTokens = ltoken.size();
    switch (tokCommand) {
    case Token.nada:
      // first token in command
      lastToken = Token.tokenOff;
      ichCurrentCommand = ichEnd = ichToken;
      setCommand(theToken);
      
      if (Token.tokAttr(tokCommand, Token.flowCommand)) {
        lastFlowCommand = tokenCommand;
      }
      // before processing this command, check to see if we have completed
      // a right-brace.
      int ret = checkFlowEndBrace();
      if (ret == ERROR)
        return ERROR;
      else if (ret == CONTINUE) {
        // yes, so re-read this one
        isEndOfCommand = true;
        cchToken = 0;
        return CONTINUE;
      }

      if (Token.tokAttr(tokCommand, Token.flowCommand)) {
        if (!checkFlowCommand((String) tokenCommand.value))
          return ERROR;
        theToken = tokenCommand;
        if (theTok == Token.casecmd) {
          addTokenToPrefix(tokenCommand);
          theToken = Token.tokenLeftParen;
        }
        break;
      }
      if (theTok == Token.colon) {
        braceCount++;
        isEndOfCommand = true;
        break;
      }
      if (theTok == Token.rightbrace) {
        // if }, just push onto vBrace, but NOT onto ltoken
        vBraces.add(tokenCommand);
        iBrace++;
        tokCommand = Token.nada;
        return CONTINUE;
      }
      if (theTok != Token.leftbrace)
        lastFlowCommand = null;

      if (Token.tokAttr(tokCommand, Token.scriptCommand))
        break;

      // not the standard command
      // isSetBrace: {xxx}.yyy = or {xxx}[xx].
      // isNewSet: xxx =
      // but not xxx = where xxx is a known "set xxx" variant
      // such as "set hetero" or "set hydrogen" or "set solvent"
      
      isSetBrace = (theTok == Token.leftbrace);
      if (isSetBrace) {
        if (!lookingAtBraceSyntax()) {
          isEndOfCommand = true;
          if (flowContext != null)
            flowContext.forceEndIf = false;
        }
      } else {
        switch (theTok) {
        case Token.plusPlus:
        case Token.minusMinus:
        case Token.identifier:
        case Token.var:
        case Token.define:
        case Token.leftparen:
          break;
        default:
          if (!Token.tokAttr(theTok, Token.misc)
              && !Token.tokAttr(theTok, Token.setparam)
              && !isContextVariable(ident)) {
            commandExpected();
            return ERROR;
          }
        }
      }
      theToken = setNewSetCommand(isSetBrace, ident);
      break;
    case Token.catchcmd:
      switch(nTokens) {
      case 1:
        if (theTok != Token.leftparen)
          return ERROR(ERROR_tokenExpected, "(");
        break; 
      case 2:
        ((ContextToken)tokenCommand).name0 = ident;
        addContextVariable(ident);
        break;
      case 3:
        if (theTok != Token.rightparen)
          return ERROR(ERROR_tokenExpected, ")");
        isEndOfCommand = true;
        ichEnd = ichToken + 1;
        flowContext.setLine();
        break;
      default:
        return ERROR(ERROR_badArgumentCount);
      }
      break;
    case Token.parallel:
    case Token.function:
      if (tokenCommand.intValue == 0) {
        if (nTokens != 1)
          break; // anything after name is ok
        // user has given macro command
        tokenCommand.value = ident;
        return CONTINUE; // don't store name in stack
      }
      if (nTokens == 1) {
        if (thisFunction != null)
          vFunctionStack.add(0, thisFunction);
        thisFunction = (tokCommand == Token.parallel ? new ParallelProcessor(ident, tokCommand) : new ScriptFunction(ident, tokCommand));
        htUserFunctions.put(ident, Boolean.TRUE);
        flowContext.setFunction(thisFunction);
        break; // function f
      }
      if (nTokens == 2) {
        if (theTok != Token.leftparen)
          return ERROR(ERROR_tokenExpected, "(");
        break; // function f (
      }
      if (nTokens == 3 && theTok == Token.rightparen)
        break; // function f ( )
      if (nTokens % 2 == 0) {
        // function f ( x , y )
        if (theTok != Token.comma && theTok != Token.rightparen)
          return ERROR(ERROR_tokenExpected, ")");
        break;
      }
      thisFunction.addVariable(ident, true);
      break;
    case Token.casecmd:
      if (nTokens > 1 && parenCount == 0 && braceCount == 0 && theTok == Token.colon) {
        addTokenToPrefix(Token.tokenRightParen);
        braceCount = 1;
        isEndOfCommand = true;
        cchToken = 0;
        return CONTINUE;
      }
      break;
    case Token.defaultcmd:
      if (nTokens > 1) {
        braceCount = 1;
        isEndOfCommand = true;
        cchToken = 0;
        return CONTINUE;
      }
      break;
    case Token.elsecmd:
      if (nTokens == 1 && theTok != Token.ifcmd) {
        isEndOfCommand = true;
        cchToken = 0;
        return CONTINUE;
      }
      if (nTokens != 1 || theTok != Token.ifcmd && theTok != Token.leftbrace)
        return ERROR(ERROR_badArgumentCount);
      replaceCommand(flowContext.token = new ContextToken(Token.elseif, "elseif"));
      tokCommand = Token.elseif;
      return CONTINUE;
    case Token.var:
      if (nTokens != 1)
        break;
      addContextVariable(ident);
      replaceCommand(Token.tokenSetVar);
      tokCommand = Token.set;
      break;
    case Token.end:
      if (nTokens != 1)
        return ERROR(ERROR_badArgumentCount);
      if (!checkFlowEnd(theTok, ident, ichCurrentCommand))
        return ERROR;
      if (theTok == Token.function || theTok == Token.parallel) {
        return CONTINUE;
      }
      break;
    case Token.switchcmd:
    case Token.whilecmd:
      if (nTokens > 2 && braceCount == 0 && parenCount == 0) {
        isEndOfCommand = true;
        ichEnd = ichToken + 1;
        flowContext.setLine();
      }
      break;
    case Token.elseif:
    case Token.ifcmd:
      if (nTokens > 2 && braceCount == 0 && parenCount == 0) {
        // so { or : end up new commands
        isEndOfCommand = true;
        ichEnd = ichToken + 1;
        flowContext.setLine();
      }
      break;
    case Token.process: 
      isEndOfCommand = true;
      ichEnd = ichToken + 1;
      flowContext.setLine();
      break;
    case Token.forcmd:
      if (nTokens == 1) {
        if (theTok != Token.leftparen)
          return ERROR(ERROR_unrecognizedToken, ident);
        forPoint3 = nSemiSkip = 0;
        nSemiSkip += 2;
      } else if (nTokens == 3 && tokAt(2) == Token.var) {
        addContextVariable(ident);
      } else if ((nTokens == 3 || nTokens == 4) && theTok == Token.in) {
        nSemiSkip -= 2;
        forPoint3 = 2;
        // for ( var x IN
        // for ( x IN
      } else if (braceCount == 0 && parenCount == 0) {
        isEndOfCommand = true;
        ichEnd = ichToken + 1;
        flowContext.setLine();
      }
      break;
    case Token.set:
      if (theTok == Token.leftbrace)
        setBraceCount++;
      else if (theTok == Token.rightbrace) {
        setBraceCount--;
        if (isSetBrace && setBraceCount == 0
            && ptNewSetModifier == Integer.MAX_VALUE)
          ptNewSetModifier = nTokens + 1;
      }
      if (nTokens == ptNewSetModifier) { // 1 when { is not present
        Token token = tokenAt(0);
        if (theTok == Token.leftparen || isUserFunction(token.value.toString())) {
          // mysub(xxx,xxx,xxx)
          ltoken.set(0, setCommand(new Token(Token.identifier, 0, token.value)));
          setBraceCount = 0;
          break;
        }
        if (theTok != Token.identifier && theTok != Token.andequals && theTok != Token.define
            && (!Token.tokAttr(theTok, Token.setparam))) {
          if (isNewSet)
            commandExpected();
          else
            error(ERROR_unrecognizedParameter, "SET", ": " + ident);
          return ERROR;
        }
        if (nTokens == 1
            && (lastToken.tok == Token.plusPlus || lastToken.tok == Token.minusMinus)) {
          replaceCommand(Token.tokenSet);
          addTokenToPrefix(lastToken);
          break;
        }
      }
      break;
    case Token.load:
      if (theTok == Token.define && (nTokens == 1 || lastToken.tok == Token.filter || lastToken.tok == Token.spacegroup)) {
        addTokenToPrefix(Token.tokenDefineString);
        return CONTINUE;          
      }
      if (theTok == Token.as)
        iHaveQuotedString = false;
      break;
    case Token.display:
    case Token.hide:
    case Token.restrict:
    case Token.select:
    case Token.delete:
    case Token.define:
      if (tokCommand == Token.define) {
        if (nTokens == 1) {
          // we are looking at the variable name
          if (theTok != Token.identifier) {
            if (preDefining) {
              if (!Token.tokAttr(theTok, Token.predefinedset)) {
                error(
                    "ERROR IN Token.java or JmolConstants.java -- the following term was used in JmolConstants.java but not listed as predefinedset in Token.java: "
                        + ident, null);
                return ERROR;
              }
            } else if (Token.tokAttr(theTok, Token.predefinedset)) {
              Logger
                  .warn("WARNING: predefined term '"
                      + ident
                      + "' has been redefined by the user until the next file load.");
            } else if (!isCheckOnly && ident.length() > 1) {
              Logger
                  .warn("WARNING: redefining "
                      + ident
                      + "; was "
                      + theToken
                      + "not all commands may continue to be functional for the life of the applet!");
              theTok = theToken.tok = Token.identifier;
              Token.addToken(ident, theToken);
            }
          }
          addTokenToPrefix(theToken);
          lastToken = Token.tokenComma;
          return CONTINUE;
        }
        if (nTokens == 2) {
          if (theTok == Token.opEQ) {
            // we are looking at @x =.... just insert a SET command
            // and ignore the =. It's the same as set @x ...
            ltoken.add(0, Token.tokenSet);
            return CONTINUE;
          }
        }
      }
      if (bracketCount == 0 && theTok != Token.identifier
          && !Token.tokAttr(theTok, Token.expression)
          && !Token.tokAttr(theTok, Token.misc)
          && (theTok & Token.minmaxmask) != theTok)
        return ERROR(ERROR_invalidExpressionToken, ident);
      break;
    case Token.center:
      if (theTok != Token.identifier && theTok != Token.dollarsign
          && !Token.tokAttr(theTok, Token.expression))
        return ERROR(ERROR_invalidExpressionToken, ident);
      break;
    case Token.plot3d:
    case Token.pmesh:
    case Token.isosurface:
      // isosurface ... name.xxx
      if (parenCount == 0 && bracketCount == 0
          && ".:/\\+-!?".indexOf(nextChar()) >= 0)
        checkUnquotedFileName();
    }
    return OK;
  }

  private Token setNewSetCommand(boolean isSetBrace, String ident) {
    tokCommand = Token.set;
    isNewSet = (!isSetBrace && !isUserFunction(ident));
    setBraceCount = (isSetBrace ? 1 : 0);
    bracketCount = 0;
    setEqualPt = Integer.MAX_VALUE;
    ptNewSetModifier = (isNewSet ? (ident.equals("(") ? 2 : 1) : Integer.MAX_VALUE);
    return (isSetBrace ? theToken : new Token(Token.identifier, ident));
  }

  private char nextChar() {
    int ich = ichToken + cchToken;
    return (ich >= cchScript ? ' ' : script.charAt(ich));
  }

  private void checkUnquotedFileName() {
    int ichT = ichToken;
    char ch;
    while (++ichT < cchScript 
        && !Character.isWhitespace(ch = script.charAt(ichT)) 
        && ch != '#' && ch != ';' && ch != '}') {
    }
    String name = script.substring(ichToken, ichT).replace('\\','/');
    cchToken = ichT - ichToken;
    theToken = new Token(Token.string, name);   
  }

  private boolean checkFlowStartBrace(boolean atEnd) {
    if ((!Token.tokAttr(tokCommand, Token.flowCommand)
        || tokCommand == Token.breakcmd || tokCommand == Token.continuecmd))
      return false;
    if (atEnd) {
      if (tokenCommand.tok != Token.casecmd && tokenCommand.tok != Token.defaultcmd) {
        iBrace++;
        vBraces.add(tokenCommand);
        lastFlowCommand = null;
      }
      parenCount = braceCount = 0;
    }
    return true;
  }

  List<Token> vPush = new ArrayList<Token>();
  int pushCount;
  
  private int checkFlowEndBrace() {
    
    if (iBrace <= 0
        || vBraces.get(iBrace - 1).tok != Token.rightbrace)
      return OK;
    // time to execute end
    vBraces.remove(--iBrace);
    Token token = vBraces.remove(--iBrace);
    if (theTok == Token.leftbrace) {
      braceCount--;
      parenCount--;
    }
    if (token.tok == Token.push) {
      vPush.remove(--pushCount);
      addTokenToPrefix(setCommand(new ContextToken(Token.pop, 0, "}")));
      isEndOfCommand = true;
      return CONTINUE;
    }
    switch (flowContext == null ? 0 : 
      flowContext.token.tok) {
    case Token.ifcmd:
    case Token.elseif:
    case Token.elsecmd:      
      if (tokCommand == Token.elsecmd || tokCommand == Token.elseif)
        return OK;
      break;
    case Token.switchcmd:
    case Token.casecmd:
    case Token.defaultcmd:
      if (tokCommand == Token.casecmd || tokCommand == Token.defaultcmd)
        return OK;
    }
    return forceFlowEnd(token);
  }

  private int forceFlowEnd(Token token) {    
    Token t0 = tokenCommand;    
    setCommand(new Token(Token.end, "end"));
    if (!checkFlowCommand("end"))
      return Token.nada;
    addTokenToPrefix(tokenCommand);
    switch (token.tok) {
    case Token.ifcmd:
    case Token.elsecmd:
    case Token.elseif:
      token = Token.tokenIf;
      break;
    case Token.defaultcmd:
    case Token.casecmd:
      token = Token.tokenSwitch;
      break;
    default:
      token = Token.getTokenFromName((String)token.value);
      break;
    }
    if (!checkFlowEnd(token.tok, (String)token.value, ichBrace))
      return ERROR;
    if (token.tok != Token.function && token.tok != Token.parallel
        && token.tok != Token.trycmd)
      addTokenToPrefix(token);
    setCommand(t0);
    return CONTINUE;
  }

  static boolean isBreakableContext(int tok) {
    return tok == Token.forcmd 
      || tok == Token.process
      || tok == Token.whilecmd 
      || tok == Token.casecmd 
      || tok == Token.defaultcmd;
  }

  private boolean checkFlowCommand(String ident) {
    int pt = lltoken.size();
    boolean isEnd = false;
    boolean isNew = true;
    switch (tokCommand) {
    case Token.function:
    case Token.parallel:
      if (flowContext != null)
        return error(ERROR_badContext, Token.nameOf(tokCommand));
      break;
    case Token.end:
      if (flowContext == null)
        return error(ERROR_badContext, ident);
      isEnd = true;
      if (flowContext.token.tok != Token.function && flowContext.token.tok != Token.parallel
          && flowContext.token.tok != Token.trycmd)
        setCommand(new Token(tokCommand, (flowContext.ptDefault > 0 ? flowContext.ptDefault : -flowContext.pt0), ident)); //copy
      break;
    case Token.trycmd:
    case Token.catchcmd:
      break;
    case Token.forcmd:
    case Token.ifcmd:
    case Token.process:
    case Token.switchcmd:
    case Token.whilecmd:
      break;
    case Token.endifcmd:
      isEnd = true;
      if (flowContext == null 
          || flowContext.token.tok != Token.ifcmd
          && flowContext.token.tok != Token.process
          && flowContext.token.tok != Token.elsecmd
          && flowContext.token.tok != Token.elseif)
        return error(ERROR_badContext, ident);
      break;
    case Token.elsecmd:
      if (flowContext == null || flowContext.token.tok != Token.ifcmd
          && flowContext.token.tok != Token.elseif)
        return error(ERROR_badContext, ident);
      flowContext.token.intValue = flowContext.setPt0(pt, false);
      break;
    case Token.breakcmd:
    case Token.continuecmd:
      isNew = false;
      ScriptFlowContext f = (flowContext == null ? null : flowContext.getBreakableContext(0));
      if (f == null)
        return error(ERROR_badContext, ident);
      setCommand( new Token(tokCommand, f.pt0, ident)); //copy
      break;
    case Token.defaultcmd:
      if (flowContext == null 
          || flowContext.token.tok != Token.switchcmd
          && flowContext.token.tok != Token.casecmd
          && flowContext.ptDefault > 0)
        return error(ERROR_badContext, ident);
      flowContext.token.intValue = flowContext.setPt0(pt, true);
      break;
    case Token.casecmd:
      if (flowContext == null 
          || flowContext.token.tok != Token.switchcmd
          && flowContext.token.tok != Token.casecmd
          && flowContext.token.tok != Token.defaultcmd)
        return error(ERROR_badContext, ident);
      flowContext.token.intValue = flowContext.setPt0(pt, false);
      break;
    case Token.elseif:
      if (flowContext == null || flowContext.token.tok != Token.ifcmd
          && flowContext.token.tok != Token.elseif
          && flowContext.token.tok != Token.elsecmd)
        return error(ERROR_badContext, "elseif");
      flowContext.token.intValue = flowContext.setPt0(pt, false);
      break;
    }
    if (isEnd) {
      flowContext.token.intValue = (tokCommand == Token.catchcmd ? -pt : pt);
      if (tokCommand == Token.endifcmd)
        flowContext = flowContext.getParent();
      if (tokCommand == Token.trycmd) {
        
      }
    } else if (isNew) {
      ContextToken ct = new ContextToken(tokCommand, tokenCommand.value);
      setCommand(ct); //copy
      switch (tokCommand) {
      case Token.trycmd:
        flowContext = new ScriptFlowContext(this, ct, pt, flowContext);
        if (thisFunction != null)
          vFunctionStack.add(0, thisFunction);
        thisFunction = new ParallelProcessor("", tokCommand);
        flowContext.setFunction(thisFunction);
        pushCount++;
        vPush.add(ct);
        break;
      case Token.elsecmd:
      case Token.elseif:
        flowContext.token = ct;
        break;
      case Token.casecmd:
      case Token.defaultcmd:
        ct.contextVariables = flowContext.token.contextVariables;
        flowContext.token = ct;
        break;
      case Token.process:
      case Token.forcmd:
      case Token.whilecmd:
      case Token.catchcmd:
        pushCount++;
        vPush.add(ct);
        // fall through
      case Token.ifcmd:
      case Token.switchcmd:
      default:
        flowContext = new ScriptFlowContext(this, ct, pt, flowContext);
        break;
      }
    }
    return true;
  }

  private boolean checkFlowEnd(int tok, String ident, int pt1) {
    if (flowContext == null || flowContext.token.tok != tok) {
      boolean isOK = true;
      switch(tok) {
      case Token.ifcmd:
        isOK = (flowContext.token.tok == Token.elsecmd
            || flowContext.token.tok == Token.elseif);
        break;
      case Token.switchcmd:
        isOK = (flowContext.token.tok == Token.casecmd
            || flowContext.token.tok == Token.defaultcmd);
        break;
      default:
        isOK = false;
      }
      if (!isOK)
        return error(ERROR_badContext, "end " + ident);
    }
    switch (tok) {
    case Token.ifcmd:
    case Token.switchcmd:
      break;
    case Token.catchcmd:
    case Token.forcmd:
    case Token.process:
    case Token.whilecmd:
      vPush.remove(--pushCount);
      break;
    case Token.parallel:
    case Token.function:
    case Token.trycmd:
      if (!isCheckOnly) {
        addTokenToPrefix(new Token(tok, thisFunction));
        ScriptFunction.setFunction(thisFunction, script, pt1, lltoken.size(),
            lineNumbers, lineIndices, lltoken);
      }
      thisFunction = (vFunctionStack.size() == 0 ? null : (ScriptFunction) vFunctionStack.remove(0));
      tokenCommand.intValue = 0;
      if (tok == Token.trycmd)
        vPush.remove(--pushCount);
      break;
    default:
      return error(ERROR_unrecognizedToken, "end " + ident);
    }
    flowContext = flowContext.getParent();
    return true;
  }

  private boolean getData(String key) {
    addTokenToPrefix(new Token(Token.string, key));
    ichToken += key.length() + 2;
    if (script.length() > ichToken && script.charAt(ichToken) == '\r') {
      lineCurrent++;ichToken++;
    }
    if (script.length() > ichToken && script.charAt(ichToken) == '\n') {
      lineCurrent++;ichToken++;
    }
    int i = script.indexOf(chFirst + key + chFirst, ichToken) - 4;
    if (i < 0 || !script.substring(i, i + 4).equalsIgnoreCase("END "))
      return false;
    String str = script.substring(ichToken, i);
    incrementLineCount(str);
    addTokenToPrefix(new Token(Token.data, str));
    addTokenToPrefix(new Token(Token.identifier, "end"));
    addTokenToPrefix(new Token(Token.string, key));
    cchToken = i - ichToken + key.length() + 6;
    return true;
  }

  private int incrementLineCount(String str) {
    char ch;
    int pt = str.indexOf('\r');
    int pt2 = str.indexOf('\n');
    if (pt < 0 && pt2 < 0)
      return 0;
    int n = lineCurrent;
    if (pt < 0 || pt2 < pt)
      pt = pt2;
    for (int i = str.length(); --i >= pt;) {
      if ((ch = str.charAt(i)) == '\n' || ch == '\r')
        lineCurrent++;
    }
    return lineCurrent - n;
  }
  
  private static boolean isSpaceOrTab(char ch) {
    return ch == ' ' || ch == '\t';
  }

  private boolean eol(char ch) {
    return eol(ch, nSemiSkip);  
  }
  
  static boolean eol(char ch, int nSkip) {
    return (ch == '\r' || ch == '\n' || ch == ';' && nSkip <= 0);  
  }
  
  private boolean lookingAtBraceSyntax() {
    // isSetBrace: {xxx}.yyy =  or {xxx}[xx].
    int ichT = ichToken;
    int nParen = 1;
    while (++ichT < cchScript && nParen > 0) {
      switch (script.charAt(ichT)) {
      case '{':
        nParen++;
        break;
      case '}':
        nParen--;
      break;
      }
    }
    if (ichT < cchScript && script.charAt(ichT) == '[' && ++nParen == 1)
      while (++ichT < cchScript && nParen > 0) {
        switch (script.charAt(ichT)) {
        case '[':
          nParen++;
          break;
        case ']':
          nParen--;
        break;
        }
      }
    if (ichT < cchScript && script.charAt(ichT) == '.' && nParen == 0) {
      return true;
    }
    
    return false;
  }

  char chFirst;
  private boolean lookingAtString(boolean allowPrime) {
    if (ichToken == cchScript)
      return false;
    chFirst = script.charAt(ichToken);
    if (chFirst != '"' && (!allowPrime || chFirst != '\''))
      return false;
    int ichT = ichToken;
    char ch;
    boolean previousCharBackslash = false;
    while (++ichT < cchScript) {
      ch = script.charAt(ichT);
      if (ch == chFirst && !previousCharBackslash)
        break;
      previousCharBackslash = (ch == '\\' ? !previousCharBackslash : false);
    }
    if (ichT == cchScript) {
      cchToken = -1;
      ichEnd = cchScript;
    } else {
      cchToken = ++ichT - ichToken;
    }
    return true;
  }

  String getUnescapedStringLiteral() {
    if (cchToken < 2)
      return "";
    StringBuffer sb = new StringBuffer(cchToken - 2);
    int ichMax = ichToken + cchToken - 1;
    int ich = ichToken + 1;
    while (ich < ichMax) {
      char ch = script.charAt(ich++);
      if (ch == '\\' && ich < ichMax) {
        ch = script.charAt(ich++);
        switch (ch) {
        case 'b':
          ch = '\b';
          break;
        case 'n':
          ch = '\n';
          break;
        case 't':
          ch = '\t';
          break;
        case 'r':
          ch = '\r';
        // fall into
        case '"':
        case '\\':
        case '\'':
          break;
        case 'x':
        case 'u':
          int digitCount = ch == 'x' ? 2 : 4;
          if (ich < ichMax) {
            int unicode = 0;
            for (int k = digitCount; --k >= 0 && ich < ichMax;) {
              char chT = script.charAt(ich);
              int hexit = Escape.getHexitValue(chT);
              if (hexit < 0)
                break;
              unicode <<= 4;
              unicode += hexit;
              ++ich;
            }
            ch = (char) unicode;
          }
        }
      }
      sb.append(ch);
    }
    return sb.toString();
  }

  // static String[] loadFormats = { "append", "files", "trajectory", "menu",
  // "models",
  // /*ancient:*/ "alchemy", "mol2", "mopac", "nmrpdb", "charmm", "xyz", "mdl",
  // "pdb" };

  private boolean lookingAtLoadFormat() {
    // just allow a simple word or =xxxx or $CCCC
    int ichT = ichToken;
    char ch = '\0';
    boolean allchar = (ichT < cchScript && ((ch = script.charAt(ichT)) == '$' || ch == '='));
    while (ichT < cchScript
        && (Character.isLetterOrDigit(ch = script.charAt(ichT)) || allchar
            && (!eol(ch) && !Character.isWhitespace(ch))))
      ++ichT;
    if (!allchar && ichT == ichToken || !isSpaceOrTab(ch))
      return false;
    cchToken = ichT - ichToken;
    return true;
  }

  /**
   * An "implied string" is a parameter that is not quoted but because of its
   * position in a command is implied to be a string. First we must exclude @xxxx.
   * Then we consume the entire math syntax @{......} or any set of
   * characters not involving white space.
   * echo, hover, label, message, pause are odd-valued; no initial parsing of variables for them. 
   * @param allowSpace 
   * 
   * @return true or false
   */
  private boolean lookingAtImpliedString(boolean allowSpace) {
    int ichT = ichToken;
    char ch = script.charAt(ichT);
    boolean parseVariables = !(Token.tokAttr(tokCommand, Token.implicitStringCommand) 
        || (tokCommand & 1) == 0); 
    boolean isVariable = (ch == '@');
    boolean isMath = (isVariable && ichT + 3 < cchScript && script.charAt(ichT + 1) == '{');
    if (isMath && parseVariables) {
      ichT = ichMathTerminator(script, ichToken + 1, cchScript);
      return (ichT != cchScript && (cchToken = ichT + 1 - ichToken) > 0);
    }
    int ptSpace = -1;
    int ptLastChar = -1;
    // look ahead to \n, \r, terminal ;, or }
    boolean isOK = true;
    int parenpt = 0;
    while (isOK && ichT < cchScript && !eol(ch = script.charAt(ichT))) {
      switch (ch) {
      case '{':
        parenpt++;
        break;
      case '}':
        // only consider this if it is extra
        parenpt--;
        if (parenpt < 0 && braceCount > 0) {
          isOK = false;
          continue;
        }
        break;
      default:
        if (Character.isWhitespace(ch)) {
          if (ptSpace < 0)
            ptSpace = ichT;
        } else {
          ptLastChar = ichT;
        }
        break;
      }
      if (Character.isWhitespace(ch)) {
        if (ptSpace < 0)
          ptSpace = ichT;
      } else {
        ptLastChar = ichT;
      }
      ++ichT;
    }
    // message/echo/label @x
    // message/echo/label @{.....}
    // message/echo/label @{....} testing  NOT ok
    // message/echo/label @x bananas OK -- "@x bananas"
    // {... message/echo label ok }  
    if (allowSpace)
      ichT = ptLastChar + 1;
    else if (ptSpace > 0)
      ichT = ptSpace;
    if (isVariable && ptSpace < 0 && parenpt <= 0 && ichT - ichToken > 1) {
      // if we have @xxx then this is not an implied string
      return false;
    }
    return (cchToken = ichT - ichToken) > 0;
  }

  /**
   * For @{....}
   * 
   * @param script
   * @param ichT
   * @param len
   * @return     position of "}"
   */
  public static int ichMathTerminator(String script, int ichT, int len) {
    int nP = 1;
    char chFirst = '\0';
    char chLast = '\0';
    while (nP > 0 && ++ichT < len) {
      char ch = script.charAt(ichT);
      if (chFirst != '\0') {
        if (chLast == '\\') {
          ch = '\0';
        } else if (ch == chFirst) {
          chFirst = '\0';
        }
        chLast = ch;
        continue;
      }
      switch(ch) {
      case '\'':
      case '"':
        chFirst = ch;
        break;
      case '{':
        nP++;
        break;
      case '}':
        nP--;
        break;
      }
    }
    return ichT;
  }

  private float lookingAtExponential() {
    if (ichToken == cchScript)
      return Float.NaN; //end
    int ichT = ichToken;
    int pt0 = ichT;
    if (script.charAt(ichT) == '-')
      ++ichT;
    boolean isOK = false;
    char ch = 'X';
    while (ichT < cchScript && Character.isDigit(ch = script.charAt(ichT))) {
      ++ichT;
      isOK = true;
    }
    if (ichT < cchScript && ch == '.')
      ++ichT;
    while (ichT < cchScript && Character.isDigit(ch = script.charAt(ichT))) {
      ++ichT;
      isOK = true;
    }
    if (ichT == cchScript || !isOK)
      return Float.NaN; //integer
    isOK = (ch != 'E' && ch != 'e');
    if (isOK || ++ichT == cchScript)
      return Float.NaN;
    ch = script.charAt(ichT);
    // I THOUGHT we only should allow "E+" or "E-" here, not "2E1" because
    // "2E1" might be a PDB het group by that name. BUT it turns out that
    // any HET group starting with a number is unacceptable and must
    // be given as [nXm], in brackets.

    if (ch == '-' || ch == '+')
      ichT++;
    while (ichT < cchScript && Character.isDigit(ch = script.charAt(ichT))) {
      ichT++;
      isOK = true;
    }
    if (!isOK)
      return Float.NaN;
    cchToken = ichT - ichToken;
    return (float) Double.valueOf(script.substring(pt0, ichT)).doubleValue();
  }

  private boolean lookingAtDecimal() {
    if (ichToken == cchScript)
      return false;
    int ichT = ichToken;
    if (script.charAt(ichT) == '-')
      ++ichT;
    boolean digitSeen = false;
    char ch = 'X';
    while (ichT < cchScript && Character.isDigit(ch = script.charAt(ichT++)))
      digitSeen = true;
    if (ch != '.')
      return false;
    // only here if  "dddd."

    // to support 1.ca, let's check the character after the dot
    // to determine if it is an alpha
    char ch1;
    if (ichT < cchScript && !eol(ch1 = script.charAt(ichT))) {
      if (Character.isLetter(ch1) || ch1 == '?' || ch1 == '*')
        return false;
      //well, guess what? we also have to look for 86.1Na, so...
      //watch out for moveto..... 56.;refresh...
      if (ichT + 1 < cchScript
          && (Character.isLetter(ch1 = script.charAt(ichT + 1)) || ch1 == '?'))
        return false;
    }
    while (ichT < cchScript && Character.isDigit(script.charAt(ichT))) {
      ++ichT;
      digitSeen = true;
    }
    cchToken = ichT - ichToken;
    return digitSeen;
  }

  private boolean lookingAtSeqcode() {
    int ichT = ichToken;
    char ch = ' ';
    if (ichT + 1 < cchScript && script.charAt(ichT) == '*'
        && script.charAt(ichT + 1) == '^') {
      ch = '^';
      ++ichT;
    } else {
      if (script.charAt(ichT) == '-')
        ++ichT;
      while (ichT < cchScript && Character.isDigit(ch = script.charAt(ichT)))
        ++ichT;
    }
    if (ch != '^')
      return false;
    ichT++;
    if (ichT == cchScript)
      ch = ' ';
    else
      ch = script.charAt(ichT++);
    if (ch != ' ' && ch != '*' && ch != '?' && !Character.isLetter(ch))
      return false;
    cchToken = ichT - ichToken;
    return true;
  }

  private int lookingAtInteger() {
    if (ichToken == cchScript)
      return Integer.MAX_VALUE;
    int ichT = ichToken;
    if (script.charAt(ichToken) == '-')
      ++ichT;
    int ichBeginDigits = ichT;
    while (ichT < cchScript && Character.isDigit(script.charAt(ichT)))
      ++ichT;
    if (ichBeginDigits == ichT)
      return Integer.MAX_VALUE;
    cchToken = ichT - ichToken;
    try {
      int val = Integer.parseInt(script.substring(ichToken, ichT));
      return val;
    } catch (NumberFormatException e) {
      // ignore
    }
    return Integer.MAX_VALUE;
  }

  BitSet lookingAtBitset() {
    // ({n n:m n}) or ({null})
    // [{n:m}] is a BOND bitset
    // EXCEPT if the previous token was a function:
    // {carbon}.distance({3 3 3})
    // Yes, I wish I had used {{...}}, but this will work. 
    // WITHIN ({....}) unfortunately has two contexts
    
    if (script.indexOf("({null})", ichToken) == ichToken) {
      cchToken = 8;
      return new BitSet();
    }
    int ichT;
    if (ichToken + 4 > cchScript 
        || script.charAt(ichToken + 1) != '{'
        || (ichT = script.indexOf("}", ichToken)) < 0
        || ichT + 1 == cchScript)
    return null;
    BitSet bs = Escape.unescapeBitset(script.substring(ichToken, ichT + 2));
    if (bs != null)
      cchToken = ichT + 2 - ichToken;
    return bs;
  }
  
  private boolean lookingAtObjectID(boolean allowWildID) {
    int ichT = ichToken;
    if (ichT == cchScript || script.charAt(ichT) != '$')
      return false;
    if (++ichT != cchScript && script.charAt(ichT) == '"')
      return false;
    while (ichT < cchScript) {
      char ch;
      if (Character.isWhitespace(ch = script.charAt(ichT))) {
        if (ichT == ichToken + 1)
          return false;
        break;
      }
      if (!Character.isLetterOrDigit(ch)) {
        switch (ch) {
        default:
          return false;
        case '*':
          if (!allowWildID)
            return false;
        case '~':
        case '_':
          break;
        }
      }
      ichT++;
    }
    cchToken = ichT - (++ichToken);
    return true;
  }

  private boolean lookingAtLookupToken(int ichT) {
    if (ichT == cchScript)
      return false;
    int ichT0 = ichT;
    tokLastMath = 0;
    char ch;
    switch (ch = script.charAt(ichT++)) {
    case '-':
    case '+':
    case '&':
    case '|':
    case '*':
      if (ichT < cchScript) {
        if (script.charAt(ichT) == ch) {
          ++ichT;
          if (ch == '-' || ch == '+')
            break;
          if (ch == '&' && ichT < cchScript && script.charAt(ichT) == ch)
            ++ichT; // &&&
        } else if (script.charAt(ichT) == '=') {
          ++ichT;
        }
      }
      tokLastMath = 1;
      break;
    case '/':
      if (ichT < cchScript && script.charAt(ichT) == '/')
        break;
    case '\\':  // leftdivide
    case '!':
      if (ichT < cchScript && script.charAt(ichT) == '=')
        ++ichT;
      tokLastMath = 1;
      break;
    case ')':
    case ']':
    case '}':
    case '.':
      break;
    case '@':
    case '{':
      tokLastMath = 2; // NOT considered a continuation if at beginning of a line
      break;
    case ':':
      tokLastMath = 1;
      break;
    case '(':
    case ',':
    case '$':
    case ';':
    case '[':
    case '%':
      tokLastMath = 1;
      break;
    case '<':
    case '=':
    case '>':
      if (ichT < cchScript
          && ((ch = script.charAt(ichT)) == '<' || ch == '=' || ch == '>'))
        ++ichT;
      tokLastMath = 1;
      break;
    default:
      if (!Character.isLetter(ch))
        return false;
    //fall through
    case '~':
    case '_':
    case '\'':
    case '?': // include question marks in identifier for atom expressions
      if (ch == '?')
        tokLastMath = 1;
      while (ichT < cchScript
          && (Character.isLetterOrDigit(ch = script.charAt(ichT)) 
              || ch == '_' || ch == '?' || ch == '~' || ch == '\'')
          ||
          // hack for insertion codes embedded in an atom expression :-(
          // select c3^a
          (ch == '^' && ichT > ichT0 && Character.isDigit(script
              .charAt(ichT - 1)))
          || ch == '\\' && ichT + 1 < cchScript && script.charAt(ichT + 1) == '?')
        ++ichT;
      break;
    }
    cchToken = ichT - ichT0;
    return true;
  }

  private boolean charToken() {
    char ch;
    if (ichToken == cchScript || (ch = script.charAt(ichToken)) == '"' || ch == '@')
      return false;
    int ichT = ichToken;
    while (ichT < cchScript && !isSpaceOrTab(ch = script.charAt(ichT)) 
        && ch != '#' && ch != '}' && !eol(ch))
        ++ichT;
    cchToken = ichT - ichToken;
    return true;
  }
 
  
  private int ERROR(int error) {
    error(error, null, null);
    return ERROR;
  }
  
  private int ERROR(int error, String value) {
    error(error, value);
    return ERROR;
  }
  
  private boolean handleError() {
    errorType = errorMessage;
    errorLine = script.substring(ichCurrentCommand, ichEnd <= ichCurrentCommand ? ichToken : ichEnd);
    String lineInfo = (ichToken < ichEnd 
        ? errorLine.substring(0, ichToken - ichCurrentCommand)
              + " >>>> " + errorLine.substring(ichToken - ichCurrentCommand) 
        : errorLine)
        + " <<<<";
    errorMessage = GT._("script compiler ERROR: ") + errorMessage
         + ScriptEvaluator.setErrorLineMessage(null, filename, lineCurrent, iCommand, lineInfo);
    if (!isSilent) {
      ichToken = Math.max(ichEnd, ichToken);
      while (!lookingAtEndOfLine() && !lookingAtEndOfStatement())
        ichToken++;
      errorLine = script.substring(ichCurrentCommand, ichToken);      
      viewer.addCommand(errorLine + CommandHistory.ERROR_FLAG);
      Logger.error(errorMessage);
    }
    return false;
  }

  /**
   * used by app to separate a command line into three sections:
   * 
   * prefix....;cmd ........ token
   * 
   * where token can be a just-finished single or double quote or
   * a string of characters
   * 
   * @param cmd
   * @return String[] {prefix, cmd..... token}
   */
  public static String[] splitCommandLine(String cmd) {
    String[] sout = new String[3];
    boolean isEscaped1 = false;
    boolean isEscaped2 = false;
    boolean isEscaped = false;
    if (cmd.length() == 0)
      return null;
    int ptQ = -1;
    int ptCmd = 0;
    int ptToken = 0;
    for (int i = 0; i < cmd.length(); i++) {
      switch(cmd.charAt(i)) {
      case '"':
        if (!isEscaped && !isEscaped1) {
          isEscaped2 = !isEscaped2;
          if (isEscaped2)
            ptQ = ptToken = i;
        }
        break;
      case '\'':
        if (!isEscaped && !isEscaped2) {
          isEscaped1 = !isEscaped1;
          if (isEscaped1)
            ptQ = ptToken = i;
        }
        break;
      case '\\':
        isEscaped = !isEscaped;
        continue;
      case ' ':
        if (!isEscaped && !isEscaped1 && !isEscaped2) {
          ptToken = i + 1;
          ptQ = -1;
        }
        break;
      case ';':
        if (!isEscaped1 && !isEscaped2) {
          ptCmd = ptToken = i + 1;
          ptQ = -1;
        }
        break;
      default:
        if (!isEscaped1 && !isEscaped2)
          ptQ = -1;
      }
      isEscaped = false;        
     }
    sout[0] = cmd.substring(0, ptCmd);
    sout[1] = (ptToken == ptCmd ? cmd.substring(ptCmd) : cmd.substring(ptCmd, (ptToken > ptQ ? ptToken : ptQ)));
    sout[2] = (ptToken == ptCmd ? null : cmd.substring(ptToken));
    return sout;
  }


}
