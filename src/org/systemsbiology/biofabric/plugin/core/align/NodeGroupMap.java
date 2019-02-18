/*
**
**    Copyright (C) 2018-2019 Rishi Desai
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.systemsbiology.biofabric.api.io.BuildData;
import org.systemsbiology.biofabric.api.model.NetLink;
import org.systemsbiology.biofabric.api.model.NetNode;
import org.systemsbiology.biofabric.api.worker.AsynchExitRequestException;
import org.systemsbiology.biofabric.api.worker.BTProgressMonitor;
import org.systemsbiology.biofabric.api.worker.LoopReporter;
import org.systemsbiology.biofabric.io.BuildExtractorImpl;
import org.systemsbiology.biofabric.plugin.PluginSupportFactory;

/***************************************************************************
 **
 ** HashMap based Data structure
 **
 **
 ** LG = LINK GROUP
 **
 ** FIRST LG   = PURPLE EDGES           // COVERERED EDGE
 ** SECOND LG  = BLUE EDGES             // INDUCED_GRAPH1
 ** THIRD LG   = CYAN EDGES             // HALF_ORPHAN_GRAPH1       (TECHNICALLY BLUE EDGES)
 ** FOURTH LG  = GREEN EDGES            // FULL_ORPHAN_GRAPH1       (TECHNICALLY BLUE EDGES)
 ** FIFTH LG   = RED EDGES              // INDUCED_GRAPH2
 ** SIXTH LG   = ORANGE EDGES           // HALF_UNALIGNED_GRAPH2    (TECHNICALLY RED EDGES)
 ** SEVENTH LG = YELLOW EDGES           // FULL_UNALIGNED_GRAPH2    (TECHNICALLY RED EDGES)
 **
 ** PURPLE NODE =  ALIGNED NODE
 ** BLUE NODE   =  ORPHAN (UNALIGNED) BLUE NODE
 ** RED NODE    =  UNALIGNED NODE
 **
 **
 ** WE HAVE 40 DISTINCT CLASSES (NODE GROUPS) FOR EACH ALIGNED AND UNALIGNED NODE
 **
 */

public class NodeGroupMap {
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  public enum PerfectNGMode {
    NONE, NODE_CORRECTNESS, JACCARD_SIMILARITY
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE INSTANCE MEMBERS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  private final PerfectNGMode mode_;
  private Set<NetLink> links_;
  private Set<NetNode> loners_;
  private Map<NetNode, Boolean> mergedToCorrectNC_;
  private NetworkAlignment.NodeColorMap nodeColorMap_;
  
  private Map<NetNode, Set<NetLink>> nodeToLinks_;
  private Map<NetNode, Set<NetNode>> nodeToNeighbors_;
  
  private Map<GroupID, Integer> groupIDtoIndex_;
  private Map<Integer, GroupID> indexToGroupID_;
  private Map<GroupID, String> groupIDtoColor_;
  private final int numGroups_;
  
  private Map<String, Double> nodeGroupRatios_, linkGroupRatios_;
  private JaccardSimilarity funcJS_;
  private BTProgressMonitor monitor_;
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTORS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  public NodeGroupMap(BuildData bd, String[] nodeGroupOrder, String[][] colorMap,
                      BTProgressMonitor monitor) throws AsynchExitRequestException {
    this(bd.getLinks(), 
    		 bd.getSingletonNodes(), 
    		 ((NetworkAlignmentBuildData)bd.getPluginBuildData()).mapG1toG2, 
    		 ((NetworkAlignmentBuildData)bd.getPluginBuildData()).perfectG1toG2, 
    		 ((NetworkAlignmentBuildData)bd.getPluginBuildData()).linksLarge, 
    		 ((NetworkAlignmentBuildData)bd.getPluginBuildData()).lonersLarge,
             ((NetworkAlignmentBuildData)bd.getPluginBuildData()).mergedToCorrectNC,
             ((NetworkAlignmentBuildData)bd.getPluginBuildData()).nodeColorMap,
             ((NetworkAlignmentBuildData)bd.getPluginBuildData()).mode,
             ((NetworkAlignmentBuildData)bd.getPluginBuildData()).jaccSimThreshold,
         nodeGroupOrder, 
         colorMap, 
         monitor);
  }
  
  public NodeGroupMap(Set<NetLink> allLinks, Set<NetNode> loneNodeIDs,
                      Map<NetNode, NetNode> mapG1toG2, Map<NetNode, NetNode> perfectG1toG2,
                      ArrayList<NetLink> linksLarge, HashSet<NetNode> lonersLarge,
                      Map<NetNode, Boolean> mergedToCorrectNC,
                      NetworkAlignment.NodeColorMap nodeColorMap,
                      PerfectNGMode mode, final Double jaccSimThreshold,
                      String[] nodeGroupOrder, String[][] colorMap,
                      BTProgressMonitor monitor) throws AsynchExitRequestException {
    this.links_ = allLinks;
    this.loners_ = loneNodeIDs;
    this.mergedToCorrectNC_ = mergedToCorrectNC;
    this.nodeColorMap_ = nodeColorMap;
    this.numGroups_ = nodeGroupOrder.length;
    this.mode_ = mode;
    if (mode == PerfectNGMode.JACCARD_SIMILARITY) {
//      this.funcJS_ = new JaccardSimilarity(allLinks, loneNodeIDs, nodeColorMap, jaccSimThreshold, monitor);
    }
    this.monitor_ = monitor;
    this.nodeToNeighbors_ = new HashMap<NetNode, Set<NetNode>>();
    this.nodeToLinks_ = new HashMap<NetNode, Set<NetLink>>();
    PluginSupportFactory.getBuildExtractor().createNeighborLinkMap(links_, loners_, nodeToNeighbors_, nodeToLinks_, monitor_);
    generateOrderMap(nodeGroupOrder);
    generateColorMap(colorMap);
    calcNGRatios();
    calcLGRatios();
    return;
  }
  
  //////////////////////////////////////////////////////////////////////////
  //
  //  PRIVATE METHODS
  //
  //////////////////////////////////////////////////////////////////////////
  
  private void generateOrderMap(String[] nodeGroupOrder) {
    groupIDtoIndex_ = new HashMap<GroupID, Integer>();
    indexToGroupID_ = new HashMap<Integer, GroupID>();
    for (int index = 0; index < nodeGroupOrder.length; index++) {
      GroupID gID = new GroupID(nodeGroupOrder[index]);
      groupIDtoIndex_.put(gID, index);
      indexToGroupID_.put(index, gID);
    }
    return;
  }
  
  private void generateColorMap(String[][] colorMap) {
    groupIDtoColor_ = new HashMap<GroupID, String>();
    
    for (String[] ngCol : colorMap) {
      GroupID groupID = new GroupID(ngCol[0]);
      String color = ngCol[1];
      groupIDtoColor_.put(groupID, color);
    }
    return;
  }
  
  /***************************************************************************
   **
   ** Hash function
   */
  
  private GroupID generateID(NetNode node) {
    //
    // See which types of link groups the node's links are in
    //
    
    boolean[] inLG = new boolean[NetworkAlignment.LINK_GROUPS.length];
    
    
    for (NetLink link : nodeToLinks_.get(node)) {
      for (int rel = 0; rel < inLG.length; rel++) {
        if (link.getRelation().equals(NetworkAlignment.LINK_GROUPS[rel].tag)) {
          inLG[rel] = true;
        }
      }
    }
  
    List<String> tags = new ArrayList<String>();
    
    for (NetworkAlignment.EdgeType type : NetworkAlignment.LINK_GROUPS) {
      if (inLG[type.index]) {
        tags.add(type.tag);
      }
    }
    
    if (tags.isEmpty()) { // singletons
      tags.add("0");
    }
    
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    sb.append(nodeColorMap_.getColor(node).tag);  // node color (P,B,R)
    sb.append(":");
  
    for (int i = 0; i < tags.size(); i++) {
      sb.append(tags.get(i));    // link group tags
      if (i != tags.size() - 1) {
        sb.append("/");
      }
    }
    
    if (mode_ != PerfectNGMode.NONE) {   // perfect NG mode is activated
      sb.append("/");
      if (mergedToCorrectNC_.get(node) == null) {   // red node
        sb.append(0);
      } else {
        boolean isCorrect;
        if (mode_ == PerfectNGMode.NODE_CORRECTNESS) {
          isCorrect = mergedToCorrectNC_.get(node);
        } else if (mode_ == PerfectNGMode.JACCARD_SIMILARITY) {
          throw (new IllegalStateException());
//          isCorrect = funcJS_.isCorrectJS(node);
        } else {
          throw new IllegalStateException("Incorrect mode for Perfect NGs Group Map");
        }
        sb.append((isCorrect) ? 1 : 0);
      }
    }
    sb.append(")");
    
    return (new GroupID(sb.toString()));
  }
  
  /***************************************************************************
   **
   ** Calculate node group size to total #nodes for each group
   */
  
  private void calcNGRatios() {
    Set<NetNode> nodes = nodeToLinks_.keySet();
    double size = nodes.size();
    Set<GroupID> tags = groupIDtoIndex_.keySet();
  
    Map<GroupID, Integer> counts = new HashMap<GroupID, Integer>(); // initial vals
    for (GroupID gID : tags) {
      counts.put(gID, 0);
    }
    
    for (NetNode node : nodes) {
      GroupID gID = generateID(node);
      counts.put(gID, counts.get(gID) + 1);
    }
  
    nodeGroupRatios_ = new HashMap<String, Double>();
    for (Map.Entry<GroupID, Integer> count : counts.entrySet()) {
      String tag = count.getKey().getKey();
      double ratio = count.getValue() / size;
      nodeGroupRatios_.put(tag, ratio);
    }
    return;
  }
  
  /***************************************************************************
   **
   ** Calculate link group size to total #links for each group
   */
  
  private void calcLGRatios() throws AsynchExitRequestException {
    
    double size = links_.size();
    
    Map<String, Integer> counts = new HashMap<String, Integer>(); // initial vals
    for (NetworkAlignment.EdgeType type : NetworkAlignment.LINK_GROUPS) {
      counts.put(type.tag, 0);
    }
    
    LoopReporter lr = new LoopReporter(links_.size(), 20, monitor_, 0.0, 1.0, "progress.calculatingLinkRatios");
    for (NetLink link : links_) {
      lr.report();
      String rel = link.getRelation();
      counts.put(rel, counts.get(rel) + 1);
    }
    
    linkGroupRatios_ = new HashMap<String, Double>();
    for (Map.Entry<String, Integer> count : counts.entrySet()) {
      String tag = count.getKey();
      double ratio = count.getValue() / size;
      linkGroupRatios_.put(tag, ratio);
    }
    return;
  }
  
  ////////////////////////////////////////////////////////////////////////////
  //
  // PUBLIC METHODS
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /***************************************************************************
   **
   ** Return the index in given ordering given node
   */
  
  public int getIndex(NetNode node) {
    GroupID groupID = generateID(node);
    Integer index = groupIDtoIndex_.get(groupID);
    if (index == null) {
      throw new IllegalArgumentException("GroupID " + groupID + " not found in given order list; given node " + node.getName());
    }
    return (index);
  }
  
  /***************************************************************************
   **
   ** Return the index from the given ordering given tag
   */
  
  public int getIndex(String key) {
    Integer index = groupIDtoIndex_.get(new GroupID(key));
    if (index == null) {
      throw new IllegalArgumentException("GroupID " + key + " not found in given order list; given key " + key);
    }
    return (index);
  }

  /***************************************************************************
   **
   ** Return the GroupID from the given node group ordering
   */
  
  public String getKey(Integer index) {
    if (indexToGroupID_.get(index) == null) {
      throw new IllegalArgumentException("Index not found in given order list; given index " + index);
    }
    return (indexToGroupID_.get(index).getKey());
  }
 
  /***************************************************************************
   **
   ** Return the Annot Color for index of NG label
   */
  
  public String getColor(Integer index) {
    GroupID groupID = indexToGroupID_.get(index);
    return (groupIDtoColor_.get(groupID));
  }
  
  public int numGroups() {
    return (numGroups_);
  }
  
  public Map<String, Double> getNodeGroupRatios() {
    return (nodeGroupRatios_);
  }
  
  public Map<String, Double> getLinkGroupRatios() {
    return (linkGroupRatios_);
  }
  
  /***************************************************************************
   **
   ** Sorts in decreasing node degree - method is here for convenience
   */
  
  public Comparator<NetNode> sortDecrDegree() {
    return (new Comparator<NetNode>() {
      public int compare(NetNode node1, NetNode node2) {
        int diffSize = nodeToNeighbors_.get(node2).size() - nodeToNeighbors_.get(node1).size();
        return (diffSize != 0) ? diffSize : node1.getName().compareTo(node2.getName());
      }
    });
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // PRIVATE CLASSES
  //
  ////////////////////////////////////////////////////////////////////////////
  
  /***************************************************************************
   **
   ** Hash
   */
  
  private static class GroupID {
    
    private final String key_;
    
    public GroupID(String key) {
      this.key_ = key;
    }
    
    public String getKey() {
      return (key_);
    }
    
    @Override
    public String toString() {
      return (key_);
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return (true);
      if (! (o instanceof GroupID)) return (false);
      
      GroupID groupID = (GroupID) o;
      
      if (key_ != null ? (! key_.equals(groupID.key_)) : (groupID.key_ != null)) return (false);
      return (true);
    }
    
    @Override
    public int hashCode() {
      return (key_ != null ? key_.hashCode() : 0);
    }
    
  }
}
