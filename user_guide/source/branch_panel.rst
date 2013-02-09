.. _branch_panel:

************
Branch Panel
************
The branch panel is the third panel on the tree toolbar.

.. figure::  _images/branch_panel.png
   :align:   center

This panel is used to:

  *  Color branches by metadata
  *  Turn on/off branch coloring
  *  Turn on/off majority coloring
  *  Change coloring mode from weighted to non
  *  Specify a color to ignore when calculating colors
  *  Set line widths by abundance
  *  Change line width scale

Color by
--------
Use this button to color the tree's branches by tip data or sample data. All tree coloring functions are described in the `Coloring Trees <http://topiaryexplorer.sourceforge.net/user_guide/quickstart.html#step-5-coloring-the-tree>`_ section of the `Quick Start Tutorial <http://topiaryexplorer.sourceforge.net/user_guide/quickstart.html>`_.

Majority Coloring
-----------------
When the majority coloring checkbox is selected, internal nodes will take on the color that appears most often in its tips. When unchecked, internal nodes will take on a color that is a combination of the colors of all of its tips.

.. figure::  _images/majority_coloring.png
   :align:   center

   A tree with majority coloring selected on top, the same tree with combination coloring on bottom.

Weighted
--------
If the weighted option is clicked, abundance information contained in the OTU table is taken into account when calculating the colors. If weighted is not checked, the abundance information is treated as binary.

No count color
--------------
The no count color function lets you choose a color and as such a category whose abundance information to ignore in the current coloring.