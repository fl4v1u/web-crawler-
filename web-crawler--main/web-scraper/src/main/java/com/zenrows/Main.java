package com.zenrows;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static Set<String> visited = ConcurrentHashMap.newKeySet();
    static List<String> allPages = Collections.synchronizedList(new ArrayList<>());
    static AtomicInteger count = new AtomicInteger(0);
    static AtomicInteger activeTasks = new AtomicInteger(0);

    static int maxPages = 10;
    static String baseDomain = "";
    static ExecutorService executor;

    // ==========================================
    // 🐳 DEIN DOCKER OLLAMA SETUP (AUS DEN SCREENSHOTS)
    // ==========================================
    static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    static final String OLLAMA_MODEL = "gemma4:latest"; // Exakt aus deiner Terminal-Ausgabe

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== MULTITHREADED CRAWLER MODE ===");
        System.out.println("1 - Single Page");
        System.out.println("2 - FULL CRAWL (entire domain)");
        System.out.println("3 - Limited Crawl");

        System.out.print("Select mode: ");
        int mode = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter start URL: ");
        String startUrl = scanner.nextLine();
        baseDomain = startUrl;

        if (mode == 1) {
            maxPages = 1;
        } else if (mode == 2) {
            maxPages = Integer.MAX_VALUE;
        } else if (mode == 3) {
            System.out.print("How many pages? ");
            maxPages = Integer.parseInt(scanner.nextLine());
        } else {
            System.out.println("Invalid mode.");
            return;
        }

        scanner.close();

        int numThreads = 10;
        System.out.println("\nStarting crawl with " + numThreads + " threads...");
        executor = Executors.newFixedThreadPool(numThreads);

        submitCrawlTask(startUrl);

        try {
            while (activeTasks.get() > 0 && count.get() < maxPages) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdownNow();

        System.out.println("\n==============================");
        System.out.println("CRAWL FINISHED");
        System.out.println("Pages scraped: " + visited.size());
        System.out.println("==============================");

        // 1. JSON-Datei speichern
        String jsonFilename = "crawl_results.json";
        saveJsonToFile(jsonFilename);

        // 2. JSON an Gemma4 senden und analysieren lassen
        sendToOllama(jsonFilename);
    }

    private static void submitCrawlTask(String url) {
        if (count.get() >= maxPages) return;
        if (!visited.add(url)) return;

        activeTasks.incrementAndGet();

        executor.submit(() -> {
            try {
                int currentCount = count.incrementAndGet();
                if (currentCount > maxPages) return;

                Document doc = Jsoup.connect(url).get();

                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append("  {\n");
                jsonBuilder.append("    \"page_number\": ").append(currentCount).append(",\n");
                jsonBuilder.append("    \"url\": \"").append(escapeJson(url)).append("\",\n");
                jsonBuilder.append("    \"title\": \"").append(escapeJson(doc.title())).append("\",\n");
                jsonBuilder.append("    \"content\": [\n");

                synchronized (System.out) {
                    System.out.println("\n==============================");
                    System.out.println("PAGE " + currentCount);
                    System.out.println("SOURCE: " + url);
                    System.out.println("TITLE: " + doc.title());
                    System.out.println("==============================\n");

                    Elements content = doc.select("h1, h2, h3, p");
                    boolean firstElement = true;

                    for (Element el : content) {
                        String text = el.text().trim();
                        if (text.isEmpty()) continue;

                        if (el.tagName().matches("h1|h2|h3")) {
                            System.out.println("\n " + text.toUpperCase());
                        } else {
                            System.out.println(" " + text);
                        }

                        if (!firstElement) jsonBuilder.append(",\n");
                        firstElement = false;

                        String type = el.tagName().matches("h1|h2|h3") ? "heading" : "paragraph";
                        jsonBuilder.append("      {\"type\": \"").append(type).append("\", \"text\": \"").append(escapeJson(text)).append("\"}");
                    }
                }

                jsonBuilder.append("\n    ]\n  }");
                allPages.add(jsonBuilder.toString());

                Elements links = doc.select("a");
                for (Element a : links) {
                    String href = a.attr("abs:href");
                    if (href.isEmpty() || !href.startsWith(baseDomain)) continue;
                    submitCrawlTask(href);
                }

            } catch (Exception e) {
                System.out.println("ERROR (" + url + "): " + e.getMessage());
                allPages.add("  {\n    \"url\": \"" + escapeJson(url) + "\",\n    \"error\": \"" + escapeJson(e.getMessage()) + "\"\n  }");
            } finally {
                activeTasks.decrementAndGet();
            }
        });
    }

    private static void saveJsonToFile(String filename) {
        System.out.println("Saving results to " + filename + "...");
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("[\n");
            synchronized (allPages) {
                for (int i = 0; i < allPages.size(); i++) {
                    writer.write(allPages.get(i));
                    if (i < allPages.size() - 1) writer.write(",\n");
                    else writer.write("\n");
                }
            }
            writer.write("]");
            System.out.println("Successfully saved to " + filename);
        } catch (IOException e) {
            System.out.println("ERROR saving JSON file: " + e.getMessage());
        }
    }

    private static void sendToOllama(String jsonFilename) {
        System.out.println("\n==========================================");
        System.out.println("SENDING DATA TO OLLAMA (" + OLLAMA_MODEL + ")...");
        System.out.println("==========================================\n");

        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(jsonFilename)));

            // ==========================================
            // 🎯 PROMPT ENGINEERING FÜR DIE KUNDENPRÄSENTATION
            // ==========================================
            String systemPrompt = "Du bist ein professioneller Business-Analyst. Vor dir liegen im JSON-Format die gecrawlten Daten der Webseite eines potenziellen Kunden.\\n\\n"
                    + "Deine Aufgabe: Erstelle eine prägnante Zusammenfassung aller wichtigen Kernpunkte für eine bevorstehende Firmenpräsentation. Halte dich strikt an folgende Regeln:\\n"
                    + "1. ZIELGRUPPE & TON: Erkläre die Inhalte extrem leicht, verständlich und professionell. Vermeide verschachtelte Sätze.\\n"
                    + "2. LÄNGE: Die Zusammenfassung darf maximal 2 Seiten lang sein (Richtwert: ca. 300 Wörter). Schreibe auf keinen Fall mehr Wörter als nötig.\\n"
                    + "3. WAHRHEITSPFICHT: Wenn auf der Webseite nur sehr wenige oder magere Informationen zu finden sind, darfst du absolut nichts hinzuerfinden oder fantasieren! Gib in diesem Fall einfach nur sachlich das wieder, was da ist.\\n"
                    + "4. STRUKTUR: Nutze klare, kurze Zwischenüberschriften und Bulletpoints für die wichtigsten Fakten.\\n\\n"
                    + "Hier sind die Daten:\\n";

            String fullPrompt = systemPrompt + jsonData;

            String jsonPayload = "{\n" +
                    "  \"model\": \"" + OLLAMA_MODEL + "\",\n" +
                    "  \"prompt\": \"" + escapeJson(fullPrompt) + "\",\n" +
                    "  \"stream\": false\n" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();

                String searchString = "\"response\":\"";
                int startIndex = responseBody.indexOf(searchString);
                if (startIndex != -1) {
                    startIndex += searchString.length();
                    int endIndex = responseBody.lastIndexOf("\",\"done\"");
                    if (endIndex != -1 && endIndex > startIndex) {
                        String llmAnswer = responseBody.substring(startIndex, endIndex);

                        llmAnswer = llmAnswer.replace("\\n", "\n").replace("\\t", "\t").replace("\\\"", "\"");

                        System.out.println("\n💼 === KUNDENPRÄSENTATION ZUSAMMENFASSUNG (GEMMA4) ===");
                        System.out.println(llmAnswer);
                        System.out.println("======================================================");
                    } else {
                        System.out.println("Fehler beim Parsen der Antwort. Rohdaten:\n" + responseBody);
                    }
                }
            } else {
                System.out.println("Fehler von Ollama-API. Statuscode: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (Exception e) {
            System.out.println("Fehler bei der Kommunikation mit Ollama: " + e.getMessage());
        }
    }

    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}