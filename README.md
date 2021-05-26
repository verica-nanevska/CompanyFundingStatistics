# Company statistics application

### Context
This Spring Boot application provides statics on the capital raised by different companies, broken down by the country.

It also extracts the data stored in the input json file, and write it to multiple CSV files.
The CSV files are named A.csv, B.csv, …, Z.csv, and contain all the companies whose name starts with that letter.
The separator used in the CSV files is a tabulation.
The amounts in the output are all displayed in dollars.

### Usage
To run the application either:
 - Run spring-boot:run with maven
 - Run Play from the main class directly from your IDE
 - Compile the source code and run: java -jar nameOfjar.jar

If you want to use your own access key for calling IPStack, change it in application.properties.
You can also provide your own path for the creation of the directory where the csv files will be stored.
