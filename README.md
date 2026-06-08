# Java Web Crawler with Jsoup

A simple but powerful web crawler written in Java using Jsoup.

---

# NEW UPDATE — Multithreading Support

The web crawler now supports multithreaded crawling with up to 10 concurrent threads, significantly improving crawling speed and reducing waiting times.

```java
int numThreads = 10; // Line 70
```

---

# NEW UPDATE — JSON Export Support

The crawler can now automatically save crawled data into JSON files.

Exported data may include:

* URL
* Page title
* Headings
* Paragraph content
* Product information
* AI generated summaries

Useful for:

* Data pipelines
* AI training datasets
* Search indexing
* Analytics
* Database imports

---

# IMPORTANT — Hardware Requirements

Running local AI models requires a powerful computer.

Recommended:

* Modern multi-core CPU
* 16GB+ RAM
* NVIDIA GPU recommended
* Docker installed
* Ollama properly configured

Large crawls combined with AI summarization can consume significant system resources.

If your system becomes unstable or slow:

* Reduce thread count
* Limit crawl size
* Use smaller AI models
* Disable AI summaries temporarily

---

# NEW UPDATE — AI Summary Integration

The crawler now supports AI-powered page summarization.

After crawling a page, the extracted content can be processed by an AI model to generate concise summaries automatically.

AI integration is powered by:

* Gemma 4
* Ollama
* Docker

Example use cases:

* Website summarization
* Content categorization
* AI preprocessing pipelines
* Knowledge base generation

---

# Docker & Ollama Setup

If the AI container is not installed or running, you can start the required services using the provided Docker Compose configuration located inside the project directory.

Run:

```bash id="mczqdc"
docker-compose up -d
```

or depending on your Docker version:

```bash id="r67x1l"
docker compose up -d
```

This will automatically start:

* Ollama
* Gemma 4
* Required AI services

Make sure Docker is installed and running before executing the command.

After startup, the crawler can communicate with the local AI container for automatic page summarization.

---

# Important — Rate Limits & Responsible Usage

Some websites enforce rate limits or anti-bot protection systems.

Using too many concurrent threads may result in:

* Temporary IP bans
* CAPTCHA challenges
* Blocked requests
* Increased server load

If you experience blocking issues, reduce the thread count in the source code:

```java
int numThreads = 1; // Line 70
```

---

# Ethical Usage Warning

This software is intended for educational and research purposes only.

Improper use of multithreaded crawling can place heavy load on web servers and may negatively impact website performance.

In extreme cases, aggressive crawling behavior could resemble a denial-of-service (DoS) attack.

---

# Disclaimer

The developer of this project is not responsible for any illegal, abusive, or unethical use of this software.

By using this project, you accept full responsibility for your actions and agree to comply with all applicable laws and regulations.

Always:

* Respect robots.txt
* Follow website Terms of Service
* Use reasonable crawl delays
* Avoid excessive request rates

---

# Features

* Duplicate URL protection using HashSet
* Internal domain restriction
* Content extraction (h1, h2, h3, p)
* JSON export support
* AI summary generation
* Multithreaded crawling
* Lightweight and beginner friendly

---

# Technologies Used

* Java
* Jsoup
* Ollama
* Gemma 4
* Docker
* JSON

---

# Project Structure

```text
src/
│
├── Main.java
├── Product.java
├── JsonExporter.java
├── AiSummaryService.java
```

---

# How It Works

The crawler starts from a given URL and:

1. Visits the page
2. Extracts content
3. Finds all internal links
4. Adds new links to the queue
5. Generates AI summaries (optional)
6. Saves results into JSON
7. Repeats until the crawl limit is reached

---

# Crawl Modes

## Single Page

Scrapes only one page.

Mode: `1`

---

## Full Crawl

Crawls the entire domain recursively.

Mode: `2`

---

## Limited Crawl

Crawls a specified number of pages.

Mode: `3`

Example:

```text
How many pages? 25
```

---

# Installation

## Clone Repository

```bash
git clone https://github.com/fl4v1u/web-crawler-.git
```

---

# Execution of the program in the terminal

```bash
Main.java
```

Then follow the console instructions.

Example:

```text
=== CRAWLER MODE ===
1 - Single Page
2 - FULL CRAWL (entire domain)
3 - Limited Crawl
```

---

# Example Output

```text
PAGE 1
SOURCE: https://example.com
TITLE: Example Domain
```

---

# Product Class

The project also includes a simple Product model:

```java
public class Product {

    private String url;
    private String image;
    private String name;
    private String price;
}
```

Useful for:

* E-Commerce scraping
* Product extraction
* JSON export
* Data gathering

---

# Future Improvements

Possible upgrades:

* CSV export
* Database integration
* Distributed crawling
---

# Disclaimer

This project is for educational purposes only.

Always respect:

* website terms of service
* robots.txt
* rate limits
