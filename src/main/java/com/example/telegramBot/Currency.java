package com.example.telegramBot;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class Currency {

    private String base;

    private LocalDateTime date;

    private double kztRate;


}
