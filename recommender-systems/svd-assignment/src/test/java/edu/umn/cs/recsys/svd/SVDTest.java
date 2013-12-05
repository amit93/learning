package edu.umn.cs.recsys.svd;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import static org.junit.Assert.assertEquals;

public class SVDTest
{
	@Test
	public void globalMeanPrediction1024()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(393l, "3.6839");
		expected.put(36955l, "3.2596");
		expected.put(77l, "4.0733");
		expected.put(268l, "3.1743");
		expected.put(462l, "3.8826");

		String[] input = { "1024:77", "1024:268", "1024:462", "1024:393", "1024:36955"};
		Map<Long, String> actual = SVDMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void globalMeanPrediction2048()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(788l, "4.1358");
		expected.put(36955l, "4.0373");
		expected.put(77l, "3.8746");

		String[] input = { "2048:77", "2048:36955", "2048:788"};

		Map<Long, String> actual = SVDMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void userMeanPrediction1024()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(393l, "3.4828");
		expected.put(36955l, "3.1081");
		expected.put(77l, "4.0812");
		expected.put(268l, "3.2358");
		expected.put(462l, "3.6925");

		String[] input = { "--user-mean", "1024:77", "1024:268", "1024:462", "1024:393", "1024:36955"};
		Map<Long, String> actual = SVDMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void userMeanPrediction2048()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(788l, "4.1667");
		expected.put(36955l, "4.0806");
		expected.put(77l, "4.1860");

		String[] input = { "--user-mean", "2048:77", "2048:36955", "2048:788"};

		Map<Long, String> actual = SVDMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void itemMeanPrediction1024()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(393l, "3.4858");
		expected.put(36955l, "2.8705");
		expected.put(77l, "4.3499");
		expected.put(268l, "3.0809");
		expected.put(462l, "3.6878");

		String[] input = { "--item-mean", "1024:77", "1024:268", "1024:462", "1024:393", "1024:36955"};
		Map<Long, String> actual = SVDMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void itemMeanPrediction2048()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(788l, "4.1674");
		expected.put(36955l, "4.0488");
		expected.put(77l, "3.8812");

		String[] input = { "--item-mean", "2048:77", "2048:36955", "2048:788"};

		Map<Long, String> actual = SVDMain.predict(input);

		equalMaps(expected, actual);
	}

	public static void equalMaps(Map<Long, String> expected, Map<Long, String> actual)
	{
		assertEquals(expected.size(), actual.size());

		for (Long key: expected.keySet())
		{
			assertEquals(expected.get(key), actual.get(key));
		}
	}
}
