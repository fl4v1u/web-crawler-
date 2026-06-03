Java Web Crawler with Jsoup

A simple but powerful web crawler written in Java using Jsoup.

This crawler can:

* Crawl a single page
* Crawl an entire domain
* Crawl a limited number of pages
* Extract headings and paragraph content
* Follow internal links automatically

⸻

Features

* BFS-style crawling using Queue
* Duplicate URL protection using HashSet
* Internal domain restriction
* Content extraction (h1, h2, h3, p)
* Simple console interface
* Lightweight and beginner friendly

⸻

Technologies Used

* Java
* Jsoup

⸻

Project Structure

src/
│
├── Main.java
├── Product.java

⸻

How It Works

The crawler starts from a given URL and:

1. Visits the page
2. Extracts content
3. Finds all internal links
4. Adds new links to the queue
5. Repeats until the crawl limit is reached

⸻

Crawl Modes

1. Single Page

Scrapes only one page.

Mode: 1

⸻

2. Full Crawl

Crawls the entire domain recursively.

Mode: 2

⸻

3. Limited Crawl

Crawls a specified number of pages.

Mode: 3

Example:

How many pages? 25

⸻

Installation

Clone Repository

git clone https://github.com/fl4v1u/web-crawler-.git

⸻
Execution of the programm in the terminal :

Main.java

Then follow the console instructions.

Example:

=== CRAWLER MODE ===
1 - Single Page
2 - FULL CRAWL (entire domain)
3 - Limited Crawl

⸻

Example Output

PAGE 1
SOURCE: https://example.com
TITLE: Example Domain

⸻

Product Class

The project also includes a simple Product model:

public class Product {
    private String url;
    private String image;
    private String name;
    private String price;
}

Useful for:

* E-Commerce scraping
* Product extraction
* JSON export
* Data pipelines

⸻

Future Improvements

Possible upgrades:

* Export to JSON
* Export to CSV
* Multi-threaded crawling
* Database integration
* AI content extraction

⸻

Disclaimer

This project is for educational purposes only.

Always respect:
* website terms of service


⸻

License

MIT License
