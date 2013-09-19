import java.io.*;
import processing.serial.*;
import mindset.*;
import java.util.Date;
import java.text.SimpleDateFormat;
Neurosky neurosky = new Neurosky();
String com_port = "/dev/tty.MindWave";
PrintWriter log; 
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
void setup() {
	try { log = new PrintWriter(new FileWriter("/Users/csmpls/my-great-neurolog.txt", true));  
	} catch (IOException e) { println("nope");}
	neurosky.initialize(this,com_port, log);
}
void draw() {
	neurosky.update();
}

void stop() {
	log.close();
}