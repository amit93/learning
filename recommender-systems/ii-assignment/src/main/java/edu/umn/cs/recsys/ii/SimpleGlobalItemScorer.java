package edu.umn.cs.recsys.ii;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.grouplens.lenskit.basic.AbstractGlobalItemScorer;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;

/**
 * Global item scorer to find similar items.
 * 
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleGlobalItemScorer extends AbstractGlobalItemScorer
{
	private final SimpleItemItemModel model;

	@Inject
	public SimpleGlobalItemScorer(SimpleItemItemModel mod)
	{
		model = mod;
	}

	/**
	 * Score items with respect to a set of reference items.
	 * 
	 * @param items The reference items.
	 * @param scores The score vector. Its domain is the items to be scored, and the scores should be stored into this
	 *        vector.
	 */
	@Override
	public void globalScore(@Nonnull Collection<Long> items, @Nonnull MutableSparseVector scores)
	{
		scores.fill(0);
		// TODO score items in the domain of scores
		// each item's score is the sum of its similarity to each item in items, if they are
		// neighbors in the model.

		for (VectorEntry entry: scores.fast())
		{
			double score = 0.0;
			long itemToScore = entry.getKey();
			List<ScoredId> neighbors = model.getNeighbors(itemToScore);

			for (ScoredId neighbor: neighbors)
			{
				if (items.contains(neighbor.getId()))
				{
					score = score + neighbor.getScore();// similarity
				}
			}
			scores.set(entry, score);
		}
	}
}
