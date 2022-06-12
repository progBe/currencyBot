package com.example.telegramBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) throws TelegramApiException {

		SpringApplication.run(TelegramBotApplication.class, args);

		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
		try{
			telegramBotsApi.registerBot(new MyBot());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
