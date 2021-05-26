package coding.test.company.pojos;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CompanyStats {

    private String fundingStats;
    private Map<Character, List<String>> csvFileData = new HashMap<>();
}
