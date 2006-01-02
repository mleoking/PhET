package com.graphbuilder.math;

import com.graphbuilder.math.func.*;

/**
<p>FuncMap maps a name to a function.  A FuncMap is used in the eval method of an Expression object.
This class can be used as the default function-map.  The loadDefaultFunctions() method can be used
to take advantage of the many already implemented functions (see below).

<p>During the evaluation of an expression, if a function is not supported then a RuntimeException is thrown.

<p>Default functions:

<p>
<dl>
<dt>No Parameters</dt>
<dd>
<ul>
<li>e() &rarr; Math.E</li>
<li>pi() &rarr; Math.PI</li>
<li>rand() &rarr; Math.random()</li>
<li>min() &rarr; Double.MIN_VALUE</li>
<li>max() &rarr; Double.MAX_VALUE</li>
</ul>
</dd>
<dt>1 Parameter</dt>
<dd>
<ul>
<li>sin(x) &rarr; Math.sin(double)</li>
<li>cos(x) &rarr; Math.cos(double)</li>
<li>tan(x) &rarr; Math.tan(double)</li>
<li>asin(x) &rarr; Math.asin(double)</li>
<li>acos(x) &rarr; Math.acos(double)</li>
<li>atan(x) &rarr; Math.atan(double)</li>
<li>asinh(x) &rarr; 2 * ln(sqrt((x+1)/2) + sqrt((x-1)/2))</li>
<li>acosh(x) &rarr; ln(x + sqrt(1 + x<sup>2</sup>))</li>
<li>atanh(x) &rarr; (ln(1+x) - ln(1-x)) / 2</li>
<li>sinh(x) &rarr; (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/2</li>
<li>cosh(x) &rarr; (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2</li>
<li>tanh(x) &rarr; (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)</li>
<li>sqrt(x) &rarr; Math.sqrt(double)</li>
<li>abs(x) &rarr; Math.abs(double)</li>
<li>ceil(x) &rarr; Math.ceil(double)</li>
<li>floor(x) &rarr; Math.floor(double)</li>
<li>exp(x) &rarr; <i>e</i><sup>x</sup></li>
<li>ln(x) &rarr; log<sub><i>e</i></sub>x</li>
<li>lg(x) &rarr; log<sub>2</sub>x</li>
<li>log(x) &rarr; log<sub>10</sub>x</li>
<li>sign(x) &rarr; x > 0 = 1, x < 0 = -1, else 0</li>
<li>fact(n) &rarr; n! = 1 * 2 * ... * (n - 1) * n</li>
<li>round(x) &rarr; Math.round(double)</li>
</ul>
</dd>
<dt>2 Parameters</dt>
<dd>
<ul>
<li>log(x,y) &rarr; log<sub>y</sub>x</li>
<li>combin(n, r) &rarr; PascalsTriangle.nCr(n, r)</li>
<li>mod(x, y) &rarr; x % y</li>
<li>pow(x, y) &rarr; x<sup>y</sup></li>
</ul>
</dd>
<dt>n Parameters</dt>
<dd>
<ul>
<li>min(x1,x2,...,xn)</li>
<li>max(x1,x2,...,xn)</li>
<li>sum(x1,x2,...,xn) &rarr; x1 + x2 + ... + xn</li>
<li>avg(x1,x2,...,xn) &rarr; (x1 + x2 + ... + xn) / n</li>
</ul>
</dd>

</dl>
</p>

<p>Note: Case sensitivity can only be specified in the constructor (for consistency).  When case sensitivity is false,
the String.equalsIgnoreCase method is used.  When case sensitivity is true, the String.equals method is used.  The
matching does not include the parenthesis.  For example, when case sensitivity is false and the default functions have been
loaded, then "RaNd", "rand", and "RAND" all map to the RandFunction().  By default, case sensitivity is false.
*/
public class FuncMap {

	private String[] name = new String[50];
	private Function[] func = new Function[50];
	private int numFunc = 0;
	private boolean caseSensitive = false;

	public FuncMap() {}

	public FuncMap(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	Adds the mappings for many common functions.  The names are specified in all lowercase letters.
	*/
	public void loadDefaultFunctions() {
		// >= 0 parameters
		setFunction("min", new MinFunction());
		setFunction("max", new MaxFunction());

		// > 0 parameters
		setFunction("sum", new SumFunction());
		setFunction("avg", new AvgFunction());

		// 0 parameters
		setFunction("pi", new PiFunction());
		setFunction("e", new EFunction());
		setFunction("rand", new RandFunction());

		// 1 parameter
		setFunction("sin", new SinFunction());
		setFunction("cos", new CosFunction());
		setFunction("tan", new TanFunction());
		setFunction("sqrt", new SqrtFunction());
		setFunction("abs", new AbsFunction());
		setFunction("ceil", new CeilFunction());
		setFunction("floor", new FloorFunction());
		setFunction("exp", new ExpFunction());
		setFunction("lg", new LgFunction());
		setFunction("ln", new LnFunction());
		setFunction("sign", new SignFunction());
		setFunction("round", new RoundFunction());
		setFunction("fact", new FactFunction());
		setFunction("cosh", new CoshFunction());
		setFunction("sinh", new SinhFunction());
		setFunction("tanh", new TanhFunction());
		setFunction("acos", new AcosFunction());
		setFunction("asin", new AsinFunction());
		setFunction("atan", new AtanFunction());
		setFunction("acosh", new AcoshFunction());
		setFunction("asinh", new AsinhFunction());
		setFunction("atanh", new AtanhFunction());

		// 2 parameters
		setFunction("pow", new PowFunction());
		setFunction("mod", new ModFunction());
		setFunction("combin", new CombinFunction());

		// 1 or 2 parameters
		setFunction("log", new LogFunction());
	}

	/**
	Returns a function based on the name and the specified number of parameters.

	@throws RuntimeException If no supporting function can be found.
	*/
	public Function getFunction(String funcName, int numParam) {
		for (int i = 0; i < numFunc; i++) {
			if (func[i].acceptNumParam(numParam) && (caseSensitive && name[i].equals(funcName) || !caseSensitive && name[i].equalsIgnoreCase(funcName)))
				return func[i];
		}

		throw new RuntimeException("function not found: " + funcName + " " + numParam);
	}

	/**
	Assigns the name to map to the specified function.

	@throws IllegalArgumentException If any of the parameters are null.
	*/
	public void setFunction(String funcName, Function f) {
		if (funcName == null)
			throw new IllegalArgumentException("function name cannot be null");

		if (f == null)
			throw new IllegalArgumentException("function cannot be null");

		for (int i = 0; i < numFunc; i++) {
			if (caseSensitive && name[i].equals(funcName) || !caseSensitive && name[i].equalsIgnoreCase(funcName)) {
				func[i] = f;
				return;
			}
		}

		if (numFunc == name.length) {
			String[] tmp1 = new String[2 * numFunc];
			Function[] tmp2 = new Function[tmp1.length];

			for (int i = 0; i < numFunc; i++) {
				tmp1[i] = name[i];
				tmp2[i] = func[i];
			}

			name = tmp1;
			func = tmp2;
		}

		name[numFunc] = funcName;
		func[numFunc] = f;
		numFunc++;
	}

	/**
	Returns true if the case of the function names is considered.
	*/
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	Returns an array of exact length of the function names stored in this map.
	*/
	public String[] getFunctionNames() {
		String[] arr = new String[numFunc];

		for (int i = 0; i < arr.length; i++)
			arr[i] = name[i];

		return arr;
	}

	/**
	Returns an array of exact length of the functions stored in this map.  The returned
	array corresponds to the order of the names returned by getFunctionNames.
	*/
	public Function[] getFunctions() {
		Function[] arr = new Function[numFunc];

		for (int i = 0; i < arr.length; i++)
			arr[i] = func[i];

		return arr;
	}

	/**
	Removes the function-name and the associated function from the map.  Does nothing if the function-name
	is not found.
	*/
	public void remove(String funcName) {
		for (int i = 0; i < numFunc; i++) {
			if (caseSensitive && name[i].equals(funcName) || !caseSensitive && name[i].equalsIgnoreCase(funcName)) {
				for (int j = i + 1; j < numFunc; j++) {
					name[j - 1] = name[j];
					func[j - 1] = func[j];
				}
				numFunc--;
				name[numFunc] = null;
				func[numFunc] = null;
				break;
			}
		}
	}
}
