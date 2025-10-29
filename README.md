# 🛍️ Vandana Retail Billing System

A complete, production-ready retail billing system built with modern technologies for clothing/fashion retail stores.

## ✨ Features

### 💳 Billing & Sales
- **Quick Bill Creation** - Add items on-the-fly without pre-catalog setup
- **Smart Discounts** - 10% fixed discount + optional manual discount
- **Multiple Payment Methods** - Cash, UPI, Card, Online
- **Auto Bill Numbering** - Format: VNDB-YYYYMMDD-XXXX
- **Customer Management** - Optional customer details (name, phone)
- **Anonymous Sales** - Support for walk-in customers

### 📦 Product Management
- **Auto-Add Products** - Products automatically added to catalog when used in bills
- **Smart Autocomplete** - Case-insensitive product name suggestions
- **Price Memory** - Remembers last used price for each product
- **No Duplicates** - Intelligent matching prevents "T-Shirt", "t-shirt", "T-SHIRT" duplicates

### 📊 Reports & Analytics
- **Today's Sales Summary** - Real-time dashboard
- **Date Range Reports** - Custom period analysis
- **Payment Breakdown** - By method (Cash, UPI, Card, Online)
- **Product Sales Analysis** - Top-selling products, quantities, revenue
- **Average Bill Value** - Business insights

### 🏪 Shop Management
- **Shop Settings** - Name, address, phone, email
- **Logo Support** - Ready for shop logo upload
- **Tax Configuration** - Customizable tax rates

## 🛠️ Technology Stack

### Backend
- **Java 21** - Modern Java with latest features
- **Spring Boot 3.5.7** - Production-grade framework
- **Spring Data JPA** - Database abstraction
- **PostgreSQL 17.6** - Robust relational database
- **Flyway** - Database version control & migrations
- **Lombok** - Reduced boilerplate code
- **Maven** - Dependency management

### Infrastructure
- **Docker & Docker Compose** - Containerized PostgreSQL
- **RESTful APIs** - 23+ endpoints
- **JSON** - Data exchange format

### Frontend (Coming Soon)
- **React Native** - Mobile app (Android/iOS)
- **React.js** - Web dashboard
- **Offline Support** - PWA capabilities

## 📂 Project Structure

retail-billing-system/
├── backend/
│ └── retail-billing-backend/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/vandana/retailbilling/
│ │ │ │ ├── config/ # Security & configuration
│ │ │ │ ├── controller/ # REST API endpoints
│ │ │ │ ├── dto/ # Data transfer objects
│ │ │ │ ├── entity/ # JPA entities
│ │ │ │ ├── repository/ # Database access
│ │ │ │ ├── service/ # Business logic
│ │ │ │ └── util/ # Utilities
│ │ │ └── resources/
│ │ │ ├── db/migration/ # Flyway SQL scripts
│ │ │ └── application.properties
│ │ └── test/ # Unit & integration tests
│ └── pom.xml
├── frontend/ # React Native & React web (TBD)
├── docker-compose.yml # PostgreSQL setup
└── README.md


## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Docker & Docker Compose
- Maven 3.8+
- IntelliJ IDEA (recommended) or any Java IDE

### Installation

1. **Clone the repository**

git clone https://github.com/chaitanyamandviya/vandana-retail-billing-system.git
cd vandana-retail-billing-system


2. **Start PostgreSQL Database**

docker compose up -d


3. **Run the Backend**

cd backend/retail-billing-backend
./mvnw spring-boot:run


Or open in IntelliJ and run `RetailBillingBackendApplication.java`

4. **Verify Application**
- Backend: http://localhost:8080/api/actuator/health
- Should return: `{"status":"UP"}`

### Default Credentials
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** OWNER

## 📡 API Endpoints

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID

### Products
- `GET /api/products` - Get all products
- `GET /api/products/suggestions?query={term}` - Autocomplete
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Bills
- `POST /api/bills` - Create new bill
- `GET /api/bills` - Get all bills
- `GET /api/bills/{id}` - Get bill by ID
- `GET /api/bills/today` - Get today's bills
- `GET /api/bills/number/{billNumber}` - Get by bill number

### Shop Settings
- `GET /api/shop-settings` - Get settings
- `PUT /api/shop-settings` - Update settings

### Reports
- `GET /api/reports/today` - Today's summary
- `GET /api/reports/date-range` - Custom range report
- `GET /api/reports/products` - Product sales analysis

## 📊 Database Schema

- **users** - User accounts & authentication
- **shop_settings** - Shop configuration
- **products** - Product catalog (auto-populated)
- **bills** - Main billing records
- **bill_items** - Bill line items
- **payment_transactions** - Payment tracking
- **sync_logs** - Offline sync management
- **flyway_schema_history** - Migration tracking

## 🔒 Security Features (Planned)
- JWT-based authentication
- Role-based access control (OWNER, CASHIER, MANAGER)
- Password encryption with BCrypt
- CORS configuration for frontend

## 📱 Upcoming Features
- React Native mobile app
- React web dashboard
- Offline support with sync
- Bill PDF generation
- WhatsApp bill sharing
- Product image upload
- Barcode scanning
- Inventory management

## 🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License
This project is private and proprietary.

## 👤 Author
**Chaitanya**

## 🙏 Acknowledgments
- Spring Boot community
- PostgreSQL team
- Docker community

---

Built with ❤️ for Vandana Retail Store

