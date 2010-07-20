package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import main.cluster.KMeans;
import main.math.MathUtils;

public class Main
{
	public static void main(String s[])
	{
		double[][] data = new double[2074][2];

		try
		{
			BufferedReader in = new BufferedReader(new FileReader("data/simulacao_teste.base"));
			String str;

			for (int i = 0; in.ready(); i++)
			{
				str = in.readLine();

				String[] elements = str.split(" ");
				for (int j = 0; j < elements.length; j++)
				{
					if (j != 0 && j != 3)
						data[i][j-1] = Double.parseDouble(elements[j]);
				}
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		//System.out.println(MathUtils.showMatrix(data));

		KMeans cluster = new KMeans(5, data);

		System.out.println(MathUtils.showMatrix(cluster.getMeans()));
		System.out.println(MathUtils.showVector(cluster.getAssignments()));
	}
}
