package info.mmpa.concoction.util;

import java.awt.*;

/**
 * A modified version of {@link FlowLayout} for dynamic vertical component arrangement.
 * The flow direction is determined by the container's <code>componentOrientation</code>
 * property and may be one of two values:
 * <ul>
 * <li><code>ComponentOrientation.TOP_TO_BOTTOM</code>
 * <li><code>ComponentOrientation.BOTTOM_TO_TOP</code>
 * </ul>
 *
 * @see FlowLayout Original layout, but modified to be vertically oriented
 */
public class VerticalFlowLayout implements LayoutManager, java.io.Serializable {
	/**
	 * This value indicates that each row of components should be left-justified.
	 */
	public static final int TOP = 0;

	/**
	 * This value indicates that each row of components should be centered.
	 */
	public static final int CENTER = 1;

	/**
	 * This value indicates that each row of components should be right-justified.
	 */
	public static final int BOTTOM = 2;

	private int align;
	private int hgap;
	private int vgap;

	/**
	 * Constructs a new vertical layout with a centered alignment and a default 5-unit horizontal and vertical gap.
	 */
	public VerticalFlowLayout() {
		this(CENTER, 5, 5);
	}

	/**
	 * Constructs a new vertical layout with the given alignment and a default 5-unit horizontal and vertical gap.
	 *
	 * @param align
	 * 		the alignment value
	 */
	public VerticalFlowLayout(int align) {
		this(align, 5, 5);
	}

	/**
	 * Constructs a new vertical layout with the given alignment and gaps.
	 *
	 * @param align
	 * 		The alignment value
	 * @param hgap
	 * 		The horizontal gap between components
	 * 		and between the components and the
	 * 		borders of the {@link Container}
	 * @param vgap
	 * 		The vertical gap between components
	 * 		and between the components and the
	 * 		borders of the {@link Container}
	 */
	public VerticalFlowLayout(int align, int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
		setAlignment(align);
	}

	/**
	 * @return Current vertical alignment.
	 *
	 * @see #TOP
	 * @see #CENTER
	 * @see #BOTTOM
	 */
	public int getAlignment() {
		return align;
	}

	/**
	 * @param align
	 * 		One of the alignment values supported.
	 *
	 * @see #TOP
	 * @see #CENTER
	 * @see #BOTTOM
	 */
	public void setAlignment(int align) {
		this.align = align;
	}

	/**
	 * @return Current horizontal gap between components and the barrier of the {@link Container}.
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * @param hgap
	 * 		New horizontal gap to put between components and the barrier of the {@link Container}.
	 */
	public void setHgap(int hgap) {
		this.hgap = hgap;
	}


	/**
	 * @return Current vertical gap between components and the barrier of the {@link Container}.
	 */
	public int getVgap() {
		return vgap;
	}

	/**
	 * @param vgap
	 * 		New vertical gap to put between components and the barrier of the {@link Container}.
	 */
	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// no-op
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// no-op
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		synchronized (container.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int numChildren = container.getComponentCount();
			boolean firstVisibleComponent = true;

			for (int i = 0; i < numChildren; i++) {
				Component child = container.getComponent(i);

				if (child.isVisible()) {
					Dimension childSize = child.getPreferredSize();
					dim.width = Math.max(dim.width, childSize.width);

					if (firstVisibleComponent) {
						firstVisibleComponent = false;
					} else {
						dim.height += vgap;
					}

					dim.height += childSize.height;
				}
			}

			Insets insets = container.getInsets();
			dim.width += insets.left + insets.right + hgap * 2;
			dim.height += insets.top + insets.bottom + vgap * 2;
			return dim;
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		synchronized (container.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int numChildren = container.getComponentCount();
			boolean firstVisibleComponent = true;

			for (int i = 0; i < numChildren; i++) {
				Component child = container.getComponent(i);
				if (child.isVisible()) {
					Dimension childSize = child.getMinimumSize();
					dim.width = Math.max(dim.width, childSize.width);

					if (firstVisibleComponent) {
						firstVisibleComponent = false;
					} else {
						dim.height += vgap;
					}

					dim.height += childSize.height;
				}
			}

			Insets insets = container.getInsets();
			dim.width += insets.left + insets.right + hgap * 2;
			dim.height += insets.top + insets.bottom + vgap * 2;
			return dim;
		}
	}

	@Override
	public void layoutContainer(Container container) {
		synchronized (container.getTreeLock()) {
			Insets insets = container.getInsets();
			int maxHeight = container.getHeight() - (insets.top + insets.bottom + vgap * 2);
			int numChildren = container.getComponentCount();
			int x = insets.left + hgap;
			int y = 0;
			int width = container.getWidth();
			int widthPadding = insets.left + insets.right + hgap * 2;

			int columnWidth = 0;
			int start = 0;

			boolean ttb = container.getComponentOrientation().isLeftToRight();

			for (int i = 0; i < numChildren; i++) {
				Component child = container.getComponent(i);

				if (child.isVisible()) {
					Dimension childSize = child.getPreferredSize();
					child.setSize(width - widthPadding, childSize.height);

					if ((y == 0) || ((y + childSize.height) <= maxHeight)) {
						if (y > 0) {
							y += vgap;
						}

						y += childSize.height;
						columnWidth = Math.max(columnWidth, childSize.width);
					} else {
						moveComponents(container, x, insets.top + vgap, columnWidth, maxHeight - y, start, i, ttb);
						y = childSize.height;
						x += hgap + columnWidth;
						columnWidth = childSize.width;
						start = i;
					}
				}
			}

			moveComponents(container, x, insets.top + vgap, columnWidth, maxHeight - y, start, numChildren, ttb);
		}
	}

	/**
	 * Centers the elements in the specified row, if there is any slack.
	 *
	 * @param target
	 * 		the component which needs to be moved
	 * @param x
	 * 		the x coordinate
	 * @param y
	 * 		the y coordinate
	 * @param width
	 * 		the width dimensions
	 * @param height
	 * 		the height dimensions
	 * @param columnStart
	 * 		the beginning of the column
	 * @param columnEnd
	 * 		the ending of the column
	 */
	private void moveComponents(Container target, int x, int y, int width, int height, int columnStart, int columnEnd, boolean ttb) {
		switch (align) {
			case TOP:
				y += ttb ? 0 : height;
				break;
			case CENTER:
				y += height / 2;
				break;
			case BOTTOM:
				y += ttb ? height : 0;
				break;
		}

		for (int i = columnStart; i < columnEnd; i++) {
			Component child = target.getComponent(i);

			if (child.isVisible()) {
				if (ttb) {
					child.setLocation(x, y);
				} else {
					child.setLocation(x, target.getHeight() - y - child.getHeight());
				}

				y += child.getHeight() + vgap;
			}
		}
	}

	@Override
	public String toString() {
		String str = "";
		switch (align) {
			case TOP:
				str = ",align=top";
				break;
			case CENTER:
				str = ",align=center";
				break;
			case BOTTOM:
				str = ",align=bottom";
				break;
		}
		return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + str + "]";
	}
}