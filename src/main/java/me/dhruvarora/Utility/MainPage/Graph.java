package me.dhruvarora.Utility.MainPage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

/*
 * extension of JPanel
 * 
 * drawing a custom graph by overriding painting functions
 */
public class Graph extends JPanel {
    // graph height is 500 pixels
    private static final int PREF_W = 500;
    // graph width is 300 pixels
    private static final int PREF_H = 300;
    // the gap between top window border and between other graphs
    private static final int Y_BORDER_GAP = 30;
    // the gap between the sides of the window
    private static final int X_BORDER_GAP = 45;
    // color of graph lines are blue
    private static Color GRAPH_COLOR = new Color(67, 129, 255);
    // color of graph point is blue
    private static Color GRAPH_POINT_COLOR = new Color(67, 129, 255);
    // color of the graph in general is white
    private static Color GRAPHIC_COLOR = Color.white;
    // width of graph lines is 3 pixels
    private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
    // width of each point is 6 pixels
    private static final int GRAPH_POINT_WIDTH = 6;
    // number of hash marks on the Y axis
    private static final int Y_HATCH_CNT = 10;
    // number of months that the app will predict; imported from mainPage
    private final int predictionsCount;
    // list of all the data points for graph
    private ArrayList<Double> dataSet;

    /*
     * Initalizing global variables and setting background
     */
    public Graph(ArrayList<Double> dataSet, int predictionsCount) {
        this.dataSet = dataSet;
        this.predictionsCount = predictionsCount;
        setBackground(Color.darkGray);
    }

    /*
     * overriding built in method for JPanel
     */
    @Override
    public void paintComponent(Graphics g) {
        /*
         * preparing panel for drawing graph
         * 
         * painting 3d component and then making 2d object
         */
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(GRAPHIC_COLOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // creating scale for the x axis for the graph; used for being referance when
        // drawing lines
        double xScale = ((double) getWidth() - 2 * X_BORDER_GAP) / (dataSet.size() - 1);

        // drawing the graph's data (points and line from point to point)
        this.drawGraphData(g2, xScale);

        // drawing x and y axis lines
        g2.setColor(Color.WHITE);
        g2.drawLine(X_BORDER_GAP, getHeight() - Y_BORDER_GAP, X_BORDER_GAP, Y_BORDER_GAP);
        g2.drawLine(X_BORDER_GAP, getHeight() - Y_BORDER_GAP, getWidth() - Y_BORDER_GAP, getHeight() - Y_BORDER_GAP);

        // drawing hash marks for both axis
        drawHashes(g2);

    } // CONSTRUCTOR

    /*
     * 
     * 
     * 
     * 
     * Function to draw the data for the graph
     * 
     * 3 blocks
     * - gets the data and the location of where the data should be
     * - draws the lines from point to point
     * - draws the points
     * 
     * lines are drawn first so the points (if a different color) may be shown on
     * top of the lines
     */
    private void drawGraphData(Graphics2D g2, double xScale) {

        /*
         * block #1:
         * 
         * using dataSet to get the points and then caculates their location based on
         * the graph scaling
         * uses getYLocation() to get the scaled position of the point
         */
        List<Point> graphPoints = new ArrayList<Point>();
        List<Double> graphPointsPrice = new ArrayList<Double>();
        // iterating through each price in the dataSet
        for (int i = 0; i < dataSet.size(); i++) {
            int x1 = (int) (i * xScale + X_BORDER_GAP);
            int y1 = (int) (Math.round(this.getYLocation(dataSet.get(i))) + Y_BORDER_GAP); // calculating position after
                                                                                           // scaling
            graphPoints.add(new Point(x1, y1));
            graphPointsPrice.add(dataSet.get(i));
        }

        /*
         * setting color of the lines stroke and getting old color for points
         */
        Stroke oldStroke = g2.getStroke();
        g2.setColor(GRAPH_COLOR);
        g2.setStroke(GRAPH_STROKE);

        /*
         * drawing lines from point to point on graph
         * 
         * uses graphPoints to get x and y cords of graph points
         */
        // iterating through each point in graphPoints
        for (int i = 0; i < graphPoints.size() - 1; i++) {

            // getting point dimensions
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;

            g2.drawLine(x1, y1, x2, y2); // drawing line
        }

        /*
         * changing colors to match the color of the points desired colors
         */
        g2.setStroke(oldStroke);
        g2.setColor(GRAPH_POINT_COLOR);

        /*
         * drawing graph points specified in graphPoints
         * 
         * uses GRAPH_POINT_WIDTH to get the size of the points
         */
        // iterating through graphPoints
        for (int i = 0; i < graphPoints.size(); i++) {

            /* getting dimensions of the point */
            int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
            int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;

            g2.fillOval(x, y, ovalW, ovalH); // drawing point
            // drawing price above each point
            if (i == 0)
                x = x + 20;
            // drawing label of each point
            g2.drawString(String.valueOf(Math.round(graphPointsPrice.get(i) * 100.0) / 100.0), x - 15, y - 5);
        }
    }

    /*
     * 
     * 
     * 
     * 
     * function to draw the hash marks for the x and y axis
     * 
     * after drawing hashes it will label each hash
     */
    private void drawHashes(Graphics2D g2) {

        /*
         * creating lines/hashes on top of the y axis for points
         * 
         * uses Y_HATCH_CNT to get number of hash marks needed
         * also draws labels next to hashmarks
         */
        for (int i = 0; i < Y_HATCH_CNT; i++) { // iterating through number of hash marks needed
            // getting dimensions of hash
            int x0 = Y_BORDER_GAP + 8;
            int x1 = GRAPH_POINT_WIDTH + Y_BORDER_GAP + 8;
            int y0 = getHeight() - (((i + 1) * (getHeight() - Y_BORDER_GAP * 2)) / Y_HATCH_CNT + Y_BORDER_GAP);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1); // drawing line
            g2.drawString("$" + String.valueOf(getHashLabel(i)), x0 - 30, y0 + 5); // labeling the hash's price value
        }

        /*
         * creating hashes for the x axis
         * 
         * then creates labels for each
         * - uses getMonth() to find the short string version of month
         */
        for (int i = 0; i < dataSet.size() - 1; i++) {
            int x0 = (i + 1) * (getWidth() - Y_BORDER_GAP * 2) / (dataSet.size() - 1) + Y_BORDER_GAP;
            int x1 = x0;
            int y0 = getHeight() - Y_BORDER_GAP;
            int y1 = y0 - GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString(this.getMonth(i - 1), x0 - 10, y0 + 15); // drawing month label
        }
    }

    /*
     * 
     * 
     * 
     * sets preferred size of graph
     * 
     * overriding default function
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    /*
     * 
     * 
     * 
     * 
     * function to find the biggest value and smallest value of dataSet
     * 
     * iterates through entire data set to find lowest and largest numbers
     * ROUNDS numbers
     * - lowest rounds down to nearest tens place
     * - highest rounds up to nearest tens place
     * 
     * stored in list of integer
     */
    private int[] getMaxAndMin() {
        // initalizing variables which is store values
        int[] maxAndMin = new int[2];
        maxAndMin[0] = (int) Math.floor(this.dataSet.get(0) / 10) * 10;
        maxAndMin[1] = (int) ((this.dataSet.get(this.dataSet.size() - 1) / 10) + 1) * 10;

        /* iterating through entire data set to find highest and lowest values */
        for (int i = 0; i < dataSet.size(); i++) {
            if ((int) Math.floor(this.dataSet.get(i) / 10) * 10 < maxAndMin[0]) {
                maxAndMin[0] = (int) Math.floor(this.dataSet.get(i) / 10) * 10;
            }
            if ((int) ((this.dataSet.get(i) / 10) + 1) * 10 > maxAndMin[1]) {
                maxAndMin[1] = (int) ((this.dataSet.get(i) / 10) + 1) * 10;
            }
        }
        return maxAndMin;
    }

    /*
     * 
     * 
     * 
     * 
     * returns y Location of point
     * 
     * - calculates size of graph
     * - finds percentage of price to maximum price and scales graph accordingly
     * 
     * returns y axis INVERTED
     * - y axis starts from top and ends at bottom
     */
    private int getYLocation(Double price) {
        int graphableHeight = this.getHeight() - (Y_BORDER_GAP * 2); // calculates pixels of graph
        int[] maxAndMin = getMaxAndMin();
        int range = maxAndMin[1] - maxAndMin[0];

        Double percentage = ((price - maxAndMin[0]) / range);
        int position = (int) Math.round(graphableHeight * percentage);
        int yLocation = (graphableHeight - (position - 30)) - 50;
        return yLocation;
    }

    /*
     * 
     * 
     * 
     * 
     * returns label for y axis hash mark
     * 
     * calculates scaling to find the top and bottom
     * then, calculates all middle points and input's value
     */
    private int getHashLabel(int i) {
        int[] maxAndMin = this.getMaxAndMin();
        int range = maxAndMin[1] - maxAndMin[0]; // full graph range

        int labelDistance = range / Y_HATCH_CNT; // the difference between labels
        return (maxAndMin[0] + (labelDistance * i));
    }

    /*
     * 
     * 
     * 
     * 
     * uses built in java Date to find month name
     * calculates stock's month using enoch time
     * 
     * 31536000 - enoch time for a year
     * 2628288 - enoch time for a month
     */
    private String getMonth(int i) {
        Date date = new Date(
                ((System.currentTimeMillis() / 1000) - ((31536000 + (31536000 / 2) - 2628288) - (2628288 * i))) * 1000);
        DateFormat format = new SimpleDateFormat("MMM"); // returns month in short form ex. "Jan"
        return format.format(date);
    }

}
