package ru.gnev.conciergebot.utils;

import org.springframework.stereotype.Service;

@Service
public class CallbackQueryDataHelper {

    private static final String ANSWR = "#answr";

    public String createCallbackQueryData(long tgUserId, long questionId, String answer) {
        return ANSWR + ":" + tgUserId + ":" + questionId + ":" + answer;
    }

    public CallbackQueryParsed parseCallbackQueryData(String data) {
        final String[] split = data.split(":");
        return new CallbackQueryParsed(Long.parseLong(split[1]), Long.parseLong(split[2]), split[3]);
    }
}
