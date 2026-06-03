package com.zenrows;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    // Thread-sicheres Set für besuchte URLs
    static Set<String> visited = ConcurrentHashMap.newKeySet();

    // Thread-sichere Zähler
    static AtomicInteger count = new AtomicInteger(0);
    static AtomicInteger activeTasks = new AtomicInteger(0); // Trackt laufende Threads

    static int maxPages = 10;
    static String baseDomain = "";

    // Executor Service für das Multithreading
    static ExecutorService executor;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // =========================
        //  MODE SELECTION
        // =========================
        System.out.println("=== SELECT THREADS FOR THE MULTITHREADED CRAWLER===");
        System.out.println("=== MULTITHREADED CRAWLER MODE ===");
        System.out.println("1 - Single Page");
        System.out.println("2 - FULL CRAWL (entire domain)");
        System.out.println("3 - Limited Crawl");

        System.out.print("Select mode: ");
        int mode = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter start URL: ");
        String startUrl = scanner.nextLine();
        baseDomain = startUrl;

        // =========================
        // MODE LOGIC
        // =========================
        if (mode == 1) {
            maxPages = 1;
        } else if (mode == 2) {
            maxPages = Integer.MAX_VALUE; // FULL CRAWL
        } else if (mode == 3) {
            System.out.print("How many pages? ");
            maxPages = Integer.parseInt(scanner.nextLine());
        } else {
            System.out.println("Invalid mode.");
            return;
        }

        scanner.close();

        // =========================
        // 🔥 MULTITHREADING SETUP
        // =========================
        int numThreads = 10; // Anzahl der gleichzeitigen Threads
        System.out.println("\nStarting crawl with " + numThreads + " threads...");
        executor = Executors.newFixedThreadPool(numThreads);

        // Ersten Task starten
        submitCrawlTask(startUrl);

        // Main-Thread wartet, bis alle Tasks fertig sind oder das Limit erreicht ist
        try {
            while (activeTasks.get() > 0 && count.get() < maxPages) {
                Thread.sleep(500); // Kurz warten, um die CPU nicht auszulasten
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Thread-Pool ordnungsgemäß herunterfahren
        executor.shutdownNow();

        System.out.println("\n==============================");
        System.out.println("CRAWL FINISHED");
        System.out.println("Pages scraped: " + visited.size());
        System.out.println("==============================");
    }

    /**
     * Reicht eine neue URL in den Thread-Pool ein.
     */
    private static void submitCrawlTask(String url) {
        // Abbruchbedingung: Max Pages erreicht
        if (count.get() >= maxPages) return;

        // Versuche URL hinzuzufügen. Wenn false, wurde sie bereits besucht/eingereiht.
        if (!visited.add(url)) return;

        // Task-Zähler erhöhen
        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try {
                // Zähler innerhalb des Threads sicher erhöhen und prüfen
                int currentCount = count.incrementAndGet();
                if (currentCount > maxPages) {
                    return;
                }

                Document doc = Jsoup.connect(url).get();

                // Synchronisierter Block, damit Ausgaben verschiedener Threads sich nicht überschneiden
                synchronized (System.out) {
                    System.out.println("\n==============================");
                    System.out.println("PAGE " + currentCount);
                    System.out.println("SOURCE: " + url);
                    System.out.println("TITLE: " + doc.title());
                    System.out.println("==============================\n");

                    // CONTENT
                    Elements content = doc.select("h1, h2, h3, p");
                    for (Element el : content) {
                        String text = el.text().trim();
                        if (text.isEmpty()) continue;

                        if (el.tagName().matches("h1|h2|h3")) {
                            System.out.println("\n " + text.toUpperCase());
                        } else {
                            System.out.println(" " + text);
                        }
                    }
                }

                // LINKS
                Elements links = doc.select("a");
                for (Element a : links) {
                    String href = a.attr("abs:href");

                    if (href.isEmpty()) continue;
                    if (!href.startsWith(baseDomain)) continue;

                    // Neue URLs rekursiv als neuen Task in den Pool schieben
                    submitCrawlTask(href);
                }

            } catch (Exception e) {
                System.out.println("ERROR (" + url + "): " + e.getMessage());
            } finally {
                // Task-Zähler wieder verringern, egal ob Erfolg oder Exception
                activeTasks.decrementAndGet();
            }
        });
    }
}