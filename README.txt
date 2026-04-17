========================================================
                 AQI FORECASTING SYSTEM
========================================================

OVERVIEW
--------
This application predicts future Air Quality Index (AQI)
values utilizing 9 years of historical data. It features
a custom Java Swing graphical interface with dynamic 2D
graphing and time filters. It employs an Autoregressive
(AR) mathematical model trained via matrix operations
(Gauss-Jordan elimination) to forecast daily AQI values
up to a user-specified target date.

HOW TO RUN THE CODE
-------------------
1. Ensure a MySQL server is running locally on port 3306.
2. Verify the database 'aqi_project' exists and contains
   the table 'aqi_data' with columns 'date' and 'aqi'.
3. Update the USER and PASSWORD constants in
   DBConnection.java to match your MySQL credentials.
4. Add the MySQL JDBC Driver (mysql-connector-j-x.x.x.jar)
   to your project's classpath.
5. Compile all Java files and execute MainUI.java.

SYSTEM FEATURES
---------------
* Neo-Dark GUI: Built entirely in pure Java Swing with
  no external UI libraries required.
* Interactive Graphing: Visualizes historical and
  predicted AQI trends with color-coded trajectories.
* Time Filters: Instantly slice the graph to view the
  last 1 Week, 1 Month, 1 Year, or All Time data.

ASSUMPTIONS MADE
----------------
* The historical data retrieved from the database is
  continuous, has no missing days, and is sorted
  chronologically.
* The last entry in the MySQL database corresponds
  exactly to the dbEndDate defined in MainUI.java
  (2025-12-31).
* The Autoregressive model assumes that the AQI of any
  given day is a linear combination of the AQI from the
  preceding 'n' days (lag parameter set to 7).

OOP CONCEPTS & DESIGN PATTERNS APPLIED
--------------------------------------
1. SINGLETON PATTERN
   Implementation: DBConnection.java
   Managing database connections is resource-intensive.
   The Singleton pattern guarantees that the application
   instantiates exactly one Connection object during its
   entire lifecycle. This prevents memory leaks and
   provides a safe global point of access.

2. ENCAPSULATION
   Implementation: AQIRecord.java and ARModel.java
   Classes strictly hide their internal mathematical and
   state data (private double aqi, private double[] phi).
   Retrieval is forced through designated getter methods,
   protecting the integrity of the forecasting data.

3. ABSTRACTION & POLYMORPHISM
   Implementation: AQIDataProvider.java (Interface)
   and MySQLDataFetcher.java
   Data retrieval is completely abstracted. The MainUI
   and ForecastEngine depend purely on the interface
   rather than the concrete MySQL implementation. This
   allows the system to seamlessly switch to a different
   data source without altering core logic.
========================================================~