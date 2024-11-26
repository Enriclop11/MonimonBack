package com.enriclop.kpopbot;

import com.enriclop.kpopbot.security.Settings;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(Settings.class)
public class KpopBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpopBotApplication.class, args);
	}

	@Autowired
	DataSource dataSource;

	@PreDestroy
	public void onExit() {
		try {
			dataSource.getConnection().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//do a cron job to / every 5 minutes to keep the bot alive

	@Scheduled(fixedRate = 300000)
	public void keepAlive() {
		//https get to /
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL("https://kpopcardbot.onrender.com/").openConnection();
			connection.setRequestMethod("GET");
			connection.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

}