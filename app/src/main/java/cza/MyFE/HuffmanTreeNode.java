package cza.MyFE;

public class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {
	public int code;
	public int freq;
	public int id;
	public HuffmanTreeNode left;
	public HuffmanTreeNode right;
	public HuffmanTreeNode parent;

	@Override
	public int compareTo(HuffmanTreeNode node) {
		return freq - node.freq;
	}

	public void bindLeft(HuffmanTreeNode node){
		node.parent = this;
		node.id = 0;
		left = node;
	}

	public void bindRight(HuffmanTreeNode node){
		node.parent = this;
		node.id = 1;
		right = node;
	}

	public void removeFromParent(){
		if (id == 0)
			parent.left = null;
		else 
			parent.right = null;
		System.gc();
	}

	public boolean isLeaf(){
		return left == null && right == null;
	}
}
