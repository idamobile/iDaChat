package com.idamobile.platform.telegram.bot.framework;

import com.idamobile.platform.fn.XFunction;
import com.idamobile.platform.telegram.bot.api.dto.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ChainManager {

    @Getter @Setter
    private List<PreProcessor> preProcessors = Collections.emptyList();

    @Getter @Setter
    private List<MessageHandler> handlers = Collections.emptyList();

    @Getter @Setter
    private List<PostProcessor> postProcessors = Collections.emptyList();

    public Result process(Message message) throws PreProcessingException, PostProcessingException {

        for (PreProcessor pre: preProcessors) {
            message = pre.apply(message);
        }

        Result result = handle(message);

        for (PostProcessor post: postProcessors) {
            result = post.apply(result);
        }

        return result;
    }

    private Result handle(Message m) {
        try {
            for (MessageHandler handler : handlers) {
                handler.apply(m);
            }
            return Result.empty(m);
        } catch (MessageHandlingCompletedException completion) {
            return Result.completed(completion.getSendMessageRequest(), m, completion.getMessageHandler());
        } catch (MessageHandlingFailedException failure) {
            return Result.failed(m, failure, failure.getMessageHandler());
        }
    }

    public interface PreProcessor extends XFunction<Message, Message, PreProcessingException> {}
    public interface PostProcessor extends XFunction<Result, Result, PostProcessingException> {}

}
