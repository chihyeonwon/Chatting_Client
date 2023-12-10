package com.djdjsn.emochat.utils.res;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;

public class StringRes {

    public static String friendsNumber(int number) {
        if (number > 0) {
            return String.format(Locale.getDefault(), "%d명의 친구가 있습니다", number);
        }
        return "새로운 친구를 추가해보세요";
    }

    public static String phone(String source) {
        if (source.length() == 11) {
            String head = source.substring(0, 3);
            String body = source.substring(3, 7);
            String tail = source.substring(7, 11);
            return String.format(Locale.getDefault(), "%s-%s-%s", head, body, tail);
        }
        return source;
    }

    public static String newFriendAdded(String nickname) {
        return String.format(Locale.getDefault(), "%s님이 새로운 친구가 되었습니다", nickname);
    }

    public static String friendDeleted(String nickname) {
        return String.format(Locale.getDefault(), "%s님이 친구에서 삭제되었습니다", nickname);
    }

    public static String hourMinute(long millis) {
        LocalTime ldt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime();
        int hour = ldt.getHour();
        int minute = ldt.getMinute();
        return String.format(Locale.getDefault(), "%s %d:%02d",
                (hour >= 12 ? "오후" : "오전"),
                (hour == 12 ? 12 : hour % 12),
                minute);
    }

    public static String dateTime(long millis) {
        LocalDateTime ldt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        int year = ldt.getYear();
        int month = ldt.getMonthValue();
        int dayOfMonth = ldt.getDayOfMonth();
        int hour = ldt.getHour();
        int minute = ldt.getMinute();
        return String.format(Locale.getDefault(), "%d-%02d-%02d %s %d:%02d",
                year, month, dayOfMonth,
                (hour >= 12 ? "오후" : "오전"),
                (hour == 12 ? 12 : hour % 12),
                minute);
    }

    public static String secondsRemainingForVerification(int seconds) {
        return String.format(Locale.getDefault(), "인증 (%s초)", seconds);
    }

}








