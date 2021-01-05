vmic
====

This turns your android phone into a virtual microphone that streams its microphone samples over UDP.

Write raw PCM data to file from UDP packets
-------------------------------------------

Use socat to listen on a port for UDP packets and write the raw PCM samples to a file:
```
socat udp4-listen:9009,reuseaddr,fork - > rawdata.pcm
```


Play the saved raw PCM samples using:

```
vlc --demux=rawaud --rawaud-channels 1 --rawaud-samplerate 8000 --rawaud-fourcc s16l rawdata.pcm
```

If you're using 8-bit per sample mode, use the following fourcc code:

```
vlc --demux=rawaud --rawaud-channels 1 --rawaud-samplerate 8000 --rawaud-fourcc "u8  " rawdata.pcm
```

Virtual mic using pulseaudio
----------------------------

Create a virtual mic that gets raw PCM samples from a named pipe:
```
pactl load-module module-pipe-source source_name=virtmic file=/home/sagar/Desktop/virtmic format=s16le rate=8000 channels=1
```

Use socat to stream samples got via UDP to the named pipe `virtmic`:

```
socat udp4-listen:9009,reuseaddr,fork - > /home/sagar/Desktop/virtmic
```

To remove the virtual mic:

```
pactl unload-module module-pipe-source
```

Links
-----

Some links that helped me:

- https://stackoverflow.com/a/43553706
- https://superuser.com/a/331595

