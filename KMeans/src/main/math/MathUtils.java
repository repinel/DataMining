package main.math;

public class MathUtils
{
	/**
	 * Generates a String representation of the matrix.
	 * 
	 * @param m Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static String showMatrix(double[][] m)
	{
		if (m == null)
		{
			return new String("{ (empty) }");
		}
		String s = new String("{ ");
		for (int i = 0; i < m.length; i++)
		{
			s += " " + m[i][0];
			for (int j = 1; j < m[i].length; j++)
			{
				s += ", " + m[i][j];
			}
			s += "\n";
		}
		s += "}";
		return s;
	}

	/**
	 * Generates a String representation of the matrix.
	 * 
	 * @parameter the matrix, usually a colormap.
	 * @return The string.
	 */
	public static String showMatrix(byte[][] m)
	{
		if (m == null)
		{
			return new String("{ (empty) }");
		}
		String s = new String("{ ");
		for (int i = 0; i < m.length; i++)
		{
			s += " " + m[i][0];
			for (int j = 1; j < m[i].length; j++)
			{
				s += ", " + m[i][j];
			}
			s += "\n";
		}
		s += "}";
		return s;
	}

	public static String showMatrix(int[][] m)
	{
		if (m == null)
		{
			return new String("{ (empty) }");
		}
		String s = new String("{ ");
		for (int i = 0; i < m.length; i++)
		{
			s += " " + m[i][0];
			for (int j = 1; j < m[i].length; j++)
			{
				s += ", " + m[i][j];
			}
			s += "\n";
		}
		s += "}";
		return s;
	}

	public static String showVector(int[] m)
	{
		if (m == null)
		{
			return new String("{ (empty) }");
		}
		String s = new String("{ " + m[0]);
		for (int i = 1; i < m.length; i++)
		{
			s += ", " + m[i];
		}
		s += "}";
		return s;
	}

	public static String showVector(float[] m)
	{
		if (m == null)
		{
			return new String("{ (empty) }");
		}
		String s = new String("{ " + m[0]);
		for (int i = 1; i < m.length; i++)
		{
			s += ", " + m[i];
		}
		s += "}";
		return s;
	}

	public static String showVector(double[] m)
	{
		if (m == null)
		{
			return new String("(empty)");
		}
		String s = new String("" + m[0]);
		for (int i = 1; i < m.length; i++)
		{
			s += ", " + m[i];
		}
		//s += "}";
		return s;
	}

	public static String showVector(java.util.Vector v)
	{
		if (v == null)
		{
			return new String("{ (empty) }");
		}
		String s = new String("{ " + v.firstElement());
		for (int i = 1; i < v.size(); i++)
		{
			s += ", " + v.elementAt(i);
		}
		s += "}";
		return s;

	}
}
