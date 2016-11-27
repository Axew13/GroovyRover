import net.dv8tion.jda.JDA
import net.dv8tion.jda.entities.*
import net.dv8tion.jda.entities.impl.*
import net.dv8tion.jda.managers.*
import net.dv8tion.jda.events.*
import net.dv8tion.jda.events.message.*
import net.dv8tion.jda.events.guild.member.*
import net.dv8tion.jda.utils.AvatarUtil
import net.dv8tion.jda.audio.*
import java.net.URL
@Grab(group='org.jsoup', module='jsoup', version='1.8.3')
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import java.time.OffsetDateTime

String id="119184325219581952"
String owner="107894146617868288"



JDA.metaClass.play={String game->delegate.accountManager.setGame(game)}
JDA.metaClass.setAvatar={String avatar->delegate.accountManager.avatar=AvatarUtil.getAvatar(new File(avatar))}
JDA.metaClass.getEmotes={delegate.guilds*.emotes.sum()}
JDA.metaClass.getChannels={delegate.channelMap*.value}

Event.metaClass.getJda={delegate.JDA}

MessageReceivedEvent.metaClass.sendMessage={String content->delegate.channel.sendMessage("\u200b"+content?.replaceAll(['@everyone','@here'],['@\u0435veryone','@h\u0435re']))}
MessageReceivedEvent.metaClass.startTyping={Thread.start{delegate.channel.sendTyping()}}
MessageReceivedEvent.metaClass.sendFile={File file->delegate.channel.sendFile(file,null)}

GenericGuildMemberEvent.metaClass.sendMessage={String content->delegate.guild.defaultChannel.sendMessage("\u200b"+content?.replaceAll(['@everyone','@here'],['@\u0435veryone','@h\u0435re']))}

Message.metaClass.getAttachment={delegate.attachments[0]}
Message.metaClass.edit={String content->delegate.updateMessage(content)}
Message.metaClass.delete={delegate.deleteMessage()}
Message.metaClass.getGuild={delegate.channel?.guild}
Message.metaClass.getMentions={delegate.mentionedUsers}
Message.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
Message.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
Message.metaClass.isTts={delegate.TTS}

Message.Attachment.metaClass.getName={delegate.fileName}
Message.Attachment.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
Message.Attachment.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
Message.Attachment.metaClass.download={String laura->new File(laura)<<delegate.url.toURL().newInputStream(requestProperties:["User-Agent":"groovy/2.4.3 DiscordBot (https://github.com/DV8FromTheWorld/JDA, 2.2.1)",Accept:"*/*"])}

User.metaClass.getStatus={delegate.onlineStatus.toString().toLowerCase()}
User.metaClass.getName={delegate.username}
User.metaClass.getAvatar={delegate.avatarUrl}
User.metaClass.getMention={delegate.asMention}
User.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
User.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
User.metaClass.isMember={Guild guild->
	guild.userRoles[delegate]||(delegate==guild.owner)||(delegate.id==owner)
}
User.metaClass.isStaff={Guild guild->
	("Trainer"in guild.userRoles[delegate]*.name)||guild.userRoles[delegate].any{"MESSAGE_MANAGE"in it.permissions*.toString()}||(delegate==guild.owner)||(delegate.id==owner)
}
User.metaClass.isOwner={Guild guild->
	("Bot Commander"in guild.userRoles[delegate]*.name)||guild.userRoles[delegate].any{"ADMINISTRATOR"in it.permissions*.toString()}||(delegate==guild.owner)||(delegate.id==owner)
}

Channel.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
Channel.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
Channel.metaClass.edit={Map data->
	if(data.name!=null)delegate.manager.setName(data.name)
	if(data.topic!=null)delegate.manager.setName(data.topic)
	if(data.bitrate!=null)delegate.manager.setName(data.bitrate)
	if(data.userLimit!=null)delegate.manager.setUserLimit(data.userLimit)
}

TextChannel.metaClass.getLogs={Number amount->
	delegate.history.retrieve(amount)
}

VoiceChannel.metaClass.join={delegate.server.audioManager.openAudioConnection(delegate)}
VoiceChannel.metaClass.leave={delegate.server.audioManager.closeAudioConnection()}

PrivateChannel.metaClass.isSpam={true}
PrivateChannel.metaClass.isLog={false}
PrivateChannel.metaClass.isNsfw={true}
PrivateChannel.metaClass.isSong={true}
PrivateChannel.metaClass.isIgnored={false}
PrivateChannel.metaClass.getGuild={null}
PrivateChannel.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
PrivateChannel.metaClass.getCreateTime={new Date(((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long)}

Role.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
Role.metaClass.getCreateTime={new Date(((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long)}
Role.metaClass.edit={Map data->
	  if(data.name!=null)delegate.manager.setName(data.name)
	  if(data.color!=null)delegate.manager.setColor(data.color)
	  if(data.mentionable!=null)delegate.manager.setMentionable(data.mentionable)
	  delegate.manager.update()
	  delegate.manager.role
}
Role.metaClass.isColour={(delegate.name==~/#?[A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9]/)&&!delegate.name.toLowerCase().containsAny('g'..'z')}
Role.metaClass.isConfig={delegate.name in["Bot Commander","Assist Owner","Trainer","Literally Hitler","spoo.py colorful","Spectra","spoo.py admin","spoo.py mod","Nadeko","Baka","regall commander","Ender","KOSMOS","Discoid Admin","Watchr Commander","Discone Admin","Living Meme","Bot Pony Commander","Beemo Music"]}

Emote.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
Emote.metaClass.getCreateTime={new Date(((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long)}
Emote.metaClass.getManaged={false}

Guild.metaClass.getChannels={delegate.textChannels+delegate.voiceChannels}
Guild.metaClass.getIcon={delegate.iconUrl}
Guild.metaClass.getDefaultChannel={delegate.publicChannel}
Guild.metaClass.getDefaultRole={delegate.publicRole}
Guild.metaClass.getAfkChannel={delegate.voiceChannels.find{it.id==delegate.afkChannelId}}
Guild.metaClass.getCreateTimeMillis={((Long.parseLong(delegate.id)>>22)+(1420070400000as long))as long}
Guild.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
Guild.metaClass.getMembers={delegate.users}
Guild.metaClass.edit={Map data->
	  if(data.name!=null)delegate.manager.setName(data.name)
	  if(data.icon!=null)delegate.manager.setIcon(data.icon)
	  if(data.verificationLevel!=null)delegate.manager.setVerificationLevel(data.verificationLevel)
	  if(data.afkChannel!=null)delegate.manager.setAfkChannel(data.afkChannel)
	  if(data.afkTimeout!=null)delegate.manager.setAfkTimeout(data.afkTimeout)
}

SelfInfo.metaClass.getGame={delegate.currentGame}



JDA.metaClass.findGuild={String args->delegate.guilds.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
JDA.metaClass.findUser={String args->delegate.users.toList().sort{it.identity.length()}.find{[it.name.toLowerCase(),it.identity.toLowerCase(),it.identity.abbreviate().toLowerCase()].findAll{it}.any{it.contains(args.toLowerCase())}}}
Guild.metaClass.findUser={String args->delegate.users.toList().sort{it.identity.length()}.find{[it.name.toLowerCase(),delegate.nickMap[it]?.toLowerCase(),it.identity.toLowerCase(),it.identity.abbreviate().toLowerCase()].findAll{it}.any{it.contains(args.toLowerCase())}}}
JDA.metaClass.findChannel={String args->delegate.channels.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
Guild.metaClass.findChannel={String args->delegate.channels.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
JDA.metaClass.findTextChannel={String args->delegate.textChannels.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
Guild.metaClass.findTextChannel={String args->delegate.textChannels.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
JDA.metaClass.findVoiceChannel={String args->delegate.voiceChannels.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
Guild.metaClass.findVoiceChannel={String args->delegate.voiceChannels.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
JDA.metaClass.findPrivateChannel={String args->delegate.privateChannels.toList().sort{it.user.name.length()}.find{[it.user.name.toLowerCase(),it.user.identity.toLowerCase()].any{it.contains(args.toLowerCase())}}}
JDA.metaClass.findEmote={String args->delegate.emotes.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
Guild.metaClass.findEmote={String args->delegate.emotes.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}
Guild.metaClass.findRole={String args->delegate.roles.toList().sort{it.name.length()}.find{[it.name.toLowerCase()].any{it.contains(args.toLowerCase())}}}



String.metaClass.cut={int at->
  String ass=delegate
  if(ass.length>at)ass=ass.substring(0,at-1)+"\u2026"
  ass
}
String.metaClass.rawBirthday={delegate.replaceAll(/\b(\d+)(?:th|st|nd|rd)\b/){all,number->number}}
String.metaClass.formatBirthday={delegate.replace('11 ','11th ').replace('12 ','12th ').replace('13 ','13th ').replace('1 ','1st ').replace('2 ','2nd ').replace('3 ','3rd ').replace('4 ','4th ').replace('5 ','5th ').replace('6 ','6th ').replace('7 ','7th ').replace('8 ','8th ').replace('9 ','9th ').replace('0 ','0th ')}
OffsetDateTime.metaClass.toDate={Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSXXX",delegate.toString())}
String.metaClass.abbreviate={delegate.replaceAll(['\'','"','(',')','[',']','{','}','|','.'],'').split(/(-|_| )/)*.capitalize().join().findAll{it.isUpperCase()}.join()}
String.metaClass.formatTime={
	  String taimu=delegate.toLowerCase().replaceAll(['.','/',':','-'],'')
	  int seconds=(taimu.findAll(/(\d+)s/){full,num->num.toInteger()})[0]?:0
	  int minutes=(taimu.findAll(/(\d+)m/){full,num->num.toInteger()})[0]?:0
	  int hours=(taimu.findAll(/(\d+)h/){full,num->num.toInteger()})[0]?:0
	  int days=(taimu.findAll(/(\d+)d/){full,num->num.toInteger()})[0]?:0
	  int weeks=(taimu.findAll(/(\d+)w/){full,num->num.toInteger()})[0]?:0
	  int months=(taimu.findAll(/(\d+)n/){full,num->num.toInteger()})[0]?:0
	  int years=(taimu.findAll(/(\d+)y/){full,num->num.toInteger()})[0]?:0
	  long time=System.currentTimeMillis()
	  if(seconds)for(sec in 0..seconds)time+=1000
	  minutes.times{time+=60000}
	  hours.times{time+=3600000}
	  days.times{time+=86400000}
	  weeks.times{time+=604800000}
	  months.times{time+=2592000000}
	  years.times{time+=31536000000}
	  time
}



Closure addVariables={String group,String data,String args,Event e->
	Object returned
	if(group.startsWith('random;')){
		returned=group.substring(7).tokenize(';').randomItem()
	}else if(group.startsWith('args;')){
		returned=group.substring(5).tokenize(';')
		try{
			returned=args.tokenize()[returned[0].toInteger()..returned[-1].toInteger()].join(' ')
		}catch(ex){
			returned=args
		}
	}else if(group.startsWith('range;')){
		try{
			returned=group.substring(6).tokenize(';')*.toBigDecimal()
		}catch(ex){
			returned=group.substring(6).tokenize(';')
		}
		returned=(returned[0]..returned[-1]).toList().randomItem()
	}else if(group.startsWith('urlify;')){
		returned=URLEncoder.encode(group.substring(7),"UTF-8")
	}else if(group.startsWith('nameof;')){
		User ass=e.jda.users.find{it.id==group.substring(7).replaceAll(/\D+/,'')}
		if(!ass)ass=e.jda.users.find{it.identity.toLowerCase()==group.substring(7).toLowerCase()}
		returned=ass?ass.name:"???"
	}else if(group.startsWith('idof;')){
		User ass=e.jda.users.find{it.identity.toLowerCase()==group.substring(5).toLowerCase()}
		if(!ass)ass=e.jda.users.find{it.name.toLowerCase()==group.substring(5).toLowerCase()}
		returned=ass?ass.id:"???"
	}else if(group.startsWith('dbof;')){
		User ass=e.jda.users.find{it.id==group.substring(7).replaceAll(/\D+/,'')}
		if(!ass)ass=e.jda.users.find{it.name.toLowerCase()==group.substring(7).toLowerCase()}
		returned=ass?ass.identity:"???"
	}else if(group.startsWith('avatarof;')){
		User ass=e.jda.users.find{it.id==group.substring(9).replaceAll(/\D+/,'')}
		if(!ass)ass=e.jda.users.find{it.name.toLowerCase()==group.substring(9).toLowerCase()}
		if(!ass)ass=e.jda.users.find{it.identity.toLowerCase()==group.substring(9).toLowerCase()}
		returned=ass?ass.avatar:"???"
	}
	if(returned)data=data.replace("{$group}",returned.toString())
	data
}
String.metaClass.addVariables={MessageReceivedEvent e,String args->
	String data=delegate.replace('{args}',args)
	if(data.containsAll(['{','}'])){
		User user
		if(e.guild){
			user=e.guild.users.toList().randomItem()
			Channel channel=e.guild.channels.toList().randomItem()
			data=data.replaceAll(['{channel}','{channelname}'],e.channel.name).replaceAll(['{server}','{servername}'],e.guild.name).replace('{serverid}',e.guild.id).replace('{servericon}',e.guild.icon).replaceAll(['{serverchannel}','{serverchannelname}'],channel.name).replace('{serverchannelid}',channel.id)
		}else{
			user=[e.author,e.jda.selfInfo].randomItem()
			PrivateChannel channel=e.jda.privateChannels.toList().randomItem()
			data=data.replaceAll(['{channel}','{channelname}'],e.channel.user.name).replaceAll(['{server}','{servername}'],"Direct Messages").replace('{serverid}',id).replace('{servericon}',e.jda.selfInfo.avatar).replaceAll(['{serverchannel}','{serverchannelname}'],channel.user.name).replace('{serverchannelid}',channel.user.id)
		}
		data=data.replaceAll(['{author}','{authordb}'],e.author.identity).replace('{authorname}',e.author.name).replace('{authorid}',e.author.id).replace('{authoravatar}',e.author.avatar?:e.author.defaultAvatarUrl).replace('{channelid}',e.channel.id).replaceAll(['{serveruser}','{serveruserdb}'],user.identity).replace('{serverusername}',user.name).replace('{serveruserid}',user.id).replace('{serveruseravatar}',user.avatar?:user.defaultAvatarUrl)
		if(data.containsAll(['{','}'])){
			List groups=data.range('{','}').split(/\}([^\{.+\}]+)?\{/)
			groups.each{
				data=addVariables(it,data,args,e)
			}
		}
	}
	data
}
String.metaClass.addVariables={GenericGuildMemberEvent e,String args->
	String data=delegate.replace('{args}',args)
	if(data.containsAll(['{','}'])){
		User user=e.guild.users.toList().randomItem()
		Channel channel=e.guild.channels.toList().randomItem()
		data=data.replaceAll(['{channel}','{channelname}'],e.guild.defaultChannel.name).replaceAll(['{server}','{servername}'],e.guild.name).replace('{serverid}',e.guild.id).replace('{servericon}',e.guild.icon).replaceAll(['{serverchannel}','{serverchannelname}'],channel.name).replace('{serverchannelid}',channel.id)
		data=data.replaceAll(['{author}','{authordb}'],e.user.identity).replace('{authorname}',e.user.name).replace('{authorid}',e.user.id).replace('{authoravatar}',e.user.avatar?:e.user.defaultAvatarUrl).replace('{channelid}',e.guild.defaultChannel.id).replaceAll(['{serveruser}','{serveruserdb}'],user.identity).replace('{serverusername}',user.name).replace('{serveruserid}',user.id).replace('{serveruseravatar}',user.avatar?:user.defaultAvatarUrl)
		if(data.containsAll(['{','}'])){
			List groups=data.range('{','}').split(/\}([^\{.+\}]+)?\{/)
			groups.each{
				data=addVariables(it,data,args,e)
			}
		}
	}
	data
}



String.metaClass.strip={delegate.replaceAll(['@everyone','@here'],['@\u0435veryone','@h\u0435re'])}
ArrayList.metaClass.randomItem={delegate[(Math.floor((Math.random()*delegate.size())+1)-1)as int]}
String.metaClass.addImports={"""import net.dv8tion.jda.JDA
import net.dv8tion.jda.JDABuilder
import net.dv8tion.jda.MessageHistory
import net.dv8tion.jda.OnlineStatus
import net.dv8tion.jda.core.*
import net.dv8tion.jda.hooks.ListenerAdapter
import net.dv8tion.jda.entities.*
import net.dv8tion.jda.entities.impl.*
import net.dv8tion.jda.managers.*
import net.dv8tion.jda.events.*
import net.dv8tion.jda.events.message.*
import net.dv8tion.jda.events.guild.member.*
import net.dv8tion.jda.utils.AvatarUtil
import net.dv8tion.jda.audio.*
import net.dv8tion.jda.player.*
import net.dv8tion.jda.player.source.*
import java.net.URL
import java.awt.*
import java.awt.List as AWTList
import java.util.List
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
@Grab(group='org.jsoup', module='jsoup', version='1.8.3')
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import groovy.json.*
$delegate"""}
Message.metaClass.deleteAfter={
  long ms->Thread.sleep(ms)
  delegate.delete()
}
JDA.metaClass.getLegitimateGuilds={delegate.guilds.findAll{it.users.size()/2>=it.users.count{it.bot}}}