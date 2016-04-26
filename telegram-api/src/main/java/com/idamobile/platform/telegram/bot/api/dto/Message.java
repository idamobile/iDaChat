package com.idamobile.platform.telegram.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private int messageId;

    private User from;

    private int date;

    private Object chat;

    private User forwardFrom;

    private Integer forwardDate;

    private Message replyToMessage;

    private String text;



    /*

entities	Array of MessageEntity	Optional. For text messages, special entities like usernames, URLs, bot commands, etc. that appear in the text
audio	Audio	Optional. Message is an audio file, information about the file
document	Document	Optional. Message is a general file, information about the file
photo	Array of PhotoSize	Optional. Message is a photo, available sizes of the photo
sticker	Sticker	Optional. Message is a sticker, information about the sticker
video	Video	Optional. Message is a video, information about the video
voice	Voice	Optional. Message is a voice message, information about the file
caption	String	Optional. Caption for the document, photo or video, 0-200 characters
contact	Contact	Optional. Message is a shared contact, information about the contact
location	Location	Optional. Message is a shared location, information about the location
venue	Venue	Optional. Message is a venue, information about the venue
new_chat_member	User	Optional. A new member was added to the group, information about them (this member may be the bot itself)
left_chat_member	User	Optional. A member was removed from the group, information about them (this member may be the bot itself)
new_chat_title	String	Optional. A chat title was changed to this value
new_chat_photo	Array of PhotoSize	Optional. A chat photo was change to this value
delete_chat_photo	True	Optional. Service message: the chat photo was deleted
group_chat_created	True	Optional. Service message: the group has been created
supergroup_chat_created	True	Optional. Service message: the supergroup has been created
channel_chat_created	True	Optional. Service message: the channel has been created
migrate_to_chat_id	Integer	Optional. The group has been migrated to a supergroup with the specified identifier, not exceeding 1e13 by absolute value
migrate_from_chat_id	Integer	Optional. The supergroup has been migrated from a group with the specified identifier, not exceeding 1e13 by absolute value
pinned_message	Message	Optional. Specified message was pinned. Note that the Message object in this field will not contain further reply_to_message fields even if it is itself a reply.
     */
}
