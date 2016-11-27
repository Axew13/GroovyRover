package uk.michael.selfbot

import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.MessageHistory
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.entities.impl.*
import net.dv8tion.jda.core.managers.*
import net.dv8tion.jda.core.events.*
import net.dv8tion.jda.core.events.message.*
import net.dv8tion.jda.core.events.user.*
import groovy.json.*



JDABuilder builder=new JDABuilder(AccountType.CLIENT)
builder.setToken(new File("token").readLines()[2])
builder.setBulkDeleteSplittingEnabled(false)
builder.setAudioEnabled(false)
builder.addListener(new SelfBot())
builder.buildBlocking()



class Bot{
	List prefixes=["self.","+ "]
	String id="107894146617868288"
	List commands=[
		new EvalCommand(),new PingasCommand(),new SuicideCommand(),new CleanCommand(),new ChairoCommand(),
		new LennyCommand(),new LeneyesCommand(),new WhatisCommand(),new RandoMoteCommand(),new StatsCommand()
	]
}



class Command{
	Command(){}
	List aliases=[]
	String toString(){aliases[0]}
}



class Mod{
	List ignored=[]
	List nsfw=[]
	List spam=[]
	String log=[]
	String mute=[]
	int lines=25
	long quota=3000
	long timeout=30000
	long cache=180000
	List pornsites=['pornhub.com','rule34.xxx','rule34.paheal.net','gelbooru.com','e621.net']
}



class JSON{
	JSON(){}
	String root="data/"
	Closure load={String donkey->new JsonSlurper().parse(new File(root+donkey),"UTF-8")}
	Closure save={Map diddy,String dixie->new File(root+dixie).write(JsonOutput.prettyPrint(JsonOutput.toJson(diddy)))}
	Closure database={load("database.json")}
}



class SelfBot extends ListenerAdapter{
	Bot bot=new Bot()
	JSON json=new JSON()
	Map db=json.database()
	Map mods=[
		"145904657833787392":new Mod([
			ignored:["145907300173873152","171815384360419328","231403714747826176"],
			nsfw:["193478510390542337"],
			spam:["145907195689566209"],
			log:"231403714747826176",
			mute:"180469475211083776"
		]),
		"165499535630663680":new Mod([
			ignored:["177479480699387905","240209353951543297"],
			nsfw:["177154046342332416"],
			spam:["183934447526084609"],
			log:"240209353951543297",
			mute:"185725838015201281"
		])
	]
	Map recent=[:]
	
	
	
	// Methods
	SelfBot(){
		new GroovyShell().evaluate(new File("../Libraries/MichaelsUtil.groovy"))
		new GroovyShell().evaluate(new File("methodsbeta.groovy"))
		User.metaClass.getIdentity={db[delegate.id]?db[delegate.id]["name"]:delegate.name}
		User.metaClass.getRawIdentity={db[delegate.id]?db[delegate.id]["name"]:null}
	}
	
	
	
	// Message Received Event
	void onMessageReceived(MessageReceivedEvent e){
		if(!e.author.bot){
			if(e.author.id==bot.id){
				Object args=e.message.rawContent
				String prefix=args.startsWithAny(bot.prefixes)
				if(prefix!=null){
					Closure command={List aliases->(args.toLowerCase()+" ").startsWithAny(aliases*.plus(' '))?.trim()}
					args=args.substring(prefix.size())
					Thread.start{
						
						Command cmd=bot.commands.find{command(it.aliases)}
						if(cmd){
							args=args.substring(command(cmd.aliases).length()).trim()
							Map binding=[bot:bot,json:json,prefix:prefix,args:args,db:db,mods:mods,recent:recent]
							cmd.run(binding,e)
						}
						
					}
				}
			}else if(e.guild){
				if(e.guild.id in mods*.key){
					Mod mod=mods[e.guild.id]
					if(!(e.channel.id in mod.ignored)){
						Thread.start{
							
							if(!recent[e.guild.id])recent[e.guild.id]=[:]
							if(!(e.channel.id in mod.spam)&&((e.message.content.count('\n')+e.message.content.count('\r'))>mod.lines)){
								e.message.delete().queue()
								e.guild.channels.find{it.id==mod.log}.sendMessage(":wastebasket: Deleted a message because it flooded the channel.").queue()
							}else if(!(e.channel.id in mod.spam)&&(e.message.content in recent[e.guild.id][e.author.id])){
								e.message.delete().queue()
								e.guild.channels.find{it.id==mod.log}.sendMessage(":wastebasket: Deleted a message because it was too similar to another message.").queue()
							}else if(!(e.channel.id in mod.nsfw)&&e.message.content.containsAny(mod.pornsites)){
								e.message.delete().queue()
								e.guild.channels.find{it.id==mod.log}.sendMessage(":wastebasket: Deleted a message because it contained pornongraphic content.").queue()
							}else if(recent[e.guild.id][e.author.id]?.sum()?.length()>mod.quota){
								Role role=e.guild.roles.find{it.id==mod.mute}
								Channel log=e.guild.channels.find{it.id==mod.log}
								e.guild.controller.addRolesToMember(e.guild.membersMap[e.author.id],[role]).queue()
								log.sendMessage(":timer: Muted a user because they exceeded their message limit.").queue()
								Thread.sleep(mod.timeout)
								e.guild.controller.removeRolesFromMember(e.guild.membersMap[e.author.id],[role]).queue()
								log.sendMessage(":timer: Unmuted a user because their time is up.").queue()
							}else{
								if(!recent[e.guild.id][e.author.id])recent[e.guild.id][e.author.id]=[]
								String content=e.message.content.toLowerCase().replaceAll(/\s+/,'')
								recent[e.guild.id][e.author.id]+=content
								Thread.sleep(mod.cache)
								recent[e.guild.id][e.author.id]-=content
							}
							
						}
					}
				}
			}
		}
	}
	
	
	
	// Status Changed Event
	void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent e){
		if(!e.user.bot){
			if(e.guild.id=="145904657833787392"){
				if(e.user.id=="107939670616203264"){
					if(e.guild.membersMap[e.user.id].onlineStatus==OnlineStatus.OFFLINE){
						e.guild.controller.removeRolesFromMember(e.guild.membersMap[bot.id],[e.guild.roles.find{it.id=="145905839650111488"}]).queue()
					}else{
						e.guild.controller.addRolesToMember(e.guild.membersMap[bot.id],[e.guild.roles.find{it.id=="145905839650111488"}]).queue()
					}
				}
			}
		}
	}
}



class EvalCommand extends Command{
	List aliases=['eval']
	void run(Map d,Event e){
		try{
			Binding binding=new Binding(d+[e:e])
			long startTime=System.currentTimeMillis()
			String eval=new GroovyShell(binding).evaluate(d.args).toString()
			long stopTime=System.currentTimeMillis()
			long startTime2=System.currentTimeMillis()
			e.message.edit(eval).queue()
			long stopTime2=System.currentTimeMillis()
			long elapsedTime=stopTime-startTime
			long elapsedTime2=stopTime2-startTime2
			e.message.edit("$eval\n`${elapsedTime}ms`, `${elapsedTime2}ms`").queue()
		}catch(ex){
			e.message.edit(ex.message).queue()
			ex.printStackTrace()
		}
	}
}


class PingasCommand extends Command{
	List aliases=['pingas']
	void run(Map d,Event e){
		e.message.edit("Pongas!").queue()
	}
}


class SuicideCommand extends Command{
	List aliases=['kms','suicide']
	void run(Map d,Event e){
		["five","four","three","two","one"].each{
			e.message.edit("$d.args:gun: :$it:").queue()
			Thread.sleep(1500)
		}
		e.message.edit(":boom::gun: :zero:").queue()
	}
}


class CleanCommand extends Command{
	List aliases=['clean']
	void run(Map d,Event e){
		e.message.edit("$e.guild.name is cancer.").queue()
		int deep=30
		if(d.args==~/\d+/)deep=d.args.toInteger()
		e.channel.history.retrievePast(deep).findAll{it.author.bot}*.delete()*.queue()
	}
}


class ChairoCommand extends Command{
	List aliases=['chairo','hiro']
	void run(Map d,Event e){
		e.message.edit("$d.args (\uff65\u03c9\uff65)").queue()
	}
}


class LennyCommand extends Command{
	List aliases=['lenny','snipars']
	void run(Map d,Event e){
		e.message.edit("$d.args ( \u0361\u00b0 \u035c\u0296 \u0361\u00b0)").queue()
	}
}


class LeneyesCommand extends Command{
	List aliases=['leneyes','lenneyes']
	void run(Map d,Event e){
		e.message.edit("$d.args ( \u0361\ud83d\udc41 \u035c\u0296 \u0361\ud83d\udc41 )").queue()
	}
}


class WhatisCommand extends Command{
	List aliases=['whatis','emote']
	void run(Map d,Event e){
		if(e.message.emotes){
			Guild guild=e.message.emotes[0].guild
			e.message.edit("$d.args is from $guild.name ($guild.id)").queue()
		}else{
			e.message.edit("**B-baka!** You need to specify an emote.").queue()
		}
	}
}


class RandoMoteCommand extends Command{
	List aliases=['randomote','randemote']
	void run(Map d,Event e){
		List emotes=[]
		int times=1
		if(d.args==~/\d+/)times=d.args.toInteger()
		times.times{
			emotes+=e.jda.emotes.findAll{it.managed||(it.guild.id==e.guild?.id)}.randomItem().asMention
		}
		e.message.edit(emotes.join()).queue()
	}
}


class StatsCommand extends Command{
	List aliases=['stats','quota']
	void run(Map d,Event e){
		if(e.guild){
			if(e.guild.id in d.mods*.key){
				if(d.args){
					User user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser
					if(user){
						e.message.edit("$user.identity: `${d.recent[e.guild.id][user.id]?.sum()?.length()?:0}/${d.mods[e.guild.id].quota}`").queue()
					}else{
						e.message.edit("**B-baka!** I couldn't find a user like that.").queue()
					}
				}else{
					List list=[]
					d.recent[e.guild.id].findAll{it.value}.each{def data->
						list+="${e.guild.members*.user.find{it.id==data.key}?.identity?:data.key}: `${data.value?.sum()?.length()}/${d.mods[e.guild.id].quota}`"
					}
					if(list){
						e.message.edit(list.join(', ')).queue()
					}else{
						e.message.edit("I didn't find anything cached.").queue()
					}
				}
			}else{
				e.message.edit("**B-baka!** There's a time and place for everything, but not in unmoderated servers.").queue()
			}
		}else{
			e.message.edit("**B-baka!** There's a time and place for everything, but not in DM.").queue()
		}
	}
}