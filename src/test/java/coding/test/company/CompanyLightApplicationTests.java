package coding.test.company;

import coding.test.company.pojos.Company;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyLightApplicationTests {

	@Test
	public void testPrintOutput() {
		Map<String, List<BigDecimal>> data = new HashMap<>();
		List<BigDecimal> numbers = Arrays.asList(BigDecimal.valueOf(6), BigDecimal.valueOf(7), BigDecimal.valueOf(10.76) );
		List<BigDecimal> numbers2 = Arrays.asList(BigDecimal.valueOf(123), BigDecimal.valueOf(12.87), BigDecimal.valueOf(98.54) );

		data.put("France", numbers);
		data.put("Germany", numbers2);

		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		String expectedOutput= "Country,# Companies,$ Average funding\r\nFrance,3,7.92\r\nGermany,3,78.14\r\n";
		CompanyApplication.printCompanyStats(data);
		Assert.assertEquals(expectedOutput, outContent.toString());

	}

	@Test
	public void testCurrencyConverter() throws ParseException {
		Assert.assertEquals(BigDecimal.valueOf(12.6), CompanyApplication.convertCurrencyToNumber("$12.6"));
		Assert.assertEquals(BigDecimal.valueOf(1460800.0), CompanyApplication.convertCurrencyToNumber("C$1.76M"));
		Assert.assertEquals(BigDecimal.valueOf(1401540.0), CompanyApplication.convertCurrencyToNumber("£987k"));
		Assert.assertEquals(BigDecimal.valueOf(3690.0), CompanyApplication.convertCurrencyToNumber("€3.5k"));
	}

	@Test
	public void testCompanyToStringCSV() {
		String companyString = "Test Company\thttp://test-company.com\thttp://crunchbase.com/test-company\thttp://test-company.com\tblog.com\tblog.com/feed\ttwitter-username\tCOM\t165\t2020\t2\t4\t2020\t3\t15\thttp://deadpooled-url.com\twiki, seattle, elowitz, media-industry\talias1, alias2\tnull\t+38907262627\tCompany description\t12-06-2020\t<p>Skype is a software application that allows users  to make voice and video calls and chats over the Internet.</p>\t$12.5M";

		Company company = new Company();
		company.setName("Test Company");
		company.setPermalink("http://test-company.com");
		company.setCrunchbaseUrl("http://crunchbase.com/test-company");
		company.setBlogUrl("blog.com");
		company.setBlogFeedUrl("blog.com/feed");
		company.setHomepageUrl("http://test-company.com");
		company.setCategoryCode("COM");
		company.setTwitterUsername("twitter-username");
		company.setNumberOfEmployees(165);
		company.setUpdatedAt(LocalDate.now().toString());
		company.setFoundedYear(2020);
		company.setFoundedMonth(2);
		company.setFoundedDay(4);
		company.setDeadpooledYear(2020);
		company.setDeadpooledMonth(3);
		company.setDeadpooledDay(15);
		company.setDeadpooledUrl("http://deadpooled-url.com");
		company.setTagList("wiki, seattle, elowitz, media-industry");
		company.setAliasList("alias1, alias2");
		company.setPhoneNumber("+38907262627");
		company.setDescription("Company description");
		company.setUpdatedAt("12-06-2020");
		company.setTotalMoneyRaised("$12.5M");
		company.setOverview("<p>Skype is a software application that " +
				"allows users \n\n to make voice and video calls and " +
				"chats over the Internet.</p>");
		Assert.assertEquals(companyString, company.toStringCSV());
	}

}
