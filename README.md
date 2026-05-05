# Electricity Bill System

This is a console-based electricity billing system developed using Java, JDBC, and MySQL.
The project is designed to manage consumer data, generate monthly bills, and handle payments efficiently.

## Features

* Admin and User login system
* Add and manage consumers
* Generate monthly electricity bills
* Prevent duplicate bill generation
* Payment system with history tracking
* Delete consumer only if no unpaid bills exist
* Users can view their profile and update phone number

## Technologies Used

* Core Java
* JDBC
* MySQL

## How to Run

1. Import the database using the file in the `database` folder
2. Open the project in Eclipse or any Java IDE (e.g., IntelliJ IDEA)
3. Update database username and password in `DBConnection.java`
4. Run `MainApp.java`

## Demo
Run `MainApp.java` and use the menu options:
- Login as Admin to add consumers and generate bills
- Login as User to view bills, pay bills, and update phone number

## Project Structure

* `src` → Java source files
* `database` → SQL file
* `docs` → Project report

## Note

This project focuses on backend logic and database handling. It can be further improved by adding a graphical interface or web-based features.


## What I learned
This project helped me understand JDBC connectivity, database design, and enforcing data integrity (e.g., preventing deletion when unpaid bills exist).
