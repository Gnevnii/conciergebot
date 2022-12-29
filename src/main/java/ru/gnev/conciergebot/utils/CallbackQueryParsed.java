package ru.gnev.conciergebot.utils;

public record CallbackQueryParsed(long tgUserId, long questionId, String answer) {
}
