package delco.twitter.scraping.model.model_content;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.Fitzpatrick;

public class Emojis {

    private final Emoji emoji;
    private final Fitzpatrick fitzpatrick;
    private final int startIndex;

    public Emojis(Emoji emoji, String fitzpatrick, int startIndex) {
        this.emoji = emoji;
        this.fitzpatrick = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick);
        this.startIndex = startIndex;
    }

    public Emoji getEmoji() {
        return this.emoji;
    }

    public boolean hasFitzpatrick() {
        return this.getFitzpatrick() != null;
    }

    public Fitzpatrick getFitzpatrick() {
        return this.fitzpatrick;
    }

    public String getFitzpatrickType() {
        return this.hasFitzpatrick() ? this.fitzpatrick.name().toLowerCase() : "";
    }

    public String getFitzpatrickUnicode() {
        return this.hasFitzpatrick() ? this.fitzpatrick.unicode : "";
    }

    public int getEmojiStartIndex() {
        return this.startIndex;
    }

    public int getEmojiEndIndex() {
        return this.startIndex + this.emoji.getUnicode().length();
    }

    public int getFitzpatrickEndIndex() {
        return this.getEmojiEndIndex() + (this.fitzpatrick != null ? 2 : 0);
    }

}
