package bluetowel.com.langchecker.network;

import java.io.IOException;

import bluetowel.com.langchecker.utils.BasicCallback;
import bluetowel.com.langchecker.utils.Utilities;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Pawan on 5/14/2017.
 */

public class networkUtils {


    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final String HTTP_HEADER_JSON = "application/json";
//    private static final String baseURL = "https://languagetool.org/api/v2/check";
//private static final String LanguagesURL = "https://languagetool.org/api/v2/languages";

    private static final String baseLocalURL = "http://192.168.0.150:8081/v2/check";
    //    private static final String baseLocalURL = "http://192.168.0.150:8081/v2/check";
    private static final String baseLocalLanguagesURL = "http://192.168.0.150:8081/v2/languages";
    private static final String HTTP_HEADER_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final OkHttpClient client = new OkHttpClient();
    private static String enabledTAG = "&enabledOnly=false";

    private static String reformatText(String text) {
        return text.replace(" ", "%20");
    }

    public static void GETSupportedLanguages() {
        Request request1 = new Request.Builder()
                .url(baseLocalLanguagesURL)
                .get()
                .build();

    }

    public static void POSTcall(String text, String langCode, final BasicCallback callback) {
        text = reformatText(text);
        langCode = "en-US";
        String postBody = "text=" + text + "&language=" + langCode + enabledTAG;

        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody);
        Request request = new Request.Builder()
                .url(baseLocalURL)
                .header("Host", "localhost:8081")
                .addHeader("Content-Type", HTTP_HEADER_FORM_URLENCODED)
                .addHeader("Referer", "Android-Device")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Connection", "keep-alive")
                .post(body)
                .build();

        /*final Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
            if (response.isSuccessful()) {
                String data = response.body().string();
                response.body().close();
                return data;
            } else {
                if (response.code() == HttpURLConnection.HTTP_ENTITY_TOO_LARGE) {
                    response.body().close();
                    return new JSONObject().put("code", HttpURLConnection.HTTP_ENTITY_TOO_LARGE).put("message", "Request data is too large, Try again.").toString();
                }
                response.body().close();
            }

        } catch (Exception e) {

        }
*/
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.callBack(Utilities.CallbackResultCode.FAIL, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    if (response.isSuccessful()) {
                        callback.callBack(Utilities.CallbackResultCode.SUCCESS, response.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
