package com.example.telegramBot;

import com.google.gson.Gson;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MyBot extends TelegramLongPollingBot {

    private final String username = "test_aru_bot";
    private final String token = "5458291387:AAErZIZKBRShUyAcAgxVGYW_UrAJhJ6QMjE";
    private final String apiKey = "crqlrVjmM3eYJiFdYt2d9IG5QPBCT3EU";
    private final String baseUrl = "https://api.apilayer.com/fixer/";

    List<String> symbol = Collections.singletonList(CurrencyType.KZT.toString());
    List<CurrencyType> availableCurrencies = Arrays.asList(CurrencyType.EUR, CurrencyType.USD, CurrencyType.RUB);

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
            String msg = getMessageByBase(CurrencyType.USD.toString());
            sendMessage.setText(msg);
        } else if (update.getMessage().getText().equals(CurrencyType.EUR.toString())) {
            String msg = getMessageByBase(CurrencyType.EUR.toString());
            sendMessage.setText(msg);
        } else if (update.getMessage().getText().equals(CurrencyType.RUB.toString())) {
            String msg = getMessageByBase(CurrencyType.RUB.toString());
            sendMessage.setText(msg);
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

    public Currency getCurrency(List<String> symbols, String base, String date) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl + date + "?symbols=" + symbols.stream().collect(Collectors.joining(",")) + "&base=" + base)
                .addHeader("apikey", apiKey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String res = (response.body().string());
        Gson gson = new Gson();
        return gson.fromJson(res, Currency.class);
    }

    public List<String> generateFormattedDateStringForDays(int count) {
        List<String> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -count);

        for (int i = 0; i < count; i++) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            data.add(sdf.format(cal.getTime()));
        }
        return data;
    }

    public String getMessageByBase(String base) {
        List<String> dates = generateFormattedDateStringForDays(10);
        StringBuilder msg = new StringBuilder();
        for (String date : dates) {
            try {
                Currency currency = getCurrency(symbol, base, date);
                msg.append("BASE: ").append(currency.getBase()).append("\n")
                        .append("DATE: ").append(currency.getDate()).append("\n")
                        .append("RATE: ").append(currency.getRates().get(CurrencyType.KZT.toString()))
                        .append("\n\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg.toString();
    }
}
