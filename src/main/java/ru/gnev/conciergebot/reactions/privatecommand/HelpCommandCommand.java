package ru.gnev.conciergebot.reactions.privatecommand;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.reactions.eventreactions.AbstractPrivateChatReaction;

import java.util.function.BiConsumer;

@Component
public class HelpCommandCommand extends AbstractPrivateChatReaction {
    @Override
    public Policy getPolicy() {
        return Policy.PRIVATE_CHAT;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), """
                    Команды бота в общем чате:
                    #адрес, #индекс - общая информацию о доме;
                    #квX - позвать в чат соседей из квартиры X;
                    #этажX - позвать в чат соседей с этажа X;
                    #планX - позвать в чат соседей с плана X;
                    #планXэтажY (или #этажYпланX) - позвать соседей с плана X этажа Y.
                                    
                    Команды бота в личном чате с ботом (включают в себя и команды бота в общем чате):
                    #обомне - все, что бот знает о Вас;
                    #соседснизу - список соседей, живущих под Вами;
                    #соседсверху - список соседей, живущих над Вами;
                    #соседирядом - список ближайших соседей, живущих на одном с вами этаже;
                    #соседиплана - список соседей c одинаковым с вами планом;
                    #соседиэтажа - список соседей, живущих на одном с вами этаже.
                    
                    P.S. Если заметите неверную работу бота,
                    скиньте ему скриншот с проблемой - он перешлет ее разработчикам"""));
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }
}
