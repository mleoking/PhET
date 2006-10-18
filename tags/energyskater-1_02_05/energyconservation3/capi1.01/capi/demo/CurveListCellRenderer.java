import javax.swing.*;
import java.awt.*;
import com.graphbuilder.curve.*;

public class CurveListCellRenderer extends DefaultListCellRenderer {

	public CurveListCellRenderer() {
		super();
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		String name = "<unknown>";

		if (value instanceof BezierCurve)
			name = "Bezier Curve";
		else if (value instanceof NURBSpline) // must come before BSpline
			name = "NURB-Spline";
		else if (value instanceof BSpline)
			name = "B-Spline";
		else if (value instanceof CardinalSpline)
			name = "Cardinal Spline";
		else if (value instanceof CatmullRomSpline)
			name = "Catmull-Rom Spline";
		else if (value instanceof CubicBSpline)
			name = "Cubic B-Spline";
		else if (value instanceof LagrangeCurve)
			name = "Lagrange Curve";
		else if (value instanceof NaturalCubicSpline)
			name = "Natural Cubic Spline";
		else if (value instanceof Polyline)
			name = "Polyline";

		return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
	}
}