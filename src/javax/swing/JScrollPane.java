/*
 * @(#)JScrollPane.java	1.86 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.accessibility.*;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Point;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * Provides a scrollable view of a lightweight component.
 * A <code>JScrollPane</code> manages a viewport, optional
 * vertical and horizontal scroll bars, and optional row and
 * column heading viewports.
 * You can find task-oriented documentation of <code>JScrollPane</code> in
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/scrollpane.html">How to Use Scroll Panes</a>,
 * a section in <em>The Java Tutorial</em>.  Note that
 * <code>JScrollPane</code> does not support heavyweight components.
 * <p>
 * <TABLE ALIGN="RIGHT" BORDER="0">
 *    <TR>
 *    <TD ALIGN="CENTER">
 *      <P ALIGN="CENTER"><IMG SRC="doc-files/JScrollPane-1.gif" WIDTH="256" HEIGHT="248" ALIGN="BOTTOM" BORDER="0">
 *    </TD>
 *    </TR>
 * </TABLE>
 * The <code>JViewport</code> provides a window,
 * or &quot;viewport&quot; onto a data 
 * source -- for example, a text file. That data source is the 
 * &quot;scrollable client&quot; (aka data model) displayed by the 
 * <code>JViewport</code> view.
 * A <code>JScrollPane</code> basically consists of <code>JScrollBar</code>s,
 * a <code>JViewport</code>, and the wiring between them,
 * as shown in the diagram at right. 
 * <p>
 * In addition to the scroll bars and viewport,
 * a <code>JScrollPane</code> can have a
 * column header and a row header. Each of these is a
 * <code>JViewport</code> object that
 * you specify with <code>setRowHeaderView</code>,
 * and <code>setColumnHeaderView</code>.
 * The column header viewport automatically scrolls left and right, tracking
 * the left-right scrolling of the main viewport.
 * (It never scrolls vertically, however.)
 * The row header acts in a similar fashion.
 * <p>
 * By default, the corners are empty.
 * You can put a component into a corner using 
 * <code>setCorner</code>,
 * in case you there is some function or decoration you
 * would like to add to the scroll pane. The size of corner components is
 * entirely determined by the size of the headers and scroll bars that
 * surround them.
 * <p>
 * To add a border around the main viewport,
 * you can use <code>setViewportBorder</code>. 
 * (Of course, you can also add a border around the whole scroll pane using
 * <code>setBorder</code>.)
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JScrollPane">JScrollPane</a> 
 * key assignments.
 * <p>
 * A common operation to want to do is to set the background color that will
 * be used if the main viewport view is smaller than the viewport, or is
 * not opaque. This can be accomplished by setting the background color
 * of the viewport, via <code>scrollPane.getViewport().setBackground()</code>.
 * The reason for setting the color of the viewport and not the scrollpane
 * is that by default <code>JViewport</code> is opaque
 * which, among other things, means it will completely fill
 * in its background using its background color.  Therefore when
 * <code>JScrollPane</code> draws its background the viewport will
 * usually draw over it.
 * <p>
 * By default <code>JScrollPane</code> uses <code>ScrollPaneLayout</code>
 * to handle the layout of its child Components. <code>ScrollPaneLayout</code>
 * determines the size to make the viewport view in one of two ways:
 * <ol>
 *   <li>If the view implements <code>Scrollable</code>
 *       a combination of <code>getPreferredScrollableViewportSize</code>,
 *       <code>getScrollableTracksViewportWidth</code> and
 *       <code>getScrollableTracksViewportHeight</code>is used, otherwise
 *   <li><code>getPreferredSize</code> is used.
 * </ol>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @see JScrollBar
 * @see JViewport
 * @see ScrollPaneLayout
 * @see Scrollable
 * @see Component#getPreferredSize
 * @see #setViewportView
 * @see #setRowHeaderView
 * @see #setColumnHeaderView
 * @see #setCorner
 * @see #setViewportBorder
 * 
 * @beaninfo
 *     attribute: isContainer true
 *     attribute: containerDelegate getViewport
 *   description: A specialized container that manages a viewport, optional scrollbars and headers
 *
 * @version 1.79 09/01/00
 * @author Hans Muller
 */
public class JScrollPane extends JComponent implements ScrollPaneConstants, Accessible
{
    private Border viewportBorder;

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ScrollPaneUI";

    /** 
     * The display policy for the vertical scrollbar.
     * The default is <code>JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED</code>.
     * @see #setVerticalScrollBarPolicy
     */
    protected int verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED;


    /**
     * The display policy for the horizontal scrollbar.
     * The default is <code>JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED</code>.
     * @see #setHorizontalScrollBarPolicy
     */
    protected int horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED;


    /** 
     * The scrollpane's viewport child.  Default is an empty 
     * <code>JViewport</code>.
     * @see #setViewport
     */
    protected JViewport viewport;


    /**
     * The scrollpane's vertical scrollbar child. 
     * Default is a <code>JScrollBar</code>.
     * @see #setVerticalScrollBar
     */
    protected JScrollBar verticalScrollBar;


    /**
     * The scrollpane's horizontal scrollbar child. 
     * Default is a <code>JScrollBar</code>.
     * @see #setHorizontalScrollBar
     */
    protected JScrollBar horizontalScrollBar;


    /** 
     * The row header child.  Default is <code>null</code>.
     * @see #setRowHeader
     */
    protected JViewport rowHeader;


    /** 
     * The column header child.  Default is <code>null</code>.
     * @see #setColumnHeader
     */
    protected JViewport columnHeader;


    /**
     * The component to display in the lower left corner.  
     * Default is <code>null</code>.
     * @see #setCorner
     */
    protected Component lowerLeft;


    /**
     * The component to display in the lower right corner.  
     * Default is <code>null</code>.
     * @see #setCorner
     */
    protected Component lowerRight;


    /**
     * The component to display in the upper left corner. 
     * Default is <code>null</code>.
     * @see #setCorner
     */
    protected Component upperLeft;


    /**
     * The component to display in the upper right corner.  
     * Default is <code>null</code>.
     * @see #setCorner
     */
    protected Component upperRight;

    /*
     * State flag for mouse wheel scrolling
     */
    private boolean wheelScrollState = true;

    /**
     * Creates a <code>JScrollPane</code> that displays the view
     * component in a viewport
     * whose view position can be controlled with a pair of scrollbars.
     * The scrollbar policies specify when the scrollbars are displayed, 
     * For example, if <code>vsbPolicy</code> is
     * <code>VERTICAL_SCROLLBAR_AS_NEEDED</code>
     * then the vertical scrollbar only appears if the view doesn't fit
     * vertically. The available policy settings are listed at 
     * {@link #setVerticalScrollBarPolicy} and
     * {@link #setHorizontalScrollBarPolicy}.
     * 
     * @see #setViewportView
     * 
     * @param view the component to display in the scrollpanes viewport
     * @param vsbPolicy an integer that specifies the vertical
     *		scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal
     *		scrollbar policy
     */
    public JScrollPane(Component view, int vsbPolicy, int hsbPolicy) 
    {
	setLayout(new ScrollPaneLayout.UIResource());
        setVerticalScrollBarPolicy(vsbPolicy);
        setHorizontalScrollBarPolicy(hsbPolicy);
	setViewport(createViewport());
	setVerticalScrollBar(createVerticalScrollBar());
	setHorizontalScrollBar(createHorizontalScrollBar());
	if (view != null) {
	    setViewportView(view);
	}
	setOpaque(true);
        updateUI();

	if (!this.getComponentOrientation().isLeftToRight()) {
	    viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
	}
    }


    /**
     * Creates a <code>JScrollPane</code> that displays the
     * contents of the specified
     * component, where both horizontal and vertical scrollbars appear
     * whenever the component's contents are larger than the view.
     * 
     * @see #setViewportView
     * @param view the component to display in the scrollpane's viewport
     */
    public JScrollPane(Component view) {
        this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * Creates an empty (no viewport view) <code>JScrollPane</code>
     * with specified 
     * scrollbar policies. The available policy settings are listed at 
     * {@link #setVerticalScrollBarPolicy} and
     * {@link #setHorizontalScrollBarPolicy}.
     * 
     * @see #setViewportView
     * 
     * @param vsbPolicy an integer that specifies the vertical
     *		scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal
     *		scrollbar policy
     */
    public JScrollPane(int vsbPolicy, int hsbPolicy) {
        this(null, vsbPolicy, hsbPolicy);
    }


    /**
     * Creates an empty (no viewport view) <code>JScrollPane</code>
     * where both horizontal and vertical scrollbars appear when needed.
     */
    public JScrollPane() {
        this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }


    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the <code>ScrollPaneUI</code> object that renders this
     *				component
     * @see #setUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel. 
     */
    public ScrollPaneUI getUI() {
        return (ScrollPaneUI)ui;
    }


    /**
     * Sets the <code>ScrollPaneUI</code> object that provides the
     * look and feel (L&F) for this component.
     *
     * @param ui the <code>ScrollPaneUI</code> L&F object
     * @see #getUI
     */
    public void setUI(ScrollPaneUI ui) {
        super.setUI(ui);
    }


    /**
     * Replaces the current <code>ScrollPaneUI</code> object with a version 
     * from the current default look and feel.
     * To be called when the default look and feel changes.
     *
     * @see JComponent#updateUI
     * @see UIManager#getUI
     */
    public void updateUI() {
        setUI((ScrollPaneUI)UIManager.getUI(this));
    }


    /**
     * Returns the suffix used to construct the name of the L&F class used to
     * render this component.
     * 
     * @return the string "ScrollPaneUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * 
     * @beaninfo
     *    hidden: true
     */
    public String getUIClassID() {
        return uiClassID;
    }



    /** 
     * Sets the layout manager for this <code>JScrollPane</code>.
     * This method overrides <code>setLayout</code> in
     * <code>java.awt.Container</code> to ensure that only
     * <code>LayoutManager</code>s which
     * are subclasses of <code>ScrollPaneLayout</code> can be used in a
     * <code>JScrollPane</code>. If <code>layout</code> is non-null, this
     * will invoke <code>syncWithScrollPane</code> on it.
     * 
     * @param layout the specified layout manager
     * @exception ClassCastException if layout is not a
     *			<code>ScrollPaneLayout</code>
     * @see java.awt.Container#getLayout
     * @see java.awt.Container#setLayout
     * 
     * @beaninfo
     *    hidden: true
     */
    public void setLayout(LayoutManager layout) {
        if (layout instanceof ScrollPaneLayout) {
            super.setLayout(layout);
            ((ScrollPaneLayout)layout).syncWithScrollPane(this);
        }
        else if (layout == null) {
            super.setLayout(layout);
        }
	else {
	    String s = "layout of JScrollPane must be a ScrollPaneLayout";
	    throw new ClassCastException(s);
	}
    }

    /** 
     * Calls <code>revalidate</code> on any descendant of this
     * <code>JScrollPane</code>.  For example,
     * the viewport's view, will cause a request to be queued that
     * will validate the <code>JScrollPane</code> and all its descendants.
     * 
     * @return true
     * @see JComponent#revalidate
     * 
     * @beaninfo
     *    hidden: true
     */
    public boolean isValidateRoot() {
        return true;
    }


    /**
     * Returns the vertical scroll bar policy value.
     * @return the <code>verticalScrollBarPolicy</code> property
     * @see #setVerticalScrollBarPolicy
     */
    public int getVerticalScrollBarPolicy() {
        return verticalScrollBarPolicy;
    }


    /**
     * Determines when the vertical scrollbar appears in the scrollpane. 
     * Legal values are:
     * <ul>
     * <li>JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     * <li>JScrollPane.VERTICAL_SCROLLBAR_NEVER
     * <li>JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     * </ul>
     *
     * @param policy one of the three values listed above
     * @exception IllegalArgumentException if <code>policy</code> 
     *				is not one of the legal values shown above
     * @see #getVerticalScrollBarPolicy
     * 
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The scrollpane vertical scrollbar policy
     *        enum: VERTICAL_SCROLLBAR_AS_NEEDED JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     *              VERTICAL_SCROLLBAR_NEVER JScrollPane.VERTICAL_SCROLLBAR_NEVER
     *              VERTICAL_SCROLLBAR_ALWAYS JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
     */
    public void setVerticalScrollBarPolicy(int policy) {
	switch (policy) {
	case VERTICAL_SCROLLBAR_AS_NEEDED:
	case VERTICAL_SCROLLBAR_NEVER:
	case VERTICAL_SCROLLBAR_ALWAYS:
		break;
	default:
	    throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
	}
	int old = verticalScrollBarPolicy;
	verticalScrollBarPolicy = policy;
	firePropertyChange("verticalScrollBarPolicy", old, policy);
	revalidate();
	repaint();
    }


    /**
     * Returns the horizontal scroll bar policy value.
     * @return the <code>horizontalScrollBarPolicy</code> property
     * @see #setHorizontalScrollBarPolicy
     */
    public int getHorizontalScrollBarPolicy() {
	return horizontalScrollBarPolicy;
    }


    /**
     * Determines when the horizontal scrollbar appears in the scrollpane. 
     * The options are:<ul>
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     * <li>JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     * </ul>
     * 
     * @param policy one of the three values listed above
     * @exception IllegalArgumentException if <code>policy</code> 
     *				is not one of the legal values shown above
     * @see #getHorizontalScrollBarPolicy
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The scrollpane scrollbar policy
     *        enum: HORIZONTAL_SCROLLBAR_AS_NEEDED JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
     *              HORIZONTAL_SCROLLBAR_NEVER JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     *              HORIZONTAL_SCROLLBAR_ALWAYS JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
     */
    public void setHorizontalScrollBarPolicy(int policy) {
	switch (policy) {
	case HORIZONTAL_SCROLLBAR_AS_NEEDED:
	case HORIZONTAL_SCROLLBAR_NEVER:
	case HORIZONTAL_SCROLLBAR_ALWAYS:
		break;
	default:
	    throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
	}
	int old = horizontalScrollBarPolicy;
	horizontalScrollBarPolicy = policy;
	firePropertyChange("horizontalScrollBarPolicy", old, policy);
	revalidate();
	repaint();
    }


    /**
     * Returns the <code>Border</code> object that surrounds the viewport.
     *
     * @return the <code>viewportBorder</code> property
     * @see #setViewportBorder
     */
    public Border getViewportBorder() {
        return viewportBorder;
    }


    /**
     * Adds a border around the viewport.  Note that the border isn't
     * set on the viewport directly, <code>JViewport</code> doesn't support 
     * the <code>JComponent</code> border property. 
     * Similarly setting the <code>JScrollPane</code>s
     * viewport doesn't affect the <code>viewportBorder</code> property.
     * <p>
     * The default value of this property is computed by the look
     * and feel implementation.
     *
     * @param viewportBorder the border to be added
     * @see #getViewportBorder
     * @see #setViewport
     *
     * @beaninfo
     *   preferred: true
     *       bound: true
     * description: The border around the viewport.
     */
    public void setViewportBorder(Border viewportBorder) {
        Border oldValue = this.viewportBorder;
        this.viewportBorder = viewportBorder;
        firePropertyChange("viewportBorder", oldValue, viewportBorder);
    }


    /**
     * Returns the bounds of the viewport's border.
     *
     * @return a <code>Rectangle</code> object specifying the viewport border
     */
    public Rectangle getViewportBorderBounds()
    {
	Rectangle borderR = new Rectangle(getSize());

	Insets insets = getInsets();
	borderR.x = insets.left;
	borderR.y = insets.top;
	borderR.width -= insets.left + insets.right;
	borderR.height -= insets.top + insets.bottom;

        boolean leftToRight = SwingUtilities.isLeftToRight(this);

	/* If there's a visible column header remove the space it 
	 * needs from the top of borderR.  
	 */

	JViewport colHead = getColumnHeader();
	if ((colHead != null) && (colHead.isVisible())) {
	    int colHeadHeight = colHead.getHeight();
	    borderR.y += colHeadHeight;
	    borderR.height -= colHeadHeight;
	}

	/* If there's a visible row header remove the space it needs
	 * from the left of borderR.  
	 */

	JViewport rowHead = getRowHeader();
	if ((rowHead != null) && (rowHead.isVisible())) {
	    int rowHeadWidth = rowHead.getWidth();
            if ( leftToRight ) {
	        borderR.x += rowHeadWidth;
	    }
	    borderR.width -= rowHeadWidth;
	}

	/* If there's a visible vertical scrollbar remove the space it needs
	 * from the width of borderR.  
	 */
	JScrollBar vsb = getVerticalScrollBar();
	if ((vsb != null) && (vsb.isVisible())) {
            int vsbWidth = vsb.getWidth();
            if ( !leftToRight ) {
                borderR.x += vsbWidth;
	    }
	    borderR.width -= vsbWidth;
	}

	/* If there's a visible horizontal scrollbar remove the space it needs
	 * from the height of borderR.  
	 */
	JScrollBar hsb = getHorizontalScrollBar();
	if ((hsb != null) && (hsb.isVisible())) {
	    borderR.height -= hsb.getHeight();
	}

	return borderR;
    }


    /**
     * By default <code>JScrollPane</code> creates scrollbars
     * that are instances
     * of this class.  <code>Scrollbar</code> overrides the
     * <code>getUnitIncrement</code> and <code>getBlockIncrement</code>
     * methods so that, if the viewport's view is a <code>Scrollable</code>,
     * the view is asked to compute these values. Unless
     * the unit/block increment have been explicitly set.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     *
     * @see Scrollable
     * @see JScrollPane#createVerticalScrollBar
     * @see JScrollPane#createHorizontalScrollBar
     */
    protected class ScrollBar extends JScrollBar implements UIResource
    {
	/** 
         * Set to true when the unit increment has been explicitly set.
	 * If this is false the viewport's view is obtained and if it
	 * is an instance of <code>Scrollable</code> the unit increment
         * from it is used.
	 */
	private boolean unitIncrementSet;
	/** 
         * Set to true when the block increment has been explicitly set.
	 * If this is false the viewport's view is obtained and if it
	 * is an instance of <code>Scrollable</code> the block increment
         * from it is used.
	 */
	private boolean blockIncrementSet;

        /**
         * Creates a scrollbar with the specified orientation,
         * where the options are:<ul>
         * <li>JScrollPane.VERTICAL
         * <li>JScrollPane.HORIZONTAL
         * </ul>
         *
         * @param orientation  an integer specifying one of the legal
 	 * 	orientation values shown above
         */
        public ScrollBar(int orientation) {
            super(orientation);
        }

	/**
	 * Messages super to set the value, and resets the
	 * <code>unitIncrementSet</code> instance variable to true.
  	 *
   	 * @param unitIncrement the new unit increment value, in pixels
	 */
	public void setUnitIncrement(int unitIncrement) { 
	    unitIncrementSet = true;
	    super.setUnitIncrement(unitIncrement);
	}

        /**
         * Computes the unit increment for scrolling if the viewport's
    	 * view is a <code>Scrollable</code> object.
         * Otherwise return <code>super.getUnitIncrement</code>.
         * 
         * @param direction less than zero to scroll up/left,
         *	greater than zero for down/right
         * @return an integer, in pixels, containing the unit increment
         * @see Scrollable#getScrollableUnitIncrement
         */
        public int getUnitIncrement(int direction) {
            JViewport vp = getViewport();
            if (!unitIncrementSet && (vp != null) &&
		(vp.getView() instanceof Scrollable)) {
                Scrollable view = (Scrollable)(vp.getView());
                Rectangle vr = vp.getViewRect();
                return view.getScrollableUnitIncrement(vr, getOrientation(), direction);
            }
            else {
                return super.getUnitIncrement(direction);
            }
        }

	/**
	 * Messages super to set the value, and resets the
	 * <code>blockIncrementSet</code> instance variable to true.
	 *
	 * @param blockIncrement the new block increment value, in pixels
	 */
	public void setBlockIncrement(int blockIncrement) { 
	    blockIncrementSet = true;
	    super.setBlockIncrement(blockIncrement);
	}

        /**
 	 * Computes the block increment for scrolling if the viewport's
	 * view is a <code>Scrollable</code> object.  Otherwise
         * the <code>blockIncrement</code> equals the viewport's width
         * or height.  If there's no viewport return 
         * <code>super.getBlockIncrement</code>.
         * 
         * @param direction less than zero to scroll up/left,
         *	greater than zero for down/right
  	 * @return an integer, in pixels, containing the block increment
         * @see Scrollable#getScrollableBlockIncrement
         */
        public int getBlockIncrement(int direction) {
            JViewport vp = getViewport();
            if (blockIncrementSet || vp == null) {
                return super.getBlockIncrement(direction);
            }
            else if (vp.getView() instanceof Scrollable) {
                Scrollable view = (Scrollable)(vp.getView());
                Rectangle vr = vp.getViewRect();
                return view.getScrollableBlockIncrement(vr, getOrientation(), direction);
            }
            else if (getOrientation() == VERTICAL) {
                return vp.getExtentSize().height;
            }
            else {
                return vp.getExtentSize().width;
            }
        }

    }


    /**
     * Returns a <code>JScrollPane.ScrollBar</code> by default.
     * Subclasses may override this method to force <code>ScrollPaneUI</code>
     * implementations to use a <code>JScrollBar</code> subclass.
     * Used by <code>ScrollPaneUI</code> implementations to
     * create the horizontal scrollbar.
     *
     * @return a <code>JScrollBar</code> with a horizontal orientation
     * @see JScrollBar
     */
    public JScrollBar createHorizontalScrollBar() {
        return new ScrollBar(JScrollBar.HORIZONTAL);
    }


    /**
     * Returns the horizontal scroll bar that controls the viewport's
     * horizontal view position.
     *
     * @return the <code>horizontalScrollBar</code> property
     * @see #setHorizontalScrollBar
     */
    public JScrollBar getHorizontalScrollBar() {
        return horizontalScrollBar;
    }

    
    /**
     * Adds the scrollbar that controls the viewport's horizontal view 
     * position to the scrollpane.
     * This is usually unnecessary, as <code>JScrollPane</code> creates
     * horizontal and vertical scrollbars by default.
     * 
     * @param horizontalScrollBar the horizontal scrollbar to be added
     * @see #createHorizontalScrollBar
     * @see #getHorizontalScrollBar
     * 
     * @beaninfo
     *        expert: true
     *         bound: true
     *   description: The horizontal scrollbar.
     */
    public void setHorizontalScrollBar(JScrollBar horizontalScrollBar) {
	JScrollBar old = getHorizontalScrollBar();
	this.horizontalScrollBar = horizontalScrollBar;
	add(horizontalScrollBar, HORIZONTAL_SCROLLBAR);
	firePropertyChange("horizontalScrollBar", old, horizontalScrollBar);

	revalidate();
	repaint();
    }


    /**
     * Returns a <code>JScrollPane.ScrollBar</code> by default.  Subclasses
     * may override this method to force <code>ScrollPaneUI</code>
     * implementations to use a <code>JScrollBar</code> subclass.
     * Used by <code>ScrollPaneUI</code> implementations to create the 
     * vertical scrollbar.  
     *
     * @return a <code>JScrollBar</code> with a vertical orientation
     * @see JScrollBar
     */
    public JScrollBar createVerticalScrollBar() {
        return new ScrollBar(JScrollBar.VERTICAL);
    }


    /**
     * Returns the vertical scroll bar that controls the viewports
     * vertical view position.
     *
     * @return the <code>verticalScrollBar</code> property
     * @see #setVerticalScrollBar
     */
    public JScrollBar getVerticalScrollBar() {
	return verticalScrollBar;
    }


    /**
     * Adds the scrollbar that controls the viewports vertical view position
     * to the scrollpane.  This is usually unnecessary,
     * as <code>JScrollPane</code> creates vertical and
     * horizontal scrollbars by default.
     * 
     * @param verticalScrollBar the new vertical scrollbar to be added
     * @see #createVerticalScrollBar
     * @see #getVerticalScrollBar
     * 
     * @beaninfo
     *        expert: true
     *         bound: true
     *   description: The vertical scrollbar.
     */
    public void setVerticalScrollBar(JScrollBar verticalScrollBar) {
	JScrollBar old = getVerticalScrollBar();
	this.verticalScrollBar = verticalScrollBar;
	add(verticalScrollBar, VERTICAL_SCROLLBAR);
	firePropertyChange("verticalScrollBar", old, verticalScrollBar);

	revalidate();
	repaint();
    }


    /**
     * Returns a new <code>JViewport</code> by default. 
     * Used to create the
     * viewport (as needed) in <code>setViewportView</code>,
     * <code>setRowHeaderView</code>, and <code>setColumnHeaderView</code>.
     * Subclasses may override this method to return a subclass of 
     * <code>JViewport</code>.
     *
     * @return a new <code>JViewport</code>
     */
    protected JViewport createViewport() {
        return new JViewport();
    }


    /**
     * Returns the current <code>JViewport</code>.
     *
     * @see #setViewport
     * @return the <code>viewport</code> property
     */
    public JViewport getViewport() {
        return viewport;
    }

    
    /**
     * Removes the old viewport (if there is one); forces the
     * viewPosition of the new viewport to be in the +x,+y quadrant;
     * syncs up the row and column headers (if there are any) with the
     * new viewport; and finally syncs the scrollbars and
     * headers with the new viewport.
     * <p>
     * Most applications will find it more convenient to use 
     * <code>setViewportView</code>
     * to add a viewport and a view to the scrollpane.
     * 
     * @param viewport the new viewport to be used; if viewport is
     *		<code>null</code>, the old viewport is still removed
     *		and the new viewport is set to <code>null</code>
     * @see #createViewport
     * @see #getViewport
     * @see #setViewportView
     * 
     * @beaninfo
     *       expert: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The viewport child for this scrollpane
     * 
     */
    public void setViewport(JViewport viewport) {
	JViewport old = getViewport();
	this.viewport = viewport;
	if (viewport != null) {
	    add(viewport, VIEWPORT);
	}
	else if (old != null) {
	    remove(old);
	}
	firePropertyChange("viewport", old, viewport);

	if (accessibleContext != null) {
	    ((AccessibleJScrollPane)accessibleContext).resetViewPort();
	}

	revalidate();
	repaint();
    }


    /**
     * Creates a viewport if necessary and then sets its view.  Applications
     * that don't provide the view directly to the <code>JScrollPane</code>
     * constructor
     * should use this method to specify the scrollable child that's going
     * to be displayed in the scrollpane. For example:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * </pre>
     * Applications should not add children directly to the scrollpane.
     *
     * @param view the component to add to the viewport
     * @see #setViewport
     * @see JViewport#setView
     */
    public void setViewportView(Component view) {
        if (getViewport() == null) {
            setViewport(createViewport());
        }
        getViewport().setView(view);
    }



    /**
     * Returns the row header.
     * @return the <code>rowHeader</code> property
     * @see #setRowHeader
     */
    public JViewport getRowHeader() {
        return rowHeader;
    }


    /**
     * Removes the old rowHeader, if it exists.  If the new rowHeader
     * isn't <code>null</code>, syncs the y coordinate of its
     * viewPosition with
     * the viewport (if there is one) and then adds it to the scrollpane.
     * <p>
     * Most applications will find it more convenient to use 
     * <code>setRowHeaderView</code>
     * to add a row header component and its viewport to the scrollpane.
     * 
     * @param rowHeader the new row header to be used; if <code>null</code>
     *		the old row header is still removed and the new rowHeader
     *		is set to <code>null</code>
     * @see #getRowHeader
     * @see #setRowHeaderView
     * 
     * @beaninfo
     *        bound: true
     *       expert: true
     *  description: The row header child for this scrollpane
     */
    public void setRowHeader(JViewport rowHeader) {
	JViewport old = getRowHeader();
	this.rowHeader = rowHeader;	
	if (rowHeader != null) {
	    add(rowHeader, ROW_HEADER);
	}
	else if (old != null) {
	    remove(old);
	}
	firePropertyChange("rowHeader", old, rowHeader);
	revalidate();
	repaint();
    }


    /**
     * Creates a row-header viewport if necessary, sets
     * its view and then adds the row-header viewport
     * to the scrollpane.  For example:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setRowHeaderView(myBigComponentsRowHeader);
     * </pre>
     *
     * @see #setRowHeader
     * @see JViewport#setView
     * @param view the component to display as the row header
     */
    public void setRowHeaderView(Component view) {
        if (getRowHeader() == null) {
            setRowHeader(createViewport());
        }
        getRowHeader().setView(view);
    }



    /**
     * Returns the column header.
     * @return the <code>columnHeader</code> property
     * @see #setColumnHeader
     */
    public JViewport getColumnHeader() {
        return columnHeader;
    }


    /**
     * Removes the old columnHeader, if it exists.  If the new columnHeader
     * isn't <code>null</code>, sync the x coordinate of the its viewPosition 
     * with the viewport (if there is one) and then add it to the scrollpane.
     * <p>
     * Most applications will find it more convenient to use 
     * <code>setRowHeaderView</code>
     * to add a row header component and its viewport to the scrollpane.
     * 
     * @see #getColumnHeader
     * @see #setColumnHeaderView
     * 
     * @beaninfo
     *        bound: true
     *  description: The column header child for this scrollpane
     *    attribute: visualUpdate true
     */
    public void setColumnHeader(JViewport columnHeader) {
	JViewport old = getColumnHeader();
	this.columnHeader = columnHeader;	
	if (columnHeader != null) {
	    add(columnHeader, COLUMN_HEADER);
	}
	else if (old != null) {
	    remove(old);
	}
	firePropertyChange("columnHeader", old, columnHeader);

	revalidate();
	repaint();
    }



    /**
     * Creates a column-header viewport if necessary, sets
     * its view, and then adds the column-header viewport
     * to the scrollpane.  For example:
     * <pre>
     * JScrollPane scrollpane = new JScrollPane();
     * scrollpane.setViewportView(myBigComponentToScroll);
     * scrollpane.setColumnHeaderView(myBigComponentsColumnHeader);
     * </pre>
     * 
     * @see #setColumnHeader
     * @see JViewport#setView
     * 
     * @param view the component to display as the column header
     */
    public void setColumnHeaderView(Component view) {
        if (getColumnHeader() == null) {
            setColumnHeader(createViewport());
        }
        getColumnHeader().setView(view);
    }


    /**
     * Returns the component at the specified corner. The
     * <code>key</code> value specifying the corner is one of:
     * <ul>
     * <li>JScrollPane.LOWER_LEFT_CORNER
     * <li>JScrollPane.LOWER_RIGHT_CORNER
     * <li>JScrollPane.UPPER_LEFT_CORNER
     * <li>JScrollPane.UPPER_RIGHT_CORNER
     * <li>JScrollPane.LOWER_LEADING_CORNER
     * <li>JScrollPane.LOWER_TRAILING_CORNER
     * <li>JScrollPane.UPPER_LEADING_CORNER
     * <li>JScrollPane.UPPER_TRAILING_CORNER
     * </ul>
     *
     * @param key one of the values as shown above
     * @return one of the components listed below or <code>null</code>
     *		if <code>key</code> is invalid:
     * <ul>
     * <li>lowerLeft
     * <li>lowerRight
     * <li>upperLeft
     * <li>upperRight
     * </ul>
     * @see #setCorner
     */
    public Component getCorner(String key) {
	if (key.equals(LOWER_LEFT_CORNER)) {
	    return lowerLeft;
	}
	else if (key.equals(LOWER_RIGHT_CORNER)) {
	    return lowerRight;
	}
	else if (key.equals(UPPER_LEFT_CORNER)) {
	    return upperLeft;
	}
	else if (key.equals(UPPER_RIGHT_CORNER)) {
	    return upperRight;
	}
	else {
	    return null;
	}
    }


    /**
     * Adds a child that will appear in one of the scroll panes
     * corners, if there's room.   For example with both scrollbars
     * showing (on the right and bottom edges of the scrollpane) 
     * the lower left corner component will be shown in the space
     * between ends of the two scrollbars. Legal values for 
     * the <b>key</b> are:
     * <ul>
     * <li>JScrollPane.LOWER_LEFT_CORNER
     * <li>JScrollPane.LOWER_RIGHT_CORNER
     * <li>JScrollPane.UPPER_LEFT_CORNER
     * <li>JScrollPane.UPPER_RIGHT_CORNER
     * <li>JScrollPane.LOWER_LEADING_CORNER
     * <li>JScrollPane.LOWER_TRAILING_CORNER
     * <li>JScrollPane.UPPER_LEADING_CORNER
     * <li>JScrollPane.UPPER_TRAILING_CORNER
     * </ul>
     * <p>
     * Although "corner" doesn't match any beans property
     * signature, <code>PropertyChange</code> events are generated with the
     * property name set to the corner key.
     * 
     * @param key identifies which corner the component will appear in
     * @param corner one of the following components:
     * <ul>
     * <li>lowerLeft
     * <li>lowerRight
     * <li>upperLeft
     * <li>upperRight
     * </ul>
     * @exception IllegalArgumentException if corner key is invalid
     */
    public void setCorner(String key, Component corner) 
    {
	Component old;
	if (key.equals(LOWER_LEFT_CORNER)) {
	    old = lowerLeft;
	    lowerLeft = corner;
	}
	else if (key.equals(LOWER_RIGHT_CORNER)) {
	    old = lowerRight;
	    lowerRight = corner;
	}
	else if (key.equals(UPPER_LEFT_CORNER)) {
	    old = upperLeft;
	    upperLeft = corner;
	}
	else if (key.equals(UPPER_RIGHT_CORNER)) {
	    old = upperRight;
	    upperRight = corner;
	}
	else {
	    throw new IllegalArgumentException("invalid corner key");
	}
	add(corner, key);
	firePropertyChange(key, old, corner);
	revalidate();
	repaint();
    }

    /**
     * Sets the orientation for the vertical and horizontal
     * scrollbars as determined by the
     * <code>ComponentOrientation</code> argument.
     *
     * @param  co one of the following values:
     * <ul>
     * <li>java.awt.ComponentOrientation.LEFT_TO_RIGHT
     * <li>java.awt.ComponentOrientation.RIGHT_TO_LEFT
     * <li>java.awt.ComponentOrientation.UNKNOWN
     * </ul>
     * @see java.awt.ComponentOrientation
     */
    public void setComponentOrientation( ComponentOrientation co ) {
        super.setComponentOrientation( co );
        if( verticalScrollBar != null )
            verticalScrollBar.setComponentOrientation( co );
        if( horizontalScrollBar != null )
            horizontalScrollBar.setComponentOrientation( co );
    }

    /**
     * Indicates whether or not scrolling will take place in response to the
     * mouse wheel.  Wheel scrolling is enabled by default.
     * 
     * @see #setWheelScrollingEnabled
     * @since 1.4
     * @beaninfo
     *       bound: true
     * description: Flag for enabling/disabling mouse wheel scrolling
     */
    public boolean isWheelScrollingEnabled() {return wheelScrollState;}

    /**
     * Enables/disables scrolling in response to movement of the mouse wheel.
     * Wheel scrolling is enabled by default.
     *
     * @param handleWheel   <code>true</code> if scrolling should be done
     *                      automatically for a MouseWheelEvent,
     *                      <code>false</code> otherwise.
     * @see #isWheelScrollingEnabled
     * @see java.awt.event.MouseWheelEvent
     * @see java.awt.event.MouseWheelListener
     * @since 1.4
     * @beaninfo
     *       bound: true
     * description: Flag for enabling/disabling mouse wheel scrolling
     */
    public void setWheelScrollingEnabled(boolean handleWheel) {
        boolean old = wheelScrollState;
        wheelScrollState = handleWheel;
        firePropertyChange("wheelScrollingEnabled", old, handleWheel);
    }

    /** 
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }


    /**
     * Returns a string representation of this <code>JScrollPane</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JScrollPane</code>.
     */
    protected String paramString() {
        String viewportBorderString = (viewportBorder != null ?
				       viewportBorder.toString() : "");
        String viewportString = (viewport != null ?
				 viewport.toString() : "");
        String verticalScrollBarPolicyString;
        if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_AS_NEEDED";
        } else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_NEVER) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_NEVER";
        } else if (verticalScrollBarPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
            verticalScrollBarPolicyString = "VERTICAL_SCROLLBAR_ALWAYS";
        } else verticalScrollBarPolicyString = "";
        String horizontalScrollBarPolicyString;
        if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
        } else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_NEVER";
        } else if (horizontalScrollBarPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
            horizontalScrollBarPolicyString = "HORIZONTAL_SCROLLBAR_ALWAYS";
        } else horizontalScrollBarPolicyString = "";
        String horizontalScrollBarString = (horizontalScrollBar != null ?
					    horizontalScrollBar.toString()
					    : "");
        String verticalScrollBarString = (verticalScrollBar != null ?
					  verticalScrollBar.toString() : "");
        String columnHeaderString = (columnHeader != null ?
				     columnHeader.toString() : "");
        String rowHeaderString = (rowHeader != null ?
				  rowHeader.toString() : "");
        String lowerLeftString = (lowerLeft != null ?
				  lowerLeft.toString() : "");
        String lowerRightString = (lowerRight != null ?
				  lowerRight.toString() : "");
        String upperLeftString = (upperLeft != null ?
				  upperLeft.toString() : "");
        String upperRightString = (upperRight != null ?
				  upperRight.toString() : "");

        return super.paramString() +
        ",columnHeader=" + columnHeaderString +
        ",horizontalScrollBar=" + horizontalScrollBarString +
        ",horizontalScrollBarPolicy=" + horizontalScrollBarPolicyString +
        ",lowerLeft=" + lowerLeftString +
        ",lowerRight=" + lowerRightString +
        ",rowHeader=" + rowHeaderString +
        ",upperLeft=" + upperLeftString +
        ",upperRight=" + upperRightString +
        ",verticalScrollBar=" + verticalScrollBarString +
        ",verticalScrollBarPolicy=" + verticalScrollBarPolicyString +
        ",viewport=" + viewportString +
        ",viewportBorder=" + viewportBorderString;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Gets the AccessibleContext associated with this JScrollPane. 
     * For scroll panes, the AccessibleContext takes the form of an 
     * AccessibleJScrollPane. 
     * A new AccessibleJScrollPane instance is created if necessary.
     *
     * @return an AccessibleJScrollPane that serves as the 
     *         AccessibleContext of this JScrollPane
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJScrollPane();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JScrollPane</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to scroll pane user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    protected class AccessibleJScrollPane extends AccessibleJComponent 
    implements ChangeListener {

        protected JViewport viewPort = null;

        public void resetViewPort() {
            viewPort.removeChangeListener(this);
            viewPort = JScrollPane.this.getViewport();
            viewPort.addChangeListener(this);
        }

        /**
         * Constructor to set up listener on viewport.
         */
        public AccessibleJScrollPane() {
            super();
            if (viewPort == null) {
               viewPort = JScrollPane.this.getViewport();
            }
            viewPort.addChangeListener(this);
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_PANE;
        }

        /**
         * Supports the change listener interface and fires property change
         */
        public void stateChanged(ChangeEvent e) {
            AccessibleContext ac = ((Accessible)JScrollPane.this).getAccessibleContext();
            if (ac != null) {
                ac.firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, new Boolean(false), new Boolean(true));
            }
        }
    }
}

