Java Web Crawler with Jsoup

A simple but powerful web crawler written in Java using Jsoup.

NEW UPDATE — Multithreading Support

The web crawler now supports multithreaded crawling with up to 10 concurrent threads, significantly improving crawling speed and reducing waiting times. 

int numThreads = 10; // Line 70 

Important — Rate Limits & Responsible Usage

Some websites enforce rate limits or anti-bot protection systems.
Using too many concurrent threads may result in:

* Temporary IP bans
* CAPTCHA challenges
* Blocked requests
* Increased server load

If you experience blocking issues, reduce the thread count in the source code:

int numThreads = 1; // Line 70

Ethical Usage Warning

This software is intended for educational and research purposes only.

Improper use of multithreaded crawling can place heavy load on web servers and may negatively impact website performance. In extreme cases, aggressive crawling behavior could resemble a denial-of-service (DoS) attack.

Disclaimer

The developer of this project is not responsible for any illegal, abusive, or unethical use of this software.

By using this project, you accept full responsibility for your actions and agree to comply with all applicable laws and regulations.

Always:

* Respect robots.txt
* Follow website Terms of Service
* Use reasonable crawl delays
* Avoid excessive request rates
        
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
* Database integration
* AI content extraction

⸻

Disclaimer

This project is for educational purposes only.

Always respect:
* website terms of service

