.. _batch_tutorial:

*********************
Batch Export Tutorial
*********************
TopiaryExplorer has the ability to batch export images of a tree over the current metadata coloring category. This tutorial will explain how this functionality can be utilized.

Assumptions made in this tutorial
=================================

* Prior to working through this tutorial we recommend running through the `TopiaryExplorer Overview Tutorial <./quickstart.html>`_ which will show you how to work with the basic features of TopiaryExplorer. This tutorial assumes that you already know how to load the TopiaryExplorer application and that you have some familiarity with the basic interface.


Step 1. Open your tree and relevant metadata
============================================
In order to batch export images, the tree must be colored by some metadata. You should also choose the tree layout and collapse level desired for images.

.. figure::  _images/tree_colored_wkey.png
   :align:   center

Step 2. Adjust the window size
==============================
Resize the tree window so that the tree view pane is the size of images that you want to export. **Make sure to click recenter in the tree view panel to resize the tree to fit the window.**

.. figure::  _images/small_window.png
   :align:   center

Step 3. Batch Export dialog
===========================
Use the Tree Window file menu and select "Batch Export Tree Images..." TopiaryExplorer will automatically populate the dimensions from the size of the tree window. Each value for the current coloring category will get its own image where that value is colored whichever color is in the color key. All other values will be colored the color you select in this dialog.

.. figure::  _images/thumbnail_dialog.png
   :align:   center

.. note:: You can set the unselected color to be the same as the current `no count color <./branch_panel.html#no-count-color>`_ to get images with no count color functionality.

Normalize by Abundance
----------------------
In this dialog you also have the ability to normalize by category abundance. If your tree is collapsed so that there are different wedges, for each coloring category the wedges will be colored such that the wedge with the highest relative abundance of tips will have the most saturated version of the chosen color, and other wedges will have less saturated variants of the chosen color according to relative abundance.

Step 4. View images
===================
TopiaryExplorer will attempt to open the images in your operating system's default pdf viewer. If this does not work, you can find the images in your local TopiaryExplorer directory /tree_export_images/ in a folder with the name you supplied in the batch export imagess dialog.

.. figure::  _images/thumbnails.png
   :align:   center
