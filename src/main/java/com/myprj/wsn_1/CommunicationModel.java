package com.myprj.wsn_1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommunicationModel {
    private Set<Nodes> clusterHeadSet = new HashSet<>();
    private Set<Nodes> nodesListInSomeCluster = new HashSet<>();

    public void addClusterHeadRayon(Nodes clusterHead) {
    
        clusterHeadSet.add(clusterHead);
    }

    public Set<Nodes> getClusterHeadSet() {
        return clusterHeadSet;
    }

    public Set<Nodes> getNodesListInSomeCluster() {
        return nodesListInSomeCluster;
    }

    public void determineClusterMembership(List<Nodes> nodesList) {
        // Logic to determine cluster membership based on proximity to a cluster head
        for (Nodes node : nodesList) {
            if (!node.isClusterHead() && !node.isSink() && !node.isIsolated()) {
                Nodes closestCH = findClosestClusterHead(node);
                if (closestCH != null) {
                    closestCH.addClusterMember(node);
                    node.setCurrentCluster(closestCH);
                    nodesListInSomeCluster.add(node);
                }
            }
        }
    }

    private Nodes findClosestClusterHead(Nodes node) {
        Nodes closestCH = null;
        int minDistance = Integer.MAX_VALUE;

        for (Nodes ch : clusterHeadSet) {
            int distance = calculateDistance(node, ch);
            if (distance < minDistance) {
                minDistance = distance;
                closestCH = ch;
            }
        }

        return closestCH;
    }

    private int calculateDistance(Nodes node1, Nodes node2) {
        int dx = node1.getPositionX() - node2.getPositionX();
        int dy = node1.getPositionY() - node2.getPositionY();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}
