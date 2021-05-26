package coding.test.company.service;

import coding.test.company.exception.RetrofitException;
import coding.test.company.pojos.Company;
import coding.test.company.pojos.IpStackResponse;
import coding.test.company.retrofit.RetrofitService;
import coding.test.company.utils.CurrencyConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CompanyStatsService {
    @Autowired
    private RetrofitService retrofitService;

    @Value("${json.data.file.path}")
    private String companyDataFile;

    @Value("${directory.creation.path}")
    private String directoryPath;

    @Value("${csv.file.header}")
    private String csvHeader;

    private static final Logger LOGGER = Logger.getLogger(CompanyStatsService.class.getName());

    public String printCompanyStats() throws FileNotFoundException {
        String stats = StringUtils.EMPTY;
        File file = ResourceUtils.getFile(companyDataFile);

        Map<String, List<BigDecimal>> companyStatisticsData = new HashMap<>();
        Map<Character, List<String>> csvFilesData = new HashMap<>();

        try (
                InputStream inputStream = new FileInputStream(file);
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        ) {
            reader.beginArray();
            while (reader.hasNext()) {
                extractDataFromJsonFile(reader, companyStatisticsData, csvFilesData);
            }

            stats = appendCompanyStats(companyStatisticsData);
            writeToCsvFile(csvFilesData);

            reader.endArray();
        } catch (ParseException | IOException | RetrofitException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return stats;
    }

    private void extractDataFromJsonFile(JsonReader reader,
                                         Map<String, List<BigDecimal>> companyStatisticsData,
                                         Map<Character, List<String>> csvFilesData) throws ParseException, RetrofitException {
        Company company = new Gson().fromJson(reader, Company.class);
        writeToCsvFormat(company, csvFilesData);
        String homepageUrl = company.getHomepageUrl();
        if (StringUtils.isNotEmpty(homepageUrl)) {
            callIpStackAPI(company, homepageUrl, companyStatisticsData);
        }
    }

    private void callIpStackAPI(Company company, String homepageUrl, Map<String, List<BigDecimal>> companyStatisticsData) throws RetrofitException, ParseException {
        IpStackResponse response = retrofitService.getIpInfo(extractDomainName(homepageUrl));
        String country = response.getCountryName();
        if (StringUtils.isNotEmpty(country)) {
            BigDecimal totalMoneyRaised = CurrencyConverter.convertCurrencyToNumber(company.getTotalMoneyRaised());
            companyStatisticsData.putIfAbsent(country, new ArrayList<>());
            companyStatisticsData.get(country).add(totalMoneyRaised);
        }
    }

    public static String appendCompanyStats(Map<String, List<BigDecimal>> companyStatisticsData) {
        StringBuilder stringBuilder = new StringBuilder();
        companyStatisticsData.keySet().stream()
                .map(country ->
                        String.join(",", Arrays.asList(country,
                                String.valueOf(companyStatisticsData.get(country).size()),
                                String.valueOf(average(companyStatisticsData.get(country))) + StringUtils.LF)))
                .forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    private static BigDecimal average(final List<BigDecimal> numbers) {
        return numbers.stream().reduce(BigDecimal::add)
                .map(sum -> sum.divide(BigDecimal.valueOf(numbers.size()), RoundingMode.HALF_EVEN)
                        .setScale(2, RoundingMode.HALF_EVEN))
                .orElse(BigDecimal.ZERO);
    }

    private static void writeToCsvFormat(Company company, Map<Character, List<String>> csvFilesData) {
        String name = company.getName();
        if (StringUtils.isNotEmpty(name)) {
            Character firstLetter = Character.toUpperCase((name.charAt(0)));
            csvFilesData.putIfAbsent(firstLetter, new ArrayList<>());
            csvFilesData.get(firstLetter).add(company.toStringCSV());
        }
    }

    private void writeToCsvFile(Map<Character, List<String>> csvFilesData) throws IOException {
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
        LOGGER.info("\nThe generated csv files are available in : " + directory.getAbsolutePath());
    }

    private static String extractDomainName(String homepageUrl) {
        String domainName = homepageUrl.split("//")[1];
        if (domainName.contains("/")) {
            return domainName.split("/")[0];
        }
        return domainName;
    }
}
