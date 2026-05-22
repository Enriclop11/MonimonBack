package com.enriclop.kpopbot.kpopDB;

import com.enriclop.kpopbot.dto.KpopComeback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class KpopComebacksApi {

    public static List<KpopComeback> getUpcomingComebacks() {
        //get the current month and year and call the getComebacks method
        LocalDate currentDate = LocalDate.now();
        String month = currentDate.getMonth().toString().toLowerCase();
        String year = String.valueOf(currentDate.getYear());
        return getComebacks(month, year);
    }

    public static List<KpopComeback> getComebacks(String month, String year) {
        String urlString = String.format("https://corsproxy.io/?url=https://oauth.reddit.com/r/kpop/wiki/upcoming-releases/%s/%s.json", year, month);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlString)
                .header("User-Agent", "KpopBot/1.0 by /u/Enriclop11")
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("HTTP error: " + response.code() + " " + response.message());
                return null;
            }
            String responseBody = response.body().string();
            log.info(responseBody);
            return KpopComeback.parseScheduledReleases(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
