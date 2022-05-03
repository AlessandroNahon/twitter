package delco.twitter.scraping.services.implementations;


import com.google.gson.Gson;
import delco.twitter.scraping.model.Images;
import delco.twitter.scraping.model.twitterapi.model_content.Root;
import delco.twitter.scraping.services.interfaces.ImageService;
import delco.twitter.scraping.services.interfaces.VisionAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ImageServiceImplTest {

    private ImageService imageService;
    private VisionAPIService visionAPIService;
    private String validImageURl = "https://pbs.twimg.com/media/FRV057aXEAAGlWs?format=jpg&name=medium";
    private String invalidImageURL = "https://pbs.twimg.com/media/FRdUVv0VsAA1HH1?format=jpg&name=medium";

    private String responseExample = "{\"data\":[{\"id\":\"1512845127810572288\",\"possibly_sensitive\":false,\"conversation_id\":\"1512845127810572288\",\"attachments\":{\"media_keys\":[\"3_1512845121439809537\"]},\"created_at\":\"2022-04-09T17:29:20.000Z\",\"text\":\"Our planet is beautiful and fragile - and we can all act to protect it \\uD83D\\uDC9A\\uD83C\\uDF0E &gt;&gt; https://t.co/duz1wOsXpL https://t.co/EVVEgPK6r5\"},{\"id\":\"1512643954713325573\",\"possibly_sensitive\":false,\"conversation_id\":\"1512643954713325573\",\"attachments\":{\"media_keys\":[\"7_1512642848696729603\"]},\"created_at\":\"2022-04-09T04:09:57.000Z\",\"text\":\"It's time for @AGLAustralia (aka #Australia's biggest climate polluter) to stop clowning around and get serious about closing down all of their #coal-burning power stations before 2030.\\n\\n#IPCC #ActOnClimate https://t.co/H3Ch0lrzPs\"},{\"id\":\"1512551957914824707\",\"possibly_sensitive\":false,\"conversation_id\":\"1512551957914824707\",\"attachments\":{\"media_keys\":[\"3_1512551953670160388\"]},\"created_at\":\"2022-04-08T22:04:23.000Z\",\"text\":\"Basta de violência - Enough violence! Thousands of Indigenous People are gathering in Brasília as part of the Free Land Camp to demand their rights to their land and an end to violence. Stand with them ? https://t.co/gaNDAC54nN\"},{\"id\":\"1512479142582710277\",\"possibly_sensitive\":false,\"conversation_id\":\"1512479142582710277\",\"attachments\":{\"media_keys\":[\"3_1512479138065485825\"]},\"created_at\":\"2022-04-08T17:15:02.000Z\",\"text\":\"The world’s top climate scientists just delivered their rescue plan for humanity, directly to our governments. Here’s what you need to know &gt;&gt;\\nhttps://t.co/Zy4Hkkx5VI https://t.co/snLA5y4mpr\"},{\"id\":\"1512266492087209987\",\"possibly_sensitive\":false,\"conversation_id\":\"1512266492087209987\",\"attachments\":{\"media_keys\":[\"3_1512266486634541056\"]},\"created_at\":\"2022-04-08T03:10:02.000Z\",\"text\":\"Omkoi people in Chiang Mai filed a lawsuit to revoke coal mine's Environmental Impact Assessment (EIA) as it will deprive the local residents of their rights to a healthy environment and natural resources if implemented. #SaveOmkoi #OmkoiNoCoal\\n\\nMore: https://t.co/ORubTObBbf https://t.co/xrz8yqGyu1\"},{\"id\":\"1512173757779087370\",\"possibly_sensitive\":false,\"conversation_id\":\"1512173757779087370\",\"attachments\":{\"media_keys\":[\"3_1512164248520216584\",\"3_1512164565877993477\",\"3_1512164709679747074\",\"3_1512164863782662145\"]},\"created_at\":\"2022-04-07T21:01:33.000Z\",\"text\":\"1/2 From April 4th to the 14th, Indigenous leaders return to Brasília to demand justice and end to violence against Indigenous Peoples. https://t.co/mtBweKALLT\"},{\"id\":\"1512038246489853952\",\"possibly_sensitive\":false,\"conversation_id\":\"1512038246489853952\",\"attachments\":{\"media_keys\":[\"3_1512038241536397312\"]},\"created_at\":\"2022-04-07T12:03:04.000Z\",\"text\":\"The profits of fossil fuel companies are powering Putin’s war machine.\\n\\nHelp us expose 10 European financial institutions and demand that they immediately cut their ties with the Russian fossil fuel industry!\\n\\n\\uD83D\\uDC49 https://t.co/y4p01jJ5Dz\\n\\n#StopFuellingWar #MoneyForChange https://t.co/2ypNAnBYNF\"},{\"id\":\"1511994291576483841\",\"possibly_sensitive\":false,\"conversation_id\":\"1511994291576483841\",\"attachments\":{\"media_keys\":[\"3_1511994285456982016\"]},\"created_at\":\"2022-04-07T09:08:25.000Z\",\"text\":\"Only 1% of Nestlé’s packaging is reusable.\\n\\n\\uD83D\\uDCA7 This is a drop in the ocean of their plastic waste.\\n\\nTheir shareholder meeting is today! Take action, tell #Nestle to be better and commit to 50% reuse and refill by 2030\\n\\nhttps://t.co/NNJsW2HhBm\\n\\n#BreakFreeFromPlastic #ClimateCrisis https://t.co/vhP9lBedYH\"},{\"id\":\"1511944571827388418\",\"possibly_sensitive\":false,\"conversation_id\":\"1511944571827388418\",\"attachments\":{\"media_keys\":[\"3_1511942300246896640\",\"3_1511942300989267971\",\"3_1511942301924618241\"]},\"created_at\":\"2022-04-07T05:50:51.000Z\",\"text\":\"Millions of people are breathing in toxic, polluted air every single day. #WorldHealthDay [1/3] https://t.co/wIj4XMzsbY\"},{\"id\":\"1511814769770045448\",\"possibly_sensitive\":false,\"conversation_id\":\"1511814769770045448\",\"attachments\":{\"media_keys\":[\"3_1511814758021750787\"]},\"created_at\":\"2022-04-06T21:15:03.000Z\",\"text\":\"\\\"We must give ourselves the space to dream without limitations and boundaries.\\\" —Erinn Carter of @FrailtyMyths, a nonprofit that offers empowerment workshops for women, shared 5 lessons for imagining the future with Greenpeace Storytelling &gt;&gt; https://t.co/jQKTSe3sh5 https://t.co/GpR0L4pdHQ\"}],\"includes\":{\"media\":[{\"media_key\":\"3_1512845121439809537\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FP60n1-aQAEwd2x.jpg\"},{\"media_key\":\"7_1512642848696729603\",\"preview_image_url\":\"https://pbs.twimg.com/ext_tw_video_thumb/1512642848696729603/pu/img/0cWmgjEZEtp5aYdG.jpg\",\"type\":\"video\"},{\"media_key\":\"3_1512551953670160388\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FP2p_O7VQAQR6aN.jpg\"},{\"media_key\":\"3_1512479138065485825\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FP1nwzqVEAEkmoc.jpg\"},{\"media_key\":\"3_1512266486634541056\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPymW3WUcAAJ72y.jpg\"},{\"media_key\":\"3_1512164248520216584\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPxJX0sX0AgUIcH.jpg\"},{\"media_key\":\"3_1512164565877993477\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPxJqS8XMAU2ydW.jpg\"},{\"media_key\":\"3_1512164709679747074\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPxJyqpX0AIr8oG.jpg\"},{\"media_key\":\"3_1512164863782662145\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPxJ7ouXsAEDHHU.jpg\"},{\"media_key\":\"3_1512038241536397312\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPvWxQGVUAAWrJ-.jpg\"},{\"media_key\":\"3_1511994285456982016\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPuuyq8VgAAZLHY.jpg\"},{\"media_key\":\"3_1511942300246896640\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPt_gu8acAAKvq7.jpg\"},{\"media_key\":\"3_1511942300989267971\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPt_gxtaIAMT-nu.jpg\"},{\"media_key\":\"3_1511942301924618241\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPt_g1MacAEJw8M.jpg\"},{\"media_key\":\"3_1511814758021750787\",\"type\":\"photo\",\"url\":\"https://pbs.twimg.com/media/FPsLgzIUYAMNNfz.jpg\"}]},\"meta\":{\"newest_id\":\"1512845127810572288\",\"oldest_id\":\"1511814769770045448\",\"result_count\":10,\"next_token\":\"b26v89c19zqg8o3fpytlg4xu7d42l0z00bsw1jsbt3f5p\"}}\n";
    private Root root = new Gson().fromJson(responseExample, Root.class);

    @BeforeEach
    void setup(){
        imageService = new ImageServiceImpl();
        visionAPIService = new VisionAPIServiceImpl();
    }

    @Test
    void testRootCorrect(){
        assertFalse(root.getData().isEmpty());
    }

    @Test
    void downloadImage(){
        Images i = imageService.downloadImage(validImageURl);
        System.out.println(i.toString());
        assertNotEquals(i.getImage().length,0);

    }


    @Test
    void checkAnnotateImageWithObjects(){
        Images image = new Images();
        imageService.annotateImageWithObjects(
                visionAPIService.getValidPictureType("https://pbs.twimg.com/media/FPW4peeVsAQVOtu?format=jpg&name=small"),image);
        System.out.println("\n-->\tCONTENT OF THE IMAGE\n"+image.getImageObjects());
        assertFalse(image.getImageObjects().isEmpty());
    }

    @Test
    void checkDownloadImagesWithoutAnalysis(){
        Images i = imageService.downloadImagesWithoutAnalysis(validImageURl);
        System.out.println(i.toString());
        assertFalse(i.getImageObjects().isEmpty());
    }

    @Test
    void checkGetImages(){
        List<Images> list = new ArrayList<>();
        list = imageService.getImages(root.getIncludes(),root.getData().get(0));
        list.forEach(System.out::println);
        assertFalse(list.isEmpty());
    }


}