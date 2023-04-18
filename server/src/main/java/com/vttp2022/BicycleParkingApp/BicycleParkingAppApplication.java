package com.vttp2022.BicycleParkingApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.vttp2022.BicycleParkingApp.services.TelegramBot;
import com.vttp2022.BicycleParkingApp.services.WebNotificationService;
import com.vttp2022.BicycleParkingApp.services.EmailSenderService;

@SpringBootApplication
public class BicycleParkingAppApplication {

	@Autowired
	private EmailSenderService senderService;

	@Autowired
	private WebNotificationService webNotificationService;

	public static void main(String[] args) {
		
		/* 
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new BicycleParkingBot());
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}
		*/

		SpringApplication.run(BicycleParkingAppApplication.class, args);
	}

	/*
	@EventListener(ApplicationReadyEvent.class)
	public void webNotification() {
		webNotificationService.sendNotification();
	}
	*/

	/*
	@EventListener(ApplicationReadyEvent.class)
	public void telegramBot(){

		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new TelegramBot());
		} catch (TelegramApiException e) {
				e.printStackTrace();
		}

	}
	*/



	/* 
	@EventListener(ApplicationReadyEvent.class)
	public void sendMail() {
		senderService.sendEmail("fcweixun@gmail.com", "This is the subject", "This is the Body of the Email");
	}
	*/
}
