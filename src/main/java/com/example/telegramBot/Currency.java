package com.example.telegramBot;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Currency {

    private String base;

    private String date;

    private HashMap<String, String> rates;

}
