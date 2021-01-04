nc -l -u -p 9009 > rawdata.pcm

vlc --demux=rawaud --rawaud-channels 1 --rawaud-samplerate 8000 --rawaud-fourcc "u8  " rawdata.pcm

vlc --demux=rawaud --rawaud-channels 1 --rawaud-samplerate 8000 --rawaud-fourcc s16l rawdata.pcm
