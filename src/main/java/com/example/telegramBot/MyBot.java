package com.example.telegramBot;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

@Component
public class MyBot extends TelegramLongPollingBot {

    private final String username = "test_aru_bot";
    private final String token = "5458291387:AAErZIZKBRShUyAcAgxVGYW_UrAJhJ6QMjE";


    List<String> symbol = Collections.singletonList(CurrencyType.KZT.toString());
    List<CurrencyType> availableCurrencies = Arrays.asList(CurrencyType.EUR, CurrencyType.USD, CurrencyType.RUB);

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.getMessage().getText().equals("/start")) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> keyboardRowList = new ArrayList<>();

            KeyboardRow keyboardRow;
            for (CurrencyType availableCurrency : availableCurrencies) {
                keyboardRow = new KeyboardRow();
                keyboardRow.add(availableCurrency.toString());
                keyboardRowList.add(keyboardRow);
            }

            replyKeyboardMarkup.setKeyboard(keyboardRowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText("Hi! Please select from the menu: ");
        } else if (update.getMessage().getText().equals(CurrencyType.USD.toString())) {
            List<Currency> currencies = getCurrency(update.getMessage().getChatId().toString(), CurrencyType.USD);
            sendMessage.setText(generateMessageByCurrency(currencies));
        } else if (update.getMessage().getText().equals(CurrencyType.EUR.toString())) {
            List<Currency> currencies = getCurrency(update.getMessage().getChatId().toString(), CurrencyType.EUR);
            sendMessage.setText(generateMessageByCurrency(currencies));
        } else if (update.getMessage().getText().equals(CurrencyType.RUB.toString())) {
            List<Currency> currencies = getCurrency(update.getMessage().getChatId().toString(), CurrencyType.RUB);
            sendMessage.setText(generateMessageByCurrency(currencies));
        } else {
            sendMessage.setText("No chosen");
        }
        try {
            sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private List<Currency> getCurrency(String chatId, CurrencyType base) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8181/subscribers/" + chatId + "/" + base)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String res = (response.body().string());
        Gson gson = new Gson();
        return Arrays.asList(gson.fromJson(res, Currency[].class));
    }

    public String generateMessageByCurrency(List<Currency> currencies) {
        StringBuilder msg = new StringBuilder();

        for (Currency currency : currencies) {
            msg.append("BASE: ").append(currency.getBase()).append("\n")
                    .append("DATE: ").append(currency.getDate()).append("\n")
                    .append("RATE: ").append(currency.getKztRate())
                    .append("\n\n");
        }
        return msg.toString();

    }
}
