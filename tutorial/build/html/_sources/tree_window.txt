.. _tree_window:

***********
Tree Window
***********
This guide explains all of the functions that a user can perform on a tree using the tree window.

The tree window contains the following elements:

  *  `Tree toolbar <./tree_toolbar.html>`_
  *  Collapse tree toolbar
  *  Tree view
  *  Vertical zoom slider
  *  Horizontal zoom slider
  *  Zoom lock button
  *  Node search field

.. figure::  _images/tree_window.png
   :align:   center

Collapse tree toolbar
=====================
The collapse tree toolbar is a slider corresponding to the collapse level of the tree.

.. note:: Collapsing is not available in the polar view.

Tree view
=========
The tree view holds the tree, and when focused, hovering over nodes will give more information about each node.

.. figure::  _images/tree_info.png
   :align:   center

Right clicking on a node in the tree view will give access to a context menu with different options that can be performed on the clicked node.

These options include:

  *  Find in metadata
  *  Hide
  *  Lock/Unlock
  *  Rotate
  *  Toggle pie chart
  *  Toggle label
  *  Consensus lineage
  *  View subtree in new window
  *  Delete

Find in metadata
----------------
This option will bring the OTU metadata table to the front with the selected node highlighted(if the node can be found in the metadata). 

Hide
----
Nodes can be hidden from view using this option. To reveal hidden nodes, use the Show Hidden Nodes button in the `Tree toolbar <./tree_toolbar.html>`_.

Lock/Unlock
-----------
Node collapse states can be locked/unlocked. When a node is locked, it will not respond to the collapse slider.

.. note:: Double clicking will also lock/unlock a node.

.. note:: Changing to polar view will uncollapse the entire tree, but node lock/unlock states are preserved so when the layout is changed the locked nodes are still locked.

Toggle pie chart
----------------
Pie charts are a convenient way of displaying the percentage of branches of an internal node painted by different metadata values.

.. figure::  _images/pie_chart.png
   :align:   center

.. note:: Pie charts correspond to branch color, not label color.

Toggle label
------------
Labels can be set visible or hidden on a per-node basis using this function.

Consensus lineage
-----------------
Once the consensus lineage is set using the button in the `Tree toolbar <./tree_toolbar.html>`_, a full string can be viewed using this function.

.. figure::  _images/consensus_popup.png
   :align:   center

View subtree in new window
--------------------------
In order to study the tree more carefully, a user may want to focus on smaller subtrees of a larger tree. This function allows the user to do so.

.. note:: Coloring is identical across all tree windows, but all other tree functions such as pruning, collapsing and layout are independent.

Delete
------
Nodes can be pruned individually using this function.

Zooming
=======
A user can zoom in on the tree in two different ways, using the sliders or by using the keyboard.

Sliding the horizontal and vertical sliders will zoom the tree in the respective orientations.

The zoom lock button will lock the sliders together so that sliding one of them zooms the view in both directions by the same amount.

.. note:: Radial and polar views are only available in zoom lock mode. Switching to either of these views will set zoom lock for you.

A user can also zoom in using the = and - keys on the keyboard, to zoom in and out, respectively.

Node search
===========
A user can search for nodes with labels matching a given string using the node search box located at the bottom right corner of the tree window. 

.. figure::  _images/node_search.png
   :align:   center
