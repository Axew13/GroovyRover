package uk.michael.dogbot

import net.dv8tion.jda.core.*
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.entities.impl.*
import net.dv8tion.jda.core.managers.*
import net.dv8tion.jda.core.events.*
import net.dv8tion.jda.core.events.message.*
import net.dv8tion.jda.core.events.message.react.*
import net.dv8tion.jda.core.events.guild.*
import net.dv8tion.jda.core.events.guild.member.*
import net.dv8tion.jda.core.events.user.*
import java.net.URL
import java.awt.*
import java.awt.List as AWTList
import java.util.List
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import com.mashape.unirest.http.Unirest
@Grab(group='org.jsoup',module='jsoup',version='1.8.3')
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import groovy.json.*



JDABuilder builder=new JDABuilder(AccountType.BOT)
builder.setToken(new File('token').readLines()[0])
builder.setBulkDeleteSplittingEnabled(false)
builder.addListener(new GRover())
builder.buildBlocking()



class Bot{
	List prefixes=['-','~']
	List mention=['<@119184325219581952> ','<@!119184325219581952> ']
	String owner='107894146617868288'
	List commands=[
		new SayCommand(),new PlayCommand(),new UserinfoCommand(),new ServerinfoCommand(),new ChannelinfoCommand(),
		new RoleinfoCommand(),new EmoteinfoCommand(),new AvatarCommand(),new InfoCommand(),new HelpCommand(),
		new JoinCommand(),new GoogleCommand(),new YouTubeCommand(),new ImageCommand(),new NsfwCommand(),
		new LevelPalaceCommand(),new AnimeCommand(),new WebsiteCommand(),new MiiverseCommand(),
		new MarioMakerCommand(),new DefineCommand(),new UrbanCommand(),new TagCommand(),new MiscCommand(),
		new TextCommand(),new ChatBoxCommand(),new IdentifyCommand(),new IrlCommand(),new AgeCommand(),
		new AreaCommand(),new AltsCommand(),new MinecraftCommand(),new TimeCommand(),new SeenCommand(),
		new EventsCommand(),new ColourCommand(),new StatsCommand(),new LoveCommand(),new BallCommand(),
		new SetAvatarCommand(),new SetPrefixCommand(),new EvalCommand(),new InspectCommand(),new WordCountCommand(),
		new MemberCommand(),new MuteCommand(),new KickCommand(),new LogCommand(),new ScopeCommand(),
		new FeedCommand(),new ClearCommand(),new SetChannelCommand(),new SetRoleCommand(),new VotePinCommand(),
		new ConfigCommand(),new SingCommand(),new BanCommand(),new SmiliesCommand(),new CloneCommand(),
		new AccessCommand(),new TrackerCommand(),new IsupCommand(),new TopCommand(),new CleanCommand(),
		new NoteCommand(),new ProfileCommand(),new CustomCommand(),new PwnedCommand(),new MathCommand(),
		new MapCommand(),new SourceCommand()
	]
	String oauth='https://discordapp.com/oauth2/authorize?client_id=170646931641466882&scope=bot&permissions=268443670'
	String server='https://discord.gg/0vJZEroWHiGWWQc7'
}



class Command{
	List aliases=[]
	boolean dev=false
	int limit=5
	Map pool=[:]
	String category='Uncategorized'
	String help="\u00af\\_(\u30c4)_/\u00af"
}



class Web{
	Map agents=[
		'G.Chrome':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36',
		'M.Firefox':'Mozilla/5.0 (Windows NT 6.1; rv:50.0) Gecko/20100101 Firefox/50.0',
		'N.3DS':'Mozilla/5.0 (Nintendo 3DS; U; ; en) Version/1.7498.US',
		'API':'Groovy/2.4.5 DiscordBot (https://github.com/DV8FromTheWorld/JDA, 3.0)'
	]
	Document get(String url,String agent='G.Chrome'){
		Jsoup.connect(url).userAgent(agents[agent]).get()
	}
	String sendStats(Event e){
		Unirest.post("https://bots.discord.pw/api/bots/$e.jda.selfUser.id/stats").header('Content-Type','application/json').header('Authorization',new File('token').readLines()[4]).body(JsonOutput.toJson(server_count:e.jda.guilds.size())).asString().body
	}
	File download(String from,String to){
		new File(to)<<from.toURL().newInputStream(requestProperties:['User-Agent':agents['API'],Accept:'*/*'])
	}
}



class JSON{
	String root='data/'
	String end='.json'
	Map load(String donkey){
		new JsonSlurper().parse(new File(root+donkey+end),'UTF-8')
	}
	void save(Map diddy,String dixie){
		new File(root+dixie+end).write(JsonOutput.prettyPrint(JsonOutput.toJson(diddy)))
	}
}



class GRover extends ListenerAdapter{
	Bot bot=new Bot()
	Web web=new Web()
	JSON json=new JSON()
	Map db=json.load('database')
	Map tags=json.load('tags')
	Map seen=json.load('lastseen')
	Map channels=json.load('channels')
	Map roles=json.load('roles')
	Map info=json.load('properties')
	Map colours=json.load('colours')
	Map misc=json.load('misc')
	Map conversative=json.load('conversative')
	Map feeds=json.load('feeds')
	Map settings=json.load('settings')
	Map temp=json.load('temp')
	Map tracker=json.load('tracker')
	Map notes=json.load('notes')
	Map customs=json.load('customs')
	String lastReply
	boolean tableTimeout
	long started=System.currentTimeMillis()
	List messages=[]
	Closure errorMessage={'**'+["Let's try that again.","Bots aren't your strong point. I can tell.","Watch how an expert does it.","You're doing it wrong.","Nah, it's more like this.","She wants to know if you're really a tech person.","Consider the following:","git: 'gud' is not a git command. See 'git --help'.","I become meguca?"].randomItem()+'**\n'}
	Closure permissionMessage={'**'+["The desire for something becomes stronger when you can't have it.","You may look, but don't touch.","What could go wrong in allowing that for everyone?","Access is denied to that.","I can't exploit your bot, therefore it sucks.","I can't let you do that, Star Fox.","There are function keys beyond F12. You are not ready for them.","You don't have a license to do that.","Are you saying I'm stupid?"].randomItem()+'**\n'}
	Closure failMessage={'**'+["JDA, why you no user-friendly?!","I have succeeded in my failure. Proud of me?","What on earth was that?","Hey, that was supposed to work. No fair.","LOSE LOSE LOSE LOSE LOSE LOSE!","I thought I fixed that.","It's java again isn't it?","Let's motivate it with a controlled shock.","ALART."].randomItem()+'**\n'}
	
	
	// Methods
	GRover(){
		new GroovyShell().evaluate(new File('../Libraries/MichaelsUtil.groovy'))
		new GroovyShell().evaluate(new File('methods.groovy'))
		User.metaClass.getIdentity={db[delegate.id]?.name?:delegate.name}
		User.metaClass.getRawIdentity={db[delegate.id]?.name}
		Channel.metaClass.isSpam={channels.spam[delegate.id]||(channels.spam[delegate.id]==null)&&delegate.name.toLowerCase().containsAny(['spam','testing','shitpost'])}
		Channel.metaClass.isLog={channels.log[delegate.id]||(channels.log[delegate.id]==null)&&delegate.name.toLowerCase().endsWithAny(['-log','logs'])}
		Channel.metaClass.isNsfw={channels.nsfw[delegate.id]||(channels.nsfw[delegate.id]==null)&&delegate.name.toLowerCase().containsAny(['nsfw','porn','hentai'])}
		Channel.metaClass.isSong={channels.song[delegate.id]||(channels.song[delegate.id]==null)&&delegate.name.toLowerCase().containsAny(['music','song'])}
		Channel.metaClass.isIgnored={channels.ignored[delegate.id]}
	}
	
	
	// Ready Event
	void onReady(ReadyEvent e){
		Thread.start{
			e.jda.guilds.findAll{!roles.member[it.id]}.each{roles.member[it.id]=it.roles.findAll{!it.managed&&!it.colour&&!it.config}.max{Role role->role.guild.members*.roles.flatten()*.id.count(role.id)}?.id}
			e.jda.guilds.findAll{!roles.mute[it.id]}.each{roles.mute[it.id]=it.roles.findAll{!it.managed&&it.name.toLowerCase().containsAny(['mute','shun','naughty','punish'])&&!it.config}.max{Role role->role.guild.members*.roles.flatten()*.id.count(role.id)}?.id}
			json.save(roles,'roles')
			while(true){
				long now=System.currentTimeMillis()
				e.jda.guilds*.members.flatten().groupBy{it.user.id}.values()*.first().findAll{it.status!='offline'}.each{
					seen[it.user.id]=[
						time:now,
						game:it.game?.name
					]
				}
				json.save(seen,'lastseen')
				List sticky=notes.timed.findAll{it.time<now}
				sticky.each{Map note->
					try{
						e.jda.users.find{it.id==note.user}.openPrivateChannel().block()
						e.jda.users.find{it.id==note.user}.privateChannel.sendMessage("It is ${new Date().format('HH:mm:ss, d MMMM YYYY').formatBirthday()}. You asked me to remind you${if(note.content){":\n\n$note.content"}else{'.'}}").queue()
					}catch(ex){
						ex.printStackTrace()
					}
					notes.timed-=note
					json.save(notes,'notes')
				}
				Thread.sleep(90000)
			}
		}
		Thread.start{
			while(true){
				if(info.game){
					e.jda.play(info.game)
					Thread.sleep(160000)
				}
				e.jda.play('levelpalace.com')
				Thread.sleep(80000)
				if(info.game){
					e.jda.play(info.game)
					Thread.sleep(160000)
				}
				e.jda.play("with ${db*.key.size()} DB entries")
				Thread.sleep(80000)
				if(info.game){
					e.jda.play(info.game)
					Thread.sleep(160000)
				}
				e.jda.play("${bot.prefixes.randomItem()}${bot.commands.findAll{!it.dev}.randomItem().aliases[0]}")
				Thread.sleep(80000)
			}
		}
		Thread.start{
			while(true){
				try{
					List channels=e.jda.textChannels+e.jda.privateChannels
					Map cache=[:]
					feeds.youtube.clone().each{Map feed->
						def channel=channels.find{it.id==feed.channel}
						if(channel){
							try{
								Document doc=cache[feed.link]?:web.get(feed.link,'G.Chrome')
								cache[feed.link]=doc
								String id=doc.getElementsByClass('yt-lockup-title')[0].getElementsByTag('a')[0].attr('href')
								if(id!=feed.last){
									String title=doc.getElementsByTag('title').text().tokenize().join(' ')
									channel.sendMessage("**New video from $title**:\nhttps://www.youtube.com$id").queue()
									feeds.youtube.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=id
								}
							}catch(bad){
								bad.printStackTrace()
								feeds.youtube.remove(feed)
								channel.sendMessage("$feed.link is not a valid YouTube channel and has been removed from the feed for this channel.\nThe channel may have been deleted or terminated.").queue()
							}
						}
					}
					feeds.animelist.clone().each{Map feed->
						def channel=channels.find{it.id==feed.channel}
						if(channel){
							try{
								Document doc=cache[feed.link]?:web.get(feed.link,'G.Chrome')
								cache[feed.link]=doc
								Element anime=doc.getElementsByTag('item')[0]
								List data=anime.getElementsByTag('description')[0].text().replace(' episodes','').split(' - ')
								String name=anime.getElementsByTag('title')[0].text().split(' - ')[0]
								String id="$name/${data[1].tokenize()[0]}"
								if(id!=feed.last){
									String title=doc.getElementsByTag('title')[0].text().tokenize()[0]
									String link=anime.getElementsByTag('link')[0].text()
									channel.sendMessage("**New episode on $title anime list**:\n${data[0]}: Episode ${data[1]} of $name.\n<$link>").queue()
									feeds.animelist.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=id
								}
							}catch(bad){
								bad.printStackTrace()
								feeds.animelist.remove(feed)
								channel.sendMessage("$feed.link is not a valid anime list and has been removed from the feed for this channel.\nThe list may have been privated or changed username.").queue()
							}
						}
					}
					feeds.twitter.clone().each{Map feed->
						def channel=channels.find{it.id==feed.channel}
						if(channel){
							try{
								Document doc=cache[feed.link]?:web.get(feed.link,'G.Chrome')
								cache[feed.link]=doc
								String link=doc.getElementsByClass('tweet-timestamp')[0].attr('href')
								String id=link.substring(link.lastIndexOf('/'))
								if(id!=feed.last){
									String title=doc.getElementsByClass('ProfileHeaderCard-nameLink').text()
									channel.sendMessage("**New tweet from $title**:\nhttps://twitter.com$link").queue()
									feeds.twitter.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=id
								}
							}catch(bad){
								bad.printStackTrace()
								feeds.twitter.remove(feed)
								channel.sendMessage("$feed.link is not a valid Twitter handle and has been removed from the feed for this channel.\nThe Twitter handle may have been privated or deleted.").queue()
							}
						}
					}
					feeds.levelpalace.clone().each{Map feed->
						def channel=channels.find{it.id==feed.channel}
						if(channel){
							try{
								Document doc=cache[feed.link]?:web.get(feed.link,'G.Chrome')
								cache[feed.link]=doc
								Elements level=doc.getElementsByClass('levels-table')[0].getElementsByTag('a')
								String id=level[0].attr('href')
								if(id!=feed.last){
									String title=level[1].text()
									String name=level[0].text()
									channel.sendMessage("**New level from $title**:\n$name.\n<https://levelpalace.com/$id>").queue()
									feeds.levelpalace.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=id
								}
							}catch(bad){
								bad.printStackTrace()
								feeds.levelpalace.remove(feed)
								channel.sendMessage("$feed.link is not a valid Level Palace account and has been removed from the feed for this channel.\nThe account may have been banned or cleared.").queue()
							}
						}
					}
					cache=[:]
					json.save(feeds,'feeds')
				}catch(ex){
					ex.printStackTrace()
				}
				Thread.sleep(1800000)
				db=json.load('database')
			}
		}
	}
	
	
	// Message Create Event
	void onMessageReceived(MessageReceivedEvent e){
		if(!(e.author.bot||e.channel.ignored)){
			def args=e.message.rawContent
			String prefix=args.startsWithAny(e.guild?(settings.prefix[e.guild.id]?:bot.prefixes)+bot.mention:bot.prefixes)
			if(prefix!=null){
				args=args.substring(prefix.size())
				Thread.start{
					
					String alias
					Command cmd=bot.commands.find{alias=(args.toLowerCase()+' ').startsWithAny(it.aliases*.plus(' '))?.trim()}
					Map binding=[bot:bot,json:json,web:web,prefix:prefix,alias:alias,args:args,db:db,tags:tags,seen:seen,channels:channels,roles:roles,info:info,colours:colours,misc:misc,conversative:conversative,feeds:feeds,settings:settings,temp:temp,tracker:tracker,notes:notes,customs:customs,lastReply:lastReply,tableTimeout:tableTimeout,started:started,messages:messages,errorMessage:errorMessage,permissionMessage:permissionMessage,failMessage:failMessage]
					if(cmd){
						int status=200
						if(!cmd.pool[e.author.id]){
							try{
								args=args.substring(alias.length()).trim()
								binding.args=args
								def response=cmd.run(binding,e)
								if(response?.class==Integer)status=response
								Thread.start{
									cmd.pool[e.author.id]=System.currentTimeMillis()
									Thread.sleep(cmd.limit*100)
									cmd.pool.remove(e.author.id)
								}
							}catch(ex){
								try{
									e.sendMessage(failMessage()+"Error: `$ex.message`").queue()
									ex.printStackTrace()
									status=500
								}catch(ex2){
									try{
										e.author.openPrivateChannel().block()
										e.author.privateChannel.sendMessage("Looks like I don't have permission to bark up this tree. Ask an administrator to let me speak in <#$e.channel.id>.").queue()
									}catch(ex3){
										// welp
									}
								}
							}
							messages+=e.message
						}else{
							e.sendMessage("You can do that again in ${(System.currentTimeMillis()-cmd.pool[e.author.id])/1000} seconds.").queue{
								Thread.sleep(9999)
								it.delete().queue()
								status=429
							}
						}
						e.jda.textChannels.find{it.id=='270998683003125760'}.sendMessage("""\u200b
<:grover:234242699211964417> `Command Log`
**Server**: ${e.guild?.name?:'Direct Messages'} (${e.guild?.id?:e.jda.selfUser.id})
**Channel**: ${e.guild?e.channel.name:e.channel.user.name} ($e.channel.id)
**User**: $e.author.identity ($e.author.id)
**Command**: ${cmd.aliases.join('/')}
**Arguments**: $args
**Status**: $status""").queue()
					}else if(e.guild){
						Map custom=customs[e.guild.id]?.find{(args.toLowerCase()+' ').startsWith(it.name.plus(' '))}
						if(custom){
							int status=200
							try{
								args=custom.args.addVariables(e,args.substring(args.tokenize()[0].length()).trim())
								binding.args=args
								cmd=bot.commands.find{custom.command in it.aliases}
								def response=cmd.run(binding,e)
								if(response?.class==Integer)status=response
								try{
									customs[e.guild.id].find{it.name==custom.name}.uses+=1
								}catch(ex){
									// lol
								}
								json.save(customs,'customs')
							}catch(ex){
								try{
									e.sendMessage(failMessage()+"Error: `$ex.message`").queue()
									ex.printStackTrace()
									status=500
								}catch(ex2){
									// welp
								}
							}
							messages+=e.message
							e.jda.textChannels.find{it.id=='270998683003125760'}.sendMessage("""\u200b
<:grover:234242699211964417> `Command Log`
**Server**: ${e.guild?.name?:'Direct Messages'} (${e.guild?.id?:e.jda.selfUser.id})
**Channel**: ${e.guild?e.channel.name:e.channel.user.name} ($e.channel.id)
**User**: $e.author.identity ($e.author.id)
**Command**: ${cmd.aliases.join('/')} (via $custom.name)
**Arguments**: $args
**Status**: $status""").queue()
						}
					}
					
				}
			}else{
				
				// DM Conversative
				if(!e.guild){
					String chat=' '+e.message.content.toLowerCase().replaceAll(['.',',','!','?','\'',':',';','(',')','"','-'],'')+' '
					if(e.message.attachment)chat+="$e.message.attachment.url "
					if(chat.contains('discordgg')){
						e.sendMessage("I can't accept this. Please use this instead:\n$bot.oauth").queue()
					}else{
						List entry=conversative.findAll{chat.contains(" $it.key ")}*.key
						if(lastReply?.length()>1){
							User client=e.jda.selfUser
							String add=e.message.content.capitalize().replaceEach([client.name,client.id,client.identity],['[name]','[id]','[identity]'])
							if(!add.endsWithAny(['.','!','?',')']))add+=['.','!','?','...'].randomItem()
							if(!conversative[lastReply])conversative[lastReply]=[]
							conversative[lastReply]+=add
							json.save(conversative,'conversative')
						}
						if(entry){
							String response=conversative[entry.randomItem()].randomItem().replaceEach(['[name]','[id]','[identity]'],[e.author.name,e.author.id,e.author.identity])
							lastReply=response.toLowerCase().replaceAll(['.',',','!','?','\'',':',';','(',')','"','-'],'').trim()
							e.sendMessage(response).queue()
						}else{
							String response=conversative*.value.randomItem().randomItem().replaceEach(['[name]','[id]','[identity]'],[e.author.name,e.author.id,e.author.identity])
							lastReply=response.toLowerCase().replaceAll(['.',',','!','?','\'',':',';','(',')','"','-'],'').trim()
							e.sendMessage(response).queue()
						}
					}
				}else if(e.channel.spam){
					Thread.start{
						if(e.message.content=='(\u256f\u00b0\u25a1\u00b0\uff09\u256f\ufe35 \u253b\u2501\u253b'){
							if(!tableTimeout){
								e.sendMessage('(\u256f\u00b0\u2302\u00b0)\u256f\ufe35 \u253b\u2501\u2501\u2501\u253b').queue()
								tableTimeout=1
								Thread.sleep(8000)
								tableTimeout=0
							}else{
								e.sendMessage('(-\u00b0 \u00b7\u00b0)-  \u252c\u2500\u2500\u252c\nHow do you think the table feels?')
							}
						}else if(e.message.content=='\u252c\u2500\u252c\ufeff \u30ce( \u309c-\u309c\u30ce)'){
							e.sendMessage('-\u252c\u2500\u2500\u252c\u256f\u30ce( o\u200b_o\u30ce)').queue()
							tableTimeout=1
							Thread.sleep(8000)
							tableTimeout=0
						}else if(e.message.content=='ayy'){
							e.sendMessage('le mayo').queue()
						}else if(e.message.content=='wew'){
							e.sendMessage('lad').queue()
						}
					}
				}
				
				// Smilies
				if(e.message.rawContent.containsAll(['(',')'])){
					String tag=e.message.rawContent.lastRange('(',')')
					if(tag==~/\w+/){
						File image=new File("images/xat/${tag}_xat.png")
						if(image.exists()&&(!e.guild||settings.smilies[e.guild.id])){
							e.sendFile(image).queue()
						}else if(e.guild){
							image=new File("images/cs/${tag}_${e.guild.id}.png")
							if(image.exists())e.sendFile(image).queue()
						}
					}
				}
				
			}
		}
/*		String log="${e.message.createTime.format('HH:mm:ss')} "
		if(e.guild)log+=("[$e.guild.name] [${e.channel.name.capitalize()}] <$e.author.identity>:\n$e.message.content")
		else log+=("[Direct Messages] [${e.channel.user.identity.capitalize()}] <$e.author.identity>:\n$e.message.content")
		if(e.message.attachment)log+="${if(e.message.content){"\n"}else{''}}[$e.message.attachment.name]"
		println(log)*/
	}
	
	
	// Message Delete Event
	void onMessageDelete(MessageDeleteEvent e){
		if(e.guild){
			Message message=messages.find{it.id==e.messageId}
			if(message){
				String address=(db[message.author.id]?.gender=='Female')?'her':'his'
				e.channel.sendMessage("**$message.author.identity** deleted $address command message.").queue()
				messages-=message
			}
		}
	}
	
	
	// User Join Event
	void onGuildMemberJoin(GuildMemberJoinEvent e){
		String message=tracker.join[e.guild.id]
		if(message)e.sendMessage(message.addVariables(e,message)).queue()
	}
	
	
	// User Leave Event
	void onGuildMemberLeave(GuildMemberLeaveEvent e){
		String message=tracker.leave[e.guild.id]
		if(message)e.sendMessage(message.addVariables(e,message)).queue()
	}
	
	
	// User Ban Event
	void onGuildBan(GuildBanEvent e){
		String message=tracker.ban[e.guild.id]
		if(message)e.sendMessage(message.addVariables(e,message)).queue()
	}
	
	
	// User Unban Event
	void onGuildUnban(GuildUnbanEvent e){
		String message=tracker.unban[e.guild.id]
		if(message)e.sendMessage(message.addVariables(e,message)).queue()
	}
	
	
	// Presence Update Event
	void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent e){
		if(!e.user.bot){
			List sticky=notes.user.findAll{it.mention==e.user.id}
			sticky.each{Map note->
				notes.user-=note
				try{
					e.jda.users.find{it.id==note.user}.openPrivateChannel().block()
					e.jda.users.find{it.id==note.user}.privateChannel.sendMessage("$e.user.identity is online. You asked me to tell you${if(note.content){":\n\n$note.content"}else{"."}}").queue()
				}catch(ex){
					ex.printStackTrace()
				}
				json.save(notes,'notes')
			}
		}
	}
	
	
	// Reaction Create Event
	void onMessageReactionAdd(MessageReactionAddEvent e){
		if(e.reaction.emote.name=='\ud83d\udccc'){
			if(!e.channel.ignored){
				Message message=e.channel.getMessageById(e.messageId).block()
				if(!message.pinned&&(!message.guild||message.guild.selfMember.roles.any{'MESSAGE_MANAGE'in it.permissions*.toString()})){
					bot.commands.find{it.aliases[0]=='votepin'}.votes[message.id]=message.reactions.find{it.emote.name=='\ud83d\udccc'}.users.block().findAll{!(it.bot||(it.id==message.author.id)||it.rawIdentity.endsWithAny(['\'s Incognito','\'s Alternate Account']))}*.id
					int max=settings.votepin[message.guild?.id]?:3
					if(bot.commands.find{it.aliases[0]=='votepin'}.votes[message.id].size()>=max)message.pin().queue()
				}
			}
		}
	}
}



class SayCommand extends Command{
	List aliases=['say']
	def run(Map d,Event e){
		if(d.args){
			e.sendMessage(d.args.addVariables(e,d.args)).queue()
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}say [text]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`say [text]` will make me repeat the text in chat.
Useful for getting someone to insult himself, but why am I telling you that?"""
}


class PlayCommand extends Command{
	List aliases=['play','setgame']
	def run(Map d,Event e){
		String old=d.info.game
		d.info.game=d.args
		if(d.args){
			e.jda.play(d.args)
			e.sendMessage("I am now playing $d.args. Check my game status!").queue()
		}else{
			e.sendMessage("I have finished playing $old.").queue()
		}
		d.json.save(d.info,'properties')
	}
	String category='General'
	String help="""`play [text]` will make me change my playing status to the text.
Don't say 'with fire' please."""
}


class UserinfoCommand extends Command{
	List aliases=['userinfo','user']
	def run(Map d,Event e){
		String area=d.db[e.author.id]?.area?:'United States'
		String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
		int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
		if(e.guild){
			if(e.message.mentions.size()>1){
				List mens=e.message.mentions
				if(mens.size()>5)mens=mens[0..4]
				List shared=e.jda.guilds.findAll{List ass=it.users*.id;mens.every{it.id in ass}}*.name
				List joined=[]
				List avatar=[]
				List created=[]
				mens.each{
					joined+=new Date(e.guild.membersMap[it.id].joinDate.toDate().time+(zone*3600000)).format('d MMMM yyyy').formatBirthday()
					avatar+=it.avatarId?:it.defaultAvatarId
					created+=new Date(it.createTimeMillis+(zone*3600000)).format('d MMMM yyyy').formatBirthday()
				}
				e.sendMessage("""**${mens*.name*.capitalize().join(', ')}** (${mens.size()}): ```css
IDs: ${mens*.id.join(', ')}
Names: ${mens*.identity.join(', ')}
Avatars: ${avatar.join(', ')}
Created: ${created.join(', ')} (${key.abbreviate()})
Joined: ${joined.join(', ')} (${key.abbreviate()})
Shared: ${if(shared.size()>9){shared[0..9].join(', ')+'..'}else{shared.join(', ')}} (${shared.size()})
${if(mens.every{!e.guild.membersMap[it.id].roles}){'Guests'}else if(mens.every{it.bot}){'Bots'}else if(mens.every{e.guild.membersMap[it.id].roles}){'Members'}else{'Multiple values'}}```""").queue()
			}else{
				User user=e.author
				if(d.args)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
				Member member=e.guild.membersMap[user?.id]
				if(user){
					List shared=e.jda.guilds.findAll{user in it.users}*.name
					e.sendMessage("""**${user.name.capitalize()}** is $member.status: ```css
ID: $user.id
Name: $user.identity
Avatar: ${user.avatar?:user.defaultAvatar}
Created: ${new Date(user.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Joined: ${new Date(e.guild.membersMap[e.author.id].joinDate.toDate().time+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Shared: ${if(shared.size()>9){shared[0..9].join(', ')+'..'}else{shared.join(', ')}} (${shared.size()})
Seen: ${d.seen[user.id]?new Date(d.seen[user.id].time+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy'):"???"} (${key.abbreviate()})
${if(user.bot){'Bot'}else if(member.owner){'Owner'}else if(member.roles){'Member'}else{'Guest'}}```""").queue()
				}else{
					e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
					404
				}
			}
		}else{
			User user=e.author
			List shared=e.jda.guilds.findAll{user in it.users}*.name
			e.sendMessage("""**${user.name.capitalize()}** is ${e.jda.guilds.find{user.id in it.users*.id}.membersMap[user.id].status}: ```css
ID: $user.id
Name: $user.identity
Avatar: ${user.avatar?:user.defaultAvatar}
Created: ${new Date(user.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Shared: ${if(shared.size()>9){shared[0..9].join(', ')+'...'}else{shared.join(', ')}} (${shared.size()})
Seen: ${d.seen[user.id]?new Date(d.seen[user.id].time+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy'):"???"} (${key.abbreviate()})```""").queue()
		}
	}
	String category='General'
	String help="""`userinfo [user]` will make me tell you some useful information about the user.
Where they live is not included this time."""
}


class ServerinfoCommand extends Command{
	List aliases=['serverinfo','server']
	def run(Map d,Event e){
		String area=d.db[e.author.id]?.area?:'United States'
		String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
		int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
		if(e.guild||d.args){
			Guild guild=e.guild
			if(d.args)guild=e.jda.findGuild(d.args)
			if(guild){
				int timeout=guild.afkTimeout.seconds/60
				e.sendMessage("""**${guild.name.capitalize()}** owned by $guild.owner.user.identity: ```css
ID: $guild.id
Icon: $guild.icon
Region: $guild.region
Opened: ${new Date(guild.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
AFK: ${guild.afkChannel?guild.afkChannel.name+', ':''}$timeout minute${if(timeout==1){''}else{'s'}}
Roles: ${if(guild.roles.size()>4){guild.roles[0..4]*.name.join(', ')+'..'}else{guild.roles*.name.join(', ')}} (${guild.roles.size()})
Users: ${if(guild.users.size()>4){guild.users[0..4]*.identity.join(', ')+'..'}else{guild.users*.identity.join(', ')}} (${guild.users.size()})
Emotes: ${if(guild.emotes.size()>4){guild.emotes[0..4]*.name.join(', ')+'..'}else{guild.emotes*.name.join(', ')}} (${guild.emotes.size()})
Channels: ${if(guild.textChannels.size()>1){guild.textChannels[0..1]*.name.join(', ')+'..'}else{guild.textChannels*.name.join(', ')}}${if(guild.voiceChannels){", ${if(guild.voiceChannels.size()>1){guild.voiceChannels[0..1]*.name.join(', ')+'..'}else{guild.voiceChannels*.name.join(', ')}}"}else{''}} (${guild.textChannels.size()}, ${guild.voiceChannels.size()})
${if(guild.users.size()>249){'Large'}else{'Small'}}```""").queue()
			}else{
				e.sendMessage("I couldn't find a server matching '$d.args.'").queue()
				404
			}
		}else{
			User user=e.jda.selfUser
			e.sendMessage("""**Direct Messages** owned by $user.identity: ```css
ID: $user.id
Icon: $user.avatar
Region: London
Opened: ${new Date(user.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Users: $e.jda.selfUser.identity, ${if(e.jda.privateChannels.size()>3){e.jda.privateChannels[0..3]*.user*.identity.join(', ')+'..'}else{e.jda.privateChannels*.user*.identity.join(', ')}} (${e.jda.privateChannels.size()+1})
Channels: ${if(e.jda.privateChannels.size()>1){e.jda.privateChannels[0..1]*.user*.identity.join(', ')+'..'}else{e.jda.privateChannels*.user*.identity.join(', ')}} (${e.jda.privateChannels.size()}, 0)
${if(e.jda.privateChannels.size()>249){'Large'}else{'Small'}}```""").queue()
		}
	}
	String category='General'
	String help="""`serverinfo [server]` will make me tell you some useful information about the server.
How may I server you, master?"""
}


class ChannelinfoCommand extends Command{
	List aliases=['channelinfo','channel']
	def run(Map d,Event e){
		String area=d.db[e.author.id]?.area?:'United States'
		String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
		int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
		def channel=e.channel
		if(d.args&&e.guild)channel=e.message.mentionedChannels?e.message.mentionedChannels[-1]:e.guild.findChannel(d.args)
		if(channel){
			if(channel.guild){
				List props=[]
				if(channel.spam)props+='Spam'
				if(channel.log)props+='Log'
				if(channel.nsfw)props+='NSFW'
				if(channel.song)props+='Song'
				if(channel.ignored)props+='Ignored'
				e.sendMessage("""**${channel.name.capitalize()}** in $channel.guild.name: ```css
ID: $channel.id
Created: ${new Date(channel.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Users: ${if(channel.users.size()>4){channel.users[0..4]*.identity.join(', ')+'..'}else{channel.users*.identity.join(', ')}} (${channel.users.size()})
${if(channel.class==TextChannelImpl){"Last Activity: ${try{channel.history.retrievePast(1).block()[0].createTime.format('HH:mm:ss, d MMMM yyyy')}catch(CantView){'???'}}"}else{"Bit Rate: ${channel.bitrate/1000} kbps"}}
${if(props){"Properties: ${props.join(', ')}"}else{"No special properties"}}
${if(channel.guild.defaultChannel==channel){'Default '}else if(channel.guild.afkChannel==channel){'AFK '}else{''}}${channel.class.simpleName-'ChannelImpl'}```""").queue()
			}else{
				e.sendMessage("""**${channel.user.name.capitalize()}** in Direct Messages: ```css
ID: $channel.id
Created: ${new Date(channel.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Users: $e.jda.selfUser.identity, $e.author.identity (2)
Last Activity: ${channel.history.retrievePast(1).block()[0].createTime.format('HH:mm:ss, d MMMM yyyy')}
Properties: NSFW, Song
Direct Text```""").queue()
			}
		}else{
			e.sendMessage("I couldn't find a channel matching '$d.args.'").queue()
			404
		}
	}
	String category='General'
	String help="""`channelinfo [channel]` will make me tell you some useful information about the channel.
This is getting a little mundane, isn't it?"""
}


class RoleinfoCommand extends Command{
	List aliases=['roleinfo','role']
	def run(Map d,Event e){
		String area=d.db[e.author.id]?.area?:'United States'
		String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
		int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
		if(e.guild){
			Role role
			if(d.args&&e.guild)role=e.message.mentionedRoles?e.message.mentionedRoles[-1]:e.guild.findRole(d.args)
			if(role){
				List collection=role.guild.members.findAll{role.id in it.roles*.id}*.user*.identity
				e.sendMessage("""**${role.name.capitalize()}** in $role.guild.name: ```css
ID: $role.id
Colour: ${if(role.color){role.color.collect{"rgb($it.red,$it.blue,$it.green)"}}else{'Default'}}
Created: ${new Date(role.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Users: ${if(collection.size()>9){collection[0..9].join(', ')+'..'}else{collection.join(', ')}} (${collection.size()})
${if(role.managed){'Integrated'}else if(role.config){'Config'}else if(role.id==d.roles.mute[role.guild.id]){'Mute'}else if(role.id==d.roles.member[role.guild.id]){'Member'}else if(role.colour){'Colour'}else if(role==role.guild.defaultRole){'Default'}else{'Regular'}}```""").queue()
			}else{
				e.sendMessage("I couldn't find a role matching '$d.args.'").queue()
				404
			}
		}else{
			User user=e.jda.selfUser
			e.sendMessage("""**@everyone** in Direct Messages: ```css
ID: $user.id
Colour: Default
Created: ${new Date(user.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Users: ${try{(e.jda.privateChannels*.user*.identity+user.identity)[0..14].join(', ')+'..'}catch(NotThatMany){(e.jda.privateChannels*.user*.identity+user.identity).join(', ')}} (${e.jda.privateChannels.size()+1})
Default Role```""").queue()
		}
	}
	String category='General'
	String help="""`roleinfo [role]` will make me tell you some useful information about the role.
How many of you have that NSFW role..."""
}


class EmoteinfoCommand extends Command{
	List aliases=['emoteinfo','emote']
	def run(Map d,Event e){
		Emote emote
		if(d.args)emote=e.message.emotes?e.message.emotes[-1]:e.jda.findEmote(d.args)
		if(emote){
			String area=d.db[e.author.id]?.area?:'United States'
			String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
			int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
			e.sendMessage("""**${emote.name.capitalize()}** in ${if(emote.guild){emote.guild.name}else{'???'}}: ```css
ID: $emote.id
Uploaded: ${new Date(emote.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Image: $emote.imageUrl
${if(emote.managed){'Integrated'}else{'Regular'}}```""").queue()
		}else if(d.args){
			e.sendMessage("I couldn't find an emote matching '$d.args.'").queue()
			404
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}emoteinfo [emote]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`emoteinfo [emote]` will make me tell you some useful information about the emote.
Unfortunately, I probably can't use it."""
}


class AvatarCommand extends Command{
	List aliases=['avatar','icon']
	def run(Map d,Event e){
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>5)mens=mens[0..4]
			String list=mens.collect{"**${it.identity.capitalize()}**: ${it.avatar?it.avatar.replace('.jpg','.png')+'?size=1024':it.defaultAvatar}"}.join('\n')
			e.sendMessage(list).queue()
		}else if(e.message.emotes){
			String list=e.message.emotes.collect{"**${it.name.capitalize()}**: $it.imageUrl"}.join('\n')
			e.sendMessage(list).queue()
		}else{
			User user=e.author
			if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
			if(user){
				e.sendMessage("**${user.identity.capitalize()}**'s avatar:\n${user.avatar?user.avatar.replace('.jpg','.png')+'?size=1024':user.defaultAvatar}").queue()
			}else{
				Guild guild
				if(d.args)guild=e.jda.findGuild(d.args)
				if(guild){
					if(guild.icon){
						e.sendMessage("**${guild.name.capitalize()}**'s icon:\n${guild.icon.replace('.jpg','.png')}?size=1024").queue()
					}else{
						e.sendMessage("**${guild.name.capitalize()}**'s icon:\n`${guild.name.abbreviate()}`").queue()
					}
				}else{
					e.sendMessage("I couldn't find a user or server matching '$d.args.'").queue()
					404
				}
			}
		}
	}
	String category='General'
	String help="""`avatar [user/server/emote] will make me get the avatar/icon of them.
Now tilt your head..."""
}


class InfoCommand extends Command{
	List aliases=['info']
	def run(Map d,Event e){
		String info="""**About GR\\\u2699VER**:
Created by ${d.db['107894146617868288'].name}. JDA by ${d.db['107562988810027008'].name}. ${d.db['98457401363025920'].name} helped too.

GRover \u2018the DOGBOT Project\u2019 is a bot with an ever-expanding database recording the Internet identity of everyone on Discord.
GRover is based on the xat FEXBot and was designed to remedy the issue of recognising users who change their name.
Made before discriminators, notes, nicknames and embeds and its irrelevance will show in its functions.

Use `${d.prefix}help` to get a list of commands.

OAuth invite: $d.bot.oauth
Github code: https://github.com/Axew13/GroovyRover/blob/master/main.groovy
Official server: $d.bot.server"""
		try{
			e.author.openPrivateChannel().block()
			e.author.privateChannel.sendMessage(info).queue()
			if(e.guild){
				e.sendMessage("Information has been sent! <@$e.author.id>").queue{
					Thread.sleep(5000)
					it.delete().queue()
				}
			}
		}catch(alt){
			e.sendMessage(info).queue()
		}
	}
	String category='General'
	String help="""`info` will make me DM you some obligatory information.
That's about it."""
}


class HelpCommand extends Command{
	List aliases=['help','commands']
	def run(Map d,Event e){
		d.args=d.args.toLowerCase()
		if(!d.args){
			String list=''
			List commands=d.bot.commands.findAll{!it.dev}
			commands*.category.unique().each{String cat->
				list+="**$cat Commands**:\n${commands.findAll{it.category==cat}.collect{"$d.prefix${it.aliases[0]}"}.join(',  ')}\n\n"
			}
			list+="Use `${d.prefix}help <command>` to get further assistance."
			try{
				e.author.openPrivateChannel().block()
				list.split(1999).each{
					e.author.privateChannel.sendMessage(it).queue()
					Thread.sleep(150)
				}
				if(e.guild){
					e.sendMessage("Help has been sent! <@$e.author.id>").queue{
						Thread.sleep(5000)
						it.delete().queue()
					}
				}
			}catch(alt){
				list.split(1999).each{
					e.sendMessage(it).queue()
					Thread.sleep(150)
				}
			}
		}else if(d.args.containsAny(['<','>'])){
			e.sendMessage("Don't include the < and >.").queue()
			400
		}else{
			Command cmd=d.bot.commands.find{d.args in it.aliases}
			Map custom=d.customs[e.guild?.id]?.find{it?.name==d.args}
			if(cmd){
				e.sendMessage("**${cmd.aliases[0].capitalize()} Command**:\n$cmd.help").queue()
			}else if(custom){
				e.sendMessage("**$custom.name Custom Command**:\n`$custom.name` will make me run `$custom.command $custom.args`.").queue()
			}else{
				e.sendMessage("I've not heard of that one.").queue()
				404
			}
		}
	}
	String category='General'
	String help="""`help` will make me DM you the list of commands.
`help [command]` will make me tell you more about that command.
I think by now you understand this though."""
}


class JoinCommand extends Command{
	List aliases=['join','invite']
	def run(Map d,Event e){
		if(d.args.toLowerCase()=='server'){
			e.sendMessage("Me and some other bots can be found here:\n$d.bot.server").queue()
		}else{
			e.sendMessage("Add me to your server:\n$d.bot.oauth").queue()
		}
	}
	String category='General'
	String help="""`join` will make me fetch my OAuth URL.
`join server` will make me give the invite link to Totally Groovy.
Inject me all these Discord servers."""
}


class GoogleCommand extends Command{
	List aliases=['google','search']
	int limit=60
	Map cache=[:]
	def run(Map d,Event e){
		if(d.args){
			String link="https://encrypted.google.com/search?q=${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				String ass=d.args.split().join()
				if(cache[ass]){
					Elements links=cache[ass]
					Element linkTag=links[1].getElementsByTag('a')[0]
					e.sendMessage("${linkTag.text().capitalize()}: ${linkTag.attr('href')}").queue()
					cache.remove(ass)
				}else{
					e.sendTyping().queue()
					Document doc=d.web.get(link,'G.Chrome')
					Elements links=doc.getElementsByClass('r')
					if(links){
						Element linkTag=links[0].getElementsByTag('a')[0]
						e.sendMessage("${linkTag.text().capitalize()}: ${linkTag.attr('href')}").queue()
						cache[ass]=links
					}else{
						e.sendMessage("There are no search results for '$d.args.'\n$link").queue()
					}
				}
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage("You are being rate limited.").queue()
					429
				}else{
					e.sendMessage("There are no search results for '$d.args.'\n$link").queue()
					404
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}google [search term]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`google [search term]` will make me Google it, of course.
Putting in 'google' won't break anything though."""
}


class YouTubeCommand extends Command{
	List aliases=['youtube','yt']
	int limit=30
	Map cache=[:]
	def run(Map d,Event e){
		try{
			String ass=d.args.split().join()
			if(cache[ass]){
				Elements links=cache[ass]
				Element linkTag=links[1].getElementsByTag('a')[0]
				String lonk="https://youtube.com${linkTag.attr('href')}"
				if(!lonk.containsAny(['&list=','/user/','/channel/']))lonk=lonk.replace('youtube.com/watch?v=','youtu.be/')
				e.sendMessage("${linkTag.attr('title')}: $lonk").queue()
				cache.remove(ass)
			}else{
				e.sendTyping().queue()
				String link="https://www.youtube.com/results?search_query=${URLEncoder.encode(d.args,'UTF-8')}"
				Document doc=d.web.get(link,'G.Chrome')
				Elements links=doc.getElementsByClass('yt-lockup-title').findAll{!it.toString().contains('https://googleads')}
				if(links){
					Element linkTag=links[0].getElementsByTag('a')[0]
					String lonk="https://youtube.com${linkTag.attr('href')}"
					if(!lonk.containsAny(['&list=','/user/','/channel/']))lonk=lonk.replace('youtube.com/watch?v=','youtu.be/')
					e.sendMessage("${linkTag.attr('title')}: $lonk").queue()
					cache[ass]=links
				}else{
					e.sendMessage("There are no YouTube videos for '$d.args.'\n$link").queue()
					404
				}
			}
		}catch(none){
			e.sendMessage("There are no YouTube videos for '$d.args.'\n$link").queue()
			404
		}
	}
	String category='Online'
	String help="""`youtube` will make me get the featured videos on YouTube.
`youtube [search term]` will make me search YouTube for that.
Getting your daily fix of Vinesauce, I see."""
}


class ImageCommand extends Command{
	List aliases=['image','is']
	int limit=60
	def run(Map d,Event e){
		boolean gif=d.args.contains('GIF')
		d.args=d.args.replace('GIF','').trim()
		if(d.args){
			e.sendTyping().queue()
			String link="https://encrypted.google.com/search?q=${URLEncoder.encode(d.args,'UTF-8')}&tbm=isch"
			if(gif)link+='&tbs=itp:animated'
			try{
				Document doc1=d.web.get(link,'N.3DS')
				Element image=doc1.getElementsByClass('image')[0]
				Document doc2=d.web.get(image.attr('href'),'N.3DS')
				String imagelink=doc2.getElementById('thumbnail').attr('href')
				String trailer=imagelink.containsAny(['.png','.gif','.jpg','.svg'])
				if(trailer&&!imagelink.contains('wikimedia'))imagelink=imagelink.substring(0,imagelink.indexOf(trailer)+4)
				e.sendMessage(imagelink).queue()
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage("You are being rate limited.").queue()
					429
				}else{
					e.sendMessage("There are no images for '$d.args.'\n$link").queue()
					404
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}image [search term]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`image [search term]` will make me search Google Images for that.
`image [search term] GIF` will get a lot of results from tumblr.
Some people are just visual learners."""
}


class NsfwCommand extends Command{
	List aliases=['nsfw','gelbooru']
	int limit=15
	Map cache=[:]
	Map cache2=[:]
	def run(Map d,Event e){
		d.args=d.args.replace(',',' ').trim()
		if(!e.guild||e.channel.nsfw||e.author.isOwner(e.guild)){
			if(!d.args)d.args='all'
			String link="http://gelbooru.com/index.php?page=post&s=list&tags=${URLEncoder.encode(d.args,'UTF-8')}"
			Document doc
			int pages=1
			String page
			if(cache[d.args]){
				pages=cache[d.args]
			}else{
				doc=d.web.get(link,'G.Chrome')
				if(doc.toString().contains('Nobody here')){
					e.sendMessage("Sorry, no results. ;_;\nRemember Gelbooru is for hentai, so keywords should use underlines and names are reversed (like ${["kaname_madoka","aisaka_taiga","izumi_konata"].randomItem()}).\n$link").queue()
				}else{
					Element lastpagebtn=doc.getElementsByClass('pagination')[0].getElementsByTag('a').last()
					String lastpage
					if(lastpagebtn){
						lastpage=lastpagebtn.attr('href')
						pages=(lastpage.substring(lastpage.indexOf('pid=')+4).toInteger()/42)+1
					}else{
						lastpage="http://gelbooru.com/index.php?page=post&s=list&tags=${URLEncoder.encode(d.args,'UTF-8')}&pid=42"
					}
				}
			}
			if(pages>476)pages=476
			if(!doc?.text()?.contains('Nobody here')){
				cache[d.args]=pages
				page=(((Math.floor((Math.random()*pages)+1)*42)as int)-42).toString()
				String ass="$link&pid=$page"
				doc=cache2[ass]
				if(!doc)doc=d.web.get(ass,'G.Chrome')
				Elements previews=doc.getElementsByClass('thumb')
				cache2[ass]=previews
				doc=d.web.get("http://gelbooru.com/${previews.randomItem().getElementsByTag('a')[0].attr('href')}",'G.Chrome')
				String hentai=doc.getElementsByClass('sidebar3')[1].getElementsByTag('div')[2].getElementsByTag('a')[0].attr('href')
				if(hentai.length()<2)hentai=doc.getElementById('image').attr('src')
				e.sendMessage("http:$hentai").queue()
			}else{
				404
			}
		}else{
			TextChannel nsfwChannel=e.guild.textChannels.find{it.nsfw}
			e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`${if(nsfwChannel){", `Use in #$nsfwChannel.name`"}else{''}}.\nStaff can set NSFW channels with `${d.prefix}setchannel nsfw`.").queue()
			403
		}
	}
	String category='Online'
	String help="""`nsfw` will make me send a random Gelbooru image. This could be anything.
`nsfw [tags]` will make me search Gelbooru for hentai.
L-lewd."""
}


class LevelPalaceCommand extends Command{
	List aliases=['levelpalace','lp']
	int limit=60
	def run(Map d,Event e){
		if(d.args){
			try{
				e.sendTyping().queue()
				Map temporaryFix=['pixelfox':8,'mario1luigi9':8,'7supermariobros7':212,'mariomaster7771':212,'brendan':1,'doomslayer522':266,'masterkastyl1nos222':285,'tnttimelord':349,'unown':555,'evol vex':694]
				Document doc2
				String link
				String ass=d.args.toLowerCase()
				if(temporaryFix[ass]){
					link="https://www.levelpalace.com/profile.php?user_id=${temporaryFix[ass]}"
				}else{
					Document doc1=d.web.get("https://encrypted.google.com/search?q=${URLEncoder.encode("$d.args profile site:levelpalace.com",'UTF-8')}",'G.Chrome')
					link=doc1.getElementsByClass('r')[0].getElementsByTag('a')[0].attr('href')
				}
				try{
					if(!link.startsWith('http'))link="http://$link"
					doc2=d.web.get("$link&client=dogbot",'G.Chrome')
					Elements cards=doc2.getElementsByClass('card-content')
					try{
						String profileText=doc2.getElementById('main').text().trim()
						if(profileText.length()>1000)profileText=profileText.substring(0,1000)+'...'
						String location=":flag_${cards[0].getElementsByClass('card-title')[5]?.getElementsByTag('img')?.attr('alt')?.toLowerCase()}:"
						if(location==':flag_:')location='\u2753'
						e.sendMessage("**${cards[0].getElementsByClass('card-title')[0].text().capitalize()}**  (${cards[0].getElementsByClass('subtitle')[0].text()})\nRank: ${cards[0].getElementsByClass('card-title')[1].text()}  Levels: ${cards[0].getElementsByClass('card-title')[2].text()}  Rates: ${cards[0].getElementsByClass('card-title')[3].text()}  Friends: ${cards[0].getElementsByClass('card-title')[4].text()}  $location\n$profileText\n\n<$link>").queue()
					}catch(none){
						e.sendMessage("No user matching '$d.args' was found.").queue()
					}
				}catch(down){
					e.sendMessage("Looks like Level Palace is offline. Press `f` to pay respects.").queue()
					503
				}
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage("You are being rate limited.").queue()
					429
				}else{
					e.sendMessage("No user matching '$d.args' was found.").queue()
					404
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}levelpalace [search term]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`levelpalace [search term]` will make me search Level Palace's members.
GRover may or may not be affiliated with this website."""
}


class AnimeCommand extends Command{
	List aliases=['anime','animeonline']
	int limit=30
	def run(Map d,Event e){
		if(d.args){
			e.sendTyping().queue()
			String link="https://myanimelist.net/anime.php?q=${URLEncoder.encode(d.args)}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				try{
					link=doc.getElementsByClass('hoverinfo_trigger')[0].attr('href')
					doc=d.web.get(link,'G.Chrome')
					String name=doc.getElementsByTag('span').find{it.attr('itemprop')=='name'}.text().capitalize()
					String photo=doc.getElementsByClass('ac')?.attr('src')?:''
					String type=doc.getElementsByClass('type').text()
					String season=doc.getElementsByClass('season').text()
					if(season)season='/$season'
					String score=doc.getElementsByClass('score').text()
					String favourites=doc.getElementsByClass('js-scrollfix-bottom')[0].getElementsByClass('dark_text')[-1].parent().text()
					String ranked=doc.getElementsByClass('ranked')[0].getElementsByTag('strong')[0].text().replace('#','')
					String description
					try{
						description=doc.getElementsByTag('span').find{it.attr('itemprop')=='description'}.text().capitalize()
						if(description.length>1000)description=description.substring(0,1000)+'...'
					}catch(empty){
						description="(No synopsis yet.)"
					}
					String links=[link,[d.misc.subs[link.substring(link.indexOf('/anime/')+7,link.lastIndexOf('/'))]?:[]]].flatten().join('>\n<')
					e.sendMessage("**$name**  (${type}${season})\n$photo\nScore: $score  $favourites  Rank: $ranked\n\n$description\n\n<$links>").queue()
				}catch(none){
					e.sendMessage("No anime matching '$d.args' was found.").queue()
					404
				}
			}catch(down){
				e.sendMessage("Looks like MyAnimeList is offline. Press `f` to pay respects.").queue()
				down.printStackTrace()
				503
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}anime [search term]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`anime [search term]` will make me search MyAnimeList's database for the anime.
I'll even throw in a link to watch if I have one. Piracy yay."""
}


class WebsiteCommand extends Command{
	List aliases=['website','site']
	int limit=30
	def run(Map d,Event e){
		d.args=d.args.replace(' ','-')
		if(!d.args.startsWithAny(['http://','https://']))d.args="http://$d.args"
		if(!d.args.contains('.'))d.args+='.com'
		if(d.args.contains('.')){
			e.sendTyping().queue()
			List months=['January','February','March','April','May','June','July','August','September','October','November','December']
			String link="http://website.informer.com/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				String title=doc.getElementById('title')?doc.getElementById('title').text().capitalize():d.args
				String description=doc.getElementById('description')?"\n${doc.getElementById('description').text().capitalize()}":''
				String keywords=doc.getElementById('keywords')?.text()?.length()>9?"\n_${doc.getElementById('keywords').text().replace('Keywords: ','')}_":''
				String alexa=doc.getElementById('alexa_rank')?"     #${doc.getElementById('alexa_rank').getElementsByTag('b').text()}":''
				Elements table=doc.getElementsByClass('domenGenTable')[0].getElementsByTag('td')
				try{
					List date=table[1].text().tokenize('-').reverse()+table[3].text().tokenize('-')[0]
					date[1]=months[date[1].toInteger()-1]
					e.sendMessage("**$title**:$alexa$description$keywords\n\n**Period**: ${date[0..1].join(' ').formatBirthday()} ${date[2]}-${date[3]}\n**Owner**: ${table[5].text()}\n**Host**: ${table[7].text()}\n**IPs**: ${table[11].text()}\n\n<$link>").queue()
				}catch(alt){
					e.sendMessage("**$title**:$alexa$description$keywords\n\n<$link>").queue()
				}
			}catch(none){
				e.sendMessage("There is no data for '$d.args.'\n$link").queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}website [domain]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`website [domain]` will make me get the website's data on Website Informer.
You're not going to use this to DDOS, are you?"""
}


class MiiverseCommand extends Command{
	List aliases=['miiverse','mvs']
	int limit=30
	def run(Map d,Event e){
		d.args=d.args.replace('@','')
		if(d.args){
			e.sendTyping().queue()
			String link="https://miiverse.nintendo.net/users/${URLEncoder.encode(d.args,'UTF-8')}/posts"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				Elements posts=doc.getElementsByClass('post')
				if(posts){
					if(posts.size()>3)posts=posts[0..2]
					Element post=posts.randomItem()
					String url=post.getElementsByClass('timestamp')[0].attr('href')
					if(!url)url=post.getElementsByClass('timestamp')[0].attr('data-href-hidden')
					String community=post.getElementsByClass('test-community-link')[0].text().replace('Community','').trim()
					String type=post.getElementsByClass('post-subtype-label')[0].text()
					String content=post.getElementsByClass('post-content-text')[0].text()
					String screenshot=post.getElementsByClass('screenshot-container')?'\n'+post.getElementsByClass('screenshot-container')[0].getElementsByTag('img').attr('src'):''
					String yeahs=post.getElementsByClass('empathy-count')[0].text()
					String replies=post.getElementsByClass('reply-count')[0].text()
					e.sendMessage("**$community** ($type):\n$content$screenshot\n\ud83d\ude03`$yeahs`   \ud83d\udcac`$replies`\n\n<https://miiverse.nintendo.net$url>\n<$link>").queue()
				}else{
					e.sendMessage("There are no Miiverse posts for '$d.args.'\n$link").queue()
					404
				}
			}catch(none){
				e.sendMessage("There are no Miiverse posts for '$d.args.'\n$link").queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}miiverse [nnid]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`miiverse [nnid]` will make me get one of the latest Miiverse posts by the NNID.
That is if the admins haven't banned them."""
}


class MarioMakerCommand extends Command{
	List aliases=['mariomaker','smm']
	int limit=30
	def run(Map d,Event e){
		d.args=d.args.replace('@','')
		String ass=d.args+('0'*15)
		if((ass[4]=='-')&&(ass[9]=='-')&&(ass[14]=='-')){
			e.sendTyping().queue()
			String link="https://supermariomakerbookmark.nintendo.net/courses/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				String title=doc.getElementsByClass('course-title')[0].text()
				String uploader=doc.getElementsByClass('name')[0].text()
				String levelmap=doc.getElementsByClass('course-image-full')[0].attr('src')
				String type=doc.getElementsByClass('course-tag')[0].text()
				String difficulty=doc.getElementsByClass('course-header')[0].text()
				String likes=doc.getElementsByClass('liked-count')[0].getElementsByClass('typography')*.className()*.split('-')*.last().join()
				String plays=doc.getElementsByClass('played-count')[0].getElementsByClass('typography')*.className()*.split('-')*.last().join()
				e.sendMessage("__**$title** by ${uploader}__\n**Level Type**: $type\n**Difficulty**: $difficulty\n**Map**: $levelmap\n\ud83d\udc63`$plays`   \u2b50`$likes`\n\n<$link>").queue()
			}catch(none){
				e.sendMessage("That course doesn't exist. Ensure the course ID is correct.\n$link").queue()
				404
			}
		}else if(d.args){
			e.sendTyping().queue()
			String link="https://supermariomakerbookmark.nintendo.net/profile/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				Elements links=doc.getElementsByClass('course-card')
				if(links){
					if(links.size()>2)links=links[0..1]
					String text=''
					for(l in links){
						String id=l.getElementsByClass('button')[0].attr('href').replace('/courses/','')
						String name=l.getElementsByClass('course-title')[0].text()
						String tag=l.getElementsByClass('course-tag')[0].text()
						String difficulty=l.getElementsByClass('course-header')[0].text()
						String likes=l.getElementsByClass('liked-count')[0].getElementsByClass('typography')*.className()*.split('-')*.last().join()
						String plays=l.getElementsByClass('played-count')[0].getElementsByClass('typography')*.className()*.split('-')*.last().join()
						text+="**$name**: $id\n\ud83d\udc63`$plays`   \u2b50`$likes`   $difficulty $tag\n\n"
					}
					e.sendMessage("$text<$link>").queue()
				}else{
					e.sendMessage("There are no Super Mario Maker courses for '$d.args.'\n$link").queue()
					404
				}
			}catch(none){
				e.sendMessage("There are no Super Mario Maker courses for '$d.args.'\n$link").queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}mariomaker [nnid/course id]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`mariomaker [nnid]` will make me get the latest SMM courses by the NNID.
`mariomaker [course id]` will make me get information about that course.
Dannyh09, eat your heart out."""
}


class DefineCommand extends Command{
	List aliases=['define','dictionary']
	int limit=30
	def run(Map d,Event e){
		if(d.args){
			e.sendTyping().queue()
			String link="http://dictionary.cambridge.org/dictionary/english/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				Elements error=doc.getElementsByClass('cdo-hero__error')
				if(error){
					e.sendMessage("There is no definition for '$d.args.'\n$link").queue()
					404
				}else{
					Elements meanings=doc.getElementsByClass('sense-block')
					if(meanings.size()>3)meanings=meanings[0..2]
					String result=''
					try{
						for(meaning in meanings)result+="**${d.args.capitalize()}** (${meaning.getElementsByClass('pos')[0].text()}):  ${meaning.getElementsByClass('guideword')[0].text().toLowerCase().replaceAll(['(',')'],'')}\n${meaning.getElementsByClass('def')[0].text().capitalize().replaceAll(/:$/,'')}${Element example=meaning.getElementsByClass('eg')[0];if(example){": *${example.text().replaceAll(/.$/,'')}*"}else{''}}.\n"
					}catch(alt){
						result+="**${d.args.capitalize()}**:  *${doc.getElementsByClass('pos')[0].text()}*\n${meanings[0].getElementsByClass('def')[0].text().capitalize().replaceAll(/:$/,'')}${Element example=meanings[0].getElementsByClass('eg')[0];if(example){": *${example.text().replaceAll(/.$/,'')}*"}else{''}}.\n"
					}
					Elements synonyms=doc.getElementsByClass('smartt')?.getAt(0)?.getElementsByClass('cdo-cloud-content')?.getAt(0)?.getElementsByClass('hw')
					if(synonyms)result+="\n**Synonyms**:  ${synonyms*.text().join(',  ')}"
					if(result.length()>3){
						e.sendMessage("${result.replaceEach(['?.','!.'],['?','!'])}\n\n<$link>").queue()
					}else{
						e.sendMessage("There is no definition for '$d.args.'\n$link").queue()
						404
					}
				}
			}catch(none){
				e.sendMessage("There is no definition for '$d.args.'\n$link").queue()
				none.printStackTrace()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}define [word]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`define [word]` will make me get the definition of that word.
Someone is going to call you stupid anyway."""
}


class UrbanCommand extends Command{
	List aliases=['urban']
	int limit=30
	def run(Map d,Event e){
		if(d.args){
			e.sendTyping().queue()
			String link="http://www.urbandictionary.com/define.php?term=${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				Element de=doc.getElementsByClass('def-panel')[0]
				if(de){
					Element worddef=de.getElementsByClass('word')[0]
					Element meaning=de.getElementsByClass('meaning')[0]
					Element example=de.getElementsByClass('example')[0]
					String definition=("**${worddef.text().replace('_','\\_').replace('*','\\*').replace('`','\\`').replace('~','\\~').capitalize()}**:\n${meaning.text().replace('_','\\_').replace('*','\\*').replace('`','\\`').replace('~','\\~')}\n_${example.text()}_\n\n")
					if(definition.length>1500)definition=definition.substring(0,1500)+'...'
					e.sendMessage("${definition.replace('\n**','')}\n<$link>").queue()
				}else{
					e.sendMessage("There is no urban definition for '$d.args.'\n$link").queue()
					404
				}
			}catch(none){
				e.sendMessage("There is no urban definition for '$d.args.'\n$link").queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}urban [word]`").queue()
			400
		}
	}
	String category='Online'
	String help="""`urban [word]` will make me get the Urban Dictionary definition of that word.
A place formerly used to find out about slang, and now a place that teens with no life use as a burn book to whine about celebrities, their friends, etc., let out their sexual frustrations, show off their racist/sexist/homophobic/anti-(insert religion here) opinions, troll, and babble about things they know nothing about. That was an example."""
}


class TagCommand extends Command{
	List aliases=['tag','tags']
	def run(Map d,Event e){
		d.args=d.args.split('( |\n|\r)',3).toList()
		d.args[0]=d.args[0]?.toLowerCase()
		if(e.message.attachment)d.args+=e.message.attachment.url
		if(d.args[0]=='create'){
			if(d.args[2]){
				d.args[1]=d.args[1]?.toLowerCase()
//				String ass=d.args[1].startsWithAny(e.jda.guilds.findAll{e.author in it.users}.id*.plus(':'))
				if(d.args[1].containsAny(['@everyone','@here'])){
					e.sendMessage("You cannot create a tag with that name.").queue()
/*				}else if(d.args[1]=~/^\d+:/&&!ass){
					e.sendMessage("You cannot create a tag in a server you're not in.").queue()*/
				}else if(d.tags[d.args[1]]){
					e.sendMessage("That tag already exists. You can edit it if it belongs to you or a server you're in.").queue()
					400
				}else{
/*					String server=e.guild?.id
					if(ass)server=ass.substring(0,ass.length()-1)*/
					d.tags[d.args[1]]=[
						server:/*server*/e.guild?.id,
						history:[[
							content:d.args[2..-1].join(' '),
							author:e.author.id
						]],
						uses:0
					]
					e.sendMessage("The tag **${d.args[1]}** has been created. You can now use `${d.prefix}tag ${e.guild?d.args[1].replaceAll(/^$e.guild.id:/,''):d.args[1]}`.").queue()
					d.json.save(d.tags,'tags')
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag create [tag name] [tag content]`.").queue()
				400
			}
		}else if(d.args[0]=='edit'){
			if(d.args[2]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if((d.tags[d.args[1]].server in e.jda.guilds.findAll{e.author in it.users}.id)||(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner])){
						d.tags[d.args[1]].history+=[
							content:d.args[2..-1].join(' '),
							author:e.author.id
						]
						e.sendMessage("The tag **${d.args[1]}** has been edited.").queue()
						d.json.save(d.tags,'tags')
					}else{
						e.sendMessage("You can't edit that tag because you don't own it, nor are you in the server where it was created.").queue()
						403
					}
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag edit [tag name] [tag content]`.").queue()
				400
			}
		}else if(d.args[0]=='delete'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner]){
						d.tags.remove(d.args[1])
						e.sendMessage("The tag **${d.args[1]}** has been deleted.").queue()
						d.json.save(d.tags,'tags')
					}else{
						e.sendMessage("You can't delete that tag because you don't own it.").queue()
						403
					}
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag delete [tag name]`.").queue()
				400
			}
		}else if(d.args[0]=='move'){
			if(d.args[2]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner]){
						d.args[2]=d.args[2..-1].join().toLowerCase()
						List servers=e.jda.guilds.findAll{e.author in it.users}
						String server=servers.find{it.id==d.args[2]}?.id
						if(!server)server=servers.find{it.name.toLowerCase().contains(d.args[2])}?.id
						if(!server)server='?'
						if(d.args[2]=='direct messages')server=null
						if(server!='?'){
							d.tags[d.args[1]].server=server
							e.sendMessage("The tag **${d.args[1]}** has been moved to ${if(server){servers.find{it.id==server}.name}else{'Direct Messages'}}.").queue()
							d.json.save(d.tags,'tags')
						}else{
							e.sendMessage("I couldn't find a shared server with the name '${d.args[2]}.'").queue()
							404
						}
					}else{
						e.sendMessage("You can't move that tag because you don't own it.").queue()
						403
					}
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag move [tag name] [server]`.").queue()
				400
			}
		}else if(d.args[0]=='list'){
			d.args+=''
			List list
			String ass
			User heck=d.args[1]?e.guild?.findUser(d.args[1]):null
			if(!e.guild||e.message.mentions||heck){
				User user=e.message.mentions?e.message.mentions[-1]:e.author
				if(heck&&!e.message.mentions)user=heck
				list=d.tags.findAll{it.value.history[0].author==user.id}*.key
				ass="**__${user.identity.capitalize()}'s Tags ($list.size)__:**\n"
			}else{
				list=d.tags.findAll{it.value.server==e.guild.id}*.key
				ass="**__${e.guild.name.capitalize()}'s Tags ($list.size)__:**\n"
			}
			if(list){
				ass+=list.join(',  ').replace('_','\\_').replace('*','\\*').replace('~~','\\~~')
			}else{
				ass+="No tags to see here."
			}
			List result=ass.split(1999)
			def destination=e.channel
			if(result.size()>2){
				e.author.openPrivateChannel().block()
				destination?.id==e.author.privateChannel?.id
			}
			try{
				result.each{
					destination.sendMessage(it).queue()
					Thread.sleep(150)
				}
				if((destination?.id==e.author.privateChannel?.id)&&e.guild){
					e.sendMessage("It was really long (shield), so I sent it to you. <@$e.author.id>").queue{
						Thread.sleep(5000)
						it.delete().queue()
					}
				}
			}catch(ex){
				e.sendMessage("I couldn't send you the list of tags because you have me blocked.").queue()
				400
			}
		}else if(d.args[0]=='info'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					d.args[1]=d.args[1].toLowerCase().replaceAll(['\n','\r'],'_')
					String server='Direct Messages'
					String author=e.jda.users.find{it.id==d.tags[d.args[1]].history[0].author}?.identity?:d.tags[d.args[1]].history[0].author
					if(d.tags[d.args[1]].server)server=e.jda.guilds.find{it.id==d.tags[d.args[1]].server}?.name?:d.tags[d.args[1]].server
					"Created by $author in $server.\n\n${d.tags[d.args[1]].history[-1].content}\n\nThis tag has ${d.tags[d.args[1]].uses} use${if(d.tags[d.args[1]].uses==1){""}else{"s"}} and ${d.tags[d.args[1]].history*.key.size()} history.".split(1999).each{
						e.sendMessage(it).queue()
					}
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag info [tag name]`.").queue()
				400
			}
		}else if(d.args[0]=='history'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if((d.tags[d.args[1]].server in e.jda.guilds.findAll{e.author in it.users}.id)||(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner])){
						d.args[1]=d.args[1].toLowerCase().replaceAll(['\n','\r'],'_')
						String ass="**__${d.args[1].capitalize()}'s History (${d.tags[d.args[1]].history.size()})__:**\n"
						d.tags[d.args[1]].history.reverse().each{Map kona->
							ass+="${(e.jda.users.find{it.id==kona.author}?.identity?:kona.author).capitalize()} ${if(kona.index(d.tags[d.args[1]].history)){'edited'}else{'created'}}:\n$kona.content\n\n"
						}
						e.author.openPrivateChannel().block()
						try{
							ass.split(1999).each{
								e.author.privateChannel.sendMessage(it).queue()
							}
							if(e.guild){
								e.sendMessage("I have sent you that tag's history. <@$e.author.id>").queue{
									Thread.sleep(5000)
									it.delete().queue()
								}
							}
						}catch(ex){
							e.sendMessage("I couldn't send you that tag's history because you have me blocked.").queue()
							400
						}
					}else{
						e.sendMessage("You can't view that tag's history because you don't own it, nor are you in the server where it was created.").queue()
						403
					}
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag history [tag name]`.").queue()
				400
			}
		}else if(d.args[0]=='owner'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					String id=d.tags[d.args[1]].history[0].author
					String owner=e.jda.users.find{it.id==id}?.identity?:id
					List contrids=d.tags[d.args[1]].history*.author.unique()-id
					List contributors=[]
					contrids.each{String sass->
						contributors+=e.jda.users.find{it.id==sass}?.identity?:sass
					}
					if(!contributors)contributors+='None'
					e.sendMessage("Owner: $owner\n\nContributors: ${contributors.join(', ')}").queue()
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag owner [tag name]`.").queue()
				400
			}
		}else if(d.args[0]=='popular'){
			int amount=10
			List popular=d.tags*.key.sort{d.tags[it].uses}.reverse()
			if(e.message.mentions)popular=popular.findAll{d.tags[it].history[0].author==e.message.mentions[-1].id}
			String nom=d.args.join(' ').replace('<@!','<@').replaceAll(e.message.mentions*.mention,'').findAll(/\b\d+\b/)[0]
			if(nom)amount=nom.toInteger()
			if(amount>20)amount=20
			if(popular){
				popular=popular.sort{d.tags[it].uses}.reverse()
				if(popular.size>amount)popular=popular[0..amount-1]
				int num=0
				e.sendMessage(popular[0..(popular.size()-1)].collect{"`#${num+=1}` **$it** (${d.tags[it].uses} uses)"}.join('\n')).queue()
			}else{
				e.sendMessage("That user doesn't seem to have any tags.").queue()
				404
			}
		}else if(d.args[0]=='search'){
			if(d.args[1]){
				String search=d.args[1..-1].join().toLowerCase()
				List tags=d.tags*.key.findAll{it.contains(search)}
				String result=tags.join(', ').replace(search,'**$search**')
				if(result.length()>1000)result=result.substring(0,1000)+'...'
				else if(!result)result="No matching tags found."
				e.sendMessage("**__Tag Results (${tags.size()})__:**\n$result").queue()
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag search [search term]`.").queue()
				400
			}
		}else if(d.args[0]=='get'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(d.tags[d.args[1]]){
					List result=d.tags[d.args[1]].history[-1].content.addVariables(e,d.args.join(' ').substring(d.args[0..1].join(' ').length()).trim()).split(1999)
					if(result.size()>2){
						e.sendMessage("It's over 4000... Yeah, no.").queue()
					}else{
						result.each{
							e.sendMessage(it).queue()
							Thread.sleep(150)
						}
					}
					d.tags[d.args[1]].uses+=1
					d.json.save(d.tags,'tags')
				}else{
					e.sendMessage("The tag '${d.args[1]}' doesn't exist.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag get [tag name]`.").queue()
				400
			}
		}else if(d.args[0]){
			if(e.guild&&d.tags["$e.guild.id:${d.args[0]}"])d.args[0]="$e.guild.id:${d.args[0]}"
			if(d.tags[d.args[0]]){
				List result=d.tags[d.args[0]].history[-1].content.addVariables(e,d.args.join(' ').substring(d.args[0].length()).trim()).split(1999)
				if(result.size()>2){
					e.sendMessage("It's over 4000... Yeah, no.").queue()
				}else{
					result.each{
						e.sendMessage(it).queue()
						Thread.sleep(150)
					}
				}
				d.tags[d.args[0]].uses+=1
				d.json.save(d.tags,'tags')
			}else{
				e.sendMessage("The tag '${d.args[0]}' doesn't exist.").queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tag create/edit/delete/move/info/history/owner/popular/search/get/[tag name] ..`.").queue()
			400
		}
	}
	String category='General'
	String help="""`tag [tag name]` will make me get the tag.
`tag create [tag name] [tag content]` will make me create the tag.
`tag edit [tag name] [tag content]` will make me edit the tag if you have rights to it.
`tag delete [tag name] [tag content]` will make me delete the tag if you own it.
`tag move [tag name] [server]` will make me move the tag if you own it.
`tag list [@mention]` will make me list the user's tags or the tags in this server.
`tag info [tag name]` will make me give you the raw information and statistics of the tag.
`tag history [tag name]` will make me send you the history of the tag if you have rights to it.
`tag owner [tag name]` will make me tell you who owns and contributed to the tag.
`tag popular [@mention]` will make me get the user's most popular tags or the most popular tags in general.
`tag search [query]` will make me search for tags with a matching name.
`tag get [tag name]` will make me get the tag even if its name is masked by a subcommand.
Shitposting, shitposting everywhere."""
}


class MiscCommand extends Command{
	List aliases=['misc']
	def run(Map d,Event e){
		d.args=d.args.toLowerCase().tokenize()
		if(d.args[0]in['pi','\u03c0']){
			String pi='141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648566923460348610454326648213393607260249141273'
			int decimals=11
			if(d.args[1]==~/\d+/){
				decimals=d.args[1].toInteger()
				if(decimals>300)decimals=300
				if(decimals<1)decimals=1
			}
			e.sendMessage("3.${pi.substring(0,decimals)}").queue()
		}else if(d.args[0]in['uptime','up']){
			List uptime=[0,(((System.currentTimeMillis()-d.started)/1000)/60)as int]
			(uptime[1]/60).times{
				uptime[0]+=1
				uptime[1]-=60
			}
			e.sendMessage("`${uptime[0]}` hour${if(uptime[0]!=1){"s"}else{''}}, `${uptime[1]}` minute${if(uptime[1]!=1){"s"}else{''}}").queue()
		}else if(d.args[0]in['timefromnow','time']){
			try{
				e.sendMessage(new Date(d.args[1].formatTime()).format('HH:mm:ss, dd MMMM YYYY')).queue()
			}catch(ex){
				e.sendMessage("Enter a timefromnow format, like `1d12h`.").queue()
				400
			}
		}else if(d.args[0]in['area','location']){
			try{
				d.args=d.args[1..-1].join(' ').toLowerCase().replaceEach(['england','america'],['united kingdom','united states'])
				List people=d.db.findAll{it.value.area.toLowerCase().contains(d.args)}*.value.name.unique()
				if(people){
					String inhabits=people.join(', ').capitalize()
					if(inhabits.length()>1000)inhabits=inhabits.substring(0,1000)+'...'
					e.sendMessage("$inhabits. (${people.size()})").queue()
				}else{
					e.sendMessage("Looks like I don't know anyone who lives there.").queue()
					404
				}
			}catch(ex){
				e.sendMessage("Enter a location.").queue()
				400
			}
		}else if(d.args[0]=='http'){
			if(d.args[1]){
				String status=d.misc.http[d.args[1]]
				if(status){
					e.sendMessage(status).queue()
				}else{
					e.sendMessage(d.misc.http['404']).queue()
					404
				}
			}else{
				e.sendMessage("Enter a HTTP error code, like `404`.").queue()
				400
			}
		}else if(d.args[0]in['name','reg']){
			if(!d.args[1])d.args[1]=''
			d.args[1]=d.args[1].replaceAll(/\D/,'')
			if(d.args[1]){
				Object thing=e.jda.users.find{it.id==d.args[1]}
				if(!thing)thing=e.jda.guilds.find{it.id==d.args[1]}
				if(!thing)thing=e.jda.channels.find{it.id==d.args[1]}
				if(!thing)thing=e.jda.guilds*.roles.flatten().find{it.id==d.args[1]}
				if(!thing)thing=e.jda.guilds*.emotes.flatten().find{it.id==d.args[1]}
				if(thing){
					String ass=thing.class.simpleName.replace('Impl','')
					e.sendMessage("$thing.name ($ass)").queue()
				}else{
					e.sendMessage("I couldn't find anything matching that ID.").queue()
					404
				}
			}else{
				e.sendMessage("Enter an ID, like `$e.author.id`.").queue()
				400
			}
		}else if(d.args[0]=='created'){
			if(!d.args[1])d.args[1]=''
			d.args[1]=d.args[1]?d.args[1].replaceAll(/\D/,''):e.author.id
			if(d.args[1]){
				String area=d.db[e.author.id]?.area?:'United States'
				String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
				int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
				long time=((Long.parseLong(d.args[1])>>22)+1420070400000)+(zone*3600000)
				String date=new Date(time).format('HH:mm:ss, d MMMM YYYY').formatBirthday()
				e.sendMessage("$date ($time) (${key.abbreviate()})").queue()
			}else{
				e.sendMessage("Enter an ID, like `$e.author.id`.").queue()
				400
			}
		}else if(d.args[0]in['prefix','prefixes']){
			if(e.guild){
				String ass=''
				e.guild.users.findAll{it.bot}.findAll{it.id in d.db*.key}.findAll{d.db[it.id].tags.startsWith('Bot')}.each{
					ass+="${it.identity.capitalize()}: `${d.db[it.id].tags.range('(',')').replace('@mention','@'+it.name).replace('`','` ')}`\n"
				}
				if(ass.length()>1500){
					ass=ass.substring(0,1500)
					ass=ass.substring(0,ass.lastIndexOf('\n'))
				}
				e.sendMessage(ass).queue()
			}else{
				e.sendMessage("${d.db[e.jda.selfUser.id].name}: `${d.db[e.jda.selfUser.id].tags.range('(',')')}`").queue()
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}misc pi/uptime/timefromnow/area/http/name/prefix ..`").queue()
			400
		}
	}
	String category='General'
	String help="""`misc pi [number]` will make me tell you pi to that decimal.
`misc uptime` will tell you how long I've been running. JDA strong!
`misc timefromnow [time]` will make me tell you the expiry time of your timefromnow format. This function is used in other commands.
`misc area [area]` will make me tell you the people I know who live there. It's a small world.
`misc http [http]` will make me tell you more about the HTTP status code.
`misc name [id]` will make me trace an ID back to an object on Discord.
`misc created [id]` will make me calculate an ID's creation date.
`misc prefix` will make me tell you prefixes of the bots in the server.
Whew."""
}


class TextCommand extends Command{
	List aliases=['text']
	def run(Map d,Event e){
		try{
			d.args=d.args.tokenize()
			String manipulatives=d.args[0].toLowerCase()
			String output=d.args[1..-1].join(' ')
			if(output&&manipulatives.containsAny(['space','expand','reverse','backward','super','upper','bold','block','italic','cursive','compress','trim','bubble','circle','small','mini','full','fw','strike','line','random','shuffle','emoji','regional','fancy','handwritten'])){
				if(manipulatives.containsAny(['space','expand']))output=output.replace('',' ')
				if(manipulatives.containsAny(['reverse','backward']))output=output.reverse()
				if(manipulatives.containsAny(['super','upper']))output=output.replaceEach(('a'..'z')+('1'..'9')+['0','!','$','%','&','*','(',')','-','=',';',':','.','?']+('A'..'Z')+['\u03b2'],["\u1d43","\u1d47","\u1d9c","\u1d48","\u1d49","\u1da0","\u1d4d","\u02b0","\u1da6","\u02b2","\u1d4f","\u1dab","\u1d50","\u1db0","\u1d52","\u1d56","\u146b","\u02b3","\u02e2","\u1d57","\u1d58","\u1d5b","\u02b7","\u02e3","\u02b8","\u1dbb","\u00b9","\u00b2","\u00b3","\u2074","\u2075","\u2076","\u2077","\u2078","\u2079","\u2070","\ufe57","\ufe69","\ufe6a","\ufe60","\ufe61","\u207d","\u207e","\u207b","\u207c","\ufe54","\ufe55","\u22c5","\ufe56","\u1d2c","\u1d2e","\u1d9c","\u1d30","\u1d31","\u1da0","\u1d33","\u1d34","\u1d35","\u1d36","\u1d37","\u1d38","\u1d39","\u1d3a","\u1d3c","\u1d3e","\u146b","\u1d3f","\u02e2","\u1d40","\u1d41","\u2c7d","\u1d42","\u02e3","\u02b8","\u1dbb","\u1d5d"])
				if(manipulatives.containsAny(['bold','block']))output=output.replaceEach(('a'..'z')+('1'..'9')+['0']+('A'..'Z'),["\ud835\udc1a","\ud835\udc1b","\ud835\udc1c","\ud835\udc1d","\ud835\udc1e","\ud835\udc1f","\ud835\udc20","\ud835\udc21","\ud835\udc22","\ud835\udc23","\ud835\udc24","\ud835\udc25","\ud835\udc26","\ud835\udc27","\ud835\udc28","\ud835\udc29","\ud835\udc2a","\ud835\udc2b","\ud835\udc2c","\ud835\udc2d","\ud835\udc2e","\ud835\udc2f","\ud835\udc30","\ud835\udc31","\ud835\udc32","\ud835\udc33","\ud835\udfcf","\ud835\udfd0","\ud835\udfd1","\ud835\udfd2","\ud835\udfd3","\ud835\udfd4","\ud835\udfd5","\ud835\udfd6","\ud835\udfd7","\ud835\udfce","\ud835\udc00","\ud835\udc01","\ud835\udc02","\ud835\udc03","\ud835\udc04","\ud835\udc05","\ud835\udc06","\ud835\udc07","\ud835\udc08","\ud835\udc09","\ud835\udc0a","\ud835\udc0b","\ud835\udc0c","\ud835\udc0d","\ud835\udc0e","\ud835\udc0f","\ud835\udc10","\ud835\udc11","\ud835\udc12","\ud835\udc13","\ud835\udc14","\ud835\udc15","\ud835\udc16","\ud835\udc17","\ud835\udc18","\ud835\udc19"])
				if(manipulatives.containsAny(['italic','cursive']))output=output.replaceEach(('a'..'z')+('A'..'Z'),["\ud835\udc4e","\ud835\udc4f","\ud835\udc50","\ud835\udc51","\ud835\udc52","\ud835\udc53","\ud835\udc54","\ud835\udc55","\ud835\udc56","\ud835\udc57","\ud835\udc58","\ud835\udc59","\ud835\udc5a","\ud835\udc5b","\ud835\udc5c","\ud835\udc5d","\ud835\udc5e","\ud835\udc5f","\ud835\udc60","\ud835\udc61","\ud835\udc62","\ud835\udc63","\ud835\udc64","\ud835\udc65","\ud835\udc66","\ud835\udc67","\ud835\udc34","\ud835\udc35","\ud835\udc36","\ud835\udc37","\ud835\udc38","\ud835\udc39","\ud835\udc3a","\ud835\udc3b","\ud835\udc3c","\ud835\udc3d","\ud835\udc3e","\ud835\udc3f","\ud835\udc40","\ud835\udc41","\ud835\udc42","\ud835\udc43","\ud835\udc44","\ud835\udc45","\ud835\udc46","\ud835\udc47","\ud835\udc48","\ud835\udc49","\ud835\udc4a","\ud835\udc4b","\ud835\udc4c","\ud835\udc4d"])
				if(manipulatives.containsAny(['compress','trim']))output=output.replaceAll([' ','\u3000'],'')
				if(manipulatives.containsAny(['bubble','circle']))output=output.replaceEach(('A'..'Z')+('a'..'z')+('1'..'9'),['\u24b6','\u24b7','\u24b8','\u24b9','\u24ba','\u24bb','\u24bc','\u24bd','\u24be','\u24bf','\u24c0','\u24c1','\u24c2','\u24c3','\u24c4','\u24c5','\u24c6','\u24c7','\u24c8','\u24c9','\u24ca','\u24cb','\u24cc','\u24cd','\u24ce','\u24cf','\u24d0','\u24d1','\u24d2','\u24d3','\u24d4','\u24d5','\u24d6','\u24d7','\u24d8','\u24d9','\u24da','\u24db','\u24dc','\u24dd','\u24de','\u24df','\u24e0','\u24e1','\u24e2','\u24e3','\u24e4','\u24e5','\u24e6','\u24e7','\u24e8','\u24e9','\u2780','\u2781','\u2782','\u2783','\u2784','\u2785','\u2786','\u2787','\u2788'])
				if(manipulatives.containsAny(['small','mini']))output=output.replaceEach('a'..'z',['\u1d00','\u0299','\u1d04','\u1d05','\u1d07','\u0493','\u0262','\u029c','\u026a','\u1d0a','\u1d0b','\u029f','\u1d0d','\u0274','\u1d0f','\u1d18','\u01eb','\u0280','s','\u1d1b','\u1d1c','\u1d20','\u1d21','x','\u028f','\u1d22'])
				if(manipulatives.containsAny(['full','fw']))output=output.replaceEach(('A'..'Z')+('a'..'z')+('1'..'9')+['0','!','"','$','%','^','&','*','(',')','-','_','+','=','[','{',']','}',';',':','A?AÅí','@','#','|',',','<','.','>','?','~',' '],['\uff21','\uff22','\uff23','\uff24','\uff25','\uff26','\uff27','\uff28','\uff29','\uff2a','\uff2b','\uff2c','\uff2d','\uff2e','\uff2f','\uff30','\uff31','\uff32','\uff33','\uff34','\uff35','\uff36','\uff37','\uff38','\uff39','\uff3a','\uff41','\uff42','\uff43','\uff44','\uff45','\uff46','\uff47','\uff48','\uff49','\uff4a','\uff4b','\uff4c','\uff4d','\uff4e','\uff4f','\uff50','\uff51','\uff52','\uff53','\uff54','\uff55','\uff56','\uff57','\uff58','\uff59','\uff5a','\uff11','\uff12','\uff13','\uff14','\uff15','\uff16','\uff17','\uff18','\uff19','\uff10','\uff01','\u201d','\uff04','\uff05','\uff3e','\uff06','\uff0a','\uff08','\uff09','\uff0d','\uff3f','\uff0b','\uff1d','\u300c','\uff5b','\u300d','\uff5d','\uff1b','\uff1a','\uffe5','\uff20','\uff03','\uff5c','\uff0c','\uff1c','\uff0e','\uff1e','\uff1f','\uff5e','\u3000'])
				if(manipulatives.containsAny(['strike','line']))output=output.replace('','\u0336')
				if(manipulatives.containsAny(['random','shuffle']))output=output.randomize()
				if(manipulatives.containsAny(['emoji','regional']))output=output.replaceEach(('A'..'Z'),('a'..'z')).replaceEach(('a'..'z')+('0'..'9')+['!','?','+','-','\u00d7','\u00f7','\$','\u221a','\u263c','*','>','<','^','.','\u2588','\u25cf','\u25cb','#','\u2605','\u2020','~'],['\ud83c\udde6 ','\ud83c\udde7 ','\ud83c\udde8 ','\ud83c\udde9 ','\ud83c\uddea ','\ud83c\uddeb ','\ud83c\uddec ','\ud83c\udded ','\ud83c\uddee ','\ud83c\uddef ','\ud83c\uddf0 ','\ud83c\uddf1 ','\ud83c\uddf2 ','\ud83c\uddf3 ','\ud83c\uddf4 ','\ud83c\uddf5 ','\ud83c\uddf6 ','\ud83c\uddf7 ','\ud83c\uddf8 ','\ud83c\uddf9 ','\ud83c\uddfa ','\ud83c\uddfb ','\ud83c\uddfc ','\ud83c\uddfd ','\ud83c\uddfe ','\ud83c\uddff ','0\u20e3 ','1\u20e3 ','2\u20e3 ','3\u20e3 ','4\u20e3 ','5\u20e3 ','6\u20e3 ','7\u20e3 ','8\u20e3 ','9\u20e3 ','\u2757 ','\u2753 ','\u2795 ','\u2796 ','\u2716 ','\u2797 ','\ud83d\udcb2 ','\u2714 ','\ud83d\udd06 ','*\u20e3 ','\u25b6 ','\u25c0 ','\ud83d\udd3c ','\u25aa ','\u23f9 ','\u26ab ','\u23fa ','#\u20e3','\u2b50','\u271d','\u3030'])
				if(manipulatives.containsAny(['fancy','handwritten']))output=output.replaceEach(('a'..'z')+('A'..'Z'),['\ud835\udcea','\ud835\udceb','\ud835\udcec','\ud835\udced','\ud835\udcee','\ud835\udcef','\ud835\udcf0','\ud835\udcf1','\ud835\udcf2','\ud835\udcf3','\ud835\udcf4','\ud835\udcf5','\ud835\udcf6','\ud835\udcf7','\ud835\udcf8','\ud835\udcf9','\ud835\udcfa','\ud835\udcfb','\ud835\udcfc','\ud835\udcfd','\ud835\udcfe','\ud835\udcff','\ud835\udd00','\ud835\udd03','\ud835\udd02','\ud835\udd03','\ud835\udcd0','\ud835\udcd1','\ud835\udcd2','\ud835\udcd3','\ud835\udcd4','\ud835\udcd5','\ud835\udcd6','\ud835\udcd7','\ud835\udcd8','\ud835\udcd9','\ud835\udcda','\ud835\udcdb','\ud835\udcdc','\ud835\udcdd','\ud835\udcde','\ud835\udcdf','\ud835\udce0','\ud835\udce1','\ud835\udce2','\ud835\udce3','\ud835\udce4','\ud835\udce5','\ud835\udce6','\ud835\udce7','\ud835\udce8','\ud835\udce9'])
				output.trim().split(1999).each{
					e.sendMessage(it).queue()
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}text [effects] [text]`.").queue()
				400
			}
		}catch(ex){
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}text [effects] [text]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`text [effects] [text]` will make me add effects to the text.
The effects are space, reverse, super, bold, italic, compress, bubble, small, full, strike, random, emoji and fancy, so try them all."""
}


class ChatBoxCommand extends Command{
	List aliases=['chatbox','ascii']
	int limit=150
	def run(Map d,Event e){
		d.args=d.args.toLowerCase()
		String p=''
		int type=d.args.contains('compact')?1:0
		int offset=d.args.contains('wide')?18:0
		if(e.guild){
			Guild guild=e.guild
			User user=e.author
			Channel channel=e.channel
			String guildName=guild.name.cut(14)
			String channelInfo="#$channel.name | ${if(channel.topic){channel.topic}else{''}}".cut(40+offset)
			p+=" $guildName${' '*(14-guildName.length)} | $channelInfo${' '*((40+offset)-channelInfo.length)} \ud83d\udd14 \ud83d\udccc\n----------------|------------------------------------------------${"-"*offset}\n"
			List channels=['TEXT CHANNELS ']
			List tc=guild.textChannels.toList().sort{it.position}
			if(tc.size()>25)tc=tc[0..24]
			tc.each{
				String channelName="#$it.name".cut(14)
				channels+="$channelName${' '*(14-channelName.length)}"
			}
			channels+='              '
			channels+='VOICE CHANNELS'
			List vc=guild.voiceChannels.toList().sort{it.position}
			if(vc.size()>15)vc=vc[0..14]
			vc.each{
				String channelName="\ud83d\udd3d$it.name".cut(14)
				if(it.userLimit){
					String limit="${it.users.size()}/$it.userLimit"
					channelName="${"\ud83d\udd3d$it.name".cut(14-(limit.length+1))} $limit"
				}
				channels+="$channelName${' '*(14-channelName.length)}"
			}
			channels+='              '
			int height=channels.size
			List logs=channel.history.retrievePast(50).block().reverse()-e.message
			List messages=[]
			logs.each{Message m->
				String ampm='AM'
				if(m.createTime.format('H').toInteger()>12)ampm='PM'
				if(type){
					int index=m.index(logs)
					if(index&&logs[index-1].author.id!=m.author.id){
						if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){"BOT "}else{''}}${guild.membersMap[m.author.id]?.effectiveName?:m.author.name}: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}else{
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){"BOT "}else{''}}${guild.membersMap[m.author.id]?.effectiveName?:m.author.name}: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}
					}
				}else{
					if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
						messages+="${guild.membersMap[m.author.id]?.effectiveName?:m.author.name}${if(m.author.bot){" BOT"}else{''}} - Today at ${m.createTime.format('HH:mm')} $ampm".cut(46+offset)
					}else{
						messages+="${guild.membersMap[m.author.id]?.effectiveName?:m.author.name}${if(m.author.bot){" BOT"}else{''}} - ${m.createTime.format('dd/MM/YYYY')}".cut(46+offset)
					}
					messages+=m.content.replace('```','').tokenize('\n')*.split(46+offset)
				}
			}
			messages=messages.flatten()-null
			if(messages.size>height)messages=messages.reverse()[0..(height-1)].reverse()
			int index=0
			channels.each{
				p+=" $it | ${messages[index]?.trim()?:''}\n"
				index+=1
			}
			String clientName=user.name.cut(14)
			p+="----------------|\n $clientName${' '*(14-clientName.length)} | |\u00af\u00af|\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af${'\u00af'*offset}|\n #$user.discriminator  \ud83c\udfa4 \ud83c\udfa7 \u2699 | |__|_________________________________________${'_'*offset}|"
			p.split(1992).each{
				e.sendMessage("```\n $it```").queue()
			}
		}else{
			User user=e.author
			PrivateChannel channel=e.channel
			List aka=[]
			e.jda.guilds.findAll{user in it.users}.each{
				String nack=it.membersMap[user.id].nickname
				if(nack)aka+=nack
			}
			String channelInfo="@$channel.user.name | ${if(aka){"AKA ${aka.join(', ')}"}else{''}}".cut(40+offset)
			p+=" Find or start\u2026 | $channelInfo${' '*((40+offset)-channelInfo.length)} \ud83d\udcde \ud83d\udccc\n----------------|------------------------------------------------${"-"*offset}\n"
			List channels=['Friends       ','              ','DIRECT MESSAGE']
			List pc=e.jda.privateChannels.toList()
			if(pc.size()>30)pc=pc[0..29]
			pc.each{
				String channelName=it.user.name.cut(14)
				channels+="$channelName${' '*(14-channelName.length)}"
			}
			channels+='              '
			int height=channels.size
			List logs=channel.history.retrievePast(50).block().reverse()-e.message
			List messages=[]
			logs.each{Message m->
				String ampm='AM'
				if(m.createTime.format('H').toInteger()>12)ampm='PM'
				if(type){
					int index=m.index(logs)
					if(index&&logs[index-1].author.id!=m.author.id){
						if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){"BOT "}else{''}}$m.author.name: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}else{
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){"BOT "}else{''}}$m.author.name: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}
					}
				}else{
					if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
						messages+="$m.author.name${if(m.author.bot){" BOT"}else{''}} - Today at ${m.createTime.format('HH:mm')} $ampm".cut(46+offset)
					}else{
						messages+="$m.author.name${if(m.author.bot){" BOT"}else{''}} - ${m.createTime.format('dd/MM/YYYY')}".cut(46+offset)
					}
					messages+=m.content.replace('```','').tokenize('\n')*.split(46+offset)
				}
			}
			messages=messages.flatten()-null
			if(messages.size()>height)messages=messages.reverse()[0..(height-1)].reverse()
			int index=0
			channels.each{
				p+=" $it | ${messages[index]?.trim()?:''}\n"
				index+=1
			}
			String clientName=user.name.cut(14)
			p+="----------------|\n $clientName${' '*(14-clientName.length)} | |\u00af\u00af|\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af\u00af${'\u00af'*offset}|\n #$user.discriminator  \ud83c\udfa4 \ud83c\udfa7 \u2699 | |__|_________________________________________${'_'*offset}|"
			p.split(1991).each{
				e.sendMessage("```\n $it```").queue()
			}
		}
	}
	String category='General'
	String help="""`chatbox [effects]` will make me make an ASCII of the chat.
Optional added effects are compact and wide. No added sugar."""
}


class IdentifyCommand extends Command{
	List aliases=['identify','identity']
	def run(Map d,Event e){
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				try{
					String from=d.db[m.id].tags
					if(from.contains(','))from=from.substring(0,from.indexOf(','))
					String aka=d.db[m.id].aka
					ass+="**${m.name.capitalize()}**: $m.identity${if(aka){", $aka"}else{''}}, $from.\n"
				}catch(entry){
					ass+="**${m.name.capitalize()}**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			User user=e.author
			if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
			if(user){
				try{
					String aka=d.db[user.id].aka
					String mc=d.db[user.id].mc
					String also=d.db[user.id].also
					e.sendMessage("**${user.name.capitalize()}**'s identity is ${d.db[user.id].name}${if(aka){", $aka"}else{''}}${if(mc){" ($mc)"}else{''}}.\n${d.db[user.id].tags}${if(also){"\n${also.replaceAll(/<@(\d+)>/){full,id->"${try{e.jda.users.find{it.id==id}.name}catch(no){d.db[id].name}}"}}"}else{''}}").queue()
				}catch(entry){
					e.sendMessage("There is no information in my database for $user.name.").queue()
					404
				}
			}else{
				e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
				404
			}
		}
	}
	String category='Database'
	String help="""`identify [user]` will make me tell you who they really are, and a bit about them.
Who could forget the classic command?"""
}


class IrlCommand extends Command{
	List aliases=['irl','realname']
	def run(Map d,Event e){
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				try{
					String irl=d.db[m.id].irl
					ass+="**${m.identity.capitalize()}**: ${if(irl in['unknown','private']){"Real name $irl."}else{irl}}.\n"
				}catch(entry){
					ass+="**${m.identity.capitalize()}**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			User user=e.author
			if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
			if(user){
				try{
					String irl=d.db[user.id].irl
					e.sendMessage("**${user.identity.capitalize()}**'s real name is ${if(irl!='unknown'){irl}else{"not in my database"}}.").queue()
				}catch(entry){
					e.sendMessage("There is no information in my database for $user.name.").queue()
					404
				}
			}else{
				e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
				404
			}
		}
	}
	String category='Database'
	String help="""`irl [user]` will make me tell you the user's real name, if I know it.
We're all friendly here so I don't want to see any stalking."""
}


class AgeCommand extends Command{
	List aliases=['age','birthday']
	def run(Map d,Event e){
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				try{
					String age=d.db[m.id].age
					ass+="**${m.identity.capitalize()}**: ${if(age in['unknown','private']){"Age $irl."}else{irl}}.\n"
				}catch(entry){
					ass+="**${m.identity.capitalize()}**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			User user=e.author
			if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
			if(user){
				try{
					String age=d.db[user.id].age
					e.sendMessage("**${user.identity.capitalize()}**'s birthday is ${if(age!='unknown'){age}else{"not in my database"}}.").queue()
				}catch(entry){
					e.sendMessage("There is no information in my database for $user.name.").queue()
					404
				}
			}else{
				e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
				404
			}
		}
	}
	String category='Database'
	String help="""`age [user]` will make me tell you the user's birthday, if I know it.
What are the chances it'll be today?"""
}


class AreaCommand extends Command{
	List aliases=['area','location']
	def run(Map d,Event e){
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				try{
					String area=d.db[m.id].area
					ass+="**${m.identity.capitalize()}**: ${if(area in['unknown','private']){"Location $area."}else{area}}.\n"
				}catch(entry){
					ass+="**${m.identity.capitalize()}**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			User user=e.author
			if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
			if(user){
				try{
					String area=d.db[user.id].area
					e.sendMessage("**${user.identity.capitalize()}**'s location is ${if(area!='unknown'){(area.startsWith('Uni')?'the ':'')+area}else{"not in my database"}}.").queue()
				}catch(entry){
					e.sendMessage("There is no information in my database for $user.name.").queue()
					404
				}
			}else{
				e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
				404
			}
		}
	}
	String category='Database'
	String help="""`area [user]` will make me tell you where they live, if I know it.
Not down to the road name, though."""
}


class AltsCommand extends Command{
	List aliases=['alts']
	def run(Map d,Event e){
		User user=e.author
		if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			try{
				if(d.db[user.id].also){
					e.sendMessage(d.db[user.id].also.replaceAll(/<@(\d+)>/){full,id->"${try{e.jda.users.find{it.id==id}.name}catch(no){d.db[id].name}} ($id)"}.replace('   ','\n')).queue()
				}else{
					e.sendMessage("**${user.identity.capitalize()}** doesn't have any alternate accounts in my database.").queue()
				}
			}catch(entry){
				e.sendMessage("There is no information in my database for $user.name.").queue()
				404
			}
		}else{
			e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
			404
		}
	}
	String category='Database'
	String help="""`alts [user]` will make me tell you the alternate accounts of the user.
Oh man, it was really you all along!"""
}


class MinecraftCommand extends Command{
	List aliases=['mc','minecraft']
	def run(Map d,Event e){
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				try{
					String mc=d.db[user.id].mc
					ass+="**${m.identity.capitalize()}**: ${if(mc){mc}else{'None'}}.\n"
				}catch(entry){
					ass+="**${m.identity.capitalize()}**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			if(e.message.mentions){
				try{
					String mc=d.db[e.message.mentions[-1].id].mc
					e.sendMessage("**${e.message.mentions[-1].identity.capitalize()}**${if(mc){"'s Minecraft username is ${mc}.\nhttps://visage.surgeplay.com/full/512/${mc}.png"}else{" does not have a Minecraft account."}}").queue()
				}catch(entry){
					e.sendMessage("There is no information in my database for ${e.message.mentions[-1].name}.").queue()
					404
				}
			}else if(d.args){
				String mc=d.args.replace(' ','_')
				String owner=d.db.find{it.value.mc.toLowerCase()==mc.toLowerCase()}?.value?.name
				if(owner){
					e.sendMessage("Minecraft account $mc, owned by $owner:\nhttps://visage.surgeplay.com/full/512/${mc}.png").queue()
				}else{
					e.sendMessage("Minecraft account $mc:\nhttps://visage.surgeplay.com/full/512/${mc}.png").queue()
				}
			}else{
				try{
					String mc=d.db[e.author.id].mc
					e.sendMessage("**${e.author.identity.capitalize()}**${if(mc){"'s Minecraft username is ${mc}.\nhttps://visage.surgeplay.com/full/512/${mc}.png"}else{" does not have a Minecraft account."}}").queue()
				}catch(entry){
					e.sendMessage("There is no information in my database for $e.author.name.").queue()
					404
				}
			}
		}
	}
	String category='Database'
	String help="""`mc [username]` will make me get an avatar of the Minecraft account.
`mc [user]` will make me get an avatar of the user's Minecraft account, if I know it.
Beaconville functionality."""
}


class TimeCommand extends Command{
	List aliases=['time']
	def run(Map d,Event e){
		User ass
		if(e.guild&&d.args)ass=e.guild.findUser(d.args)
		if(!d.args||e.message.mentions){
			User user=e.message.mentions?e.message.mentions[-1]:e.author
			if(ass)user=ass
			if(d.db[user.id]){
				if(d.db[user.id].area=='private'){
					e.sendMessage("${user.identity.capitalize()}'s location is private.").queue()
				}else{
					String key=d.misc.time*.key.sort{it.length()}.find{d.db[user.id].area.endsWith(it)}
					if(key){
						Object zone=d.misc.time[key]
						e.sendMessage("The time for **$user.identity** is ${new Date((System.currentTimeMillis()+(zone*3600000)).toLong()).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (GMT${if(zone>0){"+$zone"}else if(zone<0){zone}else{''}}).").queue()
					}else{
						e.sendMessage("The time for **$user.identity** is unknown.").queue()
						404
					}
				}
			}else{
				e.sendMessage("There is no information in my database for $user.name.").queue()
				404
			}
		}else if(d.args.toLowerCase()=='earth'){
			e.sendMessage('<:mfw:259384457772007443>').queue()
			418
		}else{
			d.args=d.args.toLowerCase().replaceEach(['england','america'],['united kingdom','united states'])
			List keys=d.misc.time*.key.sort{it.length()}
			String key=keys.find{it.abbreviate().toLowerCase()==d.args}
			if(!key)key=keys.find{it.toLowerCase().endsWith(d.args)}?:keys.find{it.toLowerCase().startsWith(d.args)}
			if(key){
				Object zone=d.misc.time[key]
				e.sendMessage("The time in **${d.args.tokenize()*.capitalize().join(' ')}** is ${new Date((System.currentTimeMillis()+(zone*3600000)).toLong()).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (GMT${if(zone>0){"+$zone"}else if(zone<0){zone}else{''}}).").queue()
			}else{
				e.sendMessage("The location **${d.args.tokenize()*.capitalize().join(' ')}** is invalid.").queue()
				404
			}
		}
	}
	String category='Database'
	String help="""`time [area]` will make me tell you the time for that area.
`time [user]` will make me tell you the time for them.
I had it before BooBot."""
}


class SeenCommand extends Command{
	List aliases=['seen','lastseen']
	def run(Map d,Event e){
		User user=e.author
		if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			String address='he is'
			if(d.db[user.id]?.gender=='Female')address='she is'
			if(d.seen[user.id]){
				String area=d.db[e.author.id]?.area?:'United States'
				String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
				int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
				String status=e.jda.guilds*.members.flatten().find{it.user.id==user.id}.status
				if(d.seen[user.id].game){
					e.sendMessage("**${user.identity.capitalize()}** was last seen at ${new Date(d.seen[user.id].time+(zone*3600000)).format('HH:mm:ss, d MMMM YYYY').formatBirthday()} (${key.abbreviate()} time) playing ${d.seen[user.id].game}.\nCurrently, $address $status.").queue()
				}else{
					e.sendMessage("**${user.identity.capitalize()}** was last seen at ${new Date(d.seen[user.id].time+(zone*3600000)).format('HH:mm:ss, d MMMM YYYY').formatBirthday()} (${key.abbreviate()} time.)\nCurrently, $address $status.").queue()
				}
			}else{
				e.sendMessage("I have not seen $user.identity online.").queue()
				404
			}
		}else{
			e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
			404
		}
	}
	String category='General'
	String help="""`seen [user]` will make me tell you when I last saw them, and what they were doing.
But where are they now?"""
}


class EventsCommand extends Command{
	List aliases=['events']
	int limit=25
	Map specials=['25 December':'Christmas','31 October':'Halloween','1 January':'New Year','26 December':'Boxing Day']
	def run(Map d,Event e){
		e.sendTyping().queue()
		List eventsToday=[]
		if(d.args){
			try{
				int numberOfDays=d.args.toInteger()
				List eventsUpcoming=[]
				if(numberOfDays>70)numberOfDays=70
				if(numberOfDays<-70)numberOfDays=-70
				for(n in 1..numberOfDays){
					String upcomingDate=(new Date()+n).format('d MMMM')
					int upcomingYear=(new Date()+n).format('YYYY').toInteger()
					for(b in d.db.entrySet().findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
						if(b.value.age.rawBirthday().startsWith(upcomingDate)){
							int birthYear
							try{
								birthYear=b.value.age.rawBirthday().tokenize()[-1].toInteger()
							}catch(NotANumber){}
							if(!birthYear){
								eventsUpcoming+="\u2022 ${b.value.name.capitalize()}\'s birthday (${upcomingDate.formatBirthday()})"
							}else{
								eventsUpcoming+="\u2022 ${b.value.name.capitalize()}\'s ${((upcomingYear-birthYear).toString()+' ').formatBirthday()}birthday (${upcomingDate.formatBirthday()})"
							}
						}
					}
					for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==upcomingDate)eventsUpcoming+="\u2022 ${s.name.capitalize()}\'s ${((upcomingYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary (${upcomingDate.formatBirthday()})"
					if(specials[upcomingDate])eventsUpcoming+="\u2022 ${specials[upcomingDate]} (${upcomingDate.formatBirthday()})"
				}
				eventsUpcoming=eventsUpcoming.unique()
				e.sendMessage("**__Upcoming Events ($numberOfDays Days) ($eventsUpcoming.size)__:**\n${if(eventsUpcoming){eventsUpcoming.join('\n')}else{"Sure is boring around here."}}").queue()
			}catch(ex){
				String todaysDate=d.args.tokenize()*.toLowerCase()*.capitalize().join(' ').replaceAll(/\bJan\b/,'January').replaceAll(/\bFeb\b/,'February').replaceAll(/\bMar\b/,'March').replaceAll(/\bApr\b/,'April').replaceAll(/\bJun\b/,'June').replaceAll(/\bJul\b/,'July').replaceAll(/\bAug\b/,'August').replaceAll(/\bSep\b/,'September').replaceAll(/\bOct\b/,'October').replaceAll(/\bNov\b/,'November').replaceAll(/\bDec\b/,'December').rawBirthday()
				if(specials.find{it.value==todaysDate})todaysDate=specials.find{it.value==todaysDate}.key
				if(todaysDate.tokenize().size()>2)todaysDate=todaysDate.tokenize()[0..1].join(' ')
				try{
					if(todaysDate.tokenize()[0]==~/\D+/)todaysDate=todaysDate.tokenize().reverse().join(' ')
				}catch(ex2){
					
				}
				int todaysYear=new Date().format('YYYY').toInteger()
				try{
					if(d.args.tokenize()[-1].length()>2)todaysYear=d.args.tokenize()[-1].toInteger()
				}catch(ex2){
					
				}
				for(b in d.db.findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
					if(b.value.age.rawBirthday()=~/\b$todaysDate\b/){
						int birthYear
						try{
							birthYear=b.value.age.rawBirthday().tokenize()[-1].toInteger()
						}catch(NotANumber){}
						if(!birthYear){
							eventsToday+="\u2022 ${b.value.name.capitalize()}\'s birthday"
						}else{
							eventsToday+="\u2022 ${b.value.name.capitalize()}\'s ${((todaysYear-birthYear).toString()+' ').formatBirthday()}birthday"
						}
					}
					for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')=~/\b$todaysDate\b/)eventsToday+="\u2022 ${s.name.capitalize()}\'s ${((todaysYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary"
					if(specials.find{it.key=~/\b$todaysDate\b/})eventsToday+="\u2022 ${specials.find{it.key=~/\b$todaysDate\b/}.value}"
				}
				eventsToday=eventsToday.unique()
				e.sendMessage("**__Events for ${todaysDate.formatBirthday()} ($eventsToday.size)__:**\n${if(eventsToday){eventsToday.join('\n')}else{"Nothing interesting on this day."}}\n").queue()
			}
		}else{
			String todaysDate=new Date().format('d MMMM')
			int todaysYear=new Date().format('YYYY').toInteger()
			for(b in d.db.findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
				if(b.value.age.rawBirthday().startsWith(todaysDate)){
					int birthYear
					try{
						birthYear=b.value.age.rawBirthday().tokenize()[-1].toInteger()
					}catch(ex){
						
					}
					if(!birthYear){
						eventsToday+="\u2022 ${b.value.name.capitalize()}\'s birthday"
					}else{
						eventsToday+="\u2022 ${b.value.name.capitalize()}\'s ${((todaysYear-birthYear).toString()+' ').formatBirthday()}birthday"
					}
				}
				for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==todaysDate)eventsToday+="\u2022 ${s.name.capitalize()}\'s ${((todaysYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary"
				if(specials[todaysDate])eventsToday+="\u2022 ${specials[todaysDate]}"
			}
			eventsToday=eventsToday.unique()
			List eventsUpcoming=[]
			for(n in 1..7){
				String upcomingDate=(new Date()+n).format('d MMMM')
				int upcomingYear=(new Date()+n).format('YYYY').toInteger()
				for(b in d.db.findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
					if(b.value.age.rawBirthday().startsWith(upcomingDate)){
						int birthYear
						try{
							birthYear=b.value.age.rawBirthday().tokenize()[-1].toInteger()
						}catch(ex){
							
						}
						if(!birthYear){
							eventsUpcoming+="\u2022 ${b.value.name.capitalize()}\'s birthday (${upcomingDate.formatBirthday()})"
						}else{
							eventsUpcoming+="\u2022 ${b.value.name.capitalize()}\'s ${((upcomingYear-birthYear).toString()+' ').formatBirthday()}birthday (${upcomingDate.formatBirthday()})"
						}
					}
				}
				for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==upcomingDate)eventsUpcoming+="\u2022 ${s.name.capitalize()}\'s ${((upcomingYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary (${upcomingDate.formatBirthday()})"
				if(specials[upcomingDate])eventsUpcoming+="\u2022 ${specials[upcomingDate]} (${upcomingDate.formatBirthday()})"
			}
			eventsUpcoming=eventsUpcoming.unique()
			e.sendMessage("**__Events for ${todaysDate.formatBirthday()} (Today) ($eventsToday.size)__:**\n${if(eventsToday){eventsToday.join('\n')}else{"Nothing interesting today."}}\n**__Upcoming Events (7 Days) ($eventsUpcoming.size)__:**\n${if(eventsUpcoming){eventsUpcoming.join('\n')}else{"Sure is boring around here."}}").queue()
		}
	}
	String category='Database'
	String help="""`events` will make me tell you today's and upcoming events.
`events [number]` will make me tell you the events upcoming in those days.
`events [date]` will make me tell you the events for that day.
No-one ever remembers my birthday..."""
}


class ColourCommand extends Command{
	List aliases=['colour','color']
	int limit=50
	def run(Map d,Event e){
		if(e.guild){
			if(d.args){
				if(e.guild.selfMember.roles.any{'MANAGE_ROLES'in it.permissions*.toString()}){
					List options=["Enjoy your new colour! It looks good on you!","Done! How's it look?"]
					try{
						String colour=d.args.tokenize()[0].toLowerCase().replaceAll(/\W+/,'')
						if(d.colours[colour]){
							colour=d.colours[colour]
						}else if(colour=='random'){
							colour=''
							6.times{colour+=(('0'..'9')+('a'..'f')).randomItem()}
						}else if(colour.length()>6){
							colour=colour.substring(0,6)
						}else if(colour.length()<6){
							colour+="0"*(6-colour.length())
						}
						User user=e.author
						if(e.message.mentions&&user.isStaff(e.guild)){
							user=e.message.mentions[-1]
							options=["Enjoy your new colour, $user.identity! It looks good on you!","Done, $user.identity! How's it look?"]
						}
						Member member=e.guild.membersMap[user.id]
						Color hex=new Color(Integer.parseInt(colour,16))
						e.sendTyping().queue()
						String name="#${colour.toUpperCase()}"
						Role role=e.guild.roles.find{it.name.toLowerCase().replace('#','')==colour}
						if(!role){
							RoleManager manager=e.guild.controller.createRole().block().manager
							manager.setName(name).block()
							Thread.sleep(150)
							manager.setColor(hex).block()
							Thread.sleep(150)
							manager.setPermissions(0).block()
							role=manager.role
						}
						List old=e.guild.membersMap[user.id].roles.findAll{it.colour}
						e.guild.controller.addRolesToMember(member,[role]).block()
						List failed=[]
						old.each{
							try{
								Thread.sleep(150)
								e.guild.controller.removeRolesFromMember(member,it).block()
							}catch(perms){
								perms.printStackTrace()
								failed+=it
								old-=it
							}
						}
						String message=options.randomItem()
						if(old)message+="\n(Your previous colour was ${old*.asMention.join(' ')}.)"
						if(failed)message+="\n(Due to permissions, I couldn't remove ${failed*.asMention.join(' ')})"
						e.sendMessage(message).block()
						old.findAll{!(it.id in e.guild.members*.roles*.id.flatten())}.each{
							it.delete().block()
							Thread.sleep(150)
						}
					}catch(ex){
						e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}colour [hex/svg/random]`.").queue()
						ex.printStackTrace()
						400
					}
				}else{
					e.sendMessage("I need to be able to manage roles to do that...").queue()
					511
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}colour [hex/svg]/random`.").queue()
				400
			}
		}else{
			e.sendMessage("Colours cannot be given in Direct Messages.").queue()
			405
		}
	}
	String category='General'
	String help="""`colour [hex/svg]/random` will make me give you the colour using roles.
`colour [hex/svg]/random [user]` will make me give the user the colour. (Staff)
Let there be rainbows."""
}


class StatsCommand extends Command{
	List aliases=['stats']
	def run(Map d,Event e){
		List uptime=[0,(((System.currentTimeMillis()-d.started)/1000)/60)as int]
		(uptime[1]/60).times{
			uptime[0]+=1
			uptime[1]-=60
		}
		Runtime runtime=Runtime.runtime
		String stats="""Connected to ${if(e.guild?.id=='145904657833787392'){"`${e.jda.guilds.size()-1}` servers that are more important than Popcorn Plaza,"}else{"`${e.jda.guilds.size()}` servers with"}} `${e.jda.channels.size()}` channels and `${e.jda.users.size()}` users.
Total `${d.db*.key.size}` database entries, `${d.tags*.key.size}` tags and `${new File('images/xat').listFiles().size()+new File('images/cs').listFiles().size()}` smilies.
Online for `${uptime[0]}` hour${if(uptime[0]!=1){'s'}else{''}} and `${uptime[1]}` minute${if(uptime[1]!=1){'s'}else{''}}."""
		if(d.args.toLowerCase()=='full'){
			List os=[System.getProperty('os.name'),System.getProperty('os.version'),System.getProperty('os.arch'),System.getProperty('sun.os.patch.level')]
			String groove=GroovySystem.version
			String jave=System.getProperty('java.version').split('_')[0]
			stats+="""
Recieving `$e.responseNumber` messages over a `v6` doggy door.
I'm a Microsoft fanboy using ${os[0]} (${os[1]}) ${os[2]} ${os[3]}.
Remembering where I buried `${((runtime.totalMemory()-runtime.freeMemory())/1000000)as int}` out of `${(runtime.totalMemory()/1000000)as int}` bones.
DOSing `${d.feeds*.value.link.flatten().unique().size()}` different webpages for feed purposes. Sorry!
I have my coffee with `$jave` sugars which raises my groove to `$groove`.
I've successfully taken `${d.messages.size()}` command${if(d.messages.size()!=1){'s'}else{''}} this session. Woof!"""
		}
		e.sendMessage(stats).queue()
	}
	String category='General'
	String help="""`stats` will make me tell you some of my stats.
`stats full` will make me tell you some more of my stats.
I don't know what else you were expecting."""
}


class LoveCommand extends Command{
	List aliases=['love','ship']
	def run(Map d,Event e){
		d.args=d.args.split(/ and | & /)
		if(d.args.size()==2){
			2.times{
				if(d.args[it].length()>500)d.args[it]=d.args[it].substring(0,500)
			}
			int result=d.args[0].toCharArray().inject(d.args[1].toCharArray().inject(0){i,j->i+j}){i,j->i+j}%102
			if(result>100){
				e.sendMessage("**Wow!** ${d.args[0]} + ${d.args[1]} = :heartpulse:").queue()
			}else{
				e.sendMessage("${d.args[0]} and ${d.args[1]} are $result% compatible.").queue()
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}love [someone] & [someone]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`love [someone] & [someone]` will make me ship two people. Retains consistent results.
OTP! Like OMG!"""
}


class BallCommand extends Command{
	List aliases=['8ball','magicball']
	def run(Map d,Event e){
		if(d.args){
			if(d.args.length()>1000)d.args=d.args.substring(0,1000)+'...'
			String question=d.args.replaceAll(/\?$/,'').capitalize()
			String answer=[["It is certain"]*3,["It is decidedly so"]*3,["Without a doubt"]*3,["Yes, definitely"]*3,["You may rely on it"]*3,["As I see it, yes"]*3,["Most likely"]*3,["Outlook good"]*3,["Signs point to yes"]*3,"Reply hazy, try again","Ask again later","Better not tell you now","Concentrate and ask again",["Don't count on it"]*3,["My reply is no"]*3,["My sources say no"]*3,["Outlook not so good"]*3,["Very doubtful"]*3].flatten().randomItem()
			e.sendMessage("*$question?*\n$answer, $e.author.identity.").queue()
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}8ball [question]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`8ball [question]` will make me shake my magic 8ball and answer the question.
More likely to return an actual answer."""
}


class SetAvatarCommand extends Command{
	List aliases=['setavatar']
	int maximum=11
	int limit=50
	def run(Map d,Event e){
		d.args=d.args.toLowerCase()
		if(!d.args||(d.args=='random')||(d.args in(1..maximum)*.toString())){
			if(!d.args||(d.args=='random'))d.args=(1..maximum).randomItem()
			else d.args=d.args.toInteger()
			int old=d.info.avatar
			d.info.avatar=d.args
			d.json.save(d.info,'properties')
			e.jda.setAvatar("images/avatars/${d.args}.jpg").block()
			e.sendMessage("My avatar has been changed to $d.args. My previous one was $old.").queue()
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}setavatar [1..$maximum]/random`.").queue()
			400
		}
	}
	String category='General'
	String help="""`setavatar [1..9]/random` will make me change my avatar.
Just 10 to choose from now."""
}


class SetPrefixCommand extends Command{
	List aliases=['prefix','setprefix']
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				d.args=d.args.tokenize()
				d.settings.prefix[e.guild.id]=[]
				if(d.args){
					d.args.each{
						if(it==~/\w+/){
							d.settings.prefix[e.guild.id]+=["$it ",it]
						}else{
							d.settings.prefix[e.guild.id]+=it
						}
					}
					e.sendMessage("My prefix in this server is now `${d.args.join('`, `')}`.").queue()
				}else{
					d.settings.prefix[e.guild.id]=['']
					e.sendMessage("We're going unprefixed, baby.").queue()
				}
				d.json.save(d.settings,'settings')
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to set the prefix in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`setprefix [prefixes]` will make me set my prefix. Separate with spaces.
Finally, my bot can respond to !"Åí\$%^&*()_+help."""
}


class EvalCommand extends Command{
	List aliases=['eval']
	boolean dev=true
	def run(Map d,Event e){
		if(e.author.id==d.bot.owner){
			try{
				Binding binding=new Binding(d+[e:e])
				long startTime=System.currentTimeMillis()
				String eval=new GroovyShell(binding).evaluate(d.args.addImports()).toString()
				long stopTime=System.currentTimeMillis()
				println(eval)
				long startTime2=System.currentTimeMillis()
				e.sendMessage(eval).queue{
					long stopTime2=System.currentTimeMillis()
					it.edit("$it.rawContent\n`${stopTime-startTime}ms`, `${stopTime2-startTime2}ms`").queue()
				}
			}catch(ex){
				e.sendMessage("$ex").queue()
				ex.printStackTrace()
				500
			}
		}else{
			204
		}
	}
	String category='Developer'
	String help="""`eval [code]` will make me evaluate some Groovy code.
Not like you can use it though."""
}


class InspectCommand extends Command{
	List aliases=['inspect']
	boolean dev=true
	def run(Map d,Event e){
		if(e.author.id==d.bot.owner){
			try{
				Binding binding=new Binding(d+[e:e])
				Object object=new GroovyShell(binding).evaluate(d.args.addImports())
				if(object.getClass()!=Class)object=object.getClass()
				List classes=[object]
				while(object&&object.hasProperty('genericSuperclass')){
					object=object.getGenericSuperclass()
					if(object&&(object.getClass()!=Class))object=object.getClass()
					if(object)classes+=object
				}
				e.sendMessage(classes*.getName().join(' `extends` ')).queue()
			}catch(ex){
				e.sendMessage("$ex").queue()
				ex.printStackTrace()
				500
			}
		}else{
			204
		}
	}
	String category='Developer'
	String help="""`inspect [code]` will make me trace a class back to its origins.
Again, not like you can use it."""
}


class ConfigCommand extends Command{
	List aliases=['config']
	boolean dev=true
	def run(Map d,Event e){
		if(e.author.id==d.bot.owner){
			Guild ass=e.jda.guilds.find{it.id==d.args}?:e.guild
			e.sendMessage("""```css
Owners: ${ass.users.findAll{it.isOwner(ass)}*.identity.join(', ')}
Staff: ${(ass.users.findAll{it.isStaff(ass)}-ass.users.findAll{it.isOwner(ass)})*.identity.join(', ')}
Spam Channels: ${ass.channels.findAll{it.spam}*.name.join(', ')}
Log Channels: ${ass.channels.findAll{it.log}*.name.join(', ')}
NSFW Channels: ${ass.channels.findAll{it.nsfw}*.name.join(', ')}
Song Channels: ${ass.channels.findAll{it.song}*.name.join(', ')}
Ignored Channels: ${ass.channels.findAll{it.ignored}*.name.join(', ')}
Member Role: ${ass.roles.find{it.id==d.roles.member[ass.id]}?.name?:d.roles.member[ass.id]}
Mute Role: ${ass.roles.find{it.id==d.roles.mute[ass.id]}?.name?:d.roles.mute[ass.id]}```""").queue()
			e.sendMessage("""```css
Prefix: ${d.settings.prefix[ass.id]?.join(' ')}
Customs: ${(customs[ass.id]?:[])*.name}
Join: ${d.tracker.join[ass.id]}
Leave: ${d.tracker.leave[ass.id]}
xat Smilies: ${d.settings.smilies[ass.id]as boolean}
Vote Pin: ${d.settings.votepin[ass.id]?:3}```""").queue()
			e.sendMessage("""```css
${ass.textChannels.findAll{it.id in d.feeds*.value.flatten()*.channel}.collect{Channel mom->"Feeds (#$mom.name): ${d.feeds*.value.flatten().findAll{it.channel==mom.id}*.link.join(', ')}"}.join('\n')}```""").queue()
		}else{
			204
		}
	}
	String category='Developer'
	String help="""`config` will make me check the configuration of the server.
You still can't use it, of course."""
}


class WordCountCommand extends Command{
	List aliases=['wordcount','words']
	def run(Map d,Event e){
		String input=d.args
		if(e.message.attachment)input+=web.download(e.message.attachment.url,"temp/wordcount.txt")
		if(input){
			List words=input.replace('\r\n','\r').replace('\n\r','\n').replaceAll(['\r','\n','-','_','\u3000','\u30fc','\uff3f','\u00a1','?','!','\uff1f','\uff01','(',')','+','=',':',';','{','}','[',']','/','<','>','.',',','\u3002','\u3001'],' ').tokenize()
			List lines=input.replace('\r\n','\r').replace('\n\r','\n').tokenize('\r')*.tokenize('\n').flatten()
			String longestWord=words.join(' ').replaceAll(/\d+/,'').replaceAll(['"','*','\'','|'],' ').tokenize().sort{it.length()}.last()
			if(longestWord.length()>500)longestWord=longestWord.substring(0,500)+'...'
			e.sendMessage("""${words.size()} words
${lines.size()} lines (${lines.join('\n').replace(' ','').replace('\n\n','\n').replace('\n\n','\n').replace('\n\n','\n').tokenize('\n').size()} without empty)
${input.length()} length (${input.replaceAll([' ','-','_','\n','\r'],'').length()} without spaces)

Longest word: "$longestWord"
Longest line: line ${lines.indexOf(lines.sort{it.length()}.last())+1}""".replace('\n1 lines','\n1 line')).queue()
			new File("temp/wordcount.txt").delete()
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}wordcount [text/file]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`wordcount [text/file]` will make me give you some word statistics.
It won't evaluate your essay homework, though."""
}


class MemberCommand extends Command{
	List aliases=['member','guest']
	int limit=25
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isStaff(e.guild)){
				if(e.guild.selfMember.roles.any{'MANAGE_ROLES'in it.permissions*.toString()}){
					if(d.args.containsAny(['everyone','here'])){
						e.sendMessage("You may not make everyone in the server member.")
					}else{
						List users=e.message.mentions?:[e.guild.findUser(d.args)]
						if(!users)users=[e.guild.members.toList().sort{it.joinDate}[-1].user]
						try{
							String id=d.roles.member[e.guild.id]
							users.each{User user->
								Member member=e.guild.membersMap[user.id]
								if(id in member.roles*.id){
									e.guild.controller.removeRolesFromMember(member,[e.guild.roles.find{it.id==id}]).queue()
									e.sendMessage("${user.identity.capitalize()} is now a guest.").queue()
								}else{
									e.guild.controller.addRolesToMember(member,[e.guild.roles.find{it.id==id}])
									e.sendMessage("${user.identity.capitalize()} is now a member.").queue()
								}
								boolean type=(id in member.roles*.id)
								e.guild.textChannels.findAll{it.log}*.sendMessage("**${e.author.identity.capitalize()}**: ${if(type){"Promoted"}else{"Demoted"}} $user.identity to ${if(type){"member"}else{"guest"}}.")*.queue()
							}
						}catch(ex){
							e.sendMessage("This server doesn't seem to have a suitable member role.\nStaff can set a member role with `{d.prefix}setrole member`.").queue()
							ex.printStackTrace()
							404
						}
					}
				}else{
					e.sendMessage("I need to be able to manage roles to do that...").queue()
					511
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Staff (Trainer/MANAGE_MESSAGES)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to give or revoke membership in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`member [user]` will make me give or revoke the member role to/from them.
There was no help for this one. Now there is."""
}


class MuteCommand extends Command{
	List aliases=['mute','shun']
	int limit=25
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isStaff(e.guild)){
				if(e.guild.selfMember.roles.any{'MANAGE_ROLES'in it.permissions*.toString()}){
					d.args=d.args.tokenize()
						User user=e.guild.members.toList().sort{it.joinDate}[-1].user
						if(e.message.mentions){
							user=e.message.mentions[-1]
						}else if(d.args[0]){
							User ass=e.guild.findUser(d.args[0])
							if(ass)user=ass
						}
						Member member=e.guild.membersMap[user.id]
						String reason
						int offset=user.name.tokenize().size()
						try{
							reason=d.args[2-offset..-1].join(' ').trim()?:''
						}catch(alt){
							
						}
						long time=0
						try{
							if(d.args[1-offset]=~/\d+\w/){
								time=d.args[1-offset].formatTime()
							}else{
								try{
									reason=d.args[1-offset..-1].join(' ').trim()
								}catch(alt){
									
								}
							}
							String id
							try{
								id=d.roles.mute[e.guild.id]
								if(reason.length()>1500)reason=reason.substring(0,1500)+'...'
								if(id in member.roles*.id){
									e.guild.controller.removeRolesFromMember(member,[e.guild.roles.find{it.id==id}]).queue()
									e.sendMessage("${user.identity.capitalize()} is no longer muted.").queue()
									if(!d.temp.mutes[e.guild.id])d.temp.mutes[e.guild.id]=[:]
									d.temp.mutes[e.guild.id].remove(user.id)
								}else{
									e.guild.controller.addRolesToMember(member,[e.guild.roles.find{it.id==id}]).queue()
									e.sendMessage("${user.identity.capitalize()} is now muted.").queue()
									if(!d.temp.mutes[e.guild.id])d.temp.mutes[e.guild.id]=[:]
									d.temp.mutes[e.guild.id][user.id]=[
										start:System.currentTimeMillis(),
										end:time,
										commander:e.author.id,
										reason:reason
									]
								}
							}catch(ex2){
								e.sendMessage("This server doesn't seem to have a suitable mute role.\nStaff can set a mute role with `{d.prefix}setrole mute`.").queue()
								ex2.printStackTrace()
							}
							boolean type=(id in e.guild.membersMap[user.id].roles*.id)
							e.guild.textChannels.findAll{it.log}.each{
								if(type){
									it.sendMessage("**${e.author.identity.capitalize()}**: Unmuted $user.identity${if(reason){".\nReason: $reason"}else{" for no reason."}}").queue()
								}else{
									it.sendMessage("**${e.author.identity.capitalize()}**: Muted $user.identity ${if(time){"until ${new Date(time).format('HH:mm:ss, dd MMMM YYYY').formatBirthday()}"}else{"forever"}}${if(reason){".\nReason: $reason"}else{" for no reason."}}").queue()
								}
							}
							d.json.save(d.temp,'temp')
						}catch(ex){
							e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}mute [user] [time] [reason]`.").queue()
							ex.printStackTrace()
							400
						}
					}else{
						e.sendMessage("I need to be able to manage roles to do that...").queue()
						511
					}
				}else{
					e.sendMessage(d.permissionMessage()+"Required: `Staff (Trainer/MANAGE_MESSAGES)`.").queue()
					403
				}
		}else{
			e.sendMessage("No need to mute or unmute in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`mute [user] [time] [reason]` will make me mute or unmute the user for that time.
All parameters optional. Go all inclusive for the best results though."""
}


class KickCommand extends Command{
	List aliases=['kick']
	int limit=25
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isStaff(e.guild)){
				if(e.guild.selfMember.roles.any{'KICK_MEMBERS'in it.permissions*.toString()}){
					d.args=[d.args.tokenize(),'',''].flatten()
					d.args[0]=d.args[0].toLowerCase()
					User user=e.guild.members.toList().sort{it.joinDate}[-1].user
					if(e.message.mentions){
						user=e.message.mentions[-1]
					}else if(d.args){
						User ass=e.guild.findUser(d.args[0])
						if(ass)user=ass
					}
					Member member=e.guild.membersMap[user.id]
					if(member.owner){
						e.sendMessage("I can't kick the owner of the server.").queue()
						511
					}else if(user.id==e.jda.selfUser.id){
						e.sendMessage('...Nah.').queue()
						511
					}else{
						int offset=user.name.tokenize().size()
						String reason=d.args[offset..-1].join(' ').trim()
						if(reason.length()>1500)reason=reason.substring(0,1500)+'...'
						e.guild.controller.kick(member)
						e.sendMessage("I have kicked $user.identity.").queue()
						e.guild.textChannels.findAll{it.log}*.sendMessage("**${e.author.identity.capitalize()}**: Kicked $user.identity${if(reason){".\nReason: $reason"}else{" for no reason."}}")*.queue()
					}
				}else{
					e.sendMessage("I need to be able to kick to do that...").queue()
					511
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Staff (Trainer/MANAGE_MESSAGES)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to kick in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`kick [user] [reason]` will make me kick the user.
Remember to give them an invite if you want them to come back."""
}


class LogCommand extends Command{
	List aliases=['log','archive']
	int limit=100
	def run(Map d,Event e){
		e.sendTyping().queue()
		int size=50
		d.args=d.args.tokenize()+''
		String arg=d.args[-1]
		if(arg==~/\d+/)size=arg.toInteger()
		if(size>100)size=100
		if(size<2)size=2
		String log="${new Date().format('d MMMM YYYY').formatBirthday()}, #${if(e.guild){e.channel.name}else{e.author.name}} in ${try{e.guild.name}catch(DM){'Direct Messages'}}:\r\n"
		List logs=e.channel.history.retrievePast(size).block().reverse()-e.message
		for(l in logs)log+="\r\n[${l.createTime.format('HH:mm:ss')}] [$l.author.identity]: ${l.content.replace('\r\n','\n').replace('\r','\r\n  ').replace('\n','\r\n  ').replace('\u200b','')}${if(l.attachments){"${if(l.content){"\r\n"}else{''}}${l.attachments*.name}"}else{''}}"
		e.sendMessage(Unirest.post('https://puush.me/api/up').field('k',new File('token').readLines()[5]).field('z','dogbot').field('f',log.bytes,"archive-${e.author.id}.log").asString().body.split(',')[1]).queue()
	}
	String category='General'
	String help="""`log [number]` will make me generate a log of the channel history to up to 100 messages ago.
It's too late to take back what you said."""
}


class ScopeCommand extends Command{
	List aliases=['scope','online']
	int limit=25
	def run(Map d,Event e){
		Map emotes=[online:':o:212789758110334977',idle:':i:212789859071426561',do_not_disturb:':d:236744731088912384',offline:':o:212790005943369728',unknown:':u:213672875973017600']
		if(e.guild){
			List used=[]
			def ass=''
			List roles=e.guild.roles.findAll{it.hoisted}
			List base=e.guild.members.findAll{it.status!='offline'}.findAll{!it.user.bot}.findAll{it.user.rawIdentity}
			roles.each{Role r->
				List users=base.findAll{r in it.roles}.findAll{!(it.user.id in used)}.toList().sort{it.effectiveName}
				if(users){
					ass+="**$r.name**:\n"
					int pos=0
					users.each{Member u->
						used+=u.user.id
						String alias=u.effectiveName
						String abbrev=alias.replaceAll([' ','-','_','\'','"','(',')','[',']','{','}','<','>','|','.','/',':',';'],'')?:alias
						String name="$u.user.identity ($abbrev)"
						if(name.length()>26)name=name.substring(0,24)+'\u2026'
						ass+="<${emotes[u.status]}> `$name${' '*(26-name.length())}\u200b`"
						ass+=pos?'\n':' '
						pos=pos?0:1
					}
					ass+="\n"
				}
			}
			List users=e.guild.members.findAll{it.status!="offline"}.findAll{!it.user.bot}.findAll{it.user.rawIdentity}.findAll{!(it.user.id in used)}.toList().sort{it.effectiveName}
			if(users){
				ass+="**${e.guild.name.capitalize()}**:\n"
				int pos=0
				users.each{Member u->
					used+=u.user.id
					String alias=u.effectiveName
					String abbrev=alias.replaceAll([' ','-','_','\'','"','(',')','[',']','{','}','<','>','|','.'],'')?:alias
					String name="$u.user.identity ($abbrev)"
					if(name.length()>26)name=name.substring(0,24)+'\u2026'
					ass+="<${emotes[u.status]}> `$name${' '*(26-name.length())}\u200b`"
					ass+=pos?'\n':' '
					pos=pos?0:1
				}
				ass+="\n"
			}
			if(!ass)ass="It would appear that I don't actually know anyone here."
			ass=ass.replace('\n\n','\n').split(1999)
			if(ass.size()>1){
				String split=ass.substring(ass.lastIndexOf('\n'))
				ass[0]-=split
				ass[1]=split+ass[1]
			}
			if(ass.size()>2){
				e.sendMessage("This server has way too many members to scope...")
				400
			}else{
				ass.each{
					e.sendMessage(it).queue()
					Thread.sleep(150)
				}
			}
		}else{
			if(e.author.rawIdentity){
				String ass="**Direct Messages**:\n"
				String abbrev=e.author.name.replaceAll([' ','-','_','\'','"','(',')','[',']','{','}','|','.'],'')
				String name="$e.author.identity ($abbrev)"
				if(name.length()>26)name=name.substring(0,24)+"\u2026"
				ass+="<${emotes[e.jda.guilds*.members.flatten().find{it.user.id==e.author.id}.status]}> `$name${' '*(26-name.length())}\u200b` "
				e.sendMessage(ass).queue()
			}else{
				e.sendMessage("It would appear that I don't actually know you.").queue()
				500
			}
		}
	}
	String category='Database'
	String help="""`scope` will make me identify everyone who's online.
It's like the Silph Scope but it doesn't work on ghosts."""
}


class FeedCommand extends Command{
	List aliases=['feed','feeds']
	def run(Map d,Event e){
		if(!e.guild||e.author.isStaff(e.guild)){
			List feeds=(d.feeds.youtube+d.feeds.animelist+d.feeds.twitter+d.feeds.levelpalace).findAll{it.channel==e.channel.id}
			if(d.args.toLowerCase()=='list'){
				if(feeds){
					String fed=feeds*.link.join('>\n<').replace('&client=dogbot','').replace('rss.php?type=rw&u=','animelist/')
					e.sendMessage("**Feeds for #${e.guild?e.channel.name:e.channel.user.name}**:\n<$fed>\n\nFeeds are updated every half an hour.").queue()
				}else{
					e.sendMessage("**Feeds for #${e.guild?e.channel.name:e.channel.user.name}**:\nNo feeds, add some!").queue()
				}
			}else if(d.args.toLowerCase()=='check'){
				e.sendTyping().queue()
				List list=[]
				d.feeds.youtube.findAll{it.channel==e.channel.id}.each{Map feed->
					Document doc=d.web.get(feed.link,'G.Chrome')
					String id=doc.getElementsByClass('yt-lockup-title')[0].getElementsByTag('a')[0].attr('href')
					if(id!=feed.last){
						String title=doc.getElementsByTag('title').text().tokenize().join(' ')
						list+="**New video from $title**:\nhttps://www.youtube.com$id"
						d.feeds.youtube.find{(it.link==feed.link)&&(it.channel==e.channel.id)}.last=id
					}
				}
				d.feeds.animelist.findAll{it.channel==e.channel.id}.each{Map feed->
					Document doc=d.web.get(feed.link,'G.Chrome')
					Element anime=doc.getElementsByTag('item')[0]
					List data=anime.getElementsByTag('description')[0].text().replace(' episodes','').split(' - ')
					String name=anime.getElementsByTag('title')[0].text().split(' - ')[0]
					String id="$name/${data[1].tokenize()[0]}"
					if(id!=feed.last){
						String title=doc.getElementsByTag('title')[0].text().tokenize()[0]
						String link=anime.getElementsByTag('link')[0].text()
						list+="**New episode on $title anime list**:\n${data[0]}: Episode ${data[1]} of $name.\n<$link>"
						d.feeds.animelist.find{(it.link==feed.link)&&(it.channel==e.channel.id)}.last=id
					}
				}
				d.feeds.twitter.findAll{it.channel==e.channel.id}.each{Map feed->
					Document doc=d.web.get(feed.link,'G.Chrome')
					String link=doc.getElementsByClass('tweet-timestamp')[0].attr('href')
					String id=link.substring(link.lastIndexOf('/'))
					if(id!=feed.last){
						String title=doc.getElementsByClass('ProfileHeaderCard-nameLink').text()
						list+="**New tweet from $title**:\nhttps://twitter.com$link"
						d.feeds.twitter.find{(it.link==feed.link)&&(it.channel==e.channel.id)}.last=id
					}
				}
				d.feeds.levelpalace.findAll{it.channel==e.channel.id}.each{Map feed->
					Document doc=d.web.get(feed.link,'G.Chrome')
					Elements level=doc.getElementsByClass('levels-table')[0].getElementsByTag('a')
					String id=level[0].attr('href')
					if(id!=feed.last){
						String title=level[1].text()
						String name=level[0].text()
						list+="**New level from $title**:\n$name.\n<https://levelpalace.com/$id>"
						d.feeds.levelpalace.find{(it.link==feed.link)&&(it.channel==e.channel.id)}.last=id
					}
				}
				if(list){
					e.sendMessage(list.join('\n')).queue()
					d.json.save(d.feeds,'feeds')
				}else{
					e.sendMessage("All up-to-date for this channel.").queue()
				}
			}else if(feeds.size()>=15){
				e.sendMessage("You've hit the feed limit for this channel. Please consider removing a feed.").queue()
				403
			}else if(d.args.contains('youtube.com')){
				try{
					String link=d.args
					if(!link.startsWith('http'))link="https://$link"
					if(!link.endsWith('/videos'))link+='/videos'
					if(link in feeds*.link){
						d.feeds.youtube-=feeds.find{(it.link==link)&&(it.channel==e.channel.id)}
						e.sendMessage("YouTube channel removed from the feed for this channel.").queue()
					}else{
						e.sendTyping().queue()
						Document doc=d.web.get(link,'G.Chrome')
						String id=doc.getElementsByClass('yt-lockup-title')[0].getElementsByTag('a')[0].attr('href')
						d.feeds.youtube+=[
							channel:e.channel.id,
							link:link,
							last:id
						]
						e.sendMessage("YouTube channel added to the feed for this channel.").queue()
					}
					d.json.save(d.feeds,'feeds')
				}catch(ex){
					e.sendMessage("Malformed YouTube channel. Make sure the link leads to a channel and that at least one video has been uploaded.").queue()
					404
				}
			}else if(d.args.contains('myanimelist.net')){
				try{
					String link=d.args.replaceAny(['animelist/','profile/'],'rss.php?type=rw&u=')
					if(!link.startsWith('http'))link="https://$link"
					if(link in feeds*.link){
						d.feeds.animelist-=feeds.find{(it.link==link)&&(it.channel==e.channel.id)}
						e.sendMessage("Anime list removed from the feed for this channel.").queue()
					}else{
						e.sendTyping().queue()
						Document doc=d.web.get(link,'G.Chrome')
						Element anime=doc.getElementsByTag('item')[0]
						List data=anime.getElementsByTag('description')[0].text().replace(' episodes','').split(' - ')
						String name=anime.getElementsByTag('title')[0].text().split(' - ')[0]
						String id="$name/${data[1].tokenize()[0]}"
						d.feeds.animelist+=[
							channel:e.channel.id,
							link:link,
							last:id
						]
						e.sendMessage("Anime list added to the feed for this channel.").queue()
					}
					d.json.save(d.feeds,'feeds')
				}catch(ex){
					e.sendMessage("Malformed MyAnimeList profile. Make sure the link leads to a profile and that at least one episode has been watched.").queue()
					404
				}
			}else if(d.args.contains('twitter.com')){
				try{
					String link=d.args
					if(!link.startsWith('http'))link="https://$link"
					if(link in feeds*.link){
						d.feeds.twitter-=feeds.find{(it.link==link)&&(it.channel==e.channel.id)}
						e.sendMessage("Twitter handle removed from the feed for this channel.").queue()
					}else{
						e.sendTyping().queue()
						Document doc=d.web.get(link,'G.Chrome')
						String stamp=doc.getElementsByClass('tweet-timestamp')[0].attr('href')
						String id=stamp.substring(stamp.lastIndexOf('/'))
						d.feeds.twitter+=[
							channel:e.channel.id,
							link:link,
							last:id
						]
						e.sendMessage("Twitter handle added to the feed for this channel.").queue()
					}
					d.json.save(d.feeds,'feeds')
				}catch(ex){
					e.sendMessage("Malformed Twitter handle. Make sure the link leads to a handle and that at least one tweet has been posted.").queue()
					404
				}
			}else if(d.args.contains('levelpalace.com')){
				try{
					String link=d.args
					if(!link.startsWith('http'))link="https://$link"
					if(!link.endsWith('&client=dogbot'))link+='&client=dogbot'
					if(link in feeds*.link){
						d.feeds.levelpalace-=feeds.find{(it.link==link)&&(it.channel==e.channel.id)}
						e.sendMessage("Level Palace account removed from the feed for this channel.").queue()
					}else{
						e.sendTyping().queue()
						Document doc=d.web.get(link,'G.Chrome')
						String id=doc.getElementsByClass('levels-table')[0].getElementsByTag('a')[0].attr('href')
						d.feeds.levelpalace+=[
							channel:e.channel.id,
							link:link,
							last:id
						]
						e.sendMessage("Level Palace account added to the feed for this channel.").queue()
					}
					d.json.save(d.feeds,'feeds')
				}catch(ex){
					e.sendMessage("Malformed Level Palace account. Make sure the link leads to a level list and that at least one level has been posted.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}feed [url]/list/check`.").queue()
				400
			}
		}else{
			e.sendMessage(d.permissionMessage()+"Required: `Staff (Trainer/MANAGE_MESSAGES)`.").queue()
			403
		}
	}
	String category='Online'
	String help="""`feed [url]` will make me add or remove that feed from the list for this channel.
`feed list` will make me tell you what feeds this channel is listening to.
`feed check` will make me check the feeds for this channel now.
You can feed into YouTube, MyAnimeList, Level Palace and Twitter. Isn't that neat?"""
}


class ClearCommand extends Command{
	List aliases=['clear','prune']
	int limit=25
	def run(Map d,Event e){
		List users=e.message.mentions
		if(!e.guild||e.author.isStaff(e.guild)||((users*.id==[e.author.id])&&e.author.isMember(e.guild))){
			if(!e.guild||e.guild.selfMember.roles.any{'MESSAGE_MANAGE'in it.permissions*.toString()}||(users==[e.jda.selfUser])){
				e.sendTyping().queue()
				List numbers=d.args.findAll(/\d+/)
				long number=numbers?numbers[-1].toLong():50
				if(number<1)number=1
				if(number>99)number=99
				List messages=e.channel.history.retrievePast((number+1).toInteger()).block()-e.message
				if(d.args){
					e.message.mentions.each{User ass->
						messages=messages.findAll{it.author.id==ass.id}
					}
					if(d.args.contains('bot'))messages=messages.findAll{it.author.bot}
					if(d.args.contains('link'))messages=messages.findAll{it.content=~/\w+:\/\//}
					if(d.args.contains('file'))messages=messages.findAll{it.attachments}
					if(d.args.contains('embed'))messages=messages.findAll{it.embeds}
					if(d.args.contains('command')&&e.guild){
						List prefixes=e.guild.users.findAll{it.bot}.findAll{d.db[it.id]}.collect{d.db[it.id].tags.range('(',')')}
						messages=messages.findAll{it.content.startsWithAny(prefixes)}
					}
				}
				if(users)messages=messages.findAll{it.author.id in users*.id}
				if(e.guild){
					if(messages.size()>1){
						try{
							e.channel.deleteMessages(messages).queue()
						}catch(ex){
							messages.each{
								it.delete().queue()
								Thread.sleep(150)
							}
						}
					}else{
						messages.each{
							it.delete().queue()
							Thread.sleep(150)
						}
					}
				}else{
					messages.findAll{it.author.id==e.jda.selfUser.id}*.delete()*.queue()
				}
				e.sendMessage("Cleared ${messages.size()} message${if(messages.size()!=1){'s'}else{''}}.").queue()
			}else{
				e.sendMessage("I need to be able to manage messages to do that...").queue()
				511
			}
		}else{
			e.sendMessage(d.permissionMessage()+"Required: `Staff (Trainer/MANAGE_MESSAGES)`, `Use on self with Member (any role)`.").queue()
			400
		}
	}
	String category='Moderation'
	String help="""`clear [arguments]` will make me clear the chat.
Tick all that apply: [@mention (delete from)], [number (to delete)], bot, link, file, embed, command. Tick none to nuke the chat."""
}


class SetChannelCommand extends Command{
	List aliases=['setchannel','setproperty']
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				d.args=d.args.toLowerCase().tokenize()
				Channel channel
				if(d.args[1]){
					channel=e.guild.findChannel(d.args[1..-1].join(' '))
					if(e.message.mentionedChannels)channel=e.message.mentionedChannels[0]
				}
				if(!channel)channel=e.channel
				if(d.args[0]in['spam','testing']){
					d.channels.spam[channel.id]=channel.spam?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.spam){"now"}else{"no longer"}} a spam channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['log','report']){
					d.channels.log[channel.id]=channel.log?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.log){"now"}else{"no longer"}} a log channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['nsfw','porn']){
					d.channels.nsfw[channel.id]=channel.nsfw?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.nsfw){"now"}else{"no longer"}} an NSFW channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['song','music']){
					d.channels.song[channel.id]=channel.song?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.song){"now"}else{"no longer"}} a song channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['ignored','ignore']){
					if(e.guild.textChannels.findAll{it.ignored}.size()<(e.guild.textChannels.size()-1)){
						d.channels.ignored[channel.id]=channel.ignored?false:true
						e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.ignored){"now"}else{"no longer"}} an ignored channel.").queue()
						d.json.save(d.channels,'channels')
					}else{
						e.sendMessage("But how will I unignore it?").queue()
						400
					}
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}setchannel spam/log/nsfw/song/ignored [channel]`").queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to set channel properties in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`setchannel spam [channel]` will make me set the channel as spam, allowing automatic responses.
`setchannel log [channel]` will make me set the channel as a log, making me log staff actions there.
`setchannel nsfw [channel]` will make me set the channel as NSFW, so relevant commands can be used.
`setchannel song [channel]` will make me set the channel as song, so relevant commands can be used.
`setchannel ignored [channel]` will make me set the channel as ignored... Ignoring it.
These settings are probably already correct, but just to be safe..."""
}


class SetRoleCommand extends Command{
	List aliases=['setrole','setroles']
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				d.args=d.args.toLowerCase().tokenize()
				Role role
				if(d.args[1]){
					role=e.guild.findRole(d.args[1..-1].join(' '))
					if(e.message.mentionedRoles)role=e.message.mentionedRoles[0]
				}
				if(!role&&d.args){
					e.sendMessage("I couldn't find a role like that.").queue()
					404
				}else if(d.args[0]in['member','user']){
					d.roles.member[e.guild.id]=role.id
					e.sendMessage("**${role.name.capitalize()}** is now this server's member role.").queue()
					d.json.save(d.roles,'roles')
				}else if(d.args[0]in['mute','muted']){
					d.roles.mute[e.guild.id]=role.id
					e.sendMessage("**${role.name.capitalize()}** is now this server's mute role.").queue()
					d.json.save(d.roles,'roles')
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}setrole member/mute [role]`").queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to set roles in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`setchannel member [role]` will make me set the member role for this server.
`setchannel mute [role]` will make me set the mute role for this server.
These settings are possibly already correct, but just to be safe..."""
}


class VotePinCommand extends Command{
	List aliases=['votepin','vp']
	int limit=25
	Map votes=[:]
	def run(Map d,Event e){
		if(d.args){
			if(e.guild){
				String arg=d.args.tokenize()[0]
				if(e.author.isMember(e.guild)){
					if(!e.guild||e.guild.selfMember.roles.any{'MESSAGE_MANAGE'in it.permissions*.toString()}||e.author.isOwner(e.guild)&&(arg=='max')){
						if((arg=='max')&&e.author.isOwner(e.guild)){
							try{
								int max=d.args.substring(4).toInteger()
								if(max<1)max=1
								d.settings.votepin[e.guild.id]=max
								d.json.save(d.settings,'settings')
								e.sendMessage("The number of votes needed to pin a message has been changed to $max.").queue()
							}catch(ex){
								e.sendMessage("Please enter a number.").queue()
								400
							}
						}else{
							if(e.author.rawIdentity.endsWithAny(['\'s Incognito','\'s Alternate Account'])){
								e.sendMessage("Incognito can't vote.").queue()
								403
							}else{
								try{
									Message message
									if(d.args==~/\d+/){
										message=e.channel.getMessageById(d.args).block()
									}else{
										List logs=e.channel.history.retrievePast(100).block().findAll{!it.content.startsWithAny(d.bot.prefixes*.plus('v'))}
										message=logs.find{it.content.toLowerCase().contains(d.args.toLowerCase())}
									}
									if(message){
										int max=d.settings.votepin[e.guild.id]?:3
										if(message.pinned){
											e.sendMessage("That message is already pinned.").queue()
											511
										}else if(e.author.id in votes[message.id]){
											votes[message.id]-=e.author.id
											e.sendMessage("Unvoted to pin $message.author.identity's message. (${votes[message.id].size()}/$max)").queue()
										}else if(message.author.id==e.author.id){
											e.sendMessage("Wow, shameless self-promotion.").queue()
											403
										}else{
											if(!votes[message.id])votes[message.id]=[]
											votes[message.id]+=e.author.id
											if(votes[message.id].size()>=max){
												message.pin().queue()
											}else{
												e.sendMessage("Voted to pin $message.author.identity's message. (${votes[message.id].size()}/$max)").queue()
											}
										}
									}else{
										e.sendMessage("I couldn't find a message like that in the last 100 messages. Use IDs to fetch any message in the channel.").queue()
										404
									}
								}catch(ex){
									e.sendMessage("Your channel has 50 pins. I am unable to pin a message now.").queue()
									511
								}
							}
						}
					}else{
						e.sendMessage("I need to be able to manage messages to do that...").queue()
						511
					}
				}else{
					e.sendMessage(d.permissionMessage()+"Required: `Member (any role)`.").queue()
					403
				}
			}else{
				Message message
				if(d.args==~/\d+/){
					message=e.channel.getMessageById(d.args).block()
				}else{
					List logs=e.channel.history.retrievePast(100).block().findAll{!it.content.startsWithAny(d.bot.prefixes*.plus('v'))}
					message=logs.find{it.content.toLowerCase().contains(d.args.toLowerCase())}
				}
				if(message){
					try{
						if(message.pinned){
							e.sendMessage("That message is already pinned.").queue()
							511
						}else{
							message.pin().queue()
						}
					}catch(ex){
						e.sendMessage("Your channel has 50 pins. I am unable to pin a message now.").queue()
						511
					}
				}else{
					e.sendMessage("I couldn't find a message like that in the last 100 messages. Use IDs to fetch any message in the channel.").queue()
					404
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}votepin [id/content]/max [number]`").queue()
			400
		}
	}
	String category='General'
	String help="""`votepin [id/content]` will make me find a message and vote to pin it.
`votepin max [number]` will make me set how many votes are required to pin a message.
No more asking the staff to 'pin this.'"""
}


class SingCommand extends Command{
	List aliases=['sing','song']
	int limit=50
	boolean covered
	boolean singing
	Element lyricsLink
	String author
	String coverLink
	MessageChannel venue
	String nick
	String starter
	def run(Map d,Event e){
		d.args=d.args.toLowerCase()
		if(singing&&d.args=='stop'){
			if(e.channel.id==venue.id){
				if(!e.guild||e.author.isOwner(e.guild)||e.channel.song&&(e.author.id==starter)){
					e.jda.setAvatar("images/avatars/${d.info.avatar}.jpg").queue()
					if(e.guild)e.guild.controller.setNickname(e.guild.selfMember,nick).queue()
					new File('images/album.jpg').delete()
					singing=false
					venue=null
					coverLink=null
					e.sendMessage("The song has been cancelled. Sorry folks.").queue()
				}else{
					TextChannel songChannel=e.guild.textChannels.find{it.song}
					e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`${if(songChannel){", `Use in #$songChannel.name and be the starter of the song`"}else{''}}.\nStaff can set song channels with `${d.prefix}setchannel song`.").queue()
					403
				}
			}else{
				if(e.guild.id==venue.guild.id){
					e.sendMessage("The song is not playing in this channel.").queue()
					403
				}else{
					e.sendMessage("The song is not playing in this server.").queue()
					403
				}
			}
		}else if(singing&&d.args=='info'){
			e.sendMessage("""**${lyricsLink.text()}**, by $author:```css
Lyrics: ${lyricsLink.attr('href')}
Cover: $coverLink```""").queue()
		}else if(!singing){
			if(!e.guild||e.author.isOwner(e.guild)||e.channel.song){
				if(d.args){
					singing=true
					try{
						String link="http://search.azlyrics.com/search.php?q=${URLEncoder.encode(d.args.trim(),'UTF-8')}"
						Document search=d.web.get(link,'G.Chrome')
						lyricsLink=search.getElementsByClass('panel')[-1].getElementsByClass('text-left')[0].getElementsByTag('a')[0]
						author=search.getElementsByClass('panel')[-1].getElementsByClass('text-left')[0].getElementsByTag('b')[1].text()
						Document song=d.web.get(lyricsLink.attr('href'),'G.Chrome')
						e.sendTyping().queue()
						try{
							Element getLyrics=song.getElementsByTag('div').findAll{it.classNames().empty}[1]
							String ass=Jsoup.parse(getLyrics.html().replaceAll(/(?i)<br[^>]*>/,'#br#')).text()
							List lyrics=ass.replaceAll(/(\#br\#)+/,'\n').split('\n')*.trim()
							Iterator iterator=lyrics.iterator()
							try{
								Document doc=d.web.get(("https://www.google.co.uk/search?q=${URLEncoder.encode(("${lyricsLink.text()} $author")+'UTF-8')}&tbm=isch"),'N.3DS')
								Element image=doc.getElementsByClass('image')[0]
								doc=d.web.get(image.attr('href'),'N.3DS')
								coverLink=doc.getElementById('thumbnail').attr('href')
								if(coverLink.contains('/revision/'))coverLink=coverLink.substring(0,coverLink.indexOf('/revision/'))
								new File('images/album.jpg')<<new URL(coverLink).openStream()
								covered=true
							}catch(imageex){
								imageex.printStackTrace()
								covered=false
							}
							venue=e.channel
							starter=e.author.id
							e.sendMessage("The song starts in 10! I'm GRover and I'll be singing:\n**$author - ${lyricsLink.text()}**\nUse `${d.prefix}sing stop` to cancel the song!").queue()
							Thread.sleep(10000)
							e.sendTyping().queue()
							if(singing){
								if(e.guild){
									nick=e.guild.selfMember.nickname
									if(!nick||nick?.startsWith('\u266b'))nick=''
									String title=lyricsLink.text()
									if(title.length()>31)title="${title.substring(0,30)}\u2026"
									e.guild.controller.setNickname(e.guild.selfMember,"\u266b$title").queue()
								}
								try{
									if(covered){
										e.jda.setAvatar('images/album.jpg').block()
									}else{
										e.jda.setAvatar('images/musicgrover.jpg').block()
									}
								}catch(v6){
									v6.printStackTrace()
								}
								while(singing&&iterator.hasNext()){
									String lyric=iterator.next().trim()
									if((lyric.length()>2)&&!lyric.contains(']')){
										e.sendMessage("_${lyric.replace('_','\\_')}_").queue()
										Thread.sleep(2000)
									}
								}
								if(e.guild)e.guild.controller.setNickname(e.guild.selfMember,nick).queue()
								try{
									e.jda.setAvatar("images/avatars/${d.info.avatar}.jpg").block()
									if(covered)new File('images/album.jpg').delete()
								}catch(v6){
									v6.printStackTrace()
								}
								singing=false
								venue=null
								coverLink=null
								e.sendMessage("My performance here is done.").queue()
							}
						}catch(ex){
							try{
								if(e.guild)e.guild.controller.setNickname(e.guild.selfMember,nick).queue()
								e.jda.setAvatar("images/avatars/${d.info.avatar}.jpg").block()
								if(covered)new File('images/album.jpg').delete()
							}catch(v6){
								v6.printStackTrace()
							}
							singing=false
							e.sendMessage("There was a problem with the lyrics.").queue()
							lyricex.printStackTrace()
							500
						}
					}catch(none){
						singing=false
						e.sendMessage("I couldn't find a song matching '$d.args.'").queue()
						none.printStackTrace()
						404
					}
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}sing [song name]/stop/info`").queue()
					400
				}
			}else{
				TextChannel songChannel=e.guild.textChannels.find{it.song}
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`${if(songChannel){", `Use in #$songChannel.name`"}else{''}}.\nStaff can set song channels with `${d.prefix}setchannel song`.").queue()
				403
			}
		}else{
			if(venue?.guild){
				e.sendMessage("I am already singing a song in $venue.guild.name.").queue()
				403
			}else{
				e.sendMessage("I am already singing a song in Direct Messages.").queue()
				403
			}
		}
	}
	String category='General'
	String help="""`sing [song name]` will make me sing a song in this channel.
`sing stop` will make me stop the song.
`sing info` will make me tell you some information about the song.
O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A- JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA."""
}


class BanCommand extends Command{
	List aliases=['ban','bans']
	int limit=25
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isStaff(e.guild)){
				if(e.guild.selfMember.roles.any{'BAN_MEMBERS'in it.permissions*.toString()}){
					d.args=[d.args.tokenize(),'','',''].flatten()
					d.args[0]=d.args[0].toLowerCase()
					if(d.args[0]in['list','all']){
						String newString=("**__${e.guild.name.capitalize()}'s Bans (${e.guild.manager.bans.size()})__:**\n")
						List bans=e.guild.manager.getBans().block()
						if(bans){
							bans.each{
								newString+="**${it.identity.capitalize()}**   ($it.id)\n"
								if(it.id in d.temp.bans[e.guild.id]*.key)newString+="${new Date(d.temp.bans[e.guild.id][it.id].end).format('HH:mm:ss, dd MMMM YYYY')}${if(d.temp.bans[e.guild.id][it.id].reason){"   ${d.temp.bans[e.guild.id][it.id].reason}"}else{''}}\n"
							}
							if(newString.length()>1500){
								newString=newString.substring(0,1500)
								e.sendMessage("${newString.substring(0,newString.lastIndexOf('\n'))}\n\n...And more. Use `${d.prefix}ban search` to find bans by name.").queue()
							}else{
								e.sendMessage(newString).queue()
							}
						}else{
							e.sendMessage("${newString}This server has no bans.").queue()
						}
					}else if(d.args[0]in['search','find']){
						String query=d.args[1..-1].join(' ').toLowerCase().trim()
						List bans=e.guild.manager.getBans().block().findAll{[it.name.toLowerCase(),it.identity.toLowerCase()].any{it.contains(query)}}
						String newString="**__Ban Results (${bans.size()})__:**\n"
						if(bans){
							bans.each{
								newString+="**${it.identity.capitalize()}**   ($it.id)\n"
								if(it.id in d.temp.bans[e.guild.id]*.key)newString+="${new Date(d.temp.bans[e.guild.id][it.id].end).format('HH:mm:ss, dd MMMM YYYY')}${if(d.temp.bans[e.guild.id][it.id].reason){"   ${d.temp.bans[e.guild.id][it.id].reason}"}else{''}}\n"
							}
							if(newString.length()>1500){
								newString=newString.substring(0,1500)
								e.sendMessage("${newString.substring(0,newString.lastIndexOf('\n'))}\n\n...And more. Use `${d.prefix}ban search` to find bans by name.").queue()
							}else{
								e.sendMessage(newString).queue()
							}
						}else{
							e.sendMessage("${newString}I couldn't find any bans matching '$query.'").queue()
							404
						}
					}else if(d.args[0]in['@everyone','@here']){
						e.sendMessage('No.').queue()
						511
					}else{
						User user
						if(d.args)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args[0])
						if(!user)user=e.guild.members.toList().sort{it.joinDate}[-1].user
						Member member=e.guild.membersMap[user.id]
						int offset=e.message.mentions?1:0
						if(member.owner){
							e.sendMessage("I can't ban the owner of the server.").queue()
							511
						}else if(user.id==e.jda.selfUser.id){
							e.sendMessage('...Nah.').queue()
							511
						}else if(d.args=~/^@.+#\d\d\d\d/){
							e.sendMessage("The user you tried to mention seems to have left the server. Looks like I saved you there.").queue()
							400
						}else{
							String reason=d.args[2-offset..-1].join(' ').trim()?:''
							long time=0
							try{
								if(d.args[1-offset]=~/\d+\w/){
									time=d.args[1-offset].formatTime()
								}else{
									reason=d.args[1-offset..-1].join(' ').trim()
								}
								if(reason.length()>1500)reason=reason.substring(0,1500)+'...'
								e.guild.controller.ban(member).queue()
								if(time){
									if(!d.temp.bans[e.guild.id])d.temp.bans[e.guild.id]=[:]
									d.temp.bans[e.guild.id][user.id]=[
										start:System.currentTimeMillis(),
										end:time,
										commander:e.author.id,
										reason:reason
									]
									d.json.save(d.temp,'temp')
								}
								e.sendMessage("I have banned $user.identity ${if(time){"until ${new Date(time).format('HH:mm:ss, dd MMMM YYYY').formatBirthday()}"}else{"forever"}}.").queue()
								e.guild.textChannels.findAll{it.log}*.sendMessage("**${e.author.identity.capitalize()}**: Banned $user.identity ${if(time){"until ${new Date(time).format('HH:mm:ss, dd MMMM YYYY').formatBirthday()}"}else{"forever"}}${if(reason){".\nReason: $reason"}else{" for no reason."}}")*.queue()
							}catch(ex){
								e.sendMessage(d.errorMessage()+"Usage: `ban [user]/list/search ..`.").queue()
								400
							}
						}
					}
				}else{
					e.sendMessage("I need to be able to ban to do that...").queue()
					511
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Staff (Trainer/MANAGE_MESSAGES)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to manage bans in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`ban list` will make me list this server's bans.
`ban search [search term]` will make me search this server's bans.
`ban [user] [time] [reason]` will make me ban the user. All parameters are optional.
Your banne."""
}


class SmiliesCommand extends Command{
	List aliases=['smilies','smilie']
	int limit=25
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				d.args=[d.args.tokenize(),'','',''].flatten()
				String arg=d.args[0].toLowerCase()
				String name=d.args[1].replaceAll(['(',')'],'').toLowerCase()
				if(arg=='xat'){
					if(d.settings.smilies[e.guild.id]){
						d.settings.smilies[e.guild.id]=false
						e.sendMessage("Xat smilies have been disabled for this server.").queue()
					}else{
						d.settings.smilies[e.guild.id]=true
						e.sendMessage("Xat smilies have been enabled for this server.").queue()
					}
					d.json.save(d.settings,'settings')
				}else if(arg in['create','add']){
					if(new File("images/cs").listFiles().findAll{it.name.endsWith('_'+e.guild.id+'_png')}.size()>49){
						e.sendMessage("You can't add any more smilies because you've reached the maximum amount, which is 50. Delete some of the less important ones and come see me again.").queue()
						403
					}else if(!name){
						e.sendMessage("What is the name supposed to be?").queue()
						400
					}else if(!d.args[2]){
						e.sendMessage("What is the smilie supposed to be?").queue()
						400
					}else if(name.containsAny(['@everyone','@here'])||(name in new File("images/xat").listFiles()*.name*.replace('_xat.png',''))){
						e.sendMessage("You can't add a smilie with that name${if(d.args[1].contains('@')){''}else{" because an xat smilie with that name already exists"}}.").queue()
						403
					}else if(new File("images/cs/${name}_${e.guild.id}.png").exists()){
						e.sendMessage("You can't add a smilie with that name because a smilie already exists with that name. Edit the smilie instead.").queue()
						400
					}else{
						try{
							InputStream input=new URL(d.args[2]).newInputStream(requestProperties:[Accept:"*/*"])
							BufferedImage smilie=ImageIO.read(input)
							if(smilie.height>150){
								e.sendMessage("The image is too tall. It should be less than 150 pixels in each dimension.").queue()
								403
							}else if(smilie.width>150){
								e.sendMessage("The image is too wide. It should be less than 150 pixels in each dimension.").queue()
								403
							}else{
								ImageIO.write(smilie,'png',new File("images/cs/${name}_${e.guild.id}.png"))
								e.sendMessage("The smilie **$name** has been added.").queue()
								e.sendFile("images/cs/${name}_${e.guild.id}.png").queue()
							}
						}catch(ex){
							e.sendMessage("That isn't an image, or it wasn't found.").queue()
							404
						}
					}
				}else if(arg in['edit','change']){
					if(!name){
						e.sendMessage("Which smilie are you trying to edit?").queue()
						400
					}else if(!d.args[2]){
						e.sendMessage("What is the smilie supposed to be changed to?").queue()
						400
					}else{
						File image=new File("images/cs/${name}_${e.guild.id}.png")
						if(image.exists()){
							try{
								InputStream input=new URL(d.args[2]).newInputStream(requestProperties:[Accept:"*/*"])
								BufferedImage smilie=ImageIO.read(input)
								if(smilie.height>150){
									e.sendMessage("The image is too tall. It should be less than 150 pixels in each dimension.").queue()
									403
								}else if(smilie.width>150){
									e.sendMessage("The image is too wide. It should be less than 150 pixels in each dimension.").queue()
									403
								}else{
									ImageIO.write(smilie,"png",new File("images/cs/${name}_${e.guild.id}.png"))
									e.sendMessage("The smilie **$name** has been changed.").queue()
									e.sendFile("images/cs/${name}_${e.guild.id}.png").queue()
								}
							}catch(ex){
								e.sendMessage("That isn't an image, or it wasn't found.").queue()
								404
							}
						}else{
							e.sendMessage("I couldn't find a custom smilie matching '$name.'").queue()
							404
						}
					}
				}else if(arg in['delete','remove']){
					if(!name){
						e.sendMessage("Which smilie are you trying to delete?").queue()
					}else{
						File image=new File("images/cs/${name}_${e.guild.id}.png")
						if(image.exists()){
							image.delete()
							e.sendMessage("The smilie **$name** has been removed.").queue()
						}else{
							e.sendMessage("I couldn't find a custom smilie matching '$name.'").queue()
						}
					}
				}else if(arg=='list'){
					List smilers=new File("images/cs").listFiles().findAll{it.name.endsWith('_'+e.guild.id+'.png')}
					if(smilers){
						e.sendMessage("**__${e.guild.name.capitalize()}'s Custom Smilies ($smilers.size)__:**\n${smilers.collect{"(${it.name.replaceAll(/_\d+.png$/,'')})"}.join(',  ')}").queue()
					}else{
						e.sendMessage("**__${e.guild.name.capitalize()}'s Custom Smilies (0)__:**\nThis server doesn't have any custom smilies.").queue()
					}
				}else if(arg=='xatlist'){
					e.sendTyping().queue()
					File ass=new File('temp/smilies.png')
					OutputStream os=ass.newOutputStream()
					List a=new File('images/xat').listFiles().toList()*.toString().sort{it.replaceAll(['images\\xat\\','_xat.png'],'')}.split(10)
					BufferedImage image=new BufferedImage(700,(62*a.size)+4,BufferedImage.TYPE_INT_RGB)
					Graphics2D graphics=image.createGraphics()
					graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
					graphics.color=new Color(0xffffff)
					graphics.font=new Font('Arial',Font.PLAIN,10)
					int shet=5
					int shat=5
					for(b in a){
						for(c in b){
							graphics.drawImage(ImageIO.read(new File(c)),shet,shat,null)
							graphics.drawString("(${c.replaceAll(['images\\xat\\','_xat.png'],'')})",shet,shat+46)
							shet+=68
						}
						shet=5
						shat+=62
					}
					graphics.dispose()
					ByteArrayOutputStream baos=new ByteArrayOutputStream()
					ImageIO.write(image,'png',baos)
					baos.writeTo(os)
					os.close()
					e.sendTyping().queue()
					try{
						e.sendFile('temp/smilies.png').queue()
					}catch(ex){
						e.sendMessage("I need to be able to upload files to do that...").queue()
						ex.printStackTrace()
						511
					}
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}smilies xat/create/edit/delete/list/xatlist ..`").queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to manage smilies in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`smilies xat` will make me enable or disable posting xat smilies.
`smilies create [name] [link]` will make me create a custom smilie.
`smilies edit [name] [link]` will make me edit a custom smilie.
`smilies delete [name]` will make me delete a custom smilie.
`smilies list` will make me list this server's custom smilies.
`smilies xatlist` will make me list my xat smilies. Use sparingly.
Not like anyone uses this now though."""
}


class CloneCommand extends Command{
	List aliases=['clone']
	int limit=25
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				if(d.args){
					TextChannel text=e.message.mentionedChannels?e.message.mentionedChannels[-1]:e.guild.findTextChannel(d.args)
					VoiceChannel voice=e.guild.findVoiceChannel(d.args)
					Role role=e.message.mentionedRoles?e.message.mentionedRoles[-1]:e.guild.findRole(d.args)
					if(text){
						if(e.guild.selfMember.roles.any{'MANAGE_CHANNEL'in it.permissions*.toString()}){
							ChannelManager manager=e.guild.controller.createTextChannel(text.name).block().manager
							manager.setTopic(text.topic).queue()
							e.sendMessage("The channel **$text.name** has been cloned.").queue()
						}else{
							e.sendMessage("I need to be able to manage channels to do that...").queue()
							511
						}
					}else if(voice){
						if(e.guild.selfMember.roles.any{'MANAGE_CHANNEL'in it.permissions*.toString()}){
							ChannelManager manager=e.guild.controller.createVoiceChannel(voice.name).block().manager
							manager.setBitrate(voice.bitrate).queue()
							manager.setUserLimit(voice.userLimit).queue()
							e.sendMessage("The channel **$voice.name** has been cloned.").queue()
						}else{
							e.sendMessage("I need to be able to manage channels to do that...").queue()
							511
						}
					}else if(role){
						if(e.guild.selfMember.roles.any{'MANAGE_ROLES'in it.permissions*.toString()}){
							RoleManager manager=e.guild.controller.createRole().block().manager
							manager.setName(role.name).queue()
							manager.setColor(role.color).queue()
							manager.sethoisted(role.hoisted).queue()
							manager.setMentionable(role.mentionable).queue()
							manager.setPermissions(role.permissionsRaw).queue()
							e.sendMessage("The role **$role.name** has been cloned.").queue()
						}else{
							e.sendMessage("I need to be able to manage roles to do that...").queue()
							511
						}
					}else{
						e.sendMessage("I couldn't find anything cloneable matching '$d.args.'").queue()
						404
					}
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}clone [channel/role]`").queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to clone in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`clone [channel]` will make me create an identical copy of the channel.
`clone [role]` will make me create an identical copy of the role.
Let the clone wars begin."""
}


class AccessCommand extends Command{
	List aliases=['access']
	def run(Map d,Event e){
		if(d.args||e.message.attachments){
			if(d.args.toLowerCase().containsAny(['avatar','icon'])){
				d.args=d.args.replaceAny(['avatar','icon'],'').trim()
				d.bot.commands.find{it.aliases[0]=='avatar'}.run(d,e)
			}else if(e.message.mentions){
				d.bot.commands.find{it.aliases[0]=='userinfo'}.run(d,e)
			}else if(e.message.mentionedChannels){
				d.bot.commands.find{it.aliases[0]=='channelinfo'}.run(d,e)
			}else if(e.message.mentionedRoles){
				d.bot.commands.find{it.aliases[0]=='roleinfo'}.run(d,e)
			}else if(e.message.emotes){
				d.bot.commands.find{it.aliases[0]=='emoteinfo'}.run(d,e)
			}else if(d.args.containsAny(['everyone','here'])){
				d.bot.commands.find{it.aliases[0]=='scope'}.run(d,e)
			}else if(d.args.toLowerCase()in d.tags*.key.findAll{it.length()>1}){
				d.bot.commands.find{it.aliases[0]=='tag'}.run(d,e)
			}else if(e.message.attachment){
				d.bot.commands.find{it.aliases[0]=='wordcount'}.run(d,e)
			}else if(d.args=~/\w\.\w/){
				d.bot.commands.find{it.aliases[0]=='website'}.run(d,e)
			}else if(d.args.startsWith('#')){
				d.bot.commands.find{it.aliases[0]=='colour'}.run(d,e)
			}else if(d.args.toLowerCase().containsAny(['youtube','video'])){
				d.args=d.args.replaceAny(['youtube','video'],'').trim()
				d.bot.commands.find{it.aliases[0]=='youtube'}.run(d,e)
			}else if(d.args.toLowerCase().containsAny(['image','picture'])){
				d.args=d.args.replaceAny(['image','picture'],'').trim()
				d.bot.commands.find{it.aliases[0]=='image'}.run(d,e)
			}else if((d.args.toLowerCase()+' ')in d.bot.commands.findAll{!it.dev}.aliases*.getAt(0).flatten()*.plus(' ')){
				Command cmd=d.bot.commands.find{(d.args+' ').startsWith(it.aliases[0]+' ')}
				d.args=d.args.replace(cmd.aliases[0],'').trim()
				cmd.run(d,e)
			}else{
				d.bot.commands.find{it.aliases[0]=='google'}.run(d,e)
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}access [query]`.").queue()
			400
		}
	}
	String category='General'
	String help="""`access [query]` will make me use the best command for your query.
Now there's a Beaconville artifact if I've ever seen one."""
}


class TrackerCommand extends Command{
	List aliases=['tracker']
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				d.args=d.args.tokenize()
				if(d.args){
					d.args[0]=d.args[0]?.toLowerCase()
					String message
					if(d.args[1])message=d.args[1..-1].join(' ')
					if(d.args[0]=='join'){
						if(message){
							d.tracker.join[e.guild.id]=message
							e.sendMessage("The join message has been set. I will now welcome new users to this server.").queue()
						}else{
							d.tracker.join.remove(e.guild.id)
							e.sendMessage("The join message has been cleared. I will no longer welcome new users to this server.").queue()
						}
						d.json.save(d.tracker,'tracker')
					}else if(d.args[0]=='leave'){
						if(message){
							d.tracker.leave[e.guild.id]=message
							e.sendMessage("The leave message has been set. I will now send off formers of this server.").queue()
						}else{
							d.tracker.leave.remove(e.guild.id)
							e.sendMessage("The leave message has been cleared. I will no longer send off formers of this server.").queue()
						}
						d.json.save(d.tracker,'tracker')
					}else if(d.args[0]=='ban'){
						if(message){
							d.tracker.ban[e.guild.id]=message
							e.sendMessage("The ban message has been set. I will now insult the banished people.").queue()
						}else{
							d.tracker.ban.remove(e.guild.id)
							e.sendMessage("The ban message has been cleared. I will no longer insult the banished people.").queue()
						}
						d.json.save(d.tracker,'tracker')
					}else if(d.args[0]=='unban'){
						if(message){
							d.tracker.unban[e.guild.id]=message
							e.sendMessage("The unban message has been set. I will now pardon forgiven users.").queue()
						}else{
							d.tracker.unban.remove(e.guild.id)
							e.sendMessage("The unban message has been cleared. I will no longer pardon forgiven users.").queue()
						}
						d.json.save(d.tracker,'tracker')
					}else{
						e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tracker join/leave/ban/unban [message]`").queue()
						400
					}
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}tracker join/leave/ban/unban [message]`").queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to track in Direct Messages.").queue()
			405
		}
	}
	String category='Moderation'
	String help="""`tracker join [message]` will make me send the message whenever someone joins the server.
`tracker leave [message]` will make me send the message whenever someone leaves the server.
Welcome-!"""
}


class IsupCommand extends Command{
	List aliases=['isup','isitdownrightnow']
	int limit=30
	def run(Map d,Event e){
		if(d.args){
			d.args=d.args.replace(' ','-')
			if(!d.args.startsWithAny(['http://','https://']))d.args="http://$d.args"
			if(!d.args.contains('.'))d.args+=".com"
			String alias=d.args.substring(d.args.indexOf('//')+2)
			try{
				long startTime=System.currentTimeMillis()
				Document doc=d.web.get(d.args,'G.Chrome')
				long stopTime=System.currentTimeMillis()
				Element title=doc.getElementsByTag('title')[0]
				e.sendMessage("It's just you. **${title?title.text():alias}** is up and running. (${(stopTime-startTime)/1000}s)").queue()
			}catch(ex){
				if(ex.class==org.jsoup.HttpStatusException){
					e.sendMessage("It's not just you. **$alias** is down for everyone. (HTTP $ex.statusCode)").queue()
				}else{
					e.sendMessage("It's not just you. **$alias** is down for everyone. (${ex.class.simpleName})").queue()
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}isup [domain]`").queue()
			400
		}
	}
	String category='Online'
	String help="""`isup [domain]` will make me check if that domain is online.
It's just you."""
}


class TopCommand extends Command{
	List aliases=['top','popular']
	int limit=25
	def run(Map d,Event e){
		d.args=d.args.toLowerCase().tokenize()+"1"
		List top=[]
		int num=(d.args[1]==~/\d+/)?d.args[1].toInteger():1
		num-=num?1:0
		num*=15
		Range range=(0+num)..(14+num)
		try{
			int status=200
			if(d.args[0].startsWith('name')){
				Map table=e.jda.users.groupBy{it.name.toLowerCase()}.sort{it.value.size()}
				table*.key.reverse()[range].each{
					top+="`#${num+=1}` **${it.tokenize()*.capitalize().join(' ')}** (${table[it].size()} users)"
				}
			}else if(d.args[0].startsWith('game')){
				Map table=e.jda.guilds*.members.flatten().findAll{it.game}.groupBy{it.user.id}*.value*.first().groupBy{it.game.name}.sort{it.value.size()}
				table*.key.reverse()[range].each{
					top+="`#${num+=1}` **$it** (${table[it].size()} playing)"
				}
			}else if(d.args[0].startsWith('bot')){
				Map table=e.jda.guilds*.users.flatten().findAll{it.bot}.groupBy{it.id}.sort{it.value.size()}
				table*.key.reverse()[range].each{
					top+="`#${num+=1}` **<@$it>** (${table[it].size()} servers)"
				}
			}else{
				top+=d.errorMessage()+"Usage: `${d.prefix}top [names/games/bots] [page]`"
				status=400
			}
			e.sendMessage(top.join('\n')).queue()
			status
		}catch(ex){
			e.sendMessage("I'm unable to look this deep into it. Try a smaller page number.").queue()
			ex.printStackTrace()
			500
		}
	}
	String category='General'
	String help="""`top names [page]` will make me tell you the most common usernames.
`top games [page]` will make me tell you the most played games right now.
`top bots [page]` will make me tell you which bots are in the most servers.
This is only as far as I can see, though. Imagine the global statistic."""
}


class CleanCommand extends Command{
	List aliases=['clean']
	int limit=25
	def run(Map d,Event e){
		int amount=(d.args==~/\d+/)?d.args.toInteger():50
		amount+=1
		if(amount<2)amount=2
		if(amount>50)amount=50
		List history=e.channel.history.retrievePast(amount).block()
		List messages=history.findAll{it.author.id==e.jda.selfUser.id}
		boolean permission=(e.guild&&e.guild.selfMember.roles.any{'MESSAGE_MANAGE'in it.permissions*.toString()})
		List invokers=history.findAll{it.id in d.messages*.id}.findAll{it.author.id==e.author.id}
		invokers.each{Message invoker->
			d.messages-=d.messages.find{it.id==invoker.id}
		}
		if(permission)messages+=invokers
		if((messages.size()>1)&&permission){
			e.channel.deleteMessages(messages).queue()
			d.messages-=d.messages.find{it.id==e.message.id}
		}else{
			messages.each{
				it.delete().queue()
				Thread.sleep(150)
			}
		}
	}
	String category='General'
	String help="""`clean [number]` will make me remove my messages and your commands back that far.
Quick and painless, before they start saying 'GO TO TESTING!!'"""
}


class NoteCommand extends Command{
	List aliases=['note','notes']
	def run(Map d,Event e){
		d.args=d.args.tokenize()
		d.args[0]=d.args[0]?.toLowerCase()
		String id=Long.toString((e.message.createTimeMillis/500)as long,36)
		if(d.args[0]=='list'){
			List total=d.notes*.value.flatten().findAll{it.user==e.author.id}
			if(total){
				e.sendMessage(total.collect{Map note->
					"`$note.id` \"${note.content.length()>100?note.content.substring(0,100)+'...':note.content}\" ${if(note.time){"(${new Date(note.time).format('H:mm d/M/YYYY')})"}else if(note.mention){"(${e.jda.users.find{it.id==note.mention}?.identity?:note.mention})"}else{''}}"
				}.join('\n')).queue()
			}else{
				e.sendMessage("No notes found, add some!").queue()
			}
		}else if(d.args[0]in['remove','delete']){
			List total=d.notes*.value.flatten().findAll{it.user==e.author.id}
			Map note=total.find{it.id==d.args[1].toLowerCase()}
			if(!note)note=total.find{it.content.contains(d.args[1])}
			if(note){
				d.notes.generic-=note
				d.notes.timed-=note
				d.notes.user-=note
				e.sendMessage("That note has been removed.").queue()
				d.json.save(d.notes,'notes')
			}else{
				e.sendMessage("I couldn't find a note matching '${d.args[1]}.'").queue()
				404
			}
		}else if(d.args[0]=='clear'){
			List total=d.notes*.value.flatten().findAll{it.user==e.author.id}
			total.each{
				d.notes.generic-=it
				d.notes.timed-=it
				d.notes.user-=it
			}
			e.sendMessage("A shiny clean desk emerges.").queue()
			d.json.save(d.notes,'notes')
		}else if(d.args[0]in['generic','create']){
			if(d.args[1]){
				d.notes.generic+=[
					id:id,
					user:e.author.id,
					content:d.args[1..-1].join(' ')
				]
				e.sendMessage("A generic note has been created at `$id`.").queue()
				d.json.save(d.notes,'notes')
			}else{
				e.sendMessage("Please add some text for the note.").queue()
				400
			}
		}else if((d.args[0]==~/<@!?\d+>/)&&e.message.mentions||e.guild&&e.guild.findUser(d.args[0])){
			User user=e.guild?.findUser(d.args[0])?:e.message.mentions[-1]
			if(user.bot){
				e.sendMessage("Oh no, anyone but them.").queue()
				400
			}else{
				d.notes.user+=[
					id:id,
					user:e.author.id,
					mention:user.id,
					content:d.args[1]?d.args[1..-1].join(' '):''
				]
				e.sendMessage("A status note for $user.identity has been created at `$id`.").queue()
				d.json.save(d.notes,'notes')
			}
		}else if((d.args[0]=~/\d+\w/)||(d.args[0]==~/\d\d\/\d\d\/\d\d\d\d/)){
			def time=(d.args[0]==~/\d\d\/\d\d\/\d\d\d\d/)?Date.parse('dd/MM/YYYY',d.args[0]).time:d.args[0].formatTime()
			if(time>Long.MAX_VALUE){
				e.sendMessage("I think the paper would rot by then.").queue()
				400
			}else{
				d.notes.timed+=[
					id:id,
					user:e.author.id,
					time:time,
					content:d.args[1]?d.args[1..-1].join(' '):''
				]
				e.sendMessage("A timed note for ${new Date(time).format('HH:mm:ss, d MMMM YYYY').formatBirthday()} has been created at `$id`.").queue()
				d.json.save(d.notes,'notes')
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}note [generic/@mention/time/list/remove/clear] ..`").queue()
		}
	}
	String category='General'
	String help="""`note generic [text]` will make me create a note that never triggers.
`note [@mention] [text]` will make me create a note that triggers when the user comes online.
`note [time] [text]` will make me create a note that triggers in time.
`note list` will make me list your notes.
`note remove [number/text]` will make me remove that note.
`note clear` will make me remove all your notes."""
}


class ProfileCommand extends Command{
	List aliases=['profile']
	int limit=25
	def run(Map d,Event e){
		User user=e.author
		if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			if(d.db[user.id]){
				e.sendTyping().queue()
				File ass=new File('temp/profile.png')
				OutputStream os=ass.newOutputStream()
				BufferedImage image=new BufferedImage(251,229,BufferedImage.TYPE_INT_ARGB)
				Graphics2D graphics=image.createGraphics()
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
				graphics.drawImage(ImageIO.read(new File("images/profile.png")),0,0,null)
				graphics.color=new Color(0x010101)
				graphics.font=new Font('WhitneyBold',Font.PLAIN,16)
				File avatar=new File("temp/avatars/${user.avatarId?:user.defaultAvatarId}.png")
				if(!avatar.exists())avatar=d.web.download("${user.avatar.replace('.jpg','.png')}?size=64","temp/avatars/${user.avatarId}.png")
				graphics.drawImage(ImageIO.read(avatar),5,22,null)
				String title="$user.identity's Profile"
				if(d.db[user.id].aka)title+=" (${d.db[user.id].aka})"
				graphics.drawString(title,3,15)
				graphics.font=new Font("Calibri",Font.PLAIN,13)
				List area=d.db[user.id].area.split(', ')
				graphics.drawString(area[0,-1].unique().join(', '),96,34)
				graphics.drawString(d.db[user.id].age,96,54)
				graphics.drawString(d.db[user.id].mc?:"none",96,73)
				graphics.font=new Font('Calibri',Font.BOLD,15)
				graphics.drawString('Communities',3,103)
				graphics.font=new Font('Whitney Book',Font.PLAIN,14)
				int down=108
				List guilds=e.jda.guilds.findAll{user.id in it.users*.id}.sort{-it.members.size()}
				if(guilds.size()>7)guilds=guilds[0..6]
				guilds.each{
					if(it.icon){
						File icon=new File("temp/icons/${it.iconId}.png")
						if(!icon.exists())icon=d.web.download("${it.iconUrl.replace('.jpg','.png')}?size=16","temp/icons/${it.iconId}.png")
						graphics.drawImage(ImageIO.read(icon),3,down,null)
					}
					graphics.drawString(it.name,21,down+13)
					down+=17
				}
				graphics.color=new Color(0xCDCDCD)
				graphics.font=new Font('Calibri',Font.PLAIN,8)
				graphics.drawString(user.id,76,86)
				graphics.drawImage(ImageIO.read(new File("images/${d.db[user.id].gender.toLowerCase()}.png")),236,78,null)
				graphics.dispose()
				ByteArrayOutputStream baos=new ByteArrayOutputStream()
				ImageIO.write(image,'png',baos)
				baos.writeTo(os)
				os.close()
				e.sendFile(ass).queue()
			}else{
				e.sendMessage("There is no information in my database for $user.name.").queue()
				404
			}
		}else{
			e.sendMessage("I couldn't find a user matching '$d.args.'").queue()
			404
		}
	}
	String category='Database'
	String help="""`profile [@mention] will make me post their profile.
This is a compiled image containing their information. And it uses bandwidth."""
}


class CustomCommand extends Command{
	List aliases=['custom','alias']
	def run(Map d,Event e){
		if(e.guild){
			if(e.author.isOwner(e.guild)){
				d.args=d.args.tokenize()
				d.args[0]=d.args[0]?.toLowerCase()
				if(d.args[0]=='create'){
					d.args[1]=d.args[1]?.toLowerCase()
					if(d.args[1]){
						if(d.customs[e.guild.id].find{it.name==d.args[1]}){
							e.sendMessage("A custom command with that name already exists.").queue()
							400
						}else if(d.bot.commands.find{d.args[1]in it.aliases}){
							e.sendMessage("You can't create a custom command with that name because a command already exists with that name.").queue()
							400
						}else{
							d.args[2]=d.args[2].toLowerCase()
							if(d.args[2]){
								Command call=d.bot.commands.find{d.args[2]in it.aliases}
								if(call){
									if(!d.customs[e.guild.id])d.customs[e.guild.id]=[]
									d.customs[e.guild.id]+=[
										name:d.args[1],
										command:call.aliases[0],
										args:d.args[3]?d.args[3..-1].join(' '):'',
										uses:0
									]
									e.sendMessage("The custom command **${d.args[1]}** has been created. You can now use `${d.prefix}${d.args[1]}`.").queue()
									d.json.save(d.customs,'customs')
								}else{
									e.sendMessage("I couldn't find a command matching '${d.args[2]}.'").queue()
									404
								}
							}else{
								e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom create [name] [command] [arguments]`").queue()
								400
							}
						}
					}else{
						e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom create [name] [command] [arguments]`").queue()
						400
					}
				}else if(d.args[0]=='edit'){
					d.args[1]=d.args[1].toLowerCase()
					if(d.args[1]){
						if(d.customs[e.guild.id].find{it.name==d.args[1]}){
							d.args[2]=d.args[2].toLowerCase()
							if(d.args[2]){
								Command call=d.bot.commands.find{d.args[2]in it.aliases}
								if(call){
									if(!d.customs[e.guild.id])d.customs[e.guild.id]=[]
									d.customs[e.guild.id].find{it.name==d.args[1]}.command=call.aliases[0]
									d.customs[e.guild.id].find{it.name==d.args[1]}.args=d.args[3]?d.args[3..-1].join(' '):''
									e.sendMessage("The custom command **${d.args[1]}** has been edited.").queue()
									d.json.save(d.customs,'customs')
								}else{
									e.sendMessage("I couldn't find a command matching '${d.args[2]}.'").queue()
									404
								}
							}else{
								e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom create [name] [command] [arguments]`").queue()
								400
							}
						}else{
							e.sendMessage("I couldn't find a custom command matching '${d.args[1]}.'").queue()
							404
						}
					}else{
						e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom create [name] [command] [arguments]`").queue()
						400
					}
				}else if(d.args[0]=='delete'){
					d.args[1]=d.args[1].toLowerCase()
					if(d.args[1]){
						if(d.customs[e.guild.id].find{it.name==d.args[1]}){
							d.customs[e.guild.id]-=d.customs[e.guild.id].find{it.name==d.args[1]}
							e.sendMessage("The custom command **${d.args[1]}** has been deleted.").queue()
							d.json.save(d.customs,'customs')
						}else{
							e.sendMessage("I couldn't find a custom command matching '${d.args[1]}.'").queue()
							404
						}
					}else{
						e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom create [name] [command] [arguments]`").queue()
						400
					}
				}else if(d.args[0]=='info'){
					d.args[1]=d.args[1].toLowerCase()
					if(d.args[1]){
						Map custom=d.customs[e.guild.id].find{it.name==d.args[1]}
						if(custom){
							Command cmd=d.bot.commands.find{custom.command in it.aliases}
							e.sendMessage("Command: ${cmd.aliases.join('/')}\nArguments: $custom.args\nUses: $custom.uses").queue()
						}else{
							e.sendMessage("I couldn't find a custom command matching '${d.args[1]}.'").queue()
							404
						}
					}else{
						e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom create [name] [command] [arguments]`").queue()
						400
					}
				}else if(d.args[0]=='list'){
					e.sendMessage("**__$e.guild.name's Custom Commands (${d.customs[e.guild.id]?.size()?:''})__:**\n${d.customs[e.guild.id]?d.customs[e.guild.id]*.name.join(', '):"No custom commands to see here."}").queue()
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}custom [create/edit/delete/info/list] ..`").queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Required: `Owner (Bot Commander/ADMINISTRATOR)`.").queue()
				403
			}
		}else{
			e.sendMessage("There's really no point to having this work in Direct Messages.").queue()
			405
		}
	}
	String category='General'
	String help="""`custom create [name] [command] [arguments]` will make me create a custom command/alias.
`custom edit [name] [command] [arguments]` will make me edit a custom command.
`custom delete [name]` will make me delete a custom command.
`custom info [name]` will make me tell you some information about the custom command.
`custom info [name]` will make me list this server's custom commands.
No witty comment, tell Axew."""
}


class PwnedCommand extends Command{
	List aliases=['pwned','haveibeenpwned']
	int limit=25
	def run(Map d,Event e){
		if(d.args.containsAll(['@','.'])){
			e.sendTyping().queue()
			try{
				List hecks=new JsonSlurper().parse(Unirest.get("https://haveibeenpwned.com/api/v2/breachedaccount/$d.args").asString().body.bytes).collect{Map breach->"**$breach.Title** ($breach.Domain): ${breach.DataClasses.join(', ')} ${if(breach.IsVerified){'(verified)'}else if(breach.IsFabricated){'(fabricated)'}else{''}}"}
				"You've been pwned $hecks.size time${if(hecks.size>1){'s'}else{''}}:\n${hecks.join('\n')}".split(1999).each{
					e.sendMessage(it).queue()
					Thread.sleep(150)
				}
			}catch(ex){
				ex.printStackTrace()
				e.sendMessage("All safe. Looks like `$d.args`'s information hasn't been stolen.").queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}pwned [email]`").queue()
			400
		}
	}
	String category='Online'
	String help="""`pwned [email]` will make me list data breaches in which your information was stolen.
It wasn't me this time."""
}


class MathCommand extends Command{
	List aliases=['math','maths']
	def run(Map d,Event e){
		if(d.args){
			e.sendTyping().queue()
			String sum=d.args.replaceAll(/([A-Za-z]+)/){full,word->"Math.$word"}.replaceAll(['"',"'"],'').replaceEach(['{','}'],['[',']']).replaceAll(['\\','\$','_'],'').replaceAll(/[\u00c0-\u00f6\u00f8-\ufffe]+/,'').replaceAll(/(\/)+.+(\/)\.*\[\s+\w+.+(,|\.|\])+/,'')
			String ass
			try{
				ass=new GroovyShell().evaluate(sum).toString()
			}catch(ex){
				ass="$ex".replaceAll(["${ex.class.name}:",'startup failed:','Script1.groovy:'],'').replaceAll(/\d error(?:s?)/,'').trim()
			}
			ass.split(1997).each{
				e.sendMessage("`$it`").queue()
				Thread.sleep(150)
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}math [sum]`").queue()
			400
		}
	}
	String category='General'
	String help="""`math [sum]` will make me evaluate the math sum in Groovy.
So yes, it's a homework machine."""
}


class MapCommand extends Command{
	List aliases=['map','world']
	int limit=300
	def run(Map d,Event e){
		if(e.guild){
			e.sendTyping().queue()
			int size=18
			int spread=12
			d.args=d.args.tokenize()
			if(d.args[0]==~/\d+/){
				size=d.args[0].toInteger()
				if(size<1)size=1
			}
			if(d.args[1]==~/\d+/){
				spread=d.args[1].toInteger()
				if(spread<1)spread=1
			}
			Range range=(-spread..spread)
			List users=e.guild.users.findAll{d.db[it.id]}.sort{-it.createTimeMillis}
			File ass=new File("temp/map.png")
			OutputStream os=ass.newOutputStream()
			BufferedImage image=new BufferedImage(2023,954,BufferedImage.TYPE_INT_ARGB)
			Graphics2D graphics=image.createGraphics()
			graphics.drawImage(ImageIO.read(new File("images/map.png")),0,0,null)
			users.each{User user->
				String area=d.db[user.id].area
				String key=d.misc.geos*.key.sort{-it.length()}.find{area.endsWith(it)}
				if(key){
					List coords=d.misc.geos[key].tokenize()*.toInteger()
					File avatar=new File("temp/avatars/${user.avatarId?:user.defaultAvatarId}.png")
					if(!avatar.exists())avatar=d.web.download("${user.avatar.replace('.jpg','.png')}?size=64","temp/avatars/${user.avatarId}.png")
					Image scaled=ImageIO.read(avatar).getScaledInstance(size,size,Image.SCALE_SMOOTH)
					graphics.drawImage(scaled,coords[0]+range.randomItem(),coords[1]+range.randomItem(),null)
				}
			}
			graphics.dispose()
			ByteArrayOutputStream baos=new ByteArrayOutputStream()
			ImageIO.write(image,'png',baos)
			baos.writeTo(os)
			os.close()
			e.sendFile(ass).queue()
		}else{
			e.sendMessage("There's really no point to having this work in Direct Messages.").queue()
			405
		}
	}
	String category='Database'
	String help="""`map [avatar size] [avatar spread]` will make me generate a world map with the users of the server placed on it.
So many hours of writing down co-ordinates..."""
}


class SourceCommand extends Command{
	List aliases=['source','src']
	int limit=60
	def run(Map d,Event e){
		d.args=d.args.replaceAll(/^</,'').replaceAll(/>$/,'').trim()
		if(d.args.contains('.')||e.message.attachment||e.message.mentions||e.message.emotes){
			e.sendTyping().queue()
			if(e.message.attachment)d.args=e.message.attachment.url
			else if(e.message.mentions)d.args=(e.message.mentions[-1].avatar?:e.message.mentions[-1].defaultAvatar)+'?size=512'
			else if(e.message.emotes)d.args=e.message.emotes[-1].imageUrl
			String link="https://encrypted.google.com/searchbyimage?image_url=${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link,'G.Chrome')
				String match=doc.getElementsByClass('_gUb')[0].text()
				String source=doc.getElementsByClass('srg')?.getAt(0)?.getElementsByClass('r')?.getAt(0)?.getElementsByTag('a')?.getAt(0)?.attr('href')
				String starter=["Oh, I think that's","That looks like","It's probably"].randomItem()
				e.sendMessage("$starter **$match**.\n\nSearch: <https://encrypted.google.com/search?q=${URLEncoder.encode(match)}&tbm=isch>${if(source){"\nSource: <$source>"}else{''}}").queue()
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage("You are being rate limited.").queue()
					429
				}else{
					e.sendMessage("I really can't describe the image.\n$link").queue()
					404
				}
				ex.printStackTrace()
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}source [image url/image/@mention/emote]`.").queue()
			400
		}
	}
	String category='Online'
	String help="""`source [image url/image]` will make me reverse Google search for the image.
`source [@mention/emote]` will make me reverse Google search for the user's avatar or the emote.
Where'd you get that?"""
}

/*
class GuideCommand extends Command{
	List aliases=['guide']
	def run(Map d,Event e){
		d.args=d.args.toLowerCase()
		List path=[]
		if(d.args.containsAny(['message','post'])path[0]='message'
		else if(d.args.containsAny(['channel','room']))path[0]='channel'
		else if(d.args.containsAny(['user','member']))path[0]='user'
		else if(d.args.containsAny(['server','guild']))path[0]='server'
		else if(d.args.containsAny(['role','rank']))path[0]='role'
		else if(d.args.contains('webhook'))path[0]='webhook'
		else if(d.args.contains('bot'))path[0]='
		if(d.misc.help[path[0]]){
			if(d.args.containsAny(['make','create']))path[1]='create'
			else if(d.args.containsAny(['add','give']))path[1]='add'
			else if(d.args.containsAny(['remove','take']))path[1]='remove'
			else if(d.args.contains('delete'))path[1]='delete'
			else if(d.args.contains(['edit','change']))path[1]='edit'
			else if(d.args.contains('move'))path[1]='move'
			if(d.misc.help[path[0]][path[1]]){
				if(d.args.containsAny(['computer','desktop']))path[2]='desktop'
				else if(d.args.containsAny(['mobile','android']))path[2]='android'
				else if(d.args.containsAny(['ios','iphone']))path[2]='ios'
				if(d.misc.help[path[0]][path[1]][path[2]]){
					
				}else{
					
				}
			}else{
				e.sendMessage("I don't understand what you're trying to do. Please include the question in question in your question and try again.").queue()
			}
		}else{
			e.sendMessage("I don't understand what you're trying to manipulate. Please include the name of the Discord object in question in your question and try again.").queue()
		}
	}
	String category='General'
	String help="""`guide [task]` will make me teach you how to do something on Discord.
Because the common IQ is depleting by the minute."""
}
*/