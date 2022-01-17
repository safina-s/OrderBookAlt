package com.orderbook;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        OrderBookCollection orderBooks = new OrderBookCollection();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(orderBooks);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(
                        """
                                1. Print top level
                                2. Print all bids/asks
                                3. Average price
                                4. Total Quantity
                                5. Volume Weighted Price
                                
                                Enter 1-5:"""

                );
                int action = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter instrument:");
                String instrument = scanner.nextLine();
                OrderBook orderBook = orderBooks.getOrderBook(instrument);
                if (orderBook == null) {
                    System.out.println("No data exists for this instrument " + instrument);
                    continue;
                }
                switch (action) {
                    case 1 -> System.out.println(orderBook.topLevel());
                    case 2-> System.out.println(orderBook.printAllQuotes());
                    case 3,4,5 -> {
                        System.out.println("Enter number of levels: ");
                        int levels = Integer.parseInt(scanner.nextLine());
                        switch (action) {
                            case 3 -> System.out.println(orderBook.averagePriceOverNLevels(levels).toPlainString());
                            case 4 -> System.out.println(orderBook.totalQuantityOverNLevels(levels).toPlainString());
                            case 5 -> System.out.println(orderBook.volumeWeightedPriceOverNLevels(levels).toPlainString());
                        }
                    }
                    default -> System.out.println("Invalid action choice");
                }
                System.out.println();
            } catch (Exception ex) {
                System.out.println("Exception occurred " + ex.getMessage());
            }
        }
    }
}