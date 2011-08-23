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