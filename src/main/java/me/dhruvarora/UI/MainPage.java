package me.dhruvarora.UI;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import me.dhruvarora.Utility.StockData;
import me.dhruvarora.Utility.MainPage.Graph;
import me.dhruvarora.Utility.MainPage.GraphTitle;
import me.dhruvarora.Utility.MainPage.Refresh;
import me.dhruvarora.Utility.MainPage.AddOrRemoveStock;
import yahoofinance.Stock;

public class MainPage extends JPanel {
    // stores the frame content
    private JComponent content;
    // stores the frame instance
    private Frame frame;
    /*
     * Stores all the content on the page in format of a scrollable page.
     * mainPage JPanel is appended to scroll pane
     */
    private JScrollPane scrollableGraphs;
    // stores refresh button instance for initializing and making default button
    private Refresh refresh;
    private StockData stockData;

    /*
     * CONSTRUCTOR
     * extends JPanel - displays all the graphs and stock information
     * 
     * exceptions if error on reading/writing files
     * or
     * when retriving data from yahoofinance
     */
    public MainPage(JComponent content, Frame frame, StockData stockData)
            throws FileNotFoundException, IOException, ParseException {
        // setting global variables for panel
        this.content = content;
        this.frame = frame;
        this.stockData = stockData;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        setBackground(Color.darkGray);

        // builds panel
        createContentPane();
    }

    /*
     * Initalizes panel by adding it to frame content and setting it visible
     * 
     * run in Frame.java
     */
    public void initialize() {
        frame.getRootPane().setDefaultButton(refresh);
        content.add(scrollableGraphs);
        this.setVisible(true);
    }

    /*
     * Deletes panel from frame by removing from frame content and making it
     * invisible
     */
    public void dispose() {
        content.remove(scrollableGraphs);
        this.setVisible(false);
    }

    /*
     * responsible for painting entire panel
     * 
     * includes:
     * - all graphs
     * - add stock button
     * - refresh button
     */
    private void createContentPane()
            throws FileNotFoundException, IOException, ParseException {

        /*
         * interates through StockMarket.json
         * 
         * creates graphs for each stock in json file
         */
        for (int i = 0; i < stockData.getWatchListLength(); i++) {
            /*
             * using YahooFinanceAPI to get the stock current price
             * after getting current price, creates a title for the graph including the
             * current price
             */
            JSONObject stockWatchListField = (JSONObject) stockData.getWatchList().get(i);
            Graph newGraphPanel = createGraph(stockData, (String) stockWatchListField.get("tickerSymbol"));

            Stock stock = stockData.findStock((String) stockWatchListField.get("tickerSymbol"));
            BigDecimal price = stockData.findPrice(stock);
            GraphTitle newGraphTitle = new GraphTitle((String) stockWatchListField.get("companyName") + ": $" + price);

            /*
             * creates space between graphs
             * or
             * space from the top of the panel
             */
            this.add(Box.createVerticalStrut(25));
            // appending title and graph
            this.add(newGraphTitle);
            this.add(newGraphPanel);
        }

        /*
         * if there is a small amount of graphs then it will add space
         * else
         * it will add a small amount of space between the graphs
         */
        int verticalStrut = 20;
        if (stockData.getWatchListLength() <= 1) {
            verticalStrut += 300;
        }

        /*
         * adding buttons to bottom of JPanel
         * and
         * space between them
         */
        this.add(Box.createVerticalStrut(verticalStrut));
        this.add(new AddOrRemoveStock(this.frame));
        this.add(Box.createVerticalStrut(20));
        refresh = new Refresh(this.frame);
        this.add(refresh);

        /*
         * sets format of JPanel by creating new instance of JScrollPane
         */
        JScrollPane scrollableGraphs = new JScrollPane(this);
        scrollableGraphs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // increases speed of scrollbar
        scrollableGraphs.getVerticalScrollBar().setUnitIncrement(10);

        // setting global variable
        this.scrollableGraphs = scrollableGraphs;

    }

    /*
     * function to create new graph from random data
     * 
     * returns graph
     */
    private Graph createGraph(StockData stockData, String ticker)
            throws IOException, ParseException {
        ArrayList<Double> dataSet = stockData.getGraphData(ticker);
        Graph newGraphPanel = new Graph(dataSet);
        return newGraphPanel;
    }

}
