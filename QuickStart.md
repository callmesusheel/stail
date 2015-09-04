1. Download a [release tarball](http://code.google.com/p/stail/downloads/list)
```
    $ mkdir stail
    $ cd stail
    $ wget http://stail.googlecode.com/files/stail-1.1.zip
    $ unzip stail-1.1.zip
```
2. Edit configuration file if needed (see readme, for short descriptions of each configuration properties)
```
    $ emacs -nw config.js
```
Example configuration is:
```
    var server = {
       port: 8080,
       frontend: "front-end"
    };

    var tails =
    [
       {command: "tail -f -n 1000 /var/log/dmesg", size: 1000, alias: "dmesg"},
       {command: "tail -f /var/log/syslog", size: 800, alias: "syslog"},
       {command: "tail -f /var/log/udev"}
    ];

    var trustedOrigins = [(/.*/)];
```

**server.frontend** — front-end directory, will be mapped to http://domain:port/front-end/

**tails** — list of commands their output will be redirected to front-end

**trustedOrigins** — regular expression of trusted origins.

3. Edit front-end/index.html if needed. Check server variable.
```
    $ emacs -nw front-end/index.html
    ...
    var server = "http://localhost:8080";
```
4. Startup the server:
```
    $ java -jar stail.jar config.js
```
5. Check url http://localhost:8080/front-end/index.html.

<img src='http://caiiiycuk.info/wp-content/uploads/2011/08/preview.png' alt='stail' />


**Notice:** you should always write index.html, because server make direct mapping directory to web. Also you can look for server info by url http://localhost:8080/info.