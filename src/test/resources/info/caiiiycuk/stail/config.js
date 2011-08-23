var server = {
		port: 8080
};

var tails = 
[
	{command: "tail -f /var/log/dmesg", size: 400, alias: "dmesg"},
	{command: "tail -f /var/log/syslog", size: 800, alias: "syslog"},
	{command: "tail -f /var/log/udev"}
];

var trustedOrigins = [(/.*/)];
