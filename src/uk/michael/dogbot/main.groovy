package uk.michael.dogbot

/**import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import java.util.regex.Pattern**/
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
import java.net.*
import java.awt.*
import java.awt.List as AWTList
import java.util.List
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import com.mashape.unirest.http.Unirest
@Grab(group = 'org.jsoup', module = 'jsoup', version = '1.8.3')
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import groovy.json.*



JDABuilder builder = new JDABuilder(AccountType.BOT)
builder.setToken(new File('token').readLines()[0])
builder.setBulkDeleteSplittingEnabled(false)
builder.addEventListener(new GRover())
builder.buildBlocking()




class Bot {
	List prefixes = ['-', '~']
	List mention = ['<@119184325219581952> ', '<@!119184325219581952> ']
	String owner = '107894146617868288'
	String libber = '107562988810027008'
	List commands = [
		new SayCommand(), new PlayCommand(), new UserinfoCommand(), new ServerinfoCommand(), new ChannelinfoCommand(),
		new RoleinfoCommand(), new EmoteinfoCommand(), new AvatarCommand(), new InfoCommand(), new HelpCommand(),
		new JoinCommand(), new GoogleCommand(), new YouTubeCommand(), new ImageCommand(),
		new AnimeCommand(), new WebsiteCommand(), new MarioMakerCommand(), new DefineCommand(),
		new UrbanCommand(), new TagCommand(), new MiscCommand(), new TextCommand(), new ChatBoxCommand(),
		new IdentifyCommand(), new IrlCommand(), new AgeCommand(), new AreaCommand(), new AltsCommand(),
		new MinecraftCommand(), new TimeCommand(), new ChooseCommand(), new EventsCommand(), new ColourCommand(),
		new StatsCommand(), new LoveCommand(), new BallCommand(), new SetAvatarCommand(), new SetPrefixCommand(),
		new EvalCommand(), new WordCountCommand(), new LogCommand(), new ScopeCommand(), new FeedCommand(),
		new ClearCommand(), new SetChannelCommand(), new VotePinCommand(), new ConfigCommand(), new SingCommand(),
		new AccessCommand(),new TrackerCommand(), new IsupCommand(), new TopCommand(), new CleanCommand(), new NoteCommand(),
		new ProfileCommand(), new CustomCommand(), new PwnedCommand(), new MathCommand(), new MapCommand(),
		new SourceCommand(), new EmojiCommand(), new PingmodCommand(), new CategoryinfoCommand(), new PingCommand(),
		new KawaiiCommand(), new AltdetectCommand(), new SetServerCommand(), new PlayingCommand(), new SlotsCommand(),
		new FortuneCommand(), new EntryCommand(), new EventsCommandNew(), new MarkovCommand(),new JsonCommand(),
		new WaitCommand()
	]
	String oauth = 'https://discordapp.com/oauth2/authorize?client_id=170646931641466882&scope=bot&permissions=335932438'
	String server = 'https://discord.gg/0vJZEroWHiGWWQc7'
}



class Command {
	List aliases = []
	boolean dev = false
	int limit = 10
	Map pool = [:]
	String category = 'Uncategorized'
	String help = '\u00af\\_(\u30c4)_/\u00af'
}



class Web {
	Map agents = [
		'Chrome': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36',
		'Firefox': 'Mozilla/5.0 (Windows NT 6.1; rv:50.0) Gecko/20100101 Firefox/50.0',
		'3DS': 'Mozilla/5.0 (Nintendo 3DS; U; ; en) Version/1.7498.US',
		'API': 'Groovy/2.4.5 DiscordBot (https://github.com/DV8FromTheWorld/JDA, 3.0)'
	]
	Document get (String url, String agent = 'Chrome') {
		Jsoup.connect(url).userAgent(agents[agent]).get()
	}
	String sendStats (Event e) {
		Unirest.post("https://bots.discord.pw/api/bots/$e.jda.selfUser.id/stats").header('Content-Type', 'application/json')
		.header('Authorization', new File('token').readLines()[4]).body(JsonOutput.toJson(server_count: e.jda.guilds.size())).asString().body
	}
	File download (String from, String to, String agent = 'API') {
		new File(to) << from.toURL().newInputStream(requestProperties: ['User-Agent': agents[agent], Accept: '*/*'])
	}
}



class JSON {
	String root = 'data/'
	String end = '.json'
	Map load (String donkey) {
		Map ass = new JsonSlurper().parse(new File(root + donkey + end), 'UTF-8')
	}
	void save (Map diddy,String dixie) {
		new File(root + dixie + end).write(JsonOutput.prettyPrint(JsonOutput.toJson(diddy)))
	}
}



class TooManyLoopsException extends Exception {
	int limit
	String message = "Exceeded limit of $limit loops"
	TooManyLoopsException(int limit) { this.limit = limit }
}



class GRover extends ListenerAdapter {
	Bot bot = new Bot()
	Web web = new Web()
	JSON json = new JSON()
	Map db = json.load('database')
	Map tags = json.load('tags')
	Map channels = json.load('channels')
	Map info = json.load('properties')
	Map colours = json.load('colours')
	Map misc = json.load('misc')
	Map conversative = json.load('conversative')
	Map feeds = json.load('feeds')
	Map settings = json.load('settings')
	Map temp = json.load('temp')
	Map tracker = json.load('tracker')
	Map notes = json.load('notes')
	Map customs = json.load('customs')
	Map modes = json.load('modes')
//	Map rpg = json.load('rpg')
	String lastReply
	final long started = System.currentTimeMillis()
	List messages = []
	Closure errorMessage = {
		'**' + ["Let's try that again.", "Bots aren't your strong point. I can tell.", "Watch how an expert does it.", "You're doing it wrong.",
		"Nah, it's more like this.", "She wants to know if you're really a tech person.", "Consider the following:", "git: 'gud' is not a git command. See 'git --help'.",
		"What the hell is this?", "Looks like your IQ needs a little adjusting."].random() + '**\n'
	}
	Closure permissionMessage = {
		'**' + ["The desire for something becomes stronger when you can't have it.", "You may look, but don't touch.","What could go wrong in allowing that for everyone?",
		"Access is denied to that.", "I can't exploit your bot, therefore it sucks.","I can't let you do that, Star Fox.",
		"There are function keys beyond F12. You are not ready for them.", "You don't have a license to do that.", "Are you saying I'm stupid?",
		"Yeah I'm gonna let you do that.", "You need to check your privelege."].random() + '**\n'
	}
	Closure failMessage = {
		'**' + ["JDA, why you no user-friendly?!", "I have succeeded in my failure. Proud of me?", "What on earth was that?", "Hey, that was supposed to work. No fair.",
		"LOSE LOSE LOSE LOSE LOSE LOSE!","I thought I fixed that.", "It's java again isn't it?", "Let's motivate it with a controlled shock.", "ALART.",
		"Oops, my system crashed.", "Guys! Bad code is being attacked by bad code!"].random() + '**\n'
	}
	
	
	// Methods
	GRover() {
		new GroovyShell().evaluate(new File('../Libraries/MichaelsUtil.groovy'))
		new GroovyShell().evaluate(new File('methods.groovy'))
		User.metaClass.getIdentity = { db.find{ delegate.id in it.value.ids }?.value?.aka?.getAt(0) ?: delegate.name}
		User.metaClass.getRawIdentity = { db.find{ delegate.id in it.value.ids }?.value?.aka?.getAt(0)}
		Channel.metaClass.isSpam = {channels.spam[delegate.id] || (channels.spam[delegate.id] == null) && delegate.name.toLowerCase().containsAny(['spam', 'test', 'shitpost'])}
		Channel.metaClass.isLog = {channels.log[delegate.id] || (channels.log[delegate.id] == null) && delegate.name.toLowerCase().endsWithAny(['-log', 'logs'])}
		Channel.metaClass.isNsfw = {channels.nsfw[delegate.id] || (channels.nsfw[delegate.id] == null) && delegate.name.toLowerCase().containsAny(['nsfw', 'porn', 'hentai'])}
		Channel.metaClass.isSong = {channels.song[delegate.id] || (channels.song[delegate.id] == null)}
		Channel.metaClass.isIgnored = {channels.ignored[delegate.id]}
		List.metaClass.lang = { Event e ->
			List langs = ['United States/United Kingdom', 'Netherlands', 'Brazil/Portugal', 'Poland'][0..(delegate.size() - 1)]
			Map entry = db.find{e.author.id in it.value.ids}?.value
			int index = langs.find{entry?.area?.endsWithAny(it.tokenize('/').toList())}.index(langs)
			delegate[(index < 0) ? 0 : index]
		}
	}
	
	
	// Ready Event
	void onReady(ReadyEvent e) {
		Thread.start{
/*			e.jda.guilds.findAll{!roles.member[it.id]}.each{
				roles.member[it.id] = it.roles.findAll{
					!it.managed && !it.colour && !it.config
				}.max{ Role role ->
					role.guild.members*.roles.flatten()*.id.count(role.id)
				}?.id
			}
			e.jda.guilds.findAll{!roles.mute[it.id]}.each{
				roles.mute[it.id] = it.roles.findAll{
					!it.managed && it.name.toLowerCase().containsAny(['mute', 'shun', 'naughty', 'punish']) && !it.config
				}.max{ Role role ->
					role.guild.members*.roles.flatten()*.id.count(role.id)
				}?.id
			}
			json.save(roles,'roles')*/
			while(true) {
				notes.timed.each{ Map note ->
					if(note.time<System.currentTimeMillis()) {
						try {
							User user = e.jda.users.find{it.id == note.user}
							user.openPrivateChannel().complete()
							user.privateChannel.sendMessage("It is ${new Date().format('HH:mm:ss, d MMMM YYYY').formatBirthday()} (UK time). You asked me to remind you${if(note.content){":\n\n$note.content"}else{'.'}}").queue()
						} catch(ex) {
							ex.printStackTrace()
						}
						notes.timed -= note
						json.save(notes, 'notes')
					}
				}
				notes.user.each{ Map note ->
					if( !(e.jda.guilds*.members.flatten().find{it.user.id == note.mention}?.status in ['offline', null])) {
						try{
							User user = e.jda.users.find{ it.id == note.user }
							user.openPrivateChannel().complete()
							user.privateChannel.sendMessage("${e.jda.users.find{it.id==note.mention}.identity.capitalize()} is online. You asked me to tell you${if(note.content){":\n\n$note.content"}else{"."}}").queue()
						}catch(ex){
							ex.printStackTrace()
						}
						notes.user -= note
						json.save(notes, 'notes')
					}
				}
				Thread.sleep(60000)
			}
		}
		if (info.game) {
			if (info.act == 2) e.jda.listen(info.game)
			else if (info.act) e.jda.watch(info.game)
			else e.jda.play(info.game)
		}
		e.jda.guilds.findAll{ !(it.id in info.servers) }.each{
			if (it.users.count{it.bot} > 15) {
				println("Joined and left a bot collection server: $it.name")
				it.leave().queue()
			} else {
				final User axew = e.jda.users.find{it.id == bot.owner}
				println("Joined a new server: $it.name")
				try {
					it.defaultChannel.sendMessage("Woof! Hello, humans of $it.name. I'm a bot made by $axew.name#$axew.discriminator.\nUse `-help` for my command list or `-info` to learn more about me.").complete()
					info.servers += it.id
					json.save(info, 'properties')
				} catch (ex) {
					println("However, I don't have permission to post a message in the default channel, so I'll wait until next startup.")
				}
			}
		}
		json.save(info, 'properties')
		Thread.start{
			while(true){
				try{
					List channels = e.jda.textChannels + e.jda.privateChannels
					Map cache=[:]
					feeds.youtube.clone().each{Map feed->
						def channel=channels.find{it.id==feed.channel}
						if(feed.user&&!channel){
							try{
								channel=e.jda.users.find{it.id==feed.user}.openPrivateChannel().complete()
							}catch(ex){
								println("Failed to create DM channel for $feed.user when unable to speak in designated channel!")
							}
						}
						if(channel){
							try{
								Document doc=cache[feed.link]?:web.get(feed.link)
								cache[feed.link]=doc
								boolean done
								int past=0
								List out=[]
								String title=doc.getElementsByTag('title').text().tokenize().join(' ')
								while(!done){
									try{
										String id=doc.getElementsByClass('yt-lockup-title')[past].getElementsByTag('a')[0].attr('href')
										if(id!=feed.last){
											out+=id
											past+=1
										}else{
											done=true
										}
									}catch(end){
										end.printStackTrace()
										done=true
									}
								}
								if(out){
									feeds.youtube.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=out[0]
									out=out.reverse()
									if(out.size()<20)channel.sendMessage("**New video${(out.size()==1)?'':'s'} from $title**:\n"+out.collect{"https://www.youtube.com$it"}.join('\n')).queue()
									else println("There were either over 15 new videos from $title, or the latest video was deleted.")
								}
							}catch(bad){
								bad.printStackTrace()
							}
						}
					}
					Thread.sleep(6000)
					if(bot.commands.find{'anime'in it.aliases}.available){
						feeds.animelist.clone().each{Map feed->
							def channel=channels.find{it.id==feed.channel}
							if(feed.user&&!channel){
								try{
									channel=e.jda.users.find{it.id==feed.user}.openPrivateChannel().complete()
								}catch(ex){
									println("Failed to create DM channel for $feed.user!")
								}
							}
							if(channel){
								try{
									Document doc=cache[feed.link]?:web.get(feed.link)
									cache[feed.link]=doc
									boolean done
									int past=0
									List out=[]
									String title=doc.getElementsByTag('title')[0].text().tokenize()[0]
									while(!done){
										try{
											Element anime=doc.getElementsByTag('item')[past]
											List data=anime.getElementsByTag('description')[0].text().replace(' episodes','').split(' - ')
											if(!data[1])data=['Re-watching',data[0]]
											String name=anime.getElementsByTag('title')[0].text()
											String id="$name/${data[1].tokenize()[0]}"
											if(id!=feed.last){
												String link=anime.getElementsByTag('link')[0].text()
												out+=[[data[0],data[1],name,link]]
												past+=1
											}else{
												done=true
											}
										}catch(end){
											end.printStackTrace()
											done=true
										}
									}
									if(out){
										feeds.animelist.find{(it.link==feed.link)&&(it.channel==channel.id)}.last="${out[0][2]}/${out[0][1].tokenize()[0]}"
										out=out.reverse()
										/*if(out.size()<20)*/channel.sendMessage("**New episode${(out.size()==1)?'':'s'} on $title anime list**:\n"+out.collect{"${it[0]}: Episode ${it[1]} of ${it[2]}.\n<${it[3]}>"}.join('\n')).queue()
										/*else println("There were either over 15 new episodes on $title anime list, or the latest episode was deleted.")*/
									}
								}catch(bad){
									bad.printStackTrace()
								}
							}
						}
					}
					Thread.sleep(6000)
					feeds.twitter.clone().each{Map feed->
						def channel=channels.find{it.id==feed.channel}
						if(feed.user&&!channel){
							try{
								channel=e.jda.users.find{it.id==feed.user}.openPrivateChannel().complete()
							}catch(ex){
								println("Failed to create DM channel for $feed.user!")
							}
						}
						if(channel){
							try{
								Document doc=cache[feed.link]?:web.get(feed.link)
								cache[feed.link]=doc
								boolean done
								int past=doc.getElementsByClass('js-pinned-text')?1:0
								List out=[]
								String title=doc.getElementsByClass(/*'ProfileHeaderCard-nameLink'*/'fullname')[0].text()
								String user=doc.getElementsByClass(/*'u-linkComplex-target'*/'screen-name')[0].text()
								while(!done){
									try{
//										String id=doc/getElementsByClass('tweet-timestamp')[past].attr('data-conversation-id')
										String id=doc.getElementsByClass('tweet')[past].getElementsByClass('tweet-text')[0].attr('data-id')
										if(id!=feed.last){
											out+=id
											past+=1
										}else{
											done=true
										}
									}catch(end){
										end.printStackTrace()
										done=true
									}
								}
								out=out.unique()
								if(out){
									feeds.twitter.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=out[0]
									out=out.reverse()
									if(out.size()<20)channel.sendMessage("**New tweet${(out.size()==1)?'':'s'} from $title**:\n"+out.collect{"https://twitter.com/$user/status/$it"}.join('\n')).queue()
									else println("There were either over 15 new tweets from $title, or the latest tweet was deleted.")
								}
							}catch(bad){
								bad.printStackTrace()
							}
						}
					}
					Thread.sleep(6000)
					if(bot.commands.find{'levelpalace'in it.aliases}.available){
						feeds.levelpalace.clone().each{Map feed->
							def channel=channels.find{it.id==feed.channel}
							if(feed.user){
								try{
									channel=e.jda.users.find{it.id==feed.user}.openPrivateChannel().complete()
								}catch(ex){
									println("Failed to create DM channel for $feed.user!")
								}
							}
							if(channel){
								try{
									Document doc=cache[feed.link]?:web.get(feed.link)
									cache[feed.link]=doc
									boolean done
									int past=1
									List out=[]
									String title
									while(!done){
										try{
											Elements level=doc.getElementsByClass('levels-table')[0].getElementsByTag('tbody')[0].getElementsByTag('tr')[past].getElementsByTag('td')
											String id=level[0].getElementsByTag('a')[0].attr('href')
											if(id!=feed.last){
												title=level[1].text()
												out+=[[level[0].text(),id,level[2].text().replace('Super Mario Flash','SMF'),level[3].text()]]
												past+=1
											}else{
												done=true
											}
										}catch(end){
											end.printStackTrace()
											done=true
										}
									}
									if(out){
										feeds.levelpalace.find{(it.link==feed.link)&&(it.channel==channel.id)}.last=out[0][1]
										out=out.reverse()
										if(out.size()<20)channel.sendMessage("**New level${(out.size()==1)?'':'s'} from $title**:\n"+out.collect{"${it[0]} (${it[2]} / ${it[3]}).\n<https://levelpalace.com/${it[1]}>"}.join('\n')).queue()
										else println("There were either over 15 new levels from $title, or the latest level was deleted.")
									}
								}catch(bad){
									bad.printStackTrace()
								}
							}
						}
					}
					cache=[:]
					json.save(feeds,'feeds')
				}catch(ex){
					ex.printStackTrace()
				}
				Thread.sleep(3600000)
			}
		}
	}
	
	
	// Message Create Event
	void onMessageReceived(MessageReceivedEvent e){
		if (!(e.author.bot || e.channel.ignored)) {
			def args = e.message.contentRaw
			final String prefix = args.startsWithAny(e.guild ? (settings.prefix[e.guild.id] ?: bot.prefixes) + bot.mention : bot.prefixes)
			if (prefix != null) {
				Thread.start{
					args = args.substring(prefix.size())
					String alias
					Command cmd = bot.commands.find{alias = (args.toLowerCase() + ' ').startsWithAny(it.aliases*.plus(' '))?.trim()}
					Map binding = [bot: bot, json: json, web: web, prefix: prefix, alias: alias, args: args, db: db, tags: tags, channels: channels,
					info:info, colours: colours, misc: misc, conversative: conversative, feeds: feeds, settings: settings, temp: temp, modes: modes,
					tracker: tracker, notes: notes, customs: customs, started: started, messages: messages, errorMessage: errorMessage,
					permissionMessage: permissionMessage, failMessage: failMessage]
					if (cmd) {
						int status = 200
						if (cmd.pool[e.author.id]) {
							Thread.start{
								long time = -((System.currentTimeMillis() - cmd.limit * 100) - cmd.pool[e.author.id]) / 1000
								if (time < 1) time = 1
								Message cool = e.sendMessage(["You can do that again in $time second${time==1?'':'s'}.", "Je kan gebruik dat in $time second${time==1?'':'en'}.", "Voce pode usar isso novamente em segundo${time==1?'':'s'} $time.", "Mozesz z niej skorzystac w ciagu $time sekund."].lang(e)).complete()
								Thread.sleep(9999)
								cool.delete().queue()
							}
							status = 429
						} else {
							try {
								args = args.substring(alias.length()).trim()
								binding.args = args
								def response = cmd.run(binding,e)
								if (response?.class == Integer) status = response
								if (status == 200) {
									Thread.start{
										cmd.pool[e.author.id] = System.currentTimeMillis()
										Thread.sleep(cmd.limit * 100)
										cmd.pool.remove(e.author.id)
									}
								}
							} catch(ex) {
								try {
									e.sendMessage(failMessage() + ["Error: `$ex.message`.", "Fout: `$ex.message`.", "Erro: `$ex.message`.", "Blad: `$ex.message`."].lang(e)).complete()
									ex.printStackTrace()
									status=500
								} catch(ex2) {
									try {
										e.author.openPrivateChannel().complete()
										e.author.privateChannel.sendMessage(["Looks like I don't have permission to bark up this tree. Ask an administrator to let me speak in <#$e.channel.id>.", "Het lijkt erop dat ik geen toestemming heb om hier nieuwe haring te eten. Vraag een beheerder om mij in <#$e.channel.id> te laten spreken.", "Parece que n\u00e3o tenho permiss\u00e3o para cozinhar a paella aqui aqui. Pe\u00e7a a um administrador que me deixe falar em <#$e.channel.id>.", "Wygl\u0105da na to, \u017ce nie mam tutaj prawa do pisania. Popro\u015b administratora, aby pozwoli\u0142 mi pisa\u0107 na kanale <#$e.channel.id>."].lang(e)).queue()
										ex2.printStackTrace()
										status = 416
									} catch (ex3) {
										// welp
									}
								}
							}
							messages += e.message
						}
						e.jda.textChannels.find{it.id == '270998683003125760'}.sendMessage("""\u200b
<:grover:234242699211964417> `Command Log`
**Server**: ${e.guild?.name?:'Direct Messages'} (${e.guild?.id?:e.jda.selfUser.id})
**Channel**: ${e.guild?e.channel.name:e.channel.user.name} ($e.channel.id)
**User**: $e.author.identity ($e.author.id)
**Command**: ${cmd.aliases.join('/')}
**Arguments**: $args
**Status**: $status""").queue()
					} else if (e.guild) {
						Map custom = customs[e.guild.id]?.find{(args.toLowerCase() + ' ').startsWith(it.name.plus(' '))}
						if (custom) {
							custom.list.each{ Map mini ->
								int status = 200
								args = mini.args.addVariables(e, args.substring(args.tokenize()[0].length()).trim())
								binding.args = args
								cmd = bot.commands.find{mini.command in it.aliases}
								if (!cmd) {
									e.sendMessage("Huh. Seems like the command being called no longer exists.").queue()
								} else if (cmd.pool[e.author.id]) {
									Thread.start{
										long time = -((System.currentTimeMillis() - cmd.limit * 100) - cmd.pool[e.author.id]) / 1000
										if (time < 1) time = 1
										Message cool = e.sendMessage(["You can do that again in $time second${time==1?'':'s'}.", "Je kan gebruik dat in $time second${time==1?'':'en'}.", "Voce pode usar isso novamente em segundo${time==1?'':'s'} $time.", "Mozesz z niej skorzystac w ciagu $time sekund."].lang(e)).complete()
										Thread.sleep(9999)
										cool.delete().queue()
									}
									status = 429
								} else {
									try {
										def response = cmd.run(binding,e)
										if (response?.class == Integer) status = response
										if (status == 200) {
											Thread.start{
												cmd.pool[e.author.id] = System.currentTimeMillis()
												Thread.sleep(cmd.limit * 100)
												cmd.pool.remove(e.author.id)
											}
										}
									} catch(ex) {
										try {
											e.sendMessage(failMessage() + ["Error: `$ex.message`.", "Fout: `$ex.message`.", "Erro: `$ex.message`.", "Blad: `$ex.message`."].lang(e)).complete()
											ex.printStackTrace()
											print('\7')
											status = 500
										} catch(ex2) {
											try {
												e.author.openPrivateChannel().complete()
												e.author.privateChannel.sendMessage(["Looks like I don't have permission to bark up this tree. Ask an administrator to let me speak in <#$e.channel.id>.", "Het lijkt erop dat ik geen toestemming heb om hier nieuwe haring te eten. Vraag een beheerder om mij in <#$e.channel.id> te laten spreken.", "Parece que n\u00e3o tenho permiss\u00e3o para cozinhar a paella aqui aqui. Pe\u00e7a a um administrador que me deixe falar em <#$e.channel.id>.", "Wygl\u0105da na to, \u017ce nie mam tutaj prawa do pisania. Popro\u015b administratora, aby pozwoli\u0142 mi pisa\u0107 na kanale <#$e.channel.id>."].lang(e)).queue()
												ex2.printStackTrace()
												status = 416
											} catch (ex3) {
												// welp
											}
										}
									}
									messages += e.message
								}
								messages += e.message
								e.jda.textChannels.find{it.id == '270998683003125760'}.sendMessage("""\u200b
<:grover:234242699211964417> `Command Log`
**Server**: ${e.guild?.name?:'Direct Messages'} (${e.guild?.id?:e.jda.selfUser.id})
**Channel**: ${e.guild?e.channel.name:e.channel.user.name} ($e.channel.id)
**User**: $e.author.identity ($e.author.id)
**Command**: ${cmd.aliases.join('/')} (via $custom.name)
**Arguments**: $args
**Status**: $status""").queue()
								Thread.sleep(500)
							}
							try {
								customs[e.guild.id].find{it.name == custom.name}.uses += 1
							} catch (lol) {
								e.sendMessage('Suddenly, a black hole opens up somewhere in the universe.').queue()
							}
							json.save(customs, 'customs')
						}
					}
				}
			}else{
				if (!e.guild) {
					String chat = ' ' + e.message.content.toLowerCase().replaceAll(['.', ',', '!', '?', "'", ':', ';', '(', ')', '"', '-'], '') + ' '
					if (e.message.attachment) chat += "$e.message.attachment.url "
					if (chat.contains('discordgg')) {
						e.sendMessage(["I can't accept this. Please use this instead:\n$bot.oauth", "Ik kan dit niet gebruik. Gebruik dit alsjeblieft in plaats daarvan:\n$bot.oauth", "Nao posso aceitar isso. Por favor, use isso em vez disso:\n$bot.oauth", "Nie moge u\u017cy\u0107 tego zaproszenia. U\u017cyj tego:\n$bot.oauth"].lang(e)).queue()
					}else{
						List entry = conversative.findAll{chat.contains(" $it.key ")}*.key
						if (lastReply?.length() > 9) {
							User client = e.jda.selfUser
							String add = e.message.content.capitalize().replaceEach([client.name, client.id, client.identity], ['[name]', '[id]', '[identity]'])
							if (!add.endsWithAny(['.', '!', '?', ')'])) add += ['.', '!', '?', '...'].random()
							if (!conversative[lastReply]) conversative[lastReply] = []
							conversative[lastReply] += add
							json.save(conversative, 'conversative')
						}
						String response
						if (entry) response = conversative[entry.random()].random()
						else response = conversative*.value.random().random()
						response = response.replaceEach(['[name]', '[id]', '[identity]'],[e.author.name, e.author.id, e.author.identity])
						lastReply = response.toLowerCase().replaceAll(['.', ',', '!', '?', "'", ':', ';', '(', ')', '"', '-'], '').trim()
						e.sendMessage(response).queue()
					}
				}else if(e.channel.spam){
					Thread.start{
						if (e.message.content == '(\u256f\u00b0\u25a1\u00b0\uff09\u256f\ufe35 \u253b\u2501\u253b') {
							e.sendMessage('(\u256f\u00b0\u2302\u00b0)\u256f\ufe35 \u253b\u2501\u2501\u2501\u253b').queue()
						}else if (e.message.content == '\u252c\u2500\u252c \u30ce( \u309c-\u309c\u30ce)') {
							e.sendMessage('-\u252c\u2500\u2500\u252c\u256f\u30ce( o\u200b_o\u30ce)').queue()
						}else if (e.message.content == 'ayy') {
							e.sendMessage('le mayo').queue()
						}
					}
				}
				// Smilies
/*				if(e.message.contentRaw.containsAll(['(',')'])){
					String tag=e.message.contentRaw.lastRange('(',')')
					if(tag==~/\w+/){
						try{
							File image=new File("images/xat/${tag}_xat.png")
							if(image.exists()&&(!e.guild||settings.smilies[e.guild.id])){
								e.sendFile(image).complete()
							}else if(e.guild){
								image=new File("images/cs/${tag}_${e.guild.id}.png")
								if(image.exists())e.sendFile(image).complete()
							}
						}catch(ex){
							e.message.addReaction('\u2757').queue()
						}
					}
				}*/
			}
		}
		// Markovs
		if (!e.message.content.startsWithAny(bot.prefixes)) {
			if (/*!(e.guild?.id in ['110373943822540800','165499535630663680'])*/(e.guild?.id in ['325490421419606016', '287659842330558464']) && e.message.content) {
				File file = new File("markov/${e.author.id}.txt")
				if (!file.exists()) file.createNewFile()
				file.append(("$e.message.contentRaw\r\n"), 'UTF-8')
			}
		}
/*		String log="${e.message.createTime.format('HH:mm:ss')} "
		if(e.guild)log+=("[$e.guild.name] [${e.channel.name.capitalize()}] <$e.author.identity>:\n$e.message.content")
		else log+=("[Direct Messages] [${e.channel.user.identity.capitalize()}] <$e.author.identity>:\n$e.message.content")
		if(e.message.attachment)log+="${if(e.message.content){'\n'}else{''}}[$e.message.attachment.fileName]"
		println(log)*/
	}
	
	
	// Message Delete Event
	void onMessageDelete (MessageDeleteEvent e) {
		if (e.guild) {
			Message message = messages.find{it.id == e.messageId}
			if(message) {
				def gender = db.find{message.author.id in it.value.ids}?.value?.gender
				if (gender == null) gender = 0
				e.channel.sendMessage("**${message.author.identity.capitalize()}** deleted ${['their','his','her'][gender]} command message.").queue()
				messages -= message
			}
		}
	}
	
	
	// User Join Event
	void onGuildMemberJoin (GuildMemberJoinEvent e) {
		final String message = tracker.join[e.guild.id]
		if (message) e.sendMessage(message.addVariables(e, message)).queue()
		final String role = tracker.role[e.guild.id]
		if (role) {
			e.guild.controller.addRolesToMember(e.member, [e.guild.controller.roles.find{it.id == tracker.role[e.guild.id]}]).queue()
		}
	}
	
	
	// User Leave Event
	void onGuildMemberLeave (GuildMemberLeaveEvent e) {
		final String message = tracker.leave[e.guild.id]
		if (message) e.sendMessage(message.addVariables(e, message)).queue()
	}
	
	
	// Reaction Create Event
	void onMessageReactionAdd(MessageReactionAddEvent e) {
//		if (e.guild.id == '325490421419606016') e.jda.guilds.find{it.name == 'Totally Groovy'}.channels.find{it.id == '240209353951543297'}.sendMessage("`[${new Date().format('HH:mm:ss')}]` $e.user.name#$e.user.discriminator($e.user.id) added $e.reaction.emote reaction to \"$e.message.content\"($e.message.id) in $e.guild.name($e.guild.id)").queue()
		if (e.reaction.emote.name == '\ud83d\udccc') {
			if (!e.channel.ignored) {
				final Message message = e.channel.getMessageById(e.messageId).complete()
				if (!message.pinned && (!message.guild || message.guild.selfMember.roles.any{'MESSAGE_MANAGE' in it.permissions*.toString()})){
					bot.commands.find{it.aliases[0] == 'votepin'}.votes[message.id] = message.reactions.find{it.emote.name=='\ud83d\udccc'}.users.complete().findAll{!(it.bot || (it.id == message.author.id) || it.rawIdentity?.endsWithAny(['\'s Incognito', '\'s Alternate Account']))}*.id
					int max = settings.votepin[message.guild?.id] ?: 3
					if (bot.commands.find{it.aliases[0] == 'votepin'}.votes[message.id].size() >= max) {
						try {
							message.pin().complete()
						} catch (ex) {
							message.addReaction('\u2757').queue()
						}
					}
				}
			}
		}
	}
	
	
	// Guild Create Event
	void onGuildJoin(GuildJoinEvent e) {
		if (!(e.guild.id in info.servers)) {
			if (e.guild.users.count{it.bot} > 15) {
				println("Joined and left a bot collection server: $e.guild.name")
				e.guild.leave().queue()
			} else {
				final User axew = e.jda.users.find{it.id == bot.owner}
				println("Joined a new server: $e.guild.name")
				try {
					e.guild.defaultChannel.sendMessage("Woof! Hello, humans of $e.guild.name. I'm a bot made by $axew.name#$axew.discriminator.\nUse `-help` for my command list or `-info` to learn more about me.").complete()
					info.servers += e.guild.id
					json.save(info, 'properties')
				} catch (ex) {
					println("However, I don't have permission to post a message in the default channel, so I'll wait until next startup.")
				}
			}
		}
	}
}



class SayCommand extends Command {
	List aliases = ['say']
	def run(Map d, Event e) {
		if(d.args){
			e.sendMessage(d.args.addVariables(e,d.args)).queue()
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}say [text]`.","Gebruik: `${d.prefix}say [tekst]`.","Uso: `${d.prefix}say [texto]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`say [text]` will make me repeat the text in chat.
Useful for getting someone to insult himself, but why am I telling you that?"""
}


class PlayCommand extends Command {
	List aliases = ['setstatus','play']
	int limit = 60
	def run(Map d, Event e) {
		d.args=d.args.tokenize()
		if(d.args[0]){
			if(d.args[1]&&d.args[1].toLowerCase().replace(' ','').containsAny(['jews','hitler','nazi'])){
				e.sendMessage("I'm not gonna repeat NotSoBot's mistake.").queue()
				403
			}else if((d.args[0]in['play','game','watch','video','listen','music'])&&d.args[1]){
				d.args[1]=d.args[1..-1].join(' ')
				if(d.args[1].length()>127){
					e.sendMessage("A status message that long won't even display, so don't even try it.").queue()
					403
				}else if(d.args[0]in['play','game']){
					d.info.game=d.args[1]
					d.info.act=0
					d.info.player=e.author.id
					e.jda.play(d.args[1])
					e.sendMessage("I am now playing ${d.args[1]}. Check my status!").queue()
				}else if(d.args[0]in['watch','video']){
					d.info.game=d.args[1]
					d.info.act=1
					d.info.player=e.author.id
					e.jda.watch(d.args[1])
					e.sendMessage("I am now watching ${d.args[1]}. Check my status!").queue()
				}else if(d.args[0]in['listen','music']){
					d.info.game=d.args[1]
					d.info.act=2
					d.info.player=e.author.id
					e.jda.listen(d.args[1])
					e.sendMessage("I am now listening to ${d.args[1]}. Check my status!").queue()
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}setstatus play/watch/listen [text]`.","Gebruik: `${d.prefix}setstatus play/watch/listen [tekst]`.","Uso: `${d.prefix}setstatus play/watch/listen [texto]`."].lang(e)).queue()
					400
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}setstatus play/watch/listen [text]`.","Gebruik: `${d.prefix}setstatus play/watch/listen [tekst]`.","Uso: `${d.prefix}setstatus play/watch/listen [texto]`."].lang(e)).queue()
				400
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}setstatus play/watch/listen [text]`.","Gebruik: `${d.prefix}setstatus play/watch/listen [tekst]`.","Uso: `${d.prefix}setstatus play/watch/listen [texto]`."].lang(e)).queue()
			400
		}
		d.json.save(d.info,'properties')
	}
	String category = 'General'
	String help = """`setstatus play [text]` will make me change my status to playing the thing.
`setstatus watch [text]` will make me change my status to watching the thing.
`setstatus listen [text]` will make me change my status to listening to the thing.
AWW YEAH ABUSABLE FEATURES!"""
}


class UserinfoCommand extends Command {
	List aliases = ['userinfo','user']
	def run(Map d, Event e) {
		if(e.guild){
			if(e.message.mentions.size()>1){
				List mens=e.message.mentions
				if(mens.size()>5)mens=mens[0..4]
				List shared=e.jda.guilds.findAll{List ass=it.users*.id;mens.every{it.id in ass}}.findAll{!d.modes.hidden[it.id]}*.name
				List joined=[]
				List avatar=[]
				List created=[]
				mens.each{User man->
					joined+=e.guild.members.find{it.user.id==man.id}*.joinDate*.toString()
					avatar+=man.avatarId?:man.defaultAvatarId
					created+=new Date(man.createTimeMillis).format('d MMMM yyyy').formatBirthday()
				}
				e.sendMessage("""**${mens.collect{"$it.name#$it.discriminator"}.join(', ').capitalize()}** (${mens.size()}): ```css
IDs: ${mens*.id.join(', ')}
Names: ${mens*.identity.join(', ')}
Avatars: ${avatar.join(', ')}
Created: ${created.join(', ')}
Joined: ${joined.join(', ')}
Shared: ${if(shared.size()>9){shared[0..9].join(', ')+'..'}else{shared.join(', ')}} (${shared.size()})
${if(mens.every{it.bot}){'Discord bots'}else if(mens.every{!it.bot}){'Regular users'}else{'Multiple values'}}```""").queue()
			}else{
				User user=e.author
				if(d.args)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
				Member member=e.guild.members.find{it.user.id==user?.id}
				if(user){
					List shared=e.jda.guilds.findAll{user in it.users}.findAll{!d.modes.hidden[it.id]}*.name
					e.sendMessage("""**${user.name.capitalize()}#$user.discriminator** is $member.status: ```css
ID: $user.id
Name: $user.identity
Avatar: ${user.avatar?:user.defaultAvatar}
Created: ${new Date(user.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Joined: ${e.guild.members.find{it.user.id==e.author.id}.joinDate}
Shared: ${if(shared.size()>9){shared[0..9].join(', ')+'..'}else{shared.join(', ')}} (${shared.size()})
${if(user.bot){'Discord bot'}else if(member.owner){'Server owner'}else{'Regular user'}}```""").queue()
				}else{
					e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
					404
				}
			}
		}else{
			User user=e.author
			List shared=e.jda.guilds.findAll{user in it.users}.findAll{!d.modes.hidden[it.id]}*.name
			e.sendMessage("""**${user.name.capitalize()}#$user.discriminator** is ${e.jda.guilds.find{user.id in it.users*.id}.members.find{it.id==user.id}.status}: ```css
ID: $user.id
Name: $user.identity
Avatar: ${user.avatar?:user.defaultAvatar}
Created: ${new Date(user.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Shared: ${if(shared.size()>9){shared[0..9].join(', ')+'...'}else{shared.join(', ')}} (${shared.size()})```""").queue()
		}
	}
	String category = 'General'
	String help = """`userinfo [user]` will make me tell you some useful information about the user.
Where they live is not included this time."""
}


class ServerinfoCommand extends Command {
	List aliases = ['serverinfo','server']
	def run(Map d, Event e) {
		if(e.guild||d.args){
			Guild guild=e.guild
			if(d.args)guild=e.jda.findGuild(d.args)
			if(guild){
				if(d.modes.hidden[guild.id]){
					e.sendMessage(["I couldn't find a server matching '$d.args.'","Ik kon niet vind een guild vind '$d.args' leuk.","N\u00e3o consegui encontrar um servidor que corresponda '$d.args.'","Nie mog\u0119 znale\u017a\u0107 serwera pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
					403
				}else{
					int timeout=guild.afkTimeout.seconds/60
					e.sendMessage("""**${guild.name.capitalize()}** owned by $guild.owner.user.identity: ```css
ID: $guild.id
Icon: $guild.icon?size=1024
Region: $guild.region
Opened: ${new Date(guild.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
AFK: ${guild.afkChannel?guild.afkChannel.name+', ':''}$timeout minute${timeout==1?'':'s'}
Roles: ${if(guild.roles.size()>4){guild.roles[0..4]*.name.join(', ')+'..'}else{guild.roles*.name.join(', ')}} (${guild.roles.size()})
Users: ${if(guild.users.size()>4){guild.users[0..4]*.identity.join(', ')+'..'}else{guild.users*.identity.join(', ')}} (${guild.users.size()})
Categories: ${if(guild.categories.size()>4){guild.categories[0..4]*.name.join(', ')+'..'}else{guild.categories*.name.join(', ')}} (${guild.categories.size()})
Channels: ${if(guild.textChannels.size()>1){guild.textChannels[0..1]*.name.join(', ')+'..'}else{guild.textChannels*.name.join(', ')}}${if(guild.voiceChannels){", ${if(guild.voiceChannels.size()>1){guild.voiceChannels[0..1]*.name.join(', ')+'..'}else{guild.voiceChannels*.name.join(', ')}}"}else{''}} (${guild.textChannels.size()}, ${guild.voiceChannels.size()})
Emotes: ${if(guild.emotes.size()>4){guild.emotes[0..4]*.name.join(', ')+'..'}else{guild.emotes*.name.join(', ')}} (${guild.emotes.size()})
${if(guild.users.size()>249){'Large server'}else{'Small server'}}```""").queue()
				}
			}else{
				e.sendMessage(["I couldn't find a server matching '$d.args.'","Ik kon niet vind een guild vind '$d.args' leuk.","N\u00e3o consegui encontrar um servidor que corresponda '$d.args.'","Nie mog\u0119 znale\u017a\u0107 serwera pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
				404
			}
		}else{
			User user=e.jda.selfUser
			e.sendMessage("""**Direct Messages** owned by $user.identity: ```css
ID: $user.id
Icon: $user.avatar?size=1024
Region: London
Opened: ${new Date(user.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})```""").queue()
		}
	}
	String category = 'General'
	String help = """`serverinfo [server]` will make me tell you some useful information about the server.
How may I server you, master?"""
}


class ChannelinfoCommand extends Command {
	List aliases = ['channelinfo','channel']
	def run(Map d, Event e) {
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
Created: ${new Date(channel.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Users: ${if(channel.users.size()>4){channel.users[0..4]*.identity.join(', ')+'..'}else{channel.users*.identity.join(', ')}} (${channel.users.size()})
${if(channel.class==TextChannelImpl){"Last Activity: ${try{channel.history.retrievePast(1).complete()[0].createTime.format('HH:mm:ss, d MMMM yyyy')}catch(CantView){'???'}}"}else{"Bit Rate: ${channel.bitrate/1000} kbps"}}
${if(props){"Properties: ${props.join(', ')}"}else{'None'}}
${if(channel.guild.defaultChannel==channel){'Default '}else if(channel.guild.afkChannel==channel){'AFK '}else{''}}${channel.class.simpleName-'ChannelImpl'} channel```""").queue()
			}else{
				e.sendMessage("""**${channel.user.name.capitalize()}'s DM** in Direct Messages: ```css
ID: $channel.id
Created: ${new Date(channel.createTimeMillis+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()})
Users: $e.jda.selfUser.identity, $e.author.identity (2)
Last Activity: ${channel.history.retrievePast(1).complete()[0].createTime.format('HH:mm:ss, d MMMM yyyy')}
Properties: NSFW, Song
Direct text```""").queue()
			}
		}else{
			e.sendMessage(["I couldn't find a channel matching '$d.args.'","Ik kon niet vind een kanaal vind '$d.args' leuk.","N\u00e3o consegui encontrar um kanal que corresponda '$d.args.'","Nie mog\u0119 znale\u017a\u0107 kana\u0142 pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}
	}
	String category = 'General'
	String help = """`channelinfo [channel]` will make me tell you some useful information about the channel.
This is getting a little mundane, isn't it?"""
}


class RoleinfoCommand extends Command {
	List aliases = ['roleinfo','role']
	def run(Map d, Event e) {
		if(e.guild){
			Role role
			if(d.args&&e.guild)role=e.message.mentionedRoles?e.message.mentionedRoles[-1]:e.guild.findRole(d.args)
			if(role){
				List collection=role.guild.members.findAll{role.id in it.roles*.id}*.user*.identity
				e.sendMessage("""**${role.name.capitalize()}** in $role.guild.name: ```css
ID: $role.id
Colour: ${if(role.color){role.color.collect{"rgb($it.red,$it.blue,$it.green)"}[0]}else{'Default'}}
Created: ${new Date(role.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Users: ${if(collection.size()>9){collection[0..9].join(', ')+'..'}else if(collection){collection.join(', ')}else{'Nobody'}} (${collection.size()})
${if(role.managed){'Integrated'}else if(role.colour){'Colour'}else if(role==role.guild.defaultRole){'Default'}else{'Regular'}} role```""").queue()
			}else{
				e.sendMessage(["I couldn't find a role matching '$d.args.'","Ik kon niet vind een rol vind '$d.args' leuk.","N\u00e3o consegui encontrar um role que corresponda '$d.args.'","Nie mog\u0119 znale\u017a\u0107 rola pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
				404
			}
		}else{
			User user=e.jda.selfUser
			e.sendMessage("""**@everyone** in Direct Messages: ```css
ID: $user.id
Colour: Default
Created: ${new Date(user.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Users: ${try{(e.jda.privateChannels*.user*.identity+user.identity)[0..14].join(', ')+'..'}catch(NotThatMany){(e.jda.privateChannels*.user*.identity+user.identity).join(', ')}} (${e.jda.privateChannels.size()+1})
Default role```""").queue()
		}
	}
	String category = 'General'
	String help = """`roleinfo [role]` will make me tell you some useful information about the role.
How many of you have that NSFW role..."""
}


class EmoteinfoCommand extends Command {
	List aliases = ['emoteinfo','emote']
	def run(Map d, Event e) {
		Emote emote
		if(d.args)emote=e.message.emotes?e.message.emotes[-1]:e.jda.findEmote(d.args)
		if(emote){
			e.sendMessage("""**${emote.name.capitalize()}** in ${if(emote.guild){emote.guild.name}else{'???'}}: ```css
ID: $emote.id
Uploaded: ${new Date(emote.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Image: $emote.imageUrl
${if(emote.managed){'Integrated'}else if(emote.animated){'Animated'}else{'Regular'}} emote```""").queue()
		}else if(d.args){
			e.sendMessage(["I couldn't find an emote matching '$d.args.'","Ik kon niet vind een emote vind '$d.args' leuk.","N\u00e3o consegui encontrar um emote que corresponda '$d.args.'","Nie mog\u0119 znale\u017a\u0107 emote pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}emoteinfo [emote]`.","Gebruik: `${d.prefix}emoteinfo [emote]`.","Uso: `${d.prefix}emoteinfo [emote]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`emoteinfo [emote]` will make me tell you some useful information about the emote.
As of a recent update, I can probably use it."""
}


class AvatarCommand extends Command {
	List aliases = ['avatar','icon']
	def run(Map d, Event e) {
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
				if(!guild||d.modes.hidden[guild.id]){
					e.sendMessage(["I couldn't find a user or server matching '$d.args.'","Ik kon niet vind een gebruiker of guild vind '$d.args' leuk."].lang(e)).queue()
					guild?404:403
				}else{
					if(guild.icon){
						e.sendMessage("**${guild.name.capitalize()}**'s icon:\n${guild.icon.replace('.jpg','.png')}?size=1024").queue()
					}else{
						e.sendMessage("**${guild.name.capitalize()}**'s icon:\n`${guild.name.abbreviate()}`").queue()
					}
				}
			}
		}
	}
	String category = 'General'
	String help = """`avatar [user/server/emote]` will make me get the avatar/icon of them.
Now tilt your head..."""
}


class InfoCommand extends Command {
	List aliases = ['info']
	def run (Map d, Event e) {
		String owner=d.db.find{d.bot.owner in it.value.ids}?.value.aka[0]
		String info=["""**About GRover**:
Created by ${owner}. Library: JDA 3.0

GRover \u2018the DOGBOT Project\u2019 is a bot with an ever-expanding database recording the internet identity of everyone on Discord.
GRover is based on the xat FEXBot and was designed to remedy the issue of recognising users who change their name.
Made before discriminators, notes, nicknames and embeds and its irrelevance will show in its functions.

Use `${d.prefix}help` to get a list of commands.

OAuth invite: <$d.bot.oauth>
Github code: <https://github.com/Axew13/GroovyRover/blob/master/main.groovy>
Official server: $d.bot.server""", """Over GRover**:
Maakt bij ${owner}. Library: JDA 3.0

GRover \u2018the DOGBOT Project\u2019 is een robot met een heel-groten databank opname de internet identiteit van alle gebruikers op Discord.
GRover is gekopieerd op de xat FEXBot en was dachte tot verwijderen de probleem van wetende gebruikers wie bewerk hun naam.
Gemaakt niet voor discriminators, notes, (guild) gebruikersnaams en in-de-babbelen instortvoorzieningen en zijn irrelevante wil tonen in zijn functies.

Gebruik `${d.prefix}help` tot krijg een lijst van commando's.

Nodig uit van OAuth: <$d.bot.oauth>
Github bewaarplaats: <https://github.com/Axew13/GroovyRover/blob/master/main.groovy>
Officieel guild: $d.bot.server""", """**Sobre GRover**:
Criado de ${owner}. Library: JDA 3.0

GRover \u2018the DOGBOT Project\u2019 e um bot com uma base de dados cada vez maior que grava a identidade da internet de todos em Discord.
O GRover e baseado no xat FEXBot e foi projetado para remediar a questao do reconhecimento de usuarios que mudam seu nome.
Feito antes de discriminadores, notas, apelidos e incorporacoes e sua irrelevancia mostrara em suas funcoes.

Usar `${d.prefix}help` para obter uma lista de comandos.

OAuth convite: <$d.bot.oauth>
Github codigo: <https://github.com/Axew13/GroovyRover/blob/master/main.groovy>
Servidor oficial: $d.bot.server""", """**O GRover**:
Stworzone przez ${owner}. Library: JDA 3.0

GRover \u2018the DOGBOT Project\u2019 jest botem z ciagle rozwijajaca sie baza danych rejestrujaca tozsamosc internetowa kazdego z Discord.
Firma GRover oparta jest na xdes FEXBot i zostala zaprojektowana, aby zaradzic kwestii uznawania uzytkownikow, ktorzy zmieniaja swoje imie.
Wyprodukowane przed dyskryminatorami, notatkami, pseudonimami i osadzeniami, a ich nieporzadek pokaze sie w jego funkcjach.

Uzyj `${d.prefix}help`, aby uzyskac liste polecen

Zaproszenie OAuth: <$d.bot.oauth>
Kod Github: <https://github.com/Axew13/GroovyRover/blob/master/main.groovy>
Oficjalny serwer: $d.bot.server"""].lang(e)
		try {
			e.author.openPrivateChannel().complete()
			e.author.privateChannel.sendMessage(info).complete()
			if(e.guild){
				e.sendMessage(["Information has been sent! <@$e.author.id>", "Informatie heb ben enviei! <@$e.author.id>", "Informacoes foram enviadas! <@$e.author.id>", "Informacja zostala wyslana! <@$e.author.id>"].lang(e)).queue{
					Thread.sleep(5000)
					it.delete().queue()
				}
			}
		} catch(alt) {
			e.sendMessage(info).queue()
		}
	}
	String category = 'General'
	String help = """`info` will make me DM you some obligatory information about myself.
That's about it."""
}


class HelpCommand extends Command {
	List aliases = ['help','commands']
	def run(Map d, Event e) {
		d.args=d.args.toLowerCase()
		if(!d.args){
			String list=''
			List commands=d.bot.commands.findAll{!it.dev&&(it.category!='RPG')}
			commands*.category.unique().each{String cat->
				list+="**$cat Commands**:\n${commands.findAll{it.category==cat}.collect{"$d.prefix${it.aliases[0]}"}.join(',  ')}\n\n"
			}
			list += ["Use `${d.prefix}help [command]` to get further assistance.", "Gebruik `${d.prefix}help [commando]` tot krijg meer informatie.", "Use `${d.prefix}help [comando]` para obter informacoes detalhadas.", "Uzyj `${d.prefix}help [rozkaz]`, aby uzyskac dodatkowa pomoc."].lang(e)
			try{
				e.author.openPrivateChannel().complete()
				list.split(1999).each{
					e.author.privateChannel.sendMessage(it).complete()
					Thread.sleep(150)
				}
				if(e.guild){
					e.sendMessage(["Help has been sent! <@$e.author.id>","Helpen heb ben enviei! <@$e.author.id>","Socorro foram enviadas! <@$e.author.id>","Pomoc zostala wyslana! <@$e.author.id>"].lang(e)).queue{
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
			e.sendMessage(["Don't include the < and >." ,'Doe niet voegen de < en >.', 'Nao inclua o < e >.', 'Nie wlaczaj < i >.'].lang(e)).queue()
			400
		}else{
			Command cmd=d.bot.commands.find{d.args in it.aliases}
			Map custom
			if(e.guild)custom=d.customs[e.guild.id].find{it.name==d.args}
			if(cmd){
				e.sendMessage("**${cmd.aliases[0].capitalize()} Command**:\n$cmd.help").queue()
			}else if(custom){
				List collected = custom.list.collect{"`$it.command $it.args`"}
				e.sendMessage(["**$custom.name Custom Command**:\n`$custom.name` will make me run:\n${collected.join('\n')}", "**$custom.name Gewoonte Commando**:\n`$custom.name` wil maak mij renn:\n${custom.join('\n')}", "**$custom.name Comando Personalizado**:\n`$custom.name` vai me fazer usar:\n${custom.join('\n')}", "**$custom.name Niestandardowe Polecenie**:\n`$custom.name` zmusi mnie:\n${custom.join('\n')}"].lang(e)).queue()
			}else{
				e.sendMessage(["I've not heard of that one.", 'Ik heb niet snap dat.', 'Eu nao sei sobre isso.', 'Nie znam tego.'].lang(e)).queue()
				404
			}
		}
	}
	String category = 'General'
	String help = """`help` will make me DM you the list of commands.
`help [command]` will make me tell you more about that command.
I think by now you understand this though."""
}


class JoinCommand extends Command {
	List aliases = ['join','invite']
	def run(Map d, Event e) {
		if(d.args.toLowerCase()=='server'){
			e.sendMessage(["Me and some other bots can be found here:\n$d.bot.server","Mij en ander roboten kan worden vinden hier:\n$d.bot.server","Eu e alguns outros bots podem ser encontrados aqui:\n$d.bot.server","Ja i niektore inne boty mozna tu znalezc:\n$d.bot.server"].lang(e)).queue()
		}else{
			e.sendMessage(["Add me to your server:\n<$d.bot.oauth>","Voegen mij op je guild:\n<$d.bot.oauth>","Adicione-me ao seu servidor:\n<$d.bot.oauth>","Dodaj mnie do swojego serwera:\n<$d.bot.oauth>"].lang(e)).queue()
		}
	}
	String category = 'General'
	String help = """`join` will make me fetch my OAuth URL.
`join server` will make me give the invite link to Totally Groovy.
Inject me all these Discord servers."""
}


class GoogleCommand extends Command {
	List aliases = ['google','search']
	int limit = 90
	Map cache=[:]
	def run(Map d, Event e) {
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
					Document doc=d.web.get(link)
					Elements links=doc.getElementsByClass('r')
					if(links){
						Element linkTag=links[0].getElementsByTag('a')[0]
						e.sendMessage("${linkTag.text().capitalize()}: ${linkTag.attr('href')}").queue()
						cache[ass]=links
					}else{
						e.sendMessage(["There are no search results for '$d.args.'\n$link","Er zijn geen zoekresultaten voor '$d.args.'\n$link"].lang(e)).queue()
					}
				}
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage(['You are being rate limited.','Je bent gebruik beperkt.','Voce esta sendo limitado a taxas.','Zostaniesz szybkosc ograniczona.'].lang(e)).queue()
					429
				}else{
					e.sendMessage(["There are no search results for '$d.args.'\n$link","Er zijn geen zoekresultaten voor '$d.args.'\n$link"].lang(e)).queue()
					404
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}google [search term]`.","Gebruik: `${d.prefix}google [zoekterm]`.","Uso: `${d.prefix}google [termo pesquisa]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`google [search term]` will make me Google it, of course.
Putting in 'google' won't break anything though."""
}


class YouTubeCommand extends Command {
	List aliases = ['youtube','yt']
	int limit = 50
	Map cache=[:]
	def run(Map d, Event e) {
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
				Document doc=d.web.get(link)
				Elements links=doc.getElementsByClass('yt-lockup-title').findAll{!it.toString().contains('https://googleads')}
				if(links){
					Element linkTag=links[0].getElementsByTag('a')[0]
					String lonk="https://youtube.com${linkTag.attr('href')}"
					if(!lonk.containsAny(['&list=','/user/','/channel/']))lonk=lonk.replace('youtube.com/watch?v=','youtu.be/')
					e.sendMessage("${linkTag.attr('title')}: $lonk").queue()
					cache[ass]=links
				}else{
					e.sendMessage(["There are no YouTube videos for '$d.args.'\n$link","Er zijn geen YouTube videos voor '$d.args.'\n$link"].lang(e)).queue()
					404
				}
			}
		}catch(none){
			e.sendMessage(["There are no YouTube videos for '$d.args.'\n$link","Er zijn geen YouTube videos voor '$d.args.'\n$link"].lang(e)).queue()
			404
		}
	}
	String category = 'Online'
	String help = """`youtube` will make me get the featured videos on YouTube.
`youtube [search term]` will make me search YouTube for that.
Getting your daily fix of Vinesauce, I see."""
}


class ImageCommand extends Command {
	List aliases = ['image','is']
	int limit = 90
	def run(Map d, Event e) {
		boolean gif=d.args.contains('GIF')
		d.args=d.args.replace('GIF','').trim()
		if(d.args.containsAny(['dead','gore','waffle','child','loli','shota'])){
			e.sendMessage('Like I would let you search up something like that! <:gtfo:318318593299316736>').queue()
		}else if(d.args){
			e.sendTyping().queue()
			String link="https://encrypted.google.com/search?q=${URLEncoder.encode(d.args,'UTF-8')}&tbm=isch"
			if(gif)link+='&tbs=itp:animated'
			try{
				Document doc1=d.web.get(link,'3DS')
				Element image=doc1.getElementsByClass('image')[0]
				Document doc2=d.web.get(image.attr('href'),'3DS')
				String imagelink=doc2.getElementById('thumbnail').attr('href')
				String trailer=imagelink.containsAny(['.png','.gif','.jpg','.svg'])
				if(trailer&&!imagelink.contains('wikimedia'))imagelink=imagelink.substring(0,imagelink.indexOf(trailer)+4)
				e.sendMessage(imagelink).queue()
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage(['You are being rate limited.','Je bent gebruik beperkt.','Voce esta sendo limitado a taxas.','Zostaniesz szybkosc ograniczona.'].lang(e)).queue()
					429
				}else{
					e.sendMessage(["There are no images for '$d.args.'\n$link","Er zijn nu afbeelden voor '$d.args.'\n$link"].lang(e)).queue()
					404
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}image [search term]`.","Gebruik: `${d.prefix}image [zoekterm]`.","Uso: `${d.prefix}image [termo pesquisa]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`image [search term]` will make me search Google Images for that.
`image [search term] GIF` will get a lot of results from tumblr.
Some people are just visual learners."""
}


class AnimeCommand extends Command {
	List aliases = ['anime']
	int limit = 70
	boolean available=false
	def run(Map d, Event e) {
		if(available){
			if(d.args){
				e.sendTyping().queue()
				String link="https://myanimelist.net/anime.php?q=${URLEncoder.encode(d.args)}"
				try{
					Document doc=d.web.get(link)
					try{
						link=doc.getElementsByClass('hoverinfo_trigger')[0].attr('href')
						doc=d.web.get(link)
						String name=doc.getElementsByTag('span').find{it.attr('itemprop')=='name'}.text().capitalize()
						String photo=doc.getElementsByClass('ac')?.attr('src')?:''
						String type=doc.getElementsByClass('type').text()
						String season=doc.getElementsByClass('season').text()
						if(season)season="/$season"
						String score=doc.getElementsByClass('score').text()
						String favourites=doc.getElementsByClass('js-scrollfix-bottom')[0].getElementsByClass('dark_text')[-1].parent().text()
						String ranked=doc.getElementsByClass('ranked')[0].getElementsByTag('strong')[0].text().replace('#','')
						String description
						try{
							description=doc.getElementsByTag('span').find{it.attr('itemprop')=='description'}.text().capitalize()
							if(description.length>1000)description=description.substring(0,1000)+'...'
						}catch(empty){
							description=['(No synopsis yet.)','(Geen korte inhound nog.)','(Ainda nao ha nenhuma sinopse.)','(Nie ma jeszcze streszczenia.)'].lang(e)
						}
						e.sendMessage("**$name**  (${type}${season})\n$photo\nScore: $score  $favourites  Rank: $ranked\n\n$description\n\n<$link>").queue()
					}catch(none){
						e.sendMessage(["No anime matching '$d.args' was found.","Geen anime vind '$d.args' leuk was vinden."].lang(e)).queue()
						404
					}
				}catch(down){
					e.sendMessage('Looks like MyAnimeList is unavailable. Press `f` to pay respects.').queue()
					down.printStackTrace()
					503
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}anime [search term]`.","Gebruik: `${d.prefix}anime [zooekterm]`.","Uso: `${d.prefix}anime [termo pesquisa]`."].lang(e)).queue()
				400
			}
		}else{
			final User axew = e.jda.users.find{it.id == d.bot.owner}
			e.sendMessage("MyAnimeList is under maintenance right now, check back later.\n(If you believe this is in error, go bug $axew.name#$axew.discriminator.)").queue()
			503
		}
	}
	String category = 'Online'
	String help = """`anime [search term]` will make me search MyAnimeList's database for the anime.
I'll even throw in a link to watch if I have one. Piracy yay."""
}


class WebsiteCommand extends Command {
	List aliases = ['website','siteinfo','site']
	int limit = 50
	def run(Map d, Event e) {
		if(d.args){
			d.args=d.args.replace(' ','-')
			if(!d.args.startsWithAny(['http://','https://']))d.args="http://$d.args"
			if(!d.args.contains('.')&&d.args)d.args+='.com'
			e.sendTyping().queue()
			List months=['January','February','March','April','May','June','July','August','September','October','November','December']
			String link="http://website.informer.com/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link)
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
				e.sendMessage(["There is no data for '$d.args.'\n$link","Er zijn geen gegevens voor '$d.args.'\n$link"].lang(e)).queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}website [domain]`.","Gebruik: `${d.prefix}website [website]`.","Uso: `${d.prefix}website [local na rede internet]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`website [domain]` will make me get the website's data on Website Informer.
You're not going to use this to DDOS, are you?"""
}


class MarioMakerCommand extends Command {
	List aliases = ['mariomaker','smm']
	int limit = 50
	def run(Map d, Event e) {
		d.args=d.args.replace('@','').replace(' ','-')
		String ass=d.args+('0'*15)
		if((ass[4]=='-')&&(ass[9]=='-')&&(ass[14]=='-')){
			e.sendTyping().queue()
			String link="https://supermariomakerbookmark.nintendo.net/courses/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link)
				String title=doc.getElementsByClass('course-title')[0].text()
				String uploader=doc.getElementsByClass('name')[0].text()
				String levelmap=doc.getElementsByClass('course-image-full')[0].attr('src')
				String date=doc.getElementsByClass('created_at')[0].text()
				String type=doc.getElementsByClass('course-tag')[0].text()
				String difficulty=doc.getElementsByClass('course-header')[0].text()
				String likes=doc.getElementsByClass('liked-count')[0].getElementsByClass('typography')*.className()*.split('-')*.last().join()
				String plays=doc.getElementsByClass('played-count')[0].getElementsByClass('typography')*.className()*.split('-')*.last().join()
				e.sendMessage("__**$title** by ${uploader}__\n$date\n**Level Type**: $type\n**Difficulty**: $difficulty\n**Map**: $levelmap\n\ud83d\udc63`$plays`   \u2b50`$likes`\n\n<$link>").queue()
			}catch(none){
				e.sendMessage(["That course doesn't exist. Ensure the course ID is correct.\n$link","Dat niveau niet bestaan. Ervoor zorgen niveau ID is correct.\n$link"].lang(e)).queue()
				404
			}
		}else if(d.args){
			e.sendTyping().queue()
			String link="https://supermariomakerbookmark.nintendo.net/profile/${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link)
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
					e.sendMessage(["There are no Super Mario Maker courses for '$d.args.'\n$link","Er zijn geen Super Mario Maker niveau vooor '$d.args.'\n$link"].lang(e)).queue()
					404
				}
			}catch(none){
				e.sendMessage(["There are no Super Mario Maker courses for '$d.args.'\n$link","Er zijn geen Super Mario Maker niveau vooor '$d.args.'\n$link"].lang(e)).queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}mariomaker [nnid/course id]`.","Gebruik: `${d.prefix}mariomaker [nnid/niveau id]`.","Uso: `${d.prefix}mariomaker [nnid/nivel id]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`mariomaker [nnid]` will make me get the latest SMM courses by the NNID.
`mariomaker [course id]` will make me get information about that course.
Dannyh09, eat your heart out."""
}


class DefineCommand extends Command {
	List aliases = ['define','dictionary']
	int limit = 50
	def run (Map d, Event e) {
		if (d.args) {
			e.sendTyping().queue()
			List defs = new JsonSlurper().parseText(Unirest.get("http://api.pearson.com/v2/dictionaries/ldoce5/entries?headword=${URLEncoder.encode(d.args, 'UTF-8')}").asString().body).results
			if (defs.size() > 4) defs = defs[0..3]
			if (defs) {
				defs = defs.collect{Map de ->
					String ass = "**${de.headword}** _${de.part_of_speech ?: 'typeless'}_"
					if (de.pronounciations) ass += " `${de.pronounciations[-1].ipa}"
					List ex=de.senses*.definition*.join('\n')
					if (ex.size() > 3) ex = ex[0..2]
					ass += "\n${ex.join('\n')}"
					if (de.examples) ass+="\n_${de.examples[0].text ?: 'no definition somehow'}_"
					ass
				}
				e.sendMessage(defs.join('\n\n')).queue()
			} else {
				e.sendMessage("There is no definition for '$d.args.'").queue()
			}
		} else {
			e.sendMessage(d.errorMessage() + ["Usage: `${d.prefix}define [word]`.", "Gebruik: `${d.prefix}define [woord]`.", "Uso: `${d.prefix}define [palavra]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`define [word]` will make me get the definition of that word.
Someone is going to call you stupid anyway."""
}


class UrbanCommand extends Command {
	List aliases = ['urban']
	int limit = 50
	def run(Map d, Event e) {
		if(d.args){
			e.sendTyping().queue()
			String link="http://www.urbandictionary.com/define.php?term=${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link)
				Element de=doc.getElementsByClass('def-panel')[0]
				if(de){
					Element worddef=de.getElementsByClass('word')[0]
					Element meaning=de.getElementsByClass('meaning')[0]
					Element example=de.getElementsByClass('example')[0]
					String definition=("**${worddef.text().capitalize()}**:\n${meaning.text().capitalize()}\n_${example.text()}_")
					if(definition.length>1500)definition=definition.substring(0,1500)+'...'
					e.sendMessage("${definition.replace('\n**','')}\n\n<$link>").queue()
				}else{
					e.sendMessage(["There is no urban definition for '$d.args.'\n$link","Er zijn geen urban-definitie voor '$d.args.'\n$link"].lang(e)).queue()
					404
				}
			}catch(none){
				e.sendMessage(["There is no urban definition for '$d.args.'\n$link","Er zijn geen urban-definitie voor '$d.args.'\n$link"].lang(e)).queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}urban [word]`","Gebruik: `${d.prefix}urban [woord]`","Uso: `${d.prefix}urban [palavra]`"].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`urban [word]` will make me get the Urban Dictionary definition of that word.
A place formerly used to find out about slang, and now a place that teens with no life use as a burn book to whine about celebrities, their friends, etc., let out their sexual frustrations, show off their racist/sexist/homophobic/anti-(insert religion here) opinions, troll, and babble about things they know nothing about. That was an example."""
}


class TagCommand extends Command {
	List aliases = ['tag','tags']
	def run(Map d, Event e) {
		d.args=d.args.split('( |\n|\r)',3).toList()
		d.args[0]=d.args[0]?.toLowerCase()
		if(e.message.attachments)d.args+=e.message.attachments*.url
		if(d.args[0]=='create'){
			if(d.args[2]){
				d.args[1]=d.args[1]?.toLowerCase()
//				String ass=d.args[1].startsWithAny(e.jda.guilds.findAll{e.author in it.users}.id*.plus(':'))
				if(d.args[1].containsAny(['@everyone','@here'])){
					e.sendMessage(['You cannot create a tag with that name.','Je kan niet maken een tag met dat naam.'].lang(e)).queue()
/*				}else if(d.args[1]=~/^\d+:/&&!ass){
					e.sendMessage("You cannot create a tag for a server you're not in.").queue()*/
				}else if(d.tags[d.args[1]]){
					e.sendMessage(["That tag already exists. You can edit it if it belongs to you or a server you're in.",'Dat tag bestaat al. Je kan bewerk het als het is van je of een guild je bent in.'].lang(e)).queue()
					400
				}else{
					String content=d.args[2..-1].join(' ')
/*					String server=e.guild?.id
					if(ass)server=ass.substring(0,ass.length()-1)*/
					d.tags[d.args[1]]=[
						server:/*server*/e.guild?.id,
						history:[[
							content:content,
							author:e.author.id,
							time:System.currentTimeMillis()
						]],
						uses:0
					]
					String sample=e.guild?d.args[1].replaceAll(/^$e.guild.id:/,''):d.args[1]
					String done=["The tag **${d.args[1]}** has been created. You can now use `${d.prefix}tag $sample`.","De tag **${d.args[1]}** is aangemaakt. Je kan het gebruiken met `${d.prefix}tag $sample`."].lang(e)
					if(d.args[1]in['create','edit','delete','append','move','list','info','history','owner','popular','search','random','get'])done+="\nPlease note that a tag with this name will only be usable through `${d.prefix}tag get ${d.args[1]}`."
					else if(d.args[1]=~/<:\w+:\d+>/)done+="\nPlease note that it will be impossible to utilize a tag with an custom emote in its name if you become unable to use the emote."
					if(content.containsAny(['<@{serveruserid}>','<@!{serveruserid}>']))done+="\nI noticed that your tag mentions a random user. Please ensure the tag is considerate of other users."
					e.sendMessage(done).queue()
					d.json.save(d.tags,'tags')
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag create [tag name] [tag content]`.","Gebruik: `${d.prefix}tag create [tag naam] [tag inhoud]`.","Uso: `${d.prefix}tag create [tag nome] [tag conteudo]`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]=='edit'){
			if(d.args[2]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if((d.tags[d.args[1]].server in e.jda.guilds.findAll{e.author in it.users}.id)||(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner])){
						String content=d.args[2..-1].join(' ')
						d.tags[d.args[1]].history+=[
							content:content,
							author:e.author.id,
							time:System.currentTimeMillis()
						]
						String done=["The tag **${d.args[1]}** has been edited.","De tag **${d.args[1]}** is bewerkt."].lang(e)
						if(content.containsAny(['<@{serveruserid}>','<@!{serveruserid}>']))done+="\nI noticed that your tag mentions a random user. Please ensure the tag is considerate of other users."
						e.sendMessage(done).queue()
						d.json.save(d.tags,'tags')
					}else{
						e.sendMessage(["You can't edit that tag because you don't own it, nor are you in the server where it was created.",'Je kan niet bewerk dat tag omdat het is niet van je, en je zijn niet in een guild waar het bent in bezit van.'].lang(e)).queue()
						403
					}
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag edit [tag name] [tag content]`.","Gebruik: `${d.prefix}tag edit [tag naam] [tag inhoud]`.","Uso: `${d.prefix}tag edit [tag nome] [tag conteudo]`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]=='delete'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner]){
						if ((d.args[2] != 'yes') && (d.tags[d.args[1]].history.size() > 1)) {
							e.sendMessage("Are you sure you want to delete the tag **${d.args[1]}** and all its history?\nUse `${d.prefix}tag delete ${d.args[1]} yes` to confirm.").queue()
						} else {
							d.tags.remove(d.args[1])
							e.sendMessage(["The tag **${d.args[1]}** has been deleted.", "De tag **${d.args[1]}** is verwijderde."].lang(e)).queue()
							d.json.save(d.tags, 'tags')
						}
					}else{
						e.sendMessage(["You can't delete that tag because you don't own it.",'Je kan niet verwijderen dat tag omdat het is niet van je.'].lang(e)).queue()
						403
					}
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag delete [tag name]`.","Gebruik: `${d.prefix}tag delete [tag naam]`.","Uso: `${d.prefix}tag delete [tag nome]`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]=='append'){
			if(d.args[2]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(e.guild&&d.tags["$e.guild.id:${d.args[1]}"])d.args[1]="$e.guild.id:${d.args[1]}"
				if(d.tags[d.args[1]]){
					if((d.tags[d.args[1]].server in e.jda.guilds.findAll{e.author in it.users}.id)||(e.author.id in[d.tags[d.args[1]].history[0].author,d.bot.owner])){
						String content=d.tags[d.args[1]].history[-1].content
						String content2=d.args[2..-1].join(' ')
						if(content=~/.+;.+}$/)content=content.replaceLast('}',";$content2}")
						else content+=" $content2"
						d.tags[d.args[1]].history+=[
							content:content,
							author:e.author.id,
							time:System.currentTimeMillis()
						]
						String done="The tag **${d.args[1]}** has been added to."
						if(content.containsAny(['<@{serveruserid}>','<@!{serveruserid}>']))done+="\nI noticed that your tag mentions a random user. Please ensure the tag is considerate of other users."
						e.sendMessage(done).queue()
						d.json.save(d.tags,'tags')
					}else{
						e.sendMessage(["You can't append to that tag because you don't own it, nor are you in the server where it was created.",'Je kan niet adden tot dat tag omdat het is niet van je, en je zijn niet in een guild waar het bent in bezit van.'].lang(e)).queue()
						403
					}
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag append [tag name] [further content]`.","Gebruik: `${d.prefix}tag append [tag naam] [meer inhoud]`.","Uso: `${d.prefix}tag append [tag nome] [m\u00e1s conteudo]`."].lang(e)).queue()
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
							e.sendMessage(["The tag **${d.args[1]}** has been moved to ${if(server){servers.find{it.id==server}.name}else{'Direct Messages (only you will be able to edit it)'}}.","De tag **{d.args[1]}** is verhuisd tot ${if(server){servers.find{it.id==server}.name}else{'Persoonlijke Berichten'}}."].lang(e)).queue()
							d.json.save(d.tags,'tags')
						}else{
							e.sendMessage(["I couldn't find a shared server with the name '${d.args[2]}.'","Ik kon niet vind een shaarred guild met de naam '${d.args[1]}.'"].lang(e)).queue()
							404
						}
					}else{
						e.sendMessage(["You can't move that tag because you don't own it.",'Je kan niet verhuis dat tag omdat het is niet van je.'].lang(e)).queue()
						403
					}
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag move [tag name] [server]`.","Gebruik: `${d.prefix}tag move [tag naam] [guild]`.","Uso: `${d.prefix}tag move [tag nome] [servidor]`."].lang(e)).queue()
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
				ass+=['No tags to see here.','Noo tags op hier.','Nenhuma tag esta aqui.'].lang(e)
			}
			List result=ass.split(1999)
			def destination=e.channel
			if(result.size()>2){
				e.author.openPrivateChannel().complete()
				destination?.id==e.author.privateChannel?.id
			}
			try{
				result.each{
					destination.sendMessage(it).queue()
				}
				if(result.size()>2){
					e.sendMessage(["It was really long (shield), so I sent it to you. <@$e.author.id>","Het was heel lang (geen TWSS), zo ik stuurde het naar u toe. <@$e.author.id>"].lang(e)).queue{
						Thread.sleep(5000)
						it.delete().queue()
					}
				}
			}catch(ex){
				e.sendMessage(["I couldn't send you the list of tags because you have me blocked.",'Ik kon niet stuurde je de list van tags tot je omdat jij heb mij blokkeren.'].lang(e)).queue()
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
					Date time=d.tags[d.args[1]].history[0].time?new Date(d.tags[d.args[1]].history[0].time):null
					if(d.tags[d.args[1]].server)server=e.jda.guilds.find{it.id==d.tags[d.args[1]].server}?.name?:d.tags[d.args[1]].server
					"Created by $author in $server${if(time){' at '+time.format('HH:mm:ss dd/MMMM/YYYY')}else{''}}.\n\n${d.tags[d.args[1]].history[-1].content}\n\nThis tag has ${d.tags[d.args[1]].uses} use${if(d.tags[d.args[1]].uses==1){""}else{"s"}} and ${d.tags[d.args[1]].history*.key.size()} history.".split(1999).each{
						e.sendMessage(it).queue()
					}
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag info [tag name]`.","Gebruik: `${d.prefix}tag info [tag naam]`.","Uso: `${d.prefix}tag info [tag nome]`."].lang(e)).queue()
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
							ass+="${(e.jda.users.find{it.id==kona.author}?.identity?:kona.author).capitalize()} ${if(kona.index(d.tags[d.args[1]].history)){['edited','bewerkt','editado','edytowane'].lang(e)}else{['created','aangemaakt','criada','stworzony'].lang(e)}}${if(kona.time){' at '+new Date(kona.time).format('HH:mm:ss dd MMM YYYY')}else{''}}:\n$kona.content\n\n"
						}
						e.author.openPrivateChannel().complete()
						try{
							ass.split(1999).each{
								e.author.privateChannel.sendMessage(it).queue()
							}
							if(e.guild){
								e.sendMessage(["I have sent you that tag's history. <@$e.author.id>","Ik heb stuurde je de geschiedenis van het tag. <@$e.author.id>"].lang(e)).queue{
									Thread.sleep(5000)
									it.delete().queue()
								}
							}
						}catch(ex){
							e.sendMessage(["I couldn't send you that tag's history because you have me blocked.",'Ik kon niet stuurde je de geschiendenis van het tag tot je omdat jij heb mij blokkeren.'].lang(e)).queue()
							400
						}
					}else{
						e.sendMessage(["You can't view that tag's history because you don't own it, nor are you in the server where it was created.",'Je kan niet uitzicht de geschiendenis van dat tag omdat het is niet van je, en je zijn niet in een guild waar het bent in bezit van.'].lang(e)).queue()
						403
					}
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag history [tag name]`.","Gebruik: `${d.prefix}tag history [tag naam]`.","Uso: `${d.prefix}tag history [tag nome]`."].lang(e)).queue()
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
					if(!contributors)contributors+=['None','Geen','Nenhum','Zaden'].lang(e)
					e.sendMessage("Owner: $owner\n\nContributors: ${contributors.join(', ')}").queue()
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag owner [tag name]`.","Gebruik: `${d.prefix}tag owner [tag naam]`.","Uso: `${d.prefix}tag owner [tag nome]`."].lang(e)).queue()
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
				e.sendMessage(["That user doesn't seem to have any tags.",'Dat gebruiker doe niet lijken te heb tags.'].lang(e)).queue()
				404
			}
		}else if(d.args[0]=='search'){
			if(d.args[1]){
				String search=d.args[1..-1].join().toLowerCase()
				List tags=d.tags*.key.findAll{it.contains(search)}
				String result=tags.join(', ').replace(search,"**$search**")
				if(result.length()>1000)result=result.substring(0,1000)+'...'
				else if(!result)result=['No matching tags found.','Noo tags vind leuk vinden.','Nao foi encontrado tags correspondente.'].lang(e)
				e.sendMessage("**__Tag Results (${tags.size()})__:**\n$result").queue()
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag search [search term]`.","Gebruik: `${d.prefix}tag search [zoekterm]`.","Uso: `${d.prefix}tag search [termo pesquisa]`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]=='random'){
			String tag=d.tags*.key.random()
			List result=(d.tags[tag].history[-1].content.addVariables(e,d.args.join(' ').substring(6).trim())+"\n\n(Tag: **$tag**)").split(1999)
			if(result.size()>2){
				e.sendMessage(["It's over 4000... Yeah, no.",'Het is over-4000... Ja, geen.','E mais de 4000... Sim, nao.','To ponad 4000... Tak, nie.'].lang(e)).queue()
			}else{
				result.each{
					e.sendMessage(it).queue()
					Thread.sleep(150)
				}
			}
			d.tags[tag].uses+=1
			d.json.save(d.tags,'tags')
		}else if(d.args[0]=='get'){
			if(d.args[1]){
				d.args[1]=d.args[1]?.toLowerCase()
				if(d.tags[d.args[1]]){
					List result=d.tags[d.args[1]].history[-1].content.addVariables(e,d.args.join(' ').substring(d.args[0..1].join(' ').length()).trim()).split(1999)
					if(result.size()>2){
						e.sendMessage(["It's over 4000... Yeah, no.",'Het is over-4000... Ja, geen.','E mais de 4000... Sim, nao.','To ponad 4000... Tak, nie.'].lang(e)).queue()
					}else{
						result.each{
							e.sendMessage(it).queue()
							Thread.sleep(150)
						}
					}
					d.tags[d.args[1]].uses+=1
					d.json.save(d.tags,'tags')
				}else{
					e.sendMessage(["The tag '${d.args[1]}' doesn't exist.","De tag '${d.args[1]}' doe niet bestaan.","A tag '${d.args[1]}' nao existe.","Tag '${d.args[1]}' nie istnieje."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag get [tag name]`.","Gebruik: `${d.prefix}tag get [tag naam]`.","Uso: `${d.prefix}tag get [tag nome]`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]){
			if(e.guild&&d.tags["$e.guild.id:${d.args[0]}"])d.args[0]="$e.guild.id:${d.args[0]}"
			if(d.tags[d.args[0]]){
				List result=d.tags[d.args[0]].history[-1].content.addVariables(e,d.args.join(' ').substring(d.args[0].length()).trim()).split(1999)
				if(result.size()>2){
					e.sendMessage(["It's over 4000... Yeah, no.",'Het is over-4000... Ja, geen.','E mais de 4000... Sim, nao.','To ponad 4000... Tak, nie.'].lang(e)).queue()
				}else{
					result.each{
						e.sendMessage(it).queue()
						Thread.sleep(150)
					}
				}
				d.tags[d.args[0]].uses+=1
				d.json.save(d.tags,'tags')
			}else{
				e.sendMessage(["The tag '${d.args[0]}' doesn't exist.","De tag '${d.args[0]}' doe niet bestaan.","A tag '${d.args[0]}' nao existe.","Tag `${d.args[0]}` nie istnieje."].lang(e)).queue()
				404
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tag create/edit/delete/move/info/history/owner/popular/search/get/[tag name] ..`.","Gebruik: `${d.prefix}tag create/edit/delete/move/info/history/owner/popular/search/get/[tag naam] ..`.","Uso: `${d.prefix}tag create/edit/delete/move/info/history/owner/popular/search/get/[tag nome] ..`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`tag [tag name]` will make me get the tag.
`tag create [tag name] [tag content]` will make me create the tag. Tag names cannot contain spaces.
`tag edit [tag name] [tag content]` will make me edit the tag if you have rights to it.
`tag delete [tag name]` will make me delete the tag if you created it.
`tag append [tag name] [further content]` will make me add one more option to a random tag or append the text to a normal tag.
`tag move [tag name] [server]` will make me move the tag if you created it. Move it to 'Direct Messages' to make it only yours to edit.
`tag list [@mention]` will make me list the user's tags or the tags created in this server if you don't put any arguments.
`tag info [tag name]` will make me give you the raw information and statistics of the tag.
`tag history [tag name]` will make me send you the history of the tag if you have rights to it.
`tag owner [tag name]` will make me tell you who owns and contributed to the tag.
`tag popular [@mention]` will make me get the user's most popular tags or the most popular tags in general.
`tag search [query]` will make me search for tags with a matching name.
`tag random` will make me get a random tag and tell you which one it is.
`tag get [tag name]` will make me get the tag even if its name is masked by a subcommand.
Shitposting, shitposting everywhere."""
}


class MiscCommand extends Command {
	List aliases = ['misc']
	def run(Map d, Event e) {
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
			e.sendMessage("`${uptime[0]}` hour${if(uptime[0]!=1){'s'}else{''}}, `${uptime[1]}` minute${if(uptime[1]!=1){'s'}else{''}}").queue()
		}else if(d.args[0]in['timefromnow','time']){
			try{
				e.sendMessage(new Date(d.args[1].formatTime()).format('HH:mm:ss, dd MMMM YYYY')).queue()
			}catch(ex){
				e.sendMessage(['Enter a timefromnow format, like `1d12h`.','Vul een tijdvannu format, lijkt `1d12h`.','Insira um tempoapartir do formato, como `1d12h`.'].lang(e)).queue()
				400
			}
		}else if(d.args[0]in['area','location']){
			try{
				d.args=d.args[1..-1].join(' ').toLowerCase().replaceEach(['england','america'],['united kingdom','united states'])
				List people=((d.args=='earth')?d.db:d.db.findAll{it.value.area.toLowerCase().contains(d.args)})*.value*.aka*.getAt(0).unique()
				if(people){
					String inhabits=people.join(', ').capitalize()
					if(inhabits.length()>1000)inhabits=inhabits.substring(0,1000)+'...'
					e.sendMessage("$inhabits. (${people.size()})").queue()
				}else{
					e.sendMessage("Looks like I don't know anyone who lives there.").queue()
					404
				}
			}catch(ex){
				e.sendMessage(['Enter a location.','Vul een locatie.','Insira um local.'].lang(e)).queue()
				ex.printStackTrace()
				400
			}
		}else if(d.args[0]=='http'){
			if(d.args[1]){
				String status=d.misc.http[d.args[1]]
				if(status){
					e.sendMessage(status).queue()
				}else{
					e.sendMessage("**404 Not Found**:\nThat HTTP status could not be found.").queue()
					404
				}
			}else{
				e.sendMessage(['Enter a HTTP error code, like `404`.','Vul een HTTP foutcode, lijkt `404`.','Digite um codigo de erro HTTP, como `404`.'].lang(e)).queue()
				400
			}
		}else if(d.args[0]=='name'){
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
				e.sendMessage(["Enter an ID, like `$e.author.id`.","Vul een ID, lijkt `$e.author.id`.","Insura uma ID, como `$e.author.id`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]=='created'){
			if(!d.args[1])d.args[1]=''
			d.args[1]=d.args[1]?d.args[1].replaceAll(/\D/,''):e.author.id
			if(d.args[1]){
				long time=((Long.parseLong(d.args[1])>>22)+1420070400000)
				String date=new Date(time).format('HH:mm:ss, d MMMM YYYY').formatBirthday()
				e.sendMessage("$date ($time)").queue()
			}else{
				e.sendMessage(["Enter an ID, like `$e.author.id`.","Vul een ID, lijkt `$e.author.id`.","Insura uma ID, como `$e.author.id`."].lang(e)).queue()
				400
			}
		}else if(d.args[0]in['prefix','prefixes']){
			if(e.guild){
				String ass=''
				e.guild.users.findAll{it.bot}.each{User bot->
					Map entry=d.db.find{bot.id in it.value.ids}?.value
					if(entry)ass+="${bot.identity.capitalize()}: `${entry.prefixes*.replace('mention','@'+it.name)*.replace('`','` ').join('`, `')}`\n"
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
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}misc pi/uptime/timefromnow/area/http/name/prefix ..`","Gebruik: `${d.prefix}misc pi/uptime/timefromnow/area/http/name/prefix ..`","Uso: `${d.prefix}misc pi/uptime/timefromnow/area/http/name/prefix ..`"].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`misc pi [number]` will make me tell you pi to that decimal.
`misc uptime` will tell you how long I've been running. JDA strong!
`misc timefromnow [time]` will make me tell you the expiry time of your timefromnow format. This function is used in other commands.
`misc area [area]` will make me tell you the people I know who live there. It's a small world.
`misc http [http]` will make me tell you more about the HTTP status code.
`misc name [id]` will make me trace an ID back to an object on Discord.
`misc created [id]` will make me calculate an ID's creation date.
`misc prefix` will make me tell you prefixes of the bots in the server.
Whew."""
}


class TextCommand extends Command {
	List aliases = ['text']
	def run(Map d, Event e) {
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
				if(manipulatives.containsAny(['full','fw']))output=output.replaceEach(('A'..'Z')+('a'..'z')+('1'..'9')+['0','!','"','$','%','^','&','*','(',')','-','_','+','=','[','{',']','}',';',':','A?A��','@','#','|',',','<','.','>','?','~',' '],['\uff21','\uff22','\uff23','\uff24','\uff25','\uff26','\uff27','\uff28','\uff29','\uff2a','\uff2b','\uff2c','\uff2d','\uff2e','\uff2f','\uff30','\uff31','\uff32','\uff33','\uff34','\uff35','\uff36','\uff37','\uff38','\uff39','\uff3a','\uff41','\uff42','\uff43','\uff44','\uff45','\uff46','\uff47','\uff48','\uff49','\uff4a','\uff4b','\uff4c','\uff4d','\uff4e','\uff4f','\uff50','\uff51','\uff52','\uff53','\uff54','\uff55','\uff56','\uff57','\uff58','\uff59','\uff5a','\uff11','\uff12','\uff13','\uff14','\uff15','\uff16','\uff17','\uff18','\uff19','\uff10','\uff01','\u201d','\uff04','\uff05','\uff3e','\uff06','\uff0a','\uff08','\uff09','\uff0d','\uff3f','\uff0b','\uff1d','\u300c','\uff5b','\u300d','\uff5d','\uff1b','\uff1a','\uffe5','\uff20','\uff03','\uff5c','\uff0c','\uff1c','\uff0e','\uff1e','\uff1f','\uff5e','\u3000'])
				if(manipulatives.containsAny(['strike','line']))output=output.replace('','\u0336')
				if(manipulatives.containsAny(['random','shuffle']))output=output.randomize()
				if(manipulatives.containsAny(['emoji','regional']))output=output.replaceEach(('A'..'Z'),('a'..'z')).replaceEach(('a'..'z')+('0'..'9')+['!','?','+','-','\u00d7','\u00f7','\$','\u221a','\u263c','*','>','<','^','.','\u2588','\u25cf','\u25cb','#','\u2605','\u2020','~'],['\ud83c\udde6\u200b','\ud83c\udde7\u200b','\ud83c\udde8\u200b','\ud83c\udde9\u200b','\ud83c\uddea\u200b','\ud83c\uddeb\u200b','\ud83c\uddec\u200b','\ud83c\udded\u200b','\ud83c\uddee\u200b','\ud83c\uddef\u200b','\ud83c\uddf0\u200b','\ud83c\uddf1\u200b','\ud83c\uddf2\u200b','\ud83c\uddf3\u200b','\ud83c\uddf4\u200b','\ud83c\uddf5\u200b','\ud83c\uddf6\u200b','\ud83c\uddf7\u200b','\ud83c\uddf8\u200b','\ud83c\uddf9\u200b','\ud83c\uddfa\u200b','\ud83c\uddfb\u200b','\ud83c\uddfc\u200b','\ud83c\uddfd\u200b','\ud83c\uddfe\u200b','\ud83c\uddff\u200b','0\u20e3\u200b','1\u20e3\u200b','2\u20e3\u200b','3\u20e3\u200b','4\u20e3\u200b','5\u20e3\u200b','6\u20e3\u200b','7\u20e3\u200b','8\u20e3\u200b','9\u20e3\u200b','\u2757\u200b','\u2753\u200b','\u2795\u200b','\u2796\u200b','\u2716\u200b','\u2797\u200b','\ud83d\udcb2\u200b','\u2714\u200b','\ud83d\udd06\u200b','*\u20e3\u200b','\u25b6\u200b','\u25c0\u200b','\ud83d\udd3c\u200b','\u25aa\u200b','\u23f9\u200b','\u26ab\u200b','\u23fa\u200b','#\u20e3','\u2b50','\u271d','\u3030'])
				if(manipulatives.containsAny(['fancy','handwritten']))output=output.replaceEach(('a'..'z')+('A'..'Z'),['\ud835\udcea','\ud835\udceb','\ud835\udcec','\ud835\udced','\ud835\udcee','\ud835\udcef','\ud835\udcf0','\ud835\udcf1','\ud835\udcf2','\ud835\udcf3','\ud835\udcf4','\ud835\udcf5','\ud835\udcf6','\ud835\udcf7','\ud835\udcf8','\ud835\udcf9','\ud835\udcfa','\ud835\udcfb','\ud835\udcfc','\ud835\udcfd','\ud835\udcfe','\ud835\udcff','\ud835\udd00','\ud835\udd03','\ud835\udd02','\ud835\udd03','\ud835\udcd0','\ud835\udcd1','\ud835\udcd2','\ud835\udcd3','\ud835\udcd4','\ud835\udcd5','\ud835\udcd6','\ud835\udcd7','\ud835\udcd8','\ud835\udcd9','\ud835\udcda','\ud835\udcdb','\ud835\udcdc','\ud835\udcdd','\ud835\udcde','\ud835\udcdf','\ud835\udce0','\ud835\udce1','\ud835\udce2','\ud835\udce3','\ud835\udce4','\ud835\udce5','\ud835\udce6','\ud835\udce7','\ud835\udce8','\ud835\udce9'])
				output.trim().split(1999).each{
					e.sendMessage(it).queue()
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}text [effects] [text]`.","Gebruik: `${d.prefix}text [type] [tekst]`.","Uso: `${d.prefix}text [descricao] [texto]`."].lang(e)).queue()
				400
			}
		}catch(ex){
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}text [effects] [text]`.","Gebruik: `${d.prefix}text [type] [tekst]`.","Uso: `${d.prefix}text [descricao] [texto]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`text [effects] [text]` will make me add effects to the text.
The effects are space, reverse, super, bold, italic, compress, bubble, small, full, strike, random, emoji and fancy, so try them all."""
}


class ChatBoxCommand extends Command {
	List aliases = ['chatbox','ascii']
	int limit = 150
	def run(Map d, Event e) {
		d.args=d.args.toLowerCase()
		String p=''
		int type=d.args.contains('compact')?1:0
		int offset=d.args.contains('wide')?18:0
		if(e.guild){
			Guild guild=e.guild
			User user=e.author
			Channel channel=e.channel
			String guildName=guild.name.cut(14)
			String channelInfo="#$channel.name | ${if(channel.topic){channel.topic}else{''}}".cut(37+offset)
			p+="$guildName${' '*(14-guildName.length)} | $channelInfo${' '*((37+offset)-channelInfo.length)} \ud83d\udd14 \ud83d\udccc \ud83d\udc6b\n----------------|------------------------------------------------${'-'*offset}\n"
			List channels=[['TEXT CHANNELS ','TEKSTKANALEN  ','CANAIS TEXTO  '].lang(e)]
			List tc=guild.textChannels.toList().sort{it.position}
			if(tc.size()>25)tc=tc[0..24]
			tc.each{
				String channelName="#$it.name".cut(14)
				channels+="$channelName${' '*(14-channelName.length)}"
			}
			channels+='              '
			channels+=['VOICE CHANNELS','SPRAAKKANALEN ','KANAIS VOZ    '].lang(e)
			List vc=guild.voiceChannels.toList().sort{it.position}
			if(vc.size()>15)vc=vc[0..14]
			vc.each{
				String channelName="\ud83d\udd3d$it.name".cut(14)
				if(it.userLimit){
					String limit="${it.users.size()}/$it.userLimit"
					channelName="${"\ud83d\udd0a$it.name".cut(14-(limit.length+1))} $limit"
				}
				channels+="$channelName${' '*(14-channelName.length)}"
			}
			channels+='              '
			int height=channels.size
			List logs=channel.history.retrievePast(50).complete().reverse()-e.message
			List messages=[]
			logs.each{Message m->
				String ampm='AM'
				if(m.createTime.format('H').toInteger()>12)ampm='PM'
				if(type){
					int index=m.index(logs)
					if(index&&logs[index-1].author.id!=m.author.id){
						if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){'BOT '}else{''}}${guild.members.find{it.user.id==m.author.id}?.effectiveName?:m.author.name}: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}else{
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){'BOT '}else{''}}${guild.members.find{it.user.id==m.author.id}?.effectiveName?:m.author.name}: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}
					}
				}else{
					if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
						messages+="${guild.members.find{it.user.id==m.author.id}?.effectiveName?:m.author.name}${if(m.author.bot){' BOT'}else{''}} - Today at ${m.createTime.format('HH:mm')} $ampm".cut(46+offset)
					}else{
						messages+="${guild.members.find{it.user.id==m.author.id}?.effectiveName?:m.author.name}${if(m.author.bot){' BOT'}else{''}} - ${m.createTime.format('dd/MM/YYYY')}".cut(46+offset)
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
				String nack=it.members.find{it.user.id==user.id}.nickname
				if(nack)aka+=nack
			}
			String channelInfo="@$channel.user.name | ${if(aka){"AKA ${aka.join(', ')}"}else{''}}".cut(40+offset)
			p+=" Find or start\u2026 | $channelInfo${' '*((40+offset)-channelInfo.length)} \ud83d\udcde \ud83d\udccc\n----------------|------------------------------------------------${'-'*offset}\n"
			List channels=[['Friends       ','              ','DIRECT MESSAGE'],['Vrienden      ','              ','PERSOONLIJKE B'],['Amigos        ','              ','MENSAGENS DIRE']].lang(e)
			List pc=e.jda.privateChannels.toList()
			if(pc.size()>30)pc=pc[0..29]
			pc.each{
				String channelName=it.user.name.cut(14)
				channels+="$channelName${' '*(14-channelName.length)}"
			}
			channels+='              '
			int height=channels.size
			List logs=channel.history.retrievePast(50).complete().reverse()-e.message
			List messages=[]
			logs.each{Message m->
				String ampm='AM'
				if(m.createTime.format('H').toInteger()>12)ampm='PM'
				if(type){
					int index=m.index(logs)
					if(index&&logs[index-1].author.id!=m.author.id){
						if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){'BOT '}else{''}}$m.author.name: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}else{
							messages+="${m.createTime.format('HH:mm')} $ampm ${if(m.author.bot){'BOT '}else{''}}$m.author.name: ${m.content.replace('```','')}".tokenize('\n')*.split(46+offset)
						}
					}
				}else{
					if(m.createTime.format('d MMMM')==new Date().format('d MMMM')){
						messages+="$m.author.name${if(m.author.bot){' BOT'}else{''}} - Today at ${m.createTime.format('HH:mm')} $ampm".cut(46+offset)
					}else{
						messages+="$m.author.name${if(m.author.bot){' BOT'}else{''}} - ${m.createTime.format('dd/MM/YYYY')}".cut(46+offset)
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
	String category = 'General'
	String help = """`chatbox [effects]` will make me make an ASCII of the chat.
Optional added effects are compact and wide. No added sugar."""
}


class IdentifyCommand extends Command {
	List aliases = ['identify','identity']
	def run(Map d, Event e) {
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				Map entry=d.db.find{m.id in it.value.ids}?.value
				if(entry){
					if(from.contains(','))from=from.substring(0,from.indexOf(','))
					ass+="**${m.name.capitalize()}#$m.discriminator**: ${entry.aka.join(', ')}: _$entry.bio_.\n"
				}else{
					ass+="**${m.name.capitalize()}#$m.discriminator**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			User user=e.author
			if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
			if(user){
				Map entry=d.db.find{user.id in it.value.ids}?.value
				if(entry){
					String ass="**${user.name.capitalize()}#$user.discriminator** is also known as: ${entry.aka.join(', ')}.\n"
					if(entry.type){
						ass += "\nPrefixes: `${entry.prefixes.join('`, `')}`    Owners: "
						try {
							List lisa = []
							entry.owners.each{String owner->
								Map bart = d.db[owner]
								lisa += "${bart.aka[0]} (${bart.ids.join(', ')})"
							}
							ass += lisa.join(', ')
						} catch (gsdkgjksdg) {
							gsdkgjksdg.printStackTrace()
							ass += 'error, check console'
						}
					}else{
						String mc=entry.accounts.mc
						String lp=entry.accounts.lp
						String mal=entry.accounts.mal
						String mail=entry.accounts.mail
						if(mc||lp){
							ass+='\n'
							if(mc)ass+="<:mc:466642120535703563> $mc    "
							if(lp)ass+="<:lp:327139896198299649> $lp    "
							if(mal)ass+="<:mal:467995374754332683> $mal    "
							if(mail)ass+=":e_mail: $mail"
						}
					}
					if(entry.bio)ass+="\n_${entry.bio}_"
					List others=entry.ids-user.id
					if(others){
						ass+='\nMore accounts: '
						List lisa=[]
						others.each{String other->
							User bart=e.jda.users.find{it.id==other}
							if(bart)lisa+="$bart.name#$bart.discriminator"
							else lisa+=other
						}
						ass+=lisa.join(', ')
					}
					e.sendMessage(ass).queue()
				}else{
					e.sendMessage(["There is no information in my database for $user.name#$user.discriminator.","Er is geen informatie in mijn databank voor $user.name#$user.discriminator.","Nao ha informacoes no meu banco de dados para $user.name#$user.discriminator.","W mojej bazie danych nie ma zadnych informacji o $user.name#$user.discriminator."].lang(e)).queue()
					404
				}
			}else{
				e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
				404
			}
		}
	}
	String category = 'Database'
	String help = """`identify [user]` will make me tell you who they really are, and a bit about them.
Who could forget the classic command?"""
}


class IrlCommand extends Command {
	List aliases = ['irl','realname']
	int limit = 25
	def run(Map d, Event e) {
		if(e.guild&&(d.modes.database[e.guild.id]==false)){
			e.sendMessage('Personal information has been disabled by your jurisdiction.').queue()
		}else{
			if(e.message.mentions.size()>1){
				List mens=e.message.mentions
				if(mens.size()>10)mens=mens[0..9]
				String ass=''
				for(m in mens){
					Map entry=d.db.find{m.id in it.value.ids}?.value
					if(entry){
						ass+="**${m.identity.capitalize()}**: Real name is ${if(m.bot){"$entry.identity, duh"}else if(entry.irl){entry.irl}else{'not in my database'}}.\n"
					}else{
						ass+="**${m.name.capitalize()}#$m.discriminator**: No information in my database.\n"
					}
				}
				e.sendMessage(ass).queue()
				207
			}else{
				User user=e.author
				if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
				if(user){
					Map entry=d.db.find{user.id in it.value.ids}?.value
					if(entry){
						e.sendMessage("**${user.identity.capitalize()}**'s real name is ${if(user.bot){"$user.identity, you freaking idiot"}else if(entry.irl){entry.irl}else{'not in my database'}}.${if(user.id==e.author.id){"\n\nIf you would like your information changed, use `${d.prefix}entry`."}else{''}}").queue()
					}else{
						e.sendMessage(["There is no information in my database for $user.name#$user.discriminator.","Er is geen informatie in mijn databank voor $user.name#$user.discriminator.","Nao ha informacoes no meu banco de dados para $user.name#$user.discriminator.","W mojej bazie danych nie ma zadnych informacji o $user.name#$user.discriminator."].lang(e)).queue()
						404
					}
				}else{
					e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
					404
				}
			}
		}
	}
	String category = 'Database'
	String help = """`irl [user]` will make me tell you the user's real name, if I know it.
We're all friendly here so I don't want to see any stalking."""
}


class AgeCommand extends Command {
	List aliases = ['age','birthday']
	int limit = 25
	def run(Map d, Event e) {
		String benis=d.args.toLowerCase()
		if(benis=='discord'){
			e.sendMessage("Discord's anniversary is 12th May 2015.").queue()
		}else if(benis=='beaconville'){
			e.sendMessage("Beaconville's anniversary is 8th August 2014.").queue()
		} else if (e.guild && (benis == e.guild.name.toLowerCase())) {
			e.sendMessage("$e.guild.name's anniversary is ${m.createTime.format('dd MMMM YYYY').formatBirthday()}.").queue()
		}else if(e.guild&&(d.modes.database[e.guild.id]==false)){
			e.sendMessage('Personal information has been disabled by your jurisdiction.').queue()
		}else{
			if(e.message.mentions.size()>1){
				List mens=e.message.mentions
				if(mens.size()>10)mens=mens[0..9]
				String ass=''
				for(m in mens) {
					Map entry=d.db.find{m.id in it.value.ids}?.value
					if(entry){
						ass+="**$m.identity**: Birthday is ${if(m.bot){"${m.createTime.format('dd MMMM YYYY')} (account)"}else if(entry.age){entry.age.formatBirthday()}else{'not in my database'}}.\n"
					}else{
						ass+="**$m.name#$m.discriminator**: No information in my database.\n"
					}
				}
				e.sendMessage(ass).queue()
				207
			}else{
				User user=e.author
				if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
				if(user){
					Map entry = d.db.find{user.id in it.value.ids}?.value
					if(entry){
						e.sendMessage("**$user.identity**'s birthday is ${if(user.bot){"${m.createTime.format('dd MMMM YYYY')}, according to account creation date"}else if(entry.age){entry.age.formatBirthday()}else{'not in my database'}}.${if(user.id==e.author.id){"\n\nIf you would like your information changed, use `${d.prefix}entry`."}else{''}}").queue()
					}else{
						e.sendMessage(["There is no information in my database for $user.name#$user.discriminator.","Er is geen informatie in mijn databank voor $user.name#$user.discriminator.","Nao ha informacoes no meu banco de dados para $user.name#$user.discriminator.","W mojej bazie danych nie ma zadnych informacji o $user.name#$user.discriminator."].lang(e)).queue()
						404
					}
				}else{
					e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
					404
				}
			}
		}
	}
	String category = 'Database'
	String help = """`age [user]` will make me tell you the user's birthday, if I know it.
What are the chances it'll be today?"""
}


class AreaCommand extends Command {
	List aliases = ['area','location']
	int limit = 25
	def run(Map d, Event e) {
		if(e.guild&&(d.modes.database[e.guild.id]==false)){
			e.sendMessage('Personal information has been disabled by your jurisdiction.').queue()
		}else{
			if(e.message.mentions.size()>1){
				List mens=e.message.mentions
				if(mens.size()>10)mens=mens[0..9]
				String ass=''
				for(m in mens){
					Map entry=d.db.find{m.id in it.value.ids}?.value
					if(entry){
						ass+="**${m.identity.capitalize()}**: Lives in ${if(m.bot){'the Internet'}else if(entry.area){entry.area}else{'not in my database'}}.\n"
					}else{
						ass+="**${m.name.capitalize()}#$m.discriminator**: No information in my database.\n"
					}
				}
				e.sendMessage(ass).queue()
				207
			}else{
				User user=e.author
				if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
				if(user){
					Map entry=d.db.find{user.id in it.value.ids}?.value
					if(entry){
						e.sendMessage("**${user.identity.capitalize()}** lives in ${if(user.bot){'the cyberspace of the Internet'}else if(entry.area){(entry.area.startsWith('United')?'the ':'')+entry.area}else{'not in my database'}}.${if(user.id==e.author.id){"\n\nIf you would like your information changed, use `${d.prefix}entry`."}else{''}}").queue()
					}else{
						e.sendMessage(["There is no information in my database for $user.name#$user.discriminator.","Er is geen informatie in mijn databank voor $user.name#$user.discriminator.","Nao ha informacoes no meu banco de dados para $user.name#$user.discriminator.","W mojej bazie danych nie ma zadnych informacji o $user.name#$user.discriminator."].lang(e)).queue()
						404
					}
				}else{
					e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
					404
				}
			}
		}
	}
	String category = 'Database'
	String help = """`area [user]` will make me tell you where they live, if I know it.
Not down to the road name, though."""
}


class AltsCommand extends Command {
	List aliases = ['alts']
	def run(Map d, Event e) {
		User user=e.author
		if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			Map entry=d.db.find{user.id in it.value.ids}?.value
			if(entry){
				List others=entry.ids-user.id
				List owners=[]
				List bots=[]
				if(user.bot){
					owners=entry.owners.collect{String owner->
						d.db.find{it.key==owner}.value
					}
				}else{
					String id=d.db.find{user.id in it.value.ids}?.value.key
					bots=d.db.findAll{id in it.value.owners}.value
				}
				if(others||bots||owners){
					String ass="**${user.identity.capitalize()}'s other accounts**:\n\n"
					if(others){
						'Alternate: '
						List lisa=[]
						others.each{String maggie->
							User bart=e.jda.users.find{it.id==maggie}
							if(bart)lisa+="$bart.name#$bart.discriminator ($maggie)"
							else lisa+=maggie
						}
						ass+=lisa.join(', ')
					}
					if(owners){
						'Owners: '
						List lisa=[]
						owners.each{String maggie->
							bep+="${it.aka[0]} (${it.ids.join(', ')})"
						}
						ass+=bep.join(', ')
					}
					if(bots){
						'Bots owned: '
						List bep=[]
						bots.each{
							bep+="${it.aka[0]} (${it.ids.join(', ')})"
						}
						ass+=bep.join(', ')
					}
					e.sendMessage(ass).queue()
				}else{
					e.sendMessage(["**${user.identity.capitalize()}** doesn't have any other accounts in my database.","**${user.identity.capitalize()}** doe niet heb alternatieve accounten in mijn databank."].lang(e)).queue()
				}
			}else{
				e.sendMessage(["There is no information in my database for $user.name#$user.discriminator.","Er is geen informatie in mijn databank voor $user.name#$user.discriminator.","Nao ha informacoes no meu banco de dados para $user.name#$user.discriminator.","W mojej bazie danych nie ma zadnych informacji o $user.name.#$user.discriminator"].lang(e)).queue()
				404
			}
		}else{
			e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}
	}
	String category = 'Database'
	String help = """`alts [user]` will make me tell you the alternate accounts of the user.
Oh man, it was really you all along!"""
}


class MinecraftCommand extends Command {
	List aliases = ['mc','minecraft']
	def run(Map d, Event e) {
		if(e.message.mentions.size()>1){
			List mens=e.message.mentions
			if(mens.size()>10)mens=mens[0..9]
			String ass=''
			for(m in mens){
				Map entry=d.db.find{m.id in it.value.ids}?.value
				if(entry){
					ass+="**${m.identity.capitalize()}**: ${if(m.bot){'No MC for bots, hopefully'}else if("Username is $mc"){mc}else{'No account in my database'}}.\n"
				}else{
					ass+="**${m.name.capitalize()}#$m.discriminator**: No information in my database.\n"
				}
			}
			e.sendMessage(ass).queue()
			207
		}else{
			if(e.message.mentions){
				Map entry=d.db.find{message.mentions[-1].id in it.value.ids}?.value
				if(entry){
					String mc=entry.accounts.mc
					e.sendMessage("**${e.message.mentions[-1].identity.capitalize()}**${if(mc){"'s Minecraft username is $mc.\nhttps://visage.surgeplay.com/full/512/${mc}.png"}else{[' does not have a Minecraft account in my database.',' heeft geen Minecraft-account in mijn database.'].lang(e)}}").queue()
				}else{
					e.sendMessage("There is no information in my database for ${e.message.mentions[-1].name}#${e.message.mentions[-1].discriminator}.").queue()
					404
				}
			}else if(d.args){
				String mc=d.args.replace(' ','_')
				String owner=d.db.find{it.value.mc.toLowerCase()==mc.toLowerCase()}?.value?.aka[0]
				if(owner){
					e.sendMessage("Minecraft account $mc, owned by $owner:\nhttps://visage.surgeplay.com/full/512/${mc}.png").queue()
				}else{
					e.sendMessage("Minecraft account $mc:\nhttps://visage.surgeplay.com/full/512/${mc}.png").queue()
				}
			}else{
				String mc=d.db[e.author.id].mc
				try{
					e.sendMessage("**${e.author.identity.capitalize()}**${if(mc){"'s Minecraft username is $mc.\nhttps://visage.surgeplay.com/full/512/${mc}.png"}else{" does not have a Minecraft account in my database."}}\n\nIf you would like your information changed, use `${d.prefix}entry`.").queue()
				}catch(entry){
					e.sendMessage(["There is no information in my database for $user.name#$user.discriminator.","Er is geen informatie in mijn databank voor $user.name#$user.discriminator.","Nao ha informacoes no meu banco de dados para $user.name#$user.discriminator.","W mojej bazie danych nie ma zadnych informacji o $user.name#$user.discriminator."].lang(e)).queue()
					404
				}
			}
		}
	}
	String category = 'Database'
	String help = """`mc [username]` will make me get an avatar of the Minecraft account.
`mc [user]` will make me get an avatar of the user's Minecraft account, if I know it.
Beaconville functionality."""
}


class TimeCommand extends Command {
	List aliases = ['time']
	boolean dev = true
	def run(Map d, Event e) {
		e.sendMessage("Currently disabled due to innacuracies with DST. <:anal:365936818882871296>").queue()
	}
	String category = 'Database'
	String help = """`time [area]` will make me tell you the time for that area.
`time [user]` will make me tell you the time for them.
I had it before BooBot."""
}


class EventsCommand extends Command {
	List aliases = ['events']
	int limit = 150
	Map specials=['25 December':'Christmas','31 October':'Halloween','1 January':'New Year','26 December':'Boxing Day','14 February':"Valentine's Day",'8 August':'Beaconville Anniversary','1 April':'Unfunny Pranks Day','13 May':'Discord Anniversary']
	def run(Map d, Event e) {
		if(d.args.contains('today'))d.args=(d.args-'today').trim()
		Map legacy=d.json.load('database-old')
		Message message=e.sendMessage('(Using legacy database.)\nChecking my calendar...').complete()
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
					for(b in legacy.entrySet().findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
						String raw=b.value.age.rawBirthday()
						if(raw.startsWith(upcomingDate)){
							int birthYear
							try{
								birthYear=raw.tokenize()[-1].toInteger()
							}catch(NotANumber){}
							if(!birthYear){
								eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s birthday (${upcomingDate.formatBirthday()})"
							}else{
								eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s ${((upcomingYear-birthYear).toString()+' ').formatBirthday()}birthday (${upcomingDate.formatBirthday()})"
							}
						}
					}
					for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==upcomingDate)eventsUpcoming+="\u2022 ${s.name.capitalize()}'s ${((upcomingYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary (${upcomingDate.formatBirthday()})"
					if(specials[upcomingDate])eventsUpcoming+="\u2022 ${specials[upcomingDate]} (${upcomingDate.formatBirthday()})"
				}
				eventsUpcoming=eventsUpcoming.unique()
				message.edit("(Using legacy database.)\n**__${if(numberOfDays<0){'Passed'}else{'Upcoming'}} Events ($numberOfDays Days) ($eventsUpcoming.size)__:**\n${if(eventsUpcoming){eventsUpcoming.join('\n')}else{'Sure is boring around here.'}}").queue()
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
				for(b in legacy.findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
					String raw=b.value.age.rawBirthday()
					if(raw=~/\b$todaysDate\b/){
						int birthYear
						try{
							birthYear=raw.tokenize()[-1].toInteger()
						}catch(NotANumber){}
						if(!birthYear){
							eventsToday+="\u2022 ${b.value.name.capitalize()}'s birthday"
						}else{
							eventsToday+="\u2022 ${b.value.name.capitalize()}'s ${((todaysYear-birthYear).toString()+' ').formatBirthday()}birthday"
						}
					}
					for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')=~/\b$todaysDate\b/)eventsToday+="\u2022 ${s.name.capitalize()}'s ${((todaysYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary"
					if(specials.find{it.key=~/\b$todaysDate\b/})eventsToday+="\u2022 ${specials.find{it.key=~/\b$todaysDate\b/}.value}"
				}
				eventsToday=eventsToday.unique()
				message.edit("(Using legacy database.)\n**__Events for ${todaysDate.formatBirthday()} ($eventsToday.size)__:**\n${if(eventsToday){eventsToday.join('\n')}else{'Nothing interesting on this day.'}}\n").queue()
			}
		}else{
			String todaysDate=new Date().format('d MMMM')
			int todaysYear=new Date().format('YYYY').toInteger()
			for(b in legacy.findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
				String raw=b.value.age.rawBirthday()
				if(raw.startsWith(todaysDate)){
					int birthYear
					try{
						birthYear=raw.tokenize()[-1].toInteger()
					}catch(ex){
						
					}
					if(!birthYear){
						eventsToday+="\u2022 ${b.value.name.capitalize()}'s birthday"
					}else{
						eventsToday+="\u2022 ${b.value.name.capitalize()}'s ${((todaysYear-birthYear).toString()+' ').formatBirthday()}birthday"
					}
				}
				for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==todaysDate)eventsToday+="\u2022 ${s.name.capitalize()}'s ${((todaysYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary"
				if(specials[todaysDate])eventsToday+="\u2022 ${specials[todaysDate]}"
			}
			eventsToday=eventsToday.unique()
			List eventsUpcoming=[]
			for(n in 1..7){
				String upcomingDate=(new Date()+n).format('d MMMM')
				int upcomingYear=(new Date()+n).format('YYYY').toInteger()
				for(b in legacy.findAll{!(it.value.age in['unknown','private'])&&!it.value.tags.startsWith('Bot')&&!it.value.name.endsWithAny(['Incognito','Alternate Account'])}){
					String raw=b.value.age.rawBirthday()
					if(raw.startsWith(upcomingDate)){
						int birthYear
						try{
							birthYear=raw.tokenize()[-1].toInteger()
						}catch(ex){
							
						}
						if(!birthYear){
							eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s birthday (${upcomingDate.formatBirthday()})"
						}else{
							eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s ${((upcomingYear-birthYear).toString()+' ').formatBirthday()}birthday (${upcomingDate.formatBirthday()})"
						}
					}
				}
				for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==upcomingDate)eventsUpcoming+="\u2022 ${s.name.capitalize()}'s ${((upcomingYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary (${upcomingDate.formatBirthday()})"
				if(specials[upcomingDate])eventsUpcoming+="\u2022 ${specials[upcomingDate]} (${upcomingDate.formatBirthday()})"
			}
			eventsUpcoming=eventsUpcoming.unique()
			message.edit("(Using legacy database.)\n**__Events for ${todaysDate.formatBirthday()} (Today) ($eventsToday.size)__:**\n${if(eventsToday){eventsToday.join('\n')}else{'Nothing interesting today.'}}\n**__Upcoming Events (7 Days) ($eventsUpcoming.size)__:**\n${if(eventsUpcoming){eventsUpcoming.join('\n')}else{'Sure is boring around here.'}}").queue()
		}
	}
	String category = 'Database'
	String help = """`events` will make me tell you today's and upcoming events (using the old database until I fix this).
`events [number]` will make me tell you the events upcoming in those days.
`events [date]` will make me tell you the events for that day.
No-one ever remembers my birthday..."""
}


class EventsCommandNew extends Command {
	List aliases = ['events2']
	int limit = 150
	Map specials=['25 December':'Christmas','31 October':'Halloween','1 January':'New Year','26 December':'Boxing Day','14 February':"Valentine's Day",'8 August':'Beaconville Anniversary','1 April':'Unfunny Pranks Day','13 May':'Discord Anniversary']
	def run(Map d, Event e) {
		if(d.args.contains('today'))d.args=(d.args-'today').trim()
		Message message=e.sendMessage('Checking my calendar...').complete()
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
					for(b in d.db*.findAll{it.value.age}){
						String raw=b.value.age
						if(raw.startsWith(upcomingDate)){
							int birthYear
							try{
								birthYear=raw.tokenize()[-1].toInteger()
							}catch(NotANumber){}
							if(!birthYear){
								eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s birthday (${upcomingDate.formatBirthday()})"
							}else{
								eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s ${((upcomingYear-birthYear).toString()+' ').formatBirthday()}birthday (${upcomingDate.formatBirthday()})"
							}
						}
					}
					for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==upcomingDate)eventsUpcoming+="\u2022 ${s.name.capitalize()}'s ${((upcomingYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary (${upcomingDate.formatBirthday()})"
					if(specials[upcomingDate])eventsUpcoming+="\u2022 ${specials[upcomingDate]} (${upcomingDate.formatBirthday()})"
				}
				eventsUpcoming=eventsUpcoming.unique()
				message.edit("**__${if(numberOfDays<0){'Passed'}else{'Upcoming'}} Events ($numberOfDays Days) ($eventsUpcoming.size)__:**\n${if(eventsUpcoming){eventsUpcoming.join('\n')}else{'Sure is boring around here.'}}").queue()
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
				for(b in d.db*.findAll{it.value.age}){
					String raw=b.value.age
					if(raw=~/\b$todaysDate\b/){
						int birthYear
						try{
							birthYear=raw.tokenize()[-1].toInteger()
						}catch(NotANumber){}
						if(!birthYear){
							eventsToday+="\u2022 ${b.value.name.capitalize()}'s birthday"
						}else{
							eventsToday+="\u2022 ${b.value.name.capitalize()}'s ${((todaysYear-birthYear).toString()+' ').formatBirthday()}birthday"
						}
					}
					for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')=~/\b$todaysDate\b/)eventsToday+="\u2022 ${s.name.capitalize()}'s ${((todaysYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary"
					if(specials.find{it.key=~/\b$todaysDate\b/})eventsToday+="\u2022 ${specials.find{it.key=~/\b$todaysDate\b/}.value}"
				}
				eventsToday=eventsToday.unique()
				message.edit("**__Events for ${todaysDate.formatBirthday()} ($eventsToday.size)__:**\n${if(eventsToday){eventsToday.join('\n')}else{'Nothing interesting on this day.'}}\n").queue()
			}
		}else{
			String todaysDate=new Date().format('d MMMM')
			int todaysYear=new Date().format('YYYY').toInteger()
			for(b in d.db*.findAll{it.value.age}){
				String raw=b.value.age
				if(raw.startsWith(todaysDate)){
					int birthYear
					try{
						birthYear=raw.tokenize()[-1].toInteger()
					}catch(ex){
						
					}
					if(!birthYear){
						eventsToday+="\u2022 ${b.value.name.capitalize()}'s birthday"
					}else{
						eventsToday+="\u2022 ${b.value.name.capitalize()}'s ${((todaysYear-birthYear).toString()+' ').formatBirthday()}birthday"
					}
				}
				for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==todaysDate)eventsToday+="\u2022 ${s.name.capitalize()}'s ${((todaysYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary"
				if(specials[todaysDate])eventsToday+="\u2022 ${specials[todaysDate]}"
			}
			eventsToday=eventsToday.unique()
			List eventsUpcoming=[]
			for(n in 1..7){
				String upcomingDate=(new Date()+n).format('d MMMM')
				int upcomingYear=(new Date()+n).format('YYYY').toInteger()
				for(b in d.db*.findAll{it.value.age}){
					String raw=b.value.age
					if(raw.startsWith(upcomingDate)){
						int birthYear
						try{
							birthYear=raw.tokenize()[-1].toInteger()
						}catch(ex){
							
						}
						if(!birthYear){
							eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s birthday (${upcomingDate.formatBirthday()})"
						}else{
							eventsUpcoming+="\u2022 ${b.value.name.capitalize()}'s ${((upcomingYear-birthYear).toString()+' ').formatBirthday()}birthday (${upcomingDate.formatBirthday()})"
						}
					}
				}
				for(s in e.jda.guilds.findAll{it.users.size()>249})if(s.createTime.format('d MMMM')==upcomingDate)eventsUpcoming+="\u2022 ${s.name.capitalize()}'s ${((upcomingYear-s.createTime.format('YYYY').toInteger()).toString()+' ').formatBirthday()}anniversary (${upcomingDate.formatBirthday()})"
				if(specials[upcomingDate])eventsUpcoming+="\u2022 ${specials[upcomingDate]} (${upcomingDate.formatBirthday()})"
			}
			eventsUpcoming=eventsUpcoming.unique()
			message.edit("**__Events for ${todaysDate.formatBirthday()} (Today) ($eventsToday.size)__:**\n${if(eventsToday){eventsToday.join('\n')}else{'Nothing interesting today.'}}\n**__Upcoming Events (7 Days) ($eventsUpcoming.size)__:**\n${if(eventsUpcoming){eventsUpcoming.join('\n')}else{'Sure is boring around here.'}}").queue()
		}
	}
	String category = 'Database'
	String help = """`events2` will make me tell you today's and upcoming events.
`events2 [number]` will make me tell you the events upcoming in those days.
`events2 [date]` will make me tell you the events for that day.
No-one ever remembers my birthday..."""
}


class ColourCommand extends Command {
	List aliases = ['colour','color']
	int limit = 55
	def run(Map d, Event e) {
		if(e.guild){
			if(d.modes.colour[e.guild.id]==false){
				e.sendMessage('Colours have been disabled by your jurisdiction.').queue()
			}else{
				if(d.args){
					if(e.guild.selfMember.roles.any{'MANAGE_ROLES'in it.permissions*.toString()}){
						e.sendTyping().queue()
						List options=[["Enjoy your new colour! It looks good on you!","Done! How's it look?"],["Geniet van je nieuwe kleur! Het staat je goed!","Doed! Hoe doe het kijk?"],["Aproveite a sua nova cor! Fica bem em voce!","Feito! Como esta?"]].lang(e)
						try{
							boolean remove
							String colour=d.args.tokenize()[0].toLowerCase().replace('0x','').replaceAll(/\W+/,'')
							boolean random=(colour=='random')
							if(d.colours[colour]){
								colour=d.colours[colour]
							}else if(colour=='remove'){
								remove=true
								options=[["I've removed your colour. Better now?",'You look good as new!'],['Ik heb verwijderd je kleur. Beter nu?','Je kijken goed vind nu!']].lang(e)
							}else if(random){
								colour=''
								6.times{colour+=(('0'..'9')+('a'..'f')).random()}
							}else if(colour.length()>6){
								colour=colour.substring(0,6)
							}else if(colour.length()<6){
								colour+="0"*(6-colour.length())
							}
							User user=e.author
							boolean another
							if(e.message.mentions&&user.isStaff(e.guild)){
								user=e.message.mentions[-1]
								if(remove)options=[["I've removed your colour, $user.identity. Better now?","You look good as new, $user.identity!"]]
								else options=[["Enjoy your new colour, $user.identity! It looks good on you!","Done, $user.identity! How's it look?"],["Geniet van je nieuwe kleur, $user.identity! Het staat je goed!","Doed, $user.identity! Hoe doe het kijk?"],["Aproveite a sua nova cor, $user.identity! Fica bem em voce!","Feito, $user.identity! Como esta?"]].lang(e)
								another=true
							}
							Member member=e.guild.members.find{it.user.id==user.id}
							Color hex=new Color(Integer.parseInt(colour,16))
							e.sendTyping().queue()
							String name="#${colour.toUpperCase()}"
							Role role=e.guild.roles.find{it.name.toLowerCase().replace('#','')==colour}
							if(!role){
								RoleManager manager=e.guild.controller.createRole().complete().manager
								manager.setName(name).complete()
								Thread.sleep(200)
								manager.setColor(hex).complete()
								Thread.sleep(200)
								manager.setPermissions(0).complete()
								role=manager.role
							}
							if(random){
								if(another)options=[["Enjoy your $role.mention colour, $user.identity! It looks good on you!","Done, $user.identity! How's $role.mention look?"],["Geniet van je $role.mention kleur, $user.identity! Het staat je goed!","Doed, $user.identity! Hoe doe $role.mention kijk?"],["Aproveite a sua $role.mention cor, $user.identity! Fica bem em voce!","Feito, $user.identity! Como esta $role.mention?"]].lang(e)
								else options=[["Enjoy your $role.mention colour! It looks good on you!","Done! How's $role.mention look?"],["Geniet van je $role.mention kleur! Het staat je goed!","Doed! Hoe doe $role.mention kijk?"],["Aproveite a sua $role.mention cor! Fica bem em voce!","Feito! Como esta $role.mention?"]].lang(e)
							}
							List old=e.guild.members.find{it.user.id==user.id}.roles.findAll{it.colour}
							if(!remove)e.guild.controller.addRolesToMember(member,[role]).complete()
							List failed=[]
							old.each{
								try{
									Thread.sleep(150)
									e.guild.controller.removeRolesFromMember(member,it).complete()
								}catch(perms){
									perms.printStackTrace()
									failed+=it
									old-=it
								}
							}
							String message=options.random()
							if(old)message+=["\n(Your previous colour was ${old*.mention.join(' ')}.)","\n(Je kleur voor was ${old*.mention.join(' ')}.)","\n(Sua cor anterior era ${old*.mention.join(' ')}.)"].lang(e)
							if(failed)message+=["\n(Due to permissions, I couldn't remove ${failed*.mention.join(' ')}.)","\n(Aangezien machtigingen bestaan, ik kon niet verwijderen ${failed*.mention.join(' ')}.)","\n(Devido as permissoes, nao pude remover o ${failed*.mention.join(' ')}.)"].lang(e)
							e.sendMessage(message).complete()
							old.findAll{!(it.id in e.guild.members*.roles*.id.flatten())}.each{
								it.delete().complete()
								Thread.sleep(150)
							}
						}catch(ex){
							e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}colour [hex/svg]/random/remove`. SVG list: http://december.com/html/spec/colorsvg.","Gebruik: `${d.prefix}colour [hex/svg]/random/remove`. SVG-lijst: http://december.com/html/spec/colorsvg.","Uso: `${d.prefix}colour [hex/svg]/random/remove`. Lista SVG: http://december.com/html/spec/colorsvg."].lang(e)).queue()
							ex.printStackTrace()
							400
						}
					}else{
						e.sendMessage("I need to be able to manage roles to do that...").queue()
						511
					}
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}colour [hex/svg]/random/remove`.","Gebruik: `${d.prefix}colour [hex/svg]/random/remove`.","Uso: `${d.prefix}colour [hex/svg]/random/remove`."].lang(e)).queue()
					400
				}
			}
		}else{
			e.sendMessage("Colours cannot be given in Direct Messages.").queue()
			405
		}
	}
	String category = 'General'
	String help = """`colour [hex/svg]/random/remove` will make me give you the colour using roles, give you a random colour or remove all your colour roles.
`colour [hex/svg]/random/remove [user]` will make me give the user the colour. (Staff)
Let there be rainbows."""
}


class StatsCommand extends Command {
	List aliases = ['stats']
	def run(Map d, Event e) {
		List uptime = [0, (((System.currentTimeMillis() - d.started) / 1000) / 60)as int]
		(uptime[1] / 60).times{
			uptime[0] += 1
			uptime[1] -= 60
		}
		final Runtime runtime = Runtime.runtime
		String stats = """Connected to `${e.jda.guilds.size()}` servers with `${e.jda.channels.size()}` channels and `${e.jda.users.size()}` users.
Total `${d.db*.key.size()}` database entries, and `${d.tags*.key.size}` tags and `${d.custom*.value.flatten().size()}` custom commands.
Online for `${uptime[0]}` hour${if (uptime[0] != 1) {'s'} else {''}} and `${uptime[1]}` minute${if (uptime[1] != 1) {'s'} else {''}}."""
		if (d.args.toLowerCase() == 'full') {
			final List os = [System.getProperty('os.name'), System.getProperty('os.version'), System.getProperty('os.arch'), System.getProperty('sun.os.patch.level')]
			final String groove = GroovySystem.version
			final String jave = System.getProperty('java.version')
			stats += """
Recieving `$e.responseNumber` messages over a `v6` doggy door.
I'm a Microsoft fanboy using ${os[0]} (${os[1]}) ${os[2]} ${os[3]}.
Remembering where I buried `${((runtime.totalMemory() - runtime.freeMemory()) / 1048576) as int}` out of `${(runtime.totalMemory() / 1048576) as int}` bones.
DOSing `${d.feeds*.value.link.flatten().unique().size()}` different webpages for feed purposes. Sorry!
I have my coffee with `$jave` sugars which raises my groove to `$groove`.
I've successfully taken `${d.messages.size()}` command${if (d.messages.size() != 1) {'s'} else {''}} this session.
`${new File('main.groovy').readLines().size()}` lines of code (main file), `${new File('methods.groovy').readLines().size()}` (methods file)."""
		}
		e.sendMessage(stats).queue()
	}
	String category = 'General'
	String help = """`stats` will make me tell you some of my stats.
`stats full` will make me tell you some more of my stats.
I don't know what else you were expecting."""
}


class LoveCommand extends Command {
	List aliases = ['love','ship']
	def run(Map d, Event e) {
		d.args=d.args.split(/ and | & /)
		if(d.args.size()==2){
			2.times{
				if(d.args[it].length()>500)d.args[it]=d.args[it].substring(0,500)
			}
			int result=d.args[0].toCharArray().inject(d.args[1].toCharArray().inject(0){i,j->i+j}){i,j->i+j}%102
			if(result>100){
				e.sendMessage("**${['Wow','Wauw','Uau','Lal'].lang(e)}!** ${d.args[0]} + ${d.args[1]} = :heartpulse:").queue()
			}else{
				e.sendMessage(["${d.args[0].capitalize()} and ${d.args[1]} are $result% compatible.","${d.args[0].capitalize()} en ${d.args[1]} are $result% verenigbaar.","${d.args[0].capitalize()} e ${d.args[1]} estamos $result% compativel.","${d.args[0].capitalize()} i ${d.args[1]} sa kompatybilne z $result%."].lang(e)).queue()
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}love [someone] & [someone]`.","Gebruik: `${d.prefix}love [iemand] & [iemand]`.","Uso: `${d.prefix}love [alguem] & [alguem]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`love [someone] & [someone]` will make me ship two people. Retains consistent results.
OTP! Like OMG!"""
}


class BallCommand extends Command {
	List aliases = ['8ball','magicball']
	def run(Map d, Event e) {
		if(d.args){
			if(d.args.length()>1000)d.args=d.args.substring(0,1000)+'...'
			String question=d.args.replaceAll(/\?$/,'').capitalize()
			String answer=[["It is certain"]*3,["It is decidedly so"]*3,["Without a doubt"]*3,["Yes, definitely"]*3,["You may rely on it"]*3,["As I see it, yes"]*3,["Most likely"]*3,["Outlook good"]*3,["Signs point to yes"]*3,"Reply hazy, try again","Ask again later","Better not tell you now","Concentrate and ask again",["Don't count on it"]*3,["My reply is no"]*3,["My sources say no"]*3,["Outlook not so good"]*3,["Very doubtful"]*3].flatten().random()
			e.sendMessage("*$question?*\n$answer, $e.author.identity.").queue()
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}8ball [question]`.","Gebruik: `${d.prefix}8ball [vraag]`.","Uso: `${d.prefix}8ball [questao]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`8ball [question]` will make me shake my magic 8ball and answer the question.
More likely to return an actual answer."""
}


class SetAvatarCommand extends Command {
	List aliases = ['setavatar']
	int maximum=13
	int limit = 60
	def run(Map d, Event e) {
//		e.sendMessage("A powerful event prevents you from changing GRover's avatar.").queue()
		d.args=d.args.toLowerCase()
		if(!d.args||(d.args=='random')||(d.args in(1..maximum)*.toString())){
			if(!d.args||(d.args=='random'))d.args=(1..maximum).random()
			else d.args=d.args.toInteger()
			int old=d.info.avatar
			d.info.avatar=d.args
			d.json.save(d.info,'properties')
			try{
				e.jda.setAvatar("images/avatars/${d.args}.jpg").complete()
				Thread.sleep(2000)
				e.sendMessage("My avatar has been changed to $d.args. My previous one was $old.").queue()
			}catch(ex){
				e.sendMessage("You are changing my avatar too fast. Try again in a bit.").queue()
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}setavatar [1..$maximum]/random`.","Gebruik: `${d.prefix}setavatar [1..$maximum]/random`.","Uso: `${d.prefix}setavatar [1..$maximum]/random`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`setavatar [1..9]/random` will make me change my avatar.
Just 10 to choose from now."""
}


class SetPrefixCommand extends Command {
	List aliases = ['prefix','setprefix']
	def run(Map d, Event e) {
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
					e.sendMessage("We're going unprefixed, baby. Remember you can always use ${d.bot.mention[0]} as a prefix.").queue()
				}
				d.json.save(d.settings,'settings')
			}else{
				e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to set the prefix in Direct Messages.").queue()
			405
		}
	}
	String category = 'Moderation'
	String help = """`setprefix [prefixes]` will make me set my prefix. Separate with spaces.
Finally, my bot can respond to !"��\$%^&*()_+help."""
}


class EvalCommand extends Command {
	List aliases = ['eval']
	def run(Map d, Event e) {
		if (e.author.id == d.bot.owner) {
			if (d.args.contains('system32')) {
				e.sendMessage('THIS IS THE ACTUAL EVAL YOU MORON').queue()
			} else {
				try {
					Binding binding = new Binding(d + [e: e])
					long startTime = System.currentTimeMillis()
					String eval = new GroovyShell(binding).evaluate(d.args).toString()
					long stopTime=System.currentTimeMillis()
					println(eval)
					long startTime2 = System.currentTimeMillis()
					e.sendMessage(eval).queue {
						long stopTime2 = System.currentTimeMillis()
						it.edit("$it.contentRaw\n`${stopTime-startTime}ms`, `${stopTime2-startTime2}ms`").queue()
					}
				} catch (ex) {
					e.sendMessage("$ex").queue()
					ex.printStackTrace()
					500
				}
			}
		} else {
			if (d.args) {
				Map evaluation = new JsonSlurper().parseText(Unirest.post('http://groovyconsole.appspot.com/executor.groovy')
				.field('script', d.args).asString().body)
				String output = ''
				if (evaluation.executionResult) output += "**Result**:\n$evaluation.executionResult\n"
				if (evaluation.outputText) output += "**Output**:\n$evaluation.outputText\n"
				if (evaluation.stacktraceText) output += "**Error**:\n$evaluation.stacktraceText\n"
				try {
					e.sendMessage(output).queue()
				} catch (ignored) {
					e.sendTyping().queue()
					String up = Unirest.post('https://puush.me/api/up').field('k', new File('token').readLines()[5]).field('z', 'dogbot').field('f', output.bytes, "evaluation.txt").asString().body.split(',')[1]
					e.sendMessage().queue("Result was over 2000 characters: $up")
				}
			} else {
				e.sendMessage(d.errorMessage() + "Usage: `${d.prefix}eval [code]/random`.").queue()
			}
		}
	}
	String category = 'Online'
	String help = """`eval [code]` will make me evaluate some Groovy code online.
Everyone can use this now."""
}


class ConfigCommand extends Command {
	List aliases = ['config']
	boolean dev = true
	def run(Map d, Event e) {
		if(e.author.id==d.bot.owner){
			Guild ass=e.jda.guilds.find{it.id==d.args}?:e.guild
			e.sendMessage("""```css
Owners: ${ass.users.findAll{it.isOwner(ass)&&!it.bot}*.identity.join(', ')}
Staff: ${(ass.users.findAll{it.isStaff(ass)&&!it.bot}-ass.users.findAll{it.isOwner(ass)})*.identity.join(', ')}
Spam Channels: ${ass.channels.findAll{it.spam}*.name.join(', ')}
Log Channels: ${ass.channels.findAll{it.log}*.name.join(', ')}
NSFW Channels: ${ass.channels.findAll{it.nsfw}*.name.join(', ')}
Song Channels: ${ass.channels.findAll{it.song}*.name.join(', ')}
Ignored Channels: ${ass.channels.findAll{it.ignored}*.name.join(', ')}
Prefix: ${d.settings.prefix[ass.id]?.join(' ')}
Customs: ${(d.customs[ass.id]?:[])*.name.join(' ')}
Join: ${d.tracker.join[ass.id]}
Leave: ${d.tracker.leave[ass.id]}
xat Smilies: ${d.settings.smilies[ass.id]as boolean}
Vote Pin: ${d.settings.votepin[ass.id]?:3}```""").queue()
			e.sendMessage("""```css
${ass.textChannels.findAll{it.id in d.feeds*.value.flatten()*.channel}.collect{Channel mom->"Feeds (#$mom.name): ${d.feeds*.value.flatten().findAll{it.channel==mom.id}*.link.join(' ')}"}.join('\n')}```""").queue()
		}else{
			204
		}
	}
}


class WordCountCommand extends Command {
	List aliases = ['wordcount','words']
	def run(Map d, Event e) {
		String input=d.args
		if (e.message.attachment) input += d.web.download(e.message.attachment.url, 'temp/wordcount.txt').text()
		if(input){
			List words=input.replace('\r\n','\r').replace('\n\r','\n').replaceAll(['\r','\n','-','_','\u3000','\u30fc','\uff3f','\u00a1','?','!','\uff1f','\uff01','(',')','+','=',':',';','{','}','[',']','/','<','>','.',',','\u3002','\u3001'],' ').tokenize()
			List lines=input.replace('\r\n','\r').replace('\n\r','\n').tokenize('\r')*.split('\n').flatten()
			String longestWord=words.join(' ').replaceAll(/\d+/,'').replaceAll(['"','*',"'",'|'],' ').tokenize().sort{it.length()}.last()
			if(longestWord.length()>500)longestWord=longestWord.substring(0,500)+'...'
			e.sendMessage("""${words.size()} words
${lines.size()} lines (${(lines*.trim()-'').size()} without empty)
${input.length()} length (${input.replaceAll([' ','-','_','\n','\r'],'').length()} without spaces)

Longest word: "$longestWord"
Longest line: line ${lines.indexOf(lines.sort{it.length()}.last())+1}""".replace('\n1 lines','\n1 line')).queue()
			new File('temp/wordcount.txt').delete()
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}wordcount [text/file]`.","Gebruik: `${d.prefix}wordcount [tekst/dossier]`.","Uso: `${d.prefix}wordcount [texto/arquivo]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`wordcount [text/file]` will make me give you some word statistics.
It won't evaluate your essay homework, though."""
}


class LogCommand extends Command {
	List aliases = ['log','archive']
	int limit = 100
	def run(Map d, Event e) {
		e.sendTyping().queue()
		int size = 50
		d.args = d.args.tokenize() + ''
		String arg = d.args[-1]
		if (arg ==~ /\d+/) size = arg.toInteger()
		if (size > 100) size = 100
		if (size < 2) size = 2
		String log = "${new Date().format('d MMMM YYYY').formatBirthday()}, #${if(e.guild){e.channel.name}else{e.author.name}} in ${try{e.guild.name}catch(DM){'Direct Messages'}}:\r\n"
		List logs = e.channel.history.retrievePast(size).complete().reverse() - e.message
		for (l in logs) log += "\r\n[${l.createTime.format('HH:mm:ss')}] [$l.author.identity]: ${l.content.replace('\r\n','\n').replace('\r','\r\n  ').replace('\n','\r\n  ').replace('\u200b','')}${if(l.attachments){"${if(l.content){"\r\n"}else{''}}${l.attachments*.url}"}else{''}}"
		e.sendMessage(Unirest.post('https://puush.me/api/up').field('k', new File('token').readLines()[5]).field('z', 'dogbot').field('f', log.bytes,"archive-${e.author.id}.log").asString().body.split(',')[1]).queue()
	}
	String category = 'General'
	String help = """`log [number]` will make me generate a log of the channel history to up to 100 messages ago.
It's too late to take back what you said."""
}


class ScopeCommand extends Command {
	List aliases = ['scope','online']
	int limit = 45
	def run(Map d, Event e) {
		Map emotes=[online:':o:313956277808005120',idle:':i:313956277220802560',do_not_disturb:':d:313956276893646850',offline:':o:313956277237710868',unknown:':u:313956277107556352']
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
						String abbrev=alias.replaceAll([' ','-','_',"'",'"','(',')','[',']','{','}','<','>','|','.','/',':',';'],'')?:alias
						String name="$u.user.identity ($abbrev)"
						if(name.length()>26)name=name.substring(0,24)+'\u2026'
						ass+="<${emotes[u.status]}> `$name${' '*(26-name.length())}\u200b`"
						ass+=pos?'\n':' '
						pos=pos?0:1
					}
					ass+='\n'
				}
			}
			List users=e.guild.members.findAll{it.status!='offline'}.findAll{!it.user.bot}.findAll{it.user.rawIdentity}.findAll{!(it.user.id in used)}.toList().sort{it.effectiveName}
			if(users){
				ass+="**${e.guild.name.capitalize()}**:\n"
				int pos=0
				users.each{Member u->
					used+=u.user.id
					String alias=u.effectiveName
					String abbrev=alias.replaceAll([' ','-','_',"'",'"','(',')','[',']','{','}','<','>','|','.'],'')?:alias
					String name="$u.user.identity ($abbrev)"
					if(name.length()>26)name=name.substring(0,24)+'\u2026'
					ass+="<${emotes[u.status]}> `$name${' '*(26-name.length())}\u200b`"
					ass+=pos?'\n':' '
					pos=pos?0:1
				}
				ass+='\n'
			}
			if(!ass)ass=["It would appear that I don't actually know anyone here.",'Er zou lijkt ik doe niet werkelijk snap iedereen hier.','Parece que na verdade nao conheco ninguem aqui.'].lang(e)
			ass=ass.replace('\n\n','\n').split(1999)
			if(ass.size()>1){
				String split=ass[0].substring(ass[0].lastIndexOf('\n'))
				ass[0]-=split
				ass[1]=split+ass[1]
			}
			if(ass.size()>2){
				e.sendMessage(['This server has way too many members to scope...','Deze server heb te veel gebruikers op scope...','Este servidor tem muitos membros para o scope...'].lang(e))
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
				String abbrev=e.author.name.replaceAll([' ','-','_',"'",'"','(',')','[',']','{','}','|','.'],'')
				String name="$e.author.identity ($abbrev)"
				if(name.length()>26)name=name.substring(0,24)+'\u2026'
				ass+="<${emotes[e.jda.guilds*.members.flatten().find{it.user.id==e.author.id}.status]}> `$name${' '*(26-name.length())}\u200b` "
				e.sendMessage(ass).queue()
			}else{
				e.sendMessage(["It would appear that I don't actually know you.",'Het wil lijken dat ik doe niet snap je.','Parece que na verdade nao conheco voce.'].lang(e)).queue()
				500
			}
		}
	}
	String category = 'Database'
	String help = """`scope` will make me use the database to tell you who everyone really is.
It's like the Silph Scope but it doesn't work on ghosts."""
}


class FeedCommand extends Command {
	List aliases = ['feed','feeds']
	int limit = 30
	def run(Map d, Event e) {
		if(!e.guild||e.author.isStaff(e.guild)){
			List feeds=(d.feeds.youtube+d.feeds.animelist+d.feeds.twitter+d.feeds.levelpalace).findAll{it.channel==e.channel.id}
			if(d.args.toLowerCase()=='list'){
				if(feeds){
					String fed=feeds*.link.join('>\n<').replace('&client=dogbot','').replace('rss.php?type=rwe&u=','animelist/')
					e.sendMessage("**Feeds for ${e.guild?'#'+e.channel.name:e.channel.user.name+'\'s DM'}**:\n<$fed>\n\nFeeds are updated every hour.").queue()
				}else{
					e.sendMessage("**Feeds for ${e.guild?'#'+e.channel.name:e.channel.user.name+'\'s DM'}**:\nNo feeds, add some!").queue()
				}
			}else if(d.args.contains('youtube.com')){
				String link=d.args
				if(!link.startsWith('http'))link="https://$link"
				if(!link.endsWith('/videos'))link+='/videos'
				try{
					if(link in feeds*.link){
						d.feeds.youtube-=feeds.find{(it.link==link)&&(it.channel==e.channel.id)}
						e.sendMessage("YouTube channel removed from the feed for this channel.").queue()
					}else if(feeds.size()>=15){
						e.sendMessage("YouTube chan- oh, looks like you've hit the feed limit for this channel. Please consider removing a feed, or go premium for just ��1000000!").queue()
					}else{
						e.sendTyping().queue()
						Document doc=d.web.get(link)
						String id=doc.getElementsByClass('yt-lockup-title')[0].getElementsByTag('a')[0].attr('href')
						d.feeds.youtube+=[
							channel:e.channel.id,
							link:link,
							last:id,
							user:e.guild?null:e.author.id
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
					String link=d.args.replaceAny(['animelist/','profile/'],'rss.php?type=rwe&u=')
					if(!link.startsWith('http'))link="https://$link"
					if(link in feeds*.link){
						d.feeds.animelist-=feeds.find{(it.link==link)&&(it.channel==e.channel.id)}
						e.sendMessage("Anime list removed from the feed for this channel.").queue()
					}else if(feeds.size()>=15){
						e.sendMessage("Anime lis- oh, looks like you've hit the feed limit for this channel. Please consider removing a feed, or go premium for just ��1000000!").queue()
					}else if(d.bot.commands.find{'anime'in it.aliases}.available){
						e.sendTyping().queue()
						Document doc=d.web.get(link)
						Element anime=doc.getElementsByTag('item')[0]
						List data=anime.getElementsByTag('description')[0].text().replace(' episodes','').split(' - ')
						String name=anime.getElementsByTag('title')[0].text().split(' - ')[0]
						String id="$name/${data[1].tokenize()[0]}"
						d.feeds.animelist+=[
							channel:e.channel.id,
							link:link,
							last:id,
							user:e.guild?null:e.author.id
						]
						e.sendMessage("Anime list added to the feed for this channel.").queue()
					}else{
						final User axew = e.jda.users.find{it.id == d.bot.owner}
						e.sendMessage("MyAnimeList is under maintenance right now, check back later.\n(If you believe this is in error, go bug $axew.name#$axew.discriminator.)").queue()
						503
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
					}else if(feeds.size()>=15){
						e.sendMessage("Twitter hand- oh, looks like you've hit the feed limit for this channel. Please consider removing a feed, or go premium for just ��1000000!").queue()
					}else{
						e.sendTyping().queue()
						Document doc=d.web.get(link)
						int ass=doc.getElementsByClass('js-pinned-text')?1:0
						String stamp=doc.getElementsByClass('tweet-timestamp')[ass].attr('data-conversation-id')
						d.feeds.twitter+=[
							channel:e.channel.id,
							link:link,
							last:stamp,
							user:e.guild?null:e.author.id
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
					}else if(feeds.size()>=15){
						e.sendMessage("Level Palace acc- oh, looks like you've hit the feed limit for this channel. Please consider removing a feed, or go premium for just ��1000000!").queue()
					}else if(d.bot.commands.find{'levelpalace'in it.aliases}.available){
						e.sendTyping().queue()
						Document doc=d.web.get(link)
						String id=doc.getElementsByClass('levels-table')[0].getElementsByTag('a')[0].attr('href')
						d.feeds.levelpalace+=[
							channel:e.channel.id,
							link:link,
							last:id,
							user:e.guild?null:e.author.id
						]
						e.sendMessage("Level Palace account added to the feed for this channel.").queue()
					}else{
						final User axew = e.jda.users.find{it.id == d.bot.owner}
						e.sendMessage("Level Palace is under maintenance right now, check back later.\n(If you believe this is in error, go bug $axew.name#$axew.discriminator.)").queue()
						503
					}
					d.json.save(d.feeds,'feeds')
				}catch(ex){
					e.sendMessage("Malformed Level Palace account. Make sure the link leads to a level list and that at least one level has been posted.").queue()
					404
				}
			}else{
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}feed [youtube/levelpalace/twitter/animelist url]/list`.","Gebruik: `${d.prefix}feed [youtube/levelpalace/twitter/animelist url]/list/check`.","Uso: `${d.prefix}feed [youtube/levelpalace/twitter/animelist url]/list/check`."].lang(e)).queue()
				400
			}
		}else{
			e.sendMessage(d.permissionMessage()+"Requires any: `'Trainer' role`, `MANAGE MESSAGES permission`.").queue()
			403
		}
	}
	String category = 'Online'
	String help = """`feed [youtube/levelpalace/twitter/animelist url]` will make me add or remove that feed from the list for this channel.
`feed list` will make me tell you what feeds this channel is listening to.
You can feed into YouTube, MyAnimeList, Level Palace and Twitter. Isn't that neat?"""
}


class ClearCommand extends Command {
	List aliases = ['clear','prune']
	int limit = 25
	def run(Map d, Event e) {
		List users=e.message.mentions
		if(!e.guild||e.author.isStaff(e.guild)||((users*.id==[e.author.id])&&e.author.isMember(e.guild))){
			if(!e.guild||e.guild.selfMember.roles.any{'MESSAGE_MANAGE'in it.permissions*.toString()}||(users==[e.jda.selfUser])){
				e.sendTyping().queue()
				List numbers=d.args.findAll(/\d+/)
				long number = numbers ? Long.parseLong(numbers[-1]) : 50
				if(number<1)number=1
				if(number>99)number=99
				List history=e.channel.history.retrievePast((number+1).toInteger()).complete()-e.message
				List messages=[]
				if(d.args){
					if(users)messages+=history.findAll{it.author.id in users}
					if(d.args.contains('bot'))messages+=history.findAll{it.author.bot}
					if(d.args.contains('link'))messages+=history.findAll{it.content=~/\w+:\/\//}
					if(d.args.contains('file'))messages+=history.findAll{it.attachments}
					if(d.args.contains('embed'))messages+=history.findAll{it.embeds}
					if(d.args.contains('command')&&e.guild){
						List prefixes=e.guild.users.findAll{it.bot}.findAll{d.db[it.id]}.collect{d.db[it.id].tags.range('(',')')}
						messages+=history.findAll{it.content.startsWithAny(prefixes)}
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
			e.sendMessage(d.permissionMessage()+"Requires any: `'Trainer' role`, `MANAGE MESSAGES permission`, `Use on self with any role`.").queue()
			400
		}
	}
	String category = 'Moderation'
	String help = """`clear [arguments]` will make me clear the chat.
Tick all that apply: [@mention (delete from)], [number (to delete)], bot, link, file, embed, command. Tick none to nuke the chat."""
}


class SetChannelCommand extends Command {
	List aliases = ['setchannel','setproperty']
	def run(Map d, Event e) {
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
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.spam){'now'}else{'no longer'}} a spam channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['log','report']){
					d.channels.log[channel.id]=channel.log?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.log){'now'}else{'no longer'}} a log channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['nsfw','porn']){
					d.channels.nsfw[channel.id]=channel.nsfw?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.nsfw){'now'}else{'no longer'}} an NSFW channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['song','music']){
					d.channels.song[channel.id]=channel.song?false:true
					e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.song){'now'}else{'no longer'}} a song channel.").queue()
					d.json.save(d.channels,'channels')
				}else if(d.args[0]in['ignored','ignore']){
					if(e.guild.textChannels.findAll{it.ignored}.size()<(e.guild.textChannels.size()-1)){
						d.channels.ignored[channel.id]=channel.ignored?false:true
						e.sendMessage("**${channel.name.capitalize()}** is ${if(channel.ignored){'now'}else{'no longer'}} an ignored channel.").queue()
						d.json.save(d.channels,'channels')
					}else{
						e.sendMessage(['But how will I unignore it?','Maar hoe wil ik niet negeer het?','Mas como eu nao ignorar isso?','Ale jak to unignore?'].lang(e)).queue()
						400
					}
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}setchannel spam/log/nsfw/song/ignored [channel]`","Gebruik: `${d.prefix}setchannel spam/log/nsfw/song/ignored [kanaal]`","Uso: `${d.prefix}setchannel spam/log/nsfw/song/ignored [canai]`"].lang(e)).queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to set channel properties in Direct Messages.").queue()
			405
		}
	}
	String category = 'Moderation'
	String help = """`setchannel spam [channel]` will make me set the channel as spam, allowing automatic responses.
`setchannel log [channel]` will make me set the channel as a log, making me log staff actions there.
`setchannel nsfw [channel]` will make me set the channel as NSFW, so relevant commands can be used.
`setchannel song [channel]` will make me set the channel as song, so relevant commands can be used.
`setchannel ignored [channel]` will make me set the channel as ignored... Ignoring it.
These settings are probably already correct, but just to be safe..."""
}


class VotePinCommand extends Command {
	List aliases = ['votepin','vp']
	int limit = 25
	Map votes=[:]
	def run(Map d, Event e) {
		if(d.args){
			if(e.guild){
				String arg=d.args.tokenize()[0]
				if(e.author.isMember(e.guild)){
					if(!e.guild||e.guild.selfMember.roles.any{'MESSAGE_MANAGE'in it.permissions*.toString()}||e.author.isOwner(e.guild)&&(arg=='max')){
						if((arg=='max')&&e.author.isStaff(e.guild)){
							try{
								int prev=d.settings.votepin[e.guild.id]
								int max=d.args.substring(4).toInteger()
								if(max<1)max=1
								d.settings.votepin[e.guild.id]=max
								d.json.save(d.settings,'settings')
								e.sendMessage("The number of votes needed to pin a message has been changed to $max. The previous was $prev.").queue()
							}catch(ex){
								e.sendMessage(['Please enter a number.','Alsjeblieft invoeren een aantal.','Por favor coloque um numero.'].lang(e)).queue()
								400
							}
						}else{
							if(e.author.rawIdentity?.endsWithAny(['\'s Incognito','\'s Alternate Account'])){
								e.sendMessage(["Incognito can't vote.",'Incognito kan niet stemmen.','Incognito nao pode votar.'].lang(e)).queue()
								403
							}else{
								try{
									Message message
									if(d.args==~/\d+/){
										message=e.channel.getMessageById(d.args).complete()
									}else{
										List logs=e.channel.history.retrievePast(100).complete().findAll{!it.content.startsWithAny(d.bot.prefixes*.plus('v'))}
										message=logs.find{it.content.toLowerCase().contains(d.args.toLowerCase())}
									}
									if(message){
										int max=d.settings.votepin[e.guild.id]?:3
										if(message.pinned){
											e.sendMessage(['That message is already pinned.','Dat bericht is nu al vastzetten.','Essa mensagem ja esta marcada.'].lang(e)).queue()
											511
										}else if(e.author.id in votes[message.id]){
											votes[message.id]-=e.author.id
											e.sendMessage("Unvoted to pin $message.author.identity's message. (${votes[message.id].size()}/$max)").queue()
										}else if(message.author.id==e.author.id){
											e.sendMessage(['Wow, shameless self-promotion.','Wauw, schaamteloze zelfbevordering.','Uau, auto-promocao sem vergonha.'].lang(e)).queue()
											403
										}else{
											if(!votes[message.id])votes[message.id]=[]
											votes[message.id]+=e.author.id
											if(votes[message.id].size()>=max){
												message.pin().complete()
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
					e.sendMessage(d.permissionMessage()+"Requires: `Any role`.").queue()
					403
				}
			}else{
				Message message
				if(d.args==~/\d+/){
					message=e.channel.getMessageById(d.args).complete()
				}else{
					List logs=e.channel.history.retrievePast(100).complete().findAll{!it.content.startsWithAny(d.bot.prefixes*.plus('v'))}
					message=logs.find{it.content.toLowerCase().contains(d.args.toLowerCase())}
				}
				if(message){
					try{
						if(message.pinned){
							e.sendMessage(['That message is already pinned.','Dat bericht is nu al vastzetten.','Essa mensagem ja esta marcada.'].lang(e)).queue()
							511
						}else{
							message.pin().complete()
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
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}votepin [id/content]/max [number]`","Gebruik: `${d.prefix}votepin [id/inhoud]/max [aantal]`","Uso: `${d.prefix}votepin [id/conteudo]/max [numero]`"].lang(e)).queue()
			400
		}
	}
	String category = 'Moderation'
	String help = """`votepin [id/content]` will make me find a message and vote to pin it.
`votepin max [number]` will make me set how many votes are required to pin a message.
No more asking the staff to 'pin this.'"""
}


class SingCommand extends Command {
	List aliases = ['sing','song']
	int limit = 50
	boolean covered
	boolean singing
	Element lyricsLink
	String author
	String coverLink
	MessageChannel venue
	String nick
	String starter
	def run(Map d, Event e) {
		d.args = d.args.toLowerCase()
		if (singing && d.args == 'stop') {
			if (e.channel.id == venue.id) {
				if (!e.guild || e.author.isOwner(e.guild) || e.channel.song && (e.author.id == starter)) {
					e.jda.setAvatar("images/avatars/${d.info.avatar}.jpg").queue()
					if(e.guild)e.guild.controller.setNickname(e.guild.selfMember,nick).queue()
					new File('images/album.jpg').delete()
					singing=false
					venue=null
					coverLink=null
					e.sendMessage(['The song has been cancelled. Sorry folks.','Het lied heb bent geannuleerd. Sorry mensen.','A musica foi cancelada. Desculpe amigos.'].lang(e)).queue()
				}else{
					TextChannel songChannel=e.guild.textChannels.find{it.song}
					e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`${if(songChannel){", `Use in #$songChannel.name and be the starter of the song`"}else{''}}.\nStaff can set song channels with `${d.prefix}setchannel song`.").queue()
					403
				}
			}else{
				if(e.guild.id==venue.guild.id){
					e.sendMessage(['The song is not playing in this channel.','Het lied is niet spelen in het kanaal.','A musica nao esta sendo tocada neste canal.'].lang(e)).queue()
					403
				}else{
					e.sendMessage(['The song is not playing in this server.','Het lied is niet spelen in het guild.','A musica nao esta sendo tocada neste servidor.'].lang(e)).queue()
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
						Document search=d.web.get(link)
						lyricsLink=search.getElementsByClass('panel')[-1].getElementsByClass('text-left')[0].getElementsByTag('a')[0]
						author=search.getElementsByClass('panel')[-1].getElementsByClass('text-left')[0].getElementsByTag('b')[1].text()
						Document song=d.web.get(lyricsLink.attr('href'))
						e.sendTyping().queue()
						try{
							Element getLyrics=song.getElementsByTag('div').findAll{it.classNames().empty}[1]
							String ass=Jsoup.parse(getLyrics.html().replaceAll(/(?i)<br[^>]*>/,'#br#')).text()
							List lyrics=ass.replaceAll(/(\#br\#)+/,'\n').split('\n')*.trim()
							Iterator iterator=lyrics.iterator()
							try{
								Document doc=d.web.get(("https://www.google.co.uk/search?q=${URLEncoder.encode(("${lyricsLink.text()} $author")+'UTF-8')}&tbm=isch"),'3DS')
								Element image=doc.getElementsByClass('image')[0]
								doc=d.web.get(image.attr('href'),'3DS')
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
							Thread.sleep(9000)
							e.sendTyping().queue()
							if(singing ){
								if(e.guild) {
									nick=e.guild.selfMember.nickname
									if(!nick||nick?.startsWith('\u266b'))nick=''
									String title=lyricsLink.text()
									if(title.length()>31)title="${title.substring(0,30)}\u2026"
									e.guild.controller.setNickname(e.guild.selfMember,"\u266b$title").queue()
								}
								try{
									if(covered){
										e.jda.setAvatar('images/album.jpg').queue()
									}else{
										e.jda.setAvatar('images/musicgrover.jpg').queue()
									}
								Thread.sleep(1500)
								}catch(v6){
									v6.printStackTrace()
								}
								while(singing&&iterator.hasNext()){
									String lyric=iterator.next().trim()
									if ((lyric.length() > 2) && !lyric.contains('[')) {
										e.sendMessage("_${lyric.replace('_','\\_')}_").queue()
										Thread.sleep(2500)
									}
								}
								if (e.guild) e.guild.controller.setNickname(e.guild.selfMember, nick).queue()
								try {
									e.jda.setAvatar("images/avatars/${d.info.avatar}.jpg").queue()
									if (covered) new File('images/album.jpg').delete()
								} catch(v6) {
									v6.printStackTrace()
								}
								singing = false
								venue = null
								coverLink = null
								e.sendMessage(['My performance here is done.', 'Mijn prestatie hier is doed.', 'Meu desempenho aqui e feito.'].lang(e)).queue()
							}
						} catch(ex) {
							try {
								if(e.guild)e.guild.controller.setNickname(e.guild.selfMember,nick).queue()
								if(covered)new File('images/album.jpg').delete()
								e.jda.setAvatar("images/avatars/${d.info.avatar}.jpg").queue()
							} catch(v6) {
								v6.printStackTrace()
							}
							singing=false
							e.sendMessage(['There was a problem with the lyrics.','Er was een probleem met de songtekst.','Havia um problema com a letra.'].lang(e)).queue()
							lyricex.printStackTrace()
							500
						}
					}catch(none){
						singing=false
						e.sendMessage(["I couldn't find a song matching '$d.args.'","Ik kon niet vind een lied vind '$d.args' leuk.","Nao consegui encontrar uma musica como '$d.args.'"].lang(e)).queue()
						none.printStackTrace()
						404
					}
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}sing [song name]/stop/info`","Gebruik: `${d.prefix}sing [naam liedje]/stop/info`","Uso: `${d.prefix}sing [nome musica]/stop/info`"].lang(e)).queue()
					400
				}
			}else{
				TextChannel songChannel=e.guild.textChannels.find{it.song}
				e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`${if(songChannel){", `Use in #$songChannel.name`"}else{''}}.\nStaff can set song channels with `${d.prefix}setchannel song`.").queue()
				403
			}
		}else{
			if(venue?.guild){
				e.sendMessage(["I am already singing a song in $venue.guild.name.","Ik ben lied het zingen in $venue.guild.name nu al.","Ja estou cantando uma musica em $venue.guild.name."].lang(e)).queue()
				403
			}else{
				e.sendMessage(["I am already singing a song in Direct Messages.","Ik ben lied het zingen in Persoonlijke Berichten nu al.","Ja estou cantando uma musica em Mensagens diretas."].lang(e)).queue()
				403
			}
		}
/*		APRIL JAPE
		if(singing){
			e.sendMessage("Already singing a song, onii-chan. IT'S HITORIGOTO ALL DAY BABY!").queue()
		}else{
			singing=true
			e.sendMessage("The song starts in 10! I'm GRover and I'll be singing:\n**ClariS - Hitorigoto**\nAnd there is absolutely nothing you can do to stop it!").queue()
			Thread.sleep(10000)
			e.author.openPrivateChannel().complete()
			["Hitorigoto da yo, hazukashii koto kikanaide yo ne","Kimi no koto da yo, demo sono saki wa iwanai kedo ne","Kakechigaeteru, botan mitai na modokashisa o","Hodokenai mama, mata muzukashi shiyou to shiteru","Tsutaetai kimochi wa kyou mo","Kotoba ni naru chokozen ni, henkan misu no renzoku de","Tameiki to issho ni nomikondara horonigai","Futo shita toki ni sagashiteiru yo","Kimi no egao o sagashiteiru yo","Muishiki no naka sono riyuu wa mada ienai kedo","Hitori de iru to aitakunaru yo","Dare to itatte aitakunaru yo","Tatta hitokoto nee doushite aa","Ienai sono kotoba ienai kono kimochi aa","Hayaku kidzuitehoshii no ni"].each{
				e.author.privateChannel.sendMessage("_$it_").queue()
				Thread.sleep(2500)
			}
			e.author.privateChannel.sendMessage(['My performance here is done.','Mijn prestatie hier is doed.','Meu desempenho aqui e feito.'].lang(e)).queue()
			singing=false
		}*/
	}
	String category = 'General'
//	String help="`sing` will make me sing a song from an award-winning piece of Japanese animation."
	String help = """`sing [song name]` will make me sing a song in this channel.
`sing stop` will make me stop the song.
`sing info` will make me tell you some information about the song.
O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A- JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA."""
}

/*
class SmiliesCommand extends Command {
	List aliases = ['smilies','smilie']
	int limit = 25
	def run(Map d, Event e) {
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
					if(new File('images/cs').listFiles().findAll{it.name.endsWith("_${e.guild.id}.png")}.size()>49){
						e.sendMessage("You can't add any more smilies because you've reached the maximum amount, which is 50. Delete some of the less important ones and come see me again.").queue()
						403
					}else if(!name){
						e.sendMessage("What is the name supposed to be?").queue()
						400
					}else if(!d.args[2]){
						e.sendMessage("What is the smilie supposed to be?").queue()
						400
					}else if(name.containsAny(['@everyone','@here'])||(name in new File('images/xat').listFiles()*.name*.replace('_xat.png',''))){
						e.sendMessage("You can't add a smilie with that name${if(d.args[1].contains('@')){''}else{' because an xat smilie with that name already exists'}}.").queue()
						403
					}else if(new File("images/cs/${name}_${e.guild.id}.png").exists()){
						e.sendMessage("You can't add a smilie with that name because a smilie already exists with that name. Edit the smilie instead.").queue()
						400
					}else{
						try{
							InputStream input=new URL(d.args[2]).newInputStream(requestProperties:[Accept:'*'+'/'+'*'])
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
								InputStream input=new URL(d.args[2]).newInputStream(requestProperties:[Accept:'*'+'/'+'*'])
								BufferedImage smilie=ImageIO.read(input)
								if(smilie.height>150){
									e.sendMessage("The image is too tall. It should be less than 150 pixels in each dimension.").queue()
									403
								}else if(smilie.width>150){
									e.sendMessage("The image is too wide. It should be less than 150 pixels in each dimension.").queue()
									403
								}else{
									ImageIO.write(smilie,'png',new File("images/cs/${name}_${e.guild.id}.png"))
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
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}smilies xat/create/edit/delete/list/xatlist ..`","Gebruik: `${d.prefix}smilies xat/create/edit/delete/list/xatlist ..`","Uso: `${d.prefix}smilies xat/create/edit/delete/list/xatlist ..`"].lang(e)).queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to manage smilies in Direct Messages.").queue()
			405
		}
	}
	String category = 'Moderation'
	String help = """`smilies xat` will make me enable or disable posting xat smilies.
`smilies create [name] [link]` will make me create a custom smilie.
`smilies edit [name] [link]` will make me edit a custom smilie.
`smilies delete [name]` will make me delete a custom smilie.
`smilies list` will make me list this server's custom smilies.
`smilies xatlist` will make me list my xat smilies. Use sparingly.
Not like anyone uses this now though."""
}
*/

class AccessCommand extends Command {
	List aliases = ['access']
	def run(Map d, Event e) {
		if(d.args||e.message.attachments){
			if(d.args.toLowerCase().containsAny(['avatar','icon'])){
				d.args=d.args.replaceAny(['avatar','icon'],'').trim()
				d.bot.commands.find{it.aliases[0]=='avatar'}.run(d,e)
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
			}else if(d.args.toLowerCase().containsAny(['porn','hentai'])){
				d.args=d.args.replaceAny(['porn','hentai'],'').trim()
				d.bot.commands.find{it.aliases[0]=='nsfw'}.run(d,e)
			}else if(e.message.content.contains('://')){
				d.bot.commands.find{it.aliases[0]=='source'}.run(d,e)
			}else if(d.args.toLowerCase().containsAny(['+','-','*','/'])){
				d.bot.commands.find{it.aliases[0]=='math'}.run(d,e)
			}else if(e.message.mentions){
				d.bot.commands.find{it.aliases[0]=='userinfo'}.run(d,e)
			}else if(d.args.toLowerCase()in d.bot.commands.findAll{!it.dev}.aliases*.getAt(0).flatten()){
				Command cmd=d.bot.commands.find{d.args==it.aliases[0]}
				d.args=d.args.replace(cmd.aliases[0],'').trim()
				cmd.run(d,e)
			}else{
				d.bot.commands.find{it.aliases[0]=='google'}.run(d,e)
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}access [query]`.","Uso: `${d.prefix}access [vraag]`.","Uso: `${d.prefix}access [inquerir]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`access [query]` will make me use the best command for your query.
Now there's a Beaconville artifact if I've ever seen one."""
}


class TrackerCommand extends Command {
	List aliases = ['tracker']
	def run(Map d, Event e) {
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
					} else if (d.args[0] == 'role') {
						if (message) {
							Role role = e.guild.findRole(message)
							if (role) {
								d.tracker.role[e.guild.id] = role.id
								e.sendMessage("The role that gets given to new users is now **$role.name** ($role.id).").queue()
							} else {
								e.sendMessage("I couldn't find a role matching '$message.'").queue()
							}
						} else {
							d.tracker.role.remove(e.guild.id)
							e.sendMessage("A role will no longer be given to new users in this server.").queue()
						}
					}else{
						e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tracker join/leave/ban/unban [message]`.","Gebruik: `${d.prefix}tracker join/leave/ban/unban [bericht]`.","Uso: `${d.prefix}tracker join/leave/ban/unban [mensagem]`."].lang(e)).queue()
						400
					}
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}tracker join/leave/ban/unban [message]`.","Gebruik: `${d.prefix}tracker join/leave/ban/unban [bericht]`.","Uso: `${d.prefix}tracker join/leave/ban/unban [mensagem]`."].lang(e)).queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`.").queue()
				403
			}
		}else{
			e.sendMessage("No need to track in Direct Messages.").queue()
			405
		}
	}
	String category = 'Moderation'
	String help = """`tracker join [message]` will make me send the message whenever someone joins the server. Clear for no message.
`tracker leave [message]` will make me send the message whenever someone leaves the server. Clear for no message.
`tracker role [message]` will make me add a role to users who join. Clear for no role.
Welcome-! Use {user} to display the user's name in a message."""
}


class IsupCommand extends Command {
	List aliases = ['isup','isitdownrightnow']
	int limit = 50
	def run(Map d, Event e) {
		if(d.args){
			d.args = d.args.replace(' ', '-')
			(d.args - 'http://') - 'https://'
			if(!d.args.contains('.')) d.args += '.com'
			try {
				def address = InetAddress.getByName(d.args)
				try {
					long startTime = System.currentTimeMillis()
					address.isReachable(10000)
					long stopTime = System.currentTimeMillis()
					long time = (stopTime - startTime) / 1000
					e.sendMessage(["It's just you. **$address** is up and running. (${time}s)","Het is alleen je. **$address** is online en gelukkig. (${time}s)","E so voce. **$address** esta funcionando. (${time}s)"].lang(e)).queue()
				}catch(ex){
					e.sendMessage(["It's not just you. **$address** is down for me too.","Het is iedereen. **$address** is offline voor alle.","Nao e so voce. **$address** esta para baixo para todos."].lang(e)).queue()
				}
			} catch (wtf) {
				e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}isup [domain]`.","Gebruik: `${d.prefix}isup [website]`.","Uso: `${d.prefix}isup [local na rede internet]`."].lang(e)).queue()
				400
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}isup [domain]`.","Gebruik: `${d.prefix}isup [website]`.","Uso: `${d.prefix}isup [local na rede internet]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`isup [domain]` will make me check if that domain is online.
It's just you."""
}


class TopCommand extends Command {
	List aliases = ['top','popular']
	int limit = 25
	def run(Map d, Event e) {
		d.args=d.args.toLowerCase().tokenize()+'1'
		List top=[]
		int num=(d.args[1]==~/\d+/)?d.args[1].toInteger():1
		num-=num?1:0
		num*=15
		Range range=(0+num)..(14+num)
		try{
			int status=200
			if(d.args[0].startsWith('name')){
				Map table=e.jda.users.findAll{!it.bot}.groupBy{it.name.toLowerCase()}.sort{it.value.size()}
				table*.key.reverse()[range].each{
					top+="`#${num+=1}` **${it.tokenize()*.capitalize().join(' ')}** (${table[it].size()} users)"
				}
			}else if(d.args[0].startsWith('discrim')){
				Map table=e.jda.users.findAll{!it.bot}.groupBy{it.discriminator}.sort{it.value.size()}
				table*.key.reverse()[range].each{
					top+="`#${num+=1}` **#${it.tokenize().join(' ')}** (${table[it].size()} users)"
				}
			}else if(d.args[0].startsWith('game')){
				Map table=e.jda.guilds*.members.flatten().findAll{!it.user.bot}.findAll{it.game}.groupBy{it.user.id}*.value*.first().groupBy{it.game.name}.sort{it.value.size()}
				table*.key.reverse()[range].each{
					top+="`#${num+=1}` **$it** (${table[it].size()} playing)"
				}
			}else{
				top+=d.errorMessage()+["Usage: `${d.prefix}top names/discrims/games [page]`.","Gebruik: `${d.prefix}top names/discrims/games [pagina]`.","Uso: `${d.prefix}top names/discrims/games [pagina]`."].lang(e)
				status=400
			}
			e.sendMessage(top.join('\n')).queue()
			status
		}catch(ex){
			e.sendMessage("I'm unable to look this deep into it. Try a smaller page number.").queue()
			500
		}
	}
	String category = 'General'
	String help = """`top names [page]` will make me tell you the most common usernames among users that I can see.
`top discrims [page]` will make me tell you the most common discriminators among users I can see.
`top games [page]` will make me tell the games being played most right now by users I can see.
`top bots [page]` will make me tell you which bots share the most servers with me right now.
This is only as far as I can see, though. Imagine the global statistic."""
}


class CleanCommand extends Command {
	List aliases = ['clean']
	int limit = 25
	def run(Map d, Event e) {
		int amount=(d.args==~/\d+/)?d.args.toInteger():50
		amount+=1
		if(amount<2)amount=2
		if(amount>50)amount=50
		List history=e.channel.history.retrievePast(amount).complete()
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
	String category = 'General'
	String help = """`clean [number]` will make me remove my messages and your commands back that far.
Quick and painless, before they start saying 'GO TO TESTING!!'"""
}


class NoteCommand extends Command {
	List aliases = ['note','notes']
	def run(Map d, Event e) {
		d.args=d.args.tokenize()
		d.args[0]=d.args[0]?.toLowerCase()
		if(e.message.attachments)d.args+=e.message.attachments*.url
		String id=Long.toString((e.message.createTimeMillis/500)as long,36)
		List total=d.notes*.value.flatten().findAll{it.user==e.author.id}
		if(d.args[0]=='list'){
			if(total){
				e.sendMessage(total.collect{Map note->
					"`$note.id` \"${note.content.length()>80?note.content.substring(0,80)+'...':note.content}\" ${if(note.time){"(${new Date(note.time).format('H:mm d/M/YYYY')})"}else if(note.mention){"(${e.jda.users.find{it.id==note.mention}?.identity?:note.mention})"}else{''}}"
				}.join('\n')).queue()
			}else{
				e.sendMessage(['No notes found, add some!','Geen notes vinden, voegen sommige!'].lang(e)).queue()
			}
		}else if(d.args[0]in total*.id){
			Map note=total.find{it.id==d.args[0]}
			e.sendMessage("`$note.id`: \"$note.content\" ${if(note.time){"(${new Date(note.time).format('H:mm d/M/YYYY')})"}else if(note.mention){"(${e.jda.users.find{it.id==note.mention}?.identity?:note.mention})"}else{''}}").queue()
		}else if(d.args[0]in['remove','delete']){
			Map note=total.find{it.id==d.args[1].toLowerCase()}
			if(!note)note=total.find{it.content.contains(d.args[1])}
			if(note){
				d.notes.generic-=note
				d.notes.timed-=note
				d.notes.user-=note
				e.sendMessage(["That note (`$note.id`) has been removed.","Dat note ($note.id) heb bent verwijderde."].lang(e)).queue()
				d.json.save(d.notes,'notes')
			}else{
				e.sendMessage(["I couldn't find a note matching '${d.args[1]}.'","Ik kon niet vind een note vind '${d.args[1]}' leuk."].lang(e)).queue()
				404
			}
		}else if(d.args[0]=='clear'){
			total.each{
				d.notes.generic-=it
				d.notes.timed-=it
				d.notes.user-=it
			}
			e.sendMessage(['A shiny clean desk emerges.','Er een glanzend schoon bureau.'].lang(e)).queue()
			d.json.save(d.notes,'notes')
		}else if(d.args[0]in['generic','create','add']){
			if(d.args[1]){
				d.notes.generic+=[
					id:id,
					user:e.author.id,
					content:d.args[1..-1].join(' ')
				]
				e.sendMessage(["A generic note has been created at `$id`.","Een algemeen note is angemaakt op `$id`."].lang(e)).queue()
				d.json.save(d.notes,'notes')
			}else{
				e.sendMessage(['Please add some text for the note.','Alsjeblieft voegen tekst voor de note.'].lang(e)).queue()
				400
			}
		}else if(((d.args[0]=~/\d+\w/)||(d.args[0]==~/\d\d\/\d\d\/\d\d\d\d/))&&!e.message.mentions){
			Map entry=d.db.find{e.author.id in it.value.ids}?.value
			def time=(d.args[0]==~/\d\d\/\d\d\/\d\d\d\d/)?Date.parse('dd/MM/YYYY',d.args[0]).time:d.args[0].formatTime()
			String area=entry?.area?:'United States'
			String key=d.misc.time*.key.sort{-it.length()}.find{area.endsWith(it)}
			int zone=(d.misc.time[key]!=null)?d.misc.time[key]:d.misc.time['United States']
			d.notes.timed+=[
				id:id,
				user:e.author.id,
				time:time,
				content:d.args[1]?d.args[1..-1].join(' '):''
			]
			e.sendMessage(["A timed note has been created at `$id`. I will DM you at ${new Date(time+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()} time).","Een getimede note is angemaakt op `$id`. Ik wil PB je bij ${new Date(time+(zone*3600000)).format('HH:mm:ss, d MMMM yyyy').formatBirthday()} (${key.abbreviate()}-tijd)."].lang(e)).queue()
			d.json.save(d.notes,'notes')
		}else if((d.args[0]==~/<@!?\d+>/)&&e.message.mentions||e.guild&&e.guild.findUser(d.args[0])){
			User user=e.guild?.findUser(d.args[0])?:e.message.mentions[-1]
			d.notes.user+=[
				id:id,
				user:e.author.id,
				mention:user.id,
				content:d.args[1]?d.args[1..-1].join(' '):''
			]
			e.sendMessage(["A status note has been created at `$id`. I will DM you when I see $user.identity.","Een online-staat note is angemaakt op `$id`. Ik wil PB je wanneer ik zien $user.identity."].lang(e)).queue()
			d.json.save(d.notes,'notes')
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}note [@mention/time]/generic/list/remove/clear ..`.","Gebruik: `${d.prefix}note [@gebruiker/tijd]/generic/list/remove/clear ..`.","Uso: `${d.prefix}note [@utilizador/tempo]/generic/list/remove/clear ..`."].lang(e)).queue()
		}
	}
	String category = 'General'
	String help = """`note generic [text]` will make me create a normal note.
`note [@mention] [text]` will make me create a note that triggers when the user comes online.
`note [time] [text]` will make me create a note that triggers in that time.
`note list` will make me list the notes you've stored with me.
`note remove [id/text]` will make me remove that note.
`note clear` will make me remove all your notes.
Please note."""
}


class ProfileCommand extends Command {
	List aliases = ['profile']
	int limit = 60
	def run(Map d, Event e) {
		User user=e.author
		if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			Map entry=d.db.find{user.id in it.value.ids}?.value
			if(entry){
				e.sendTyping().queue()
				File ass=new File('temp/profile.png')
				OutputStream os=ass.newOutputStream()
				BufferedImage image=new BufferedImage(251,229,BufferedImage.TYPE_INT_ARGB)
				Graphics2D graphics=image.createGraphics()
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
				String date=new Date().format('d MMMM')
				String back=''
				if(entry?.age?.startsWith(date))back='_birthday'
				else if(date=='25 December')back='_christmas'
				else if(date=='31 October')back='_halloween'
				else if(date=='1 January')back='_newyear'
				else if(date=='26 December')back='_boxing'
				else if(date=='14 February')back='_loveday'
				else if(date=='8 August')back='_bv'
				else if(user.bot)back='_bot'
//				else if(user.id==d.bot.owner)back='_coder'
				graphics.drawImage(ImageIO.read(new File("images/profile${back}.png")),0,0,null)
				graphics.color=new Color((date=='1 April')?(0xFFFFF..0x000000).random():0x010101)
				graphics.font=new Font((date=='1 April')?'Comic Sans MS':'WhitneyBold',Font.PLAIN,16)
				File avatar=new File("temp/avatars/${user.avatarId?:user.defaultAvatarId}.png")
				if(!avatar.exists())avatar=d.web.download("${user.avatar.replace('.jpg','.png')}?size=64","temp/avatars/${user.avatarId}.png")
				graphics.drawImage(ImageIO.read(avatar),5,22,null)
				String title=["$user.identity's Profile","Profiel van $user.identity","Perfil da $user.identity","Profil $user.identity"].lang(e)
				graphics.drawString(title,3,15)
				graphics.font=new Font((date=='1 April')?'Comic Sans MS':'Calibri',Font.PLAIN,13)
				if(e.guild&&(d.modes.database[e.guild.id]==false)){
					graphics.drawString('Hidden',96,34)
					graphics.drawString('Hidden',96,54)
				}else{
					List area=entry.area.split(', ')
					graphics.drawString(area[0,-1].unique().join(', '),96,34)
					graphics.drawString(entry.age,96,54)
				}
				graphics.drawString((entry.accounts.mc?:'none'),96,73)
				graphics.font=new Font('Calibri',Font.BOLD,15)
				graphics.drawString(['Communities','Gemeenschappen','Comunidades','Wspolnotyz'].lang(e),3,103)
				graphics.font=new Font('Whitney Book',Font.PLAIN,14)
				int down=108
				List guilds=e.jda.guilds.findAll{user.id in it.users*.id}.findAll{!d.modes.hidden[it.id]}.sort{-it.members.size()}
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
				if(entry.gender==1)graphics.drawImage(ImageIO.read(new File("images/male.png")),236,78,null)
				else if(entry.gender==2)graphics.drawImage(ImageIO.read(new File("images/female.png")),236,78,null)
				graphics.dispose()
				ByteArrayOutputStream baos=new ByteArrayOutputStream()
				ImageIO.write(image,'png',baos)
				baos.writeTo(os)
				os.close()
				e.sendFile(ass).queue()
			}else{
				e.sendMessage(["There is no information in my database for $user.name.","Er is geen informatie in mijn databank voor $user.name.","Nao ha informacoes no meu banco de dados para $user.name.","W mojej bazie danych nie ma zadnych informacji o $user.name."].lang(e)).queue()
				404
			}
		}else{
			e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}
	}
	String category = 'Database'
	String help = """`profile [@mention]` will make me post their GRover profile.
This is a compiled image containing their information. It uses the database (and bandwidth)."""
}


class CustomCommand extends Command {
	List aliases = ['custom','alias']
	def run(Map d, Event e) {
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
								Command call=d.bot.commands.find{d.args[2] in it.aliases}
								if(call){
									if(!d.customs[e.guild.id])d.customs[e.guild.id]=[]
									d.customs[e.guild.id] += [
										name: d.args[1],
										list: [
											[
												command: call.aliases[0],
												args: d.args[3] ? d.args[3..-1].join(' ') : ''
											]
										],
										uses:0,
										time:System.currentTimeMillis()
									]
									e.sendMessage("The custom command **${d.args[1]}** has been created. You can now use `${d.prefix}${d.args[1]}`.").queue()
									d.json.save(d.customs,'customs')
								}else{
									e.sendMessage("I couldn't find a command matching '${d.args[2]}.'").queue()
									404
								}
							}else{
								e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom create [name] [command] [arguments]`.","Gebruik: `${d.prefix}custom create [naam] [commando] [argumenten]`.","Uso: `${d.prefix}custom create [nome] [comando] [argumentos]`."].lang(e)).queue()
								400
							}
						}
					}else{
						e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom create [name] [command] [arguments]`.","Gebruik: `${d.prefix}custom create [naam] [commando] [argumenten]`.","Uso: `${d.prefix}custom create [nome] [comando] [argumentos]`."].lang(e)).queue()
						400
					}
				}else if(d.args[0]=='edit'){
					d.args[1] = d.args[1].toLowerCase()
					if (d.args[1]) {
						Map custom = d.customs[e.guild.id].find{it.name == d.args[1]}
						if (custom) {
							if (d.args[2] ==~ /\d+/) {
								int number = d.args[2].toInteger() - 1
								if (custom.list[number]) { 
									d.args[3]=d.args[3].toLowerCase()
									if (d.args[3]) {
										Command call = d.bot.commands.find{d.args[3] in it.aliases}
										if (call) {
											if (!d.customs[e.guild.id]) d.customs[e.guild.id] = []
											d.customs[e.guild.id].find{it.name == d.args[1]}.list[number].command = call.aliases[0]
											d.customs[e.guild.id].find{it.name == d.args[1]}.list[number].args = d.args[4] ? d.args[4..-1].join(' ') : ''
											e.sendMessage("The custom command **${d.args[1]}** has been edited.").queue()
											d.json.save(d.customs, 'customs')
										} else {
											e.sendMessage("I couldn't find a command matching '${d.args[3]}.'").queue()
											404
										}
									}else{
										d.customs[e.guild.id].find{it.name == d.args[1]}.list-=d.customs[e.guild.id].find{it.name == d.args[1]}.list[number]
										d.json.save(d.customs, 'customs')
										e.sendMessage("That line of the custom command has been removed.").queue()
									}
								} else {
									e.sendMessage("That custom command doesn't even have that many lines.").queue()
									400
								}
							} else {
								e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom edit [name] [command] [arguments]`.", "Gebruik: `${d.prefix}custom edit [naam] [commando] [argumenten]`.", "Uso: `${d.prefix}custom edit [nome] [comando] [argumentos]`."].lang(e)).queue()
								400
							}
						} else {
							e.sendMessage("I couldn't find a custom command matching '${d.args[1]}.'").queue()
							404
						}
					} else {
						e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom edit [name] [command] [arguments]`.","Gebruik: `${d.prefix}custom edit [naam] [commando] [argumenten]`.","Uso: `${d.prefix}custom edit [nome] [comando] [argumentos]`."].lang(e)).queue()
						400
					}
				} else if (d.args[0] == 'add') {
					d.args[1] = d.args[1].toLowerCase()
					if (d.args[1]) {
						Map custom = d.customs[e.guild.id].find{it.name == d.args[1]}
						if (custom) {
							if (custom.list.size() > 9) {
								e.sendMessage("I can't remember all that! (Too many commands in one custom command.)").queue()
							} else {
								d.args[2]=d.args[2].toLowerCase()
								if(d.args[2]){
									Command call=d.bot.commands.find{d.args[2] in it.aliases}
									if(call){
										if(!d.customs[e.guild.id])d.customs[e.guild.id]=[]
										d.customs[e.guild.id].find{it.name == d.args[1]}.list += [
											command: call.aliases[0],
											args: d.args[3] ? d.args[3..-1].join(' ') : ''
										]
										e.sendMessage("The custom command **${d.args[1]}** has been added to. Now try `${d.prefix}${d.args[1]}` again.").queue()
										d.json.save(d.customs,'customs')
									}else{
										e.sendMessage("I couldn't find a command matching '${d.args[2]}.'").queue()
										404
									}
								}else{
									e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom add [name] [command] [arguments]`.","Gebruik: `${d.prefix}custom add [naam] [commando] [argumenten]`.","Uso: `${d.prefix}custom add [nome] [comando] [argumentos]`."].lang(e)).queue()
									400
								}
							}
						} else {
							e.sendMessage("I couldn't find a custom command matching '${d.args[1]}.'").queue()
						}
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
						e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom delete [name]`.","Gebruik: `${d.prefix}custom delete [naam]`.","Uso: `${d.prefix}custom delete [nome]`."].lang(e)).queue()
						400
					}
				}else if(d.args[0]=='info'){
					if(d.args[1]){
						d.args[1]=d.args[1].toLowerCase()
						Map custom=d.customs[e.guild.id].find{it.name==d.args[1]}
						if(custom){
							int number = 0
							List asses = custom.list.collect{ Map ass ->
								number += 1
								Command cmd = d.bot.commands.find{ass.command in it.aliases}
								"$number: `${cmd.aliases.join('/')} $ass.args`"
							}
							e.sendMessage("This custom command performs:\n${asses.join('\n')}\n\n${if(custom.time){'\nCreated: '+new Date(custom.time).format('HH:mm:ss dd/MMMM/YYYY')}else{''}}\nUses: $custom.uses").queue()
						}else{
							e.sendMessage("I couldn't find a custom command matching '${d.args[1]}.'").queue()
							404
						}
					}else{
						e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom info [name]`.","Gebruik: `${d.prefix}custom info [naam]`.","Uso: `${d.prefix}custom info [nome]`."].lang(e)).queue()
						400
					}
				}else if(d.args[0]=='list'){
					e.sendMessage("**__$e.guild.name's Custom Commands (${d.customs[e.guild.id]?.size()?:''})__:**\n${d.customs[e.guild.id]?d.customs[e.guild.id]*.name.join(', '):"No custom commands to see here."}").queue()
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}custom create/edit/delete/info/list ..`.","Gebruik: `${d.prefix}custom create/edit/delete/info/list ..`.","Uso: `${d.prefix}custom create/edit/delete/info/list ..`."].lang(e)).queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Requires any: `'Bot Commander' role`, `ADMINISTRATOR permission`.").queue()
				403
			}
		}else{
			e.sendMessage("There's really no point to having this work in Direct Messages.").queue()
			405
		}
	}
	String category = 'General'
	String help = """`custom create [name] [command] [arguments]` will make me create a custom command/alias.
`custom edit [name] [line] [command] [arguments]` will make me edit a custom command.
`custom delete [name]` will make me delete a custom command.
`custom info [name]` will make me tell you some information about the custom command.
`custom list` will make me list this server's custom commands.
The best thing since BooBot! Not really."""
}


class PwnedCommand extends Command {
	List aliases = ['pwned','haveibeenpwned']
	int limit = 50
	def run(Map d, Event e) {
		if (d.args) {
			if (!d.args.contains('@')) d.args += '@gmail.com'
			d.args = d.args.replaceAll(/\s/, '-')
			if (e.message.mentions) {
				def ass = d.db.find{e.message.mentions[-1].id in it.value.ids}?.value?.accounts?.mail
				if (ass) d.args = ass
			}
			e.sendTyping().queue()
			try {
				List hecks = new JsonSlurper().parseText(Unirest.get("https://haveibeenpwned.com/api/v2/breachedaccount/$d.args").asString().body)
				.collect{Map breach->"**$breach.Title** ($breach.Domain): ${breach.DataClasses.join(', ')}${if (breach.IsVerified) {' (verified)'} else if (breach.IsFabricated) {' (fabricated)'} else {''}}"}
				"You've been pwned $hecks.size time${if(hecks.size>1){'s'}else{''}}:\n${hecks.join('\n')}".split(1999).each{
					e.sendMessage(it).queue()
					Thread.sleep(150)
				}
			} catch(ex) {
				e.sendMessage("All safe. Looks like `$d.args`'s information hasn't been stolen.").queue()
				404
			}
		} else {
			e.sendMessage(d.errorMessage() + ["Usage: `${d.prefix}pwned [email]`.", "Gebruik: `${d.prefix}pwned [email]`.", "Uso: `${d.prefix}pwned [email]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`pwned [email]` will make me list data breaches in which your information was stolen.
It wasn't me this time."""
}


class MathCommand extends Command {
	List aliases = ['math','maths']
	def run(Map d, Event e) {
		if(d.args){
			e.sendTyping().queue()
			String sum = d.args.replace('\u03c0', 'PI').replace('\\u', '').replace('\\x', '').replace('java', '').replace('lang', '').replace('System', '')
			.replaceAll(/([A-Za-z]+)/) {full, word -> "Math.$word"}.replaceAll(['"', "'"], '').replaceEach(['{', '}'],['[', ']']).replaceAll(["'", '\$','_'], '')
			.replaceAll(/[\u00c0-\u00f6\u00f8-\ufffe]+/, '').replaceAll(/(\/)+.+(\/)\.*\[\s+\w+.+(,|\.|\])+/, '')
			int par = sum.count('(') - sum.count(')')
			if (par > 0) sum += ')' * par
			else if (par < 0) sum= '(' * -par + sum
			String ass
			try {
				ass = new GroovyShell().evaluate(sum).toString()
			} catch(ex) {
				ass = "$ex".replaceAll(["${ex.class.name}:", 'startup failed:', 'Script1.groovy:'], '').replaceAll(/\d error(?:s?)/, '').trim()
			}
			if(ass.length() < 1000) {
				e.sendMessage("`$ass`").queue()
			} else {
				e.sendMessage(['`Error`', '`Fout`', '`Erro`', '`Blad`'].lang(e)).queue()
			}
		} else {
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}math [sum]`.","Gebruik: `${d.prefix}math [som]`.","Uso: `${d.prefix}math [equacao]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`math [sum]` will make me evaluate the math sum in Groovy.
So yes, it's a homework machine."""
}


class MapCommand extends Command {
	List aliases = ['map','world']
	int limit = 600
	def run(Map d, Event e) {
		if (e.guild) {
			Message message = e.sendMessage("Checking my atlas... If I don't respond soon, I ran out of memory.").complete()
			e.sendTyping().queue()
			int size = 18
			int spread = 12
			BigDecimal multiplier = 1.0
			d.args = d.args.tokenize()
			if (d.args[0] ==~ /\d+/) {
				size = d.args[0].toInteger()
				if (size<1) size = 1
			}
			if (d.args[1] ==~ /\d+/) {
				spread = d.args[1].toInteger()
				if (spread < 1) spread = 1
			}
			if (d.args[2] =~ /\d+/) {
				multiplier = d.args[2].toBigDecimal()
				if (multiplier > 5) multiplier = 5
				else if (multiplier < 0.05) multiplier = 0.05
			}
			int width = (2023 * multiplier).toInteger()
			int height = (954 * multiplier).toInteger()
			Range range = -spread..spread
			List users = e.guild.users.findAll{!it.bot}.findAll{it.rawIdentity}.sort{-it.createTimeMillis}
			File ass = new File('temp/map.png')
			OutputStream os = ass.newOutputStream()
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			Graphics2D graphics=image.createGraphics()
			Image map = ImageIO.read(new File('images/map.png')).getScaledInstance(width, height, Image.SCALE_SMOOTH)
			graphics.drawImage(map, 0, 0, null)
			users.each{ User user ->
				String area = d.db.find{user.id in it.value.ids}?.value?.area
				if (area) {
				String key = d.misc.geos*.key.sort{-it.length()}.find{area.endsWith(it)}
				if (key) {
					List coords = d.misc.geos[key].tokenize()*.toInteger()
					File avatar = new File("temp/avatars/${user.avatarId ?: user.defaultAvatarId}.png")
					if (!avatar.exists()) avatar = d.web.download("${user.avatar.replace('.jpg', '.png')}?size=64", "temp/avatars/${user.avatarId}.png")
					Image scaled = ImageIO.read(avatar).getScaledInstance(size, size, Image.SCALE_SMOOTH)
					graphics.drawImage(scaled, (coords[0] * multiplier).toInteger() + range.random(), (coords[1] * multiplier).toInteger() + range.random(), null)
				}
				}
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream()
			ImageIO.write(image, 'png', baos)
			graphics.dispose()
			baos.writeTo(os)
			os.close()
			e.sendFile(ass).complete()
			message.delete().queue()
		}else{
			e.sendMessage("There's really no point to having this work in Direct Messages.").queue()
			405
		}
	}
	String category = 'Database'
	String help = """`map [avatar size] [avatar spread] [relative map size]` will make me generate a world map with the users of the server placed on it using my database.
So many hours of writing down co-ordinates..."""
}


class SourceCommand extends Command {
	List aliases = ['source','src']
	int limit = 70
	def run(Map d, Event e) {
		d.args=d.args.replaceAll(/^</,'').replaceAll(/>$/,'').trim()
		if(d.args.contains('.')||e.message.attachment||e.message.mentions||e.message.emotes){
			e.sendTyping().queue()
			if(e.message.attachment)d.args=e.message.attachment.url
			else if(e.message.mentions)d.args=(e.message.mentions[-1].avatar?:e.message.mentions[-1].defaultAvatar)+'?size=512'
			else if(e.message.emotes)d.args=e.message.emotes[-1].imageUrl
			String link="https://encrypted.google.com/searchbyimage?image_url=${URLEncoder.encode(d.args,'UTF-8')}"
			try{
				Document doc=d.web.get(link)
				String match=doc.getElementsByClass('_gUb')[0].text()
				String source=doc.getElementsByClass('srg')?.getAt(0)?.getElementsByClass('r')?.getAt(0)?.getElementsByTag('a')?.getAt(0)?.attr('href')
				String starter=["Oh, I think that's",'That looks like',"It's probably"].random()
				e.sendMessage("$starter **$match**.\n\nSearch: <https://encrypted.google.com/search?q=${URLEncoder.encode(match)}&tbm=isch>${if(source){"\nSource: <$source>"}else{''}}").queue()
			}catch(ex){
				if(ex.message=='HTTP error fetching URL'){
					e.sendMessage(['You are being rate limited.','Je bent gebruik beperkt.','Voce esta sendo limitado a taxas.','Zostaniesz szybkosc ograniczona.'].lang(e)).queue()
					429
				}else{
					e.sendMessage("I really can't describe the image.\n$link").queue()
					404
				}
				ex.printStackTrace()
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}source [image url/image/@mention/emote]`.","Gebruik: `${d.prefix}source [afbeelding url/beeld/@gebruiker/emote]`.","Uso: `${d.prefix}source [imagem url/imagem/@utilizador/emote]`."].lang(e)).queue()
			400
		}
	}
	String category = 'Online'
	String help = """`source [image url/image]` will make me reverse Google search for the image.
`source [@mention/emote]` will make me reverse Google search for the user's avatar or the emote.
Where'd you get that?"""
}


class ChooseCommand extends Command {
	List aliases = ['choose','choice']
	def run(Map d, Event e) {
		d.args=d.args.tokenize()
		if(d.args.size()>1){
			d.args.size().times{
				if(d.args[it].length()>500)d.args[it]=d.args[it].substring(0,500)
			}
			List picks=d.args.randomize()[0..1]
			String first=["**${picks[0].capitalize()}** is easily the best.","**${picks[0].capitalize()}** is pretty good in my opinion.","Obviously, the only way to go is **${picks[0]}**.","**${picks[0].capitalize()}** is objectively the better option.","Meticulous calculations show **${picks[0]}** to prevail.","**${picks[0].capitalize()}** is a hidden gem."].random()
			String second=["**${picks[1].capitalize()}** is a close second though.","And **${picks[1]}** is easily the worst.","If you like **${picks[1]}**, your opinion is simply wrong.","At least **${picks[1]}** tried to compete.","You couldn't pay me to like **${picks[1]}**.","I've never seen something worse than **${picks[1]}** in my life."].random()
			e.sendMessage("$first\n$second").queue()
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}choose [choices]`.","Gebruik: `${d.prefix}choose [keuzes]`.","Uso: `${d.prefix}choose [escolhas]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`choose [choices]` will make me decide on a set of results.
Why did this addition take so long?"""
}


class EmojiCommand extends Command {
	List aliases = ['emojitest','emotetest']
	def run(Map d, Event e) {
		if(d.args){
			List ass=d.args.replaceAll(['.',',','!','?',"'",':',';','(',')','"','-'],'').tokenize()
			ass.size().times{int i->
				Emote em=e.jda.emotes.find{it?.name?.toLowerCase()==ass[i-1].toLowerCase()}
				if(em)ass[i-1]=em.asMention
			}
			ass.join(' ').strip().split(1999).each{
				e.channel.sendMessage(it).queue()
				Thread.sleep(150)
			}
		}else{
			e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}emojitest [names]`.","Gebruik: `${d.prefix}emojitest [namen]`.","Uso: `${d.prefix}emojitest [nomes]`."].lang(e)).queue()
			400
		}
	}
	String category = 'General'
	String help = """`emojitest [names]` will make me post emoji that I can use matching the names.
<:grover:234242699211964417>"""
}


class PingmodCommand extends Command {
	List aliases = ['pingmod','pingmods']
	int limit = 1800
	def run(Map d, Event e) {
		if(e.guild){
			List ass=['online','idle','offline','do_not_disturb']
			User mod=e.guild.members.findAll{!it.user.bot}.findAll{it.roles.any{Role r->['BAN_MEMBERS','KICK_MEMBERS'].any{it in r.permissions*.toString()}}||it.owner}.sort{ass.indexOf(it.status)}[0].user
			String message="<@$mod.id> **"
			message+=['ALERT!','We want you!','We got a problem here.','Your people need you!','Wakey wakey!'].random()
			message+="**   ($e.author.identity)"
			if(d.args)message+=":\n${d.args.capitalize()}"
			e.sendMessage(message).queue()
		}else{
			e.sendMessage("There's really no point to having this work in Direct Messages.").queue()
			405
		}
	}
	String category = 'Moderation'
	String help = """`pingmod [reason]` will make me ping the most available person with certain permissions.
Quick button for when the feathers fly."""
}


class CategoryinfoCommand extends Command {
	List aliases = ['categoryinfo','category']
	def run(Map d, Event e) {
		def category=e.channel?.category
		if(d.args&&e.guild)category=e.guild.findCategory(d.args)
		if(category){
			if(e.guild){
				List users=category.channels*.users.flatten().groupBy{it.id}.values()*.first()
				e.sendMessage("""**${category.name.capitalize()}** in $e.guild.name: ```css
ID: $category.id
Created: ${new Date(category.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}
Users: ${if(users.size()>4){users[0..4]*.identity.join(', ')+'..'}else{users*.identity.join(', ')}} (${users.size()})
Channels: ${if(category.textChannels.size()>1){category.textChannels[0..1]*.name.join(', ')+'..'}else{category.textChannels*.name.join(', ')}}${if(category.voiceChannels){", ${if(category.voiceChannels.size()>1){category.voiceChannels[0..1]*.name.join(', ')+'..'}else{category.voiceChannels*.name.join(', ')}}"}else{''}} (${category.textChannels.size()}, ${category.voiceChannels.size()})```""").queue()
			}else{
				e.sendMessage("""**Direct Messages** in Direct Messages: ```css
ID: $e.jda.selfUser.id
Created: ${new Date(e.jda.selfUser.createTimeMillis).format('HH:mm:ss, d MMMM yyyy').formatBirthday()}```""").queue()
			}
		}else{
			e.sendMessage(["I couldn't find a category matching '$d.args.'","Ik kon niet vind een categorie vind '$d.args' leuk.","N\u00e3o consegui encontrar um categoria que corresponda '$d.args.'","Nie mog\u0119 znale\u017a\u0107 kategoria pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}
	}
	String category = 'General'
	String help = """`categoryinfo [category]` will make me tell you some useful information about the category.
Breaking news! New Discord feature breaks thousands of bots!"""
}


class PingCommand extends Command {
	List aliases = ['ping','test']
	def run(Map d, Event e) {
		String random=["-as, pongas! That's an old meme.","! Status report?"," pong, ping, pong, ping... Aww, I missed the ball."].random()
		long startTime=System.currentTimeMillis()
		e.sendMessage("**Ping$random**").queue{
			long stopTime=System.currentTimeMillis()
			it.edit(["**Ping$random**\nIt took about `${stopTime-startTime}ms` to send the message.","**Ping$random**\nHet duurde ongeveer `${stopTime-startTime}ms` om het bericht te sturen."].lang(e)).queue()
		}
	}
	String category = 'General'
	String help = """`ping` will test GRover's connection to the Discords.
And it says some funny stuff I guess."""
}


class KawaiiCommand extends Command {
	List aliases = ['kawaii','kawaiis']
	int limit = 70
	def run(Map d, Event e) {
		if(d.args){
			List list=new File('C:/Users/michael/Pictures/kawaiis').list().toList()
			d.args=d.args.replaceAll([' ','_'],'-').replaceAll(['!','?'],'')
			if(d.args in list){
				boolean done
				e.sendTyping().queue()
				while(!done){
					File ass=new File("C:/Users/michael/Pictures/kawaiis/$d.args").listFiles().toList().random()
					e.sendFile(ass).complete()
					done=true
				}
			}else{
				list=list.collect{"`$it` ("+new File("C:/Users/michael/Pictures/kawaiis/$it").list().size()+')'}
				e.sendMessage("I have images from all of the following: ${list.join(', ')}").queue()
			}
		}else{
			boolean done
			e.sendTyping().queue()
			while(!done){
//				File ass=new File('C:/Users/michael/Pictures/kawaiis').listFiles().toList().random().listFiles().toList().random()
				File ass=new File('C:/Users/michael/Pictures/kawaiis').listFiles()*.listFiles()*.toList().flatten().random()
				e.sendFile(ass).complete()
				done=true
			}
		}
	}
	String category = 'General'
	String help = """`kawaii` will make me post an image of a cute anime girl (most of the time).
`kawaii [series]` will make me post from that specific series.
`kawaii list` will make me tell you what series' I got.
Straight from Axew's anime folders, so it's slightly bias."""
}


class AltdetectCommand extends Command {
	List aliases = ['altdetect','altdetector']
	def run(Map d, Event e) {
		if(e.guild){
			if(e.author.isStaff(e.guild)){
				List list=e.guild.users.findAll{!it.bot}.findAll{User ass->
					d.db.find{ass.id in it.value.ids}
				}.groupBy{User ass->
					d.db.find{ass.id in it.value.ids}.value
				}*.value.findAll{it.size()>1}
				if(list.size()){
					def ass=list.collect{List ass=it.sort{-it.createTimeMillis};"${ass[0..-2].collect{"$it.name#$it.discriminator"}.join(', ')} ${ass.size()>2?'are alts':'is an alt'} of ${"${ass[-1].name}#${ass[-1].discriminator}"}"}.join('\n')
					try{
						e.author.openPrivateChannel().complete()
						ass.split(1999).each{
							e.author.privateChannel.sendMessage(it).complete()
						}
						e.sendMessage(["I have sent you the list of alternate accounts. <@$e.author.id>","Ik heb stuurde je de lijst van alternatieve accounts. <@$e.author.id>"].lang(e)).queue{
							Thread.sleep(5000)
							it.delete().queue()
						}
					}catch(ex){
						ass.split(1999).each{
							e.sendMessage(it).queue()
						}
					}
				}else{
					e.sendMessage(["I couldn't detect any alternate accounts in this server.",'Ik kon het niet vind een alternatieve accounts in het guild.','N\u00e3o consegui detectar nenhuma conta alternativa neste servidor.','Nie mog\u0142em wykry\u0107 \u017cadnych alternatywnych kont na tym serwerze.'].lang(e)).queue()
				}
			}else{
				e.sendMessage(d.permissionMessage()+"Requires any: `'Trainer' role`, `MANAGE MESSAGES permission`.").queue()
				403
			}
		}else{
			e.sendMessage(["We're the only ones here, dude.",'Wij bent de alleen mensen hier...'].lang(e)).queue()
		}
	}
	String category = 'Database'
	String help = """`altdetect` will make me list alternate accounts in the server.
Bots don't count."""
}


class SetServerCommand extends Command {
	List aliases = ['setserver','modes']
	def run(Map d, Event e) {
		if(e.guild){
			if(e.author.id in[e.guild.owner.user.id,d.bot.owner]){
				d.args=d.args.toLowerCase()
				if(d.args=='nsfw'){
					if(d.modes.nsfw[e.guild.id]==false){
						d.modes.nsfw[e.guild.id]=true
						e.sendMessage("Pornographic content using `${d.prefix}nsfw` has been enabled in this server (set NSFW channels and administrators only).").queue()
					}else{
						d.modes.nsfw[e.guild.id]=false
						e.sendMessage("Pornographic content using `${d.prefix}nsfw` has been disabled in this server.").queue()
					}
					d.json.save(d.modes,'modes')
				}else if(d.args=='database'){
					if(d.modes.database[e.guild.id]==false){
						d.modes.database[e.guild.id]=true
						e.sendMessage('Personal information using database commands has been enabled in this server.').queue()
					}else{
						d.modes.database[e.guild.id]=false
						e.sendMessage('Personal information using database commands has been disabled in this server.').queue()
					}
					d.json.save(d.modes,'modes')
				}else if(d.args in['colour','color']){
					if(d.modes.colour[e.guild.id]==false){
						d.modes.colour[e.guild.id]=true
						e.sendMessage("Custom colours using `${d.prefix}colour` have been enabled in this server.").queue()
					}else{
						d.modes.colour[e.guild.id]=false
						e.sendMessage("Custom colours using `${d.prefix}colour` have been disabled in this server.").queue()
					}
					d.json.save(d.modes,'modes')
				}else if(d.args in['hidden','shown']){
					if(d.modes.hidden[e.guild.id]==true){
						d.modes.hidden[e.guild.id]=false
						e.sendMessage('This server can appear on my public lists again.').queue()
					}else{
						d.modes.hidden[e.guild.id]=true
						e.sendMessage('This server can no longer appear appear on my public lists.').queue()
					}
					d.json.save(d.modes,'modes')
				}else{
					e.sendMessage(d.errorMessage()+["Usage: `${d.prefix}setserver nsfw/database/colour/hidden`.","Gebruik: `${d.prefix}setserver nsfw/database/colour/hidden`.","Uso: `${d.prefix}setserver nsfw/database/colour/hidden`."].lang(e)).queue()
					400
				}
			}else{
				e.sendMessage(d.permissionMessage()+'Only the owner of the server can use this command.').queue()
				403
			}
		}else{
			e.sendMessage("Shouldn't that be up to your judgement?").queue()
		}
	}
	String category = 'Moderation'
	String help = """`setserver nsfw` will toggle off porn commands regardless of permissions and channels.
`setserver database` will toggle off commands that show the personal information of specied users in the server.
`setserver colour` will toggle off custom colours for everyone.
`setserver hidden` will hide the server on public lists.
These can only be changed by the server owner... And Axew."""
}

/*
class SlotsCommand extends Command {
	List aliases = ['slots','gamble']
	int limit = 30
	def run(Map d, Event e) {
		if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
		d.args=d.args.tokenize()
		def moneys=(d.args[0]==~/\d+/)?d.args[0].toLong():1
		List input=((e.guild?e.guild.emotes.toList().findAll{!it.animated}.randomize()*.mention:[])+[':tangerine:',':banana:',':apple:',':pineapple:',':peach:',':tomato:',':lemon:',':eggplant:'])[0..(3..7).random()]
		if(moneys>d.rpg[e.author.id].money){
			e.sendMessage("You don't even have that much money, do you? (Answer: No.)").queue()
		}else{
			d.rpg[e.author.id].money-=moneys
			List output=[]
			9.times{
				output+=input.random()
			}
			boolean match=((output[0]==output[1])&&(output[1]==output[2])||(output[3]==output[4])&&(output[4]==output[5])||(output[6]==output[7])&&(output[7]==output[8])||(output[0]==output[4])&&(output[4]==output[8])||(output[6]==output[4])&&(output[4]==output[2]))
			String machine="`>` ${output[0..2].join()} `<`\n`>` ${output[3..5].join()} `<`\n`>` ${output[6..8].join()} `<`\n\n"
			if(match){
				int out=moneys*[0.5,1.5,2,2.5].random()
				if(moneys<1){
					e.sendMessage("$machine**You won!**\nBut you didn't get anything because you didn't put any money in, you cheeky fucker.").queue()
				}else if([0,0,0,0,0,1].random()){
					e.sendMessage("$machine**You won!**\nBut you drop the money on the floor, and before you can pick it up, a random dog eats it.").queue()
				}else{
					d.rpg[e.author.id].money+=out
					e.sendMessage("$machine**You won!**\nYou got <:coin:339089342356258826> `$out`. You now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
				}
			}else{
				String comment=["Have you still not noticed you're only losing money?","Get out of here, I bet you're too young to gamble and probably too young to use Discord.","Don't worry, your money isn't going to any noble cause.",'Feel like trying again, loser?'].random()
				if([0,0,0,0,0,0,0,0,1].random())e.sendMessage("$machine**You won!** Only joking, you lost! $comment\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
				else e.sendMessage("$machine**You lost!** $comment\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
			}
			d.json.save(d.rpg,'rpg')
		}
	}
	String category = 'RPG'
	String help = """`slots [amount]` will bet that amount on a (custom) emoji slot machine. You'll get a random return, if any.
The obligatory feature for any monetary system."""
}


class ItemsCommand extends Command {
	List aliases = ['items','item']
	def run(Map d, Event e) {
		if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
		if(d.args){
			d.args=d.args.toLowerCase().tokenize()
			def item=d.rpg.items*.key.find{d.args[0].contains(it)}
			if(item){
				if(e.message.mentions){
					item=d.rpg[e.author.id].items.find{d.args[0].contains(it[0])}
					if(item[0]){
						d.rpg[e.author.id].items-=[item]
						if(e.message.mentions[-1].bot){
							if(item[0]=='bomb'){
								e.sendMessage("You throw the bomb at the bot, which is stupid because the bot cannot play the game. And the bomb is wasted. :clap::skin-tone-1:").queue()
							}else{
								e.sendMessage("As you try to dump the item on a poor robot, it explodes with an 'OOF' sound. It's gone now.").queue()
							}
						}else if(e.message.mentions[-1].id==e.author.id){
							e.sendMessage("Jesus fucking Christ, you're lonely. Let me take it.").queue()
						}else{
							if(!d.rpg[e.message.mentions[-1].id])d.rpg[e.message.mentions[-1].id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
							String message="The ${d.rpg.items[item[0]]} has been sent to ${e.message.mentions[-1].identity}."
							if(item[0]=='bomb'){
								d.rpg[e.message.mentions[-1].id].health=100
								int bill=d.rpg[e.message.mentions[-1].id].money/3
								d.rpg[e.message.mentions[-1].id].money-=bill
								if(e.message.mentions[-1].id==e.author.id)message+=" Good job killing yourself, dude.\n\nHurrying to the nearest hospital, you pay a <:coin:339089342356258826> `$bill` bill to regain full health."
								else message+=" Boom!\n\nHurrying to the nearest hospital, they had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
							}else{
								d.rpg[e.message.mentions[-1].id].items+=[item]
							}
							e.sendMessage("$message <@${e.message.mentions[-1].id}>").queue()
						}
						d.json.save(d.rpg,'rpg')
					}else{
						e.sendMessage("Unfortunately you can't just fabricate that out of nowhere.").queue()
					}
				}else{
					String desc="${d.rpg.items[item]}\n"
					if(item=='cart')desc+="A Nintendo Switch game card containing The Legend of Zelda: Breath of the Wild. Too bad there's nothing you can play it on."
					else if(item=='pillow')desc+="A human-sized pillow of Satan as an anime girl. A certain user would love to have this."
					else if(item=='eggplant')desc+="It's just a plant bro."
					else if(item=='sink')desc+="The kitchen sink. Not a metaphor, but a literal kitchen sink."
					else if(item=='sword')desc+="Sweet, I can dual wield now."
					else if(item=='key')desc+="A key that unlocks both treasure chests and doors. How convenient."
					else if(item=='shit')desc+="An actual piece of fecal matter. Everything else in your backpack is probably covered with it."
					else if(item=='picture')desc+="It's a cursed image. **Cursed!**"
					else if(item=='pear')desc+="A better fruit than apples."
					else if(item=='bomb')desc+="A strange bomb that could blow up any time, but doesn't destroy other items."
					else if(item=='eyes')desc+="Sometimes, when I am stalking someone, I send these to their phone, and they get real spooked."
					else if(item=='grover')desc+="This dog robot was once relevant as a staple of a certain community. Now he whores out commands that'll be popular with the kids to stay relevant."
					else if(item=='bikini')desc+="An essential item of clothing for all fanservice episodes. Prone to falling off spontaneously."
					else if(item=='cook')desc+="An electric pan cooking an egg. The energy going through it seems to be perpetual, and the egg is never quite done."
					else if(item=='whale')desc+="Whale whale, what do- oh, Egbert already used this joke?"
					else if(item=='burger')desc+="A burger. There are zucchinis in it, but they are sliced so thin you cannot see them. Devilish."
					else if(item=='camera')desc+="It's a digital camera. You have no idea how to use it, but it feels sinister somehow."
					else desc+="This exists, but there's no data for it. Huh."
					e.sendMessage(desc).queue()
				}
			}else{
				e.sendMessage('There are no items called that. Did you make a typo?').queue()
			}
		}else{
			List items=(d.rpg[e.author.id].items+([null,null]*30))[0..29].collect{
				if(it)d.rpg.items[it[0]]
				else':black_medium_small_square:'
			}
			e.sendMessage("""**${e.author.identity.capitalize()}'s Inventory and Statistics**:
<:e:433672473985417226> `${d.rpg[e.author.id].experience}` | <:c:339089342356258826> `${d.rpg[e.author.id].money}` | :broken_heart: `${d.rpg[e.author.id].health}`
Weapon: ${d.rpg[e.author.id].weapon?d.rpg.items[d.rpg[e.author.id].weapon]:'Your tiny fist'} | Armour: ${d.rpg[e.author.id].armour?d.rpg.items[d.rpg[e.author.id].armour]:'Your regular clothes'}

${items[0..14].join('')}
${items[15..29].join('')}""").queue()
		}
	}
	String category = 'RPG'
	String help = """`items` will make me tell you your RPG items, money and other details.
`items [item]` will make me tell you about an item, even if you don't have it.
`items [item] [@mention]` will make me give someone else an item.
Backpack, backpack. Backpack, backpack."""
}


class AdventureCommand extends Command {
	List aliases = ['adventure','explore','run']
	int limit = 30
	def run(Map d, Event e) {
		if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
		if(d.args.toLowerCase()=='key'){
			if('key'in d.rpg[e.author.id].items*.getAt(0)){
				if(d.rpg[e.author.id].next==1){
					d.rpg[e.author.id].items-=d.rpg[e.author.id].items.find{it[0]=='key'}
					if((d.rpg[e.author.id].items.size()>29)||[0,0,0,0,1].random()){
						e.sendMessage("As you're about to open the chest, it grows wings and begins to lift off the ground. You try to catch it, but it bites you, with the same mouth that then says 'Your PokeGEAR is full. Please delete some phone numbers.' before hitting 88 miles per hour and zooming off to another timeline.").queue()
					}else{
						int random=(0..2).random()
						if(random==1){
							d.rpg[e.author.id].items+=[['camera',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Wow! A digital ${d.rpg.items['camera']}! Maybe you could `${d.prefix}wear` it around your neck for the time being, as either a fashion statement or a practicality.").queue()
						}else if(random==2){
							d.rpg[e.author.id].items+=[['shit',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Lol you got a piece of fucking ${d.rpg.items['shit']} bahahaha.").queue()
						}else if(random){
							d.rpg[e.author.id].items+=[['cart',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Oh shit a ${d.rpg.items['cart']}! This is just like something out of my Zeldas!").queue()
						}else{
							e.sendMessage("The chest is empty.").queue()
						}
					}
				}else if(d.rpg[e.author.id].next==2){
					d.rpg[e.author.id].items+=[['key',(0..Short.MAX_VALUE).random()]]
					e.sendMessage('PLACEHOLDER FOR BOSS (YOUR KEY WILL BE RETURNED)').queue()
					// open boss door
				}else{
					e.sendMessage("There's literally nothing to use a key on.").queue()
				}
			}else{
				e.sendMessage("You don't even have a key you stupid bitch.").queue()
			}
		}else{
			if(d.rpg.battles.find{e.author.id in it.users}){
				if(d.rpg[e.author.id].next==3){
					Map battle=d.rpg.battles.find{e.author.id in it.users}
					int money=(1..9).random()*battle.enemies.size()
					d.rpg[e.author.id].money-=money
					if(d.rpg[e.author.id].money<1)d.rpg[e.author.id].money=0
					d.rpg[e.author.id].next=0
					if(battle.users.size()>1){
						e.sendMessage('PLACEHOLDER FOR RUNNING FROM A GROUP BATTLE').queue()
						// delet yourself
					}else{
						d.rpg.battles-=battle
						String message=["Got away safely.","You managed to escape this time.","Run away, you pussy."].random()
						e.sendMessage("$message You lost <:coin:339089342356258826> `${money}`.\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
					}
				}else{
					d.rpg[e.author.id].next=3
					e.sendMessage(":runner::skin-tone-1: Do you want to run from the battle?\nType `${d.prefix}adventure` again to confirm. (You'll lose money.)").queue()
				}
			}else{
				int random=[0,0,0,0,0,0,0,0,1,1,1,1,1,2,2,3,4,4,4,4,4,5,5].random()
				d.rpg[e.author.id].next=0
				if(random==1){
					Map another=d.rpg.battles.find{it.users.each{String ass->e.jda.guilds*.members.flatten().find{it.user.id==ass}.status=='online'}}
					if([0,1].random()&&another){
						e.sendMessage('PLACEHOLDER FOR JOINING SOMEONE ELSES BATTLE').queue()
						// join battle
					}else{
						Map battle=[users:[e.author.id],enemies:[d.rpg.enemies.random().clone()],turns:0,channels:[[e.channel.id,(0..Short.MAX_VALUE).random()]]]
						battle.enemies[0].skills=battle.enemies[0].skills.clone()
						battle.enemies[0].discrim=(0..Short.MAX_VALUE).random()
						d.rpg.battles+=battle
						String analogy=['appeared magically','says hi','jumped out of the tall grass','materialized in front of you','has beef with you'].random()
						e.sendMessage("Enemy ${battle.enemies[0].emoji} $analogy!\n\nUse `${d.prefix}move` to see a list of skills and `${d.prefix}move [skill]` to use one.").queue()
					}
				}else if(random==2){
					Map battle=[users:[e.author.id],enemies:[],who:0,turns:0,channels:[[e.channel.id,(0..Short.MAX_VALUE).random()]]]
					int number=[3,3,3,3,3,4,4,4,4,4,4,5,5,5,5,10].random()
					number.times{
						Map ass=d.rpg.enemies.random().clone()
						ass.skills=ass.skills.clone()
						ass.discrim=(0..Short.MAX_VALUE).random()
						ass.attack-=number
						ass.defense-=number
						battle.enemies+=ass
					}
					d.rpg.battles+=battle
					String expletive=['Wowzer!','Wholy shit!','What the fuck?','Wew lad.','Oh heck.'].random()
					String analogy=['came to fuck you up','appeared out of fucking nowhere','want you dead. All of them'].random()
					e.sendMessage("$expletive Enemies ${battle.enemies*.emoji.join(', ').replaceLast(', ',' and ')} $analogy!\n\nUse `${d.prefix}move` to see a list of skills and `${d.prefix}move [skill]` to use one on a random target.").queue()
					// horde battle
				}else if(random==3){
					if(d.rpg[e.author.id].health>90){
						String message=['You accidentally walk off a cliff and... You survive the fall.',"You step on a mine and it explodes, but you survive and now you're a war veteran.","You walk off the flat Earth and end up falling into hell, but even Satan can't stand you so he sends you back."].random()
						d.rpg[e.author.id].health-=90
					}else{
						String message=['You accidentally walk off a cliff and die.','You step on a mine and it explodes, killing you.','You walk off the flat Earth and end up falling into hell. That means death.'].random()
						List item=[]
						if(d.rpg[e.author.id].items){
							item=d.rpg[e.author.id].items.random()
							d.rpg[e.author.id].items-=[item]
						}
						d.rpg[e.author.id].health=100
						int bill=d.rpg[e.author.id].money/3
						d.rpg[e.author.id].money-=bill
						if(item)message+="\n\nHurrying to the nearest hospital, you dropped ${d.rpg.items[item[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
						else message+="\n\nYou hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health."
						e.sendMessage(message).queue()
					}
				}else if(random==4){
					random=(0..85).random()
					List item=[null,(0..Short.MAX_VALUE).random()]
					if(random in(0..7)){
						item[0]='cart'
						if([0,1].random())e.sendMessage("Lucky! There was a ${d.rpg.items['cart']} lying on the ground. Wonder if it still works.").queue()
						else e.sendMessage("Walking around the local GAME store, you find a ${d.rpg.items['cart']} on the floor. You pocket it and escape.").queue()
					}else if(random in 8..14){
						item[0]='pillow'
						if([0,1].random())e.sendMessage("You come across a huge ${d.rpg.items['pillow']} that'd be impossible to miss.").queue()
						else e.sendMessage("You find a bed. It has a ${d.rpg.items['pillow']} on it, which you take for comfort.").queue()
					}else if(random in 15..28){
						item[0]='eggplant'
						if([0,1].random())e.sendMessage("You find a ${d.rpg.items['eggplant']} in a chicken's nest.").queue()
						else e.sendMessage("You pluck ${d.rpg.items['eggplant']} off a tree branch.").queue()
					}else if(random in 29..32){
						item[0]='sink'
						if([0,1].random())e.sendMessage("While out walking, a ${d.rpg.items['sink']} almost falls on your head. It's unharmed, so you put it in your backpack.").queue()
						else e.sendMessage("You break into someone's kitchen and steal their ${d.rpg.items['sink']}.").queue()
					}else if(random in 33..36){
						item[0]='sword'
						if([0,1].random())e.sendMessage("You find a ${d.rpg.items['sword']} on the ground along with a corpse. Someone must've died here.").queue()
						else e.sendMessage("A nice traveller gives you their old ${d.rpg.items['sword']}. You say thank you.").queue()
					}else if(random in 37..41){
						item[0]='key'
						if([0,1].random())e.sendMessage("You find someone's ${d.rpg.items['key']} in the grass.").queue()
						else e.sendMessage("You steal a passerby's ${d.rpg.items['key']} out of their pocket.").queue()
					}else if(random in 42..49){
						item[0]='shit'
						if([0,1].random())e.sendMessage("Ew, you stepped in some ${d.rpg.items['shit']}. You pick it up.").queue()
						else e.sendMessage("You pass by a dung beetle rolling a ball of ${d.rpg.items['shit']}. You steal it from him just for satisfaction.").queue()
					}else if(random in 50..53){
						item[0]='picture'
						if([0,1].random())e.sendMessage("While passing by the local haunted house, you find ${d.rpg.items['picture']}.").queue()
						else e.sendMessage("Huh? This ${d.rpg.items['picture']} wasn't in my bag before.").queue()
					}else if(random in 54..65){
						item[0]='pear'
						if([0,1].random())e.sendMessage("In a sudden bout of hunger, you pick a ${d.rpg.items['pear']} off a tree and forget to eat it.").queue()
						else e.sendMessage("While out walking, a ${d.rpg.items['pear']} falls on your head. You take it.").queue()
					}else if(random in 66..69){
						item[0]='bomb'
						if([0,1].random())e.sendMessage("Browsing what looks like a battlefield, you find a ${d.rpg.items['bomb']}").queue()
						else e.sendMessage("Huh?! Why am I carrying ${d.rpg.items['bomb']}?!").queue()
					}else if(random in 70..74){
						item[0]='eyes'
						if([0,1].random())e.sendMessage("You found some ${d.rpg.items['eyes']}, and they aren't your own.").queue()
						else e.sendMessage("Someone attacks you, but you poke their ${d.rpg.items['eyes']} out and put them in your bag.").queue()
					}else if(random in 75){
						if(d.rpg[e.author.id].virginity){
							e.sendMessage("You go to the same place where you found the ${d.rpg.items['grover']} before, but there's nothing there.").queue()
						}else{
							e.sendMessage("You find a rusted piece of scrap metal while walking the plains of nothingness. Upon further inspection, it looks like an old military tool.\nYou pick ${d.rpg.items['grover']} up and put it in your bag.\nDid I just hear barking?").queue()
						}
					}else if(random in 75..77){
						item[0]='bikini'
						if([0,1].random())e.sendMessage("You go to the beach and steal a woman's ${d.rpg.items['bikini']} while they are still wearing it.").queue()
						else e.sendMessage("You walk into the clothes store and find a ${d.rpg.items['bikini']} in the changing room. You take it.").queue()
					}else if(random in 78..79){
						item[0]='cook'
						if([0,1].random())e.sendMessage("You're just playing Mario Odyssey, when suddenly a Pan Bro throws a pan out of the screen! You pocket it and put the Switch into your pocket.").queue()
						e.sendMessage("You walk into Gordon Ramsay's kitchen and steal one of the frying pans. Someone's gonna get a verbal beating.").queue()
					}else if(random==80){
						item[0]='whale'
						e.sendMessage("While swimming from one country to another, you find a whale. You pocket it.").queue()
					}else if(random in 81..83){
						item[0]='burger'
						if([0,1].random())e.sendMessage("After searching the world for ingredients, you've finally made it: The ${d.rpg.items['burger']}.").queue()
						else e.sendMessage("You take a short break on the bottom of the ocean and grab a Krabby Patty (${d.rpg.items['burger']}).").queue()
					}else if(random in 84..85){
						item[0]='camera'
						if([0,1].random())e.sendMessage("You find a ${d.rpg.items['camera']} on the ground. Free technology!").queue()
						else e.sendMessage("In a computer shop, you find a ${d.rpg.items['burger']} for really fuckin' cheap. So cheap, they give it to you for free.").queue()
					}
					if(item[0]){
						if(d.rpg[e.author.id].items.size()>29){
							e.sendMessage("Hey wait a second, your items are full. The ${item[0]} disappears into a black hole.").queue()
						}else{
							d.rpg[e.author.id].items+=[item]
						}
					}
				}else if(random==5){
					if([0,1].random()){
						d.rpg[e.author.id].next=1
						String message='You find a chest. '
						if(d.rpg[e.author.id].items.find{it[0]=='key'})message+="Will you appraise it with `${d.prefix}adventure key`?"
						else message+="But it's locked and you need a key."
						e.sendMessage(message).queue()
					}else{
						d.rpg[e.author.id].next=2
						String message='A big red door with a keyhole you could almost fit through looms in front of you... '
						if(d.rpg[e.author.id].items.find{it[0]=='key'})message+="Will you use `${d.prefix}adventure key` to open it?"
						else message+="Too bad you don't have anything to open it with."
						e.sendMessage(message).queue()
					}
				}else{
					random=(0..6).random()
					if(random in 0..5){
						String area=['wade through some tall grass','walk across a big field','treck a desert','tour through a cheese land','swim in a pond','navigate a forest','sidle on the edge of a cliff','go shopping in the city','visit a nudist beach','fly in the sky by flapping your arms','investigate a ghost house'].random()
						String sense=['see','hear','smell','touch','taste','sense','imagine'].random()
						String thing=['some flying fish','some strange mushrooms','Egbert','a couple of llamas','a nuclear explosion','your mom','a wild Ikue Ootani voiced creature',"a useful item, but it's too far away to get",'the beautiful sunrise','Jesus','a disembodied head','a spooky ghost, better call the ghost busters'].random()
						e.sendMessage("You $area and $sense $thing.").queue()
					}else if(random==6){
						String thing=['a homeless person','a charity shop','a street performer'].random()
						String and=['in the middle of nowhere','while in town','with a dog'].random()
						int money=(1..50).random()
						if(d.rpg[e.author.id].money<money){
							e.sendMessage("You pass by $thing $and, but don't have the money to spare.").queue()
						}else{
							d.rpg[e.author.id].money-=money
							e.sendMessage("Passing by $thing $and, you end up giving them <:coin:339089342356258826> `$money`.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}
					}
				}
			}
		}
		d.json.save(d.rpg,'rpg')
	}
	String category = 'RPG'
	String help = """`adventure` will go on an adventure, or run from a battle (but you'll lose money).
`adventure key` will unlock a door or a chest.
To adventure!"""
}


class MoveCommand extends Command {
	List aliases = ['move','skill']
	int limit = 30
	def run(Map d, Event e) {
		if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
		d.args=d.args.toLowerCase()
		if(d.args){
			Map battle=d.rpg.battles.find{e.author.id in it.users}
			if(d.rpg[e.author.id].next==4){
				e.sendMessage('PLACEHOLDER FOR USING ATTACKS OUTSIDE OF BATTLES').queue()
				// similar to cut/strength in pokemon games
			}else if(battle){
				if(battle.users[battle.who]==e.author.id){
					if(d.args in d.rpg[e.author.id].skills){
						// make it wait for the other player if there is one
						String message="**${e.author.identity.capitalize()}** used ${d.args.capitalize()}."
						Map enemy=battle.enemies.random()
						int power=d.rpg[e.author.id].attack-enemy.defense
						if(d.args=='thrash'){
							power+=20
						}
						// do more attacks!!!
						if(power)message+=" The attack did $power damage."
						if(power>0)enemy.health-=power
						if(enemy.health<1){
							int exp=d.rpg.enemies.find{it.emoji==enemy.emoji}.health/2
							d.rpg[e.author.id].experience
							int money=(enemy.attack-enemy.defense)*5
							if(money<2)money=2
							battle.enemies-=enemy
							if(!battle.enemies)d.rpg.battles-=battle
							message+="\n\n$enemy.emoji pretended to die.\n\nYou got <:exp:433672473985417226> `$exp` and <:coin:339089342356258826> `$money`. You now have <:exp:433672473985417226> `${d.rpg[e.author.id].experience}` and <:coin:339089342356258826> `${d.rpg[e.author.id].money}`."
							// do learning
						}
						if(battle?.enemies){
							battle?.enemies.each{Map wing->
								String ass=wing.skills.random()
								User you=e.jda.users.find{it.id==battle.users.random()}
								message+="\n\n$wing.emoji used ${ass.capitalize()} on **$you.identity**."
								power=wing.attack-d.rpg[you.id].defense
								if(ass=='thrash'){
									power+=20
								}else if(ass=='salmonella'){
									power=0
									int turns=(3..5).random()
									d.rpg[you.id].poison=turns
									message+=" **${you.identity.capitalize()}** is now poisoned ($turns turns)."
								}
								// do more attacks!!! the squeakquel
								if(power)message+=" The attack did $power damage."
								if(power>0)d.rpg[you.id].health-=power
								if(d.rpg[you.id].health<1){
									List item=[]
									if(d.rpg[you.id].items){
										item=d.rpg[you.id].items.random()
										d.rpg[you.id].items-=[item]
									}
									d.rpg[you.id].health=100
									int bill=d.rpg[you.id].money/3
									d.rpg[you.id].money-=bill
									if(item)message+="\n\nHurrying to the nearest hospital, $you.identity dropped ${d.rpg.items[item[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
									else message+="\n\n${you.identity.capitalize()} hurries to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health."
									battle.channels-=d.rpg.channels[d.rpg.users.indexOf(you.id)]
									battle.users-=you.id
								}
							}
						}
						battle.users.findAll{d.rpg[it].poison}.each{String sonicthehedgehogcdmusicthathasbeenextendedtoplayforatleast155minutes->
							Map bepis=d.rpg[sonicthehedgehogcdmusicthathasbeenextendedtoplayforatleast155minutes]
							message+="\n\n**${bepis.identity.capitalize()}** took 10 damage from the poison."
							bepis.health-=10
							if(bepis.health<1){
								List item=[]
								if(d.rpg[you.id].items){
									item=d.rpg[you.id].items.random()
									d.rpg[you.id].items-=[item]
								}
								d.rpg[you.id].health=100
								int bill=d.rpg[you.id].money/3
								d.rpg[you.id].money-=bill
								if(item)message+="\n\nHurrying to the nearest hospital, $you.identity dropped ${d.rpg.items[item[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
								else message+="\n\n${you.identity.capitalize()} hurries to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health."
								battle.channels-=d.rpg.channels[d.rpg.users.indexOf(you.id)]
								battle.users-=you.id
							}
						}
						if(!battle.users)d.rpg.battles-=battle
						battle.channels.each{List anus->
							e.jda.textChannels.find{it.id==anus[0]}.sendMessage("\u200b$message").queue()
							Thread.sleep(500)
						}
					}else{
						e.sendMessage("You don't even have a skill like that (yet), you bloody wanker.").queue()
					}
				}else{
					e.sendMessage("It's not even your turn you bumbling moron.").queue()
				}
			}else{
				e.sendMessage("Your mum's words echoed:\n\"Just because you can do it doesn't mean you should. Now get out, you're like 25.\"").queue()
			}
		}else{
			Map descs=[
				'thrash':'Flap about like a fucking retard. Does basic damage.',
				'salmonella':"Offer the enemy some meat but it's not cooked so he takes damage for the next few turns.",
				'explode':"Explode. You die immediately so it's conventionally useless.",
				'stupidity':'Show your true intelligence. It causes the enemy to lose a skill.',
				'diamond':'Your diamond-tipped nose breaks all shields.',
				'flirt':'Lowers their defense a bit.'
			]
			String ass=d.rpg[e.author.id].skills.collect{String skill->
				"$skill | ${descs[skill]}"
			}.join('\n')
			e.sendMessage("**${e.author.identity.capitalize()}'s Skills**:\nAttack: `${d.rpg[e.author.id].attack}` | Defense: `${d.rpg[e.author.id].defense}`\n\n$ass\n\nUse `${d.prefix}move [skill]` to use one.").queue()
		}
	}
	String category = 'RPG'
	String help = """`move` will make me list your skills.
`move [skill]` will use it on the enemy or obstacle.
This game apparently revolves around pica."""
}


class EatCommand extends Command {
	List aliases = ['eat','ingest']
	def run(Map d, Event e) {
		if(d.args){
			d.args=d.args.toLowerCase()
			if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
			List item=d.rpg[e.author.id].items.find{d.args.contains(it[0])}
			if(!item){
				e.sendMessage("Sorry bro, you can't eat what's not there.").queue()
			}else if(item[0]=='cart'){
				d.rpg[e.author.id].items-=[item]
				List item2=[]
				if(d.rpg[e.author.id].items){
					item2=d.rpg[e.author.id].items.random()
					d.rpg[e.author.id].items-=[item]
				}
				d.rpg[e.author.id].health=100
				int bill=d.rpg[e.author.id].money/3
				d.rpg[e.author.id].money-=bill
				String message="You eat the ${d.rpg.items[item[0]]}.\nYour taste buds are assaulted with the most bitter and disgusting taste imaginable.\nIt sticks around on your tongue for the next few days, and you refuse to eat under the religious belief that you are wasting the taste of good food taste by overlaying it with the unique flavour of the Nintendo Switch cartridge when it hits your tongue.\nAfter about a week, you die of starvation.\n\n"
				if(item2)message+="Hurrying to the nearest hospital, you dropped ${d.rpg.items[item2[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
				else message+="You hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health."
				e.sendMessage(message).queue()
			}else if(item[0]=='pillow'){
				d.rpg[e.author.id].items-=[item]
				e.sendMessage("You eat the entire ${d.rpg.items[item[0]]} over a few days. Nothing particularly bad happens, but you find it has no nutritional value.").queue()
			}else if(item[0]=='eggplant'){
				d.rpg[e.author.id].items-=[item]
				String message="You swallow the ${d.rpg.items[item[0]]} whole. "
				if(d.rpg[e.author.id].health==100){
					message+='It had no effect, because your health was full, you idiot.'
				}else{
					d.rpg[e.author.id].health+=30
					if(d.rpg[e.author.id].health>100)d.rpg[e.author.id].health=100
					message+="You regained :broken_heart: `30`!"
				}
				e.sendMessage(message).queue()
			}else if(item[0]=='sink'){
				e.sendMessage("You have no idea where to start with this, or if your teeth could ${d.rpg.items[item[0]]} into any part of it at all.").queue()
			}else if(item[0]in['sword','bomb','burger']){
				d.rpg[e.author.id].items-=[item]
				String message=''
				if(item[0]=='sword')message='You swallow the blade.'
				else if(item[0]=='bomb')message="Why the fuck did you eat a ${d.rpg.items['bomb']}?"
				else if(item[0]=='burger')message="You eat a ${d.rpg.items['burger']}, but it was actually a McDonalds burger in disguise, so you die."
				List item2=[]
				if(d.rpg[e.author.id].items){
					item2=d.rpg[e.author.id].items.random()
					d.rpg[e.author.id].items-=[item]
				}
				d.rpg[e.author.id].health=100
				int bill=d.rpg[e.author.id].money/3
				d.rpg[e.author.id].money-=bill
				if(item2)e.sendMessage("$message\n\nHurrying to the nearest hospital, you dropped ${d.rpg.items[item2[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health.").queue()
				else e.sendMessage("$message\n\nYou hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health.").queue()
			}else if(item[0]=='key'){
				d.rpg[e.author.id].items-=[item]
				e.sendMessage("You swallow the ${d.rpg.items[item[0]]}. Now no-one can ever unlock the door! Hahaha!").queue()
			}else if(item[0]=='shit'){
				d.rpg[e.author.id].items-=[item]
				String message="Congratulations, you actually ate ${d.rpg.items[item[0]]}. "
				if(d.rpg[e.author.id].health==100){
					message+='Now you feel ill.'
				}else{
					d.rpg[e.author.id].health+=5
					if(d.rpg[e.author.id].health>100)d.rpg[e.author.id].health=100
					message+="You regained :broken_heart: `5`!"
				}
				e.sendMessage(message).queue()
			}else if(item[0]=='picture'){
				d.rpg[e.author.id].items-=[item]
				e.sendMessage("You eat the ${d.rpg.items[item[0]]}. Your health is now :broken_heart: `1`.").queue()
			}else if(item[0]=='pear'){
				d.rpg[e.author.id].items-=[item]
				String message="You eat the ${d.rpg.items[item[0]]}. "
				if(d.rpg[e.author.id].health==100){
					message+='It tasted way better than any apple, but that was all that happened, because your health was already full.'
				}else{
					d.rpg[e.author.id].health+=70
					if(d.rpg[e.author.id].health>100)d.rpg[e.author.id].health=100
					message+="You regained :broken_heart: `30`!"
				}
				e.sendMessage(message).queue()
			}else if(item[0]=='eyes'){
				d.rpg[e.author.id].items-=[item]
				d.rpg[e.author.id].experience+=80
				e.sendMessage("You eat the ${d.rpg.items[item[0]]}... You feel strangely smarter.").queue()
			}else if(item[0]=='grover'){
				d.rpg[e.author.id].items-=[item]
				d.rpg[e.author.id].experience+=100
				d.rpg[e.author.id].health=100
				e.sendMessage("You eat ${d.rpg.items[item[0]]} like the Chinese. You gain `100` experience and full health. There's an item you can never get again.").queue()
			}else if(item[0]=='bikini'){
				d.rpg[e.author.id].items-=[item]
				String message="You eat the pants of the ${d.rpg.items[item[0]]}. What kind of pervert are you? "
				if(d.rpg[e.author.id].health==100){
					message+="Anyway, there were no consequences, just don't try it in real life."
				}else{
					d.rpg[e.author.id].health+=10
					if(d.rpg[e.author.id].health>100)d.rpg[e.author.id].health=100
					message+="You regained :broken_heart: `10`!"
				}
				e.sendMessage(message).queue()
			}else if(item[0]=='cook'){
				d.rpg[e.author.id].items-=[item]
				String message="You eat the egg that's been cooking in the pan every since you have it. You also eat the pan. "
				if(d.rpg[e.author.id].health==100){
					message+='Nothing happened because your health was full. Waste of a good egg.'
				}else{
					d.rpg[e.author.id].health+=80
					if(d.rpg[e.author.id].health>100)d.rpg[e.author.id].health=100
					message+="You regained :broken_heart: `80`!"
				}
				e.sendMessage(message).queue()
			}else if(item[0]=='whale'){
				d.rpg[e.author.id].items-=[item]
				String message='You eat the whale. '
				if(d.rpg[e.author.id].health==100){
					message+='But your health was full, so it was transported to another universe.'
				}else{
					d.rpg[e.author.id].health+=98
					if(d.rpg[e.author.id].health>100)d.rpg[e.author.id].health=100
					message+="You regained :broken_heart: `98`!"
				}
				e.sendMessage(message).queue()
			}else if(item[0]=='camera'){
				d.rpg[e.author.id].items=[['camera',(0..Short.MAX_VALUE).random()]]
				29.times{
					d.rpg[e.author.id].items+=[['picture',(0..Short.MAX_VALUE).random()]]
				}
				String message="As you're about to eat the camera, it suddenly flashes. You wonder why"
				if(d.rpg[e.author.id].items.findAll{it[0]!='picture'}.size()>3)message+=', but your bag now feels a lot lighter for some reason.'
				else message+='.'
				e.sendMessage(message).queue()
			}
			d.json.save(d.rpg,'rpg')
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}eat [item]`.").queue()
		}
	}
	String category = 'RPG'
	String help = """`eat [item]` will make you eat an item, whether it's good for you or not.
This game apparently revolves around pica."""
}


class WearCommand extends Command {
	List aliases = ['wear','armor']
	int limit = 150
	def run(Map d, Event e) {
		if(d.args){
			d.args=d.args.toLowerCase()
			if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
			List item=d.rpg[e.author.id].items.find{d.args.contains(it[0])}
			if(item){
				d.rpg[e.author.id].items-=[item]
				if(d.rpg[e.author.id].armour)d.rpg[e.author.id].items+=[[d.rpg[e.author.id].armour,(0..Short.MAX_VALUE).random()]]
				d.rpg[e.author.id].armour=item[0]
				if(item[0]=='cart'){
					d.rpg[e.author.id].defense=1
					e.sendMessage("You cover yourself with the ${d.rpg.items[item[0]]}, however, the most it can cover is your bepis, because it's so small (as in your bepis).\n\nYour defense is now `1`.").queue()
				}else if(item[0]=='pillow'){
					d.rpg[e.author.id].defense=10
					e.sendMessage("You unzip the ${d.rpg.items[item[0]]}, take out the fluff and pull it over your head. Perfect defense _and_ disguise!\n\nYour defense is now `10`.").queue()
				}else if(item[0]=='eggplant'){
					d.rpg[e.author.id].defense=-2
					e.sendMessage("You balance the ${d.rpg.items[item[0]]} atop your head, and think about how this is supposed to help at all.\n\nYour defense is now `-2`.").queue()
				}else if(item[0]=='sink'){
					d.rpg[e.author.id].defense=15
					e.sendMessage("With some DIY, you make it possible to stand in the ${d.rpg.items[item[0]]} and have your legs come out of the bottom. Now you're sink man!\n\nYour defense is now `15`.").queue()
				}else if(item[0]=='sword'){
					d.rpg[e.author.id].defense=0
					e.sendMessage("You wear the ${d.rpg.items[item[0]]} on your back. Now you look awesome.\n\n(Your defense is `0`, though.)").queue()
				}else if(item[0]=='key'){
					d.rpg[e.author.id].defense=0
					e.sendMessage("You grab the ${d.rpg.items[item[0]]} by the actual key-part and use the handle as a shield. It does nothing.").queue()
				}else if(item[0]=='shit'){
					d.rpg[e.author.id].defense=3
					e.sendMessage("You cover yourself with... What the fuck? I didn't know you were into that, but you do you.\n\nYour defense is now `3`.").queue()
				}else if(item[0]=='picture'){
					d.rpg[e.author.id].defense=4
					e.sendMessage("You hold out the ${d.rpg.items[item[0]]} in front of you, hoping it'll scare off anything that comes.\n\nYour defense is now `4`.")
				}else if(item[0]=='pear'){
					d.rpg[e.author.id].defense=-1
					e.sendMessage("You balance the ${d.rpg.items[item[0]]} atop your head, and think about how this is supposed to help at all.\n\nYour defense is now `-1`.").queue()
				}else if(item[0]=='bomb'){
					d.rpg[e.author.id].defense=0
					d.rpg[e.author.id].armour=null
					d.rpg[e.author.id].health=100
					int bill=d.rpg[e.author.id].money/3
					d.rpg[e.author.id].money-=bill
					e.sendMessage("Now that's just sil-\n\nYou hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health.").queue()
				}else if(item[0]=='eyes'){
					d.rpg[e.author.id].defense=0
					e.sendMessage("You put the ${d.rpg.items[item[0]]} over your actual eyes like glasses. You think you might be able to see a bit better.")
				}else if(item[0]=='grover'){
					d.rpg[e.author.id].defense=20
					e.sendMessage("You fuse with ${d.rpg.items[item[0]]} and become a mech. This change is reversible.\n\nYour defense is now `20`.").queue()
				}else if(item[0]=='bikini'){
					d.rpg[e.author.id].defense=3
					e.sendMessage("You wear the bikini over the top of your existing clothes.\n\nYour defense is now `3`.").queue()
				}else if(item[0]=='cook'){
					d.rpg[e.author.id].defense=7
					e.sendMessage("You put the pan on top of your head upside-down, and the egg magically stays inside.\n\nYour defense is now `7`.").queue()
				}else if(item[0]=='whale'){
					d.rpg[e.author.id].defense=23
					e.sendMessage("You go inside the whale's mouth and take control of its brain like the parasite you are.\n\nYour defense is now `23`.").queue()
				}else if(item[0]=='burger'){
					d.rpg[e.author.id].defense=0
					e.sendMessage("You wear the burger on your head as a cute little hat.\n\nYour defense is now `999`. Just kidding, it's `0`.").queue()
				}else if(item[0]=='camera'){
					d.rpg[e.author.id].armour=null
					d.rpg[e.author.id].defense=0
					d.rpg[e.author.id].items=[['camera',(0..Short.MAX_VALUE).random()]]
					29.times{
						d.rpg[e.author.id].items+=[['picture',(0..Short.MAX_VALUE).random()]]
					}
					String message="As you string the camera around your neck, it suddenly flashes. You wonder why"
					if(d.rpg[e.author.id].items.findAll{it[0]!='picture'}.size()>3)message+=', but your bag now feels a lot lighter for some reason.'
					else message+='.'
					e.sendMessage(message).queue()
				}
				d.json.save(d.rpg,'rpg')
			}else if(!item){
				if(d.rpg[e.author.id].armour){
					e.sendMessage("You don't have any armour like that, buddy.").queue()
				}else{
					e.sendMessage("You're already wearing nothing.").queue()
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}wear [item]`.").queue()
		}
	}
	String category = 'RPG'
	String help = """`wear [item]` will make you wear an item as armour or clothes.
Show some modesty."""
}


class WeaponCommand extends Command {
	List aliases = ['weapon','wield']
	int limit = 150
	def run(Map d, Event e) {
		if(d.args){
			d.args=d.args.toLowerCase()
			if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
			List item=d.rpg[e.author.id].items.find{d.args.contains(it[0])}
			if(item){
				d.rpg[e.author.id].items-=[item]
				if(d.rpg[e.author.id].weapon)d.rpg[e.author.id].items+=[[d.rpg[e.author.id].weapon,(0..Short.MAX_VALUE).random()]]
				d.rpg[e.author.id].weapon=item[0]
				if(item[0]=='cart'){
					d.rpg[e.author.id].attack=1
					e.sendMessage("You hold the ${d.rpg.items[item[0]]} in one hand, with the sharpest corner pointing forward.\n\nYour attack is now `1`.").queue()
				}else if(item[0]=='pillow'){
					d.rpg[e.author.id].attack=-4
					e.sendMessage("You hold the ${d.rpg.items[item[0]]} in front of you, preparing to bash it against anything that comes... If you can see it.\n\nYour attack is now `-4`.").queue()
				}else if(item[0]=='eggplant'){
					d.rpg[e.author.id].attack=1
					e.sendMessage("You hold the ${d.rpg.items[item[0]]} in one hand, pointing the stalk forward.\n\nYour attack is now `1`.").queue()
				}else if(item[0]=='sink'){
					d.rpg[e.author.id].attack=16
					e.sendMessage("You hold the enormous application in two hands. It's quite heavy, actually.\n\nYour attack is now `16`.").queue()
				}else if(item[0]=='sword'){
					d.rpg[e.author.id].attack=15
					e.sendMessage("Okay, you can't actually dual wield, so you hold two ${d.rpg.items[item[0]]} in one hand.\n\nYour attack is now `15`.").queue()
				}else if(item[0]=='key'){
					d.rpg[e.author.id].attack=1
					e.sendMessage("You hold the ${d.rpg.items[item[0]]} like a dagger, ready to stab.\n\nYour attack is now `1`.").queue()
				}else if(item[0]=='shit'){
					d.rpg[e.author.id].attack=4
					e.sendMessage("Finally, a ranged weapon!\n\nYour attack is now `4`.").queue()
				}else if(item[0]=='picture'){
					d.rpg[e.author.id].attack=2
					e.sendMessage("You hold the ${d.rpg.items[item[0]]} in one hand, with one corner pointing forward.\n\nYour attack is now `2`.").queue()
				}else if(item[0]=='pear'){
					d.rpg[e.author.id].attack=2
					e.sendMessage("You hold the ${d.rpg.items[item[0]]} in the hand you wouldn't normally handle apples with, pointing the stalk forward.\n\nYour attack is now `2`.").queue()
				}else if(item[0]=='bomb'){
					d.rpg[e.author.id].attack=0
					d.rpg[e.author.id].weapon=null
					d.rpg[e.author.id].health=100
					int bill=d.rpg[e.author.id].money/3
					d.rpg[e.author.id].money-=bill
					e.sendMessage("You prepare to thro-\n\nYou hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health.").queue()
				}else if(item[0]=='eyes'){
					d.rpg[e.author.id].attack=2
					e.sendMessage("Theoretically, you should only be able to throw two of these, but I can't be bothered to code.\n\nYour attack is now `2`.").queue()
				}else if(item[0]=='grover'){
					d.rpg[e.author.id].attack=20
					e.sendMessage("You tell ${d.rpg.items[item[0]]} to bite any baddies that come along.\n\nYour attack is now `20`.").queue()
				}else if(item[0]=='bikini'){
					d.rpg[e.author.id].attack=3
					e.sendMessage("You hold the bra in one hand and the pants in the other, and you aren't afraid to slap enemies with the strap if they approach you.\n\nYour attack is now `3`.").queue()
				}else if(item[0]=='cook'){
					d.rpg[e.author.id].attack=9
					e.sendMessage("You weaponize the pan.\n\nYour attack is now `9`.").queue()
				}else if(item[0]=='whale'){
					d.rpg[e.author.id].attack=2
					e.sendMessage("You grab the whale by its tail (lol rhyme xd). Turns out its bite isn't very powerful at all.\n\nYour attack is now `2`.")
				}else if(item[0]=='burger'){
					d.rpg[e.author.id].attack=-1
					e.sendMessage("You dismantle the burger into individual pieces of bread and hold one in each hand, hoping to complete a bread-lectrical circuit by grabbing your foes with them. Of course, it actually just makes things worse.\n\nYour attack is now `-1`.")
				}else if(item[0]=='camera'){
					d.rpg[e.author.id].attack=0
					d.rpg[e.author.id].items=[['camera',(0..Short.MAX_VALUE).random()]]
					29.times{
						d.rpg[e.author.id].items+=[['picture',(0..Short.MAX_VALUE).random()]]
					}
					String message="As swing the camera around by its string, it suddenly flashes. You wonder why"
					if(d.rpg[e.author.id].items.findAll{it[0]!='picture'}.size()>3)message+=', but your bag now feels a lot lighter for some reason.'
					else message+='.'
					e.sendMessage(message).queue()
				}
				d.json.save(d.rpg,'rpg')
			}else if(!item){
				if(d.rpg[e.author.id].weapon){
					e.sendMessage("You don't have any weapons like that, buddy.").queue()
				}else{
					e.sendMessage("Can't get any barer than bare fists.").queue()
				}
			}
		}else{
			e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}weapon [item]`.").queue()
		}
	}
	String category = 'RPG'
	String help = """`weapon [item]` will equip an item in your inventory as a weapon.
Ha! Ya!"""
}


class ShopCommand extends Command {
	List aliases = ['shop']
	def run(Map d, Event e) {
		if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
		d.args=d.args.toLowerCase().tokenize()
		if(d.args){
			if(d.args[0]=='buy'){
				if(d.rpg[e.author.id].items.size()>29){
					e.sendMessage("You aren't fit to buy shit. Good thing we don't sell shit. But seriously, inventory full.").queue()
				}else if(d.args[1]){
					if(d.args[1]=='pear'){
						if(d.rpg[e.author.id].money>115){
							d.rpg[e.author.id].money-=115
							d.rpg[e.author.id].items+=[['pear',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Here's your ${d.rpg.items['pear']}. It's not in a pair though.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else{
							e.sendMessage("Whatever money you were gonna use to buy this seems to have disap**pear**ed.").queue()
						}
					}else if(d.args[1]=='eggplant'){
						if(d.rpg[e.author.id].money>60){
							d.rpg[e.author.id].money-=60
							d.rpg[e.author.id].items+=[['eggplant',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Here's your ${d.rpg.items['eggplant']}. Doesn't grow eggs unfortunately.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else{
							e.sendMessage("I grew this eggplant. Why don't you grow a money plant? 'Cause you're short on it.").queue()
						}
					}else if(d.args[1]=='sword'){
						if(d.rpg[e.author.id].money>240){
							d.rpg[e.author.id].money-=240
							d.rpg[e.author.id].items+=[['sword',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Here's your ${d.rpg.items['sword']}. I hope you're over 3 years old.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else{
							e.sendMessage("You **sword** you had more money? Guess you **sword** wrong.").queue()
						}
					}else if(d.args[1]=='bomb'){
						if(d.rpg[e.author.id].money>300){
							d.rpg[e.author.id].money-=300
							d.rpg[e.author.id].items+=[['bomb',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Lamp oil? Rope? ${d.rpg.items['bomb']}? It's yours my friend.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else{
							e.sendMessage("Did your wallet get bombed? Cause there's not enough in it to buy this.").queue()
						}
					}else if(d.args[1]=='burger'){
						if(d.rpg[e.author.id].money>25){
							d.rpg[e.author.id].money-=25
							d.rpg[e.author.id].items+=[['burger',(0..Short.MAX_VALUE).random()]]
							e.sendMessage("Order 6669001337? Here you go (${d.rpg.items['burger']}).\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else{
							e.sendMessage("Jesus Christ you're poor. Just go back to living out on the streets.").queue()
						}
					}else{
						e.sendMessage("I'm not even selling that, WTF?").queue()
					}
					d.json.save(d.rpg,'rpg')
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}shop buy/sell [item]`.").queue()
				}
			}else if(d.args[0]=='sell'){
				if(d.args[1]){
					List item=d.rpg[e.author.id].items.find{d.args[1].contains(it[0])}
					if(item){
						d.rpg[e.author.id].items-=[item]
						if(item[0]=='bikini'){
							d.rpg[e.author.id].money+=20
							e.sendMessage("Where'd you find this, kid? Anyway, we'll take it for <:coin:339089342356258826> `20` for uhh... Resale purposes.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='bomb'){
							int money=(5..50).random()
							d.rpg[e.author.id].money+=money
							e.sendMessage("The shopkeeper explodes and drops <:coin:339089342356258826> `$money`.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]in['burger','cook']){
							d.rpg[e.author.id].items+=[[item]]
							e.sendMessage("Sorry bud, I can't take food like this.").queue()
						}else if(item[0]=='cart'){
							d.rpg[e.author.id].money+=60
							e.sendMessage("Are you the Nintendo employee we needed stock from? We'll take this for <:coin:339089342356258826> `60`.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='eggplant'){
							d.rpg[e.author.id].money+=15
							e.sendMessage("Is this a lewd metaphor? Anyway, I'll buy it for <:coin:339089342356258826> `15`.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='eyes'){
							d.rpg[e.author.id].money+=45
							e.sendMessage("A donation for research? I don't have much money on me personally, but will you accept <:coin:339089342356258826> `45`? (You can't say no.)\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='grover'){
							d.rpg[e.author.id].money+=500
							e.sendMessage("What? You're selling ${d.rpg.items[item[0]]}? That's a time paradox! A black hole opened up and <:coin:339089342356258826> `500` came out. It took ${d.rpg.items[item[0]]} with it.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='key'){
							d.rpg[e.author.id].money+=5
							e.sendMessage("This ${d.rpg.items[item[0]]} isn't much use to me, so I'll just pay <:coin:339089342356258826> `5`.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='pear'){
							e.sendMessage("Fresh fruit? Don't mind if I do... Nom nom!\n\nWait, were you selling that? Oops...").queue()
						}else if(item[0]in['pillow','picture','shit']){
							d.rpg[e.author.id].items+=[item]
							e.sendMessage("I'd rather not.").queue()
						}else if(item[0]=='sink'){
							d.rpg[e.author.id].money+=100
							e.sendMessage("Sweet, iron! I'll pay <:coin:339089342356258826> `100`.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='sword'){
							d.rpg[e.author.id].money+=60
							e.sendMessage("I can pay you <:coin:339089342356258826> `60` for that.\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='whale'){
							d.rpg[e.author.id].money+=150
							e.sendMessage("How'd you even get this in the store? Have <:coin:339089342356258826> `150`!\n\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`.").queue()
						}else if(item[0]=='camera'){
							d.rpg[e.author.id].items+=[item]
							d.rpg[e.author.id].items=[['camera',(0..Short.MAX_VALUE).random()]]
							29.times{
								d.rpg[e.author.id].items+=[['picture',(0..Short.MAX_VALUE).random()]]
							}
							String message="As you give the camera to the nice man, it flashes. You wonder why"
							if(d.rpg[e.author.id].items.findAll{it[0]!='picture'}.size()>3)message+=', but your bag now feels a lot lighter for some reason.'
							else message+='.'
							e.sendMessage(message).queue()
						}
						d.json.save(d.rpg,'rpg')
					}else{
						String message="You dare try to scam me?! I'll scam your face! :boom:"
						item=[]
						if(d.rpg[e.author.id].items){
							item=d.rpg[e.author.id].items.random()
							d.rpg[e.author.id].items-=[item]
						}
						d.rpg[e.author.id].health=100
						int bill=d.rpg[e.author.id].money/3
						d.rpg[e.author.id].money-=bill
						if(item)message+="\n\nHurrying to the nearest hospital, you dropped ${d.rpg.items[item[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
						else message+="\n\nYou hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health."
						e.sendMessage(message).queue()
					}
				}else{
					e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}shop buy/sell [item]`.").queue()
				}
			}else{
				e.sendMessage(d.errorMessage()+"Usage: `${d.prefix}shop buy/sell [item]`.").queue()
			}
		}else{
			e.sendMessage("""Here's what I'm selling. `${d.prefix}shop buy`, `${d.prefix}shop sell`.

${d.rpg.items['pear']} | <:coin:339089342356258826> `115`
${d.rpg.items['eggplant']} | <:coin:339089342356258826> `60`
${d.rpg.items['sword']} | <:coin:339089342356258826> `240`
${d.rpg.items['bomb']} | <:coin:339089342356258826> `300`
${d.rpg.items['burger']} | <:coin:339089342356258826> `25`""").queue()
		}
	}
	String category = 'RPG'
	String help = """`shop` will make me list what's for sale at the RPG shop.
`shop buy [item]` will buy the item from the shop if you have the money.
`shop sell [item]` will sell the item to the shop if they'll accept it.
Finally, a command women can enjoy."""
}


class DailyCommand extends Command {
	List aliases = ['daily','dailies']
	def run(Map d, Event e) {
		if(!d.rpg[e.author.id])d.rpg[e.author.id]=[money:500,items:[],experience:0,health:100,skills:['thrash'],attack:0,defense:0]
		String date=new Date().format('d/M/YYYY')
		if(d.rpg[e.author.id].daily!=date){
			if([0,0,0,0,0,0,0,1].random()){
				e.sendMessage("Oh, you wanted dailies? Too bad, you aren't getting them. :slight_smile:").queue()
			}else{
				String message=['Oh, your dailies? Here you go.',"Here's your allowance as promised.",'Thanks for playing my game!'].random()
				int money=[100,200,300].random()
				d.rpg[e.author.id].money+=money
				String item=['shit','eggplant','pear','key','cook'].random()
				if(d.rpg[e.author.id].items.size()>29){
					message+="\nYou got <:coin:339089342356258826> `$money` and dropped the ${d.rpg.items[item]} you also recieved on the floor.\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`."
				}else{
					d.rpg[e.author.id].items+=[[item,(0..Short.MAX_VALUE).random()]]
					message+="\nYou got ${d.rpg.items[item]} and <:coin:339089342356258826> `$money`.\nYou now have <:coin:339089342356258826> `${d.rpg[e.author.id].money}`."
				}
				e.sendMessage(message).queue()
			}
			d.rpg[e.author.id].daily=date
		}else{
			String message=":zap: MORE?! YOU DARE ASK FOR MORE?! You must die! :zap:"
			List item=[]
			if(d.rpg[e.author.id].items){
				item=d.rpg[e.author.id].items.random()
				d.rpg[e.author.id].items-=[item]
			}
			d.rpg[e.author.id].health=100
			int bill=d.rpg[e.author.id].money/3
			d.rpg[e.author.id].money-=bill
			if(item)message+="\n\nHurrying to the nearest hospital, you dropped ${d.rpg.items[item[0]]} and had to pay a <:coin:339089342356258826> `$bill` bill to regain full health."
			else message+="\n\nYou hurry to the nearest hospital, paying a <:coin:339089342356258826> `$bill` bill to regain full health."
			e.sendMessage(message).queue()
		}
		d.json.save(d.rpg,'rpg')
	}
	String category = 'RPG'
	String help = """`daily` will make me give you money and a random item - once every UK day.
Better make that Septapus reminder."""
}
*/

class PlayingCommand extends Command {
	List aliases = ['playing','gameinfo']
	def run(Map d, Event e) {
		def user=e.jda.selfUser
		if(d.args&&e.guild)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			Guild guild=e.jda.guilds.find{user.id in it.users*.id}
			def member=guild.members.find{it.user.id==user.id}
			if(user.id==e.jda.selfUser.id){
				if(d.info.game){
					List type=['playing','watching','listening to']
					User player=e.jda.users.find{it.id==d.info.player}
					e.sendMessage("I am ${type[d.info.act]} **${d.info.game}** with **${player?player.identity:d.info.player}**.").queue()
				}else{
					e.sendMessage("I'm not playing anything right now. How about you?").queue()
				}
			}else if(member.game){
				def game=member.game
				String message="**${user.identity.capitalize()}** is "
				if(game.type==Game.GameType.STREAMING){
					message+="streaming **$game.name**"
				}else if(game.type==Game.GameType.LISTENING){
					message+="listening to **$game.name**"
				}else if(game.type==Game.GameType.WATCHING){
					message+="watching **$game.name**"
				}else{
					message+="playing **$game.name**"
				}
				if(game.rich){
					if(game.timestamps){
						long ass=(game.timestamps.end?:System.currentTimeMillis())-game.timestamps.start
						List time=[0,(((ass)/1000)/60)as int]
						(time[1]/60).times{
							time[0]+=1
							time[1]-=60
						}
						message+=" for ${time[0]} hour${time[0]==1?'':'s'} and ${time[1]} minute${time[1]==1?'':'s'}."
					}else{
						message+='.'
					}
					if(game.details)message+="\n$game.details"
					if(game.state)message+="\n$game.state"
					if(game.url)message+="\nLink: $game.url"
					if(game.largeImage)message+="\nLarge image: $game.largeImage.url${game.largeImage.text?" ($game.largeImage.text)":''}"
					if(game.smallImage)message+="\nSmall image: $game.smallImage.url${game.smallImage.text?" ($game.smallImage.text)":''}"
					if(game.party){
						message+="\nParty ID: $game.party.id"
						if(game.party.size||game.party.max)message+=" ($game.party.size / $game.party.max)"
					}
					if(game.applicationIdLong)message+="\nGame ID: $game.applicationId"
					if(game.sessionId)message+="\nSession ID: $game.sessionId"
					if(game.syncId)message+="\nSync ID: $game.syncId"
					if(game.flags)message+="\nFlags: $game.flags"
				}
				if(!user.bot){
					String fast=game.name.toLowerCase()
					int count=e.jda.guilds*.members.flatten().findAll{!it.user.bot}.findAll{it.game}.groupBy{it.user.id}*.value*.first().findAll{it.game.name.toLowerCase()==fast}.size()-1
					if(count)message+="\n\n**$count** other ${count==1?'person is':'people are'}, too!"
					else message+="\n\nLooks like nobody else is."
				}
				e.sendMessage(message).queue()
			}else{
				e.sendMessage("**${user.identity.capitalize()}** isn't playing anything.").queue()
			}
		}else{
			e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}
	}
	String category = 'General'
	String help = """`playing [user]` will make me give details about their game and links to the images.
Not in a code block, though."""
}


class SlotsCommand extends Command {
	List aliases = ['slots','gamble']
	def run(Map d, Event e) {
		d.args=d.args.tokenize()
		final long moneys = (d.args[0] ==~ /\d+/) ? Long.parseLong(d.args[0]) : 1
		List input=((e.guild?e.guild.emotes.toList().findAll{!it.animated}.randomize()*.mention:[])+[':tangerine:',':banana:',':apple:',':pineapple:',':peach:',':tomato:',':lemon:',':eggplant:'])[0..(3..7).random()]
		List output=[]
		9.times{
			output+=input.random()
		}
		boolean match=((output[0]==output[1])&&(output[1]==output[2])||(output[3]==output[4])&&(output[4]==output[5])||(output[6]==output[7])&&(output[7]==output[8])||(output[0]==output[4])&&(output[4]==output[8])||(output[6]==output[4])&&(output[4]==output[2]))
		String machine="`>` ${output[0..2].join()} `<`\n`>` ${output[3..5].join()} `<`\n`>` ${output[6..8].join()} `<`\n\n"
		if(match){
			int out=moneys*[0.5,1.5,2,2.5].random()
			if(moneys<1){
				e.sendMessage("$machine**You won!**\nBut you didn't get anything because you didn't put any money in, you cheeky fucker.").queue()
			}else if([0,0,0,0,0,1].random()){
				e.sendMessage("$machine**You won!**\nBut you drop the money on the floor, and before you can pick it up, a random dog eats it.").queue()
			}else{
				e.sendMessage("$machine**You won!**\nYou got <:coin:339089342356258826> `$out`.").queue()
			}
		}else{
			String comment=["Have you still not noticed you're only losing money?","Get out of here, I bet you're too young to gamble and probably too young to use Discord.","Don't worry, your money isn't going to any noble cause.",'Feel like trying again, loser?'].random()
			if([0,0,0,0,0,0,0,0,1].random())e.sendMessage("$machine**You won!** Only joking, you lost! $comment").queue()
			else e.sendMessage("$machine**You lost!** $comment").queue()
		}
	}
	String category = 'General'
	String help = """`slots [amount]` will bet that amount on a (custom) emoji slot machine. You'll get a random return, if any.
There's no actual monetary system involved, though."""
}


class FortuneCommand extends Command {
	List aliases = ['fortune','crystalball']
	def run(Map d, Event e) {
		User user=e.author
		if(d.args)user=e.message.mentions?e.message.mentions[-1]:e.guild.findUser(d.args)
		if(user){
			// Do not change
			List goods=["You will meet a beautiful young lady while doing your job that you hate","You'll think of a creative new idea for a horror game, and it will make you a millionaire","You'll meet a very hot guy in a dark alleyway, and learn you were fucking gay all along","You will be the first to find proof of aliens","You'll join the trend of creating Discord bots, but your bot will be a hit bigger than Mee6","One day while doing nothing of note, you'll suddenly figure out a cure for cancer","In an undeterminable period of time, you will wake up and discover that you are God","While learning how to design websites, you'll accidentally create the successor to Facebook","A large company like Google will recruit you under their wing","You'll make stupid videos on YouTube and succeed Pewdiepie","You will meet your last fictional crush in real life form","You'll make an amazing archaelogical discovery in your backyard"]
			List bads=["However, on the night of that event, a limbless, floating body will come into your room and kill you","You will also die in a car crash","But in a few years, the person you love the most will die of cancer","Then you get hit by a truck and your soul is transported to an RPG world where you team up with a bunch of dipshits","Some dude wearing a white mask will then hack into your bank account","Immediately after, you'll be imprisoned as suspect of a crime you're pretty sure you didn't commit","You get caught in a terrorist attack the next day","Nothing bad happens","Afterwards, a psychopath decapitates you and stores your bleeding head in the fridge","Then you'll get a huge bill from something you don't remember signing up for and lose all your money","But your friends will all betray you as a result","Then you get raped lmao"]
			String good = goods[(Long.parseLong(user.id[3..-1]) % (goods.size())).toInteger()]
			String bad = bads[(Long.parseLong(user.createTimeMillis) % (bads.size())).toInteger()]
			e.sendMessage("**${(user.id==e.author.id)?'Your':user.identity+'\'s'} ${[0,0,0,0,1].random()?'mis':''}fortune**:\n$good.\n$bad.").queue()
		}else{
			e.sendMessage(["I couldn't find a user matching '$d.args.'","Ik kon niet vind een gebruiker vind '$d.args' leuk.","N\u00e3o consegui encontrar um usu\u00e1rio que corresponda a '$d.args.'","Nie mog\u0142em znale\u017a\u0107 u\u017cytkownika pasuj\u0105cego do '$d.args.'"].lang(e)).queue()
			404
		}
	}
	String category = 'General'
	String help = """`fortune [user]` will make me predict that user's future, or your future, and remember my prediction.
* Not a qualified fortune teller."""
}


class EntryCommand extends Command {
	List aliases = ['entry','edit']
	Map minipool = [create: [], name: [], irl: [], age: [], area: [], mc: [], lp: [], mal: [], mail: []]
	Map requests = [:]
	def run(Map d, Event e) {
		Map entry = d.db.find{e.author.id in it.value.ids}?.value
		if (entry) {
			if (d.args) {
				e.sendTyping().queue()
				d.args = d.args.tokenize()
				d.args[0] = d.args[0].toLowerCase()
				if (d.args[0] in ['name', 'aka']) {
					d.args[1] = d.args[1] ? d.args[1..-1].join(' ') : null
					if (!d.args[1]) {
						e.sendMessage('Please input something. (Names cannot be removed on their own.)').queue()
						400
					} else if (e.author.id in minipool.name) {
						e.sendMessage("Sorry, you can't change your name again for a while.").queue()
						429
					}else if ((d.args[1] in (d.db*.value*.aka.flatten() - entry.aka)) || (d.args[1].length() > 31)) {
						e.sendMessage('Name is unavailable.').queue()
						403
					} else {
						entry.aka = entry.aka.reverse() - d.args[1]
						entry.aka += d.args[1]
						entry.aka = entry.aka.reverse()
						if (entry.aka.size() > 10) entry.aka -= entry.aka[-1]
						d.json.save(d.db, 'database')
						e.sendMessage("Your current name is now: ${d.args[1]}.").queue()
						Thread.start{
							minipool.name += e.author.id
							Thread.sleep(3600000)
							minipool.name -= e.author.id
						}
					}
				} else if (d.args[0] in ['irl', 'realname']) {
					d.args[1] = d.args[1] ? d.args[1..-1].join(' ') : null
					if (!d.args[1]) {
						entry.remove('irl')
						d.json.save(d.db, 'database')
						e.sendMessage('Your real name has been removed.').queue()
						Thread.start{
							minipool.irl += e.author.id
							Thread.sleep(3600000)
							minipool.irl -= e.author.id
						}
					} else if (e.author.id in minipool.irl) {
						e.sendMessage("Sorry, you can't change your real name again for a while.").queue()
						429
					} else if ((d.args[1].length()>99)||d.args[1].containsAny(['!','?'])||(d.args.tokenize().size()>4)) {
						e.sendMessage('Invalid real name.').queue()
						403
					} else {
						entry.irl = d.args[1]
						d.json.save(d.db, 'database')
						e.sendMessage("Your real name has been updated to: ${d.args[1]}.").queue()
						Thread.start{
							minipool.irl += e.author.id
							Thread.sleep(3600000)
							minipool.irl -= e.author.id
						}
					}
					// age
				} else if (d.args[0] in ['area', 'location']) {
					d.args = d.args[1] ? d.args[1..-1].join(' ') : null
					if (!d.args[1]) {
						entry.accounts.remove('area')
						d.json.save(db, 'database')
						e.sendMessage('Your home location has been removed.').queue()
						Thread.start{
							minipool.area += e.author.id
							Thread.sleep(3600000)
							minipool.area -= e.author.id
						}
					} else if (e.author.id in minipool.area) {
						e.sendMessage("Sorry, you can't change your home location again for awhile.").queue()
					} else if (d.args.toLowerCase().endsWithAny(d.misc.geos*.key*.toLowerCase())) {
						entry.area = d.args.capitalize()
						d.json.save(d.db, 'database')
						e.sendMessage("Your home location has been updated to: ${d.args[1]}.").queue()
						Thread.start{
							minipool.area += e.author.id
							Thread.sleep(3600000)
							minipool.area -= e.author.id
						}
					} else {
						e.sendMessage("Invalid home location.").queue()
					}
				}else if(d.args[0]in['bio','info']){
					String bio=d.args[1]?d.args[1..-1].join(' '):null
					if(!bio){
						entry.accounts.remove('bio')
						d.json.save(d.db,'database')
						e.sendMessage('Your bio has been removed.').queue()
					}else if(bio.length()>500){
						e.sendMessage('Sorry, your bio cannot be over 500 characters.').queue()
						413
					}else{
						entry.bio=bio
						d.json.save(d.db,'database')
						e.sendMessage("Your bio has been updated to: _${bio}_").queue()
					}
				}else if(d.args[0]in['lp','levelpalace']){
					d.args[1]=d.args[1]?d.args[1..-1].join(' '):null
					if(!d.args[1]){
						entry.accounts.remove(lp)
						d.json.save(d.db,'database')
						e.sendMessage('Your Level Palace account has been removed.').queue()
						Thread.start{
							minipool.lp+=e.author.id
							Thread.sleep(1800000)
							minipool.lp-=e.author.id
						}
					}else if(e.author.id in minipool.lp){
						e.sendMessage("Sorry, you can't change your Level Palace account again for a while.").queue()
						429
					}else if(d.args[1].join(' ')in(d.db*.value*.accounts*.lp-entry.accounts.lp)){
						e.sendMessage('Invalid Level Palace account.').queue()
						403
					}else{
						def doc=d.web.get("https://www.levelpalace.com/profile.php?user=${URLEncoder.encode(d.args[1].join(' '),'UTF-8')}&client=dogbot")
						def el=doc.getElementsByClass('card-content')
						if(el){
							if(d.bot.commands.find{'levelpalace'in it.aliases}.available){
								entry.accounts.lp=d.args[1].toLowerCase()
								d.json.save(d.db,'database')
								e.sendMessage("Your Level Palace account has been updated to: ${d.args[1]}.").queue()
								Thread.start{
									minipool.lp+=e.author.id
									Thread.sleep(1800000)
									minipool.lp-=e.author.id
								}
							}else{
								final User axew = e.jda.users.find{it.id == d.bot.owner}
								e.sendMessage("Level Palace is under maintenance right now, check back later.\n(If you believe this is in error, go bug $axew.name#$axew.discriminator.)").queue()
								503
							}
						}else{
							e.sendMessage('Invalid Level Palace account.').queue()
							403
						}
					}
				}else if(d.args[0]in['mc','minecraft']){
					if(!d.args[1]){
						entry.accounts.remove('mc')
						d.json.save(d.db,'database')
						e.sendMessage('Your Minecraft account has been removed.').queue()
						Thread.start{
							minipool.mc+=e.author.id
							Thread.sleep(1800000)
							minipool.mc-=e.author.id
						}
					}else if(e.author.id in minipool.mc){
						e.sendMessage("Sorry, you can't change your Minecraft account again for a while.").queue()
						429
					}else if((d.args[1]in(d.db*.value*.accounts*.mc-entry.accounts.mc))||(d.args[1].length()>63)||d.args[1].containsAny(['!','.',',','?','/'])){
						e.sendMessage('Invalid Minecraft account.')
						403
					}else{
						String mc=d.args[1].replace(' ','_')
						entry.accounts.mc=mc
						d.json.save(d.db,'database')
						e.sendMessage("Your Minecraft account has been updated to: $mc.").queue()
						Thread.start{
							minipool.mc+=e.author.id
							Thread.sleep(1800000)
							minipool.mc-=e.author.id
						}
					}
				}else if(d.args[0]in['mal','myanimelist']){
					if(!d.args[1]){
						entry.accounts.remove('mal')
						d.json.save(d.db,'database')
						e.sendMessage('Your MyAnimeList account has been removed.').queue()
						Thread.start{
							minipool.mal+=e.author.id
							Thread.sleep(1800000)
							minipool.mal-=e.author.id
						}
					}else if(e.author.id in minipool.mal){
						e.sendMessage("Sorry, you can't change your MyAnimeList account again for a while.").queue()
						429
					}else if(d.args[1].join(' ')in(d.db*.value*.accounts*.mal-entry.accounts.mal)){
						e.sendMessage('Invalid MyAnimeList account.').queue()
						403
					}else{
						def doc=d.web.get("https://myanimelist.net/profile/${URLEncoder.encode(d.args[1],'UTF-8')}")
						def el=doc.getElementsByClass('user-image')
						if(el){
							entry.accounts.mal=d.args[1]
							d.json.save(d.db,'database')
							e.sendMessage("Your MyAnimeList account has been updated to: ${d.args[1]}.").queue()
							Thread.start{
								minipool.mal+=e.author.id
								Thread.sleep(1800000)
								minipool.mal-=e.author.id
							}
						}else{
							e.sendMessage('Invalid Level Palace account.').queue()
							404
						}
					}
				}else if(d.args[0]in['mail','email']){
					if(!d.args[1]){
						entry.accounts.remove('mail')
						d.json.save(d.db,'database')
						e.sendMessage('Your email address has been removed.').queue()
						Thread.start{
							minipool.mail+=e.author.id
							Thread.sleep(1800000)
							minipool.mail-=e.author.id
						}
					}else if(e.author.id in minipool.mail){
						e.sendMessage("Sorry, you can't change your email address again for a while.").queue()
					}else if((d.args[1]in(d.db*.value*.accounts*.mail-entry.accounts.mail))||(d.args[1].length()>255)||!d.args[1].contains('@')){
						e.sendMessage('Invalid email address.').queue()
						403
					}else{
						try{
							d.web.get(d.args[1].substring(d.args[1].indexOf('@')+1))
							entry.accounts.mail=d.args[1]
							d.json.save(d.db,'database')
							e.sendMessage("Your email address has been updated to: ${d.args[1]}").queue()
							Thread.start{
								minipool.mail+=e.author.id
								Thread.sleep(1800000)
								minipool.mail-=e.author.id
							}
						}catch(nope){
							e.sendMessage('Invalid email address.')
							404
						}
					}
				}else if(d.args[0]=='export'){
					def full = d.db.find{e.author.id in it.value.ids}
					String export="```${JsonOutput.prettyPrint(JsonOutput.toJson([(full.key): full.value]))}```Created: ${new Date(full.key * 10)}"
					if (d.args[1]?.toLowerCase() == 'here') {
						e.sendMessage(export).queue()
					} else {
						e.author.openPrivateChannel().complete()
						try {
							e.author.privateChannel.sendMessage(export).queue()
						}catch (ex) {
							e.sendMessage('Your Discord settings prevent me from DMing you the export. Use `${d.prefix}entry export here` to export to this channel.').queue()
						}
					}
				}else if(d.args[0]=='import'){
					e.sendMessage('Sorry, you cannot import database entries.').queue()
				}else{
					e.sendMessage("Unknown subcommand. Please read `${d.prefix}help entry` for usage.").queue()
				}
			}else{
				e.sendMessage("No subcommand specified. Please read `${d.prefix}help entry` for usage.").queue()
			}
		}else{
			if(minipool.create[e.author.id]){
				e.sendMessage("You just deleted your entry less than a week ago!").queue()
			}else{
				String name=e.author.name
				if(!(d.args in['name','irl','age','area','join','bio','mc','mal','lp','email','export','delete']))name=d.args
				d.db[((System.currentTimeMillis/10)as int).toString()]=[
					ids:[e.author.id],
					aka:[name],
					accounts:[:]
				]
				d.json.save(d.db, 'database')
				e.sendMessage("A database entry has been created for you. Please read `${d.prefix}help entry` _carefully_.\nIf you used this command by accident, you can delete the entry with `${d.prefix}entry delete`, but you won't be able to recreate it for 7 days.\n\nThis information is only used for statistic purposes and as a response to some commands performed in servers you're in. I probably don't even know you, so I have no malicious use for your information.").queue()
			}
		}
	}
	String category = 'Database'
	String help = """`entry [name]` will make me create a database entry for you.
`entry name [new name]` will add a name to the list and I will refer to you as that name. Names cannot be removed.
`entry irl [real name]` will make me add your real life name to the database. Make it blank to remove it. Options are _FIRSTNAME_, _FIRSTNAME SURNAME_ or _FIRSTNAME MIDDLE NAMES SURNAME_.
`entry age [birthday]` will make me add your birthday to the database. Make it blank to remove it. Options are _YEAR_, _DATE MONTH_ or _DATE MONTH YEAR_.
`entry area [home location]` will make me add where you live to the database. Make it blank to remove it. Inputs must end with a valid country.
`entry join [other account id]` will make me add another account to your database entry. That account also has to use the command. Alts cannot be removed.
`entry bio [about you]` will make me add some information about you to your database entry. Make it blank to remove it.
`entry mc [minecraft username]` will make me add your Minecraft account to your database entry. Make it blank to remove it.
`entry lp [levelpalace username]` will make me add your Level Palace account to your database entry. Make it blank to remove it.
`entry mal [myanimelist username]` will make me add your MyAnimeList account to your database entry. Make it blank to remove it.
`entry mail [email address]` will make me add your email address to your database entry. Make it blank to remove it.
`entry export` will make send you your database entry in JSON form.
`entry delete` will delete your database entry. **You will not be able to recreate it for a week.**
In accordance with the new EU data protection laws..."""
}


class MarkovCommand extends Command {
	List aliases = ['markov']
	int limit = 35
	def run(Map d, Event e) {
		int times = 1
		User user = e.author
		if(d.args.contains('batch')){
			d.args = (d.args - 'batch').trim()
			times = 3
		}
		if (e.message.mentions) {
			user = e.message.mentions[-1]
		} else if (e.guild) {
			if (d.args.startsWith('random')) {
				List ids = new File('markov').list()*.replace('.txt','')
				user = e.guild.users.toList().findAll{it.id in ids}.random()
			} else if(d.args) {
				user = e.guild.findUser(d.args)
			}
		}
		String id = user?.id ?: d.args
		if (!user) user = (e.jda.users - e.guild?.users).find{it.id == id}
		File markov = new File("markov/${id}.txt")
		if (markov.exists()) {
			e.sendTyping().queue()
			final lines = markov.readLines()
			try{
				List list = []
				times.times{
					def sentences = new ArrayList<List<String>> (lines.size().intdiv(4).intValue())
					for (l in lines) {
						final t = l.tokenize()
						if (!t.empty) sentences.add(t)
					}
					def output
					def sentence = new ArrayList<String> (),last
					sentence.add(last = sentences.random().random())
					def iterations = 0
					while (true) {
						if (++iterations > 1000) throw new TooManyLoopsException(1000)
						def following = new ArrayList<String> ()
						for (s in sentences) {
							for (int i = 1; i < s.size(); i++) {
								final before = s[i-1]
								if (before.hashCode() == last.hashCode() && before.equalsIgnoreCase(last)) following.add(s[i])
							}
						}
						if (following.empty) break
						sentence.add(last = following.random())
					}
					output = sentence
					output = output.join(' ')
					.replaceAll(~/<#(\d+)>/) {full, ids -> "#${e.jda.channels.find{it.id == ids} ?: 'deleted-channel'}"}
					.replaceAll(~/<@!?(\d+)>/) {full, ids -> "@${e.jda.users.find{it.id == ids} ?: 'invalid-user'}]"}
					.replaceAll(/https?:\/\/[^\s<]+[^<"{|^~`\[\s]/) {"<$it>"}//.capitalize()
/*					try { // START EXPERIMENTAL
					if (!output.count('~~').even) {
						if (output.ends('~~')) output += '~~'
						else output='~~' + output
					}
					if (!output.count('***').even) {
						if (output.ends('***')) output += '***'
						else output='***' + output
					}
					if (!output.count('**').even) {
						if (output.ends('**')) output += '**'
						else output='**' + output
					}
					if (!output.count('*').even) {
						if (output.ends('*')) output += '*'
						else output='*' + output
					}
					if (!output.count('__').even) {
						if (output.ends('__')) output += '__'
						else output = '__' + output
					}
					if (!output.count('_').even) {
						if (output.ends('_')) output += '_'
						else output = '_' + output
					}
					if (!output.count('`').even) {
						if (output.contains('```')) output += '```'
						else output += '`'
					}
					} catch(ex3) {
						ex3.printStackTrace()
					} // END EXPERIMENTAL*/
					list += output
				}
				String name = id
				if (user) name = "$user.identity ($user.name#$user.discriminator)"
				e.sendMessage("**$name**:\n${list.join('\n\n')}").queue()
			} catch (IllegalArgumentException ex) {
				e.sendMessage("I don't really have any markovs for that user.").queue()
			} catch (TooManyLoopsException ex2) {
				e.sendMessage('I feel dizzy. Can you try that again?').queue()
			}
		} else if (user) {
			e.sendMessage("I don't have any markovs for that user.").queue()
		} else {
			e.sendMessage(d.errorMessage() + "Usage: `${d.prefix}markov [name/@mention/user id]`.").queue()
		}
	}
	String category = "General"
	String help = """`markov [@mention]` will make me create a markov of that person's messages.
`markov [id]` will make me create a markov of the messages by a person with that ID, even if they aren't in the server.
`markov batch` will make me create 3 markovs instead of 1, although this can take quite some time.
`markov random` will make me create a markov of a random user in the server.
Original command by claude#7436."""
}


/**
Code manufactured by Claude Inc
Made in China
Don't touch loser
@CompileStatic class MarkovCommand extends Command {
	List aliases = ['markov']
	int limit = 35
	static final Pattern channelMentionRegex = ~/<#(\d+)>/, userMentionRegex = ~/<@!?(\d+)>/,
	urlRegex = ~/https?:\/\/[^\s<]+[^<"{|^~`\[\s]/, batchTrimRegex = ~/(batch|^\s+|\s+$)/
	
	// feel free to replace this later with something better
	@CompileDynamic
	static User findUser(Guild guild, String str) { guild.findUser(str) }
	static def sendMessage(Channel channel, String str) { channel.sendMessage(str).queue() }
	static def sendTyping(Channel channel) { channel.sendTyping().queue() }
	static def identityOf(User user) { db.find{ user.id in it.value.ids }?.value?.aka?.getAt(0) ?: user.name }
	
	static String markov(List<String> lines) {
		def sentences = new ArrayList<List<String>>(lines.size().intdiv(4).intValue())
		for (l in lines) {
			final t = l.tokenize()
			if(!t.empty) sentences.add(t)
		}
		def output, last
		def sentence = new ArrayList<String>()
		sentence.add(last = sentences.random().random())
		def iterations = 0
		while (true) {
			if (++iterations > 1000) throw new TooManyLoopsException(1000)
			def following = new ArrayList<String>()
			for (s in sentences) {
				def before = s.get(0)
				for (int i = 1; i < s.size(); i++) {
					def item = s.get(i)
					if (before.hashCode() == last.hashCode() && before == last)
						following.add item
					before = item
				}
			}
			if (following.empty) break
			sentence.add(last = following.random())
		}
		sentence.join(' ')
		.replaceAll(channelMentionRegex) { full, ids -> "#${e.jda.getTextChannelById(ids) ?: 'deleted-channel'}" }
		.replaceAll(userMentionRegex) { full, ids -> "@${e.jda.getUserById(ids) ?: 'invalid-user'}" }
		.replaceAll(urlRegex) { String it ->
			// rawest form of string concatenation
			final itl = it.length()
			def s = new char[2 + itl]
			s[0] = (char) '<'
			it.getChars(0, itl, s, 1)
			s[itl + 1] = (char) '>'
			String.valueOf(s)
		}
	}
	
	def run(Map d, Event e) {
		int times = 1
		def user = (User) e.author, args = (String) d.args
		if (args.contains('batch')) {
			args = args.replaceAll(batchTrimRegex, '')
			times = 3
		}
		if (e.message.mentions) user = e.message.mentions.last()
		else if (!args.empty) user = findUser(e.guild, args)
		String id
		if (null == user) user = e.jda.getUserById(id = args)
		else id = user.id
		def markov = new File("markov/${id}.txt")
		if (markov.exists()) {
			sendTyping(e.message.channel)
			final lines = markov.readLines()
			try {
				def list = new ArrayList<String>(times)
				for (int _ = 0; _ < times; ++_) list.add(markov(lines))
				sendMessage(e.message.channel,"**${user?.identity?.capitalize()?:id}**:\n${list.join('\n\n')}")
			} catch (IllegalArgumentException ex) {
				sendMessage(e.message.channel,"I don't really have any markovs for that user.")
			} catch (TooManyLoopsException ex2) {
				sendMessage(e.message.channel,'I feel dizzy. Can you try that again?')
			}
		} else if (null != user) {
			sendMessage(e.message.channel,"I don't have any markovs for that user.")
		} else {
			sendMessage(e.message.channel,((Closure) d.errorMessage)() + "Usage: `${d.prefix}markov [name/@mention/user id]`.")
		}
	}
	
	String category = "General"
	String help = """`markov [@mention]` will make me create a markov of that person's messages.
`markov [id]` will make me create a markov of the messages by a person with that ID, even if they aren't in the server.
`markov batch` will make me create 3 markovs instead of 1, although this can take quite some time.
Original command by claude#7436."""
}**/


class JsonCommand extends Command {
	List aliases = ['json']
	def run (Map d, Event e) {
		d.args = d.args.tokenize()
		String task = d.args[0].toLowerCase()
		if (!d.args[1]) {
			e.sendMessage(d.errorMessage() + "Usage: `${d.prefix}json encode/decode [text]`.").queue()
		} else if (task == 'encode') {
			e.sendMessage(JsonOutput.toJson(d.args[1..-1].join(' '))[1..-2]).queue()
		} else if (task == 'decode') {
			e.sendMessage(new JsonSlurper().parseText(("\"${d.args[1..-1].join(' ')}\""))).queue()
		} else {
			e.sendMessage(d.errorMessage() + "Usage: `${d.prefix}json encode/decode [text]`.").queue()
		}
	}
	String category = "General"
	String help = """`json encode [text]` will make me add ANSI-friendly \\u escapes to the text.
`json decode [text]` will make me transform encoded \\u escaped text back to normal.
Jason! Jason!"""
}


class WaitCommand extends Command {
	List aliases = ['wait']
	boolean dev = true
	def run (Map d, Event e) {
		try {
			final long ms = Long.parseLong(d.args)
			if (ms > 60000) {
				e.sendMessage("I'm not going to wait over a minute.").queue()
			} else {
				Thread.sleep(ms)
			}
		} catch (ex) {
			e.sendMessage("$d.args isn't a number.")
		}
	}
	String category = "Special"
	String help = """`wait [milliseconds]` will make me add a delay of that time.
This command is for use with custom commands only."""
}