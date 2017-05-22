package Commands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

public class MessageListenerActions extends ListenerAdapter implements PropertyChangeListener{

	private JDA jda;
	
	public MessageListenerActions(JDA jda){
		if(this.jda == null){
			this.jda = jda;
		}
	}
	
	public void addListener(){
		jda.addEventListener(new MessageListenerActions(jda));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getPropertyName().equals("VideoId")){
			jda.getGuildById("311953226851155968").getPublicChannel().sendMessage("Une nouvelle vidéo est sortie !: https://youtube.com/watch?v="+event.getNewValue()).complete();
		}
		
	}
	
	public JDA getJda(){
		return this.jda;
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		event.getGuild().getPublicChannel().sendMessage(String.format("Bienvenue à %s qui vient de nous rejoindre !", event.getMember().getEffectiveName())).complete();
	}

}
