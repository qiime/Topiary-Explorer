#!/usr/bin/env python
# encoding: utf-8
"""
untitled.py

Created by meg pirrung on 2010-09-20.
Copyright (c) 2010 __MyCompanyName__. All rights reserved.
"""

import sys
import os
from cogent.seqsim.tree import CombTree
from cogent.core.tree import PhyloNode
from numpy import random


def main():
    # n = 100000
    # tree = BalancedTree(n, node_class=PhyloNode)
    # tree.nameUnnamedNodes()
    # for node in tree.traverse():
    #     node._set_length("%0.4f"%(random.random_sample()/100))
    # tree.writeToFile("tree_%dnodes.tree"%n)
    for n in [x*1000 for x in range(4,8)]:
            tree = CombTree(n, node_class=PhyloNode)
            tree.nameUnnamedNodes()
            for node in tree.traverse():
                node._set_length("%0.4f"%(random.random_sample()/100))
            tree.writeToFile("comb_tree_%dnodes.tree"%n)


if __name__ == '__main__':
    main()

