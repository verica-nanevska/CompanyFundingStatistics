package coding.test.company.retrofit;

import coding.test.company.exception.RetrofitException;
import coding.test.company.pojos.IpStackResponse;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Configures the retrofit calls to REST APIs
 */
@Service
public class RetrofitService {

    private final RestApiInterface restApiInterface;
    private final String accessKey;
    private final String ipStackUrl;

    public RetrofitService(@Value("${access.key}") String accessKey,
                           @Value("${ipstack.base.url}") String ipStackUrl) {
        this.accessKey = accessKey;
        this.ipStackUrl = ipStackUrl;
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ipStackUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        restApiInterface = retrofit.create(RestApiInterface.class);
    }

    public IpStackResponse getIpInfo(String domainName) throws RetrofitException {
        Call<IpStackResponse> retrofitCall = restApiInterface.getInfo(domainName, accessKey);
        try {
            Response<IpStackResponse> response = retrofitCall.execute();
            return response.body();
        } catch (IOException e) {
            throw new RetrofitException("An error occurred while trying to reach " + retrofitCall.request().url());
        }
    }

}
