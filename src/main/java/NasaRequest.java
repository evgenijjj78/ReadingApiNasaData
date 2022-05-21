import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.FileOutputStream;
import java.io.IOException;


public class NasaRequest {

    public static NasaRequest object;
    public String copyright;
    public String date;
    public String explanation;
    public String hdurl;
    public String media_type;
    public String service_version;
    public String title;
    public String url;

    public NasaRequest(){}

    public static void main(String[] args) {

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build()) {
            String url = "https://api.nasa.gov/planetary/apod?api_key=";
            String myKey = "...";
            url += myKey;
            int iter = 0;
            while (iter < 2) {
                CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
                if (response.getStatusLine().getStatusCode() == 200) {
                    if (iter < 1) {
                        object = new ObjectMapper().readValue(response.getEntity().getContent(), NasaRequest.class);
                        url = object.url;
                        response.close();
                    } else {
                        FileOutputStream writer = new FileOutputStream(url.substring(url.lastIndexOf("/") + 1));
                        writer.write(response.getEntity().getContent().readAllBytes());
                        writer.close();
                        response.close();
                    }
                }
                iter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
