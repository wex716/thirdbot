package org.example.service.logic;

import org.example.db.Registration;
import org.example.db.RegistrationsRepository;
import org.example.statemachine.State;
import org.example.statemachine.TransmittedData;
import org.example.util.buttons.InlineButtonsStorage;
import org.example.util.buttons.InlineKeyboardsStorage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;

public class StartLogic {
    private RegistrationsRepository registrationsRepository;

    public StartLogic() {
        registrationsRepository = new RegistrationsRepository();
    }
    public SendMessage processWaitingCommandStart(String textFromUser, TransmittedData transmittedData) throws Exception {
        SendMessage messageToUser = new SendMessage();
        messageToUser.setChatId(transmittedData.getChatId());

        if (!textFromUser.equals("/start")) {
            messageToUser.setText("Ошибка запуска бота. Для старта пожалуйста введите /start");
            return messageToUser;
        }

        messageToUser.setText("Выберите действие для команд");
        messageToUser.setReplyMarkup(InlineKeyboardsStorage.getStartKeyboard());

        transmittedData.setState(State.WaitingShowOrRegisterCommand);


        return messageToUser;
    }

    public SendMessage processWaitingShowOrRegisterCommand(String textFromUser, TransmittedData transmittedData) throws Exception {
        SendMessage messageToUser = new SendMessage();
        messageToUser.setChatId(transmittedData.getChatId());

        if (textFromUser.equals(InlineButtonsStorage.ShowCommandsStart.getCallBackData()) == false && textFromUser.equals(InlineButtonsStorage.RegisterNewCommandStart.getCallBackData()) == false) {
            messageToUser.setText("Ошибка. Нажмите на кнопку.");
            return messageToUser;
        }

        if (textFromUser.equals(InlineButtonsStorage.ShowCommandsStart.getCallBackData()) == true) {
            ArrayList<Registration> registrations = registrationsRepository.getAllRegistrations();

            int countRegistrations = registrations.size();
            int currentRegistrationNumber = 1;

            transmittedData.getDataStorage().add("registrations", registrations);

            transmittedData.getDataStorage().add("countRegistrations", countRegistrations);

            transmittedData.getDataStorage().add("currentRegistrationNumber", currentRegistrationNumber);

            Registration currentRegistration = registrations.get(currentRegistrationNumber - 1);

            messageToUser.setText(String.format("Команда %d из %d\nНазвание команды: %s\nКоличество членов в команде: %d\nНомер выбранной задачи: %d", currentRegistrationNumber, countRegistrations, currentRegistration.getTeamName(), currentRegistration.getNumberOfParticipants(), currentRegistration.getSelectedTask()));

            messageToUser.setReplyMarkup(InlineKeyboardsStorage.getFirstShowKeyboard());

            transmittedData.setState(State.WaitingFirstShowCommands);

        } else if (textFromUser.equals(InlineButtonsStorage.RegisterNewCommandStart.getCallBackData()) == true) {
            messageToUser.setText("Начало регистрации. Пожалуйста введите название команды длиной от 1 до 30 символов");

            transmittedData.setState(State.WaitingInputTeamName);
        }

        return messageToUser;
    }

}
