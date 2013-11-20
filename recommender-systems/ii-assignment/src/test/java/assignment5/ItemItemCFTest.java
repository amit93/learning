package assignment5;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import edu.umn.cs.recsys.ii.IIMain;

import static org.junit.Assert.assertEquals;

public class ItemItemCFTest
{
	Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void prediction1024()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(462l, "2.9900");
		expected.put(393l, "3.7702");
		expected.put(36955l, "2.5612");
		expected.put(77l, "4.1968");
		expected.put(268l, "2.3366");

		String[] input = { "1024:77", "1024:268", "1024:462", "1024:393", "1024:36955"};
		Map<Long, String> actual = IIMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void prediction2048()
	{
		Map<Long, String> expected = Maps.newHashMap();
		expected.put(788l, "4.1253");
		expected.put(36955l, "3.8545");
		expected.put(77l, "4.5102");

		String[] input = { "2048:77", "2048:36955", "2048:788"};

		Map<Long, String> actual = IIMain.predict(input);

		equalMaps(expected, actual);
	}

	@Test
	public void similarItems77()
	{
		Map<Long, String> expected = Maps.newHashMap();

		expected.put(550l, "0.3192");
		expected.put(629l, "0.3078");
		expected.put(38l, "0.2574");
		expected.put(278l, "0.2399");
		expected.put(680l, "0.2394");

		String[] input = { "77"};

		Map<Long, String> actual = IIMain.basket(input);

		equalMaps(expected, actual);
	}

	@Test
	public void similarItems77and680()
	{
		Map<Long, String> expected = Maps.newHashMap();

		expected.put(550l, "0.6941");
		expected.put(629l, "0.5984");
		expected.put(238l, "0.5361");
		expected.put(278l, "0.5166");
		expected.put(274l, "0.4916");

		String[] input = { "77", "680"};

		Map<Long, String> actual = IIMain.basket(input);

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
