package coding.test.company;

import coding.test.company.pojos.Company;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class CompanyTest {

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
