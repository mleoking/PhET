/* $Author: hansonr $
 * $Date: 2007-09-09 21:37:07 -0500 (Sun, 09 Sep 2007) $
 * $Revision: 8231 $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ScriptFunction {

  // / functions

  /*
   * functions are either local or global (static). The idea there is that a set
   * of applets might share a set of functions. The default is global; prefix
   * underscore makes them local.
   * 
   * functions have contexts. Or, more specifically, contexts may have associated
   * functions.
   * 
   * Bob Hanson -- 11.3.29
   * 
   * includes parallel, catch
   * 
   */

  int pt0;
  int chpt0;
  int cmdpt0 = -1;
  protected String typeName;
  public String name;
  int nParameters;
  List<String> names = new ArrayList<String>();
  int tok;

  Map<String, String> variables = new Hashtable<String, String>();
  public boolean isVariable(String ident) {
    return variables.containsKey(ident);
  }

  ScriptVariable returnValue;
  public Token[][] aatoken;
  int[][] lineIndices;
  short[] lineNumbers;
  String script;

  ScriptFunction(String name, int tok) {
    this.name = name;
    this.tok = tok;
    typeName = Token.nameOf(tok);
  }

  void setVariables(Map<String, ScriptVariable> contextVariables, List<ScriptVariable> params) {
    int nParams = (params == null ? 0 : params.size());
    for (int i = names.size(); --i >= 0;) {
      String name = names.get(i).toLowerCase();
      ScriptVariable var = (i < nParameters && i < nParams ? params.get(i) : null);
      if (var != null && var.tok != Token.varray)  // TODO: list type?
        var = new ScriptVariable(var);
      contextVariables.put(name, (var == null ? 
          (new ScriptVariable(Token.string, "")).setName(name) : var));
    }
    contextVariables.put("_retval", ScriptVariable.intVariable(tok == Token.trycmd ? Integer.MAX_VALUE : 0));
  }

  public void unsetVariables(Map<String, ScriptVariable> contextVariables, List<ScriptVariable> params) {
    // note: this method is never called.
    // set list values in case they have changed.
    int nParams = (params == null ? 0 : params.size());
    int nNames = names.size();
    if (nParams == 0 || nNames == 0)
      return;
    for (int i = 0; i < nNames && i < nParams; i++) {
      ScriptVariable global = params.get(i);
      if (global.tok != Token.varray)  // TODO: list type?
        continue;
      ScriptVariable local = contextVariables.get(names.get(i).toLowerCase());
      if (local.tok != Token.varray)  // TODO: list type?
        continue;
      global.value = local.value;
    }
  }

  void addVariable(String name, boolean isParameter) {
    variables.put(name, name);
    names.add(name);
    if (isParameter)
      nParameters++;
  }

  static void setFunction(ScriptFunction function, String script,
                          int ichCurrentCommand, int pt, short[] lineNumbers,
                          int[][] lineIndices, List<Token[]> lltoken) {
    int cmdpt0 = function.cmdpt0;
    int chpt0 = function.chpt0;
    int nCommands = pt - cmdpt0;
    function.setScript(script.substring(chpt0, ichCurrentCommand));
    Token[][] aatoken = function.aatoken = new Token[nCommands][];
    function.lineIndices = new int[nCommands][];
    function.lineNumbers = new short[nCommands];
    short line0 = (short) (lineNumbers[cmdpt0] - 1);
    for (int i = 0; i < nCommands; i++) {
      function.lineNumbers[i] = (short) (lineNumbers[cmdpt0 + i] - line0);
      function.lineIndices[i] = new int[] {lineIndices[cmdpt0 + i][0] - chpt0, lineIndices[cmdpt0 + i][1] - chpt0 };
      //System.out.println("Line " + i + ": " + function.script.substring(function.lineIndices[i][0], function.lineIndices[i][1]));
      aatoken[i] = lltoken.get(cmdpt0 + i);
      // adjust intValues, which are pointers into the command stack,
      // by the 0-point offset of the command pointer
      // negative less negative;positive less positive
      if (aatoken[i].length > 0) {
        Token tokenCommand = aatoken[i][0];
        if (Token.tokAttr(tokenCommand.tok, Token.flowCommand))
          tokenCommand.intValue -= (tokenCommand.intValue < 0 ? -cmdpt0
              : cmdpt0);
      }
    }
    for (int i = pt; --i >= cmdpt0;) {
      lltoken.remove(i);
      lineIndices[i][0] = lineIndices[i][1] = 0;
    }
  }

  private void setScript(String s) {
    script = s;
    if (script != null && script != "" && !script.endsWith("\n"))
      script += "\n";
  }

  public String getSignature() {
    StringBuffer s = new StringBuffer(typeName);
    s.append(" ").append(name).append(" (");
    for (int i = 0; i < nParameters; i++) {
      if (i > 0)
        s.append(", ");
      s.append(names.get(i));
    }
    s.append(")");
    return s.toString();
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer("/*\n * ");
    s.append(name).append("\n */\n").append(getSignature()).append("{\n");
    if (script != null)
      s.append(script);
    s.append("}\n");
    return s.toString();
  }
}
