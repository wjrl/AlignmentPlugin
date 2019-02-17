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

import java.util.Arrays;
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
  
//  private Map<ComboNode, Set<ComboNode>> nodeToNeighMain_, nodeToNeighPerfect_;
//  private Map<BareNode, NetNode> v1ToV12Main_, v1ToV12Perfect_;
//  private Map<NetNode, BareNode> v12ToV1Main_, v12ToV1Perfect_;
  
  private Map<NetNode, Set<NetLink>> nodeToLinksMain_;
  private Map<NetNode, Set<NetLink>> nodeToLinksPerfect_;
  
  
  Oracle oracle_;
  
  Set<NetLink> allLinksMain_;
  Set<NetNode> loneNodeIDsMain_;
  NetworkAlignment.NodeColorMap colorMapMain_;
  Set<NetLink> allLinksPerfect_;
  Set<NetNode> loneNodeIDsPerfect_;
  NetworkAlignment.NodeColorMap colorMapPerfect_;
  Map<NetNode, Set<NetNode>> nodeToNeighborsMain_;
  Map<NetNode, Set<NetNode>> nodeToNeighborsPerfect_;
  
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
                    Map<NetNode, Set<NetLink>> nodeToLinksMain,
                    Map<NetNode, Set<NetNode>> nodeToNeighborsPerfect,
                    Map<NetNode, Set<NetLink>> nodeToLinksPerfect,
                    final Double jaccSimThreshold,
                    BTProgressMonitor monitor) throws AsynchExitRequestException {
    
    
    this.allLinksMain_ = allLinksMain;
    this.loneNodeIDsMain_ = loneNodeIDsMain;
    this.colorMapMain_ = colorMapMain;
    this.nodeToNeighborsMain_ = nodeToNeighborsMain;
    this.nodeToLinksMain_ = nodeToLinksMain;
    this.allLinksPerfect_ = allLinksPerfect;
    this.loneNodeIDsPerfect_ = loneNodeIDsPerfect;
    this.colorMapPerfect_ = colorMapPerfect;
    this.nodeToNeighborsPerfect_ = nodeToNeighborsPerfect;
    this.nodeToLinksPerfect_ = nodeToLinksPerfect;
    this.jaccSimThreshold_ = jaccSimThreshold;
    this.monitor_ = monitor;
    
    this.oracle_ = new Oracle(allLinksMain, loneNodeIDsMain, colorMapMain,
            allLinksPerfect, loneNodeIDsPerfect, colorMapPerfect,
            nodeToNeighborsMain, nodeToLinksMain, nodeToNeighborsPerfect, nodeToLinksPerfect, jaccSimThreshold, monitor);
    this.oracle_.summon();
    
//    System.out.println(oracle_.allGreekNodes_);
//    System.out.println(oracle_.allGreekLinks_);
//
//    double totJ = 0.0;
//    int size  = 0;
//    for (BareNode node : oracle_.perfectToGreek_.keySet()) {
//      if (node.color == NetworkAlignment.NodeColor.RED) {
//        continue;
//      }
//      size ++;
//      GreekNode nodePer = oracle_.perfectToGreek_.get(node);
//      GreekNode nodeMain = oracle_.mainToGreek_.get(node);
//      totJ += jaccSimValue(nodeMain, nodePer);
//    }
//    double measure = totJ / size;
//    System.out.println(measure);
  
  
    return;
  }
  
  /***************************************************************************
   **
   ** @param node must be from V12 (Blue or Purple)
   */
  
  boolean isCorrectJS(NetNode node) {
    
//    BareNode v1Eq = v12ToV1Main_.get(node);
//
//    NetNode match = v1ToV12Perfect_.get(v1Eq);
//
//    ComboNode nodeEq = new ComboNode(node.getName()), matchEq = new ComboNode(match.getName());
//
//    double jsVal = jaccSimValue(nodeEq, matchEq);
//
//    if (jaccSimThreshold_ == null) {
//      throw new IllegalStateException("JS Threshold is null"); // should never happen
//    }
//
//    boolean isCorrect = Double.compare(jsVal, jaccSimThreshold_) >= 0;
//    return (isCorrect);

    throw (new IllegalStateException());
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
    
//    double totJ = 0.0;
//    for (BareNode graph1Node : v1ToV12Main_.keySet()) {
////      System.out.println(graph1Node + "   " + v1ToV12Main_.get(graph1Node));
//      NetNode nodeEqV12Main = v1ToV12Main_.get(graph1Node),
//              nodeEqV12Perfect = v1ToV12Perfect_.get(graph1Node);
//      ComboNode node = new ComboNode(nodeEqV12Main.getName()),
//              match = new ComboNode(nodeEqV12Perfect.getName());
//      Double val = jaccSimValue(node, match);
//      totJ += val;
//    }
//
//    double measure = totJ / v1ToV12Main_.size();
    double totJ = 0.0;
    int size  = 0;
    for (BareNode node : oracle_.perfectToGreek_.keySet()) {
      if (node.color == NetworkAlignment.NodeColor.RED) {
        continue;
      }
      size ++;
      GreekNode nodePer = oracle_.perfectToGreek_.get(node);
      GreekNode nodeMain = oracle_.mainToGreek_.get(node);
      totJ += jaccSimValue(nodeMain, nodePer);
    }
    double measure = totJ / size;
    System.out.println(measure);
    
    
    return (measure);
  }
  
  private double jaccSimValue(GreekNode node, GreekNode match) {
    int lenAdjust = 0;
    HashSet<GreekNode> scratchNode = new HashSet<GreekNode>(oracle_.nodeToNeighGreek_.get(node)),
            scratchMatch = new HashSet<GreekNode>(oracle_.nodeToNeighGreek_.get(match));
    
//    System.out.println(scratchNode);
//    System.out.println(scratchMatch);
//    System.out.println();
    
    if (scratchNode.contains(match)) {
      scratchNode.remove(match);
      scratchMatch.remove(node);
      lenAdjust = 1;
    }
    HashSet<GreekNode> union = new HashSet<GreekNode>(), intersect = new HashSet<GreekNode>();
    union(scratchNode, scratchMatch, union);
    intersection(scratchNode, scratchMatch, intersect);
    
    int iSize = intersect.size() + lenAdjust;
    int uSize = union.size() + lenAdjust;
    Double jaccard = (double) (iSize) / (double) uSize;
    if (jaccard.isNaN()) {  // case of 0/0 for two singletons
      jaccard = 1.0;
    }
    return (jaccard);
  }
  
  /***************************************************************************
   **
   ** Jaccard Similarity between two nodes in V12 of G12=(V12,E12);
   ** sigma(x,y) : V12 -> [0,1]                   (from the paper)
   ** node, match - both MUST be V12 nodes (Blue, Purple, or Red)
   */
  
//  private double jaccSimValue(ComboNode node, ComboNode match) {
//    int lenAdjust = 0;
//    HashSet<ComboNode> scratchNode = new HashSet<ComboNode>(nodeToNeighMain_.get(node)),
//            scratchMatch = new HashSet<ComboNode>(nodeToNeighPerfect_.get(match));
//
////    System.out.println(scratchNode);
////    System.out.println(scratchMatch);
////    System.out.println();
//
//
//    if (scratchNode.contains(match)) {
//      scratchNode.remove(match);
//      scratchMatch.remove(node);
//      lenAdjust = 1;
//    }
//    HashSet<ComboNode> union = new HashSet<ComboNode>(), intersect = new HashSet<ComboNode>();
//    union(scratchNode, scratchMatch, union);
//    intersection(scratchNode, scratchMatch, intersect);
//
//    int iSize = intersect.size() + lenAdjust;
//    int uSize = union.size() + lenAdjust;
//    Double jaccard = (double) (iSize) / (double) uSize;
//    if (jaccard.isNaN()) {  // case of 0/0 for two singletons
//      jaccard = 1.0;
//    }
//    return (jaccard);
//  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
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
  
  
  private static class Oracle {
    
    Set<NetLink> allLinksMain_;
    Set<NetNode> loneNodeIDsMain_;
    NetworkAlignment.NodeColorMap colorMapMain_;
    Set<NetLink> allLinksPerfect_;
    Set<NetNode> loneNodeIDsPerfect_;
    NetworkAlignment.NodeColorMap colorMapPerfect_;
    Map<NetNode, Set<NetNode>> nodeToNeighborsMain_;
    Map<NetNode, Set<NetNode>> nodeToNeighborsPerfect_;
    private Map<NetNode, Set<NetLink>> nodeToLinksMain_;
    private Map<NetNode, Set<NetLink>> nodeToLinksPerfect_;
    private BTProgressMonitor monitor_;
    
    static int counter = 0;
    
    Map<BareNode, GreekNode> mainToGreek_, perfectToGreek_;
    private Set<GreekNode> allGreekNodes_;
    private Set<GreekLink> allGreekLinks_;
    private Set<NetNode> misassignedBlueNodes_;
    private Set<GreekNode> loneGreekNodes_;
    private Map<GreekNode, Set<GreekNode>> nodeToNeighGreek_;
    
    Oracle(Set<NetLink> allLinksMain, Set<NetNode> loneNodeIDsMain,
                  NetworkAlignment.NodeColorMap colorMapMain,
                  Set<NetLink> allLinksPerfect, Set<NetNode> loneNodeIDsPerfect,
                  NetworkAlignment.NodeColorMap colorMapPerfect,
                  Map<NetNode, Set<NetNode>> nodeToNeighborsMain,
                  Map<NetNode, Set<NetLink>> nodeToLinksMain,
                  Map<NetNode, Set<NetNode>> nodeToNeighborsPerfect,
                  Map<NetNode, Set<NetLink>> nodeToLinksPerfect,
                  final Double jaccSimThreshold,
                  BTProgressMonitor monitor) {
      
      this.allLinksMain_ = allLinksMain;
      this.loneNodeIDsMain_ = loneNodeIDsMain;
      this.colorMapMain_ = colorMapMain;
      this.allLinksPerfect_ = allLinksPerfect;
      this.loneNodeIDsPerfect_ = loneNodeIDsPerfect;
      this.colorMapPerfect_ = colorMapPerfect;
      this.nodeToNeighborsMain_ = nodeToNeighborsMain;
      this.nodeToNeighborsPerfect_ = nodeToNeighborsPerfect;
      
      this.allGreekNodes_ = new HashSet<GreekNode>();
      this.allGreekLinks_ = new HashSet<GreekLink>();
      this.perfectToGreek_ = new HashMap<BareNode, GreekNode>();
      this.mainToGreek_ = new HashMap<BareNode, GreekNode>();
      this.loneGreekNodes_ = new HashSet<GreekNode>();
      this.monitor_ = monitor;
      this.nodeToLinksMain_ = nodeToLinksMain;
      this.nodeToLinksPerfect_ = nodeToLinksPerfect;
    }
    
    void summon() throws AsynchExitRequestException {
      createGreekMap();
      createGreekEdges();
      findLoners();
      createNeighborMap();
    }
    
    private void createGreekMap() throws AsynchExitRequestException {
      Set<NetNode> perfectNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinksPerfect_, loneNodeIDsPerfect_, monitor_);
      
      // This contains the G1 Part of the node name e.g. "A" (derived from "A::")
      Set<BareNode> blueNodesPerfect = new HashSet<BareNode>();
//      Map<BareNode, GreekNode> g2nodeToGreek = new HashMap<BareNode, GreekNode>();
      
      //
      // All aligned perfect alignment nodes (Blue and Purple) get a Greek ID
      //
      
      for (NetNode node : perfectNodes) {
//        if (colorMapPerfect_.getColor(node).equals(NetworkAlignment.NodeColor.RED)) {
//          continue;
//        }
        NetworkAlignment.NodeColor color = colorMapPerfect_.getColor(node);
        BareNode bareNode = BareNode.getNode(node, color);
        addGreekNode(bareNode, perfectToGreek_);
//        GreekNode greek = addGreekNode(bareNode, perfectToGreek_);

//        if (color.equals(NetworkAlignment.NodeColor.PURPLE)) {
//          g2nodeToGreek.put(bareNode, greek);
//        } else
        if (color.equals(NetworkAlignment.NodeColor.BLUE)) {
          blueNodesPerfect.add(bareNode);
        }
      }
      
      //
      // Mis-assigned blue nodes (should be purple) in test alignment are added too
      //
      
      misassignedBlueNodes_ = new HashSet<NetNode>();
      Set<NetNode> testNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinksMain_, loneNodeIDsMain_, monitor_);
      for (NetNode node : testNodes) {
//        if (colorMapMain_.getColor(node).equals(NetworkAlignment.NodeColor.RED)) {
//          continue;
//        }
        NetworkAlignment.NodeColor color = colorMapMain_.getColor(node);
        BareNode bareNode = BareNode.getNode(node, color);
        
        if (color.equals(NetworkAlignment.NodeColor.BLUE) && ! blueNodesPerfect.contains(bareNode)) { // Mis-assigned Blue nodes get a Greek ID
          addGreekNode(bareNode, mainToGreek_);
          misassignedBlueNodes_.add(node);
        } else {
          GreekNode match = perfectToGreek_.get(bareNode);
          mainToGreek_.put(bareNode, match);
        }
//        if (color.equals(NetworkAlignment.NodeColor.BLUE)) {
//          if (! blueNodesPerfect.contains(bareNode)) { // Mis-assigned Blue nodes get a Greek ID
//            addGreekNode(bareNode, mainToGreek_);
//          } else {                                  // Find Greek ID match for correctly Blue nodes
////            GreekNode match = perfectToGreek_.get(bareNode);
////            mainToGreek_.put(bareNode, match);
//          }

//        } else if (color.equals(NetworkAlignment.NodeColor.PURPLE)) { // Find Greek ID match for Purple nodes
////          BareNode v2Node = BareNode.getNode(node, color);
////          GreekNode match = g2nodeToGreek.get(v2Node);
//          GreekNode match = g2nodeToGreek.get(bareNode);
//          GreekNode match = perfectToGreek_.get(bareNode);
//          mainToGreek_.put(bareNode, match);
      }
      return;
    }
    
    private void addGreekNode(BareNode node, Map<BareNode, GreekNode> greekMap) {
      GreekNode greek = GreekNode.getNewGreek(node.name);
      greekMap.put(node, greek);
      allGreekNodes_.add(greek);
      return;
    }
    
    private void addGreekLink(NetLink link, Map<BareNode, GreekNode> greekMap, NetworkAlignment.NodeColorMap colorMap) {
      NetworkAlignment.NodeColor srcC = colorMap.getColor(link.getSrcNode()), trgC = colorMap.getColor(link.getTrgNode());
      BareNode srcV1 = BareNode.getNode(link.getSrcNode(), srcC), trgV1 = BareNode.getNode(link.getTrgNode(), trgC);
      GreekNode src = greekMap.get(srcV1), trg = greekMap.get(trgV1);
      
      GreekNode[] arr = {src, trg}; // This way src/trg order does not create two synonymous links
      Arrays.sort(arr);
      
      GreekLink greeklink = new GreekLink(arr[0], arr[1], link.getRelation());
      allGreekLinks_.add(greeklink);
      return;
    }
    
    private void createGreekEdges() {
      for (NetLink link : allLinksPerfect_) {
        if (!link.getRelation().equals(NetworkAlignment.EdgeType.FULL_ORPHAN_GRAPH1.tag)) {  // remove bBb edges under Perfect alignment
          addGreekLink(link, perfectToGreek_, colorMapPerfect_);
        }
      }
      for (NetNode node : misassignedBlueNodes_) {
        Set<NetLink> links = nodeToLinksMain_.get(node);
        for (NetLink link : links) {
          if (link.getRelation().equals(NetworkAlignment.EdgeType.HALF_ORPHAN_GRAPH1.tag)) { // add pBb edges under Test (main) alignment
            addGreekLink(link, mainToGreek_, colorMapMain_);
          }
        }
      }
      return;
    }
    
    private void findLoners() {
      Set<GreekNode> visited = new HashSet<GreekNode>();
      for (GreekLink link : allGreekLinks_) {
        visited.add(link.src);
        visited.add(link.trg);
      }
      for (GreekNode node : allGreekNodes_) {
        if (! visited.contains(node)) {
          loneGreekNodes_.add(node);
        }
      }
      return;
    }
    
    private void createNeighborMap() {
      this.nodeToNeighGreek_ = new HashMap<GreekNode, Set<GreekNode>>();
      for (GreekLink link : allGreekLinks_) {
        
        if (nodeToNeighGreek_.get(link.src) == null) {
          nodeToNeighGreek_.put(link.src, new HashSet<GreekNode>());
        }
        if (nodeToNeighGreek_.get(link.trg) == null) {
          nodeToNeighGreek_.put(link.trg, new HashSet<GreekNode>());
        }
        nodeToNeighGreek_.get(link.src).add(link.trg);
        nodeToNeighGreek_.get(link.trg).add(link.src);
      }
      for (GreekNode node : loneGreekNodes_) {
        nodeToNeighGreek_.put(node, new HashSet<GreekNode>());
      }
      return;
    }
    
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INNER CLASSES
  //
  ////////////////////////////////////////////////////////////////////////////
  
  private static String[] greek = {"alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "iota"};
  
  private static class GreekNode implements Comparable {
    
    private final String name;
    
    GreekNode(String name) {
      this.name = name;
    }
    
    static GreekNode getNewGreek(String name) {
//      return new GreekNode(name + " " + (Oracle.counter++) + "");
//      return new GreekNode(greek[Oracle.counter++]);
      return (new GreekNode((Oracle.counter++) + ""));
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof GreekNode)) return false;
      
      GreekNode greek = (GreekNode) o;
      
      return name != null ? name.equals(greek.name) : greek.name == null;
    }
    
    @Override
    public int hashCode() {
      return name != null ? name.hashCode() : 0;
    }
  
    @Override
    public String toString() {
      return (name);
    }
  
    @Override
    public int compareTo(Object o) {
      GreekNode gn = (GreekNode) o;
      return name.compareTo(gn.name);
    }
    
  }
  
  private static class GreekLink {
    
    final GreekNode src, trg;
    final String rel;
    
    public GreekLink(GreekNode src, GreekNode trg, String rel) {
      if (src == null || trg == null || rel == null) {
        throw (new IllegalArgumentException("Null in GreekLink"));
      }
      this.src = src;
      this.trg = trg;
      this.rel = rel;
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof GreekLink)) return false;
      
      GreekLink greekLink = (GreekLink) o;
      
      if (! src.equals(greekLink.src)) return false;
      if (! trg.equals(greekLink.trg)) return false;
      return rel.equals(greekLink.rel);
    }
    
    @Override
    public int hashCode() {
      int result = src.hashCode();
      result = 31 * result + trg.hashCode();
      result = 31 * result + rel.hashCode();
      return result;
    }
    
  }
  
  /***************************************************************************
   **
   ** String Wrapper for Nodes in V1 (A as in "A::B")
   */
  
  private static class BareNode { // note COLOR does not impact equalness
    
    final String name;
    final NetworkAlignment.NodeColor color;
    
    BareNode(String name, NetworkAlignment.NodeColor color) {
      if (name == null) {
        throw (new IllegalArgumentException());
      }
      this.name = name;
      this.color = color;
    }
    
//    @Override
//    public boolean equals(Object o) {
//      if (this == o) return true;
//      if (! (o instanceof BareNode)) return false;
//      BareNode bareNode = (BareNode) o;
//      if (! name.equals(bareNode.name)) return false;
//      return color == bareNode.color;
//    }
//
//    @Override
//    public int hashCode() {
//      int result = name.hashCode();
//      result = 31 * result + (color != null ? color.hashCode() : 0);
//      return result;
//    }
  
  
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof BareNode)) return false;
    
      BareNode node = (BareNode) o;
  
      return name.equals(node.name);
    }
  
    @Override
    public int hashCode() {
      return name.hashCode();
    }
  
    @Override
    public String toString() {
      return (name);
    }
    
    static BareNode getNode(NetNode node, NetworkAlignment.NodeColor type) {
      String nodeStr;
      if (type == NetworkAlignment.NodeColor.BLUE || type == NetworkAlignment.NodeColor.PURPLE) {
        nodeStr = StringUtilities.separateNodeOne(node.getName());
      } else if (type == NetworkAlignment.NodeColor.RED) {
        nodeStr = StringUtilities.separateNodeTwo(node.getName());
      } else {
        throw (new IllegalArgumentException("incorrect NodeType"));
      }
      return (new BareNode(nodeStr, type));
    }
    
  }
  
//  /***************************************************************************
//   **
//   ** String Wrapper for Nodes in V12 (e.g. "A::B")
//   */
//
//  private static class ComboNode {
//
//    private final String name;
//
//    ComboNode(String name) {
//      this.name = name;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//      if (this == o) return true;
//      if (! (o instanceof ComboNode)) return false;
//
//      ComboNode comboNode = (ComboNode) o;
//
//      return name != null ? name.equals(comboNode.name) : comboNode.name == null;
//    }
//
//    @Override
//    public int hashCode() {
//      return name != null ? name.hashCode() : 0;
//    }
//
//    @Override
//    public String toString() {
//      return (name);
//    }
//  }
  
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
      if (! in.contains("::")) {
        throw (new IllegalArgumentException("Node name needs ::"));
      }
      String ret = (in.split("::"))[0];
      return (ret);
    }
    
    static String separateNodeTwo(String in) {
      if (! in.contains("::")) {
        throw (new IllegalArgumentException("Node name needs ::"));
      }
      String ret = (in.split("::"))[1];
      return (ret);
    }
    
    
  }
  
}


//  /****************************************************************************
//   **
//   ** Create map : String -> Graph 1 node (blue or purple)
//   */
//
//  private Map<V1Node, NetNode> findGraphOneNodes(Set<NetLink> allLinks, Set<NetNode> loneNodeIDs,
//                                                 NetworkAlignment.NodeColorMap colorMap)
//          throws AsynchExitRequestException {
//
//    Map<V1Node, NetNode> ret = new HashMap<V1Node, NetNode>();
//
//    Set<NetNode> allNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinks, loneNodeIDs, monitor_);
//    for (NetNode node : allNodes) {
//      if (colorMap.getColor(node) == NetworkAlignment.NodeColor.PURPLE ||
//              colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {
//        String g1name = StringUtilities.separateNodeOne(node.getName());
//        V1Node v1Node = new V1Node(g1name);
//        ret.put(v1Node, node);
//      }
//    }
//    return (ret);
//  }
//
//  /***************************************************************************
//   **
//   ** Flip Map for map of V12 (Purple or Blue) -> V1 Node Name
//   */
//
//  private Map<NetNode, V1Node> inverseMap(Map<V1Node, NetNode> v1NodeNameToV12Node) {
//    Map<NetNode, V1Node> ret = new HashMap<NetNode, V1Node>();
//    for (Map.Entry<V1Node, NetNode> entry : v1NodeNameToV12Node.entrySet()) {
//      ret.put(entry.getValue(), entry.getKey());
//    }
//    return (ret);
//  }

//  /***************************************************************************
//   **
//   ** Convert Map to Greek for Oracle
//   ** NO Blue Nodes
//   */
//
//  private Map<Greek, Set<Greek>> createGreekMap(Map<NetNode, Set<NetNode>> nodeToNeighbors, NetworkAlignment.NodeColorMap colorMap) {
//
//    ;
//
//
////    Map<Greek, Set<Greek>> ret = new HashMap<Greek, Set<Greek>>();
////
////    for (Map.Entry<NetNode, Set<NetNode>> entry : nodeToNeighbors.entrySet()) {
////      NetNode node = entry.getKey();
////
//////      if (colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {
//////        continue;
//////      }
////      Set<Greek> neighbors = new HashSet<Greek>();
////      for (NetNode neighbor : entry.getValue()) {
//////        if (colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {
//////          continue;
//////        }
////        if (colorMap.getColor(node) == NetworkAlignment.NodeColor.BLUE) {  // this eliminates 'bBb' edges while retaining the B nodes
////          continue;
////        }
////        neighbors.add(perfectToGreek_.get(neighbor));
////      }
////      ret.put(perfectToGreek_.get(node), neighbors);
////    }
//    return (null);
//  }

//  private static class V2Node {
//
//    final String name;
//
//    V2Node(String name) {
//      this.name = name;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//      if (this == o) return true;
//      if (! (o instanceof V2Node)) return false;
//
//      V2Node v2Node = (V2Node) o;
//
//      return name != null ? name.equals(v2Node.name) : v2Node.name == null;
//    }
//
//    @Override
//    public int hashCode() {
//      return name != null ? name.hashCode() : 0;
//    }
//
//    @Override
//    public String toString() {
//      return (name);
//    }
//
//    static V2Node getV2Node(NetNode node) {
//      String g2nodeStr = StringUtilities.separateNodeTwo(node.getName());
//      return (new V2Node(g2nodeStr));
//    }
//
//  }


//  private void createGreekMap() throws AsynchExitRequestException {
//    Set<NetNode> perfectNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinksPerfect_, loneNodeIDsPerfect_, monitor_);
//
//    // This contains the G1 Part of the node name e.g. "A" (derived from "A::")
//    Set<V1Node> blueNodesPerfect = new HashSet<V1Node>();
//    Map<String, GreekNode> g2nodeToGreek = new HashMap<String, GreekNode>();
//
//    //
//    // All aligned perfect alignment nodes (Blue and Purple) get a Greek ID
//    //
//
//    for (NetNode node : perfectNodes) {
//      if (colorMapPerfect_.getColor(node).equals(NetworkAlignment.NodeColor.RED)) {
//        continue;
//      }
//
//      V1Node v1Node = V1Node.getV1Node(node);
//
////      GreekNode greek = GreekNode.getNewGreek();
////      perfectToGreek_.put(v1Node, greek);
////      allGreekNodes_.add(greek);
//      GreekNode greek = addGreekNode(v1Node, perfectToGreek_);
//
//      if (colorMapPerfect_.getColor(node).equals(NetworkAlignment.NodeColor.PURPLE)) {
//        String g2node = StringUtilities.separateNodeTwo(node.getName());
//        g2nodeToGreek.put(g2node, greek);
//      } else if (colorMapPerfect_.getColor(node).equals(NetworkAlignment.NodeColor.BLUE)) {
//        blueNodesPerfect.add(v1Node);
//      }
//    }
//
//    //
//    // Mis-assigned blue nodes (should be purple) in test alignment are added too
//    //
//
//    misassignedBlueNodes_ = new HashSet<NetNode>();
//    Set<NetNode> testNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinksMain_, loneNodeIDsMain_, monitor_);
//    for (NetNode node : testNodes) {
//      if (colorMapMain_.getColor(node).equals(NetworkAlignment.NodeColor.RED)) {
//        continue;
//      }
//
//      V1Node v1Node = V1Node.getV1Node(node);
//
//      if (colorMapMain_.getColor(node).equals(NetworkAlignment.NodeColor.BLUE)) {
//        if (! blueNodesPerfect.contains(v1Node)) { // Mis-assigned Blue nodes get a Greek ID
////          greek = GreekNode.getNewGreek();
////          allGreekNodes_.add(greek);
////          misassignedBlueNodes_.add(node);
//          addGreekNode(v1Node, mainToGreek_);
//        } else {                                  // Find Greek ID match for correctly Blue nodes
//          GreekNode greek = perfectToGreek_.get(v1Node);
//          mainToGreek_.put(v1Node, greek);
//        }
//
//      } else if (colorMapMain_.getColor(node).equals(NetworkAlignment.NodeColor.PURPLE)) { // Find Greek ID match for Purple nodes
//        String g2nodeStr = StringUtilities.separateNodeTwo(node.getName());
//        GreekNode match = g2nodeToGreek.get(g2nodeStr);
//        mainToGreek_.put(v1Node, match);
//      }
//    }
//    return;
//  }
//
//  private GreekNode addGreekNode(V1Node node, Map<V1Node, GreekNode> map) {
//    GreekNode greek = GreekNode.getNewGreek();
//    map.put(node, greek);
//    allGreekNodes_.add(greek);
//    return (greek);
//  }
//
//  private void addGreekLink(NetLink link, Map<V1Node, GreekNode> map) {
//    V1Node srcV1 = V1Node.getV1Node(link.getSrcNode()), trgV1 = V1Node.getV1Node(link.getTrgNode());
//    GreekNode src = map.get(srcV1), trg = map.get(trgV1);
//
//    GreekNode[] arr = {src, trg};
//    Arrays.sort(arr);
//
//    GreekLink greeklink = new GreekLink(arr[0], arr[1], link.getRelation());
//    allGreekLinks_.add(greeklink);
//    return;
//  }
//
//  private void createGreekEdges() {
//
//    for (NetLink link : allLinksPerfect_) {
//      if (link.getRelation().equals(NetworkAlignment.EdgeType.FULL_ORPHAN_GRAPH1.tag)) {   // remove bBb edges under Perfect alignment
//        continue;
//      }
//      addGreekLink(link, perfectToGreek_);
//    }
//
//    for (NetNode node : misassignedBlueNodes_) {
//      Set<NetLink> links = nodeToLinksMain_.get(node);
//      for (NetLink link : links) {
//        if (link.getRelation().equals(NetworkAlignment.EdgeType.HALF_ORPHAN_GRAPH1.tag)) { // add pBb edges under Test (main) alignment
//          addGreekLink(link, mainToGreek_);
//        }
//      }
//    }
//    return;
//  }

