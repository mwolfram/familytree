package at.or.wolfram.mate.familytree.common.ftp;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import at.or.wolfram.mate.familytree.common.Tools;

public class ToolsTest {

	@Test
	public void testFindName() throws URISyntaxException, IOException {
		Assert.assertTrue(Tools.isNameInWhitelist("KÖRMENDI Márta Mária", new String[]{"Körmendi", "Körmendy", "Körmöndi"}));
	}

}