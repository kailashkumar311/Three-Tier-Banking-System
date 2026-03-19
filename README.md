🌟 Key Features Explained
Three-Tier Architecture: The project is organized into three logical layers (DAO, Model, and Presentation). This makes the code highly maintainable and professional.

Robust Transaction Management: All monetary actions (Withdraw, Deposit, Transfer) use SQL Transactions (commit and rollback). If a transfer fails mid-way, the money is safely rolled back to the sender's account.

Custom Exception Handling: We have implemented a specialized InsufficientBalanceException to handle cases where a user tries to withdraw more than their current balance.

Security First: Database credentials are not hardcoded. The application fetches the MySQL password from System Environment Variables to prevent unauthorized access.

Clean Code with Lombok: Used Project Lombok annotations like @Getter, @Setter, and @AllArgsConstructor to keep the model classes clean and readable.

Formatted Mini-Statement: Displays the last 5 transactions in a neat, tabular console format for better user experience.

🛠️ How to Run This Project (For Other Users)
If you want to run this project on your local machine, follow these steps:

1. Prerequisites
Java JDK 25 or higher installed.

MySQL Server installed and running.

Maven (for dependency management).

IntelliJ IDEA (Recommended IDE).

2. Database Setup
Create a new schema in your MySQL Workbench:

SQL
CREATE DATABASE three_tier_banking_system;
-- Execute the table creation queries for 'users' and 'transactions' 
-- (Refer to the SQLConstants.java for table structures)
3. Environment Variable Setup (Crucial)
To keep the database secure, this app uses an environment variable for the password.

Windows: Search for "Edit the system environment variables" -> Environment Variables -> New System Variable.

Variable Name: PASSWORD

Variable Value: Your_MySQL_Password

Note: Restart your IDE after setting the variable.

4. Clone and Build
Bash
# Clone the repository
git clone https://github.com/kailashkumar311/Three-Tier-Banking-System.git

# Open in IntelliJ and wait for Maven to download dependencies (Lombok, MySQL Connector)
5. Execute
Run the BankingApp.java file located in src/main/java/com/bank/main/.
