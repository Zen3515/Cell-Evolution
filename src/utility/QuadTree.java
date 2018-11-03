package utility;

import java.util.ArrayList;

import sharedobject.Collidable;

public class QuadTree {

	private static int MAX_OBJECTS = 15;
	private static int MAX_LEVELS = 3;

	private int level;
	private ArrayList<Collidable> objects;
	private Rectangle bounds;
	private QuadTree[] nodes;

	/*
	 * Constructor
	 */
	public QuadTree(int pLevel, Rectangle pBounds) {
		level = pLevel;
		objects = new ArrayList<Collidable>();
		bounds = pBounds;

//		GameScreenCanvas.getCanvasLayer()[4].getGraphicsContext2D().strokeRect(pBounds.x, pBounds.y, pBounds.width,	pBounds.height);

		// System.out.println("Bound at " + pBounds.x + ", " + pBounds.y + ", w&h = " +
		// pBounds.width + ", " + pBounds.height);
		nodes = new QuadTree[4];
	}

	/*
	 * Clears the quadtree
	 */
	public void clear() {
		objects.clear();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}

	/*
	 * Splits the node into 4 subnodes
	 */
	private void split() {
		int subWidth = (int) (bounds.width / 2);
		int subHeight = (int) (bounds.height / 2);
		int x = (int) bounds.x;
		int y = (int) bounds.y;

		// System.out.println("spliting to level = " + (level+1) + ", AT " + x + ", " +
		// y);

		nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
		nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
		nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
		nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}

	/*
	 * Determine which node the object belongs to. -1 means object cannot completely
	 * fit within a child node and is part of the parent node
	 */
	private int getIndex(Collidable pRect) {
		int index = -1;
		double verticalMidpoint = bounds.x + (bounds.width / 2);
		double horizontalMidpoint = bounds.y + (bounds.height / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (pRect.getBound().getMinY() < horizontalMidpoint
				&& /*pRect.getBound().getMinY() + pRect.getBound().getHeight()*/ pRect.getBound().getMaxY() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (pRect.getBound().getMinY() > horizontalMidpoint);

		// Object can completely fit within the left quadrants
		if (pRect.getBound().getMinX() < verticalMidpoint && /*pRect.getBound().getMinX() + pRect.getBound().getWidth()*/ pRect.getBound().getMaxX() < verticalMidpoint) {
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		}
		// Object can completely fit within the right quadrants
		else if (pRect.getBound().getMinX() > verticalMidpoint) {
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}

		return index;
	}

	// /*
	// * Determine which node the object belongs to. -1 means object cannot
	// completely
	// * fit within a child node and is part of the parent node
	// */
	// private int getIndex(Collidable pRect) {
	// int index = -1;
	// double verticalMidpoint = bounds.x + (bounds.width / 2);
	// double horizontalMidpoint = bounds.y + (bounds.height / 2);
	//
	// // Object can completely fit within the top quadrants
	// boolean topQuadrant = (pRect.getBound().getMaxY() <= horizontalMidpoint &&
	// pRect.getBound().getMaxY() >= bounds.y);
	// // Object can completely fit within the bottom quadrants
	// boolean bottomQuadrant = (pRect.getBound().getMinY() >= horizontalMidpoint &&
	// pRect.getBound().getMaxY() >= (bounds.y + bounds.height));
	//
	// // Object can completely fit within the left quadrants
	// if (pRect.getBound().getMaxX() < verticalMidpoint &&
	// pRect.getBound().getMinX() > bounds.x) {
	// if (topQuadrant) {
	// index = 1;
	// } else if (bottomQuadrant) {
	// index = 2;
	// }
	// }
	// // Object can completely fit within the right quadrants
	// else if (pRect.getBound().getMinX() > verticalMidpoint &&
	// pRect.getBound().getMaxX() < (bounds.x + bounds.width)) {
	// if (topQuadrant) {
	// index = 0;
	// } else if (bottomQuadrant) {
	// index = 3;
	// }
	// }
	//
	// return index;
	// }

	/*
	 * Insert the object into the quadtree. If the node exceeds the capacity, it
	 * will split and add all objects to their corresponding nodes.
	 */
	public void insert(Collidable pRect) {
		if (nodes[0] != null) {
			int index = getIndex(pRect);

			if (index != -1) {
				nodes[index].insert(pRect);

				return;
			}
		}

		objects.add(pRect);

		if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
			if (nodes[0] == null) {
				split();
			}

			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i));
				if (index != -1) {
					nodes[index].insert(objects.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	/*
	 * Return all objects that could collide with the given object
	 */
	public ArrayList<Collidable> retrieve(ArrayList<Collidable> returnObjects, Collidable pRect) {
		int index = getIndex(pRect);
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, pRect);
		}

		returnObjects.addAll(objects);

		return returnObjects;
	}
}

// import java.util.ArrayList;
// import java.util.List;
// import javafx.animation.AnimationTimer;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.RegionBuilder;
// import javafx.scene.paint.Color;
// import viwofx.sprit.Sprite;
// import viwofx.ui.GameScene;
//
// public class QuadTree {
//
// private int MAX_OBJECTS = 10;
// private int MAX_LEVELS = 5;
// private int level;
// private ArrayList<Sprite> sprites;
// private ArrayList<Sprite> unAllocatedSprites;
// private Region bounds;
// private QuadTree[] nodes;
// private QuadTree parent;
// private AnimationTimer detection;
// private boolean detecting = false;
//
// private QuadTree getqt() {
// return this;
// }
//
// public QuadTree(QuadTree p, int pLevel, Region pBounds)
// {
// this.parent = p;
// level = pLevel;
// sprites = new ArrayList<>(0);
// unAllocatedSprites = new ArrayList<>(0);
// bounds = pBounds;
// nodes = new QuadTree[4];
//
// detection = new AnimationTimer()
// {
// @Override
// public void handle(long l)
// {
// // This for happens when this node has child nodes and there is some object
// which can not fit whitin the bounds of child nodes
// // these object being checked till they can fit inside the bounds of child
// nodes then they will be added to correspinding child node,
// // or object is out of bounds then it will be pushed to the parent node
// for (int i = 0; i < unAllocatedSprites.size(); i++)
// {
// if (!isInside(unAllocatedSprites.get(i)))
// {
//
// pushToParent(unAllocatedSprites.get(i));
// continue;
// }
// int index = getIndex(unAllocatedSprites.get(i));
// if (index != -1)
// {
// nodes[index].add(unAllocatedSprites.remove(i));
// }
// }
// for (int i = 0; i < sprites.size(); i++)
// {
// Sprite ts = sprites.get(i);
// if (isInside(ts))
// {
// int ii = 0;
// for (ii = 0; ii < sprites.size(); ii++)
// {
// Sprite ts2 = sprites.get(ii);
// if (ts != ts2)
// {
// Your collision detection logic
// }
// }
// if (parent != null)
// {
// for (ii = 0; ii < parent.getUnAllocatedSprites().size(); ii++)
// {
// Sprite ts2 = parent.getUnAllocatedSprites().get(ii);
// if (ts != ts2 && isInside(ts2))
// {
// Your collision detection logic
// }
// }
// }
// }
// else
// {
// pushToParent(ts);
// }
// }
// }
// };
// }
//
// public int getLevel() {
// return level;
// }
//
// public ArrayList<Sprite> getUnAllocatedSprites() {
// return unAllocatedSprites;
// }
//
// // Split the node into 4 subnodes
// private void split() {
// double subWidth = (bounds.getPrefWidth() / 2);
// double subHeight = (bounds.getPrefHeight() / 2);
// double x = bounds.getLayoutX();
// double y = bounds.getLayoutY();
//
// nodes[0] = new QuadTree(this, level + 1,
// RegionBuilder.create().layoutX(x).layoutY(y).prefWidth(subWidth).prefHeight(subHeight).build());
// nodes[1] = new QuadTree(this, level + 1, RegionBuilder.create().layoutX(x +
// subWidth).layoutY(y)
// .prefWidth(subWidth).prefHeight(subHeight).build());
// nodes[2] = new QuadTree(this, level + 1,
// RegionBuilder.create().layoutX(x).layoutY(y + subHeight)
// .prefWidth(subWidth).prefHeight(subHeight).build());
// nodes[3] = new QuadTree(this, level + 1, RegionBuilder.create().layoutX(x +
// subWidth).layoutY(y + subHeight)
// .prefWidth(subWidth).prefHeight(subHeight).build());
// }
//
// private int getIndex(Sprite s) {
// int index = -1;
//
// double verticalMidpoint = bounds.getLayoutX() + (bounds.getPrefWidth() / 2);
// double horizontalMidpoint = bounds.getLayoutY() + (bounds.getPrefHeight() /
// 2);
// double spriteMaxX = (s.getNode().getTranslateX() + s.width);
// double spriteMaxY = (s.getNode().getTranslateY() + s.height);
//
// // Object can completely fit within the top quadrants
// boolean topQuadrant = (spriteMaxY < horizontalMidpoint);
// // Object can completely fit within the bottom quadrants
// boolean bottomQuadrant = (s.getNode().getTranslateY() >= horizontalMidpoint);
//
// // Object can completely fit within the left quadrants
// if (s.getNode().getTranslateX() >= bounds.getLayoutX() && spriteMaxX <
// verticalMidpoint) {
// if (topQuadrant) {
// index = 0;
// } else if (bottomQuadrant) {
// index = 2;
// }
// }
// // Object can completely fit within the right quadrants
// else if (s.getNode().getTranslateX() >= verticalMidpoint
// && (s.getNode().getTranslateX() + s.width) < (bounds.getLayoutX() +
// bounds.getPrefWidth())) {
// if (topQuadrant) {
// index = 1;
// } else if (bottomQuadrant) {
// index = 3;
// }
// }
//
// return index;
// }
//
// public boolean isInside(Sprite s) {
// double maxX = bounds.getLayoutX() + bounds.getPrefWidth();
// double maxY = bounds.getLayoutY() + bounds.getPrefHeight();
//
// // Object can completely fit within the left quadrants
// if (s.getNode().getTranslateX() >= bounds.getLayoutX() &&
// (s.getNode().getTranslateX() + s.width) < maxX
// && s.getNode().getTranslateY() >= bounds.getLayoutY()
// && (s.getNode().getTranslateY() + s.height) < maxY) {
// return true;
// }
// if (parent != null && parent.getUnAllocatedSprites().contains(s)) {
// return true;
// }
// return false;
//
// }
//
// public void pushToParent(Sprite s) {
// sprites.remove(s);
// unAllocatedSprites.remove(s);
// if (parent == null) {
//
// // System.out.println("parent");
// if (!unAllocatedSprites.contains(s)) {
// unAllocatedSprites.add(s);
// }
// return;
// }
//
// parent.add(s);
// if (sprites.size() < 1 && unAllocatedSprites.size() < 1) {
// stopDetection();
// }
// }
//
// public void add(viwofx.sprit.Sprite sprite) {
// // if sprite is not fit in the bounds of node, it will be pushed to the
// parent
// // node.
// // this is a optimization for when child node push a object to this node and
// // object still is not fit in the bounds this node,
// // so it will be pushed to the parent node till object can be fited whitin
// the
// // node bounds
// // this if prevent of out of bounds object to being added to
// unAllocatedSprites
// // and then being pushed to parent
// if (!isInside(sprite)) {
// pushToParent(sprite);
// return;
// }
// // if tree has been splited already add sprite to corrosponding child
// if (nodes[0] != null) {
// int index = getIndex(sprite);
// if (index != -1) {
// nodes[index].add(sprite);
// return;
// } else {
// unAllocatedSprites.add(sprite);
// return;
// }
// }
//
// sprites.add(sprite);
// if (!detecting) {
// startDetection();
// }
//
// if (sprites.size() > MAX_OBJECTS && level < MAX_LEVELS) {
// if (nodes[0] == null) {
// split();
// }
// int i = 0;
// while (i < sprites.size()) {
// int index = getIndex(sprites.get(i));
// if (index != -1) {
// nodes[index].add(sprites.remove(i));
// } else {
// unAllocatedSprites.add(sprites.remove(i));
// }
// }
// }
// }
//
// public List<Sprite> retrieve(List<Sprite> returnObjects, Sprite pRect) {
// int index = getIndex(pRect);
// if (index != -1 && nodes[0] != null) {
// nodes[index].retrieve(returnObjects, pRect);
// }
// returnObjects.addAll(sprites);
// return returnObjects;
// }
//
// public void startDetection() {
// detecting = true;
// detection.start();
// }
//
// public void stopDetection() {
// // detecting = false;
// // detection.stop();
// }
// }