package com.zenrows;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Main {

    static Set<String> visited = new HashSet<>();
    static Queue<String> queue = new LinkedList<>();

    static int maxPages = 10;
    static int count = 0;
    static String baseDomain = "";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // =========================
        // 💬 MODE SELECTION
        // =========================
        System.out.println("=== CRAWLER MODE ===");
        System.out.println("1 - Single Page");
        System.out.println("2 - FULL CRAWL (entire domain)");
        System.out.println("3 - Limited Crawl");

        System.out.print("Select mode: ");
        int mode = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter start URL: ");
        String startUrl = scanner.nextLine();

        // Base Domain setzen
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

        queue.add(startUrl);

        // =========================
        // 🔥 CRAWLER LOOP
        // =========================
        while (!queue.isEmpty() && count < maxPages) {

            String url = queue.poll();

            if (visited.contains(url)) continue;

            // nur gleiche Domain
            if (!url.startsWith(baseDomain)) continue;

            try {

                visited.add(url);
                count++;

                Document doc = Jsoup.connect(url).get();

                System.out.println("\n==============================");
                System.out.println("PAGE " + count);
                System.out.println("SOURCE: " + url);
                System.out.println("TITLE: " + doc.title());
                System.out.println("==============================\n");

                // =========================
                // 🟢 CONTENT
                // =========================
                Elements content = doc.select("h1, h2, h3, p");

                for (Element el : content) {

                    String text = el.text().trim();
                    if (text.isEmpty()) continue;

                    if (el.tagName().matches("h1|h2|h3")) {
                        System.out.println("\n🔵 " + text.toUpperCase());
                    } else {
                        System.out.println("🟢 " + text);
                    }
                }

                // =========================
                // 🔗 LINKS
                // =========================
                Elements links = doc.select("a");

                for (Element a : links) {

                    String href = a.attr("abs:href");

                    if (href.isEmpty()) continue;

                    if (!href.startsWith(baseDomain)) continue;

                    if (!visited.contains(href)) {
                        queue.add(href);
                    }
                }

            } catch (Exception e) {
                System.out.println("ERROR: " + url);
            }
        }

        System.out.println("\n==============================");
        System.out.println("CRAWL FINISHED");
        System.out.println("Pages scraped: " + visited.size());
        System.out.println("==============================");

        scanner.close();
    }
}