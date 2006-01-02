package com.graphbuilder.curve;

import com.graphbuilder.math.*;

/**
<p>A GroupIterator allows a curve to choose the points from a control-path that the curve uses to
define itself.  The subset of points to use is defined by a control-string.  The control-string is evaluated
to produce a series of integer groups that are iterated over.

<p>A common way to create a GroupIterator that uses all the points of a control-path is:

<pre>
GroupIterator gi = new GroupIterator("0:n-1", cp.getNumPoints());
</pre>

<p>The "0:n-1" is the control-string and cp is a control-path.  Once created, the groups that are iterated
over cannot be changed.  Thus, if the number of points in the control-path changes, then a new group iterator
is required.

<p>Some blended curves do not evaluate over the first and last points.  A technique to get the curve to
connect to the endpoints is to create duplicate endpoints.  But instead of doing that, a control-string
such as "0,0:n-1,n-1" will do the same thing.

<p>The syntax of the control-string is fairly basic.  A control-string consists of one or more groups
separated by a comma and possibly additional whitespace.  Each group consists of either 1 or 2 expressions.
If there are two expressions, then a colon ':' separates them.  The expressions are parsed using the
ExpressionTree.parse(String) method.  Each expression can contain at most one variable and the value of that
variable is set to the value specified in the GroupIterator constructor.  An exception is thrown if there
are multiple variables in a single expression.  The result of evaluating the expressions is rounded.

<p><center><b><font size="4">Detailed Example</font></b></center>

<p>Suppose the control-string is: "1:4,8:n/2,7:3,5"
<br>Suppose n = 21, then the internal group array is {1, 4, 8, 11, 7, 3, 5, 5}
<br>Notice that the length of the group array is twice the number groups.
<br>The group size is (|1 - 4| + 1) + (|8 - 11| + 1) + (|7 - 3| + 1) + (|5 - 5| + 1) = 14.

<pre>
GroupIterator gi = new GroupIterator("1:4,8:n/2,7:3,5", 21);

while (gi.hasNext())
	System.out.print(gi.next() + ", ");

Output:  1, 2, 3, 4, 8, 9, 10, 11, 7, 6, 5, 4, 3, 5
index_i: 0, 0, 0, 0, 2, 2,  2,  2, 4, 4, 4, 4, 4, 6
count_j: 0, 1, 2, 3, 0, 1,  2,  3, 0, 1, 2, 3, 4, 0

</pre>

<p>Notice that the number of numbers outputted is 14, which is the group size.  At this point, calling next()
will result in an exception.  However, GroupIterator objects are reusable and can be reset by calling the
reset() method or by calling set(0, 0).

<p>The variables index_i and count_j represent the current state of the group-iterator.  The index_i
is an index location in the internal group-array that keeps track of the current group.  Since each
group always has a start index and a finish index, index_i refers to the first index and increments by 2.

<p>The count_j variable increments by one until group[index_i] &plusmn; count_j == group[index_i + 1].
At this point, count_j is reset to 0 and index_i is incremented by 2.

@see com.graphbuilder.math.ExpressionTree
@see com.graphbuilder.curve.Curve
@see com.graphbuilder.curve.ControlPath
*/

public class GroupIterator {

	protected String controlString = null;
	protected int[] group = null;

	protected int index_i = 0;
	protected int count_j = 0;

	/**
	Constructs a group-iterator by parsing the control-string string according to the class description.

	@throws IllegalArgumentException If the control-string is null.
	@throws ControlStringParseException If the control-string is invalid.
	*/
	public GroupIterator(String controlString, int n) {
		if (controlString == null)
			throw new IllegalArgumentException("control string cannot be null");

		group = parseControlString(controlString, n);
		this.controlString = controlString;
	}

	/**
	Constructs a group-iterator by copying the specified group array into a new internal array.  A control-string
	is created based on the values in the array.

	@throws IllegalArgumentException If the specified array is null, the array length is 0 or the array length is odd.
	*/
	public GroupIterator(int[] group) {
		if (group == null)
			throw new IllegalArgumentException("group array cannot be null");

		if (group.length == 0)
			throw new IllegalArgumentException("group array length cannot be 0");

		if (group.length % 2 != 0)
			throw new IllegalArgumentException("group array must have even length");

		double log10 = Math.log(10);

		int numDigits = 0;

		int[] arr = new int[group.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = group[i];
			int x = arr[i];
			if (x < 0) {
				numDigits++; // minus sign
				x = -x;
			}
			numDigits += (int) (Math.log(x) / log10) + 1;
		}

		this.group = arr;

		StringBuffer sb = new StringBuffer(numDigits + (arr.length / 2) + (arr.length - 1));
		sb.append(arr[0]);
		sb.append(":");
		sb.append(arr[1]);

		for (int i = 2; i < group.length; i += 2) {
			sb.append(",");
			sb.append(group[i]);
			sb.append(":");
			sb.append(group[i+1]);
		}

		controlString = sb.toString();
	}

	/**
	Parses the specified control-string according to the class description and returns a group-array.  An
	exception will be thrown if the control-string is invalid.
	*/
	public static int[] parseControlString(String controlString, int n) {
		final String s = controlString;
		final int sLength = s.length();

		int numGroups = 1;
		int b = 0;

		for (int i = 0; i < sLength; i++) {
			char c = s.charAt(i);

			if (c == ',' && b == 0)
				numGroups++;
			else if (c == '(')
				b++;
			else if (c == ')')
				b--;
		}

		if (b != 0)
			throw new ControlStringParseException("round brackets do not balance");

		int[] group = new int[2 * numGroups];
		int j = 0;
		int k = 0;
		int colon = -1;

		VarMap vm = new VarMap();
		FuncMap fm = new FuncMap();
		fm.loadDefaultFunctions();

		for (int i = 0; i <= sLength; i++) {
			char c = ' ';
			if (i < sLength) c = s.charAt(i);

			if (i == sLength || c == ',' && b == 0) {
				
				if (colon == -1) {
					Expression x = setVariables(s, vm, n, j, i);
					group[k] = (int) Math.round(x.eval(vm, fm));
					group[k+1] = group[k];
					k += 2;
				}
				else {
					Expression x1 = setVariables(s, vm, n, j, colon);
					group[k++] = (int) Math.round(x1.eval(vm, fm));

					Expression x2 = setVariables(s, vm, n, colon + 1, i);
					group[k++] = (int) Math.round(x2.eval(vm, fm));
				}

				j = i + 1;
				colon = -1;
			}
			else if (c == '(')
				b++;
			else if (c == ')')
				b--;
			else if (c == ':')
				colon = i;
		}

		return group;
	}

	private static Expression setVariables(String s, VarMap vm, int n, int j, int i) {
		Expression x = null;

		try {
			x = ExpressionTree.parse(s.substring(j, i));
		} catch (ExpressionParseException epe) {
			throw new ControlStringParseException("error parsing expression", j, i, epe);
		}

		if (x == null)
			throw new ControlStringParseException("control substring is empty", j, i);

		String[] v = x.getVariableNames();

		if (v.length > 1)
			throw new ControlStringParseException("too many variables", j, i);
		else if (v.length == 1)
			vm.setValue(v[0], n);

		return x;
	}

	/**
	Returns the control-string used in the constuctor.
	*/
	public String getControlString() {
		return controlString;
	}

	/**
	Returns the length of the internal group-array.
	*/
	public int getGroupLength() {
		return group.length;
	}

	/**
	Returns the value at the specified index in the internal group-array.

	@see #getGroupLength()
	@throws IllegalArgumentException If index < 0 or index >= group.length.
	*/
	public int getGroupValue(int index) {
		if (index < 0 || index >= group.length)
			throw new IllegalArgumentException("required: (index >= 0 && index < group.length) but: (index = " + index + ", group.length = " + group.length + ")");

		return group[index];
	}

	/**
	Returns the total number of times next() can be called before hasNext() returns false starting from state 0, 0.
	*/
	public int getGroupSize() {
		int size = 0;

		for (int i = 0; i < group.length; i += 2) {
			int dif = group[i] - group[i+1];
			if (dif < 0) dif = -dif;
			size += (dif + 1);
		}

		return size;
	}

	/**
	Copies the internal group-array into the specified array.

	@see #getGroupLength()
	@throws IllegalArgumentException If the specified array is null or is shorter than the group-length.
	*/
	public void copyGroupArray(int[] arr) {
		if (arr == null)
			throw new IllegalArgumentException("specified array cannot be null");

		if (arr.length < group.length)
			throw new IllegalArgumentException("specified array is too small");

		for (int i = 0; i < group.length; i++)
			arr[i] = group[i];
	}

	/**
	Returns true if the iterator is not finished.  True will be returned if the value of index_i is
	less than the internal group array length, false otherwise.
	*/
	public boolean hasNext() {
		return index_i < group.length;
	}

	/**
	Returns the current index and advances the state to the next index.
	*/
	public int next() {
		int x = group[index_i];
		int y = group[index_i + 1];

		if (x <= y) {
			x += count_j;

			if (x >= y) {
				count_j = 0;
				index_i += 2;
			}
			else
				count_j++;
		}
		else {
			x -= count_j;

			if (x <= y) {
				count_j = 0;
				index_i += 2;
			}
			else
				count_j++;
		}

		return x;
	}

	/**
	Sets the current state of the iterator.

	@throws IllegalArgumentException If index_i < 0, count_j < 0 or index_i is an odd number.
	*/
	public void set(int index_i, int count_j) {
		if (index_i < 0) throw new IllegalArgumentException("index_i >= 0 required");
		if (index_i % 2 == 1) throw new IllegalArgumentException("index_i must be an even number");
		if (count_j < 0) throw new IllegalArgumentException("count_j >= 0 required");

		this.index_i = index_i;
		this.count_j = count_j;
	}

	/**
	index_i is the index location into the internal group array of the current group.
	*/
	public int index_i() {
		return index_i;
	}

	/**
	count_j is the increment that keeps track of the position in the current group.
	*/
	public int count_j() {
		return count_j;
	}

	/**
	Resets the state of the iterator back to the initial state.
	*/
	public void reset() {
		index_i = 0;
		count_j = 0;
	}

	/**
	Returns true if all values returned by next() are >= min and < max, false otherwise.  This is useful
	to determine if next() will generate an index value outside of a specified range.
	*/
	public boolean isInRange(int min, int max) {
		for (int i = 0; i < group.length; i++)
			if (group[i] < min || group[i] >= max)
				return false;
		return true;
	}
}