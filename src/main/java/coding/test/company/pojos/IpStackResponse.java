package coding.test.company.pojos;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class IpStackResponse {
    @SerializedName("country_name")
    private String countryName;
}
