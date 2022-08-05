package me.dhruvarora.Utility;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import lombok.AllArgsConstructor;
import me.dhruvarora.UI.Frame;

import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class StockData {
    /*
     * 
     * purpose: holds the user preferred stocks
     * 
     * is imported from StockWatchList.json in assets dir
     * 
     * used during creation of graphs
     * 
     */
    private JSONArray stockWatchList;

    /*
     * 
     * CLASS CONSTRUCTOR
     * 
     * importing & parsing json file from assets dir using simple.json library
     * 
     * sets stockWatchList to the contents of the json file
     * 
     * may throw a ParseException if json is improperly written during modification-
     * of code
     * 
     */
    public StockData() throws FileNotFoundException, IOException, ParseException {
        InputStream fis = StockData.class
                .getResourceAsStream("../StockMarkets.json");
        JSONParser jsonParser = new JSONParser();
        stockWatchList = (JSONArray) jsonParser.parse(
                new InputStreamReader(fis, "UTF-8"));
    }

    /*
     * 
     * returns the JSONArray watch list
     * 
     * usage:
     * getting company name and ticker for building graphs
     * 
     */
    public JSONArray getWatchList() {
        return stockWatchList;
    }

    /*
     * 
     * returns the length of the personal stock watch list
     * 
     * stockWatchList: JSONArray
     * imported library (simple.json)
     * 
     * usage:
     * if there is only 1 item on watch list it will add blank graph to keep button-
     * in correct position
     * 
     */
    public int getWatchListLength() {
        return stockWatchList.size();
    }

    /*
     * checks is stock already exists in stockWatchList
     * 
     * returns true if it exists
     * returns false if it does not exist
     */
    public boolean isStockExisting(String string) {
        for (Object obj : stockWatchList) {
            JSONObject obj2 = (JSONObject) obj;
            String companyName = (String) obj2.get("companyName");
            String tickerSymbol = (String) obj2.get("tickerSymbol");
            if (companyName.equalsIgnoreCase(string) || tickerSymbol.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    public void deleteStock(String string, Frame frame) throws FileNotFoundException, IOException, ParseException {
        for (int i = 0; i < stockWatchList.size(); i++) {
            JSONObject obj2 = (JSONObject) stockWatchList.get(i);
            String companyName = (String) obj2.get("companyName");
            String tickerSymbol = (String) obj2.get("tickerSymbol");
            if (companyName.equalsIgnoreCase(string) || tickerSymbol.equalsIgnoreCase(string)) {
                stockWatchList.remove(stockWatchList.get(i));
                try (FileWriter file = new FileWriter("src/main/resources/me/dhruvarora/StockMarkets.json")) {
                    file.write(this.stockWatchList.toJSONString());
                    file.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * 
     * 
     * checks if given ticker symbol is real and accurately represents a company
     * 
     * will use yahoo finance and if catches error then is false
     * 
     * returns a boolean:
     * - true means ticker is valid
     * - false means ticker is invalid
     * 
     * throws IOException if error with connection
     */
    public boolean checkStockValidity(String ticker) throws IOException {
        Stock test = YahooFinance.get(ticker);
        if (test.getName() == null) {
            return false;
        }
        return true;
    }

    /*
     * 
     * Adds stock to StockMarkets.json
     * 
     * suppresses warnings of using "PUT" method with JSONObject
     * 
     */

    @SuppressWarnings("unchecked")
    public void addStockToWatchList(String ticker) throws IOException {
        /*
         * getting stock data for storing
         * 
         * uses YahooFinanceAPI
         */
        Stock stock = YahooFinance.get(ticker);
        JSONObject stockInfo = new JSONObject();
        stockInfo.put("companyName", stock.getName());
        stockInfo.put("tickerSymbol", ticker.toUpperCase());

        /*
         * writing to file StockMarkets.json in assets folder
         * 
         * uses simple.json
         * 
         * TODO: pretty print json file
         */
        this.stockWatchList.add(stockInfo);
        try (FileWriter file = new FileWriter("src/main/resources/me/dhruvarora/StockMarkets.json")) {
            file.write(this.stockWatchList.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 
     * uses YahooFinance API (imported external library)
     * 
     * used to get current stock data
     * 
     * returns a Stock (YahooFinanceAPI object)
     * used by findPrice function to then get "quote" or current price of stock
     * 
     * current is shown in the graph's titles
     * 
     */
    public Stock findStock(String ticker) {
        try {
            return YahooFinance.get(ticker);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    /*
     * 
     * takes Stock object from findStock and returns the price of given stock
     * 
     * may throw a connection error
     * 
     * returns BigDecimal (stock price can be multiple decimals)
     * 
     */
    public BigDecimal findPrice(final Stock stock) throws IOException {
        return stock.getQuote(true).getPrice();
    }

    /*
     * 
     * returns monthly data from the last 4 months of the given stock
     * 
     * used by getGraphData to get the data
     * 
     */
    public JSONObject getHistoricalData(String ticker) throws IOException, ParseException {

        /*
         * connecting to YahooFinance (not API)
         * 
         * is returned a CSV file
         * 
         * period: beggining of when you want historicalData
         * - currently is set to 4 years previous the current date
         * 
         * period2: end of historicalData list
         * - currently set to current date of call
         */
        Long period2 = System.currentTimeMillis() / 1000;
        Long period = period2 - 31536000 * 4 - 2628288;

        String string = String.format(
                "https://query2.finance.yahoo.com/v8/finance/chart/%1$s?formatted=true&crumb=6iPfwrHM.4i&lang=en-IN&region=IN&period1=%2$s&period2=%3$s&interval=1mo&events=div|split&corsDomain=in.finance.yahoo.com",
                ticker, period, period2);
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        /*
         * reading data given by yahoofinance
         * 
         * parses the STRING given into a JSONObject
         * 
         * simplifies data and removes much of the data that IS NOT USED IN THIS PROJECT
         */
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {

            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(in.readLine());
            JSONObject chart = (JSONObject) json.get("chart");
            JSONArray result = (JSONArray) chart.get("result");
            JSONObject obj = (JSONObject) result.get(0);

            return obj;
        }
    }

    /*
     * 
     * uses getHistoricalData function to return the data needed to graph the
     * mainpage graphs
     * 
     * returns ArrayList of ONLY PRICE VALUES
     * returns years worth of data
     * 
     * supressing warning of unchecked cast with ArrayLists
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Double> getGraphData(String ticker) throws IOException, ParseException {

        /*
         * simplifying data given by getHistoricalData so it is iterable
         * 
         * timeStamps holds all the ENOCH dates for historical data
         * data holds all prices for when company is open
         * - index for both is the same
         * --- ex: timeStamps[0] (date) is when company was at this price data[0]
         */
        JSONObject rawData = (JSONObject) this.getHistoricalData(ticker);
        JSONObject indicators = (JSONObject) rawData.get("indicators");
        JSONArray quote = (JSONArray) indicators.get("quote");
        JSONObject obj = (JSONObject) quote.get(0);

        ArrayList<Double> data = (ArrayList<Double>) obj.get("open");
        ArrayList<Long> timeStamps = (ArrayList<Long>) rawData.get("timestamp");

        /*
         * calculating the year and month it was 1 year ago
         * 
         * stores the year and month into targetDate array as Integer
         * - ex: [2007, 04]
         */
        Date date = new Date(((System.currentTimeMillis() / 1000) - (31536000 + (31536000 / 2))) * 1000);
        DateFormat format = new SimpleDateFormat("yyyy MM");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String[] targetDateString = format.format(date).split(" ");
        int[] targetDate = { Integer.parseInt(targetDateString[0]), Integer.parseInt(targetDateString[1]) };

        /*
         * interating through timeStamps and checking if dates line up with targetDate
         * 
         * filteredData: if a date is needed for the graph, it is indexed and the price-
         * is put into this ArrayList
         * - all prices are arranged in order from oldest to current
         */
        ArrayList<Double> filteredData = new ArrayList<>();

        for (Long enochDate : timeStamps) { // iterating through each time in timeStamps
            // getting the date from timeStamps
            Date date2 = new Date(enochDate * 1000);
            String[] formattedDateString = format.format(date2).split(" ");
            int[] formatDate = { Integer.parseInt(formattedDateString[0]), Integer.parseInt(formattedDateString[1]) };

            // if the date matches to the target date
            if (formatDate[0] == targetDate[0] && formatDate[1] == targetDate[1]) {
                filteredData.add(data.get(timeStamps.indexOf(enochDate))); // add the price to filtered data

                targetDate[1] = targetDate[1] + 1; // change the target date to a month ahead
                if (targetDate[1] == 13) { // if the months becomes 13
                    targetDate[0] = targetDate[0] + 1; // add a year
                    targetDate[1] = 1; // and set the month to jan
                }
            }
        }
        return filteredData; // returned the collected data
    }

    /*
     * function to add predicted data to the dataSet
     * 
     * returns the new dataSet
     * 
     * TODO: work in progress (dead code)
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Double> addPredictionData(ArrayList<Double> dataSet, int predictionCount, String ticker)
            throws IOException, ParseException {

        // getting the data and timestamps from the JSON object
        JSONObject rawData = (JSONObject) this.getHistoricalData(ticker);
        JSONObject indicators = (JSONObject) rawData.get("indicators");
        JSONArray quote = (JSONArray) indicators.get("quote");
        JSONObject obj = (JSONObject) quote.get(0);

        ArrayList<Double> data = (ArrayList<Double>) obj.get("open");
        ArrayList<Long> timeStamps = (ArrayList<Long>) rawData.get("timestamp");

        // if the graph is going downwards
        if (isDownwardTrend(data, timeStamps)) {
            addDownwardTrendData(data, timeStamps); // add another prediction of downwards
        }
        // TODO: create new checks for predictions

        return dataSet; // return the new dataset

    }

    /*
     * function to check if the stock's price is on a decline
     *
     * returns true if it is on a decline
     * return false if it is not on a decline
     */
    private boolean isDownwardTrend(ArrayList<Double> data, ArrayList<Long> timeStamps) {
        // setting format
        DateFormat format = new SimpleDateFormat("yyyy MM");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        // getting the date 6 months ago
        Date targetDate = new Date(((System.currentTimeMillis() / 1000) - (2628288 * 2)) * 1000);
        String[] targetDateString = format.format(targetDate).split(" ");
        int[] targetDateList = { Integer.parseInt(targetDateString[0]), Integer.parseInt(targetDateString[1]) };
        System.out.println(targetDateString[0] + " " + targetDateList[1]);

        for (Long time : timeStamps) { // iterates through all the timeStamps
            // getting the human format of the timeStamp
            Date date = new Date(time * 1000);
            String[] dateString = format.format(date).split(" ");
            int[] dateList = { Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]) };

            // if the date matches
            if (dateList[0] == targetDateList[0] && dateList[1] == targetDateList[1]) {
                // if the month previous price was higher than this months
                if (data.get(timeStamps.indexOf(time)) > data.get(timeStamps.indexOf(time) + 1)) {
                    return true; // return that the trend is a decline
                } else {
                    return false; // otherwise the trend is not a decline
                }
            }
        }
        return false; // if the timestamp was not in that list (shouldn't hit this at any point)
    }

    /*
     * function to return the data for a downwards trend
     * - almost a REPEAT of the isDownwardTrend() function
     * 
     * calculates how steep the trend is and what the price may look like for the
     * next month
     */
    private Double addDownwardTrendData(ArrayList<Double> data, ArrayList<Long> timeStamps) {
        // setting human formatting
        DateFormat format = new SimpleDateFormat("yyyy MM");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        // setting the date the function is trying to find as 6 months previous
        Date targetDate = new Date(((System.currentTimeMillis() / 1000) - (2628288 * 6)) * 1000);
        String[] targetDateString = format.format(targetDate).split(" ");
        int[] targetDateList = { Integer.parseInt(targetDateString[0]), Integer.parseInt(targetDateString[1]) };

        // iterating through all the timeStamps
        for (Long time : timeStamps) {
            // getting the human formatting of the timeStamp
            Date date = new Date(time * 1000);
            String[] dateString = format.format(date).split(" ");
            int[] dateList = { Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]) };

            // TODO: finish all the checks
            if (dateList[0] == targetDateList[0] && dateList[1] == targetDateList[1]) {
                Double amount = 0.0;
                if (data.get(timeStamps.indexOf(time) + 6) > data.get(timeStamps.indexOf(time) + 7)) {
                    amount = (data.get(timeStamps.indexOf(time) + 6) - data.get(timeStamps.indexOf(time) + 7)) / 2;
                }
                return amount;
            }
        }
        return null;
    }
}