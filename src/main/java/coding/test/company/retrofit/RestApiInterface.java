package coding.test.company.retrofit;

import coding.test.company.pojos.IpStackResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiInterface {

    @GET("{domainName}")
    Call<IpStackResponse> getInfo(@Path("domainName") String domainName,
                                  @Query("access_key") String accessKey);
}
