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
  
  private Set<NetLink> allLinksMain_, allLinksPerfect_;
  private Set<NetNode> loneNodeIDsMain_, loneNodeIDsPerfect_;
  private NetworkAlignment.NodeColorMap colorMapMain_, colorMapPerfect_;
  private Map<NetNode, Set<NetNode>> nodeToNeighborsMain_, nodeToNeighborsPerfect_;
  private Map<NetNode, Set<NetLink>> nodeToLinksMain_, nodeToLinksPerfect_;
  private BTProgressMonitor monitor_;
  
  private final Double jaccSimThreshold_;
  private OracleNetwork oracle_;
  
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
    this.monitor_ = monitor;
    this.jaccSimThreshold_ = jaccSimThreshold;
    
    this.oracle_ = new OracleNetwork(allLinksMain, loneNodeIDsMain, colorMapMain,
            allLinksPerfect, loneNodeIDsPerfect, colorMapPerfect,
            nodeToNeighborsMain, nodeToLinksMain, nodeToNeighborsPerfect, nodeToLinksPerfect, monitor);
    this.oracle_.summon();
    return;
  }
  
  /***************************************************************************
   **
   ** @param node must be from V12 (Blue or Purple)
   */
  
  boolean isCorrectJS(NetNode node) {
    BareNode part1 = BareNode.getNode(node, colorMapMain_.getColor(node));
    GreekNode main = oracle_.mainToGreek_.get(part1), match = oracle_.perfectToGreek_.get(part1);
    double jsVal = jaccSimValue(main, match);
    if (jaccSimThreshold_ == null) {
      throw new IllegalStateException("JS Threshold is null"); // should never happen
    }
    boolean isCorrect = Double.compare(jsVal, jaccSimThreshold_) >= 0;
    return (isCorrect);
  }
  
  /****************************************************************************
   **
   ** Jaccard Similarity Measure - Historically Adapted from NodeEQC.java
   ** For Blue Nodes Case - Now uses 'oracle' network
   */
  
  double calcScore() {
    double totJ = 0.0;
    int size = 0;
    for (BareNode node : oracle_.perfectToGreek_.keySet()) {
      if (node.type == GraphType.GRAPH2) {
        continue;
      }
      size++;
      GreekNode nodePer = oracle_.perfectToGreek_.get(node);
      GreekNode nodeMain = oracle_.mainToGreek_.get(node);
      totJ += jaccSimValue(nodeMain, nodePer);
    }
    double measure = totJ / size;
    return (measure);
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /***************************************************************************
   **
   ** JS value between two nodes; [sigma(x, y) in paper]
   */
  
  private double jaccSimValue(GreekNode node, GreekNode match) {
    int lenAdjust = 0;
    HashSet<GreekNode> scratchNode = new HashSet<GreekNode>(oracle_.nodeToNeighGreek_.get(node)),
            scratchMatch = new HashSet<GreekNode>(oracle_.nodeToNeighGreek_.get(match));

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
  
  private enum GraphType {GRAPH1, GRAPH2}
  
  /****************************************************************************
   **
   ** The ORACLE network that allows us to compare Test and Perfect alignment
   ** under one namespace. It uses abstractions of 'Greek' nodes and links.
   **
   ** The oracle consists of:
   **    a) G1 union G2 under the perfect alignment, *removing all bBb edges*, plus
   **    b) All unaligned blue nodes under the test alignment that are *supposed* to
   **       be purple under the perfect alignment, plus the pBb edges incident on those nodes.
   */
  
  private static class OracleNetwork {
    
    static int counter = 0; // This allows to create GreekNodes
    
    //
    // Perfect and Main alignment Data Structures
    //
    
    private Set<NetLink> allLinksMain_, allLinksPerfect_;
    private Set<NetNode> loneNodeIDsMain_, loneNodeIDsPerfect_;
    private NetworkAlignment.NodeColorMap colorMapMain_, colorMapPerfect_;
    private Map<NetNode, Set<NetNode>> nodeToNeighborsMain_, nodeToNeighborsPerfect_;
    private Map<NetNode, Set<NetLink>> nodeToLinksMain_, nodeToLinksPerfect_;
    private BTProgressMonitor monitor_;
    
    //
    // Greek Data Structures
    //
    
    Map<BareNode, GreekNode> mainToGreek_, perfectToGreek_;
    private Set<GreekNode> allGreekNodes_;
    private Set<GreekLink> allGreekLinks_;
    private Set<NetNode> misassignedBlueNodes_;
    private Set<GreekNode> loneGreekNodes_;
    private Map<GreekNode, Set<GreekNode>> nodeToNeighGreek_;
    
    OracleNetwork(Set<NetLink> allLinksMain, Set<NetNode> loneNodeIDsMain,
                  NetworkAlignment.NodeColorMap colorMapMain,
                  Set<NetLink> allLinksPerfect, Set<NetNode> loneNodeIDsPerfect,
                  NetworkAlignment.NodeColorMap colorMapPerfect,
                  Map<NetNode, Set<NetNode>> nodeToNeighborsMain,
                  Map<NetNode, Set<NetLink>> nodeToLinksMain,
                  Map<NetNode, Set<NetNode>> nodeToNeighborsPerfect,
                  Map<NetNode, Set<NetLink>> nodeToLinksPerfect,
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
      this.nodeToNeighGreek_ = new HashMap<GreekNode, Set<GreekNode>>();
      this.monitor_ = monitor;
      this.nodeToLinksMain_ = nodeToLinksMain;
      this.nodeToLinksPerfect_ = nodeToLinksPerfect;
    }
    
    void summon() throws AsynchExitRequestException {
      // Create the Greek nodes and fill the maps from Main (test) and Perfect alignment to Greek Nodes
      createGreekMap();
      createGreekEdges();
      findLoners();
      createNeighborMap();
      return;
    }
  
    /***************************************************************************
     **
     ** Create Greek Nodes and Maps for Main and Perfect Alignment
     **
     **   -In order to calculate JS - I need maps : G1 -> Greek for Main & Perfect
     **     so I can iterate through the key set and get the respective GreekNodes;
     *      These maps are instance fields
     *    - In order to properly match Main's Purple nodes to their Greek ID, I need
     *      to create a map : G2 -> Greek of Perfect, so I can use it while creating
     *      Main's map : G1 -> Greek;
     *    - To find which Greek ID (A::B)t (t for test/main) gets, I need to know which
     *      Greek ID (X::B)p (p for perfect) gets; hence the need for G2-> Greek map
     *      based on Perfect;
     *      This map is a local variable;
     *      This map here also contains G1's Greek Nodes under Perfect for the sake of
     *      simplicity
     */
    
    private void createGreekMap() throws AsynchExitRequestException {
      Set<NetNode> perfectNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinksPerfect_, loneNodeIDsPerfect_, monitor_);
      
      // This contains the G1 Part of the node name e.g. "A" (derived from "A::")
      Set<BareNode> blueNodesPerfect = new HashSet<BareNode>();
      Map<BareNode, GreekNode> perfectPart2toGreek = new HashMap<BareNode, GreekNode>();
      
      //
      // All aligned perfect alignment nodes (Blue and Purple, and even Red (for sake of implementation)) get a Greek ID
      //
      
      for (NetNode node : perfectNodes) {
        NetworkAlignment.NodeColor color = colorMapPerfect_.getColor(node);
        BareNode bareNode = BareNode.getNode(node, color);
        
        GreekNode greekNode = addGreekNode(bareNode, perfectToGreek_);
        
        if (color.equals(NetworkAlignment.NodeColor.BLUE)) {
          blueNodesPerfect.add(bareNode);
        }
        
        BareNode secondPart;  // for (A::) and (::C) this is A and C, respectively; for (A::B) this is B - we want the second part
                              // This is for when we get a Purple node in the main (test) alignment and we need to match it to its Greek ID
        if (color.equals(NetworkAlignment.NodeColor.PURPLE)) {
          secondPart = BareNode.getNode(node, NetworkAlignment.NodeColor.RED);
        } else {
          secondPart = bareNode;  // For (A::) and (::C) secondPart is just A and C, respectively
        }
        perfectPart2toGreek.put(secondPart, greekNode);
      }
      
      //
      // Mis-assigned (should be purple) Blue nodes in test alignment get Greek IDs too
      //
      
      misassignedBlueNodes_ = new HashSet<NetNode>();
      Set<NetNode> mainNodes = PluginSupportFactory.getBuildExtractor().extractNodes(allLinksMain_, loneNodeIDsMain_, monitor_);
      for (NetNode node : mainNodes) {
        NetworkAlignment.NodeColor color = colorMapMain_.getColor(node);
        BareNode bareNode = BareNode.getNode(node, color);
        
        if (color.equals(NetworkAlignment.NodeColor.BLUE) && ! blueNodesPerfect.contains(bareNode)) { // Mis-assigned Blue nodes get new Greek ID
          addGreekNode(bareNode, mainToGreek_);
          misassignedBlueNodes_.add(node);
        } else {
          BareNode secondPart;
          if (color.equals(NetworkAlignment.NodeColor.PURPLE)) {
            secondPart = BareNode.getNode(node, NetworkAlignment.NodeColor.RED);
          } else {
            secondPart = bareNode;
          }
          GreekNode match = perfectPart2toGreek.get(secondPart);
          mainToGreek_.put(bareNode, match);
        }
      }
      return;
    }
    
    private GreekNode addGreekNode(BareNode node, Map<BareNode, GreekNode> greekMap) {
      GreekNode greek = GreekNode.getNewGreek();
      greekMap.put(node, greek);
      allGreekNodes_.add(greek);
      return (greek);
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
  
    /***************************************************************************
     **
     ** Create edge list based off Greek methodology;
     */
    
    private void createGreekEdges() {
      for (NetLink link : allLinksPerfect_) {
        if (! link.getRelation().equals(NetworkAlignment.EdgeType.FULL_ORPHAN_GRAPH1.tag)) {  // remove bBb edges under Perfect alignment
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
  
    /***************************************************************************
     **
     ** For simplicity, I ditch having loners and edge set; Oracle initially has
     ** edge set and list of all nodes; node in list that doesn't appear in edge
     ** set is obviously a singleton (same philosophy as SIF files)
     */
    
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
  
    /***************************************************************************
     **
     ** Create neighbor map
     */
    
    private void createNeighborMap() {
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
  
  /****************************************************************************
   **
   ** Abstraction Node for Oracle Network
   */
  
  private static class GreekNode implements Comparable {
    
    final String name;
    
    GreekNode(String name) {
      if (name == null) {
        throw (new IllegalArgumentException("Null in GreekNode"));
      }
      this.name = name;
    }
    
    static GreekNode getNewGreek() {
//      return new GreekNode(greek[Oracle.counter++]);
      return (new GreekNode((OracleNetwork.counter++) + ""));
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof GreekNode)) return false;
      GreekNode greek = (GreekNode) o;
      return (name.equals(greek.name));
    }
    
    @Override
    public int hashCode() {
      return (name.hashCode());
    }
    
    @Override
    public String toString() {
      return (name);
    }
    
    @Override
    public int compareTo(Object o) {
      GreekNode gn = (GreekNode) o;
      return (name.compareTo(gn.name));
    }
    
  }
  
  /****************************************************************************
   **
   ** Abstraction Link for Oracle Network
   */
  
  private static class GreekLink {
    
    final GreekNode src, trg;
    final String rel;
    
    GreekLink(GreekNode src, GreekNode trg, String rel) {
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
   ** String Wrapper for Nodes in V1 and V2 ("A" as in "A::B", or "B" in "A::B")
   */
  
  private static class BareNode {
    
    final String name;
    final GraphType type;
    
    BareNode(String name, NetworkAlignment.NodeColor color) {
      if (name == null) {
        throw (new IllegalArgumentException());
      }
      this.name = name;
      if (color == NetworkAlignment.NodeColor.PURPLE || color == NetworkAlignment.NodeColor.BLUE) {
        this.type = GraphType.GRAPH1;
      } else if (color == NetworkAlignment.NodeColor.RED) {
        this.type = GraphType.GRAPH2;
      } else {
        throw (new IllegalArgumentException());
      }
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (! (o instanceof BareNode)) return false;
      BareNode node = (BareNode) o;
      return (name.equals(node.name));
    }
    
    @Override
    public int hashCode() {
      return (name.hashCode());
    }
    
    @Override
    public String toString() {
      return (name);
    }
    
    /***************************************************************************
     **
     ** Create BareNode from a NetNode given which Graph (1 or 2) this node will be from or
     ** where to make the 'incision' for a Purple node: (A::B) can be A or B depends on specification
     */
    
    static BareNode getNode(NetNode node, NetworkAlignment.NodeColor incision) {
      String nodeStr;
      if (incision == NetworkAlignment.NodeColor.BLUE || incision == NetworkAlignment.NodeColor.PURPLE) {
        nodeStr = StringUtilities.separateNodeOne(node.getName());
      } else if (incision == NetworkAlignment.NodeColor.RED) {
        nodeStr = StringUtilities.separateNodeTwo(node.getName());
      } else {
        throw (new IllegalArgumentException("incorrect NodeType"));
      }
      return (new BareNode(nodeStr, incision));
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
