package delco.twitter.scraping.bootstrap;

import delco.twitter.scraping.model.*;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.repositories.*;
import delco.twitter.scraping.services.interfaces.RepliesService;
import delco.twitter.scraping.services.interfaces.TweetService;
import delco.twitter.scraping.services.interfaces.TwitterAPIService;
import delco.twitter.scraping.services.interfaces.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Component class DataLoader  {

    @Autowired
    private RepliesService repliesService;

    @Autowired
    private RepliesRepository repliesRepository;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private SentimentRepository sentimentRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private WordService wordService;

    @Autowired
    private TwitterAPIService twitterAPIService;

    @Autowired
    private TweetService tweetService;

    public DataLoader(){

    }


    public void exportarDatabaseSucio(){
        try{
            File carpeta = new File("C:\\Users\\chris\\OneDrive\\Desktop\\ficherosSerializados");
            List<Tweet> tweets = tweetRepository.findAll();
            List<Reply> replies = repliesRepository.findAll();
            List<Word> words = wordRepository.findAll();
            List<Sentiments> sentiments = new ArrayList<>();
            sentimentRepository.findAll().forEach(sentiments::add);
            List<Images> images = new ArrayList<>();
            imageRepository.findAll().forEach(images::add);

            File tuits = new File(carpeta.getAbsolutePath()+File.separator+"tweets.dat");
            tuits.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(carpeta.getAbsolutePath()+File.separator+"tweets.dat")));
            out.writeObject(tweets);
            out.close();

            File respuestas = new File(carpeta.getAbsolutePath()+File.separator+"replies.dat");
            respuestas.createNewFile();
            ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(new File(carpeta.getAbsolutePath()+File.separator+"replies.dat")));
            out2.writeObject(replies);
            out2.close();

            File palabras = new File(carpeta.getAbsolutePath()+File.separator+"words.dat");
            palabras.createNewFile();
            ObjectOutputStream out3 = new ObjectOutputStream(new FileOutputStream(palabras));
            out3.writeObject(words);
            out3.close();

            File sentimientos = new File(carpeta.getAbsolutePath()+File.separator+"sentiment.dat");
            sentimientos.createNewFile();
            ObjectOutputStream out4 = new ObjectOutputStream(new FileOutputStream(sentimientos));
            out4.writeObject(sentiments);
            out4.close();

            File imagenes = new File(carpeta.getAbsolutePath()+File.separator+"images.dat");
            imagenes.createNewFile();
            ObjectOutputStream out5 = new ObjectOutputStream(new FileOutputStream(imagenes));
            out5.writeObject(images);
            out5.close();
        }catch (JpaObjectRetrievalFailureException e){
            System.out.println("Error al exportar la base de datos");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public void executeSearch(String username){
        String startDate = "2021-06-30"+"T00:00:00-00:00";
        String endDate = "2021-12-31"+"T00:00:00-00:00";
        Root r = twitterAPIService.getTweets(username,startDate,endDate);
        tweetService.parseTweetDatumFromRoot(r, username);
        while(r.getMeta().getNext_token() != null){
            r = twitterAPIService.getNextTweets(r.getMeta().getNext_token(),startDate,endDate,username);

            tweetService.parseTweetDatumFromRoot(r, username);
        }
    }
//
//    public void initiateProgram(){
//        try {
//            URI uri = new URI("https://localhost:8083");
//            Desktop.getDesktop().browse(uri);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//https://api.twitter.com/2/tweets/search/all?start_time=2021-01-01T00:00:00-00:00&end_time=2021-06-30T00:00:00-00:00&max_results=25&tweet.fields=conversation_id,created_at,possibly_sensitive&expansions=attachments.media_keys&media.fields=preview_image_url,url&user.fields=username&query=from:Greenpeace -is:retweet -is:reply (has:media OR has:videos)
//    }




}







