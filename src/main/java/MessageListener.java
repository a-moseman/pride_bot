import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class MessageListener extends ListenerAdapter { // TODO: rename
    private String commandIndicator = "p>"; // TODO: Add command to change
    private final Bot BOT;

    private long lastSaveTime = 0;

    public MessageListener(String token) {
        try {
            JDA jda = JDABuilder.createDefault(token)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.addEventListener(this);
        }
        catch (LoginException e) {
            e.printStackTrace();
        }
        this.BOT = new Bot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // determine pride dms
        // TODO: attempt to optimize
        // TODO: BUG, the code for determining roles does not work
        /*
        Guild guild = event.getGuild();
        ArrayList<String> prideDms = new ArrayList<>();
        for (Member member : guild.getMembersWithRoles(guild.getRolesByName("pride_dm", false))) {
            prideDms.add(member.getId());
        }
        */


        if (!event.getAuthor().isBot()) { // filter out bot commands
            MessageChannel channel = event.getChannel();
            String authId = event.getAuthor().getId();
            String message = event.getMessage().getContentRaw();

            // TODO: test to see if checking for pride_dm works
            Guild guild = event.getGuild();
            Member member = guild.getMember(event.getAuthor());
            boolean isPrideDM = false;
            for (Role role : member.getRoles()) {
                if (role.getName().equals("pride_dm")) {
                    isPrideDM = true;
                }
            }

            if (!BOT.knowsPlayer(authId)) {
                BOT.addPlayer(authId, new Player(authId, event.getAuthor().getName()));
            }
            else if (!BOT.getPlayer(authId).getName().equals(event.getAuthor().getName())){
                BOT.getPlayer(authId).setName(event.getAuthor().getName());
            }

            if (message.length() > 1) {
                if (("" + message.charAt(0) + message.charAt(1)).equals(commandIndicator)) {
                    String[] command = message.substring(2).split(" ");
                    String response = BOT.doCommand(command, authId, isPrideDM);
                    channel.sendMessage(response).queue();
                }
            }
        }

        // save every hour on message
        // TODO: make async
        if (System.nanoTime() - lastSaveTime >= 3600000000000L) { // if at least been an hour since last save
            BOT.save();
            lastSaveTime = System.nanoTime();
        }
    }
}
