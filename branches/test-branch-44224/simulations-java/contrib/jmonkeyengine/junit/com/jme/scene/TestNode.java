package com.jme.scene;

public class TestNode extends junit.framework.TestCase {
    private Node root;

    @Override
    protected void setUp() throws Exception {
        root = new Node("root");
    }

    public void testInit() {
        assertEquals(0, root.getQuantity());
        assertEquals(0, root.getVertexCount());
        assertEquals(0, root.getTriangleCount());
        assertNull(root.getParent());
        assertNull(root.getChild(5));
        assertNull(root.getChild("test"));
        assertEquals("root", root.getName());
    }

    public void testHierarchy() {
        Node child1 = new Node("child1");
        Node child2 = new Node("child2");
        Node child3 = new Node("child3");

        root.attachChild(child1);
        root.attachChild(child2);
        root.attachChild(child3);
        assertEquals(3, root.getQuantity());

        assertEquals(child2, root.getChild(1));
        assertEquals(child2, root.getChild("child2"));
        assertEquals(root, child1.getParent());

        Spatial s = root.detachChildAt(2);
        assertEquals(child3, s);
        assertEquals(2, root.getQuantity());
        int index = root.detachChild(child2);
        assertEquals(1, index);
        assertEquals(1, root.getQuantity());
        index = root.detachChildNamed("not a child");
        assertEquals(-1, index);
        assertEquals(1, root.getQuantity());
        index = root.detachChildNamed("child1");
        assertEquals(0, index);
        assertEquals(0, root.getQuantity());

        root.attachChild(child1);
        root.attachChild(child2);
        root.attachChildAt(child3, 1);
        assertEquals(3, root.getQuantity());
        assertEquals(child3, root.getChild(1));
        assertEquals(2, root.getChildIndex(child2));

        root.detachAllChildren();
        assertEquals(0, root.getQuantity());
    }
}
