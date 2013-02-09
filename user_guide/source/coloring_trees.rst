.. _coloring_tutorial:

*****************
Coloring Tutorial
*****************
This tutorial will explain how to color a tree in TopiaryExplorer using metadata, and the many different options available when coloring.

Assumptions made in this tutorial
---------------------------------

* Prior to working through this tutorial we recommend running through the `TopiaryExplorer Overview Tutorial <./quickstart.html>`_ which will show you how to work with the basic features of TopiaryExplorer. This tutorial assumes that you already know how to load the TopiaryExplorer application and that you have some familiarity with the basic interface.

Step 0. Load a tree and relevant metadata
-----------------------------------------
In order to color a tree you must have some metadata that corresponds to your tree. 

You can color a tree using only tip data, which corresponds directly to the tips on the tree.

.. figure::  _images/tip_data.png
   :align:   center

You can also color a tree based on sample metadata, where the tips on the tree are linked to samples through an OTU table and relevant sample metadata.
This is an OTU table:

.. figure::  _images/otu_table.png
   :align:   center

This is a sample metadata table:

.. figure::  _images/sample_meta.png
   :align:   center

Coloring Branches
-----------------
To color the branches of a tree, use the branch panel in the tree toolbar. There is a button labeled 'Color by...' which when clicked will give options for coloring by Tip data or Sample metadata.

.. figure::  _images/color_by_menu.png
   :align:   center

TopiaryExplorer will provide options for coloring by each column defined in either Tip data table or the Sample metadata table.

Color by tip data:

.. figure::  _images/color_by_tips.png
   :align:   center

.. note:: You can not color by Tip ID because that would color each branch a different color, which is not useful.

Color by sample metadata:

.. figure::  _images/color_by_sample.png
   :align:   center

Coloring Labels
---------------
You can also color the labels of the tree by sample metadata or tip metadata using the same steps described above for branches.

.. figure::  _images/colored_labels.png
   :align:   center

.. note:: Make sure you have tip labels turned on and that you are zoomed in enough to see tip labels, otherwise they will not show up.