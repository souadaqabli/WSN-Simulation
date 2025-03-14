package com.myprj.wsn_1;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NodesFrame extends JFrame {

    private List<Nodes> nodesList;
    private final StartSim network;
    private final CommunicationModel communicationModel;
    private HistoryFrame historyFrame;
    private List<Arrow> arrowsList = new ArrayList<>();
    private boolean isSimulationRunning = false;

    private int currentRound = 1;
    private final JButton nextRoundButton;

    private final List<Nodes> previousCHs = new ArrayList<>();

    public NodesFrame(StartSim net) {
        this.nodesList = new ArrayList<>();
        this.network = net;
        this.communicationModel = new CommunicationModel();
        this.historyFrame = new HistoryFrame();

        setTitle("Simulation WSN with LEACH protocol");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Ajouter les nœuds au réseau
        initializeNodes(network.getNbrNoeuds());

        // Panneau des boutons
        JPanel buttonPanel = new JPanel();
        nextRoundButton = new JButton("Next Round");
        nextRoundButton.addActionListener(e -> runRound());
        JButton historyButton = new JButton("History");
        historyButton.addActionListener(e -> historyFrame.setVisible(true));
        buttonPanel.add(nextRoundButton);
        buttonPanel.add(historyButton);

        // Panneau de dessin
        JPanel drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Nodes node : nodesList) {
                    if (node.isClusterHead()) {
                        drawClusterHead(g, node);
                    } else {
                        drawNode(g, node);
                    }
                }
                for (Arrow arrow : arrowsList) {
                    arrow.draw(g);
                }
            }
        };

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
        add(drawPanel, BorderLayout.CENTER);
    }

    private void initializeNodes(int nbrNodes) {
        for (int i = 0; i < nbrNodes; i++) {
            Nodes node = new Nodes();
            node.setId(i + 1);
            node.setPositionX((int) (Math.random() * getWidth()));
            node.setPositionY((int) (Math.random() * getHeight()));
            node.setColorNode(Color.BLUE);
            nodesList.add(node);
        }

        // Ajouter le Sink
        Nodes sink = new Nodes();
        sink.setId(nbrNodes + 1);
        sink.setPositionX(getWidth() / 2);
        sink.setPositionY(getHeight() - 50);
        sink.setColorNode(Color.YELLOW);
        sink.setSink(true); // Marquer ce nœud comme Sink
        nodesList.add(sink);
    }

    private void runRound() {
        if (!isSimulationRunning) {
            isSimulationRunning = true;
        }

        if (currentRound > network.getNbrRounds()) {
            historyFrame.appendText("Simulation completed!");
            nextRoundButton.setEnabled(false);
            isSimulationRunning = false;
            return;
        }

        resetRolesForNewRound();
        historyFrame.appendText("Starting Round " + currentRound);

        // Étape 1 : Élection des Cluster Heads
        electClusterHeads(0.1);

        // Étape 2 : Formation des clusters
        formClusters();

        // Étape 3 : Simulation des communications
        simulateCommunication();

        // Mise à jour des CHs des rounds précédents
        previousCHs.clear();
        for (Nodes node : nodesList) {
            if (node.isClusterHead()) {
                previousCHs.add(node);
            }
        }

        // Mise à jour de l'affichage
        repaint();

        // Passer au round suivant
        currentRound++;
    }

    private void resetRolesForNewRound() {
        previousCHs.clear();
        for (Nodes node : nodesList) {
            if (node.isClusterHead()) {
                previousCHs.add(node);
            }
        }

        for (Nodes node : nodesList) {
            node.setClusterHead(false);
            node.setCurrentCluster(null);
            node.setColorNode(Color.BLUE); // Couleur par défaut
            node.clearMessage();
            node.clearClusterMembers();
            node.setIsolated(false); // Réinitialiser l'état isolé
        }
        arrowsList.clear();
    }

    private void electClusterHeads(double p) {
        // Réinitialiser les rôles des anciens Cluster Heads
        for (Nodes node : nodesList) {
            if (!node.isSink()) {
                node.setClusterHead(false);
            }
        }

        // Élection des nouveaux Cluster Heads
        for (Nodes node : nodesList) {
            if (!node.isSink() && node.canBeClusterHead(currentRound, p, previousCHs)) {
                node.setClusterHead(true);
                communicationModel.addClusterHeadRayon(node);
            }
        }

        updateNodeColors(); // Met à jour les couleurs après l'élection des CHs
    }

    private void formClusters() {
        for (Nodes node : nodesList) {
            if (!node.isClusterHead() && !node.isSink() && !node.isIsolated()) {
                Nodes closestCH = findClosestClusterHead(node);
                if (closestCH != null && isNodeInCluster(node, closestCH)) {
                    closestCH.addClusterMember(node);
                    node.setColorNode(Color.BLUE); // Nœud affecté à un Cluster Head
                } else {
                    node.setColorNode(Color.GRAY); // Nœud isolé
                    node.setIsolated(true);
                    //historyFrame.appendText("Node " + node.getId() + " is isolated.");
                }
            }
        }

        updateNodeColors(); // Met à jour les couleurs après l'affectation des membres
        updateNodeLabels();
    }

    private void simulateCommunication() {
        arrowsList.clear();

        for (Nodes ch : communicationModel.getClusterHeadSet()) {
            for (Nodes member : ch.getClusterMembers()) {
                if (!member.isIsolated() && member != ch && !member.isSink()) {
                    member.setMessage("Message from Node " + member.getId() + " to CH " + ch.getId());
                    historyFrame.appendText(member.getMessage());
                    drawArrow(member.getPositionX(), member.getPositionY(), ch.getPositionX(), ch.getPositionY());
                }
            }

            Nodes sink = nodesList.get(nodesList.size() - 1);
            if (!ch.isSink()) {
                ch.setMessage("Message from CH " + ch.getId() + " to Sink");
                historyFrame.appendText(ch.getMessage());
                drawArrow(ch.getPositionX(), ch.getPositionY(), sink.getPositionX(), sink.getPositionY());
            }
        }
    }

    private Nodes findClosestClusterHead(Nodes node) {
        Nodes closestCH = null;
        int minDistance = Integer.MAX_VALUE;

        for (Nodes ch : communicationModel.getClusterHeadSet()) {
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

    private void drawArrow(int x1, int y1, int x2, int y2) {
        arrowsList.add(new Arrow(x1, y1, x2, y2));
    }

    private void drawNode(Graphics g, Nodes node) {
        int x = node.getPositionX();
        int y = node.getPositionY();

        g.setColor(node.getColorNode());
        g.fillOval(x, y, 15, 15);

        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(node.getId()), x - 3, y + 5);
    }

    private void drawClusterHead(Graphics g, Nodes clusterHead) {
        int x = clusterHead.getPositionX();
        int y = clusterHead.getPositionY();

        g.setColor(Color.RED);
        g.fillOval(x - 7, y - 7, 15, 15);

        g.setColor(Color.BLACK);
        g.drawString("CH" + clusterHead.getId(), x + 10, y - 5);
    }

    private void updateNodeLabels() {
        for (Nodes node : nodesList) {
            if (node.isClusterHead()) {
                node.setMessage("CH" + node.getId());
            } else {
                node.setMessage(String.valueOf(node.getId()));
            }
        }
        repaint();
    }

    private void updateNodeColors() {
        for (Nodes node : nodesList) {
            if (node.isSink()) {
                node.setColorNode(Color.YELLOW);
            
            }
        }
        repaint();
    }

    private boolean isNodeInCluster(Nodes node, Nodes clusterHead) {
        // Seuil de distance pour déterminer si un nœud est dans le rayon du Cluster Head
        int distanceThreshold = 150;
        int distance = calculateDistance(node, clusterHead);
        return distance <= distanceThreshold;
    }
}
