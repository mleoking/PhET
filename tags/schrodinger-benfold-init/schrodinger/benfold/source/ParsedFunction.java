class ParsedFunction implements Function {
    public static void main( String[] args ) {
        ParsedFunction f = new ParsedFunction( args[0] );
        System.err.println( f.evaluate( Double.valueOf( args[1] ).doubleValue() ) );
    }

    public ParsedFunction( String s ) {
        setString( s );
    }

    public Expression parseExpression( String s ) {
        int index, index2;

        s = s.trim();

        System.err.println( "Parsing:  \"" + s + "\"" );

        if( s.equals( "" ) ) {
            throw new IllegalArgumentException( "Missing operand" );
        }

        //	Rules should be in reverse order of precedence

        if( ( index = findOperator( '+', s, 0 ) ) != -1 ) {
            return new Addition(
                    parseExpression( s.substring( 0, index ) ),
                    parseExpression( s.substring( index + 1 ) )
            );
        }

        if( ( index = findOperator( '-', s, 0 ) ) != -1 ) {
            return new Subtraction(
                    parseExpression( s.substring( 0, index ) ),
                    parseExpression( s.substring( index + 1 ) )
            );
        }

        if( ( index = findOperator( '*', s ) ) != -1 ) {
            return new Multiplication(
                    parseExpression( s.substring( 0, index ) ),
                    parseExpression( s.substring( index + 1 ) )
            );
        }

        if( ( index = findOperator( '/', s ) ) != -1 ) {
            return new Division(
                    parseExpression( s.substring( 0, index ) ),
                    parseExpression( s.substring( index + 1 ) )
            );
        }


        if( s.toLowerCase().startsWith( "sin(" ) ) {
            return new Sine( parseExpression( s.substring( 3 ) ) );
        }
        if( s.toLowerCase().startsWith( "cos(" ) ) {
            return new Cosine( parseExpression( s.substring( 3 ) ) );
        }
        if( s.toLowerCase().startsWith( "tan(" ) ) {
            return new Tan( parseExpression( s.substring( 3 ) ) );
        }
        if( s.toLowerCase().startsWith( "exp(" ) ) {
            return new Exp( parseExpression( s.substring( 3 ) ) );
        }
        if( s.toLowerCase().startsWith( "abs(" ) ) {
            return new Abs( parseExpression( s.substring( 3 ) ) );
        }

        /*if(s.startsWith("-"))
              return new Subtraction(
                  new Constant(0),
                  parseExpression(s.substring(1))
              );    */

        /*if(s.startsWith("+"))
              return parseExpression(s.substring(1));

          if((index=findOperator('^',s))!=-1)
              return new Power(
                  parseExpression(s.substring(0,index)),
                  parseExpression(s.substring(index+1))
              ); */


        if( s.charAt( 0 ) == '(' ) {
            index = findOperator( ')', s.substring( 1 ) );
            if( index == -1 ) {
                throw new IllegalArgumentException( "Mismatched brackets" );
            }
            if( index != s.length() - 2 ) {
                throw new IllegalArgumentException( "Parse error:  ..." + s.substring( index + 1 ) );
            }
            return parseExpression( s.substring( 1, index + 1 ) );
        }


        if( s.equalsIgnoreCase( "x" ) ) {
            return new X();
        }

        if( s.equalsIgnoreCase( "pi" ) ) {
            return new Constant( Math.PI );
        }

        try {
            return new Constant( Double.valueOf( s ).doubleValue() );
        }
        catch( NumberFormatException e ) {
            throw new IllegalArgumentException( "Parse error:  " + s );
        }
    }

    public static int findOperator( char op, String s ) {
        return findOperator( op, s, 0 );
    }

    public static int findOperator( char op, String s, int startPos ) {
        int bracketCount = 0;
        for( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            if( c == op && bracketCount == 0 && i >= startPos ) {
                return i;
            }
            if( c == '(' ) {
                bracketCount++;
            }
            if( c == ')' ) {
                bracketCount--;
            }
        }

        if( bracketCount == 0 ) {
            return -1;
        }
        else {
            throw new IllegalArgumentException( "Mismatched brackets" );
        }
    }

    public double evaluate( double x ) {
        return expression.evaluate( x );
    }

    public void setString( String s ) {
        if( s.equals( string ) ) {
            return;
        }

        expression = parseExpression( s );
        string = s;
    }

    public String toString() {
        return string;
    }


    public Expression expression;
    public String string;


    abstract class Expression {
        public abstract double evaluate( double x );
    }


    class Constant extends Expression {
        public Constant( double val ) {
            this.val = val;
        }

        public double evaluate( double x ) {
            return val;
        }

        protected double val;
    }

    class X extends Expression {
        public X() {
        }

        public double evaluate( double x ) {
            return x;
        }
    }


    class Addition extends Expression {
        public Addition( Expression lhs, Expression rhs ) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public double evaluate( double x ) {
            return lhs.evaluate( x ) + rhs.evaluate( x );
        }

        protected Expression lhs, rhs;
    }

    class Subtraction extends Expression {
        public Subtraction( Expression lhs, Expression rhs ) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public double evaluate( double x ) {
            return lhs.evaluate( x ) - rhs.evaluate( x );
        }

        protected Expression lhs, rhs;
    }

    class Multiplication extends Expression {
        public Multiplication( Expression lhs, Expression rhs ) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public double evaluate( double x ) {
            return lhs.evaluate( x ) * rhs.evaluate( x );
        }

        protected Expression lhs, rhs;
    }

    class Division extends Expression {
        public Division( Expression lhs, Expression rhs ) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public double evaluate( double x ) {
            return lhs.evaluate( x ) / rhs.evaluate( x );
        }

        protected Expression lhs, rhs;
    }

    class Power extends Expression {
        public Power( Expression lhs, Expression rhs ) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public double evaluate( double x ) {
            return Math.pow( lhs.evaluate( x ), rhs.evaluate( x ) );
        }

        protected Expression lhs, rhs;
    }


    class LessThan extends Expression {
        protected Expression l, r;

        public LessThan( Expression l, Expression r ) {
            this.l = l;
            this.r = r;
        }

        public double evaluate( double x ) {
            return ( l.evaluate( x ) < r.evaluate( x ) ) ? 1 : 0;
        }
    }

    ;

    class LessThanEqual extends Expression {
        protected Expression l, r;

        public LessThanEqual( Expression l, Expression r ) {
            this.l = l;
            this.r = r;
        }

        public double evaluate( double x ) {
            return ( l.evaluate( x ) <= r.evaluate( x ) ) ? 1 : 0;
        }
    }

    ;

    class GreaterThan extends Expression {
        protected Expression l, r;

        public GreaterThan( Expression l, Expression r ) {
            this.l = l;
            this.r = r;
        }

        public double evaluate( double x ) {
            return ( l.evaluate( x ) > r.evaluate( x ) ) ? 1 : 0;
        }
    }

    ;

    class GreaterThanEqual extends Expression {
        protected Expression l, r;

        public GreaterThanEqual( Expression l, Expression r ) {
            this.l = l;
            this.r = r;
        }

        public double evaluate( double x ) {
            return ( l.evaluate( x ) >= r.evaluate( x ) ) ? 1 : 0;
        }
    }

    ;

    class Equal extends Expression {
        protected Expression l, r;

        public Equal( Expression l, Expression r ) {
            this.l = l;
            this.r = r;
        }

        public double evaluate( double x ) {
            return ( l.evaluate( x ) == r.evaluate( x ) ) ? 1 : 0;
        }
    }

    ;

    class NotEqual extends Expression {
        protected Expression l, r;

        public NotEqual( Expression l, Expression r ) {
            this.l = l;
            this.r = r;
        }

        public double evaluate( double x ) {
            return ( l.evaluate( x ) != r.evaluate( x ) ) ? 1 : 0;
        }
    }

    ;


    abstract class Fn extends Expression {
        public Fn( Expression arg ) {
            this.arg = arg;
        }

        public double evaluate( double x ) {
            return evalFn( arg.evaluate( x ) );
        }

        abstract double evalFn( double val );

        protected Expression arg;
    }

    class Sine extends Fn {
        public Sine( Expression e ) {
            super( e );
        }

        public double evalFn( double val ) {
            return Math.sin( val );
        }
    }

    ;

    class Cosine extends Fn {
        public Cosine( Expression e ) {
            super( e );
        }

        public double evalFn( double val ) {
            return Math.cos( val );
        }
    }

    ;

    class Tan extends Fn {
        public Tan( Expression e ) {
            super( e );
        }

        public double evalFn( double val ) {
            return Math.tan( val );
        }
    }

    ;

    class Exp extends Fn {
        public Exp( Expression e ) {
            super( e );
        }

        public double evalFn( double val ) {
            return Math.exp( val );
        }
    }

    ;

    class Abs extends Fn {
        public Abs( Expression e ) {
            super( e );
        }

        public double evalFn( double val ) {
            return Math.abs( val );
        }
    }

    ;
}