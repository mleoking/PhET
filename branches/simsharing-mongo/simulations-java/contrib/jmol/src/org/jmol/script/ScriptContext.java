/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Jmol Development Team
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
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *  02110-1301, USA.
 */

package org.jmol.script;

import java.util.Map;

public class ScriptContext {
  /**
   * 
   */
  public String fullpath = "";
  public String filename;
  public ParallelProcessor parallelProcessor;
  public String functionName;
  public String script;
  public short[] lineNumbers;
  public int[][] lineIndices;
  public Token[][] aatoken;
  public Token[] statement;
  public int statementLength;
  public int pc;
  public int pcEnd = Integer.MAX_VALUE;
  public int lineEnd = Integer.MAX_VALUE;
  public int iToken;
  public StringBuffer outputBuffer;
  public Map<String, ScriptVariable> contextVariables;
  public boolean isFunction;
  public boolean isStateScript;
  public String errorMessage;
  public String errorMessageUntranslated;
  public int iCommandError = -1;
  public String errorType;
  public int scriptLevel;
  public boolean isSyntaxCheck;
  public boolean executionStepping;
  public boolean executionPaused;
  public String scriptExtensions;
  public String contextPath = " >> ";
  public ScriptContext parentContext;
  public ContextToken token;

  ScriptContext() {
  }

  StringBuffer getContextTrace(StringBuffer sb, boolean isTop) {
    if (sb == null)
      sb = new StringBuffer();
    sb.append(ScriptEvaluator.setErrorLineMessage(functionName, filename,
        lineNumbers[pc], pc, ScriptEvaluator.statementAsString(statement, (isTop ? iToken : 9999), false)));
    if (parentContext != null)
      parentContext.getContextTrace(sb, false);
    return sb;
  }
}