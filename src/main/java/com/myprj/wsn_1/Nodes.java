package com.myprj.wsn_1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Nodes {

    private int id;
    private int energie;
    private int rayonCommunication;
    private int positionX;
    private int positionY;
    private Color colorNode;
    private boolean isClusterHead;
    private boolean previouslyClusterHead;
    private double currentEnergy;
    private String message;
    private List<Nodes> clusterMembers;

    private Nodes currentCluster; // Le Cluster Head auquel ce nœud est affecté
    private boolean isSink = false;
    private boolean isIsolated = false; // Nouveau champ pour gérer les nœuds isolés

    public Nodes() {
        this.clusterMembers = new ArrayList<>();
        this.message = "";
    }

    public boolean canBeClusterHead(int currentRound, double p, List<Nodes> previousCHs) {
        if (isClusterHead() || previousCHs.contains(this)) return false;
        double threshold = p / (1 - p * (currentRound % (1 / p)));
        return Math.random() < threshold;
    }

    public void addClusterMember(Nodes member) {
        clusterMembers.add(member);
    }

    public List<Nodes> getClusterMembers() {
        return clusterMembers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Color getColorNode() {
        return colorNode;
    }

    public void setColorNode(Color colorNode) {
        this.colorNode = colorNode;
    }

    public boolean isClusterHead() {
        return isClusterHead;
    }

    public void setClusterHead(boolean isClusterHead) {
        this.isClusterHead = isClusterHead;
    }

    public boolean isPreviouslyClusterHead() {
        return previouslyClusterHead;
    }

    public void setPreviouslyClusterHead(boolean previouslyClusterHead) {
        this.previouslyClusterHead = previouslyClusterHead;
    }

    public double getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(double currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnergie() {
        return energie;
    }

    public void setEnergie(int energie) {
        this.energie = energie;
    }

    public int getRayonCommunication() {
        return rayonCommunication;
    }

    public void setRayonCommunication(int rayonCommunication) {
        this.rayonCommunication = rayonCommunication;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void clearMessage() {
        this.message = "";
    }

    public void clearClusterMembers() {
        clusterMembers.clear();
    }

    public Nodes getCurrentCluster() {
        return currentCluster;
    }

    public void setCurrentCluster(Nodes currentCluster) {
        this.currentCluster = currentCluster;
    }

    public boolean isSink() {
        return isSink;
    }

    public void setSink(boolean isSink) {
        this.isSink = isSink;
    }

    // Nouveaux ajouts pour la gestion des nœuds isolés

    public boolean isIsolated() {
        return isIsolated;
    }

    public void setIsolated(boolean isIsolated) {
        this.isIsolated = isIsolated;
    }
}
