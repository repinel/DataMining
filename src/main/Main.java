package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import main.cluster.KMeans;
import main.math.MathUtils;

public class Main
{
	//private static final String FILE_NAME = "data/data.base";
	//private static final String FILE_NAME = "data/simulacao_teste.base";
	private static final String FILE_NAME = "data/u.data";
	
	private static final double FILE_PERCENTAGE = 0.7;

	private static final int CLUSTER_NUMBER = 3;

	private static final int COL_USER_ID = 0;
	private static final int COL_ITEM_ID = 1;
	private static final int COL_RATTING = 2;

	/* recomendation for user (id - 1) */
	private static final int USER_ID = 420;
	private static final int[] ITEM_IDS = {207, 97, 524, 268, 422, 172, 508, 78, 426, 497};
	private static final int[] ITEM_RATTINGS = {2, 5, 4, 3, 2, 1, 2, 4, 4, 4};

	private static int recomendationCount = 0;
	private static int recomendationErrorCount = 0;

	public static void main(String s[])
	{
		int maxUserId = 0;
		int maxItemId = 0;

		int lineNumer = 0;

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));

			while (in.ready())
			{
				String[] elements = in.readLine().split("\t");

				int userId = Integer.parseInt(elements[COL_USER_ID]);
				if (userId > maxUserId)
					maxUserId = userId;

				int itemId = Integer.parseInt(elements[COL_ITEM_ID]);
				if (itemId > maxItemId)
					maxItemId = itemId;

				lineNumer++;
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("maxUserId: " + maxUserId + " | maxItemId: " + maxItemId);

		int[][] evaluations = new int[maxUserId][maxItemId];
		int[][] postEval = new int[maxUserId][maxItemId];

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(FILE_NAME));

			int i = 0;

			while (in.ready())
			{
				String[] elements = in.readLine().split("\t");

				int userId = Integer.parseInt(elements[COL_USER_ID]) - 1;
				int itemId = Integer.parseInt(elements[COL_ITEM_ID]) - 1;

				//System.out.println("userId: " + userId + " | itemId: " + itemId);

				if (i < lineNumer * FILE_PERCENTAGE)
					evaluations[userId][itemId] = Integer.parseInt(elements[COL_RATTING]);
				else
					postEval[userId][itemId] = Integer.parseInt(elements[COL_RATTING]);

				i++;
			}

			System.out.println("Number of lines read: " + i);

			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		double[][] data = new double[maxUserId][maxUserId];

		for (int i = 0; i < maxUserId; i++)
		{
			for (int j = i + 1; j < maxUserId; j++)
			{
				int count = 0;

				for (int filme = 0; filme < maxItemId; filme++)
				{
					if (evaluations[i][filme] != KMeans.NO_VALUE && evaluations[j][filme] != KMeans.NO_VALUE)
					{
						data[i][j] += Double.valueOf(evaluations[i][filme] + evaluations[j][filme]) / 2;

						count++;
					}
				}

				if (count > 0)
				{
					data[i][j] /= count;
				}

				data[j][i] = data[i][j];
			}
		}

		//System.out.println(MathUtils.showMatrix(data));

		KMeans cluster = new KMeans(CLUSTER_NUMBER, data);

		//System.out.println(MathUtils.showMatrix(cluster.getMeans()));
		//System.out.println("Element number: " + cluster.getAssignments().length);
		System.out.println(MathUtils.showVector(cluster.getAssignments()));

		try
		{
//			BufferedWriter out = new BufferedWriter(new FileWriter("data/recomendation_test.out"));
//			recomendationForUser(USER_ID, ITEM_IDS, ITEM_RATTINGS, ITEM_IDS.length, evaluations, cluster.getAssignments(), out);
//			out.close();

			recomendationCheck(evaluations, postEval, cluster.getAssignments());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void recomendationCheck(int[][] evaluations, int[][] postEval, int[] cAssignment) throws IOException
	{
		BufferedWriter out = new BufferedWriter(new FileWriter("data/recomendation.out"));

		double[] itemGuessMean = new double[postEval[0].length];
		double[] sum = new double[postEval[0].length];
		double[] squareSum = new double[postEval[0].length];

		int[] count = new int[postEval[0].length];

		for (int i = 0; i < itemGuessMean.length; i++)
		{
			itemGuessMean[i] = 0.;
			sum[i] = 0.;
			squareSum[i] = 0.;

			count[i] = 0;
		}

		for (int i = 0; i < postEval.length; i++)
		{
			int[] itemIds = new int[postEval[0].length];
			int[] itemRatting = new int[postEval[0].length];

			int counter = 0;

			for (int j = 0; j < postEval[0].length; j++)
			{
				if (postEval[i][j] != KMeans.NO_VALUE)
				{
					itemIds[counter] = j;
					itemRatting[counter] = postEval[i][j];
					counter++;

					itemGuessMean[j] += postEval[i][j];

					sum[j] += postEval[i][j];
					squareSum[j] += postEval[i][j] * postEval[i][j];

					count[j]++;
				}
			}

			recomendationForUser(i, itemIds, itemRatting, counter, evaluations, cAssignment, out);
		}

		NumberFormat nf = new DecimalFormat("#0.00");

		out.write(" -- STATISTICS --");
		out.write("\n");
		
		double per = Double.valueOf(recomendationErrorCount) / Double.valueOf(recomendationCount) * 100;

		out.write("Good recomendation: " + nf.format(per) + "%\n");
		out.write("\n");
		out.write("\t Item\t Avg\t Var\t\n");

		for (int i = 0; i < itemGuessMean.length; i++)
		{
			StringBuffer sb = new StringBuffer();

			sb.append("\t ").append(i + 1).append("\t");

			if (i < 99)
				sb.append("\t ");
			else
				sb.append(" ");

			double var = 0.;
			
			if (count[i] > 0)
			{
				itemGuessMean[i] /= count[i];

				if (count[i] > 1)
					var = (squareSum[i] - (sum[i] / Double.valueOf(count[i]))) / Double.valueOf(count[i] - 1);
				else
					var = 0.;
			}
			
			sb.append(nf.format(itemGuessMean[i])).append("\t ").append(nf.format(var)).append("\n");

			out.write(sb.toString());
		}

		out.close();
	}

	private static void recomendationForUser(int userId, int[] itemIds, int[] itemRatting, int itemNumber, int[][] evaluations, int[] cAssignment, BufferedWriter out) throws IOException
	{
		int cluster = cAssignment[userId];

		double[] itemGuessMean = new double[itemNumber];
		double[] itemGuessVar = new double[itemNumber];
		double[] guessError = new double[itemNumber];

		NumberFormat nf = new DecimalFormat("#0.00");

		out.write("\n");
		out.write(" -- RECOMENDATION FOR USER --\n");
		out.write("UserId: " + (userId + 1) + " Cluster: " + cluster + "\n");
		out.write("\n");
		out.write("\t ItemId\t Guess\t Ratting\t Variance\t Error\n");

		for (int i = 0; i < itemNumber; i++)
		{
			int counter = 0;
			double sum = 0;
			double squareSum = 0.;

			itemGuessMean[i] = 0.;

			for (int j = 0; j < cAssignment.length; j++)
			{
				if (cAssignment[j] == cluster)
				{
					//out.write("i: " + i + " j: " + j + " itemId: " + ITEM_IDS[j] + " avalSize[i]: " + avaliacoes[i].length + "\n");

					if (evaluations[j][itemIds[i]] != KMeans.NO_VALUE)
					{
						// mean
						itemGuessMean[i] += evaluations[j][itemIds[i]];

						// variance
						sum += evaluations[j][itemIds[i]];
						squareSum += evaluations[j][itemIds[i]] * evaluations[j][itemIds[i]];

						counter++;
					}
				}
			}

			if (counter > 0)
			{
				itemGuessMean[i] /= counter;

				if (counter > 1)
					itemGuessVar[i] = (squareSum - (sum / Double.valueOf(counter))) / Double.valueOf(counter - 1);
				else
					itemGuessVar[i] = 0;
			}

			guessError[i] = itemRatting[i] - itemGuessMean[i];


			// error statistics
			recomendationCount++;
			
			if (guessError[i] >= -1 && guessError[i] <= 1)
				recomendationErrorCount++;


			StringBuffer sb = new StringBuffer();

			sb.append("\t ").append(itemIds[i] + 1).append("\t");

			if (itemIds[i] < 99)
				sb.append("\t ");
			else
				sb.append(" ");

			sb.append(nf.format(itemGuessMean[i])).append("\t ").append(itemRatting[i]);
			sb.append("\t\t\t ").append(nf.format(itemGuessVar[i])).append("\t\t ");
			sb.append(nf.format(guessError[i])).append("\n");

			out.write(sb.toString());

			//out.write("\t " + itemIds[i] +"\t " + nf.format(itemGuessMean[i]) + "\t " + itemRatting[i] + "\t\t\t " + nf.format(itemGuessVar[i]) + "\t\t " + nf.format(guessError[i]) + "\n");
		}

		out.write("\n");
	}
}
