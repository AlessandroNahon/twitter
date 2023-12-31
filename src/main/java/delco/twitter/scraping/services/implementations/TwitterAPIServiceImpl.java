package delco.twitter.scraping.services.implementations;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TwitterAPIServiceImpl extends Thread implements TwitterAPIService {


    @Value("${token.BEARER_TOKEN}")
    private String BEARER_TOKEN;
    private String max_tweets = "100";



    /**
     * This methods gather the Root file (JSON) from the Twitter API
     * @param username The Screen name of the user that you want to get the tweets
     * @return The Root file (JSON) from the Twitter API
     */
    @Override
    public Root getTweets(String username, String startDate, String endDate) {
//        Response response = null;
//        Root raiz = new Root();
//
//        try {
//
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("https://api.twitter.com/2/tweets/search/all?start_time=" +startDate+
//                            "&end_time="+endDate+"&max_results="+max_tweets+"&tweet.fields=conversation_id,created_at," +
//                            "possibly_sensitive&expansions=attachments.media_keys&media.fields=preview_image_url,url&" +
//                            "user.fields=username&query=from:"+username+" -is:retweet -is:reply (has:media OR has:videos)")
//                    .method("GET", null)
//                    .addHeader("Authorization", "Bearer "+this.BEARER_TOKEN)
//                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
//                    .build();
//            raiz = new Gson().fromJson(client.newCall(request)
//                            .execute()
//                            .body()
//                            .string(), Root.class);
//            Thread.sleep(600);
//            return raiz;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * This methods gather the next page of tweets that comes after the last tweet of the previous page
     * @param paginationToken Token to the next page of tweets
     * @param startDate Start date of the search
     * @param endDate End date of the search
     * @return The Root file (JSON) from the Twitter API
     */
    @Override
    public Root getNextTweets( String paginationToken, String startDate, String endDate, String username) {
//        Response response = null;
//        Root raiz = new Root();
//        try {
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("https://api.twitter.com/2/tweets/search/all?expansions=attachments.media_keys&media.fields=" +
//                            "preview_image_url,url&user.fields=username&tweet.fields=conversation_id,created_at," +
//                            "possibly_sensitive&query=from:"+username+" -is:retweet -is:reply (has:media OR has:videos)" +
//                            "&max_results="+max_tweets+"&start_time="+startDate+"&end_time=" + endDate +
//                            "&next_token="+paginationToken)
//                    .method("GET", null)
//                    .addHeader("Authorization", "Bearer "+BEARER_TOKEN)
//                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
//                    .build();
//            raiz = new Gson().fromJson(client.newCall(request)
//                    .execute()
//                    .body()
//                    .string(), Root.class);
//            Thread.sleep(600);
//            if(raiz.getData().isEmpty()){
//                return null;
//            }
//            return raiz;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    /**
     * This method is user to recover the first ten replies of a tweet
     * @param conversationId Conversation ID of the tweet that you want to get the replies (You find this
     *                       in the tweet.conversation_id field)
     * @return The Root file (JSON) from the Twitter API with the replies
     */
    @Override
    public Root getReplies(String conversationId, String sinceId) {
//        Response response = null;
//        Root raiz = new Root();
//        try {
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("https://api.twitter.com/2/tweets/search/all?max_results=10&tweet.fields=conversation_id,created_at," +
//                            "possibly_sensitive&expansions=attachments.media_keys&media.fields=media_key,preview_image_url,url" +
//                            "&since_id="+sinceId+"&query=conversation_id:"+conversationId)
//                    .method("GET", null)
//                    .addHeader("Authorization", "Bearer "+this.BEARER_TOKEN)
//                    .addHeader("Cookie", "guest_id=v1%3A164851793848590618")
//                    .build();
//            raiz = new Gson().fromJson(client.newCall(request)
//                    .execute()
//                    .body()
//                    .string(), Root.class);
//            Thread.sleep(500);
//            return raiz;
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }





}
