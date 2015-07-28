```
go get github.com/turbobytes/pulse/digdroid
cd /tmp
ANDROID_HOME="/path/to/Android/Sdk/" gomobile bind -v github.com/turbobytes/pulse/digdroid
mv digdroid.aar ./digdroid/digdroid.aar 
```
Then build the apk in android studio...
