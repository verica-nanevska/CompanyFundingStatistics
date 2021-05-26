package coding.test.company;

import coding.test.company.exception.RetrofitException;
import coding.test.company.pojos.Company;
import coding.test.company.pojos.IpStackResponse;
import coding.test.company.retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class CompanyApplication {

    @Autowired
    private RetrofitService retrofitService;

    @Value("${json.data.file.path}")
    private String companyDataFile;

    @Value("${directory.creation.path}")
    private String directoryPath;

    @Value("${csv.file.header}")
    private String csvHeader;

    private static final Logger LOGGER = Logger.getLogger(CompanyApplication.class.getName());
    public static final String CSV_SEPARATOR = "\t";

    public static void main(String[] args) throws FileNotFoundException, RetrofitException {
        SpringApplication.run(CompanyApplication.class, args);
    }

    @Bean
    public void printCompanyDataAndGenerateCSVFiles() throws RetrofitException, FileNotFoundException {
        File file = ResourceUtils.getFile(companyDataFile);
        Map<String, List<BigDecimal>> companyStatisticsData = new HashMap<>();
        Map<Character, List<String>> csvFilesData = new HashMap<>();

        try (
                InputStream inputStream = new FileInputStream(file);
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        ) {
            reader.beginArray();
            while (reader.hasNext()) {
                Company company = new Gson().fromJson(reader, Company.class);

                writeToCsvFormat(company, csvFilesData);
                String homepageUrl = company.getHomepageUrl();
                if (StringUtils.isNotEmpty(homepageUrl)) {
                    IpStackResponse response = retrofitService.getIpInfo(extractDomainName(homepageUrl));
                    String country = response.getCountryName();
                    if (StringUtils.isNotEmpty(country)) {
                        BigDecimal totalMoneyRaised = convertCurrencyToNumber(company.getTotalMoneyRaised());
                        if (!companyStatisticsData.containsKey(country)) {
                            companyStatisticsData.put(country, new ArrayList<>());
                        }
                        companyStatisticsData.get(country).add(totalMoneyRaised);
                    }
                }
            }

            printCompanyStats(companyStatisticsData);
            writeToCsvFile(csvFilesData);

            reader.endArray();
        } catch (ParseException | IOException | RetrofitException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void printCompanyStats(Map<String, List<BigDecimal>> companyStatisticsData) {
        System.out.println("Country,# Companies,$ Average funding");
        companyStatisticsData.keySet().stream()
                .map(country ->
                        String.join(",", Arrays.asList(country,
                                String.valueOf(companyStatisticsData.get(country).size()),
                                String.valueOf(average(companyStatisticsData.get(country))))))
                .forEach(System.out::println);
    }

    public static BigDecimal convertCurrencyToNumber(String currency) throws ParseException {
        return deduceCurrency(currency)
                .multiply(BigDecimal.valueOf(deduceMultiplicationFactor(currency))).setScale(1, RoundingMode.HALF_DOWN);
    }

    private static BigDecimal average(final List<BigDecimal> numbers) {
        return numbers.stream().reduce(BigDecimal::add)
                .map(sum -> sum.divide(BigDecimal.valueOf(numbers.size()), RoundingMode.HALF_EVEN)
                        .setScale(2, RoundingMode.HALF_EVEN))
                .orElse(BigDecimal.ZERO);
    }

    public void writeToCsvFile(Map<Character, List<String>> csvFilesData) throws IOException {
        File directory = new File(directoryPath);
        directory.mkdir();
        for (Character character : csvFilesData.keySet()) {
            Path path = Paths.get(directoryPath + character + ".csv");
            List<String> rows = csvFilesData.get(character);
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                writer.write(csvHeader);
                writer.newLine();

                for (String row : rows) {
                    writer.write(row);
                    writer.newLine();
                }
            }
        }
        LOGGER.info("\nThe csv files are available in : " + directory.getAbsolutePath());
    }

    private static void writeToCsvFormat(Company company, Map<Character, List<String>> csvFilesData) {
        String name = company.getName();
        if (StringUtils.isNotEmpty(name)) {
            Character firstLetter = Character.toUpperCase((name.charAt(0)));
            if (!csvFilesData.containsKey(firstLetter)) {
                csvFilesData.put(firstLetter, new ArrayList<>());
            }
            csvFilesData.get(firstLetter).add(company.toStringCSV());
        }
    }

    private static String extractDomainName(String homepageUrl) {
        String domainName = homepageUrl.split("//")[1];
        if (domainName.contains("/")) {
            return domainName.split("/")[0];
        }
        return domainName;
    }

    private static int deduceMultiplicationFactor(String amount) {
        int factor;
        switch (amount.substring(amount.length() - 1)) {
            case "k":
                factor = 1000;
                break;
            case "M":
                factor = 1000000;
                break;
            case "B":
                factor = 1000000000;
                break;
            default:
                factor = 1;
                break;
        }
        return factor;
    }

    private static BigDecimal deduceCurrency(String amountString) throws ParseException {
        BigDecimal amountInDollars;
        switch (amountString.charAt(0)) {
            case 'C':
                amountInDollars = parseCurrencyString(amountString, Locale.CANADA).multiply(BigDecimal.valueOf(0.83));
                break;
            case '£':
                amountInDollars = parseCurrencyString(amountString, Locale.UK).multiply(BigDecimal.valueOf(1.42));
                break;
            case '€':
                amountInDollars = parseCurrencyString(amountString, Locale.FRANCE).multiply(BigDecimal.valueOf(1.23));
                break;
            default:
                amountInDollars = parseCurrencyString(amountString, Locale.US);
                break;
        }
        return amountInDollars.stripTrailingZeros();
    }

    private static BigDecimal parseCurrencyString(final String amount, final Locale locale) throws ParseException {
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
        return (BigDecimal) format.parse(amount.replaceAll("[^\\d.,]", ""));
    }
}
