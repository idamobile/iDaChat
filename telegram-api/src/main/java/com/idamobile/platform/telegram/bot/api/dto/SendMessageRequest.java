package com.idamobile.platform.telegram.bot.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendMessageRequest {

    public static final String PARSE_MODE_MD = "Markdown";
    public static final String PARSE_MODE_HTML = "HTML";

    /**
     * String or Integer
     */
    private Object chatId;

    private String text;

    private String parseMode;

    private Boolean disableWebPageView;

    private Boolean disableNotification;

    private Integer replyToMessageId;

    private Object replyMarkup;

    public SendMessageRequest(int chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }

    public SendMessageRequest(int chatId, String text, String parseMode) {
        this.chatId = chatId;
        this.text = text;
        this.parseMode = parseMode;
    }

    /*

chat_id	Integer or String	Yes	Unique identifier for the target chat or username of the target channel (in the format @channelusername)
text	String	Yes	Text of the message to be sent
parse_mode	String	Optional	Send Markdown or HTML, if you want Telegram apps to show bold, italic, fixed-width text or inline URLs in your bot's message.
disable_web_page_preview	Boolean	Optional	Disables link previews for links in this message
disable_notification	Boolean	Optional	Sends the message silently. iOS users will not receive a notification, Android users will receive a notification with no sound.
reply_to_message_id	Integer	Optional	If the message is a reply, ID of the original message
reply_markup	InlineKeyboardMarkup or ReplyKeyboardMarkup or ReplyKeyboardHide or ForceReply	Optional	Additional interface options. A JSON-serialized object for an inline keyboard, custom reply keyboard, instructions to hide reply keyboard or to force a reply from the user.

     */
}
