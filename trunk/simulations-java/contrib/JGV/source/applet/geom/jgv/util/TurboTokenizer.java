//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.util;

import java.io.InputStream;

public
class TurboTokenizer {
    private InputStream input;
    private char buf[];
    private int peekc = ' ';
    private boolean pushedBack;
    private boolean forceLower;
    /** The line number of the last token read */
    private int LINENO = 1;

    private boolean eolIsSignificantP = false;
    private boolean slashSlashCommentsP = false;
    private boolean slashStarCommentsP = false;


    private byte ctype[] = new byte[256];
    private static final byte CT_WHITESPACE = 1;
    private static final byte CT_DIGIT = 2;
    private static final byte CT_ALPHA = 4;
    private static final byte CT_QUOTE = 8;
    private static final byte CT_COMMENT = 16;

    static final int DIGIT   = 1;
    static final int DECIMAL = 2;
    static final int SIGN    = 3;
    static final int EXP     = 4;
    static final int OTHER   = 5;

    /**
     * The type of the last token returned.  It's value will either
     * be one of the following TT_* constants, or a single
     * character.  For example, if '+' is encountered and is
     * not a valid word character, ttype will be '+'
     */
    public int ttype;

    /**
     * The End-of-file token.
     */
    public static final int TT_EOF = -1;

    /**
     * The End-of-line token.
     */
    public static final int TT_EOL = '\n';

    /**
     * The number token.  This value is in nval.
     */
    public static final int TT_NUMBER = -2;

    /**
     * The word token.  This value is in sval.
     */
    public static final int TT_WORD = -3;

    public static final int TT_INT     = -4;
    public static final int TT_DOUBLE  = -5;

    /**
     * The Stream value.
     */
    public String sval;

    /**
     * The number value (or double value).
     */
    public double nval;


    /**
     * The int value.
     */
    public int ival;

    boolean turbo = false;
    boolean numbers = false;
    boolean wasNumbers = false;

    /**
     * Creates a stream tokenizer that parses the specified input
     * stream.
     * By default, it recognizes numbers, Strings quoted with
     * single and double quotes, and all the alphabetics.
     * @param I the input stream
     */
    public TurboTokenizer (InputStream I) {
	input = I;
	buf = new char[20];
	byte ct[] = ctype;
	int i;
	wordChars('a', 'z');
	wordChars('A', 'Z');
	wordChars(128 + 32, 255);
	whitespaceChars(0, ' ');
	commentChar('/');
	quoteChar('"');
	quoteChar('\'');
	parseNumbers();
    }

    /**
     * Resets the syntax table so that all characters are special.
     */
    public void resetSyntax() {
	for (int i = ctype.length; --i >= 0;)
	    ctype[i] = 0;
    }

    /**
     * Specifies that characters in this range are word characters.
     * @param low the low end of the range
     * @param hi the high end of the range
     */
    public void wordChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi > ctype.length)
	    hi = ctype.length;
	while (low <= hi)
	    ctype[low++] |= CT_ALPHA;
    }

    /**
     * Specifies that characters in this range are whitespace
     * characters.
     * @param low the low end of the range
     * @param hi the high end of the range
     */
    public void whitespaceChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi > ctype.length)
	    hi = ctype.length;
	while (low <= hi)
	    ctype[low++] = CT_WHITESPACE;
    }

    /**
     * Specifies that characters in this range are 'ordinary'.
     * Ordinary characters mean that any significance as words,
     * comments, strings, whitespaces or number characters are removed.
     * When these characters are encountered by the
     * parser, they return a ttype equal to the character.
     * @param low the low end of the range
     * @param hi the high end of the range
     */
    public void ordinaryChars(int low, int hi) {
	if (low < 0)
	    low = 0;
	if (hi > ctype.length)
	    hi = ctype.length;
	while (low <= hi)
	    ctype[low++] = 0;
    }

    /**
     * Specifies that this character is 'ordinary': it removes any
     * significance as a word, comment, string, whitespace or number
     * character.  When encountered by the parser, it returns a ttype
     * equal to the character.
     * @param ch the character
     */
    public void ordinaryChar(int ch) {
	ctype[ch] = 0;
    }

    /**
     * Specifies that this character starts a single line comment.
     * @param ch the character
     */
    public void commentChar(int ch) {
	ctype[ch] = CT_COMMENT;
    }

    /**
     * Specifies that matching pairs of this character delimit String
     * constants.  When a String constant is recognized, ttype will be
     * the character that delimits the String, and sval will have
     * the body of the String.
     * @param ch the character
     */
    public void quoteChar(int ch) {
	ctype[ch] = CT_QUOTE;
    }

    /**
     * Specifies that numbers should be parsed.  This method accepts
     * double precision floating point numbers and returns a ttype of
     * TT_NUMBER with the value in nval.
     */
    public void parseNumbers() {
        numbers = true;
	for (int i = '0'; i <= '9'; i++) {
	    ctype[i] |=  CT_DIGIT;
	    ctype[i] &= ~CT_ALPHA;
	  }
	ctype['.'] |=  CT_DIGIT;
	ctype['.'] &= ~CT_ALPHA;
	ctype['-'] |=  CT_DIGIT;
	ctype['-'] &= ~CT_ALPHA;
    }

    public void noParseNumbers() {
        numbers = false;
	for (int i = '0'; i <= '9'; i++) {
	    ctype[i] &= ~CT_DIGIT;
	    ctype[i] |=  CT_ALPHA;
	  }
	ctype['.'] &= ~CT_DIGIT;
	ctype['.'] |=  CT_ALPHA;
	ctype['-'] &= ~CT_DIGIT;
	ctype['-'] |=  CT_ALPHA;
    }

  static final String digitChars = "0123456789";
  int charType(int c) {
    if (digitChars.indexOf(c) >= 0) return DIGIT;
    if (c == '.') return DECIMAL;
    if (c == '-') return SIGN;
    if (c == 'e' || c == 'E') return EXP;
    return OTHER;
  }


  public void turboParse(boolean turbo) {
    this.turbo = turbo;
    if (turbo) {
      wasNumbers = numbers;
      noParseNumbers();
    } else {
      if (wasNumbers) parseNumbers();
      wasNumbers = false;
    }
  }

    /**
     * If the flag is true, end-of-lines are significant (TT_EOL will
     * be returned by nexttoken).  If false, they will be treated
     * as whitespace.
     */
    public void eolIsSignificant(boolean flag) {
	eolIsSignificantP = flag;
    }

    /**
     * If the flag is true, recognize C style( /* ) comments.
     */
    public void slashStarComments(boolean flag) {
	slashStarCommentsP = flag;
    }

    /**
     * If the flag is true, recognize C++ style( // ) comments.
     */
    public void slashSlashComments(boolean flag) {
	slashSlashCommentsP = flag;
    }

    /**
     * Examines a boolean to decide whether TT_WORD tokens are
     * forced to be lower case.
     * @param fl the boolean flag
     */
    public void lowerCaseMode(boolean fl) {
	forceLower = fl;
    }

    /**
     * Parses a token from the input stream.  The return value is
     * the same as the value of ttype.  Typical clients of this
     * class first set up the syntax tables and then sit in a loop
     * calling nextToken to parse successive tokens until TT_EOF
     * is returned.
     */
    public int nextToken() throws java.io.IOException {
      nextToken0();
      if (turbo && this.ttype==TT_WORD) {
	switch (this.ttype=numberType()) {
	case TT_INT:
	  this.ival = Integer.valueOf(this.sval).intValue();
	  break;
	case TT_DOUBLE:
	  this.nval = Double.valueOf(this.sval).doubleValue();
	  break;
	}
      }
      return this.ttype;
    }

    int nextToken0() throws java.io.IOException {
	if (pushedBack) {
	    pushedBack = false;
	    return ttype;
	}
	InputStream is = input;
	byte ct[] = ctype;
	int c = peekc;
	sval = null;

	if (c < 0)
	    return ttype = TT_EOF;
	int ctype = c < 256 ? ct[c] : CT_ALPHA;
	while ((ctype & CT_WHITESPACE) != 0) {
	    if (c == '\r') {
		LINENO++;
		c = is.read();
		if (c == '\n')
		    c = is.read();
		if (eolIsSignificantP) {
		    peekc = c;
		    return ttype = TT_EOL;
		}
	    } else {
		if (c == '\n') {
		    LINENO++;
		    if (eolIsSignificantP) {
			peekc = ' ';
			return ttype = TT_EOL;
		    }
		}
		c = is.read();
	    }
	    if (c < 0)
		return ttype = TT_EOF;
	    ctype = c < 256 ? ct[c] : CT_ALPHA;
	}
	if ((ctype & CT_DIGIT) != 0) {
	    boolean neg = false;
	    if (c == '-') {
		c = is.read();
		if (c != '.' && (c < '0' || c > '9')) {
		    peekc = c;
		    return ttype = '-';
		}
		neg = true;
	    }
	    double v = 0;
	    int decexp = 0;
	    int seendot = 0;
	    while (true) {
		if (c == '.' && seendot == 0)
		    seendot = 1;
		else if ('0' <= c && c <= '9') {
		    v = v * 10 + (c - '0');
		    decexp += seendot;
		} else
		    break;
		c = is.read();
	    }
	    peekc = c;
	    if (decexp != 0) {
		double denom = 10;
		decexp--;
		while (decexp > 0) {
		    denom *= 10;
		    decexp--;
		}
		/* do one division of a likely-to-be-more-accurate number */
		v = v / denom;
	    }
	    nval = neg ? -v : v;
	    return ttype = TT_NUMBER;
	}
	if ((ctype & CT_ALPHA) != 0) {
	    int i = 0;
	    do {
		if (i >= buf.length) {
		    char nb[] = new char[buf.length * 2];
		    System.arraycopy(buf, 0, nb, 0, buf.length);
		    buf = nb;
		}
		buf[i++] = (char) c;
		c = is.read();
		ctype = c < 0 ? CT_WHITESPACE : c < 256 ? ct[c] : CT_ALPHA;
	    } while ((ctype & (CT_ALPHA | CT_DIGIT)) != 0);
	    peekc = c;
	    sval = String.copyValueOf(buf, 0, i);
	    if (forceLower)
		sval = sval.toLowerCase();
	    return ttype = TT_WORD;
	}
	if ((ctype & CT_COMMENT) != 0) {
	    while ((c = is.read()) != '\n' && c != '\r' && c >= 0);
	    peekc = c;
	    return nextToken();
	}
	if ((ctype & CT_QUOTE) != 0) {
	    ttype = c;
	    int i = 0;
	    while ((c = is.read()) >= 0 && c != ttype && c != '\n' && c != '\r') {
		if (c == '\\')
		    switch (c = is.read()) {
		      case 'a':
			c = 0x7;
			break;
		      case 'b':
			c = '\b';
			break;
		      case 'f':
			c = 0xC;
			break;
		      case 'n':
			c = '\n';
			break;
		      case 'r':
			c = '\r';
			break;
		      case 't':
			c = '\t';
			break;
		      case 'v':
			c = 0xB;
			break;
		      case '0':
		      case '1':
		      case '2':
		      case '3':
		      case '4':
		      case '5':
		      case '6':
		      case '7':
			c = c - '0';
			int c2 = is.read();
			if ('0' <= c2 && c2 <= '7') {
			    c = (c << 3) + (c2 - '0');
			    c2 = is.read();
			    if ('0' <= c2 && c2 <= '7')
				c = (c << 3) + (c2 - '0');
			    else
				peekc = c;
			} else
			    peekc = c;
			break;
		    }
		if (i >= buf.length) {
		    char nb[] = new char[buf.length * 2];
		    System.arraycopy(buf, 0, nb, 0, buf.length);
		    buf = nb;
		}
		buf[i++] = (char) c;
	    }
	    peekc = ' ';
	    sval = String.copyValueOf(buf, 0, i);
	    return ttype;
	}
	if (c == '/' && (slashSlashCommentsP || slashStarCommentsP)) {
	    c = is.read();
	    if (c == '*' && slashStarCommentsP) {
		int prevc = 0;
		while ((c = is.read()) != '/' || prevc != '*') {
		    if (c == '\n')
			LINENO++;
		    if (c < 0)
			return ttype = TT_EOF;
		    prevc = c;
		}
		peekc = ' ';
		return nextToken();
	    } else if (c == '/' && slashSlashCommentsP) {
		while ((c = is.read()) != '\n' && c != '\r' && c >= 0);
		peekc = c;
		return nextToken();
	    } else {
		peekc = c;
		return ttype = '/';
	    }
	}
	peekc = ' ';
	return ttype = c;
    }

  public int numberType() {
    int i=0, len = this.sval.length();
    int ctype, state = 0;
    boolean isDouble = false;
    while (i<len) {
      ctype = charType(this.sval.charAt(i));
      isDouble |= ( (ctype==DECIMAL) || (ctype==EXP) );
      switch (state) {
      case 0:
	switch (ctype) {
	case DIGIT:   state =  1; break;
	case SIGN:    state =  2; break;
        case DECIMAL: state =  3; break;
	default:      state = 10; break;
	}
	break;
      case 1:
	switch (ctype) {
	case DIGIT:   break;
        case EXP:     state =  5; break;
        case DECIMAL: state =  4; break;
	default:      state = 10; break;
	}
	break;
      case 2:
	switch (ctype) {
        case DECIMAL: state =  3; break;
	default:      state = 10; break;
	}
	break;
      case 3:
	switch (ctype) {
        case DIGIT:   state =  6; break;
	default:      state = 10; break;
	}
	break;
      case 4:
	switch (ctype) {
        case DIGIT:   state =  6; break;
	default:      state = 10; break;
	}
	break;
      case 5:
	switch (ctype) {
        case SIGN:    state =  7; break;
        case DIGIT:   state =  8; break;
	default:      state = 10; break;
	}
	break;
      case 6:
	switch (ctype) {
        case DIGIT:   break;
        case EXP:     state =  5; break;
	default:      state = 10; break;
	}
	break;
      case 7:
	switch (ctype) {
        case DIGIT:   state =  8; break;
	default:      state = 10; break;
	}
	break;
      case 8:
	switch (ctype) {
        case DIGIT:   break;
	default:      state = 10; break;
	}
	break;
      case 10: // FAIL state
	i = len;
	break;
      }
      ++i;
    }
    boolean accept = ((state==1) || (state==4) || (state==6) || (state==8));
    if (accept) {
      if (isDouble) return TT_DOUBLE;
      else return TT_INT;
    } else {
      return TT_WORD;
    }
  }





    /**
     * Pushes back a stream token.
     */
    public void pushBack() {
	pushedBack = true;
    }

    /** Return the current line number. */
    public int lineno() {
	return LINENO;
    }

    /**
     * Returns the String representation of the stream token.
     */
    public String toString() {
	String ret;
	switch (ttype) {
	  case TT_EOF:
	    ret = "EOF";
	    break;
	  case TT_EOL:
	    ret = "EOL";
	    break;
	  case TT_WORD:
	    ret = sval;
	    break;
	  case TT_NUMBER:
	    ret = "n=" + nval;
	    break;
	  case TT_DOUBLE:
	    ret = "d=" + nval;
	    break;
	  case TT_INT:
	    ret = "i=" + ival;
	    break;
	  default:{
		char s[] = new char[3];
		s[0] = s[2] = '\'';
		s[1] = (char) ttype;
		ret = new String(s);
		break;
	    }
	}
	return "Token[" + ret + "], line " + LINENO;
    }
}

