# Epic Sounds 2

A Discord-Musicbot designed to be similar to the [Groovy Music-Bot](https://groovy.bot/).  

## Features  

- Support for a lot of Music-Streams including Youtube, SoundCloud and mp3-Files (For details visit the
[Lavaplayer](https://github.com/sedmelluq/lavaplayer) GitHub-Page)  
- No constant leaving/joining of the Voice-Channel, because of the join and leave commands  
- User controllable Master-Volume for the Bot, to change the Volume for everyone  
- Customizable Prefix for the Bot. (Server Admin-Permissions needed)
- Console-like usage. All commands are programmed to be used like a Command in a Console.
- (Planned) Fully customizable Soundboard  

## Commands  

The Default Prefix is `Yo!`  
To view details of Commands while the Bot operates, use `-h` or `--help`  

### General Purpose  

- `customizePrefix` to change the current prefix to a new one  
- `help` to view all Commands and how they are used  

### Music Commands  

- `join` to join a channel.  
- `leave` to leave a channel. (It can do that automatically)  
- `play` to start music playback  
- `pause` to pause music playback  
- `stop` to pause playback and delete the current queue  
- `skip` to skip the current song  
- `volume` to change or view the Master volume  
- `current` to view the currently playing song  
- `queue` to view the current queue  
- `undo` to remove the most recent addition of the queue  
- `delete` to remove the next song in the queue  
- `shuffle` to shuffle the current queue  

### Soundboard Commands

WIP  

## Installation  

Download the most recent jar-File from this Repository and launch it from commandline
with java. You also need a Discord Bot-Application and the Bot-Tag from it.  
Now write `java -jar epic_sounds2.jar "TAG"` while replacing TAG with your Bot-Tag.
maybe you also need to change name of the .jar-file if it differs from your Download.