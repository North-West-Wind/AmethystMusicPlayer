# Amethyst Music Player
A client side mod that plays music using amethyst blocks and clusters.

## Adding your music
This mod reads from MIDI files placed inside `config/amethystmusicplayer`. It ONLY reads MIDI files, so you may have to make them yourself or download from the Internet. Both of which can be done using Musescore.
- [Musescore Website (for searching and downloading)](https://musescore.com/dashboard)
- [Musescore 4 (for editing)](https://musescore.org/en/download)

### Notes when creating MIDI files
This mod only reads the first track (instrument) of each MIDI file.  
If 2 notes are present at the same position, it will pick the one with higher pitch.

This repository provides an example MIDI file and config folder structure.  
You may copy the content INSIDE the `example` directory to your game directory to test it out.

`Swing Door Saloon` is composed by Crispin Merrell and is my favourite piece of music. The MIDI/score is arranged by myself (NorthWestWind).

## License
GPLv3
