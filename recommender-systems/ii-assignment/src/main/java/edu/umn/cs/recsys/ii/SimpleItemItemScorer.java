package edu.umn.cs.recsys.ii;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.grouplens.lenskit.basic.AbstractItemScorer;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.history.History;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemScorer extends AbstractItemScorer
{
	private final SimpleItemItemModel model;
	private final UserEventDAO userEvents;
	private final int neighborhoodSize;
	private static final Logger logger = LoggerFactory.getLogger(SimpleItemItemScorer.class);

	@Inject
	public SimpleItemItemScorer(SimpleItemItemModel m, UserEventDAO dao, @NeighborhoodSize int nnbrs)
	{
		model = m;
		userEvents = dao;
		neighborhoodSize = nnbrs;
	}

	/**
	 * Score items for a user.
	 * 
	 * @param user The user ID.
	 * @param scores The score vector. Its key domain is the items to score, and the scores (rating predictions) should
	 *        be written back to this vector.
	 */
	@Override
	public void score(long user, @Nonnull MutableSparseVector scores)
	{
		SparseVector ratings = getUserRatingVector(user);

		// TODO Score this item and save the score into scores
		for (VectorEntry e: scores.fast(VectorEntry.State.EITHER))
		{
			long item = e.getKey();
			List<ScoredId> neighbors = model.getNeighbors(item);

			double numerator = 0.0;
			double denominator = 0.0;

			// find most similar ones
			logger.trace("Total neighbors with non-negative similarity {}", neighbors.size());
			logger.trace("Neighbor scores for user:item {}:{}", user, item);

			int count = 1;
			for (ScoredId neighbor: neighbors)
			{
				if (ratings.containsKey(neighbor.getId()) && count <= neighborhoodSize && count <= neighbors.size())
				{
					logger.trace("{}", neighbor);

					numerator = numerator + ratings.get(neighbor.getId()) * neighbor.getScore();
					denominator = denominator + Math.abs(neighbor.getScore());

					count++;
				}
			}

			double prediction = numerator / denominator;
			scores.set(e, prediction);
		}
	}

	/**
	 * Get a user's ratings.
	 * 
	 * @param user The user ID.
	 * @return The ratings to retrieve.
	 */
	private SparseVector getUserRatingVector(long user)
	{
		UserHistory<Rating> history = userEvents.getEventsForUser(user, Rating.class);
		if (history == null)
		{
			history = History.forUser(user);
		}

		return RatingVectorUserHistorySummarizer.makeRatingVector(history);
	}
}
