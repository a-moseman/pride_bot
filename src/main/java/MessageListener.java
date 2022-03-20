import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class MessageListener extends ListenerAdapter { // TODO: rename
    private String commandIndicator = "p>";
    private Bot bot;
    private JDA jda;

    private long lastSaveTime = 0;

    public MessageListener(String token) {
        try {
            jda = JDABuilder.createDefault(token)
                    .build();
            jda.addEventListener(this);
        }
        catch (LoginException e) {
            e.printStackTrace();
        }
        this.bot = new Bot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // determine pride dms
        // TODO: attempt to optimize
        Guild guild = event.getGuild();
        ArrayList<String> prideDms = new ArrayList<>();
        for (Member member : guild.getMembersWithRoles(guild.getRolesByName("pride_dm", false))) {
            prideDms.add(member.getId());
        }

        if (!event.getAuthor().isBot()) { // filter out bot commands
            MessageChannel channel = event.getChannel();
            String authId = event.getAuthor().getId();
            String message = event.getMessage().getContentRaw();

            if (!bot.knowsPlayer(authId)) {
                bot.addPlayer(authId, new Player(authId, event.getAuthor().getName()));
            }
            else if (!bot.getPlayer(authId).getName().equals(event.getAuthor().getName())){
                bot.getPlayer(authId).setName(event.getAuthor().getName());
            }

            if (message.length() > 1) {
                if (("" + message.charAt(0) + message.charAt(1)).equals(commandIndicator)) {
                    String[] command = message.substring(2).split(" ");
                    String response = bot.doCommand(command, authId, prideDms.contains(authId));
                    channel.sendMessage(response).queue();
                }
            }
        }

        // save every hour on message
        // TODO: make async
        if (System.nanoTime() - lastSaveTime >= 3600000000000l) { // if at least been an hour since last save
            bot.save();
            lastSaveTime = System.nanoTime();
        }
    }
}
