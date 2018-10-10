import net.dv8tion.jda.core.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.entities.impl.*
import net.dv8tion.jda.core.managers.*
import net.dv8tion.jda.core.events.*
import net.dv8tion.jda.core.events.guild.*
import net.dv8tion.jda.core.events.message.*
import net.dv8tion.jda.core.events.message.react.*
import net.dv8tion.jda.core.events.guild.member.*
import net.dv8tion.jda.core.utils.MiscUtil
import java.time.OffsetDateTime
import java.text.ParseException

final String id='155684861770858496'
final String owner='107894146617868288'


JDA.metaClass.play={String game->delegate.presence.setGame(Game.of(Game.GameType.DEFAULT,game))}
JDA.metaClass.listen={String game->delegate.presence.setGame(Game.of(Game.GameType.LISTENING,game))}
JDA.metaClass.watch={String game->delegate.presence.setGame(Game.of(Game.GameType.WATCHING,game))}
JDA.metaClass.setName={String name->delegate.selfUser.manager.setName(name)}
JDA.metaClass.setAvatar={String avatar->delegate.selfUser.manager.setAvatar(Icon.from(new File(avatar)))}
JDA.metaClass.getChannels={delegate.textChannels+delegate.voiceChannels+delegate.privateChannels}

Event.metaClass.getJda={delegate.JDA}

MessageReceivedEvent.metaClass.sendMessage={ String content -> delegate.channel.sendMessage("\u200b${ content?.replaceEach(['@everyone', '@here'], ['@\u0435veryone', '@h\u0435re']) }") }
MessageReceivedEvent.metaClass.sendTyping={ delegate.channel.sendTyping() }
MessageReceivedEvent.metaClass.sendFile={String file->delegate.channel.sendFile(new File(file),null)}
MessageReceivedEvent.metaClass.sendFile={File file->delegate.channel.sendFile(file,null)}

GenericGuildMemberEvent.metaClass.sendMessage={String content->delegate.guild.defaultChannel.sendMessage('\u200b'+content?.replaceEach(['@everyone','@here'],['@\u0435veryone','@h\u0435re']))}

Message.metaClass.getAttachment={delegate.attachments[0]}
Message.metaClass.edit={String content->delegate.editMessage(content)}
//Message.metaClass.delete={delegate.deleteMessage()}
Message.metaClass.getGuild={delegate.channel?.guild}
Message.metaClass.getMentions={delegate.mentionedUsers}
Message.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
Message.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}

Message.Attachment.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
Message.Attachment.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}

Member.metaClass.getStatus={delegate.onlineStatus.toString().toLowerCase()}

User.metaClass.getAvatar={delegate.avatarUrl}
User.metaClass.getMention={delegate.asMention}
User.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
User.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
User.metaClass.getAvatarUrl={delegate.avatarId?"https://cdn.discordapp.com/avatars/$delegate.id/$delegate.avatarId${if (delegate.avatarId.startsWith('a_')) {'.gif'} else {'.jpg'}}":delegate.defaultAvatarUrl}
User.metaClass.getDefaultAvatar={delegate.defaultAvatarUrl}
User.metaClass.getAvatar={delegate.avatarUrl}
User.metaClass.isMember={Guild guild->
	guild.members.find {it.user.id==delegate.id}.roles||(delegate==guild.owner)||(delegate.id==owner)
}
User.metaClass.isStaff={Guild guild->
	("Trainer"in guild.members.find {it.user.id==delegate.id}.roles*.name)||guild.members.find {it.user.id==delegate.id}.roles.any {'MESSAGE_MANAGE'in it.permissions*.toString()}||(delegate==guild.owner)||(delegate.id==owner)
}
User.metaClass.isOwner={Guild guild->
	("Bot Commander"in guild.members.find {it.user.id==delegate.id}.roles*.name)||guild.members.find {it.user.id==delegate.id}.roles.any {'ADMINISTRATOR'in it.permissions*.toString()}||(delegate==guild.owner)||(delegate.id==owner)
}

Channel.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
Channel.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
Channel.metaClass.getMention={delegate.asMention}
Channel.metaClass.getCategory={delegate.guild?delegate.guild.categories.find {delegate in it.channels}:null}

TextChannel.metaClass.getUsers={delegate.members*.user}

VoiceChannel.metaClass.join={delegate.guild.audioManager.openAudioConnection(delegate)}
VoiceChannel.metaClass.leave={delegate.guild.audioManager.closeAudioConnection()}
VoiceChannel.metaClass.getUsers={delegate.members*.user}

PrivateChannel.metaClass.isSpam={true}
PrivateChannel.metaClass.isLog={false}
PrivateChannel.metaClass.isNsfw={true}
PrivateChannel.metaClass.isSong={true}
PrivateChannel.metaClass.isIgnored={false}
PrivateChannel.metaClass.getGuild={null}
PrivateChannel.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
PrivateChannel.metaClass.getCreateTime={new Date((Long.parseLong(delegate.id)>>22)+1420070400000)}

Role.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
Role.metaClass.getCreateTime={new Date((Long.parseLong(delegate.id)>>22)+1420070400000)}
Role.metaClass.getMention={delegate.asMention}
Role.metaClass.isColour={(delegate.name==~/#?[A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9][A-Fa-f0-9]/)&&!delegate.name.toLowerCase().containsAny('g'..'z')}

Emote.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
Emote.metaClass.getCreateTime={new Date((Long.parseLong(delegate.id)>>22)+1420070400000)}
Emote.metaClass.getMention={delegate.asMention}

Guild.metaClass.getChannels={delegate.textChannels+delegate.voiceChannels}
Guild.metaClass.getIcon={delegate.iconUrl}
Guild.metaClass.getDefaultChannel={delegate.channels.find {it.id==delegate.id}?:delegate.textChannels[0]}
Guild.metaClass.getDefaultRole={delegate.publicRole}
Guild.metaClass.getCreateTimeMillis={(Long.parseLong(delegate.id)>>22)+1420070400000}
Guild.metaClass.getCreateTime={new Date(delegate.createTimeMillis)}
Guild.metaClass.getUsers={delegate.members*.user}


JDA.metaClass.findGuild={String args->delegate.guilds.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
JDA.metaClass.findUser = { String args ->
	User user = delegate.users.toList().sort {it.name.length()}.find {
		String ass = it.identity
		[ass.abbreviate().toLowerCase(), it.name.toLowerCase(), ass.toLowerCase()].any {it?.contains(args.toLowerCase())}
	}
	if (!user) user = delegate.users.find {it.id == args}
	user
}
Guild.metaClass.findUser = { String args ->
	User user = delegate.users.toList().sort {it.name.length()}.find {
		String ass = it.identity
		String member = delegate.members.find { def fuckjava -> fuckjava.user.id == it.id}.nickname?.toLowerCase()
		[ass.abbreviate().toLowerCase(), it.name.toLowerCase(), member, ass.toLowerCase()].any {it?.contains(args.toLowerCase())}
	}
	if (!user) user = delegate.users.find {it.id == args}
	user
}
JDA.metaClass.findChannel={String args->delegate.channels.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
Guild.metaClass.findChannel={String args->delegate.channels.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
JDA.metaClass.findTextChannel={String args->delegate.textChannels.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
Guild.metaClass.findTextChannel={String args->delegate.textChannels.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
JDA.metaClass.findVoiceChannel={String args->delegate.voiceChannels.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
Guild.metaClass.findVoiceChannel={String args->delegate.voiceChannels.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
JDA.metaClass.findPrivateChannel={String args->delegate.privateChannels.toList().sort {it.user.name.length()}.find {[it.user.name.toLowerCase(),it.user.identity.toLowerCase()].any {it.contains(args.toLowerCase())}}}
JDA.metaClass.findEmote={String args->delegate.emotes.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
Guild.metaClass.findEmote={String args->delegate.emotes.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
Guild.metaClass.findRole={String args->delegate.roles.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
JDA.metaClass.findCategory={String args->delegate.categories.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}
Guild.metaClass.findCategory={String args->delegate.categories.toList().sort {it.name.length()}.find {[it.name.toLowerCase()].any {it.contains(args.toLowerCase())}}}


String.metaClass {
	cut { int at ->
  	if (at < length()) delegate[0..<at] + "\u2026"
  	else delegate
	}
  rawBirthday {
  	delegate.replaceAll(/\b(\d+)(?:th|st|nd|rd)\b/, '$1')
  }
  formatBirthday {
  	delegate.replace('11 ','11th ')
  	  .replace('12 ','12th ')
  	  .replace('13 ','13th ')
  	  .replace('1 ','1st ')
  	  .replace('2 ','2nd ')
  	  .replace('3 ','3rd ')
  	  .replace('4 ','4th ')
  	  .replace('5 ','5th ')
  	  .replace('6 ','6th ')
  	  .replace('7 ','7th ')
  	  .replace('8 ','8th ')
  	  .replace('9 ','9th ')
  	  .replace('0 ','0th ')
  }
  addVariables { MessageReceivedEvent e, String args ->
		if (data.containsAll(['{','}'])) {
			if (e.guild != null) {
				final user = e.guild.users.toList().random()
				final channel = e.guild.channels.toList().random()
				final identy = e.author.identity
				final useridenty = user.identity
				substitute(data, args, e, [
					id: MiscUtil.getDiscordTimestamp(System.currentTimeMillis()),
					args: args,
					channel: e.channel.name,
					channelname: e.channel.name,
					channelid: e.channel.id,
					server: e.guild.name,
					servername: e.guild.name,
					serverid: e.guild.id,
					servericon: e.guild.icon ?: e.guild.name.tokenize()*.charAt(0).collect(Character.toUpperCase).join(''),
					serverchannel: channel.name,
					serverchannelname: channel.name,
					serverchannelid: channel.id,
					author: identy, authordb: identy,
					authorname: e.author.name, authorid: e.author.id,
					authoravatar: e.author.avatar ?: e.author.defaultAvatar,
					serveruser: useridenty, serveruserdb: useridenty,
					serverusername: user.name, serveruserid: user.id,
					serveruseravatar: user.avatar ?: user.defaultAvatar,
					date: new Date().format('HH:mm:ss, dd MMMM YYYY')])
			} else {
				final identy = e.author.identity
				substitute(data, args, e, [
					id: MiscUtil.getDiscordTimestamp(System.currentTimeMillis()),
					args: args,
					channel: e.channel.name,
					channelname: e.channel.name,
					channelid: e.channel.id,
					server: 'Direct Messages',
					servername: 'Direct Messages',
					serverid: id,
					servericon: e.jda.selfUser.avatar,
					serverchannel: e.channel.name,
					serverchannelname: e.channel.name,
					serverchannelid: e.channel.id,
					author: identy, authordb: identy,
					authorname: e.author.name, authorid: e.author.id,
					authoravatar: e.author.avatar ?: e.author.defaultAvatar,
					serveruser: identy, serveruserdb: identy,
					serverusername: e.author.name, serveruserid: e.author.id,
					serveruseravatar: e.author.avatar ?: e.author.defaultAvatar,
					date: new Date().format('HH:mm:ss, dd MMMM YYYY')])
			}
		} else delegate
	}

  addVariables { GenericGuildMemberEvent e, String args ->
		if (data.containsAll(['{','}'])) {
			final user = e.guild.users.toList().random()
			final channel = e.guild.channels.toList().random()
			final identy = e.member.user.identity
			final useridenty = user.identity
			substitute(data, args, e, [
				id: MiscUtil.getDiscordTimestamp(System.currentTimeMillis()),
				args: args,
				channel: e.guild.defaultChannel.name,
				channelname: e.guild.defaultChannel.name,
				channelid: e.guild.defaultChannel.id,
				server: e.guild.name,
				servername: e.guild.name,
				serverid: e.guild.id,
				servericon: e.guild.icon ?: e.guild.name.tokenize()*.charAt(0).collect(Character.toUpperCase).join(''),
				serverchannel: channel.name,
				serverchannelname: channel.name,
				serverchannelid: channel.id,
				author: identy, authordb: identy,
				authorname: e.member.user.name, authorid: e.member.user.id,
				authoravatar: e.member.user.avatar ?: e.member.user.defaultAvatar,
				serveruser: useridenty, serveruserdb: useridenty,
				serverusername: user.name, serveruserid: user.id,
				serveruseravatar: user.avatar ?: user.defaultAvatar,
				date: new Date().format('HH:mm:ss, dd MMMM YYYY')])
		} else delegate
	}

  addVariables { GenericGuildEvent e, String args ->
		if (data.containsAll(['{','}'])) {
			final user = e.guild.users.toList().random()
			final channel = e.guild.channels.toList().random()
			final identy = e.user.identity
			final useridenty = user.identity
			substitute(data, args, e, [
				id: MiscUtil.getDiscordTimestamp(System.currentTimeMillis()),
				args: args,
				channel: e.guild.defaultChannel.name,
				channelname: e.guild.defaultChannel.name,
				channelid: e.guild.defaultChannel.id,
				server: e.guild.name,
				servername: e.guild.name,
				serverid: e.guild.id,
				servericon: e.guild.icon ?: e.guild.name.tokenize()*.charAt(0).collect(Character.toUpperCase).join(''),
				serverchannel: channel.name,
				serverchannelname: channel.name,
				serverchannelid: channel.id,
				author: identy, authordb: identy,
				authorname: e.user.name, authorid: e.user.id,
				authoravatar: e.user.avatar ?: e.user.defaultAvatar,
				serveruser: useridenty, serveruserdb: useridenty,
				serverusername: user.name, serveruserid: user.id,
				serveruseravatar: user.avatar ?: user.defaultAvatar,
				date: new Date().format('HH:mm:ss, dd MMMM YYYY')])
		} else delegate
	}


  strip {delegate.replaceEach(['@everyone','@here'],['@\u0435veryone','@h\u0435re'])}
  abbreviate {delegate.replaceAll(['\'','"','(',')','[',']','{','}','|','.'],'').split(/(-|_| )/)*.capitalize().join().findAll {it.isUpperCase()}.join()}
  formatTime {
	  String taimu=delegate.replaceAll(/\W+/,'').toLowerCase()
	  int seconds=(taimu.findAll(/(\d+)s/) {full,num->num.toInteger()})[0]?:0
	  int minutes=(taimu.findAll(/(\d+)m/) {full,num->num.toInteger()})[0]?:0
	  int hours=(taimu.findAll(/(\d+)h/) {full,num->num.toInteger()})[0]?:0
	  int days=(taimu.findAll(/(\d+)d/) {full,num->num.toInteger()})[0]?:0
	  int weeks=(taimu.findAll(/(\d+)w/) {full,num->num.toInteger()})[0]?:0
	  int months=(taimu.findAll(/(\d+)n/) {full,num->num.toInteger()})[0]?:0
	  int years=(taimu.findAll(/(\d+)y/) {full,num->num.toInteger()})[0]?:0
	  System.currentTimeMillis()
	    + seconds * 1000
	    + minutes * 60000
	    + hours * 3600000
	    + days * 86400000
	    + weeks * 604800000
	    + months * 2592000000
	    + years * 31536000000
	}
}


OffsetDateTime.metaClass.toDate={Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSSS",delegate.toString())}


String substitute(String data, String args, Event e, Map additional) {
	List<StringBuilder> tokens = new ArrayList<>()
	StringBuilder last = new StringBuilder()
	tokens.add(last)
	def parentage = new ArrayList<List<StringBuilder>>()
	def lastParent = tokens
	parentage.add(lastParent)
	def escaped = false, commentEscaped = false, comment = false
	for (int i = 0; i < data.length(); ++i) {
		final ch = data.charAt(i)
		if (!escaped) {
			if (!commentEscaped && (comment ?
					ch == ((char) '/') : ch == ((char) '*'))) {
				if ((comment = !comment))
					// if it wasn't a comment at first, remove the previous /
					last.deleteCharAt(last.length() - 1)
				continue
			} else if (!comment) {
				if (ch == ((char) '{')) {
					lastParent = new ArrayList<>()
					lastParent.add(last = new StringBuilder())
					commentEscaped = false
					parentage.add(lastParent)
					continue
				} else if (!lastParent.is(tokens) && ch == ((char) ';')) {
					lastParent.add(last = new StringBuilder())
					commentEscaped = false
					continue
				} else if (!lastParent.is(tokens) && ch == ((char) '}')) {
					def p = parentage.pop()
					lastParent = parentage.last()
					lastParent.last().append(evalsub(p, args, e, additional))
					lastParent.add(last = new StringBuilder())
					commentEscaped = false
					continue
				} else last.append(ch)
			}
		} else if (!comment) last.append(ch)
		commentEscaped = !commentEscaped && ch == (comment ?
			((char) '*') :
			((char) '/')) && escaped
		escaped = !escaped && ch == ((char) '\\')
	}
	// for people who forgot/purposely left out braces
	while (!lastParent.is(tokens)) {
		def p = parentage.pop()
		lastParent = parentage.last()
		lastParent.last().append(evalsub(p, args, e, additional))
	}
	tokens.join('')
}

boolean cmpstr(String orig, int h, String other) {
	h == other.hashCode() && orig == other
}

String evalsub(List<StringBuilder> expr, String args, Event e, Map<String, String> additional) {
	final s = evalsub(expr[0], args, e, additional)
	final h = s.hashCode()
	final rand = new Random()
	if (cmpstr(s, h, 'random'))
		expr.get(rand.nextInt(expr.size() - 1) + 1).toString()
	else if (cmpstr(s, h, 'args')) args
	else if (cmpstr(s, h, 'range')) {
		try {
			def a = BigDecimal.valueOf(expr[1].toString())
		  def b = BigDecimal.valueOf(expr[2].toString())
		  def range = a..b
		  range[rand.nextInt(range.size())]
		} catch (NumberFormatException ignored) {
			def joiner = new StringJoiner('{', ';', '}')
			for (x in expr) joiner.add(x)
			joiner.toString()
		}
	} else if (cmpstr(s, h, 'replace')) {
		def result = expr[1].toString()
		for (int i = 3; i < expr.size(); i += 2) {
			result = result.replace(expr[i-1].toString(), expr[i].toString())
		}
		result
	} else if (cmpstr(s, h, 'urlify')) {
		URLEncoder.encode(expr[1].toString(), 'UTF-8')
	} else if (cmpstr(s, h, 'nameof')) {
		try {
			final id2 = Long.parseLong(expr[1].toString())
			for (u in jda.users)
				if (u.idLong == id2)
					return u.name
		} catch (NumberFormatException ignored) {
			final id2 = expr[1].toString()
			for (u in jda.users)
			  if (u.rawIdentity.equalsIgnoreCase(id2))
			  	return u.name
		}
		'???'
	} else if (cmpstr(s, h, 'date')) {
		try {
			new Date().format(exprs[1]?.toString() ?: 'HH:mm:ss, dd MMMM YYYY')
		} catch (ParseException ignored) {
			new Date().format('HH:mm:ss, dd MMMM YYYY')
		}
	} else if (cmpstr(s, h, 'idof')) {
		final str = exprs[1].toString()
		for (u in e.jda.users)
			if (u.rawIdentity.equalsIgnoreCase(str) ||
					u.name.equalsIgnoreCase(str))
				return u.id
		'???'
	} else if (cmpstr(s, h, 'dbof')) {
		try {
			final id2 = Long.parseLong(expr[1].toString())
			for (u in jda.users)
				if (u.idLong == id2)
					return u.rawIdentity
		} catch (NumberFormatException ignored) {
			final id2 = expr[1].toString()
			for (u in jda.users)
			  if (u.name.equalsIgnoreCase(id2))
			  	return u.rawIdentity
		}
		'???'
	} else {
		def ad = additional[s]
		if (null != ad) return ad
		def joiner = new StringJoiner('{', ';', '}')
		for (x in expr) joiner.add(x)
		joiner.toString()
	}
}