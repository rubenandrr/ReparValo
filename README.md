# ReparValo

**ReparValo** is a fullstack web application tailored for the Swiss automotive market (Geneva). It targets two core use cases:
1. **Dealer Mode (Trade-in):** Estimates vehicle buy-back prices in Swiss Francs (CHF) based on age, mileage, and condition, and generates a professional negotiation summary using AI.
2. **Owner Mode (Repair):** Estimates repair costs based on local Geneva garage rates and highlights damaged car parts on an interactive SVG vehicle schematic. Users can describe damage in natural language parsed by AI, and receive direct links to find new/used spare parts on Swiss platforms (*Mister-Auto.ch*, *Ricardo.ch*, *tutti.ch*, *anibis.ch*).

## Tech Stack
* **Backend:** Java 17+, Maven, Spring Boot (Spring Web, Spring Data JPA, Spring AI for Gemini).
* **Database:** H2 (Embedded in-memory database).
* **Frontend:** HTML5, CSS3 (Custom variables, Dark/Light modes, Glassmorphism), Vanilla JS, and interactive SVG.
* **Testing:** JUnit 5 and `@DataJpaTest` for unit and database integration tests.