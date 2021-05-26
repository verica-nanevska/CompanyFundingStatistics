package coding.test.company;

import coding.test.company.service.CompanyStatsService;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyStatsServiceTest {

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
		Assert.assertEquals(expectedOutput, CompanyStatsService.appendCompanyStats(data));

	}

}
