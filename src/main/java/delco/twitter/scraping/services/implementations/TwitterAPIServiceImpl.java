package delco.twitter.scraping.services.implementations;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import delco.twitter.scraping.model.Image;
import delco.twitter.scraping.model.Reply;
import delco.twitter.scraping.model.Tweet;
import delco.twitter.scraping.model.model_content.Datum;
import delco.twitter.scraping.model.model_content.Includes;
import delco.twitter.scraping.model.model_content.Medium;
import delco.twitter.scraping.model.model_content.Root;
import delco.twitter.scraping.model.user_model_content.UserRoot;
import delco.twitter.scraping.repositories.ImageRepository;
import delco.twitter.scraping.repositories.ThesaurusRepository;
import delco.twitter.scraping.repositories.TweetRepository;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
public class TwitterAPIServiceImpl implements TwitterAPIService {

    private final String BEARER_TOKEN;
    private final ImageRepository imageRepository;
    private final ThesaurusRepository thesaurusRepository;
    private final SentimentServiceImpl sentimentService;
    private final TweetRepository tweetRepository;
    private final ThesaurusServiceImpl thesaurusService;
    private Date maxDate;
    boolean alcanzaFecha = false;

    public TwitterAPIServiceImpl(@Value("${BEARER_TOKEN}") String bearer_token, ImageRepository imageRepository,
                                 ThesaurusRepository wordRepository, SentimentServiceImpl sentimentService,
                                 TweetRepository tweetRepository, ThesaurusServiceImpl thesaurusService) {
        BEARER_TOKEN = bearer_token;
        this.imageRepository = imageRepository;
        this.thesaurusRepository = wordRepository;
        this.sentimentService = sentimentService;
        this.tweetRepository = tweetRepository;
        this.thesaurusService = thesaurusService;
    }

    @Override
    public void getTweets(String username, Date maxDate) {
        this.maxDate = maxDate;
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
            parseTweetDatumFromRoot(raiz ,username);
            while(!alcanzaFecha){
                getNextTweets(username,raiz);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getNextTweets(String username,Root lastSearch) {
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
            parseTweetDatumFromRoot(raiz ,username);
            if(!alcanzaFecha){
                getNextTweets(username,raiz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getReplies(String conversationId, Tweet originalTweet) {
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
            parseReplyDatumFromRoot(new Gson().fromJson(response.body().string(),Root.class),originalTweet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRetweet(String tweet) {
        return tweet.contains("RT @");
    }

    @Override
    public boolean containsMedia(Datum datum) {
        return datum.getAttachments().getMedia_keys().size() != 0;
    }

    @Override
    public void setTweetImage(Includes include, Datum datum, Tweet originalTweet) {
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()){
                for(Medium media : include.getMedia()){
                    if(media.getMedia_key().equals(mediaKey)){
                        URL url = null;
                        try {
                            if(media.getType().equals("photo")){
                                url = new URL(media.getUrl());
                            }else{
                                url = new URL(media.getPreview_image_url());
                            }
                            image = ImageIO.read(url);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", baos);
                            Image i = new Image();
                            i.setImage(baos.toByteArray());
                            originalTweet.addImage(i);
                            System.out.println("\tIMAGEN AÑADIDA CORRECTAMENTE");
                        }  catch (IOException | NullPointerException e) {
                            System.out.println("ERROR EN LA IMAGEN "+e.getMessage());
                        }

                    }
                }
            }
        }catch (NullPointerException e){
            System.out.println("No media for Tweet with ID: "+originalTweet.getId());
        }
    }

    @Override
    public void setReplyImage(Includes include, Datum datum, Reply originalTweet) {
        BufferedImage image = null;
        try{
            for(String mediaKey : datum.getAttachments().getMedia_keys()) {
                for (Medium media : include.getMedia()) {
                    if (media.getMedia_key().equals(mediaKey)) {
                        URL url = null;
                        try {
                            if (media.getType().equals("photo")) {
                                url = new URL(media.getUrl());
                            } else {
                                url = new URL(media.getPreview_image_url());
                            }
                            image = ImageIO.read(url);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(image, "jpg", baos);
                            Image i = new Image();
                            i.setImage(baos.toByteArray());
                            originalTweet.addImage(i);
                            System.out.println("\tIMAGEN AÑADIDA CORRECTAMENTE");
                        } catch (IOException | NullPointerException e) {
                            System.out.println("ERROR EN LA IMAGEN " + e.getMessage());
                        }

                    }
                }
            }
        }catch (NullPointerException e){
            System.out.println("No media for Tweet with ID: "+originalTweet.getId());
        }
    }


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


    @Override
    public void parseTweetDatumFromRoot(Root root,String username) {
        root.getData().forEach(datum -> {
            if (!isRetweet(datum.getText()) && containsMedia(datum)) {
                System.out.println("Analiza tweet: " + datum.getText());
                if(!datum.getCreated_at().before(this.maxDate)){
                    Tweet tweet = new Tweet();
                    tweet.setText(datum.getText());
                    tweet.setCreatedAt(datum.getCreated_at());
                    tweet.setUsername(username);
                    tweet.setConversationId(datum.getConversation_id());
                    tweet.setTextSentiment(sentimentService.getSentiment(datum.getText()));
                    setTweetImage(root.getIncludes(), datum, tweet);
                            getReplies(datum.getConversation_id(), tweet);
                            tweetRepository.save(tweet);
                    thesaurusService.addText(datum.getText());
                }else{
                    alcanzaFecha = true;
                    return;
                }
            }
        });
    }

    @Override
    public void parseReplyDatumFromRoot(Root datum, Tweet originalTweet) {
        if (datum.getData().size() != 0) {
            for (int i = 0; i < Math.min(datum.getData().size(), 10); i++) {
                try {
                    Datum dt = datum.getData().get(i);
                        System.out.println("Analiza respuesta: " + dt.getText());
                        Reply reply = new Reply();
                        reply.setText(dt.getText());
                        reply.setTextSentiment(sentimentService.getSentiment(dt.getText()));
                        setReplyImage(datum.getIncludes(), dt, reply);
                        originalTweet.addReply(reply);
                        thesaurusService.addText(dt.getText());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                    return;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }


}
