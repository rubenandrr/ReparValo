# ReparValo 🚗

**ReparValo** is a premium, full-stack vehicle valuation and repair estimation web application tailored specifically for the Swiss automotive market (Geneva region). 

The platform supports two major workflows:
1. **Dealer Portal (Trade-In Valuation):** Helps car dealerships compute trade-in values in Swiss Francs (CHF) factoring in vehicle age, mileage, and visual condition. It integrates **Spring AI & Google Gemini** to generate a compelling, professional buy-back pitch to justify the dealer's offer to the customer.
2. **Owner Portal (Repair & Sourcing):** Empowers car owners to evaluate damage repair costs. Users can describe damages in natural language (parsed by AI) or click directly on an interactive **SVG vehicle schematic**. The system estimates labor costs using Geneva's average rate of **150 CHF/hour** and matches damaged components with direct search links to Swiss e-commerce and classified networks (*Mister-Auto.ch*, *Ricardo.ch*, *tutti.ch*, *anibis.ch*).

---

## ✨ Features

- **Interactive SVG Vehicle Schematic:** Dynamic vector graphic representing a top-down view of a car. Clickable panels sync in real-time with the valuation and estimation engine.
- **AI Natural Language Processing (Spring AI 1.1.7):**
  - **Damage Parser:** Extracts body parts and severity from conversational text inputs (e.g., *"The front bumper is scratched and the left headlight is cracked"*).
  - **Sales Valuation Reports:** Generates professional business justifications in bilingual formatting.
- **Swiss Market Localization:**
  - Standardized labor estimation using Geneva average rates (**150 CHF/h**).
  - Spare parts sourcing curated for Swiss networks (Ricardo, tutti, anibis, Mister-Auto).
- **Modern User Interface:** Sleek Single Page Application (SPA) designed with a curated dark/light theme, custom glassmorphism components, and interactive micro-animations.

---

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3.2.5 (Spring Web, Spring Data JPA, Spring AI 1.1.7 BOM).
- **Database:** H2 (Embedded in-memory database) with custom schema pre-seeding ([data.sql](file:///C:/Users/ruben/Desktop/PROJET_PERSO/ReparValo/src/main/resources/data.sql)).
- **Frontend:** Vanilla HTML5, CSS3 Custom Properties, and Modern ES6 Javascript ([app.js](file:///C:/Users/ruben/Desktop/PROJET_PERSO/ReparValo/src/main/resources/static/js/app.js)).
- **Testing:** JUnit 5, `@DataJpaTest` for database operations, and Mockito for mock-based Gemini LLM structured outputs.

---

## ⚠️ Regional API Quota Note (Switzerland & Europe)

When integrating Google Gemini API keys in Switzerland, the European Economic Area (EEA), or the United Kingdom, free-tier requests return `429 RESOURCE_EXHAUSTED` with `limit: 0` for `generativelanguage.googleapis.com/generate_content_free_tier_requests`.

To comply with GDPR/LPD regulations:
1. **Recommended solution:** Link a valid billing account in the [Google Cloud Console](https://console.cloud.google.com/) for the project containing your API key. This transitions the key to a paid account (which does not train on user data). Google Gemini 2.0 Flash usage costs fractions of a cent per request.
2. **Alternative workaround:** Run your server behind a VPN configured in a region outside Europe (e.g., the United States) to access the standard Gemini Free Tier.

---

## 🚀 How to Run

### 1. Prerequisites
- **Java JDK 17** or higher
- **Apache Maven** 3.8+
- A Google Gemini API Key configured in your system environment under the name: `SPRING_AI_GOOGLE_GENAI_API_KEY`

### 2. Compile and Test
Run the full test suite to verify project status:
```bash
mvn clean test
```

### 3. Start the Server
Launch the Spring Boot application:
```bash
mvn spring-boot:run
```
*Note: The server port is set to **8081** to prevent conflicts with local services running on 8080.*

### 4. Access the Application
Open your browser and navigate to:
- **Dashboard:** [http://localhost:8081](http://localhost:8081)
- **H2 Console:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console) (JDBC URL: `jdbc:h2:mem:reparvalodb`, Username: `sa`, Password: *[leave empty]*)

---

## 📂 Project Structure

```text
reparvalo/
├── pom.xml                                   # Dependency Management (Spring AI 1.1.7)
├── README.md                                 # Core Documentation
└── src/
    ├── main/
    │   ├── java/com/reparvalo/
    │   │   ├── model/                        # Data Entities & DTOs
    │   │   ├── repository/                   # JPA Repositories
    │   │   ├── service/                      # Core Valuation & AI Gemini services
    │   │   └── controller/                   # REST API Controllers
    │   └── resources/
    │       ├── application.properties                # Spring Configurations
    │       ├── data.sql                              # Seed Data
    │       └── static/                               # Static UI Resources
    └── test/java/com/reparvalo/
        ├── repository/                       # JPA integration tests
        └── service/                          # Service unit & AI Mock tests
```