package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import main.cluster.KMeans;
import main.math.MathUtils;

public class Main
{
	private static final String FILE_NAME = "data/data.base";

	private static final int COL_USER_ID = 0;
	private static final int COL_ITEM_ID = 1;
	private static final int COL_RATTING = 2;

	public static void main(String s[])
	{
		int maxUserId = 0;
		int maxItemId = 0;

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));

			for (int i = 0; in.ready(); i++)
			{
				String[] elements = in.readLine().split(" ");

				int userId = Integer.parseInt(elements[COL_USER_ID]);
				if (userId > maxUserId)
					maxUserId = userId;

				int itemId = Integer.parseInt(elements[COL_ITEM_ID]);
				if (itemId > maxItemId)
					maxItemId = userId;
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("maxUserId: " + maxUserId + " | maxItemId: " + maxItemId);

		double[][] avaliacoes = new double[maxUserId][maxItemId];
/*
		for (int i = 0; i < maxUserId; i++)
			for (int j = 0; j < maxUserId; j++)
				avaliacoes[i][j] = NO_ELEMENT;
*/
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));

			while (in.ready())
			{
				String[] elements = in.readLine().split(" ");

				int userId = Integer.parseInt(elements[COL_USER_ID]) - 1;
				int itemId = Integer.parseInt(elements[COL_ITEM_ID]) - 1;

				//System.out.println("userId: " + userId + " | itemId: " + itemId);

				avaliacoes[userId][itemId] = Double.parseDouble(elements[COL_RATTING]);
			}

			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		double[][] distance = new double[maxUserId][maxUserId];

		for (int i = 0; i < maxUserId; i++)
		{
			for (int j = i + 1; j < maxUserId; j++)
			{
				int count = 0;

				for (int filme = 0; filme < maxItemId; filme++)
				{
					if (avaliacoes[i][filme] != KMeans.NO_VALUE && avaliacoes[j][filme] != KMeans.NO_VALUE)
					{
						distance[i][j] += (avaliacoes[i][filme] + avaliacoes[j][filme]) / 2;

						count++;
					}
				}

				if (count > 0)
				{
					distance[i][j] /= count;
				}

				distance[j][i] = distance[i][j];
			}
		}

		//System.out.println(MathUtils.showMatrix(distance));

		KMeans cluster = new KMeans(3, avaliacoes, distance);

		System.out.println(MathUtils.showMatrix(cluster.getMeans()));
		System.out.println(MathUtils.showVector(cluster.getAssignments()));
	}
}
