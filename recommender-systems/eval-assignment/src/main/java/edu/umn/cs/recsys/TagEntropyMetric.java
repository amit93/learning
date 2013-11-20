package edu.umn.cs.recsys;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.eval.algorithm.AlgorithmInstance;
import org.grouplens.lenskit.eval.data.traintest.TTDataSet;
import org.grouplens.lenskit.eval.metrics.AbstractTestUserMetric;
import org.grouplens.lenskit.eval.metrics.TestUserMetricAccumulator;
import org.grouplens.lenskit.eval.metrics.topn.ItemSelectors;
import org.grouplens.lenskit.eval.traintest.TestUser;
import org.grouplens.lenskit.scored.ScoredId;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umn.cs.recsys.dao.ItemTagDAO;

/**
 * A metric that measures the tag entropy of the recommended items.
 * 
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class TagEntropyMetric extends AbstractTestUserMetric
{
	private final int listSize;
	private final List<String> columns;

	/**
	 * Construct a new tag entropy metric.
	 * 
	 * @param nitems The number of items to request.
	 */
	public TagEntropyMetric(int nitems)
	{
		listSize = nitems;
		// initialize column labels with list length
		columns = ImmutableList.of(String.format("TagEntropy@%d", nitems));
	}

	/**
	 * Make a metric accumulator. Metrics operate with <em>accumulators</em>, which are created for each algorithm and
	 * data set. The accumulator measures each user's output, and accumulates the results into a global statistic for
	 * the whole evaluation.
	 * 
	 * @param algorithm The algorithm being tested.
	 * @param data The data set being tested with.
	 * @return An accumulator for analyzing this algorithm and data set.
	 */
	@Override
	public TestUserMetricAccumulator makeAccumulator(AlgorithmInstance algorithm, TTDataSet data)
	{
		return new TagEntropyAccumulator();
	}

	/**
	 * Return the labels for the (global) columns returned by this metric.
	 * 
	 * @return The labels for the global columns.
	 */
	@Override
	public List<String> getColumnLabels()
	{
		return columns;
	}

	/**
	 * Return the lables for the per-user columns returned by this metric.
	 */
	@Override
	public List<String> getUserColumnLabels()
	{
		// per-user and global have the same fields, they just differ in aggregation.
		return columns;
	}

	private class TagEntropyAccumulator implements TestUserMetricAccumulator
	{
		private double totalEntropy = 0;
		private int userCount = 0;
		private final Map<Long, Set<String>> CACHE = Maps.newConcurrentMap();

		/**
		 * Evaluate a single test user's recommendations or predictions.
		 * 
		 * @param testUser The user's recommendation result.
		 * @return The values for the per-user columns.
		 */
		@Nonnull
		@Override
		public Object[] evaluate(TestUser testUser)
		{
			List<ScoredId> recommendations = testUser.getRecommendations(listSize, ItemSelectors.allItems(),
					ItemSelectors.trainingItems());
			if (recommendations == null)
			{
				return new Object[1];
			}
			LenskitRecommender lkrec = (LenskitRecommender) testUser.getRecommender();
			ItemTagDAO tagDAO = lkrec.get(ItemTagDAO.class);

			double entropy = 0.0;

			// TODO Implement the entropy metric
			// https://class.coursera.org/recsys-001/forum/thread?thread_id=1003
			if (recommendations.size() > 0)
			{
				Set<String> normTagsForRecommendationsForThisUser = getNormalizedTags(recommendations, tagDAO);

				for (String tag: normTagsForRecommendationsForThisUser)
				{
					double probOfTagInRecommendations = 0.0;

					for (ScoredId movie: recommendations)
					{
						Set<String> normalizedTagsForMovie = getNormalizedTags(movie, tagDAO);

						if (normalizedTagsForMovie.contains(tag))
						{
							double probOfTagInMovie = 1.0 / normalizedTagsForMovie.size();
							probOfTagInRecommendations = probOfTagInRecommendations + probOfTagInMovie;
						}
					}
					probOfTagInRecommendations = probOfTagInRecommendations / recommendations.size();

					// entropy in nats
					entropy = entropy - probOfTagInRecommendations * Math.log(probOfTagInRecommendations);
				}

				// entropy in bits
				entropy = entropy / Math.log(2.0);
			}
			// end TODO

			// overall entropy for all users on this partition
			totalEntropy += entropy;

			// number of users for averaging the entropy for this partition
			userCount += 1;

			return new Object[]{ entropy};
		}

		private Set<String> getNormalizedTags(List<ScoredId> recommendations, ItemTagDAO tagDAO)
		{
			Set<String> normalizedTags = Sets.newTreeSet();
			for (ScoredId recommendedMovie: recommendations)
			{
				normalizedTags.addAll(getNormalizedTags(recommendedMovie, tagDAO));

			}
			return normalizedTags;
		}

		private Set<String> getNormalizedTags(ScoredId movie, ItemTagDAO tagDAO)
		{
			if (CACHE.containsKey(movie.getId()))
			{
				return CACHE.get(movie.getId());
			}
			else
			{

				Set<String> normalizedTags = Sets.newHashSet();
				for (String raw: tagDAO.getItemTags(movie.getId()))
				{
					normalizedTags.add(raw.trim().toLowerCase());
				}
				CACHE.put(movie.getId(), normalizedTags);
				return normalizedTags;
			}
		}

		/**
		 * Get the final aggregate results. This is called after all users have been evaluated, and returns the values
		 * for the columns in the global output.
		 * 
		 * @return The final, aggregated columns.
		 */
		@Nonnull
		@Override
		public Object[] finalResults()
		{
			// return a single field, the average entropy
			return new Object[]{ totalEntropy / userCount};
		}
	}
}
