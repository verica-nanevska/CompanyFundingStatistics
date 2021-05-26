package coding.test.company.pojos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * A partial mapping of the Company object.
 * All nested objects are excluded.
 */
@Data
public class Company {
    private String name;
    private String permalink;

    @SerializedName("crunchbase_url")
    private String crunchbaseUrl;

    @SerializedName("homepage_url")
    private String homepageUrl;

    @SerializedName("blog_url")
    private String blogUrl;

    @SerializedName("blog_feed_url")
    private String blogFeedUrl;

    @SerializedName("twitter_username")
    private String twitterUsername;

    @SerializedName("category_code")
    private String categoryCode;

    @SerializedName("number_of_employees")
    private int numberOfEmployees;

    @SerializedName("founded_year")
    private int foundedYear;

    @SerializedName("founded_month")
    private int foundedMonth;

    @SerializedName("founded_day")
    private int foundedDay;

    @SerializedName("deadpooled_year")
    private int deadpooledYear;

    @SerializedName("deadpooled_month")
    private int deadpooledMonth;

    @SerializedName("deadpooledDay")
    private int deadpooledDay;

    @SerializedName("deadpooled_url")
    private String deadpooledUrl;

    @SerializedName("tagList")
    private String tagList;

    @SerializedName("aliasList")
    private String aliasList;

    @SerializedName("email_address")
    private String emailAddress;

    @SerializedName("phone_number")
    private String phoneNumber;

    private String description;

    @SerializedName("updatedAt")
    private String updatedAt;

    private String overview;

    @SerializedName("total_money_raised")
    private String totalMoneyRaised;

    /**
     * Convert the Company object into a String, with the values separated by a tabulation
     * so it can be stored into a CSV file.
     * @return a String representing the Company object
     */
    public String toStringCSV() {
        String overviewNoLF = getOverview();
        if(StringUtils.isNotEmpty(overview)) {
            overviewNoLF = overview.replace(StringUtils.LF, StringUtils.EMPTY);
        }
        List<String> company = Arrays.asList(
                getName(), getPermalink(), getCrunchbaseUrl(), getHomepageUrl(),
                getBlogUrl(), getBlogFeedUrl(), getTwitterUsername(), getCategoryCode(),
                String.valueOf(getNumberOfEmployees()), String.valueOf(getFoundedYear()),
                String.valueOf(getFoundedMonth()), String.valueOf(getFoundedDay()),
                String.valueOf(getDeadpooledYear()), String.valueOf(getDeadpooledMonth()),
                String.valueOf(getDeadpooledDay()), getDeadpooledUrl(), getTagList(), getAliasList(),
                getEmailAddress(), getPhoneNumber(), getDescription(), getUpdatedAt(),
                overviewNoLF,
                getTotalMoneyRaised());
        return String.join("\t", company);
    }
}
