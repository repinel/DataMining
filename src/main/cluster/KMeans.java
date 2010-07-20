//
//  KMeans.java
//  blobby
//
//  Created by David Gavilan on 10/30/06.
//
package main.cluster;

import main.math.MathUtils;


public class KMeans
{
	/** Maximum number of iterations */
	public static final int MAXITERS = 100;

	/** the number of clusters */
	private int K;

	/** the assignments of each element to its class */
	private int[] cAssignment;

	/** the cluster centers */
	private double[][] mu;

	public KMeans(int k, double[][] data)
	{
		this(k, data, null);
	}

	public KMeans(int k, double[][] data, int[] initialAssignment)
	{
		this.K = k;
		int n = data.length;
		int d = data[0].length;
		mu = new double[k][d];

		if (initialAssignment == null)
		{
			cAssignment = new int[n];
			randomInit();
		}
		else
		{
			cAssignment = initialAssignment;
		}

		cluster(data);
	}

	public int[] getAssignments()
	{
		return cAssignment;
	}

	public double[][] getMeans()
	{
		return mu;
	}

	private void randomInit()
	{
		for (int i = 0; i < cAssignment.length; i++)
		{
			cAssignment[i] = (int) Math.floor(Math.random() * K) + 1;
		}
	}

	private void cluster(double[][] data)
	{
		int n = data.length;
		int d = data[0].length;

		int e = 1;
		int iter = 0;
		int[] count = new int[K];
		double[] dist = new double[K];

		while (e > 0 && iter < MAXITERS)
		{
			// compute the means and the distances
			for (int i = 0; i < K; i++)
			{
				count[i] = 0;
				for (int v = 0; v < d; v++)
					mu[i][v] = 0.;
			}
			for (int j = 0; j < n; j++)
			{
				int member = cAssignment[j];
				if (member > 0)
				{
					count[member - 1]++;
					for (int v = 0; v < d; v++)
						mu[member - 1][v] += data[j][v];
				}
			}
			for (int i = 0; i < K; i++)
			{
				if (count[i] > 0)
					for (int v = 0; v < d; v++)
						mu[i][v] /= (double) count[i];
			}

			//System.out.println(MathUtils.showMatrix(mu));
			// dist(1:n,i)=sum((X-ones(n, 1)*mu(i,:)).^2,2);
			// argmin
			// [Y,I]=min(dist,[],2);
			// e=n-sum(C==I); C=I;
			e = 0;
			for (int j = 0; j < n; j++)
			{
				for (int i = 0; i < K; i++)
				{
					dist[i] = 0.;
					for (int v = 0; v < d; v++)
						dist[i] += (data[j][v] - mu[i][v]) * (data[j][v] - mu[i][v]);
				}

				// min
				int cluster = 1;
				double min = dist[cluster - 1];
				for (int i = 1; i < K; i++)
				{
					if (dist[i] < min)
					{
						cluster = i + 1;
						min = dist[i];
					}
				}

				if (cAssignment[j] != cluster)
					e++;

				cAssignment[j] = cluster;
			}

			iter++;
		}

		System.out.println("Iterated " + iter + " times.");
	}
}
