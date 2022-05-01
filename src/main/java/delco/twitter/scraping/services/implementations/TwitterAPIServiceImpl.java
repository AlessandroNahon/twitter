package delco.twitter.scraping.services.implementations;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.model.user_model_content.UserRoot;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class TwitterAPIServiceImpl extends Thread implements TwitterAPIService {

    private final String BEARER_TOKEN;

    public TwitterAPIServiceImpl(@Value("${BEARER_TOKEN}") String bearer_token) {
        BEARER_TOKEN = bearer_token;
    }

    /**
     * This methods gather the Root file (JSON) from the Twitter API
     * @param username The Screen name of the user that you want to get the tweets
     * @param maxDate The date of the last tweet that you want to get
     * @return The Root file (JSON) from the Twitter API
     */
    @Override
    public Root getTweets(String username, Date maxDate) {
        Response response = null;
        Root raiz = new Root();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/users/"+getUserId(username)+"/tweets?tweet.fields=conversation_id,created_at&expansions=attachments.media_keys&" +
                            "media.fields=media_key,preview_image_url,url&max_results=100")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer "+BEARER_TOKEN)
                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
                    .build();
            raiz = new Gson().fromJson(client.newCall(request)
                            .execute()
                            .body()
                            .string(), Root.class);
            return raiz;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This methods is used to recover the next page of the Root file (JSON) from the Twitter API
     * @param username The Screen name of the user that you want to get the tweets
     * @param lastSearch The last Root file (JSON) recovered, to get the PaginationToken and continue searching
     *                   from the last recovered twet
     * @return The Root file (JSON) from the Twitter API
     */
    @Override
    public Root getNextTweets(String username,Root lastSearch) {
        Response response = null;
        Root raiz = new Root();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/users/"+getUserId(username)+"/tweets?tweet.fields=conversation_id,created_at&expansions=attachments.media_keys&media.fields=" +
                            "media_key,preview_image_url,url&&max_results=100&pagination_token="+lastSearch.getMeta().getNext_token())
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer "+BEARER_TOKEN)
                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
                    .build();
            raiz = new Gson().fromJson(client.newCall(request)
                    .execute()
                    .body()
                    .string(), Root.class);
            Thread.sleep(500);
            return raiz;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is user to recover the first ten replies of a tweet
     * @param conversationId Conversation ID of the tweet that you want to get the replies (You find this
     *                       in the tweet.conversation_id field)
     * @param originalTweet The tweet that you want to assign these replies to
     * @return The Root file (JSON) from the Twitter API with the replies
     */
    @Override
    public Root getReplies(String conversationId, Tweet originalTweet) {
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/tweets/search/recent?expansions=attachments.media_keys&" +
                            "query=conversation_id:"+conversationId+"&media.fields=media_key,preview_image_url,url")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer "+BEARER_TOKEN)
                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
                    .build();

            response = client.newCall(request).execute();
            Thread.sleep(500);
            return new Gson().fromJson(response.body().string(), Root.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calls the Twitter API to get the user ID from the screen name
     * @param username The screen name of the user that you want to get the ID
     * @return The user ID
     */
    @Override
    public String getUserId(String username) {
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/users/by/username/"+username)
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer "+BEARER_TOKEN)
                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
                    .build();

            response = client.newCall(request).execute();
            return new Gson().fromJson(response.body().string(), UserRoot.class).getData().getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }





}
