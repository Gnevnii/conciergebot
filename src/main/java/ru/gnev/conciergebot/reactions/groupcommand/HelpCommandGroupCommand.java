package ru.gnev.conciergebot.reactions.groupcommand;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.gnev.conciergebot.bean.Command;
import ru.gnev.conciergebot.bean.entity.Policy;
import ru.gnev.conciergebot.service.IRegistrationService;

import java.util.function.BiConsumer;

@Component
public class HelpCommandGroupCommand extends AbstractGroupChatReaction {

    public HelpCommandGroupCommand(final IRegistrationService registrationService) {
        super(registrationService);
    }

    @Override
    public Policy getPolicy() {
        return Policy.SUPERGROUP_CHAT;
    }

    @Override
    public void react(final Update update, final BiConsumer<Long, Object> sender, final String commandLine) {
        final Message message = update.getMessage();
        if (isNotRegisteredUser(message.getFrom().getId())) {
            sendNotRegisteredMessage(sender, message);
            return;
        }

        sender.accept(message.getChatId(), new SendMessage(String.valueOf(message.getChatId()), """
                    Команды бота в общем чате:
                    #адрес, #индекс - общая информацию о доме;
                    #этажX - тегнуть соседей на этаже X;
                    #планX - тегнуть соседей плана X;
                    #планXэтажY или #этажYпланX - тегнуть жителей плана X с этажа Y.
                                    
                    Команды бота в личном чате с ботом (включают в себя и команды бота в общем чате):
                    #обомне - все что бот знает о Вас
                    #соседснизу - тегнуть соседей, живущих под Вами
                    #соседсверху - тегнуть соседей, живущих над Вами
                    #соседирядом - тегнуть ближайших соседей, живущих на одном с вами этаже
                    #соседиплана - тегнуть соседей c одинаковым с вами планом
                    #соседиэтажа - тегнуть соседей, живущих на одном с вами этаже
                    
                    P.S. Если заметите неверную работу бота, 
                    скиньте ему скриншот с проблемой - он перешлет ее разработчикам"""));
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }
}
