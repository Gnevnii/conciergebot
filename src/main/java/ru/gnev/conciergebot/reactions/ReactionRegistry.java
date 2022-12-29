package ru.gnev.conciergebot.reactions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gnev.conciergebot.config.BotConfiguration;
import ru.gnev.conciergebot.persist.repository.UserRegistrationAnswerRepository;
import ru.gnev.conciergebot.persist.repository.UserRepository;
import ru.gnev.conciergebot.reactions.eventreactions.BotAddedToChatReaction;
import ru.gnev.conciergebot.reactions.eventreactions.BotRecievePhotoReaction;
import ru.gnev.conciergebot.reactions.eventreactions.IReact;
import ru.gnev.conciergebot.reactions.eventreactions.UpdateUserInfoReaction;
import ru.gnev.conciergebot.reactions.eventreactions.UserRemovedFromChatReaction;
import ru.gnev.conciergebot.reactions.eventreactions.withanswerkeyboard.UserRegistrationWKeyboardReaction;
import ru.gnev.conciergebot.reactions.groupcommand.AddressGroupCommand;
import ru.gnev.conciergebot.reactions.groupcommand.GetUsersByFlatGroupCommand;
import ru.gnev.conciergebot.reactions.groupcommand.GetUsersByFloorGroupCommand;
import ru.gnev.conciergebot.reactions.groupcommand.GetUsersByFloorNSectionGroupCommand;
import ru.gnev.conciergebot.reactions.groupcommand.GetUsersBySectionGroupCommand;
import ru.gnev.conciergebot.reactions.groupcommand.HelpCommandGroupCommand;
import ru.gnev.conciergebot.reactions.groupcommand.IndexGroupCommand;
import ru.gnev.conciergebot.reactions.privatecommand.AboutMeCommand;
import ru.gnev.conciergebot.reactions.privatecommand.AddressCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetBottomNeighborCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetNeighborNearCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetSameFloorNeighborCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetSameSectionNeighborCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetUpperNeighborCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetUsersByFlatCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetUsersByFloorCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetUsersByFloorNSectionCommand;
import ru.gnev.conciergebot.reactions.privatecommand.GetUsersBySectionCommand;
import ru.gnev.conciergebot.reactions.privatecommand.HelpCommandCommand;
import ru.gnev.conciergebot.reactions.privatecommand.IndexCommand;
import ru.gnev.conciergebot.service.DeleteService;
import ru.gnev.conciergebot.service.IRegistrationService;
import ru.gnev.conciergebot.utils.CallbackQueryDataHelper;
import ru.gnev.conciergebot.utils.FlatHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Component
@Setter
@Getter
public class ReactionRegistry {
    private final Collection<IReact> reactions = new HashSet<>();
    private final Map<Class, IReact> reactionsMap = new HashMap<>();
    private final Collection<IReact> commands = new HashSet<>();

    @Autowired
    public ReactionRegistry(UserRepository repository,
                            BotConfiguration configuration,
                            IRegistrationService registrationService,
                            DeleteService deleteService,
                            FlatHelper flatHelper,
                            UserRegistrationAnswerRepository userRegistrationAnswerRepository,
                            CallbackQueryDataHelper callbackQueryDataHelper) {
        commands.add(new AboutMeCommand(repository));
        commands.add(new AddressCommand());
        commands.add(new GetBottomNeighborCommand(repository));
        commands.add(new GetNeighborNearCommand(repository));
        commands.add(new GetSameFloorNeighborCommand(repository));
        commands.add(new GetSameSectionNeighborCommand(repository));
        commands.add(new GetUpperNeighborCommand(repository));
        commands.add(new GetUsersByFloorNSectionCommand(repository));
        commands.add(new GetUsersByFloorCommand(repository));
        commands.add(new GetUsersBySectionCommand(repository));
        commands.add(new HelpCommandCommand());
        commands.add(new IndexCommand());
        commands.add(new BotRecievePhotoReaction(configuration));
        commands.add(new GetUsersByFlatCommand(repository));

        commands.add(new AddressGroupCommand(registrationService));
        commands.add(new IndexGroupCommand(registrationService));
        commands.add(new GetUsersByFloorGroupCommand(repository, registrationService));
        commands.add(new GetUsersBySectionGroupCommand(repository, registrationService));
        commands.add(new GetUsersByFloorNSectionGroupCommand(repository, registrationService));
        commands.add(new HelpCommandGroupCommand(registrationService));
        commands.add(new GetUsersByFlatGroupCommand(repository, registrationService));

        reactionsMap.put(BotAddedToChatReaction.class, new BotAddedToChatReaction(configuration, registrationService, repository));
        reactions.add(reactionsMap.get(BotAddedToChatReaction.class));

        reactionsMap.put(UserRegistrationWKeyboardReaction.class, new UserRegistrationWKeyboardReaction(registrationService,
                flatHelper,
                repository,
                userRegistrationAnswerRepository,
                callbackQueryDataHelper));
        reactions.add(reactionsMap.get(UserRegistrationWKeyboardReaction.class));

        reactionsMap.put(BotRecievePhotoReaction.class, new BotRecievePhotoReaction(configuration));
        reactions.add(reactionsMap.get(BotRecievePhotoReaction.class));

        reactionsMap.put(UpdateUserInfoReaction.class, new UpdateUserInfoReaction(repository));
        reactions.add(reactionsMap.get(UpdateUserInfoReaction.class));

        reactionsMap.put(UserRemovedFromChatReaction.class, new UserRemovedFromChatReaction(deleteService, repository));
        reactions.add(reactionsMap.get(UserRemovedFromChatReaction.class));
    }

    public Optional<IReact> getReaction(Class clz) {
        final IReact iReact = reactionsMap.get(clz);
        if (iReact == null) return Optional.empty();
        return Optional.of(iReact);
    }
}
