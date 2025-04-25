## Auto Repair Shop Website

**Automechanic-backend** is a REST API backend designed for managing customer appointments, car details, and more. It includes features like user authentication, car management, and scheduling. The API is designed to be used in conjunction with a frontend to provide a fully interactive user experience.

### Explore Services:
- View a full list of services offered by the auto repair shop, from routine maintenance to complex repairs.

### Sign up as a Customer:
- Easily create an account to access personalized features and track service history.

### Book Appointments:
- Schedule and manage service appointments directly through the platform, with real-time availability and reminders.

### Track Services:
- View past appointments and keep track of vehicle service history to stay on top of future maintenance needs.

---

## Prerequisites

To run this project, you’ll need the following installed:
- **Docker**: To run the application in containers.

Additionally, you will need a **frontend** to interact with the API. The frontend is not included in this repository, but it can be connected to the API using standard HTTP requests.

---

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/GTheD01/automechanic-backend.git
    cd automechanic
    ```

2. Create an `.env` file in the root of the project and add the necessary environment variables (see Configuration section below).

3. Build and run the application with Docker Compose:
    ```bash
    docker-compose up -d
    ```

4. **Frontend Setup**: Ensure you have a frontend that communicates with this API. If you don't have one, you can build a simple frontend using any framework like **React**, **Angular**, or **Vue.js** that interacts with the API.

---

## Configuration

Create a `.env` file in the root directory and provide the required environment variables. Example:

- **JWT_SECRET_KEY**: Secret key for JWT signing.
- **ADMIN_FIRSTNAME**: Admin's first name.
- **ADMIN_LASTNAME**: Admin's last name.
- **ADMIN_EMAIL**: Admin's email.
- **ADMIN_PASSWORD**: Admin's password.
- **DATABASE_URL**: URL for connecting to the database (e.g., PostgreSQL).
- **SPRING_PROFILES_ACTIVE**: Active Spring profiles.
- **SUPPORT_EMAIL_HOST**: Email host for support-related emails.
- **APP_PASSWORD**: Password for application services.
- **AVATAR_DIR**: Directory path for storing avatars.

### RabbitMQ Configuration:
- **RABBITMQ_HOST**: RabbitMQ server host.
- **RABBITMQ_PORT**: RabbitMQ server port.
- **RABBITMQ_USERNAME**: RabbitMQ username.
- **RABBITMQ_PASSWORD**: RabbitMQ password.
- **RABBITMQ_QUEUE_EMAIL**: Email queue name in RabbitMQ.
- **RABBITMQ_EXCHANGE_EMAIL**: Exchange for email-related messages.
- **RABBITMQ_ROUTING_KEY_EMAIL**: Routing key for email.
- **RABBITMQ_EMAIL_DLX_QUEUE**: Dead letter queue for email failures.
- **RABBITMQ_EMAIL_DLX_EXCHANGE**: Dead letter exchange for email failures.
- **RABBITMQ_EMAIL_FAILED**: Name of the failed email queue.

### PostgreSQL Configuration:
- **POSTGRES_DB**: PostgreSQL database name.
- **POSTGRES_PASSWORD**: PostgreSQL password.
- **POSTGRES_USER**: PostgreSQL username.

### Swagger UI and API Docs Paths:
- **SPRINGDOC_API_DOCS_PATH**: Path for Spring API docs.
- **SWAGGER_UI_PATH**: Path for Swagger UI.

Make sure to replace the values with your actual configuration.

---

## API DOCUMENTATION

Once the backend is running, you can view the full API documentation at the configured URL:

- **Swagger UI**: The Swagger UI will be available at the path specified by the `SWAGGER_UI_PATH` environment variable.

- **API Docs**: The raw API documentation (in JSON format) will be available at the path specified by the `SPRINGDOC_API_DOCS_PATH` environment variable.

### Example:
- If you have set the environment variables like this:
  ```properties
  SPRINGDOC_API_DOCS_PATH=/api-docs
  SWAGGER_UI_PATH=/swagger-ui

Then the Swagger UI will be available at:
`http://localhost:8080/swagger-ui`

And the API documentation will be accessible at:
`http://localhost:8080/api-docs`


## Usage
The API is accessible at `http://localhost:8080`. It provides various endpoints to manage user accounts, car details, and appointments.
- For detailed usage of each endpoint, please refer to the API Documentation.
