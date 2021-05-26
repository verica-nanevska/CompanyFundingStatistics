package coding.test.company;

import coding.test.company.service.CompanyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class CompanyApplication {

    @Autowired
    private CompanyStatsService companyStatsService;

    private static final Logger LOGGER = Logger.getLogger(CompanyApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(CompanyApplication.class, args);
    }

    @Bean
    public void printCompanyDataAndGenerateCSVFiles() {
        try {
            String stats = this.companyStatsService.printCompanyStats();
            System.out.println("Country,# Companies,$ Average funding");
            System.out.println(stats);

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "The JSON data file could not be found! Please check if the path you provided was correct.");
        }
    }
}
