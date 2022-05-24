package com.company;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Responder extends TelegramLongPollingBot {

    @Override
    public synchronized void onUpdateReceived(Update update) {

        try {
            String response = "I'm sorry, but I haven't understood your message.";
            String chatId = "";

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(response);

            if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null && !update.getCallbackQuery().getData().isEmpty()) {
                chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

                String callBackData = update.getCallbackQuery().getData();

                if (callBackData.equalsIgnoreCase(CallBackData.CD_YES.toString())) {
                    LocalDateTime currentTime = LocalDateTime.now();
                    sendMessage.setText(currentTime.toLocalTime().toString());
                }

                if (callBackData.equalsIgnoreCase(CallBackData.CD_NO.toString())) {
                    sendMessage.setText("Fine, thanks.");
                }
            }

            if (update.hasMessage()) {
                chatId = String.valueOf(update.getMessage().getChatId());

                boolean userExists = MongoDB.userExists(chatId);

                MongoDB.insertNewUserId(chatId);

                if (update.getMessage().hasText()) {
                    String userMessage = update.getMessage().getText().trim();

                    if (userMessage.equalsIgnoreCase("Hello")) {
                        if (userExists) {
                            sendMessage.setText("Hello again! How are you? \uD83D\uDE00");
                        } else {
                            sendMessage.setText("How are you? \uD83D\uDE00");
                        }
                    }

                    if (userMessage.equalsIgnoreCase("How are you")) {
                        sendMessage.setText("I'm fine thank you!");
                    }

                    if (userMessage.contains("time")) {
                        sendMessage.setText("Would you like to know the current time?");


                        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
                        InlineKeyboardButton yesButton = new InlineKeyboardButton();
                        yesButton.setText("\uD83D\uDC4D");
                        yesButton.setCallbackData(CallBackData.CD_YES.toString());
                        InlineKeyboardButton noThanksButton = new InlineKeyboardButton();
                        noThanksButton.setText("No thanks");
                        noThanksButton.setCallbackData(CallBackData.CD_NO.toString());
                        buttonsRow.add(yesButton);
                        buttonsRow.add(noThanksButton);
                        keyboard.add(buttonsRow);

                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        inlineKeyboardMarkup.setKeyboard(keyboard);

                        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                    }

                    if (userMessage.equalsIgnoreCase("/day")) {
                        DayOfWeek todayDayOfTheWeek = LocalDateTime.now().getDayOfWeek();
                        sendMessage.setText(todayDayOfTheWeek.toString());
                    }

                    if (userMessage.contains("contact")) {
                        sendMessage.setText("Can you share your Telegram's phone number with me so our customer service representatives can contact you?");
                        KeyboardRow keyboardRow = new KeyboardRow();
                        KeyboardButton keyboardButton = new KeyboardButton();
                        keyboardButton.setText("Yes, share contact");
                        keyboardButton.setRequestContact(true);
                        keyboardRow.add(keyboardButton);
                        List<KeyboardRow> listOfKeyboardRows = new ArrayList<>();
                        listOfKeyboardRows.add(keyboardRow);
                        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                        replyKeyboardMarkup.setKeyboard(listOfKeyboardRows);
                        replyKeyboardMarkup.setOneTimeKeyboard(true);
                        replyKeyboardMarkup.setResizeKeyboard(true);

                        sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    }
                }

                if (update.getMessage().hasContact()) {
                    sendMessage.setText("Thank you for sending us your phone number. We will contact you shortly!");

                    String phoneNumber = update.getMessage().getContact().getPhoneNumber().trim();
                    //You can now encrypt the phone number and store it.

                }
            }


            if (chatId.isEmpty()) {
                throw new IllegalStateException("The chat id couldn't be identified or found.");
            }
            sendMessage.setChatId(chatId);
            sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }catch (Exception exception){
            exception.printStackTrace();
        }finally {
        }

    }


    @Override
    public String getBotUsername() {
        return Bot.USERNAME;
    }

    @Override
    public String getBotToken() {
        return Bot.BOT_TOKEN;
    }

}