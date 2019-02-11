/*
 **
 **    Copyright (C) 2016-2019 Rishi Desai
 **
 **    Copyright (C) 2003-2018 Institute for Systems Biology
 **                            Seattle, Washington, USA.
 **
 **    This library is free software; you can redistribute it and/or
 **    modify it under the terms of the GNU Lesser General Public
 **    License as published by the Free Software Foundation; either
 **    version 2.1 of the License, or (at your option) any later version.
 **
 **    This library is distributed in the hope that it will be useful,
 **    but WITHOUT ANY WARRANTY; without even the implied warranty of
 **    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 **    Lesser General Public License for more details.
 **
 **    You should have received a copy of the GNU Lesser General Public
 **    License along with this library; if not, write to the Free Software
 **    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.systemsbiology.biofabric.plugin.core.align;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.systemsbiology.biofabric.api.model.NetLink;
import org.systemsbiology.biofabric.api.model.NetNode;
import org.systemsbiology.biofabric.api.worker.AsynchExitRequestException;
import org.systemsbiology.biofabric.api.worker.BTProgressMonitor;
import org.systemsbiology.biofabric.plugin.PluginSupportFactory;

/***************************************************************************
 **
 ** Functions for Jaccard Similarity
 */

public class JaccardSimilarity {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INSTANCE MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  private BTProgressMonitor monitor_;
  private final Double jaccSimThreshold_;
  
  private Map<ComboNode, Set<ComboNode>> nodeToNeighMain_, nodeToNeighPerfect_;
  private Map<V1Node, NetNode> v1ToV12Main_, v1ToV12Perfect_;
  private Map<NetNode, V1Node> v12ToV1Main_, v12ToV1Perfect_;
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  JaccardSimilarity(Set<NetLink> allLinksMain, Set<NetNode> loneNodeIDsMain,
                    NetworkAlignment.NodeColorMap colorMapMain,
                    Set<NetLink> allLinksPerfect, Set<NetNode> loneNodeIDsPerfect,
                    NetworkAlignment.NodeColorMap colorMapPerfect,
                    Map<NetNode, Set<NetNode>> nodeToNeighborsMain,
                    Map<NetNode, Set<NetNode>> nodeToNeighborsPerfect,
                    final Double jaccSimThreshold,
                    BTProgressMonitor monitor) throws AsynchExitRequestException {
    
    // Build oracle network
    
    
    
    this.jaccSimThreshold_ = jaccSimThreshold;
    this.nodeToNeighMain_ = convertToString(nodeToNeighborsMain, colorMapMain);
    this.v1ToV12Main_ = findGraphOneNodes(allLinksMain, loneNodeIDsMain, colorMapMain);
    this.v12ToV1Main_ = inverseMap(v1ToV12Main_);
    this.monitor_ = monitor;
    this.nodeToNeighPerfect_ = convertToString(nodeToNeighborsPerfect, colorMapPerfect);
    this.v1ToV12Perfect_ = findGraphOneNodes(allLinksPerfect, loneNodeIDsPerfect, colorMapPerfect);
    this.v12ToV1Perfect_ = inverseMap(v1ToV12Perfect_);
    return;
  }
  
  /***************************************************************************
   **
   ** @param node must be from V12 (Blue or Purple)
   */
  
  boolean isCorrectJS(NetNode node) {
    
    V1Node v1Eq = v12ToV1Main_.get(node);
    
    NetNode match = v1ToV12Perfect_.get(v1Eq);
    
    ComboNode nodeEq = new ComboNode(node.getName()), matchEq = new ComboNode(match.getName());
    
    double jsVal = jaccSimValue(nodeEq, matchEq);
  
    if (jaccSimThreshold_ == null) {
        throw new IllegalStateException("JS Threshold is null"); // should never happen
      }
      
    boolean isCorrect = Double.compare(jsVal, jaccSimThreshold_) >= 0;
    return (isCorrect);
    
//    String v1NodeEquivalent = v12ToV1Main_.get(node);
//
//      String largeName = (node.getName().split("::"))[1];
//
//      NetNode largeNode = nameToLarge_.get(largeName);
//      if (largeNode == null) {
//        throw new IllegalStateException("Large node for " + node.getName() + " not found for Jaccard Similarity");
//      }
//      NetNode match = entrezAlign.get(largeNode);
//
//      double jsVal = jaccSimValue(largeNode, match);
//      if (jaccSimThreshold_ == null) {
//        throw new IllegalStateException("JS Threshold is null"); // should never happen
//      }
//      boolean isCorrect = Double.compare(jsVal, jaccSimThreshold_) >= 0;
//      return (isCorrect);
//    throw (new IllegalStateException());
  }
  
  /****************************************************************************
   **
   ** Jaccard Similarity Measure - Adapted from NodeEQC.java
   */
  
  double calcScore() {
    
    double totJ = 0.0;
    for (V1Node graph1Node : v1ToV12Main_.keySet()) {
//      System.out.println(graph1Node + "   " + v1ToV12Main_.get(graph1Node));
      NetNode nodeEqV12Main = v1ToV12Main_.get(graph1Node),
              nodeEqV12Perfect = v1ToV12Perfect_.get(graph1Node);
      ComboNode node = new ComboNode(nodeEqV12Main.getName()),
              match = new ComboNode(nodeEqV12Perfect.getName());
      Double val = jaccSimValue(node, match);
      totJ += val;
    }
    double measure = totJ / v1ToV12Main_.size();
    return (measure);
  }
  
  /***************************************************************************
   **
   ** Jaccard Similarity between two nodes in V12 of G12=(V12,E12);
   ** sigma(x,y) : V12 -> [0,1]                   (from the paper)
   ** node, match - both MUST be V12 nodes (Blue, Purple, or Red)
   */
  
  private double jaccSimValue(ComboNode node, ComboNode match) {
    int lenAdjust = 0;
    HashSet<ComboNode> scratchNode = new HashSet<ComboNode>(nodeToNeighMain_.get(node)),
            scratchMatch = new HashSet<ComboNode>(nodeToNeighPerfect_.get(match));
  
    System.out.println(scratchNode);
    System.out.println(scratchMatch);
    System.out.println();
    
    
    if (scratchNode.contains(match)) {
      scratchNode.remove(match);
      scratchMatch.remove(node);
      lenAdjust = 1;
    }
    HashSet<ComboNode> union = new HashSet<ComboNode>(), intersect = new HashSet<ComboNode>();
    union(scratchNode, scratchMatch, union);
    intersection(scratchNode, scratchMatch, intersect);
    
    int iSize = intersect.size() + lenAdjust;
    int uSize = union.size() + lenAdjust;
    Double jaccard = (double)(iSize) / (double)uSize;
    if (jaccard.isNaN()) {  // case of 0/0 for two singletons
      jaccard = 1.0;
    }
    return (jaccard);
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /****************************************************************************
   **
   ** Create map : String -> Graph 1 node (blue or purple)
   */
  
  private Map<V1Node, NetNode> findGraphOneNodes(Set<NetLink> allLinks, Set<NetNode> loneNodeIDs,
                                                 NetworkAlignment.NodeColorMap colorMap)
          throws AsynchExitRequestException {
  
    Map<V1Node, NetNode> ret = new HashMap<V1Node, NetNode>();
    
    Set<NetNode> allNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinks, loneNodeIDs, monitor_);
    for (NetNode node : allNodes) {
      if (colorMap.getColor(node) == NetworkAlignment.NodeColor.PURPLE ||
              colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {
        String g1name = StringUtilities.separateNodeOne(node.getName());
        V1Node v1Node = new V1Node(g1name);
        ret.put(v1Node, node);
      }
    }
    return (ret);
  }
  
  /***************************************************************************
   **
   ** Flip Map for map of V12 (Purple or Blue) -> V1 Node Name
   */
  
  private Map<NetNode, V1Node> inverseMap(Map<V1Node, NetNode> v1NodeNameToV12Node) {
    Map<NetNode, V1Node> ret = new HashMap<NetNode, V1Node>();
    for (Map.Entry<V1Node, NetNode> entry : v1NodeNameToV12Node.entrySet()) {
      ret.put(entry.getValue(), entry.getKey());
    }
    return (ret);
  }
  
  /***************************************************************************
   **
   ** Convert Map to String Wrappers
   */
  
  private Map<ComboNode, Set<ComboNode>> convertToString(Map<NetNode, Set<NetNode>> nodeToNeighbors,
                                                         NetworkAlignment.NodeColorMap colorMap) {
    
    Map<ComboNode, Set<ComboNode>> ret = new HashMap<ComboNode, Set<ComboNode>>();
    
    for (Map.Entry<NetNode, Set<NetNode>> entry : nodeToNeighbors.entrySet()) {
      NetNode node = entry.getKey();
//      if (colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {
//        continue;
//      }
      Set<ComboNode> neighbors = new HashSet<ComboNode>();
      for (NetNode neighbor : entry.getValue()) {
//        if (colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {
//          continue;
//        }
        neighbors.add(new ComboNode(neighbor.getName()));
      }
      String name = entry.getKey().getName();
      ComboNode comboNode = new ComboNode(name);
      ret.put(comboNode, neighbors);
    }
    return (ret);
  }
  
  /***************************************************************************
   **
   ** Set intersection helper
   */
  
  private <T> void intersection(Set<T> one, Set<T> two, Set<T> result) {
    result.clear();
    result.addAll(one);
    result.retainAll(two);
    return;
  }
  
  /***************************************************************************
   **
   ** Set union helper
   */
  
  private <T> void union(Set<T> one, Set<T> two, Set<T> result) {
    result.clear();
    result.addAll(one);
    result.addAll(two);
    return;
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INNER CLASSES
  //
  ////////////////////////////////////////////////////////////////////////////
  
  private static class Greek {
    
    private final String name;
    
    Greek(String name) {
      this.name = name;
    }
  
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof Greek)) return false;
    
      Greek greek = (Greek) o;
  
      return name != null ? name.equals(greek.name) : greek.name == null;
    }
  
    @Override
    public int hashCode() {
      return name != null ? name.hashCode() : 0;
    }
    
  }
  
  /***************************************************************************
   **
   ** String Wrapper for Nodes in V1 (A as in "A::B")
   */
  
  private static class V1Node {
    
    private final String name;
    
    V1Node(String name) {
      this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof V1Node)) return false;
      
      V1Node v1Node = (V1Node) o;
      
      return name != null ? name.equals(v1Node.name) : v1Node.name == null;
    }
    
    @Override
    public int hashCode() {
      return name != null ? name.hashCode() : 0;
    }
  
    @Override
    public String toString() {
      return (name);
    }
  }
  
  /***************************************************************************
   **
   ** String Wrapper for Nodes in V12 (e.g. "A::B")
   */
  
  private static class ComboNode {
    
    private final String name;
    
    ComboNode(String name) {
      this.name = name;
    }
  
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof ComboNode)) return false;
    
      ComboNode comboNode = (ComboNode) o;
  
      return name != null ? name.equals(comboNode.name) : comboNode.name == null;
    }
  
    @Override
    public int hashCode() {
      return name != null ? name.hashCode() : 0;
    }
  
    @Override
    public String toString() {
      return (name);
    }
  }
  
  /****************************************************************************
   **
   ** String Utilities
   */
  
  private static class StringUtilities {
    
    /****************************************************************************
     **
     ** For "A::" and "A::B" this returns "A"
     */
    
    static String separateNodeOne(String in) {
      if (!in.contains("::")) {
        throw (new IllegalArgumentException("Node name needs ::"));
      }
      String ret = (in.split("::"))[0];
      return ret;
    }
    
    
  }
  
}
