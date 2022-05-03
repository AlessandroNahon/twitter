package delco.twitter.scraping.services.implementations;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.model.twitterapi.user_model_content.UserRoot;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
public class TwitterAPIServiceImpl extends Thread implements TwitterAPIService {

    private final String BEARER_TOKEN;
    private final String max_tweets = "10";

    public TwitterAPIServiceImpl(@Value("${BEARER_TOKEN}") String bearer_token) {
        BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAAFelbAEAAAAA6BKWPBKmWTviEy2Pr1BPj1yhh3Q%3D8Kq8C7d0Vl9vOE0LwXroMtAiYriC5yq9FperLTjQBCNIXXndam";
    }

    /**
     * This methods gather the Root file (JSON) from the Twitter API
     * @param username The Screen name of the user that you want to get the tweets

     * @return The Root file (JSON) from the Twitter API
     */
    @Override
    public Root getTweets(String username, String startDate, String endDate) {
        Response response = null;
        Root raiz = new Root();
        try {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/tweets/search/all?start_time=" +startDate+
                            "&end_time="+endDate+"&max_results="+max_tweets+"&tweet.fields=conversation_id,created_at," +
                            "possibly_sensitive&expansions=attachments.media_keys&media.fields=preview_image_url,url&" +
                            "user.fields=username&query=from:"+username+" -is:retweet -is:reply (has:media OR has:videos)")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer "+this.BEARER_TOKEN)
                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
                    .build();
            raiz = new Gson().fromJson(client.newCall(request)
                            .execute()
                            .body()
                            .string(), Root.class);
            System.out.println(raiz);

            Thread.sleep(500);
            return raiz;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Root getNextTweets( String paginationToken, String startDate, String endDate) {
        Response response = null;
        Root raiz = new Root();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/tweets/search/all?expansions=attachments.media_keys&media.fields=" +
                            "preview_image_url,url&user.fields=username&tweet.fields=conversation_id,created_at," +
                            "possibly_sensitive&query=from:Greenpeace -is:retweet -is:reply (has:media OR has:videos)" +
                            "&max_results="+max_tweets+"&start_time="+startDate+"&end_time=" + endDate +
                            "&next_token="+paginationToken)
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
     * @return The Root file (JSON) from the Twitter API with the replies
     */
    @Override
    public Root getReplies(String conversationId) {
        Response response = null;
        Root raiz = new Root();
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.twitter.com/2/tweets/search/all?media.fields=media_key,preview_image_url" +
                            ",url&query=conversation_id:"+conversationId+"&max_results=10&" +
                            "expansions=attachments.media_keys&tweet.fields=conversation_id,created_at,possibly_sensitive")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer "+this.BEARER_TOKEN)
                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
                    .build();
            raiz = new Gson().fromJson(client.newCall(request)
                    .execute()
                    .body()
                    .string(), Root.class);
            Thread.sleep(500);
            return raiz;
        } catch (IOException | InterruptedException e) {
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
