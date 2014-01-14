package gametree;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ActionTreeTest extends TestCase {
	
	public void testRiverSubGame() {
		ActionTree tree = ActionTree.getRiverCheckOrBetSubTree();
		
		Assert.assertEquals(5, tree.getNumberNodes());		
	}
}
